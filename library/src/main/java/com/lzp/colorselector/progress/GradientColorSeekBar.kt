package com.lzp.colorselector.progress

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.lzp.colorselector.R
import com.lzp.colorselector.listener.OnSelectColorChangedListener

/**
 * @author li.zhipeng
 *
 *      水平的渐变色的选色器进度条
 * */
class GradientColorSeekBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     *  所有的颜色
     * */
    var colorArray =
        intArrayOf(-0x10000, -0x100, -0xff0100, -0xff0001, -0xffff01, -0xff01, -0x10000)
        set(value) {
            field = value
            // 颜色发生变化，重新计算渐变
            linearGradient = null
            invalidate()
        }

    private lateinit var positions: FloatArray

    /**
     * 进度条的高度
     * */
    private var progressBarHeight = 20

    /**
     * 进度条的圆角
     * */
    private var radius = 0f

    /**
     * 拖动按钮的颜色
     * */
    private var thumbColor = Color.BLACK

    /**
     * 拖动按钮的大小
     * */
    private var thumbSize = 50

    /**
     * xDown
     * */
    private var xDown = 0f

    /**
     * 进度值[0 ,1]
     * */
    private var progress: Float = 0f

    private val rectF = RectF()

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }

    private var linearGradient: LinearGradient? = null

    var listener: OnSelectColorChangedListener? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.GradientColorSeekBar)
        thumbColor = typedArray.getColor(R.styleable.GradientColorSeekBar_thumbColor, Color.BLACK)
        thumbSize = typedArray.getColor(R.styleable.GradientColorSeekBar_thumbSize, 50)
        progressBarHeight =
            typedArray.getDimensionPixelSize(R.styleable.GradientColorSeekBar_progressHeight, 20)
        radius = typedArray.getDimension(R.styleable.GradientColorSeekBar_radius, 0f)
        typedArray.recycle()
    }

    private fun initLinearGradient() {
        positions = FloatArray(colorArray.size)
        for (i in colorArray.indices) {
            positions[i] = i * (1f / (colorArray.size - 1))
        }
        linearGradient = LinearGradient(
            0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(),
            colorArray, positions, Shader.TileMode.CLAMP
        )
    }

    private fun getLinearGradient(): LinearGradient {
        if (linearGradient == null) {
            initLinearGradient()
        }
        return linearGradient!!
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        if (mode == MeasureSpec.AT_MOST) {
            val newHeightSpec = MeasureSpec.makeMeasureSpec(
                thumbSize,
                MeasureSpec.getMode(heightMeasureSpec)
            )
            super.onMeasure(widthMeasureSpec, newHeightSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                xDown = event.rawX
                // 直接到手指按下的位置
                val lastProgress = progress
                progress = event.x / (width - thumbSize)
                if (lastProgress != progress) {
                    invokeCallback()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val lastProgress = progress
                val changeProgress = (event.rawX - xDown) / (width - thumbSize)
                progress += changeProgress
                if (progress < 0) {
                    progress = 0f
                }

                if (progress > 1) {
                    progress = 1f
                }
                if (lastProgress != progress) {
                    invokeCallback()
                }
                xDown = event.rawX
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    private fun invokeCallback() {
        listener?.onSelectColorChanged(this, calculateCurrentColor())
        invalidate()
    }

    /**
     * 计算应该当前的颜色值
     * */
    private fun calculateCurrentColor(): Int {
        // 找到两个颜色区间
        if (progress == 0f) {
            return colorArray.first()
        }

        if (progress >= 1f) {
            return colorArray.last()
        }

        var startColor = 0
        var endColor = 0
        var ratio = 0f
        var i = positions.size - 1
        while (i >= 0) {
            if (progress >= positions[i]) {
                startColor = colorArray[i]
                endColor = colorArray[i + 1]
                ratio = (progress - positions[i]) / (positions[i + 1] - positions[i])
                break
            }
            i--
        }
        val argbEvaluator = ArgbEvaluator()
        return argbEvaluator.evaluate(ratio, startColor, endColor) as Int
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制进度条
        paint.shader = getLinearGradient()
        rectF.set(
            (thumbSize / 2).toFloat(),
            (height / 2 - progressBarHeight / 2).toFloat(),
            width.toFloat() - thumbSize / 2,
            (height / 2 + progressBarHeight / 2).toFloat()
        )
        canvas.drawRoundRect(rectF, radius, radius, paint)

        // 画一个可拖动的按钮
        drawButtonThumb(canvas)

    }

    private fun drawButtonThumb(canvas: Canvas) {
        paint.reset()
        paint.color = thumbColor
        val center = (height / 2).toFloat()
        canvas.drawCircle(
            thumbSize / 2 + (width - thumbSize) * progress,
            center,
            (thumbSize / 2).toFloat(),
            paint
        )
    }

    fun getSelectColor(): Int {
        return calculateCurrentColor()
    }

    fun setProgress(progress: Float) {
        this.progress = progress
        invokeCallback()
    }

}