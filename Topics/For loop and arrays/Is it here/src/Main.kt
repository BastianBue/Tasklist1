fun main() {
    // write your code here
    val n = readln().toInt()
    val others = MutableList(n) { readln().toInt() }
    val m =readln().toInt()
    println(if (others.contains(m)) "YES" else "NO")
}