package com.scratch.vba.http

import com.scratch.vba.model.VbaModel

class VbaRequest(
    var vbaList: List<VbaModel>?
) {
    constructor(): this(
        listOf()
    )
}