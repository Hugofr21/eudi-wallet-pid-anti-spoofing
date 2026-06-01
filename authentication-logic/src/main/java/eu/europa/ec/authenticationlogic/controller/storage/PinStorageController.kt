/*
 * Copyright (c) 2023 European Commission
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European
 * Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work
 * except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 */

package eu.europa.ec.authenticationlogic.controller.storage

import eu.europa.ec.authenticationlogic.config.StorageConfig

interface PinStorageController {
    fun setPin(pin: String)
    fun isPinValid(pin: String): Boolean
    fun hasPin(): Boolean
    fun clearPin()

    fun getFailedAttempts(): Int
    fun recordFailedAttempt(): Int
    fun resetFailedAttempts()
    fun getLockoutUntil(): Long
    fun setLockoutUntil(timestampMs: Long)
}

class PinStorageControllerImpl(private val storageConfig: StorageConfig) : PinStorageController {
    override fun hasPin(): Boolean = storageConfig.pinStorageProvider.hasPin()
    override fun clearPin() = storageConfig.pinStorageProvider.clearPin()
    override fun getFailedAttempts(): Int = storageConfig.pinStorageProvider.getFailedAttempts()
    override fun setPin(pin: String) = storageConfig.pinStorageProvider.setPin(pin)
    override fun isPinValid(pin: String): Boolean = storageConfig.pinStorageProvider.isPinValid(pin)
    override fun recordFailedAttempt(): Int = storageConfig.pinStorageProvider.recordFailedAttempt()
    override fun resetFailedAttempts() = storageConfig.pinStorageProvider.resetFailedAttempts()
    override fun getLockoutUntil(): Long = storageConfig.pinStorageProvider.getLockoutUntil()
    override fun setLockoutUntil(timestampMs: Long) = storageConfig.pinStorageProvider.setLockoutUntil(timestampMs)



}