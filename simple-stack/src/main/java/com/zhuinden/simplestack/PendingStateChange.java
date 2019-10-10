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
package com.zhuinden.simplestack;

import java.util.List;

/**
 * Represents the state that will be available once state change is complete.
 */
class PendingStateChange {
    enum Status {
        ENQUEUED,
        IN_PROGRESS,
        COMPLETED
    }

    final List<?> newHistory;
    final int direction;
    final boolean initialization;
    final boolean isTerminal;
    final boolean isForceEnqueued;

    private Status status = Status.ENQUEUED;

    StateChanger.Callback completionCallback;
    boolean didForceExecute = false;

    PendingStateChange(List<?> newHistory, @StateChange.StateChangeDirection int direction, boolean initialization, boolean isTerminal, boolean isForceEnqueued) {
        this.newHistory = newHistory;
        this.direction = direction;
        this.initialization = initialization;
        this.isTerminal = isTerminal;
        this.isForceEnqueued = isForceEnqueued;
    }

    Status getStatus() {
        return status;
    }

    void setStatus(Status status) {
        if(status == null) {
            throw new NullPointerException("Status of pending state change cannot be null!");
        }
        if(status.ordinal() < this.status.ordinal()) {
            throw new IllegalStateException("A pending state change cannot go to one of its previous states: [" + this.status + "] to [" + status + "]");
        }
        this.status = status;
    }
}
