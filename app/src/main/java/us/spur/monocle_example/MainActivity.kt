package us.spur.monocle_example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import us.spur.monocle.Monocle

class MainActivity : AppCompatActivity() {

    // Location is optional: run the assessment whether or not the user grants it
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        runMonocleAssessment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!MonocleExampleApplication.isPublishableKeySet(getString(R.string.publishable_key))) {
            showResult("Please set your Publishable key in the strings.xml file")
            return
        }

        if (hasLocationPermission()) {
            runMonocleAssessment()
        } else {
            requestLocationPermissions()
        }
    }

    private fun runMonocleAssessment() {
        lifecycleScope.launch {
            val assessmentResult = Monocle.getInstance().assess()
            showResult(
                assessmentResult.fold(
                    onSuccess = { response -> response.data },
                    onFailure = { error -> "Assessment failed: ${error.message}" }
                )
            )
        }
    }

    private fun showResult(text: String) {
        findViewById<TextView>(R.id.assessmentResultTextView).text = text
    }

    private fun hasLocationPermission(): Boolean {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseLocationGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fineLocationGranted || coarseLocationGranted
    }

    private fun requestLocationPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}
