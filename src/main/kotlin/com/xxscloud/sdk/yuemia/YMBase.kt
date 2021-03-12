@file:Suppress("UNCHECKED_CAST")

package com.xxscloud.sdk.yuemia

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.io.IOException

/**
 * 悦米SDK父类.
 * @property token YMWeChatToken
 * @constructor
 */
abstract class YMBase(private val token: YMWeChatToken) {
    companion object {
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapper()
        private val IMAGE_TYPE = arrayListOf("jpg", "jpeg", "png", "gif", "webp")
        private val VOICE_TYPE = arrayListOf("mp3")
        private val VIDEO_TYPE = arrayListOf("mp4")
    }

    /**
     * 上传文件.
     * @param file File 文件路径.
     * @return String 返回文件资源ID.
     */
    fun uploadFile(file: File): String {
        val type = getType(file.name)
        val url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?access_token=${getToken()}&type=${type}"
        val response = HttpClient.postFile(url, file)
        val responseBody = response.body?.string() ?: ""
        if (response.code != 200) {
            throw IOException(responseBody)
        }
        val result = OBJECT_MAPPER.readValue(responseBody, HashMap::class.java)
        if (result["errcode"].toString().trim() != "0") {
            throw IOException(responseBody)
        }
        return (result["media_id"] ?: "").toString()
    }

    private fun getType(name: String): String {
        if (name.lastIndexOf(".") < 0) {
            return "file"
        }
        val a = name.substring(name.lastIndexOf(".") + 1)
        if (IMAGE_TYPE.contains(a)) {
            return "image"
        }
        if (VOICE_TYPE.contains(a)) {
            return "voice"
        }
        if (VIDEO_TYPE.contains(a)) {
            return "video"
        }
        return "file"
    }

    private fun getToken(): String {
        val response = HttpClient.get("https://wechat.yuemia.com/api/token", token)
        val responseBody = response.body?.string() ?: "{}"
        val result = OBJECT_MAPPER.readValue(responseBody, HashMap::class.java) as HashMap<String, Any>
        if (result["r"].toString().trim() != "0") {
            throw IOException(responseBody)
        }
        return (result["token"] ?: "").toString()
    }
}