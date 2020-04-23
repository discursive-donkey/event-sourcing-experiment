import api.EventQueue
import api.Query
import api.ReadModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlin.math.min

class PrayerRequestReadModel(private val queue: EventQueue) : ReadModel, CoroutineScope by CoroutineScope(newSingleThreadContext("PrayerRequestReadModel")) {
    private val prayerRequests : MutableList<PrayerRequest> = mutableListOf()

    data class PrayerRequest(val id: String, val text: String)

    private val job = launch {
        queue.get().collect { re ->
            println(re)
            when (val event = re.event) {
                is PrayerRequestAgg.CreatedEvent -> {
                    prayerRequests.add(PrayerRequest(re.aggregateId, event.text))
                }
                is PrayerRequestAgg.ChangedEvent -> {
                    val index = prayerRequests.indexOfFirst { it.id == re.aggregateId }
                    val request = prayerRequests[index]
                    prayerRequests[index] = request.copy(text = event.text)
                }
            }
        }
    }

    override suspend fun <T> query(query: Query<T>): T {
        return when (query) {
            is PrayerRequestQuery.Find -> {
                prayerRequests.subList(query.offset?:0,  min((query.offset?:0) + (query.limit?: Int.MAX_VALUE), prayerRequests.size)).map { it }
            }
            else -> error("Invalid query type")
        } as T
    }
}

sealed class PrayerRequestQuery : Query<Any> {
    data class Find(val limit: Int?, val offset: Int?) : Query<List<PrayerRequestReadModel.PrayerRequest>>
}