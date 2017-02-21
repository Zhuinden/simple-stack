package com.zhuinden.simplestack;

import android.os.Parcelable;
import android.util.SparseArray;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.Serializable;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Owner on 2017. 02. 21..
 */

public class StateBundleTest {
    @Test
    public void testBooleanIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putBoolean("key", false);
        boolean val = stateBundle.getBoolean("key");
        assertThat(val).isEqualTo(false);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_boolean);
    }

    @Test
    public void testByteIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putByte("key", (byte) 0x01);
        byte val = stateBundle.getByte("key");
        assertThat(val).isEqualTo((byte) 0x01);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_byte);
    }

    @Test
    public void testCharIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putChar("key", 'C');
        char val = stateBundle.getChar("key");
        assertThat(val).isEqualTo('C');
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_char);
    }

    @Test
    public void testShortIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putShort("key", (short) 5);
        short val = stateBundle.getShort("key");
        assertThat(val).isEqualTo((short) 5);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_short);
    }

    @Test
    public void testIntIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putInt("key", 5);
        int val = stateBundle.getInt("key");
        assertThat(val).isEqualTo(5);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_int);
    }

    @Test
    public void testLongIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putLong("key", 4L);
        long val = stateBundle.getLong("key");
        assertThat(val).isEqualTo(4L);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_long);
    }

    @Test
    public void testFloatIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putFloat("key", 0.25f);
        float val = stateBundle.getFloat("key");
        assertThat(val).isEqualTo(0.25f);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_float);
    }

    @Test
    public void testDoubleIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putDouble("key", 0.25);
        double val = stateBundle.getDouble("key");
        assertThat(val).isEqualTo(0.25);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_double);
    }

    @Test
    public void testStringIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putString("key", "asd");
        String val = stateBundle.getString("key");
        assertThat(val).isEqualTo("asd");
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_String);
    }

    @Test
    public void testCharSequenceIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putCharSequence("key", "asd");
        CharSequence val = stateBundle.getCharSequence("key");
        assertThat(val).isEqualTo("asd");
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_CharSequence);
    }

    @Test
    public void testSerializableIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putSerializable("key", 5);
        Serializable val = stateBundle.getSerializable("key");
        assertThat(val).isEqualTo(5);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_Serializable);
    }

    @Test
    public void testIntegerArrayListIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putIntegerArrayList("key", new ArrayList<Integer>() {{
            add(5);
        }});
        ArrayList<Integer> val = stateBundle.getIntegerArrayList("key");
        assertThat(val).containsExactly(5);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_IntegerArrayList);
    }

    @Test
    public void testStringArrayListIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putStringArrayList("key", new ArrayList<String>() {{
            add("asd");
            add("def");
        }});
        ArrayList<String> val = stateBundle.getStringArrayList("key");
        assertThat(val).containsExactly("asd", "def");
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_StringArrayList);
    }

    @Test
    public void testCharSequenceArrayListIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putCharSequenceArrayList("key", new ArrayList<CharSequence>() {{
            add("asd");
            add("def");
        }});
        ArrayList<CharSequence> val = stateBundle.getCharSequenceArrayList("key");
        assertThat(val).containsExactly("asd", "def");
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_CharSequenceArrayList);
    }

    @Test
    public void testBooleanArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putBooleanArray("key", new boolean[]{true, false});
        boolean[] val = stateBundle.getBooleanArray("key");
        assertThat(val).containsExactly(true, false);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_BooleanArray);
    }

    @Test
    public void testByteArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putByteArray("key", new byte[]{(byte) 0x01, (byte) 0x02});
        byte[] val = stateBundle.getByteArray("key");
        assertThat(val).containsExactly((byte) 0x01, (byte) 0x02);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_ByteArray);
    }

    @Test
    public void testShortArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putShortArray("key", new short[]{(short) 1, (short) 2});
        short[] val = stateBundle.getShortArray("key");
        assertThat(val).containsExactly((short) 1, (short) 2);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_ShortArray);
    }

    @Test
    public void testCharArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putCharArray("key", new char[]{'A', 'B'});
        char[] val = stateBundle.getCharArray("key");
        assertThat(val).containsExactly('A', 'B');
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_CharArray);
    }

    @Test
    public void testIntArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putIntArray("key", new int[]{1, 2});
        int[] val = stateBundle.getIntArray("key");
        assertThat(val).containsExactly(1, 2);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_IntArray);
    }

    @Test
    public void testLongArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putLongArray("key", new long[]{1L, 2L});
        long[] val = stateBundle.getLongArray("key");
        assertThat(val).containsExactly(1L, 2L);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_LongArray);
    }

    @Test
    public void testFloatArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putFloatArray("key", new float[]{0.25f, 0.5f});
        float[] val = stateBundle.getFloatArray("key");
        assertThat(val).containsExactly(0.25f, 0.5f);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_FloatArray);
    }

    @Test
    public void testDoubleArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putDoubleArray("key", new double[]{0.25, 0.5});
        double[] val = stateBundle.getDoubleArray("key");
        assertThat(val).containsExactly(0.25, 0.5);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_DoubleArray);
    }

    @Test
    public void testStringArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putStringArray("key", new String[]{"asd", "def"});
        String[] val = stateBundle.getStringArray("key");
        assertThat(val).containsExactly("asd", "def");
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_StringArray);
    }

    @Test
    public void testCharSequenceArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        stateBundle.putCharSequenceArray("key", new String[]{"asd", "def"});
        CharSequence[] val = stateBundle.getCharSequenceArray("key");
        assertThat(val).containsExactly("asd", "def");
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_CharSequenceArray);
    }

    @Test
    public void testStateBundleIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        StateBundle otherBundle = new StateBundle();
        otherBundle.putString("hello", "world");
        stateBundle.putBundle("key", otherBundle);
        StateBundle val = stateBundle.getBundle("key");
        assertThat(val).isEqualTo(otherBundle);
        assertThat(val.getString("hello")).isEqualTo("world");
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_StateBundle);
    }

    @Test
    public void testParcelableIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        TestKey testKey = new TestKey("hello");
        stateBundle.putParcelable("key", testKey);
        Parcelable val = stateBundle.getParcelable("key");
        assertThat(val).isEqualTo(testKey);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_Parcelable);
    }

    @Test
    public void testParcelableArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        TestKey testKey = new TestKey("hello");
        TestKey testKey2 = new TestKey("world");
        stateBundle.putParcelableArray("key", new Parcelable[]{testKey, testKey2});
        Parcelable[] val = stateBundle.getParcelableArray("key");
        assertThat(val).containsExactly(testKey, testKey2);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_ParcelableArray);
    }

    @Test
    public void testParcelableArrayListIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        final TestKey testKey = new TestKey("hello");
        final TestKey testKey2 = new TestKey("world");
        stateBundle.putParcelableArrayList("key", new ArrayList<Parcelable>() {{
            add(testKey);
            add(testKey2);
        }});
        ArrayList<Parcelable> val = stateBundle.getParcelableArrayList("key");
        assertThat(val).containsExactly(testKey, testKey2);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_ParcelableArrayList);
    }

    @Test
    public void testSparseParcelableArrayIsPutAndRetrieved() {
        StateBundle stateBundle = new StateBundle();
        final TestKey testKey = new TestKey("hello");
        final TestKey testKey2 = new TestKey("world");
        SparseArray<Parcelable> parcelableSparseArray = Mockito.mock(SparseArray.class);
        Mockito.when(parcelableSparseArray.get(1)).thenReturn(testKey);
        Mockito.when(parcelableSparseArray.get(2)).thenReturn(testKey2);
        parcelableSparseArray.put(1, testKey);
        parcelableSparseArray.put(2, testKey);
        stateBundle.putSparseParcelableArray("key", parcelableSparseArray);
        SparseArray<Parcelable> val = stateBundle.getSparseParcelableArray("key");
        assertThat(val.get(1)).isEqualTo(testKey);
        assertThat(val.get(2)).isEqualTo(testKey2);
        assertThat(stateBundle.typeMap.get("key")).isEqualTo(StateBundle.type_SparseParcelableArray);
    }
}
