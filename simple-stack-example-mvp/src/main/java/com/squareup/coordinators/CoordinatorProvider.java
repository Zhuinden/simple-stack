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

public interface CoordinatorProvider {

  /**
   * Called to obtain a {@link Coordinator} for a View.
   *
   * Called from {@link Coordinators#bind}. Whether or not Coordinator instances are reused is up
   * to the implementer, but a Coordinator instance may only be bound to one View instance at a
   * time.
   *
   * @return null if the view has no associated coordinator
   */
  @Nullable Coordinator provideCoordinator(View view);
}
