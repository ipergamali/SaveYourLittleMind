package ioannapergamali.savejoannepink.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.edit

/**
 * Απλός helper που εντοπίζει τον καθαρισμό δεδομένων της εφαρμογής χωρίς να
 * βασίζεται στο περιορισμένο broadcast `ACTION_PACKAGE_DATA_CLEARED` σε Android 14+.
 * Σε παλαιότερα API (μέχρι Android 13) εξακολουθεί να γίνεται registration, όμως
 * προστατεύεται από τον έλεγχο της έκδοσης ώστε να αποφεύγεται το σχετικό warning.
 */
object PackageDataClearHandler {

    private const val PREFS_NAME = "package_data_clear_handler"
    private const val KEY_SENTINEL = "sentinel"

    @Volatile
    private var onDataCleared: (() -> Unit)? = null

    private val packageDataClearedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val targetPackage = intent.data?.schemeSpecificPart ?: return
            if (targetPackage == context.packageName) {
                handleDataCleared(context.applicationContext)
            }
        }
    }

    fun initialize(context: Context, onAppDataCleared: () -> Unit) {
        val appContext = context.applicationContext
        onDataCleared = onAppDataCleared

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val filter = IntentFilter(Intent.ACTION_PACKAGE_DATA_CLEARED).apply {
                addDataScheme("package")
            }
            ContextCompat.registerReceiver(
                appContext,
                packageDataClearedReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }

        if (!hasSentinel(appContext)) {
            handleDataCleared(appContext)
        }
    }

    private fun hasSentinel(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_SENTINEL, false)
    }

    private fun handleDataCleared(context: Context) {
        onDataCleared?.invoke()
        markSentinel(context)
    }

    private fun markSentinel(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_SENTINEL, true) }
    }
}
