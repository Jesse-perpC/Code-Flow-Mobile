package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.CloudSyncSnapshotEntity
import com.example.model.GitBranchEntity
import com.example.model.GitCommitEntity
import com.example.model.GitRemoteEntity
import com.example.model.ServerConfigEntity
import com.example.model.TerminalHistoryEntity
import com.example.model.WorkspaceEntity
import com.example.model.WorkspaceFileEntity

@Database(
    entities = [
        WorkspaceEntity::class,
        WorkspaceFileEntity::class,
        GitCommitEntity::class,
        GitBranchEntity::class,
        GitRemoteEntity::class,
        ServerConfigEntity::class,
        TerminalHistoryEntity::class,
        CloudSyncSnapshotEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workspaceDao(): WorkspaceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "opencode_server_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
