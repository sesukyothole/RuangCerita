package com.example.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.InetAddress
import java.util.concurrent.TimeUnit

class BloggerFetcher {

    // Standard bootstrap HTTP client
    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    // Custom Cloudflare 1.1.1.1 DNS over HTTPS (DoH) resolver
    private class CloudflareDns(private val bootstrapClient: OkHttpClient) : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            try {
                // Query Cloudflare 1.1.1.1 DoH API directly via secure HTTPS
                val url = "https://1.1.1.1/dns-query?name=$hostname&type=A"
                val request = Request.Builder()
                    .url(url)
                    .header("Accept", "application/dns-json")
                    .build()

                bootstrapClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: ""
                        val json = JSONObject(body)
                        val answerArray = json.optJSONArray("Answer")
                        if (answerArray != null && answerArray.length() > 0) {
                            val addresses = mutableListOf<InetAddress>()
                            for (i in 0 until answerArray.length()) {
                                val item = answerArray.getJSONObject(i)
                                val data = item.optString("data")
                                if (data.isNotEmpty() && !data.contains(":")) { // IPv4 address
                                    addresses.addAll(InetAddress.getAllByName(data))
                                }
                            }
                            if (addresses.isNotEmpty()) {
                                Log.i("CloudflareDns", "Berhasil resolve $hostname via Cloudflare 1.1.1.1 DoH: $addresses")
                                return addresses
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("CloudflareDns", "Gagal lookup DNS $hostname via Cloudflare 1.1.1.1: ${e.message}")
            }
            // Fallback to system default DNS if DoH fails
            return Dns.SYSTEM.lookup(hostname)
        }
    }

    // OkHttpClient configured with Cloudflare 1.1.1.1 DoH
    private val cloudflareClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .dns(CloudflareDns(client))
        .build()

