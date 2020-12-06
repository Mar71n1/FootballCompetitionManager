package pl.pwr.footballcompetitionmanager.fragments.teamupdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Team
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent

class TeamUpdateViewModel(
    private val repository: IRepository,
    teamId: Int
) : ViewModel() {

    private val _team = MutableLiveData<Team>()
    val team: LiveData<Team>
        get() = _team

    private val _teamNameTextFieldError = MutableLiveData<Int>()
    val teamNameTextFieldError: LiveData<Int>
        get() = _teamNameTextFieldError

    private val _descriptionTextFieldError = MutableLiveData<Int>()
    val descriptionTextFieldError: LiveData<Int>
        get() = _descriptionTextFieldError

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage

    private val _updated = MutableLiveData<Boolean>()
    val updated: LiveData<Boolean>
        get() = _updated

    init {
        viewModelScope.launch {
            _team.value = repository.getTeam(teamId)
        }
    }

    fun updateTeam(name: String, description: String) {
        if (name.length < Constants.MIN_TEAM_NAME_LENGTH || Constants.MAX_TEAM_NAME_LENGTH < name.length
            || Constants.MAX_TEAM_DESCRIPTION_LENGTH < description.length) {

            if (name.length < Constants.MIN_TEAM_NAME_LENGTH || Constants.MAX_TEAM_NAME_LENGTH < name.length)
                _teamNameTextFieldError.value = R.string.fragment_team_create_name_length_invalid_error

            if (Constants.MAX_TEAM_DESCRIPTION_LENGTH < description.length)
                _descriptionTextFieldError.value = R.string.fragment_team_create_description_length_invalid_error

        } else {
            viewModelScope.launch {
                _team.value!!.name = name
                _team.value!!.description = description
                try {
                    repository.updateTeam(_team.value!!)
                    joinAll()
                    _updated.value = true
                } catch (exception: IllegalArgumentException) {
                    _snackbarMessage.value = R.string.fragment_team_create_duplicate_name_error
                } catch (exception: Exception) {
                    _snackbarMessage.value = R.string.server_exception_message
                }
            }
        }
    }
}