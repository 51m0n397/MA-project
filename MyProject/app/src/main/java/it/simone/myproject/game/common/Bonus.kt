package it.simone.myproject.game.common

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import it.simone.myproject.MainActivity.Companion.context
import it.simone.myproject.R
import it.simone.myproject.gameserver.model.BonusType
import kotlin.random.Random

class Bonus {

    private val radius = 0.125f

    private val xPosition = Random.nextDouble(radius.toDouble(), 3.0-radius).toFloat()

    private val boundingRect = RectF(xPosition-radius, 5.5f, xPosition+radius, 5.5f+radius*2)

    val type = BonusType.values().random()

    private var paint = Paint().apply {
        color = Color.WHITE
    }

    fun update(timePassed: Long, scrollSpeed: Float) {
        boundingRect.offset(0f, -scrollSpeed * timePassed)
    }

    fun outOfScreen(): Boolean {
        if (boundingRect.top < - 2*radius) return true
        return false
    }

    fun draw(frame: RectF, canvas: Canvas?) {
        canvas?.drawCircle(
                frame.left + boundingRect.centerX()/3*frame.width(),
                frame.top + boundingRect.centerY()/5*frame.height(),
                boundingRect.width()/2/3*frame.width(),
                paint
        )

        val svg = when(type) {
            BonusType.FAST -> R.drawable.fast
            BonusType.BIG -> R.drawable.big
            BonusType.FREEZE -> R.drawable.freeze
        }

        val bitmap = ResourcesCompat.getDrawable(context?.resources!!, svg, null)?.
        toBitmap(
                (boundingRect.width()/3*frame.width()).toInt(),
                (boundingRect.height()/5*frame.height()).toInt()
        )!!
        canvas?.drawBitmap(
                bitmap,
                frame.left + boundingRect.left/3*frame.width(),
                frame.top + boundingRect.top/5*frame.height(),
                null
        )
    }

    fun intersect(ball: Ball): Boolean {
        return RectF.intersects(ball.boundingRect, boundingRect)
    }
}