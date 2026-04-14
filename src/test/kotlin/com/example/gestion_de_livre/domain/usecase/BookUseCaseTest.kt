package com.example.gestion_de_livre.domain.usecase

import com.example.gestion_de_livre.domain.model.Book
import com.example.gestion_de_livre.domain.port.BookPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseTest : FunSpec({

    val bookPort = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookPort)

    beforeEach { clearMocks(bookPort) }

    test("ajouter un livre valide le sauvegarde et le retourne") {
        every { bookPort.save(any()) } returns Unit
        val result = bookUseCase.addBook("Clean Code", "Robert Martin")
        result shouldBe Book("Clean Code", "Robert Martin")
        verify(exactly = 1) { bookPort.save(Book("Clean Code", "Robert Martin")) }
    }

    test("getAllBooks retourne les livres triés alphabétiquement") {
        every { bookPort.findAll() } returns listOf(
            Book("Zorro", "Johnston"),
            Book("Algorithmes", "Cormen"),
            Book("Kotlin in Action", "Jemerov")
        )
        bookUseCase.getAllBooks() shouldBe listOf(
            Book("Algorithmes", "Cormen"),
            Book("Kotlin in Action", "Jemerov"),
            Book("Zorro", "Johnston")
        )
    }

    test("titre vide lance une exception") {
        shouldThrow<IllegalArgumentException> { bookUseCase.addBook("", "Robert Martin") }
    }

    test("auteur vide lance une exception") {
        shouldThrow<IllegalArgumentException> { bookUseCase.addBook("Clean Code", "") }
    }

    test("titre blanc lance une exception") {
        shouldThrow<IllegalArgumentException> { bookUseCase.addBook("   ", "Robert Martin") }
    }

    test("getAllBooks liste vide retourne liste vide") {
        every { bookPort.findAll() } returns emptyList()
        bookUseCase.getAllBooks() shouldBe emptyList()
    }

    // Property-based tests
    test("la liste retournée contient tous les éléments stockés") {
        checkAll<String, String, String, String> { t1, a1, t2, a2 ->
            every { bookPort.findAll() } returns listOf(Book(t1, a1), Book(t2, a2))
            val result = bookUseCase.getAllBooks()
            result.size shouldBe 2
            result.containsAll(listOf(Book(t1, a1), Book(t2, a2))) shouldBe true
        }
    }

    test("la liste retournée est toujours triée par titre") {
        checkAll<String, String, String, String> { t1, a1, t2, a2 ->
            every { bookPort.findAll() } returns listOf(Book(t1, a1), Book(t2, a2))
            val result = bookUseCase.getAllBooks()
            result shouldBe result.sortedBy { it.title }
        }
    }
})