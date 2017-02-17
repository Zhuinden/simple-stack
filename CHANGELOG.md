# Change log

-Simple Stack 2.0.0-RC1 (2017-02-XX)
--------------------------------
- BREAKING CHANGE: `new BackstackDelegate(null)` is replaced with `BackstackDelegate.create()`.

Additionally, `new BackstackDelegate(stateChanger)` is replaced with `BackstackDelegate.configure().setStateChanger(stateChanger).build()`.

To support subclasses of `BackstackDelegate`, `configure().setDelegateProvider(MyDelegate::new)` can be used - but typically this is not needed.

- BREAKING CHANGE: `KeyContextWrapper` constructor is no longer public, and is renamed to `ManagedContextWrapper`.

- BREAKING CHANGE: `ManagedContextWrapper` is no longer public.

- BREAKING CHANGE: `StateChange.createContext()` is removed, and moved to `BackstackDelegate.createContext()`.

It is recommended that in order to create nested views, the `BackstackDelegate` is shared via `context.getSystemService()`, to make it accessible via any view through its context.

- BREAKING CHANGE (?): `SavedState.Builder` is no longer public.

- **ENHANCEMENT: SCOPED SERVICES INTEGRATION.**

You are now able to create SCOPED SERVICES that are created when a given Key is set, and they are torn down when the Key is no longer active.

The services are created and torn down by `ServiceFactory`, which can be added to the backstack delegate via `BackstackDelegate.configure().addServiceFactory()`.

Every registered service factory is executed when a particular key is set.

An example implementation would be the following:

    BackstackDelegate.configure().addServiceFactory(new ServiceFactory() {
        @Override
        public void bindServices(@NonNull Services.Builder builder) {
            Key key = builder.getKey();
            key.bindServices(builder);
        }

        @Override
        public void tearDownServices(@NonNull Services services) {
            Key key = services.getKey();
            key.tearDownServices(services);
        }
    }).build();

Hierarchies can be defined between keys using `Services.Child` and `Services.Composite`.

`Services.Child` allows you to create a link between two root keys (the ones explicitly found in the `Backstack`).

`Services.Composite` allows you to specify nested keys that belong to a given key.

When a given root key is set, their parents' services and all of their composite childrens' services are created.

Previous services are destroyed when no longer active TODO:, but if the Service implements `Bundleable`, then their state is preserved within `SavedState` along with the view's saved state.

TODO: `BackstackDelegate.clearStatesNotIn()` honors `Services.Child` and `Services.Component`, and you're able to store state that belongs to nested views directly.

- ADDED: `simple-stack-example-services`.

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