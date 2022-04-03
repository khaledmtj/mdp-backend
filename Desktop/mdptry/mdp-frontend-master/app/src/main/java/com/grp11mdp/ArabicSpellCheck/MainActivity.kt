package com.grp11mdp.ArabicSpellCheck

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.*
import okio.IOException
import java.io.ByteArrayOutputStream
import java.io.File

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.util.concurrent.TimeUnit


private const val FILE_NAME = "photo.png"
private const val GALL_REQUEST_CODE = 3
private const val CAM_REQUEST_CODE = 4
private lateinit var photoFile: File

@Serializable
class SentJson(val base64: String)

@Serializable
class ReceivedJson(val text_detected: String)

class MainActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var galleryButton: FloatingActionButton
    lateinit var scrollViewOfText: ScrollView
    lateinit var textView: TextView
    lateinit var cameraButton: FloatingActionButton
    private var base64OfImage = ""
    lateinit var convertButton: FloatingActionButton
    lateinit var removeImageButton: FloatingActionButton

    private val urlMyBackend = "https://mdp-tesseract.herokuapp.com/data"
    private val urlLocalBackend = "http://192.168.1.103:5000/data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        galleryButton = findViewById(R.id.galleryButton)
        textView = findViewById(R.id.myTextView)
        cameraButton = findViewById(R.id.cameraButton)
        convertButton = findViewById(R.id.convertButton)
        removeImageButton = findViewById(R.id.buttonRemoveImage)
        scrollViewOfText = findViewById(R.id.MY_SCROLLER_ID)


        galleryButton.setOnClickListener {
            val myIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(myIntent, 3)

        }

        cameraButton.setOnClickListener {
            val camPicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)

            val fileProvider = FileProvider.getUriForFile(
                this,
                "com.grp11mdp.ArabicSpellCheck.fileprovider",
                photoFile
            )
            camPicIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            startActivityForResult(camPicIntent, 4)
        }

        convertButton.setOnClickListener {
            convert()
        }
        removeImageButton.setOnClickListener {
            imageView.setImageBitmap(null)
            base64OfImage = ""
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                GALL_REQUEST_CODE -> {
                    val selectedImage = data?.data
                    imageView.setImageURI(selectedImage)

                    val bitmap = convertImageToBitmap()
                    base64OfImage = convertBitmapToBase64(bitmap)

//                    post(base64OfImage)
                }

                CAM_REQUEST_CODE -> {
//                    val bitmap = data?.extras?.get("data") as Bitmap
                    val takenImageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    imageView.setImageBitmap(takenImageBitmap)

                    base64OfImage = convertBitmapToBase64(takenImageBitmap)

//                    post(base64OfImage)
                }

                else -> {
                    Toast.makeText(this, "Unknown request code.", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    private fun convert() {
//        get()
        if (base64OfImage != null && base64OfImage != "") {
            val imageAfter2Conversions = convertBase64ToBitmap(base64OfImage)
            imageView.setImageBitmap(imageAfter2Conversions)
            post(base64OfImage)
        } else {
            Toast.makeText(this, "No image available to convert.", Toast.LENGTH_LONG).show()
        }

    }

    private fun getPhotoFile(fileName: String): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".png", storageDir)
    }

    private fun convertImageToBitmap(): Bitmap {
        val drawable = imageView.drawable as BitmapDrawable
        return drawable.bitmap
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val image = stream.toByteArray()
        return Base64.encodeToString(image, Base64.DEFAULT)
    }

    private fun convertBase64ToBitmap(base64String: String): Bitmap {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        return decodedImage
//        imageView.setImageBitmap(decodedImage)
    }

    private fun get() {
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .get()
            .url(urlMyBackend)
            .build()

        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
//                textView_response.text = e.message
                println("error: " + e.message)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Server Down", Toast.LENGTH_SHORT).show()
                    textView.text = "Error connecting to server"
                }
            }

            override fun onResponse(call: Call, response: Response) {

                println("Worked! Response success? ${response.isSuccessful}\nCode: ${response.code}")
                if (response.isSuccessful) {
                    try {
//                        val strReturned = Json.decodeFromString<ReceivedJson>(response.body!!.string())
                        val strTest = response.body?.string()
                        println(strTest)
                        val text_detected = strTest?.let { ReceivedJson(it) }
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "test", Toast.LENGTH_LONG)
                            textView.text = strTest
//                        textView.text = strReturned.textDetected
                        }
                    } catch (e: IOException) {
                        println("Error reading response body.\n${e}")
                    }
                }
            }
        })
    }


    private fun post(base64: String) {
        println("Started post")
        val payloadJson = SentJson(base64)
        val sentPayloadJson = Json.encodeToString(payloadJson)

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
        val requestBody: RequestBody = FormBody.Builder()
            .add("sample", base64)
            .build()

        val request = Request.Builder()
            .post(requestBody)
            .url(urlMyBackend)
            .build()

        runOnUiThread {
            textView.text = "Loading..."
        }

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                textView_response.text = e.message
                println("error: " + e.message)
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Server Down", Toast.LENGTH_SHORT).show()
                    textView.text = "Error connecting to server"
                }
            }

            override fun onResponse(call: Call, response: Response) {

                println("Worked! Response success? ${response.isSuccessful}\nCode: ${response.code}")
                if (response.isSuccessful) {
                    try {
//                        val strTest = response.body?.string()
//                        println(strTest)
                        val jsonReturned = Json.decodeFromString<ReceivedJson>(response.body!!.string())
                        val strReturned = jsonReturned.text_detected
                        println(strReturned)
                        runOnUiThread {
                            Toast.makeText(this@MainActivity, "Result Obtained", Toast.LENGTH_LONG)
                            if (strReturned != "") {
                                textView.text = strReturned
                            } else {
                                textView.text = "No text detected."
                            }
                        }
                    } catch (e: IOException) {
                        println("Error reading response body.\n${e}")
                    }
                }
                else {
                    runOnUiThread {
                        textView.text = "Error while connecting to server - code: ${response.code}\n"
                        if (response.code == 503) {
                            textView.text = "Server timed out."
                        }
                    }
                }
            }
        })
    }
}