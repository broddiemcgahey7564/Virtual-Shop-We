package com.ivettehernandez.virtual_shop.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivettehernandez.virtual_shop.R
import com.ivettehernandez.virtual_shop.utils.Utils
import kotlinx.android.synthetic.main.activity_user_modify.*
import org.json.JSONObject
import java.util.HashMap


/**
 * Created by Ivette Hernández on 2019-08-09.
 *
 *
 */
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
class UserModify: AppCompatActivity() {

    lateinit var userId: String
    lateinit var userMail: String
    private lateinit var userPassword: TextView
    private lateinit var userEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_modify)

        val intent = intent
        userId = intent.getStringExtra("userId")
        userMail = intent.getStringExtra("userEmail")

        Log.e("userMail", userMail)

        userEmail = findViewById(R.id.et_email_user)
        userPassword = findViewById(R.id.et_password_user)

        userEmail.text = userMail

        btn_profile_user_modify.setOnClickListener {
            sendPost()
        }

    }

    private fun sendPost() {
        val queue = Volley.newRequestQueue(this)
        val url = Utils.user + userId

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val tokenUser = preferences.getString("token", "")

        val postRequest = @SuppressLint("CommitPrefEdits")
        object : StringRequest(
            Method.PUT, url,
            Response.Listener { response ->
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
                val params = HashMap<String, String>()
                params["email"] = userEmail.text.toString().trim()
                params["password"] = userPassword.text.toString().trim()

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

    fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()


}