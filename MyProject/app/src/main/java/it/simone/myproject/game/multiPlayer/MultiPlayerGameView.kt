package it.simone.myproject.game.multiPlayer

import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.View
import androidx.core.content.ContextCompat
import it.simone.myproject.BackButton
import it.simone.myproject.MainActivity
import it.simone.myproject.R
import it.simone.myproject.game.common.Ball
import it.simone.myproject.game.common.TileList
import it.simone.myproject.gameserver.api.GameserverApi
import it.simone.myproject.gameserver.model.Game
import it.simone.myproject.gameserver.model.GameState
import it.simone.myproject.gameserver.model.PlayerState
import kotlinx.coroutines.*

class MultiPlayerGameView(context: Context?) : View(context), SensorEventListener {

    companion object {
        lateinit var game: Game
        var playerNum = 1
    }

    private var gameLoop: Job? = null
    private var updateLoop: Job? = null

    private var tileList: TileList? = null
    private var ball: Ball? = null
    private var xAcceleration = 0f

    private var playerState = PlayerState.ALIVE

    private var scrollSpeed = 0.001f
    private var scrollAcceleration = 0.00000002f

    private fun newGame() {
        tileList = TileList()
        ball = Ball()
        xAcceleration = 0f
        scrollSpeed = 0.001f
        playerState = PlayerState.ALIVE
    }

    init {
        val sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
        )
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
        if (playerState == PlayerState.ALIVE) {
            gameLoop = GlobalScope.launch {
                var lastIterationTime = System.currentTimeMillis()

                while (isActive) {
                    val now = System.currentTimeMillis()
                    val timePassed = now - lastIterationTime
                    lastIterationTime = now

                    update(timePassed)

                    if (ball?.gameOver == true) {
                        //gameOver = true
                        playerState = PlayerState.DEAD
                        break
                    }
                }
            }

            updateLoop = GlobalScope.launch {
                while (isActive) {
                    val state = playerState
                    val updatedGame = GameserverApi().updateGame(playerNum, tileList!!.tileNum, state, game.id)
                    if (updatedGame != null) {
                        game = updatedGame
                    } else {
                        //error
                    }

                    if (state == PlayerState.DEAD) {
                        break
                    }
                    delay(500L)
                }

                MainActivity.backButton = BackButton.BACK_TO_MENU

                while (isActive && game.state == GameState.PLAYING) {
                    val updatedGame = GameserverApi().getGame(game.id)
                    if (updatedGame != null) {
                        game = updatedGame
                    } else {
                        //error
                    }
                    delay(500L)
                }
            }
        }
    }

    fun pause() {
        gameLoop?.cancel()
        updateLoop?.cancel()
    }

    var i = 0F


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(ContextCompat.getColor(context, R.color.purple_700))

        var textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 0.05f * height
        }

        var framePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.STROKE
            strokeWidth = 0.01f * height
        }

        canvas?.drawText(
                resources.getString(R.string.score) + ": " + tileList!!.tileNum,
                0.03f * width,
                0.05f * height,
                textPaint
        )

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




        if (playerState == PlayerState.DEAD) {
            canvas?.drawARGB(100, 0, 0, 0)

            when(game.state) {
                GameState.PLAYING -> {
                    val size = 0.15f*width
                    canvas?.drawArc(
                            width/2-size,
                            height/2-size,
                            width/2+size,
                            height/2+size,
                            i++,
                            270F,
                            false,
                            framePaint
                    )
                    invalidate()
                }
                GameState.OVER -> {
                    var win = false
                    when(playerNum) {
                        1 -> win = game.player1.score >= game.player2.score
                        2 -> win = game.player2.score >= game.player1.score
                    }

                    val r = Rect()
                    val t = if(win) "You won" else "You lost"

                    var gameOverPaint = Paint().apply {
                        color = Color.WHITE
                        textSize = (width/t.length).toFloat()
                    }

                    gameOverPaint.getTextBounds(t, 0, t.length, r)

                    val x: Float = width / 2f - r.width() / 2f - r.left
                    val y: Float = height / 2f + r.height() / 2f - r.bottom

                    canvas?.drawText(t, x, y, gameOverPaint)
                }
            }
        } else {
            invalidate()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}