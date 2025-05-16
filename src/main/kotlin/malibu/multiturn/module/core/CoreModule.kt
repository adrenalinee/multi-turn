package malibu.multiturn.module.core

import malibu.multiturn.model.MultiTurnModule

class CoreModule(

): MultiTurnModule(
    type = TYPE
) {
    companion object {
        val TYPE: String = CoreModule::class.java.simpleName
    }
}