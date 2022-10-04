package com.plcoding.stockmarketapp.presentation.stock_listing.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.stockmarketapp.domain.model.CompanyListing

@Composable
fun CompanyItemList(
    item: CompanyListing,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colors.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    maxLines = 1

                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = item.exchange,
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.onBackground,

                    )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = item.symbol,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}