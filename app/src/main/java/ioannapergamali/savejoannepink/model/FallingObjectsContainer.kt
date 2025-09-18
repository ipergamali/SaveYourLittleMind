package ioannapergamali.savejoannepink.model

import android.content.Context
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import ioannapergamali.savejoannepink.view.Character
import kotlinx.coroutines.delay
import kotlin.random.Random
@Composable
fun FallingObjectsContainer(
    objects: List<FallingObject>,
    screenWidth: Int,
    screenHeight: Int,
    character: Character,
    onCollision: (FallingObject, Float, Float) -> Unit
) {
    val density = LocalDensity.current.density
    val context = LocalContext.current

    // State variables for the offsets
    val offsetsX = remember { objects.map { mutableStateOf(it.offsetX) } }
    val offsetsY = remember { objects.map { mutableStateOf(it.offsetY) } }

    LaunchedEffect(Unit) {
        while (true) {
            objects.filter { !it.collected }.forEachIndexed { index, obj ->
                // Update the vertical position of the object
                offsetsY[index].value += 3f * density
                if (offsetsY[index].value > screenHeight) {
                    offsetsY[index].value = 0f
                    offsetsX[index].value = Random.nextFloat() * (screenWidth - obj.getObjectWidth(context) / density)
                }
                // Check for collision with the character
                if (checkCollision(context, character, obj, offsetsX[index].value, offsetsY[index].value)) {
                    Log.d("Collision", "Collision detected with object: ${obj.name} at (${offsetsX[index].value}, ${offsetsY[index].value})")
                    onCollision(obj, offsetsX[index].value, offsetsY[index].value)
                }
            }
            delay(16) // Update every 16ms (roughly 60fps)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        objects.forEachIndexed { index, obj ->
            Box(modifier = Modifier.offset(x = (offsetsX[index].value / density).dp, y = (offsetsY[index].value / density).dp)) {
                val imageView = remember { ImageView(context) }
                Glide.with(context)
                    .load(obj.imageResourceId)
                    .into(imageView)

                AndroidView(
                    factory = { imageView },
                    modifier = Modifier
                        .width((obj.getObjectWidth(context) / density).dp)
                        .height((obj.getObjectHeight(context) / density).dp)
                )
            }
        }
    }
}

fun checkCollision(
    context: Context,
    character: Character,
    obj: FallingObject,
    objOffsetX: Float,
    objOffsetY: Float
): Boolean {
    val charX = character.offsetX
    val charY = character.currentYPosition
    val charWidth = character.getCharacterWidth()
    val charHeight = character.getCharacterHeight()
    val objWidth = obj.getObjectWidth(context)
    val objHeight = obj.getObjectHeight(context)

    Log.d("Collision", "Character bounds: left=$charX, right=${charX + charWidth}, top=$charY, bottom=${charY + charHeight}")
    Log.d("Collision", "Object bounds: left=$objOffsetX, right=${objOffsetX + objWidth}, top=$objOffsetY, bottom=${objOffsetY + objHeight}")

    // Calculate the boundaries of the character and the FallingObject
    val charLeft = charX
    val charRight = charX + charWidth
    val charTop = charY
    val charBottom = charY + charHeight

    val objLeft = objOffsetX
    val objRight = objOffsetX + objWidth
    val objTop = objOffsetY
    val objBottom = objOffsetY + objHeight

    // Check for collision based on the boundaries
    val collisionDetected = charLeft < objRight && charRight > objLeft && charTop < objBottom && charBottom > objTop
    Log.d("Collision", "Collision detected: $collisionDetected")
    return collisionDetected
}

fun initializeFallingObject(obj: FallingObject, initialX: Float, initialY: Float) {
    obj.offsetX = initialX
    obj.offsetY = initialY
    Log.d("FallingObject", "Initialized at X: $initialX, Y: $initialY")
}
