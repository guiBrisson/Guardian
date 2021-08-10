package me.brisson.guardian.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import me.brisson.guardian.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/*
    todo image from camera is coming rotated
    todo export the image uri in the callback
 */
class ImageHelper(private val activity: Activity) {

    private var photoURI: Uri? = null

    private var callback: Callback? = null

    fun showDialogGalleryOrCamera() {
        AlertHelper.alertTwoButtonsDialog(activity,
            activity.getString(R.string.add_image),
            activity.getString(R.string.camera),
            activity.getString(R.string.gallery),
            { _, _ -> getImageFromCamera() },
            { _, _ -> getImageFromGallery() })
    }

    private fun getImageFromCamera() {
        val permissionReadExternalStorage = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permissionWriteExternalStorage = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (
            permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED ||
            permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED
        ) {
            askForExternalStorage(REQUEST_PERMISSION_CODE_CAMERA)

        } else {
            val packageManager = activity.packageManager
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (takePictureIntent.resolveActivity(packageManager) != null) {
                var photoFile: File? = null

                try {
                    photoFile = ImageFileHandler.create(activity)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(
                        activity,
                        activity.packageName + ".provider",
                        photoFile
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    activity.startActivityForResult(takePictureIntent, PHOTO_REQUEST_CODE)
                } else {
                    callback!!.onError()
                }
            }
        }



    }

    private fun getImageFromGallery() {
        val permissionReadExternalStorage = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permissionWriteExternalStorage = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED
            || permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED
        ) {
            askForExternalStorage(REQUEST_PERMISSION_CODE_GALLERY)

        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            activity.startActivityForResult(
                Intent.createChooser(intent, "Escolha uma imagem:"),
                GALLERY_REQUEST_CODE
            )

        }


    }

    private fun askForExternalStorage(REQUEST_CODE: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_CODE
        )
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_CODE_CAMERA -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                getImageFromCamera()
            }
            REQUEST_PERMISSION_CODE_GALLERY -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                getImageFromGallery()
            }
        }
    }

    fun handleResult(requestCode: Int, resultCode: Int, data: Intent?, callback: Callback) {
        if (requestCode == GALLERY_REQUEST_CODE){
            handleGalleryResult(requestCode, resultCode, data, callback)
        }
        else if (requestCode == PHOTO_REQUEST_CODE){
            handlePhotoResult(requestCode, resultCode, callback)
        }
    }

    private fun handleGalleryResult(requestCode: Int, resultCode: Int, data: Intent?, callback: Callback) {
        this.callback = callback
        if (data != null && requestCode == GALLERY_REQUEST_CODE) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> try {
                    photoURI = data.data
                    val imageStream: InputStream? = activity.contentResolver.openInputStream(photoURI!!)
                    val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
                    callback.onImageCompressed(null, selectedImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                AppCompatActivity.RESULT_CANCELED -> callback.onCanceled()
                else -> callback.onError()
            }
        }
    }

    private fun handlePhotoResult(requestCode: Int, resultCode: Int, callback: Callback) {
        this.callback = callback
        if (photoURI != null && requestCode == PHOTO_REQUEST_CODE) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> try {
                    val imageStream: InputStream? = activity.contentResolver.openInputStream(photoURI!!)
                    val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
                    callback.onImageCompressed(null, selectedImage)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                AppCompatActivity.RESULT_CANCELED -> callback.onCanceled()
                else -> callback.onError()
            }
        }
    }

    private object ImageFileHandler {
        @Throws(IOException::class)
        fun create(context: Context): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFileName = "ANDROID_" + timeStamp + "_"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

            return File.createTempFile(
                imageFileName, /* prefix */
                ".jpeg", /* suffix */
                storageDir     /* directory */
            )

        }
    }

    interface Callback {
        fun onImageCompressed(image64: String?, imageBitmap: Bitmap?)
        fun onCanceled()
        fun onError()
    }

    companion object {
        const val PHOTO_REQUEST_CODE = 6564
        const val GALLERY_REQUEST_CODE = 6565
        const val REQUEST_PERMISSION_CODE_CAMERA = 56
        const val REQUEST_PERMISSION_CODE_GALLERY = 57
    }
}