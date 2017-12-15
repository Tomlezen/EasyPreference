package com.tlz.easypreference.compiler.model

import com.tlz.easypreference.EasyPreference
import com.tlz.easypreference.Key
import com.tlz.easypreference.compiler.pkg
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Elements

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 14:41.
 */
class EasyPreferenceModel(type: TypeElement, elementUtils: Elements) {

  val keys = mutableListOf<VariableElement>()
  val pkg = EasyPreference::class.java.`package`.name//type.pkg(elementUtils)
  val originalClassName = type.qualifiedName.toString().substring(pkg.length + 1).replace(".", "")
  val name = type.getAnnotation(EasyPreference::class.java).name
  val model = type.getAnnotation(EasyPreference::class.java).model
  val className = "${originalClassName}Utils"

  init {
    findAnnotations(type)
  }

  /**
   * 查找key注解.
   */
  private fun findAnnotations(element: Element){
    element.enclosedElements.forEach { e ->
      findAnnotations(e)
      e.getAnnotation(Key::class.java)?.let {
        keys.add(e as VariableElement)
      }
    }
  }

}