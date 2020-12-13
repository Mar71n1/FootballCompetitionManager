package pl.pwr.footballcompetitionmanager.fragments.matchcreate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Competition
import pl.pwr.footballcompetitionmanager.model.Match
import pl.pwr.footballcompetitionmanager.model.Team
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent
import timber.log.Timber
import org.threeten.bp.LocalDateTime
import java.util.*

class MatchCreateViewModel(
    private val repository: IRepository,
    homeTeamId: Int,
    awayTeamId: Int,
    competitionId: Int
) : ViewModel() {

    private val _homeTeam = MutableLiveData<Team>()
    val homeTeam: LiveData<Team>
        get() = _homeTeam

    private val _awayTeam = MutableLiveData<Team>()
    val awayTeam: LiveData<Team>
        get() = _awayTeam

    private val _competition = MutableLiveData<Competition>(null)
    val competition: LiveData<Competition>
        get() = _competition

    private val _chosenDateTime = MutableLiveData<LocalDateTime>(null)
    val chosenDateTime: LiveData<LocalDateTime>
        get() = _chosenDateTime

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage

    private val _newMatchId = SingleLiveEvent<Int>()
    fun getNewMatchId(): SingleLiveEvent<Int> = _newMatchId

    init {
        viewModelScope.launch {
            try {
                _homeTeam.value = repository.getTeam(homeTeamId)
                _awayTeam.value = repository.getTeam(awayTeamId)
                if (competitionId != -1)
                    _competition.value = repository.getCompetition(competitionId)
                joinAll()
            } catch (exception: Exception) {
                _snackbarMessage.value = R.string.server_exception_message
            }
        }
    }

    fun createMatch(latitude: Double?, longitude: Double?, length: Int, playersPerTeam: Int) {
        if (chosenDateTime.value == null)
            _snackbarMessage.value = R.string.fragment_match_create_no_date_time_set_message
        else if (!isCorrectTime(chosenDateTime.value!!))
            _snackbarMessage.value = R.string.fragment_match_create_incorrect_date_time_message
        else {
            viewModelScope.launch {
                try {
                    if (competition.value != null)
                        _newMatchId.value = repository.createMatch(Match(competition.value!!.competitionId!!, homeTeam.value!!.teamId!!, awayTeam.value!!.teamId!!, chosenDateTime.value!!, latitude, longitude, length, playersPerTeam)).matchId!!
                    else
                        _newMatchId.value = repository.createMatch(Match(null, homeTeam.value!!.teamId!!, awayTeam.value!!.teamId!!, chosenDateTime.value!!, latitude, longitude, length, playersPerTeam)).matchId!!
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

    private fun isCorrectTime(set: LocalDateTime): Boolean {
        val minCorrectDateTime = LocalDateTime.now().plusDays(1)

        return set.isAfter(minCorrectDateTime)

//        Timber.d("set: $set, min: $minCorrectDateTime")
//        Timber.d("set year: ${set.year}, min: ${minCorrectDateTime.year}")
//        Timber.d("set: ${set.monthValue}, min: ${minCorrectDateTime.monthValue}")
//        Timber.d("set: ${set.dayOfMonth}, min: ${minCorrectDateTime.dayOfMonth}")
//        Timber.d("set: ${set.hour}, min: ${minCorrectDateTime.hour}")
//        Timber.d("set: ${set.minute}, min: ${minCorrectDateTime.minute}")

//        if (set.year < minCorrectDateTime.year)
//            Timber.d("Zły rok")
//        if (set.monthValue < minCorrectDateTime.monthValue)
//            Timber.d("Zły miesiąc")
//        if (set.dayOfMonth < minCorrectDateTime.dayOfMonth)
//            Timber.d("Zły dzień")
//        if (set.hour < minCorrectDateTime.hour)
//            Timber.d("Zła godzina")
//        if (set.minute < minCorrectDateTime.minute)
//            Timber.d("Zła minuta")
//
//        return !(set.year < minCorrectDateTime.year
//                || set.monthValue < minCorrectDateTime.monthValue
//                || set.dayOfMonth < minCorrectDateTime.dayOfMonth
//                || set.hour < minCorrectDateTime.hour
//                || set.minute < minCorrectDateTime.minute)
    }
}