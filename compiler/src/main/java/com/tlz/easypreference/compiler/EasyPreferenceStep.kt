package com.tlz.easypreference.compiler

import com.google.auto.common.BasicAnnotationProcessor
import com.google.common.collect.SetMultimap
import com.tlz.easypreference.EasyPreference
import com.tlz.easypreference.Key
import com.tlz.easypreference.compiler.model.EasyPreferenceModel
import com.tlz.easypreference.compiler.writer.EasyPreferenceInitializerWriter
import com.tlz.easypreference.compiler.writer.EasyPreferenceWriter
import java.io.File
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 14:32.
 */
class EasyPreferenceStep(private val processingEnv: ProcessingEnvironment) : BasicAnnotationProcessor.ProcessingStep  {

  /**
   * 源码生成位置.
   */
  private val sourceLocation by lazy {
    val infoFile = processingEnv.filer.createSourceFile("package-info", null)
    val out = infoFile.openWriter()
    out.close()
    File(infoFile.name).parentFile
  }

  private val messager = processingEnv.messager

  override fun process(elementsByAnnotation: SetMultimap<Class<out Annotation>, Element>?): MutableSet<Element> {
    val deferredTypes = mutableSetOf<Element>()
    try {
      elementsByAnnotation?.let {
        EasyPreferenceInitializerWriter().write(sourceLocation)
        it[EasyPreference::class.java]
            .filter { it is TypeElement }
            .map { it as TypeElement }
            .forEach {
              EasyPreferenceWriter(EasyPreferenceModel(it, processingEnv.elementUtils)).write(sourceLocation)
            }
      }
    }catch (e: Exception){
      messager.printMessage(Diagnostic.Kind.ERROR, e.message)
    }
    return deferredTypes
  }

  override fun annotations(): MutableSet<out Class<out Annotation>> {
    val set = HashSet<Class<out Annotation>>()
    set.add(EasyPreference::class.java)
    set.add(Key::class.java)
    return set
  }

}