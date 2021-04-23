package com.lunchfood.ui.main.view.history

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.lunchfood.R
import com.lunchfood.data.model.history.HistoryResponse
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.utils.Constants.Companion.AWS_COGNITO_CREDENTIAL_POOL_ID
import com.lunchfood.utils.Dlog
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.android.synthetic.main.activity_menu_regist.*
import org.threeten.bp.LocalDate
import java.io.File
import java.lang.Exception

class MenuRegistActivity : BaseActivity(TransitionMode.HORIZON) {

    private val mDayMenu by lazy { intent.getSerializableExtra("dayMenu") as HistoryResponse }
    private var mScore = 0
    private val PLACE_SEARCH_REQUEST_CODE = 0
    private val MENU_SEARCH_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_regist)
        setCommonHeaderText(getString(R.string.day_menu_regist))

        Dlog.i("데이메뉴::: $mDayMenu")

        setupDayMenu()
        setupEventListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            PLACE_SEARCH_REQUEST_CODE -> {
                if(resultCode == RESULT_OK) {
                    val result = data?.getStringExtra("result")
                    Dlog.i("결과값::: $result")
                } else {

                }
            }
            MENU_SEARCH_REQUEST_CODE -> {
                if(resultCode == RESULT_OK) {
                    val result = data?.getStringExtra("result")
                    Dlog.i("결과값::: $result")
                } else {

                }
            }
        }
    }

    private fun setupDayMenu() {
        val convertDate = CalendarDay.from(LocalDate.parse(mDayMenu.insertedDate))
        tvCurrentDate.text = getString(R.string.dayMenuDate, convertDate.year, convertDate.month, convertDate.day)
        etImageUpload.inputType = InputType.TYPE_NULL
        etHistoryPlaceName.let {
            it.setText(mDayMenu.placeName)
            it.inputType = InputType.TYPE_NULL
        }
        etHistoryMenuName.let {
            it.setText(mDayMenu.menuName)
            it.inputType = InputType.TYPE_NULL
        }
    }

    private fun setupEventListener() {
        score1.setOnClickListener {
            score1.setImageResource(R.drawable.score_like)
            score2.setImageResource(R.drawable.score_unlike)
            score3.setImageResource(R.drawable.score_unlike)
            score4.setImageResource(R.drawable.score_unlike)
            score5.setImageResource(R.drawable.score_unlike)
            mScore = 1
        }
        score2.setOnClickListener {
            score1.setImageResource(R.drawable.score_like)
            score2.setImageResource(R.drawable.score_like)
            score3.setImageResource(R.drawable.score_unlike)
            score4.setImageResource(R.drawable.score_unlike)
            score5.setImageResource(R.drawable.score_unlike)
            mScore = 2
        }
        score3.setOnClickListener {
            score1.setImageResource(R.drawable.score_like)
            score2.setImageResource(R.drawable.score_like)
            score3.setImageResource(R.drawable.score_like)
            score4.setImageResource(R.drawable.score_unlike)
            score5.setImageResource(R.drawable.score_unlike)
            mScore = 3
        }
        score4.setOnClickListener {
            score1.setImageResource(R.drawable.score_like)
            score2.setImageResource(R.drawable.score_like)
            score3.setImageResource(R.drawable.score_like)
            score4.setImageResource(R.drawable.score_like)
            score5.setImageResource(R.drawable.score_unlike)
            mScore = 4
        }
        score5.setOnClickListener {
            score1.setImageResource(R.drawable.score_like)
            score2.setImageResource(R.drawable.score_like)
            score3.setImageResource(R.drawable.score_like)
            score4.setImageResource(R.drawable.score_like)
            score5.setImageResource(R.drawable.score_like)
            mScore = 5
        }

        etImageUpload.setOnClickListener {
            Dlog.i("이미지 업로드 클릭!!!")
        }

        etHistoryPlaceName.setOnClickListener {
            val intent = Intent(this@MenuRegistActivity, PlaceSearchActivity::class.java)
            startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PLACE_SEARCH_REQUEST_CODE)
        }

        ivHistoryPlaceSearch.setOnClickListener {
            val intent = Intent(this@MenuRegistActivity, PlaceSearchActivity::class.java)
            startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PLACE_SEARCH_REQUEST_CODE)
        }

        etHistoryMenuName.setOnClickListener {
            val intent = Intent(this@MenuRegistActivity, MenuSearchActivity::class.java)
            startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), MENU_SEARCH_REQUEST_CODE)
        }

        ivHistoryMenuSearch.setOnClickListener {
            val intent = Intent(this@MenuRegistActivity, MenuSearchActivity::class.java)
            startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), MENU_SEARCH_REQUEST_CODE)
        }
    }

    private fun uploadImagesWithAwsS3(fileName: String, file: File) {
        val credentialProvider = CognitoCachingCredentialsProvider(
            applicationContext,
            AWS_COGNITO_CREDENTIAL_POOL_ID,
            Regions.AP_NORTHEAST_2
        )

        TransferNetworkLossHandler.getInstance(applicationContext)

        val transferUtility = TransferUtility.builder()
            .context(applicationContext)
            .defaultBucket("")  // bucket name
            .s3Client(AmazonS3Client(credentialProvider, Region.getRegion(Regions.AP_NORTHEAST_2)))
            .build()

        val uploadObserver = transferUtility.upload("bucket_path/$fileName", file, CannedAccessControlList.PublicRead)
        uploadObserver.setTransferListener(object: TransferListener {
            override fun onStateChanged(id: Int, state: TransferState?) {
                if(state == TransferState.COMPLETED) {
                    Dlog.i("UPLOAD Completed")
                }
            }

            override fun onProgressChanged(id: Int, current: Long, total: Long) {
                val done = ((current.toDouble() / total) * 100.0).toInt()
                Dlog.i("UPLOAD -- ID: $id, percent done = $done")
            }

            override fun onError(id: Int, ex: Exception?) {
                Dlog.i("UPLOAD -- ID: $id, error message: ${ex?.message.toString()}")
            }
        })

        // If you prefer to long-poll for updates
        if (uploadObserver.state == TransferState.COMPLETED) {
            /* Handle completion */
        }
    }
}