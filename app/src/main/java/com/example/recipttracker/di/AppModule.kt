package com.example.recipttracker.di

import android.app.Application
import androidx.room.Room
import com.example.recipttracker.data.local.ReceiptDatabase
import com.example.recipttracker.data.local.UserDatabase
import com.example.recipttracker.data.repository.ReceiptRepositoryImpl
import com.example.recipttracker.data.repository.UserRepositoryImpl
import com.example.recipttracker.domain.repository.ReceiptRepository
import com.example.recipttracker.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideReceiptDatabase(app: Application): ReceiptDatabase {
        return Room.databaseBuilder(
            app,
            ReceiptDatabase::class.java,
            ReceiptDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideReceiptRepository(db: ReceiptDatabase, app: Application): ReceiptRepository {
        return ReceiptRepositoryImpl(db.receiptDao, app.applicationContext)
    }

    @Provides
    @Singleton
    fun providesUserDatabase(app: Application): UserDatabase {
        return Room.databaseBuilder(
            app,
            UserDatabase::class.java,
            UserDatabase.DATABASE_NAME
        ).build()
    }
    @Provides
    @Singleton
    fun provideUserRepository(db: UserDatabase, app: Application): UserRepository {
        return UserRepositoryImpl(db.userDao, app.applicationContext)
    }
}