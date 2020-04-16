class PrayerRequest : Aggregate<PrayerRequest.State> {
    override fun Aggregate.CommandContext<State>.handleCommand(command: Command) {
        when(command) {
            is CreateCommand -> {
                if (command.text.isBlank()) reject("Text must not be blank")
                emit(CreatedEvent(command.text))
            }
            is ChangeCommand -> {
                if (state?.created != true) reject("Prayer Request must be created before it can be changed")
                if (command.text.isBlank()) reject("Text must not be blank")
                emit(ChangedEvent(command.text))
            }
        }
    }

    override fun Aggregate.EventContext<State>.handleEvent(event: Event) {
        when(event) {
            is CreatedEvent -> setState(State(true, event.text))
            is ChangedEvent -> setState(state!!.copy(text = event.text))
        }
    }

    data class State(
        val created: Boolean,
        val text: String
    )

    data class CreateCommand(
        val text: String
    ) : Command

    data class ChangeCommand(
        val text: String
    ) : Command

    data class CreatedEvent  (
        val text: String
    ) : Event

    data class ChangedEvent(
        val text: String
    ): Event
}