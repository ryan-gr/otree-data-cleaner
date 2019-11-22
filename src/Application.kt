package ryan.yu

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondFile
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.coroutines.delay
import java.io.File
import java.lang.ProcessBuilder.Redirect
import java.util.concurrent.TimeUnit

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    fun String.runCommand(workingDir: File? = null) {
        val process = ProcessBuilder(*split(" ").toTypedArray())
            .directory(workingDir)
            .redirectOutput(Redirect.INHERIT)
            .redirectError(Redirect.INHERIT)
            .start()
        if (!process.waitFor(10, TimeUnit.SECONDS)) {
            process.destroy()
            throw RuntimeException("execution timed out: $this")
        }
        if (process.exitValue() != 0) {
            throw RuntimeException("execution failed with code ${process.exitValue()}: $this")
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/reset") {
            println("Performing $resetCommand")
            Runtime.getRuntime().exec(resetCommand)
            call.respondText("Ok", contentType = ContentType.Text.Plain)
        }

        get("/latest_groupformat") {
            Runtime.getRuntime().exec(takeCommand)
            delay(1000)
            call.respondText(Parser().parse(), contentType = ContentType.Text.Plain)
        }

        get("/latest_groupformat_csv") {
            Runtime.getRuntime().exec(takeCommand)
            delay(1000)
            Parser().parse()
            call.respondFile(File("./test.csv"))
        }

    }
}

