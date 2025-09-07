package us.leaf3stones.hy2droid.data

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object VpnServiceState {
    private val _state = MutableSharedFlow<String>(replay = 1)
    val state = _state.asSharedFlow()
    suspend fun update(state: String) = _state.emit(state)
}
