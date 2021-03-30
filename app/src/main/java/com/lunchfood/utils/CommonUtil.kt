package com.lunchfood.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


class CommonUtil {

    companion object {
        private val mapper = jacksonObjectMapper().setVisibility(
            PropertyAccessor.FIELD,
            JsonAutoDetect.Visibility.ANY
        )

        fun convertFromDataClassToMap(DataClass: Any): HashMap<String, Any> {
            return mapper.convertValue(DataClass, Map::class.java) as HashMap<String, Any>
        }

        fun <T>convertFromMapToDataClass(convertMap: Map<String, Any>, type: Class<T>): T {
            return mapper.convertValue(convertMap, type)
        }

        fun hideKeyboard(activity: Activity) {
            val imm: InputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view: View? = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}