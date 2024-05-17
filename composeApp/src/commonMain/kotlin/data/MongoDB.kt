package data

import domain.RequestState
import domain.TodoTask
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.delete
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class MongoDB {
    private var realm: Realm? = null

    init {
        configureRealm()
    }

    private fun configureRealm() {
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(TodoTask::class)
            )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    fun readPendingTasks(): Flow<RequestState<List<TodoTask>>> {
        return realm?.query<TodoTask>(query = "completed == $0", false)?.asFlow()
            ?.map { result ->
                RequestState.Success(
                    data = result.list.sortedByDescending { task -> task.favorite }
                )
            } ?: flow { RequestState.Error(message = "Realm is not available") }
    }

    fun readCompletedTasks(): Flow<RequestState<List<TodoTask>>> {
        return realm?.query<TodoTask>(query = "completed == $0", true)?.asFlow()
            ?.map { result ->
                RequestState.Success(
                    data = result.list
                )
            } ?: flow { RequestState.Error(message = "Realm is not available") }
    }

    suspend fun addTask(task: TodoTask) {
        realm?.write { copyToRealm(task) }
    }

    suspend fun updateTask(task: TodoTask) {
        realm?.write {
            try {
                val queriedTask = query<TodoTask>("_id == $0", task._id)
                    .find()
                    .first()

                queriedTask.let { currentTask ->
                    currentTask.title = task.title
                    currentTask.description = task.description
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun setCompleted(task: TodoTask, taskCompleted: Boolean) {
        realm?.write {
            try {
                val queriedTask = query<TodoTask>("_id == $0", task._id)
                    .find()
                    .first()

                queriedTask.apply { completed = taskCompleted }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun setFavorite(task: TodoTask, isFavorite: Boolean) {
        realm?.write {
            try {
                val queriedTask = query<TodoTask>("_id == $0", task._id)
                    .find()
                    .first()

                queriedTask.apply { favorite = isFavorite }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteTask(task: TodoTask) {
        realm?.write {
            try {
                val queriedTask = query<TodoTask>("_id == $0", task._id)
                    .find()
                    .first()

                queriedTask.let {
                    findLatest(it)?.let { currentTask ->
                        delete(currentTask)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}