package malibu.multiturn.framework

import com.fasterxml.jackson.databind.ObjectMapper
import malibu.multiturn.TestBotScenarios
import org.junit.jupiter.api.Test
import java.util.*

class MultiTurnHandlerTest {

    @Test
    fun test() {
        val om = ObjectMapper()
        val botScenario = TestBotScenarios.generate()
        val configuration = MultiTurnConfiguration()
        configuration.initialize(om)

        val handler = MultiTurnHandler(
            botScenario = botScenario,
            configuration = configuration,
        )

        val req = MultiTurnReq(
            requestId = UUID.randomUUID().toString(),
            intent = "test",
        )
        val resultJson = handler.handle(req)
            .block()
            .let { om.writerWithDefaultPrettyPrinter().writeValueAsString(it) }

        println(resultJson)
    }
}