package dev.morphia.critter.parser.gizmo

import dev.morphia.critter.parser.CritterGizmoGenerator
import dev.morphia.critter.parser.GeneratorTest
import dev.morphia.critter.parser.java.CritterParser.critterClassLoader
import dev.morphia.critter.sources.Example
import dev.morphia.mapping.codec.pojo.PropertyModel
import dev.morphia.mapping.codec.pojo.TypeData
import io.quarkus.gizmo.ClassCreator
import io.quarkus.gizmo.MethodDescriptor
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import org.objectweb.asm.Type
import org.testng.Assert.assertEquals
import org.testng.Assert.assertNotNull
import org.testng.Assert.assertTrue
import org.testng.Assert.fail
import org.testng.annotations.Test

class TestGizmoGeneration {
    @Test
    fun testMapStringExample() {
        var descString = "Ljava/util/Map<Ljava/lang/String;Ldev/morphia/critter/sources/Example;>;"
        var descriptor =
            descriptor(
                Map::class.java,
                descriptor(String::class.java),
                descriptor(Example::class.java)
            )

        assertEquals(descriptor, descString)
        var typeData = typeData(descString)[0]
        assertEquals(
            typeData,
            Map::class.typeData(String::class.typeData(), Example::class.typeData())
        )
    }

    @Test
    fun testListMapStringExample() {
        val descString =
            "Ljava/util/List<Ljava/util/Map<Ljava/lang/String;Ldev/morphia/critter/sources/Example;>;>;"
        val descriptor =
            descriptor(
                List::class.java,
                descriptor(
                    Map::class.java,
                    descriptor(String::class.java),
                    descriptor(Example::class.java)
                )
            )
        assertEquals(descriptor, descString)

        val typeData = typeData(descString)[0]
        assertEquals(
            typeData,
            List::class.typeData(
                Map::class.typeData(String::class.typeData(), Example::class.typeData())
            )
        )
    }

    @Test
    fun testMapOfList() {
        val descString =
            "Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ldev/morphia/critter/sources/Example;>;>;"
        val descriptor =
            descriptor(
                Map::class.java,
                descriptor(String::class.java),
                descriptor(List::class.java, descriptor(Example::class.java))
            )
        assertEquals(descriptor, descString)
        val typeData = typeData(descriptor)[0]
        assertEquals(
            typeData,
            Map::class.typeData(
                String::class.typeData(),
                List::class.typeData(Example::class.typeData())
            )
        )
    }

    @Test
    fun testPrimitiveArray() {
        val typeData = typeData("[I")[0]
        assertTrue(typeData.array)
    }

    private fun descriptor(type: Class<*>, vararg typeParameters: String): String {
        var desc = Type.getDescriptor(type)
        if (typeParameters.isNotEmpty()) {
            desc =
                desc.dropLast(1) +
                    typeParameters.joinToString("", prefix = "<", postfix = ">") +
                    ";"
        }

        return desc
    }

    private fun KClass<*>.typeData(vararg typeParameters: TypeData<*>): TypeData<*> {
        return TypeData(this.java, listOf(*typeParameters))
    }

    @Test
    fun testGizmo() {
        val generator = CritterGizmoGenerator(critterClassLoader, GeneratorTest.mapper)
        generator.generate(Example::class.java)
        critterClassLoader.loadClass("dev.morphia.critter.sources.__morphia.example.AgeModel")
        val nameModel =
            critterClassLoader.loadClass("dev.morphia.critter.sources.__morphia.example.NameModel")
        invokeAll(PropertyModel::class.java, nameModel)
        critterClassLoader.loadClass("dev.morphia.critter.sources.__morphia.example.SalaryModel")
        critterClassLoader
            .loadClass("dev.morphia.critter.sources.__morphia.example.AgeAccessor")
            .getConstructor()
            .newInstance()
        critterClassLoader
            .loadClass("dev.morphia.critter.sources.__morphia.example.NameAccessor")
            .getConstructor()
            .newInstance()
        critterClassLoader
            .loadClass("dev.morphia.critter.sources.__morphia.example.SalaryAccessor")
            .getConstructor()
            .newInstance()
    }

    private fun invokeAll(type: Class<*>, nameModel: Class<*>) {
        val instance = nameModel.constructors[0].newInstance(null)
        val results =
            type.declaredMethods
                .filter {
                    Modifier.isPublic(it.modifiers) &&
                        !Modifier.isFinal(it.modifiers) &&
                        it.parameterCount == 0
                }
                .filter { it.name !in listOf("hashCode", "toString") }
                .sortedBy { it.name }
                .map { method ->
                    try {
                        nameModel.getDeclaredMethod(method.name, *method.parameterTypes)
                        null
                    } catch (e: Exception) {
                        e.message
                    }
                }
                .filterNotNull()

        if (results.isNotEmpty()) {
            fail("Missing methods from ${type.name}: \n${results.joinToString("\n")}")
        }
    }

    @Test
    fun testConstructors() {
        val className = "dev.morphia.critter.GizmoSubclass"
        val constructorCall =
            ClassCreator.builder()
                .classOutput { name, data ->
                    critterClassLoader.register(name.replace('/', '.'), data)
                }
                .className("dev.morphia.critter.ConstructorCall")
                .build()
        val fieldCreator =
            constructorCall
                .getFieldCreator("name", String::class.java)
                .setModifiers(Modifier.PUBLIC)
        val constructorCreator = constructorCall.getConstructorCreator(String::class.java)
        constructorCreator.invokeSpecialMethod(
            MethodDescriptor.ofConstructor(Object::class.java),
            constructorCreator.`this`
        )
        constructorCreator.setParameterNames(arrayOf("name"))
        constructorCreator.writeInstanceField(
            fieldCreator.fieldDescriptor,
            constructorCreator.`this`,
            constructorCreator.getMethodParam(0)
        )

        constructorCreator.returnVoid()
        constructorCall.close()
        val newInstance =
            critterClassLoader
                .loadClass("dev.morphia.critter.ConstructorCall")
                .getConstructor(String::class.java)
                .newInstance("here i am")

        val creator =
            ClassCreator.builder()
                .classOutput { name, data ->
                    critterClassLoader.register(name.replace('/', '.'), data)
                }
                .className(className)
                .superClass("dev.morphia.critter.ConstructorCall")
                .build()
        val constructor = creator.getConstructorCreator(String::class.java)
        constructor.invokeSpecialMethod(
            MethodDescriptor.ofConstructor(
                "dev.morphia.critter.ConstructorCall",
                String::class.java
            ),
            constructor.getThis(),
            constructor.getMethodParam(0)
        )
        constructor.setParameterNames(arrayOf("subName"))
        constructor.returnVoid()
        constructor.close()
        creator.close()
        val instance =
            critterClassLoader
                .loadClass(className)
                .getConstructor(String::class.java)
                .newInstance("This is my name")

        assertNotNull(instance)
    }
}
