/*
 *  Chord project implement of lookup algorithm Chord 
 *  Copyright (C) 2010  Daniel Pelaez, Daniel Lopez, Hector Hurtado
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

package co.edu.uniquindio.chord.node;

import java.math.BigInteger;

import co.edu.uniquindio.chord.protocol.Protocol;
import co.edu.uniquindio.chord.protocol.Protocol.BootStrapResponseParams;
import co.edu.uniquindio.chord.protocol.Protocol.GetPredecessorResponseParams;
import co.edu.uniquindio.chord.protocol.Protocol.GetSuccessorListResponseParams;
import co.edu.uniquindio.chord.protocol.Protocol.LookupParams;
import co.edu.uniquindio.chord.protocol.Protocol.LookupResponseParams;
import co.edu.uniquindio.chord.protocol.Protocol.PingResponseParams;
import co.edu.uniquindio.chord.protocol.Protocol.SetPredecessorParams;
import co.edu.uniquindio.chord.protocol.Protocol.SetSuccessorParams;
import co.edu.uniquindio.overlay.OverlayException;
import co.edu.uniquindio.utils.communication.Observer;
import co.edu.uniquindio.utils.communication.message.Message;
import co.edu.uniquindio.utils.communication.message.MessageXML;
import co.edu.uniquindio.utils.communication.message.Message.SendType;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManager;
import co.edu.uniquindio.utils.communication.transfer.CommunicationManagerCache;
import co.edu.uniquindio.utils.hashing.Key;
import org.apache.log4j.Logger;

/**
 * The <code>NodeEnvironment</code> class is the node responsible for handling
 * with the messages. This class is notified when a message arrives and decides
 * what the chord node must do. This class also initializes and starts the
 * {@link StableRing} of the node.
 * 
 * @author Daniel Pelaez
 * @author Hector Hurtado
 * @author Daniel Lopez
 * @version 1.0, 17/06/2010
 * @since 1.0
 * @see ChordNode
 */
class NodeEnvironment implements Observer<Message> {

	/**
	 * Logger
	 */
	private static final Logger logger = Logger
			.getLogger(NodeEnvironment.class);

	/**
	 * Communication manager
	 */
	private CommunicationManager communicationManager;

	/**
	 * Defines if an arrive message must be process.
	 */
	private boolean process = true;

	/**
	 * The reference of the chord node.
	 */
	private ChordNode chordNode;

	/**
	 * The thread that uses the commands for stabilizing the node.
	 */
	private StableRing stableRing;

	/**
	 * The constructor of the class. Sets the chord node reference and
	 * initializes the stable ring.
	 * 
	 * @param chordNode
	 *            The reference of the chord node.
	 */
	NodeEnvironment(ChordNode chordNode) {

		this.chordNode = chordNode;
		this.stableRing = new StableRing(chordNode);
		this.communicationManager = CommunicationManagerCache
				.getCommunicationManager(ChordNodeFactory.CHORD);
	}

	NodeEnvironment(ChordNode chordNode, CommunicationManager communicationManager, int stableRingTime) {

		this.chordNode = chordNode;
		this.stableRing = new StableRing(chordNode, stableRingTime, true);
		this.communicationManager = communicationManager;
	}

	NodeEnvironment(CommunicationManager communicationManager, ChordNode chordNode, StableRing stableRing) {
		this.communicationManager = communicationManager;
		this.chordNode = chordNode;
		this.stableRing = stableRing;
	}

