package it.simone.myproject.game.multiPlayer

import android.content.Context
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import it.simone.myproject.BackButton
import it.simone.myproject.MainActivity
import it.simone.myproject.R
import it.simone.myproject.game.common.Ball
import it.simone.myproject.game.common.BonusList
import it.simone.myproject.game.common.TileList
import it.simone.myproject.gameserver.api.GameserverApi
import it.simone.myproject.gameserver.model.BonusType
import it.simone.myproject.gameserver.model.Game
import it.simone.myproject.gameserver.model.GameState
import it.simone.myproject.gameserver.model.PlayerState
import kotlinx.coroutines.*

class MultiPlayerGameView(context: Context?) : View(context), SensorEventListener {

    companion object {
        lateinit var game: Game
        var playerNum = 1
    }

    private var bonusList: BonusList? = null

    private var gameLoop: Job? = null
    private var updateLoop: Job? = null

    private var tileList: TileList? = null
    private var ball: Ball? = null
    private var xAcceleration = 0f

    private var playerState = PlayerState.ALIVE

    private var scrollSpeed = 0.0005f
    private var scrollAcceleration = 0.00000002f

    private var activeBonus: BonusType? = null

    private val textPaint = Paint().apply {
        color = Color.WHITE
    }

    private val backgroundPaint = Paint().apply {
        color = ContextCompat.getColor(MainActivity.context!!, R.color.purple_500)
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

    private var spinnerRotation = 0F

    private fun newGame() {
        bonusList = BonusList()
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
        bonusList?.update(timePassed, scrollSpeed, ball!!)
        tileList?.update(timePassed, scrollSpeed)
        ball?.update(timePassed, tileList?.clone()!!, scrollSpeed, xAcceleration)
    }

    fun play() {
        if (playerState == PlayerState.ALIVE) {
            gameLoop = GlobalScope.launch {
                var lastIterationTime = System.currentTimeMillis()

                while (isActive) {
                    val now = System.currentTimeMillis()
                    var timePassed = now - lastIterationTime
                    lastIterationTime = now

                    if (activeBonus == BonusType.FREEZE) timePassed = 0

                    update(timePassed)

                    if (ball?.gameOver == true) {
                        playerState = PlayerState.DEAD
                        break
                    }
                }
            }

            updateLoop = GlobalScope.launch {
                while (isActive) {
                    val state = playerState

                    if (state == PlayerState.DEAD) {
                        val updatedGame = GameserverApi()
                                .updateGame(tileList!!.tileNum, state, null, game.id)
                        if (updatedGame != null) game = updatedGame
                        break
                    }
                    val updatedGame = GameserverApi()
                            .updateGame(tileList!!.tileNum, state, bonusList?.activeBonus, game.id)
                    if (updatedGame != null) {
                        game = updatedGame
                        val bonus = if (playerNum == 1) game.player2.bonus else game.player1.bonus

                        if (bonus != activeBonus) {
                            // disable current bonus
                            when (activeBonus) {
                                BonusType.BIG -> {
                                    ball?.small()
                                }
                                BonusType.FAST -> {
                                    scrollSpeed /= 1.5f
                                }
                            }

                            // activate new bonus
                            activeBonus = bonus
                            when(bonus) {
                                BonusType.BIG -> {
                                    ball?.big()
                                }
                                BonusType.FAST -> {
                                    scrollSpeed *= 1.5f
                                }
                            }
                        }

                    } else {
                        //error
                    }

                    delay(500L)
                }

                MainActivity.backButton = BackButton.BACK_TO_MENU

                while (isActive && game.state == GameState.PLAYING) {
                    val updatedGame = GameserverApi().getGame(game.id)
                    if (updatedGame != null) {
                        game = updatedGame
                    } else {
                        //the game was deleted by the server
                        game.state = GameState.OVER
                        break
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


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawPaint(backgroundPaint)

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

        bonusList?.clone()?.draw(frame, canvas)
        tileList?.clone()?.draw(frame, canvas)
        ball?.draw(frame, canvas)

        canvas?.drawRect(0f, 0f, width.toFloat(), offset, backgroundPaint)

        textPaint.textSize = 0.05f * height

        canvas?.drawText(
                resources.getString(R.string.score) + ": " + tileList!!.tileNum,
                0.03f * width,
                0.05f * height,
                textPaint
        )


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
                            spinnerRotation++,
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

                    val t = resources.getString(if (win) R.string.won else R.string.lost)

                    gameOverPaint.textSize = (width/t.length).toFloat()
                    gameOverPaint.getTextBounds(t, 0, t.length, textRect)

                    val x: Float = width / 2f - textRect.width() / 2f - textRect.left
                    val y: Float = height / 2f + textRect.height() / 2f - textRect.bottom

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