package com.plcoding.stockmarketapp.presentation.stock_details

import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.IntradayInfo

data class CompanyInfoState(
    val intradayInfoList: List<IntradayInfo> = emptyList(),
    val companyInfo: CompanyInfo? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
}