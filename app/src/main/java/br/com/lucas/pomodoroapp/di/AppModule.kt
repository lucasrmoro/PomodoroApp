package br.com.lucas.pomodoroapp.di

import android.content.Context
import androidx.room.Room
import br.com.lucas.pomodoroapp.database.AppDataBase
import br.com.lucas.pomodoroapp.database.TaskDao
import br.com.lucas.pomodoroapp.helpers.PreferencesHelper
import br.com.lucas.pomodoroapp.ui.listTaskScreen.ListTaskViewStateManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Singleton
    @Provides
    fun provideAppContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun provideTaskDao(appDataBase: AppDataBase): TaskDao = appDataBase.taskDao()

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, "pomodoro-db")
            .build()

    @Singleton
    @Provides
    fun providePreferencesHelper(context: Context): PreferencesHelper = PreferencesHelper(context)

    @Singleton
    @Provides
    fun provideListTaskViewStateManager(): ListTaskViewStateManager = ListTaskViewStateManager()
}