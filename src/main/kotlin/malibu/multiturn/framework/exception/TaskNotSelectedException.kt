package malibu.multiturn.framework.exception

import malibu.multiturn.model.Intend

class TaskNotSelectedException(
    intend: Intend
): MultiTurnException(
    message = "실행할 task 가 선택되지 않았습니다. intent: $intend"
)