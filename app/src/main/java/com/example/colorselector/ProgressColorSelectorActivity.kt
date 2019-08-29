package com.example.colorselector

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity
import com.lzp.colorselector.listener.OnSelectColorChangedListener

import kotlinx.android.synthetic.main.activity_progress_color_selector.*

class ProgressColorSelectorActivity : AppCompatActivity() {

    private val hsv = FloatArray(3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_color_selector)

        val defaultColor = gradient_color_progressBar.getSelectColor()
        Color.colorToHSV(defaultColor, hsv)
        updateColor(h = defaultColor)
        calculateSaturation(defaultColor)
        calculateBrightness(defaultColor)

        saturation_color_progressBar.setProgress(hsv[1])
        brightness_color_progressBar.setProgress(hsv[2])

        gradient_color_progressBar.listener = object : OnSelectColorChangedListener {
            override fun onSelectColorChanged(view: View, color: Int) {
                Log.e("lzp", "onSelectColorChanged")
                updateColor(h = color)
                calculateSaturation(color)
                calculateBrightness(color)
            }

        }

        saturation_color_progressBar.listener = object : OnSelectColorChangedListener {
            override fun onSelectColorChanged(view: View, color: Int) {
                updateColor(s = color)
            }

        }

        brightness_color_progressBar.listener = object : OnSelectColorChangedListener {
            override fun onSelectColorChanged(view: View, color: Int) {
                updateColor(v = color)
            }

        }
    }

    private fun updateColor(h: Int? = null, s: Int? = null, v: Int? = null) {
        val temp = FloatArray(3)
        h?.let {
            Color.colorToHSV(it, temp)
            hsv[0] = temp[0]
        }
        s?.let {
            Color.colorToHSV(it, temp)
            hsv[1] = temp[1]
        }
        v?.let {
            Color.colorToHSV(it, temp)
            hsv[2] = temp[2]
        }
        select_color.setBackgroundColor(Color.HSVToColor(hsv))
    }

    /***
     *  计算颜色的
     * */
    private fun calculateSaturation(color: Int) {
        // 把argb颜色转化为HSV颜色
        val temp = FloatArray(3)
        Color.colorToHSV(color, temp)
        // 取出其中的纯度
        temp[1] = 0f
        val startColor = Color.HSVToColor(temp)
        temp[1] = 1f
        val endColor = Color.HSVToColor(temp)
        saturation_color_progressBar.colorArray = intArrayOf(
            startColor,
            endColor
        )

    }

    private fun calculateBrightness(color: Int) {
        // 把argb颜色转化为HSV颜色
        val temp = FloatArray(3)
        Color.colorToHSV(color, temp)
        // 取出其中的纯度
        temp[2] = 0f
        val startColor = Color.HSVToColor(temp)
        temp[2] = 1f
        val endColor = Color.HSVToColor(temp)
        brightness_color_progressBar.colorArray = intArrayOf(
            startColor,
            endColor
        )

    }

}
