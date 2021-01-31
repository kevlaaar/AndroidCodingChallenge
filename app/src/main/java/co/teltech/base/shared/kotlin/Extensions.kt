@file:Suppress("unused")

package co.teltech.base.shared.kotlin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.TransitionDrawable
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import co.teltech.base.BuildConfig.BASE_URL
import co.teltech.base.R
import co.teltech.base.shared.base.Resource
import com.google.android.material.snackbar.Snackbar
import okhttp3.Request
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


fun URL.toBitmap(): Bitmap? {
    return try {
        BitmapFactory.decodeStream(openStream())
    } catch (e: IOException) {
        null
    }
}

fun View.setBackgroundColorWithoutChangingShape(colorArg: String, alpha: Int){
    val color = ColorUtils.setAlphaComponent(Color.parseColor(colorArg), alpha)
    background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}


fun View.setCircleGradientColor(startColor: String, endColor: String) {
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(
            Color.parseColor(startColor),
            Color.parseColor(endColor)
        )
    )
    gradientDrawable.shape = GradientDrawable.OVAL
    //Set Gradient
    background = gradientDrawable
}

fun <T> LiveData<Resource<T>>.observeWithLoading(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    observer: Observer<Resource<T>>
) {
    val loading = AlertDialog.Builder(context)
        .setView(R.layout.popup_loading)
        .setCancelable(false)
        .create()
    loading.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    observe(lifecycleOwner, { t ->
        if (t is Resource.Loading) loading.show()
        else loading.cancel()
        observer.onChanged(t)
    })
}

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE
    else View.GONE
}

fun View.showError(mError: String?) {
    var error = mError
    val defaultError = context?.getString(R.string.default_error)
    error = if (error.isNullOrEmpty() && !defaultError.isNullOrEmpty()) defaultError
    else ""
    Snackbar.make(
        this,
        error,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun Context.showInfoDialog(messageResId: Int) {
    AlertDialog.Builder(this)
        .setMessage(messageResId)
        .setPositiveButton(
            R.string.ok,
            null
        )
        .show()
}

fun Context.showConfirmationDialog(titleResId: Int, messageResId: Int, action: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(titleResId)
        .setMessage(messageResId)
        .setPositiveButton(
            R.string.yes
        ) { _, _ ->
            action.invoke()
        }
        .setNegativeButton(
            R.string.no, null
        )
        .show()
}

fun View.showWithAnimation() {
    if (visibility != View.VISIBLE)
        this.apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(150)
                .setListener(null)
        }
}

fun View.hideWithAnimation() {
    if (this.visibility == View.VISIBLE) {
        this.animate()
            .alpha(0f)
            .setDuration(150)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    this@hideWithAnimation.visibility = View.GONE
                }
            })
    }
}

fun View.changeBackgroundAnimated(
    startColor: String,
    endColor: String,
    radius: Float,
    orientation: GradientDrawable.Orientation,
    type: Int
) {
    val gradientDrawable = GradientDrawable(
        orientation,
        intArrayOf(
            Color.parseColor(startColor),
            Color.parseColor(endColor)
        )
    )
    gradientDrawable.gradientType = type
    gradientDrawable.shape = GradientDrawable.OVAL
    gradientDrawable.gradientRadius = radius
    val colorDrawables = arrayOf(background, gradientDrawable)
    val transitionDrawable = TransitionDrawable(colorDrawables)
    background = transitionDrawable
    transitionDrawable.startTransition(1000)
}

fun View.changeBackgroundAnimated(drawableId: Int) {
    val colorDrawables = arrayOf(
        background,
        ContextCompat.getDrawable(context, drawableId)
    )
    val transitionDrawable = TransitionDrawable(colorDrawables)
    background = transitionDrawable
    transitionDrawable.startTransition(200)
}

fun Request.signWithToken(accessToken: String?) =
    if (this.url.toString().contains(BASE_URL) && !accessToken.isNullOrEmpty()) {
        newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
    } else this

fun PopupWindow.setElementOnClick(id: Int, function: () -> Unit) {
    contentView.findViewById<View>(id).setOnClickListener {
        function()
    }
}

fun createID(): Int {
    val now = Date()
    return Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.US).format(now))
}

fun PopupWindow.dimBackground() {
    val container = this.contentView.rootView
    val context = this.contentView.context
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val lp = container.layoutParams as WindowManager.LayoutParams
    lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
    lp.dimAmount = 0.5f
    wm.updateViewLayout(container, lp)
}
