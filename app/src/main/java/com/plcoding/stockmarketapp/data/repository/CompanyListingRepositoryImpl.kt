package com.plcoding.stockmarketapp.data.repository

import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.csv.CompanyListingParser
import com.plcoding.stockmarketapp.data.local.StockDao
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.toCompanyInfo
import com.plcoding.stockmarketapp.data.mapper.toCompanyList
import com.plcoding.stockmarketapp.data.mapper.toCompanyListEntity
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import com.plcoding.stockmarketapp.domain.repository.CompanyListingRepository
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingRepositoryImpl @Inject constructor(
    private val api: StockApi,
    private val db: StockDatabase,
    private val companyListingParser: CSVParser<CompanyListing>,
    private val intradayInfoParser: CSVParser<IntradayInfo>
) : CompanyListingRepository {

    val dao = db.stockDao

    override suspend fun getCompanyListing(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListing>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListing = dao.getStockListing(query)
            emit(
                Resource.Success(
                    data = localListing.map { it.toCompanyList() }
                )
            )

            val isDbEmpty = localListing.isEmpty() && query.isBlank()
            val shouldJustFetchFromCache = !isDbEmpty && !fetchFromRemote
            if (shouldJustFetchFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListing = try {
                val response = api.getStockListing()
                companyListingParser.parse(response.byteStream())
            } catch (e: IOException) {
                emit(Resource.Error(message = "Parsing error"))
                null
            } catch (e: HttpException) {
                emit(Resource.Error(message = "Please check your internet connection"))
                null
            } catch (e: Exception) {
                emit(Resource.Error(message = "Something went wrong..!"))
                null
            }

            remoteListing?.let { listings ->
                dao.clearListing()
                dao.insertCompanyListing(
                    listings.map { it.toCompanyListEntity() }
                )
                val localListing = dao.getStockListing("")
                emit(
                    Resource.Success(
                        localListing.map { it.toCompanyList() }
                    )
                )
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getIntradayInfo(symbol: String): Resource<List<IntradayInfo>> {
        return try {
            val response = api.getIntradayInfo(symbol)
            val list = intradayInfoParser.parse(response.byteStream())
            Resource.Success(list)
        } catch (exception: IOException) {
            exception.printStackTrace()
            Resource.Error("Something went wrong")
        } catch (exception: HttpException) {
            exception.printStackTrace()
            Resource.Error("Please check your internet connection")
        }
    }

    override suspend fun getCompanyInfo(symbol: String): Resource<CompanyInfo> {
        return try {
            val response = api.getCompanyInfo(symbol).toCompanyInfo()
            Resource.Success(response)
        } catch (exception: IOException) {
            exception.printStackTrace()
            Resource.Error("Something went wrong")
        } catch (exception: HttpException) {
            exception.printStackTrace()
            Resource.Error("Please check your internet connection")
        }
    }
}
