package com.tlz.easypreferenceExample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.tlz.easypreference.EasyPreference
import com.tlz.easypreference.EasyPreferenceInitializer
import com.tlz.easypreference.Key
import com.tlz.easypreference.prefs.MainActivityUtils

@EasyPreference("Test")
class MainActivity : AppCompatActivity() {

  @Key
  val test1 = 0
  @Key
  val test2 = 0f

  @Key(name = "test33")
  val test3 = "test3"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    EasyPreferenceInitializer.init(this)
    MainActivityUtils.test1 = 123
    Log.e("Test", MainActivityUtils.test1.toString())
  }
}
