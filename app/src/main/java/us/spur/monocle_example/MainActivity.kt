package us.spur.monocle_example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import us.spur.monocle.Monocle
import us.spur.monocle.MonocleConfig

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        Log.d("Monocle", "setting up permissions launcher")
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationGranted || coarseLocationGranted) {
            CoroutineScope(Dispatchers.Main).launch {
                runMonocleAssessment()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val success = setupMonocle()
        if (!success) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.assessmentResultTextView).text =
                        "Please set your site token in the strings.xml file"
                }
            }
            return
        }

        if (hasLocationPermission()) {
            CoroutineScope(Dispatchers.Main).launch {
                runMonocleAssessment()
            }
        } else {
            requestLocationPermissions()
        }
    }

    // This method sets up us.spur.monocle.Monocle
    private fun setupMonocle(): Boolean {
        Log.d("Monocle", "setting up monocle")
        val siteToken = getString(R.string.site_token)
        // If you do not want all the plugins to run, you can specify the ones you want
//        val config = MonocleConfig(
//            token = siteToken,
//            enabledPlugins = MonoclePluginOptions.DNS or MonoclePluginOptions.LOCATION,
//        )

        if (siteToken.isEmpty() || siteToken == "CHANGEME") {
            return false
        } else {
            val config = MonocleConfig(token = siteToken)
            Monocle.setup(config, this)
            return true
        }
    }

    private suspend fun runMonocleAssessment() {
        val monocle = Monocle.getInstance()
        val assessmentResult = monocle.assess()
        assessmentResult.fold(
            onSuccess = { response ->
                Log.d("Monocle", "Assessment successful: ${response.data}")
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        findViewById<TextView>(R.id.assessmentResultTextView).text = response.data
                    }
                }
            },
            onFailure = { error ->
                Log.d("Monocle", "Assessment failed: ${error.message}")
                runOnUiThread {
                    // Assuming you have a TextView with the id `assessmentResultTextView`
                    findViewById<TextView>(R.id.assessmentResultTextView).text =
                        "Assessment failed: ${error.message}"
                }
            }
        )
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