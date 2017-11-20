package com.shoeboxscientist.mouthmaths

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.util.Log
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        Log.d("adam", "onStart")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val requiredPermission: String = Manifest.permission.RECORD_AUDIO;

            // Prompt for permission
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(requiredPermission), 101);
            }
        }

        // Runtime check not needed.
        Log.d("adam", "Runtime permission not needed")
        onStartAfterPermissionsCheck();
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        Log.d("adam", "Got permissions result. $permissions with grants $grantResults");
        onStartAfterPermissionsCheck()
    }

    fun onStartAfterPermissionsCheck() {
        Log.d("adam", "activity: permissions set up, starting")

        start.setOnClickListener {
            val whichTables = ArrayList<Int>()
            val toggles = arrayListOf(twos, threes, fours, fives, sixes, sevens,
                    eights, nines, tens, elevens, twelves);

            toggles.forEach { if (it.isChecked) whichTables.add(
                    Integer.parseInt(it.text.toString())) }

            if (whichTables.size == 0) {
                Log.d("adam", "No tables specified, ignoring.")
                return@setOnClickListener
            }

            val intent = Intent(this, TablesActivity::class.java)
            intent.putExtra("whichTables", whichTables)
            intent.putExtra("howMany", Integer.parseInt(numberOfQuestions.text.toString()))
            startActivity(intent)
        }
    }

    private fun gotResults(results: ArrayList<String>) {
        Log.d("adam", "Got speech results $results")
    }

    override fun onResume() {
        super.onResume()

    }
}
