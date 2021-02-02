package co.teltech.base.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import co.teltech.base.R
import co.teltech.base.shared.util.SettingsManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    val viewModel: MainViewModel by viewModel()
    private lateinit var navController: NavController
    private val settingsManager: SettingsManager by inject()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(settingsManager.attachBaseContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.employeeDetailsFragment) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
            }
        }
    }
}