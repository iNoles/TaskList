package com.jonathansteele.tasklist

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jonathansteele.Database
import com.jonathansteele.Task
import com.jonathansteele.TaskList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * com.jonathansteele.tasklist.DatabaseHelper
 * This class act like a helper between application and sqldelight
 */
class DatabaseHelper(androidSqliteDriver: AndroidSqliteDriver) {
    private val database = Database(androidSqliteDriver)

    fun getAllPages(): ImmutableList<TaskList> =
        database.listQueries
            .selectAllTasks()
            .executeAsList()
            .toImmutableList()

    fun getAllTasksBySpecificPageId(page: Int): Flow<List<Task>> =
        database.taskQueries.getAllTasksByListId(page.toLong()).asFlow().mapToList(Dispatchers.IO)

    fun getTopTaskNames() =
        database.taskQueries
            .GetTop3TasksName()
            .executeAsList()
            .toImmutableList()

    suspend fun insertTask(
        name: String,
        notes: String,
        listId: Long,
        id: Long? = null,
        hidden: Long,
    ) {
        withContext(Dispatchers.IO) {
            database.taskQueries.insertTask(
                id = id,
                listId = listId,
                name = name,
                notes = notes,
                completedDate = "0",
                hidden = hidden,
            )
        }
    }

    suspend fun insertTask(
        task: Task,
        completedDate: String,
    ) {
        withContext(Dispatchers.IO) {
            database.taskQueries.insertTask(
                id = task.id,
                listId = task.listId,
                name = task.name,
                notes = task.notes,
                completedDate = completedDate,
                hidden = task.hidden,
            )
        }
    }

    suspend fun deleteTask(id: Long) {
        withContext(Dispatchers.IO) {
            database.taskQueries.deleteTaskById(id)
        }
    }
}
