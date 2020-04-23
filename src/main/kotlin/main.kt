import api.CommandHandler
import api.InMemoryEventQueue
import api.InMemoryEventStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    val agg = PrayerRequestAgg()
    val eventStore = InMemoryEventStore()
    val eventQueue = InMemoryEventQueue()
    val commandHandler = CommandHandler(eventStore, eventQueue)
    val prayerRequestReadModel = PrayerRequestReadModel(eventQueue)

    runBlocking {
        repeat((1..12).count()) {
            val id = commandHandler.handleCommand(agg, null, PrayerRequestAgg.CreateCommand("Testanliegen"))
            commandHandler.handleCommand(agg, id, PrayerRequestAgg.ChangeCommand("Geänderter Text"))
        }
        delay(1000)
        val result = prayerRequestReadModel.query(PrayerRequestQuery.Find(10, 0))
        println(result)
    }
}