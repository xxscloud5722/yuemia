@file:Suppress("DuplicatedCode", "UNCHECKED_CAST", "unused")

package com.xxscloud.sdk.yuemia

import com.fasterxml.jackson.databind.ObjectMapper
import okio.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * 微信公众号.
 * @property token YMWeChatToken
 * @constructor
 */
class YMWeChat(private val token: YMWeChatToken) : YMBase(token) {
    companion object {
        private const val URL = "https://wechat.yuemia.com"
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
    }

    /**
     * 设置用户标签.
     * @param openId String ID.
     * @param tagId String 标签ID.
     * @return Boolean 是否成功.
     */
    fun setUserLabels(openId: String, tagId: String): Boolean {
        val request = HashMap<String, String>()
        request["openid"] = openId
        request["tagid"] = tagId
        val response = HttpClient.post("${URL}/api/bindusertag", token, request)
        return response.code == 200
    }

    /**
     * 创建标签啊.
     * @param tagName String 标签名称.
     * @return String 标签ID, 空字符串表示失败
     */
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

    /**
     * 创建二维码，响应的是文章.
     * @param articleList List<YMArticle> 文章内容.
     * @return String 返回二维码地址.
     */
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

    /**
     * 创建二维码, 响应文本.
     * @param text String 文本内容.
     * @return String 返回二维码地址.
     */
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

    /**
     * 创建二维码, 响应图片.
     * @param link String 点击图片跳转地址.
     * @param mediaId String 资源ID.
     * @return String 返回二维码地址.
     */
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

    /**
     * 发送消息.
     * @param openIdList List<String> 用户ID.
     * @param templateId String 模板ID.
     * @param args HashMap<String, String> 消息参数.
     * @param link String 跳转地址.
     * @return Boolean 是否成功.
     */
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

    /**
     * 获取用户信息.
     * @param openId String 用户ID.
     * @return HashMap<String, Any> 用户信息.
     */
    fun getUserInfo(openId: String): HashMap<String, Any> {
        val request = HashMap<String, String>()
        request["openid"] = openId
        val response = HttpClient.post("${URL}/api/getuserdetail", token, request)
        if (response.code != 200) {
            throw IOException(response.body?.string() ?: "")
        }
        return OBJECT_MAPPER.readValue(response.body?.string() ?: "{}", HashMap::class.java) as HashMap<String, Any>
    }
}