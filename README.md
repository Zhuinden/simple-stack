# Simple Stack

Similarly to [square/flow](https://github.com/square/flow), Simple Stack allows you to represent your application state in a list of immutable data classes.

The library also allows easy backstack persisting through a delegate class, which handles configuration changes and process death.

If your data classes are not `Parcelable` by default, then you can specify a custom parcellation strategy using `setKeyParceler()`.

Additionally, the library also allows you to persist state of custom viewgroups that are associated with a given UI state into a `StateBundle`.

This way, you can easily create a single-Activity application using either views, fragments, or whatevers.

## Operators

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) provides 3 primary operators for manipulating state.

- `goTo()`: if state does not previously exist in the backstack, then adds it to the stack. Otherwise navigate back to given state.
- `goBack()`: returns true if [StateChange](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java) is in progress, or if there are more than 1 entries in history (and handled the back press). Otherwise, return false.
- `setHistory()`: sets the state to the provided elements, with the direction that is specified.

The secondary operators are:

- `replaceTop()`: removes the current top element, and replaces it with the newly provided one.
- `goUp()`: navigates back to the element if exists, replaces current top with it if does not.
- `goUpChain()`: goes up to the parent chain if exists completely, replaces current with the chain if partially exists (while re-ordering existing duplicates to match the provided chain), and replaces current with chain if doesn't exist.
- `jumpToRoot()`: goes to the root of the stack with the given direction (by default, backwards).
- `moveToTop()`: moves to provided key to the top if exists, otherwise adds it to top. 

## What does it do?

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) stores the screens.

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) also allows navigation between the states (works as a router), and enables handling this state change using the [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java).

The library also provides two ways to handle both view-state persistence for views associated with a key, and persisting the keys across configuration change / process death.

- The [Navigator](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/Navigator.java), which uses the [BackstackHost](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/BackstackHost.java) retained fragment (API 11+) to automatically receive the lifecycle callbacks, and survive configuration change.

- The [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java), which works via manual Activity lifecycle callbacks - typically needed only for fragments.

Internally, both the [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java) and the [Navigator](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/Navigator.java) uses a [BackstackManager](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackManager.java), which can also be used.

-----------

The library provides a [DefaultStateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/DefaultStateChanger.java), which by default uses [Navigator]([Navigator](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/Navigator.java)) to handle the persistence.

The keys used by a [DefaultStateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/DefaultStateChanger.java) must implement [StateKey](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/StateKey.java), which expects a layout key and a view change handler.

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

    compile 'com.github.Zhuinden:simple-stack:1.14.0'

## How does it work?

The [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) must be initialized with at least one initial state, and a [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java) must be set when it is able to handle the state change.

The [BackstackManager](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackManager.java) is provided to handle state persistence.

Convenience classes [BackstackDelegate](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackDelegate.java) and [Navigator](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/navigator/Navigator.java) are provided to help integration of the [BackstackManager](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/BackstackManager.java).

Setting a [StateChanger](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChanger.java) begins an `initialization` (in Flow terms, a bootstrap traversal), which provides a [StateChange](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/StateChange.java) in form of `{[], [{...}, {...}]}` (meaning the previous state is empty, the new state is the initial keys).

This allows you to initialize your views according to your current state.

Afterwards, the [Backstack](https://github.com/Zhuinden/simple-stack/blob/master/simple-stack/src/main/java/com/zhuinden/simplestack/Backstack.java) operators allow changing between states.

## Example code

### Fragments

Check out the Fragment samples to see how to make Simple-Stack work with Fragments (or the relevant wiki page).

- **End result**

``` kotlin
    fun navigateTo(key: Key) {
        backstack.goTo(key)
    }
```

and

``` kotlin
    homeButton.onClick {
        navigateTo(OtherKey())
    }
```

- **Activity**

``` kotlin
class MainActivity : AppCompatActivity(), StateChanger {
    lateinit var backstackDelegate: BackstackDelegate
    lateinit var fragmentStateChanger: FragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        backstackDelegate = BackstackDelegate()
        backstackDelegate.onCreate(savedInstanceState,
            lastCustomNonConfigurationInstance,
            History.single(TasksKey()))

        backstackDelegate.registerForLifecycleCallbacks(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        this.fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.root)
        
        backstackDelegate.setStateChanger(this)
    }
    override fun onRetainCustomNonConfigurationInstance(): Any =
        backstackDelegate.onRetainCustomNonConfigurationInstance()

    override fun onBackPressed() {
        if (!backstackDelegate.onBackPressed()) { // calls backstack.goBack()
            super.onBackPressed()
        }
    }

    override fun handleStateChange(stateChange: StateChange, completionCallback: StateChanger.Callback) {
        if (stateChange.isTopNewStateEqualToPrevious) {
            // no-op
            completionCallback.stateChangeComplete()
            return
        }

        fragmentStateChanger.handleStateChange(stateChange)

        completionCallback.stateChangeComplete()
    }
}
```

- **Key** for Fragments

``` kotlin
@Parcelize
data class TasksKey(val placeholder: String = "") : BaseKey() {
    override fun createFragment(): Fragment = TasksFragment()
}
```

And

``` kotlin
abstract class BaseKey : Parcelable {
    val fragmentTag: String
        get() = toString()

    fun newFragment(): BaseFragment = createFragment().apply {
        arguments = (arguments ?: Bundle()).also { bundle ->
            bundle.putParcelable("KEY", this@BaseKey)
        }
    }

    protected abstract fun createFragment(): BaseFragment
}
```

- **FragmentStateChanger**

For `FragmentStateChanger`, see the example [here](https://github.com/Zhuinden/simple-stack/blob/dfc5fef9c2f2236651162b8ec8b36062615a1cf6/simple-stack-example-mvp-fragments/src/main/java/com/zhuinden/simplestackdemoexamplefragments/util/FragmentStateChanger.kt).

### Custom Views

- **Activity**

``` kotlin
class MainActivity : AppCompatActivity() {

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Navigator.install(this, root, History.single(FirstKey()))
        // additional configuration possible with `Navigator.configure()...install()`
    }

    @Override
    override fun onBackPressed() {
        if(!Navigator.onBackPressed(this)) { // calls `backstack.goBack()`
            super.onBackPressed()
        }
    }
}
```

- **StateKey**

``` kotlin
@Parcelize
data class TasksKey(val placeholder: String = "") : StateKey {
    override fun layout(): Int = R.layout.path_tasks
    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
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

``` kotlin
class TasksView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    
    override fun onFinishInflate() {
         super.onFinishInflate()
         firstButton.onClick {
             backstack.goTo(SecondKey())
         }
    }
}

val View.backstack 
    get() = Navigator.getBackstack(context)
```

## More information

For more information, check the [wiki page](https://github.com/Zhuinden/simple-stack/wiki).


## License

    Copyright 2017-2019 Gabor Varadi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
