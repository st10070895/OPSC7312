package com.student.guildwarsapi2

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
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

    val apiKey = "4A29EC1D-4C3C-694B-B1CD-91CBEB77A782C4862212-F05E-4BB3-8296-AE745846FB4E"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        accountMaterials()
    }

    private fun accountMaterials() = runBlocking {
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

            // Fetch names and icons for each material in batches
            val materialWithNamesAndIcons = coroutineScope {
                val itemIds = materials.map { it.id }.distinct()
                val items = fetchItemNames(client, itemIds)
                materials.map { material ->
                    val item = items[material.id]
                    val itemName = item?.name ?: "Unknown"
                    val itemIcon = item?.icon ?: ""
                    material to Pair(itemName, itemIcon)
                }
            }

            // Display the combined result
            val text = findViewById<TextView>(R.id.txtOutput)
            text.text = materialWithNamesAndIcons.joinToString("\n") { "ID: ${it.first.id}, Name: ${it.second.first}, Category: ${it.first.category}, Count: ${it.first.count}" }

            // Load the first icon into the ImageView
            val imageView = findViewById<ImageView>(R.id.imageView)
            val firstIconUrl = materialWithNamesAndIcons.firstOrNull()?.second?.second
            firstIconUrl?.let {
                loadImage(it, imageView)
            }

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

    private fun loadImage(url: String, imageView: ImageView) {
        Glide.with(this)
            .load(url)
            .into(imageView)
    }
}
