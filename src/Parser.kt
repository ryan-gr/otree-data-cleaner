package ryan.yu

import java.io.File

fun main() {
    Parser().parse()
}

class Parser() {

    // Parse each line of the raw chat data.
    private fun parseChat()
            = File(chatFilePath).bufferedReader().readLines().map { ChatData(it) }

    fun parse(): String {
        val lines = File(dataFilePath).bufferedReader().readLines()

        // Process data lines into Participants.
        val dataLines = lines.map { LineData(it) }.sortedBy { it.index }
        val participants = dataLines.distinctBy { it.participantId }.map { Participant(it) }

        dataLines.forEach { line -> participants.first { it.id == line.participantId }.process(line) }

        val sessions = participants.map{ it.sessionId }.distinct().map { Session(it) }
        val exports = ArrayList<Exporter>()
        val chats = parseChat()

        // Split the participants into their relevant sessions, and add the parsed chat data.
        sessions.forEachIndexed { i, session ->
            participants.forEach { session.process(it) }
            chats.filter { session.contains(it.participantId) }.forEach { session.add(it) }
            exports += session.exportGroupFormat()
            exports.last().insert("Session $i")
        }

        // Return the resulting information as a string in a CSV format.
        with (Exporter.combine(exports)) {
            print()
            return getAsString(File("./test.csv"))
        }

    }

}