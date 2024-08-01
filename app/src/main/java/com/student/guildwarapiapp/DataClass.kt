package com.student.guildwarapiapp

import kotlinx.serialization.Serializable

@Serializable
class DataClass {
    data class MaterialData(
        val id: Int,
        val category: Int,
        val binding: String? = null,
        val count: Int
    )
}