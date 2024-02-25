package com.ivettehernandez.virtual_shop.article

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.ivettehernandez.virtual_shop.DrawerActivity
import com.ivettehernandez.virtual_shop.R
import com.ivettehernandez.virtual_shop.utils.Utils
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionFragment
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_article_add.*
import org.json.JSONObject
import java.io.*
import java.util.*


/**
 * Created by Ivette Hernández on 2019-08-09.
 */

class ArticleAdd : PermissionFragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private val GALLERY = 1
    private val CAMERA = 2
    var stockImage: ImageView? = null
    private val BACK_STACK_ROOT_TAG = "root_fragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_article_add, container, false)
        val nameArticle: TextView =  view.findViewById(R.id.et_artname)
        val descriptionArticle: TextView = view.findViewById(R.id.et_artdesc)
        val priceArticle: TextView = view.findViewById(R.id.et_artprice)
        val takePhoto: Button = view.findViewById(R.id.btn_camera_article)
        val addArticle: Button = view.findViewById(R.id.btn_article_add)
        stockImage = view.findViewById(R.id.article_photo)

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

        addArticle.setOnClickListener {
            stockImage!!.isDrawingCacheEnabled = true
            val bmap: Bitmap = stockImage!!.drawingCache
            val baos = ByteArrayOutputStream()

            bmap.compress(Bitmap.CompressFormat.JPEG, 50, baos as OutputStream?)
            val imageBytes = baos.toByteArray()
            val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)

            val preferences = PreferenceManager.getDefaultSharedPreferences(this.requireContext())
            val tokenUser = preferences.getString("token", "")

            val queue = Volley.newRequestQueue(this.requireContext())
            val url = Utils.article

            val postRequest = @SuppressLint("CommitPrefEdits")
            object : StringRequest(
                Method.POST, url,
                Response.Listener { response ->
                    // response
                    Log.d("Response", response)


                    if (!response.isEmpty()) {

                        val jsonObject = JSONObject(response)
                        val message = jsonObject.getString("message")

                        showToast(message)

                        val fragment = ArticleList()

                        fragmentManager?.beginTransaction()?.addToBackStack(BACK_STACK_ROOT_TAG)
                            ?.replace(R.id.content_drawer, fragment)
                            ?.commit()


                    }

                },
                Response.ErrorListener { response ->
                    // error
                    Log.d("Error.Response", response.toString())
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
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

        return view
    }

    override fun onResume() {
        super.onResume()

    }

    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    fun showToast(message: String) = Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).show()


    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data.data
                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,contentURI)
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent,CAMERA)
    }
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
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
            MediaScannerConnection.scanFile(this.requireContext(), arrayOf(f.getPath()), arrayOf("image/jpeg"), null)
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
