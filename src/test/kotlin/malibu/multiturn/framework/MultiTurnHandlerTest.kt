package malibu.multiturn.framework

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import malibu.multiturn.TestBotScenarios
import malibu.multiturn.framework.config.MultiTurnConfiguration
import org.junit.jupiter.api.Test
import java.util.*

class MultiTurnHandlerTest {

    @Test
    fun test() {
        val om = ObjectMapper()
        om.registerModule(KotlinModule.Builder().build())
        val botScenario = TestBotScenarios.generate()

        val configuration = MultiTurnConfiguration()
        configuration.initialize(om)

        om
//            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(botScenario)
            .also { println(it) }

        val handler = MultiTurnHandler(
            botScenario = botScenario,
            configuration = configuration,
        )

        val req = MultiTurnReq(
            requestId = UUID.randomUUID().toString(),
            intent = "test",
            conversationParams = mapOf("conversation-param" to "test"),
            instantParams = mapOf("instant-param" to "test"),
        )
        val resultJson = handler.handle(req, true)
            .block()
            .let { om.writerWithDefaultPrettyPrinter().writeValueAsString(it) }

        println(resultJson)
    }
}