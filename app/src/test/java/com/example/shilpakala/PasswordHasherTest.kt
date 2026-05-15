package com.example.shilpakala

import com.example.shilpakala.utils.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PasswordHasherTest {
    @Test
    fun samePasswordProducesStableHash() {
        val hasher = PasswordHasher()
        assertEquals(hasher.hash("secret123"), hasher.hash("secret123"))
    }

    @Test
    fun differentPasswordsProduceDifferentHashes() {
        val hasher = PasswordHasher()
        assertNotEquals(hasher.hash("secret123"), hasher.hash("secret456"))
    }
}
