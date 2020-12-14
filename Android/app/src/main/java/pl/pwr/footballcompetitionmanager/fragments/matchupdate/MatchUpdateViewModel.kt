package pl.pwr.footballcompetitionmanager.fragments.matchupdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Match
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent
import org.threeten.bp.LocalDateTime
import java.util.*

class MatchUpdateViewModel(
    private val repository: IRepository,
    private val matchId: Int
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(true)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _match = MutableLiveData<Match>()
    val match: LiveData<Match>
        get() = _match

    private val _chosenDateTime = MutableLiveData<LocalDateTime>(null)
    val chosenDateTime: LiveData<LocalDateTime>
        get() = _chosenDateTime

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage

    private val _updated = SingleLiveEvent<Int>()
    fun getUpdated(): SingleLiveEvent<Int> = _updated

    init {
        viewModelScope.launch {
            try {
                _match.value = repository.getMatch(matchId)
                joinAll()
                _chosenDateTime.value = match.value!!.time
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateMatch(latitude: Double?, longitude: Double?, length: Int, playersPerTeam: Int) {
        if (chosenDateTime.value == null)
            _snackbarMessage.value = R.string.fragment_match_create_no_date_time_set_message
        else if (!isCorrectTime(chosenDateTime.value!!))
            _snackbarMessage.value = R.string.fragment_match_create_incorrect_date_time_message
        else {
            viewModelScope.launch {
                with (_match.value!!) {
                    this.time = chosenDateTime.value!!
                    this.latitude = latitude
                    this.longitude = longitude
                    this.length = length
                    this.playersPerTeam = playersPerTeam
                }

                try {
                    val updatedMatch = repository.updateMatch(match.value!!)
                    joinAll()
                    _updated.value = updatedMatch.matchId!!
                } catch (exception: Exception) {
                    _snackbarMessage.value = R.string.server_exception_message
                }
            }
        }
    }

    fun setDateTime(dateTimeInMillis: Long, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateTimeInMillis
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        _chosenDateTime.value = LocalDateTime.of(year, month, dayOfMonth, hour, minute)
    }

    private fun isCorrectTime(set: LocalDateTime) = set.isAfter(LocalDateTime.now().plusDays(1))
}