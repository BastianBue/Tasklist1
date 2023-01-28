package tasklist

import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

data class MoshiTest(val test: String = "hi", val anotherMember: Int = 2)

class MoshiTestAdapter {
    @ToJson
    fun toJson(moshiTest: MoshiTest): String {
        return moshiTest.test + 14
    }

    @FromJson
    fun fromJson(moshiTest: String): MoshiTest {
        if (moshiTest.length != 2) throw JsonDataException("Unknown moshiTest: $moshiTest")
        println(moshiTest)
        return MoshiTest()
    }
}

/*
fun serialize(): String {
    val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    val adapter: JsonAdapter<List<MoshiTest>> = moshi.adapter(List::class.java,MoshiTest::class.java)
    val instance = MoshiTest()
    val json = adapter.toJson(listOf(instance))
    val newInstance = adapter.fromJson(json)
    println(json)
    println(newInstance)
    return json
}*/
