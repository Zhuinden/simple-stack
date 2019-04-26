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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestack.KeyChange;
import com.zhuinden.simplestack.KeyChanger;
import com.zhuinden.simplestack.navigator.changehandlers.FadeViewChangeHandler;

/**
 * A default key changer that handles view changes, and allows an optional external key changer (which is executed before the view change).
 *
 * For the default behavior to work, all keys must implement {@link DefaultViewKey}, which specifies a layout, and a {@link ViewChangeHandler}.
 *
 * But if {@link LayoutInflationStrategy} and {@link GetViewChangeHandlerStrategy} are re-defined, then this is no longer necessary.
 */
public final class DefaultViewKeyChanger
        implements KeyChanger {
    private static class NoOpKeyChanger
            implements KeyChanger {
        @Override
        public void handleKeyChange(@NonNull KeyChange keyChange, @NonNull Callback completionCallback) {
            completionCallback.keyChangeComplete();
        }
    }

    private static class NoOpViewChangeStartListener
            implements ViewChangeStartListener {
        @Override
        public void handleViewChangeStart(@NonNull KeyChange keyChange, @NonNull ViewGroup container, @Nullable View previousView, @NonNull View newView, @NonNull Callback startCallback) {
            startCallback.startViewChange();
        }
    }

    private static class NoOpViewChangeCompletionListener
            implements ViewChangeCompletionListener {
        @Override
        public void handleViewChangeComplete(@NonNull KeyChange keyChange, @NonNull ViewGroup container, @Nullable View previousView, @NonNull View newView, @NonNull Callback completionCallback) {
            completionCallback.viewChangeComplete();
        }
    }

    private static class DefaultLayoutInflationStrategy
            implements LayoutInflationStrategy {
        @Override
        public void inflateLayout(@NonNull KeyChange keyChange, @NonNull Object key, @NonNull Context context, @NonNull ViewGroup container, @NonNull Callback callback) {
            final View newView = LayoutInflater.from(context).inflate(((DefaultViewKey) key).layout(), container, false);
            callback.layoutInflationComplete(newView);
        }
    }

    private static class NavigatorStatePersistenceStrategy
            implements StatePersistenceStrategy {
        @Override
        public void persistViewToState(@NonNull Object previousKey, @NonNull View previousView) {
            Navigator.persistViewToState(previousView);
        }

        @Override
        public void restoreViewFromState(@NonNull Object newKey, @NonNull View newView) {
            Navigator.restoreViewFromState(newView);
        }
    }

    private static class DefaultGetPreviousViewStrategy
            implements GetPreviousViewStrategy {
        @Nullable
        @Override
        public View getPreviousView(@NonNull ViewGroup container, @NonNull KeyChange keyChange, @Nullable Object previousKey) {
            return container.getChildAt(0);
        }
    }

    private static class DefaultContextCreationStrategy
            implements ContextCreationStrategy {
        @NonNull
        @Override
        public Context createContext(@NonNull Context baseContext, @NonNull Object newKey, @NonNull ViewGroup container, @NonNull KeyChange keyChange) {
            return keyChange.createContext(baseContext, newKey);
        }
    }

    private static class DefaultGetViewChangeHandlerStrategy
            implements GetViewChangeHandlerStrategy {
        @Override
        @NonNull
        public ViewChangeHandler getViewChangeHandler(@NonNull KeyChange keyChange, @NonNull ViewGroup container, @NonNull Object previousKey, @NonNull Object newKey, @NonNull View previousView, @NonNull View newView, int direction) {
            ViewChangeHandler viewChangeHandler;
            if(direction == KeyChange.FORWARD) {
                viewChangeHandler = ((DefaultViewKey) newKey).viewChangeHandler();
            } else if(direction == KeyChange.BACKWARD) {
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
         * Notifies the {@link DefaultViewKeyChanger} that the view change start listener completed its callback.
         */
        public interface Callback {
            void startViewChange();
        }

        /**
         * Called when a view change is to be started.
         *
         * @param keyChange   the key change
         * @param container     the container
         * @param previousView  the previous view
         * @param newView       the new view
         * @param startCallback the start callback that must be called once the view change callback is completed by the listener
         */
        void handleViewChangeStart(@NonNull KeyChange keyChange, @NonNull ViewGroup container, @Nullable View previousView, @NonNull View newView, @NonNull ViewChangeStartListener.Callback startCallback);
    }

    /**
     * Allows the possibility of listening to when the view change is completed.
     */
    public interface ViewChangeCompletionListener {
        /**
         * Notifies the {@link DefaultViewKeyChanger} that the view change completion listener completed its callback.
         */
        public interface Callback {
            void viewChangeComplete();
        }

        /**
         * Called when a view change is completed.
         *
         * @param keyChange        the key change
         * @param container          the container
         * @param previousView       the previous view
         * @param newView            the new view
         * @param completionCallback the completion callback that must be called once the view change callback is completed by the listener
         */
        void handleViewChangeComplete(@NonNull KeyChange keyChange, @NonNull ViewGroup container, @Nullable View previousView, @NonNull View newView, @NonNull Callback completionCallback);
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
         * @param keyChange the key change
         * @return the new context
         */
        @NonNull
        Context createContext(@NonNull Context baseContext, @NonNull Object newKey, @NonNull ViewGroup container, @NonNull KeyChange keyChange);
    }

    /**
     * Allows specifying a custom way to obtain the view change handler for the view change.
     */
    public interface GetViewChangeHandlerStrategy {
        /**
         * Gets the view change handler used for the view change, between the given keys and with specified direction.
         *
         * @param keyChange  the key change
         * @param container    the container
         * @param previousKey  the previous key
         * @param newKey       the new key
         * @param previousView the previous view
         * @param newView      the new view
         * @param direction    the direction
         * @return the view change handler
         */
        @NonNull
        ViewChangeHandler getViewChangeHandler(@NonNull KeyChange keyChange, @NonNull ViewGroup container, @NonNull Object previousKey, @NonNull Object newKey, @NonNull View previousView, @NonNull View newView, int direction);
    }

    /**
     * Allows defining a custom way of determining what the previous view is.
     */
    public interface GetPreviousViewStrategy {
        /**
         * Gets the previous view from the container.
         *
         * @param container   the container
         * @param keyChange the key change
         * @param previousKey the previous key
         * @return the previous view
         */
        @Nullable
        View getPreviousView(@NonNull ViewGroup container, @NonNull KeyChange keyChange, @Nullable Object previousKey);
    }

    /**
     * Allows the possibility of using a custom layout inflation strategy for inflating the new view.
     */
    public interface LayoutInflationStrategy {
        /**
         * This callback must be called to provide the inflated view.
         */
        public interface Callback {
            void layoutInflationComplete(@NonNull View view);
        }

        /**
         * This method needs to inflate the new view, preferably using the provided context.
         *
         * @param keyChange the key change
         * @param key         the new key this view is inflated for
         * @param context     the context the layout inflater is originally acquired from
         * @param container   the container
         * @param callback    the inflation callback that must be called when layout inflation is complete
         */
        void inflateLayout(@NonNull KeyChange keyChange, @NonNull Object key, @NonNull Context context, @NonNull ViewGroup container, @NonNull Callback callback);
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
        void persistViewToState(@NonNull Object previousKey, @NonNull View previousView);

        /**
         * Restores the new active view's view state.
         *
         * @param newKey  the new key
         * @param newView the new view
         */
        void restoreViewFromState(@NonNull Object newKey, @NonNull View newView);
    }

    private static final FadeViewChangeHandler FADE_VIEW_CHANGE_HANDLER = new FadeViewChangeHandler();

    private Context baseContext;
    private ViewGroup container;
    private KeyChanger externalKeyChanger;
    private ViewChangeStartListener viewChangeStartListener;
    private ViewChangeCompletionListener viewChangeCompletionListener;
    private LayoutInflationStrategy layoutInflationStrategy;
    private StatePersistenceStrategy statePersistenceStrategy;
    private GetViewChangeHandlerStrategy getViewChangeHandlerStrategy;
    private GetPreviousViewStrategy getPreviousViewStrategy;
    private ContextCreationStrategy contextCreationStrategy;

    /**
     * Used to configure the instance of the {@link DefaultViewKeyChanger}.
     *
     * Allows setting an external key changer, which is executed before the view change.
     * Also allows setting a {@link ViewChangeCompletionListener} which is executed after the view change.
     */
    public static class Configurer {
        KeyChanger externalKeyChanger = null;
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
         * Sets the external key changer. It is executed before the view change.
         *
         * @param keyChanger the key changer
         * @return the configurer
         */
        @NonNull
        public Configurer setExternalKeyChanger(@NonNull KeyChanger keyChanger) {
            if(keyChanger == null) {
                throw new NullPointerException("If set, external key changer cannot be null!");
            }
            this.externalKeyChanger = keyChanger;
            return this;
        }

        /**
         * Sets the {@link ViewChangeStartListener}. It is executed before the view change.
         *
         * @param viewChangeStartListener the view change start listener
         * @return the configurer
         */
        @NonNull
        public Configurer setViewChangeStartListener(@NonNull ViewChangeStartListener viewChangeStartListener) {
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
        @NonNull
        public Configurer setViewChangeCompletionListener(@NonNull ViewChangeCompletionListener viewChangeCompletionListener) {
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
        @NonNull
        public Configurer setStatePersistenceStrategy(@NonNull StatePersistenceStrategy statePersistenceStrategy) {
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
        @NonNull
        public Configurer setLayoutInflationStrategy(@NonNull LayoutInflationStrategy layoutInflationStrategy) {
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
        @NonNull
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
        @NonNull
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
        @NonNull
        public Configurer setGetViewChangeHandlerStrategy(GetViewChangeHandlerStrategy getViewChangeHandlerStrategy) {
            if(getViewChangeHandlerStrategy == null) {
                throw new NullPointerException("If set, get view change handler strategy cannot be null!");
            }
            this.getViewChangeHandlerStrategy = getViewChangeHandlerStrategy;
            return this;
        }

        /**
         * Creates the {@link DefaultViewKeyChanger} with the specified parameters.
         *
         * @param baseContext the base context used to inflate the views
         * @param container   the container into which views are added and removed from
         * @return the new {@link DefaultViewKeyChanger}
         */
        @NonNull
        public DefaultViewKeyChanger create(@NonNull Context baseContext, @NonNull ViewGroup container) {
            return new DefaultViewKeyChanger(baseContext,
                    container,
                    externalKeyChanger,
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
     * Factory method to create a configured {@link DefaultViewKeyChanger}.
     * You can set an external key changer which is executed before the view change, and a {@link ViewChangeCompletionListener} that is executed after view change.
     *
     * @return the {@link Configurer}
     */
    public static Configurer configure() {
        return new Configurer();
    }

    /**
     * Factory method to create the {@link DefaultViewKeyChanger} with default configuration.
     *
     * To add additional configuration such as external key changer or {@link ViewChangeCompletionListener}, use the {@link DefaultViewKeyChanger#configure()} method.
     *
     * @param baseContext the base context used to inflate views
     * @param container   the container into which views are added to or removed from
     * @return the key changer
     */
    @NonNull
    public static DefaultViewKeyChanger create(Context baseContext, ViewGroup container) {
        return new DefaultViewKeyChanger(baseContext, container, null, null, null, null, null, null, null, null);
    }

    DefaultViewKeyChanger(@NonNull Context baseContext, @NonNull ViewGroup container, @Nullable KeyChanger externalKeyChanger, ViewChangeStartListener viewChangeStartListener, @Nullable ViewChangeCompletionListener viewChangeCompletionListener, @Nullable LayoutInflationStrategy layoutInflationStrategy, @Nullable StatePersistenceStrategy statePersistenceStrategy, @Nullable GetPreviousViewStrategy getPreviousViewStrategy, @Nullable ContextCreationStrategy contextCreationStrategy, GetViewChangeHandlerStrategy getViewChangeHandlerStrategy) {
        if(baseContext == null) {
            throw new NullPointerException("baseContext cannot be null");
        }
        if(container == null) {
            throw new NullPointerException("container cannot be null");
        }
        this.baseContext = baseContext;
        this.container = container;
        if(externalKeyChanger == null) {
            externalKeyChanger = new NoOpKeyChanger();
        }
        this.externalKeyChanger = externalKeyChanger;
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

    private void finishKeyChange(KeyChange keyChange, ViewGroup container, View previousView, View newView, final Callback completionCallback) {
        viewChangeCompletionListener.handleViewChangeComplete(keyChange,
                container,
                previousView,
                newView,
                new ViewChangeCompletionListener.Callback() {
                    @Override
                    public void viewChangeComplete() {
                        completionCallback.keyChangeComplete();
                    }
                });
    }

    @Override
    public final void handleKeyChange(@NonNull final KeyChange keyChange, @NonNull final Callback completionCallback) {
        externalKeyChanger.handleKeyChange(keyChange, new Callback() {
            @Override
            public void keyChangeComplete() {
                if(keyChange.isTopNewKeyEqualToPrevious()) {
                    completionCallback.keyChangeComplete();
                    return;
                }
                performViewChange(keyChange.topPreviousKey(),
                        keyChange.topNewKey(),
                        keyChange,
                        completionCallback);
            }
        });
    }

    /**
     * Handles the view change using the provided parameters. The direction is specified by the direction in the key change.
     *
     * @param previousKey        the previous key
     * @param newKey             the new key
     * @param keyChange        the key change
     * @param completionCallback the completion callback
     */
    public final void performViewChange(@Nullable Object previousKey, @NonNull Object newKey, @NonNull final KeyChange keyChange, @NonNull final Callback completionCallback) {
        performViewChange(previousKey, newKey, keyChange, keyChange.getDirection(), completionCallback);
    }

    /**
     * Handles the view change using the provided parameters. The direction is also manually provided.
     *
     * @param previousKey        the previous key
     * @param newKey             the new key
     * @param keyChange        the key change
     * @param direction          the direction
     * @param completionCallback the completion callback
     */
    public void performViewChange(@Nullable final Object previousKey, @NonNull final Object newKey, @NonNull final KeyChange keyChange, final int direction, @NonNull final Callback completionCallback) {
        final View previousView = getPreviousViewStrategy.getPreviousView(container, keyChange, previousKey);
        if(previousView != null && previousKey != null) {
            statePersistenceStrategy.persistViewToState(previousKey, previousView);
        }
        Context newContext = contextCreationStrategy.createContext(keyChange.createContext(baseContext, newKey),
                newKey,
                container,
                keyChange);
        layoutInflationStrategy.inflateLayout(keyChange,
                newKey,
                newContext,
                container,
                new LayoutInflationStrategy.Callback() {
                    @Override
                    public void layoutInflationComplete(@NonNull final View newView) {
                        statePersistenceStrategy.restoreViewFromState(newKey, newView);
                        viewChangeStartListener.handleViewChangeStart(keyChange,
                                container,
                                previousView,
                                newView,
                                new ViewChangeStartListener.Callback() {
                                    @Override
                                    public void startViewChange() {
                                        if(previousView == null) {
                                            container.addView(newView);
                                            finishKeyChange(keyChange,
                                                    container,
                                                    previousView,
                                                    newView,
                                                    completionCallback);
                                        } else {
                                            final ViewChangeHandler viewChangeHandler = getViewChangeHandlerStrategy.getViewChangeHandler(
                                                    keyChange,
                                                    container,
                                                    previousKey,
                                                    newKey,
                                                    previousView, newView, direction);
                                            viewChangeHandler.performViewChange(container,
                                                    previousView,
                                                    newView,
                                                    direction,
                                                    new ViewChangeHandler.CompletionCallback() {
                                                        @Override
                                                        public void onCompleted() {
                                                            finishKeyChange(keyChange,
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
