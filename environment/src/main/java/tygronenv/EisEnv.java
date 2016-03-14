package tygronenv;

import static org.junit.Assert.assertNull;

import java.util.LinkedList;
import java.util.Map;

import eis.EIDefaultImpl;
import eis.eis2java.translation.Java2Parameter;
import eis.eis2java.translation.Parameter2Java;
import eis.eis2java.translation.Translator;
import eis.exceptions.ActException;
import eis.exceptions.ManagementException;
import eis.exceptions.NoEnvironmentException;
import eis.exceptions.PerceiveException;
import eis.iilang.Action;
import eis.iilang.EnvironmentState;
import eis.iilang.Parameter;
import eis.iilang.Percept;
import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.net.Network;
import nl.tytech.core.net.serializable.User;
import nl.tytech.core.net.serializable.User.AccessLevel;
import nl.tytech.core.util.SettingsManager;
import tygronenv.settings.Settings;
import tygronenv.translators.Configuration2J;
import tygronenv.translators.HashMap2J;
import tygronenv.translators.ParamEnum2J;

/**
 * Implements the Tygron EIS adapter
 * 
 * @author W.Pasman
 *
 */
public class EisEnv extends EIDefaultImpl {

	/**
	 * General initialization: translators,
	 */
	public EisEnv() {
		installTranslators();
	}

	@Override
	protected LinkedList<Percept> getAllPerceptsFromEntity(String entity)
			throws PerceiveException, NoEnvironmentException {
		return null;
	}

	@Override
	protected boolean isSupportedByEnvironment(Action action) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isSupportedByType(Action action, String type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean isSupportedByEntity(Action action, String entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Percept performEntityAction(String entity, Action action) throws ActException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(Map<String, Parameter> parameters) throws ManagementException {
		super.init(parameters);
		try {
			Configuration config = new Configuration(parameters);
		} catch (Exception e) {
			throw new ManagementException("Problem with init parameters", e);
		}
		connectServer();
		setState(EnvironmentState.RUNNING);
	}

	@Override
	public void kill() throws ManagementException {
		super.kill();
		disconnectServer();
	};

	@Override
	public boolean isStateTransitionValid(EnvironmentState oldState, EnvironmentState newState) {
		return true;
	}

	/************************* SUPPORT FUNCTIONS ****************************/

	Java2Parameter<?>[] j2p = new Java2Parameter<?>[] {};
	Parameter2Java<?>[] p2j = new Parameter2Java<?>[] { new Configuration2J(), new ParamEnum2J(), new HashMap2J() };

	/**
	 * Installs the required EIS2Java translators
	 */
	private void installTranslators() {
		Translator translatorfactory = Translator.getInstance();

		for (Java2Parameter<?> translator : j2p) {
			translatorfactory.registerJava2ParameterTranslator(translator);
		}
		for (Parameter2Java<?> translator : p2j) {
			translatorfactory.registerParameter2JavaTranslator(translator);
		}
	}

	/**
	 * Connect with the server, using the {@link Settings}.
	 * 
	 * @throws ManagementException
	 */
	private void connectServer() throws ManagementException {
		Settings credentials = new Settings();

		// setup settings
		SettingsManager.setup(SettingsManager.class, Network.AppType.EDITOR);
		SettingsManager.setServerIP(credentials.getServerIp());

		String result = ServicesManager.testServerConnection();
		assertNull(result, result);

		ServicesManager.setSessionLoginCredentials(credentials.getUserName(), credentials.getPassword());
		User user = ServicesManager.getMyUserAccount();

		if (user == null) {
			throw new ManagementException("failed to attach user" + credentials.getUserName()
					+ ". Wrong name/pass? Please check the configuration.cfg file");
		}

		if (user.getMaxAccessLevel().ordinal() < AccessLevel.EDITOR.ordinal()) {
			throw new ManagementException("You need to have at least EDITOR access level");
		}

	}

	private void disconnectServer() {
		throw new IllegalStateException("NOT IMPLEMENTED");
		// assertTrue(ServicesManager.fireServiceEvent(IOServiceEventType.DELETE_PROJECT,
		// data.getFileName()));
	}

}