package chat.rocket.core.model

class Reactions : HashMap<String, List<String>>() {
    fun getUsernames(shortname: String) = get(shortname)

    fun getShortNames(): List<String> = keys.toList()
}