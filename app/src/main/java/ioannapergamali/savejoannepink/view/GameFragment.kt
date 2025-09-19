package ioannapergamali.savejoannepink.view

import android.content.Context
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
import ioannapergamali.savejoannepink.R
import ioannapergamali.savejoannepink.model.FallingObject
import ioannapergamali.savejoannepink.model.FallingObjectsContainer

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
        val imageView = remember {
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
        Glide.with(context)
            .asGif()
            .load(R.drawable.game_background)
            .into(imageView)

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

        var score by remember { mutableStateOf(100) }
        var maxScore by remember { mutableStateOf(100) }
        var wisdom by remember { mutableStateOf(character.getWisdom()) }

        fun handleCollision(obj: FallingObject, offsetX: Float, offsetY: Float) {
            Log.d("Collision", "handleCollision called for object: ${obj.name} at (${offsetX}, ${offsetY})")
            val scoreDelta = when (obj.type) {
                FallingObject.ObjectType.DAMAGE -> {
                    character.decreaseWisdom(10)
                    -10
                }
                FallingObject.ObjectType.WISDOM -> {
                    character.increaseWisdom(10)
                    10
                }
            }

            score = (score + scoreDelta).coerceAtLeast(0)
            if (score > maxScore) {
                maxScore = score
            }
            wisdom = character.getWisdom()
            Log.d("Collision", "Score: $score, MaxScore: $maxScore, Wisdom: $wisdom")
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { imageView },
                modifier = Modifier.matchParentSize()
            )

            FallingObjectsContainer(
                objects = fallingObjects,
                screenWidth = width,
                screenHeight = height,
                character = character,
                onCollision = ::handleCollision
            )

            CharacterContainer(character, width, height)

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Score: $score", fontSize = 24.sp, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                CustomProgressBar(
                    score = score,
                    maxScore = maxScore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                )
            }
        }
    }
}
