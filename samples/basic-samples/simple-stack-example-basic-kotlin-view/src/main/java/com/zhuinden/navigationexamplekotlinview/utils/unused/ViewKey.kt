package com.zhuinden.navigationexamplekotlinview.utils.unused

import android.os.Parcelable
import com.zhuinden.simplestack.navigator.DefaultViewKey

interface ViewKey : DefaultViewKey, Parcelable {
    override fun layout(): Int
}
