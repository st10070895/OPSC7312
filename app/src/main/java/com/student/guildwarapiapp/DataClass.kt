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
    data class Item(
        val id: Int,
        val name: String,
        val description: String,
        val type: String,
        val level: Int,
        val rarity: String,
        val vendor_value: Int,
        val game_types: List<String>,
        val flags: List<String>,
        val restrictions: List<String>,
        val chat_link: String,
        val icon: String
    )
}