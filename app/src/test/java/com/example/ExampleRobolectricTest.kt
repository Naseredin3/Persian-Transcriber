package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LocalToneConverter
import com.example.model.ToneType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    assertEquals("بیان گویا", appName)
  }

  @Test
  fun `test colloquial to formal tone conversion`() {
    val input = "سلام مهندس، دیروز نرسیدم بیام"
    val converted = LocalToneConverter.convertLocally(input, ToneType.FORMAL)
    assertTrue(converted.contains("با سلام") || converted.contains("احتراماً"))
    assertTrue(converted.contains("حضور یابم") || converted.contains("میسر نگردید") || converted.contains("جناب مهندس"))
  }

  @Test
  fun `test formal to colloquial tone conversion`() {
    val input = "احتراماً به استحضار می‌رساند جلسه لغو می‌گردد."
    val converted = LocalToneConverter.convertLocally(input, ToneType.COLLOQUIAL)
    assertTrue(converted.contains("کنسل") || converted.contains("سلام") || converted.contains("بگم"))
  }
}
