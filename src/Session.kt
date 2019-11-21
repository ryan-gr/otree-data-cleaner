package ryan.yu

class Session(private val sessionId: String) {

    private val participants = ArrayList<Participant>()
    private val chats = ArrayList<ChatData>()

    private data class Chat(val participantId: String, val body: String, val timestamp: String, val round: Int)

    fun contains(participantId: String) = participants.map{ it.id }.contains(participantId)

    fun add(chatData: ChatData) = chats.add(chatData)

    private fun participantsInSubGroup(subGroupId: String)
        = participants.filter { it.firstSubGroupId() == subGroupId || it.lastSubGroupId() == subGroupId }

    fun process(participant: Participant) {
        if (participant.sessionId == sessionId) participants.add(participant)
    }

    fun exportGroupFormat(): Exporter {

        val firstSubGroupIds = ArrayList<String>()
        val lastSubGroupIds = ArrayList<String>()

        for (p in participants) {
            if (p.firstSubGroupId() !in firstSubGroupIds) firstSubGroupIds.add(p.firstSubGroupId())
            if (p.lastSubGroupId() !in lastSubGroupIds) lastSubGroupIds.add(p.lastSubGroupId())
        }

        val exporter = Exporter()

        var groupIndex = 1
        for (pair in firstSubGroupIds zip lastSubGroupIds) {

            with (exporter) {
                add("Group $groupIndex")
                endLine()
            }

            val firsts = participantsInSubGroup(pair.first)
            val lasts = participantsInSubGroup(pair.second)

            val chatRounds = ArrayList<Chat>()

            fun String.within(s1: String, s2: String):Boolean {
                if (s1 == "" || s2 == "") return false
                return (this in s1..s2)
            }

            // Assign the chats into the correct rounds.
            for (c in chats) {
                val p = firsts.firstOrNull { it.id == c.participantId }
                p?.let {
                    val rd = p.getFirstRoundsData()
                    (rd.entryTimes zip rd.exitTimes).forEachIndexed { i, pair ->
                        if (c.timestamp.within(pair.first, pair.second)) {
                            chatRounds.add(Chat(c.participantId, c.body, c.timestamp, i))
                        }
                    }
                }
            }

            for (c in chats) {
                val p = lasts.first { it.id == c.participantId }
                val rd = p.getLastRoundsData()
                (rd.entryTimes zip rd.exitTimes).forEachIndexed { i, pair ->
                    if (c.timestamp.within(pair.first, pair.second)) {
                        chatRounds.add(Chat(c.participantId, c.body, c.timestamp, i))
                    }
                }

            }

            with (exporter) {
                add("")
                add(firsts.first().getFirstRoundsData().precipitations)
                add("")
                add(lasts.first().getLastRoundsData().precipitations)
                endLine()
            }

            // Add data on whether a participant did build a levee at a certain round.
            for (i in 0..2) {
                with (exporter) {
                    firsts[i].let { participant ->
                        add("P${participant.id}:")
                        add(participant.getFirstRoundsData().didBuildLevees)
                    }
                    lasts[i].let { participant ->
                        add("P${participant.id}:")
                        add(participant.getLastRoundsData().didBuildLevees)
                        endLine()
                    }
                }
            }

            // Add chat data.
            with (exporter) {

                (0..19).forEach { i ->
                    add(chatRounds.filter { it.round == i }.joinToString("\n") { "${it.participantId}: ${it.body}" })
                }
                insert("", index = 0)
                insert("", index = 12)
                endLine()
            }

            groupIndex ++
        }

        return exporter

    }

}