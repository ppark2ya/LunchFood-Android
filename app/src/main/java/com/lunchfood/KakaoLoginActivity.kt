package com.lunchfood

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.lunchfood.util.Dlog
import kotlinx.android.synthetic.main.login_activity.*

class KakaoLoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        //로그인 공통 콜백 구성
        val callback: ((OAuthToken?, Throwable?) -> Unit) = { token, error ->
            if (error != null) { //Login Fail
                Dlog.e("Kakao Login Failed :" + error)
            } else if (token != null) { //Login Success
                Dlog.i("로그인성공!!")
//                startMainActivity()

                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
                    }
                    else if (tokenInfo != null) {
                        Toast.makeText(this, "회원번호: ${tokenInfo.id}" + "\n만료시간: ${tokenInfo.expiresIn}", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, SecondActivity::class.java)
//                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    }
                }
            }
        }

        kakao_login_btn.setOnClickListener {
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

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}