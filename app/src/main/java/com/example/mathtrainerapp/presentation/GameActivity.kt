package com.example.mathtrainerapp.presentation

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.mathtrainerapp.R
import com.example.mathtrainerapp.appComponent
import com.example.mathtrainerapp.data.GameParameters
import com.example.mathtrainerapp.databinding.ActivityGameBinding
import com.example.mathtrainerapp.domain.entities.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityGameBinding
    private val gameViewModel: GameViewModel by viewModels{
        factory.create(playerId ?: "")
    }
    private var playerId: String? = null

    @Inject
    lateinit var factory: GameViewModelFactory.Factory

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        appComponent.injectGameActivity(this)

        arrayOf (binding.keyboard.imageButton0,
            binding.keyboard.imageButton1,
            binding.keyboard.imageButton2,
            binding.keyboard.imageButton3,
            binding.keyboard.imageButton4,
            binding.keyboard.imageButton5,
            binding.keyboard.imageButton6,
            binding.keyboard.imageButton7,
            binding.keyboard.imageButton8,
            binding.keyboard.imageButton9,
            binding.keyboard.imageButton0,
            binding.keyboard.imageButtonDel)
            .map { imageButton -> imageButton.setOnClickListener(this) }

        binding.progressBarTime.max = (GameParameters.DEFAULT_ROUND_DURATION_IN_STEPS * GameParameters.DEFAULT_TIMER_STEP_MS).toInt()

        runInLifecycleScope {
            gameViewModel.timerValueFlow.collect {
                updateTimerValue(it)
            }
        }

        runInLifecycleScope {
            gameViewModel.taskFlow.collect{
                clearAnswer()
                if (it != null) updateTask(it)
            }
        }

        runInLifecycleScope {
            gameViewModel.scoreFlow.collect{
                updateScore(it)
            }
        }

        runInLifecycleScope {
            gameViewModel.eventsToShowFlow.collect {
                when(it.first) {
                    GameViewModel.EventsToShow.NONE -> { }
                    GameViewModel.EventsToShow.ROUND_LOST -> signalRoundLost()
                    GameViewModel.EventsToShow.ROUND_WON -> signalRoundWon()
                    GameViewModel.EventsToShow.WRONG_ANSWER -> signalWrongAnswer()
                    GameViewModel.EventsToShow.GAME_FINISHED -> {
                        val score = it.second as? Int ?: 0
                        signalGameFinished(score)
                        clearAnswer()
                        clearTask()
                    }
                    GameViewModel.EventsToShow.ERROR -> {
                        showError(it.second.toString())
                    }
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        startGame()
    }

    override fun onStop() {
        super.onStop()
        gameViewModel.pause()
    }

    override fun onClick(view: View?) {
        val answer  = binding.editTextAnswer.text.toString()
        when (view?.id) {
            R.id.imageButton1 -> binding.editTextAnswer.setText(answer + "1")
            R.id.imageButton2 -> binding.editTextAnswer.setText(answer + "2")
            R.id.imageButton3 -> binding.editTextAnswer.setText(answer + "3")
            R.id.imageButton4 -> binding.editTextAnswer.setText(answer + "4")
            R.id.imageButton5 -> binding.editTextAnswer.setText(answer + "5")
            R.id.imageButton6 -> binding.editTextAnswer.setText(answer + "6")
            R.id.imageButton7 -> binding.editTextAnswer.setText(answer + "7")
            R.id.imageButton8 -> binding.editTextAnswer.setText(answer + "8")
            R.id.imageButton9 -> binding.editTextAnswer.setText(answer + "9")
            R.id.imageButton0 -> binding.editTextAnswer.setText(answer + "0")
            R.id.imageButtonDel -> {
                binding.editTextAnswer.setText(answer.dropLast(1))
            }
            R.id.imageButtonMin -> {}
        }
        gameViewModel.tryAnswer(binding.editTextAnswer.text.toString())
    }

    fun updateTask(task: Task) {
        binding.textViewTask.text = task.toString()
    }

    fun updateScore(score: Int) {
        binding.textViewScore.text = score.toString()
    }

    fun updateTimerValue(newValue: Long) {
        binding.progressBarTime.progress = newValue.toInt()
    }

    fun clearAnswer(){
        binding.editTextAnswer.setText("")
    }

    fun signalWrongAnswer() {
        //no need to show anything
    }

    fun signalRoundWon() {
        //TODO: show win animation
        //Toast.makeText(this, "Round won", Toast.LENGTH_SHORT).show()
    }

    fun signalRoundLost() {
        //TODO: show loose animation
        //Toast.makeText(this, "Round lost", Toast.LENGTH_SHORT).show()
    }

    @ExperimentalCoroutinesApi
    fun signalGameFinished(score: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.game_over)
        builder.setMessage(getString(R.string.final_score, score))
        builder.setCancelable(false)
        builder.setPositiveButton(R.string.play_again){_, _ -> startGame()}
        builder.setNegativeButton(R.string.end_game){ _, _ -> finish()}
        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.primary))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.primary))
    }

    fun clearTask() {
        binding.textViewTask.text = ""
    }

    fun showError(description: String) {
        //TODO: replace with dialog message
        Toast.makeText(this, "Error: $description", Toast.LENGTH_SHORT).show()
    }

    @ExperimentalCoroutinesApi
    fun startGame() {
        gameViewModel.startOrResume()
    }

    private fun runInLifecycleScope(action: suspend ()->Unit) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                action()
            }
        }
    }
}