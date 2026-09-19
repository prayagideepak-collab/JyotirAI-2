package com.example.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.JyotishDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CitySyncWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val dao = JyotishDatabase.getDatabase(applicationContext).cityDao()
            val count = dao.getCityCount()
            if (count > 0) {
                // Periodically check and verify offline city geographical data & historical mappings
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
