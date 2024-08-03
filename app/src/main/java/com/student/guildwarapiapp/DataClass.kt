package com.student.guildwarapiapp

import kotlinx.serialization.Serializable

@Serializable
class DataClass {
    data class Materials(
        val id: Int,
        val category: Int,
        val binding: String? = null,
        val count: Int
    )
    data class Items(
        val id: Int,
        val name: String,
        val icon: String

    )
}