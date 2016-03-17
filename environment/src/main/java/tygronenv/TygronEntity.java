package tygronenv;

import java.util.LinkedList;
import java.util.List;

import eis.iilang.Action;
import eis.iilang.Percept;
import nl.tytech.core.client.event.EventManager;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.SlotConnection;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.net.serializable.MapLink;
import nl.tytech.core.structure.ItemMap;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.data.engine.event.ParticipantEventType;
import nl.tytech.data.engine.item.Stakeholder;

/**
 * the 'participant' - a single stakeholder connection. Handles events coming in
 * for this stakeholder
 * 
 * @author W.Pasman
 *
 */
public class TygronEntity {

	private SlotConnection slotConnection = null;
	/**
	 * the confirmation from the server that we joined.
	 */
	private JoinReply joinedConfirm;
	private EntityEventHandler eventHandler;

	/**
	 * 
	 * @param stakeholder
	 * @param slotID
	 *            the slot ID of the team.
	 */
	public TygronEntity(Stakeholder.Type stakeholdertype, Integer slotID) {
		try {
			eventHandler = new EntityEventHandler();
			getSlotConnection(slotID);
			eventHandler.waitForReady();
			Stakeholder stakeholder = getStakeholder(stakeholdertype);
			if (stakeholder == null) {
				throw new IllegalArgumentException("Stakeholder of type " + stakeholdertype + " is not available");
			}
			slotConnection.fireServerEvent(true, ParticipantEventType.STAKEHOLDER_SELECT, stakeholder.getID(),
					joinedConfirm.client.getClientToken());
		} catch (Exception e) {
			close(); // constructor fails, close down properly
			throw e;
		}
	}

	/**
	 * Select given stakeholder.
	 * 
	 * @param intendedStakeHolder
	 *            the stakeholder to use. Or null if any stakeholder is ok.
	 * @return stakeholder, or null if requested type is not available.
	 */
	private Stakeholder getStakeholder(Stakeholder.Type intendedStakeHolder) {

		ItemMap<Stakeholder> stakeholders = EventManager.getItemMap(MapLink.STAKEHOLDERS);

		if (intendedStakeHolder == null) {
			// pick the first
			return stakeholders.toList(0).get(0);
		}

		for (Stakeholder holder : stakeholders) {
			if (holder.getType().equals(intendedStakeHolder)) {
				return holder;
			}
		}
		return null;
	}

	/**
	 * Join the existing session on the given slot.
	 * 
	 * @param slotID
	 *            the slot where our team is on.
	 */
	private void getSlotConnection(Integer slotID) {
		joinedConfirm = ServicesManager.fireServiceEvent(IOServiceEventType.JOIN_SESSION, slotID, AppType.PARTICIPANT);
		if (joinedConfirm == null) {
			throw new IllegalStateException("Failed to join session " + slotID + " as participant");
		}

		slotConnection = new SlotConnection();
		slotConnection.initSettings(AppType.PARTICIPANT, SettingsManager.getServerIP(), slotID,
				joinedConfirm.serverToken, joinedConfirm.client.getClientToken());

		if (!slotConnection.connect()) {
			throw new IllegalStateException("Failed to connect slotConnection");
		}

	}

	/**
	 * Close server connection.
	 */
	public void close() {
		eventHandler.stop();
		eventHandler = null;
		if (slotConnection != null) {
			slotConnection.disconnect(false);
			slotConnection = null;
		}
	}

	public LinkedList<Percept> getPercepts() {
		LinkedList<Percept> allPercepts = new LinkedList<Percept>();
		for (List<Percept> percepts : eventHandler.getPercepts().values()) {
			allPercepts.addAll(percepts);
		}
		return allPercepts;
	}

	/**
	 * Perform given action
	 * 
	 * @param action
	 *            action of the form action(p1,...). Action must be a
	 *            {@link ParticipantEventType}. The arguments are those as
	 *            specified in {@link ParticipantEventType}, except that the
	 *            obligatory first argument "Stakeholder ID" is automatically
	 *            filled in so you can leave that out and start with parameter
	 *            2.
	 * @return
	 */
	public Percept performAction(Action action) {
		/**
		 * Action is of the form 'BUILDING_PLAN_CONSTRUCTION'(p1,p2,p3...).
		 * 
		 * We must call something like this: <code>
		Integer newBuildingID = slotConnection.fireServerEvent(true, ParticipantEventType.BUILDING_PLAN_CONSTRUCTION,
				stakeholderID, functionID, floors, roadMultiPolygon);
				</code>
		 */
		// Check the arguments and translate.

		return null;
	}
}