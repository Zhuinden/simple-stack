# Simple Stack

## Why do I want this?

To make navigation to another screen as simple as `backstack.goTo(SomeScreen())`, and going back as simple as `backstack.goBack()`.

No more `FragmentTransaction`s in random places. Predictable and customizable navigation in a single location.

## What is Simple Stack?

Simple Stack is a backstack library (or technically, a navigation framework) that allows you to represent your navigation state in a list of immutable, parcelable data classes ("keys").

This allows preserving your navigation history across configuration changes and process death - this is handled automatically.

Each screen can be associated with a scope, or a shared scope - to easily share data between screens.

This simplifies navigation and state management within an Activity using fragments, views, or whatever else.

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
implementation("com.github.Zhuinden:simple-stack:2.6.2")
implementation("com.github.Zhuinden:simple-stack-extensions:2.2.2")
```

or

``` groovy
// build.gradle
implementation 'com.github.Zhuinden:simple-stack:2.6.2'
implementation 'com.github.Zhuinden:simple-stack-extensions:2.2.2'
```

## How do I use it?

You can check out [**the tutorials**](https://github.com/Zhuinden/simple-stack/tree/611e8c7db738a776156b8f709db22b8e37413221/tutorials) for simple examples.

## Fragments

With Fragments, the Activity code typically looks like this:

``` kotlin
class MainActivity : AppCompatActivity(), SimpleStateChanger.NavigationHandler {
    private lateinit var fragmentStateChanger: DefaultFragmentStateChanger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentStateChanger = DefaultFragmentStateChanger(supportFragmentManager, R.id.container)

        Navigator.configure()
            .setStateChanger(SimpleStateChanger(this))
            .install(this, findViewById(R.id.container), History.of(FirstScreen()))
    }

    override fun onBackPressed() {
        if (!Navigator.onBackPressed(this)) {
            super.onBackPressed()
        }
    }

    override fun onNavigationEvent(stateChange: StateChange) {
        fragmentStateChanger.handleStateChange(stateChange)
    }
}
```

Where `FirstScreen` looks like this:

``` kotlin
@Parcelize // typically data class
class FirstScreen: DefaultFragmentKey() {
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
class FirstScreen: DefaultFragmentKey(), DefaultServiceProvider.HasServices {
    override fun instantiateFragment(): Fragment = FirstFragment()

    override fun getScopeTag() = javaClass.name

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
        backstack.goTo(SecondScreen())
    }
}
```

Another additional benefit is that your navigation history can be unit tested.

``` java
assertThat(backstack.getHistory()).containsExactly(SomeScreen(), OtherScreen())
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

## What about Jetpack Compose?

See https://github.com/Zhuinden/simple-stack-compose-integration/ for a default way to use composables as screens.

This however is only required if ONLY composables are used, and NO fragments. When using Fragments, refer to the official [Fragment Compose interop](https://developer.android.com/jetpack/compose/interop/interop-apis#compose-in-fragments) guide.

## License

    Copyright 2017-2021 Gabor Varadi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
