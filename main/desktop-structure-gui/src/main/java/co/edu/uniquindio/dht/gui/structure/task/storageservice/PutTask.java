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

import co.edu.uniquindio.dhash.resource.BytesResource;
import co.edu.uniquindio.storage.StorageNode;
import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;

public class PutTask extends StorageServiceTask {
    private final File file;

    public PutTask(JFrame jFrame, StorageNode storageNode, File file) {
        super(jFrame, storageNode);
        this.file = file;
    }


    @Override
    protected Void doInBackground() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            BytesResource fileResource = new BytesResource(file.getName(), IOUtils.toByteArray(fileInputStream));

            storageNode.put(fileResource);
        }
        return null;
    }
}
