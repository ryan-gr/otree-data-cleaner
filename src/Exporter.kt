package ryan.yu

import java.io.File

class Exporter {

    companion object {
        fun combine(exporters: List<Exporter>)= Exporter().apply { exporters.forEach(::extend) }
    }

    private val lines = ArrayList<ArrayList<String>>().apply { add(ArrayList()) }

    fun insert(string: String, index: Int = 0) { if (index < lines.size) lines.add(index, arrayListOf(string)) }
    fun add(string: String) { lines.last().add(string) }
    fun add(strings: List<String>) { strings.forEach { lines.last().add(it) } }
    fun endLine() { lines.add(ArrayList()) }
    fun extend(exporter: Exporter) { exporter.lines.forEach { lines.add(it) } }

    fun print() = lines.forEach { println(it.joinToString(",")) }
    fun getAsString(file: File) = lines.joinToString("\n") { it.joinToString(",") { s -> "\"${s}\"" } }
    fun export(file: File) = file.writeText(lines.joinToString("\n") { it.joinToString(",") { s -> "\"${s}\"" } })

}