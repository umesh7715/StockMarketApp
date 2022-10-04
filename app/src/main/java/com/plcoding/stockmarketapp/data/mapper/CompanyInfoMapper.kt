package com.plcoding.stockmarketapp.data.mapper

import com.plcoding.stockmarketapp.data.dto.IntradayInfoDto
import com.plcoding.stockmarketapp.domain.model.IntradayInfo
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

fun IntradayInfoDto.toIntradayInfo(): IntradayInfo {

    val pattern = "yyyy-MM-dd HH:mm:ss"
    val fomratter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
    val localDateTime = LocalDateTime.parse(time, fomratter)

    return IntradayInfo(
        date = localDateTime,
        close = close
    )
}