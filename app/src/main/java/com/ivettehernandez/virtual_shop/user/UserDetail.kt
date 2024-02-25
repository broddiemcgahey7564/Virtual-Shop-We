package com.ivettehernandez.virtual_shop.user

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivettehernandez.virtual_shop.R
import com.ivettehernandez.virtual_shop.article.ArticleList
import com.ivettehernandez.virtual_shop.article.ArticleModify
import com.ivettehernandez.virtual_shop.auth.LoginActivity
import com.ivettehernandez.virtual_shop.utils.Utils
import kotlinx.android.synthetic.main.article_detail.*
import kotlinx.android.synthetic.main.fragment_user_detail.*
import org.json.JSONObject


@Suppress("DEPRECATION")
class UserDetail : AppCompatActivity() {

    lateinit var userId: String
    lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user_detail)

        getDetail()

        button_modify_user.setOnClickListener {
            val intent = Intent(this, UserModify::class.java)
            intent.putExtra("userId", userId)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)
        }

        button_delete_user.setOnClickListener {
            val queue = Volley.newRequestQueue(this)
            val url = Utils.user + Utils._id
            val preferences = PreferenceManager.getDefaultSharedPreferences(this@UserDetail)
            val token_user = preferences.getString("token", "")

            val getRequest = @SuppressLint("CommitPrefEdits")
            object : StringRequest(
                Method.DELETE, url,
                Response.Listener { response ->
                    Log.d("Response", response)

                    if (!response.isEmpty()) {

                        val jsonObject = JSONObject(response)
                        val message = jsonObject.getString("message")

                        showToast(message)

                        emptyPreferences()

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
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

    private fun emptyPreferences() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this@UserDetail)
        val editor = preferences.edit()

        editor.putString("token", "")
        editor.putString("_id", "")
        editor.putString("email", "")


        editor.apply()
    }

    private fun getDetail() {

        val queue = Volley.newRequestQueue(this)
        val url = Utils.user + Utils._id

        val preferences = PreferenceManager.getDefaultSharedPreferences(this@UserDetail)
        val token_user = preferences.getString("token", "")

        val getRequest = @SuppressLint("CommitPrefEdits")
        object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                Log.d("Response", response)
                if (!response.isEmpty()) {

                    val jsonObject = JSONObject(response)

                    Log.e("jsonObject", jsonObject.toString())
                    val userObject = jsonObject.getJSONObject("user")
                    userId = userObject.getString("_id")
                    userEmail = userObject.getString("email")

                    tv_email_description.text = userObject.getString("email")

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


    fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
