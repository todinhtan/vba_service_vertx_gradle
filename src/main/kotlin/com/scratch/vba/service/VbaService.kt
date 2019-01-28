package com.scratch.vba.service

import com.scratch.vba.model.VbaModel
import io.reactivex.Single
import io.vertx.core.json.JsonObject

interface VbaService {
    fun loadVBAs(walletId: String): Single<List<VbaModel>>
    fun createVBA(vba: VbaModel): Single<String>
    fun updateVBA(vba: VbaModel): Single<JsonObject?>
}