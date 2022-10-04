package com.plcoding.stockmarketapp.presentation.stock_details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.CompanyListingRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: CompanyListingRepository

) : ViewModel() {

    var state by mutableStateOf(CompanyInfoState())

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            val symbol = savedStateHandle.get<String>("symbol") ?: return@launch
            state = state.copy(isLoading = true)

            val intradayInfoListResult = async { repository.getIntradayInfo(symbol) }
            val companyInfoResult = async { repository.getCompanyInfo(symbol) }

            when (val result = intradayInfoListResult.await()) {
                is Resource.Success -> {
                    state = state.copy(
                        isLoading = false,
                        intradayInfoList = result.data ?: emptyList()
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        errorMessage = result.message,
                    )
                }
            }

            when (val result = companyInfoResult.await()) {
                is Resource.Success -> {
                    state = state.copy(
                        isLoading = false,
                        companyInfo = result.data
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        isLoading = false,
                        errorMessage = result.message,
                    )
                }
            }

        }
    }

}