# Change log


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