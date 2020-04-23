package api

import kotlinx.coroutines.flow.*
import java.time.Instant

interface EventStore {
    suspend fun add(aggregateId: String, event: Event) : RecordedEvent
    fun get(aggregate: String? = null, after: String? = null): Flow<RecordedEvent>
}

class InMemoryEventStore : EventStore {
    private var counter : Long = 0
    private val events: MutableList<InternalStoredEvent> = mutableListOf()

    override suspend fun add(aggregateId: String, event: Event) : RecordedEvent {
        val recordedEvent = InternalStoredEvent(
                id = "${counter++}",
                timestamp = Instant.now(),
                aggregateId = aggregateId,
                event = event
        )
        events.add(recordedEvent)
        return recordedEvent
    }

    override fun get(aggregate: String?, after: String?): Flow<RecordedEvent> {
        return events.asFlow().filter { (aggregate == null || it.aggregateId == aggregate) && (after == null || it.id.toLong() > after.toLong()) }
    }

    private data class InternalStoredEvent(override val id: String, override val timestamp: Instant, override val aggregateId: String, override val event: Event) : RecordedEvent
}