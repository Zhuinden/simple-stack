package com.zhuinden.simplestackdemonestedstack.presentation.paths.main.chromecast;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.RelativeLayout;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.Bundleable;
import com.zhuinden.simplestack.StateBundle;

import java.util.ArrayList;

/**
 * Created by Owner on 2017. 01. 12..
 */

public class ChromeCastView
        extends RelativeLayout
        implements Bundleable {
    public ChromeCastView(Context context) {
        super(context);
        init(context);
    }

    public ChromeCastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChromeCastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(21)
    public ChromeCastView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        if(!isInEditMode()) {
            chromeCastKey = Backstack.getKey(context);
        }
    }

    ChromeCastKey chromeCastKey;

    @NonNull
    @Override
    public StateBundle toBundle() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putBoolean("_boolean", true);
        stateBundle.putByte("_byte", (byte) 0x01);
        stateBundle.putChar("_char", 'C');
        stateBundle.putShort("_short", (short) 5);
        stateBundle.putInt("_int", 6);
        stateBundle.putLong("_long", 7L);
        stateBundle.putFloat("_float", 0.25f);
        stateBundle.putDouble("_double", 0.5);
        stateBundle.putString("_String", "HEYA");
        stateBundle.putCharSequence("_CharSequence", "HELLO");
        stateBundle.putSerializable("_Serializable", "boop");
        stateBundle.putIntegerArrayList("_IntegerArrayList", new ArrayList<Integer>() {{
            add(5);
            add(6);
            add(7);
        }});
        stateBundle.putStringArrayList("_StringArrayList", new ArrayList<String>() {{
            add("world");
            add("dlrod");
        }});
        stateBundle.putCharSequenceArrayList("_CharSequenceArrayList", new ArrayList<>());
        stateBundle.putBooleanArray("_BooleanArray", new boolean[]{true, true, false, false});
        stateBundle.putByteArray("_ByteArray", new byte[]{(byte) 0x05, (byte) 0x09});
        stateBundle.putShortArray("_ShortArray", new short[]{(short) 4});
        stateBundle.putCharArray("_CharArray", new char[]{'c', 'd'});
        stateBundle.putIntArray("_IntArray", new int[]{1, 4, 5, 6});
        stateBundle.putLongArray("_LongArray", new long[]{1L, 2L, 8L});
        stateBundle.putFloatArray("_FloatArray", new float[]{0.25f, 0.40f, 0.55f});
        stateBundle.putDoubleArray("_DoubleArray", new double[]{0.4, 0.7});
        final StateBundle _stateBundle = new StateBundle();
        _stateBundle.putString("key", "value");
        stateBundle.putBundle("_StateBundle", _stateBundle);
        stateBundle.putParcelable("_Parcelable", _stateBundle);
        stateBundle.putParcelableArrayList("_ParcelableArrayList", new ArrayList<Parcelable>() {{
            add(_stateBundle);
        }});
        stateBundle.putSparseParcelableArray("_SparseParcelableArray", new SparseArray<>());
        return stateBundle;
    }

    @Override
    public void fromBundle(@Nullable StateBundle bundle) {
        if(bundle != null) {
            bundle.getBoolean("_boolean");
            bundle.getByte("_byte");
            bundle.getChar("_char");
            bundle.getShort("_short");
            bundle.getInt("_int");
            bundle.getLong("_long");
            bundle.getFloat("_float");
            bundle.getDouble("_double");
            bundle.getString("_String");
            bundle.getCharSequence("_CharSequence");
            bundle.getSerializable("_Serializable");
            bundle.getIntegerArrayList("_IntegerArrayList");
            bundle.getStringArrayList("_StringArrayList");
            bundle.getCharSequenceArrayList("_CharSequenceArrayList");
            bundle.getBooleanArray("_BooleanArray");
            bundle.getByteArray("_ByteArray");
            bundle.getShortArray("_ShortArray");
            bundle.getCharArray("_CharArray");
            bundle.getIntArray("_IntArray");
            bundle.getLongArray("_LongArray");
            bundle.getFloatArray("_FloatArray");
            bundle.getDoubleArray("_DoubleArray");
            bundle.getParcelable("_Parcelable");
            bundle.getParcelableArrayList("_ParcelableArrayList");
            bundle.getSparseParcelableArray("_SparseParcelableArray");
        }
    }
}
