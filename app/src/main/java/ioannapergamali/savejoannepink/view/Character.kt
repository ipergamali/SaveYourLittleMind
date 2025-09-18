package ioannapergamali.savejoannepink.view

import android.graphics.Rect
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ioannapergamali.savejoannepink.App
import ioannapergamali.savejoannepink.viewModel.MainViewModel

class Character(
    private var name: String,
    private var wisdom: Int,
    private var imagePainter: Painter
) {
    var offsetX by mutableStateOf(0f)
    var currentXPosition: Float = 0f
    var currentYPosition: Float = 0f

    fun getBounds(): Rect {
        val context = App.context
        val density = context.resources.displayMetrics.density
        val width = (getCharacterWidth() / density).toInt()
        val height = (getCharacterHeight() / density).toInt()
        val x = currentXPosition.toInt()
        val y = currentYPosition.toInt()

        Log.d("CharacterBounds", "Character bounds: left=$x, right=${x + width}, top=$y, bottom=${y + height}")

        return Rect(
            x,
            y,
            x + width,
            y + height
        )
    }

    fun move(dragAmountX: Float, screenWidth: Int, characterWidth: Int) {
        offsetX = (offsetX + dragAmountX).coerceIn(0f, screenWidth - characterWidth.toFloat())
    }

    fun getName(): String = name
    fun getWisdom(): Int = wisdom
    fun getImagePainter(): Painter = imagePainter

    fun increaseWisdom(amount: Int) {
        wisdom += amount
    }

    fun decreaseWisdom(amount: Int) {
        wisdom -= amount
    }

    fun getCharacterWidth(): Int {
        val bitmapPainter = imagePainter as BitmapPainter
        val bitmapSize = bitmapPainter.intrinsicSize
        return bitmapSize.width.toInt()
    }

    fun getCharacterHeight(): Int {
        val bitmapPainter = imagePainter as BitmapPainter
        val bitmapSize = bitmapPainter.intrinsicSize
        return bitmapSize.height.toInt()
    }
}

@Composable
fun CharacterContainer(character: Character, screenWidth: Int, screenHeight: Int) {
    val characterWidth = character.getCharacterWidth()
    val density = LocalDensity.current.density

    Box(modifier = Modifier.fillMaxSize()) {
        var initialOffsetX by remember { mutableStateOf(162f) }
        var initialOffsetY by remember { mutableStateOf(565f) }
        var initialPositionSet by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = initialPositionSet) {
            if (!initialPositionSet) {
                character.offsetX = MainViewModel.dpToPx(dp = initialOffsetX.dp, density = density)
                character.currentYPosition = MainViewModel.dpToPx(dp = initialOffsetY.dp, density = density)
                initialPositionSet = true
            }
        }

        Box(modifier = Modifier.offset(x = (character.offsetX / density).dp, y = (character.currentYPosition / density).dp)) {
            Image(
                painter = character.getImagePainter(),
                contentDescription = character.getName(),
                modifier = Modifier.pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        character.move(dragAmount.x * density, screenWidth, characterWidth)
                        character.currentXPosition = character.offsetX
                    }
                }
            )
        }
    }
}
