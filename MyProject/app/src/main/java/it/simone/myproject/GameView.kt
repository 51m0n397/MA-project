package it.simone.myproject

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*

class GameView(context: Context?) : View(context), SensorEventListener2 {

    private val backgroundPaint = Paint().apply {
        color = ContextCompat.getColor(context!!, R.color.purple_700)
    }


    private var gameLoop: Job? = null

    val tileList = TileList()
    val ball = Ball()
    val scrollSpeed = 0.001f
    var xAcceleration = 0f

    init {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        )
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            xAcceleration = -event.values[0]
        }
    }


    private fun update(timePassed: Long) {
        tileList.update(timePassed, scrollSpeed)
        ball.update(timePassed, tileList.clone(), scrollSpeed, xAcceleration)
    }


    fun play() {
        gameLoop = GlobalScope.launch {
            var lastIterationTime = System.currentTimeMillis()

            while (isActive) {
                val now = System.currentTimeMillis()
                val timePassed = now - lastIterationTime
                lastIterationTime = now

                update(timePassed)
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


        var textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 0.05f * height
        }

        var framePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 0.01f * height
        }

        canvas?.drawText("SCORE", 0.03f * width, 0.05f * height, textPaint)

        val offset = 0.07f * height

        val remainingHeight = height - offset

        val border = framePaint.strokeWidth/2

        val frame = RectF()

        if (remainingHeight/width > 5f/3f) {
            // taller

            frame.set(
                border,
                offset + border,
                width - border,
                offset + width/3f*5f - border
            )

            canvas?.drawRect(frame, framePaint)
        } else {
            // larger
            val w = remainingHeight/5f*3f

            frame.set(
                (width-w)/2 + border,
                offset + border,
                (width-w)/2+w - border,
                height.toFloat() - border,
            )

            canvas?.drawRect(frame, framePaint)
        }

        tileList.clone().draw(frame, canvas)

        ball.draw(frame, canvas)

        invalidate()

        //Log.i("info", width.toString() + " " + height.toString())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onFlushCompleted(sensor: Sensor?) {
    }

}