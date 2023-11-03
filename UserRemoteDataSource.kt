interface UserRemoteDataSource {
    suspend fun getSubscription(): SubscriptionModel
    
    suspend fun updateUserData(user: UserUpdateData): User
}

class UserRemoteDataSourceImpl(
    private val api: Api,
    private val prefs: GemlightBoxPreferences,
) : RemoteDataSource {

    override suspend fun getSubscription(): SubscriptionModel {
        val responseModel = api.getSubscription()
        prefs.recordSubscription(responseModel.toPrefsModel())
        return responseModel
    }
    
    override suspend fun updateUserData(user: UserUpdateData): User {
        val userResponse = api.updateUser(
            firstName = user.firstName?.toRequestBody("text/plain".toMediaTypeOrNull()),
            lastName = user.lastName?.toRequestBody("text/plain".toMediaTypeOrNull()),
            company = user.company?.toRequestBody("text/plain".toMediaTypeOrNull()),
            website = user.website?.toRequestBody("text/plain".toMediaTypeOrNull()),
            phone = user.phone?.toRequestBody("text/plain".toMediaTypeOrNull()),
            avatar = user.avatar?.let {
                File(it).run {
                    MultipartBody.Part.createFormData(
                        "avatar",
                        "ava.jpg",
                        this.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
            }
        )
        prefs.saveUserResponse(userResponse.toPrefsModel())
        return userResponse
    }
}