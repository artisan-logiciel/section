package backend.monolith

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("backend.monolith")

        noClasses()
            .that()
            .resideInAnyPackage("backend.monolith.service..")
            .or()
            .resideInAnyPackage("backend.monolith.repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..backend.monolith.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses)
    }
}
