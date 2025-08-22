package com.alexdev.guid3.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object CustomIconRepository {
    private const val PREFS = "custom_icons_prefs"
    private const val KEY_SET = "hash_url_set" // each entry: hash|url

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun addCustomIcon(context: Context, hash: String?, url: String) {
        if (hash.isNullOrEmpty()) return
        val set = prefs(context).getStringSet(KEY_SET, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        // Avoid duplicates
        if (set.any { it.startsWith("$hash|") }) return
        set.add("$hash|$url")
        prefs(context).edit { putStringSet(KEY_SET, set) }
    }

    fun findUrlByHash(context: Context, hash: String): String? {
        val set = prefs(context).getStringSet(KEY_SET, emptySet()) ?: emptySet()
        return set.firstOrNull { it.startsWith("$hash|") }?.substringAfter('|')
    }

    fun getCustomIcons(context: Context): List<String> {
        val set = prefs(context).getStringSet(KEY_SET, emptySet()) ?: emptySet()
        return set.mapNotNull { it.substringAfter('|') }
    }

    fun removeCustomIcons(context: Context, urls: Collection<String>) {
        if (urls.isEmpty()) return
        val set = prefs(context).getStringSet(KEY_SET, emptySet())?.toMutableSet() ?: return
        val sizeBefore = set.size
        urls.forEach { targetUrl ->
            set.removeAll { entry -> entry.substringAfter('|') == targetUrl }
        }
        if (set.size != sizeBefore) {
            prefs(context).edit { putStringSet(KEY_SET, set) }
        }
    }

    fun clearAll(context: Context) {
        prefs(context).edit { remove(KEY_SET) }
    }
}