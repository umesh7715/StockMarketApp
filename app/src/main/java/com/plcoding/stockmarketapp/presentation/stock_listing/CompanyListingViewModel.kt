package com.plcoding.stockmarketapp.presentation.stock_listing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.CompanyListingRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CompanyListingViewModel @Inject constructor(
    private val companyListingRepository: CompanyListingRepository
) : ViewModel() {

    var state by mutableStateOf(CompanyListingState())
    val searchJob: Job? = null

    init {
        getCompanyListing()
    }

    fun onEvent(event: CompanyListingEvent) {
        when (event) {
            is CompanyListingEvent.Refresh -> {
                getCompanyListing(shouldFetchFromRemote = true)
            }
            is CompanyListingEvent.OnSearchQueryChange -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                viewModelScope.launch {
                    delay(500L)
                    getCompanyListing()
                }
            }
        }
    }

    private fun getCompanyListing(
        query: String = state.searchQuery.lowercase(),
        shouldFetchFromRemote: Boolean = false
    ) {

        viewModelScope.launch {
            companyListingRepository
                .getCompanyListing(shouldFetchFromRemote, query)
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                state = state.copy(
                                    companyListing = it
                                )
                            }
                        }
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }

                }
        }

    }

}