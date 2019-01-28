package com.scratch.vba.model

data class AddressModel(
    val street1: String?,
    val street2: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val country: String?
) {
    constructor(): this(
        "",
        "",
        "",
        "",
        "",
        ""
    )

    fun validate(): Set<String> {
        val errors = mutableListOf<String>()

        if (street1.isNullOrEmpty()) errors.add("address.street1 is required.")
        if (street2.isNullOrEmpty()) errors.add("address.street2 is required.")
        if (city.isNullOrEmpty()) errors.add("address.city is required.")
        if (state.isNullOrEmpty()) errors.add("address.state is required.")
        if (postalCode.isNullOrEmpty()) errors.add("address.postalCode is required.")
        if (country.isNullOrEmpty()) errors.add("address.country is required.")

        return errors.toSet()
    }
}