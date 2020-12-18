package com.example.fragmenttransitions.features.kitten.details;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.fragmenttransitions.R;
import com.example.fragmenttransitions.core.navigation.BaseFragment;

/**
 * Display details for a given kitten
 *
 * @author bherbst
 */
public class KittenDetailsFragment
        extends BaseFragment {
    public KittenDetailsFragment() {
        super(R.layout.details_fragment);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        KittenDetailsKey key = getKey();
        int kittenNumber = key.kittenNumber();

        ImageView image = view.findViewById(R.id.image);
        switch(kittenNumber) {
            case 1:
                image.setImageResource(R.drawable.placekitten_1);
                break;
            case 2:
                image.setImageResource(R.drawable.placekitten_2);
                break;
            case 3:
                image.setImageResource(R.drawable.placekitten_3);
                break;
            case 4:
                image.setImageResource(R.drawable.placekitten_4);
                break;
            case 5:
                image.setImageResource(R.drawable.placekitten_5);
                break;
            case 6:
                image.setImageResource(R.drawable.placekitten_6);
                break;
        }
    }
}
