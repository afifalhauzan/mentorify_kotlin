package com.example.cobasupabase.data.repositories

import com.example.cobasupabase.data.dto.PlaceDto
import com.example.cobasupabase.data.remote.SupabaseHolder
import com.example.cobasupabase.domain.mapper.PlaceMapper
import com.example.cobasupabase.domain.model.Place
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage

class PlaceRepository {

    private val client = SupabaseHolder.client

    /** READ */
    suspend fun getPlaces(): List<Place> {
        val response = client.postgrest["places"]
            .select {
                order(
                    "created_at",
                    io.github.jan.supabase.postgrest.query.Order.DESCENDING
                )
            }
            .decodeList<PlaceDto>()

        return response.map { PlaceMapper.map(it) }
    }

    /** CREATE */
    suspend fun createPlace(
        name: String,
        address: String,
        rating: Double,
        imageBytes: ByteArray?
    ) {
        var imageUrl: String? = null

        if (imageBytes != null) {
            val fileName = "${System.currentTimeMillis()}.jpg"
            val bucket = client.storage["review-images"]
            bucket.upload(fileName, imageBytes)
            imageUrl = bucket.publicUrl(fileName)
        }

        client.postgrest["places"].insert(
            mapOf(
                "name" to name,
                "address" to address,
                "rating" to rating,
                "image_url" to imageUrl
            )
        )
    }

    /** UPDATE */
    suspend fun updatePlace(
        id: Int,
        name: String,
        address: String,
        rating: Double
    ) {
        client.postgrest["places"].update(
            {
                set("name", name)
                set("address", address)
                set("rating", rating)
            }
        ) {
            filter { eq("id", id) }
        }
    }

    /** DELETE */
    suspend fun deletePlace(id: Int) {
        client.postgrest["places"].delete {
            filter { eq("id", id) }
        }
    }
}
