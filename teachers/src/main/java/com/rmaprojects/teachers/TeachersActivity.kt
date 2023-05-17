package com.rmaprojects.teachers

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.rmaprojects.core.utils.buildSnackbar
import com.rmaprojects.teachers.databinding.ActivityTeachersActivtiyBinding
import dagger.hilt.android.AndroidEntryPoint
import mumayank.com.airlocationlibrary.AirLocation
import android.location.Location as AndroidLocation

@AndroidEntryPoint
class TeachersActivity : AppCompatActivity(R.layout.activity_teachers_activtiy) {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var airLocation: AirLocation

    private val sharedViewModel: SharedViewModel by viewModels()

    private val binding: ActivityTeachersActivtiyBinding by viewBinding()

    override fun onCreate(savedInstanceState: Bundle?) {
        setupAirLocation()
        super.onCreate(savedInstanceState)

        airLocation.start()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_student_list,
                R.id.nav_assign_student,
                R.id.nav_teacher_attendance,
                R.id.nav_profile,
            )
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_graph_teacher_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->

            var isBottomNavVisible = true
            var topAppBarTitle = ""

            when (destination.id) {
                R.id.assignStudentInputFragment -> {
                    isBottomNavVisible = false
                    topAppBarTitle = "Tambahkan Kelas"
                }
                R.id.studentListInputFragment -> {
                    isBottomNavVisible = false
                    topAppBarTitle = "Tambahkan Siswa"
                }
                R.id.nav_student_list -> {
                    isBottomNavVisible = true
                    topAppBarTitle = "List Siswa"
                }
                R.id.nav_assign_student -> {
                    isBottomNavVisible = true
                    topAppBarTitle = "List Kelas"
                }
                R.id.nav_teacher_attendance -> {
                    isBottomNavVisible = true
                    topAppBarTitle = "Absensi"
                }
                R.id.nav_profile -> {
                    isBottomNavVisible = true
                    topAppBarTitle = "Profile"
                }
                R.id.nav_scoring -> {
                    isBottomNavVisible = true
                    topAppBarTitle = "Penilaian"
                }
                R.id.studentScoringDashboardInput -> {
                    isBottomNavVisible = false
                    topAppBarTitle = "Input Nilai"
                }
            }

            binding.bottomNavTeacher.isVisible = isBottomNavVisible
            binding.toolBar.title = topAppBarTitle
        }

        binding.bottomNavTeacher.setupWithNavController(navController)
        setSupportActionBar(binding.toolBar)
    }

    private fun setupAirLocation() {
        airLocation = AirLocation(this, object : AirLocation.Callback {
            override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
                buildSnackbar(
                    binding.root,
                    "Gagal mendapatkan Lokasi"
                ).show()
            }

            override fun onSuccess(locations: ArrayList<AndroidLocation>) {
                sharedViewModel.setLocation(
                    Location(
                        longitude = locations.first().longitude,
                        latitude = locations.first().latitude
                    )
                )
            }

        }, true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLocation.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}