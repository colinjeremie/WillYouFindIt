package com.github.colinjeremie.willyoufindit.utils

import android.content.Context
import android.content.SharedPreferences

object SplashScreenHelper {
    private val TUTO = "TUTO"
    private val IS_FIRST_TIME_LAUNCHED = "IS_FIRST_TIME_LAUNCHED"

    fun tutoDone(pContext: Context) {
        val pref = pContext.getSharedPreferences(TUTO, Context.MODE_PRIVATE)

        pref.edit().putBoolean(IS_FIRST_TIME_LAUNCHED, false).apply()
    }

    fun isFirstTimeLaunched(context: Context) =
            context.getSharedPreferences(TUTO, Context.MODE_PRIVATE).getBoolean(IS_FIRST_TIME_LAUNCHED, true)
}