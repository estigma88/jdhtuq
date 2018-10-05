/*
 *  desktop-structure-gui  is a example of the peer to peer application with a desktop ui
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
 *
 */

package co.edu.uniquindio.dht.gui.structure.task.storageservice;

import co.edu.uniquindio.dhash.resource.LocalFileResource;
import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.storage.resource.Resource;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class GetTask extends StorageServiceTask {
    private final String resourceId;
    private final String resourceDirectory;

    public GetTask(JFrame jFrame, StorageNode storageNode, String resourceId, String resourceDirectory) {
        super(jFrame, storageNode);
        this.resourceId = resourceId;
        this.resourceDirectory = resourceDirectory;
    }


    @Override
    protected Void doInBackground() throws Exception {
        Resource resource = storageNode.get(resourceId, ((name, current, size) -> {
        })).get();

        Files.createDirectories(Paths.get(resourceDirectory + storageNode.getName() + "/gets/"));

        LocalFileResource localFileResource = LocalFileResource.builder()
                .resource(resource)
                .path(resourceDirectory + storageNode.getName() + "/gets/")
                .sizeBuffer(2048)
                .build();

        localFileResource.persist(((name, current, size) -> {log.info("{} - {} - {}", name, current, size);}));

        return null;
    }
}
