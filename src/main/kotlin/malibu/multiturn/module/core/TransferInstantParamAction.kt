package malibu.multiturn.module.core

import malibu.multiturn.model.Action

/**
 * 이번 요청에 들어온 instantParam 들을 다음 요청에서도 다시 들어오게 해준다.
 * instantParam 은 원래 이번 요청에서만 유지되고 없어지도록 설게된 param 이지만,
 * TransferInstantParamAction 액션을 통해서 다음요청까지 한번더 param 이 유지되게 해준다.
 */
data class TransferInstantParamAction(
    /**
     * 넘지기 않을 파라미터의 목록
     */
    val excludeParams: List<String>? = null
): Action(
    type = TYPE
) {
    companion object {
        val TYPE: String = TransferInstantParamAction::class.java.simpleName
    }
}
