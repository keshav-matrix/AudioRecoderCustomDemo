package com.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import java.util.*

class VisualizerView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var amplitudes // amplitudes for line lengths
            : MutableList<Float>? = null
    var dataWidth // width of this View
            = 0
    var datHeight // height of this View
            = 0
    private val linePaint // specifies line drawing characteristics
            : Paint

    // called when the dimensions of the View change
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        dataWidth = w // new width of this View
        datHeight = h // new height of this View
        amplitudes = ArrayList(dataWidth / LINE_WIDTH)

    }

    // clear all amplitudes to prepare for a new visualization
    fun clear() {
        amplitudes!!.clear()

    }

    // add the given amplitude to the amplitudes ArrayList
    fun addAmplitude(amplitude: Float) {
        amplitudes!!.add(amplitude) // add newest to the amplitudes ArrayList

        // if the power lines completely fill the VisualizerView
        if (amplitudes!!.size * LINE_WIDTH >= dataWidth) {
            amplitudes!!.removeAt(0) // remove oldest power value
        }
    }

    // draw the visualizer with scaled lines representing the amplitudes
    public override fun onDraw(canvas: Canvas) {
        val middle = datHeight / 2 // get the middle of the View
        var curX = 0f // start curX at zero

        // for each item in the amplitudes ArrayList
        for (power in amplitudes!!) {
            val scaledHeight = power / LINE_SCALE // scale the power
            curX += LINE_WIDTH.toFloat() // increase X by LINE_WIDTH

            // draw a line representing this item in the amplitudes ArrayList
            canvas.drawLine(
                curX, middle + scaledHeight / 2, curX, middle
                        - scaledHeight / 2, linePaint
            )
        }
    }

    companion object {
        private const val LINE_WIDTH = 1 // width of visualizer lines
        private const val LINE_SCALE = 75 // scales visualizer lines
    }

    // constructor
    init {
        linePaint = Paint() // create Paint for lines
        linePaint.color = Color.RED // set color to green
        linePaint.strokeWidth = LINE_WIDTH.toFloat() // set stroke width
    }
}