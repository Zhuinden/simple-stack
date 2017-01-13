# Simple Stack Demo

This is a simple backstack implementation that will serve as basis for a series of Medium articles.

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
public class MainActivity extends AppCompatActivity implements StateChanger {
    @BindView(R.id.root)
    RelativeLayout root;

    Backstack backstack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // initialize backstack from persisted state if exists, otherwise with new elements.
        ArrayList<Parcelable> keys;
        if(savedInstanceState != null) {
            keys = savedInstanceState.getParcelableArrayList("BACKSTACK");
        } else {
            keys = new ArrayList<>();
            keys.add(new FirstKey());
        }
        backstack = (Backstack)getLastCustomNonConfigurationInstance();
        if(backstack == null) {
            backstack = new Backstack(keys);
        }
        
        // set this as state changer, and handle initialization
        backstack.setStateChanger(this);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() { // persist stack across config change
        return backstack;
    }

    @Override
    public void onBackPressed() {
        if(!backstack.goBack()) { // handle back press
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Parcelable> history = new ArrayList<>();
        history.addAll(backstack.getHistory());
        outState.putParcelableArrayList("BACKSTACK", history); // persist state of backstack across config change/process death
    }

    @Override
    protected void onDestroy() {
        backstack.removeStateChanger(); // backstack survives config change, so Activity should remove its reference
        super.onDestroy();
    }

    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        root.removeAllViews(); // no state persistence for previous views yet
        Key newKey = stateChange.topNewState();
        View view = LayoutInflater.from(this).inflate(newKey.layout(), root, false);
        BackstackHolder backstackHolder = (BackstackHolder)view;
        backstackHolder.setBackstack(backstack); // view currently doesn't implicitly receive the backstack
        root.addView(view);
        completionCallback.stateChangeComplete();
    }
}
```

## Limitations

Currently, the backstack does **not** do viewstate persistence, only stores the `ArrayList<Parcelable>` that represents the given screens.

Scheduling a state change while a state change is already in progress throws an `IllegalStateException` instead of queueing it.

## Demo limitations

In the demo, keys associated with the given custom views are currently not manually set for the custom views, therefore it is not accessible at the moment.

In the demo, the backstack is manually set to the custom viewgroup using a `BackStackHolder` interface, instead of implicitly providing it via the Context.
