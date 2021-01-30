package co.teltech.base.shared.widgets

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import co.teltech.base.R
import co.teltech.base.shared.GlideApp
import co.teltech.base.shared.kotlin.setVisible


@Suppress("unused")
class LottieImageView : FrameLayout, RequestListener<Drawable> {

    private lateinit var mImageView: ImageView
    private lateinit var mLoadingAnimation: LottieAnimationView
    private var mImageUrl: String? = null
    private var mErrorDrawable: Drawable? = null
    private var mState = State.CREATED

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr) {
        initLottieImageView(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    )
            : super(context, attrs, defStyleAttr, defStyleRes) {
        initLottieImageView(attrs)
    }

    private fun initLottieImageView(attrs: AttributeSet? = null) {
        readAttributeSet(attrs)
        initImageView(attrs)
        initLottieAnimationView(attrs)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mImageUrl != null) loadImage()
    }

    private fun readAttributeSet(attrs: AttributeSet?) {
        attrs?.let {
            val typedArray =
                context.obtainStyledAttributes(it, R.styleable.LottieImageView, 0, 0)
            mImageUrl = typedArray.getString(R.styleable.LottieImageView_imageUrl)
            mErrorDrawable = typedArray.getDrawable(R.styleable.LottieImageView_errorImage)
            typedArray.recycle()
        }
    }

    private fun initImageView(attrs: AttributeSet?) {
        mImageView = ImageView(context, attrs)
        mImageView.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        addView(mImageView)
    }

    private fun initLottieAnimationView(attrs: AttributeSet?) {
        mLoadingAnimation = LottieAnimationView(context, attrs)
        mLoadingAnimation.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        mLoadingAnimation.visibility = View.GONE
        addView(mLoadingAnimation)
    }

    fun setImageUrl(url: String) {
        mImageUrl = url
        loadImage()
    }

    private fun loadImage() {
        setLoading(true)
        val req = GlideApp.with(this)
            .load(mImageUrl)
            .listener(this)
        if(mErrorDrawable != null)
            req.error(mErrorDrawable)
        req.into(mImageView)
    }

    fun getImageView(): ImageView = mImageView

    fun getLottieAnimationView(): LottieAnimationView = mLoadingAnimation

    fun getImageUrl() = mImageUrl

    fun getErrorPlaceholderImage() = mErrorDrawable

    fun setErrorDrawable(@DrawableRes imageDrawableRes: Int) {
        setErrorDrawable(ContextCompat.getDrawable(context, imageDrawableRes))
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setErrorDrawable(drawable: Drawable?) {
        mErrorDrawable = drawable
        if(mErrorDrawable == null) {
            mImageView.setImageDrawable(drawable)
            return
        }
        if(mState == State.ERROR)
            mImageView.setImageDrawable(drawable)
    }

    private fun setLoading(state: Boolean) {
        if(state)
            mState = State.LOADING
        mImageView.setVisible(!state)
        mLoadingAnimation.apply {
            setVisible(state)
            if(state) playAnimation() else cancelAnimation()
        }
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        setLoading(false)
        mState = State.ERROR
        return false // returns false because we want Glide to show error placeholder
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
        return false // returns false because we want Glide to show loaded image
    }

    private enum class State {
        CREATED,
        LOADING,
        LOADED,
        ERROR
    }

}