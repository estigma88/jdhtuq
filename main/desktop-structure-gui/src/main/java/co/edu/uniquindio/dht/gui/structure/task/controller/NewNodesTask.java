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

package co.edu.uniquindio.dht.gui.structure.task.controller;

import co.edu.uniquindio.dht.gui.structure.controller.Controller;

import javax.swing.*;

public class NewNodesTask extends ControllerTask {
    private final Integer amount;

    public NewNodesTask(JFrame jFrame, Controller controller, Integer amount) {
        super(jFrame, controller);
        this.amount = amount;
    }


    @Override
    protected Void doInBackground() throws Exception {
        controller.createNnodes(amount);

        return null;
    }
}
