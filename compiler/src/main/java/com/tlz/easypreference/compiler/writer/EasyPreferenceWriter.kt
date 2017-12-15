package com.tlz.easypreference.compiler.writer

import com.squareup.kotlinpoet.FileSpec
import com.tlz.easypreference.EasyPreference
import com.tlz.easypreference.compiler.model.EasyPreferenceModel
import java.io.File

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 14:37.
 */
class EasyPreferenceWriter(private val model: EasyPreferenceModel) {

  fun write(location: File){
    FileSpec.builder(model.pkg, model.className)
        .addStaticImport(EasyPreference::class.java.`package`.name, "EasyPreferenceInitializer")
        .addType(EasyPreferenceBuilderGenerator().create(model))
        .build()
        .writeTo(location)
  }

}