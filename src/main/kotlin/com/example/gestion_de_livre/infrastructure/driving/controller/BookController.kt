package com.example.gestion_de_livre.infrastructure.driving.controller

import com.example.gestion_de_livre.domain.usecase.BookUseCase
import com.example.gestion_de_livre.infrastructure.driving.dto.BookDTO
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(private val bookUseCase: BookUseCase) {

    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks().map { BookDTO(it.title, it.author) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@Valid @RequestBody bookDTO: BookDTO) {
        bookUseCase.addBook(bookDTO.title, bookDTO.author)
    }
}