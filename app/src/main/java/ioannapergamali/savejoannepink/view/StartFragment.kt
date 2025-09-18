package ioannapergamali.savejoannepink.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import ioannapergamali.savejoannepink.R
import ioannapergamali.savejoannepink.model.MainActivity

class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                StartContent()
//                val mainViewModel = MainViewModel() // Δημιουργία ενός αντικειμένου της MainViewModel
//                MainContent(viewModel = mainViewModel)
            }
        }
    }

    @Composable
    fun StartContent() {
        val buttonPressed = remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF3762A2))
        ) {
            val context = LocalContext.current
            val imageView = remember { ImageView(context) }
            Glide.with(context)
                .asGif()
                .load(R.drawable.start)
                .into(imageView)

            AndroidView(
                factory = { imageView },
                modifier = Modifier.matchParentSize()
            )

            Button(
                onClick = {
                    buttonPressed.value = true
                    (context as? MainActivity)?.navigateToGame()
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF799E38)),
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 200.dp)
                    .background(
                        color = Color.Transparent,
                        shape = MaterialTheme.shapes.extraLarge
                    )

                //.size(100.dp) // Εδώ καθορίζουμε το μέγεθος του κουμπιού
            ) {
                Text(
                    "START",
                    color =Color(0xFF3762A2),
                    fontSize = 86.sp,
                    fontFamily = FontFamily(Font(R.font.truite, FontWeight.Black)
                    ))
            }
        }
    }
}