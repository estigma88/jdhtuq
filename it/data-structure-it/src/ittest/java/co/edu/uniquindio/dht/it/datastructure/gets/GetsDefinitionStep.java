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

package co.edu.uniquindio.dht.it.datastructure.gets;

import co.edu.uniquindio.dhash.resource.LocalFileResource;
import co.edu.uniquindio.dhash.starter.DHashProperties;
import co.edu.uniquindio.dht.it.datastructure.CucumberRoot;
import co.edu.uniquindio.dht.it.datastructure.World;
import co.edu.uniquindio.dht.it.datastructure.put.Content;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.resource.Resource;
import cucumber.api.java.en.Then;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetsDefinitionStep extends CucumberRoot {
    @Autowired
    private World world;
    @Autowired
    private DHashProperties dHashProperties;

    @Then("^I lookup the following resources:$")
    public void i_lookup_the_following_resources(List<Content> contents) throws Throwable {
        StorageNode storageNode = world.getRing().getNode(world.getNodeGateway());

        Path pathGets = Paths.get(dHashProperties.getResourceDirectory(), "gets", world.getNodeGateway());

        Files.createDirectories(pathGets);

        for (Content content : contents) {
            Resource resource = storageNode.get(content.getName(), (name, count, limit) -> {}).get();

            assertThat(resource).isNotNull();

            Path resourcePath = pathGets.resolve(resource.getId());

            LocalFileResource localFileResource = LocalFileResource.builder()
                    .resource(resource)
                    .path(pathGets.toString())
                    .bufferSize(1024)
                    .build();

            localFileResource.persist((name, count, limit) -> {});

            assertThat(Files.exists(resourcePath)).isTrue();

            assertThat(Files.readAllLines(Paths.get(content.getPath()))).isEqualTo(Files.readAllLines(resourcePath));

            resource.close();
        }
    }
}
