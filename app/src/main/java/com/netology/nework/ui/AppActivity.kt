package com.netology.nework.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import com.netology.nework.R
import com.netology.nework.auth.AppAuth
import com.netology.nework.ui.NewPostFragment.Companion.textArg
import com.netology.nework.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)


    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text?.isNotBlank() != true) {
                return@let
            }

            intent.removeExtra(Intent.EXTRA_TEXT)
            findNavController(R.id.nav_host_fragment)
                .navigate(
                    R.id.action_feedFragment_to_newPostFragment,
                    Bundle().apply {
                        textArg = text
                    }
                )
        }

        viewModel.data.observe(this) {
            invalidateOptionsMenu()
        }

//        checkGoogleApiAvailability()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menuInflater.inflate(R.menu.account_button, menu)

        menuInflater.inflate(R.menu.users_button, menu)

        menuInflater.inflate(R.menu.posts_button, menu)

        menu.let {
            it.setGroupVisible(R.id.unauthenticated, !viewModel.authenticated)
            it.setGroupVisible(R.id.authenticated, viewModel.authenticated)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.signin -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.signInFragment)
                //AppAuth.getInstance().setAuth(5, "x-token")
                true
            }
            R.id.signup -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.signUpFragment)
                //AppAuth.getInstance().setAuth(5, "x-token")
                true
            }
            R.id.signout -> {
                AppAuth.getInstance().removeAuth()
                createDialog()
                true
            }
            R.id.userAccaunt -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.userAccountFragment)
                true
            }

            R.id.userList-> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.listOfUsersFragment)
                true
            }
            R.id.posts -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.feedFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("You signed out")
        builder.setNegativeButton("ok"){dialog, i ->
            findNavController(R.id.nav_host_fragment).navigate(R.id.feedFragment)
        }
        builder.show()
    }
}