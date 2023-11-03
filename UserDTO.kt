data class UserDTO(
    @PropertyName("email")
    val email: String = "",

    @PropertyName("socialLogin")
    val isSocialLogin: Boolean = false,

    @PropertyName("emailVerified")
    val isEmailVerified: Boolean = false,

    @PropertyName("questionnaireDone")
    val isQuestionnaireDone: Boolean = false,

    @PropertyName("privacyPolicyAgreed")
    val isPrivacyPolicyAgreed: Boolean = false,

    @PropertyName("emailPromotionAgreed")
    val isEmailPromotionAgreed: Boolean = false,

    @PropertyName("questionnaire")
    val questionnaire: QuestionnaireDTO? = null,
)

fun UserDTO.toModel(id: String): UserModel =
    UserModel(
        id = id,
        email = email,
        isSocialLogin = isSocialLogin,
        isEmailVerified = isEmailVerified,
        isQuestionnaireDone = isQuestionnaireDone,
        isPrivacyPolicyAgreed = isPrivacyPolicyAgreed,
        isEmailPromotionAgreed = isEmailPromotionAgreed,
        questionnaire = questionnaire?.toModel() ?: QuestionnaireModel(),
    )