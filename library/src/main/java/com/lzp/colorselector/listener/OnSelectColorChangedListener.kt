package com.lzp.colorselector.listener

import android.view.View
import androidx.annotation.ColorInt

/**
 * @author li.zhipeng
 *
 *      回调选择的颜色的监听器
 * */
interface OnSelectColorChangedListener {

    fun onSelectColorChanged(view: View, @ColorInt color: Int)
}