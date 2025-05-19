package malibu.multiturn.model

data class Param(

    /**
     * parameter 이름
     */
    val name: String,

    /**
     *  parameter 값
     *  {{}} 형식으로 el 사용가능.
     */
    val value: String,

    val description: String? = null

//    /**
//     * 가져온 값이 암호회가 되어 있을 경우에 복호화 시켜줄 enDecoder 를 지정.
//     */
//    val encoder: EnDecoder? = null
)
