package com.example.gestion_de_livre

import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.response.ValidatableResponse
import io.kotest.matchers.shouldBe
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class BookStepDefs(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate
) {

    @LocalServerPort
    private var port: Int = 0

    private lateinit var lastResult: ValidatableResponse

    @Before
    fun setup() {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        namedParameterJdbcTemplate.update("DELETE FROM book", MapSqlParameterSource())
    }

    @Given("l'utilisateur crée le livre {string} de {string}")
    fun createBook(title: String, author: String) {
        RestAssured.given()
            .contentType(ContentType.JSON)
            .body("""{"title": "$title", "author": "$author"}""")
            .`when`()
            .post("/books")
            .then()
            .statusCode(201)
    }

    @When("l'utilisateur récupère la liste des livres")
    fun getAllBooks() {
        lastResult = RestAssured.given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
    }

    @Then("la liste contient les livres suivants")
    fun checkBooks(books: List<Map<String, String>>) {
        val titles = lastResult.extract().jsonPath().getList<String>("title")
        val authors = lastResult.extract().jsonPath().getList<String>("author")

        titles.size shouldBe books.size
        books.forEachIndexed { index, book ->
            titles[index] shouldBe book["title"]
            authors[index] shouldBe book["author"]
        }
    }
}