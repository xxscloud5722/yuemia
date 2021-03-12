package com.xxscloud.sdk.yuemia

/**
 * 配置实体类.
 * @property code String 注册的用户名
 * @property password String 密码
 * @constructor
 */
data class YMWeChatToken(
        /**
         * 注册的用户名.
         */
        val code: String = "",
        /**
         * 密码默认.
         */
        val password: String = "Yuemia@2018"
)