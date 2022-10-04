package com.plcoding.stockmarketapp.data.mapper

import com.plcoding.stockmarketapp.data.dto.CompanyInfoDto
import com.plcoding.stockmarketapp.data.local.CompanyListingEntity
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing

fun CompanyListingEntity.toCompanyList(): CompanyListing {
    return CompanyListing(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyListing.toCompanyListEntity(): CompanyListingEntity {
    return CompanyListingEntity(
        name = name,
        symbol = symbol,
        exchange = exchange
    )
}

fun CompanyInfoDto.toCompanyInfo(): CompanyInfo {
    return CompanyInfo(
        name = name ?: "",
        symbol = symbol ?: "",
        description = description ?: "",
        country = country ?: "",
        industry = industry ?: ""
    )
}
