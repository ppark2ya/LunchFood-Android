package com.lunchfood.ui.main.view.calendar

import android.graphics.*
import android.text.style.LineBackgroundSpan


class AddTextToDates(private val placeName: String) : LineBackgroundSpan {

    override fun drawBackground(
        canvas: Canvas,
        paint: Paint,
        left: Int,
        right: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        lnum: Int
    ) {
        val mPaint = Paint()
        mPaint.color = Color.parseColor("#DA291C")
        val mRectF = RectF(left.toFloat(), (top + 55).toFloat(), right.toFloat(), (bottom + 33).toFloat())
        mPaint.strokeWidth = 10f
        mPaint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawRoundRect(mRectF, 10F, 30F, mPaint)
        //canvas.drawRect(mRectF, mPaint)

        mPaint.color = Color.WHITE;
        mPaint.style = Paint.Style.FILL;
        mPaint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        mPaint.textSize = 26F;
        canvas.drawText(
            placeName,
            (left + 10).toFloat(),
            (bottom + 28).toFloat(),
            mPaint
        )
    }
}