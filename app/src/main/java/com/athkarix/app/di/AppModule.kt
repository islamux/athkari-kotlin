package com.athkarix.app.di

import android.content.Context
import com.athkarix.app.data.local.SharedPrefsManager
import com.athkarix.app.data.service.NotificationService
import com.athkarix.app.ui.screens.home.HomeViewModel
import com.athkarix.app.viewmodel.AssmaHussnaViewModel
import com.athkarix.app.viewmodel.AthkarAfterSalatViewModel
import com.athkarix.app.viewmodel.AthkarBeforeBedViewModel
import com.athkarix.app.viewmodel.AthkarMassaViewModel
import com.athkarix.app.viewmodel.AthkarSabahViewModel
import com.athkarix.app.viewmodel.DuaMenQuranViewModel
import com.athkarix.app.viewmodel.DuaMenSunnahViewModel
import com.athkarix.app.viewmodel.EstigfarViewModel
import com.athkarix.app.viewmodel.FloatingCounterViewModel
import com.athkarix.app.viewmodel.FontViewModel
import com.athkarix.app.viewmodel.HamdViewModel
import com.athkarix.app.viewmodel.NotificationSettingsViewModel
import com.athkarix.app.viewmodel.SalatAlaRasoulViewModel
import com.athkarix.app.viewmodel.TasbihViewModel

/** Manual dependency injection — singleton factory for all ViewModels and shared services.
 *  A real DI framework (Hilt/Koin) could replace this, but a plain object keeps the
 *  dependency graph visible and easy to trace for a junior dev. */
object AppModule {

    private var sharedPrefsManager: SharedPrefsManager? = null
    private var fontViewModel: FontViewModel? = null
    private var floatingCounterViewModel: FloatingCounterViewModel? = null

    // — Shared singleton services (created once, reused) —
    fun provideSharedPrefsManager(context: Context): SharedPrefsManager {
        if (sharedPrefsManager == null) {
            sharedPrefsManager = SharedPrefsManager(context)
        }
        return sharedPrefsManager!!
    }

    fun provideFontViewModel(): FontViewModel {
        if (fontViewModel == null) {
            fontViewModel = FontViewModel()
        }
        return fontViewModel!!
    }

    fun provideFloatingCounterViewModel(): FloatingCounterViewModel {
        if (floatingCounterViewModel == null) {
            floatingCounterViewModel = FloatingCounterViewModel()
        }
        return floatingCounterViewModel!!
    }

    // — ViewModel factories (fresh instance each call, no shared state) —
    fun provideHomeViewModel(): HomeViewModel = HomeViewModel()
    fun provideAthkarSabahViewModel(): AthkarSabahViewModel = AthkarSabahViewModel()
    fun provideAthkarMassaViewModel(): AthkarMassaViewModel = AthkarMassaViewModel()
    fun provideAthkarAfterSalatViewModel(): AthkarAfterSalatViewModel = AthkarAfterSalatViewModel()
    fun provideAthkarBeforeBedViewModel(): AthkarBeforeBedViewModel = AthkarBeforeBedViewModel()
    fun provideTasbihViewModel(): TasbihViewModel = TasbihViewModel()
    fun provideEstigfarViewModel(): EstigfarViewModel = EstigfarViewModel()
    fun provideHamdViewModel(): HamdViewModel = HamdViewModel()
    fun provideSalatAlaRasoulViewModel(): SalatAlaRasoulViewModel = SalatAlaRasoulViewModel()
    fun provideDuaMenQuranViewModel(): DuaMenQuranViewModel = DuaMenQuranViewModel()
    fun provideDuaMenSunnahViewModel(): DuaMenSunnahViewModel = DuaMenSunnahViewModel()
    fun provideAssmaHussnaViewModel(): AssmaHussnaViewModel = AssmaHussnaViewModel()
    fun provideNotificationService(context: Context): NotificationService {
        val service = NotificationService(context)
        service.initialize()
        return service
    }

    fun provideNotificationSettingsViewModel(context: Context): NotificationSettingsViewModel =
        NotificationSettingsViewModel(
            provideSharedPrefsManager(context),
            provideNotificationService(context),
        )
}
