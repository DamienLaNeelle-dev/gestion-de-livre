package com.example.gestion_de_livre.infrastructure.driving.controller

import com.example.gestion_de_livre.domain.usecase.BookUseCase
import com.example.gestion_de_livre.infrastructure.driving.dto.BookDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/books")
class BookController(private val bookUseCase: BookUseCase) {

    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks().map {
            BookDTO(it.title, it.author, it.available)
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody bookDTO: BookDTO) {
        bookUseCase.addBook(bookDTO.title, bookDTO.author)
    }

    @PatchMapping("/{title}/reserve")
    fun reserveBook(@PathVariable title: String): BookDTO {
        val book = bookUseCase.reserveBook(title)
        return BookDTO(book.title, book.author, book.available)
    }
}