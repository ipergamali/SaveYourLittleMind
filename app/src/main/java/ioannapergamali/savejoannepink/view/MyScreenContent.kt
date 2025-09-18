package ioannapergamali.savejoannepink.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ioannapergamali.savejoannepink.model.FallingObject

@Composable
fun MyScreenContent(
    objects: List<FallingObject>,
    density: Float,
    context: Context,
    offsetX: Float,
    offsetY: Float
) {
    // Εδώ ορίστε το περιεχόμενο της οθόνης σας, χρησιμοποιώντας τα αντικείμενα και τα δεδομένα που περνάτε σαν παραμέτρους
    // Παράδειγμα:
    Box(modifier = Modifier.fillMaxSize()) {
        // Εδώ μπορείτε να τοποθετήσετε τα αντικείμενα που πέφτουν (falling objects)
        objects.forEach { obj ->
            Image(
                painter = painterResource(id = obj.imageResourceId),
                contentDescription = null,
                modifier = Modifier.offset(
                    x = (obj.offsetX / density).dp,
                    y = (obj.offsetY / density).dp
                )
            )
        }
    }
}
