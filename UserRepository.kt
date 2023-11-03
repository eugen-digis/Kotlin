interface UserRepository {

    fun getCurrentUser(): Flow<Resource<UserModel>>

    fun setPrivacyPolicyAgreement(
        isPrivacyPolicyAgreed: Boolean,
        isEmailPromotionAgreed: Boolean,
    ): Flow<Resource<String>>

    fun setQuestionnaire(questionnaire: QuestionnaireModel): Flow<Resource<Unit>>

    fun validateAuth(email: String, password: String): Flow<Resource<Unit>>

    fun changePassword(oldPassword: String, newPassword: String): Flow<Resource<Unit>>

    fun deleteUser(): Flow<Resource<Unit>>

    fun logOut(): Flow<Resource<Unit>>
}

class UserRepositoryImpl(
    private val userRemoteDataSource: UserRemoteDataSource,
) : UserRepository {

    override fun getCurrentUser(): Flow<Resource<UserModel>> = flow {
        emitResourceCatching {
            userRemoteDataSource.getCurrentUser() ?: throw Exception()
        }
    }

    override fun setPrivacyPolicyAgreement(
        isPrivacyPolicyAgreed: Boolean,
        isEmailPromotionAgreed: Boolean
    ): Flow<Resource<String>> = flow {
        emitResourceCatching {
            userRemoteDataSource.setPrivacyPolicyAgreement(
                isPrivacyPolicyAgreed,
                isEmailPromotionAgreed
            )
        }
    }

    override fun setQuestionnaire(
        questionnaire: QuestionnaireModel
    ): Flow<Resource<Unit>> = flow {
        emitResourceCatching {
            /*
            Other Questionnaire setting functionality
            */
            userRemoteDataSource.setQuestionnaire(questionnaire = questionnaire)
        }
    }

    override fun validateAuth(
        email: String,
        password: String
    ): Flow<Resource<Unit>> = flow {
        emitResourceCatching {
            userRemoteDataSource.validateAuth(email, password)
        }
    }

    override fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Flow<Resource<Unit>> = flow {
        emitResourceCatching {
            userRemoteDataSource.changePassword(oldPassword, newPassword)
        }
    }

    override fun deleteUser(): Flow<Resource<Unit>> = flow {
        emitResourceCatching {
            /*
            Other delete functionality
             */
            userRemoteDataSource.deleteUser()
        }
    }

    override fun logOut(): Flow<Resource<Unit>> = flow {
        emitResourceCatching {
            userRemoteDataSource.logOut()
        }
    }
}