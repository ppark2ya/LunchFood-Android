package com.lunchfood.ui.main.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.text.HtmlCompat
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.lunchfood.R
import com.lunchfood.data.model.User
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.PreferenceManager
import com.lunchfood.utils.Status
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.launch

class KakaoLoginActivity: BaseActivity(TransitionMode.HORIZON) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

    private fun insertAccount(data: User) {
        GlobalApplication.getViewModel()!!.insertAccount(data).observe(this, {
            it?.let { resource ->
                when (resource.status) {
                    // 로딩
                    Status.PENDING -> {
//                        progressBar.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
//                        recyclerView.visibility = View.VISIBLE
                        resource.data?.let {
                            res -> Dlog.i("유저정보 등록 성공: $res")
                            PreferenceManager.setLong("userId", data.id)
                            val intent = Intent(this, AddressMappingActivity::class.java)
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        }
                    }
                    Status.FAILURE -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private val callback: ((OAuthToken?, Throwable?) -> Unit) = { token, error ->
        if (error != null) { //Login Fail
            Dlog.e("Kakao Login Failed : $error")
        } else if (token != null) { //Login Success
            Dlog.i("로그인성공!!")
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (error != null) {
                    Dlog.e("토큰 정보 보기 실패")
                }
                else if (tokenInfo != null) {
                    Dlog.i("회원번호: ${tokenInfo.id}" + "\n만료시간: ${tokenInfo.expiresIn}")
                    val user = User(tokenInfo.id)
//                    PreferenceManager.setLong("userId", tokenInfo.id)
                    insertAccount(user)
                }
            }
        }
    }
}
