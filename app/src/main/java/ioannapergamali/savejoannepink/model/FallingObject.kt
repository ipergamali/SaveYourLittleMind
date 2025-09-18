package ioannapergamali.savejoannepink.model

import android.content.Context
import android.graphics.Rect
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

data class FallingObject(
    val name: String,
    @DrawableRes val imageResourceId: Int,
    val type: ObjectType,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    var collected: Boolean = false // Add this property
) {
    enum class ObjectType {
        DAMAGE,
        WISDOM
    }

    fun getBounds(context: Context): Rect {
        val width = getObjectWidth(context)
        val height = getObjectHeight(context)

        return Rect(
            offsetX.toInt(),
            offsetY.toInt(),
            (offsetX + width).toInt(),
            (offsetY + height).toInt()
        )
    }

    fun getObjectWidth(context: Context): Int {
        val drawable = ContextCompat.getDrawable(context, imageResourceId)
        return drawable?.intrinsicWidth ?: 0
    }

    fun getObjectHeight(context: Context): Int {
        val drawable = ContextCompat.getDrawable(context, imageResourceId)
        return drawable?.intrinsicHeight ?: 0
    }
}
