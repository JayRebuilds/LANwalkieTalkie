package pro.devapp.walkietalkiek.service

import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Base64
import timber.log.Timber

class DiscoveryListener(private val chanelController: ChanelController) :
    NsdManager.DiscoveryListener {
    override fun onServiceFound(serviceInfo: NsdServiceInfo) {
        try {
            val ss = serviceInfo.serviceName.split(":").toTypedArray()
            val channelName = String(Base64.decode(ss[0], 0))
            Timber.i("onServiceFound: $channelName: $serviceInfo")
            chanelController.onServiceFound(serviceInfo)
        } catch (e: IllegalArgumentException) {
            Timber.w(e)
        }
    }

    override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
        Timber.i("Stop discovery failed: $errorCode")
    }

    override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
        Timber.i("Start discovery failed: $errorCode")
    }

    override fun onDiscoveryStarted(serviceType: String?) {
        Timber.i("Discovery started")
    }

    override fun onDiscoveryStopped(serviceType: String?) {
        Timber.i("Discovery stopped")
    }

    override fun onServiceLost(serviceInfo: NsdServiceInfo) {
        try {
            val ss =
                serviceInfo.serviceName.split(":")
                    .toTypedArray()
            val channelName = String(Base64.decode(ss[0], 0))
            Timber.i("onServiceLost: $channelName: $serviceInfo")
            chanelController.onServiceLost(serviceInfo)
        } catch (e: IllegalArgumentException) {
            Timber.w(e)
        }
    }
}