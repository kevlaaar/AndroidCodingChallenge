package co.teltech.base.ui.settings

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import co.teltech.base.R
import co.teltech.base.shared.util.SettingsManager
import co.teltech.base.shared.util.SettingsManager.Companion.ENGLISH
import kotlinx.android.synthetic.main.fragment_settings.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val settingsManager: SettingsManager by inject()
    private var nightModeOn = false
    private val englishOn = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        main_toggle_language.setOnClickListener {
            settingsManager.changeLanguage(requireActivity(), ENGLISH)
        }

        main_toggle_mode.setOnClickListener {
            Timber.e("NAJT MOD $nightModeOn")
            nightModeOn = !nightModeOn
            settingsManager.changeUiMode(requireActivity(), if(nightModeOn) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO)
        }
    }
}