![featured](https://androidweekly.net/issues/issue-489/badge)
[![License](https://img.shields.io/github/license/Zhuinden/simple-stack.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![](https://jitpack.io/v/Zhuinden/simple-stack.svg)](https://jitpack.io/#Zhuinden/simple-stack)

![simple-stack](https://user-images.githubusercontent.com/11718392/138650683-c09952fa-b9dd-4c7e-87b2-31ca542e2f69.png)

# Simple Stack

## Why do I want this?

To make navigation to another screen as simple as `backstack.goTo(SomeScreen())`, and going back as simple as `backstack.goBack()`.

No more `FragmentTransaction`s in random places. Predictable and customizable navigation in a single location.

## What is Simple Stack?

Simple Stack is a backstack library (or technically, a navigation framework) that allows you to represent your navigation state in a list of immutable, parcelable data classes ("keys").

This allows preserving your navigation history across configuration changes and process death - this is handled automatically.

Each screen can be associated with a scope, or a shared scope - to easily share data between screens.

This simplifies navigation and state management within an Activity using either fragments, views, or whatever else.

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

In newer projects, you need to also update the `settings.gradle` file's `dependencyResolutionManagement` block:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }  // <--
        jcenter() // Warning: this repository is going to shut down soon
    }
}
```

and then, add the dependency to your module's `build.gradle.kts` (or `build.gradle`):

``` kotlin
// build.gradle.kts
implementation("com.github.Zhuinden:simple-stack:2.7.0")

implementation("com.github.Zhuinden.simple-stack-extensions:core-ktx:2.2.5")
implementation("com.github.Zhuinden.simple-stack-extensions:fragments:2.2.5")
implementation("com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.2.5")
implementation("com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.2.5")
implementation("com.github.Zhuinden.simple-stack-extensions:services:2.2.5")
implementation("com.github.Zhuinden.simple-stack-extensions:services-ktx:2.2.5")
```

or

``` groovy
// build.gradle
implementation 'com.github.Zhuinden:simple-stack:2.7.0'

implementation 'com.github.Zhuinden.simple-stack-extensions:core-ktx:2.2.5'
implementation 'com.github.Zhuinden.simple-stack-extensions:fragments:2.2.5'
implementation 'com.github.Zhuinden.simple-stack-extensions:fragments-ktx:2.2.5'
implementation 'com.github.Zhuinden.simple-stack-extensions:navigator-ktx:2.2.5'
implementation 'com.github.Zhuinden.simple-stack-extensions:services:2.2.5'
implementation 'com.github.Zhuinden.simple-stack-extensions:services-ktx:2.2.5'
```

## How do I use it?

You can check out [**the
tutorials**](https://github.com/Zhuinden/simple-stack/tree/611e8c7db738a776156b8f709db22b8e37413221/tutorials) for
simple examples.

## Fragments

With Fragments, in `AHEAD_OF_TIME` back handling mode to support predictive back gesture (along
with `android:enableBackInvokedCallback`), the Activity code looks like this:

```kotlin
class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: FragmentStateChanger

    private lateinit var authenticationManager: AuthenticationManager

    private lateinit var backstack: Backstack

    private val backPressedCallback = object : OnBackPressedCallback(false) { // <-- !
        override fun handleOnBackPressed() {
            backstack.goBack()
        }
    }

    private val updateBackPressedCallback = AheadOfTimeWillHandleBackChangedListener { // <-- !
        backPressedCallback.isEnabled = it // <-- !
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        onBackPressedDispatcher.addCallback(backPressedCallback) // <-- !

        fragmentStateChanger = FragmentStateChanger(supportFragmentManager, R.id.container)

        backstack = Navigator.configure()
            .setBackHandlingModel(BackHandlingModel.AHEAD_OF_TIME) // <-- !
            .setStateChanger(SimpleStateChanger(this))
            .install(this, binding.container, History.single(HomeKey))

        backPressedCallback.isEnabled = backstack.willHandleAheadOfTimeBack() // <-- !
        backstack.addAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback) // <-- !
    }

    override fun onDestroy() {
        backstack.removeAheadOfTimeWillHandleBackChangedListener(updateBackPressedCallback); // <-- !
        super.onDestroy()
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}
```

*Note: Before supporting predictive back gestures and using `EVENT_BUBBLING` back handling model, the code that interops
with OnBackPressedDispatcher looked like this:*

``` kotlin
class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger

    @Suppress("DEPRECATION")
    private val backPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (!Navigator.onBackPressed(this@MainActivity)) {
                this.remove() 
                onBackPressed() // this is the reliable way to handle back for now 
                this@MainActivity.onBackPressedDispatcher.addCallback(this)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(backPressedCallback) // this is the reliable way to handle back for now

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.container)
        
        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, binding.container, History.single(HomeKey))
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}
```

Originally, handling back was simpler, as all you had to do is override `onBackPressed()` (then
call `backstack.goBack()`, if it returned `true` then you would not call `super.onBackPressed()`) , but in order to
support `BackHandler` in Compose, or Fragments that use `OnBackPressedDispatcher` internally, you cannot
override `onBackPressed` anymore in a reliable manner.

With targetSdkVersion 34 and with `android:enableOnBackInvokedCallback="true"` enabled, `onBackPressed` (
and `KEYCODE_BACK`) will no longer be called. In that case, the `AHEAD_OF_TIME` back handling model should be preferred.

## Screens

`FirstScreen` looks like this (assuming you have `data object` enabled):

```groovy
kotlinOptions {
    jvmTarget = "1.8"
    languageVersion = '1.9' // data objects, 1.8 in 1.7.21, 1.9 in 1.8.10
}

