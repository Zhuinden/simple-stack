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
    public static final String BACKSTACK = "BACKSTACK";

    @BindView(R.id.root)
    RelativeLayout root;

    Backstack backstack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayList<Parcelable> keys;
        if(savedInstanceState != null) {
            keys = savedInstanceState.getParcelableArrayList(BACKSTACK);
        } else {
            keys = HistoryBuilder.single(new FirstKey());
        }
        backstack = (Backstack)getLastCustomNonConfigurationInstance();
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
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BACKSTACK, HistoryBuilder.from(backstack.getHistory()).build());
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
        root.removeAllViews();
        Key newKey = stateChange.topNewState();
        Context newContext = new KeyContextWrapper(this, newKey);
        View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        root.addView(view);
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

## Limitations

Currently, the backstack does **not** do viewstate persistence, only stores the `ArrayList<Parcelable>` that represents the given screens.