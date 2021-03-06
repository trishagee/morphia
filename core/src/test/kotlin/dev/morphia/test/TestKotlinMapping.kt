package dev.morphia.test

import dev.morphia.annotations.Entity
import dev.morphia.annotations.Id
import dev.morphia.annotations.Version
import org.bson.types.ObjectId
import org.testng.Assert.assertEquals
import org.testng.Assert.assertFalse
import org.testng.annotations.Test

class TestKotlinMapping : TestBase() {
    @Test
    fun dataClasses() {
        val list = ds.mapper.map(MyClass::class.java)
        assertFalse(list.isEmpty())
        val myClass = MyClass(ObjectId(), 42)
        ds.save(myClass)
        val loaded = ds.find(MyClass::class.java)
            .first()

        assertEquals(loaded, myClass)
    }

    @Test
    fun versioning() {
        val map = ds.mapper.map(VersionedDataClass::class.java)
        val versioned = VersionedDataClass(null, "temp")
        ds.save(versioned)
        val loaded = ds.find(VersionedDataClass::class.java)
            .first()

        assertEquals(loaded, versioned)
        assertEquals(loaded.version, 1)
    }
}

@Entity
private data class MyClass(@Id val id: ObjectId, val value: Int = 0)

@Entity
private data class VersionedDataClass(@Id val id: ObjectId?, val name: String, @Version val version: Long = 0)
