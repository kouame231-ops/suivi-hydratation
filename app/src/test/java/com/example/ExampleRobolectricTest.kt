package com.example

import com.aistudio.hydrationtracker.hqdzrt.HydrationViewModel
import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Suivi Hydratation", appName)
  }

  @Test
  fun `test water tracker add and reset flow`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = HydrationViewModel(app)

    // Initial state
    assertEquals(0, viewModel.currentIntakeMl.value)
    assertEquals(2000, viewModel.targetMl.value)
    assertEquals(0, viewModel.history.value.size)

    // Add 250ml
    viewModel.addWater(250)
    assertEquals(250, viewModel.currentIntakeMl.value)
    assertEquals(1, viewModel.history.value.size)
    assertEquals(250, viewModel.history.value[0].amountMl)

    // Add another 500ml
    viewModel.addWater(500)
    assertEquals(750, viewModel.currentIntakeMl.value)
    assertEquals(2, viewModel.history.value.size)

    // Reset flow
    viewModel.resetWater()
    assertEquals(0, viewModel.currentIntakeMl.value)
    assertEquals(0, viewModel.history.value.size)
  }
}
