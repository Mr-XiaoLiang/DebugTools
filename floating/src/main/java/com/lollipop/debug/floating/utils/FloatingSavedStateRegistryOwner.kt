package com.lollipop.debug.floating.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class FloatingSavedStateRegistryOwner : SavedStateRegistryOwner {

    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    private val registry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() {
            return registry
        }
    override val savedStateRegistry: SavedStateRegistry
        get() {
            return savedStateRegistryController.savedStateRegistry
        }



    fun onCreate() {
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    fun onStart() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    fun onStop() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
    }

    fun onDestroy() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    fun onResume() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun onPause() {
        registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

}