package dev.kastro

import io.github.serpro69.kfaker.faker
import io.grpc.Channel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DemoService {
    private val faker = faker {  }

    suspend fun saveUser() {
        val demoServerStub = createStub()

        val saveUserRequest = SaveUserRequest.newBuilder()
            .setName("Victor")
            .setLastName("Castro")
            .setDocument("13229132412")
            .build()

        val saveUserResponse = demoServerStub.saveUser(saveUserRequest)

        println("User saved with id = " + saveUserResponse.id)
    }

    suspend fun saveUserStream(numberOfRequest:Int) {
        println("Making $numberOfRequest requests...\n")
        val demoServerStub = createStub()

        val requests = generateOutgoingRequests(numberOfRequest)

        demoServerStub.saveUserStream(requests).collect { response ->
            println("Response: " + response.id)
        }
    }

    private fun generateOutgoingRequests(numberOfRequest:Int): Flow<SaveUserRequest> = flow {
        val requests = mutableListOf<SaveUserRequest>()

        for (i in 1..numberOfRequest) {
            val request = SaveUserRequest.newBuilder()
                .setName(faker.name.firstName())
                .setLastName(faker.name.lastName())
                .setDocument((faker.random.nextInt(min = 1, max = 10) * 1202938192).toString())
                .build()
            requests.add(request)
        }

        for (request in requests) {
            println("Request: " + request.name)
            emit(request)
            delay(5000)
        }
    }

    private fun createStub(): DemoServerServiceGrpcKt.DemoServerServiceCoroutineStub {
        val channel: Channel = ManagedChannelBuilder.forAddress("localhost", 50051)
            .usePlaintext()
            .build()

        return DemoServerServiceGrpcKt.DemoServerServiceCoroutineStub(channel)
    }
}