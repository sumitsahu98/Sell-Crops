fun String.similarity(other: String): Double {
    val s1 = this.lowercase()
    val s2 = other.lowercase()
    val maxLen = maxOf(s1.length, s2.length)
    if (maxLen == 0) return 1.0
    return (maxLen - levenshtein(s1, s2)).toDouble() / maxLen
}

// Levenshtein distance function
fun levenshtein(s: String, t: String): Int {
    val m = s.length
    val n = t.length
    val dp = Array(m + 1) { IntArray(n + 1) }

    for (i in 0..m) dp[i][0] = i
    for (j in 0..n) dp[0][j] = j

    for (i in 1..m) {
        for (j in 1..n) {
            val cost = if (s[i - 1] == t[j - 1]) 0 else 1
            dp[i][j] = minOf(
                dp[i - 1][j] + 1,      // deletion
                dp[i][j - 1] + 1,      // insertion
                dp[i - 1][j - 1] + cost // substitution
            )
        }
    }
    return dp[m][n]
}
