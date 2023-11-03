interface Api {

    @GET("api/subscription")
    suspend fun getSubscription(): SubscriptionModel

    @PUT("api/users")
    @Multipart
    suspend fun updateUser(
        @Part("firstName") firstName: RequestBody?,
        @Part("lastName") lastName: RequestBody?,
        @Part("company") company: RequestBody?,
        @Part("website") website: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part avatar: MultipartBody.Part?
    ): Response<ResponseBody>
}