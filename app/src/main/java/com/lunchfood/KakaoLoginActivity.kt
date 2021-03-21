package com.lunchfood

import android.content.Intent
import android.os.Bundle
import androidx.core.text.HtmlCompat
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.lunchfood.common.BaseActivity
import com.lunchfood.common.Dlog
import kotlinx.android.synthetic.main.login_activity.*

class KakaoLoginActivity: BaseActivity(TransitionMode.HORIZON) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

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
                    val intent = Intent(this, AddressMapping::class.java)
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }
    }
}
