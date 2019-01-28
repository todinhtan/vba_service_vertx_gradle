package com.scratch.vba

import com.scratch.vba.connection.VbaConnection
import com.scratch.vba.controller.VbaController
import com.zandero.rest.RestRouter
import io.reactivex.Completable
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.core.Vertx

class VbaVerticle: AbstractVerticle() {

    override fun rxStart(): Completable {
        val vertx = Vertx.vertx()
        val router = RestRouter.register(vertx.delegate, VbaController())
        return vertx.createHttpServer()
            .requestHandler {
                (router::accept)(it.delegate)
            }
            .rxListen(config().getInteger("app.port", 8080))
            .doOnSuccess {
                VbaConnection.connect(vertx, json {
                    obj(
                        "db_name" to config().getString("mongo.dbName", "vba_service"),
                        "connection_string" to config().getString("mongo.connectionString", "mongodb://vba_user:112233aassdd@ds157223.mlab.com:57223/vba_service")
                    )
                })
            }.ignoreElement()
    }
}