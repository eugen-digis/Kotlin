val UserProfileModule = module {
    factory<UserRemoteDataSource> { UserRemoteDataSourceImpl(get(), get()) }
    factory { UserProfileRepo(get(), get()) }
    viewModel { UserProfileViewModel(get()) }
}