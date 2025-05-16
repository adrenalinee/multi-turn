package malibu.multiturn.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type"
)
@JsonPropertyOrder(*["type"])
abstract class Action(
    val type: String
) {
    /**
     * 액션이 실제 동작할지를 여부를 표현식으로 확인한다.
     * 표현식이 true/false 를 리턴해야 한다.
     * 액션을 등록은 했지만 실제 동작은 하지 않으려고 할때 사용한다.
     */
    var enabledPredicate: String? = null

    var description: String? = null
}
