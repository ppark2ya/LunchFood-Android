package com.lunchfood.ui.main.view

import android.os.Bundle
import android.text.InputType
import com.lunchfood.R
import com.lunchfood.data.model.HistoryResponse
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.utils.Dlog
import kotlinx.android.synthetic.main.activity_menu_regist.*

class MenuRegistActivity : BaseActivity(TransitionMode.HORIZON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_regist)
        setCommonHeaderText(getString(R.string.day_menu_regist))

        val dayMenu = intent.getSerializableExtra("dayMenu") as HistoryResponse
        Dlog.i("데이메뉴::: $dayMenu")

        etHistoryPlaceName.let {
            it.setText(dayMenu.placeName)
            it.inputType = InputType.TYPE_NULL
        }

        etHistoryMenuName.let {
            it.setText(dayMenu.menuName)
            it.inputType = InputType.TYPE_NULL
        }
    }

}