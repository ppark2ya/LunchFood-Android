package com.lunchfood.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.lunchfood.R
import com.lunchfood.data.api.ApiHelper
import com.lunchfood.data.api.RetrofitBuilder
import com.lunchfood.data.model.User
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.ViewModelFactory
import com.lunchfood.ui.main.viewmodel.MainViewModel
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.login_activity.*

class KakaoLoginActivity: BaseActivity(TransitionMode.HORIZON) {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
        setupViewModel()

        // 회원가입 버튼
        signupBtn.text = HtmlCompat.fromHtml(getString(R.string.signup_comment), HtmlCompat.FROM_HTML_MODE_LEGACY)

        kakaoLoginBtn.setOnClickListener {
            // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니라면 카카오계정으로 로그인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@KakaoLoginActivity)) {
                Dlog.i("카카오톡 로그인")
                UserApiClient.instance.loginWithKakaoTalk(this@KakaoLoginActivity, callback = callback)
            } else {
                Dlog.i("카카오계정 로그인")
                UserApiClient.instance.loginWithKakaoAccount(this@KakaoLoginActivity, callback = callback)
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))
        ).get(MainViewModel::class.java)
    }

    private fun insertAccount(data: User) {
        viewModel.insertAccount(data).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
//                        recyclerView.visibility = View.VISIBLE
//                        progressBar.visibility = View.GONE
                        resource.data?.let {
                            res -> Dlog.i("유저정보 등록 성공")
                            val intent = Intent(this, AddressMapping::class.java)
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        }
                    }
                    Status.FAILURE -> {
//                        recyclerView.visibility = View.VISIBLE
//                        progressBar.visibility = View.GONE
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.PENDING -> {
//                        progressBar.visibility = View.VISIBLE
//                        recyclerView.visibility = View.GONE
                    }
                }
            }
        })
    }

    private val callback: ((OAuthToken?, Throwable?) -> Unit) = { token, error ->
        if (error != null) { //Login Fail
            Dlog.e("Kakao Login Failed :" + error)
        } else if (token != null) { //Login Success
            Dlog.i("로그인성공!!")
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Dlog.e("토큰 정보 보기 실패")
                }
                else if (tokenInfo != null) {
                    Dlog.i("회원번호: ${tokenInfo.id}" + "\n만료시간: ${tokenInfo.expiresIn}")
                    val user = User(tokenInfo.id)
                    insertAccount(user)
                }
            }
        }
    }
}
