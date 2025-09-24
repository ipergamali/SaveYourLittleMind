package ioannapergamali.savejoannepink.model

import android.content.Context
import android.graphics.Rect
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
import kotlin.math.max
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
    val offsetsX = remember { mutableStateListOf<Float>() }
    val offsetsY = remember { mutableStateListOf<Float>() }

    var positionsInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(objects.size) {
        offsetsX.clear()
        offsetsY.clear()
        repeat(objects.size) {
            offsetsX.add(0f)
            offsetsY.add(0f)
        }
        positionsInitialized = false
    }

    LaunchedEffect(Unit) {
        while (true) {
            if (!positionsInitialized) {
                val occupied = mutableListOf<Pair<Float, Float>>()
                objects.forEachIndexed { index, obj ->
                    val width = obj.getObjectWidth(context).toFloat()
                    val height = obj.getObjectHeight(context).toFloat()
                    val initialX = findAvailableX(screenWidth, width, occupied)
                    offsetsX[index] = initialX
                    offsetsY[index] = -height
                    obj.offsetX = initialX
                    obj.offsetY = offsetsY[index]
                    obj.collected = false
                    occupied.add(initialX to width)
                }
                positionsInitialized = true
            }

            objects.forEachIndexed { index, obj ->
                offsetsY[index] += 3f * density
                obj.offsetX = offsetsX[index]
                obj.offsetY = offsetsY[index]

                if (offsetsY[index] > screenHeight) {
                    resetFallingObjectPosition(
                        index = index,
                        obj = obj,
                        objects = objects,
                        screenWidth = screenWidth,
                        context = context,
                        offsetsX = offsetsX,
                        offsetsY = offsetsY
                    )
                    return@forEachIndexed
                }

                if (!obj.collected && checkCollision(context, character, obj)) {
                    Log.d(
                        "Collision",
                        "Collision detected with object: ${obj.name} at (${offsetsX[index]}, ${offsetsY[index]})"
                    )
                    obj.collected = true
                    onCollision(obj, offsetsX[index], offsetsY[index])
                    resetFallingObjectPosition(
                        index = index,
                        obj = obj,
                        objects = objects,
                        screenWidth = screenWidth,
                        context = context,
                        offsetsX = offsetsX,
                        offsetsY = offsetsY
                    )
                    obj.collected = false
                }
            }
            delay(16) // Update every 16ms (roughly 60fps)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        objects.forEachIndexed { index, obj ->
            Box(
                modifier = Modifier.offset(
                    x = (offsetsX[index] / density).dp,
                    y = (offsetsY[index] / density).dp
                )
            ) {
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

private fun resetFallingObjectPosition(
    index: Int,
    obj: FallingObject,
    objects: List<FallingObject>,
    screenWidth: Int,
    context: Context,
    offsetsX: MutableList<Float>,
    offsetsY: MutableList<Float>
) {
    val width = obj.getObjectWidth(context).toFloat()
    val height = obj.getObjectHeight(context).toFloat()
    val others = offsetsX.indices
        .filter { it != index && it < objects.size }
        .map { offsetsX[it] to objects[it].getObjectWidth(context).toFloat() }
    val newX = findAvailableX(screenWidth, width, others)
    offsetsX[index] = newX
    offsetsY[index] = -height
    obj.offsetX = newX
    obj.offsetY = offsetsY[index]
}

fun checkCollision(
    context: Context,
    character: Character,
    obj: FallingObject
): Boolean {
    val characterBounds = character.getBounds()
    val objectBounds = obj.getBounds(context)

    Log.d(
        "Collision",
        "Character bounds: left=${characterBounds.left}, right=${characterBounds.right}, top=${characterBounds.top}, bottom=${characterBounds.bottom}"
    )
    Log.d(
        "Collision",
        "Object bounds: left=${objectBounds.left}, right=${objectBounds.right}, top=${objectBounds.top}, bottom=${objectBounds.bottom}"
    )

    val collisionDetected = Rect.intersects(characterBounds, objectBounds)
    Log.d("Collision", "Collision detected: $collisionDetected")
    return collisionDetected
}

fun initializeFallingObject(obj: FallingObject, initialX: Float, initialY: Float) {
    obj.offsetX = initialX
    obj.offsetY = initialY
    Log.d("FallingObject", "Initialized at X: $initialX, Y: $initialY")
}

private fun findAvailableX(
    screenWidth: Int,
    objectWidth: Float,
    occupied: List<Pair<Float, Float>>
): Float {
    val maxX = (screenWidth - objectWidth).coerceAtLeast(0f)
    if (maxX <= 0f) {
        return 0f
    }

    repeat(25) {
        val candidate = Random.nextFloat() * maxX
        val overlaps = occupied.any { (otherX, otherWidth) ->
            rangesOverlap(candidate, candidate + objectWidth, otherX, otherX + otherWidth)
        }
        if (!overlaps) {
            return candidate
        }
    }

    val sorted = occupied.sortedBy { it.first }
    var cursor = 0f
    sorted.forEach { (otherX, otherWidth) ->
        if (otherX - cursor >= objectWidth) {
            return cursor.coerceAtMost(maxX)
        }
        cursor = max(cursor, otherX + otherWidth)
    }

    return cursor.coerceAtMost(maxX)
}

private fun rangesOverlap(
    leftA: Float,
    rightA: Float,
    leftB: Float,
    rightB: Float
): Boolean {
    return leftA < rightB && rightA > leftB
}
