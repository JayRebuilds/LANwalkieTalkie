package pro.devapp.walkietalkiek.ui

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import pro.devapp.utils.permission.Permission
import pro.devapp.utils.permission.UtilPermission
import pro.devapp.walkietalkiek.R
import pro.devapp.walkietalkiek.WalkieTalkieApp
import pro.devapp.walkietalkiek.service.WalkieService
import pro.devapp.walkietalkiek.voice.VoiceRecorder
import java.nio.ByteBuffer
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var voiceRecorder: VoiceRecorder

    private val utilPermission = UtilPermission()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serviceIntent = Intent(this, WalkieService::class.java)
        startService(serviceIntent)

        exit.setOnClickListener {
            stopService(serviceIntent)
            finish()
        }

        send.setOnClickListener {
            (application as WalkieTalkieApp).chanelController.sendMessage(ByteBuffer.wrap("test ${Date().seconds}".toByteArray()))
        }

        utilPermission.checkOrRequestPermissions(this, object : UtilPermission.PermissionCallback(
            arrayOf(Permission.AUDIO_RECORD)
        ) {
            override fun onSuccessGrantedAll() {
                startVoiceRecorder()
            }
        })

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

        talk.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    voiceRecorder.startRecord()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    voiceRecorder.stopRecord()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }
}