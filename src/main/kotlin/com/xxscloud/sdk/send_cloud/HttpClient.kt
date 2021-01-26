package com.xxscloud.sdk.send_cloud

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.apache.commons.codec.digest.DigestUtils
import java.util.*
import java.text.SimpleDateFormat


object HttpClient {
    private val CLIENT = OkHttpClient.Builder().build()
    private val JSON_MAPPER: ObjectMapper = ObjectMapper()


    private fun getToken(code: String, password: String): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("yyyyMMddHH")
        val timestamp = simpleDateFormat.format(date)
        val list = ArrayList<String>()
        list.add(code)
        list.add(timestamp)
        list.add(password)
        list.sort()
        val sign = list.joinToString()
        return DigestUtils.sha1Hex(sign) ?: throw RuntimeException("sha1 error")
    }

    fun post(url: String, token: YMWeChatToken, request: Any, type: Int = 1): Response {
        return if (type == 1) {
            val body = JSON_MAPPER.writeValueAsString(request).toRequestBody("application/json".toMediaType())
            CLIENT.newCall(Request.Builder().url(getUrl(url, token)).post(body).header("Content-Type", "application/json").build()).execute()
        } else {
            val body = FormBody.Builder()
            CLIENT.newCall(Request.Builder().url(getUrl(url, token)).post(body.build()).build()).execute()
        }
    }

    fun get(url: String, token: YMWeChatToken): Response {
        return CLIENT.newCall(Request.Builder().url(getUrl(url, token)).get().build()).execute()
    }

    private fun getUrl(url: String, token: YMWeChatToken): String {
        val baseUrl = "wx=${token.code}&token=${getToken(token.code, token.password)}"
        return if (url.contains("?")) {
            if (url.endsWith("&")) "${url}${baseUrl}" else "${url}&${baseUrl}"
        } else {
            "${url}?${baseUrl}"
        }
    }
}