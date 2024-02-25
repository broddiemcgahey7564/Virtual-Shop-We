package com.ivettehernandez.virtual_shop.article

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivettehernandez.virtual_shop.R
import com.ivettehernandez.virtual_shop.utils.Utils
import kotlinx.android.synthetic.main.article_detail.*
import org.json.JSONObject






/**
 * Created by Ivette Hernández on 2019-08-09.
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
class ArticleDetail : AppCompatActivity() {
    private val BACK_STACK_ROOT_TAG = "root_fragment"
    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    private var articleId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_detail)
        supportActionBar!!.title = getString(R.string.article_detail)

        val intent = intent
        articleId = intent.getStringExtra("_id")

        getData()
        deleteData()
        modifyData()

    }

    private fun modifyData(){
        button_modify.setOnClickListener {
            val intent = Intent(this, ArticleModify::class.java)
            intent.putExtra("articleId", Utils.articleId)
            startActivity(intent)
        }
    }

    private fun deleteData() {
        button_delete.setOnClickListener {
            val queue = Volley.newRequestQueue(this)
            val url = Utils.article + articleId
            val preferences = PreferenceManager.getDefaultSharedPreferences(this@ArticleDetail)
            val token_user = preferences.getString("token", "")


            val getRequest = @SuppressLint("CommitPrefEdits")
            object : StringRequest(
                Method.DELETE, url,
                Response.Listener { response ->
                    Log.d("Response", response)

                    if (!response.isEmpty()) {

                        showToast(response.toString())

                        val fragment =  ArticleList()

                       // val fragmentTransaction = supportFragmentManager.beginTransaction()
                       // fragmentTransaction.replace(R.id.content_drawer, fragment)
                       // fragmentTransaction.addToBackStack(BACK_STACK_ROOT_TAG)
                       // fragmentTransaction.commit()

                        finish()

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
    }

    private fun getData() {
        val queue = Volley.newRequestQueue(this)
        val url = Utils.article + articleId

        val preferences = PreferenceManager.getDefaultSharedPreferences(this@ArticleDetail)
        val token_user = preferences.getString("token", "")


        val getRequest = @SuppressLint("CommitPrefEdits")
        object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                Log.d("Response", response)
                if (!response.isEmpty()) {

                    val jsonObject = JSONObject(response)
                    val productObject = jsonObject.getJSONObject("product")

                    editor = preferences.edit()
                    editor.putString("articleId", productObject.getString("_id"))
                    editor.apply()

                    Utils.articleId = productObject.getString("_id")


                    tv_article_name.text = productObject.getString("nombre")
                    val image: String = productObject.getString("imagen")
                    val imageV: ImageView = findViewById(R.id.image_product_detail)

                    imageV.setImageBitmap(base64ToByteArray(image))
                    tv_article_description.text = productObject.getString("descripcion")
                    tv_article_price.text = "$ ${productObject.getString("precio")}"



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


    fun showToast(message: String) = Toast.makeText(this@ArticleDetail, message, Toast.LENGTH_LONG).show()

    private fun base64ToByteArray(image: String): Bitmap? {
        val base64Image = image.split(",")[1]
        val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

}


