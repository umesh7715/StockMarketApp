package com.plcoding.stockmarketapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.plcoding.stockmarketapp.util.Resource
import kotlinx.coroutines.flow.Flow


@Dao
interface StockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompanyListing(
        companyListingEntityList: List<CompanyListingEntity>
    )

    @Query("DELETE FROM companylistingentity")
    suspend fun clearListing()

    @Query(
        """
        SELECT * FROM companylistingentity
        WHERE LOWER(name) LIKE '%'||  LOWER(:query)|| '%' OR
        UPPER(:query) == symbol
    """
    )
    suspend fun getStockListing(query: String): List<CompanyListingEntity>
}