val DatabaseModule = module {
    single {
        FirebaseFirestore.getInstance().apply {
            firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(
                    PersistentCacheSettings
                        .newBuilder()
                        .setSizeBytes(500 * 1024 * 1024L)
                        .build()
                )
                .build()
        }
    }

    single(named(USER_COLLECTION_NAME)) {
        get<FirebaseFirestore>().collection(USER_COLLECTION_NAME)
    }
    
    /*
    Other Collections instantiation methods
    */
}