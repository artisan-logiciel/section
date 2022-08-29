package backend

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object Log {
    @JvmStatic
    val log: Logger by lazy { LogManager.getLogger(Log.javaClass) }
}