package ca.cairn.hms.contracts

import io.cucumber.spring.CucumberContextConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

/**
 * Contexte Spring des contrats : façades du domaine, implémentations des cas d'utilisation,
 * stubs (fabriques, dépôts, services) et données de test.
 */
@Configuration
@ComponentScan(
    basePackages = [
        "ca.cairn.hms.domain",
        "ca.cairn.hms.application.usecases.implementation",
        "ca.cairn.hms.stubs",
        "ca.cairn.hms.contracts.support",
    ],
)
class ContractTestConfiguration

@CucumberContextConfiguration
@ContextConfiguration(classes = [ContractTestConfiguration::class])
class CucumberSpringConfiguration
