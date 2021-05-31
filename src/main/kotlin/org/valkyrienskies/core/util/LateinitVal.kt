package org.valkyrienskies.core.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private object Empty

// https://stackoverflow.com/questions/48443167/kotlin-lateinit-to-val-or-alternatively-a-var-that-can-set-once
fun <V> lateinitVal() = object : ReadWriteProperty<Any, V> {

    private var value: Any? = Empty

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): V {
        require(value != Empty) { "Value is not initialized" }
        return value as V
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: V) {
        require(this.value == Empty) { "Value is already initialized" }
        this.value = value
    }
}
