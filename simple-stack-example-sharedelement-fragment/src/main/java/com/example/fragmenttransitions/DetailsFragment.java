package com.example.fragmenttransitions;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Display details for a given kitten
 *
 * @author bherbst
 */
public class DetailsFragment
        extends BaseFragment
        implements HasSharedElement.Target {
    @BindView(R.id.image)
    ImageView image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        DetailsKey key = getKey();
        int kittenNumber = key.kittenNumber();

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

    @Override
    public SharedElement sharedElement() {
        DetailsKey detailsKey = getKey();
        return SharedElement.create(
                ViewCompat.getTransitionName(image) /* inverse new source */,
                detailsKey.sharedElement().sourceTransitionName() /* inverse old source */
        );
    }
}
