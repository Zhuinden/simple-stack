package com.zhuinden.simplestack;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This is internal implementation detail, but I want to test it anyway.
 */
public class CollectionHelperTest {
    class PlaceholderClass {
        private String a;
        private String b;

        public PlaceholderClass(String a, String b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            PlaceholderClass that = (PlaceholderClass) o;

            if(a != null ? !a.equals(that.a) : that.a != null) {
                return false;
            }
            return b != null ? b.equals(that.b) : that.b == null;
        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            return result;
        }

        public String getA() {
            return a;
        }

        public String getB() {
            return b;
        }
    }

    @Test
    public void retainAllWorksAsIntended() {
        Set<PlaceholderClass> items = new LinkedHashSet<>();
        items.add(new PlaceholderClass("aaaaaaaa1", "bbbbbbb1"));
        items.add(new PlaceholderClass("aaaaaaaa2", "bbbbbbb2"));
        items.add(new PlaceholderClass("aaaaaaaa3", "bbbbbbb3"));
        items.add(new PlaceholderClass("aaaaaaaa4", "bbbbbbb4"));
        items.add(new PlaceholderClass("aaaaaaaa5", "bbbbbbb5"));

        List<PlaceholderClass> list = new ArrayList<>();
        list.add(new PlaceholderClass("aaaaaaaa1", "bbbbbbb1"));
        list.add(new PlaceholderClass("aaaaaaaa5", "bbbbbbb5"));

        CollectionHelper.retainAll(items, list);

        assertThat(items).containsExactly(new PlaceholderClass("aaaaaaaa1", "bbbbbbb1"),
                                          new PlaceholderClass("aaaaaaaa5", "bbbbbbb5"));
    }
}
