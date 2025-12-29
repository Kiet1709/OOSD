package com.example.foodelivery.data.repository

import com.example.foodelivery.domain.repository.IDriverRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose;
import kotlinx.coroutines.flow.Flow;
import  kotlinx.coroutines.flow.callbackFlow;
import kotlinx.coroutines.tasks.await;
import javax.inject.Inject

class DriverRepositoryImpl @Inject constructor(private val firestore: FirebaseFirestore) : IDriverRepository {
    override fun getDriverLocation(driverId: String): Flow<Pair<Double, Double>> = callbackFlow {
        val l = firestore.collection("driver_locations").document(driverId).addSnapshotListener { s, e ->
            if(e==null && s!=null && s.exists()) {
                trySend(Pair(s.getDouble("latitude")?:0.0, s.getDouble("longitude")?:0.0))
            }
        }
        awaitClose { l.remove() }
    }
    override suspend fun updateMyLocation(driverId: String, lat: Double, lng: Double, bearing: Float) {
        val data = hashMapOf("driverId" to driverId, "latitude" to lat, "longitude" to lng, "bearing" to bearing)
        try { firestore.collection("driver_locations").document(driverId).set(data).await() } catch(e: Exception){}
    }
}