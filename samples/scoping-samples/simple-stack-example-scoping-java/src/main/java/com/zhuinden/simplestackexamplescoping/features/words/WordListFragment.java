package com.zhuinden.simplestackexamplescoping.features.words;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhuinden.eventemitter.EventSource;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.navigator.Navigator;
import com.zhuinden.simplestackexamplescoping.R;
import com.zhuinden.simplestackextensions.fragments.KeyedFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WordListFragment
        extends KeyedFragment {
    public WordListFragment() {
        super(R.layout.word_list_view);
    }

    public interface ActionHandler {
        public void onAddNewWordClicked();
    }

    public interface DataProvider {
        public LiveData<List<String>> getWordList();
    }

    private ActionHandler actionHandler;

    private DataProvider dataProvider;
    private EventSource<WordController.Events> controllerEvents;

    private WordListAdapter adapter = new WordListAdapter();

    private EventSource.NotificationToken notificationToken = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Backstack backstack = Navigator.getBackstack(requireContext());
        dataProvider = backstack.lookupService(DataProvider.class.getName());
        actionHandler = backstack.lookupService(ActionHandler.class.getName());
        controllerEvents = backstack.lookupService("WordEventEmitter");

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.buttonGoToAddNewWord).setOnClickListener(v -> actionHandler.onAddNewWordClicked());

        dataProvider.getWordList().observe(getViewLifecycleOwner(), words -> adapter.updateWords(words));
    }

    @Override
    public void onStart() {
        super.onStart();

        notificationToken = controllerEvents.startListening(event -> {
            if(event instanceof WordController.Events.NewWordAdded) {
                Toast.makeText(requireContext(), "Added " + ((WordController.Events.NewWordAdded) event).getWord(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStop() {
        if(notificationToken != null) {
            notificationToken.stopListening();
            notificationToken = null;
        }

        super.onStop();
    }
}
