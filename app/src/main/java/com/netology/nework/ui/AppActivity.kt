package com.netology.nework.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.messaging.FirebaseMessaging
import com.netology.nework.R
import com.netology.nework.auth.AppAuth
import com.netology.nework.ui.NewPostFragment.Companion.textArg
import com.netology.nework.viewmodel.AuthViewModel

class AppActivity : AppCompatActivity(R.layout.activity_app) {

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

        checkGoogleApiAvailability()
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
                true
            }
            R.id.signup -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.signUpFragment)
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
                findNavController(R.id.nav_host_fragment).navigate(R.id.usersFragment)
                true
            }
            R.id.posts -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.feedFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


        checkGoogleApiAvailability()

    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println(it)
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