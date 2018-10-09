/*
 *  Data structure it project has a integration tests for data structure communication
 *  Copyright (C) 2010 - 2018  Daniel Pelaez
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package co.edu.uniquindio.dht.it.datastructure.put;

import co.edu.uniquindio.dhash.resource.FileResource;
import co.edu.uniquindio.dhash.starter.DHashProperties;
import co.edu.uniquindio.dht.it.datastructure.CucumberRoot;
import co.edu.uniquindio.dht.it.datastructure.World;
import co.edu.uniquindio.dht.it.datastructure.ring.Ring;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.resource.ProgressStatus;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PutsDefinitionStep extends CucumberRoot {
    @Autowired
    private World world;
    @Autowired
    private DHashProperties dHashProperties;
    private Map<String, Content> contents;

    @Given("^I have the resources names and values:$")
    public void i_have_the_resources_names_and_values(List<Content> contents) throws Throwable {
        this.contents = contents.stream()
                .collect(Collectors.toMap(Content::getName, Function.identity()));
    }

    @When("^I put resources into the network$")
    public void i_put_resources_into_the_network() throws Throwable {
        ProgressStatus progressStatus = (name, count, limit) -> {
        };

        Ring ring = world.getRing();

        StorageNode storageNode = ring.getNode(world.getNodeGateway());

        for (String contentName : contents.keySet()) {
            FileResource resource = FileResource.withPath()
                    .id(contentName)
                    .path(contents.get(contentName).getPath())
                    .build(progressStatus);

            storageNode.put(resource, progressStatus).get();

            resource.close();
        }

    }

    @Then("^The resources are put in the following nodes:$")
    public void the_resources_are_put_in_the_following_nodes(Map<String, String> nodesByResource) throws Throwable {
        for (String contentName : nodesByResource.keySet()) {
            String[] nodes = nodesByResource.get(contentName).split(",");

            for (String node : nodes) {
                Path resourcePath = Paths.get(dHashProperties.getResourceDirectory(), node, contentName, contentName);

                assertThat(Files.exists(resourcePath)).isTrue();

                assertThat(Files.readAllLines(Paths.get(contents.get(contentName).getPath()))).isEqualTo(Files.readAllLines(resourcePath));
            }
        }
    }
}
