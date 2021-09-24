package br.com.lucas.pomodoroapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.lucas.pomodoroapp.R
import br.com.lucas.pomodoroapp.R.string.*
import br.com.lucas.pomodoroapp.core.extensions.toast
import br.com.lucas.pomodoroapp.databinding.ActivityListTaskBinding


class ListTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListTaskBinding

    lateinit var viewModel: ListTaskViewModel

    lateinit var adapter: ListTaskAdapter

    private var moreOptionsMenu: Menu? = null

    private var clicked = false

    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            this,
            R.anim.to_bottom_anim
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListTaskBinding.inflate(layoutInflater)
        viewModel = ListTaskViewModel(application)
        setContentView(binding.root)

        viewModel.taskList.observe(
            this
        ) { tasks ->
            (binding.recyclerView.adapter as ListTaskAdapter).addTask(tasks)
        }

        viewModel.selectionMode.observe(
            this
        ) { selectionMode ->
            changeTrashVisibilityBasedOnSelectionMode()
            if (!selectionMode) adapter.reset()
        }

        binding.fab.setOnClickListener { view ->
            EditTaskActivity.launchNewTaskScreen(this)
        }

        if (viewModel.isDebugMode()) {
            setupDebugButtons()
        }

        configureList(this)
    }

    private fun setupDebugButtons() {
        setDebugFabVisible()

        binding.debugFab.setOnClickListener {
            onButtonClicked()
        }

        binding.debugAddTasksFab.setOnClickListener {
            addTenTasksAutomatically()
            onButtonClicked()
        }
    }

    private fun setDebugFabVisible() {
        binding.debugFab.visibility = View.VISIBLE
    }

    private fun addTenTasksAutomatically() {
        try {
            viewModel.addTenTasksOnDataBase()
            toast(successfully_ten_tasks_added)
        } catch (e: Exception) {
            toast(somenthing_went_wrong)
        }
    }

    private fun onButtonClicked() {
        setVisibility()
        setAnimation()
        changeDebugIcon()
        setClickable()
        clicked = !clicked
    }

    private fun setVisibility() {
        binding.debugAddTasksFab.isVisible = clicked
    }

    private fun changeDebugIcon() {
        if (!clicked)
            binding.debugFab.setImageResource(R.drawable.ic_close)
        else
            binding.debugFab.setImageResource(R.drawable.ic_skull)
    }

    private fun setAnimation() {
        if (!clicked)
            binding.debugAddTasksFab.startAnimation(fromBottom)
        else
            binding.debugAddTasksFab.startAnimation(toBottom)
    }

    private fun setClickable() {
        binding.debugAddTasksFab.isClickable = !clicked
    }

    private fun changeTrashVisibilityBasedOnSelectionMode() {
        this.moreOptionsMenu?.findItem(R.id.menu_more_options_action)?.isVisible =
            viewModel.selectionMode.value == true
    }

    private fun configureList(context: Context) {
        adapter = ListTaskAdapter(
            selectionTaskCallback = { task, isSelected ->
                Log.d("taskSelection", "task: ${task.taskName} --> isSelected: $isSelected")
                viewModel.syncSelection(task, isSelected)
            },
            isSelectionModeEnabledCallback = { viewModel.isSelectedModeEnabled() },
            launchEditScreenCallback = { EditTaskActivity.launchEditTaskScreen(context, it) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.more_options_menu, menu)
        this.moreOptionsMenu = menu
        this.moreOptionsMenu?.findItem(R.id.menu_more_options_action)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_more_options_action) {
            setupPopupMenu()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupPopupMenu() {
        val menuItemView = findViewById<View>(R.id.menu_more_options_action)
        val popupMenu = PopupMenu(this, menuItemView)
        popupMenu.inflate(R.menu.popup_options_menu)
        popupMenu.show()
        configurePopupMenuListeners(popupMenu)
    }

    private fun configurePopupMenuListeners(popupMenu: PopupMenu) {
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.popup_menu_select_all_action -> toast(getString(feature_isnt_implemented))
                R.id.popup_menu_delete -> setupConfirmationDialog()
            }
            true
        }
    }

    private fun setupConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(confirm_delete))
        builder.setMessage(
            viewModel.setupConfirmationDialogMessage(this)
        )
        builder.setPositiveButton(
            getString(delete)
        ) { dialog, _ ->
            try {
                viewModel.deleteTasks(this)
                toast(successfully_deleted)
                dialog.cancel()
            } catch (e: Exception) {
                toast(somenthing_went_wrong)
                Log.e("exception", "${e.message}")
            }
        }
        builder.setNegativeButton(
            getString(cancel)
        ) { dialog, _ ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }
}