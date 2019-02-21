package com.plumbaria.e_10_5_puntuacionesjson

import android.util.Log
import org.apache.http.NameValuePair
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.impl.client.DefaultHttpClient
import java.io.*

class JSONManager {
    companion object {
        var inputStream: InputStream? = null
        var jsonString = ""
    }

    fun getJsonString(
        url: String, method: String,
        params: List<NameValuePair>?
    ): String {
        var urlParams: String = url
        try {
            if (method == "POST") {
                val httpClient = DefaultHttpClient()
                val httpPost = HttpPost(url)
                httpPost.setEntity(UrlEncodedFormEntity(params))
                val httpResponse = httpClient.execute(httpPost)
                val httpEntity = httpResponse.getEntity()
                inputStream = httpEntity.getContent()
            } else if (method == "GET") {
                val httpClient = DefaultHttpClient()
                if (params != null) {
                    val paramString = URLEncodedUtils.format(params, "utf-8")
                    urlParams += "?" + paramString
                }
                val httpGet = HttpGet(urlParams)
                val httpResponse = httpClient.execute(httpGet)
                val httpEntity = httpResponse.getEntity()
                inputStream = httpEntity.getContent()
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            val reader = BufferedReader(
                InputStreamReader(
                    inputStream, "iso-8859-1"
                ), 8
            )
            val stringBuilder = StringBuilder()
            var linea: String? = null
            while ({ linea = reader.readLine(); linea }() != null) {
                stringBuilder.append(linea + "\n");
            }
            inputStream!!.close()
            jsonString = stringBuilder.toString()
        } catch (e: Exception) {
            Log.e("Error", "Error obteniendo JSON " + e.toString())
        }
        return jsonString
    }
}