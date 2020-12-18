package com.example.fragmenttransitions.features.kitten.grid;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fragmenttransitions.R;
import com.example.fragmenttransitions.core.navigation.BaseFragment;

/**
 * Displays a grid of pictures
 *
 * @author bherbst
 */
public class KittenGridFragment
        extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(new KittenGridAdapter(6));
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }
}
