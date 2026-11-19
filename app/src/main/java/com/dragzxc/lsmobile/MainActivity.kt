package com.dragzxc.lsmobile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    private val backgroundColor = Color.rgb(9, 9, 11)
    private val accent = Color.rgb(167, 139, 250)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 32, 28, 32)
            setBackgroundColor(backgroundColor)
        }

        fun label(text: String, size: Float, color: Int): TextView {
            return TextView(this).apply {
                this.text = text
                textSize = size
                setTextColor(color)
                gravity = Gravity.CENTER
            }
        }

        val title = label("LS MOBILE", 32f, Color.WHITE)
        val subtitle = label("Android performance companion", 14f, Color.LTGRAY)
        val status = label("Overlay permission: checking...", 14f, accent)

        val permissionButton = Button(this).apply {
            text = "Grant overlay permission"
            setOnClickListener {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                )
            }
        }

        val startButton = Button(this).apply {
            text = "Start floating HUD"
            setOnClickListener {
                if (Settings.canDrawOverlays(this@MainActivity)) {
                    startService(Intent(this@MainActivity, OverlayService::class.java))
                } else {
                    status.text = "Please grant overlay permission first."
                }
            }
        }

        val stopButton = Button(this).apply {
            text = "Stop floating HUD"
            setOnClickListener {
                stopService(Intent(this@MainActivity, OverlayService::class.java))
            }
        }

        val info = label(
            "Version 0.1.0\n\n" +
            "Current features:\n" +
            "• Floating overlay HUD\n" +
            "• Overlay permission management\n\n" +
            "Frame generation and image upscaling are not implemented yet.",
            14f,
            Color.LTGRAY
        )

        layout.addView(title, params(LinearLayout.LayoutParams.MATCH_PARENT, 65))
        layout.addView(subtitle, params(LinearLayout.LayoutParams.MATCH_PARENT, 45))
        layout.addView(status, params(LinearLayout.LayoutParams.MATCH_PARENT, 55))
        layout.addView(permissionButton, params(LinearLayout.LayoutParams.MATCH_PARENT, 58))
        layout.addView(startButton, params(LinearLayout.LayoutParams.MATCH_PARENT, 58))
        layout.addView(stopButton, params(LinearLayout.LayoutParams.MATCH_PARENT, 58))
        layout.addView(info, params(LinearLayout.LayoutParams.MATCH_PARENT, 210))

        setContentView(layout)
    }

    override fun onResume() {
        super.onResume()
        // Permission status is checked when the screen is displayed.
    }

    private fun params(width: Int, height: Int) =
        LinearLayout.LayoutParams(width, height).apply {
            bottomMargin = 8
        }
}
