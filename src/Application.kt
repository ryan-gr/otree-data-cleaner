package ryan.yu

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.content.*
import io.ktor.http.content.*
import kotlinx.coroutines.delay
import java.io.File
import java.lang.ProcessBuilder.Redirect
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

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

        get("/groupformat") {
            call.respondText(Parser().parse(), contentType = ContentType.Text.Plain)
        }

        get("/refresh") {
            println("Performing ${takeCommand}")
            Runtime.getRuntime().exec(takeCommand)
            call.respondText("Ok", contentType = ContentType.Text.Plain)

        }

        get("/reset") {
            println("Performing ${resetCommand}")
            Runtime.getRuntime().exec(resetCommand)
            call.respondText("Ok", contentType = ContentType.Text.Plain)
        }

        get("/refreshgroupformat") {
            Runtime.getRuntime().exec(takeCommand)
            delay(1000)
            call.respondText(Parser().parse(), contentType = ContentType.Text.Plain)
        }

        // Static feature. Try to access `/static/ktor_logo.svg`
        static("/static") {
            resources("static")
        }
    }
}

