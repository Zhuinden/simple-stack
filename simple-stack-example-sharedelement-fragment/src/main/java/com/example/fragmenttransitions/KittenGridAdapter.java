package com.example.fragmenttransitions;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Adapts Views containing kittens to RecyclerView cells
 *
 * @author bherbst
 */
public class KittenGridAdapter
        extends RecyclerView.Adapter<KittenViewHolder> {
    private final int mSize;

    /**
     * Constructor
     *
     * @param size The number of kittens to show
     */
    public KittenGridAdapter(int size) {
        mSize = size;
    }

    @Override
    public KittenViewHolder onCreateViewHolder(ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(container.getContext());
        View cell = inflater.inflate(R.layout.grid_item, container, false);
        return new KittenViewHolder(cell);
    }

    @Override
    public void onBindViewHolder(final KittenViewHolder viewHolder, final int position) {
        viewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mSize;
    }

}
