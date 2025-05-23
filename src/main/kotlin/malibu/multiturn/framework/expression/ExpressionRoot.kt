package malibu.multiturn.framework.expression

import malibu.multiturn.framework.MultiTurnReq

/**
 * 표현식의 root 로 사용할 객체.
 * IntentData 의 일부 필드에만 접근하게 하기 위해 이 객체로 감싸서 사용한다.
 */
class ExpressionRoot(

    /**
     * request 원본 데이터.
     */
    val req: MultiTurnReq,

    val args: Map<String, Any?>,
)