package ryan.yu

class Participant(startingData: LineData) {

    val id = startingData.participantId
    val sessionId = startingData.sessionId

    data class RoundData (
        val roundId: String,
        val groupId: String,
        val precipitation: String,
        val didBuildLevee: String,
        val didFlood: String,
        val entryTime: String,
        val exitTime: String
    )

    class RoundsData {
        val precipitations = ArrayList<String>()
        val didBuildLevees = ArrayList<String>()
        val entryTimes = ArrayList<String>()
        val exitTimes = ArrayList<String>()
        fun add(rd: RoundData): RoundsData {
            precipitations.add(rd.precipitation)
            didBuildLevees.add(rd.didBuildLevee)
            entryTimes.add(rd.entryTime)
            exitTimes.add(rd.exitTime)
            return this
        }
    }

    private val rounds = ArrayList<RoundData>()

    fun process(data: LineData) {
        assert(data.participantId == id)
        addData(data)
    }

    // Save the data into this participant's round record.
    private fun addData(it: LineData) {
        rounds += RoundData(it.roundId, it.groupId, it.precipitation, it.didBuildLevee, it.didFlood, it.entryTime, it.exitTime)
    }

    // SubGroupId refers to the first / last 10 rounds the participant was grouped in.
    // If two participant shares the same SubGroupId, it means they were in the same group for the first / last 10 rounds.
    fun firstSubGroupId() = rounds.take(10). joinToString("") { it.groupId }
    fun lastSubGroupId() = rounds.takeLast(10).joinToString("") { it.groupId }

    // Return the data for the first / last 10 rounds, in a "horizontal" format.
    fun getFirstRoundsData() = RoundsData().apply { rounds.take(10).forEach { add(it) } }
    fun getLastRoundsData() = RoundsData().apply { rounds.takeLast(10).forEach { add(it) } }

}