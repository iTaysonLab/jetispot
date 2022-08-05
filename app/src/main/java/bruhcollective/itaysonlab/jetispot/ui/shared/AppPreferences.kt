package bruhcollective.itaysonlab.jetispot.ui.shared

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit

object AppPreferences {
    private var sharedPreferences: SharedPreferences? = null

    fun setup(context: Context) {
        sharedPreferences = context.getSharedPreferences("jetispot.sharedprefs", MODE_PRIVATE)
    }

    var ColorScheme: String?
        get() = Key.ColorScheme.getString(defValue = "#1DB954", elseValue = "#1DB954")
        set(value) = Key.ColorScheme.setString(value)

    var UseGrid: Boolean?
        get() = Key.UseGrid.getBoolean(defValue = false, elseValue = false)
        set(value) = Key.UseGrid.setBoolean(value)


    private enum class Key {
        ColorScheme, UseGrid;

        fun getBoolean(defValue: Boolean = false, elseValue: Boolean? = null): Boolean? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getBoolean(name, defValue) else elseValue
        fun getFloat(defValue: Float = 0f, elseValue: Float? = null): Float? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getFloat(name, defValue) else elseValue
        fun getInt(defValue: Int = 0, elseValue: Int? = null): Int? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getInt(name, defValue) else elseValue
        fun getLong(defValue: Long = 0, elseValue: Long? = null): Long? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getLong(name, defValue) else elseValue
        fun getString(defValue: String = "", elseValue: String? = null): String? = if (sharedPreferences?.contains(name) == true) sharedPreferences!!.getString(name, defValue) else elseValue

        fun setBoolean(value: Boolean?) = value?.let { sharedPreferences!!.edit { putBoolean(name, value) } } ?: remove()
        fun setFloat(value: Float?) = value?.let { sharedPreferences!!.edit { putFloat(name, value) } } ?: remove()
        fun setInt(value: Int?) = value?.let { sharedPreferences!!.edit { putInt(name, value) } } ?: remove()
        fun setLong(value: Long?) = value?.let { sharedPreferences!!.edit { putLong(name, value) } } ?: remove()
        fun setString(value: String?) = value?.let { sharedPreferences!!.edit { putString(name, value) } } ?: remove()

        fun remove() = sharedPreferences!!.edit { remove(name) }
    }
}