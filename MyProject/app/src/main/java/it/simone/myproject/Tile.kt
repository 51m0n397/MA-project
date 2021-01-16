package it.simone.myproject

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import kotlin.random.Random

class Tile() {
    private val w1 = Random.nextDouble(0.1, 0.8)
    private val w2 = Random.nextDouble(w1 + 0.1, 0.9)

    private val height = 0.05

    private var position = 1.0

    private val speed = 0.0002

    private var paint = Paint().apply {
        color = Color.WHITE
    }

    var moving = true

    fun update(timePassed: Long) {
        if(moving) position -= speed * timePassed
    }

    fun outOfScreen(): Boolean {
        if (position < - height) return true
        return false
    }

    fun draw(screenWidth: Int, screenHeight: Int, canvas: Canvas?) {
        canvas?.drawRect(
            0F,
            (screenHeight * position).toFloat(),
            (w1 * screenWidth).toFloat(),
            (screenHeight * (position + height)).toFloat(),
            paint
        )
        canvas?.drawRect(
            (w2 * screenWidth).toFloat(),
            (screenHeight * position).toFloat(),
            screenWidth.toFloat(),
            (screenHeight * (position + height)).toFloat(),
            paint
        )
    }

}