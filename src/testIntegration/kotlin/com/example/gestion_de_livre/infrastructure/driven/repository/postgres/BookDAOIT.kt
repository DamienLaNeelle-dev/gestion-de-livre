package com.example.gestion_de_livre.infrastructure.driven.repository.postgres

import com.example.gestion_de_livre.domain.model.Book
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIT(
    private val bookDAO: BookDAO,
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) : FunSpec() {

    override fun extensions() = listOf(SpringExtension)

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }
    }

    init {
        beforeEach {
            namedParameterJdbcTemplate.update(
                "DELETE FROM book",
                MapSqlParameterSource()
            )
        }

        afterSpec {
            container.stop()
        }

        test("save insère un livre en base") {
            bookDAO.save(Book("Clean Code", "Robert Martin"))
            val books = bookDAO.findAll()
            books.size shouldBe 1
            books[0] shouldBe Book("Clean Code", "Robert Martin")
        }

        test("findAll retourne tous les livres") {
            bookDAO.save(Book("Clean Code", "Robert Martin"))
            bookDAO.save(Book("Algorithmes", "Cormen"))
            bookDAO.findAll() shouldContainExactlyInAnyOrder listOf(
                Book("Clean Code", "Robert Martin"),
                Book("Algorithmes", "Cormen")
            )
        }

        test("findAll retourne liste vide si aucun livre") {
            bookDAO.findAll() shouldBe emptyList()
        }
    }
}