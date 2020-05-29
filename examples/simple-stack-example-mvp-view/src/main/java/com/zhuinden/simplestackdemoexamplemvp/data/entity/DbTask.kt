package com.zhuinden.simplestackdemoexamplemvp.data.entity

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

/**
 * Created by Owner on 2017. 01. 26..
 */

open class DbTask : RealmObject() {
    @PrimaryKey
    var id: String? = null

    var title: String? = null

    var description: String? = null

    @Index
    var completed: Boolean = false
}
