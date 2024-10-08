package dev.morphia.critter.parser

import dev.morphia.config.PropertyAnnotationProvider
import dev.morphia.critter.parser.asm.AddFieldAccessorMethods
import dev.morphia.critter.parser.asm.Generators.config
import dev.morphia.critter.parser.gizmo.GizmoPropertyAccessorGenerator
import dev.morphia.critter.parser.gizmo.GizmoPropertyModelGenerator
import dev.morphia.critter.parser.java.CritterClassLoader
import dev.morphia.critter.parser.java.CritterParser
import dev.morphia.mapping.Mapper
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode

class PropertyFinder(mapper: Mapper, val classLoader: CritterClassLoader) {
    val providerMap: Map<Class<out Any>, PropertyAnnotationProvider<*>>

    init {
        providerMap = mapper.config.propertyAnnotationProviders().associateBy { it.provides() }
    }

    fun find(entityType: Class<*>, classNode: ClassNode): List<String> {
        val models = mutableListOf<String>()
        val methods = discoverPropertyMethods(classNode)
        if (methods.isEmpty()) {
            val fields = discoverFields(classNode)
            classLoader.register(
                entityType.name,
                AddFieldAccessorMethods(entityType, fields).emit()
            )
            fields.forEach { field ->
                val accessorGenerator = GizmoPropertyAccessorGenerator(entityType, field)
                accessorGenerator.emit()
                val propertyModelGenerator = GizmoPropertyModelGenerator(config, entityType, field)
                propertyModelGenerator.emit()
                models += propertyModelGenerator.generatedType
            }
        }

        return models
    }

    private fun isPropertyAnnotated(
        annotationNodes: MutableList<AnnotationNode>?,
        allowUnannotated: Boolean
    ): Boolean {
        val annotations = annotationNodes ?: listOf()
        val keys = providerMap.keys.map { Type.getType(it).descriptor }
        return allowUnannotated || annotations.any { a -> a.desc in keys }
    }

    private fun discoverFields(classNode: ClassNode) =
        classNode.fields
            .filter { field ->
                (field.visibleAnnotations ?: listOf())
                    .map { a -> a.desc }
                    .none { desc -> desc in CritterParser.transientAnnotations() }
            }
            .filter { isPropertyAnnotated(it.visibleAnnotations, true) }

    private fun discoverPropertyMethods(classNode: ClassNode) =
        classNode.methods
            .filter { it.name.startsWith("get") && it.parameters.isEmpty() }
            .filter { isPropertyAnnotated(it.visibleAnnotations, false) }
}
