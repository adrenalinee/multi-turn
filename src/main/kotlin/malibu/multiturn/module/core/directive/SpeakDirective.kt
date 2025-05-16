package malibu.multiturn.module.core.directive

import malibu.multiturn.framework.Directive

class SpeakDirective(
    val sentences: List<String>
): Directive(
    type = TYPE
) {
    companion object {
        val TYPE: String = SpeakDirective::class.java.simpleName
    }
}