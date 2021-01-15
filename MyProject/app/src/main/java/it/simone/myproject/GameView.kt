package it.simone.myproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.util.concurrent.Semaphore

class GameView: View {

    constructor(context: Context?) : super(context)

    val TILE_HEIGHT = 0.05f

    private val backgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.purple_700)
    }

    private val objectsPaint = Paint().apply {
        color = Color.WHITE
    }

    val tileList = ArrayList<Tile>()

    init {
        updateTiles()
        addTiles()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas?.drawPaint(backgroundPaint)

        for(tile in tileList) {
            val rects = tile.getRects(width, height)
            //Log.i("info", rects.toString())
            canvas.drawRect(rects.first, objectsPaint)
            canvas.drawRect(rects.second, objectsPaint)
        }

        invalidate()
    }

    fun addTiles() {

        tileList.add(Tile(0.4f, TILE_HEIGHT,1f))

        Handler(Looper.getMainLooper()).postDelayed({ addTiles() }, 1000)
    }



    fun updateTiles() {

        for(tile in tileList) {
            tile.position = tile.position - 0.005f
            if (tile.position < - TILE_HEIGHT) tileList.remove(tile)
        }

        invalidate()
        Handler(Looper.getMainLooper()).postDelayed({ updateTiles() }, 1000/60)
    }


}