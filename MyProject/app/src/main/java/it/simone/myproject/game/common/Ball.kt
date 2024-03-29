package it.simone.myproject.game.common

import android.graphics.*
import androidx.core.graphics.times
import androidx.core.graphics.transform

class Ball {

    private val radius = 0.15f

    private var ySpeed = 0.0f
    private var xSpeed = 0.0f
    private val yAcceleration = 0.000005f

    var gameOver = false

    val boundingRect = RectF(1.5f - radius, 0f, 1.5f + radius, radius * 2)

    private var paint = Paint().apply {
        color = Color.WHITE
    }
    private var paintDebug = Paint().apply {
        color = Color.RED
    }

    fun big() {
        val matrix = Matrix()
        matrix.setScale(3/2f, 3/2f)
        boundingRect.transform(matrix)
    }

    fun small() {
        val matrix = Matrix()
        matrix.setScale(2/3f, 2/3f)
        boundingRect.transform(matrix)
    }

    fun update(timePassed: Long, tileList: TileList, scrollSpeed: Float, xAcceleration: Float) {
        xSpeed += xAcceleration * timePassed/1000000
        ySpeed += yAcceleration * timePassed

        val isec = tileList.intersect(boundingRect)

        if (isec == Intersection.BOTTOM) {
            ySpeed = 0f
            boundingRect.offset(xSpeed * timePassed, -scrollSpeed * timePassed)
        } else if ((isec == Intersection.LEFT && xSpeed < 0) || (isec == Intersection.RIGHT && xSpeed > 0)) {
            xSpeed = 0f
            boundingRect.offset(0f, ySpeed * timePassed)
        } else {
            boundingRect.offset(xSpeed * timePassed, ySpeed * timePassed)
        }

        if (boundingRect.left < 0) {
            xSpeed = 0f
            boundingRect.offset(-boundingRect.left, 0f)
        }
        if (boundingRect.right > 3) {
            xSpeed = 0f
            boundingRect.offset(3 - boundingRect.right, 0f)
        }
        if (boundingRect.bottom > 5) boundingRect.offset(0f, 5 - boundingRect.bottom)
        if (boundingRect.top < 0) gameOver = true
    }

    fun draw(frame: RectF, canvas: Canvas?) {
        /*canvas?.drawRect(
                frame.left + boundingRect.left/3*frame.width(),
                frame.top + boundingRect.top/5*frame.height(),
                frame.left + boundingRect.right/3*frame.width(),
                frame.top + boundingRect.bottom/5*frame.height(),
                paintDebug
        )*/
        canvas?.drawCircle(
                frame.left + boundingRect.centerX() / 3 * frame.width(),
                frame.top + boundingRect.centerY() / 5 * frame.height(),
                boundingRect.width() / 2 / 3 * frame.width(),
                paint
        )
    }

}