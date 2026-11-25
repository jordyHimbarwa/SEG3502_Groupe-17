package ca.cairn.hms.contracts

import io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME
import io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectClasspathResource
import org.junit.platform.suite.api.Suite

/** Exécute tous les contrats Cucumber de src/test/resources/contracts avec ./gradlew test. */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("contracts")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "ca.cairn.hms.contracts")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:build/reports/cucumber/contracts.html")
class RunContractsTest
