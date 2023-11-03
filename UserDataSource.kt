interface UserRemoteDataSource {

    suspend fun getAuthenticatedUser(): String

    suspend fun register(email: String, password: String): String

    suspend fun login(email: String, password: String): String

    suspend fun loginWithGoogle(idToken: String): String

    suspend fun checkIfEmailVerified(): String

    suspend fun sendEmailVerification()

    suspend fun sendResetPasswordEmail(email: String)

    suspend fun changePassword(oldPassword: String, newPassword: String)

    suspend fun validateAuth(email: String, password: String)

    suspend fun getCurrentUser(): UserModel?

    suspend fun addUser(user: UserModel)

    suspend fun updateEmail(email: String): String

    suspend fun setEmailVerified()

    suspend fun setPrivacyPolicyAgreement(
        isPrivacyPolicyAgreed: Boolean,
        isEmailPromotionAgreed: Boolean,
    ): String

    suspend fun setQuestionnaire(questionnaire: QuestionnaireModel)

    suspend fun deleteUser()

    suspend fun logOut()
}

class UserRemoteDataSourceImpl(
    private val application: Application,
    private val auth: FirebaseAuth,
    private val collection: CollectionReference
) : UserRemoteDataSource {

    override suspend fun getAuthenticatedUser(): String {
        return withContext(Dispatchers.IO) {
            auth.currentUser?.uid ?: throw DomainException(ErrorType.SomethingWentWrong)
        }
    }

    override suspend fun register(email: String, password: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth
                    .createUserWithEmailAndPassword(email, password)
                    .awaitWithConnectivity(application)
                result?.user?.let {
                    addNewUser(
                        id = it.uid,
                        email = it.email ?: "",
                        isSocialLogin = false
                    )
                    it.uid
                } ?: throw DomainException(ErrorType.SomethingWentWrong)
            } catch (e: Exception) {
                e.printStackTrace()
                when ((e as? FirebaseAuthException)?.errorCode) {
                    "ERROR_EMAIL_ALREADY_IN_USE" -> throw DomainException(ErrorType.EmailAlreadyInUse)
                    else -> {
                        if (e is DomainException) throw e
                        else throw DomainException(ErrorType.SomethingWentWrong)
                    }
                }
            }
        }
    }

    override suspend fun login(email: String, password: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth
                    .signInWithEmailAndPassword(email, password)
                    .awaitWithConnectivity(application)
                result?.user?.uid ?: throw DomainException(ErrorType.SomethingWentWrong)
            } catch (e: Exception) {
                e.printStackTrace()
                when ((e as? FirebaseAuthException)?.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> throw DomainException(ErrorType.InvalidEmail)
                    "ERROR_WRONG_PASSWORD" -> throw DomainException(ErrorType.InvalidPassword)
                    else -> {
                        if (e is DomainException) throw e
                        else throw DomainException(ErrorType.SomethingWentWrong)
                    }
                }
            }
        }
    }

    override suspend fun loginWithGoogle(idToken: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val result = auth
                    .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
                    .awaitWithConnectivity(application)
                val isNewUser = result?.additionalUserInfo?.isNewUser ?: false
                if (isNewUser) {
                    auth.currentUser?.let {
                        addNewUser(
                            id = it.uid,
                            email = it.email ?: "",
                            isSocialLogin = true
                        )
                        it.uid
                    } ?: throw DomainException(ErrorType.SomethingWentWrong)
                } else {
                    auth.currentUser?.uid ?: throw DomainException(ErrorType.SomethingWentWrong)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is DomainException) throw e
                else throw DomainException(ErrorType.SomethingWentWrong)
            }
        }
    }

    override suspend fun checkIfEmailVerified(): String {
        return withContext(Dispatchers.IO) {
            try {
                auth.currentUser?.reload()?.awaitWithConnectivity(application)
                auth.currentUser?.let {
                    if (it.isEmailVerified) {
                        setEmailVerified()
                        it.uid
                    } else {
                        throw DomainException(ErrorType.EmailNotVerified)
                    }
                } ?: throw DomainException(ErrorType.SomethingWentWrong)
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is DomainException) throw e
                else throw DomainException(ErrorType.SomethingWentWrong)
            }
        }
    }

    override suspend fun sendEmailVerification() {
        withContext(Dispatchers.IO) {
            try {
                auth.currentUser?.let {
                    it.sendEmailVerification().awaitWithConnectivity(application)
                    Unit
                } ?: throw DomainException(ErrorType.SomethingWentWrong)
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is DomainException) throw e
                else throw DomainException(ErrorType.SomethingWentWrong)
            }
        }
    }

    override suspend fun sendResetPasswordEmail(email: String) {
        withContext(Dispatchers.IO) {
            try {
                auth
                    .sendPasswordResetEmail(email)
                    .awaitWithConnectivity(application)
            } catch (e: Exception) {
                e.printStackTrace()
                when ((e as? FirebaseAuthException)?.errorCode) {
                    "ERROR_USER_NOT_FOUND" -> throw DomainException(ErrorType.InvalidEmail)
                    else -> {
                        if (e is DomainException) throw e
                        else throw DomainException(ErrorType.SomethingWentWrong)
                    }
                }
            }
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String) {
        withContext(Dispatchers.IO) {
            try {
                auth.currentUser?.let {
                    val email = getCurrentUser()?.email
                        ?: throw DomainException(ErrorType.SomethingWentWrong)
                    it
                        .reauthenticate(EmailAuthProvider.getCredential(email, oldPassword))
                        .awaitWithConnectivity(application)
                    it
                        .updatePassword(newPassword)
                        .awaitWithConnectivity(application)
                    Unit
                } ?: throw DomainException(ErrorType.SomethingWentWrong)
            } catch (e: Exception) {
                e.printStackTrace()
                when ((e as? FirebaseAuthException)?.errorCode) {
                    "ERROR_WRONG_PASSWORD" -> throw DomainException(ErrorType.InvalidPassword)
                    else -> {
                        if (e is DomainException) throw e
                        else throw DomainException(ErrorType.SomethingWentWrong)
                    }
                }
            }
        }
    }

    override suspend fun validateAuth(email: String, password: String) {
        withContext(Dispatchers.IO) {
            auth.currentUser?.let {
                it
                    .reauthenticate(EmailAuthProvider.getCredential(email, password))
                    .awaitWithConnectivity(application)
                Unit
            } ?: throw DomainException(ErrorType.SomethingWentWrong)
        }
    }

    override suspend fun getCurrentUser(): UserModel? {
        return withContext(Dispatchers.IO) {
            val userId =
                auth.currentUser?.uid ?: throw DomainException(ErrorType.SomethingWentWrong)
            collection
                .document(userId)
                .getWithConnectivitySource(application)
                .let { document ->
                    document.toObject(UserDTO::class.java)?.toModel(document.id)
                }
        }
    }

    override suspend fun addUser(user: UserModel) {
        withContext(Dispatchers.IO) {
            val dto = user.toDTO()
            collection
                .document(user.id)
                .set(dto)
                .awaitWithConnectivity(application)
        }
    }

    override suspend fun updateEmail(email: String): String {
        return withContext(Dispatchers.IO) {
            try {
                auth.currentUser?.let {
                    it.updateEmail(email).awaitWithConnectivity(application)
                    collection
                        .document(it.uid)
                        .update(
                            mapOf(
                                "email" to email
                            )
                        )
                        .awaitWithConnectivity(application)
                    email
                } ?: throw DomainException(ErrorType.SomethingWentWrong)
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is DomainException) throw e
                else throw DomainException(ErrorType.SomethingWentWrong)
            }
        }
    }

    override suspend fun setEmailVerified() {
        withContext(Dispatchers.IO) {
            val userId =
                auth.currentUser?.uid ?: throw DomainException(ErrorType.SomethingWentWrong)
            collection
                .document(userId)
                .update(
                    mapOf(
                        "emailVerified" to true
                    )
                )
                .awaitWithConnectivity(application)
        }
    }

    override suspend fun setPrivacyPolicyAgreement(
        isPrivacyPolicyAgreed: Boolean,
        isEmailPromotionAgreed: Boolean
    ): String {
        return withContext(Dispatchers.IO) {
            val userId =
                auth.currentUser?.uid ?: throw DomainException(ErrorType.SomethingWentWrong)
            collection
                .document(userId)
                .update(
                    mapOf(
                        "privacyPolicyAgreed" to isPrivacyPolicyAgreed,
                        "emailPromotionAgreed" to isEmailPromotionAgreed
                    )
                )
                .awaitWithConnectivity(application)
            userId
        }
    }

    override suspend fun setQuestionnaire(questionnaire: QuestionnaireModel) {
        withContext(Dispatchers.IO) {
            val userId =
                auth.currentUser?.uid ?: throw DomainException(ErrorType.SomethingWentWrong)
            collection
                .document(userId)
                .update(
                    mapOf(
                        "questionnaire" to questionnaire.toDTO(),
                        "questionnaireDone" to true
                    )
                )
                .awaitWithConnectivity(application)
        }
    }

    override suspend fun deleteUser() {
        withContext(Dispatchers.IO) {
            val userId =
                auth.currentUser?.uid ?: throw DomainException(ErrorType.SomethingWentWrong)
            collection
                .document(userId)
                .delete()
                .await()
            auth.currentUser?.delete()?.awaitWithConnectivity(application)
        }
    }

    override suspend fun logOut() {
        withContext(Dispatchers.IO) {
            auth.signOut()
        }
    }

    private suspend fun addNewUser(
        id: String,
        email: String,
        isSocialLogin: Boolean
    ) {
        addUser(
            UserModel(
                id = id,
                email = email,
                isSocialLogin = isSocialLogin,
                isEmailVerified = false,
                isQuestionnaireDone = false,
                isPrivacyPolicyAgreed = false,
                isEmailPromotionAgreed = false,
                questionnaire = QuestionnaireModel(),
            )
        )
    }
}