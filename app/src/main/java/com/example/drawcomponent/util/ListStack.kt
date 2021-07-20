package com.example.drawcomponent.util

import java.util.*

class ListStack<T> {
    private val _list: MutableList<T> = mutableListOf()
    private val _stack: Stack<T> = Stack()
    val size get() = _list.size
    val latestValue
        get() = if (_list.isNotEmpty()) _list[size - 1] else null
    val list: List<T>
        get() = _list
    val isNotEmptyList
        get() = _list.isNotEmpty()
    val isNotEmptyStack
        get() = _stack.isNotEmpty()


    fun removeAt(idx: Int) {
        _list.removeAt(idx)
    }

    fun removeLast() = _list.removeAt(size - 1)

    fun addAt(idx: Int, value: T) {
        _list.add(idx, value)
    }

    fun addLast(value: T) {
        _list.add(size, value)
    }

    fun push(value: T) {
        _stack.push(value)
    }

    fun pop(): T = _stack.pop()

    fun clear() {
        _list.clear()
        _stack.clear()
    }
}