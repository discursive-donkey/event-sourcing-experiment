package api

interface Query<out T> {}

interface ReadModel {
    suspend fun <T> query(query: Query<T>) : T
}
