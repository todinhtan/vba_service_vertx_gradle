package com.scratch.vba.http

import com.scratch.vba.model.VbaModel

class VbaResponse(
    var vba: List<VbaModel>,
    var message: String,
    var errors: List<String>
) {
    constructor(): this(
        listOf(),
        "",
        listOf()
    )
}