package com.example.gestion_de_livre.domain.port

import com.example.gestion_de_livre.domain.model.Book

interface BookPort {
    fun save(book: Book)
    fun findAll(): List<Book>
    fun findByTitle(title: String): Book?
    fun update(book: Book)
}