package ioannapergamali.savejoannepink.model

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import ioannapergamali.savejoannepink.view.GameFragment
import ioannapergamali.savejoannepink.view.StartFragment
import ioannapergamali.savejoannepink.viewModel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.initMediaPlayer(this)
    }

    @Composable
    fun MainContent() {
        supportFragmentManager.commit {
            replace(android.R.id.content, StartFragment())
        }
    }

    fun navigateToGame() {
        supportFragmentManager.commit {
            replace(android.R.id.content, GameFragment())
            addToBackStack(null)
        }
    }
}