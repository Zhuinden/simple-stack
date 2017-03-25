# Simple Stack

Similarly to [square/flow](https://github.com/square/flow), Simple Stack allows you to represent your application state in a list of immutable data classes.

The library also provides you with the means of persisting the backstack easily through a delegate class, which handles both configuration change and process death.

If your data classes are not `Parcelable` by default, then you can specify a custom parcellation strategy using `backstackDelegate.setKeyParceler()`.

Additionally, the delegate also allows you to persist state of custom viewgroups that are associated with a given UI state into a Bundle.

This way, you can easily create a single-Activity application using either views, fragments, or whatevers.

## Operators

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) provides 3 convenient operators for manipulating state.

- `goTo()`: if state does not previously exist in the backstack, then adds it to the stack. Otherwise navigate back to given state.
- `goBack()`: returns boolean if [StateChange](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java) is in progress, or if there are more than 1 entries in history (and handled the back press). Otherwise, return false.
- `setHistory()`: sets the state to the provided elements, with the direction that is specified.

## What does it do?

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) stores the screens, and the [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java) persists them across configuration change / process death.

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) also allows navigation between the states, and enables handling this state change using the [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java).

The [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java) provides Activity lifecycle integration, and manages view state persistence for custom views associated with a key.

Internally, the [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java) uses a [BackstackManager](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackManager.java), which can also be used.

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

    compile 'com.github.Zhuinden:simple-stack:1.4.3'

## How does it work?

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) must be initialized with at least one initial state, and a [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java) must be set when it is able to handle the state change.

The [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java) is provided to handle Activity lifecycle integration and view state persistence for custom views.

The [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java) can be set immediately for the [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java), or later (but before `onPostResume()`). For example, you can initialize the [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) before `super.onCreate()`, but set the [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java) in `onPostCreate()`.

Setting a [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java) begins an `initialization` (in Flow terms, a bootstrap traversal), which provides a [StateChange](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java) in form of `{[], [{...}, {...}]}` (meaning the previous state is empty, the new state is the initial keys).

This allows you to initialize your views according to your current state.

Afterwards, the [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) operators allow changing between states.

## Example code

``` java
public class MainActivity
        extends AppCompatActivity
        implements StateChanger {
    @BindView(R.id.root)
    RelativeLayout root;

    BackstackDelegate backstackDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backstackDelegate = new BackstackDelegate(null);
        backstackDelegate.onCreate(savedInstanceState, //
                        getLastCustomNonConfigurationInstance(), //
                        HistoryBuilder.single(new FirstKey()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        backstackDelegate.setStateChanger(this);

        // get reference to Backstack with `backstackDelegate.getBackstack()`
        // you can also share it either with
            // `BackstackService.getContext()` through `getSystemService()`
            // or with lazy-initialized Dagger module, check the examples for exact usage.
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
        backstackDelegate.persistViewToState(root.getChildAt(0)); // <-- persisting view state
        backstackDelegate.onSaveInstanceState(outState); // <-- persisting backstack + view states
    }

    @Override
    protected void onDestroy() {
        backstackDelegate.onDestroy(); // <-- very important!
        super.onDestroy();
    }

    // StateChanger implementation
    @Override
    public void handleStateChange(StateChange stateChange, Callback completionCallback) {
        if(stateChange.topNewState().equals(stateChange.topPreviousState())) {
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
        completionCallback.stateChangeComplete();
    }
}
```

## Structure

- [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java): exposes operators for manipulating the backstack, and stores current history.

- [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java): delegate class to hide Activity lifecycle integration, and provide view state persistence using `BackstackManager`.

- [BackstackManager](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackManager.java): provides view-state and backstack history persistence.

- [Bundleable](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Bundleable.java): interface that allows you to persist state directly from a custom View into a [StateBundle](https://github.com/Zhuinden/state-bundle/), using the delegate.

- [HistoryBuilder](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/HistoryBuilder.java): Convenience class for building `ArrayList<Object>`.

- [KeyContextWrapper](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/KeyContextWrapper.java): enables the ability to use `KeyContextWrapper.getKey(context)` or `Backstack.getKey(context)` to obtain key parameter in custom viewgroup.

- [KeyParceler](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/KeyParceler.java): used for defining custom parcellation strategy if keys are not `Parcelable` by default.

- [PendingStateChange](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/PendingStateChange.java): represents a change that will occur when possible.

- [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java): interface for a class that listens to changes inside the Backstack.

- [StateChange](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java): represents a state change inside the backstack, providing previous state, new state, and the direction of the change.

- [StateChanger.Callback](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java): the callback that signals to the backstack that the state change is complete.

- [SavedState](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/SavedState.java): contains the key, the view state and an optional Bundle. It is used for view state persistence.



## License

    Copyright 2017 Gabor Varadi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.