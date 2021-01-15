package it.simone.myproject

import android.graphics.Rect

class Tile(val width: Float, val height: Float, var position: Float) {

    fun getRects(screenWidth: Int, screenHeight: Int): Pair<Rect,Rect> {
        return Pair(
            Rect(
                0,
                    (screenHeight * position).toInt(),
                    (width * screenWidth).toInt(),
                    (screenHeight * (position + height)).toInt()
            ),
            Rect(
                screenWidth - (width * screenWidth).toInt(),
                    (screenHeight * position).toInt(),
                    screenWidth,
                    (screenHeight * (position + height)).toInt()
            )
        )
    }
}