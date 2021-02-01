package kr.coursemos.yonsei

import android.content.Intent
import android.graphics.*
import android.hardware.Camera
import android.media.ExifInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import kotlinx.android.synthetic.main.activity_a01_50_camera.*
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a51custometc.A903LMSTask
import kr.coursemos.yonsei.a99util.CMTimer
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.yonsei.a99util.StaticClass
import kr.coursemos.yonsei.a99util.StringHashMap
import kr.coursemos.yonsei.camera.CameraSourcePreview
import kr.coursemos.yonsei.camera.FaceGraphic
import kr.coursemos.yonsei.camera.GraphicOverlay
import kr.coursemos.yonsei.camera.MyCameraSource
import kr.coursemos.myapplication.a99util.EnvError
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class A01_50Camera : A00_00Activity() {
	private var mCameraSource: MyCameraSource? = null
	private lateinit var graphicFaceTracker: GraphicFaceTracker
	private var isFinish = false
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_a01_50_camera)
		try {
			val detector = FaceDetector.Builder(applicationContext)
				.setClassificationType(FaceDetector.ALL_CLASSIFICATIONS).build()

			detector.setProcessor(MultiProcessor.Builder(GraphicFaceTrackerFactory()).build())


			if (!detector.isOperational) {
				// Note: The first time that an app using face API is installed on a device, GMS will
				// download a native library to the device in order to do detection.  Usually this
				// completes before the app is run for the first time.  But if that download has not yet
				// completed, then the above call will not detect any faces.
				//
				// isOperational() can be used to check if the required native library is currently
				// available.  The detector will automatically become operational once the library
				// download completes on device.
				Env.debug(applicationContext, "Face detector dependencies are not yet available.")
			}
			//			FaceGraphic
			//			FaceGraphic


			val display = windowManager.defaultDisplay // in case of Activity
			/* val display = activity!!.windowManaver.defaultDisplay */ // in case of Fragment
			val size = Point()
			display.getRealSize(size) // or getSize(size)
			var metrics=DisplayMetrics()
			display.getMetrics(metrics)
//			EnvDebug("${size.x}  !!!  ${size.y}      ${metrics.widthPixels}   !!!  ${metrics.heightPixels}")
			CameraSourcePreview.width=metrics.heightPixels
			CameraSourcePreview.height=metrics.widthPixels
			mCameraSource = MyCameraSource.Builder(applicationContext, detector)
				.setRequestedPreviewSize(metrics.heightPixels, metrics.widthPixels).setFacing(CameraSource.CAMERA_FACING_FRONT)
				.setRequestedFps(30.0f).build()
			startCameraSource()
			//			edit.putBoolean(StaticClass.KEY_FACEREG,false);
			//			edit.commit();
			//			edit.putBoolean(StaticClass.KEY_FACEREG,false);
			//			edit.commit();
			TimeCheck()
		} catch (e: Exception) {
			EnvError(e)
		}
	}

	inner private class GraphicFaceTrackerFactory : MultiProcessor.Factory<Face> {
		override fun create(face: Face): Tracker<Face> {
			graphicFaceTracker = GraphicFaceTracker(overlay2)
			return graphicFaceTracker
		}
	}

	private val RC_HANDLE_GMS = 9001
	private fun startCameraSource() {
		// check that the device has play services available.
		val code =
			GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
		if (code != ConnectionResult.SUCCESS) {
			val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
			dlg.show()
		}
		if (mCameraSource == null) {
			Env.debug(applicationContext, "cameraNull??")
		} else {
			try {
				Env.debug(applicationContext, "cameraStart!!")
				preview2.start(mCameraSource, overlay2)
			} catch (e: IOException) {
				Env.error("Unable to start camera source.", e)
				mCameraSource?.release()
				mCameraSource = null
			}
		}
	}

	/**
	 * Face tracker for each detected individual. This maintains a face graphic within the app's
	 * associated face overlay.
	 */
	private class GraphicFaceTracker internal constructor(private val mOverlay: GraphicOverlay) :
		Tracker<Face>() {
		private val mFaceGraphic: FaceGraphic

		@Throws(Exception::class)
		fun initFacsStartTime() {
			mFaceGraphic.facestarttime = 0
		}
		@Throws(Exception::class)
		fun getText()  :String{
			return mFaceGraphic.text
		}


		@get:Throws(Exception::class)
		val faceStartTime: Long
			get() = mFaceGraphic.facestarttime

		@get:Throws(Exception::class)
		val faceCurrentTime: Long
			get() = mFaceGraphic.facecurrenttime

		/**
		 * Start tracking the detected face instance within the face overlay.
		 */
		override fun onNewItem(faceId: Int, item: Face?) {
			mFaceGraphic.setId(faceId)
		}

		/**
		 * Update the position/characteristics of the face within the overlay.
		 */
		override fun onUpdate(detectionResults: Detections<Face?>, face: Face?) {
			mOverlay.add(mFaceGraphic)
			mFaceGraphic.updateFace(face)
		}

		/**
		 * Hide the graphic when the corresponding face was not detected.  This can happen for
		 * intermediate frames temporarily (e.g., if the face was momentarily blocked from
		 * view).
		 */
		override fun onMissing(detectionResults: Detections<Face?>) {
			mOverlay.remove(mFaceGraphic)
		}

		/**
		 * Called when the face is assumed to be gone for good. Remove the graphic annotation from
		 * the overlay.
		 */
		override fun onDone() {
			mOverlay.remove(mFaceGraphic)
		}

		init {
			mFaceGraphic = FaceGraphic(mOverlay)
		}
	}

	var onClick = View.OnClickListener {
		try {
		} catch (e: Exception) {
			Env.error("", e)
		}
	} // OnClickListener onClick

	@Throws(Exception::class)
	protected fun open() {
		try {
		} catch (e: Exception) {
			Env.error("", e)
		}
	} //open

	@Throws(Exception::class)
	protected fun close() {
		try {
		} catch (e: Exception) {
			Env.error("", e)
		}
	} //close

	private var isPic = false
	var errorMessage: String? = null
	override fun finish() {
		isFinish = true
		val intentR = Intent()
		if (isPic) {
			setResult(RESULT_OK, intentR)
		} else {
			if (errorMessage == null) {
				intentR.putExtra("ErrorMessage", "사용자취소")
			} else {
				intentR.putExtra("ErrorMessage", errorMessage)
			}
			setResult(RESULT_CANCELED, intentR)
		}
		super.finish()
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		try {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				finish()
				return true
			}
		} catch (e: Exception) {
			Env.error("", e)
		}
		return super.onKeyDown(keyCode, event)
	}

	override fun onPause() {
		super.onPause()
		finish()
	}

	private var timeCheck: CMTimer? = null

	@Throws(Exception::class)
	protected fun TimeCheck() {
		if (timeCheck is CMTimer) {
			timeCheck!!.cancel()
			timeCheck = null
		}
		if (isFinish) {
		} else {
			timeCheck = object : CMTimer(1000, 1000) {
				override fun onTick(millisUntilFinished: Long) {}
				override fun onFinish() {
					try {
						if (graphicFaceTracker == null) {
						} else {
							a01_50msg.setText(graphicFaceTracker.getText())
							if (graphicFaceTracker.faceCurrentTime + 50 > System.currentTimeMillis()) {
							} else {
								graphicFaceTracker.initFacsStartTime()
							}
							if (graphicFaceTracker.faceStartTime > 0 && graphicFaceTracker.faceStartTime + 500 < System.currentTimeMillis()) {
								fileName = "test.jpg"
								val parameters: Camera.Parameters =
									mCameraSource!!.selectCamera.getParameters()
								val size = parameters.previewSize
								val image = YuvImage(mCameraSource!!.data,
									parameters.previewFormat,
									size.width,
									size.height,
									null)
								filePath = StaticClass.pathExternalStorage + "/" + fileName
								val imageFile = File(filePath)
								val outputStream = FileOutputStream(imageFile)
								val quality = 100
								image.compressToJpeg(Rect(0, 0, image.width, image.height),
									quality,
									outputStream)
								//								bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
								outputStream.flush()
								outputStream.close()
								var myBitmap = BitmapFactory.decodeFile(filePath)
								var exif: ExifInterface? = null
								try {
									exif = ExifInterface(filePath)
								} catch (e: IOException) {
									Env.error("", e)
								}
								val exifOrientation: Int
								val exifDegree: Int
								if (exif == null) {
									exifDegree = 0
								} else {
									exifOrientation =
										exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
											ExifInterface.ORIENTATION_NORMAL)
									exifDegree = exifOrientationToDegrees(exifOrientation)
								}
								myBitmap = rotate(myBitmap, exifDegree.toFloat())
								val out =
									FileOutputStream(StaticClass.pathExternalStorage + "/" + fileName)
								myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
								out.close()
								val shm = StringHashMap()
								shm["filePath"] = StaticClass.pathExternalStorage + "/" + fileName
								shm["fileKey"] = "userFace"
								shm["userid"] = getEnv(StaticClass.KEY_ID, "")
								shm["school"] = getEnv(StaticClass.KEY_SELECTSCHOOLTOKEN, "")
								prd()
								if (getEnvBoolean(StaticClass.KEY_FACEREG, false)) {
									object : A903LMSTask(applicationContext,
										API_MULTIPARTFACECOMP,
										shm) {
										@Throws(Exception::class)
										override fun success() {
											dismiss()
											val jo = JSONObject(result)
											var isSame = false
											if (jo.isNull("data")) {
											} else {
												//												JSONArray ja=jo.getJSONArray("data");
												//												for(int i=0;i<ja.length();i++){
												//													if(ja.getString(i).equals("True")){
												//														isSame=true;
												//														break;
												//													}
												//												}
												if (jo.getString("data") == "0000") {
													isSame = true
												} else {
												}
											}
											if (isSame) {
											} else {
												isPic = false
												errorMessage = "등록된 인증정보와 불일치 합니다."
											}
											toast("인증되었습니다.")
											finish()
										}

										@Throws(Exception::class)
										override fun fail(errmsg: String) {
											dismiss()
											toast(errmsg)
											isPic = false
											errorMessage = "서버오류 입니다."
											finish()
										}
									}
								} else {
									object :
										A903LMSTask(applicationContext, API_MULTIPARTFACEREG, shm) {
										@Throws(Exception::class)
										override fun success() {
											dismiss()
											toast("얼굴등록이 완료되었습니다.")
											edit.putBoolean(StaticClass.KEY_FACEREG, true)
											edit.commit()
											graphicFaceTracker.initFacsStartTime()
											isPic = false
											isFinish = false
											TimeCheck()
										}

										@Throws(Exception::class)
										override fun fail(errmsg: String) {
											dismiss()
											toast(errmsg)
											finish()
										}
									}
								}
								//								String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
								//								Intent intent = new Intent(Intent.ACTION_VIEW);
								//								intent.addCategory(Intent.CATEGORY_DEFAULT);
								//								String filetype = null;
								//								filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
								//								Uri photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(filePath));
								//								intent.setDataAndType(photoURI,filetype);
								//								intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
								//								startActivity(intent);
								isPic = true
								isFinish = true
							}
						}
						if (isFinish) {
						} else {
							TimeCheck()
						}
					} catch (e: Exception) {
						if (StaticClass.isDebug) {
							Env.error("hey!!!!!!!!", e)
						}
						toast("초기화 실패 다시 시도해주세요")
						finish()
					}
				}
			}
			timeCheck!!.start()
		}
	}

	private fun exifOrientationToDegrees(exifOrientation: Int): Int {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 0
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 90
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 180
		}
		return 270
	}

	private fun rotate(bitmap: Bitmap, degree: Float): Bitmap {
		val matrix = Matrix()
		matrix.postRotate(degree)
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
	}
}