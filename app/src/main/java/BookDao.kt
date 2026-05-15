package com.example.nammapustaka

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookDao {

    @Insert
    suspend fun insertBook(book: BookEntity)

    @Query("SELECT * FROM book_table")
    suspend fun getAllBooks(): List<BookEntity>

    @Query("DELETE FROM book_table")
    suspend fun deleteAllBooks()

    @Query("DELETE FROM book_table WHERE id = :id")
    suspend fun deleteBookById(id: Int)
}