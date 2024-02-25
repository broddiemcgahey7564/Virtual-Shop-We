package com.ivettehernandez.virtual_shop.article

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import androidx.recyclerview.widget.RecyclerView
import com.ivettehernandez.virtual_shop.R
import com.ivettehernandez.virtual_shop.utils.Utils
import org.json.JSONObject
import java.util.HashMap


@Suppress("DEPRECATION")
class ArticleList : Fragment() {

    private var listener: OnFragmentInteractionListener? = null

    @SuppressLint("SetTextI18n", "CommitPrefEdits")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view= inflater.inflate(R.layout.list_infinity,container,false)
        val recyclerContext = view.findViewById(R.id.recyclerView) as RecyclerView
        val dataList = mutableListOf<Article>()
        val layoutManager = LinearLayoutManager(this.requireContext())

        val queue = Volley.newRequestQueue(this.requireContext())
        val urlArticle = Utils.article

        val preferences = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
        val tokenUser = preferences.getString("token", "")

        showToast("Loading")

        val getRequest = @SuppressLint("CommitPrefEdits")
        object : StringRequest(
            Method.GET, urlArticle,
            Response.Listener { response ->
                Log.d("Response", response)

                if (!response.isEmpty()) {

                    val jsonObject = JSONObject(response)


                    val productList = jsonObject.getJSONArray("products")
                    var x = 0
                    while (x < productList.length()) {
                        val responseObject = productList.getJSONObject(x)
                        dataList.add(
                            Article(
                                responseObject.getString("_id"),
                                responseObject.getString("nombre"),
                                responseObject.getString("imagen"),
                                responseObject.getString("descripcion"),
                                responseObject.getInt("precio")

                        ))
                        x++
                    }

                    val adapter = ArticleAdapter(recyclerList = dataList)
                    recyclerContext.layoutManager = layoutManager
                    recyclerContext.adapter = adapter

                }

            },
            Response.ErrorListener { response ->
                Log.d("Error.Response", response.toString())
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = "Bearer $tokenUser"

                return params
            }
        }
        queue.add(getRequest)

        return  view
    }


    fun showToast(message: String) = Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).show()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
//            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }


}
