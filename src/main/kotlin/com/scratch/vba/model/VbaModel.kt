package com.scratch.vba.model

import io.vertx.core.json.JsonObject
import java.util.*

data class VbaModel(
    var walletId: String
) {
    var country: String = "US"
    var email: String = ""
    var phoneNumber: String = ""
    var entityType: EntityType? = null
    var companyNameCn: String = ""
    var companyNameEn: String = ""
    var registrationNumber: String? = null
    var dateOfEstablishment: Long? = null
    var beneficialOwners: JsonObject? = null
    var repAddress: AddressModel? = null
    var entityScope: String = "Shopping/Retail"
    var ip: String = ""
    var nameCn: String = ""
    var nameEn: String = ""
    var dateOfBirth: Long = -1
    var idNumber: String = ""
    var merchantIds: List<MerchantIdModel> = listOf()
    var shopName: String = ""
    var website: String = ""
    var address: AddressModel = AddressModel()
    var expectedMonthlySales: String? = null
    var idDoc: String = ""
    var coiDoc: String? = null
    var salesDoc: String? = null
    var status: Status = Status.PENDING

    constructor(): this(
        ""
    )

    fun validate(): Set<String> {
        val errors = mutableListOf<String>()

        if (email.isEmpty()) errors.add("email is required.")
        if (phoneNumber.isEmpty()) errors.add("phoneNumber is required.")
        if (entityType == null) errors.add("Invalid entityType, must be one of (CORP, M, F).")
        if (entityScope != "Shopping/Retail") errors.add("entityScope must be \"Shopping/Retail\".")
        if (ip.isEmpty()) errors.add("ip is required.")
        if (nameCn.isEmpty() && this.nameEn.isEmpty()) errors.add("nameCn or nameEn is required.")
        if (dateOfBirth < 0 || !isValidBirth(this.dateOfBirth)) errors.add("Invalid dateOfBirth, age must be between 18 and 120.")
        if (idNumber.isEmpty()) errors.add("idNumber is required.")
        errors.addAll(this.address.validate().toMutableList())
        if (idDoc.isEmpty()) errors.add("idDoc is required.")

        if (EntityType.CORP == entityType) {
            if (repAddress == null) errors.add("repAddress is required for CORP")
            else errors.addAll(repAddress!!.validate().toMutableList())
            if (registrationNumber.isNullOrEmpty()) errors.add("registrationNumber is required for CORP")
            if (dateOfEstablishment == null) errors.add("dateOfEstablishment is required for CORP")
            if (companyNameCn.isEmpty() && companyNameEn.isEmpty()) errors.add("companyNameCn or companyNameEn is required for CORP.")
            if (country == "HK" && beneficialOwners == null) errors.add("beneficialOwners is required for CORP and country HK.")
            if (coiDoc.isNullOrEmpty()) errors.add("coiDoc is required for CORP")
        }

        return errors.toSet()
    }
}

// check birth is between 18 and 120
private fun isValidBirth(unixTime: Long): Boolean {
    return Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis() - unixTime
    }.get(Calendar.YEAR) - 1970 in 18..120
}

enum class EntityType(val value: String) {
    M("M"),
    F("F"),
    CORP("CORP");

    override fun toString() = value
}

enum class Status(val value: String) {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    DENIED("DENIED");

    override fun toString() = value
}