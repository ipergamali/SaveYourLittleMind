package ioannapergamali.savejoannepink

import android.app.Application
import android.content.Context
import android.util.Log
import ioannapergamali.savejoannepink.data.PackageDataClearHandler

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        PackageDataClearHandler.initialize(this) {
            Log.i("App", "Τα δεδομένα της εφαρμογής καθαρίστηκαν. Γίνεται επανεκκίνηση προεπιλογών.")
        }
    }

    companion object {
        private var instance: App? = null

        val context: Context
            get() = instance!!.applicationContext
    }
}
