package api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

interface EventQueue {
    suspend fun get() : Flow<RecordedEvent>
    suspend fun push(event: RecordedEvent)
}

class InMemoryEventQueue : EventQueue{
    private val events = mutableListOf<RecordedEvent>()

    private val context = newSingleThreadContext("InMemoryEventQueue")

    override suspend fun get(): Flow<RecordedEvent> {
        return flow {
            var currentIndex = 0
            while (true) {
                if (currentIndex >= events.size) delay(1)
                else {
                    emit(events[currentIndex++])
                }
            }
        }
    }

    override suspend fun push(event: RecordedEvent) {
        withContext(coroutineContext + context) {
            events.add(event)
        }
    }
}