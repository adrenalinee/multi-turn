package malibu.multiturn.framework.exception

import malibu.multiturn.model.TopicState

class IntendNotSelectedException(
    topicState: TopicState,
): MultiTurnException(
    message = "실행할 intend 를 찾지 못했습니다. topicState: $topicState"
)