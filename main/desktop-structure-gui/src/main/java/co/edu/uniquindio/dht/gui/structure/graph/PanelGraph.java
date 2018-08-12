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


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import co.edu.uniquindio.dht.gui.structure.controller.Controller;
import co.edu.uniquindio.dht.gui.structure.utils.DHDChord;
//TODO Documentar
@SuppressWarnings("serial")
public class PanelGraph extends JPanel implements MouseListener {
	//TODO Documentar
	final Color fixedColor = Color.red;
	//TODO Documentar
	final Color comunicationColor = Color.blue;
	//TODO Documentar
	final Color selectColor = new Color(128, 198, 128);
	//TODO Documentar
	final Color putColor = new Color(88, 158, 88);
	//TODO Documentar
	final Color edgeColor = Color.black;
	//TODO Documentar
	final Color nodeColor = new Color(250, 220, 100);
	//TODO Documentar
	private Color actionColor = null;
	//TODO Documentar
	private int nnodes;
	//TODO Documentar
	private ArrayList<Node> nodes = new ArrayList<Node>();
	//TODO Documentar
	private ArrayList<Edge> edges = new ArrayList<Edge>();
	//TODO Documentar
	private int r = 0;
	//TODO Documentar
	private Node pick;
	//TODO Documentar
	private Image offscreen;
	//TODO Documentar
	private Dimension offscreensize;
	//TODO Documentar
	private Graphics offgraphics;
	//TODO Documentar
//	private String stabilizedString = "";
	//TODO Documentar
	private Controller controller;
	//TODO Documentar
//	private boolean isStabilized;
	//TODO Documentar
	private Edge comunicationEdge;
	//TODO Documentar
	private boolean paint = false;
	//TODO Documentar
	public PanelGraph() {
		setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(new Color(192, 192, 192));
		addMouseListener(this);
		nnodes = 0;
	}
	//TODO Documentar
	public Node findNode(String lbl) {
		for (int i = 0; i < nnodes; i++) {
			if (nodes.get(i).getLbl().equals(lbl)) {
				return nodes.get(i);
			}
		}

		return null;
	}
	//TODO Documentar
	public int addNode(String lbl, double angle, String value) {
		Node n = new Node();

		n.setX((r / 2) * Math.cos(angle * 2.0 * Math.PI / 360)
				+ (getSize().width / 2) - 2);
		n.setY((r / 2) * Math.sin(angle * 2.0 * Math.PI / 360)
				+ (getSize().height / 2));

		n.setNodeIdentifier(value);
		n.setLbl(lbl);

		nodes.add(n);

		return nnodes++;
	}
	//TODO Documentar
	public void addEdge(String from, String to, String successor) {
		Edge e = new Edge();

		e.setFrom(findNode(from));
		e.setTo(findNode(to));
		e.setSuccessor(successor);

		getEdges().add(e);
	}
	//TODO Documentar
	public void deleteTrace() {
		edges.clear();
		comunicationEdge = null;
		paint = false;
	}
	//TODO Documentar
	public void makeRing(List<DHDChord> dhdChordList) {
		reset();

		int numOfNodes = dhdChordList.size();

		if (numOfNodes == 0)
			return;

		double angle = 0;
		double dr = 360 / numOfNodes;

		for (int i = 0; i < numOfNodes; i++) {
			DHDChord dhdChord = dhdChordList.get(i);
			addNode("" + dhdChord.getNumberNode(), angle, dhdChord.getDHashNode().getName());
			angle += dr;
		}

		repaint();
	}
	//TODO Documentar
	public void makeRing() {
		int numOfNodes = nodes.size();

		if (numOfNodes == 0) {
			repaint();
			return;
		}

		double angle = 0;
		double dr = 360 / numOfNodes;

		for (int i = 0; i < numOfNodes; i++) {
			Node n = nodes.get(i);

			n.setX((r / 2) * Math.cos(angle * 2.0 * Math.PI / 360)
					+ (getSize().width / 2) - 2);
			n.setY((r / 2) * Math.sin(angle * 2.0 * Math.PI / 360)
					+ (getSize().height / 2));

			angle += dr;
		}

		//repaint();
	}
	//TODO Documentar
	public void paint(Graphics g) {
        super.paint(g);

		Graphics2D g2d = (Graphics2D) g;
		
		Dimension d = getSize();

		r = Math.min(d.height, d.width) - 100;

		makeRing();

		// Erases the graphic panel.
		if ((offscreen == null) || (d.width != offscreensize.width)
				|| (d.height != offscreensize.height)) {
			offscreen = createImage(d.width, d.height);
			offscreensize = d;

			if (offgraphics != null) {
				offgraphics.dispose();
			}

			offgraphics = offscreen.getGraphics();
			offgraphics.setFont(getFont());
		}

		offgraphics.setColor(getBackground());

		offgraphics.fillRect(0, 0, d.width, d.height);

		offgraphics.setColor(edgeColor);
		g2d.setStroke(new BasicStroke(1f));
		offgraphics.drawOval(((d.width - r) / 2), ((d.height - r) / 2), r, r);

		FontMetrics fm = offgraphics.getFontMetrics();



		if (paint) {
			
			for (int i = 0; i < edges.size(); i++) {
				paintEdge((Graphics2D)offgraphics, edges.get(i), actionColor);
			}

			//makeComunicationEdge();

			if (comunicationEdge != null)
				paintEdge((Graphics2D)offgraphics, comunicationEdge, comunicationColor);

		}

		
		
		for (int i = 0; i < nnodes; i++) {
			paintNode((Graphics2D)offgraphics, nodes.get(i), fm);
		}
//		paintStabilizedState(offgraphics, fm);

		g.drawImage(offscreen, 0, 0, null);
	}
	//TODO Documentar
	public void makeComunicationEdge() {
		if (edges.size() >= 2 && comunicationEdge == null) {
			Edge ed = edges.get(edges.size() - 2);

			comunicationEdge = new Edge();

			comunicationEdge.setFrom(pick);
			comunicationEdge.setTo(findNode(ed.getSuccessor()));
			comunicationEdge.setSuccessor(ed.getSuccessor());
		} else {
			if (edges.size() == 0 && comunicationEdge == null && paint) {
				comunicationEdge = new Edge();

				comunicationEdge.setFrom(pick);

				int suc = getNode(pick.getLbl());

				Node successor = null;

				if (suc != -1) {
					if ((suc + 1) == nodes.size()) {
						successor = nodes.get(0);
					} else {
						successor = nodes.get(suc + 1);
					}
				}

				comunicationEdge.setTo(successor);
				comunicationEdge.setSuccessor(successor.getLbl());
			}
		}
	}
	//TODO Documentar
	private void paintNode(Graphics2D g, Node n, FontMetrics fm) {
		
		int x = (int) n.getX();
		int y = (int) n.getY();

		if (n == pick) {
			g.setColor(selectColor);
		} else {
			g.setColor(nodeColor);
		}
		g.setStroke(new BasicStroke(1f));
		// calcula el ancho y la altura de cada rectangulo
		int w = fm.stringWidth(n.getLbl()) + 10;
		int h = fm.getHeight() + 4;

		// dibuja el rectangulo de color con centro en x,y y el ancho y altura
		// especificadas
		g.fillRect(x - w / 2, y - h / 2, w, h);

		g.setColor(Color.black);

		// dibuja el rectangulo (borde)
		g.drawRect(x - w / 2, y - h / 2, w - 1, h - 1);

		// escribe el nombre del nodo, teniendo en cuenta la altura de la fuente
		g.drawString(n.getLbl(), x - (w - 10) / 2, (y - (h - 4) / 2)
				+ fm.getAscent());
	}
	//TODO Documentar
	private void paintEdge(Graphics2D g, Edge e, Color color) {
		if (e == null) {
			return;
		}

		int x1 = (int) e.getFrom().getX();
		int y1 = (int) e.getFrom().getY();
		int x2 = (int) e.getTo().getX();
		int y2 = (int) e.getTo().getY();

		
		g.setColor(color);

		if (e.getFrom().getLbl().equals(e.getTo().getLbl())) {
			g.drawOval(x1, y1, 30, 30);
			return;
		}
		g.setStroke(new BasicStroke(2f));
		g.drawLine(x1, y1, x2, y2);

		g.setColor(Color.BLUE);
		g.fillOval(x1 - 3, y1 - 3, 6, 6);

	}
	//TODO Documentar
//	private void paintStabilizedState(Graphics g, FontMetrics fm) {
//		Graphics2D g2d = (Graphics2D) g;

//		int r = 20;

//		int h = fm.getHeight();

//		g2d.setColor(edgeColor);

//		g2d.setStroke(new BasicStroke(2f));

//		g2d.drawOval(r, getSize().height - 2 * r, r, r);

//		g2d.setStroke(new BasicStroke(1f));

//		if (!isStabilized) {
//			if (nodes.size() != 0)
//				stabilizedString = "Stabilizing Ring....";
//			g2d.setColor(fixedColor);
//		} else {
//			if (nodes.size() != 0)
//				stabilizedString = "Ring Stabilized....";
//			g2d.setColor(selectColor);
//		}

//		g2d.fillOval(r, getSize().height - 2 * r, r, r);

//		g2d.setColor(edgeColor);

//		g2d.setStroke(new BasicStroke(3f));

//		g2d.drawString(stabilizedString, 10 + 2 * r, (getSize().height - h - r)
//				+ fm.getAscent());

//		g2d.setStroke(new BasicStroke(1f));
//	}
	//TODO Documentar
	private void reset() {
		nnodes = 0;
		nodes.clear();
		getEdges().clear();
//		stabilizedString = "";
//		isStabilized = false;
	}
	//TODO Documentar
	@Override
	public void mouseClicked(MouseEvent e) {
		double bestdist = 70.0;

		int x = e.getX();
		int y = e.getY();

		for (int i = 0; i < nnodes; i++) {
			Node n = nodes.get(i);
			double dist = (n.getX() - x) * (n.getX() - x) + (n.getY() - y)
					* (n.getY() - y);

			if (dist < bestdist) {
				pick = n;
				break;
			}
		}

		if (pick != null) {
			controller.changeToNode(pick.getNodeIdentifier());
		}

		repaint();
	}
	//TODO Documentar
	public void setActionColor(boolean put) {
		actionColor = (put) ? putColor : fixedColor;
		paint = true;
	}
	//TODO Documentar
	public int getNnodes() {
		return nnodes;
	}
	//TODO Documentar
	public int getNode(String node) {
		for (int i = 0; i < nnodes; i++) {
			if (nodes.get(i).getLbl().equals(node)) {
				return i;
			}
		}

		return -1;

	}
	//TODO Documentar
	public void setPickNode(String name) {
		Node node = findNode(name);
		pick = node;
		controller.clearGraph();
		repaint();
	}
	//TODO Documentar
	public void setStabilizedState(boolean isStabilized) {
//		this.isStabilized = isStabilized;
//		repaint();
	}
	//TODO Documentar
	public void setController(Controller controller) {
		this.controller = controller;
	}
	//TODO Documentar
	public Controller getController() {
		return controller;
	}
	//TODO Documentar
	public void setEdges(ArrayList<Edge> edges) {
		this.edges = edges;
		//repaint();
	}
	//TODO Documentar
	public ArrayList<Edge> getEdges() {
		return edges;
	}
	//TODO Documentar
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	//TODO Documentar
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	//TODO Documentar
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	//TODO Documentar
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}