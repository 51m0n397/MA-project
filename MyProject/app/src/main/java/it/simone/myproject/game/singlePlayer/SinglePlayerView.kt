package it.simone.myproject.game.singlePlayer

import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import it.simone.myproject.R
import it.simone.myproject.game.common.Ball
import it.simone.myproject.game.common.TileList
import it.simone.myproject.login.LoginFragment.Companion.globalstatsId
import it.simone.myproject.globalstats.api.GlobalstatsApi
import kotlinx.coroutines.*

class SinglePlayerView(context: Context?) : View(context), SensorEventListener, View.OnTouchListener {

    private var gameLoop: Job? = null

    private var tileList: TileList? = null
    private var ball: Ball? = null
    private var xAcceleration = 0f
    private var gameOver = false

    private var scrollSpeed = 0.0005f
    private var scrollAcceleration = 0.00000002f

    private val textPaint = Paint().apply {
        color = Color.WHITE
    }

    private val framePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
    }

    private val gameOverPaint = Paint().apply {
        color = Color.WHITE
    }

    private val frame = RectF()
    private val textRect = Rect()

    private fun newGame() {
        tileList = TileList()
        ball = Ball()
        xAcceleration = 0f
        scrollSpeed = 0.001f
        gameOver = false
    }

    init {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        )
        setOnTouchListener(this)
        newGame()
    }


    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            xAcceleration = -event.values[0]
        }
    }


    private fun update(timePassed: Long) {
        scrollSpeed += timePassed*scrollAcceleration
        tileList?.update(timePassed, scrollSpeed)
        ball?.update(timePassed, tileList?.clone()!!, scrollSpeed, xAcceleration)
    }

    fun play() {
        if (!gameOver) {
            gameLoop = GlobalScope.launch {
                var lastIterationTime = System.currentTimeMillis()

                while (isActive) {
                    val now = System.currentTimeMillis()
                    val timePassed = now - lastIterationTime
                    lastIterationTime = now

                    update(timePassed)

                    if (ball?.gameOver == true) {
                        gameOver = true
                        GlobalstatsApi().postScore(globalstatsId, tileList!!.tileNum)
                        break
                    }
                }
            }
        }
    }

    fun pause() {
        gameLoop?.cancel()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(ContextCompat.getColor(context, R.color.purple_500))

        textPaint.textSize = 0.05f * height

        canvas?.drawText(
            resources.getString(R.string.score) + ": " + tileList!!.tileNum,
            0.03f * width,
            0.05f * height,
            textPaint
        )

        framePaint.strokeWidth = 0.01f * height
        val offset = 0.07f * height
        val remainingHeight = height - offset
        val border = framePaint.strokeWidth/2

        if (remainingHeight/width > 5f/3f) {
            // taller

            frame.set(
                    border,
                    offset + border,
                    width - border,
                    offset + width / 3f * 5f - border
            )

            canvas?.drawRect(frame, framePaint)
        } else {
            // larger
            val w = remainingHeight/5f*3f

            frame.set(
                    (width - w) / 2 + border,
                    offset + border,
                    (width - w) / 2 + w - border,
                    height.toFloat() - border,
            )

            canvas?.drawRect(frame, framePaint)
        }

        tileList?.clone()?.draw(frame, canvas)
        ball?.draw(frame, canvas)


        val t = resources.getString(R.string.game_over)

        gameOverPaint.textSize = (width/t.length).toFloat()
        gameOverPaint.getTextBounds(t, 0, t.length, textRect)

        val x: Float = width / 2f - textRect.width() / 2f - textRect.left
        val y: Float = height / 2f + textRect.height() / 2f - textRect.bottom

        if (gameOver) {
            canvas?.drawARGB(100, 0, 0, 0)
            canvas?.drawText(t, x, y, gameOverPaint)
        } else {
            invalidate()
        }

    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                if (gameOver) {
                    newGame()
                    invalidate()
                    play()
                }

            }
        }
        return true
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}