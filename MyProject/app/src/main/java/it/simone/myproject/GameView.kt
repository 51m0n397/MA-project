package it.simone.myproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

class GameView(context: Context?) : View(context) {

    private val backgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context!!, R.color.purple_700)
    }


    private var playing = false

    private var gameLoop: Job? = null

    val tileList = TileList()

    private fun initGame() {
    }

    private fun update(timePassed: Long) {
        tileList.update(timePassed)
    }


    fun play() {
        playing = true
        initGame()
        gameLoop = GlobalScope.launch {
            var lastIterationTime = System.currentTimeMillis()

            while (isActive) {
                val now = System.currentTimeMillis()
                val timePassed = now - lastIterationTime
                lastIterationTime = now

                update(timePassed)

                invalidate()
            }
        }
    }

    fun pause() = runBlocking {
        gameLoop?.cancel()
        gameLoop?.join()
        gameLoop = null
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawPaint(backgroundPaint)

        tileList.draw(width, height, canvas)
    }

}