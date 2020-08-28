package pro.devapp.walkietalkiek

import android.app.Application
import pro.devapp.walkietalkiek.controllers.ChanelController
import pro.devapp.walkietalkiek.controllers.NotificationController
import pro.devapp.walkietalkiek.data.DeviceInfoRepository
import timber.log.Timber
import timber.log.Timber.DebugTree

class WalkieTalkieApp : Application() {
    lateinit var chanelController: ChanelController
    lateinit var deviceInfoRepository: DeviceInfoRepository
    lateinit var notificationController: NotificationController

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            //Timber.plant(CrashReportingTree())
        }
        deviceInfoRepository = DeviceInfoRepository(applicationContext)

        notificationController =
            NotificationController(
                applicationContext
            )
        notificationController.createNotificationChanel()

        chanelController = ChanelController(
            applicationContext,
            deviceInfoRepository
        )
    }
}