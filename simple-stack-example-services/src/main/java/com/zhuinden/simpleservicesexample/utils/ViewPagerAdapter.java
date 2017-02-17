package com.zhuinden.simpleservicesexample.utils;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.zhuinden.simpleservicesexample.application.Key;
import java.util.List;

/**
 * Created by Zhuinden on 2017.02.14..
 */

public class ViewPagerAdapter
        extends PagerAdapter {
    private List<Key> keys;

    public ViewPagerAdapter(List<Key> keys) {
        if(keys == null) {
            throw new NullPointerException();
        }
        this.keys = keys;
    }

    public void updateKeys(List<Key> keys) {
        this.keys = keys;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return keys.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Key key = keys.get(position);
        View view = LayoutInflater.from(StackService.getDelegate(container.getContext()).createContext(container.getContext(), key)).inflate(key.layout(), container, false);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
