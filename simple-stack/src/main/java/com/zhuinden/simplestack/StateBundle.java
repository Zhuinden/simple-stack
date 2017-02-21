/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhuinden.simplestack;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A mapping from String keys to values of various types.
 */
public class StateBundle {
    static final int type_boolean = 0;
    static final int type_byte = 1;
    static final int type_char = 2;
    static final int type_short = 3;
    static final int type_int = 4;
    static final int type_long = 5;
    static final int type_float = 6;
    static final int type_double = 7;
    static final int type_String = 8;
    static final int type_CharSequence = 9;
    static final int type_Serializable = 10;
    static final int type_IntegerArrayList = 11;
    static final int type_StringArrayList = 12;
    static final int type_CharSequenceArrayList = 13;
    static final int type_BooleanArray = 14;
    static final int type_ByteArray = 15;
    static final int type_ShortArray = 16;
    static final int type_CharArray = 17;
    static final int type_IntArray = 18;
    static final int type_LongArray = 19;
    static final int type_FloatArray = 20;
    static final int type_DoubleArray = 21;
    static final int type_StringArray = 22;
    static final int type_CharSequenceArray = 23;
    static final int type_StateBundle = 26;
    static final int type_Parcelable = 27;
    static final int type_ParcelableArray = 28;
    static final int type_ParcelableArrayList = 29;
    static final int type_SparseParcelableArray = 30;

    //static final int type_Size = 24;
    //static final int type_SizeF = 25;

    private static final String TAG = "Bundle";

    Map<String, Object> map = null;
    Map<String, Integer> typeMap = null;

    /**
     * Constructs a new, empty Bundle.
     */
    public StateBundle() {
        map = new LinkedHashMap<>();
        typeMap = new LinkedHashMap<>();
    }

    /**
     * Constructs a Bundle containing a copy of the mappings from the given
     * Bundle.
     *
     * @param bundle a Bundle to be copied.
     */
    public StateBundle(StateBundle bundle) {
        this();
        putAll(bundle);
    }

    /**
     * Returns the number of mappings contained in this Bundle.
     *
     * @return the number of mappings as an int.
     */
    public int size() {
        return map.size();
    }

    /**
     * Returns true if the mapping of this Bundle is empty, false otherwise.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Removes all elements from the mapping of this Bundle.
     */
    public void clear() {
        map.clear();
        typeMap.clear();
    }

    /**
     * Returns true if the given key is contained in the mapping
     * of this Bundle.
     *
     * @param key a String key
     * @return true if the key is part of the mapping, false otherwise
     */
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    /**
     * Returns the entry with the given key as an object.
     *
     * @param key a String key
     * @return an Object, or null
     */
    @Nullable
    public Object get(String key) {
        return map.get(key);
    }

    /**
     * Removes any entry with the given key from the mapping of this Bundle.
     *
     * @param key a String key
     */
    public void remove(String key) {
        map.remove(key);
        typeMap.remove(key);
    }

    /**
     * Inserts all mappings from the given StateBundle into this StateBundle.
     *
     * @param bundle a {@link StateBundle}
     */
    public void putAll(StateBundle bundle) {
        if(bundle.map != null) {
            map.putAll(bundle.map);
            typeMap.putAll(bundle.typeMap);
        }
    }

