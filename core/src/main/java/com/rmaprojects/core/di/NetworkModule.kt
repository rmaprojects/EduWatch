package com.rmaprojects.core.di

import com.fcmsender.FCMSender
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.rmaprojects.core.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            BuildConfig.SUPABASE_API_URL,
            BuildConfig.SUPABASE_API_KEY
        ) {
            install(GoTrue)
            install(Postgrest)
        }
    }

    @Provides
    @Singleton
    fun provideFBaseMessaging(): FirebaseMessaging {
        return Firebase.messaging
    }
}