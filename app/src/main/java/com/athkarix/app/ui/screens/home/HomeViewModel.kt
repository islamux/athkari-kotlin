package com.athkarix.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/** Navigation events emitted from the Home screen to the NavHost. */
sealed class HomeNavigationEvent {
    data class GoToRoute(val route: String) : HomeNavigationEvent()
}

/** Emits navigation events for each of the 11 category buttons on the home screen. */
class HomeViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvent: SharedFlow<HomeNavigationEvent> = _navigationEvent.asSharedFlow()

    // — One navigation method per category button —
    fun goToAthkarSabah() = navigate("athkar_sabah")
    fun goToAthkarMassa() = navigate("athkar_massa")
    fun goToTasbih() = navigate("tasbih")
    fun goToEstigfar() = navigate("estigfar")
    fun goToHamd() = navigate("hamd")
    fun goToSalatAlaRasoul() = navigate("salat_ala_rasoul")
    fun goToDuaMenQuran() = navigate("duaa_quran")
    fun goToDuaMenSunnah() = navigate("duaa_sunnah")
    fun goToAthkarAfterSalat() = navigate("athkar_after_salat")
    fun goToAssmaHussna() = navigate("assma_hussna")
    fun goToAthkarBeforeBed() = navigate("athkar_before_bed")
    fun goToNotificationSettings() = navigate("notification_settings")
    fun goToSearch() = navigate("search")

    // — Shared single-shot emit —
    private fun navigate(route: String) {
        viewModelScope.launch {
            _navigationEvent.emit(HomeNavigationEvent.GoToRoute(route))
        }
    }
}