    suspend fun fetchLatestStories(
        feedUrl: String = "https://iseng-isex.blogspot.com/feeds/posts/default?alt=json",
        startIndex: Int = 1,
        maxResults: Int = 25
    ): Result<List<StoryEntity>> = withContext(Dispatchers.IO) {
        val baseUrl = if (feedUrl.contains("?")) {
            if (feedUrl.contains("alt=json")) feedUrl else "$feedUrl&alt=json"
        } else {
            "$feedUrl?alt=json"
        }
        val targetFeedPath = if (baseUrl.contains("start-index=")) {
            baseUrl
        } else {
            "$baseUrl&start-index=$startIndex&max-results=$maxResults"
        }
        
        val encodedUrl = java.net.URLEncoder.encode(targetFeedPath, "UTF-8")

        // Multi-tier resilience routes (Direct -> Cloudflare DoH -> Cloudflare Proxy Tunnel -> AllOrigins Tunnel -> Google Proxy)
        val routes = listOf(
            Route("Jalur Direct", client, targetFeedPath),
            Route("Jalur Cloudflare 1.1.1.1 DoH", cloudflareClient, targetFeedPath),
            Route("Jalur Cloudflare Proxy Tunnel", client, "https://corsproxy.io/?$encodedUrl"),
            Route("Jalur AllOrigins Cloud Tunnel", client, "https://api.allorigins.win/raw?url=$encodedUrl"),
            Route("Jalur Google Proxy Relay", client, "https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy?container=focus&gadget=a&no_expand=1&resize_h=0&rewriteMime=image%2F*&url=$encodedUrl")
        )

        var lastException: Exception? = null

        for ((index, route) in routes.withIndex()) {
            try {
                Log.d("BloggerFetcher", "Mencoba ${route.name} (${index + 1}/${routes.size}): ${route.url}")
                val request = Request.Builder()
                    .url(route.url)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 10) RuangCeritaApp/1.0")
                    .build()

                val response = route.client.newCall(request).execute()
                if (!response.isSuccessful) {
                    Log.w("BloggerFetcher", "${route.name} gagal dengan status HTTP ${response.code}")
                    lastException = Exception("HTTP ${response.code} pada ${route.name}")
                    continue
                }

                val bodyString = response.body?.string()
                if (bodyString.isNullOrBlank() || !bodyString.trim().startsWith("{")) {
                    Log.w("BloggerFetcher", "${route.name} mengembalikan format non-JSON")
                    lastException = Exception("Format data tidak valid pada ${route.name}")
                    continue
                }

                val jsonObject = JSONObject(bodyString)
                val feedObj = jsonObject.optJSONObject("feed") ?: continue
                val entries = feedObj.optJSONArray("entry") ?: continue

                val stories = mutableListOf<StoryEntity>()

                for (i in 0 until entries.length()) {
                    val entry = entries.getJSONObject(i)
                    val idObj = entry.optJSONObject("id")
                    val rawId = idObj?.optString("\$t") ?: "blog_post_${startIndex + i}"
                    val cleanId = "blogger_" + rawId.hashCode().toString().replace("-", "n")

                    val titleObj = entry.optJSONObject("title")
                    val title = titleObj?.optString("\$t") ?: "Cerita Tanpa Judul"

                    val contentObj = entry.optJSONObject("content") ?: entry.optJSONObject("summary")
                    val rawContentHtml = contentObj?.optString("\$t") ?: ""
                    
                    val cleanText = stripHtml(rawContentHtml)
                    val summary = if (cleanText.length > 150) cleanText.substring(0, 150) + "..." else cleanText

                    val publishedObj = entry.optJSONObject("published")
                    val rawDate = publishedObj?.optString("\$t") ?: ""
                    val cleanDate = if (rawDate.length >= 10) rawDate.substring(0, 10) else "Terbaru"

                    val authorArray = entry.optJSONArray("author")
                    val authorName = if (authorArray != null && authorArray.length() > 0) {
                        authorArray.getJSONObject(0).optJSONObject("name")?.optString("\$t") ?: "Penulis Cerita"
                    } else "Penulis Cerita"

                    val categoryArray = entry.optJSONArray("category")
                    var detectedGenre = "Kehidupan"
                    if (categoryArray != null && categoryArray.length() > 0) {
                        for (j in 0 until categoryArray.length()) {
                            val term = categoryArray.getJSONObject(j).optString("term", "").lowercase()
                            when {
                                term.contains("roman") || term.contains("cinta") -> detectedGenre = "Romantis"
                                term.contains("horor") || term.contains("seram") || term.contains("misteri") -> detectedGenre = "Horor"
                                term.contains("komedi") || term.contains("lucu") -> detectedGenre = "Komedi"
                                term.contains("fanta") || term.contains("gaib") -> detectedGenre = "Fantasi"
                                term.contains("petualang") -> detectedGenre = "Petualangan"
                                term.contains("sci") || term.contains("teknologi") -> detectedGenre = "Sci-Fi"
                            }
                        }
                    } else {
                        val combinedText = (title + " " + cleanText).lowercase()
                        when {
                            combinedText.contains("cinta") || combinedText.contains("senja") || combinedText.contains("janji") -> detectedGenre = "Romantis"
                            combinedText.contains("hantu") || combinedText.contains("malam") || combinedText.contains("jam") -> detectedGenre = "Horor"
                            combinedText.contains("lucu") || combinedText.contains("konyol") || combinedText.contains("warung") -> detectedGenre = "Komedi"
                            combinedText.contains("ajaib") || combinedText.contains("hutan") -> detectedGenre = "Fantasi"
                            combinedText.contains("jurang") || combinedText.contains("ombak") -> detectedGenre = "Petualangan"
                        }
                    }

                    val estimatedReadTime = (cleanText.split("\\s+".toRegex()).size / 120).coerceAtLeast(3)

                    stories.add(
                        StoryEntity(
                            id = cleanId,
                            title = title,
                            author = authorName,
                            genre = detectedGenre,
                            summary = summary,
                            content = if (cleanText.isNotBlank()) cleanText else "Cerita dapat dibaca langsung di aplikasi ini.",
                            publishedDate = cleanDate,
                            readTimeMinutes = estimatedReadTime,
                            isFavorite = false,
                            isSyncedFromWeb = true,
                            sourceUrl = ""
                        )
                    )
                }

                if (stories.isNotEmpty()) {
                    Log.i("BloggerFetcher", "Berhasil mengambil ${stories.size} cerita via ${route.name}")
                    return@withContext Result.success(stories)
                }
            } catch (e: Exception) {
                Log.e("BloggerFetcher", "Koneksi ${route.name} gagal: ${e.message}")
                lastException = e
            }
        }

        Result.failure(lastException ?: Exception("Gagal terhubung ke seluruh jalur proxy & Cloudflare DNS"))
    }

    suspend fun fetchAllAvailableStories(
        feedUrl: String = "https://iseng-isex.blogspot.com/feeds/posts/default?alt=json"
    ): Result<List<StoryEntity>> = withContext(Dispatchers.IO) {
        val allStories = mutableListOf<StoryEntity>()
        var startIndex = 1
        val pageSize = 25
        var lastError: Exception? = null

        while (true) {
            val pageResult = fetchLatestStories(feedUrl = feedUrl, startIndex = startIndex, maxResults = pageSize)
            if (pageResult.isSuccess) {
                val pageList = pageResult.getOrDefault(emptyList())
                if (pageList.isEmpty()) {
                    break
                }
                allStories.addAll(pageList)
                if (pageList.size < pageSize) {
                    // No more pages on server
                    break
                }
                startIndex += pageSize
            } else {
                lastError = pageResult.exceptionOrNull() as? Exception
                break
            }
        }

        if (allStories.isNotEmpty()) {
            Result.success(allStories)
        } else if (lastError != null) {
            Result.failure(lastError)
        } else {
            Result.success(emptyList())
        }
    }

    private data class Route(val name: String, val client: OkHttpClient, val url: String)

    private fun stripHtml(html: String): String {
        return html.replace("<[^>]*>".toRegex(), " ")
            .replace("&nbsp;".toRegex(), " ")
            .replace("&amp;".toRegex(), "&")
            .replace("&quot;".toRegex(), "\"")
            .replace("&lt;".toRegex(), "<")
            .replace("&gt;".toRegex(), ">")
            .replace("\\s+".toRegex(), " ")
            .trim()
    }
}

