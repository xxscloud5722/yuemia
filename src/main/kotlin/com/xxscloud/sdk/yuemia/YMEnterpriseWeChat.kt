@file:Suppress("DuplicatedCode", "UNCHECKED_CAST", "unused")

package com.xxscloud.sdk.yuemia

import com.fasterxml.jackson.databind.ObjectMapper
import okio.IOException
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.collections.HashMap

/**
 * 微信企业号.
 * @property token YMWeChatToken
 * @constructor
 */
class YMEnterpriseWeChat(private val token: YMWeChatToken) : YMBase(token) {
    companion object {
        private val log = LoggerFactory.getLogger(YMEnterpriseWeChat::class.java)
        private const val URL = "https://wechat.yuemia.com"
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
    }


    /**
     * 发送消息.
     * @param userId String 用户ID.
     * @param agentId String 企业微信的AgentId.
     * @param title String 标题.
     * @param content String 正文.
     * @param url String 跳转地址.
     * @return Boolean 是否成功.
     */
    fun sendMessage(userId: String, agentId: String, title: String, content: String, url: String): Boolean {
        val request = HashMap<String, String>()
        request["users"] = userId
        request["agentid"] = agentId
        request["title"] = title
        request["content"] = content
        request["url"] = url
        request["msgtype"] = "textcard"
        val response = HttpClient.post("${URL}/api/sendworkmsg", token, request)
        val responseBody = response.body?.string() ?: ""
        if (response.code != 200) {
            throw IOException(responseBody)
        }
        val result = OBJECT_MAPPER.readValue(responseBody, HashMap::class.java)
        if (result["r"].toString().trim() != "0") {
            throw IOException(responseBody)
        }
        return true
    }

    /**
     * 发送消息 (文件不能超过10MB).
     * @param userId String 用户ID.
     * @param agentId String 企业微信的AgentId.
     * @param file File 文件地址.
     * @return Boolean 是否成功.
     */
    fun sendMessage(userId: String, agentId: String, file: File): Boolean {
        val mediaId = this.uploadFile(file)
        val request = HashMap<String, String>()
        request["users"] = userId
        request["agentid"] = agentId
        request["msgtype"] = "file"
        request["mediaid"] = mediaId
        val response = HttpClient.post("${URL}/api/sendworkmsg", token, request)
        val responseBody = response.body?.string() ?: ""
        if (response.code != 200) {
            throw IOException(responseBody)
        }
        val result = OBJECT_MAPPER.readValue(responseBody, HashMap::class.java)
        if (result["r"].toString().trim() != "0") {
            throw IOException(responseBody)
        }
        return true
    }

    /**
     * 获取用户信息.
     * @param userId String 用户ID.
     * @return HashMap<String, Any> 用户信息.
     */
    fun getUserInfo(userId: String): HashMap<String, Any> {
        val response = HttpClient.get("${URL}/workapi/workuser?userid=${userId}", token)
        return OBJECT_MAPPER.readValue(response.body?.string() ?: "{}", HashMap::class.java) as HashMap<String, Any>
    }
}