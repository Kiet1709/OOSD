package com.example.foodelivery.domain.usecase.driver

import com.example.foodelivery.domain.repository.IAuthRepository
import com.example.foodelivery.domain.repository.IDriverRepository
import javax.inject.Inject

class UpdateLocationUseCase @Inject constructor(
    private val driverRepository: IDriverRepository,
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double, bearing: Float) {
        val driverId = authRepository.getCurrentUser()?.id ?: return
        driverRepository.updateMyLocation(driverId, lat, lng, bearing)
    }
}