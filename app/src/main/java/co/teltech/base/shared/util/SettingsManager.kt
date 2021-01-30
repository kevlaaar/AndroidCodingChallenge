package co.teltech.base.shared.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import java.util.*
import kotlin.collections.LinkedHashSet

class SettingsManager(private val prefsCache: PreferenceCache) {

    fun attachBaseContext(base: Context?): Context? {
        return updateSettings(base)
    }

    fun changeLanguage(activity: Activity?, languageCode: String) {
        prefsCache.locale = Locale.forLanguageTag(languageCode)
        updateSettings(activity)
        activity?.recreate()
    }

    fun changeUiMode(activity: Activity?, uiMode: Int) {
        prefsCache.uiMode = uiMode
        updateSettings(activity)
        activity?.recreate()
    }

    @Suppress("DEPRECATION")
    private fun updateSettings(base: Context?): Context? {
        val locale = prefsCache.locale
        Locale.setDefault(locale)
        val res = base?.resources
        val config = Configuration(res?.configuration)
        config.uiMode = prefsCache.uiMode
        setDefaultNightMode(config.uiMode)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocaleForApi24(config, locale)
            base?.createConfigurationContext(config)
        } else {
            config.setLocale(locale)
            base?.createConfigurationContext(config)
        }
    }

    private fun setDefaultNightMode(uiMode: Int?) {
        if (uiMode == Configuration.UI_MODE_NIGHT_NO)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun setLocaleForApi24(
        config: Configuration,
        locale: Locale
    ) {
        val set: MutableSet<Locale> = LinkedHashSet()
        set.add(locale)
        val all = LocaleList.getDefault()
        for (i in 0 until all.size()) {
            set.add(all[i])
        }
        val locales = set.toTypedArray()
        config.setLocales(LocaleList(*locales))
    }

    companion object {
        const val MONTENEGRIN = "sr-Latn-ME"
        const val ENGLISH = "en"
    }
}