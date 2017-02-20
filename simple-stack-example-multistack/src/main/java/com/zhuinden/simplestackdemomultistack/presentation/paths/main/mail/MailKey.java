package com.zhuinden.simplestackdemomultistack.presentation.paths.main.mail;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.zhuinden.simplestackdemomultistack.R;
import com.zhuinden.simplestackdemomultistack.application.Key;
import com.zhuinden.simplestackdemomultistack.application.MainActivity;

/**
 * Created by Zhuinden on 2017.02.19..
 */
@AutoValue
public abstract class MailKey
        extends Key {
    @Override
    public int layout() {
        return R.layout.path_mail;
    }

    public static Parcelable create() {
        return new AutoValue_MailKey();
    }

    @Override
    public String stackIdentifier() {
        return MainActivity.StackType.MAIL.name();
    }
}
