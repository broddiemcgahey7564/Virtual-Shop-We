package com.ivettehernandez.virtual_shop.utils


/**
 * Created by Ivette Hernández on 2019-08-08.
 */

object Utils {

    private const val url = "https://api-rest-virtualshop.herokuapp.com/"

    const val login = "${url}api/signin"
    const val register = "${url}api/signup"
    const val user = "${url}api/user/"
    const val article = "${url}api/product/"


    var token: String? = null
    var _id: String? = null
    var email: String? = null
    var articleId: String? = null

}