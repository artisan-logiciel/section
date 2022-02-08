package gateway

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("gateway")

        noClasses()
            .that()
            .resideInAnyPackage("gateway.service..")
            .or()
            .resideInAnyPackage("gateway.repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..gateway.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses)
    }
}
