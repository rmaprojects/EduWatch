package com.rmaprojects.eduwatch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.rmaprojects.auth.AuthActivity
import com.rmaprojects.core.data.source.local.LocalUser
import com.rmaprojects.core.utils.UserRole
import com.rmaprojects.parents.ParentsActivity
import com.rmaprojects.teachers.TeachersActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Firebase.initialize(this)
        when (LocalUser.role) {
            UserRole.PARENTS -> {
                startActivity(Intent(this, ParentsActivity::class.java))
                finish()
            }
            UserRole.TEACHERS -> {
                startActivity(Intent(this, TeachersActivity::class.java))
                finish()
            }
            UserRole.NONE -> {
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
            }
        }
    }
}