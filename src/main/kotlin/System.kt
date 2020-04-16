import kotlinx.coroutines.flow.fold
import java.util.*

class EventSourcingSystemSystem (
    private val eventStore: EventStore
) {
    suspend fun <S> handleCommand(aggregate: Aggregate<S>, aggregateId: String?, command: Command) : String {


        val currentState : S? = if (aggregateId == null) null else eventStore.allForAggregate(aggregateId).fold<Event, S?>(null) { lastState, event ->
            var nextState : S? = lastState
            aggregate.handleEvent(object : Aggregate.EventContext<S> {
                override val state get() = nextState
                override fun setState(state: S) {
                    nextState = state
                }

                override val aggregateId: String?
                    get() = aggregateId
            }, event)
            nextState
        }
        val events = mutableListOf<Event>()

        aggregate.handleCommand(object : Aggregate.CommandContext<S> {
            override val aggregateId: String?
                get() = aggregateId

            override fun reject(reason: String) {
                error(reason)
            }

            override fun emit(event: Event) {
                events.add(event)
            }

            override val state: S?
                get() = currentState
        }, command)

        val resultingAggregateId = aggregateId ?: UUID.randomUUID().toString()

        events.forEach { eventStore.add(resultingAggregateId, it) }
        return resultingAggregateId
    }
}

private fun <S> Aggregate<S>.handleEvent(context: Aggregate.EventContext<S>, event: Event) = context.handleEvent(event)

private fun <S> Aggregate<S>.handleCommand(context: Aggregate.CommandContext<S>, command: Command) = context.handleCommand(command)