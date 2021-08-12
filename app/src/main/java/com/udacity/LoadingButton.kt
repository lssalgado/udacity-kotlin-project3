package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextPaint
import android.util.AttributeSet
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
    var sweepAngle = 0f
    private val circleRect: RectF by lazy {
        RectF(
            (width - 300).toFloat(),
            (height / 2 - 50).toFloat(),
            (width - 200).toFloat(),
            (height / 2 + 50).toFloat()
        )
    }

    private var text = context.getString(R.string.download)

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
        paint.color = Color.BLUE
        canvas.drawRect(circleRect, paint)
        paint.color = Color.BLACK
        canvas.drawArc(circleRect, 0f, sweepAngle, true, paint)
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
        text = context.getString(R.string.downloading)
        valueAnimator.setFloatValues(0f, 1f)
        valueAnimator.duration = 20000
        valueAnimator.addUpdateListener {
            sweepAngle = 360f * it.animatedFraction
            rect.setRight(width * it.animatedFraction)
            postInvalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                buttonState = ButtonState.Clicked
            }
        })
        valueAnimator.start()
    }

    private fun speedUpAnimation() {
        //TODO use download % instead of hardcoded value
        val percentage = valueAnimator.currentPlayTime.toFloat() / valueAnimator.duration.toFloat()
        valueAnimator.duration = 200
        valueAnimator.setCurrentFraction(percentage)
    }

    private fun resetValues() {
        text = context.getString(R.string.download)
        rect.setRight(0f)
        postInvalidate()
        sweepAngle = 0f
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