# Simple Stack

This is a simple backstack implementation.

Similarly to Square's Flow, it allows you to represent your application state in a list of immutable (and parcelable) objects.

The library also provides you with the means of persisting the backstack easily through a delegate class, which handles both configuration change and process death.

Additionally, the delegate also allows you to persist state of custom viewgroups that are associated with a given UI state into a Bundle.

This way, you can easily create a single-Activity application using either views, fragments, or whatevers.

## Operators

The Backstack provides 3 convenient operators for manipulating state.

- `goTo()`: if state does not previously exist in the backstack, then adds it to the stack. Otherwise navigate back to given state.
- `goBack()`: returns boolean if state change is in progress, or if there are more than 1 entries in history (and handled the back press). Otherwise, return false.
- `setHistory()`: sets the state to the provided elements, with the direction that is specified.

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

    compile 'com.github.Zhuinden:simple-stack:0.9.4'

## How does it work?

The backstack must be initialized with at least one initial state, and a state changer must be set when it is able to handle the state change.

The `BackstackDelegate` is provided as a convenience class to hide the Activity lifecycle integration and state persistence.

The `StateChanger` can be set immediately for the delegate, or later (but before `onPostResume()`). For example, you can initialize the backstack before `super.onCreate()`, but set the state changer in `onPostCreate()`.

Setting a state changer begins an `initialization` (in Flow terms, a bootstrap traversal), which provides a state change in form of `{[], [{...}, {...}]}`.

This allows you to initialize your views according to your current state.

Afterwards, the backstack operators allow changing between states.

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        backstackDelegate = new BackstackDelegate(this);
        backstackDelegate.onCreate(savedInstanceState, //
                getLastCustomNonConfigurationInstance(), //
                HistoryBuilder.single(new FirstKey()));

        // get reference to Backstack with `backstackDelegate.getBackstack()`
        // you can also share it with `BackstackService.getContext()` or with lazy-initialized Dagger module, check examples
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

- [Backstack](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java): exposes operators for manipulating the backstack, and stores current history.

- [StateChanger](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java): interface for a class that listens to changes inside the Backstack.

- [StateChange](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java): represents a state change inside the backstack, providing previous state, new state, and the direction of the change.

- [StateChanger.Callback](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java): the callback that signals to the backstack that the state change is complete.

- [PendingStateChange](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/PendingStateChange.java): represents a change that will occur when possible.

- [HistoryBuilder](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/HistoryBuilder.java): Convenience class for building `ArrayList<Parcelable>`.

- [SavedState](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/SavedState.java): contains the key, the view state and an optional Bundle. It is used for view state persistence.

- [KeyContextWrapper](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/KeyContextWrapper.java): enables the ability to use `KeyContextWrapper.getKey(context)` or `Backstack.getKey(context)` to obtain key parameter in custom viewgroup.

- [BackstackDelegate](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java): delegate class to hide Activity lifecycle integration, and provide view state persistence.

- [Bundleable](https://github.com/Zhuinden/simple-stack-demo/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Bundleable.java): interface that allows you to persist state directly from a custom View into a Bundle, using the delegate.



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