package com.example.home_recipe.domain.auth.oauth2

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object SerializationUtils {

    fun serialize(obj: Any): ByteArray {
        ByteArrayOutputStream().use { bos ->
            ObjectOutputStream(bos).use { oos ->
                oos.writeObject(obj)
            }
            return bos.toByteArray()
        }
    }

    fun deserialize(bytes: ByteArray): Any {
        ByteArrayInputStream(bytes).use { bis ->
            ObjectInputStream(bis).use { ois ->
                return ois.readObject()
            }
        }
    }
}
