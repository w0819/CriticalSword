package com.github.w0819.critical_sword.meta_data.ability.storage

import com.github.w0819.critical_sword.util.ListUtil
import com.github.w0819.critical_sword.util.Util.simpleName
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.io.Serializable
import java.lang.reflect.Constructor
import java.lang.reflect.Parameter
import kotlin.reflect.KClass
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaType

open class SwordDataStorage<out T: Any, U: Any>(
    val sword: ItemStack,
    private val storageKey: NamespacedKey,
    valueClass: KClass<U>,
    targetTypes: List<T>,
    propertyToStoreSize: Int,
){
    private val constructor: Constructor<U> = valueClass.constructors.find { constructor ->
        val parameters = constructor.parameters
        parameters.size == (propertyToStoreSize + 1) && parameters.all { parameter -> parameter.type.javaType.typeName in ListUtil.primitiveTypeNames }
    }?.javaConstructor ?: throw IllegalStateException("${simpleName()} default constructor has too many parameters or parameters are not primitive")

    private val customKey: (T) -> String = { value -> value.toString().lowercase() }

    private val keyToStore: (T) -> (Parameter) -> NamespacedKey = { value ->
        { parameter ->
            NamespacedKey.minecraft("${storageKey.key}_${customKey(value)}_${parameter.name}")
        }
    }

    private val map: MutableMap<T, Map<Parameter, Any>> = targetTypes.associateWith { target ->
        val key = keyToStore(target)
        constructor.parameters.takeLast(propertyToStoreSize).associateWith { parameter ->
            val type = persistentType(parameter)
            val storeKey = key(parameter)

            type?.let { t -> dataContainer.get(storeKey, t) } ?: (defaultValue(parameter) ?: throw IllegalArgumentException("parameter of constructor is not primitive-type"))
        }
    }.toMutableMap()

    private val dataContainer: PersistentDataContainer
        get() = sword.itemMeta.persistentDataContainer

    private fun updatePersistentContainer(updateTask: PersistentDataContainer.() -> Unit) = sword.run {
        itemMeta = itemMeta.apply { persistentDataContainer.updateTask() }
    }

    protected fun updateToSword() = updatePersistentContainer {
        map.forEach { (key, value) ->
            val namespace = keyToStore(key)

            value.forEach { (parameter, valueToStore) ->
                val keyToStore = namespace(parameter)
                when(valueToStore) {
                    is Boolean -> {
                        val type = PersistentDataType.BOOLEAN
                        set(keyToStore, type, valueToStore)
                    }
                    is String -> {
                        val type = PersistentDataType.STRING
                        set(keyToStore, type, valueToStore)
                    }
                    is Short -> {
                        val type = PersistentDataType.SHORT
                        set(keyToStore, type, valueToStore)
                    }
                    is Int -> {
                        val type = PersistentDataType.INTEGER
                        set(keyToStore, type, valueToStore)
                    }
                    is IntArray -> {
                        val type = PersistentDataType.INTEGER_ARRAY
                        set(keyToStore, type, valueToStore)
                    }
                    is Long -> {
                        val type = PersistentDataType.LONG
                        set(keyToStore, type, valueToStore)
                    }
                    is LongArray -> {
                        val type = PersistentDataType.LONG_ARRAY
                        set(keyToStore, type, valueToStore)
                    }
                    is Double -> {
                        val type = PersistentDataType.DOUBLE
                        set(keyToStore, type, valueToStore)
                    }
                    is Float -> {
                        val type = PersistentDataType.FLOAT
                        set(keyToStore, type, valueToStore)
                    }
                    is Byte -> {
                        val type = PersistentDataType.BYTE
                        set(keyToStore, type, valueToStore)
                    }
                    is ByteArray -> {
                        val type = PersistentDataType.BYTE_ARRAY
                        set(keyToStore, type, valueToStore)
                    }
                    is PersistentDataContainer -> {
                        val type = PersistentDataType.TAG_CONTAINER
                        set(keyToStore, type, valueToStore)
                    }
                    is Array<*> -> {
                        val containers = valueToStore.filterIsInstance<PersistentDataContainer>()
                        val type = PersistentDataType.TAG_CONTAINER_ARRAY
                        set(keyToStore, type, containers.toTypedArray())
                    }
                }
            }
        }
    }

    private val persistentType: (Parameter) -> PersistentDataType<out Serializable,out Serializable>? = { parameter ->
        when(parameter.type) {
            Boolean::class.java -> PersistentDataType.BOOLEAN
            String::class.java -> PersistentDataType.STRING
            Short::class.java -> PersistentDataType.SHORT
            Int::class.java -> PersistentDataType.INTEGER
            IntArray::class.java -> PersistentDataType.INTEGER_ARRAY
            Long::class.java -> PersistentDataType.LONG
            LongArray::class.java -> PersistentDataType.LONG_ARRAY
            Double::class.java -> PersistentDataType.DOUBLE
            Float::class.java -> PersistentDataType.FLOAT
            Byte::class.java -> PersistentDataType.BYTE
            ByteArray::class.java -> PersistentDataType.BYTE_ARRAY
            else -> null
        }
    }

    private val defaultValue: (Parameter) -> Any? = { parameter ->
        when(parameter.type) {
            Boolean::class.java -> true
            String::class.java -> ""
            Short::class.java -> (0).toShort()
            Int::class.java -> 0
            IntArray::class.java -> intArrayOf()
            Long::class.java -> 0L
            LongArray::class.java -> longArrayOf()
            Double::class.java -> 0.0
            Float::class.java -> 0f
            Byte::class.java -> (0).toByte()
            ByteArray::class.java -> byteArrayOf()
            else -> null
        }
    }

    protected fun set(target: @UnsafeVariance T, vararg parameters: Any) {
        require(parameters.size == constructor.parameters.size)

        val parameterize = parameters.mapIndexed { index, any -> constructor.parameters[index] to any }.toMap()
        map.replace(target, parameterize)
    }

    fun remove(target: @UnsafeVariance T) { map.remove(target) }

    protected fun getOrDefault(target: @UnsafeVariance T, default: U): U = get(target) ?: default

    operator fun get(target: @UnsafeVariance T): U? =
        map[target]?.values?.toTypedArray()?.let { parameters ->
            constructor.newInstance(listOf(target) + parameters)
        }


}


