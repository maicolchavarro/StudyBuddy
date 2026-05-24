package com.studybuddy

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Clase de Aplicación principal que inicializa Hilt para la inyección de dependencias.
 * También configura WorkManager para usar la factoría de Hilt.
 */
@HiltAndroidApp
class StudyBuddyApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}