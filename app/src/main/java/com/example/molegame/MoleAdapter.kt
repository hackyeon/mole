package com.example.molegame

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.molegame.ConstCollection.Companion.CLICKED_BOMB
import com.example.molegame.ConstCollection.Companion.IS_BOMB
import com.example.molegame.ConstCollection.Companion.IS_HAMMER
import com.example.molegame.ConstCollection.Companion.IS_MOLE
import com.example.molegame.ConstCollection.Companion.IS_NULL
import com.example.molegame.ConstCollection.Companion.IS_PERSON
import com.example.molegame.ConstCollection.Companion.IS_RACCOON_FIRST
import com.example.molegame.ConstCollection.Companion.IS_RACCOON_SECOND
import com.example.molegame.ConstCollection.Companion.IS_X
import com.example.molegame.ConstCollection.Companion.MOLE_NUM
import com.example.molegame.ConstCollection.Companion.TIMER
import com.example.molegame.ConstCollection.Companion.gameScore

class MoleAdapter(
    private val dataSet: List<CharacterButton>,
    private val context: Context,
    private val scoreTextView: TextView,
    private val characterHandler: Handler,
    private val handler: Handler
) :
    RecyclerView.Adapter<MoleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val moleImageView: ImageView

        init {
            moleImageView = view.findViewById(R.id.moleImageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mole, parent, false)
        view.findViewById<ConstraintLayout>(R.id.constraintLayout).apply {
            maxWidth = parent.width / 3
            minWidth = parent.width / 3
            maxHeight = parent.height / 3
            minHeight = parent.height / 3
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (dataSet[position].character == IS_NULL) holder.moleImageView.setImageDrawable(null)
        else if (dataSet[position].character == IS_MOLE) holder.moleImageView.setImageDrawable(getDrawable(context, R.drawable.mole))
        else if (dataSet[position].character == IS_PERSON) holder.moleImageView.setImageDrawable(getDrawable(context, R.drawable.person))
        else if (dataSet[position].character == IS_X) holder.moleImageView.setImageDrawable(getDrawable(context, R.drawable.x_icon))
        else if (dataSet[position].character == IS_HAMMER) holder.moleImageView.setImageDrawable(getDrawable(context, R.drawable.hammer))
        else if (dataSet[position].character == IS_BOMB) holder.moleImageView.setImageDrawable(getDrawable(context, R.drawable.bomb))
        else if (dataSet[position].character == IS_RACCOON_FIRST) holder.moleImageView.setImageDrawable(getDrawable(context, R.drawable.raccoon_first))
        else if (dataSet[position].character == IS_RACCOON_SECOND) holder.moleImageView.setImageDrawable(getDrawable(context, R.drawable.raccoon_second))


        holder.moleImageView.setOnClickListener {
            if (dataSet[position].character == IS_MOLE) {
                changeCharacter(R.drawable.hammer, IS_HAMMER, position, holder)
                gameScore++
                scoreTextView.text = "$gameScore 점"
            } else if (dataSet[position].character == IS_PERSON) {
                changeCharacter(R.drawable.x_icon, IS_X, position, holder)
                if (gameScore != 0) {
                    gameScore--
                    scoreTextView.text = "$gameScore 점"
                }
            }else if(dataSet[position].character == IS_BOMB){
                handler.removeMessages(TIMER)
                handler.sendEmptyMessage(CLICKED_BOMB)
            }else if(dataSet[position].character == IS_RACCOON_FIRST){
                dataSet[position].character = IS_RACCOON_SECOND
                holder.moleImageView.setImageDrawable(getDrawable(context, R.drawable.raccoon_second))
            }else if(dataSet[position].character == IS_RACCOON_SECOND){
                changeCharacter(R.drawable.hammer, IS_HAMMER, position, holder)
                gameScore += 2
                scoreTextView.text = "$gameScore 점"
            }
        }
    }

    private fun changeCharacter(drawable: Int, characterNum: Int, position: Int, holder: ViewHolder){
        characterHandler.removeMessages(position)
        dataSet[position].character = characterNum
        holder.moleImageView.setImageDrawable(getDrawable(context, drawable))
        characterHandler.sendEmptyMessageDelayed(position, 200)
    }

    override fun getItemCount(): Int = MOLE_NUM
}

