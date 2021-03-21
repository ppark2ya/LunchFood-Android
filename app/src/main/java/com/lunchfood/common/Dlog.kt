package com.lunchfood.common

import android.util.Log
import com.lunchfood.BuildConfig
import java.lang.StringBuilder

class Dlog {
    companion object {
        val TAG = "LunchFood"

        /** Log Level Error **/
        fun e(message: String) {
            if(BuildConfig.DEBUG) Log.e(
                TAG,
                buildLogMsg(message)
            )
        }

        /** Log Level Warning **/
        fun w(message: String) {
            if(BuildConfig.DEBUG) Log.w(
                TAG,
                buildLogMsg(message)
            )
        }

        /** Log Level Information **/
        fun i(message: String) {
            if(BuildConfig.DEBUG) Log.i(
                TAG,
                buildLogMsg(message)
            )
        }

        /** Log Level Debug **/
        fun d(message: String) {
            if(BuildConfig.DEBUG) Log.d(
                TAG,
                buildLogMsg(message)
            )
        }

        /** Log Level Verbose **/
        fun v(message: String) {
            if(BuildConfig.DEBUG) Log.v(
                TAG,
                buildLogMsg(message)
            )
        }

        fun buildLogMsg(message: String): String {
            val ste: StackTraceElement = Thread.currentThread().stackTrace[4]

            var sb = StringBuilder()
            sb.append("[")
            sb.append(ste.fileName.replace(".java", ""))
            sb.append("::")
            sb.append(ste.methodName)
            sb.append("]")
            sb.append(message)

            return sb.toString()
        }
    }
}