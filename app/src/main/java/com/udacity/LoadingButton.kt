package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
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

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val initialColor: Int by lazy { context.getColor(R.color.colorPrimary) }
    private val fillColor: Int by lazy { context.getColor(R.color.colorPrimaryDark) }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }


    // Based on Dan Lew solution to center text vertically in a canvas
    // https://blog.danlew.net/2013/10/03/centering_single_line_text_in_a_canvas/
    private val textPaint = TextPaint().apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
    }
    private val textY: Float by lazy {
        val textHeight = textPaint . descent () - textPaint.ascent()
        (textHeight / 2) - textPaint.descent()
    }

    init {
        isClickable = true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.WHITE
        canvas.drawColor(initialColor)
        canvas.drawText("Download", (width / 2).toFloat(), (height / 2).toFloat() + textY, textPaint)
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
            //TODO run animation
        }

        return ret
    }
}