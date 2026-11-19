package com.dragzxc.lsmobile

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.net.Uri
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {

    private lateinit var status: TextView
    private val requestCapture = 7001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(25, 30, 25, 30)
            setBackgroundColor(Color.rgb(9, 9, 11))
        }

        fun label(text: String, size: Float, color: Int) =
            TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(color)
                gravity = Gravity.CENTER
            }

        fun button(text: String, action: () -> Unit) {
            layout.addView(Button(this).apply {
                this.text = text
                setOnClickListener { action() }
            }, LinearLayout.LayoutParams(-1, 55).apply {
                bottomMargin = 9
            })
        }

        layout.addView(
            label("LS MOBILE", 32f, Color.WHITE),
            LinearLayout.LayoutParams(-1, 65)
        )

        layout.addView(
            label("FRAME GENERATION PROTOTYPE", 13f, Color.LTGRAY),
            LinearLayout.LayoutParams(-1, 40)
        )

        status = label("Status: Ready", 15f, Color.rgb(167, 139, 250))
        layout.addView(status, LinearLayout.LayoutParams(-1, 65))

        button("Start screen capture") {
            val manager = getSystemService(MEDIA_PROJECTION_SERVICE)
                    as MediaProjectionManager
            startActivityForResult(
                manager.createScreenCaptureIntent(),
                requestCapture
            )
        }

        button("Stop screen capture") {
            stopService(Intent(this, CaptureService::class.java))
            status.text = "Status: Capture stopped"
        }

        button("Grant overlay permission") {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
            )
        }

        button("Start floating HUD") {
            if (Settings.canDrawOverlays(this)) {
                startService(Intent(this, OverlayService::class.java))
            } else {
                status.text = "Grant overlay permission first"
            }
        }

        button("Stop floating HUD") {
            stopService(Intent(this, OverlayService::class.java))
        }

        layout.addView(
            label(
                """
                VERSION 0.3.0

                CURRENT DEVELOPMENT

                • Screen capture foundation
                • Real frame acquisition
                • Frame rate measurement
                • Frame buffering preparation

                NEXT

                • Optical Flow
                • Motion interpolation
                • Frame Generation x2

                Generated frames are not implemented yet.
                """.trimIndent(),
                14f,
                Color.LTGRAY
            ),
            LinearLayout.LayoutParams(-1, 280)
        )

        setContentView(ScrollView(this).apply {
            addView(layout)
        })
    }

    @Deprecated("Activity result API compatibility")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == requestCapture) {
            if (resultCode == RESULT_OK && data != null) {
                val intent = Intent(this, CaptureService::class.java).apply {
                    putExtra(CaptureService.EXTRA_RESULT_CODE, resultCode)
                    putExtra(CaptureService.EXTRA_DATA, data)
                }

                if (android.os.Build.VERSION.SDK_INT >= 26) {
                    startForegroundService(intent)
                } else {
                    startService(intent)
                }

                status.text = "Status: Starting capture..."
            } else {
                status.text = "Status: Capture permission denied"
            }
        }
    }
}
