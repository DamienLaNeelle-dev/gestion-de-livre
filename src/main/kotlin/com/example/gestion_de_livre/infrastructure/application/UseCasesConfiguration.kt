package com.example.gestion_de_livre.infrastructure.application

import com.example.gestion_de_livre.domain.port.BookPort
import com.example.gestion_de_livre.domain.usecase.BookUseCase
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {

    @Bean
    fun bookUseCase(bookPort: BookPort): BookUseCase {
        return BookUseCase(bookPort)
    }
}