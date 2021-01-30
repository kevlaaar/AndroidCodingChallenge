package co.teltech.base.ui.popup

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import co.teltech.base.R
import co.teltech.base.shared.kotlin.dimBackground
import co.teltech.base.shared.kotlin.setElementOnClick

@SuppressLint("InflateParams")
class DebugPopup(
    private val activity: Activity,
    private val layout: View,
    private val logoutCallback: () -> Unit,
    private val resetCallback: () -> Unit
) : PopupWindow() {
    private val inflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    init {
        contentView = inflater.inflate(R.layout.popup_debug, null)
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        elevation = 36F
        isFocusable = true
        animationStyle = R.style.PopupAnimation

        setElementOnClick(R.id.debug_log_out) {
            dismiss()
            logoutCallback()
        }

        setElementOnClick(R.id.debug_reset) {
            dismiss()
            resetCallback()
        }
    }

    @Suppress("DEPRECATION")
    fun show() {
        showAtLocation(layout, Gravity.CENTER, 0, 0)
        val v = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else v.vibrate(100)
        dimBackground()
    }
}