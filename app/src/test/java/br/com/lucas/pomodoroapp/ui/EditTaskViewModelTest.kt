package br.com.lucas.pomodoroapp.ui

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.database.TaskDao
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.helpers.PreferencesHelper
import br.com.lucas.pomodoroapp.rules.CoroutinesTestRule
import br.com.lucas.pomodoroapp.rules.TimberRule
import br.com.lucas.pomodoroapp.ui.editTaskScreen.EditTaskViewModel
import com.google.common.truth.Truth
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

    @get: Rule
    val timberRule = TimberRule()

    private lateinit var context: Context
    private lateinit var preferencesHelper: PreferencesHelper
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
        preferencesHelper = PreferencesHelper(context)
        taskDao = mockk(relaxed = true)
        repository = TaskRepository(taskDao)
        viewModel = EditTaskViewModel(repository, preferencesHelper)
    }

    @Test
    fun `Add a new task - there are valid name and time`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.checkTaskTimeIsValid(0, 30)
            viewModel.checkTaskNameIsValid("New Task")
            viewModel.onSaveEvent("New Task")

            Truth.assertThat(viewModel.task).isNull()
            Truth.assertThat(viewModel.isEditMode).isFalse()
            coVerify(exactly = 1) { repository.insertTask(any()) }
        }

    @Test
    fun `Add a new task - there are valid name and invalid time`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.checkTaskTimeIsValid(0, 10)
            viewModel.checkTaskNameIsValid("New task")
            viewModel.onSaveEvent("New task")

            Truth.assertThat(viewModel.task).isNull()
            Truth.assertThat(viewModel.isEditMode).isFalse()
            coVerify(exactly = 0) { repository.insertTask(any()) }
        }

    @Test
    fun `Add a new task - there are invalid name and valid time`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.checkTaskTimeIsValid(0, 30)
            viewModel.checkTaskNameIsValid("a")
            viewModel.onSaveEvent("a")

            Truth.assertThat(viewModel.task).isNull()
            Truth.assertThat(viewModel.isEditMode).isFalse()
            coVerify(exactly = 0) { repository.insertTask(any()) }
        }


    @Test
    fun `Update an existing task - there are valid name and time`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.setup(testTask)
            viewModel.checkTaskNameIsValid("UpdatedName")
            viewModel.checkTaskTimeIsValid(0, 35)
            viewModel.onSaveEvent(taskName = "UpdatedName")

            Truth.assertThat(viewModel.task).isEqualTo(testTask)
            Truth.assertThat(viewModel.isEditMode).isTrue()
            coVerify(exactly = 1) { repository.updateTask(any()) }
        }

    @Test
    fun `Update an existing task - there are valid name and invalid time`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.setup(testTask)
            viewModel.checkTaskNameIsValid("UpdatedName")
            viewModel.checkTaskTimeIsValid(0, 20)
            viewModel.onSaveEvent(taskName = "UpdatedName")

            Truth.assertThat(viewModel.task).isEqualTo(testTask)
            Truth.assertThat(viewModel.isEditMode).isTrue()
            coVerify(exactly = 0) { repository.updateTask(any()) }
        }

    @Test
    fun `Update an existing task - there are invalid name and valid time`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.setup(testTask)
            viewModel.checkTaskNameIsValid("a")
            viewModel.checkTaskTimeIsValid(0, 45)
            viewModel.onSaveEvent(taskName = "a")

            Truth.assertThat(viewModel.task).isEqualTo(testTask)
            Truth.assertThat(viewModel.isEditMode).isTrue()
            coVerify(exactly = 0) { repository.updateTask(any()) }
        }

    @Test
    fun `Delete a task`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.setup(testTask)
            viewModel.delete()

            Truth.assertThat(viewModel.task).isEqualTo(testTask)
            Truth.assertThat(viewModel.isEditMode).isTrue()
            coVerify(exactly = 1) { repository.deleteTask(any()) }
        }
}