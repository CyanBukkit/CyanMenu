package cn.cyanbukkit.cyanmenu.data

import java.util.Base64

fun String.base64ToOrigin(): String { // 将Base64编码的字符串解码为原始字符串
    return Base64.getDecoder().decode(this).toString(Charsets.UTF_8)
}

fun String.originToBase64(): String { // 将原始字符串编码为Base64字符串
    return Base64.getEncoder().encodeToString(this.toByteArray(Charsets.UTF_8))
}