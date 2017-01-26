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

import android.view.View;

/**
 * A Coordinator is attached to one view at a time.
 *
 * What you do from there is up to you.
 *
 * @see CoordinatorProvider
 */
public class Coordinator {

  private boolean attached;

  final void setAttached(boolean attached) {
    this.attached = attached;
  }

  /**
   * Called when the view is attached to a Window.
   *
   * Default implementation does nothing.
   *
   * @see View#onAttachedToWindow()
   */
  protected void attach(View view) {
  }

  /**
   * Called when the view is detached from a Window.
   *
   * Default implementation does nothing.
   *
   * @see View#onDetachedFromWindow()
   */
  protected void detach(View view) {
  }

  /**
   * True from just before attach until just after detach.
   */
  public final boolean isAttached() {
    return attached;
  }
}
