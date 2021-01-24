# Change log

-Simple Stack X.X.X (XXXX-XX-XX)
--------------------------------

- UPDATE: Add `simple-stack-example-multistack-nested-fragment` that shows how to create a fragment that has `Backstack`s for its child fragments, thus creating true multi-stack apps using nested backstacks.

-Simple Stack 2.5.0 (2020-12-16)
--------------------------------

- ADD: `Backstack.exitScope(scopeTag)`, `Backstack.exitScope(scopeTag, direction)` and `Backstack.exitScopeTo(scopeTag, targetKey, direction)`.

If a scope is found, the backstack now allows exiting from it. Providing a target allows exiting into a new target key.

- ADD: `AsyncStateChanger` for convenience.

Mirroring the addition of `SimpleStateChanger` for synchronous state changes, `AsyncStateChanger` is for async state changes (while still no longer having to remember checking for the same key being provided using `isTopNewKeyEqualToPrevious`).

- UPDATE: `state-bundle` is updated to `1.4.0` (add a few missing `@Nullable`s that became platform types instead of nullables).

-Simple Stack 2.4.0 (2020-07-08)
--------------------------------
- SIGNATURE CHANGE: `GlobalServices.Factory` now receives `Backstack` parameter in `create()`. (#231)

I'm aware this is technically "breaking", but the effect should be minor, and hopefully shouldn't cause problems.`

The `Backstack` cannot be added as a `service` directly, but it can be added as an `alias`.

- FIX: `GlobalServices.Factory`'s `create()` method was non-null, but `@Nonnull` was missing.

- MINOR FIX: Adding the `Backstack` from `serviceBinder.getBackstack()` with `addService()` would cause a loop in `toBundle()`. Now it explicitly throws `IllegalArgumentException` instead sooner (not reported before).

- DEPRECATED: `backstack.removeAllStateChangeCompletionListeners()`. This was added "for convenience", but in reality it is not a good/safe API, and it should not exist.

- UPDATE: Create and release `simple-stack-extensions` for default scoping and default fragment behaviors.

- ADD: `GlobalServices.SCOPE_TAG` to make it possible to see the scope tag of global services without relying on internals.

-Simple Stack 2.3.2 (2020-04-11)
--------------------------------

- FIX: Bug introduced in 2.3.1, using `backstack.removeAllStateChangeCompletionListeners()` would remove an internal completion listener and therefore scope activation callbacks would stop being dispatched. 

-Simple Stack 2.3.1 (2020-03-31)
--------------------------------

- FIX: Ensure that if multiple navigation actions are enqueued, then scope activation dispatch only occurs for the final state change, instead of potentially running into an AssertionError. (#220, thanks @valeriyo)

If you use either `ScopeKey` or `ScopeKey.Child`, it is advised to update, and get this bugfix.

-Simple Stack 2.3.0 (2020-02-27)
--------------------------------

- CHANGE: Remove dependency on `android.support.annotation.*`. With that, there should be no dependency from the library on either `android.support.*` or `androidx.*`.

Replaced it using `javax.annotation.Nullable` and `javax.annotation.Nonnull` provided by `api("com.google.code.findbugs:jsr305:3.0.2")`.

- UPDATE: `state-bundle` is updated to `1.3.0` (Remove dependency on `android.support.annotation.*`, replace with `javax.annotation.*`).

With these changes, Jetifier should no longer be needed when using Simple-Stack.

-Simple Stack 2.2.5 (2020-02-18)
--------------------------------

- CHANGE: `Backstack.getSavedState(Object key).getBundle()` is now initialized to an empty `StateBundle` instead of `null` (but is still nullable because of `setBundle()`).

- FIX: `Backstack.persistViewToState(Object key)` no longer creates a new `SavedState` instance, and uses `getSavedState` to re-use (or create) the existing one.

- FIX: Ensure that `backDispatchedServices` is also cleared after execution of `dispatchBack`. 

-Simple Stack 2.2.4 (2020-01-30)
--------------------------------

- UPDATE: `state-bundle` is updated to `1.2.2` (Fix a bug in `StateBundle.equals()`).

-Simple Stack 2.2.3 (2020-01-23)
--------------------------------

- FIX: `ScopedService.Activated` callback could immediately execute a navigation action, which if destroyed the current scope, then it could result in an `AssertionError` (#215).

- ADD: `SimpleStateChanger` for convenience when a `StateChanger` is not intended to take asynchronous execution into account. 

-Simple Stack 2.2.2 (2020-01-17)
--------------------------------

- FIX: Ensure that unused services can be GCed inside ScopeManager (previously they could be kept alive in a map, #213).

-Simple Stack 2.2.1 (2020-01-12)
--------------------------------

- FIX: The explicit parent scope chain was not always resolved correctly if a key is only a ScopeKey.Child, but not a ScopeKey, and the key did not register any new scopes (as all scopes defined by the ScopeKey.Child had already been registered by previous keys).

This could provide incorrect results in `findScopesForKey(key, EXPLICIT)`, and could skip a `HandlesBack` service in the current top key's explicit parent chain.

-Simple Stack 2.2.0 (2019-12-30)
--------------------------------

- ADDED: `ScopedService.HandlesBack`.

When a service implements `HandlesBack`, then when `Backstack.goBack()` is called, it is first dispatched across the current active explicit scope chain.

This allows handling "back", without having to dispatch it through the active view hierarchy (in order to get access to it in scoped services).

- FIX: `Backstack.moveToTop()` did not re-order the scope hierarchy according to the new active keys (as the scope order depended on construction order, but existing scopes were not recreated).

This could have been a problem if services used the same name across multiple scopes, and the keys were re-ordered in place (not add/remove).

- ADDED: `Backstack.setGlobalServices(GlobalServices.Factory)` and its `BackstackDelegate`/`Navigator` equivalent.

This allows delaying the instantiation of services when the global scope is actually being created, rather than providing them immediately.

Please note that providing a `GlobalServices.Factory` will override whatever `GlobalServices` was previously set.

Also note that the `GlobalServices.Factory` should not be an anonymous inner class / lambda / inner class, as it is kept for configuration changes.

- DEPRECATED: `BackstackDelegate`.

With the renaming of `BackstackManager` to `Backstack` in 2.0.x, it's become easier to use `Backstack` directly than juggling the `BackstackDelegate`.

Also, using `Navigator` with Fragments seems to have no side-effects, therefore this is now the preferred approach (since setting a non-default state changer calls `setShouldPersistContainerChild(false)`, also since 2.0.x).

Therefore, using `Navigator` is now preferred over `BackstackDelegate`.

- FIX: `Backstack.forceClear()` now calls `finalizeScopes()` first to ensure that scoped services are also properly reset.

-Simple Stack 2.1.2 (2019-10-10)
--------------------------------

- BEHAVIOR CHANGE: The navigation operations `goBack()`, `replaceTop()`, `jumpToRoot()`, `goUp()`, `goUpChain()`, and `goTo()` (when going to existing element) are now considered "terminal" operations.

Terminal operation means that actions (that are not `setHistory`) called on the Backstack are *ignored* while the state change of the terminal action has not been completed yet.

This is done to eliminate the possibility of enqueueing incorrect "forward" navigation immediately when a "back" navigation is happening, that could potentially create "illegal" navigation history.

Illegal navigation history is a problem when using implicit scopes, as with the right button mashing, you could potentially "skip" an expected screen, and not have registered its services.

Therefore, the possibility of this scenario is now blocked.


-Simple Stack 2.1.1 (2019-09-26)
--------------------------------

- FIX: Make `findScopesForKey` work consistently even if a key is not a `ScopeKey`.

- FIX: Add missing `@NonNull` on Context argument on some methods of Navigator.

-Simple Stack 2.1.0 (2019-09-25)
--------------------------------

- FIX: Ensure that while navigation is in progress, `lookupService()` (and other service lookup methods) can access all currently built scopes, rather than only the latest navigation history currently being navigated to.

This fix is crucial when `lookupService` is used inside `onFinishInflate` method of views inflated by ViewPager adapters; and returning from process death, navigation to a different screen is immediate (f.ex. deep-linking via notifications).

-Simple Stack 2.0.3 (2019-05-20)
--------------------------------

- ADDED: `ServiceBinder.addAlias()` to allow adding an alias to an added service: available for look-up, but without getting callbacks by this registration (thus avoiding unnecessary `toBundle()` calls for a multi-registered service).

-Simple Stack 2.0.0 (2019-04-30)
--------------------------------

**MAJOR API BREAKING CHANGES!** To create better consistency and naming, certain core APIs were renamed, moved around, restructured, etc.

This means that 1.x and 2.x shouldn't be used in a project at the same time, because they're not compatible.

- BREAKING CHANGE: `BackstackManager` is now `Backstack`.

What was called `Backstack` is now called `NavigationCore` and is an internal class (not part of public API).

(!) This also means that the `new Backstack(List<?>)` constructor is replaced with `new Backstack(); backstack.setup(List<?>)`.

All sane public APIs of NavigationCore were moved over to the new Backstack (think navigation-related ones).

This means that wherever you used to receive a `Backstack`, now you still receive a "Backstack", but it has additional functionality (such as `lookupService`).

As a result, the following methods no longer exist:

  - `BackstackDelegate.getManager()` is removed

  - `Navigator.getManager()` is removed

  - `ServiceBinder.getManager()` is removed

With that, `StateChange.getBackstack()` now returns what was previously the `BackstackManager`, allowing access to scope-related functions.

- BREAKING CHANGE: `ScopedServices.Scoped` is removed, and completely replaced with `ScopedServices.Registered`.

- BREAKING CHANGE: `ScopedServices.Activated` behavior change, now only called once rather than multiple times if the service is in an active explicit parent chain.

  - `onScopeActive()/onScopeInactive()` -> `onServiceActive()`/`onServiceInactive()`.

Previously, you could get multiple callbacks to `onScopeActive` per each activated scope the service was in, now it is tracked per service instead and only called for `0->1` and `1->0`.

- BREAKING CHANGE: `setShouldPersistContainerChild(true)` was the default, now it is default to `false`. It's only `true` by default if the `DefaultStateChanger` is used.

If you use a custom-view-based setup and a custom state changer, please make sure to set `Navigator.configure().setShouldPersistContainerChild(true).install(...)`.

- BREAKING CHANGE: `ScopedServices.ServiceBinder` is now moved to top-level, now accessible as `ServiceBinder`.

- BREAKING CHANGE: `ViewChangeHandler.CompletionCallback` renamed to `ViewChangeHandler.ViewChangeCallback`.

- BREAKING CHANGE: `StateKey` is renamed to `DefaultViewKey`.

- BREAKING CHANGE: `HistoryBuilder` is now `History.Builder`. The deprecated `HistoryBuilder` factory methods were removed.

- BREAKING CHANGE:

  - `StateChange.getPreviousState()` -> `StateChange.getPreviousKeys()`
  - `StateChange.getNewState()` -> `StateChange.getNewKeys()`
  - `StateChange.topPreviousState()` -> `StateChange.topPreviousKey()`
  - `StateChange.topNewState()` -> `StateChange.topNewKey()`
  - `StateChange.isTopNewStateEqualToPrevious()` -> `StateChange.isTopNewKeyEqualToPrevious()`

- BREAKING CHANGE: `StateChange.backstack()` -> `StateChange.getBackstack()`.

- BREAKING CHANGE: ServiceBinder method renaming for sake of consistency.

  - `ServiceBinder.add()` -> `ServiceBinder.addService()`
  - `ServiceBinder.has()` -> `ServiceBinder.hasService()`
  - `ServiceBinder.get()` -> `ServiceBinder.getService()`
  - `ServiceBinder.canFind()` -> `ServiceBinder.canFindService()`
  - `ServiceBinder.lookup()` -> `ServiceBinder.lookupService()`
  - `ServiceBinder.canFindFrom()` -> `ServiceBinder.canFindFromScope()`
  - `ServiceBinder.lookUpFrom()` -> `ServiceBinder.lookupFromScope()`

- CHANGE/FIX: Added missing `@NonNull` annotation on `KeyContextWrapper.getKey()`. Now throws exception if the key is not found (previously returned null).


-Simple Stack 1.14.1 (2019-04-26)
--------------------------------

- FIX: `onServiceUnregistered()` was called multiple times if the service was being unregistered from a scope where it was registered multiple times.

- SAMPLE UPDATE: Safer version of the FragmentStateChanger that handles re-entrancy of `back` and going to the same target as where we were (handle `fragment.isRemoving`).

- SAMPLE UPDATE: MVP/MVVM samples have a better packaging structure.


-Simple Stack 1.14.0 (2019-03-16)
--------------------------------

- ADD: `ScopedServices.Registered` to receive a service lifecycle callback when a service is added to a scope for the first time (it was not in any other scopes).

- ADD: `findScopesForKey(Object key, ScopeLookupMode mode)` to retrieve the list of scopes accessible from a given key.

- FIX: A service registered in multiple scopes would receive `fromBundle(stateBundle)` callback in each scope where it was registered and when that given scope was entered, restoring potentially outdated state on back navigation.

Now, a service will only receive `fromBundle` callback before its `onServiceRegistered()` callback, and not before *each* `onEnterScope(scopeTag)` callbacks.

- FIX: during `backstackManager.finalizeScopes()`, `onExitScope` and `onScopeInactive` were dispatched in an incorrect order across nested explicit parents.

- CHANGE: `persistViewToState()`/`restoreViewFromState()` now use a separate bundle from the one that's publicly visible on SavedState.


-Simple Stack 1.13.4 (2019-03-10)
--------------------------------

- FIX: calling `backstackManager.finalizeScopes()` multiple times now results in consistent and defined behavior (namely, it gets ignored).

- CHANGE: navigation that occurs after `backstackManager.finalizeScopes()` will now trigger reconstruction and proper callbacks of services in a consistent manner.

- FIX: `ScopedServices.Activated`'s `onScopeInactive()` during scope finalization was called in order from explicit parent to child, instead of child to explicit parent.


-Simple Stack 1.13.3 (2019-02-27)
--------------------------------

- FIX: NPE when `canFindFromScope()` was used on an uninitialized stack, instead of returning `false`.

- ADD: `ScopeLookupMode` for `canFindFromScope()` and `lookupFromScope()` methods, which allows restricting the lookup only to the explicit parent chain).

- ADD: `setGlobalServices()` to allow setting global services (that functions as the last parent of any parent chains).

-Simple Stack 1.13.2 (2019-02-05)
--------------------------------

- FIX: Fix that a service registered multiple times in the same scope with different tags would receive service lifecycle callbacks as many times as it was registered.

- ADD: Convenience method `stateChange.isTopNewStateEqualToPrevious()` to replace `stateChange.topNewState<Any>() == stateChange.topPreviousState()` condition check. It does the same thing, but maybe it's a bit easier to read.

- FIX: Fix a typo which resulted in not throwing if the provided service tag was `null` (whoops. -_-)

- UPDATE: `Backstack` now checks if altering methods are called from the thread where the backstack was created. 

-Simple Stack 1.13.1 (2018-11-25)
--------------------------------

- UPDATE: Added `simple-stack-example-scoping` to showcase the usage of `ScopeKey.Child`, with Fragments and Navigator.

- ADDED: `lookupFromScope()` and `canFindFromScope()` methods that begin the lookup from the specified scope, instead of the active-most one. This is to allow safer look-ups when the same service tag is used in different scopes.

- UPDATE: Better error message if the scope does not exist for lookup (also provides the currently accessed scopes).

-Simple Stack 1.13.0 (2018-09-10)
--------------------------------
- ADDED: Adds `ScopeKey.Child` interface, which allows the definition of *explicit* parent hierarchy of a given scope.

Even by default, there is an implicit hierarchy between screens: it is possible to look up services defined by previous keys.

However, there are times when we must define scopes that are supersets of multiple screens. In this case, we know we are on a given screen, within a given state, and we require a superscope to exist that is shared across multiple screens.

In this case, the key can define an explicit parent hierarchy of scopes. These scopes are created before the key's own scope (assuming the key is also a ScopeKey).

The parent scopes are only destroyed after all their children are destroyed.

`lookupService()` prefers explicit parents, however will also continue to seek the service across implicit parents, and their explicit parent chain as well. 


-Simple Stack 1.12.3 (2018-09-02)
--------------------------------
- CHANGE: When `lookupService` cannot find the service, the exception message is improved (and tells you what *could* be wrong in your configuration).

- UPDATE: `mvp-view`, `mvp-fragments` and `mvvm-fragments` samples now use `ScopedServices` (and `Bundleable`)  to retain presenters/viewmodels across config changes and have their states persisted/restored across process death.  

-Simple Stack 1.12.2 (2018-08-29)
--------------------------------
- CHANGE: `AnimatorViewChangeHandler` has an overridable method called `resetPreviousViewValues()`, which receives the previous view after animation is complete.

- CHANGE: `FadeViewChangeHandler` and `SegueViewChangeHandler` now reset `alpha = 1f` and `translationX = 0f` respectively, after animation is complete and the view is removed.

-Simple Stack 1.12.1 (2018-08-28)
--------------------------------
- ADDED: `ScopedServices.Activated`. Implementing `Activated` for a scoped service makes the service receive a callback when the scope it is bound to becomes the top-most scope, and when it stops being the top-most scope.

There are strict ordering guarantees that `onEnterScope`, `onScopeActive`, `onScopeInactive`, `onExitScope` are called in *this* order.

`onScopeInactive` is called in reverse order (just like `onExitScope`).

When navigating from one scope to another scope, the new scope becomes active before the previous scope becomes inactive.

-Simple Stack 1.12.0 (2018-08-23)
--------------------------------
- CHANGE: `backstack.top()` and `backstack.root()` now throw exception (just like `fromTop()`) if the method is called before a StateChanger is set (and the backstack becomes initialized). This makes using `root()`/`top()` nicer in Kotlin.

- FIX: During a *second* "initialize" state change (which happens when calling `setStateChanger()`), accessing the Backstack's `getHistory()`, `top()` and `root()` inside the `StateChanger` could return incorrect value.

- UPDATE: MVP samples are now written in Kotlin. MVVM sample now has a better SQLite-based non-Room reactive wrapper (for people trying out SQLite without Room). Some samples were renamed.

-Simple Stack 1.11.7 (2018-08-18)
--------------------------------
- ADDED: `ServiceBinder.getBackstack()` method. This allows scoped services to be given the backstack as constructor argument.

-Simple Stack 1.11.6 (2018-08-14)
--------------------------------
- ADDED: `Navigator.hasScope(scopeTag)`, `BackstackDelegate.hasScope(scopeTag)`, `BackstackManager.hasScope(scopeTag)`.

- ADDED: `Navigator.canFindService(Context, serviceTag)`, `BackstackDelegate.canFindService(serviceTag)`, `BackstackManager.canFindService(serviceTag)` to check if `lookup` can find the service.

- ADDED: `ServiceBinder.lookup()` and `ServiceBinder.canFind()` to inherit from currently existing scopes while creating service binding.

- CHANGE: `onExitScope(scopeTag)` is now ensured to happen in reverse order compared to `onEnterScope(scopeTag)` (both in terms of scope creation order and service binding order).

-Simple Stack 1.11.4 (2018-08-10)
--------------------------------
- ADDED: `Navigator.isNavigatorAvailable(Activity)` to ensure the ability to check if the `BackstackHost` is added to the Activity.

- ADDED: `BackstackManager.lookupService(serviceTag)`, `BackstackDelegate.lookupService(serviceTag)`, and `Navigator.lookupService(Context, serviceTag)`, which attempts to look up the service in all currently existing scopes (starting from the newest added scope).

-Simple Stack 1.11.2 (2018-07-26)
--------------------------------
- UPDATE: `state-bundle` is updated to 1.2.1.

- CHANGE: Allow calling `BackstackDelegate.setScopedServices(activity, scopedServices)` once after an `onDestroy()` callback (to allow setting back the Activity).

- CHANGE: Allow calling `BackstackDelegate.setScopedServices(null, scopedServices)`. In this case, `onDestroy()` will finalize scopes - normally it only does that if Activity is finalizing.

-Simple Stack 1.11.1 (2018-07-14)
--------------------------------
- API CHANGE: `backstackDelegate.setScopedServices(ScopedServices)` is now `backstackDelegate.setScopedServices(Activity, ScopedServices)`.

- FIX: If enclosing Activity is destroyed (`onDestroy`) *and* `Activity.isFinishing()`, then the existing scopes are destroyed along with it so that `Scoped.onExitScope()` is called properly, and resources are cleaned up as expected across closing the app and restarting it quickly.

- MINOR FIX: Added `ScopedServices` javadoc and missing`@NonNull` on ServiceBinder.

- DEPRECATED: `reset()` method. Renamed to `forceClear()`. You probably don't need it, but that wasn't clear enough.

-Simple Stack 1.11.0 (2018-07-01)
--------------------------------
- ADDED: Ability to share data across screens via scoped services.

To use, the key must implement `ScopeKey` and define its scope's tag. If `ScopeKey` is used, then a `ScopedServices` must be provided.

Currently, a scope can be shared across keys, but there is no scope inheritance, and there is no way to have multiple scopes for a given key.

To use, one must set an implementation of `ScopedServices` on either `BackstackDelegate`, `Navigator.Installer`, or `BackstackManager`.

Services that are `Bundleable` and registered in a given scope receive callbacks to `toBundle()` and `fromBundle()` to persist/restore their state.

Services registered in a given scope receive `onEnterScope()` and `onExitScope()` callbacks if they implement `ScopedServices.Scoped`.

Tip: A possible way to simplify the usage of `service tags` is to define an inline reified extension function for `ServiceBinder` to default to using `T::class.java.name`.

-Simple Stack 1.9.3 (2018-06-28)
--------------------------------
- ADDED: Ability to change duration, interpolation and start delay of `AnimatorViewChangeHandler`.

- ADDED: `jumpToRoot(direction)`.

-Simple Stack 1.9.2 (2018-06-01)
--------------------------------
- MINOR CHANGE: `DefaultStateChanger` uses `FadeViewChanger` for `StateChange.REPLACE` by default, instead of `NoOpViewChangeHandler`.

-Simple Stack 1.9.1 (2018-05-20)
--------------------------------
- Fix: `History.single()` should return `History<T>`, not `List<T>`.

- ADDED: `jumpToRoot()` and `moveToTop()` convenience operators to `Backstack`.

- ADDED: `goUp(Object, boolean fallbackToBack)` and `goUpChain(List<?>, boolean fallbackToBack)` to allow opt-in for the newly provided navigation principle: "Up and Back are equivalent within your app's task"

-Simple Stack 1.9.0 (2018-03-04)
--------------------------------

- DEPRECATED: HistoryBuilder's factory methods are moved from HistoryBuilder to the newly added `History` class.

`HistoryBuilder.from(T... objects)` -> `History.builderOf(T... objects)`

`HistoryBuilder.from(...)` -> `History.builderFrom(...)`

`HistoryBuilder.single()` -> `History.single()`

`HistoryBuilder.newBuilder()` -> `History.newBuilder()`

Also adds `History.of(T... objects)` instead of `HistoryBuilder.of(...).build()`.

- ADDED: `History` class, an immutable list with additional operations over `List<T>` - as some methods' return type.

I also changed some return types (in history building) from `List<Object>` to `<T> List<T>`,
this resulted in having to change some `List<Object>`s to `List<?>` on the receiving side if used in assignment.

So if you run into that, just change `List<Object>` to `List<?>` and it should work.

- ADDED: Long-overdue empty constructor for `BackstackDelegate` and `backstack.setStateChanger(StateChanger)`.

- UPDATE: Kotlin sample replaces `PaperParcel` with `@Parcelize`.

-Simple Stack 1.8.2 (2018-01-20)
--------------------------------

- CRITICAL FIX: `1.8.1 (2018-01-17)` didn't retrieve `state-bundle 1.2.0` as transitive dependency because of `com.github.dcendents:android-maven-gradle-plugin:1.5`. It is updated to `2.0` to fix publishing to Jitpack.

- ADDED / CHANGE: `Navigator.findActivity(Context)` is now public. It also casts the returned Activity to whatever subclass type is expected.

- ADDED: `Backstack.fromTop(int offset)` method, which provides the element in the backstack from the top with a given offset. 

- Updated State-Bundle to 1.2.0.

- Updated to use implementation/api and AS 3.0's tooling.

- Updated implementation lib versions in samples and tests.

- Updated Kotlin example to use Fragment-based usage.

-Simple Stack 1.8.0 (2017-10-25) 
--------------------------------

- BREAKING(?) CHANGE / FIX: when `goBack()` returns false, then the backstack is not cleared automatically. Added `reset()` to allow imitating the previous behavior. 

Previous behavior would now be the same as:

    if(!backstack.goBack() && !backstack.isStateChangePending()) {
        backstack.reset();
    }
    
Honestly, this might have been unexpected, as `goBack()` returning `false` had the side-effect of clearing the stack, and next state change using the initial key!

The test that checks for this has been changed to use the above construct. Another test of course has been added to validate new behavior.

Also, to eliminate the possibility of `reset()` misuse, it is only allowed when there are no pending state changes.

- BREAKING CHANGE: `getInitialParameters()` is renamed to `getInitialKeys()`. 

- FIX: `getInitialParameters()` returned the actual list instead of an unmodifiable copy, it returns the keys provided at initialization.

- ADDED: `replaceTop()` backstack operator, which replaces current top with the provided key.

- ADDED: `goUp()` backstack operator, which will go back to the provided key if exists, replace top with new key otherwise.

- ADDED: `goUpChain()` backstack operator, which will:

  - If the chain of parents is found as previous elements, then it works as back navigation to that chain.
  - If the whole chain is not found, but at least one element of it is found, then the history is kept up to that point, then the chain is added, any duplicate element in the chain is added to the end as part of the chain.
  - If none of the chain is found, the current top is removed, and the provided parent chain is added.
  
  I added a bunch of tests for this, hopefully I didn't forget anything!

- ENHANCEMENT/FIX: `HistoryBuilder.get()` is now `@NonNull`, because `HistoryBuilder.from(List<?>)` throws if List contains `null`.

- ENHANCEMENT: `getHistory()` and `getInitialParameters()`  also returns a `List<T>` in which each element is cast to `T`. 

- FIX: `BackstackDelegate.setStateChanger()` should have been allowed even without calling `backstackDelegate.onCreate()` first. All the samples use `new BackstackDelegate(null)` so it never came up.

- ENHANCEMENT: Some improvement to `persistViewToState()` exception message if the view has no key (so that it references `KeyContextWrapper` and `stateChange.createContext()`).

-Simple Stack 1.7.2 (2017-07-24)
--------------------------------
- MINOR CHANGE + ENHANCEMENT: `StateChange.getNewState()` and `StateChange.getPreviousState()` return a copy of the list (it was already a copy, don't worry), where each item is casted to `<T>` specified as generic parameter.

For example, the following can be changed from:

``` java
for(Object _newKey : stateChange.getNewState()) {
    Key newKey = (Key)_newKey;
    // ...
}
```

to:

``` java
for(Key newKey : stateChange.<Key>getNewState()) {
   // ...
}
```

And the following works now as well:

``` java
List<Key> newKeys = stateChange.getNewState(); // used to return List<Object>
```

-Simple Stack 1.7.1 (2017-07-11)
--------------------------------
- ADDED: `BackstackDelegate.registerForLifecycleCallbacks(Activity)` convenience method (API 14+).

This method allows you to call this after `BackstackDelegate.onCreate()`, after which the following 4 methods no longer need to be called manually:

    - `onPostResume()`
    - `onPause()`
    - `onSaveInstanceState(Bundle)`
    - `onDestroy()`

Therefore the callbacks that ought to be called remain as `onCreate()`, `onRetainCustomNonConfigurationInstance()`, and of course `onBackPressed()`.

-Simple Stack 1.7.0 (2017-07-04)
--------------------------------
- REMOVED: `BackstackManager.StateChangeCompletionListener`. It is replaced by `Backstack.CompletionListener`, which was added back in 0.9.1 (and is more reliable).

This also fixes a possible bug with incorrect call order of state change completion listeners.

The API is otherwise exactly the same, `StateChangeCompletionListener` should have been `Backstack.CompletionListener` from the start.

- ADDED: `backstackDelegate.getManager()`, just to make sure its API mirrors `Navigator`.

-Simple Stack 1.6.3 (2017-06-28)
--------------------------------
- Added missing `@NonNull` and `@Nullable` annotations where applicable.

-Simple Stack 1.6.2 (2017-05-14)
--------------------------------
- MINOR CHANGE: `DefaultStateChanger` no longer explicitly demands a `StateKey`, because both `LayoutInflationStrategy` and `GetViewChangeHandlerStrategy` can be re-defined for custom behavior.

- Added `GetViewChangeHandlerStrategy` to `DefaultStateChanger` to allow re-defining the view change handler behavior.

- Added `ContextCreationStrategy` to `DefaultStateChanger` to support Mortar scopes, or anything similar in design.

- Added `BackstackManager.StateChangeCompletionListener` to add a hook where you can listen for the completion of state changes reliably - even if they were forced to execute.

Also added `addStateChangeCompletionListener` to `BackstackDelegate` and `Navigator.Installer` accordingly. 

Please make sure it does not retain a reference to enclosing Activity, to avoid memory leaks.

- Minor fix: `setKeyFilter()`, `setKeyParceler()`, and `setStateClearStrategy()` in `BackstackDelegate` now throw if they are set after calling `onCreate()`, as per docs.

- Bump `state-bundle` version to `1.1.5`

- ADDED: `simple-stack-mortar-sample`, which is based on `mortar-sample` from `square/mortar`, but using `Simple-Stack` and `Service-Tree`.

-Simple Stack 1.6.1 (2017-05-08)
--------------------------------
- Added `GetPreviousViewStrategy` to `DefaultStateChanger` as per request (#36).

-Simple Stack 1.6.0 (2017-05-03)
--------------------------------
- Added `KeyFilter` to allow clearing out keys on process death that should not be restored.
- Added `stateChange.backstack()` which returns the backstack this state change was executed by.
- Added `DefaultStateChanger.ViewChangeStartListener` for *before* the view change, but *after* the state restore
- Added `DefaultStateChanger.LayoutInflationStrategy` to support asynchronous layout inflation (if you need it) or hopefully Anko

-Simple Stack 1.5.3 (2017-04-21)
--------------------------------
- Bump `state-bundle` version to `1.1.4`

-Simple Stack 1.5.2 (2017-04-12)
--------------------------------
- Bump `state-bundle` version to `1.1.0`

-Simple Stack 1.5.1 (2017-04-07)
--------------------------------
- Added a method to `DefaultStateChanger` to allow perform view change with an externally specified direction.

-Simple Stack 1.5.0 (2017-04-07)
--------------------------------
- Merged `zhuinden/navigator` into `zhuinden/simple-stack`.

- ADDED: `Navigator` class as an optional replacement for `BackstackDelegate` (API 11+).

`BackstackDelegate` had a lot of callbacks to remember (`onPause()`, `onResume()`, `onRetainCustomNonConfigurationInstance()`, `onDestroy()`),
but more importantly you had to manage saving out the current view's state in `onSaveInstanceState()` manually.

With Navigator, this is all hidden in the `BackstackHost` installed by `Navigator.install()` (or `Navigator.configure()...install()`, so this problem is solved for you.

- ADDED: `DefaultStateChanger` that by default uses **`Navigator`-based installation**.

To use `DefaultStateChanger` with `BackstackDelegate`, you must provide `DefaultStateChanger.configure().setStatePersistenceStrategy()` and delegate persistence calls to your delegate.

- ADDED: `StateKey` interface used by `DefaultStateChanger`.

- ADDED: default view change handlers for `DefaultStateChanger`.

- All examples (**except** the `fragment` and `multistack` samples) were updated to use `Navigator`.

- `simple-stack-example-mvp` no longer uses `square/coordinator`, it uses custom viewgroups instead.

This is because `Coordinator` gets created only after `container.addView()`, which makes it hard to work with.

-Simple Stack 1.4.4 (2017-03-28)
--------------------------------
- ADDED: `backstack.top()` method that returns the last element in the backstack or `null`

-Simple Stack 1.4.3 (2017-03-25)
--------------------------------
- FIX: Fixed a bug that if a restored backstack is cleared and an initialize state change is triggered,
       then the restored keys were used instead of the initial key
    (this only surfaced if you attempt to use multiple backstacks, and a cleared backstack is re-used)

-Simple Stack 1.4.2 (2017-03-20)
--------------------------------
- CHANGE: Decreased minSDK to 1.

-Simple Stack 1.4.1 (2017-03-09)
--------------------------------
- MINOR FIX: Adjusted exception message in `BackstackManager` to say `setup()`.

-Simple Stack 1.4.0 (2017-03-06)
--------------------------------
- BREAKING CHANGE: `StateBundle` is moved from `com.zhuinden.simplestack.StateBundle` to `com.zhuinden.statebundle.StateBundle`

- CHANGE: `StateBundle` is moved to https://github.com/Zhuinden/state-bundle and is a compile dependency of `simple-stack`

-Simple Stack 1.3.0 (2017-03-03)
--------------------------------
- REFACTOR: `BackstackDelegate` is separated into `BackstackManager`.

- ADDED: `BackstackManager` class to help with creating backstacks inside views.

- ENHANCEMENT: `BackstackManager` is now `Bundleable`, therefore its state can be automatically restored along with other managed services (see examples).

- CHANGE: `clearStatesNotIn()` is no longer a method of `BackstackDelegate` or `BackstackManager`, it can be specified as custom using a `BackstackManager.StateClearStrategy`.

- ADDED: `simple-stack-example-services` that shows how to use `service-tree` with `simple-stack` to store scoped services that can have their states restored and survive configuration change.

- ADDED: `simple-stack-example-nestedstack` that shows how to use view-level `BackstackManager` stored in `service-tree` to have nested backstacks inside views.

-Simple Stack 1.2.1 (2017-02-28)
--------------------------------
- FIX: `HistoryBuilder` should receive `List<?>` as parameters, not `List<Object>` on all methods.

-Simple Stack 1.2.0 (2017-02-28)
--------------------------------
- BREAKING CHANGE: `Bundleable` and `SavedState` now use `StateBundle` class.

- ENHANCEMENT: Added `StateBundle` class to replace `android.os.Bundle`.

-Simple Stack 1.1.1 (2017-02-19)
--------------------------------
- FIX: A bug that allowed the possibility that an uninitialized backstack could restore its history to be an empty list after process death.

- ADDED: `simple-stack-example-multistack` for handling multiple backstacks in the same Activity using a BottomNavigationView.

-Simple Stack 1.1.0 (2017-02-18)
--------------------------------
- BREAKING CHANGE: `Backstack`'s APIs return `Object` instead of `Parcelable` (that includes `StateChange`, initial keys, `HistoryBuilder`, etc.)

- ENHANCEMENT: Added `KeyParceler` interface to allow defining custom strategy in order to turn keys into `Parcelable` (for example, using `Parceler` library instead)

-Simple Stack 1.0.0 (2017-02-15)
--------------------------------
- RELEASE: 1.0.0!

- ENHANCEMENT: Javadoc for all public classes and methods.

-Simple Stack 0.9.6 (2017-02-14)
---------------------------------
- INTERNAL CHANGE: Hidden `stateChangeComplete()` from public API of `BackstackDelegate`, it shouldn't have been public.

-Simple Stack 0.9.5 (2017-02-13)
---------------------------------
- INTERNAL CHANGE: `clearStatesNotIn()` now receives both `keyStateMap` and `StateChange`, instead of just the new state.

- ENHANCEMENT: Added `HistoryBuilder.from(Backstack)` and `HistoryBuilder.from(BackstackDelegate)` convenience methods.

- ENHANCEMENT: Added `HistoryBuilder.isEmpty()` method, and implements `Iterable<Parcelable>`.

- ADDED: `flow-masterdetail-fragments` example.

- FIX: A bug in `flow-masterdetail` sample that prevented Master's state from being persisted if detail directly opens a detail.

-Simple Stack 0.9.3 (2017-02-12)
---------------------------------
- ENHANCEMENT: Added ability to force execute pending state changes with `Backstack.executePendingStateChange()`.

- INTERNAL CHANGE: `BackstackDelegate.onDestroy()` calls `backstack.executePendingStateChange()` to prevent hanging state changes.

- ADDED: `ObjectAnimator`-based segue animation to MVP example.

-Simple Stack 0.9.2 (2017-02-11)
---------------------------------
- BREAKING CHANGE(?): `CompletionListener` no longer receives `isPending` parameter.

- ADDED: `Backstack.isStateChangePending()` to replace `isPending`.

- ENHANCEMENT: Added some missing `@NonNull` and `@Nullable` annotations.

- ADDED: Apache license notes, and improved the README.

-Simple Stack 0.9.1 (2017-02-09)
---------------------------------
- BREAKING CHANGE(!): `BackstackDelegate` has a new method which **must be called**: `backstackDelegate.onDestroy()`
    Not calling `backstackDelegate.onDestroy()` will most likely result in memory leak, so please make sure you call it paired with `onCreate()`.

- BREAKING CHANGE: `BackstackDelegate.clearStatesNotIn()` is no longer public, because it is automatically managed on state change completion.

- ENHANCEMENT: Added `Backstack.CompletionListener` which listens to when backstack has completed a state change.
    Added `Backstack.addCompletionListener()` and `Backstack.removeCompletionListener()` methods.
    The backstack keeps a strong reference to your completion listener, so make sure you remove your change listener when no longer needed.

- ENHANCEMENT: It is no longer the responsibility of the `StateChanger` to call `backstackDelegate.clearStatesNotIn()`.
    The `BackstackDelegate` registers itself as a `CompletionListener`, and therefore it can call `clearStatesNotIn()` automatically.

- ENHANCEMENT: Added `flow-sample` changed to use Simple-Stack, as name `simple-stack-flow-masterdetail`.

-Simple Stack 0.8.3 (2017-02-04)
---------------------------------
- ENHANCEMENT: Added `BackstackDelegate.setPersistenceTag(String)` for support of multiple backstacks. It must be called before `BackstackDelegate.onCreate()`.

-Simple Stack 0.8.2 (2017-02-03)
---------------------------------
- CHANGE: `KeyContextWrapper` is public again
- ENHANCEMENT: Created `fragments` example based on `mvp` example.

-Simple Stack 0.8.1 (2017-02-02)
---------------------------------
- BREAKING(?) CHANGE: Renamed `HistoryBuilder.peek()` to `HistoryBuilder.getLast()`
- ENHANCEMENT: Added the following new methods to `HistoryBuilder`:
    `HistoryBuilder.get(index)`,
    `HistoryBuilder.contains(key)`,
    `HistoryBuilder.containsAll(keys)`,
    `HistoryBuilder.add(key, index)`,
    `HistoryBuilder.size()`,
    `HistoryBuilder.removeAt(index)`,
    `HistoryBuilder.remove(key)`,
    `HistoryBuilder.clear()`,
    `HistoryBuilder.retainAll(keys)`,
    `HistoryBuilder.indexOf(key)`,

-Simple Stack 0.8.0 (2017-02-02)
---------------------------------
- BREAKING CHANGE: Removed `StateChange.Direction`, it is now an `int` annotated with `@IntDef`.
This means that `StateChange.Direction.FORWARD` is now `StateChange.FORWARD`, same for `BACKWARD` and `REPLACE`.
- Fix: `@StateChangerRegisterMode` shouldn't have been public

-Simple Stack 0.7.0 (2017-01-31)
---------------------------------
- BREAKING CHANGE: Removed `Backstack.get(Context)`, `BackstackDelegate.isSystemService(String)` and `BackstackDelegate.getSystemService(Context)`.

These can be easily done manually with the following setup:

``` java
    public static Backstack get(Context context) {
        // noinspection ResourceType
        return (Backstack)context.getSystemService(BACKSTACK);
    }
```

and

``` java
    @Override
    public Object getSystemService(String name) {
        if(name.equals(BACKSTACK)) {
            return backstackDelegate.getBackstack();
        }
        return super.getSystemService(name);
    }
```

Therefore the preferred solution is to provide the `Backstack` instance via `@Inject` instead of `Backstack.get(Context)`.

Example for `Backstack.get(Context)` was moved to `simple-stack-example` as `BackstackService`.
Example for `@Inject Backstack backstack;` is seen in `simple-stack-example-mvp`.

-Simple Stack 0.6.1 (2017-01-27)
---------------------------------
- It is now allowed to initialize `BackstackDelegate` without a `StateChanger`, in which case `setStateChanger()` must be called before `onPostResume()`.`
This way it is possible to postpone the initialization state change of the `Backstack`.

-Simple Stack 0.6.0 (2017-01-23)
---------------------------------
- **Simple Stack is now a library!**
- Added `BackstackDelegate.getBackstack()` for convenience over `Backstack.get(this)` in Activity

-Simple Stack 0.5.1 (2017-01-23)
---------------------------------
- Added `Bundleable` interface to allow saving view's state to Bundle
- Added `BackstackDelegate.restoreViewFromState()` method to mirror `persistViewToState()`
- `getSavedState()` now returns a new `SavedState` instead of throwing error if the key has no state bound to it
- Added `SavedState.viewHierarchyState` default value `new SparseArray<>()`, null is prohibited

-Simple Stack 0.5.0 (2017-01-22)
---------------------------------
- Added `BackstackDelegate` class to hide activity lifecycle integration
- Moved `SavedState` into library
- Added `Backstack.get(Context)` method to obtain Backstack of instance shared by the delegate
- Moved `KeyContextWrapper` into library, and it is now package-private
- Added `StateChange.createContext(base, key)` method to create `KeyContextWrapper`
- `KeyContextWrapper.getKey(Context)` is now `Backstack.getKey(Context)`

-Simple Stack 0.4.0 (2017-01-21)
---------------------------------
- Rename packages from `demostack` to `simplestack`

-Simple Stack 0.3.3 (2017-01-21)
---------------------------------
- Rename `State` to `SavedState`

-Simple Stack 0.3.2 (2017-01-21)
---------------------------------
- Add check for if `key` is `null` in `State`'s `Builder.build()`

-Simple Stack 0.3.1 (2017-01-20)
---------------------------------
- Added missing `equals()`/`hashCode()` to `State` class in example

-Simple Stack 0.3.0 (2017-01-20)
---------------------------------
- Added view persistence to example code (`MainActivity`)

-Simple Stack 0.2.6 (2017-01-20)
---------------------------------
- Added `HistoryBuilder` for convenience

-Simple Stack 0.2.5 (2017-01-17)
---------------------------------
- Minor bug fixes and simplifications

-Simple Stack 0.2.1 (2017-01-17)
---------------------------------
- Added `ReentranceTest` and ported to `simple-stack-demo` codebase
- Fixed some bugs based on `ReentranceTest` - all tests pass now

-Simple Stack 0.2.0 (2017-01-16)
---------------------------------
- State changes are now enqueued while `StateChanger` is not available (after `onPause()`) or a state change is already in progress
- Added `FlowTest` and ported to `simple-stack-demo` codebase

Simple Stack 0.1.1 (2017-01-14)
---------------------------------
- Key and backstack are now provided to custom viewgroup via `getSystemService()`

Simple Stack 0.1.0 (2017-01-13)
---------------------------------
- Added initial `Backstack`, `StateChange` and `StateChanger` classes.
- Backstack allows manipulation of state via `goTo()`, `goBack()` and `setHistory()`.
- Demo persists backstack history through config change and process death.

Limitations:
- ViewState is not persisted
- scheduling state changes (starting a state change while another is in progress) is not allowed
- there is a possibility that state change can occur even after `onPause()`
- key and backstack are manually set to the custom viewgroup, which means these are not directly accessible in their child views (and the interfaces are ugly anyways)
