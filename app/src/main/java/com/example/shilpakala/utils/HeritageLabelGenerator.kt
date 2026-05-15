package com.example.shilpakala.utils

import com.example.shilpakala.BuildConfig
import com.example.shilpakala.domain.model.PhotoMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HeritageLabelGenerator @Inject constructor() {
    suspend fun generate(metadata: PhotoMetadata): String = withContext(Dispatchers.IO) {
        if (BuildConfig.GEMINI_API_KEY.isBlank()) {
            return@withContext localLabel(metadata)
        }
        runCatching { callGemini(metadata) }.getOrElse { localLabel(metadata) }
    }

    private fun localLabel(metadata: PhotoMetadata): String =
        "${metadata.productName.ifBlank { "This handcrafted piece" }} is made by ${metadata.artisanName.ifBlank { "a Karnataka artisan" }} using ${metadata.woodType.ifBlank { "traditional materials" }}. It carries the warmth of handmade skill and the heritage of Karnataka craft."

    private fun callGemini(metadata: PhotoMetadata): String {
        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${BuildConfig.GEMINI_API_KEY}")
        val prompt = "Write a 35-word luxury heritage product label for a Karnataka handmade item. Product: ${metadata.productName}. Artisan: ${metadata.artisanName}. Material: ${metadata.woodType}."
        val body = JSONObject()
            .put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt)))))
            .toString()
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
            outputStream.use { it.write(body.toByteArray()) }
        }
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        return JSONObject(response)
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
            .trim()
    }
}
