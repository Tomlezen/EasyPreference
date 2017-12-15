package com.tlz.easypreference.compiler.writer

import com.squareup.kotlinpoet.FileSpec
import com.tlz.easypreference.EasyPreference
import java.io.File

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 15:44.
 */
class EasyPreferenceInitializerWriter {

  fun write(location: File) {
    FileSpec.builder(EasyPreference::class.java.`package`.name, "EasyPreferenceInitializer")
        .addType(EasyPreferenceInitializerBuilderGenerator().create())
        .build()
        .writeTo(location)
  }

}