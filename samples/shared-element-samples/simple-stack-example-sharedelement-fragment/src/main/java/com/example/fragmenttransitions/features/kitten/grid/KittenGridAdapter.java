package com.example.fragmenttransitions.features.kitten.grid;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fragmenttransitions.R;

import org.jetbrains.annotations.NotNull;

/**
 * Adapts Views containing kittens to RecyclerView cells
 *
 * @author bherbst
 */
public class KittenGridAdapter
        extends RecyclerView.Adapter<KittenViewHolder> {
    private final int size;

    /**
     * Constructor
     *
     * @param size The number of kittens to show
     */
    public KittenGridAdapter(int size) {
        this.size = size;
    }

    @NotNull
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
        return size;
    }

}
