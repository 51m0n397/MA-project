package it.simone.myproject.game.common

import android.graphics.Canvas
import android.graphics.RectF
import it.simone.myproject.gameserver.model.BonusType
import kotlin.random.Random

class BonusList {
    var list = ArrayList<Bonus>()

    private var nextSpawn = Random.nextLong(8, 12)

    private var counter = 0

    private var time = 0L

    var activeBonus: BonusType? = null
    private var bonusTime = 0L
    private val bonusEndTime = 5000L

    fun update(timePassed: Long, scrollSpeed: Float, ball: Ball) {
        val iter = list.iterator()

        if (bonusTime < bonusEndTime) {
            bonusTime += timePassed
        } else {
            bonusTime = 0L
            activeBonus = null
        }

        while (iter.hasNext()) {
            val bonus = iter.next()
            bonus.update(timePassed, scrollSpeed)
            if (bonus.outOfScreen()) iter.remove()
            if (bonus.intersect(ball)) {
                activeBonus = bonus.type
                bonusTime = 0L
                iter.remove()
            }
        }

        if (time < 1/scrollSpeed) {
            time += timePassed
        } else {
            time = 0L
            if (++counter > nextSpawn) {
                list.add(Bonus())
                nextSpawn = Random.nextLong(8, 12)
                counter = 0
            }
        }
    }

    fun draw(frame: RectF, canvas: Canvas?) {
        val iter = list.iterator()

        while (iter.hasNext()) {
            iter.next().draw(frame, canvas)
        }
    }

    fun clone(): BonusList {
        val t = BonusList()
        t.list = list.clone() as ArrayList<Bonus>
        return t
    }

}