package malibu.multiturn.framework.config

import malibu.multiturn.framework.BehaviorRegistry

internal interface BehaviorFunc {
    fun register(behaviorRegistry: BehaviorRegistry)
}