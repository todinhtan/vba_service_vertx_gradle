package com.scratch.vba.connection

import io.reactivex.Completable
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.ext.mongo.IndexOptions
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.mongo.MongoClient

object VbaConnection {
    lateinit var vbaClient: MongoClient

    fun connect(vertx: Vertx, config: JsonObject) {
        vbaClient = MongoClient.createShared(vertx, config)
        vbaClient.ensureWalletIndex().subscribe()
        vbaClient.ensureWalletCountryIndex().subscribe()
    }
}

fun MongoClient.ensureWalletIndex(): Completable {
    return this.rxCreateIndexWithOptions("vbarequests", json {
        obj(
            "walletId" to 1
        )
    }, IndexOptions())
}

fun MongoClient.ensureWalletCountryIndex(): Completable {
    return this.rxCreateIndexWithOptions("vbarequests", json {
        obj(
            "walletId" to 1,
            "country" to 1
        )
    }, IndexOptions().unique(true))
}