package co.teltech.base.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import co.teltech.base.R
import co.teltech.base.shared.util.SettingsManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    val viewModel: MainViewModel by viewModel()

    private val settingsManager: SettingsManager by inject()

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(settingsManager.attachBaseContext(newBase))
    }
}