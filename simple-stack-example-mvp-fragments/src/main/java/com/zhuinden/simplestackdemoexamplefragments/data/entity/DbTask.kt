package com.zhuinden.simplestackdemoexamplefragments.data.entity

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey

/**
 * Created by Zhuinden on 2018. 08. 20.
 */

open class DbTask : RealmObject() {
    @field:PrimaryKey
    var id: String? = null

    var title: String? = null

    var description: String? = null

    @field:Index
    var completed: Boolean = false
}
