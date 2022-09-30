package com.plcoding.stockmarketapp.data.repository

import com.plcoding.stockmarketapp.data.csv.CSVParser
import com.plcoding.stockmarketapp.data.csv.CompanyListingParser
import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mapper.toCompanyList
import com.plcoding.stockmarketapp.data.mapper.toCompanyListEntity
import com.plcoding.stockmarketapp.data.remote.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyListing
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
    val api: StockApi,
    val db: StockDatabase,
    val companyListingParser: CSVParser<CompanyListing>
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
}