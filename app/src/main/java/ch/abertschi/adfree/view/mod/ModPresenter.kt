package ch.abertschi.adfree.view.mod

import android.content.Context
import android.content.Intent
import ch.abertschi.adfree.AdFreeApplication
import ch.abertschi.adfree.ListenerStatus
import ch.abertschi.adfree.NotificationStatusManager
import ch.abertschi.adfree.NotificationStatusObserver
import ch.abertschi.adfree.model.AdDetectableFactory
import ch.abertschi.adfree.model.PreferencesFactory
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.runOnUiThread
import android.os.AsyncTask
import android.app.AlarmManager
import android.app.PendingIntent




class ModPresenter(val view: ModActivity, val prefs: PreferencesFactory) : AnkoLogger,
        NotificationStatusObserver {

    private lateinit var context: Context
    private lateinit var notificationStatusManager: NotificationStatusManager
    private lateinit var detectorFactory: AdDetectableFactory


    override fun onStatusChanged(status: ListenerStatus) {
        context.runOnUiThread {
            info { "notification listener changed status: $status" }
            if (status == ListenerStatus.CONNECTED) {
                view.showNotifiationListenerConnected()
            } else {
                view.showNotificationListenerDisconnected()
            }
        }
    }

    fun onCreate(context: Context) {
        info { "new presenter" }
        detectorFactory = (context.applicationContext as AdFreeApplication).adDetectors
        notificationStatusManager = (context.applicationContext as AdFreeApplication).notificationStatus
        this.context = context

        view.setEnableToggle(detectorFactory.isAdfreeEnabled())
        view.setNotificationEnabled(prefs.isAlwaysOnNotificationEnabled())
        view.setDelayValue(prefs.getDelaySeconds())

        notificationStatusManager.addObserver(this)
        notificationStatusManager.restartNotificationListener() // always restart on launch

        showDetectorCount()

        AsyncTask.execute {
            onStatusChanged(notificationStatusManager.getStatus())
        }
    }


    private fun showDetectorCount() {
        view.showDetectorCount(detectorFactory.getEnabledDetectors().size,
                detectorFactory.getVisibleDetectors().size)
    }

    fun onToggleAlwaysOnChanged() {
        val newVal = !prefs.isAlwaysOnNotificationEnabled()
        prefs.setAlwaysOnNotification(newVal)
        view.setNotificationEnabled(newVal)
        notificationStatusManager.restartNotificationListener()
        if (!newVal) {
            (view.applicationContext as AdFreeApplication)
                    .notificationChannel.hideAlwaysOnNotification()
        }
    }

    fun onDelayUnmute() {
        view.showDelayUnmute()
    }

    fun onDelayChanged(delay: Int) {
        prefs.setDelaySeconds(delay)
        view.setDelayValue(delay)
    }


    fun onEnableToggleChanged() {
        val newVal = !prefs.isBlockingEnabled()
        detectorFactory.setAdfreeEnabled(newVal)
        view.setEnableToggle(newVal)
        if (newVal) {
            view.showPowerEnabled()
        }
    }

    fun onLaunchActiveDetectorsView() {
        val myIntent = Intent(this.context, ActiveDetectorActivity::class.java)
        this.context.startActivity(myIntent)
    }

    fun onLaunchNotificationListenerSystemSettings() {
        context.startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
    }

    fun onResume() {
        showDetectorCount()
    }
}