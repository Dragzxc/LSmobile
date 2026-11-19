package com.dragzxc.lsmobile

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlay: TextView? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (overlay != null) return START_NOT_STICKY

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val panel = TextView(this).apply {
            text = "LS MOBILE  •  HUD\nOverlay is active\nTap to close"
            textSize = 13f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(22, 16, 22, 16)
            background = GradientDrawable().apply {
                setColor(Color.rgb(28, 25, 40))
                cornerRadius = 22f
                setStroke(2, Color.rgb(167, 139, 250))
            }
            setOnClickListener { stopSelf() }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 24
            y = 180
        }

        try {
            windowManager?.addView(panel, params)
            overlay = panel
        } catch (_: Exception) {
            stopSelf()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        overlay?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {
            }
        }
        overlay = null
        windowManager = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
