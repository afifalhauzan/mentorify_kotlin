package com.example.cobasupabase.data.repositories

import com.example.cobasupabase.data.dto.NewsDto
import com.example.cobasupabase.data.remote.SupabaseHolder
import com.example.cobasupabase.domain.mapper.NewsMapper
import com.example.cobasupabase.domain.mapper.TeacherMapper
import com.example.cobasupabase.domain.model.News
import io.github.jan.supabase.postgrest.postgrest


class NewsRepository {
    private val postgrest get() = SupabaseHolder.client.postgrest

    //get semua news
    suspend fun getNews(): List<News> {
        val response = postgrest["news"].select()
        val listDto = response.decodeList<NewsDto>()
        return listDto.map { NewsMapper.map(it) }
    }

    //getNews By id
    suspend fun getNewsById(id: Int): News {
        val response = postgrest["news"].select {
            filter {
                eq("id", id)
            }
        }.decodeSingle<NewsDto>()
        return NewsMapper.map(response)
    }
}
