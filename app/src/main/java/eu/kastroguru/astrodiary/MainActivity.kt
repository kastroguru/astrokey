package eu.kastroguru.astrodiary

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import eu.kastroguru.astrodiary.databinding.ActivityMainBinding
import eu.kastroguru.astrodiary.ui.legal.LegalFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Android 15 (targetSdk 35) enforces edge-to-edge: the system bars become transparent and
        // content draws behind them. Pad the toolbar by the status-bar inset (its lavender background
        // then fills the strip) and the bottom nav by the navigation-bar inset, so neither slips under
        // a system bar. No-op on older devices, where the system already insets the content.
        WindowInsetsControllerCompat(window, binding.root).apply {
            isAppearanceLightStatusBars = true       // dark status icons on the pale strip
            isAppearanceLightNavigationBars = true
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, insets ->
            v.updatePadding(top = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavigation) { v, insets ->
            v.updatePadding(bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val languagePicked = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
            .getBoolean("language_picked", false)

        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(
            if (languagePicked) R.id.birthDataListFragment else R.id.languagePickerFragment
        )
        navController.setGraph(graph, null)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.birthDataListFragment,
                R.id.eventListFragment,
                R.id.nowFragment,
                R.id.transitFragment,
                R.id.humanDesignFragment
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isPickerScreen = destination.id == R.id.languagePickerFragment
            binding.toolbar.visibility = if (isPickerScreen) View.GONE else View.VISIBLE
            binding.bottomNavigation.visibility = if (isPickerScreen) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_burger) {
            val anchor = binding.toolbar.findViewById<View>(R.id.action_burger) ?: binding.toolbar
            val popup = PopupMenu(this, anchor)
            popup.menuInflater.inflate(R.menu.menu_overflow, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_settings -> navController.navigate(R.id.settingsFragment)
                    R.id.action_terms    -> navController.navigate(
                        R.id.legalFragment,
                        bundleOf(LegalFragment.ARG_TYPE to LegalFragment.TYPE_TERMS)
                    )
                    R.id.action_privacy  -> navController.navigate(
                        R.id.legalFragment,
                        bundleOf(LegalFragment.ARG_TYPE to LegalFragment.TYPE_PRIVACY)
                    )
                    R.id.action_gdpr     -> navController.navigate(
                        R.id.legalFragment,
                        bundleOf(LegalFragment.ARG_TYPE to LegalFragment.TYPE_GDPR)
                    )
                    // AGPL-3.0: пълният изходен код на приложението е публично достъпен
                    R.id.action_source_code -> startActivity(
                        android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse("https://github.com/kastroguru/astrokey")
                        )
                    )
                }
                true
            }
            popup.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
