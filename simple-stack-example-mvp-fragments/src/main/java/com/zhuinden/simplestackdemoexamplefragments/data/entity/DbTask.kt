package com.zhuinden.simplestackdemoexamplefragments.data.entity

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

/**
 * Created by Owner on 2017. 01. 26..
 */

open class DbTask : RealmObject() {
    @field:PrimaryKey
    var id: String? = null

    var title: String? = null

    var description: String? = null

    @field:Index
    var completed: Boolean = false
}
