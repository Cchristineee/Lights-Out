package com.zybooks.lightsouttwo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.children

const val GAME_STATE = "gameState"

class MainActivity : AppCompatActivity() {

    private lateinit var game: LigghtsOutGame
    private lateinit var lightGridLayout:GridLayout
    private var lightOnColor = 0
    private var lightOffColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lightGridLayout = findViewById(R.id.light_grid)

        // Add the same click handler to all grid buttons
        for (gridButton in lightGridLayout.children) {
            gridButton.setOnClickListener(this::onLightButtonClick)

            val buttonIndex = lightGridLayout.indexOfChild(gridButton)
            if(buttonIndex == 0){
                gridButton.setOnLongClickListener(object: View.OnLongClickListener {
                    override fun onLongClick(v: View?): Boolean {
                        game.cheat()
                        setButtonColors()
                        // Congratulate the user if the game is over
                        if (game.isGameOver) {
                            Toast.makeText(getApplicationContext(), R.string.congrats, Toast.LENGTH_SHORT).show()
                        }
                        return true
                    }
                })
            }
        }

        lightOnColor = ContextCompat.getColor(this, R.color.yellow)
        lightOffColor = ContextCompat.getColor(this, R.color.black)

        game = LigghtsOutGame()
        if(savedInstanceState == null)
            startGame()
        else{
            game.state = savedInstanceState.getString(GAME_STATE)!!
            setButtonColors()
        }
    }

    private fun startGame() {
        game.newGame()
        setButtonColors()
    }

    private fun onLightButtonClick(view: View) {

        // Find the button's row and col
        val buttonIndex = lightGridLayout.indexOfChild(view)
        val row = buttonIndex / GRID_SIZE
        val col = buttonIndex % GRID_SIZE

        game.selectLight(row, col)
        setButtonColors()

        // Congratulate the user if the game is over
        if (game.isGameOver) {
            Toast.makeText(this, R.string.congrats, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setButtonColors() {

        // Set all buttons' background color
        for (buttonIndex in 0 until lightGridLayout.childCount) {
            val gridButton = lightGridLayout.getChildAt(buttonIndex)

            // Find the button's row and col
            val row = buttonIndex / GRID_SIZE
            val col = buttonIndex % GRID_SIZE

            if (game.isLightOn(row, col)) {
                gridButton.setBackgroundColor(lightOnColor)
                gridButton.contentDescription = getString(R.string.on)
            } else {
                gridButton.setBackgroundColor(lightOffColor)
                gridButton.contentDescription = getString(R.string.off)
            }
        }
    }

    fun onNewGameClick(view: View) {
        startGame()
    }

    fun onHelpClick(view: View){
        val intent = Intent(this, HelpActivity::class.java)
        startActivity(intent)
    }

    fun onChangeColorClick(view: View) {
        val intent = Intent(this, ColorActivity::class.java)
        colorResultLauncher.launch(intent)
    }

    val colorResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val colorId = result.data!!.getIntExtra(EXTRA_COLOR, R.color.yellow)
            lightOnColor = ContextCompat.getColor(this, colorId)
            setButtonColors()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(GAME_STATE, game.state)
    }
}