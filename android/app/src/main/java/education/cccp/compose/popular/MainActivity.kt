package education.cccp.compose.popular

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.compose.jetsurvey.R.id.nav_host_fragment
import com.example.compose.jetsurvey.R.id.nav_view
import com.example.compose.jetsurvey.R.layout.activity_main
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        findViewById<NavigationView>(nav_view).apply {
            setupWithNavController(findNavController(nav_host_fragment))
        }
    }
}