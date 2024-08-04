package com.student.guildwarsapi2

data class Material(
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

data class MaterialItem(
    val id: Int,
    val name: String,
    val icon: String,
    val count: Int,
    val buyPrice: Int,
    val sellPrice: Int
)

data class Buys(
    val quantity: Int,
    val unit_price: Int
)

data class  Sells(
    val quantity: Int,
    val unit_price: Int
)

data class MaterialPrices(
    val id: Int,
    val whitelisted: Boolean,
    val buys: Buys,
    val sells: Sells
)
