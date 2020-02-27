/*
 * Copyright 2017 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.simplestack.navigator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.changehandlers.FadeViewChangeHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A default state changer that handles view changes, and allows an optional external state changer (which is executed before the view change).
 *
 * For the default behavior to work, all keys must implement {@link DefaultViewKey}, which specifies a layout, and a {@link ViewChangeHandler}.
 *
 * But if {@link LayoutInflationStrategy} and {@link GetViewChangeHandlerStrategy} are re-defined, then this is no longer necessary.
 */
public final class DefaultStateChanger
        implements StateChanger {
    private static class NoOpStateChanger
            implements StateChanger {
        @Override
        public void handleStateChange(@Nonnull StateChange stateChange, @Nonnull Callback completionCallback) {
            completionCallback.stateChangeComplete();
        }
    }

    private static class NoOpViewChangeStartListener
            implements ViewChangeStartListener {
        @Override
        public void handleViewChangeStart(@Nonnull StateChange stateChange, @Nonnull ViewGroup container, @Nullable View previousView, @Nonnull View newView, @Nonnull Callback startCallback) {
            startCallback.startViewChange();
        }
    }

    private static class NoOpViewChangeCompletionListener
            implements ViewChangeCompletionListener {
        @Override
        public void handleViewChangeComplete(@Nonnull StateChange stateChange, @Nonnull ViewGroup container, @Nullable View previousView, @Nonnull View newView, @Nonnull Callback completionCallback) {
            completionCallback.viewChangeComplete();
        }
    }

    private static class DefaultLayoutInflationStrategy
            implements LayoutInflationStrategy {
        @Override
        public void inflateLayout(@Nonnull StateChange stateChange, @Nonnull Object key, @Nonnull Context context, @Nonnull ViewGroup container, @Nonnull Callback callback) {
            final View newView = LayoutInflater.from(context).inflate(((DefaultViewKey) key).layout(), container, false);
            callback.layoutInflationComplete(newView);
        }
    }

    private static class NavigatorStatePersistenceStrategy
            implements StatePersistenceStrategy {
        @Override
        public void persistViewToState(@Nonnull Object previousKey, @Nonnull View previousView) {
            Navigator.persistViewToState(previousView);
        }

        @Override
        public void restoreViewFromState(@Nonnull Object newKey, @Nonnull View newView) {
            Navigator.restoreViewFromState(newView);
        }
    }

    private static class DefaultGetPreviousViewStrategy
            implements GetPreviousViewStrategy {
        @Nullable
        @Override
        public View getPreviousView(@Nonnull ViewGroup container, @Nonnull StateChange stateChange, @Nullable Object previousKey) {
            return container.getChildAt(0);
        }
    }

    private static class DefaultContextCreationStrategy
            implements ContextCreationStrategy {
        @Nonnull
        @Override
        public Context createContext(@Nonnull Context baseContext, @Nonnull Object newKey, @Nonnull ViewGroup container, @Nonnull StateChange stateChange) {
            return stateChange.createContext(baseContext, newKey);
        }
    }

    private static class DefaultGetViewChangeHandlerStrategy
            implements GetViewChangeHandlerStrategy {
        @Override
        @Nonnull
        public ViewChangeHandler getViewChangeHandler(@Nonnull StateChange stateChange, @Nonnull ViewGroup container, @Nonnull Object previousKey, @Nonnull Object newKey, @Nonnull View previousView, @Nonnull View newView, int direction) {
            ViewChangeHandler viewChangeHandler;
            if(direction == StateChange.FORWARD) {
                viewChangeHandler = ((DefaultViewKey) newKey).viewChangeHandler();
            } else if(direction == StateChange.BACKWARD) {
                viewChangeHandler = ((DefaultViewKey) previousKey).viewChangeHandler();
            } else {
                viewChangeHandler = FADE_VIEW_CHANGE_HANDLER;
            }
            return viewChangeHandler;
        }
    }

    /**
     * Allows the possibility of listening to when the view change is to start, after the views are inflated and state is restored, but before view change is started.
     */
    public interface ViewChangeStartListener {
        /**
         * Notifies the {@link DefaultStateChanger} that the view change start listener completed its callback.
         */
        public interface Callback {
            void startViewChange();
        }

        /**
         * Called when a view change is to be started.
         *
         * @param stateChange   the state change
         * @param container     the container
         * @param previousView  the previous view
         * @param newView       the new view
         * @param startCallback the start callback that must be called once the view change callback is completed by the listener
         */
        void handleViewChangeStart(@Nonnull StateChange stateChange, @Nonnull ViewGroup container, @Nullable View previousView, @Nonnull View newView, @Nonnull ViewChangeStartListener.Callback startCallback);
    }

    /**
     * Allows the possibility of listening to when the view change is completed.
     */
    public interface ViewChangeCompletionListener {
        /**
         * Notifies the {@link DefaultStateChanger} that the view change completion listener completed its callback.
         */
        public interface Callback {
            void viewChangeComplete();
        }

        /**
         * Called when a view change is completed.
         *
         * @param stateChange        the state change
         * @param container          the container
         * @param previousView       the previous view
         * @param newView            the new view
         * @param completionCallback the completion callback that must be called once the view change callback is completed by the listener
         */
        void handleViewChangeComplete(@Nonnull StateChange stateChange, @Nonnull ViewGroup container, @Nullable View previousView, @Nonnull View newView, @Nonnull Callback completionCallback);
    }

    /**
     * Allows the possibility of creating a custom context.
     */
    public interface ContextCreationStrategy {
        /**
         * Creates the context used by layout inflation.
         *
         * @param baseContext the base context
         * @param newKey      the new key
         * @param stateChange the state change
         * @return the new context
         */
        @Nonnull
        Context createContext(@Nonnull Context baseContext, @Nonnull Object newKey, @Nonnull ViewGroup container, @Nonnull StateChange stateChange);
    }

    /**
     * Allows specifying a custom way to obtain the view change handler for the view change.
     */
    public interface GetViewChangeHandlerStrategy {
        /**
         * Gets the view change handler used for the view change, between the given keys and with specified direction.
         *
         * @param stateChange  the state change
         * @param container    the container
         * @param previousKey  the previous key
         * @param newKey       the new key
         * @param previousView the previous view
         * @param newView      the new view
         * @param direction    the direction
         * @return the view change handler
         */
        @Nonnull
        ViewChangeHandler getViewChangeHandler(@Nonnull StateChange stateChange, @Nonnull ViewGroup container, @Nonnull Object previousKey, @Nonnull Object newKey, @Nonnull View previousView, @Nonnull View newView, int direction);
    }

    /**
     * Allows defining a custom way of determining what the previous view is.
     */
    public interface GetPreviousViewStrategy {
        /**
         * Gets the previous view from the container.
         *
         * @param container   the container
         * @param stateChange the state change
         * @param previousKey the previous key
         * @return the previous view
         */
        @Nullable
        View getPreviousView(@Nonnull ViewGroup container, @Nonnull StateChange stateChange, @Nullable Object previousKey);
    }

    /**
     * Allows the possibility of using a custom layout inflation strategy for inflating the new view.
     */
    public interface LayoutInflationStrategy {
        /**
         * This callback must be called to provide the inflated view.
         */
        public interface Callback {
            void layoutInflationComplete(@Nonnull View view);
        }

        /**
         * This method needs to inflate the new view, preferably using the provided context.
         *
         * @param stateChange the state change
         * @param key         the new key this view is inflated for
         * @param context     the context the layout inflater is originally acquired from
         * @param container   the container
         * @param callback    the inflation callback that must be called when layout inflation is complete
         */
        void inflateLayout(@Nonnull StateChange stateChange, @Nonnull Object key, @Nonnull Context context, @Nonnull ViewGroup container, @Nonnull Callback callback);
    }

    /**
     * Allows replacing the default Navigator-based view state persistence with a custom one.
     */
    public interface StatePersistenceStrategy {
        /**
         * Persists the previous active view's view state.
         *
         * @param previousKey  the previous key
         * @param previousView the previous view
         */
        void persistViewToState(@Nonnull Object previousKey, @Nonnull View previousView);

        /**
         * Restores the new active view's view state.
         *
         * @param newKey  the new key
         * @param newView the new view
         */
        void restoreViewFromState(@Nonnull Object newKey, @Nonnull View newView);
    }

    private static final FadeViewChangeHandler FADE_VIEW_CHANGE_HANDLER = new FadeViewChangeHandler();

    private Context baseContext;
    private ViewGroup container;
    private StateChanger externalStateChanger;
    private ViewChangeStartListener viewChangeStartListener;
    private ViewChangeCompletionListener viewChangeCompletionListener;
    private LayoutInflationStrategy layoutInflationStrategy;
    private StatePersistenceStrategy statePersistenceStrategy;
    private GetViewChangeHandlerStrategy getViewChangeHandlerStrategy;
    private GetPreviousViewStrategy getPreviousViewStrategy;
    private ContextCreationStrategy contextCreationStrategy;

    /**
     * Used to configure the instance of the {@link DefaultStateChanger}.
     *
     * Allows setting an external state changer, which is executed before the view change.
     * Also allows setting a {@link ViewChangeCompletionListener} which is executed after the view change.
     */
    public static class Configurer {
        StateChanger externalStateChanger = null;
        ViewChangeStartListener viewChangeStartListener = null;
        ViewChangeCompletionListener viewChangeCompletionListener = null;
        LayoutInflationStrategy layoutInflationStrategy = null;
        StatePersistenceStrategy statePersistenceStrategy = null;
        GetPreviousViewStrategy getPreviousViewStrategy = null;
        ContextCreationStrategy contextCreationStrategy = null;
        GetViewChangeHandlerStrategy getViewChangeHandlerStrategy = null;

        private Configurer() {
        }

        /**
         * Sets the external state changer. It is executed before the view change.
         *
         * @param stateChanger the state changer
         * @return the configurer
         */
        @Nonnull
        public Configurer setExternalStateChanger(@Nonnull StateChanger stateChanger) {
            if(stateChanger == null) {
                throw new NullPointerException("If set, external state changer cannot be null!");
            }
            this.externalStateChanger = stateChanger;
            return this;
        }

        /**
         * Sets the {@link ViewChangeStartListener}. It is executed before the view change.
         *
         * @param viewChangeStartListener the view change start listener
         * @return the configurer
         */
        @Nonnull
        public Configurer setViewChangeStartListener(@Nonnull ViewChangeStartListener viewChangeStartListener) {
            if(viewChangeStartListener == null) {
                throw new NullPointerException("If set, view change start listener cannot be null!");
            }
            this.viewChangeStartListener = viewChangeStartListener;
            return this;
        }

        /**
         * Sets the {@link ViewChangeCompletionListener}. It is executed after the view change.
         *
         * @param viewChangeCompletionListener the view change completion listener
         * @return the configurer
         */
        @Nonnull
        public Configurer setViewChangeCompletionListener(@Nonnull ViewChangeCompletionListener viewChangeCompletionListener) {
            if(viewChangeCompletionListener == null) {
                throw new NullPointerException("If set, view change completion listener cannot be null!");
            }
            this.viewChangeCompletionListener = viewChangeCompletionListener;
            return this;
        }

        /**
         * Sets the {@link StatePersistenceStrategy}. It is used to persist and restore the view's state.
         *
         * @param statePersistenceStrategy the state persistence strategy
         * @return the configurer
         */
        @Nonnull
        public Configurer setStatePersistenceStrategy(@Nonnull StatePersistenceStrategy statePersistenceStrategy) {
            if(statePersistenceStrategy == null) {
                throw new NullPointerException("If set, state persistence strategy cannot be null!");
            }
            this.statePersistenceStrategy = statePersistenceStrategy;
            return this;
        }

        /**
         * Sets the {@link LayoutInflationStrategy}. It is used to inflate the new view before a view change.
         *
         * @param layoutInflationStrategy the layout inflation strategy
         * @return the configurer
         */
        @Nonnull
        public Configurer setLayoutInflationStrategy(@Nonnull LayoutInflationStrategy layoutInflationStrategy) {
            if(layoutInflationStrategy == null) {
                throw new NullPointerException("If set, layout inflation strategy cannot be null!");
            }
            this.layoutInflationStrategy = layoutInflationStrategy;
            return this;
        }

        /**
         * Sets the {@link GetPreviousViewStrategy}. It is used to obtain the previous view from the container.
         *
         * @param getPreviousViewStrategy the previous view strategy
         * @return the configurer
         */
        @Nonnull
        public Configurer setGetPreviousViewStrategy(GetPreviousViewStrategy getPreviousViewStrategy) {
            if(getPreviousViewStrategy == null) {
                throw new NullPointerException("If set, get previous view strategy cannot be null!");
            }
            this.getPreviousViewStrategy = getPreviousViewStrategy;
            return this;
        }

        /**
         * Sets the {@link ContextCreationStrategy}. It is used to create the new context for the new view.
         *
         * @param contextCreationStrategy the create context strategy
         * @return the configurer
         */
        @Nonnull
        public Configurer setContextCreationStrategy(ContextCreationStrategy contextCreationStrategy) {
            if(contextCreationStrategy == null) {
                throw new NullPointerException("If set, create context strategy cannot be null!");
            }
            this.contextCreationStrategy = contextCreationStrategy;
            return this;
        }

        /**
         * Sets the {@link GetViewChangeHandlerStrategy}. It is used to obtain the view change handler for a view change.
         *
         * @param getViewChangeHandlerStrategy the get view change handler strategy
         * @return the configurer
         */
        @Nonnull
        public Configurer setGetViewChangeHandlerStrategy(GetViewChangeHandlerStrategy getViewChangeHandlerStrategy) {
            if(getViewChangeHandlerStrategy == null) {
                throw new NullPointerException("If set, get view change handler strategy cannot be null!");
            }
            this.getViewChangeHandlerStrategy = getViewChangeHandlerStrategy;
            return this;
        }

        /**
         * Creates the {@link DefaultStateChanger} with the specified parameters.
         *
         * @param baseContext the base context used to inflate the views
         * @param container   the container into which views are added and removed from
         * @return the new {@link DefaultStateChanger}
         */
        @Nonnull
        public DefaultStateChanger create(@Nonnull Context baseContext, @Nonnull ViewGroup container) {
            return new DefaultStateChanger(baseContext,
                    container,
                    externalStateChanger,
                    viewChangeStartListener,
                    viewChangeCompletionListener,
                    layoutInflationStrategy,
                    statePersistenceStrategy,
                    getPreviousViewStrategy,
                    contextCreationStrategy,
                    getViewChangeHandlerStrategy);
        }
    }

    /**
     * Factory method to create a configured {@link DefaultStateChanger}.
     * You can set an external state changer which is executed before the view change, and a {@link ViewChangeCompletionListener} that is executed after view change.
     *
     * @return the {@link Configurer}
     */
    public static Configurer configure() {
        return new Configurer();
    }

    /**
     * Factory method to create the {@link DefaultStateChanger} with default configuration.
     *
     * To add additional configuration such as external state changer or {@link ViewChangeCompletionListener}, use the {@link DefaultStateChanger#configure()} method.
     *
     * @param baseContext the base context used to inflate views
     * @param container   the container into which views are added to or removed from
     * @return the state changer
     */
    @Nonnull
    public static DefaultStateChanger create(Context baseContext, ViewGroup container) {
        return new DefaultStateChanger(baseContext, container, null, null, null, null, null, null, null, null);
    }

    DefaultStateChanger(@Nonnull Context baseContext, @Nonnull ViewGroup container, @Nullable StateChanger externalStateChanger, ViewChangeStartListener viewChangeStartListener, @Nullable ViewChangeCompletionListener viewChangeCompletionListener, @Nullable LayoutInflationStrategy layoutInflationStrategy, @Nullable StatePersistenceStrategy statePersistenceStrategy, @Nullable GetPreviousViewStrategy getPreviousViewStrategy, @Nullable ContextCreationStrategy contextCreationStrategy, GetViewChangeHandlerStrategy getViewChangeHandlerStrategy) {
        if(baseContext == null) {
            throw new NullPointerException("baseContext cannot be null");
        }
        if(container == null) {
            throw new NullPointerException("container cannot be null");
        }
        this.baseContext = baseContext;
        this.container = container;
        if(externalStateChanger == null) {
            externalStateChanger = new NoOpStateChanger();
        }
        this.externalStateChanger = externalStateChanger;
        if(viewChangeStartListener == null) {
            viewChangeStartListener = new NoOpViewChangeStartListener();
        }
        this.viewChangeStartListener = viewChangeStartListener;
        if(viewChangeCompletionListener == null) {
            viewChangeCompletionListener = new NoOpViewChangeCompletionListener();
        }
        this.viewChangeCompletionListener = viewChangeCompletionListener;
        if(layoutInflationStrategy == null) {
            layoutInflationStrategy = new DefaultLayoutInflationStrategy();
        }
        this.layoutInflationStrategy = layoutInflationStrategy;
        if(statePersistenceStrategy == null) {
            statePersistenceStrategy = new NavigatorStatePersistenceStrategy();
        }
        this.statePersistenceStrategy = statePersistenceStrategy;
        if(getPreviousViewStrategy == null) {
            getPreviousViewStrategy = new DefaultGetPreviousViewStrategy();
        }
        this.getPreviousViewStrategy = getPreviousViewStrategy;
        if(contextCreationStrategy == null) {
            contextCreationStrategy = new DefaultContextCreationStrategy();
        }
        this.contextCreationStrategy = contextCreationStrategy;
        if(getViewChangeHandlerStrategy == null) {
            getViewChangeHandlerStrategy = new DefaultGetViewChangeHandlerStrategy();
        }
        this.getViewChangeHandlerStrategy = getViewChangeHandlerStrategy;
    }

    private void finishStateChange(StateChange stateChange, ViewGroup container, View previousView, View newView, final Callback completionCallback) {
        viewChangeCompletionListener.handleViewChangeComplete(stateChange,
                container,
                previousView,
                newView,
                new ViewChangeCompletionListener.Callback() {
                    @Override
                    public void viewChangeComplete() {
                        completionCallback.stateChangeComplete();
                    }
                });
    }

    @Override
    public final void handleStateChange(@Nonnull final StateChange stateChange, @Nonnull final Callback completionCallback) {
        externalStateChanger.handleStateChange(stateChange, new Callback() {
            @Override
            public void stateChangeComplete() {
                if(stateChange.isTopNewKeyEqualToPrevious()) {
                    completionCallback.stateChangeComplete();
                    return;
                }
                performViewChange(stateChange.topPreviousKey(),
                        stateChange.topNewKey(),
                        stateChange,
                        completionCallback);
            }
        });
    }

    /**
     * Handles the view change using the provided parameters. The direction is specified by the direction in the state change.
     *
     * @param previousKey        the previous key
     * @param newKey             the new key
     * @param stateChange        the state change
     * @param completionCallback the completion callback
     */
    public final void performViewChange(@Nullable Object previousKey, @Nonnull Object newKey, @Nonnull final StateChange stateChange, @Nonnull final Callback completionCallback) {
        performViewChange(previousKey, newKey, stateChange, stateChange.getDirection(), completionCallback);
    }

    /**
     * Handles the view change using the provided parameters. The direction is also manually provided.
     *
     * @param previousKey        the previous key
     * @param newKey             the new key
     * @param stateChange        the state change
     * @param direction          the direction
     * @param completionCallback the completion callback
     */
    public void performViewChange(@Nullable final Object previousKey, @Nonnull final Object newKey, @Nonnull final StateChange stateChange, final int direction, @Nonnull final Callback completionCallback) {
        final View previousView = getPreviousViewStrategy.getPreviousView(container, stateChange, previousKey);
        if(previousView != null && previousKey != null) {
            statePersistenceStrategy.persistViewToState(previousKey, previousView);
        }
        Context newContext = contextCreationStrategy.createContext(stateChange.createContext(baseContext, newKey),
                newKey,
                container,
                stateChange);
        layoutInflationStrategy.inflateLayout(stateChange,
                newKey,
                newContext,
                container,
                new LayoutInflationStrategy.Callback() {
                    @Override
                    public void layoutInflationComplete(@Nonnull final View newView) {
                        statePersistenceStrategy.restoreViewFromState(newKey, newView);
                        viewChangeStartListener.handleViewChangeStart(stateChange,
                                container,
                                previousView,
                                newView,
                                new ViewChangeStartListener.Callback() {
                                    @Override
                                    public void startViewChange() {
                                        if(previousView == null) {
                                            container.addView(newView);
                                            finishStateChange(stateChange,
                                                    container,
                                                    previousView,
                                                    newView,
                                                    completionCallback);
                                        } else {
                                            final ViewChangeHandler viewChangeHandler = getViewChangeHandlerStrategy.getViewChangeHandler(
                                                    stateChange,
                                                    container,
                                                    previousKey,
                                                    newKey,
                                                    previousView, newView, direction);
                                            viewChangeHandler.performViewChange(container,
                                                    previousView,
                                                    newView,
                                                    direction,
                                                    new ViewChangeHandler.ViewChangeCallback() {
                                                        @Override
                                                        public void onCompleted() {
                                                            finishStateChange(stateChange,
                                                                    container,
                                                                    previousView,
                                                                    newView,
                                                                    completionCallback);
                                                        }
                                                    });
                                        }
                                    }
                                });
                    }
                });
    }
}
