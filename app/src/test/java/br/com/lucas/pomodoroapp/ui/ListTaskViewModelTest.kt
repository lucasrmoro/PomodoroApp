package br.com.lucas.pomodoroapp.ui

import android.content.Context
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.com.lucas.pomodoroapp.core.extensions.toAdapterItem
import br.com.lucas.pomodoroapp.core.extensions.toTaskItem
import br.com.lucas.pomodoroapp.database.model.Task
import br.com.lucas.pomodoroapp.database.TaskDao
import br.com.lucas.pomodoroapp.database.TaskRepository
import br.com.lucas.pomodoroapp.helpers.AlarmManagerHelper
import br.com.lucas.pomodoroapp.helpers.PreferencesHelper
import br.com.lucas.pomodoroapp.mediators.AlarmMediator
import br.com.lucas.pomodoroapp.rules.CoroutinesTestRule
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskViewModel
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskViewStateManager
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
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

    private lateinit var context: Context
    private lateinit var intent: Intent
    private lateinit var alarmManagerHelper: AlarmManagerHelper
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var alarmMediator: AlarmMediator
    private lateinit var listTaskViewStateManager: ListTaskViewStateManager
    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepository
    private lateinit var viewModel: ListTaskViewModel

    private val testTask = Task(
        uid = 0,
        taskName = "Test",
        taskMinutes = 25
    )

    private val fakeTaskList = listOf(
        testTask.copy(uid = 0, taskName = "Test1"),
        testTask.copy(uid = 1, taskName = "Test2"),
        testTask.copy(uid = 2, taskName = "Test3")
    )

    private val fakeAdapterItemsList = fakeTaskList.map { it.toAdapterItem() }

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        intent = mockk()
        alarmManagerHelper = AlarmManagerHelper(context, intent)
        preferencesHelper = PreferencesHelper(context)
        alarmMediator = AlarmMediator(alarmManagerHelper, preferencesHelper)
        listTaskViewStateManager = ListTaskViewStateManager()
        taskDao = mockk(relaxed = true)
        repository = TaskRepository(taskDao)
        viewModel = ListTaskViewModel(repository, alarmMediator, listTaskViewStateManager)
    }

    @Test
    fun `Refresh for the first time - there is no previous selection - no tasks - no task timer enabled`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            coEvery { taskDao.getAll() }.returns(flowOf(emptyList()))
            viewModel.refresh()

            val taskList = viewModel.taskList.value
            val isSelectionModeEnabled = viewModel.isSelectionModeEnabled()

            Truth.assertThat(taskList).isEmpty()
            Truth.assertThat(isSelectionModeEnabled).isFalse()
        }

    @Test
    fun `Refresh for the first time - there are tasks and there is no previous selection - no task timer enabled`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            coEvery { taskDao.getAll() }.returns(flowOf(fakeTaskList))
            viewModel.refresh()

            val taskList = viewModel.taskList.value
            val isSelectionModeEnabled = viewModel.isSelectionModeEnabled()

            Truth.assertThat(taskList).isNotEmpty()
            Truth.assertThat(isSelectionModeEnabled).isFalse()
        }

    @Test
    fun `Refresh with previous selection - there are selected tasks and there is no task timer enabled`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            coEvery { taskDao.getAll() }.returns(flowOf(fakeTaskList))

            val adapterItem1 = fakeTaskList.elementAt(0).toAdapterItem()
            val adapterItem2 = fakeTaskList.elementAt(1).toAdapterItem()

            viewModel.syncSelection(adapterItem1)
            viewModel.syncSelection(adapterItem2)

            viewModel.refresh()

            Truth.assertThat(viewModel.getQuantityOfSelectedTasks()).isEqualTo(2)
        }

    @Test
    fun `Refresh with task timer enabled - there are no selected tasks and there is task timer enabled`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            coEvery { taskDao.getAll() }.returns(flowOf(fakeTaskList))

            val taskEnabled = fakeAdapterItemsList.elementAt(1)
            viewModel.syncTaskTimer(taskEnabled, isTimerEnabled = true)
            viewModel.refresh()

            verify(atLeast = 1) {
                alarmMediator.syncTaskTimer(isTimerEnabled = true,
                    taskId = taskEnabled.uid,
                    alarmTime = taskEnabled.taskMinutes)
            }
        }

    @Test
    fun `Refresh with previous selection and task timer enabled`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            coEvery { taskDao.getAll() }.returns(flowOf(fakeTaskList))

            val taskEnabled = fakeAdapterItemsList.elementAt(2)

            viewModel.syncSelection(fakeAdapterItemsList.elementAt(0))
            viewModel.syncSelection(fakeAdapterItemsList.elementAt(1))
            viewModel.syncTaskTimer(taskEnabled, isTimerEnabled = true)

            viewModel.refresh()

            verify(atLeast = 1) {
                alarmMediator.syncTaskTimer(isTimerEnabled = true,
                    taskId = taskEnabled.uid,
                    alarmTime = taskEnabled.taskMinutes
                )
            }
            Truth.assertThat(viewModel.getQuantityOfSelectedTasks()).isEqualTo(2)
        }

    @Test
    fun `Delete task when there are tasks to delete`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            coEvery { taskDao.getAll() }.returns(flowOf(fakeTaskList))

            viewModel.syncSelection(fakeAdapterItemsList.elementAt(0))
            viewModel.syncSelection(fakeAdapterItemsList.elementAt(1))

            viewModel.deleteTasks()

            coVerify(exactly = 2) { repository.deleteTask(any()) }
        }

    @Test
    fun `Add ten tasks automatically on database`() =
        coroutinesTestRule.testDispatcher.runBlockingTest {
            viewModel.addTenTasksOnDataBase()

            coVerify(exactly = 10) { repository.insertTask(any()) }
        }

    @Test
    fun `Disable task timer enabled`() {
        val taskToDisable = fakeAdapterItemsList.elementAt(1)

        viewModel.syncTaskTimer(taskToDisable, isTimerEnabled = false)

        verify(exactly = 1) {
            alarmMediator.syncTaskTimer(isTimerEnabled = false,
                taskId = taskToDisable.uid,
                alarmTime = taskToDisable.taskMinutes
            )
        }
    }

    @Test
    fun `Convert adapter item to task`() {
        val task = testTask.copy(uid = 3, taskName = "Test", 35).toAdapterItem()

        Truth.assertThat(viewModel.convertAdapterItemToTask(task)).isEqualTo(task.toTaskItem())
    }
}