package com.lunchfood.ui.main.view.history

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.Settings
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.google.android.material.snackbar.Snackbar
import com.lunchfood.BuildConfig.AWS_COGNITO_CREDENTIAL_POOL_ID
import com.lunchfood.R
import com.lunchfood.data.model.history.HistoryResponse
import com.lunchfood.data.model.history.PlaceInfo
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.utils.Dlog
import com.lunchfood.utils.PreferenceManager
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.android.synthetic.main.activity_menu_regist.*
import kotlinx.android.synthetic.main.header.*
import org.threeten.bp.LocalDate
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MenuRegistActivity : BaseActivity(TransitionMode.HORIZON), View.OnClickListener, View.OnTouchListener {

    private val mDayMenu by lazy { intent.getSerializableExtra("dayMenu") as HistoryResponse }
    private var mScore = 0
    private var mPlaceInfo: PlaceInfo? = null
    private var mFoodName: String? = ""
    private val mImageUploadPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )
    private val PLACE_SEARCH_REQUEST_CODE = 0
    private val MENU_SEARCH_REQUEST_CODE = 1
    private val PICK_IMAGE_CHOOSER_REQUEST_CODE = 2
    private val IMAGE_PERMISSION_CODE = 3
    private var mPermissionGrantedCount = 0
    private var mCurrentPhotoPath: String? = null
    private var mOutputFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_regist)
        setCommonHeaderText(getString(R.string.day_menu_regist))

        Dlog.i("데이메뉴::: $mDayMenu")

        setupDayMenu()

        headerBackBtn.setOnClickListener(this)
        score1.setOnClickListener(this)
        score2.setOnClickListener(this)
        score3.setOnClickListener(this)
        score4.setOnClickListener(this)
        score5.setOnClickListener(this)
        // 참고 : https://gist.github.com/Reacoder/0b316726564f85523251
        // EditText의 경우 이벤트리스너가 OnTouch -> OnFocusChange -> OnClick 순으로 실행되어 click의 경우 바로 안먹음
        etImageUpload.setOnTouchListener(this)
        etHistoryPlaceName.setOnTouchListener(this)
        ivHistoryPlaceSearch.setOnClickListener(this)
        etHistoryMenuName.setOnTouchListener(this)
        ivHistoryMenuSearch.setOnClickListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == IMAGE_PERMISSION_CODE) {
            grantResults.forEach {
                if(it == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGrantedCount++
                }
            }
            if(mImageUploadPermissions.size == mPermissionGrantedCount) {
                if(isImagePermissionGranted()) {
                    startActivityForResult(
                        getPickImageChooserIntent(
                            this,
                            "Pick a service",
                            includeDocuments = false,
                            includeCamera = true
                        ), PICK_IMAGE_CHOOSER_REQUEST_CODE
                    )
                }
            } else {
                Toast.makeText(applicationContext, R.string.no_permission_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isImagePermissionGranted(): Boolean {
        val isFirstCheck = PreferenceManager.getBoolean("isFirstImagePermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val snackBar = Snackbar.make(clMenuRegist, R.string.suggest_image_upload_permission_grant, Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction(R.string.permission_granted) {
                    ActivityCompat.requestPermissions(this, mImageUploadPermissions, IMAGE_PERMISSION_CODE)
                }
                snackBar.show()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                val snackBar = Snackbar.make(clMenuRegist, R.string.suggest_camera_permission_grant, Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction(R.string.permission_granted) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), IMAGE_PERMISSION_CODE)
                }
                snackBar.show()
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                val snackBar = Snackbar.make(clMenuRegist, R.string.suggest_file_permission_grant, Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction(R.string.permission_granted) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), IMAGE_PERMISSION_CODE)
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    PreferenceManager.setBoolean("isFirstImagePermissionCheck", false)
                    ActivityCompat.requestPermissions(this, mImageUploadPermissions, IMAGE_PERMISSION_CODE)
                } else {
                    // 사용자가 권한을 거부하면서 다시 묻지않음 옵션을 선택한 경우
                    // requestPermission 을 요청해도 창이 나타나지 않기 때문에 설정창으로 이동한다.
                    val snackBar = Snackbar.make(clMenuRegist, R.string.suggest_image_upload_permission_grant_in_setting, Snackbar.LENGTH_INDEFINITE)
                    snackBar.setAction(R.string.ok) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    snackBar.show()
                }
            }
            return false
        } else {
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            PLACE_SEARCH_REQUEST_CODE -> {
                if(resultCode == RESULT_OK) {
                    mPlaceInfo = data?.getSerializableExtra("placeInfo") as PlaceInfo?
                    etHistoryPlaceName.setText(mPlaceInfo?.placeName?: "")
                }
            }
            MENU_SEARCH_REQUEST_CODE -> {
                if(resultCode == RESULT_OK) {
                    mFoodName = data?.getStringExtra("foodName")
                    etHistoryMenuName.setText(mFoodName?: "")
                }
            }
            PICK_IMAGE_CHOOSER_REQUEST_CODE -> {
                if(resultCode == RESULT_OK) {
                    if(data?.data != null) {
                        // 이미지 1장 처리시
                        Dlog.i("camera return:: ${data.data}")
                        data.data?.let {
                            score1.setImageURI(it)
                        }
                    } else if(data?.clipData != null) {
                        // 이미지 여러 장 선택시 처리
                        val count = data.clipData!!.itemCount
                        if(count > 5) {
                            Toast.makeText(this, "5장 이상 업로드할 수 없습니다.", Toast.LENGTH_SHORT).show()
                            return
                        }
                        for(i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                        }
                    } else {
                        // 카메라 촬영 이미지 처리
                        mOutputFileUri?.let {
                            val bitmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, it))
                            } else {
                                MediaStore.Images.Media.getBitmap(contentResolver, it)
                            }
                            score1.setImageBitmap(bitmap)
                        }
                    }
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

    private fun uploadImagesWithAwsS3(fileName: String, file: File) {
        val credentialProvider = CognitoCachingCredentialsProvider(
            applicationContext,
            AWS_COGNITO_CREDENTIAL_POOL_ID,
            Regions.AP_NORTHEAST_2
        )

        TransferNetworkLossHandler.getInstance(applicationContext)

        val transferUtility = TransferUtility.builder()
            .context(applicationContext)
            .defaultBucket("lunch-image")  // bucket name
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.headerBackBtn -> onBackPressed()
            R.id.score1 -> {
                score1.setImageResource(R.drawable.score_like)
                score2.setImageResource(R.drawable.score_unlike)
                score3.setImageResource(R.drawable.score_unlike)
                score4.setImageResource(R.drawable.score_unlike)
                score5.setImageResource(R.drawable.score_unlike)
                mScore = 1
            }
            R.id.score2 -> {
                score1.setImageResource(R.drawable.score_like)
                score2.setImageResource(R.drawable.score_like)
                score3.setImageResource(R.drawable.score_unlike)
                score4.setImageResource(R.drawable.score_unlike)
                score5.setImageResource(R.drawable.score_unlike)
                mScore = 2
            }
            R.id.score3 -> {
                score1.setImageResource(R.drawable.score_like)
                score2.setImageResource(R.drawable.score_like)
                score3.setImageResource(R.drawable.score_like)
                score4.setImageResource(R.drawable.score_unlike)
                score5.setImageResource(R.drawable.score_unlike)
                mScore = 3
            }
            R.id.score4 -> {
                score1.setImageResource(R.drawable.score_like)
                score2.setImageResource(R.drawable.score_like)
                score3.setImageResource(R.drawable.score_like)
                score4.setImageResource(R.drawable.score_like)
                score5.setImageResource(R.drawable.score_unlike)
                mScore = 4
            }
            R.id.score5 -> {
                score1.setImageResource(R.drawable.score_like)
                score2.setImageResource(R.drawable.score_like)
                score3.setImageResource(R.drawable.score_like)
                score4.setImageResource(R.drawable.score_like)
                score5.setImageResource(R.drawable.score_like)
                mScore = 5
            }
            R.id.ivHistoryPlaceSearch -> {
                val intent = Intent(this@MenuRegistActivity, PlaceSearchActivity::class.java)
                startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PLACE_SEARCH_REQUEST_CODE)
            }
            R.id.ivHistoryMenuSearch -> {
                val intent = Intent(this@MenuRegistActivity, MenuSearchActivity::class.java)
                startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), MENU_SEARCH_REQUEST_CODE)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when(v?.id) {
            R.id.etImageUpload -> {
                if(MotionEvent.ACTION_UP == event?.action) {
                    if(isImagePermissionGranted()) {
                        startActivityForResult(
                            getPickImageChooserIntent(
                                this,
                                "Pick a service",
                                includeDocuments = false,
                                includeCamera = true
                            ), PICK_IMAGE_CHOOSER_REQUEST_CODE
                        )
                    }
                }
            }
            R.id.etHistoryPlaceName -> {
                if(MotionEvent.ACTION_UP == event?.action) {
                    val intent = Intent(this@MenuRegistActivity, PlaceSearchActivity::class.java)
                    startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PLACE_SEARCH_REQUEST_CODE)
                }
            }
            R.id.etHistoryMenuName -> {
                if(MotionEvent.ACTION_UP == event?.action) {
                    val intent = Intent(this@MenuRegistActivity, MenuSearchActivity::class.java)
                    startActivityForResult(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), PLACE_SEARCH_REQUEST_CODE)
                }
            }
        }
        return false
    }

    /**
     * Create a chooser intent to select the source to get image from.<br></br>
     * The source can be camera's (ACTION_IMAGE_CAPTURE) or gallery's (ACTION_GET_CONTENT).<br></br>
     * All possible sources are added to the intent chooser.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     * @param title the title to use for the chooser UI
     * @param includeDocuments if to include KitKat documents activity containing all sources
     * @param includeCamera if to include camera intents
     */
    private fun getPickImageChooserIntent(
        context: Context,
        title: CharSequence,
        includeDocuments: Boolean,
        includeCamera: Boolean
    ): Intent {

        val allIntents = ArrayList<Intent>()
        val packageManager = context.packageManager

        // collect all camera intents if Camera permission is available
        if (!isExplicitCameraPermissionRequired(context) && includeCamera) {
            allIntents.addAll(getCameraIntents(context, packageManager))
        }

//        var galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_GET_CONTENT, includeDocuments)
//        if (galleryIntents.isEmpty()) {
//            // if no intents found for get-content try pick intent action (Huawei P9).
//            galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK, includeDocuments)
//        }
        val galleryIntents = getGalleryIntents(packageManager, Intent.ACTION_PICK, includeDocuments)
        allIntents.addAll(galleryIntents)

        val target: Intent
        if (allIntents.isEmpty()) {
            target = Intent()
        } else {
            target = allIntents[allIntents.size - 1]
            allIntents.removeAt(allIntents.size - 1)
        }

        // Create a chooser from the main  intent
        val chooserIntent = Intent.createChooser(target, title)

        // Add all other intents
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS, allIntents.toTypedArray<Parcelable>()
        )

        return chooserIntent
    }

    /** Get all Camera intents for capturing image using device camera apps.  */
    private fun getCameraIntents(
        context: Context,
        packageManager: PackageManager
    ): List<Intent> {

        val allIntents = ArrayList<Intent>()
        // Determine Uri of camera image to save.
        mOutputFileUri = getCaptureImageOutputUri(context)

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            if (mOutputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mOutputFileUri)
            }
            allIntents.add(intent)
        }

        return allIntents
    }

    /**
     * Get all Gallery intents for getting image from one of the apps of the device that handle
     * images.
     */
    private fun getGalleryIntents(
        packageManager: PackageManager,
        action: String,
        includeDocuments: Boolean
    ): List<Intent> {
        val intents = ArrayList<Intent>()
        val galleryIntent = if (action === Intent.ACTION_GET_CONTENT)
            Intent(action)
        else
            Intent(action, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        val listGallery = packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(galleryIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intents.add(intent)
        }
        return intents
    }

    /**
     * Check if explicetly requesting camera permission is required.<br></br>
     * It is required in Android Marshmellow and above if "CAMERA" permission is requested in the
     * manifest.<br></br>
     * See [StackOverflow question](http://stackoverflow.com/questions/32789027/android-m-camera-intent-permission-bug).
     */
    private fun isExplicitCameraPermissionRequired(context: Context): Boolean {
        return (hasPermissionInManifest(context, "android.permission.CAMERA")
                && context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
    }

    /**
     * Check if the app requests a specific permission in the manifest.
     *
     * @param permissionName the permission to check
     * @return true - the permission in requested in manifest, false - not.
     */
    private fun hasPermissionInManifest(context: Context, permissionName: String): Boolean {
        val packageName = context.packageName
        try {
            val packageInfo =
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_PERMISSIONS
                )
            val declaredPermissions = packageInfo.requestedPermissions
            if (!declaredPermissions.isNullOrEmpty()) {
                for (p in declaredPermissions) {
                    if (p.equals(permissionName, ignoreCase = true)) {
                        return true
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }
        return false
    }


    /**
     * Get URI to image received from capture by camera.
     *
     * @param context used to access Android APIs, like content resolve, it is your
     * activity/fragment/widget.
     */
    private fun getCaptureImageOutputUri(context: Context): Uri? {
        var photoFile: File? = null
        var outputFileUri: Uri? = null
        try {
            photoFile = createImageFile()
        } catch(e: Exception) {
            Dlog.e("getCaptureImageOutputUri: ${e.message}")
            e.printStackTrace()
        }
        if(photoFile != null) {
            outputFileUri = FileProvider.getUriForFile(this, packageName, photoFile)
        }
        return outputFileUri
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "${timeStamp}_pickImageResult"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile: File = File.createTempFile(imageFileName, ".jpeg", storageDir)
        mCurrentPhotoPath = imageFile.absolutePath

        return imageFile
    }
}