package com.scratch.vba

import io.vertx.reactivex.core.RxHelper
import io.vertx.reactivex.core.Vertx

class VbaApp {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val vertx = Vertx.vertx()
            RxHelper.deployVerticle(vertx, VbaVerticle())
                .subscribe({
                    println(it)
                }, {
                    vertx.close()
                })
        }

    }
}