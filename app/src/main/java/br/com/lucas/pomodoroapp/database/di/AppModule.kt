package br.com.lucas.pomodoroapp.database.di

import android.content.Context
import androidx.room.Room
import br.com.lucas.pomodoroapp.database.AppDataBase
import br.com.lucas.pomodoroapp.database.TaskDao
import br.com.lucas.pomodoroapp.helpers.AlarmManagerHelper
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
    fun provideTaskDao(appDataBase: AppDataBase): TaskDao = appDataBase.taskDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, "pomodoro-db")
            .build()

    @Singleton
    @Provides
    fun provideAlarmManagerHelper(@ApplicationContext context: Context): AlarmManagerHelper =
        AlarmManagerHelper(context)
}