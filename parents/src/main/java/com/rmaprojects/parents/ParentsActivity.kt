package com.rmaprojects.parents

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.parents.databinding.ActivityParentsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParentsActivity : AppCompatActivity(R.layout.activity_parents) {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private val binding: ActivityParentsBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_calendar_student,
                R.id.nav_student_scoring,
                R.id.nav_student_stat,
                R.id.nav_profile,
            )
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_graph_teacher_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->

            var toolbarTitle = ""

            when (destination.id) {
                R.id.nav_calendar_student -> {
                    toolbarTitle = "Kalender Siswa"
                }
                R.id.nav_student_scoring -> {
                    toolbarTitle = "Nilai Siswa"
                }
                R.id.nav_student_stat -> {
                    toolbarTitle = "Statistik Siswa"
                }
                R.id.nav_profile ->  {
                    toolbarTitle = "Profile"
                }
            }

            binding.toolBar.title = toolbarTitle
        }
        binding.bottomNavTeacher.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}