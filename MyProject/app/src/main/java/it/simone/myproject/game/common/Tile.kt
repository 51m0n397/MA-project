package it.simone.myproject.game.common

import android.graphics.*
import kotlin.random.Random

class Tile() {
    private val w1 = Random.nextDouble(0.1, 2.4).toFloat()
    private val w2 = Random.nextDouble(w1 + 0.5, 2.9).toFloat()

    private val height = 0.25f

    private val rect1 = RectF(0f, 5f, w1, 5f + height)
    private val rect2 = RectF(w2, 5f, 3f, 5f + height)

    private var paint = Paint().apply {
        color = Color.WHITE
    }

    fun update(timePassed: Long, scrollSpeed: Float) {
        rect1.offset(0f, -scrollSpeed * timePassed)
        rect2.offset(0f, -scrollSpeed * timePassed)
    }

    fun outOfScreen(): Boolean {
        if (rect1.top < - height) return true
        return false
    }

    fun draw(frame: RectF, canvas: Canvas?) {
        var top = frame.top + rect1.top/5*frame.height()
        var bottom = frame.top + rect1.bottom/5*frame.height()

        if (top < frame.top) top = frame.top
        if (bottom > frame.bottom) bottom = frame.bottom

        canvas?.drawRect(
                frame.left,
                top,
                frame.left + rect1.right/3*frame.width(),
                bottom,
                paint
        )
        canvas?.drawRect(
                frame.left + rect2.left/3*frame.width(),
                top,
                frame.right,
                bottom,
                paint
        )
    }

    fun intersect(rect: RectF): Intersection? {
        val intersection = RectF()
        if (intersection.setIntersect(rect1, rect)) {
            val bottom = rect1.intersects(rect.left, rect.bottom, rect.right, rect.bottom)

            if (!bottom) return Intersection.LEFT
            if (intersection.width()<intersection.height()) return Intersection.LEFT
            return Intersection.BOTTOM

        } else if (intersection.setIntersect(rect2, rect)) {
            val bottom = rect2.intersects(rect.left, rect.bottom, rect.right, rect.bottom)

            if (!bottom) return Intersection.RIGHT
            if (intersection.width()<intersection.height()) return Intersection.RIGHT
            return Intersection.BOTTOM
        }
        return null
    }

}