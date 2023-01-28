fun main() {
    // write your code here
    val size = readln().toInt()
    val numbers = List(size) { readln().toInt() }
    val (P, M) = readln().split(" ").map { it.toInt() }
    println(if (numbers.contains(P) && numbers.contains(M)) "YES" else "NO")
}