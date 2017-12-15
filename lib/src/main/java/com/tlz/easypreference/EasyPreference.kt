package com.tlz.easypreference

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 13:55.
 * @param name SharePreferences名
 * @param model SharePreferences模式
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class EasyPreference(val name: String, val model: Int = 0x0000)