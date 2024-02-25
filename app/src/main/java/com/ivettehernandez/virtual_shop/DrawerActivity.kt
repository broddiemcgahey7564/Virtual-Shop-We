package com.ivettehernandez.virtual_shop


import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.ivettehernandez.virtual_shop.article.ArticleAdd
import com.ivettehernandez.virtual_shop.article.ArticleList
import com.ivettehernandez.virtual_shop.auth.LoginActivity
import com.ivettehernandez.virtual_shop.user.UserDetail
import com.ivettehernandez.virtual_shop.utils.Utils


@Suppress("DEPRECATION")
class DrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val BACK_STACK_ROOT_TAG = "root_fragment"

    lateinit var userMail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        userMail = Utils.email.toString()

        Log.e("userMail", userMail)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val fragment = ArticleList()
        supportFragmentManager.popBackStack(BACK_STACK_ROOT_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE)
       supportFragmentManager.beginTransaction().addToBackStack(BACK_STACK_ROOT_TAG).replace(R.id.content_drawer, fragment).commit()


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->

            val fragment = ArticleAdd()
            supportFragmentManager.beginTransaction().addToBackStack(BACK_STACK_ROOT_TAG).replace(R.id.content_drawer, fragment).commit()

        }
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.drawer, menu)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this@DrawerActivity)
        val emailUser = preferences.getString("email", "")
        val username: TextView = findViewById(R.id.name)
        username.text = emailUser

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_article_list -> {
                val fragment = ArticleList()
                supportFragmentManager.beginTransaction().addToBackStack(BACK_STACK_ROOT_TAG).replace(R.id.content_drawer, fragment).commit()
            }
            R.id.nav_add_article -> {
                val fragment = ArticleAdd()
                supportFragmentManager.beginTransaction().addToBackStack(BACK_STACK_ROOT_TAG).replace(R.id.content_drawer, fragment).commit()
            }

            R.id.nav_user_detail -> {
                val intent = Intent(this, UserDetail::class.java)
                startActivity(intent)
                finish()  }

            R.id.log_out -> {
                logout()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun logout() {

        emptyPreferences()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()

    }


    private fun emptyPreferences() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this@DrawerActivity)
        val editor = preferences.edit()

        editor.putString("token", "")
        editor.putString("_id", "")
        editor.putString("email", "")


        editor.apply()
    }


}


