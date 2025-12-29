package com.example.foodelivery.domain.usecase.customer

import com.example.foodelivery.domain.repository.IDriverRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TrackDriverUseCase @Inject constructor(
    private val repository: IDriverRepository
) {
    operator fun invoke(driverId: String): Flow<Pair<Double, Double>> {
        return repository.getDriverLocation(driverId)
    }
}