# Simple Stack Demo

This is a simple backstack implementation that will serve as basis for a series of Medium articles.

- [Part 1: Creating a basic backstack](https://medium.com/@Zhuinden/towards-a-fragmentless-world-creating-a-flow-like-custom-backstack-part-1-cf551ebda624#.wkshdkeb6)
- [Part 2: Sharing data to custom views using `getSystemService()` and `ContextWrapper`](https://medium.com/@Zhuinden/data-and-service-sharing-to-custom-views-with-contextwrappers-and-getsystemservice-creating-a-flow-aedeabbd9567#.43l4qxahe)
- [Part 3: Queueing state changes and handling `onPause()`](https://medium.com/@Zhuinden/queueing-state-changes-and-handling-onpause-creating-a-flow-like-custom-backstack-part-3-d08d69a98141#.dxfkhzji3)
- [Part 4: Persisting view-state when using a custom backstack](https://medium.com/@Zhuinden/persisting-view-state-when-using-a-custom-backstack-creating-a-flow-like-backstack-part-4-5e0ba00ed80c#.ktath328c)

## What is it?

Currently it's 3 files (and 5 classes):

- [BackStack](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/Backstack.java): exposes operators for manipulating the backstack, and stores current history.
- [StateChanger](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/StateChanger.java): interface for a class that listens to changes inside the Backstack.
- [StateChange](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/StateChange.java): represents a state change inside the backstack, providing previous state, new state, and the direction of the change.

- [StateChange.Direction](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/StateChange.java): represents the direction of the change.
- [StateChanger.Callback](https://github.com/Zhuinden/simple-stack-demo/blob/master/demo-stack/src/main/java/com/zhuinden/simplestackdemo/stack/StateChanger.java): the callback that signals to the backstack that the state change is complete.

## Operators

The Backstack provides 3 convenient operators for manipulating state.

- `goTo()`: if state does not previously exist in the backstack, then adds it to the stack. Otherwise navigate back to given state.
- `goBack()`: returns boolean if state change is in progress, or if there are more than 1 entries in history (and handled the back press). Otherwise, return false.
- `setHistory()`: sets the state to the provided elements.

## What does it do?

Currently, the backstack stores the screens, and persists them across configuration change / process death.

The backstack also allows navigation between the states, and enables handling this state change using the `StateChanger`.

## How does it work?

The backstack must be initialized with at least one initial state, and a state changer must be set when it is able to handle the state change.

Setting a state changer begins an `initialization` (in Flow terms, a bootstrap traversal), which provides a state change in form of `{[], [{...}, {...}]}`.

Afterwards, the backstack operators allow changing between states.

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

    private void persistViewToState(View view) {
        if(view != null) {
            SparseArray<Parcelable> viewHierarchyState = new SparseArray<>();
            Key key = KeyContextWrapper.getKey(view.getContext());
            view.saveHierarchyState(viewHierarchyState);
            SavedState previousSavedState = SavedState.builder() //
                    .setKey(key) //
                    .setViewHierarchyState(viewHierarchyState) //
                    .build();
            keyStateMap.put(key, previousSavedState);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        persistViewToState(root.getChildAt(0));
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
        persistViewToState(root.getChildAt(0));
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
