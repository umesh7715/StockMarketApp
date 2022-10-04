package com.plcoding.stockmarketapp.presentation.stock_listing

sealed class CompanyListingEvent {

    object Refresh : CompanyListingEvent()
    data class OnSearchQueryChange(val query: String) : CompanyListingEvent()
}