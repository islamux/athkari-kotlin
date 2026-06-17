package com.athkarix.app.viewmodel

import app.cash.turbine.test
import com.athkarix.app.ui.screens.home.HomeNavigationEvent
import com.athkarix.app.ui.screens.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `goToAthkarSabah emits athkar_sabah route`() = runTest {
        val vm = HomeViewModel()
        vm.navigationEvent.test {
            vm.goToAthkarSabah()
            assertEquals(HomeNavigationEvent.GoToRoute("athkar_sabah"), awaitItem())
        }
    }

    @Test
    fun `goToAthkarMassa emits athkar_massa route`() = runTest {
        val vm = HomeViewModel()
        vm.navigationEvent.test {
            vm.goToAthkarMassa()
            assertEquals(HomeNavigationEvent.GoToRoute("athkar_massa"), awaitItem())
        }
    }

    @Test
    fun `goToSearch emits search route`() = runTest {
        val vm = HomeViewModel()
        vm.navigationEvent.test {
            vm.goToSearch()
            assertEquals(HomeNavigationEvent.GoToRoute("search"), awaitItem())
        }
    }

    @Test
    fun `goToNotificationSettings emits notification_settings route`() = runTest {
        val vm = HomeViewModel()
        vm.navigationEvent.test {
            vm.goToNotificationSettings()
            assertEquals(HomeNavigationEvent.GoToRoute("notification_settings"), awaitItem())
        }
    }
}
