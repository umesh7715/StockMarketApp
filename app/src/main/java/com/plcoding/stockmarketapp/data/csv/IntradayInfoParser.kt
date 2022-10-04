package com.plcoding.stockmarketapp.data.csv

import com.opencsv.CSVReader
import com.plcoding.stockmarketapp.data.dto.IntradayInfoDto
import com.plcoding.stockmarketapp.data.mapper.toIntradayInfo
import com.plcoding.stockmarketapp.domain.model.CompanyListing
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.InputStreamReader
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
class IntradayInfoParser @Inject constructor() : CSVParser<IntradayInfo> {

    override suspend fun parse(inputStream: InputStream): List<IntradayInfo> {
        val csvReader = CSVReader(InputStreamReader(inputStream))

        return withContext(Dispatchers.IO) {
            csvReader
                .readAll()
                .drop(1)
                .mapNotNull { lineArray ->
                    val timestamp = lineArray[0] ?: return@mapNotNull null
                    val close = lineArray[4] ?: return@mapNotNull null

                    val dto = IntradayInfoDto(
                        time = timestamp,
                        close = close.toDouble()
                    )
                    dto.toIntradayInfo()

                }.filter {
                    it.date.dayOfMonth == LocalDateTime.now().minusDays(1).dayOfMonth
                }
                .also {
                    csvReader.close()
                }
        }
    }
}