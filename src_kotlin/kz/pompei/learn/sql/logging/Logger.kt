package kz.pompei.learn.sql.logging

class Logger {

  private val nativeLogger: org.slf4j.Logger

  private constructor(loggerName: String) {
    this.nativeLogger = org.slf4j.LoggerFactory.getLogger(loggerName)
  }

  private constructor(aClass: Class<*>) {
    this.nativeLogger = org.slf4j.LoggerFactory.getLogger(aClass)
  }

  companion object {
    fun createLogger(aClass: Class<*>): Logger {
      return Logger(aClass)
    }

    @Suppress("unused")
    fun createLogger(loggerName: String): Logger {
      return Logger(loggerName)
    }
  }


  fun infoDelay(delayNanos: Long, message: () -> String?) {
    if (nativeLogger.isInfoEnabled) {
      nativeLogger.info("${makeDelayPart(delayNanos)} ${message() ?: "<NULL MESSAGE>"}")
    }
  }

  private fun makeDelayPart(delayNanos: Long): String = "[delay:${formatDelay(delayNanos)}]"

  fun errorDelay(delayNanos: Long, message: String?, e: Throwable?) {
    if (!nativeLogger.isErrorEnabled) return
    val messageToGo = "${makeDelayPart(delayNanos)} ${message ?: "<NULL MESSAGE>"}"
    if (e == null) {
      nativeLogger.error(messageToGo)
    } else {
      nativeLogger.error(messageToGo, e)
    }
  }

  fun errorDelay(delayNanos: Long, e: Throwable) {
    if (nativeLogger.isErrorEnabled) {
      nativeLogger.error("${makeDelayPart(delayNanos)} ${e.message}")
    }
  }

  fun info(message: () -> String) {
    if (!nativeLogger.isInfoEnabled) return
    nativeLogger.info(message())
  }

  fun error(error: Throwable) {
    if (nativeLogger.isErrorEnabled) {
      nativeLogger.error(error.message, error)
    }
  }
}