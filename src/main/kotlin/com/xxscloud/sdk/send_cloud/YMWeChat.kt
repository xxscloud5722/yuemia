@file:Suppress("DuplicatedCode")

package com.xxscloud.sdk.send_cloud

import com.fasterxml.jackson.databind.ObjectMapper
import okio.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class YMWeChat(private val token: YMWeChatToken) {
    companion object {
        private const val URL = "https://wechat.yuemia.com"
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
    }

    fun setUserLabels(openId: String, tagId: String): Boolean {
        val request = HashMap<String, String>()
        request["openid"] = openId
        request["tagid"] = tagId
        val response = HttpClient.post("${URL}/api/bindusertag", token, request)
        return response.code == 200
    }

    fun createLabel(tagName: String): String {
        val request = HashMap<String, String>()
        request["tagname"] = tagName
        val response = HttpClient.post("${URL}/api/createtag", token, request)
        if (response.code != 200) {
            throw IOException(response.body?.string() ?: "")
        }
        val result = OBJECT_MAPPER.readValue(response.body?.string() ?: "{}", HashMap::class.java)
        return (result["tagid"] ?: "").toString()
    }

    fun createArticleQR(articleList: List<YMArticle>): String {
        val request = HashMap<String, Any>()
        request["response_type"] = "artical"
        request["param"] = UUID.randomUUID().toString().replace("-", "")
        val items = ArrayList<HashMap<String, String>>()
        articleList.forEach {
            val item = HashMap<String, String>()
            item["title"] = it.title
            item["img"] = it.cover
            item["artical_desc"] = it.desc
            item["url"] = it.url
            items.add(item)
        }
        request["articals"] = items
        val response = HttpClient.post("${URL}/api/createqrcode", token, request)
        if (response.code != 200) {
            throw IOException(response.body?.string() ?: "")
        }
        val result = OBJECT_MAPPER.readValue(response.body?.string() ?: "{}", HashMap::class.java)
        return result["url"].toString()
    }

    fun createTextQR(text: String): String {
        val request = HashMap<String, Any>()
        request["response_type"] = "text"
        request["param"] = UUID.randomUUID().toString().replace("-", "")
        request["response_text"] = text
        val response = HttpClient.post("${URL}/api/createqrcode", token, request)
        if (response.code != 200) {
            throw IOException(response.body?.string() ?: "")
        }
        val result = OBJECT_MAPPER.readValue(response.body?.string() ?: "{}", HashMap::class.java)
        return result["url"].toString()
    }

    fun createImageQR(link: String, mediaId: String): String {
        val request = HashMap<String, Any>()
        request["response_type"] = "image"
        request["link"] = link
        request["media_id"] = mediaId
        val response = HttpClient.post("${URL}/api/createqrcode", token, request)
        if (response.code != 200) {
            throw IOException(response.body?.string() ?: "")
        }
        val result = OBJECT_MAPPER.readValue(response.body?.string() ?: "{}", HashMap::class.java)
        return result["url"].toString()
    }

    fun sendMessage(openIdList: List<String>, templateId: String, args: HashMap<String, String>, link: String): Boolean {
        val items = ArrayList<HashMap<String, Any>>()
        args.forEach { (k, v) ->
            val item = HashMap<String, Any>()
            item["key"] = k
            item["value"] = v
            items.add(item)
        }
        val requestBody = HashMap<String, Any>()
        requestBody["tempid"] = templateId
        requestBody["items"] = items
        requestBody["url"] = link
        val request = HashMap<String, Any>()
        request["data"] = requestBody
        request["openids"] = openIdList
        val response = HttpClient.post("${URL}/api/sendtempmsg", token, request)
        return response.code != 200
    }

    fun getUserInfo(openId: String): HashMap<*, *> {
        val request = HashMap<String, String>()
        request["openid"] = openId
        val response = HttpClient.post("${URL}/api/getuserdetail", token, request)
        if (response.code != 200) {
            throw IOException(response.body?.string() ?: "")
        }
        return OBJECT_MAPPER.readValue(response.body?.string() ?: "{}", HashMap::class.java)
    }


}