	@Override
	/**
	 * This method is called when a new message has arrived.
	 * 
	 * @param message  The income message.
	 */
	public void update(Message message) {

		logger.debug("Message to '" + chordNode.getKey().getValue() + "', ["
				+ message.toString());

		/*
		 * Discards the message that comes if process==false
		 */
		if (!process) {
			return;
		}

		if (message.getType().equals(Protocol.LOOKUP.getName())) {
			processLookUp(message);
			return;
		}
		if (message.getType().equals(Protocol.PING.getName())) {
			processPing(message);
			return;
		}
		if (message.getType().equals(Protocol.NOTIFY.getName())) {
			processNotify(message);
			return;
		}
		if (message.getType().equals(Protocol.GET_PREDECESSOR.getName())) {
			processGetPredecessor(message);
			return;
		}
		if (message.getType().equals(Protocol.BOOTSTRAP.getName())) {
			processBootStrap(message);
			return;
		}
		if (message.getType().equals(Protocol.LEAVE.getName())) {
			processLeave(message);
			return;
		}
		if (message.getType().equals(Protocol.SET_PREDECESSOR.getName())) {
			processSetPredecessor(message);
			return;
		}
		if (message.getType().equals(Protocol.SET_SUCCESSOR.getName())) {
			processSetSuccessor(message);
			return;
		}
		if (message.getType().equals(Protocol.GET_SUCCESSOR_LIST.getName())) {
			processGetSuccessorList(message);
			return;
		}

	}

	/**
	 * Process message of type is GET_SUCCESSOR_LIST
	 * 
	 * @param message
	 *            Message GET_SUCCESSOR_LIST
	 */
	private void processGetSuccessorList(Message message) {
		String successorList;
		Message getSuccesorListResponseMessage;

		successorList = chordNode.getSuccessorList().toString();

		getSuccesorListResponseMessage = new MessageXML(message
				.getSequenceNumber(), SendType.RESPONSE,
				Protocol.GET_SUCCESSOR_LIST_RESPONSE, message
						.getMessageSource(), chordNode.getKey().getValue());
		getSuccesorListResponseMessage.addParam(
				GetSuccessorListResponseParams.SUCCESSOR_LIST.name(),
				successorList);

		communicationManager.sendMessageUnicast(getSuccesorListResponseMessage);
	}

	/**
	 * Process message of type is SET_SUCCESSOR
	 * 
	 * @param message
	 *            Message SET_SUCCESSOR
	 */
	private void processSetSuccessor(Message message) {
		Key nodeSucessor;

		nodeSucessor = new Key(message.getParam(SetSuccessorParams.SUCCESSOR
				.name()));

		chordNode.setSuccessor(nodeSucessor);
	}

	/**
	 * Process message of type is SET_PREDECESSOR
	 * 
	 * @param message
	 *            Message SET_PREDECESSOR
	 */
	private void processSetPredecessor(Message message) {
		Key nodePredecessor;

		nodePredecessor = new Key(message
				.getParam(SetPredecessorParams.PREDECESSOR.name()));

		chordNode.setPredecessor(nodePredecessor);
	}

	/**
	 * Process message of type is LEAVE
	 * 
	 * @param message
	 *            Message LEAVE
	 */
	private void processLeave(Message message) {

		Message setSuccessorMessage;
		Message setPredecessorMessage;

		/* Ends all stable threads */
		stableRing.setRun(false);
		stableRing = null;
		process = false;

		if (!chordNode.getSuccessor().equals(chordNode.getKey())) {

			setSuccessorMessage = new MessageXML(Protocol.SET_SUCCESSOR,
					chordNode.getPredecessor().getValue(), chordNode.getKey()
							.getValue());
			setSuccessorMessage.addParam(SetSuccessorParams.SUCCESSOR.name(),
					chordNode.getSuccessor().getValue());

			communicationManager.sendMessageUnicast(setSuccessorMessage);

			setPredecessorMessage = new MessageXML(Protocol.SET_PREDECESSOR,
					chordNode.getSuccessor().getValue(), chordNode.getKey()
							.getValue());
			setPredecessorMessage.addParam(SetPredecessorParams.PREDECESSOR
					.name(), chordNode.getPredecessor().getValue());

			communicationManager.sendMessageUnicast(setPredecessorMessage);
		}

		try {
			ChordNodeFactory.getInstance().destroyNode(
					this.chordNode.getKey().getValue());
		} catch (OverlayException e) {
			logger.error("Error while leaving node: '"
					+ chordNode.getKey().toString() + "'");
		}
	}

