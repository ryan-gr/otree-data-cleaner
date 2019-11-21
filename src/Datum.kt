package ryan.yu

class LineData(line: String) {

    private val split = line.split(',')
    val index = split[0].toInt()
    val participantId = split[3]
    val sessionId = split[4]
    val roundId = split[5]
    val didBuildLevee = when (split[9]) {
        "t" -> "1"
        "f" -> "0"
        else -> ""
    }
    val didFlood = when (split[10]) {
        "t" -> "1"
        "f" -> "0"
        else -> ""
    }
    val precipitation = split[13]
    val entryTime = split[15]
    val exitTime = split[16]
    val groupId: String = split[18]

    init { assert(split.size == 19) }

}

class ChatData(line: String) {

    private val split = line.split(',')
    val index = split[0].toInt()
    val participantId = split[2]
    val body = split[4]
    val timestamp = split[5]

    init { assert(split.size == 6) }

}