package com.tlz.easypreference.compiler.writer

import android.content.Context
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 15:46.
 */
class EasyPreferenceInitializerBuilderGenerator {

  fun create() =
      TypeSpec.objectBuilder("EasyPreferenceInitializer")
          .addProperty(PropertySpec.varBuilder("ctx", Context::class.java, KModifier.LATEINIT, KModifier.INTERNAL).build())
          .addFunction(createInitFunc())
          .build()

  /**
   * 创建初始化方法.
   */
  private fun createInitFunc() =
      FunSpec.builder("init")
          .addParameter("ctx", Context::class.java)
          .addStatement("this.ctx = ctx.applicationContext")
          .build()

}