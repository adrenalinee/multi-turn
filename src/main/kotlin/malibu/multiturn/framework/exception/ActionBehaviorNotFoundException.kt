package malibu.multiturn.framework.exception

import malibu.multiturn.model.Action

class ActionBehaviorNotFoundException(
    action: Action
): MultiTurnException(
    message = "actionBehavior 를 찾을 수 없습니다. action type: ${action.type}"
)