package api

interface Aggregate<S> {
    fun CommandContext<S>.handleCommand(command: Command)
    fun EventContext<S>.handleEvent(event: Event)

    interface CommandContext<S> {
        val aggregateId: String?
        val state: S?
        fun reject(reason: String)
        fun emit(event: Event)
    }

    interface EventContext<S> {
        val aggregateId: String?
        val state: S?
        fun setState(newState: S)
    }
}

