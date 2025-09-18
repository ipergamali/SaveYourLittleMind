package ioannapergamali.savejoannepink.viewModel

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import ioannapergamali.savejoannepink.R

class MainViewModel : ViewModel() {
    private lateinit var mediaPlayer: MediaPlayer

    fun initMediaPlayer(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.game_soundtrack)
        startMediaPlayer() // Καλείτε την startMediaPlayer() όταν ο MediaPlayer δημιουργείται
    }

    fun startMediaPlayer() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            mediaPlayer.isLooping = true
        }
    }

    fun stopMediaPlayer() {
        mediaPlayer.stop()
        mediaPlayer.prepare()
    }

    fun getObjectDimensions(context: Context): Pair<Int, Int> {
        val objectWidth = context.resources.getDimensionPixelSize(R.dimen.object_width)
        val objectHeight = context.resources.getDimensionPixelSize(R.dimen.object_height)
        return Pair(objectWidth, objectHeight)
    }

    companion object {
        fun dpToPx(dp: Dp, density: Float): Float {
            return dp.value * density
        }

    }
}