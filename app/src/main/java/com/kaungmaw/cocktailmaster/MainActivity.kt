package com.kaungmaw.cocktailmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.kaungmaw.cocktailmaster.databinding.ActivityMainBinding


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //setup navigationUi with bottomNavigation
        val navController = this.findNavController(R.id.nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        //toggle for dark mode
        val switch = binding.switch1
        switch.isChecked = DarkModeHelper.getInstance(applicationContext).isDark()
        switch.setOnClickListener {
            DarkModeHelper.getInstance(applicationContext).toggleDark()
        }

/*        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("MainActivity", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                task.result?.apply {
                    Log.i("MainActivity", "token : ${this.token}")
                }

            }
            )

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 5
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf("darkThemeSupport" to false))

        switch.isVisible = remoteConfig.getBoolean("darkThemeSupport")

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    switch.isVisible = remoteConfig.getBoolean("darkThemeSupport")
                    Toast.makeText(
                        this, "Fetch and activate succeeded $updated",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }*/
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return NavigationUI.navigateUp(
//            this.findNavController(R.id.nav_host_fragment)
//        )
//    }
}
