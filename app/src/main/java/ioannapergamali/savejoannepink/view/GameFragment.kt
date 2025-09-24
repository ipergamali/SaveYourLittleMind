package ioannapergamali.savejoannepink.view

import CustomProgressBar
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ioannapergamali.savejoannepink.App
import ioannapergamali.savejoannepink.R
import ioannapergamali.savejoannepink.model.FallingObject
import ioannapergamali.savejoannepink.model.FallingObjectsContainer

private enum class GameState {
    RUNNING,
    WON,
    LOST
}

class GameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                GameContent()
            }
        }
    }

    @Composable
    fun GameContent() {
        val context = LocalContext.current

        val characterPainter = painterResource(id = R.drawable.character)
        val character = remember { Character("JoannaPink", 100, characterPainter) }

        val fallingObjects = remember {
            mutableStateListOf(
                FallingObject("Wisdom", R.drawable.wisdom_item, FallingObject.ObjectType.WISDOM),
                FallingObject("Damage", R.drawable.damage_item, FallingObject.ObjectType.DAMAGE)
            )
        }

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val width: Int
        val height: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            width = windowMetrics.bounds.width()
            height = windowMetrics.bounds.height()
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            width = displayMetrics.widthPixels
            height = displayMetrics.heightPixels
        }

        var score by remember { mutableStateOf(30) }
        var gameState by remember { mutableStateOf(GameState.RUNNING) }

        fun adjustScore(delta: Int) {
            if (gameState != GameState.RUNNING) {
                return
            }

            score = (score + delta).coerceIn(0, 100)

            when {
                score >= 100 -> {
                    gameState = GameState.WON
                    Log.d("GameState", "Player won with score: $score")
                }
                score <= 0 -> {
                    gameState = GameState.LOST
                    Log.d("GameState", "Player lost with score: $score")
                }
            }
        }

        fun handleCollision(obj: FallingObject, _: Float, _: Float) {
            Log.d("Collision", "handleCollision called for object: ${obj.name}")

            if (gameState != GameState.RUNNING) {
                return
            }

            val context = App.context
            val characterBounds = character.getBounds()
            val objectBounds = obj.getBounds(context)

            Log.d("Collision", "Character bounds: $characterBounds")
            Log.d("Collision", "Object bounds: $objectBounds")

            if (Rect.intersects(characterBounds, objectBounds)) {
                Log.d("Collision", "Collision detected with object: ${obj.name}")
                when (obj.type) {
                    FallingObject.ObjectType.DAMAGE -> {
                        character.decreaseWisdom(10)
                        adjustScore(-10)
                    }
                    FallingObject.ObjectType.WISDOM -> {
                        character.increaseWisdom(10)
                        adjustScore(10)
                    }
                }
                Log.d("Collision", "Score: $score")
            } else {
                Log.d("Collision", "No collision detected")
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { viewContext ->
                    ImageView(viewContext).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        scaleType = ImageView.ScaleType.CENTER_CROP
                        Glide.with(viewContext)
                            .asGif()
                            .load(R.drawable.game_background)
                            .into(this)
                    }
                },
                modifier = Modifier.matchParentSize()
            )

            if (gameState == GameState.RUNNING) {
                FallingObjectsContainer(
                    objects = fallingObjects,
                    screenWidth = width,
                    screenHeight = height,
                    character = character,
                    onCollision = { obj, objOffsetX, objOffsetY -> handleCollision(obj, objOffsetX, objOffsetY) }
                )
            }

            CharacterContainer(character, width, height)

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Score: $score", fontSize = 24.sp, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomProgressBar(
                        progress = score / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                    )
                }
            }

            if (gameState != GameState.RUNNING) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val resultText = when (gameState) {
                            GameState.WON -> "win"
                            GameState.LOST -> "lose"
                            else -> ""
                        }
                        if (resultText.isNotEmpty()) {
                            Text(text = resultText, fontSize = 48.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                        Text(text = "game over", fontSize = 32.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
