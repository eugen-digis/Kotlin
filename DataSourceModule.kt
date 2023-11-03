val DataSourceModule = module {
    factory<UserRemoteDataSource> {
        UserRemoteDataSourceImpl(get(), get(), get(named(USER_COLLECTION_NAME)))
    }
    
    /*
    Other DataSources instantiation methods
    */
}