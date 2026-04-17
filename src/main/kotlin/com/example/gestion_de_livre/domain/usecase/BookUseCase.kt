package com.example.gestion_de_livre.domain.usecase

import com.example.gestion_de_livre.domain.model.Book
import com.example.gestion_de_livre.domain.port.BookPort

class BookUseCase(private val bookPort: BookPort) {

    fun addBook(title: String, author: String): Book {
        require(title.isNotBlank()) { "Le titre ne peut pas être vide." }
        require(author.isNotBlank()) { "L'auteur ne peut pas être vide." }
        val book = Book(title = title, author = author)
        bookPort.save(book)
        return book
    }

    fun getAllBooks(): List<Book> {
        return bookPort.findAll().sortedBy { it.title }
    }

    fun reserveBook(title: String): Book {
        val book = bookPort.findByTitle(title)
            ?: throw IllegalArgumentException("Le livre '$title' n'existe pas.")
        require(book.available) { "Le livre '$title' est déjà réservé." }
        val reservedBook = book.copy(available = false)
        bookPort.update(reservedBook)
        return reservedBook
    }
}