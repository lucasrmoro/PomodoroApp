package br.com.lucas.pomodoroapp.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.lucas.pomodoroapp.CoroutinesTestRule
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.database.TaskDao
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.helpers.AlarmManagerHelper
import br.com.lucas.pomodoroapp.ui.editTaskScreen.EditTaskViewModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class EditTaskViewModelTest {

    @get: Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get: Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private lateinit var context: Context
    private lateinit var alarmManagerHelper: AlarmManagerHelper
    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepository
    private lateinit var viewModel: EditTaskViewModel

    private val testTask = Task(
        uid = 0,
        taskName = "Test",
        taskMinutes = 25
    )

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        alarmManagerHelper = AlarmManagerHelper(context)
        taskDao = mockk(relaxed = true)
        repository = TaskRepository(taskDao)
        viewModel = EditTaskViewModel(repository, alarmManagerHelper)
    }

    @Test
    fun `Add a new task`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.checkTaskTimeIsValid(0, 30)
            viewModel.checkTaskNameIsValid("New Task")
            viewModel.onSaveEvent("New Task")

            coVerify(exactly = 1) { repository.insertTask(any()) }
        }


    @Test
    fun `Update an existing task`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.setup(testTask)
            viewModel.onSaveEvent(taskName = "UpdatedName")

            coVerify(exactly = 1) { repository.updateTask(any()) }
        }

    @Test
    fun `Delete a task`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.setup(testTask)
            viewModel.delete()

            coVerify(exactly = 1) { repository.deleteTask(any()) }
        }
}