package com.example.home_recipe.domain.auth.oauth2

import java.io.*

object SerializationUtils {
    fun serialize(obj: Any): ByteArray {
        ByteArrayOutputStream().use { bos ->
            ObjectOutputStream(bos).use { it.writeObject(obj) }
            return bos.toByteArray()
        }
    }

    fun deserialize(bytes: ByteArray): Any {
        ByteArrayInputStream(bytes).use { bis ->
            ObjectInputStream(bis).use { return it.readObject() }
        }
    }
}
