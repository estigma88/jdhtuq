package co.edu.uniquindio.dht.gui.structure.utils;

import java.math.BigInteger;

import co.edu.uniquindio.storage.StorageNode;
import co.edu.uniquindio.utils.hashing.HashingGenerator;
import co.edu.uniquindio.utils.hashing.Key;
//TODO Documentar
public class DHDChord implements Comparable<DHDChord> {
	//TODO Documentar
	private StorageNode dHashNode;
	//TODO Documentar
	private int numberNode;
	//TODO Documentar
	public DHDChord(StorageNode dHashNode, int numberNode) {
		this.setDHashNode(dHashNode);
		this.setNumberNode(numberNode);
	}
	//TODO Documentar
	@Override
	public int compareTo(DHDChord o) {
		BigInteger hashing1=HashingGenerator.getInstance().generateHashing(getDHashNode().getName(),Key.getKeyLength());
		BigInteger hashing2=HashingGenerator.getInstance().generateHashing(o.getDHashNode().getName(),Key.getKeyLength());
		return hashing1.compareTo(hashing2);
	}
	//TODO Documentar
	public void setDHashNode(StorageNode dHashNode) {
		this.dHashNode = dHashNode;
	}
	//TODO Documentar
	public StorageNode getDHashNode() {
		return dHashNode;
	}
	//TODO Documentar
	public void setNumberNode(int numberNode) {
		this.numberNode = numberNode;
	}
	//TODO Documentar
	public int getNumberNode() {
		return numberNode;
	}
}
