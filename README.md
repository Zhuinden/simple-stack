# Simple Stack

This is a simple backstack implementation that will serve as basis for a series of Medium articles.

- [Part 1: Creating a basic backstack](https://medium.com/@Zhuinden/towards-a-fragmentless-world-creating-a-flow-like-custom-backstack-part-1-cf551ebda624#.wkshdkeb6)
- [Part 2: Sharing data to custom views using `getSystemService()` and `ContextWrapper`](https://medium.com/@Zhuinden/data-and-service-sharing-to-custom-views-with-contextwrappers-and-getsystemservice-creating-a-flow-aedeabbd9567#.43l4qxahe)
- [Part 3: Queueing state changes and handling `onPause()`](https://medium.com/@Zhuinden/queueing-state-changes-and-handling-onpause-creating-a-flow-like-custom-backstack-part-3-d08d69a98141#.dxfkhzji3)
- [Part 4: Persisting view-state when using a custom backstack](https://medium.com/@Zhuinden/persisting-view-state-when-using-a-custom-backstack-creating-a-flow-like-backstack-part-4-5e0ba00ed80c#.ktath328c)
- [Part 5: Hiding Activity-lifecycle integration inside Delegate class](https://medium.com/@Zhuinden/hiding-the-backstacks-activity-lifecycle-integration-in-a-delegate-class-creating-a-flow-like-695fe16338ff#.w3i1pnmj2)

It is theoretically based on Flow 0.9, mixed with some aspects taken from Flow 1.0-alpha; but written from scratch.

The core concept was simplicity: maybe it should try to do less. In fact, even less than less.

## What is it?

Currently it's the following files:

- [Backstack](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java): exposes operators for manipulating the backstack, and stores current history.
- [StateChanger](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java): interface for a class that listens to changes inside the Backstack.
- [StateChange](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java): represents a state change inside the backstack, providing previous state, new state, and the direction of the change.

- [StateChange.Direction](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java): represents the direction of the change.
- [StateChanger.Callback](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java): the callback that signals to the backstack that the state change is complete.

- [PendingStateChange](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/PendingStateChange.java): represents a change that will occur when possible.

- [HistoryBuilder](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/HistoryBuilder.java): Convenience class for building `ArrayList<Parcelable>`.

- [SavedState](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/SavedState.java): contains the key, the view state and an optional Bundle. It is used for view state persistence.

- [KeyContextWrapper](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/KeyContextWrapper.java): enables the ability to use `KeyContextWrapper.getKey(context)` to obtain key parameter in custom viewgroup.

- [BackstackDelegate](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java): delegate class to hide Activity lifecycle integration.

## Operators

The Backstack provides 3 convenient operators for manipulating state.

- `goTo()`: if state does not previously exist in the backstack, then adds it to the stack. Otherwise navigate back to given state.
- `goBack()`: returns boolean if state change is in progress, or if there are more than 1 entries in history (and handled the back press). Otherwise, return false.
- `setHistory()`: sets the state to the provided elements.

## What does it do?

Currently, the backstack stores the screens, and persists them across configuration change / process death.

The backstack also allows navigation between the states, and enables handling this state change using the `StateChanger`.

Also provides delegate class that hides Activity lifecycle integration, and manages view state persistence.

## Using Simple Stack

In order to use Simple Stack, you need to add jitpack to your project root gradle:

    buildscript {
        repositories {
            // ...
            maven { url "https://jitpack.io" }
        }
        // ...
    }
    allprojects {
        repositories {
            // ...
            maven { url "https://jitpack.io" }
        }
        // ...
    }


and add the compile dependency to your module level gradle.

    compile 'com.github.Zhuinden:simple-stack:0.8.2'

## How does it work?

The backstack must be initialized with at least one initial state, and a state changer must be set when it is able to handle the state change.

Setting a state changer begins an `initialization` (in Flow terms, a bootstrap traversal), which provides a state change in form of `{[], [{...}, {...}]}`.

Afterwards, the backstack operators allow changing between states.

But `BackstackDelegate` is provided as a convenience class to hide the Activity lifecycle integration and state persistence.

``` java
public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    @BindView(R.id.root)
    RelativeLayout root;

    BackstackDelegate backstackDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        backstackDelegate = new BackstackDelegate(this);
        backstackDelegate.onCreate(savedInstanceState, //
                getLastCustomNonConfigurationInstance(), //
                HistoryBuilder.single(new FirstKey()));

        // get reference to Backstack with `backstackDelegate.getBackstack()`
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return backstackDelegate.onRetainCustomNonConfigurationInstance();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        backstackDelegate.onPostResume();
    }

    @Override
    public void onBackPressed() {
        if(!backstackDelegate.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        backstackDelegate.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        backstackDelegate.persistViewToState(root.getChildAt(0));
        backstackDelegate.onSaveInstanceState(outState);
    }

    // StateChanger implementation
    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
            // no-op
            completionCallback.stateChangeComplete();
            return;
        }
        backstackDelegate.persistViewToState(root.getChildAt(0));
        root.removeAllViews();
        Key newKey = stateChange.topNewState();
        Context newContext = stateChange.createContext(this, newKey);
        View view = LayoutInflater.from(newContext).inflate(newKey.layout(), root, false);
        backstackDelegate.restoreViewFromState(view);
        root.addView(view);
        backstackDelegate.clearStatesNotIn(stateChange.getNewState());
        completionCallback.stateChangeComplete();
    }
}
```
