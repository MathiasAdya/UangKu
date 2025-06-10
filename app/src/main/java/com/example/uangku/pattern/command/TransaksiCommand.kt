package com.example.uangku.pattern.command

import com.example.uangku.model.Transaksi
import com.example.uangku.pattern.repository.TransaksiRepository

/**
 * Command Pattern - Interface untuk command
 */
interface Command {
    suspend fun execute(): Result<Any>
    suspend fun undo(): Result<Any>
}

/**
 * Command Pattern - Command untuk menambah transaksi
 */
class AddTransaksiCommand(
    private val repository: TransaksiRepository,
    private val transaksi: Transaksi
) : Command {
    
    override suspend fun execute(): Result<Any> {
        return repository.saveTransaksi(transaksi).map { it }
    }
    
    override suspend fun undo(): Result<Any> {
        return repository.deleteTransaksi(transaksi.id).map { it }
    }
}

/**
 * Command Pattern - Command untuk mengupdate transaksi
 */
class UpdateTransaksiCommand(
    private val repository: TransaksiRepository,
    private val transaksiId: String,
    private val newTransaksi: Transaksi,
    private val oldTransaksi: Transaksi
) : Command {
    
    override suspend fun execute(): Result<Any> {
        return repository.updateTransaksi(transaksiId, newTransaksi).map { it }
    }
    
    override suspend fun undo(): Result<Any> {
        return repository.updateTransaksi(transaksiId, oldTransaksi).map { it }
    }
}

/**
 * Command Pattern - Command untuk menghapus transaksi
 */
class DeleteTransaksiCommand(
    private val repository: TransaksiRepository,
    private val transaksiId: String,
    private val deletedTransaksi: Transaksi
) : Command {
    
    override suspend fun execute(): Result<Any> {
        return repository.deleteTransaksi(transaksiId).map { it }
    }
    
    override suspend fun undo(): Result<Any> {
        return repository.saveTransaksi(deletedTransaksi).map { it }
    }
}

/**
 * Command Pattern - Command untuk batch operations
 */
class BatchTransaksiCommand(
    private val commands: List<Command>
) : Command {
    private val executedCommands = mutableListOf<Command>()
    
    override suspend fun execute(): Result<Any> {
        return try {
            for (command in commands) {
                val result = command.execute()
                if (result.isSuccess) {
                    executedCommands.add(command)
                } else {
                    // Rollback executed commands
                    undo()
                    return result
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            undo()
            Result.failure(e)
        }
    }
    
    override suspend fun undo(): Result<Any> {
        return try {
            // Undo in reverse order
            for (command in executedCommands.reversed()) {
                command.undo()
            }
            executedCommands.clear()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Command Pattern - Invoker untuk mengelola dan mengeksekusi command
 */
class TransaksiCommandInvoker {
    private val commandHistory = mutableListOf<Command>()
    private var currentPosition = -1
    
    suspend fun executeCommand(command: Command): Result<Any> {
        val result = command.execute()
        
        if (result.isSuccess) {
            // Remove any commands after current position (for redo functionality)
            if (currentPosition < commandHistory.size - 1) {
                commandHistory.subList(currentPosition + 1, commandHistory.size).clear()
            }
            
            commandHistory.add(command)
            currentPosition++
            
            // Limit history size to prevent memory issues
            if (commandHistory.size > 50) {
                commandHistory.removeAt(0)
                currentPosition--
            }
        }
        
        return result
    }
    
    suspend fun undo(): Result<Any> {
        return if (canUndo()) {
            val command = commandHistory[currentPosition]
            val result = command.undo()
            if (result.isSuccess) {
                currentPosition--
            }
            result
        } else {
            Result.failure(IllegalStateException("No commands to undo"))
        }
    }
    
    suspend fun redo(): Result<Any> {
        return if (canRedo()) {
            currentPosition++
            val command = commandHistory[currentPosition]
            val result = command.execute()
            if (result.isFailure) {
                currentPosition--
            }
            result
        } else {
            Result.failure(IllegalStateException("No commands to redo"))
        }
    }
    
    fun canUndo(): Boolean = currentPosition >= 0
    
    fun canRedo(): Boolean = currentPosition < commandHistory.size - 1
    
    fun clearHistory() {
        commandHistory.clear()
        currentPosition = -1
    }
    
    fun getHistorySize(): Int = commandHistory.size
}
