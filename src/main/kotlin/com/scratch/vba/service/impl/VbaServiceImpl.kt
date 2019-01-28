package com.scratch.vba.service.impl

import com.google.gson.Gson
import com.scratch.vba.connection.VbaConnection
import com.scratch.vba.model.Status
import com.scratch.vba.model.VbaModel
import com.scratch.vba.service.VbaService
import io.reactivex.Single
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj

object VbaServiceImpl: VbaService {
    private const val COLLECTION_NAME = "vbarequests"
    override fun loadVBAs(walletId: String): Single<List<VbaModel>> {
        return try {
            VbaConnection.vbaClient.rxFind(COLLECTION_NAME, json {
                obj( "walletId" to walletId )
            }).map {
                it.map { jsonModel -> Gson().fromJson(jsonModel.encode(), VbaModel::class.java) }
            }
        } catch (e: Exception) {
            Single.just(listOf())
        }
    }

    override fun createVBA(vba: VbaModel): Single<String> {
        return try {
            VbaConnection.vbaClient.rxSave(
                COLLECTION_NAME,
                JsonObject.mapFrom(vba)
            ).onErrorReturn {
                ""
            }
        } catch (e: Exception) {
            Single.just("")
        }
    }

    override fun updateVBA(vba: VbaModel): Single<JsonObject?> {
        return try {
            VbaConnection.vbaClient.rxFindOneAndUpdate(
                COLLECTION_NAME,
                json {
                    obj(
                        "walletId" to vba.walletId,
                        "country" to vba.country,
                        "status" to json {
                            obj( "\$ne" to Status.APPROVED.value)
                        }
                    )
                },
                json {
                    obj(
                        "\$set" to JsonObject.mapFrom(vba)
                    )
                }
            ).onErrorReturn {
                null
            }
        } catch (e: Exception) {
            Single.just(null)
        }
    }
}