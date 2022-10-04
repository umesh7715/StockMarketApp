package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyListingParser @Inject constructor() : CSVParser<CompanyListing> {

    override suspend fun parse(inputStream: InputStream): List<CompanyListing> {
        val csvReader = CSVReader(InputStreamReader(inputStream))

        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { lineArray ->
                    val name = lineArray[1]
                    val symbol = lineArray[0]
                    val exchange = lineArray[2]

                    CompanyListing(
                        name = name ?: return@mapNotNull null,
                        symbol = symbol ?: return@mapNotNull null,
                        exchange = exchange ?: return@mapNotNull null
                    )
                }.also {
                    csvReader.close()
                }
        }
    }
}