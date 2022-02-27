package br.com.lucas.pomodoroapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.lucas.pomodoroapp.rules.CoroutinesTestRule
import br.com.lucas.pomodoroapp.core.extensions.toAdapterItem
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.database.TaskDao
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskViewModel
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ListTaskViewModelTest {

    @get: Rule
    val instantExecutor = InstantTaskExecutorRule()

    @get: Rule
    val coroutinesTestRule = CoroutinesTestRule()

    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepository
    private lateinit var viewModel: ListTaskViewModel

    private val testTask = Task(
        uid = 0,
        taskName = "Test",
        taskMinutes = 25
    )

    @Before
    fun setup() {
        taskDao = mockk(relaxed = true)
        repository = TaskRepository(taskDao)
        viewModel = ListTaskViewModel(repository)
    }

    @Test
    fun `Refresh for the first time - there is no previous selection and there is no task`() =
        runBlockingTest {
            coEvery { taskDao.getAll() }.returns(flowOf(emptyList()))
            viewModel.refresh()

            val result1 = viewModel.taskList.value
            val result2 = viewModel.isSelectedModeEnabled()

            Truth.assertThat(result1).isEmpty()
            Truth.assertThat(result2).isFalse()
        }

    @Test
    fun `Refresh for the first time - there is no previous selection and there are tasks`() =
        runBlockingTest {
            coEvery { taskDao.getAll() }.returns(flowOf(listOf(testTask.copy())))
            viewModel.refresh()

            val result1 = viewModel.taskList.value
            val result2 = viewModel.isSelectedModeEnabled()

            Truth.assertThat(result1).isNotEmpty()
            Truth.assertThat(result2).isFalse()
        }

    @Test
    fun `Refresh with previous selection - there are some selected tasks`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val task1 = testTask.copy(uid = 0, taskName = "Test1")
            val task2 = testTask.copy(uid = 1, taskName = "Test2")
            val task3 = testTask.copy(uid = 2, taskName = "Test3")
            coEvery { taskDao.getAll() }.returns(flowOf(listOf(task1, task2, task3)))

            val selectedTask1 = task1.toAdapterItem().apply { toggleTask() }
            val selectedTask2 = task2.toAdapterItem().apply { toggleTask() }

            viewModel.refresh()

            viewModel.syncSelection(selectedTask1)
            viewModel.syncSelection(selectedTask2)

            Truth.assertThat(viewModel.getQuantityOfSelectedTasks()).isEqualTo(2)
        }

    @Test
    fun `Delete task when there are tasks to delete`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val task1 = testTask.copy(uid = 0, taskName = "Test1")
            val task2 = testTask.copy(uid = 1, taskName = "Test2")
            coEvery { taskDao.getAll() }.returns(flowOf(listOf(task1, task2)))

            val selectedTask1 = task1.toAdapterItem().apply { toggleTask() }
            val selectedTask2 = task2.toAdapterItem().apply { toggleTask() }

            viewModel.syncSelection(selectedTask1)
            viewModel.syncSelection(selectedTask2)

            viewModel.deleteTasks()

            coVerify(exactly = 2) { repository.deleteTask(any()) }
        }

    @Test
    fun `Add ten tasks automatically on database`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {

            viewModel.addTenTasksOnDataBase()

            coVerify(exactly = 10) { repository.insertTask(any()) }
        }
}