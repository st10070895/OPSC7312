package com.student.guildwarsapi2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.gson.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val apiKey = "4A29EC1D-4C3C-694B-B1CD-91CBEB77A782C4862212-F05E-4BB3-8296-AE745846FB4E"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        fetchMaterials()
    }

    private fun fetchMaterials() = runBlocking {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                gson()
            }
        }
        val url = "https://api.guildwars2.com/v2/account/materials"

        try {
            val response: HttpResponse = client.get(url) {
                headers {
                    append("Authorization", "Bearer $apiKey")
                }
            }

            val responseBody: String = response.bodyAsText()

            // Parse JSON response using Gson
            val materialListType = object : TypeToken<List<Material>>() {}.type
            val materials: List<Material> = Gson().fromJson(responseBody, materialListType)

            // Fetch item details including buy and sell prices
            val items = fetchItemNames(client, materials.map { it.id })

            // Fetch material details including buy and sell prices
            val materialPrices = fetchMaterialPrices(client, materials.map { it.id })

            val materialItems = materials.map { material ->
                val item = items[material.id]
                val price = materialPrices[material.id]
                MaterialItem(
                    id = material.id,
                    name = item?.name ?: "Unknown",
                    icon = item?.icon ?: "",
                    count = material.count,
                    buyPrice = price?.buys?.unit_price ?: 0,
                    sellPrice = price?.sells?.unit_price ?: 0
                )
            }

            // Set up RecyclerView
            val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = MaterialAdapter(materialItems)

        } catch (e: Exception) {
            println("Error: ${e.message}")
        } finally {
            client.close()
        }
    }

    private suspend fun fetchItemNames(client: HttpClient, ids: List<Int>): Map<Int, Item> {
        val chunkedIds = ids.chunked(200) // Batch size of 200
        val items = mutableMapOf<Int, Item>()

        coroutineScope {
            chunkedIds.map { chunk ->
                async {
                    val url = "https://api.guildwars2.com/v2/items?ids=${chunk.joinToString(",")}"
                    try {
                        val response: HttpResponse = client.get(url)
                        val responseBody: String = response.bodyAsText()
                        val itemListType = object : TypeToken<List<Item>>() {}.type
                        val batchItems: List<Item> = Gson().fromJson(responseBody, itemListType)
                        batchItems.associateByTo(items) { it.id }
                    } catch (e: Exception) {
                        println("Error fetching item names: ${e.message}")
                    }
                }
            }.awaitAll()
        }

        return items
    }

    private suspend fun fetchMaterialPrices(client: HttpClient, ids: List<Int>): Map<Int, MaterialPrices> {
        val chunkedIds = ids.chunked(200) // Batch size of 200
        val materialPrices = mutableMapOf<Int, MaterialPrices>()

        coroutineScope {
            chunkedIds.map { chunk ->
                async {
                    val url = "https://api.guildwars2.com/v2/commerce/prices?ids=${chunk.joinToString(",")}"
                    try {
                        val response: HttpResponse = client.get(url)
                        val responseBody: String = response.bodyAsText()
                        val materialPricesListType = object : TypeToken<List<MaterialPrices>>() {}.type
                        val batchMaterialPrices: List<MaterialPrices> = Gson().fromJson(responseBody, materialPricesListType)
                        batchMaterialPrices.associateByTo(materialPrices) { it.id }
                    } catch (e: Exception) {
                        println("Error fetching material prices: ${e.message}")
                    }
                }
            }.awaitAll()
        }

        return materialPrices
    }
}
