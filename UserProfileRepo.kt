class UserProfileRepo(private val local: UserProfileLocalDataSource, private val remote: UserProfileRemoteDataSource) {

    fun getUser(): LiveData<User?> = local.getUser()

    suspend fun clearUserData() = local.clearUserData()

    suspend fun updateUserData(user: UserUpdateData) = local.updateUser(remote.updateUserData(user))

    suspend fun updateUserLocal(user: User) = local.updateUser(user)
}