	/**
	 * Process message of type is BOOTSTRAP
	 * 
	 * @param message
	 *            Message BOOTSTRAP
	 */
	private void processBootStrap(Message message) {

		Message bootstrapResponseMessage;

		if (message.getMessageSource().equals(chordNode.getKey().getValue())) {
			return;
		}

		bootstrapResponseMessage = new MessageXML(message.getSequenceNumber(),
				SendType.RESPONSE, Protocol.BOOTSTRAP_RESPONSE, message
						.getMessageSource(), chordNode.getKey().getValue());
		bootstrapResponseMessage.addParam(BootStrapResponseParams.NODE_FIND
				.name(), chordNode.getKey().getValue());

		communicationManager.sendMessageUnicast(bootstrapResponseMessage);
	}

	/**
	 * Process message of type is GET_PREDECESSOR
	 * 
	 * @param message
	 *            Message GET_PREDECESSOR
	 */
	private void processGetPredecessor(Message message) {

		Message getPredecessorResponseMessage;

		getPredecessorResponseMessage = new MessageXML(message
				.getSequenceNumber(), SendType.RESPONSE,
				Protocol.GET_PREDECESSOR_RESPONSE, message.getMessageSource(),
				chordNode.getKey().getValue());

		if (chordNode.getPredecessor() == null) {

			getPredecessorResponseMessage.addParam(
					GetPredecessorResponseParams.PREDECESSOR.name(), null);

		} else {

			getPredecessorResponseMessage.addParam(
					GetPredecessorResponseParams.PREDECESSOR.name(), chordNode
							.getPredecessor().getValue());

		}

		communicationManager.sendMessageUnicast(getPredecessorResponseMessage);

	}

	/**
	 * Process message of type is NOTIFY
	 * 
	 * @param message
	 *            Message NOTIFY
	 */
	private void processNotify(Message message) {
		if (!message.isMessageFromMySelf()) {
			Key node;

			node = new Key(message.getMessageSource());

			chordNode.notify(node);
		}
	}

	/**
	 * Process message of type is PING
	 * 
	 * @param message
	 *            Message PING
	 */
	private void processPing(Message message) {

		Message pingMessage;

		pingMessage = new MessageXML(message.getSequenceNumber(),
				SendType.RESPONSE, Protocol.PING_RESPONSE, message
						.getMessageSource(), chordNode.getKey().getValue());
		pingMessage.addParam(PingResponseParams.PING.name(), Boolean.TRUE
				.toString());

		communicationManager.sendMessageUnicast(pingMessage);
	}

	/**
	 * Process message of type is LOOKUP
	 * 
	 * @param message
	 *            Message LOOKUP
	 */
	private void processLookUp(Message message) {

		Message lookupResponseMessage;

		lookupResponseMessage = new MessageXML(message.getSequenceNumber(),
				SendType.RESPONSE, Protocol.LOOKUP_RESPONSE, message
						.getMessageSource(), chordNode.getKey().getValue());
		lookupResponseMessage.addParam(LookupResponseParams.TYPE.name(),
				message.getParam(LookupParams.TYPE.name()));

		/* Discards the message that comes from the same node */
		if (message.isMessageFromMySelf()) {

			lookupResponseMessage.addParam(LookupResponseParams.NODE_FIND
					.name(), chordNode.getKey().getValue());

		} else {

			Key response;
			Key id;
			BigInteger hashing;

			hashing = new BigInteger(message.getParam(LookupParams.HASHING
					.name()));
			id = new Key(hashing);

			response = chordNode.findSuccessor(id, LookupType.valueOf(message
					.getParam(LookupParams.TYPE.name())));

			if (response == null) {

				lookupResponseMessage.addParam(LookupResponseParams.NODE_FIND
						.name(), chordNode.getKey().getValue());

			} else {

				lookupResponseMessage.addParam(LookupResponseParams.NODE_FIND
						.name(), response.getValue());

			}

		}

		communicationManager.sendMessageUnicast(lookupResponseMessage);
	}

	/**
	 * Starts the stable ring process.
	 */
	public void startStableRing() {
		stableRing.start();
	}

	/**
	 * Gets the reference of the chordNode
	 * 
	 * @return The {@link ChordNode} reference
	 */
	public ChordNode getChordNode() {
		return chordNode;
	}

	/**
	 * Gets chord node name from key value
	 */
	public String getName() {
		return chordNode.getKey().getValue();
	}

	void setProcess(boolean process) {
		this.process = process;
	}
}
