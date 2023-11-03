class UserProfileViewModel(private val repo: UserProfileRepo) : BaseViewModel() {
    fun updateUser(
        firstName: String? = null,
        lastName: String? = null,
        company: String? = null,
        website: String? = null,
        phone: String? = null,
        avatar: String? = null
    ) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.IO) {
                repo.updateUserData(user.value!!.apply {
                    this.firstName = firstName
                    this.lastName = lastName
                    this.company = company
                    this.phone = phone
                    this.website = website
                    this.avatar = avatar
                })
            }
        }
    }
}