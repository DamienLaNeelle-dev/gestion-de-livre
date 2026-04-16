package com.example.gestion_de_livre

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import io.kotest.core.spec.style.FunSpec

class ArchitectureTest : FunSpec({

    val basePackage = "com.example.gestion_de_livre"

    val importedClasses = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages(basePackage)

    test("l'architecture hexagonale est respectée") {
        val rule = layeredArchitecture().consideringAllDependencies()
            .layer("domain").definedBy("$basePackage.domain..")
            .layer("infrastructure").definedBy("$basePackage.infrastructure..")
            .layer("Standard API").definedBy("java..", "kotlin..", "kotlinx..", "org.jetbrains.annotations..")
            .withOptionalLayers(true)
            .whereLayer("domain").mayNotBeAccessedByAnyLayer()
            .whereLayer("domain").mayOnlyAccessLayers("Standard API")

        rule.check(importedClasses)
    }

    test("le domaine ne dépend pas de Spring") {
        noClasses()
            .that()
            .resideInAPackage("$basePackage.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("org.springframework..")
            .allowEmptyShould(true)
            .check(importedClasses)
    }

    test("le domaine ne dépend pas de l'infrastructure") {
        noClasses()
            .that()
            .resideInAPackage("$basePackage.domain..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("$basePackage.infrastructure..")
            .allowEmptyShould(true)
            .check(importedClasses)
    }
})