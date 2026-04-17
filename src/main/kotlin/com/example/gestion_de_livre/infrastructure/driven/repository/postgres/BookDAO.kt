package com.example.gestion_de_livre.infrastructure.driven.repository.postgres

import com.example.gestion_de_livre.domain.model.Book
import com.example.gestion_de_livre.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : BookPort {

    override fun save(book: Book) {
        namedParameterJdbcTemplate.update(
            "INSERT INTO book (title, author, available) VALUES (:title, :author, :available)",
            mapOf("title" to book.title, "author" to book.author, "available" to book.available)
        )
    }

    override fun findAll(): List<Book> {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM book",
            MapSqlParameterSource()
        ) { rs, _ ->
            Book(
                title = rs.getString("title"),
                author = rs.getString("author"),
                available = rs.getBoolean("available")
            )
        }
    }

    override fun findByTitle(title: String): Book? {
        return namedParameterJdbcTemplate.query(
            "SELECT * FROM book WHERE title = :title",
            mapOf("title" to title)
        ) { rs, _ ->
            Book(
                title = rs.getString("title"),
                author = rs.getString("author"),
                available = rs.getBoolean("available")
            )
        }.firstOrNull()
    }

    override fun update(book: Book) {
        namedParameterJdbcTemplate.update(
            "UPDATE book SET available = :available WHERE title = :title",
            mapOf("available" to book.available, "title" to book.title)
        )
    }
}