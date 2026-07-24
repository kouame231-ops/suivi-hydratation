package com.aistudio.hydrationtracker.hqdzrt

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class WaterLog(
    val id: String = UUID.randomUUID().toString(),
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
) {
    val formattedTime: String
        get() {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}

class HydrationViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefs = application.getSharedPreferences("hydration_prefs", Context.MODE_PRIVATE)

    private val _currentIntakeMl = MutableStateFlow(0)
    val currentIntakeMl: StateFlow<Int> = _currentIntakeMl.asStateFlow()

    private val _targetMl = MutableStateFlow(2000) // Default 2L
    val targetMl: StateFlow<Int> = _targetMl.asStateFlow()

    private val _history = MutableStateFlow<List<WaterLog>>(emptyList())
    val history: StateFlow<List<WaterLog>> = _history.asStateFlow()

    init {
        loadData()
    }

    /**
     * Adds an amount of water in ml to the current intake and logs it.
     */
    fun addWater(amountMl: Int) {
        val newLog = WaterLog(amountMl = amountMl)
        val updatedHistory = listOf(newLog) + _history.value
        val updatedIntake = _currentIntakeMl.value + amountMl

        _currentIntakeMl.value = updatedIntake
        _history.value = updatedHistory
        saveData(updatedIntake, updatedHistory)
    }

    /**
     * Resets the current water intake and history.
     */
    fun resetWater() {
        _currentIntakeMl.value = 0
        _history.value = emptyList()
        saveData(0, emptyList())
    }

    /**
     * Deletes a specific logged item from the history.
     */
    fun deleteLog(log: WaterLog) {
        val updatedHistory = _history.value.filter { it.id != log.id }
        val updatedIntake = (_currentIntakeMl.value - log.amountMl).coerceAtLeast(0)

        _currentIntakeMl.value = updatedIntake
        _history.value = updatedHistory
        saveData(updatedIntake, updatedHistory)
    }

    /**
     * Changes the daily intake target.
     */
    fun updateTarget(newTargetMl: Int) {
        if (newTargetMl > 0) {
            _targetMl.value = newTargetMl
            sharedPrefs.edit().putInt("target_ml", newTargetMl).apply()
        }
    }

    private fun saveData(intake: Int, historyList: List<WaterLog>) {
        val historyString = historyList.joinToString(";") { "${it.id},${it.amountMl},${it.timestamp}" }
        sharedPrefs.edit()
            .putInt("current_intake_ml", intake)
            .putString("history_logs", historyString)
            .apply()
    }

    private fun loadData() {
        val intake = sharedPrefs.getInt("current_intake_ml", 0)
        val target = sharedPrefs.getInt("target_ml", 2000)
        val historyString = sharedPrefs.getString("history_logs", "") ?: ""

        val historyList = mutableListOf<WaterLog>()
        if (historyString.isNotEmpty()) {
            try {
                val parts = historyString.split(";")
                for (part in parts) {
                    val logParts = part.split(",")
                    if (logParts.size == 3) {
                        historyList.add(
                            WaterLog(
                                id = logParts[0],
                                amountMl = logParts[1].toInt(),
                                timestamp = logParts[2].toLong()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle parsing error gracefully by clearing bad data
                sharedPrefs.edit().remove("history_logs").apply()
            }
        }

        _currentIntakeMl.value = intake
        _targetMl.value = target
        _history.value = historyList
    }
}
