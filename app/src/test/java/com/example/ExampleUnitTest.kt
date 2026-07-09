package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testBincodeSerializationFormat() {
    // Verifies Rust's u64 Little Endian length prefix (8 bytes) + UTF-8 payload format
    val input = "ABC"
    val bytes = input.toByteArray(Charsets.UTF_8)
    val length = bytes.size.toLong()
    val bincodeBytes = java.nio.ByteBuffer.allocate(8 + bytes.size)
        .order(java.nio.ByteOrder.LITTLE_ENDIAN)
        .putLong(length)
        .put(bytes)
        .array()
    val hex = bincodeBytes.joinToString("") { "%02X".format(it) }
    
    // 3 bytes (0x03) in Little Endian u64, followed by Hex representation of ABC ("414243")
    assertEquals("0300000000000000414243", hex)
  }
}
