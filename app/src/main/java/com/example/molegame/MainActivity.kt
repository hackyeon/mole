package com.example.molegame

import android.content.Context
import android.content.SharedPreferences
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.molegame.ConstCollection.Companion.CLICKED_BOMB
import com.example.molegame.ConstCollection.Companion.COUNTER
import com.example.molegame.ConstCollection.Companion.DEFAULT_CLEAR_TIME
import com.example.molegame.ConstCollection.Companion.GAME_OVER
import com.example.molegame.ConstCollection.Companion.GAME_PLAY_TIME
import com.example.molegame.ConstCollection.Companion.IS_BOMB
import com.example.molegame.ConstCollection.Companion.IS_HAMMER
import com.example.molegame.ConstCollection.Companion.IS_MOLE
import com.example.molegame.ConstCollection.Companion.IS_NULL
import com.example.molegame.ConstCollection.Companion.IS_PERSON
import com.example.molegame.ConstCollection.Companion.IS_RACCOON_FIRST
import com.example.molegame.ConstCollection.Companion.IS_X
import com.example.molegame.ConstCollection.Companion.MAX_TIME
import com.example.molegame.ConstCollection.Companion.MIN_TIME
import com.example.molegame.ConstCollection.Companion.MOLE_NUM
import com.example.molegame.ConstCollection.Companion.SPF_KEY_SCORE
import com.example.molegame.ConstCollection.Companion.SPF_NAME
import com.example.molegame.ConstCollection.Companion.TIMER
import com.example.molegame.ConstCollection.Companion.gameScore
import com.example.molegame.ConstCollection.Companion.intToLong
import com.example.molegame.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity(), EndGameDialogFragment.NoticeDialogListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var spf: SharedPreferences
    private var startCount: Int = 3
    private var isPlay: Boolean = false
    private var highScore = 0
    private var characterButtonList = mutableListOf<CharacterButton>()
    private var nowTime: Int = GAME_PLAY_TIME

    var handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == TIMER) {
                if (nowTime == 0) {
                    binding.timerProgressBar.progress = nowTime
                    endGame()
                    binding.countTextView.text = GAME_OVER
                    binding.countTextView.visibility = VISIBLE
                } else {
                    binding.timerProgressBar.progress = nowTime
                    nowTime--
                    sendEmptyMessageDelayed(TIMER, 1000)
                }
            } else if (msg.what == CLICKED_BOMB) {
                endGame()
                binding.countTextView.text = GAME_OVER
                binding.countTextView.visibility = VISIBLE
            } else if (msg.what == COUNTER) {
                if (startCount == 0) {
                    binding.countTextView.visibility = GONE
                    gameScore = 0
                    binding.scoreTextView.text = "$gameScore 점"
                    startGame()
                } else {
                    binding.countTextView.text = "$startCount"
                    binding.countTextView.visibility = VISIBLE
                    startCount--
                    sendEmptyMessageDelayed(COUNTER, 1000)
                }
            }
        }
    }

    var characterHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (characterButtonList[msg.what].character == IS_NULL) {
                // 캐릭터가 없을때 두더지, 사람 캐릭터 넣고 3초 뒤 삭제
                // 1~10: mole, 11~20: person, 21~24: raccoon, 25: bomb
                var tempCharacterType = Random.nextInt(1, 22)
                var characterType = when (tempCharacterType) {
                    in 1..10 -> IS_MOLE
                    in 11..20 -> IS_PERSON
                    in 21..24 -> IS_RACCOON_FIRST
                    25 -> IS_BOMB
                    else -> IS_NULL
                }
                characterButtonList[msg.what].character = characterType
                sendEmptyMessageDelayed(msg.what, intToLong(DEFAULT_CLEAR_TIME))
            } else if (characterButtonList[msg.what].character == IS_HAMMER || characterButtonList[msg.what].character == IS_X) {
                // 망치나 엑스표시가 나와있는 경우
                characterButtonList[msg.what].character = IS_NULL
                sendEmptyMessageDelayed(
                    msg.what,
                    intToLong(Random.nextInt(MIN_TIME, MAX_TIME)) - 200
                )
            } else {
                // 캐릭터인 경우 // 두더지, 사람, 폭탄, 너구리
                // 이게 실행되면 캐릭터가 나왔는데 클릭하지 않아서 넘어온거임
                // 캐릭터없애주고 1~5초 후 다시 캐릭터 생성
                characterButtonList[msg.what].character = IS_NULL
                sendEmptyMessageDelayed(msg.what, intToLong(Random.nextInt(MIN_TIME, MAX_TIME)))
            }
            binding.recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initView()
        clickedButton()
        saveScoreData()
    }

    private fun initView() {
        spf = getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE)
        highScore = spf.getInt(SPF_KEY_SCORE, 0)
        binding.highScoreTextView.text = "$highScore 점"
        for (i in 0 until MOLE_NUM) {
            characterButtonList.add(CharacterButton())
        }
    }

    private fun startGame() {
        val adapter =
            MoleAdapter(characterButtonList, this, binding.scoreTextView, characterHandler, handler)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)

        for (i in characterButtonList.indices) {
            var ran = Random.nextInt(
                MIN_TIME - 1,
                MAX_TIME - 1
            )   // 시작할때는 1~5초가 아닌 0~4초 시작할떄 캐릭터가 없으니 너무 허전함
            characterHandler.sendEmptyMessageDelayed(i, intToLong(ran))
        }
        handler.sendEmptyMessage(TIMER)
    }

    private fun clickedButton() {
        binding.startButton.setOnClickListener {
            if (!isPlay) {
                isPlay = true
                nowTime = GAME_PLAY_TIME
                handler.sendEmptyMessage(COUNTER)
            }
        }
        binding.endButton.setOnClickListener {
            endGameButtonFun()
        }
    }

    private fun endGameButtonFun() {
        if (isPlay) {
            handler.removeMessages(COUNTER)
            binding.countTextView.visibility = GONE
            endGame()
            handler.removeMessages(TIMER)
        } else {
            createDialogFragment()
        }
    }

    private fun endGame() {
        for (i in characterButtonList.indices) {
            characterHandler.removeMessages(i)
        }
        isPlay = false
        startCount = 3
        binding.recyclerView.adapter = null
        for (i in characterButtonList) {
            i.character = IS_NULL
        }
    }

    private fun saveScoreData() {
        binding.scoreTextView.addTextChangedListener {
            if (gameScore > highScore) {
                highScore = gameScore
                binding.highScoreTextView.text = "$highScore 점"
                spf.edit {
                    putInt(SPF_KEY_SCORE, highScore)
                }
            }
        }
    }

    override fun onBackPressed() {
        endGameButtonFun()
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        finish()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    private fun createDialogFragment() {
        EndGameDialogFragment().show(supportFragmentManager, "endMessage")
    }
}