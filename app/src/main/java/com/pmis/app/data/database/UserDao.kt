package com.pmis.app.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users WHERE googleId = :googleId")
    suspend fun getUserByGoogleId(googleId: String): User?
    
    @Query("SELECT * FROM users WHERE googleId = :googleId")
    fun getUserByGoogleIdFlow(googleId: String): Flow<User?>
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: User)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("UPDATE users SET formResponses = :formResponses WHERE googleId = :googleId")
    suspend fun updateUserFormResponses(googleId: String, formResponses: String)
    
    @Query("UPDATE users SET lastLoginAt = :timestamp WHERE googleId = :googleId")
    suspend fun updateLastLogin(googleId: String, timestamp: Long)
    
    @Query("DELETE FROM users WHERE googleId = :googleId")
    suspend fun deleteUser(googleId: String)
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
    
    @Query("SELECT * FROM users ORDER BY lastLoginAt DESC LIMIT 1")
    suspend fun getLastLoggedInUser(): User?
}

