val DomainModule = module {
    factory<AuthRepository> { AuthRepositoryImpl(get()) }

    factory<UserRepository> { UserRepositoryImpl(get()) }
    
    /*
    Other Repositories instantiation methods
    */
}