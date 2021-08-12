package com.udacity

import android.animation.*
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    var fileSelected = false

    private val valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                fillButton()
            }
            ButtonState.Completed -> {
                speedUpAnimation()
            }
            ButtonState.Clicked -> {
                resetValues()
            }
        }
    }

    private val initialColor: Int by lazy { context.getColor(R.color.colorPrimary) }
    private val fillColor: Int by lazy { context.getColor(R.color.colorPrimaryDark) }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }
    private val rect: AnimRectF by lazy {
        AnimRectF(height.toFloat())
    }
    private var text = context.getString(R.string.download)
    private lateinit var animator: ObjectAnimator

    // Based on Dan Lew solution to center text vertically in a canvas
    // https://blog.danlew.net/2013/10/03/centering_single_line_text_in_a_canvas/
    private val textPaint = TextPaint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
    }
    private val textY: Float by lazy {
        val textHeight = textPaint.descent() - textPaint.ascent()
        (textHeight / 2) - textPaint.descent()
    }

    init {
        isClickable = true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(initialColor)
        paint.color = fillColor
        canvas.drawRect(rect, paint)
        canvas.drawText(text, (width / 2).toFloat(), (height / 2).toFloat() + textY, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        //Call onClickListeners before the rest
        val ret = super.performClick()

        if (fileSelected) {
            text = context.getString(R.string.downloading)
            fillButton()
        }

        return ret
    }

    private fun fillButton() {
        animator = ObjectAnimator.ofFloat(rect, "right", width.toFloat())
        animator.duration = 20000
        animator.addUpdateListener { postInvalidate() }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                isEnabled = true
                buttonState = ButtonState.Clicked
            }
        })
        animator.start()
    }

    private fun speedUpAnimation() {
        val percentage = animator.currentPlayTime.toFloat() / animator.duration.toFloat()
        animator.duration = 200
        animator.setCurrentFraction(percentage)
    }

    private fun resetValues() {
        text = context.getString(R.string.download)
        rect.setRight(0f)
        postInvalidate()
    }

    class AnimRectF(bottom: Float) : RectF(0f, 0f, 0f, bottom) {
        fun getRight(): Float {
            return right
        }

        fun setRight(right: Float) {
            this.right = right
        }
    }
}