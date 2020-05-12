package io.dublink.logging

import com.nodesagency.logviewer.CommonSeverityLevels
import com.nodesagency.logviewer.Logger
import com.nodesagency.logviewer.data.model.Severity
import timber.log.Timber

object DebugLogger : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
        try {
            Logger.log(
                message = message,
                severityLevel = mapPriority(priority).level,
                tag = tag,
                categoryName = mapPriority(priority).level,
                throwable = t
            )
        } catch (e: Exception) {
            // ignored
        }
    }

    private fun mapPriority(priority: Int): Severity {
        return when (priority) {
            2 -> CommonSeverityLevels.VERBOSE.severity
            3 -> CommonSeverityLevels.DEBUG.severity
            4 -> CommonSeverityLevels.INFO.severity
            5 -> CommonSeverityLevels.WARNING.severity
            6 -> CommonSeverityLevels.ERROR.severity
            7 -> CommonSeverityLevels.ASSERT.severity
            else -> CommonSeverityLevels.WTF.severity
        }
    }
}
