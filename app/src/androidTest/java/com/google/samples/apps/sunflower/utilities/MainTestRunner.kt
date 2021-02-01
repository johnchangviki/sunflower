/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.utilities

import android.app.Application
import android.app.KeyguardManager
import android.content.Context
import android.os.Bundle
import android.os.PowerManager
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.karumi.shot.ShotTestRunner
import dagger.hilt.android.testing.HiltTestApplication


// A custom runner to set up the instrumented application class for tests.
class MainTestRunner : ShotTestRunner() {

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }

    override fun onCreate(args: Bundle) {
        super.onCreate(args)
        ActivityLifecycleMonitorRegistry.getInstance().addLifecycleCallback { activity, stage ->
            if (stage == Stage.CREATED) {
                val name: String = MainTestRunner::class.java.simpleName
                unlockScreen(activity.application, name);
                keepScreenAwake(activity.application, name);
            }
        }
    }

    private fun unlockScreen(app: Context, name: String) {
        val keyguard = app.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguard.newKeyguardLock(name).disableKeyguard()
    }

    private fun keepScreenAwake(app: Context, name: String) {
        val power = app.getSystemService(Context.POWER_SERVICE) as PowerManager
        power.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            name
        ).acquire()
    }
}
