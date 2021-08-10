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

class ImageHelper(private val activity: Activity) {

    private var photoURI: Uri? = null

    private var callback: Callback? = null

    private val permissionCamera = ContextCompat.checkSelfPermission(
        activity,
        Manifest.permission.CAMERA
    )

    private val permissionReadExternalStorage = ActivityCompat.checkSelfPermission(
        activity,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val permissionWriteExternalStorage = ActivityCompat.checkSelfPermission(
        activity,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun showDialogGalleryOrCamera() {
        AlertHelper.alertTwoButtonsDialog(activity,
            activity.getString(R.string.add_image),
            activity.getString(R.string.camera),
            activity.getString(R.string.gallery),
            { _, _ -> getImageFromCamera() },
            { _, _ -> getImageFromGallery() })
    }

    private fun getImageFromCamera() {
        if (permissionCamera != PackageManager.PERMISSION_GRANTED ||
            permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED ||
            permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED
        ) {
            askForExternalStorage(REQUEST_PERMISSION_CODE_CAMERA)
        }


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

        Log.d(TAG, "getImageFromCamera")
    }

    private fun getImageFromGallery() {
        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED
            || permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED
        ) {
            askForExternalStorage(REQUEST_PERMISSION_CODE_GALLERY)
        }

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        activity.startActivityForResult(
            Intent.createChooser(intent, "Escolha uma imagem:"),
            GALLERY_REQUEST_CODE
        )

        Log.d(TAG, "getImageFromGallery")

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

    //todo finish
    private fun handleGalleryResult(requestCode: Int, resultCode: Int, data: Intent?, callback: Callback) {
        this.callback = callback
        if (data != null && requestCode == GALLERY_REQUEST_CODE) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> try {
//                    actualImage = FileUtil.from(activity, data.data)

                    photoURI = data.data
                    val imageStream: InputStream? = activity.contentResolver.openInputStream(photoURI!!)
                    val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
                    callback.onImageCompressed(null, selectedImage)
//                    compressImage(activity)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                AppCompatActivity.RESULT_CANCELED -> callback.onCanceled()
                else -> callback.onError()
            }
        }
    }

    //todo finish (figure out why the camera keep coming back FUCK)
    private fun handlePhotoResult(requestCode: Int, resultCode: Int, callback: Callback) {
        this.callback = callback
        if (photoURI != null && requestCode == PHOTO_REQUEST_CODE) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> try {
//                    actualImage = FileUtil.from(activity, photoURI)

                    val imageStream: InputStream? = activity.contentResolver.openInputStream(photoURI!!)
                    val selectedImage: Bitmap = BitmapFactory.decodeStream(imageStream)
                    callback.onImageCompressed(null, selectedImage)

//                    compressImage(activity)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                AppCompatActivity.RESULT_CANCELED -> callback.onCanceled()
                else -> callback.onError()
            }
        }
    }


    //todo delete later?
    object FileUtil {
        private const val EOF = -1
        private const val DEFAULT_BUFFER_SIZE = 1024 * 4

        @Throws(IOException::class)
        fun from(context: Context, uri: Uri?): File {
            val inputStream = context.contentResolver.openInputStream(uri!!)
            val fileName = getFileName(context, uri)
            val splitName = splitFileName(fileName)
            var tempFile = File.createTempFile(splitName[0], splitName[1])
            tempFile = rename(tempFile, fileName)
            tempFile.deleteOnExit()
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(tempFile)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            if (inputStream != null) {
                copy(inputStream, out)
                inputStream.close()
            }

            out?.close()
            return tempFile
        }

        private fun splitFileName(fileName: String): Array<String> {
            var name = fileName
            var extension = ""
            val i = fileName.lastIndexOf(".")
            if (i != -1) {
                name = fileName.substring(0, i)
                extension = fileName.substring(i)
            }

            return arrayOf(name, extension)
        }

        private fun getFileName(context: Context, uri: Uri): String {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor = context.contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf(File.separator)
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result
        }

        private fun rename(file: File, newName: String): File {
            val newFile = File(file.parent, newName)
            if (newFile != file) {
                if (newFile.exists() && newFile.delete()) {
                    Log.d("FileUtil", "Delete old $newName file")
                }
                if (file.renameTo(newFile)) {
                    Log.d("FileUtil", "Rename file to $newName")
                }
            }
            return newFile
        }

        @Throws(IOException::class)
        private fun copy(input: InputStream, output: OutputStream?) {
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var n = input.read(buffer)
            while (EOF != n) {
                output!!.write(buffer, 0, n)
                n = input.read(buffer)
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
                storageDir      /* directory */
            )

        }
    }

    interface Callback {
        fun onImageCompressed(image64: String?, imageBitmap: Bitmap?)
        fun onCanceled()
        fun onError()
    }

    companion object {
        private val TAG = ImageHelper::class.java.simpleName
        const val PHOTO_REQUEST_CODE = 6564
        const val GALLERY_REQUEST_CODE = 6565
        const val REQUEST_PERMISSION_CODE_CAMERA = 56
        const val REQUEST_PERMISSION_CODE_GALLERY = 57
    }
}