package co.teltech.base.shared.widgets

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.children
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import co.teltech.base.R
import co.teltech.base.shared.GlideApp
import co.teltech.base.shared.kotlin.setVisible

//
// Example:
//
// <nv.android.shared.widgets.ProgressImageView
//         ...
//         app:imageUrl="https://..."
//         app:errorImage="@drawable/...">
//
//      <ProgressBar
//              android:layout_width="wrap_content"
//              android:layout_height="wrap_content"
//              android:layout_gravity="center"/>
//
// </nv.android.shared.widgets.ProgressImageView>
//

@Suppress("unused")
class ProgressImageView : FrameLayout, RequestListener<Drawable> {

    private lateinit var mImageView: ImageView
    private var mLoadingPlaceholder: View? = null
    private var mImageUrl: String? = null
    private var mErrorDrawable: Drawable? = null
    private var mState =
        State.CREATED

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initProgressImageView(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    )
            : super(context, attrs, defStyleAttr, defStyleRes) {
        initProgressImageView(attrs)
    }

    private fun initProgressImageView(attrs: AttributeSet? = null) {
        readAttributeSet(attrs)
        initImageView(attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mLoadingPlaceholder = if (childCount == 2) children.last() else null
        if (mImageUrl != null) loadImage()
    }

    private fun initImageView(attrs: AttributeSet?) {
        mImageView = ImageView(context, attrs)
        mImageView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(mImageView)
    }

    private fun readAttributeSet(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.ProgressImageView, 0, 0)
            mImageUrl = typedArray.getString(R.styleable.ProgressImageView_imageUrl)
            mErrorDrawable = typedArray.getDrawable(R.styleable.ProgressImageView_errorImage)
            typedArray.recycle()
        }
    }

    private fun setLoading(state: Boolean) {
        if (state)
            mState = State.LOADING
        mImageView.setVisible(!state)
        mLoadingPlaceholder?.setVisible(state)
    }

    private fun loadImage() {
        GlideApp.with(this)
            .load(mImageUrl)
            .apply { mErrorDrawable?.let { error(it) } }
            .listener(this)
            .into(mImageView)
        setLoading(true)
    }

    fun setImageUrl(url: String) {
        mImageUrl = url
        loadImage()
    }

    fun getImageView(): ImageView = mImageView

    fun getPlaceholderView(): View? = mLoadingPlaceholder

    fun getImageUrl() = mImageUrl

    fun getErrorDrawable() = mErrorDrawable

    fun setErrorDrawable(@DrawableRes imageDrawableRes: Int) {
        setErrorDrawable(ContextCompat.getDrawable(context, (imageDrawableRes)))
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setErrorDrawable(drawable: Drawable?) {
        mErrorDrawable = drawable
        if (mErrorDrawable == null) {
            mImageView.setImageDrawable(drawable)
            return
        }
        if (mState == State.ERROR)
            mImageView.setImageDrawable(drawable)
    }

    override fun addView(child: View?) {
        if (childCount > 1)
            throw IllegalStateException("ProgressImageView can host only one direct child")
        super.addView(child)
    }

    override fun addView(child: View?, index: Int) {
        if (childCount > 1)
            throw IllegalStateException("ProgressImageView can host only one direct child")
        super.addView(child, index)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (childCount > 1)
            throw IllegalStateException("ProgressImageView can host only one direct child")
        super.addView(child, params)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (childCount > 1)
            throw IllegalStateException("ProgressImageView can host only one direct child")
        super.addView(child, index, params)
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        setLoading(false)
        mState = State.ERROR
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        setLoading(false)
        mState = State.LOADED
        return false
    }

    private enum class State {
        CREATED,
        LOADING,
        LOADED,
        ERROR
    }
}