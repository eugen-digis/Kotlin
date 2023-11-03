data class UserModel(
    val id: String,
    val email: String,
    val isSocialLogin: Boolean,
    val isEmailVerified: Boolean,
    val isQuestionnaireDone: Boolean,
    val isPrivacyPolicyAgreed: Boolean,
    val isEmailPromotionAgreed: Boolean,
    val questionnaire: QuestionnaireModel,
)

fun UserModel.toDTO(): UserDTO =
    UserDTO(
        email = email,
        isSocialLogin = isSocialLogin,
        isEmailVerified = isEmailVerified,
        isQuestionnaireDone = isQuestionnaireDone,
        isPrivacyPolicyAgreed = isPrivacyPolicyAgreed,
        isEmailPromotionAgreed = isEmailPromotionAgreed,
        questionnaire = questionnaire.toDTO(),
    )