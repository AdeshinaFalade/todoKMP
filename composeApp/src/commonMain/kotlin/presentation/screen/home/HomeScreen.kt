package presentation.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.RequestState
import domain.TaskAction
import domain.TodoTask
import presentation.components.ErrorScreen
import presentation.components.LoadingScreen
import presentation.components.TaskView
import presentation.components.TasksProgress
import presentation.screen.task.TaskScreen

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<HomeViewModel>()
        val completedTasks by viewModel.completedTasks
        val pendingTasks by viewModel.activeTasks

        Scaffold(topBar = {
            CenterAlignedTopAppBar(title = { Text("Home") })
        }, floatingActionButton = {
            FloatingActionButton(
                onClick = { navigator.push(TaskScreen()) }, shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit, contentDescription = "Edit Icon"
                )
            }
        }) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 24.dp).padding(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding()
                )
            ) {
                TasksProgress(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    completedTasks = completedTasks.getSuccessDataOrNull()?.size ?: 0,
                    pendingTasks = pendingTasks.getSuccessDataOrNull()?.size ?: 0
                )
                Spacer(modifier = Modifier.height(16.dp))
                DisplayTasks(modifier = Modifier.weight(1f),
                    tasks = pendingTasks,
                    onSelect = { selectedTask -> navigator.push(TaskScreen(selectedTask)) },
                    onFavorite = { task, isFavorite ->
                        viewModel.setAction(TaskAction.SetFavorite(task, isFavorite))
                    },
                    onComplete = { task, completed ->
                        viewModel.setAction(TaskAction.SetCompleted(task, completed))
                    })
                Spacer(modifier = Modifier.height(16.dp))
                DisplayTasks(modifier = Modifier.weight(1f),
                    tasks = completedTasks,
                    showActive = false,
                    onComplete = { task, completed ->
                        viewModel.setAction(TaskAction.SetCompleted(task, completed))
                    },
                    onDelete = { task ->
                        viewModel.setAction(TaskAction.Delete(task))
                    })
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayTasks(
    modifier: Modifier = Modifier,
    tasks: RequestState<List<TodoTask>>,
    showActive: Boolean = true,
    onSelect: ((TodoTask) -> Unit)? = null,
    onFavorite: ((TodoTask, Boolean) -> Unit)? = null,
    onComplete: (TodoTask, Boolean) -> Unit,
    onDelete: ((TodoTask) -> Unit)? = null,
) {
    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete: TodoTask? by remember { mutableStateOf(null) }

    if (showDialog) {
        AlertDialog(title = {
            Text(
                text = "Delete", fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }, text = {
            Text(
                text = "Are you sure you want to remove ${taskToDelete!!.title}",
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        }, confirmButton = {
            Button(onClick = {
                onDelete?.invoke(taskToDelete!!)
                showDialog = false
                taskToDelete = null
            }) {
                Text("Yes")
            }
        }, dismissButton = {
            TextButton(onClick = {
                taskToDelete = null
                showDialog = false
            }) {
                Text("Cancel")
            }
        }, onDismissRequest = {
            taskToDelete = null
            showDialog = false
        })
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = if (showActive) "Pending Tasks" else "Completed Tasks",
            modifier = Modifier.padding(horizontal = 24.dp),
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        tasks.DisplayResult(onLoading = { LoadingScreen() },
            onError = { ErrorScreen(message = it) },
            onSuccess = { todoTasks ->
                if (todoTasks.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(items = todoTasks, key = { task -> task._id.toHexString() }) { task ->
                            TaskView(showActive = showActive,
                                task = task,
                                onSelect = { onSelect?.invoke(it) },
                                onComplete = onComplete,
                                onFavorite = { selectedTask, favorite ->
                                    onFavorite?.invoke(selectedTask, favorite)
                                },
                                onDelete = { selectedTask ->
                                    taskToDelete = selectedTask
                                    showDialog = true
                                })
                        }
                    }
                } else {
                    ErrorScreen()
                }
            })
    }
}