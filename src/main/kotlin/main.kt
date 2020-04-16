import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

fun main() {
    val agg = PrayerRequest()
    val eventStore = InMemoryEventStore()
    val system = EventSourcingSystemSystem(eventStore)

    runBlocking {
        val id = system.handleCommand(agg, null, PrayerRequest.CreateCommand("Testanliegen"))
        system.handleCommand(agg, id, PrayerRequest.ChangeCommand("Geänderter Text"))

        println(eventStore.allForAggregate(id).toList())
    }
}