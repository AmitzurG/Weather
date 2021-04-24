package com.example.weather.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.weather.R
import kotlin.math.min

class SunriseSunsetGraphView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    constructor(context: Context): this(context, null)

    private val paint = Paint()
    private val sunColor = Color.YELLOW

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val w = width - (paddingStart + paddingEnd)
        val h = height - (paddingTop + paddingBottom)
        val radius = min(w / 4, h / 4) / 2f
        val x = paddingStart + w / 2f
        val y = paddingTop + h / 2f

        paint.color = sunColor
        canvas?.drawCircle(x, y, radius, paint) // sun

        paint.color = Color.DKGRAY
        paint.textSize = context.resources.getDimension(R.dimen.sunriseSunsetViewTextSize)
        canvas?.drawText("* need to implement sunrise/sunset graph ...", x - 120, y + 110f, paint)
    }
}