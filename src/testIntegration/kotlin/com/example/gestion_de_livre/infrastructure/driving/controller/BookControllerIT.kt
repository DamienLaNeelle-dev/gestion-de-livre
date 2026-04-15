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
import org.springframework.test.web.servlet.post

@WebMvcTest(BookController::class)
class BookControllerIT(
    private val mockMvc: MockMvc,
    @MockkBean private val bookUseCase: BookUseCase
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    init {
        test("GET /books retourne la liste des livres") {
            every { bookUseCase.getAllBooks() } returns listOf(
                Book("Algorithmes", "Cormen"),
                Book("Clean Code", "Robert Martin")
            )

            mockMvc.get("/books")
                .andExpect {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$[0].title") { value("Algorithmes") }
                    jsonPath("$[0].author") { value("Cormen") }
                    jsonPath("$[1].title") { value("Clean Code") }
                    jsonPath("$[1].author") { value("Robert Martin") }
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
            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title": "", "author": "Robert Martin"}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }

        test("POST /books avec body invalide retourne 400") {
            mockMvc.post("/books") {
                contentType = MediaType.APPLICATION_JSON
                content = """{}"""
            }.andExpect {
                status { isBadRequest() }
            }
        }
    }
}