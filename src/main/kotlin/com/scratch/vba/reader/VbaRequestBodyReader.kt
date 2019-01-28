package com.scratch.vba.reader

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.scratch.vba.model.VbaModel
import com.scratch.vba.http.VbaRequest
import com.zandero.rest.reader.ValueReader

class VbaRequestBodyReader: ValueReader<VbaRequest> {
    override fun read(value: String?, type: Class<VbaRequest>?): VbaRequest? {
        return try {
            if (!value.isNullOrEmpty()) {
                val body = Gson().fromJson(value, JsonObject::class.java)
                val vba = body?.getAsJsonObject("vba")
                VbaRequest().apply {
                    vbaList = vba?.getAsJsonArray("countries")?.toSet()?.map {
                        Gson().fromJson(vba, VbaModel::class.java).apply {
                            country = it.asString
                        }
                    }
                }

            } else null
        } catch (e: Exception) {
            null
        }
    }
}