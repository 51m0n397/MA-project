package it.simone.myproject

import android.graphics.Canvas

class TileList {
    val list = ArrayList<Tile>()

    private val spawnPeriod = 1000L

    private var time = 0L

    fun update(timePassed: Long) {
        val iter = list.iterator()

        while (iter.hasNext()) {
            val tile = iter.next()
            tile.update(timePassed)
            if (tile.outOfScreen()) iter.remove()
        }

        if (time < spawnPeriod) {
            time += timePassed
        } else {
            list.add(Tile())
            time = 0
        }
    }

    fun draw(screenWidth: Int, screenHeight: Int, canvas: Canvas?) {
        val iter = list.iterator()

        while (iter.hasNext()) {
            iter.next().draw(screenWidth, screenHeight, canvas)
        }
    }

}