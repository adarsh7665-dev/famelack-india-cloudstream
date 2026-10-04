package com.adarsh7665.famelackindia

import com.fasterxml.jackson.annotation.JsonProperty
import com.lagradost.cloudstream3.HomePageList
import com.lagradost.cloudstream3.LoadResponse
import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.MainPageRequest
import com.lagradost.cloudstream3.SearchResponse
import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.newHomePageResponse
import com.lagradost.cloudstream3.newLiveSearchResponse
import com.lagradost.cloudstream3.newLiveStreamLoadResponse
import com.lagradost.cloudstream3.utils.AppUtils.parseJson
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.ExtractorLinkType
import com.lagradost.cloudstream3.utils.M3u8Helper
import com.lagradost.cloudstream3.utils.newExtractorLink

class FamelackIndiaProvider : MainAPI() {
    override var name = "Famelack India TV"
    override var mainUrl = "https://famelack.com"
    override var lang = "hi"
    override val hasMainPage = true
    override val hasDownloadSupport = false
    override val supportedTypes = setOf(TvType.Live)

    companion object {
        private const val DATA_URL =
            "https://raw.githubusercontent.com/famelack/famelack-data/main/tv/raw/countries/in.json"
    }

    private data class FamelackChannel(
        @JsonProperty("name") val name: String = "",
        @JsonProperty("nanoid") val nanoid: String = "",
        @JsonProperty("country") val country: String = "",
        @JsonProperty("category") val category: String? = null,
        @JsonProperty("logo") val logo: String? = null,
        @JsonProperty("sources") val sources: Sources? = null
    )
    private data class Sources(
        @JsonProperty("streams") val streams: List<String> = emptyList()
    )
    private var cache: List<FamelackChannel>? = null

    private suspend fun channels(): List<FamelackChannel> {
        cache?.let { return it }
        val result = try {
            parseJson<List<FamelackChannel>>(app.get(DATA_URL, timeout = 30).text)
                .filter { it.country.equals("in", true) || it.country.equals("india", true) }
                .filter { it.name.isNotBlank() && !it.sources?.streams.isNullOrEmpty() }
        } catch (_: Exception) { emptyList() }
        cache = result
        return result
    }

    private fun FamelackChannel.toSearch(): SearchResponse =
        newLiveSearchResponse(name, toJson(), TvType.Live) { posterUrl = logo }

    override suspend fun getMainPage(page: Int, request: MainPageRequest) =
        newHomePageResponse(
            listOf(HomePageList("🇮🇳 India Live TV", channels().map { it.toSearch() })),
            hasNext = false
        )

    override suspend fun search(query: String): List<SearchResponse> {
        val q = query.trim().lowercase()
        return channels().filter { it.name.lowercase().contains(q) }.map { it.toSearch() }
    }

    override suspend fun load(url: String): LoadResponse {
        val channel = parseJson<FamelackChannel>(url)
        val stream = channel.sources?.streams?.firstOrNull().orEmpty()
        return newLiveStreamLoadResponse(channel.name, url, stream) {
            posterUrl = channel.logo
            plot = "Live TV • India"
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val channel = parseJson<FamelackChannel>(data)
        val streams = channel.sources?.streams.orEmpty()
        for (stream in streams) {
            try {
                M3u8Helper.generateM3u8(channel.name, stream, "").forEach(callback)
            } catch (_: Exception) {
                callback(newExtractorLink(channel.name, channel.name, stream, ExtractorLinkType.M3U8))
            }
        }
        return streams.isNotEmpty()
    }
}
