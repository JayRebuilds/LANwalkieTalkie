package pro.devapp.walkietalkiek.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import pro.devapp.walkietalkiek.R
import pro.devapp.walkietalkiek.VoiceRecorder
import pro.devapp.walkietalkiek.WalkieTalkieApp
import pro.devapp.walkietalkiek.service.WalkieService
import pro.devapp.walkietalkiek.ui.widgets.BottomButtons
import pro.devapp.walkietalkiek.utils.permission.Permission
import pro.devapp.walkietalkiek.utils.permission.UtilPermission
import java.nio.ByteBuffer
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var voiceRecorder: VoiceRecorder

    private val utilPermission = UtilPermission()
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceIntent = Intent(this, WalkieService::class.java)
        startService(serviceIntent)
    }

    override fun onStart() {
        super.onStart()
        val serviceIntent = Intent(this, WalkieService::class.java)
        utilPermission.checkOrRequestPermissions(this, object : UtilPermission.PermissionCallback(
            arrayOf(Permission.AUDIO_RECORD)
        ) {
            override fun onSuccessGrantedAll() {
                startVoiceRecorder()
            }
        })
        (application as WalkieTalkieApp).connectedDevicesRepository.getConnectedDevicesList()
            .observeOn(AndroidSchedulers.mainThread()).subscribe { list ->
                clientsList.setItems(list)
                clientsView.text = list.filter { it.isConnected }.size.toString()
            }
            .also {
                compositeDisposable.add(it)
            }

        (application as WalkieTalkieApp).chanelController.subjectAudioData
            .observeOn(AndroidSchedulers.mainThread()).subscribe {
                audioView.text = it.size.toString()
            }
            .also {
                compositeDisposable.add(it)
            }

        bottomButtons.buttonsClickSubject.subscribe {
            when (it) {
                BottomButtons.Buttons.MESSAGES -> {
                    (application as WalkieTalkieApp).chanelController.sendMessage(ByteBuffer.wrap("test ${Date().seconds}".toByteArray()))
                }
                BottomButtons.Buttons.SETTINGS -> {
                }
                BottomButtons.Buttons.EXIT -> {
                    stopService(serviceIntent)
                    finish()
                }
            }
        }
            .also {
                compositeDisposable.add(it)
            }

        val ipAddress = (application as WalkieTalkieApp).deviceInfoRepository.getCurrentIp()
        ip.text = ipAddress
    }

    override fun onStop() {
        super.onStop()
        voiceRecorder.destroy()
        compositeDisposable.dispose()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceRecorder.destroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        utilPermission.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startVoiceRecorder() {
        voiceRecorder = VoiceRecorder() {
            (application as WalkieTalkieApp).chanelController.sendMessage(ByteBuffer.wrap(it))
        }
        voiceRecorder.create()

        val disposable = ppt.getPushActionSubject().subscribe {
            if (it) {
                voiceRecorder.startRecord()
            } else {
                voiceRecorder.stopRecord()
            }
        }
        compositeDisposable.add(disposable)
    }
}