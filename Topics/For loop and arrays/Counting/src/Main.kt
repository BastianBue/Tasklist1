fun main() {
    // write your code here
    val n = readln().toInt()
    val numbers = MutableList(n){ readln().toInt() }
    val m = readln().toInt()
    println(numbers.count { it == m })
}