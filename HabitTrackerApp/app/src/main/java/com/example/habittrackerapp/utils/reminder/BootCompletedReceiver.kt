package com.example.habittrackerapp.utils.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val restoreWork = OneTimeWorkRequestBuilder<RestoreRemindersWorker>()
                .setInitialDelay(2, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueue(restoreWork)
            println("DEBUG: Device rebooted, will restore reminders on next app launch")
        }
    }
}

//package com.example.habittrackerapp.utils.reminder
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import androidx.work.OneTimeWorkRequestBuilder
//import androidx.work.WorkManager
//import java.util.concurrent.TimeUnit
//
//class BootCompletedReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
//            // Запускаем восстановление напоминаний через 2 минуты после загрузки
//            val restoreWork = OneTimeWorkRequestBuilder<RestoreRemindersWorker>()
//                .setInitialDelay(2, TimeUnit.MINUTES)
//                .build()
//
//            WorkManager.getInstance(context).enqueue(restoreWork)
//        }
//    }
//}