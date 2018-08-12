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

package co.edu.uniquindio.dht.gui.structure.graph;
//TODO Documentar
public class Node {
	//TODO Documentar
	private double x;
	//TODO Documentar
	private double y;
	//TODO Documentar
	private String nodeIdentifier;
	//TODO Documentar
	private String lbl;
	//TODO Documentar
	public String getLbl() {
		return lbl;
	}
	//TODO Documentar
	public void setLbl(String lbl) {
		this.lbl = lbl;
	}
	//TODO Documentar
	public double getX() {
		return x;
	}
	//TODO Documentar
	public void setX(double x) {
		this.x = x;
	}
	//TODO Documentar
	public double getY() {
		return y;
	}
	//TODO Documentar
	public void setY(double y) {
		this.y = y;
	}
	//TODO Documentar
	public void setNodeIdentifier(String nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}
	//TODO Documentar
	public String getNodeIdentifier() {
		return nodeIdentifier;
	}
}