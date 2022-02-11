package br.com.lucas.pomodoroapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.lucas.pomodoroapp.CoroutinesTestRule
import br.com.lucas.pomodoroapp.database.Task
import br.com.lucas.pomodoroapp.database.TaskDao
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskViewModel
import com.google.common.truth.Truth
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.coroutines.suspendCoroutine

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
            val result2 = viewModel.previousSelection

            Truth.assertThat(result1).isEmpty()
            Truth.assertThat(result2).isEmpty()
        }

    @Test
    fun `Refresh for the first time - there is no previous selection and there are tasks`() =
        runBlockingTest {
            coEvery { taskDao.getAll() }.returns(flowOf(listOf(testTask.copy())))
            viewModel.refresh()

            val result1 = viewModel.taskList.value
            val result2 = viewModel.previousSelection

            Truth.assertThat(result1).isNotEmpty()
            Truth.assertThat(result2).isEmpty()
        }

    @Test
    fun `Refresh with previous selection - there are some selected tasks`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val task1 = testTask.copy(uid = 0, taskName = "Test1").apply { toggleTask() }
            val task2 = testTask.copy(uid = 1, taskName = "Test2").apply { toggleTask() }
            val task3 = testTask.copy(uid = 2, taskName = "Test3")
            coEvery { taskDao.getAll() }.returns(flowOf(listOf(task1, task2, task3)))
            viewModel.refresh()

            val tasksSelected = viewModel.taskList.value
                ?.filter { it.isTaskSelected() }
                ?.map { it.uid }
                ?.toList() ?: listOf(0, 1)

            viewModel.processPreviousSelection(ArrayList(tasksSelected))

            val listOfTasksUid = viewModel.taskList.value
                ?.map { it.uid }
                ?.toList()

            Truth.assertThat(listOfTasksUid?.containsAll(viewModel.previousSelection)).isTrue()
        }

    @Test
    fun `Delete task when there are tasks to delete`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            val task1 = testTask.copy(uid = 0, taskName = "Test1")
            val task2 = testTask.copy(uid = 1, taskName = "Test2")
            coEvery { taskDao.getAll() }.returns(flowOf(listOf(task1, task2)))

            viewModel.syncSelection(task1, true)
            viewModel.syncSelection(task2, true)

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