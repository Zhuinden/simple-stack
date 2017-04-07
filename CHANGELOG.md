# Change log

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