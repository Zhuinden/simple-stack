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

import android.support.annotation.NonNull;
import android.view.View;

import com.zhuinden.simplestackdemoexamplemvp.R;

final class Binding implements View.OnAttachStateChangeListener {
  private final Coordinator coordinator;
  private final View view;
  private View attached;

  Binding(Coordinator coordinator, View view) {
    this.coordinator = coordinator;
    this.view = view;
  }

  @Override public void onViewAttachedToWindow(@NonNull View v) {
    if (v != attached) {
      attached = v;
      if (coordinator.isAttached()) {
        throw new IllegalStateException(
            "Coordinator " + coordinator + " is already attached to a View");
      }
      coordinator.setAttached(true);
      coordinator.attach(view);
      view.setTag(R.id.coordinator, coordinator);
    }
  }

  @Override public void onViewDetachedFromWindow(@NonNull View v) {
    if (v == attached) {
      attached = null;
      coordinator.detach(view);
      coordinator.setAttached(false);
      view.setTag(R.id.coordinator, null);
    }
  }
}
