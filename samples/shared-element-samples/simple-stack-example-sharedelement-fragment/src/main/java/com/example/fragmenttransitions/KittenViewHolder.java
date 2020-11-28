package com.example.fragmenttransitions;

import android.view.View;
import android.widget.ImageView;

import com.zhuinden.simplestack.navigator.Navigator;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder for kitten cells in our grid
 *
 * @author bherbst
 */
public class KittenViewHolder
        extends RecyclerView.ViewHolder {
    private final ImageView image;

    public KittenViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image);
    }

    public void bind(int position) {
        switch(position % 6) {
            case 0:
                image.setImageResource(R.drawable.placekitten_1);
                break;
            case 1:
                image.setImageResource(R.drawable.placekitten_2);
                break;
            case 2:
                image.setImageResource(R.drawable.placekitten_3);
                break;
            case 3:
                image.setImageResource(R.drawable.placekitten_4);
                break;
            case 4:
                image.setImageResource(R.drawable.placekitten_5);
                break;
            case 5:
                image.setImageResource(R.drawable.placekitten_6);
                break;
        }

        // It is important that each shared element in the source screen has a unique transition name.
        // For example, we can't just give all the images in our grid the transition name "kittenImage"
        // because then we would have conflicting transition names.
        // By appending "_image" to the position, we can support having multiple shared elements in each
        // grid cell in the future.
        ViewCompat.setTransitionName(image, position + "_image");

        image.setOnClickListener(v -> {
            int kittenNumber = (position % 6) + 1;

            // we cannot risk the transition name being nulled out, so we'll just set it again...
            ViewCompat.setTransitionName(image, position + "_image");

            DetailsKey detailsKey = DetailsKey.create(SharedElement.create(ViewCompat.getTransitionName(image), "kittenImage"), kittenNumber);
            Navigator.getBackstack(v.getContext()).goTo(detailsKey);
        });
    }
}
