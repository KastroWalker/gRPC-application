package dev.kastro

import io.micronaut.runtime.Micronaut.*
suspend fun main(args: Array<String>) {
	val demoService = DemoService()
	println("Saving a User...")
	demoService.saveUser(5)

	println("Saving multiple Users...")
	demoService.saveUserStream(5)

	build()
	    .args(*args)
		.packages("dev.kastro")
		.start()
}

