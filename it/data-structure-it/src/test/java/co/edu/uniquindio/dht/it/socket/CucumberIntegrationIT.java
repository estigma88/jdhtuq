package co.edu.uniquindio.dht.it.socket;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/features"}, format = {"pretty", "html:out/reports/cucumber/html",
        "json:out/cucumber.json", "usage:out/usage.jsonx", "junit:out/junit.xml"}, tags = {"~@enable"})
public class CucumberIntegrationIT {
}
