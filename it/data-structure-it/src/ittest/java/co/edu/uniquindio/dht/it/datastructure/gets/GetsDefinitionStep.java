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

import co.edu.uniquindio.dhash.resource.NetworkResource;
import co.edu.uniquindio.dht.it.datastructure.CucumberRoot;
import co.edu.uniquindio.dht.it.datastructure.World;
import co.edu.uniquindio.dht.it.datastructure.put.Content;
import co.edu.uniquindio.storage.StorageNode;
import cucumber.api.java.en.Then;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetsDefinitionStep extends CucumberRoot {
    @Autowired
    private World world;

    @Then("^I lookup the following resources:$")
    public void i_lookup_the_following_resources(List<Content> contents) throws Throwable {
        StorageNode storageNode = world.getRing().getNode(world.getNodeGateway());

        for (Content content : contents) {
            NetworkResource resource = (NetworkResource) storageNode.get(content.getName());

            assertThat(resource).isNotNull();
            assertThat(IOUtils.toByteArray(resource.getInputStream())).isEqualTo(content.getContent().getBytes());
        }
    }
}
