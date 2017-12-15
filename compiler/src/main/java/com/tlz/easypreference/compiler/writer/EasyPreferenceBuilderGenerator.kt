package com.tlz.easypreference.compiler.writer

import android.content.SharedPreferences
import com.squareup.kotlinpoet.*
import com.tlz.easypreference.Key
import com.tlz.easypreference.compiler.isFloat
import com.tlz.easypreference.compiler.isString
import com.tlz.easypreference.compiler.model.EasyPreferenceModel
import com.tlz.easypreference.compiler.typeName
import javax.lang.model.element.VariableElement

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 14:59.
 */
class EasyPreferenceBuilderGenerator {

  /**
   * class文件.
   */
  fun create(model: EasyPreferenceModel) =
      TypeSpec.objectBuilder(model.className)
          .addProperty(createSharePreferenceProperty(model.name, model.model))
          .addProperties(createProperties(model.keys))
          .build()

  private fun createSharePreferenceProperty(name: String, model: Int) =
      PropertySpec.builder(sharePreferenceField, SharedPreferences::class.java)
          .delegate(CodeBlock.of("lazy { EasyPreferenceInitializer.ctx.getSharedPreferences(\"$name\", $model) }"))
          .build()

  /**
   * 创建所有字段属性.
   */
  private fun createProperties(keys: List<VariableElement>): List<PropertySpec> {
    val properties = mutableListOf<PropertySpec>()
    keys.forEach {
      val key = it.getAnnotation(Key::class.java)
      val name = it.simpleName.toString()
      val prefsKey = if (key.name == "default") "key_$name" else key.name
      //创建key属性
      val keyProperty = PropertySpec.builder(prefsKey.toUpperCase(), String::class.java.kotlin, KModifier.CONST)
          .initializer("\"$prefsKey\"")
          .build()
      properties.add(keyProperty)
      var defValue = it.constantValue?.toString()
      val typeName = if (defValue == null) it.asType().typeName().asNullable() else it.asType().typeName().asNonNullable()
      defValue?.let {
        defValue = when {
          typeName.isFloat() -> "${defValue}f"
          typeName.isString() -> "\"$defValue\""
          else -> defValue
        }
      }
      //创建操作属性
      val property = PropertySpec.varBuilder(name, typeName)
          .initializer(defValue ?: "null")
          .getter(createGetFunc(typeName, prefsKey))
          .setter(createSetFunc(typeName, prefsKey))
          .build()
      properties.add(property)
    }
    return properties
  }

  private fun createGetFunc(typeName: TypeName, prefsKey: String) =
      FunSpec.getterBuilder()
          .addStatement("return ${extractGetMethod(typeName)}", prefsKey)
          .build()

  private fun createSetFunc(typeName: TypeName, prefsKey: String) =
      FunSpec.setterBuilder()
          .addParameter("value", typeName)
          .addStatement("val editor = $sharePreferenceField.edit()")
          .addStatement(extractPutMethod(typeName), prefsKey)
          .addStatement("editor.apply()")
          .build()

  private fun extractGetMethod(typeName: TypeName) =
      when(typeName){
        INT -> "$sharePreferenceField.getInt(%S,field)"
        FLOAT -> "$sharePreferenceField.getFloat(%S,field)"
        BOOLEAN -> "$sharePreferenceField.getBoolean(%S,field)"
        DOUBLE -> "$sharePreferenceField.getInt(%S,field)"
        LONG -> "$sharePreferenceField.getLong(%S,field)"
        String::class.asTypeName() -> "$sharePreferenceField.getString(%S,field)"
        else -> throw IllegalArgumentException("不支持该数据类型：$typeName")
      }

  private fun extractPutMethod(typeName: TypeName) =
      when(typeName){
        INT -> "editor.putInt(%S, value)"
        FLOAT -> "editor.putFloat(%S, value)"
        BOOLEAN -> "editor.putBoolean(%S, value)"
        DOUBLE -> "editor.putInt(%S, value)"
        LONG -> "editor.putLong(%S, value)"
        String::class.asTypeName() -> "editor.putString(%S, value)"
        else -> throw IllegalArgumentException("不支持该数据类型：$typeName")
      }

  companion object {
    val sharePreferenceField = "sharePreference"
  }

}