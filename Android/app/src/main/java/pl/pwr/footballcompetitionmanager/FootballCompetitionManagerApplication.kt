package pl.pwr.footballcompetitionmanager

import android.app.Application
import timber.log.Timber

class FootballCompetitionManagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Timber initialization
        Timber.plant(Timber.DebugTree())
    }
}