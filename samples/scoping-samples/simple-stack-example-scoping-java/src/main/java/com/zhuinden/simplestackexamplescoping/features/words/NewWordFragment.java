package com.zhuinden.simplestackexamplescoping.features.words;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestackexamplescoping.R;
import com.zhuinden.simplestackextensions.fragments.KeyedFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NewWordFragment
        extends KeyedFragment {
    public NewWordFragment() {
        super(R.layout.new_word_fragment);
    }

    public interface ActionHandler {
        void onAddWordClicked(String word);
    }

    private ActionHandler actionHandler;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Backstack backstack = Navigator.getBackstack(requireContext());
        actionHandler = backstack.lookupService(ActionHandler.class.getName());

        view.findViewById(R.id.buttonAddNewWord).setOnClickListener(v -> {
            String word = view.<TextView>findViewById(R.id.textInputNewWord).getText().toString().trim();
            actionHandler.onAddWordClicked(word);
        });
    }
}
