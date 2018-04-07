package kz.pompei.learn.sql.logging

import org.fest.assertions.api.Assertions.assertThat
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class LoggerUtilKtTest {

  @DataProvider
  fun testFormatDelay_DP(): Array<Array<*>> {
    return arrayOf(
      arrayOf(4325_000_000_000, "4325sec"),
      arrayOf(1_340_000_000, "1.34sec"),
      arrayOf(1_340_000, "0.00134sec"),
      arrayOf(1_340, "0.00000134sec")
    )
  }

  @Test(dataProvider = "testFormatDelay_DP")
  fun testFormatDelay(delay: Long, expectedStr: String) {
    val actualStr = formatDelay(delay)
    assertThat(actualStr).isEqualTo(expectedStr)
  }
}