/*
 * Copyright (C) 2016 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.squareup.coordinators;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.zhuinden.simplestackdemoexamplemvp.R;

public final class Coordinators {
  private Coordinators() {
  }

  /**
   * Attempts to bind a view to a {@link Coordinator}.
   *
   * Immediately calls provider to obtain a Coordinator for the view. If a non-null Coordinator is
   * returned, that Coordinator is permanently bound to the View.
   */
  public static void bind(View view, CoordinatorProvider provider) {
    final Coordinator coordinator = provider.provideCoordinator(view);
    if (coordinator == null) {
      return;
    }

    View.OnAttachStateChangeListener binding = new Binding(coordinator, view);
    view.addOnAttachStateChangeListener(binding);
    // Sometimes we missed the first attach because the child's already been added.
    // Sometimes we didn't. The binding keeps track to avoid double attachment of the Coordinator,
    // and to guard against attachment to two different views simultaneously.
    binding.onViewAttachedToWindow(view);
  }

  /**
   * Installs a binder that calls {@link #bind(View, CoordinatorProvider)} for any child view added
   * to the group.
   */
  public static void installBinder(ViewGroup viewGroup, final CoordinatorProvider provider) {
    viewGroup.setOnHierarchyChangeListener(new Binder(provider));
  }

  @Nullable public static Coordinator getCoordinator(View view) {
    return (Coordinator) view.getTag(R.id.coordinator);
  }
}