kotlin.sourceSets.all {
    languageSettings.enableLanguageFeature("DataObjects")
}
```

```kotlin
// no args
@Parcelize
data object FirstScreen : DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = FirstFragment()
}
```

If you don't have `data object` support yet, then no-args keys look like this (to ensure stable
hashCode/equals/toString):

``` kotlin
// no args
@Parcelize
data class FirstScreen(private val noArgsPlaceholder: String = ""): DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = FirstFragment()
}

// has args
@Parcelize
data class FirstScreen(
    val username: String, 
    val password: String,
): DefaultFragmentKey() {
    override fun instantiateFragment(): Fragment = FirstFragment()
}
```

And `FirstFragment` looks like this:

``` kotlin
class FirstFragment: KeyedFragment(R.layout.first_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val key: FirstScreen = getKey() // params
    }
}
```

After which going to the second screen is as simple as `backstack.goTo(SecondScreen())`.


## Scopes

To simplify sharing data/state between screens, a screen key can implement `ScopeKey`.

The scope is described with a String tag, and services bound to that scope can be configured via `ScopedServices`.

Services bound to a `ServiceBinder` get lifecycle callbacks: `ScopedServices.Registered`, `ScopedServices.Activated`, or `Bundleable`.

This lets you easily share a class between screens, while still letting you handle Android's lifecycles seamlessly.

Using the `simple-stack-extensions`, this can be simplified using the `DefaultServiceProvider`. 

It looks like this:

``` kotlin
Navigator.configure()
    .setScopedServices(DefaultServiceProvider())
    /* ... */
```

And then:


``` kotlin
@Parcelize // typically data class
data object FirstScreen: DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    override fun instantiateFragment(): Fragment = FirstFragment()

    override fun getScopeTag() = toString()

    override fun bindServices(serviceBinder: ServiceBinder) {
        with(serviceBinder) {
            add(FirstScopedModel())
        }
    }
}

class FirstScopedModel : Bundleable, ScopedServices.Registered { // interfaces are optional
    ...
}

class FirstFragment : KeyedFragment(R.layout.first_fragment) {
    private val firstModel by lazy { lookup<FirstScopedModel>() }

    ...
}

class SecondFragment : KeyedFragment(R.layout.second_fragment) {
    private val firstModel by lazy { lookup<FirstScopedModel>() } // <- available if FirstScreen is in the backstack

    ...
}
```

And `FirstScopedModel` is shared between two screens.

Any additional shared scopes on top of screen scopes can be defined using `ScopeKey.Child`.

## What are additional benefits?

Making your navigation state explicit means you're in control of your application.

Instead of hacking around with the right fragment transaction tags, or calling `NEW_TASK | CLEAR_TASK` and making the screen flicker - you can just say `backstack.setHistory(History.of(SomeScreen(), OtherScreen())` and that is now your active navigation history.

Using `Backstack` to navigate allows you to move navigation responsibilities out of your view layer. No need to run FragmentTransactions directly in a click listener each time you want to move to a different screen. No need to mess around with  `LiveData<Event<T>>` or `SingleLiveData` to get your "view" to decide what state your app should be in either.

``` java
class FirstScopedModel(private val backstack: Backstack) {
    fun doSomething() {
        // ...
        backstack.goTo(SecondScreen)
    }
}
```

Another additional benefit is that your navigation history can be unit tested.

``` java
assertThat(backstack.getHistory()).containsExactly(SomeScreen, OtherScreen)
```

And most importantly, navigation (swapping screens) happens in one place, and you are in direct control of what happens in such a scenario. By writing a `StateChanger`, you can set up "how to display my current navigation state" in any way you want. No more `((MainActivity)getActivity()).setTitleText("blah");` inside Fragment's `onStart()`.

Write once, works in all cases.

``` java
override fun onNavigationEvent(stateChange: StateChange) { // using SimpleStateChanger
    val newScreen = stateChange.topNewKey<MyScreen>() // use your new navigation state

    setTitle(newScreen.title);

    ... // set up fragments, set up views, whatever you want
}
```

Whether you navigate forward or backward, or you rotate the screen, or you come back after low memory condition - it's irrelevant. The `StateChanger` will ***always*** handle the scenario in a predictable way.


## More information

For more information, check the [wiki page](https://github.com/Zhuinden/simple-stack/wiki).

## Talk

For an overview of the "why" and the "what" of what Simple-Stack offers, you can check out [this talk called `Simplified Single-Activity Apps using Simple-Stack`](https://www.youtube.com/watch?v=5ACcin1Z2HQ).

## What about Jetpack Compose?

See https://github.com/Zhuinden/simple-stack-compose-integration/ for a default way to use composables as screens.

This however is only required if ONLY composables are used, and NO fragments. When using Fragments, refer to the official [Fragment Compose interop](https://developer.android.com/jetpack/compose/interop/interop-apis#compose-in-fragments) guide.

For Fragment + Simple-Stack + Compose integration, you can also check [the corresponding sample](https://github.com/Zhuinden/simple-stack/tree/ced6d11e711fa2dda85e3bd7813cb2a192f10396/samples/advanced-samples/extensions-compose-example).

## License

    Copyright 2017-2023 Gabor Varadi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
