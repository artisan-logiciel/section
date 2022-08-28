package backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MonolithWebfluxApplication

fun main(args: Array<String>) {
    runApplication<MonolithWebfluxApplication>(*args)
}
