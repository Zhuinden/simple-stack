# Change log

-Simple Stack 0.8.2 (2017-02-03)
---------------------------------
- Made `KeyContextWrapper` public again.

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