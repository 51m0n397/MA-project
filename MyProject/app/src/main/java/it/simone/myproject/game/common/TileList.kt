package it.simone.myproject.game.common

import android.graphics.Canvas
import android.graphics.RectF

class TileList {
    var list = ArrayList<Tile>()

    var tileNum = 0

    private var time = 0L

    fun update(timePassed: Long, scrollSpeed: Float) {
        val iter = list.iterator()

        while (iter.hasNext()) {
            val tile = iter.next()
            tile.update(timePassed, scrollSpeed)
            if (tile.outOfScreen()) iter.remove()
        }

        if (time < 1/scrollSpeed) {
            time += timePassed
        } else {
            list.add(Tile())
            tileNum++
            time = 0
        }
    }

    fun draw(frame: RectF, canvas: Canvas?) {
        val iter = list.iterator()

        while (iter.hasNext()) {
            iter.next().draw(frame, canvas)
        }
    }

    fun intersect(rect: RectF): Intersection? {
        val iter = list.iterator()

        var res: Intersection? = null

        while (iter.hasNext()) {
            val isec = iter.next().intersect(rect)
            if (isec!=null) {
                res = isec
                break
            }
        }
        return res
    }

    fun clone(): TileList {
        val t = TileList()
        t.list = list.clone() as ArrayList<Tile>
        return t
    }

}