# Simple Stack Demo

This is a simple backstack implementation that will serve as basis for a series of Medium articles.

## What is it?

Currently it's 3 files (and 5 classes):

- [BackStack](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/Backstack.java): exposes operators for manipulating the backstack, and stores current history.
- [StateChanger](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/StateChanger.java): interface for a class that listens to changes inside the Backstack.
- [StateChange](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/StateChange.java): represents a savedState change inside the backstack, providing previous savedState, new savedState, and the direction of the change.

- [StateChange.Direction](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/StateChange.java): represents the direction of the change.
- [StateChanger.Callback](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/StateChanger.java): the callback that signals to the backstack that the savedState change is complete.

## Operators

The Backstack provides 3 convenient operators for manipulating savedState.

- `goTo()`: if savedState does not previously exist in the backstack, then adds it to the stack. Otherwise navigate back to given savedState.
- `goBack()`: returns boolean if savedState change is in progress, or if there are more than 1 entries in history (and handled the back press). Otherwise, return false.
- `setHistory()`: sets the savedState to the provided elements.

## What does it do?

Currently, the backstack stores the screens, and persists them across configuration change / process death.

The backstack also allows navigation between the savedStates, and enables handling this savedState change using the `StateChanger`.

## How does it work?

The backstack must be initialized with at least one initial savedState, and a savedState changer must be set when it is able to handle the savedState change.

Setting a savedState changer begins an `initialization` (in Flow terms, a bootstrap traversal), which provides a savedState change in form of `{[], [{...}, {...}]}`.

Afterwards, the backstack operators allow changing between savedStates.

``` java
public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    public static final String BACKSTACK = "BACKSTACK";
    public static final String STATES = "STATES";

    @BindView(R.id.root)
    RelativeLayout root;

    Backstack backstack;

    Map<Key, SavedState> keyStateMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<Parcelable> keys;
        if(savedInstanceState != null) {
            keys = savedInstanceState.getParcelableArrayList(BACKSTACK);
            List<SavedState> savedStates = savedInstanceState.getParcelableArrayList(STATES);
            if(savedStates != null) {
                for(SavedState savedState : savedStates) {
                    keyStateMap.put(savedState.getKey(), savedState);
                }
            }
        } else {
            keys = HistoryBuilder.single(new FirstKey());
        }
        backstack = (Backstack) getLastCustomNonConfigurationInstance();
        if(backstack == null) {
            backstack = new Backstack(keys);
        }
        backstack.setStateChanger(this, Backstack.INITIALIZE);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstack;
    }

    @Override
    public void onBackPressed() {
        if(!backstack.goBack()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(root != null) {
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            root.getChildAt(0).saveHierarchyState(viewHierarchyState);
            Key currentKey = KeyContextWrapper.getKey(root.getChildAt(0).getContext());
            SavedState currentSavedState = SavedState.builder()
                    .setKey(currentKey)
                    .setViewHierarchyState(viewHierarchyState)
                    .build();
            keyStateMap.put(currentKey, currentSavedState);
        }
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BACKSTACK, HistoryBuilder.from(backstack.getHistory()).build());
        outState.putParcelableArrayList(STATES, new ArrayList<>(keyStateMap.values()));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(!backstack.hasStateChanger()) {
            backstack.setStateChanger(this, Backstack.REATTACH);
        }
    }

    @Override
    protected void onPause() {
        if(backstack.hasStateChanger()) {
            backstack.removeStateChanger();
        }
        super.onPause();
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        if(stateChange.topPreviousState() != null) {
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            Key previousKey = stateChange.topPreviousState();
            root.getChildAt(0).saveHierarchyState(viewHierarchyState);
            SavedState previousSavedState = SavedState.builder()
                    .setKey(previousKey)
                    .setViewHierarchyState(viewHierarchyState)
                    .build();
            keyStateMap.put(previousKey, previousSavedState);
        }
        root.removeAllViews();
        Key newKey = stateChange.topNewState();
        Context newContext = new KeyContextWrapper(this, newKey);
        View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        if(keyStateMap.containsKey(newKey)) {
            SavedState savedState = keyStateMap.get(newKey);
            view.restoreHierarchyState(savedState.getViewHierarchyState());
        }
        root.addView(view);
        keyStateMap.keySet().retainAll(stateChange.getNewState());
        completionCallback.stateChangeComplete();
    }

    @Override
    public Object getSystemService(String name) {
        if(BACKSTACK.equals(name)) {
            return backstack;
        }
        return super.getSystemService(name);
    }
}
```