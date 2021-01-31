package co.teltech.base.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import co.teltech.base.R
import co.teltech.base.shared.util.PreferenceCache
import co.teltech.base.shared.util.SettingsManager
import co.teltech.base.shared.util.SettingsManager.Companion.ENGLISH
import co.teltech.base.shared.util.SettingsManager.Companion.MONTENEGRIN
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val settingsManager: SettingsManager by inject()
    private val preferenceCache: PreferenceCache by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        englishFlag.setOnClickListener {
            settingsManager.changeLanguage(requireActivity(), ENGLISH)
        }
        montenegroFlag.setOnClickListener {
            settingsManager.changeLanguage(requireActivity(), MONTENEGRIN)
        }
        sunIcon.setOnClickListener { settingsManager.changeUiMode(requireActivity(), Configuration.UI_MODE_NIGHT_NO) }
        moonIcon.setOnClickListener { settingsManager.changeUiMode(requireActivity(), Configuration.UI_MODE_NIGHT_YES) }
    }
}