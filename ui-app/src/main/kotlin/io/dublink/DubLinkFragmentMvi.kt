package io.dublink

import com.ww.roxie.BaseAction
import com.ww.roxie.BaseState
import io.dublink.domain.internet.InternetStatus

sealed class Action : BaseAction {
    object SubscribeToInternetStatusChanges : Action()
}

sealed class Change {
    data class InternetStatusChange(val internetStatusChange: InternetStatus) : Change()
}

data class State(
    val internetStatusChange: InternetStatus? = null
) : BaseState
