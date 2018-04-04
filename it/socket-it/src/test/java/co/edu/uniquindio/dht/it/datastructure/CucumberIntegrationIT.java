package co.edu.uniquindio.dht.it.datastructure;

import cucumber.api.CucumberOptions;
import cucumber.runtime.arquillian.CukeSpace;
import org.junit.runner.RunWith;

@RunWith(CukeSpace.class)
@CucumberOptions(features = {"src/test/resources/features"}, format = {"pretty", "html:out/reports/cucumber/html",
        "json:out/cucumber.json", "usage:out/usage.jsonx", "junit:out/junit.xml"}, tags = {"~@enable"})
public class CucumberIntegrationIT {

}
