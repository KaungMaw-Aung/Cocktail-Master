package com.kaungmaw.cocktailmaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.kaungmaw.cocktailmaster.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navController = this.findNavController(R.id.nav_host_fragment)
        val toolbar = binding.toolbar
        drawer = binding.drawerLayout

        NavigationUI.setupWithNavController(toolbar, navController, drawer)
        NavigationUI.setupWithNavController(binding.navView, navController)


        val switch = findViewById<Switch>(R.id.switch1)
        switch.isChecked = DarkModeHelper.getInstance(applicationContext).isDark()
        switch.setOnClickListener {
            DarkModeHelper.getInstance(applicationContext).toggleDark()
        }

        FirebaseInstanceId.getInstance().instanceId
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
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(this.findNavController(R.id.nav_host_fragment), drawer)
    }
}
