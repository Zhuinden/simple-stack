# Simple Stack

Similarly to [square/flow](https://github.com/square/flow), Simple Stack allows you to represent your application state in a list of immutable data classes.

The library also allows easy backstack persisting through a delegate class, which handles both configuration changes and process death.

If your data classes are not `Parcelable` by default, then you can specify a custom parcellation strategy using `backstackDelegate.setKeyParceler()`.

Additionally, the delegate also allows you to persist state of custom viewgroups that are associated with a given UI state into a Bundle.

This way, you can easily create a single-Activity application using either views, fragments, or whatevers.

## Operators

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) provides 3 convenient operators for manipulating state.

- `goTo()`: if state does not previously exist in the backstack, then adds it to the stack. Otherwise navigate back to given state.
- `goBack()`: returns boolean if [StateChange](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java) is in progress, or if there are more than 1 entries in history (and handled the back press). Otherwise, return false.
- `setHistory()`: sets the state to the provided elements, with the direction that is specified.

## What does it do?

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) stores the screens.

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) also allows navigation between the states (works as a router), and enables handling this state change using the [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java).

The library also provides two ways to handle both view-state persistence for views associated with a key, and persisting the keys across configuration change / process death.

- The [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java), which works via manual Activity lifecycle callbacks.

- The [Navigator](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/Navigator.java), which uses the [BackstackHost](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/BackstackHost.java) retained fragment (API 11+) to automatically receive the lifecycle callbacks, and survive configuration change.

Internally, both the the [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java) and the [Navigator](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/Navigator.java) uses a [BackstackManager](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackManager.java), which can also be used.

-----------

The library provides a [DefaultStateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/DefaultStateChanger.java), which by default uses [Navigator]([Navigator](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/Navigator.java)) to handle the persistence.

The keys used by a [DefaultStateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/DefaultStateChanger.java) must implement [StateKey](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/StateKey.java).

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

    compile 'com.github.Zhuinden:simple-stack:1.5.1'

## How does it work?

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) must be initialized with at least one initial state, and a [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java) must be set when it is able to handle the state change.

The [BackstackManager](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackManager.java) is provided to handle state persistence.

Convenience classes [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java) and [Navigator](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/Navigator.java) are provided to help integration of the [BackstackManager](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackManager.java).

Setting a [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java) begins an `initialization` (in Flow terms, a bootstrap traversal), which provides a [StateChange](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java) in form of `{[], [{...}, {...}]}` (meaning the previous state is empty, the new state is the initial keys).

This allows you to initialize your views according to your current state.

Afterwards, the [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) operators allow changing between states.

## Example code


- **Activity**

``` java
public class MainActivity
        extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    @BindView(R.id.root)
    RelativeLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Navigator.install(this, root, HistoryBuilder.single(FirstKey.create()));
        // additional configuration possible with `Navigator.configure()...install()`
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            super.onBackPressed();
        }
    }
}
```

- **StateKey**

``` java
@AutoValue
public abstract class FirstKey
        implements StateKey, Parcelable {
    public static FirstKey create() {
        return new AutoValue_FirstKey();
    }

    @Override
    public int layout() {
        return R.layout.path_first;
    }

    @Override
    public ViewChangeHandler viewChangeHandler() {
        return new SegueViewChangeHandler();
    }
}
```

- **Layout XML**

``` xml
<?xml version="1.0" encoding="utf-8"?>
<com.zhuinden.simplestackdemoexample.FirstView xmlns:android="http://schemas.android.com/apk/res/android"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:gravity="center"
              android:orientation="vertical">

    <EditText
        android:id="@+id/first_edittext"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:hint="Enter text here"/>

    <Button
        android:id="@+id/first_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Go to second!"/>

</com.zhuinden.simplestackdemoexample.FirstView>
```

- **Custom Viewgroup**

``` java
public class FirstView
        extends LinearLayout { // can implement Bundleable

    public FirstView(Context context) {
        super(context);
    }

    public FirstView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //...

    @OnClick(R.id.first_button)
    public void firstButtonClick(View view) {
        Navigator.getBackstack(view.getContext()).goTo(SecondKey.create());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
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
