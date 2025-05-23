# multi-turn

multi turn conversation (여러 차례 말을 주고 받아야 하는 대화) 를 설계할 수 있게 해주는 framework 입니다.
구조적 데이터로 주고 받아야 할 대화의 명세를 작성하고 handler 를 통해 실제 대화를 진행시킬 수 있습니다.  
다양한 확장 포인트를 통해 비지니스 로직에 따른 custom 기능을 추가 할 수 있습니다.

## development spec
- jdk21
- gradle
- kotlin
- ProjectReactor (reactiveStream)

## framework spec

## scenario define model
- BotScenario
- Topic
- TopicState
- Intend
- Task
- Action
- Directive


## behavior
- MultiTurnModule
- Action
- Argument


## BotScenarioHandler

- MultiTurnReq
- MultiTurnRes
- IntendData

## MultiTurnModule


## CoreModule
기본적인 기능을 제공하는 Module 입니다.

- ExpressionAction
- ExpressionArgument
- AddConversationParamAction
- RemoveConversationParamAction
- AddInstantParamAction
- TransferInstantParamAction