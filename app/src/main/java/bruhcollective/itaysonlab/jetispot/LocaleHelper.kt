package bruhcollective.itaysonlab.jetispot

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.core.os.LocaleListCompat
import bruhcollective.itaysonlab.jetispot.core.util.Log
import java.util.*

class LocaleHelper {

    companion object {
        fun onAttach(context: Context): Context {
            val lang = getPersistedData(context, Locale.getDefault().language)
            return setLocale(context, lang)
        }

        fun onAttach(context: Context, defaultLanguage: String): Context {
            val lang = getPersistedData(context, defaultLanguage)
            return setLocale(context, lang)
        }

        fun getLanguage(context: Context): String {
            return getPersistedData(context, Locale.getDefault().language)
        }

        fun setLocale(context: Context, language: String): Context {
            val locale_LocaleCompat = LocaleListCompat.getAdjustedDefault()[0]?.toLanguageTag().toString()
            Log.d("LocaleHelper", "setLocale: $locale_LocaleCompat")
            persist(context, language)

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                updateResources(context, language)
            } else updateResourcesLegacy(context, language)
        }

        private fun getPersistedData(context: Context, defaultLanguage: String): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString("Language", defaultLanguage)!!
        }

        private fun persist(context: Context, language: String) {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor = preferences.edit()

            editor.putString("Language", language)
            editor.apply()
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun updateResources(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val configuration = context.resources.configuration
            configuration.setLocale(locale)
            configuration.setLayoutDirection(locale)

            return context.createConfigurationContext(configuration)
        }

        private fun updateResourcesLegacy(context: Context, language: String): Context {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val resources = context.resources

            val configuration = resources.configuration

            val config = Configuration()
            if (language.isEmpty()) {
                val emptyLocaleList = LocaleListCompat.getEmptyLocaleList()
                config.setLocale(emptyLocaleList[0])
            } else {
                config.setLocale(locale)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                configuration.setLayoutDirection(locale)
            }

            resources.updateConfiguration(configuration, resources.displayMetrics)

            return context
        }
    }
}
