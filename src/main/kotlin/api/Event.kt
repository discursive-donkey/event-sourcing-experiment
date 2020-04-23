package api

import java.time.Instant

interface Event {}

interface RecordedEvent {
    val id: String
    val timestamp: Instant
    val aggregateId: String
    val event: Event
}