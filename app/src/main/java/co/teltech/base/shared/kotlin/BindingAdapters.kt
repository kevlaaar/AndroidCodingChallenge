package co.teltech.base.shared.kotlin

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import co.teltech.base.shared.GlideApp

/**
 * Example:
 *
 * <ImageView
 *      ...
 *      app:glideSrc="@{viewModel.imageUrl}"
 *      app:glidePlaceholderSrc="@{@drawable/image_placeholder}"
 *      app:glideErrorSrc="@{@drawable/image_error}"/>
 *
 */
@Suppress("unused")
@BindingAdapter(value = ["glideSrc", "glidePlaceholderSrc", "glideErrorSrc"], requireAll = false)
fun ImageView.bindImage(url: String?, placeholder: Drawable?, error: Drawable?) {
    if (url == null) {
        setImageDrawable(placeholder)
        return
    }
    val req = GlideApp.with(context).load(url)
    if (placeholder != null)
        req.placeholder(placeholder)
    if (error != null)
        req.error(error)
    req.into(this)
}

