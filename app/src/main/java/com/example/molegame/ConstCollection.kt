package com.example.molegame

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.core.content.ContextCompat
import kotlin.random.Random

class ConstCollection {
    companion object {
        const val MOLE_NUM = 9
        const val MIN_TIME = 1
        const val MAX_TIME = 5
        const val DEFAULT_CLEAR_TIME = 3
        const val GAME_OVER = "Game Over"
        var gameScore = 0
        const val GAME_PLAY_TIME = 30
        const val SPF_NAME = "moleGame"
        const val SPF_KEY_SCORE = "highScore"
        const val COUNTER = 999
        const val TIMER = 888
        const val CLICKED_BOMB = 444

        // character
        const val IS_NULL = 1000
        const val IS_MOLE = 1001
        const val IS_PERSON = 1002
        const val IS_HAMMER = 1003
        const val IS_X = 1004
        const val IS_BOMB = 1005
        const val IS_TIMER_PLUS = 1006
        const val IS_TIMER_MINUS = 1007
        const val IS_TIMER_STOP = 1008
        const val IS_TIMER_FAST = 1009
        const val IS_RACCOON_FIRST = 1010
        const val IS_RACCOON_SECOND = 1011

        fun intToLong(num: Int): Long = num * 1000.toLong()
    }
}
