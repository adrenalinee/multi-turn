package malibu.multiturn.framework

/**
 * 입력 파라미터중에 intent argument 에서 가져온 값을 사용하고 싶을때
 * PlaceholderResolver 를 사용합니다. 입력 문자열안에 "{{..}}" 와 같은 문자가 있을 경우에 표현식파서(ExpressionParser)에 이 문자열
 * 을 넘기고 여기서 리턴하는 값으로 치환 시켜주는 역할을 합니다.
 *
 * 만약 value 에 placeholder 가 없다면 그대로 리턴하게 됩니다.
 * 이 객체는 bot template 구성요소들을 구현하는 개발자를 지원하기 위해
 * 만들어졌습니다. 각 구성요소에서 입력값을 intent argument 에서 가져오게 하고 싶을때 PlaceholderResolver
 * 를 사용하도록 개발되어야 하고, 해당 구성 요소 사용자에게 플레이스홀더 동작이 가능함을 알려줘야 합니다.
 */
object PlaceholderResolver {

    /**
     * @return resolved string
     */
    fun resolve(rawValue: String, evaluate: (expression: String) -> String): String {
        if (rawValue.indexOf("{{") < 0) {
            return rawValue
        }

        var nextStartIdx = 0
        var placeholder = extractFirstPlaceholder(rawValue, nextStartIdx)
        if (placeholder != null) {
            if (placeholder.startIdx == 0 && placeholder.endIdx == rawValue.length) {
                //전체 value 가 하나의 placeholder 일 경우에는 string 으로 변경하지 않고 객체 자체를 리턴
                return evaluate(placeholder.value)
            }
        }

        val placeholders = mutableListOf<Placeholder>()
        while (placeholder != null) {
            placeholders.add(placeholder)
            nextStartIdx = placeholder.endIdx

            placeholder = extractFirstPlaceholder(rawValue, nextStartIdx)
        }

        var resolvedString = rawValue
        if (placeholders.isEmpty()) {
            return resolvedString
        }

        placeholders.filter { curPlaceholder -> curPlaceholder.value.isNotBlank() }
            .forEach { curPlaceholder ->
                evaluate(curPlaceholder.value)?.also { argumentValue ->
                    resolvedString = resolvedString.replace(
                        "{{${curPlaceholder.value}}}",
                        argumentValue
                    )
                }
            }

        return resolvedString
    }

    /**
     * 원본 string 의 findStartIdx 뒤에서부터 처음 발결되는 첫번째 placeholder 를 찾아줌.
     * @param string - 원본 문자열
     * @param findStartIdx - placeholder 를 찾기 시작할 인덱스.
     * @return 못찾으면 null 리턴
     */
    private fun extractFirstPlaceholder(string: String, findStartIdx: Int): Placeholder? {
        val startIdx = string.subSequence(findStartIdx, string.length).indexOf("{{") + findStartIdx

        if (startIdx < findStartIdx) {
            return null
        }


        val endIdx = string.subSequence(startIdx, string.length).indexOf("}}") + startIdx + 2

        return Placeholder(startIdx, endIdx, string.substring(startIdx + 2, endIdx - 2))
    }
}

/**
 * string = "aaaa{{bbbbbbbb}}ccc"
 *               |startIdx  |
 *                          |endIdx
 *
 * @param startIdx - placeholder 의 시작을 알리는 {{ 문자의 앞 인덱스
 * @param endIdx = placeholder 의 끝을 알리는 }} 문자가 끝나는 인덱스
 * @param value - placeholder 를 알리는 문자를 제외한 실제 파라미터 이름
 */
data class Placeholder(
    val startIdx: Int,
    val endIdx: Int,
    val value: String
)
