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
import javax.inject.Inject
import eu.kastroguru.astrodiary.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.kastroguru.astrodiary.data.ReadingMode
import eu.kastroguru.astrodiary.data.ReadingModeStore
import eu.kastroguru.astrodiary.ui.help.ScreenHelp
import eu.kastroguru.astrodiary.ui.legal.LegalFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var readingModeStore: ReadingModeStore

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
            when {
                !languagePicked -> R.id.languagePickerFragment
                !readingModeStore.isChosen -> R.id.readingModePickerFragment
                else -> R.id.birthDataListFragment
            }
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

        // The transits tab leads somewhere different in plain mode: today's contacts in words, with
        // no date controls, because moving the date means nothing to someone who does not read charts.
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.transitFragment && readingModeStore.current == ReadingMode.PLAIN) {
                if (navController.currentDestination?.id != R.id.transitReadingFragment) {
                    navController.navigate(R.id.transitReadingFragment)
                }
                true
            } else {
                androidx.navigation.ui.NavigationUI.onNavDestinationSelected(item, navController)
            }
        }

        navController.addOnDestinationChangedListener { _, destination, arguments ->
            val isPickerScreen = destination.id == R.id.languagePickerFragment ||
                destination.id == R.id.readingModePickerFragment
            binding.toolbar.visibility = if (isPickerScreen) View.GONE else View.VISIBLE
            binding.bottomNavigation.visibility = if (isPickerScreen) View.GONE else View.VISIBLE

            // The "?" belongs to the screen, not to the activity: look its explanation up here so
            // every destination gets one without having to remember anything in the fragment.
            // The wheel button belongs to the reading screen: that is the one place where the chart
            // is deliberately not on screen.
            val birthId = arguments?.getLong("birthDataId")?.takeIf { it != 0L }
            val eventId = arguments?.getLong("eventId")?.takeIf { it != 0L }
            when (destination.id) {
                R.id.chartReadingFragment -> {
                    chartToggleIcon = R.drawable.ic_chart_wheel
                    chartToggleAction = birthId?.let {
                        { navController.navigate(R.id.chartFragment, bundleOf("birthDataId" to it)) }
                    }
                }
                R.id.transitReadingFragment -> {
                    chartToggleIcon = R.drawable.ic_chart_wheel
                    chartToggleAction = { navController.navigate(R.id.transitFragment) }
                    binding.bottomNavigation.menu.findItem(R.id.transitFragment)?.isChecked = true
                }
                R.id.chartFragment, R.id.planetTableFragment, R.id.birthDataDetailFragment -> {
                    chartToggleIcon = R.drawable.ic_chart_wheel_off
                    chartToggleAction = birthId?.let {
                        { navController.navigate(R.id.chartReadingFragment, bundleOf("birthDataId" to it)) }
                    }
                }
                R.id.transitFragment -> {
                    chartToggleIcon = R.drawable.ic_chart_wheel_off
                    chartToggleAction = { navController.navigate(R.id.transitReadingFragment) }
                }
                // Events have the same pair: the entry as it reads, and the chart behind it.
                R.id.eventDetailFragment -> {
                    chartToggleIcon = R.drawable.ic_chart_wheel
                    chartToggleAction = eventId?.let {
                        { navController.navigate(R.id.eventChartFragment, bundleOf("eventId" to it)) }
                    }
                }
                R.id.eventChartFragment, R.id.eventPlanetTableFragment -> {
                    chartToggleIcon = R.drawable.ic_chart_wheel_off
                    chartToggleAction = eventId?.let {
                        { navController.navigate(R.id.eventDetailFragment, bundleOf("eventId" to it)) }
                    }
                }
                else -> {
                    chartToggleIcon = null
                    chartToggleAction = null
                }
            }

            helpTextRes = ScreenHelp.forDestination(destination.id)
            helpScreenTitle = destination.label?.toString().orEmpty()
            invalidateOptionsMenu()
        }
    }

    /** Explanation of the screen currently on top, or null for screens without a title. */
    private var helpTextRes: Int? = null
    /** Chart to open from the toolbar, when the screen on top is a reading of one. */
    /**
     * The chart button is a two-way switch, not a one-way door: a wheel on the plain screens (tap to
     * see the chart) and a struck-through wheel on the astrologer's screens (tap to leave it).
     */
    private var chartToggleIcon: Int? = null
    private var chartToggleAction: (() -> Unit)? = null
    private var helpScreenTitle: String = ""

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_help)?.isVisible = helpTextRes != null
        menu.findItem(R.id.action_chart)?.apply {
            isVisible = chartToggleAction != null
            chartToggleIcon?.let { setIcon(it) }
            setTitle(if (chartToggleIcon == R.drawable.ic_chart_wheel_off) R.string.show_reading else R.string.show_chart)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun showScreenHelp() {
        val textRes = helpTextRes ?: return
        MaterialAlertDialogBuilder(this)
            .setTitle(helpScreenTitle.ifBlank { getString(R.string.help_title) })
            .setMessage(textRes)
            .setPositiveButton(R.string.help_got_it, null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_chart) {
            chartToggleAction?.invoke()
            return true
        }
        if (item.itemId == R.id.action_help) {
            showScreenHelp()
            return true
        }
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
