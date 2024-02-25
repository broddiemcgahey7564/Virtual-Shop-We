package com.ivettehernandez.virtual_shop.article

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ivettehernandez.virtual_shop.R


/**
 * Created by Ivette Hernández on 2019-08-09.
 */

class ArticleAdapter(val recyclerList: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ViewHolder>(){
    override fun onBindViewHolder(viewHolder: ArticleAdapter.ViewHolder, position: Int) {
        viewHolder.bind(recyclerList[position])
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position:Int): ArticleAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_article, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recyclerList.size
    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)  {
        val view =  itemView
        val articleName = itemView.findViewById(R.id.tv_nombre) as TextView
        val articleDesc = itemView.findViewById(R.id.tv_descripcion) as TextView
        val articlePrice = itemView.findViewById(R.id.tv_precio) as TextView
        val articleImage = itemView.findViewById(R.id.image_article) as ImageView

        fun bind(recyclerItemText: Article)  {

            articleName.text = recyclerItemText.nombre
            articleDesc.text= recyclerItemText.descripcion
            articlePrice.text = "$${recyclerItemText.precio}"

            val imaging = recyclerItemText.imagen

            if (imaging !== "null") {
                val base64Image = imaging.split(",")[1]
                val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                articleImage.setImageBitmap(decodedByte)
            }

            view.setOnClickListener{
                val intent = Intent(view.rootView.context, ArticleDetail::class.java)
                intent.putExtra("_id", recyclerItemText._id)
                view.context.startActivity(intent)
            }
        }
    }
}