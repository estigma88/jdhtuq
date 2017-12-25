package co.edu.uniquindio.dht.gui.structure.graph;
//TODO Documentar
public class Edge {
	//TODO Documentar
	private Node from;
	//TODO Documentar
	private Node to;
	//TODO Documentar
	private String successor;
	//TODO Documentar
	public Node getFrom() {
		return from;
	}
	//TODO Documentar
	public void setFrom(Node from) {
		this.from = from;
	}
	//TODO Documentar
	public Node getTo() {
		return to;
	}
	//TODO Documentar
	public void setTo(Node to) {
		this.to = to;
	}
	//TODO Documentar
	public void setSuccessor(String successor) {
		this.successor = successor;
	}
	//TODO Documentar
	public String getSuccessor() {
		return successor;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return from.getNodeIdentifier()+" "+to.getNodeIdentifier();
	}
}