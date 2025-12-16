package com.example.cobasupabase.data.repositories

import com.example.cobasupabase.data.dto.PlaceDto
import com.example.cobasupabase.data.remote.SupabaseHolder
import com.example.cobasupabase.domain.mapper.PlaceMapper
import com.example.cobasupabase.domain.model.Place
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.gotrue.auth

class PlaceRepository {

    private val client = SupabaseHolder.client
    private val auth get() = client.auth

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
        userId: String,
        name: String,
        address: String,
        rating: Double? = null,
        imageUrl: String? = null
    ) {

        val newPlace = PlaceDto(
            userId = userId,
            name = name,
            address = address,
            rating = rating,
            imageUrl = imageUrl,
            createdAt = null
        )
        client.postgrest["places"].insert(newPlace)
    }

    /** UPDATE */
    suspend fun updatePlace(
        id: Int,
        userId: String,
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
            filter {
                eq("id", id)
                eq("user_id", userId) }
        }
    }

    /** DELETE */
    suspend fun deletePlace(id: Int, userId: String) {
        client.postgrest["places"].delete {
            filter {
                eq("id", id)
                eq("user_id", userId) }
        }
    }

    suspend fun getPlaceById(id: Int, userId: String): Place? {
        val response = client.postgrest["places"]
            .select {
                filter {
                    eq("id", id)
                    eq("user_id", userId) }
            }
            .decodeSingleOrNull<PlaceDto>()
        return response?.let { PlaceMapper.map(it) }
    }
}