package com.lunchfood.utils

import android.content.Context
import android.content.SharedPreferences
import com.lunchfood.ui.base.GlobalApplication


class PreferenceManager {
    companion object {
        private const val PREFERENCES_NAME = "lunch_security_preference"
        private const val DEFAULT_VALUE_STRING = ""
        private const val DEFAULT_VALUE_BOOLEAN = false
        private const val DEFAULT_VALUE_INT = -1
        private const val DEFAULT_VALUE_LONG = -1L
        private const val DEFAULT_VALUE_FLOAT = -1f

        private val sharedPreferences: SharedPreferences by lazy {
            val context = GlobalApplication.instance
            context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
//            val masterKey = MasterKey.Builder(context).build()
//            EncryptedSharedPreferences.create(
//                context,
//                PREFERENCES_NAME,
//                masterKey,
//                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//            )
        }

        /**
         * String 값 저장
         * @param key
         * @param value
         */
        fun setString(key: String, value: String?) {
            sharedPreferences.edit().putString(key, value).apply()
        }

        /**
         * boolean 값 저장
         * @param key
         * @param value
         */
        fun setBoolean(key: String, value: Boolean) {
            sharedPreferences.edit().putBoolean(key, value).apply()
        }

        /**
         * int 값 저장
         * @param key
         * @param value
         */
        fun setInt(key: String, value: Int) {
            sharedPreferences.edit().putInt(key, value).apply()
        }

        /**
         * long 값 저장
         * @param key
         * @param value
         */
        fun setLong(key: String, value: Long) {
            sharedPreferences.edit().putLong(key, value).apply()
        }

        /**
         * float 값 저장
         * @param key
         * @param value
         */
        fun setFloat(key: String, value: Float) {
            sharedPreferences.edit().putFloat(key, value).apply()
        }

        /**
         * String 값 로드
         * @param key
         * @return
         */
        fun getString(key: String): String? {
            return sharedPreferences.getString(key, DEFAULT_VALUE_STRING)
        }

        /**
         * boolean 값 로드
         * @param context
         * @param key
         * @return
         */
        fun getBoolean(key: String, defaultValue: Boolean = DEFAULT_VALUE_BOOLEAN): Boolean {
            return sharedPreferences.getBoolean(key, defaultValue)
        }

        /**
         * int 값 로드
         * @param key
         * @return
         */
        fun getInt(key: String, defaultValue: Int = DEFAULT_VALUE_INT): Int {
            return sharedPreferences.getInt(key, defaultValue)
        }

        /**
         * long 값 로드
         * @param key
         * @return
         */
        fun getLong(key: String, defaultValue: Long = DEFAULT_VALUE_LONG): Long {
            return sharedPreferences.getLong(key, defaultValue)
        }

        /**
         * float 값 로드
         * @param key
         * @return
         */
        fun getFloat(key: String, defaultValue: Float = DEFAULT_VALUE_FLOAT): Float {
            return sharedPreferences.getFloat(key, defaultValue)
        }

        /**
         * 키 값 삭제
         * @param key
         */
        fun removeKey(key: String) {
            sharedPreferences.edit().remove(key).apply()
        }

        /**
         * 모든 저장 데이터 삭제
         */
        fun clear() {
            sharedPreferences.edit().clear().apply()
        }
    }
}