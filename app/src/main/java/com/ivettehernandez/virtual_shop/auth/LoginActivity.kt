package com.ivettehernandez.virtual_shop.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivettehernandez.virtual_shop.DrawerActivity
import com.ivettehernandez.virtual_shop.MainActivity
import com.ivettehernandez.virtual_shop.R
import com.ivettehernandez.virtual_shop.utils.Utils
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject


/**
 * Created by Ivette Hernández on 2019-08-07.
 */

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        doLogin()
    }


    fun showToast(message: String) = Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()


    fun doLogin() {

        btn_login.setOnClickListener {
            val e = et_email.text.toString().trim()
            val p = et_password.text.toString().trim()

            val queue = Volley.newRequestQueue(this)
            val url = Utils.login

            val postRequest = @SuppressLint("CommitPrefEdits")
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // response
                    Log.d("Response", response)

                    showToast(response)

                    if (!response.isEmpty()) {

                        val jsonObject = JSONObject(response)
                        val userobject = jsonObject.getJSONObject("user")


                        Log.e("userobject", userobject.toString())

                        preferences = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                        editor = preferences.edit()

                        editor.putString("token", jsonObject.getString("token"))
                        editor.putString("_id", userobject.getString("_id"))
                        editor.putString("email", userobject.getString("email"))
                        editor.apply()

                        val intent = Intent(this@LoginActivity, DrawerActivity::class.java)
                        startActivity(intent)
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
                    params["email"] = e
                    params["password"] = p

                    return params
                }
            }
            queue.add(postRequest)

        }

        btn_register.setOnClickListener {
            val activityIntent = Intent(this, RegisterActivity::class.java)
            startActivity(activityIntent)
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}