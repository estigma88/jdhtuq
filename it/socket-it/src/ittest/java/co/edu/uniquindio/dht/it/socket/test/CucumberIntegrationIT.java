package co.edu.uniquindio.dht.it.socket.test;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(format = {"pretty", "html:build/reports/cucumber/html",
        "json:build/cucumber.json", "usage:build/usage.jsonx", "junit:build/junit.xml"}, tags = {"~@enable"})
public class CucumberIntegrationIT {

}
