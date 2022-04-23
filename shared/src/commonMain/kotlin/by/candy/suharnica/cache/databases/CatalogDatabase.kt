package by.candy.suharnica.cache.databases

import by.candy.suharnica.cache.DatabaseDriverFactory
import by.candy.suharnica.core.dataSource.database.CandyDatabase
import by.candy.suharnica.entity.CatalogItem
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CatalogDatabase(database: CandyDatabase) {

    private val dbQuery = database.catalogItemQueries

    internal fun clearDatabase() {
        dbQuery.transaction {
            dbQuery.removeAllItems()
        }
    }

    internal suspend fun fillDB(items: List<CatalogItem>) {
        withContext(Dispatchers.Default) {
            items.forEach {
                dbQuery.insertItem(
                    id = it.id.toLong(),
                    label = it.label,
                    type = it.type,
                    weight = it.weight,
                    imgUrl = it.imgUrl,
                    price = it.price,
                    priceSale = it.priceSale,
                    likes = it.likes,
                    about = it.about,
                    productComposition = it.productComposition,
                    nutritionalValue = it.nutritionalValue
                )
            }
        }
    }

    internal fun getAllLaunches(): Flow<List<CatalogItem>> {
        return dbQuery.getAll(::mapCatalog).asFlow().mapToList()
    }

    internal fun getItemFromId(id: Long) = dbQuery.getItemFromId(id).executeAsOne()

    private fun mapCatalog(
        id: Long,
        label: String,
        type: String,
        weight: Int,
        imgUrl: List<String>,
        price: Double,
        priceSale: Double,
        likes: Int,
        about: String,
        productComposition: List<String>,
        nutritionalValue: List<String>
    ): CatalogItem {
        return CatalogItem(
            id = id.toInt(),
            label = label,
            type = type,
            weight = weight,
            imgUrl = imgUrl,
            price = price,
            priceSale = priceSale,
            likes = likes,
            about = about,
            productComposition = productComposition,
            nutritionalValue = nutritionalValue
        )
    }
}