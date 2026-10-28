package us.spur.monocle_example

import android.app.Application
import us.spur.monocle.Monocle
import us.spur.monocle.MonocleConfig

class MonocleExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val publishableKey = getString(R.string.publishable_key)
        if (!isPublishableKeySet(publishableKey)) return

        // Set up Monocle once, here in the Application class. If you do not want all the
        // plugins to run, you can specify the ones you want:
//        val config = MonocleConfig(
//            token = publishableKey,
//            enabledPlugins = MonoclePluginOptions.DNS or MonoclePluginOptions.LOCATION,
//        )
        Monocle.setup(MonocleConfig(token = publishableKey), this)
    }

    companion object {
        fun isPublishableKeySet(publishableKey: String) =
            publishableKey.isNotEmpty() && publishableKey != "CHANGEME"
    }
}
