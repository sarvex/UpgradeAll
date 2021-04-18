package net.xzos.upgradeall.core.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.xzos.upgradeall.core.coreConfig
import net.xzos.upgradeall.core.database.dao.AppDao
import net.xzos.upgradeall.core.database.dao.HubDao
import net.xzos.upgradeall.core.database.migration.*
import net.xzos.upgradeall.core.database.table.AppEntity
import net.xzos.upgradeall.core.database.table.HubEntity

@Database(entities = [AppEntity::class, HubEntity::class], version = 13)
@TypeConverters(Converters::class)
abstract class MetaDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun hubDao(): HubDao
}

val metaDatabase = Room
        .databaseBuilder(
                coreConfig.androidContext,
                MetaDatabase::class.java,
                "app_metadata_database.db"
        )
        .addMigrations(MIGRATION_6_7)
        .addMigrations(MIGRATION_7_8)
        .addMigrations(MIGRATION_8_9)
        .addMigrations(MIGRATION_9_10)
        .addMigrations(MIGRATION_8_10)
        .addMigrations(MIGRATION_10_11)
        .addMigrations(MIGRATION_11_12)
        .addMigrations(MIGRATION_12_13)
        .build()
