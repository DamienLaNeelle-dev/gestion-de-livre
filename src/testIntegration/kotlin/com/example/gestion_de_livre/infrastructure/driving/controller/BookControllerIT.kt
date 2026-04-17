package com.example.gestion_de_livre.infrastructure.driving.controller

import com.example.gestion_de_livre.domain.model.Book
import com.example.gestion_de_livre.domain.usecase.BookUseCase
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@WebMvcTest(BookController::class)
class BookControllerIT(
    private val mockMvc: MockMvc,
    @MockkBean private val bookUseCase: BookUseCase
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        test("GET /books retourne la liste des livres avec disponibilité") {
            every { bookUseCase.getAllBooks() } returns listOf(
                Book("Algorithmes", "Cormen", available = true),
                Book("Clean Code", "Robert Martin", available = false)
            )

            mockMvc.get("/books")
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$[0].title") { value("Algorithmes") }
                    jsonPath("$[0].available") { value(true) }
                    jsonPath("$[1].title") { value("Clean Code") }
                    jsonPath("$[1].available") { value(false) }
                }
        }

        test("POST /books crée un livre et retourne 201") {
            every { bookUseCase.addBook("Clean Code", "Robert Martin") } returns
                    Book("Clean Code", "Robert Martin")

            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "Clean Code", "author": "Robert Martin"}"""
            }.andExpect {
                status { isCreated() }
            }

            verify(exactly = 1) { bookUseCase.addBook("Clean Code", "Robert Martin") }
        }

        test("POST /books avec titre vide retourne 400") {
            every { bookUseCase.addBook("", "Robert Martin") } throws
                    IllegalArgumentException("Le titre ne peut pas être vide.")

            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "", "author": "Robert Martin"}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }

        test("PATCH /books/{title}/reserve réserve un livre et retourne 200") {
            every { bookUseCase.reserveBook("Clean Code") } returns
                    Book("Clean Code", "Robert Martin", available = false)

            mockMvc.patch("/books/Clean Code/reserve")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.title") { value("Clean Code") }
                    jsonPath("$.available") { value(false) }
                }

            verify(exactly = 1) { bookUseCase.reserveBook("Clean Code") }
        }

        test("PATCH /books/{title}/reserve sur livre déjà réservé retourne 400") {
            every { bookUseCase.reserveBook("Clean Code") } throws
                    IllegalArgumentException("Le livre 'Clean Code' est déjà réservé.")

            mockMvc.patch("/books/Clean Code/reserve")
                .andExpect {
                    status { isBadRequest() }
                }
        }

        test("PATCH /books/{title}/reserve sur livre inexistant retourne 400") {
            every { bookUseCase.reserveBook("Inconnu") } throws
                    IllegalArgumentException("Le livre 'Inconnu' n'existe pas.")

            mockMvc.patch("/books/Inconnu/reserve")
                .andExpect {
                    status { isBadRequest() }
                }
        }
    }
}