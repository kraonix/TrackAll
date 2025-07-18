package com.example.trackall

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

import com.example.trackall.ui.expenses.ExpensesFragment
import com.example.trackall.databinding.ActivityMainBinding
import com.example.trackall.ui.bills.BillsFragment
import com.example.trackall.ui.reminders.RemindersFragment
import com.example.trackall.ui.todo.ToDoFragment
import com.example.trackall.util.SessionManager


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        var sessionManager = SessionManager(this)

        if (sessionManager.getLoggedInUsername() == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val window = window
            val params = window.attributes
            params.layoutInDisplayCutoutMode =
                android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = params
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(ExpensesFragment())

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_expenses -> loadFragment(ExpensesFragment())
                R.id.nav_bills -> loadFragment(BillsFragment())
                R.id.nav_todo -> loadFragment(ToDoFragment())
                R.id.nav_reminders -> loadFragment(RemindersFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}
