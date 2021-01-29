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
package com.zhuinden.simplestackexamplemvvm.core.scheduler


import java.util.concurrent.*

class BackgroundScheduler : Scheduler {
    private val poolWorkQueue: BlockingQueue<Runnable> = LinkedBlockingQueue()

    val executor: Executor = ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAXIMUM_POOL_SIZE,
        KEEP_ALIVE.toLong(),
        TimeUnit.SECONDS,
        poolWorkQueue) // from ModernAsyncTask

    override fun execute(runnable: Runnable) {
        executor.execute(runnable)
    }

    companion object {
        private const val CORE_POOL_SIZE = 8
        private const val MAXIMUM_POOL_SIZE = 128
        private const val KEEP_ALIVE = 1
    }
}