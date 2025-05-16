package malibu.multiturn.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import malibu.multiturn.TestBotScenarios
import org.junit.jupiter.api.Test

class BotScenarioTest {

    @Test
    fun parseTest() {
        val om = ObjectMapper()
        om.registerModule(KotlinModule.Builder().build())
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL)

        val testBotScenario = TestBotScenarios.generate()
        val json = om.writeValueAsString(testBotScenario)
        println(json)
    }
}