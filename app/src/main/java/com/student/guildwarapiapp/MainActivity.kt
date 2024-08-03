package com.student.guildwarapiapp

import android.os.Bundle
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.student.guildwarapiapp.databinding.ActivityMainBinding
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
        val name: String
    )

    val apiKey = "4A29EC1D-4C3C-694B-B1CD-91CBEB77A782C4862212-F05E-4BB3-8296-AE745846FB4E"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Ensure this function is called
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
            println(responseBody)

            // Parse JSON response using Gson
            val materialListType = object : TypeToken<List<Material>>() {}.type
            val materials: List<Material> = Gson().fromJson(responseBody, materialListType)

            // Fetch names for each material
            val materialWithNames = coroutineScope {
                materials.map { material ->
                    async {
                        val itemName = fetchItemName(client, material.id).name
                        material to itemName
                    }
                }.awaitAll()
            }

            // Display the combined result
            val text = findViewById<TextView>(R.id.txtOutput)
            text.text = materialWithNames.joinToString("\n") { "ID: ${it.first.id}, Name: ${it.second}, Category: ${it.first.category}, Count: ${it.first.count}" }

        } catch (e: Exception) {
            println("Error: ${e.message}")
        } finally {
            client.close()
        }
    }

    private suspend fun fetchItemName(client: HttpClient, id: Int): Item {
        val url = "https://api.guildwars2.com/v2/items/$id"
        val response: HttpResponse = client.get(url)
        val responseBody: String = response.bodyAsText()
        return Gson().fromJson(responseBody, Item::class.java)
    }
}
