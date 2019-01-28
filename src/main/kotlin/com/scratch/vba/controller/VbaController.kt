package com.scratch.vba.controller

import com.scratch.vba.http.VbaRequest
import com.scratch.vba.http.VbaResponse
import com.scratch.vba.reader.VbaRequestBodyReader
import com.scratch.vba.service.impl.VbaServiceImpl
import com.zandero.rest.annotation.Get
import com.zandero.rest.annotation.Post
import com.zandero.rest.annotation.Put
import com.zandero.rest.annotation.RequestReader
import io.netty.handler.codec.http.HttpResponseStatus
import io.reactivex.Single
import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType

@Path("/")
class VbaController {

    @Get
    @Path("/vba/:walletId")
    @Produces(MediaType.APPLICATION_JSON)
    fun get(@PathParam("walletId") walletId: String,
            @Context response: io.vertx.core.http.HttpServerResponse): VbaResponse {
        return try {
            val results = VbaServiceImpl.loadVBAs(walletId).blockingGet()
            if (results.isNullOrEmpty()) response.statusCode = HttpResponseStatus.NOT_FOUND.code()
            VbaResponse().apply {
                vba = results
            }
        } catch (e: Exception) {
            response.statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            VbaResponse().apply {
                message = HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()
            }
        }
    }

    @Post
    @Path("/vba/:walletId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun post(@PathParam("walletId") walletId: String,
             @RequestReader(VbaRequestBodyReader::class) vbaBody: VbaRequest,
             @Context response: io.vertx.core.http.HttpServerResponse): VbaResponse {
        return try {
            val vbaList = vbaBody.vbaList!!.map {
                it.walletId = walletId
                it
            }

            val errors = vbaList.flatMap { it.validate() }.toSet()

            return if (errors.isNotEmpty()) {
                response.statusCode = HttpResponseStatus.BAD_REQUEST.code()
                VbaResponse().apply {
                    this.message = "Invalid request"
                    this.errors = errors.toList()
                }
            } else {
                var message = ""; var countSuccess = 0
                Single.zip(vbaBody.vbaList!!.map { vbaModel ->
                    VbaServiceImpl.createVBA(vbaModel)
                }) {
                    it.map{ result ->
                        if (result.toString().isNotEmpty()) countSuccess += 1
                    }
                }.doFinally {
                    // create message
                    if (countSuccess == 0) {
                        response.statusCode = HttpResponseStatus.NOT_ACCEPTABLE.code()
                        message += "No VBA's request created."
                    } else {
                        message += "Created $countSuccess VBA's request(s) successful."
                    }
                }.blockingGet()

                VbaResponse().apply {
                    this.message = message
                }
            }
        } catch (e: Exception) {
            println(e.printStackTrace())
            response.statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            VbaResponse().apply {
                message = HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()
            }
        }
    }

    @Put
    @Path("/vba/:walletId")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun put(@PathParam("walletId") walletId: String,
            @RequestReader(VbaRequestBodyReader::class) vbaBody: VbaRequest?,
            @Context response: io.vertx.core.http.HttpServerResponse): VbaResponse {
        return try {
            val vbaList = vbaBody?.vbaList!!.map {
                it.walletId = walletId
                it
            }

            val errors = vbaList.flatMap { it.validate() }.toSet()

            return if (errors.isNotEmpty()) {
                response.statusCode = HttpResponseStatus.BAD_REQUEST.code()
                VbaResponse().apply {
                    this.message = "Invalid request"
                    this.errors = errors.toList()
                }
            } else {
                var message = ""; var countSuccess = 0
                Single.zip(vbaBody.vbaList!!.map { vbaModel ->
                    VbaServiceImpl.updateVBA(vbaModel)
                }) {
                    it.map{ result ->
                        if (result != null) countSuccess += 1
                    }
                }.doFinally {
                    // create message
                    if (countSuccess == 0) {
                        response.statusCode = HttpResponseStatus.NOT_ACCEPTABLE.code()
                        message += "No VBA's request updated."
                    } else {
                        message += "Updated $countSuccess VBA's request(s) successful."
                    }
                }.blockingGet()

                VbaResponse().apply {
                    this.message = message
                }
            }
        } catch (e: Exception) {
            println(e.printStackTrace())
            response.statusCode = HttpResponseStatus.INTERNAL_SERVER_ERROR.code()
            VbaResponse().apply {
                message = HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase()
            }
        }
    }
}