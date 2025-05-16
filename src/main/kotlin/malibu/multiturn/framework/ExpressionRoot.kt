package malibu.multiturn.framework

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

//    /**
//     * permanentContext 에 들어 있던 params
//     */
//    val permanentParams: Map<String, Any>,
//
//    /**
//     * instantContext 에 들어 있던 params
//     */
//    val instantParams: Map<String, Any>,

//    /**
//     * behavior 끼리만 공유할 수 있는 데이터.
//     * 하나의 요청안에서만 유지된다.
//     */
//    val attributes: Map<String, Any?>
)