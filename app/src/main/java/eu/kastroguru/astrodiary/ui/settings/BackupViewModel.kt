package eu.kastroguru.astrodiary.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eu.kastroguru.astrodiary.data.backup.BackupRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BackupState {
    data object Idle : BackupState
    data class Working(val importing: Boolean) : BackupState
    /** Asks before writing anything: what the picked file holds. */
    data class Confirm(val uri: Uri, val exportedAt: Long, val charts: Int, val events: Int) : BackupState
    data class Exported(val result: BackupRepository.ExportResult) : BackupState
    data class Imported(val result: BackupRepository.ImportResult) : BackupState
    data object NothingToExport : BackupState
    data class Failed(val error: Throwable) : BackupState
}

/** Export and import outlive the screen; a photo-heavy file takes a while either way. */
@HiltViewModel
class BackupViewModel @Inject constructor(
    private val repository: BackupRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<BackupState>(BackupState.Idle)
    val state: StateFlow<BackupState> = _state

    val busy get() = _state.value is BackupState.Working

    /** Checked before the file picker opens, so nobody saves an empty file by mistake. */
    suspend fun hasData() = repository.hasData()

    fun nothingToExport() { if (!busy) _state.value = BackupState.NothingToExport }

    fun export(uri: Uri) = launchWork(importing = false) {
        BackupState.Exported(repository.export(uri))
    }

    fun inspect(uri: Uri) = launchWork(importing = true) {
        val backup = repository.inspect(uri)
        BackupState.Confirm(uri, backup.exportedAt, backup.charts.size, backup.events.size)
    }

    fun confirmImport() {
        val confirm = _state.value as? BackupState.Confirm ?: return
        launchWork(importing = true) { BackupState.Imported(repository.import(confirm.uri)) }
    }

    fun cancel() {
        if (_state.value is BackupState.Confirm) _state.value = BackupState.Idle
    }

    fun clearResult() {
        if (_state.value !is BackupState.Working && _state.value !is BackupState.Confirm) {
            _state.value = BackupState.Idle
        }
    }

    private fun launchWork(importing: Boolean, block: suspend () -> BackupState) {
        if (busy) return
        _state.value = BackupState.Working(importing)
        viewModelScope.launch {
            _state.value = try {
                block()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                BackupState.Failed(e)
            }
        }
    }
}
