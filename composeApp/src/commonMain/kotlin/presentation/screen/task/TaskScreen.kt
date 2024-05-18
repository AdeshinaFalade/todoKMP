package presentation.screen.task

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.ImeOptions
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.TaskAction
import domain.TodoTask
import presentation.screen.home.HomeViewModel

const val DEFAULT_TITLE = "Enter the Title"
const val DEFAULT_DESCRIPTION = "Add some description"

data class TaskScreen(val task: TodoTask? = null) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<TaskViewModel>()
        val keyboard = LocalSoftwareKeyboardController.current

        var currentTitle by remember {
            mutableStateOf(task?.title ?: "")
        }
        var currentDescription by remember {
            mutableStateOf(task?.description ?: "")
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TextField(
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            ),
                            singleLine = true,
                            value = currentTitle,
                            onValueChange = { currentTitle = it },
                            placeholder = {
                                Text(
                                    text = DEFAULT_TITLE,
                                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back Arrow"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentTitle.isNotBlank()) {
                    FloatingActionButton(
                        onClick = {
                            if (task != null) {
                                viewModel.setAction(TaskAction.Update(
                                    TodoTask().apply {
                                        _id = task._id
                                        title = currentTitle
                                        description = currentDescription
                                    }
                                ))
                            } else {
                                viewModel.setAction(TaskAction.Add(TodoTask().apply {
                                    title = currentTitle
                                    description =  currentDescription
                                }))
                            }
                            navigator.pop()
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null
                        )
                    }
                }
            }
        ) { padding ->
            TextField(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    ),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize
                ),
                value = currentDescription,
                onValueChange = { description -> currentDescription = description },
                keyboardActions = KeyboardActions(onDone = {
                    keyboard?.hide()
                }),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = {
                    Text(
                        text = DEFAULT_DESCRIPTION,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )
                }



            )

        }
    }
}