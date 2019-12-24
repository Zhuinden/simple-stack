# Simple Stack

Simple Stack allows you to represent your navigation state in a list of immutable data classes (referred to as "screen keys").

The library also allows easily persisting said screen keys, making your navigation state handle both configuration changes and process death.

If your screen keys are not `Parcelable` by default, then you can specify a custom parcellation strategy using `setKeyParceler()`.

A screen key can also be associated with a "scope", allowing you to easily store data/state independently from your views, share them between screens, and also persist/restore the state across process death using `Bundleable`.

This way, you can easily create single-Activity applications using either views, fragments, or whatevers.

## Why would I want to use this?

Making your navigation state explicit means you're in control of your application. 

Instead of hacking around with the right fragment transaction tags, or calling `NEW_TASK | CLEAR_TASK` and making the screen flicker - you can just say `backstack.setHistory(History.of(SomeScreen(), OtherScreen())` and that is now your active navigation history.

Using `Backstack` to navigate allows you to move navigation responsibilities out of your view layer. No need to run FragmentTransactions directly in a click listener each time you want to move to a different screen. No need to mess around with  `LiveData<Event<T>>` or `SingleLiveData` to get your "view" to decide what state your app should be in either.

``` java
public class MyViewModel {
    private final Backstack backstack;

    public MyViewModel(Backstack backstack) {
        this.backstack = backstack;
    }
    
    // ...
    
    public void doSomething() {
        // ...
        backstack.goTo(new OtherScreen());
    }
}
```

Another additional benefit is that your navigation history can be unit tested.

``` java
assertThat(backstack.getHistory()).containsExactly(new SomeScreen(), new OtherScreen());
```

And most importantly, navigation (swapping screens) happens in one place, and you are in direct control of what happens in such a scenario. By writing a `StateChanger`, you can set up "how to display my current navigation state" in any way you want. No more `((MainActivity)getActivity()).setTitleText("blah");` inside Fragment's `onStart()`.

Write once, works in all cases.

``` java
public void handleStateChange(StateChange stateChange, StateChanger.Callback callback) {
    if (stateChange.isTopNewEqualToPrevious()) { // check if navigating to the same screen first!
        callback.stateChangeComplete();
        return;
    }

    Key key = stateChange.topNewKey(); // use your new navigation state
    setTitle(key.title);
    
    ... // set up fragments, set up views, whatever you want
    
    callback.stateChangeComplete();
}
```

Whether you navigate forward or backward, or you rotate the screen, or you come back after low memory condition - it's irrelevant. The `StateChanger` will ***always*** handle the scenario in a predictable way.

## Operators

The Backstack provides 3 primary operators for manipulating navigation history.

- `goTo()`: if the key does not previously exist in the backstack, then adds it to the stack. Otherwise navigate back to the given key.
- `goBack()`: returns true if navigation is currently in progress, or if there are more than 1 entries in history (and handled the back press). Otherwise, return false.
- `setHistory()`: sets the navigation history to the provided keys, with the direction that is specified.

The secondary operators are:

- `replaceTop()`: removes the current top key, and replaces it with the newly provided key.
- `goUp()`: navigates back to the key if exists, replaces current top key with it if does not.
- `goUpChain()`: goes up to the parent chain if exists completely, replaces current with the chain if partially exists (while re-ordering existing duplicates to match the provided chain), and replaces current with chain if doesn't exist.
- `jumpToRoot()`: goes to the key at the root of the stack with the given direction (by default, backwards).
- `moveToTop()`: moves provided key to the top if exists, otherwise adds it to the top.

## What does it do?

The Backstack stores the screen keys. It also allows navigation between the keys (works as a router), and enables handling this change in navigation state using the StateChanger.

The library also provides a way to simplify integrating the Backstack into the Activity lifecycle (survive config changes / process death, among other things):

- The Navigator, which uses the BackstackHost retained fragment (API 11+) to automatically receive the lifecycle callbacks, and survive configuration change.

But the Backstack can also be used directly.

-----------

Navigator by default installs a DefaultStateChanger, which works with views.

The keys used by a DefaultStateChanger must implement DefaultViewKey, which expects a layout key and a view change handler.

## Using Simple Stack

In order to use Simple Stack, you need to add `jitpack` to your project root `build.gradle.kts`
(or `build.gradle`):

``` kotlin
// build.gradle.kts
allprojects {
    repositories {
        // ...
        maven { setUrl("https://jitpack.io") }
    }
    // ...
}
```

or

``` groovy
// build.gradle
allprojects {
    repositories {
        // ...
        maven { url "https://jitpack.io" }
    }
    // ...
}
```

and then, add the dependency to your module's `build.gradle.kts` (or `build.gradle`):

``` kotlin
// build.gradle.kts
implementation("com.github.Zhuinden:simple-stack:2.1.2")
```

or

``` groovy
// build.gradle
implementation 'com.github.Zhuinden:simple-stack:2.1.2'
```

## How does it work?

The Backstack is setup with an initial navigation history, then a StateChanger must be set when it is able to handle the state change.

Navigator is a possible way to help with the integration of Backstack to the Activity lifecycle.

Setting a StateChanger sends an initialization, which provides a StateChange in form of `{[], [{...}, {...}]}` (meaning the previous keys are empty, the new keys are the initial keys).

This allows you to initialize your views according to your current state.

Afterwards, the Backstack operators allow changing between states.

## Example setup

### Compound ViewGroups

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

- **DefaultViewKey**

``` kotlin
@Parcelize
data class FirstKey(val placeholder: String = "") : DefaultViewKey {
    override fun layout(): Int = R.layout.first_view
    override fun viewChangeHandler(): ViewChangeHandler = SegueViewChangeHandler()
}
```

- **Layout XML**

``` xml
<?xml version="1.0" encoding="utf-8"?>
<com.zhuinden.simplestackdemoexample.FirstView
              xmlns:android="http://schemas.android.com/apk/res/android"
              android:layout_width="match_parent"
              android:layout_height="match_parent"
              android:gravity="center"
              android:orientation="vertical">

    <EditText
        android:id="@+id/textFirst"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:hint="Enter text here"/>

    <Button
        android:id="@+id/buttonFirst"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Go to second!"/>

</com.zhuinden.simplestackdemoexample.FirstView>
```

- **Custom ViewGroup**

``` kotlin
import kotlinx.android.synthetic.main.first_view.view.*

val View.backstack
    get() = Navigator.getBackstack(context)

class FirstView : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    
    override fun onFinishInflate() {
         super.onFinishInflate()

         buttonFirst.onClick {
             backstack.goTo(SecondKey())
         }
    }
}
```

## Scopes

To simplify sharing data/state between screens, a screen key can also be associated with a ScopeKey.

The scope is described with a String tag, and services bound to that scope can be configured via ScopedServices.

Services bound to a ServiceBinder (received inside ScopedServices) get lifecycle callbacks, for example `Bundleable` (to persist/restore state).

This lets you easily share a class between screens, while still letting you handle Android's lifecycles seamlessly.

``` kotlin
inline fun <reified T> Fragment.lookup(serviceTag: String = T::class.java.name) =
    Navigator.lookupService<T>(requireContext(), serviceTag)

Navigator.configure()
    .setStateChanger(this)
    .setScopedServices(ScopeConfiguration())
    .install(this, root, History.of(WordListKey()))

class WordController : Bundleable, ScopedServices.Registered {
    ...
}

class WordListFragment : BaseFragment() {
    private val wordController by lazy { lookup<WordController>() }

    ...
}

class NewWordFragment : BaseFragment() {
    private val controller by lazy { lookup<WordController>() }

    ...
}
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
