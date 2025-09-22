package ioannapergamali.savejoannepink.model

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ioannapergamali.savejoannepink.view.Character
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random

private const val ROTATION_SPEED_DEGREES = 2f
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
    val offsetsX = remember(objects.size) {
        mutableStateListOf<Float>().apply { repeat(objects.size) { add(0f) } }
    }
    val offsetsY = remember(objects.size) {
        mutableStateListOf<Float>().apply { repeat(objects.size) { add(0f) } }
    }
    val rotationAngles = remember(objects.size) {
        mutableStateListOf<Float>().apply { repeat(objects.size) { add(0f) } }
    }

    LaunchedEffect(objects.size) {
        objects.forEachIndexed { index, obj ->
            placeObject(
                index = index,
                obj = obj,
                objects = objects,
                offsetsX = offsetsX,
                offsetsY = offsetsY,
                rotationAngles = rotationAngles,
                context = context,
                screenWidth = screenWidth,
                screenHeight = screenHeight,
                indicesToCheck = 0 until index
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            objects.forEachIndexed { index, obj ->
                offsetsY[index] = offsetsY[index] + 3f * density
                rotationAngles[index] = (rotationAngles[index] + ROTATION_SPEED_DEGREES) % 360f

                if (offsetsY[index] > screenHeight) {
                    resetObjectPosition(
                        index = index,
                        obj = obj,
                        objects = objects,
                        offsetsX = offsetsX,
                        offsetsY = offsetsY,
                        rotationAngles = rotationAngles,
                        context = context,
                        screenWidth = screenWidth,
                        screenHeight = screenHeight
                    )
                    return@forEachIndexed
                }

                obj.offsetX = offsetsX[index]
                obj.offsetY = offsetsY[index]

                if (checkCollision(context, character, obj, offsetsX[index], offsetsY[index])) {
                    if (!obj.collected) {
                        obj.collected = true
                        Log.d(
                            "Collision",
                            "Collision detected with object: ${obj.name} at (${offsetsX[index]}, ${offsetsY[index]})"
                        )
                        onCollision(obj, offsetsX[index], offsetsY[index])
                        resetObjectPosition(
                            index = index,
                            obj = obj,
                            objects = objects,
                            offsetsX = offsetsX,
                            offsetsY = offsetsY,
                            rotationAngles = rotationAngles,
                            context = context,
                            screenWidth = screenWidth,
                            screenHeight = screenHeight
                        )
                        obj.collected = false
                    }
                    return@forEachIndexed
                }
            }
            delay(16)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        objects.forEachIndexed { index, obj ->
            Box(modifier = Modifier.offset(x = (offsetsX[index] / density).dp, y = (offsetsY[index] / density).dp)) {
                Image(
                    painter = painterResource(id = obj.imageResourceId),
                    contentDescription = obj.name,
                    modifier = Modifier
                        .width((obj.getObjectWidth(context) / density).dp)
                        .height((obj.getObjectHeight(context) / density).dp)
                        .graphicsLayer { rotationZ = rotationAngles[index] }
                )
            }
        }
    }
}

private fun generateNonOverlappingX(
    currentIndex: Int,
    objects: List<FallingObject>,
    offsetsX: List<Float>,
    context: Context,
    screenWidth: Int,
    indicesToCheck: Iterable<Int>
): Float {
    val objectWidth = objects[currentIndex].getObjectWidth(context)
    val maxX = (screenWidth - objectWidth).coerceAtLeast(0)
    val takenRanges = indicesToCheck.mapNotNull { otherIndex ->
        val otherX = offsetsX.getOrNull(otherIndex) ?: return@mapNotNull null
        val otherWidth = objects[otherIndex].getObjectWidth(context)
        otherX to otherX + otherWidth
    }.sortedBy { it.first }

    if (takenRanges.isEmpty()) {
        return Random.nextInt(0, maxX + 1).toFloat()
    }

    repeat(30) {
        val candidateX = Random.nextInt(0, maxX + 1).toFloat()
        val overlaps = takenRanges.any { range ->
            candidateX < range.second && candidateX + objectWidth > range.first
        }
        if (!overlaps) {
            return candidateX
        }
    }

    var candidateX = 0f
    takenRanges.forEach { range ->
        if (candidateX + objectWidth <= range.first) {
            return candidateX.coerceIn(0f, maxX.toFloat())
        }
        candidateX = max(candidateX, range.second)
    }

    return candidateX.coerceAtMost(maxX.toFloat())
}

fun checkCollision(
    context: Context,
    character: Character,
    obj: FallingObject,
    objOffsetX: Float,
    objOffsetY: Float
): Boolean {
    val charX = character.offsetX
    val charY = 520f // Fixed Y position of the character
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

private fun resetObjectPosition(
    index: Int,
    obj: FallingObject,
    objects: List<FallingObject>,
    offsetsX: MutableList<Float>,
    offsetsY: MutableList<Float>,
    rotationAngles: MutableList<Float>,
    context: Context,
    screenWidth: Int,
    screenHeight: Int
) {
    placeObject(
        index = index,
        obj = obj,
        objects = objects,
        offsetsX = offsetsX,
        offsetsY = offsetsY,
        rotationAngles = rotationAngles,
        context = context,
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        indicesToCheck = objects.indices.filter { it != index }
    )
}

private fun placeObject(
    index: Int,
    obj: FallingObject,
    objects: List<FallingObject>,
    offsetsX: MutableList<Float>,
    offsetsY: MutableList<Float>,
    rotationAngles: MutableList<Float>,
    context: Context,
    screenWidth: Int,
    screenHeight: Int,
    indicesToCheck: Iterable<Int>
) {
    val newX = generateNonOverlappingX(
        currentIndex = index,
        objects = objects,
        offsetsX = offsetsX,
        context = context,
        screenWidth = screenWidth,
        indicesToCheck = indicesToCheck
    )
    val objHeight = obj.getObjectHeight(context)
    val maxOffset = (objHeight + screenHeight / 3).coerceAtLeast(objHeight + 1)
    val newY = -Random.nextInt(objHeight, maxOffset).toFloat()

    offsetsX[index] = newX
    offsetsY[index] = newY
    rotationAngles[index] = Random.nextFloat() * 360f
    obj.offsetX = newX
    obj.offsetY = newY
}