    /**
     * Returns a Set containing the Strings used as keys in this Bundle.
     *
     * @return a Set of String keys
     */
    public Set<String> keySet() {
        return map.keySet();
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean
     */
    public void putBoolean(@Nullable String key, boolean value) {
        map.put(key, value);
        typeMap.put(key, type_boolean);
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a byte
     */
    public void putByte(@Nullable String key, byte value) {
        map.put(key, value);
        typeMap.put(key, type_byte);
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a char
     */
    public void putChar(@Nullable String key, char value) {
        map.put(key, value);
        typeMap.put(key, type_char);
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a short
     */
    void putShort(@Nullable String key, short value) {
        map.put(key, value);
        typeMap.put(key, type_short);
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value an int
     */
    public void putInt(@Nullable String key, int value) {
        map.put(key, value);
        typeMap.put(key, type_int);
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a long
     */
    public void putLong(@Nullable String key, long value) {
        map.put(key, value);
        typeMap.put(key, type_long);
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a float
     */
    public void putFloat(@Nullable String key, float value) {
        map.put(key, value);
        typeMap.put(key, type_float);
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key   a String, or null
     * @param value a double
     */
    public void putDouble(@Nullable String key, double value) {
        map.put(key, value);
        typeMap.put(key, type_double);
    }

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String, or null
     */
    public void putString(@Nullable String key, @Nullable String value) {
        map.put(key, value);
        typeMap.put(key, type_String);
    }

    /**
     * Inserts a CharSequence value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence, or null
     */
    public void putCharSequence(@Nullable String key, @Nullable CharSequence value) {
        map.put(key, value);
        typeMap.put(key, type_CharSequence);
    }

    /**
     * Inserts an ArrayList<Integer> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<Integer> object, or null
     */
    public void putIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        map.put(key, value);
        typeMap.put(key, type_IntegerArrayList);
    }

    /**
     * Inserts an ArrayList<String> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<String> object, or null
     */
    public void putStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        map.put(key, value);
        typeMap.put(key, type_StringArrayList);
    }

    /**
     * Inserts an ArrayList<CharSequence> value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList<CharSequence> object, or null
     */
    public void putCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        map.put(key, value);
        typeMap.put(key, type_CharSequenceArrayList);
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Serializable object, or null
     */
    public void putSerializable(@Nullable String key, @Nullable Serializable value) {
        map.put(key, value);
        typeMap.put(key, type_Serializable);
    }

    /**
     * Inserts a boolean array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a boolean array object, or null
     */
    public void putBooleanArray(@Nullable String key, @Nullable boolean[] value) {
        map.put(key, value);
        typeMap.put(key, type_BooleanArray);
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a byte array object, or null
     */
    public void putByteArray(@Nullable String key, @Nullable byte[] value) {
        map.put(key, value);
        typeMap.put(key, type_ByteArray);
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a short array object, or null
     */
    public void putShortArray(@Nullable String key, @Nullable short[] value) {
        map.put(key, value);
        typeMap.put(key, type_ShortArray);
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a char array object, or null
     */
    public void putCharArray(@Nullable String key, @Nullable char[] value) {
        map.put(key, value);
        typeMap.put(key, type_CharArray);
    }

    /**
     * Inserts an int array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value an int array object, or null
     */
    public void putIntArray(@Nullable String key, @Nullable int[] value) {
        map.put(key, value);
        typeMap.put(key, type_IntArray);
    }

    /**
     * Inserts a long array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a long array object, or null
     */
    public void putLongArray(@Nullable String key, @Nullable long[] value) {
        map.put(key, value);
        typeMap.put(key, type_LongArray);
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a float array object, or null
     */
    public void putFloatArray(@Nullable String key, @Nullable float[] value) {
        map.put(key, value);
        typeMap.put(key, type_FloatArray);
    }

    /**
     * Inserts a double array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a double array object, or null
     */
    public void putDoubleArray(@Nullable String key, @Nullable double[] value) {
        map.put(key, value);
        typeMap.put(key, type_DoubleArray);
    }

    /**
     * Inserts a String array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a String array object, or null
     */
    public void putStringArray(@Nullable String key, @Nullable String[] value) {
        map.put(key, value);
        typeMap.put(key, type_StringArray);
    }

    /**
     * Inserts a CharSequence array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a CharSequence array object, or null
     */
    public void putCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        map.put(key, value);
        typeMap.put(key, type_CharSequenceArray);
    }


    /**
     * Inserts a Parcelable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Parcelable object, or null
     */
    public void putParcelable(@Nullable String key, @Nullable Parcelable value) {
        map.put(key, value);
        typeMap.put(key, type_Parcelable);
    }

    /**
     * Inserts a Size value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Size object, or null
     */
//    public void putSize(@Nullable String key, @Nullable Size value) {
//        map.put(key, value);
//        typeMap.put(key, type_Size);
//    }

    /**
     * Inserts a SizeF value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a SizeF object, or null
     */
//    public void putSizeF(@Nullable String key, @Nullable SizeF value) {
//        map.put(key, value);
//        typeMap.put(key, type_SizeF);
//    }

    /**
     * Inserts an array of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an array of Parcelable objects, or null
     */
    public void putParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        map.put(key, value);
        typeMap.put(key, type_ParcelableArray);
    }

    /**
     * Inserts a List of Parcelable values into the mapping of this Bundle,
     * replacing any existing value for the given key.  Either key or value may
     * be null.
     *
     * @param key   a String, or null
     * @param value an ArrayList of Parcelable objects, or null
     */
    public void putParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        map.put(key, value);
        typeMap.put(key, type_ParcelableArrayList);
    }

    /**
     * Inserts a SparceArray of Parcelable values into the mapping of this
     * Bundle, replacing any existing value for the given key.  Either key
     * or value may be null.
     *
     * @param key   a String, or null
     * @param value a SparseArray of Parcelable objects, or null
     */
    public void putSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        map.put(key, value);
        typeMap.put(key, type_SparseParcelableArray);
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key   a String, or null
     * @param value a Bundle object, or null
     */
    public void putBundle(@Nullable String key, @Nullable StateBundle value) {
        map.put(key, value);
        typeMap.put(key, type_StateBundle);
    }

    /**
     * Returns the value associated with the given key, or false if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a boolean value
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a boolean value
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        Object o = map.get(key);
        if(o == null) {
            return defaultValue;
        }
        try {
            return (Boolean) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Boolean", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or (byte) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a byte value
     */
    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a byte value
     */
    public Byte getByte(String key, byte defaultValue) {
        Object o = map.get(key);
        if(o == null) {
            return defaultValue;
        }
        try {
            return (Byte) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Byte", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or (char) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a char value
     */
    public char getChar(String key) {
        return getChar(key, (char) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a char value
     */
    public char getChar(String key, char defaultValue) {
        Object o = map.get(key);
        if(o == null) {
            return defaultValue;
        }
        try {
            return (Character) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Character", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or (short) 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a short value
     */
    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a short value
     */
    public short getShort(String key, short defaultValue) {
        Object o = map.get(key);
        if(o == null) {
            return defaultValue;
        }
        try {
            return (Short) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Short", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return an int value
     */
    public int getInt(String key) {
        return getInt(key, 0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return an int value
     */
    public int getInt(String key, int defaultValue) {
        Object o = map.get(key);
        if(o == null) {
            return defaultValue;
        }
        try {
            return (Integer) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Integer", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0L if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a long value
     */
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a long value
     */
    public long getLong(String key, long defaultValue) {
        Object o = map.get(key);
        if(o == null) {
            return defaultValue;
        }
        try {
            return (Long) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Long", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0.0f if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a float value
     */
    public float getFloat(String key) {
        return getFloat(key, 0.0f);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a float value
     */
    public float getFloat(String key, float defaultValue) {
        Object o = map.get(key);
        if(o == null) {
            return defaultValue;
        }
        try {
            return (Float) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Float", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or 0.0 if
     * no mapping of the desired type exists for the given key.
     *
     * @param key a String
     * @return a double value
     */
    public double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key.
     *
     * @param key          a String
     * @param defaultValue Value to return if key does not exist
     * @return a double value
     */
    public double getDouble(String key, double defaultValue) {
        Object o = map.get(key);
        if(o == null) {
            return defaultValue;
        }
        try {
            return (Double) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Double", defaultValue, e);
            return defaultValue;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a String value, or null
     */
    @Nullable
    public String getString(@Nullable String key) {
        final Object o = map.get(key);
        try {
            return (String) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "String", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key or if a null
     * value is explicitly associated with the given key.
     *
     * @param key          a String, or null
     * @param defaultValue Value to return if key does not exist or if a null
     *                     value is associated with the given key.
     * @return the String value associated with the given key, or defaultValue
     * if no valid String object is currently mapped to that key.
     */
    public String getString(@Nullable String key, String defaultValue) {
        final String s = getString(key);
        return (s == null) ? defaultValue : s;
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a CharSequence value, or null
     */
    @Nullable
    public CharSequence getCharSequence(@Nullable String key) {
        final Object o = map.get(key);
        try {
            return (CharSequence) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "CharSequence", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or defaultValue if
     * no mapping of the desired type exists for the given key or if a null
     * value is explicitly associated with the given key.
     *
     * @param key          a String, or null
     * @param defaultValue Value to return if key does not exist or if a null
     *                     value is associated with the given key.
     * @return the CharSequence value associated with the given key, or defaultValue
     * if no valid CharSequence object is currently mapped to that key.
     */
    public CharSequence getCharSequence(@Nullable String key, CharSequence defaultValue) {
        final CharSequence cs = getCharSequence(key);
        return (cs == null) ? defaultValue : cs;
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Serializable value, or null
     */
    @Nullable
    public Serializable getSerializable(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (Serializable) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Serializable", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<String> value, or null
     */
    @Nullable
    public ArrayList<Integer> getIntegerArrayList(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (ArrayList<Integer>) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "ArrayList<Integer>", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<String> value, or null
     */
    @Nullable
    public ArrayList<String> getStringArrayList(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (ArrayList<String>) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "ArrayList<String>", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<CharSequence> value, or null
     */
    @Nullable
    public ArrayList<CharSequence> getCharSequenceArrayList(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (ArrayList<CharSequence>) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "ArrayList<CharSequence>", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a boolean[] value, or null
     */
    @Nullable
    public boolean[] getBooleanArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (boolean[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "byte[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a byte[] value, or null
     */
    @Nullable
    public byte[] getByteArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (byte[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "byte[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a short[] value, or null
     */
    @Nullable
    public short[] getShortArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (short[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "short[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a char[] value, or null
     */
    @Nullable
    public char[] getCharArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (char[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "char[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an int[] value, or null
     */
    @Nullable
    public int[] getIntArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (int[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "int[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a long[] value, or null
     */
    @Nullable
    public long[] getLongArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (long[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "long[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a float[] value, or null
     */
    @Nullable
    public float[] getFloatArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (float[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "float[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a double[] value, or null
     */
    @Nullable
    public double[] getDoubleArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (double[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "double[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a String[] value, or null
     */
    @Nullable
    public String[] getStringArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (String[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "String[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a CharSequence[] value, or null
     */
    @Nullable
    public CharSequence[] getCharSequenceArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (CharSequence[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "CharSequence[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Size value, or null
     */
//    @Nullable
//    public Size getSize(@Nullable String key) {
//        final Object o = map.get(key);
//        try {
//            return (Size) o;
//        } catch(ClassCastException e) {
//            typeWarning(key, o, "Size", e);
//            return null;
//        }
//    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Size value, or null
     */
//    @Nullable
//    public SizeF getSizeF(@Nullable String key) {
//        final Object o = map.get(key);
//        try {
//            return (SizeF) o;
//        } catch(ClassCastException e) {
//            typeWarning(key, o, "SizeF", e);
//            return null;
//        }
//    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Bundle value, or null
     */
    @Nullable
    public StateBundle getBundle(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (StateBundle) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Bundle", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Parcelable value, or null
     */
    @Nullable
    public <T extends Parcelable> T getParcelable(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (T) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Parcelable", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a Parcelable[] value, or null
     */
    @Nullable
    public Parcelable[] getParcelableArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (Parcelable[]) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "Parcelable[]", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return an ArrayList<T> value, or null
     */
    @Nullable
    public <T extends Parcelable> ArrayList<T> getParcelableArrayList(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (ArrayList<T>) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "ArrayList", e);
            return null;
        }
    }

    /**
     * Returns the value associated with the given key, or null if
     * no mapping of the desired type exists for the given key or a null
     * value is explicitly associated with the key.
     *
     * @param key a String, or null
     * @return a SparseArray of T values, or null
     */
    @Nullable
    public <T extends Parcelable> SparseArray<T> getSparseParcelableArray(@Nullable String key) {
        Object o = map.get(key);
        if(o == null) {
            return null;
        }
        try {
            return (SparseArray<T>) o;
        } catch(ClassCastException e) {
            typeWarning(key, o, "SparseArray", e);
            return null;
        }
    }

    // Log a message if the value was non-null but not of the expected type
    void typeWarning(String key, Object value, String className, Object defaultValue, ClassCastException e) {
        StringBuilder sb = new StringBuilder();
        sb.append("Key ");
        sb.append(key);
        sb.append(" expected ");
        sb.append(className);
        sb.append(" but value was a ");
        sb.append(value.getClass().getName());
        sb.append(".  The default value ");
        sb.append(defaultValue);
        sb.append(" was returned.");
        System.out.print(sb.toString());
    }

    void typeWarning(String key, Object value, String className, ClassCastException e) {
        typeWarning(key, value, className, "<null>", e);
    }

    @NonNull
    public Bundle toBundle() {
        Bundle rootBundle = new Bundle();
        ArrayList<Parcelable> types = new ArrayList<>();

        Bundle innerBundle = new Bundle();
        for(String key : map.keySet()) {
            types.add(new TypeElement(key, typeMap.get(key)));
            switch(typeMap.get(key)) {
                case type_boolean:
                    innerBundle.putBoolean(key, getBoolean(key));
                    break;
                case type_byte:
                    innerBundle.putByte(key, getByte(key));
                    break;
                case type_char:
                    innerBundle.putChar(key, getChar(key));
                    break;
                case type_short:
                    innerBundle.putShort(key, getShort(key));
                    break;
                case type_int:
                    innerBundle.putInt(key, getInt(key));
                    break;
                case type_long:
                    innerBundle.putLong(key, getLong(key));
                    break;
                case type_float:
                    innerBundle.putFloat(key, getFloat(key));
                    break;
                case type_double:
                    innerBundle.putDouble(key, getDouble(key));
                    break;
                case type_String:
                    innerBundle.putString(key, getString(key));
                    break;
                case type_CharSequence:
                    innerBundle.putCharSequence(key, getCharSequence(key));
                    break;
                case type_Serializable:
                    innerBundle.putSerializable(key, getSerializable(key));
                    break;
                case type_IntegerArrayList:
                    innerBundle.putIntegerArrayList(key, getIntegerArrayList(key));
                    break;
                case type_StringArrayList:
                    innerBundle.putStringArrayList(key, getStringArrayList(key));
                    break;
                case type_CharSequenceArrayList:
                    innerBundle.putCharSequenceArrayList(key, getCharSequenceArrayList(key));
                    break;
                case type_BooleanArray:
                    innerBundle.putBooleanArray(key, getBooleanArray(key));
                    break;
                case type_ByteArray:
                    innerBundle.putByteArray(key, getByteArray(key));
                    break;
                case type_ShortArray:
                    innerBundle.putShortArray(key, getShortArray(key));
                    break;
                case type_CharArray:
                    innerBundle.putCharArray(key, getCharArray(key));
                    break;
                case type_IntArray:
                    innerBundle.putIntArray(key, getIntArray(key));
                    break;
                case type_LongArray:
                    innerBundle.putLongArray(key, getLongArray(key));
                    break;
                case type_FloatArray:
                    innerBundle.putFloatArray(key, getFloatArray(key));
                    break;
                case type_DoubleArray:
                    innerBundle.putDoubleArray(key, getDoubleArray(key));
                    break;
                case type_StringArray:
                    innerBundle.putStringArray(key, getStringArray(key));
                    break;
                case type_CharSequenceArray:
                    innerBundle.putCharSequenceArray(key, getCharSequenceArray(key));
                    break;
                case type_StateBundle:
                    innerBundle.putBundle(key, getBundle(key) == null ? null : getBundle(key).toBundle());
                    break;
                case type_Parcelable:
                    innerBundle.putParcelable(key, getParcelable(key));
                    break;
                case type_ParcelableArray:
                    innerBundle.putParcelableArray(key, getParcelableArray(key));
                    break;
                case type_ParcelableArrayList:
                    innerBundle.putParcelableArrayList(key, getParcelableArrayList(key));
                    break;
                case type_SparseParcelableArray:
                    innerBundle.putSparseParcelableArray(key, getSparseParcelableArray(key));
                    break;
            }
        }
        rootBundle.putParcelableArrayList("___TYPES", types);
        rootBundle.putBundle("___INNER_BUNDLE", innerBundle);
        rootBundle.putBoolean("______ROOT_FLAG", true);
        return rootBundle;
    }

    @Nullable
    public static StateBundle from(@Nullable Bundle rootBundle) {
        if(rootBundle == null) {
            return null;
        }
        if(!rootBundle.getBoolean("______ROOT_FLAG")) {
            throw new IllegalArgumentException(
                    "The provided Bundle must be the root bundle of a StateBundle, obtained via `StateBundle.toBundle()`.");
        }
        StateBundle stateBundle = new StateBundle();
        Bundle innerBundle = rootBundle.getBundle("___INNER_BUNDLE");
        ArrayList<TypeElement> typeElements = rootBundle.getParcelableArrayList("___TYPES");
        if(typeElements != null && innerBundle != null) {
            for(TypeElement typeElement : typeElements) {
                switch(typeElement.type) {
                    case type_boolean:
                        stateBundle.putBoolean(typeElement.key, innerBundle.getBoolean(typeElement.key));
                        break;
                    case type_byte:
                        stateBundle.putByte(typeElement.key, innerBundle.getByte(typeElement.key));
                        break;
                    case type_char:
                        stateBundle.putChar(typeElement.key, innerBundle.getChar(typeElement.key));
                        break;
                    case type_short:
                        stateBundle.putShort(typeElement.key, innerBundle.getShort(typeElement.key));
                        break;
                    case type_int:
                        stateBundle.putInt(typeElement.key, innerBundle.getInt(typeElement.key));
                        break;
                    case type_long:
                        stateBundle.putLong(typeElement.key, innerBundle.getLong(typeElement.key));
                        break;
                    case type_float:
                        stateBundle.putFloat(typeElement.key, innerBundle.getFloat(typeElement.key));
                        break;
                    case type_double:
                        stateBundle.putDouble(typeElement.key, innerBundle.getDouble(typeElement.key));
                        break;
                    case type_String:
                        stateBundle.putString(typeElement.key, innerBundle.getString(typeElement.key));
                        break;
                    case type_CharSequence:
                        stateBundle.putCharSequence(typeElement.key, innerBundle.getCharSequence(typeElement.key));
                        break;
                    case type_Serializable:
                        stateBundle.putSerializable(typeElement.key, innerBundle.getSerializable(typeElement.key));
                        break;
                    case type_IntegerArrayList:
                        stateBundle.putIntegerArrayList(typeElement.key, innerBundle.getIntegerArrayList(typeElement.key));
                        break;
                    case type_StringArrayList:
                        stateBundle.putStringArrayList(typeElement.key, innerBundle.getStringArrayList(typeElement.key));
                        break;
                    case type_CharSequenceArrayList:
                        stateBundle.putCharSequenceArrayList(typeElement.key, innerBundle.getCharSequenceArrayList(typeElement.key));
                        break;
                    case type_BooleanArray:
                        stateBundle.putBooleanArray(typeElement.key, innerBundle.getBooleanArray(typeElement.key));
                        break;
                    case type_ByteArray:
                        stateBundle.putByteArray(typeElement.key, innerBundle.getByteArray(typeElement.key));
                        break;
                    case type_ShortArray:
                        stateBundle.putShortArray(typeElement.key, innerBundle.getShortArray(typeElement.key));
                        break;
                    case type_CharArray:
                        stateBundle.putCharArray(typeElement.key, innerBundle.getCharArray(typeElement.key));
                        break;
                    case type_IntArray:
                        stateBundle.putIntArray(typeElement.key, innerBundle.getIntArray(typeElement.key));
                        break;
                    case type_LongArray:
                        stateBundle.putLongArray(typeElement.key, innerBundle.getLongArray(typeElement.key));
                        break;
                    case type_FloatArray:
                        stateBundle.putFloatArray(typeElement.key, innerBundle.getFloatArray(typeElement.key));
                        break;
                    case type_DoubleArray:
                        stateBundle.putDoubleArray(typeElement.key, innerBundle.getDoubleArray(typeElement.key));
                        break;
                    case type_StringArray:
                        stateBundle.putStringArray(typeElement.key, innerBundle.getStringArray(typeElement.key));
                        break;
                    case type_CharSequenceArray:
                        stateBundle.putCharSequenceArray(typeElement.key, innerBundle.getCharSequenceArray(typeElement.key));
                        break;
                    case type_StateBundle:
                        stateBundle.putBundle(typeElement.key, StateBundle.from(innerBundle.getBundle(typeElement.key)));
                        break;
                    case type_Parcelable:
                        stateBundle.putParcelable(typeElement.key, innerBundle.getParcelable(typeElement.key));
                        break;
                    case type_ParcelableArray:
                        stateBundle.putParcelableArray(typeElement.key, innerBundle.getParcelableArray(typeElement.key));
                        break;
                    case type_ParcelableArrayList:
                        stateBundle.putParcelableArrayList(typeElement.key, innerBundle.getParcelableArrayList(typeElement.key));
                        break;
                    case type_SparseParcelableArray:
                        stateBundle.putSparseParcelableArray(typeElement.key, innerBundle.getSparseParcelableArray(typeElement.key));
                        break;
                }
            }
        }
        return stateBundle;
    }

    static class TypeElement
            implements Parcelable {
        String key;
        int type;

        TypeElement() {
        }

        TypeElement(String key, int type) {
            this.key = key;
            this.type = type;
        }

        protected TypeElement(Parcel in) {
            key = in.readString();
            type = in.readInt();
        }

        public static final Creator<TypeElement> CREATOR = new Creator<TypeElement>() {
            @Override
            public TypeElement createFromParcel(Parcel in) {
                return new TypeElement(in);
            }

            @Override
            public TypeElement[] newArray(int size) {
                return new TypeElement[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(key);
            dest.writeInt(type);
        }
    }
}