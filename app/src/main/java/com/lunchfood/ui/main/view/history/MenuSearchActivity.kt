package com.lunchfood.ui.main.view.history

import android.content.Intent
import android.os.Bundle
import com.lunchfood.R
import com.lunchfood.ui.base.BaseActivity

class MenuSearchActivity : BaseActivity(TransitionMode.HORIZON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_search)

        val intent = Intent();
        intent.putExtra("result", "some value");
        setResult(RESULT_OK, intent);
        finish();
    }
}