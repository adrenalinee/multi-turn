package malibu.multiturn.model

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type"
)
@JsonPropertyOrder(*["type"])
abstract class Argument(
    val type: String,
) {
    var required: Boolean? = null

    var description: String? = null
}
