package co.teltech.base.shared.util

import android.content.Context
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import java.util.*

class PreferenceCache(val context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)

    var locale: Locale
        get() = Locale.forLanguageTag(prefs.getString(LOCALE, Locale.ENGLISH.language).orEmpty())
        set(value) = with(prefs.edit()) {
            putString(LOCALE, value.toLanguageTag())
            apply()
        }

    var uiMode: Int
        get() = prefs.getInt(UI_MODE, Configuration.UI_MODE_NIGHT_NO)
        set(value) = with(prefs.edit()) {
            putInt(UI_MODE, value)
            apply()
        }
    var accessToken: String
        get() = prefs.getString(AUTH_TOKEN, "").orEmpty()
        set(value) = with(prefs.edit()) {
            putString(AUTH_TOKEN, value)
            apply()
        }
    fun clear() {
        prefs.edit()
            .remove(AUTH_TOKEN)
            .remove(LOCALE)
            .remove(UI_MODE)
            .apply()
    }

    companion object {
        private const val AUTH_TOKEN = "authentication_token"
        private const val LOCALE = "locale"
        private const val UI_MODE = "ui_mode"
    }
}