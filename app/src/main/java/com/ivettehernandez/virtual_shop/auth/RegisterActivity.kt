package com.ivettehernandez.virtual_shop.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivettehernandez.virtual_shop.DrawerActivity
import com.ivettehernandez.virtual_shop.MainActivity
import com.ivettehernandez.virtual_shop.R
import com.ivettehernandez.virtual_shop.utils.Utils
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject


/**
 * Created by Ivette Hernández on 2019-08-07.
 */
@Suppress("DEPRECATION")
class RegisterActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        doRegister()
    }

    fun showToast(message: String) = Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()


    private fun doRegister() {
        btn_register.setOnClickListener {
            val name = et_name.text.toString().trim()
            val email = et_email.text.toString().trim()
            val pass = et_password.text.toString().trim()

            val queue = Volley.newRequestQueue(this)
            val url = Utils.register

            val postRequest = @SuppressLint("CommitPrefEdits")

            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    val jsonObject = JSONObject(response)
                    if (!response.isEmpty()) {

                        val userobject = jsonObject.getJSONObject("user")

                        preferences = PreferenceManager.getDefaultSharedPreferences(this@RegisterActivity)
                        editor = preferences.edit()

                        editor.putString("token", jsonObject.getString("token"))
                        editor.putString("_id", userobject.getString("_id"))
                        editor.putString("email", userobject.getString("email"))
                        editor.apply()

                        val intent = Intent(this@RegisterActivity, DrawerActivity::class.java)
                        startActivity(intent)
                        finish()

                    }

                },
                Response.ErrorListener { response ->
                    showToast(response.toString())
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["name"] = name
                    params["email"] = email
                    params["password"] = pass

                    return params
                }
            }
            queue.add(postRequest)

        }

        btn_back.setOnClickListener {
            finish()
        }
    }

}
