package com.tlz.easypreference.compiler

import com.google.auto.common.BasicAnnotationProcessor
import com.google.auto.service.AutoService
import javax.annotation.processing.Processor
import javax.lang.model.SourceVersion

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 14:17.
 */
@AutoService(Processor::class)
class EasyPreferenceProcessor: BasicAnnotationProcessor() {

  override fun initSteps(): MutableIterable<ProcessingStep> = mutableListOf(EasyPreferenceStep(processingEnv))

  override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latestSupported()
}