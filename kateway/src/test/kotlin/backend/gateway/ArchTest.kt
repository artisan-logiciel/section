package backend.gateway

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("backend.gateway")

        noClasses()
            .that()
            .resideInAnyPackage("backend.gateway.service..")
            .or()
            .resideInAnyPackage("backend.gateway.repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..backend.gateway.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses)
    }
}
