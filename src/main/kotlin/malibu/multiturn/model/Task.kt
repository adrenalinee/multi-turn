package malibu.multiturn.model

data class Task(
    val name: String,
    val title: String? = null,
    val description: String? = null,
    val triggerExpression: String,

    /**
     * triggerExpression 을 실행하기 위해 argument 가 로딩되어 있어야 하는지 여부.
     * triggerExpression 이 argument value 에 접근해야 할때 true 로 셋팅한다.
     * argument 로드는 상황에 따라 큰 비용이 발생할 수 도 있기때문에 가능하면 최종적으로 필요해졌을때 로딩하기 위해 이런 옵션을 활용한다.
     *
     * argument 가 정의된 순서대로 로딩된다. 따라서 이전에 로딩된 argument value 에는 접근할 수 있지만 더 늦은 순서로 로딩되는
     * argument 에는 접근할 수 없다. 따라서 필요한경우 순서를 잘 조정해야 한다.
     *
     * 기본값은 false.
     */
    var triggerRequireArguments: Boolean? = null
) {
    private val arguments: MutableList<NameValue<Argument>> = mutableListOf()

    private val actions: MutableList<Action> = mutableListOf()

    fun addTaskArgument(argumentName: String, argument: Argument) {
        if (arguments.any { it.name == argumentName }) {
            throw RuntimeException("duplicated argument name: $argumentName")
        }
        arguments.add(NameValue(argumentName, argument))
    }

    fun getTaskArguments(): List<NameValue<Argument>> {
        return arguments.toList()
    }

    fun addAction(action: Action) {
        actions.add(action)
    }

    fun getActions(): List<Action> {
        return actions.toList()
    }
}
