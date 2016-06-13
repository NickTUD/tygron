package contextvh;

import java.util.LinkedList;
import java.util.List;

import contextvh.actions.ActionContainer;
import contextvh.actions.CustomAction;
import contextvh.actions.FilterPercepts;
import eis.eis2java.exception.TranslationException;
import eis.iilang.Action;
import eis.iilang.Percept;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.client.net.TSlotConnection;
import nl.tytech.core.net.Network.AppType;
import nl.tytech.core.net.event.IOServiceEventType;
import nl.tytech.core.net.serializable.JoinReply;
import nl.tytech.core.util.SettingsManager;
import tygronenv.EntityEventHandler;
import tygronenv.EntityListener;

/**
 * Created on 4-6-2016.
 *
 * @author Stefan Breetveld.
 */
public class ContextEntity extends tygronenv.TygronEntityImpl {

	private TSlotConnection slotConnection;
	private JoinReply joinedConfirm;

	private ActionContainer customActions = new ActionContainer();
	
	private EntityEventHandler eventHandler;

	/**
	 * Create new Tygron entity. It will report to env when the entity is ready
	 * to run. This happens when initial percepts have been prepared and the
	 * name matches one of the actual stakeholder names
	 *
	 * @param env                 the environment to report back to.
	 * @param intendedStakeholder the intended stakeholder name. If null, any name is ok.
	 * @param slotID              the intended slot to use. If null any slot is ok.
	 */
	public ContextEntity(final EntityListener env, final String intendedStakeholder, final Integer slotID) {
		super(env, intendedStakeholder, slotID);
	}

	@Override
	public Percept performAction(final Action action) throws TranslationException {
		CustomAction customAction = customActions.get(action);
		if (customAction != null) {
			return customAction.call(this, action.getParameters());
		} else {
			return super.performAction(action);
		}
	}

	@Override
	public tygronenv.EntityEventHandler createEntityEventhandler(final TSlotConnection slotCon) {
		this.slotConnection = slotCon;
		return new ContextEntityEventHandler(this, slotCon.getConnectionID(), this);
	}
	
	@Override
	public LinkedList<Percept> getPercepts() {
		LinkedList<Percept> allPercepts = super.getPercepts();
		FilterPercepts filter = (FilterPercepts) customActions.get("FilterPercepts");
		for (List<Percept> percepts : eventHandler.getPercepts().values()) {
			allPercepts.addAll(percepts);
		}
		return allPercepts;
	}



	/**
	 * Returns the saved TSlotConnection.
	 * @return The saved TSlotConnection.
	 */
	public TSlotConnection getSlotConnection() {
		return slotConnection;
	}
}
