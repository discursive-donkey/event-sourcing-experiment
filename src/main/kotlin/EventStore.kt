import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow

interface EventStore {
    suspend fun add(aggregateId: String, event: Event)
    fun allForAggregate(aggregateId: String) : Flow<Event>
    fun all(): Flow<Event>
}

class InMemoryEventStore : EventStore {
    private val events: MutableMap<String, MutableList<Event>> = mutableMapOf()

    override suspend fun add(aggregateId: String, event: Event) {
        events.putIfAbsent(aggregateId, mutableListOf(event))?.add(event)
    }

    override fun allForAggregate(aggregateId: String): Flow<Event> {
        return events[aggregateId]?.asFlow()?: emptyFlow()
    }

    override fun all(): Flow<Event> {
        return events.flatMap { (_, list) -> list }.asFlow()
    }
}