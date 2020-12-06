package pl.pwr.footballcompetitionmanager.fragments.teamcreate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.model.Team
import pl.pwr.footballcompetitionmanager.repository.IRepository
import pl.pwr.footballcompetitionmanager.utils.SingleLiveEvent

class TeamCreateViewModel(
    private val repository: IRepository
) : ViewModel() {

    private val _newTeam = MutableLiveData<Team>()
    val newTeam: LiveData<Team>
        get() = _newTeam

    private val _teamNameTextFieldError = MutableLiveData<Int>()
    val teamNameTextFieldError: LiveData<Int>
        get() = _teamNameTextFieldError

    private val _descriptionTextFieldError = MutableLiveData<Int>()
    val descriptionTextFieldError: LiveData<Int>
        get() = _descriptionTextFieldError

    private val _snackbarMessage = SingleLiveEvent<Int>()
    fun getSnackbarMessage(): SingleLiveEvent<Int> = _snackbarMessage

    fun createNewTeam(newTeamName: String, description: String) {
        if (newTeamName.length < Constants.MIN_TEAM_NAME_LENGTH || Constants.MAX_TEAM_NAME_LENGTH < newTeamName.length
            || Constants.MAX_TEAM_DESCRIPTION_LENGTH < description.length) {

            if (newTeamName.length < Constants.MIN_TEAM_NAME_LENGTH || Constants.MAX_TEAM_NAME_LENGTH < newTeamName.length)
                _teamNameTextFieldError.value = R.string.fragment_team_create_name_length_invalid_error

            if (Constants.MAX_TEAM_DESCRIPTION_LENGTH < description.length)
                _descriptionTextFieldError.value = R.string.fragment_team_create_description_length_invalid_error

        } else {
            viewModelScope.launch {
                try {
                    _newTeam.value = repository.createTeam(Team(newTeamName, repository.getCurrentUser().userId, description))
                } catch (exception: IllegalArgumentException) {
                    _snackbarMessage.value = R.string.fragment_team_create_duplicate_name_error
                } catch (exception: Exception) {
                    _snackbarMessage.value = R.string.server_exception_message
                }
            }
        }
    }

    fun newTeamCreated() {
        _newTeam.value = null
    }
}