package dev.kastro

import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Singleton
class DemoServerEndpoint : DemoServerServiceGrpcKt.DemoServerServiceCoroutineImplBase() {
    override suspend fun saveUser(request: SaveUserRequest): UserResponse {
        return UserResponse.newBuilder()
            .setId(1)
            .setName(request.name)
            .setLastName(request.lastName)
            .build()
    }

    override fun saveUserStream(requests: Flow<SaveUserRequest>): Flow<UserResponse> = flow {
        var id = 1
        requests.collect {
            println("Saving user...")
            emit(
                UserResponse.newBuilder()
                    .setId(id++)
                    .setName(it.name)
                    .setLastName(it.lastName)
                    .build()
            )
            println("User Saved")
        }
    }
}