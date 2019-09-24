package ie.dublinmapper.util

object StringUtils {

    const val EMPTY_STRING = ""
    private const val AMPERSAND = "&"
    const val MIDDLE_DOT = "\u00B7"

    fun join(strings: List<String>, delimeter: String): String {
        val stringBuilder = StringBuilder()
        for (i in strings.indices) {
            stringBuilder.append(strings[i])
            if (i < strings.size - 1) {
                stringBuilder.append(delimeter)
            }
        }
        return stringBuilder.toString()
    }

    fun isNullOrEmpty(string: String?): Boolean {
        return string == null || EMPTY_STRING == string.trim { it <= ' ' }
    }

}
