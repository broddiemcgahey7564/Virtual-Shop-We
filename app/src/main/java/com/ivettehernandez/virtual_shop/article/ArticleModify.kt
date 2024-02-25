package com.ivettehernandez.virtual_shop.article

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivettehernandez.virtual_shop.R
import com.ivettehernandez.virtual_shop.utils.Utils
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity

import kotlinx.android.synthetic.main.activity_article_modify.*
import kotlinx.android.synthetic.main.article_detail.*
import org.json.JSONObject
import java.io.*
import java.util.*
import kotlin.collections.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ArticleModify : PermissionsActivity() {

    private var articleId: String = ""
    private lateinit var stockImage: ImageView
    private lateinit var nameArticle: TextView
    private lateinit var descriptionArticle: TextView
    private lateinit var priceArticle: TextView
    private lateinit var takePhoto: Button
    private lateinit var modifyArticle: Button
    private val CAMERA = 2
    private val GALLERY = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article_modify)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.article_modify)
        val intent = intent
        articleId = intent.getStringExtra("articleId")

        nameArticle =  findViewById(R.id.et_artname)
        descriptionArticle = findViewById(R.id.et_artdesc)
        priceArticle = findViewById(R.id.et_artprice)
        takePhoto = findViewById(R.id.btn_camera_article)
        modifyArticle = findViewById(R.id.btn_article_modify)
        stockImage = findViewById(R.id.article_photo)


        takePhoto.setOnClickListener {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), object :
                PermissionCallBack {
                override fun permissionGranted() {
                    super.permissionGranted()
                    Log.v("Camera permissions", "OK")
                }
                override fun permissionDenied() {
                    super.permissionDenied()
                    Log.v("Camera permissions", "Denied")
                }
            })

            takePhotoFromCamera()

        }

        getArticleDetail()
        modifyArticle()

    }


    private fun modifyArticle() {
        modifyArticle.setOnClickListener {
            stockImage!!.isDrawingCacheEnabled = true
            val bmap: Bitmap = stockImage!!.drawingCache
            val baos = ByteArrayOutputStream()

            bmap.compress(Bitmap.CompressFormat.JPEG, 50, baos as OutputStream?)
            val imageBytes = baos.toByteArray()
            val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val tokenUser = preferences.getString("token", "")

            val queue = Volley.newRequestQueue(this)
            val url = Utils.article + Utils.articleId

            val postRequest = @SuppressLint("CommitPrefEdits")
            object : StringRequest(
                Method.PUT, url,
                Response.Listener { response ->
                    // response
                    Log.d("Response", response)


                    if (!response.isEmpty()) {

                        val jsonObject = JSONObject(response)
                        val message = jsonObject.getString("message")

                        showToast(message)


                        finish()


                    }

                },
                Response.ErrorListener { response ->
                    // error
                    Log.d("Error.Response", response.toString())
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = java.util.HashMap<String, String>()
                    params["nombre"] = nameArticle.text.toString().trim()
                    params["imagen"] = "data:image/png;base64,$encodedImage"
                    params["precio"] = priceArticle.text.toString().trim()
                    params["descripcion"] = descriptionArticle.text.toString().trim()

                    return params
                }

                override fun getHeaders():Map<String, String> {

                    val params =  mutableMapOf<String, String>()
                    params.put("Authorization", "Bearer $tokenUser")
                    return params

                }

            }
            queue.add(postRequest)

        }
    }




    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }



    private fun getArticleDetail() {

        val queue = Volley.newRequestQueue(this)
        val url = Utils.article + articleId

        val preferences = PreferenceManager.getDefaultSharedPreferences(this@ArticleModify)
        val token_user = preferences.getString("token", "")


        val getRequest = @SuppressLint("CommitPrefEdits")
        object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                Log.d("Response", response)
                if (!response.isEmpty()) {

                    val jsonObject = JSONObject(response)
                    val productObject = jsonObject.getJSONObject("product")

                    nameArticle.setText(productObject.getString("nombre"))
                    descriptionArticle.setText(productObject.getString("descripcion"))
                    priceArticle.setText(productObject.getString("precio"))
                    stockImage.setImageBitmap(base64ToByteArray(productObject.getString("imagen")))
                }

            },
            Response.ErrorListener { response ->
                Log.d("Error.Response", response.toString())
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = "Bearer $token_user"

                return params
            }
        }
        queue.add(getRequest)


    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI)
                    val path = saveImage(bitmap)
                    stockImage!!.setImageBitmap(bitmap)
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }

            }

        }
        else if (requestCode == CAMERA)
        {
            val thumbnail = data!!.extras!!.get("data") as Bitmap
            stockImage!!.setImageBitmap(thumbnail)
            saveImage(thumbnail)
        }
    }


    fun showToast(message: String) = Toast.makeText(this@ArticleModify, message, Toast.LENGTH_LONG).show()

    private fun base64ToByteArray(image: String): Bitmap? {
        val base64Image = image.split(",")[1]
        val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun saveImage(myBitmap: Bitmap):String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            (Environment.getExternalStorageDirectory()).toString() + IMAGE_DIRECTORY)
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try
        {
            val f = File(wallpaperDirectory, ((Calendar.getInstance().getTimeInMillis()).toString() + ".jpg"))
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.getPath()), arrayOf("image/jpeg"), null)
            fo.close()
            return f.absolutePath
        }
        catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    companion object {
        private val IMAGE_DIRECTORY = "/virtualshop"
    }

}
