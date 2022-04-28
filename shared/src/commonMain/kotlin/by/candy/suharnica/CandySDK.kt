package by.candy.suharnica

import by.candy.suharnica.cache.DatabaseDriverFactory
import by.candy.suharnica.cache.databases.Database
import by.candy.suharnica.cache.databases.OnBasketMode
import by.candy.suharnica.entity.CatalogItem
import by.candy.suharnica.network.CandySuharnicaAPI
import kotlinx.coroutines.flow.*

class CandySDK(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = Database(databaseDriverFactory)
    private val api = CandySuharnicaAPI()

    @Throws(Exception::class)
    fun getCatalogList(type: String, sort: String): Flow<List<CatalogItem>> {

        return flow {
            val cachedLaunches =
                database.catalogDatabase.getAllItems(type = if (type == "Все") "%"
                                                    else type,
                    sort = "$sort%").first()

            val catalogItems = api.getAllLaunches()

            if (cachedLaunches.isNotEmpty())
                catalogItems.ifEmpty {
                    cachedLaunches
                }
            else database.catalogDatabase.fillDB(items = catalogItems)

            emit(cachedLaunches)
        }

    }

    fun getItemFromId(id: Long) = database.catalogDatabase.getItemFromId(id)

    fun getBasket() = database.basketDatabase.getBasket()

    suspend fun addItem(id: Long, mode: OnBasketMode) {
        database.basketDatabase.onItemIntoDatabase(id, mode)
    }

    fun getCount(idItem: Long) = database.basketDatabase.getCount(idItem)

    fun getTypes() = database.catalogDatabase.getTypes()

    /*fun getTotalCount() = database.basketDatabase.getTotalCount()
    fun getTotalPrice() = database.basketDatabase.getTotalPrice()
    fun getTotalWeight() = database.basketDatabase.getTotalWeight()*/

}