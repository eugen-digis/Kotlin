suspend fun CollectionReference.getWithConnectivitySource(context: Context): QuerySnapshot {
    return this
        .get(if (context.isInternetSlow()) Source.CACHE else Source.DEFAULT).await()
}

suspend fun Query.getWithConnectivitySource(context: Context): QuerySnapshot {
    return this
        .get(if (context.isInternetSlow()) Source.CACHE else Source.DEFAULT).await()
}

suspend fun DocumentReference.getWithConnectivitySource(context: Context): DocumentSnapshot {
    return this
        .get(if (context.isInternetSlow()) Source.CACHE else Source.DEFAULT).await()
}


suspend fun <T> Task<T>.awaitWithConnectivity(
    context: Context,
): T? {
    return if (context.isInternetSlow()) {
        this.addOnSuccessListener { }
        null
    } else {
        this@awaitWithConnectivity.await()
    }
}