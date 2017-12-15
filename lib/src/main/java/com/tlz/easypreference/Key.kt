package com.tlz.easypreference

/**
 * Created by tomlezen.
 * Data: 2017/12/14.
 * Time: 14:00.
 * @param name key的具体名字，默认情况是由key+参数名组成
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class Key(val name: String = "default")