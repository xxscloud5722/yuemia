@file:Suppress("DuplicatedCode")

package com.xxscloud.sdk.send_cloud

import com.fasterxml.jackson.databind.ObjectMapper
import okio.IOException
import org.slf4j.LoggerFactory
import kotlin.collections.HashMap

class YMEnterpriseWeChat(private val token: YMWeChatToken) {
    companion object {
        private val log = LoggerFactory.getLogger(YMEnterpriseWeChat::class.java)
        private const val URL = "https://wechat.yuemia.com"
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
    }

    fun sendMessage(userId: String, agentId: String, title: String, content: String, url: String): Boolean {
        val request = HashMap<String, String>()
        request["users"] = userId
        request["agentid"] = agentId
        request["title"] = title
        request["content"] = content
        request["url"] = url
        val response = HttpClient.post("${URL}/api/sendworkmsg", token, request)
        return response.code != 200
    }

    fun sendMessage(userId: String): HashMap<*, *>? {
        val response = HttpClient.get("${URL}/workapi/workuser?userid=${userId}", token)
        return OBJECT_MAPPER.readValue(response.body?.string() ?: "{}", HashMap::class.java)
    }
}