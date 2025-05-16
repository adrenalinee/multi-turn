package malibu.multiturn.framework

class IntendData(
    val multiTurnReq: MultiTurnReq,
    val behaviorRegistry: BehaviorRegistry,
    private val arguments: MutableMap<String, Any?>,
) {
//    private val arguments: MutableMap<String, Any?> = mutableMapOf()

    var finishArgumentLoad: Boolean = false
        internal set

    internal fun putArgument(argumentName: String, argumentValue: Any?) {
        if (arguments.containsKey(argumentName)) {
            throw RuntimeException("argument key: $argumentName is already exists.")
        }
        arguments.put(argumentName, argumentValue)
    }

    fun getArguments(): Map<String, Any?> {
        return arguments.toMap()
    }
}