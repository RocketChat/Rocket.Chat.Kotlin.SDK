package chat.rocket.core.model

class Reactions : HashMap<String, Pair<List<String>, List<String>>>() {

    fun getUsernames(shortname: String) = get(shortname)

    fun getNames(shortname: String) = get(shortname)

    fun set(shortname: String, usernameList: List<String>, nameList: List<String>) =
        set(shortname, Pair(usernameList, nameList))

    fun getShortNames(): List<String> = keys.toList()
}