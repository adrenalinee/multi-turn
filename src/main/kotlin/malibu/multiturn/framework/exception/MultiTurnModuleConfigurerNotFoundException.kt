package malibu.multiturn.framework.exception

class MultiTurnModuleConfigurerNotFoundException(
    multiTurnModuleType: String
): MultiTurnException(
    message = "multiTurnModuleConfigurer 를 찾을 수 없습니다. multiTurnModuleType: $multiTurnModuleType"
)