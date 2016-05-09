package login;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.security.auth.login.LoginException;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nl.tytech.core.client.net.ServicesManager;
import nl.tytech.core.net.event.UserServiceEventType;
import nl.tytech.core.util.SettingsManager;
import nl.tytech.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Execute login procedure. Store password if asked.
 * 
 * @author W.Pasman
 *
 */
public class Login {
	private static final String HASHEDPASS = "hashedpass";
	private static final String USERNAME = "username";
	String username;
	String hashedPass;
	boolean isSaved;

	Preferences prefs = Preferences.userNodeForPackage(Login.class);

	/**
	 * Log in to the server. Check if pass needs to be stored and store.
	 * 
	 * @throws LoginException
	 *             if login fails.
	 * @throws IOException 
	 */
	public Login() throws LoginException {
		String user_var = null;
		String pwd_var = null;
		BufferedReader in = null;
		// in app.properties are the username and password as specified through environment variables
		try {
			in = new BufferedReader(new FileReader("target/app.properties"));
		} catch (Exception e1) {
			// errors are contained here because fallback is asking user for the user/pwd
		}
		String line;
		try {
			while((line = in.readLine()) != null){
				if(line.contains("=") && !line.startsWith("#")){
					String[] splittedline = line.split("=");
					if (splittedline[0].equals("user")){
						user_var = splittedline[1];
					} else if(splittedline[0].equals("pwd")){
						pwd_var = splittedline[1];
					}
				}
			}
		} catch (Exception e) {
			// errors are contained here because fallback is asking user for the user/pwd
		}
		if(user_var == null || user_var.equals("undefined") || pwd_var == null || pwd_var.equals("undefined") ){
			// user and password information is not gotten through environment variables and if these values
			// are not saved a inputbox requesting user input will be spawned
			getCredentials();
			if (!isSaved) {
				passPrompt();
			}
			if (isSaved) {
				saveCredentials();
			}
		} else {
			// the user and password information is inputted through environment variables and these are used
			ServicesManager.setSessionLoginCredentials(user_var, pwd_var);
			username = user_var;
			hashedPass = ServicesManager.fireServiceEvent(UserServiceEventType.GET_MY_HASH_KEY);
			prefs.put(USERNAME, user_var);
			prefs.put(HASHEDPASS, hashedPass);
			SettingsManager.setStayLoggedIn(true);
		}
		ServicesManager.setSessionLoginCredentials(username, hashedPass, true);
	}

	private void getCredentials() {
		username = prefs.get(USERNAME, "");
		hashedPass = prefs.get(HASHEDPASS, "");
		isSaved = StringUtils.containsData(username) && StringUtils.containsData(hashedPass);
	}

	private void saveCredentials() {
		prefs.put(USERNAME, username);
		prefs.put(HASHEDPASS, hashedPass);
		SettingsManager.setStayLoggedIn(true);
	}

	/**
	 * Ask user for the credentials.
	 * 
	 * @throws LoginException
	 * 
	 * @throws IllegalStateException
	 *             if user cancels login procedure.
	 */
	private void passPrompt() throws LoginException {
		JPanel namepasspanel = new JPanel(new BorderLayout());
		JTextField name = new JTextField(20);
		JPasswordField pwd = new JPasswordField(20);
		JCheckBox save = new JCheckBox();
		namepasspanel.add(makeRow("name:", name), BorderLayout.NORTH);
		namepasspanel.add(makeRow("password:", pwd), BorderLayout.CENTER);
		namepasspanel.add(makeRow("save password", save), BorderLayout.SOUTH);
		int choice = JOptionPane.showConfirmDialog(null, namepasspanel, "Enter Tygron Name and Password",
				JOptionPane.OK_CANCEL_OPTION);

		if (choice == JOptionPane.CANCEL_OPTION) {
			throw new LoginException("User cancelled login.");
		}
		username = name.getText();
		String pass = new String(pwd.getPassword());
		ServicesManager.setSessionLoginCredentials(username, pass);
		hashedPass = ServicesManager.fireServiceEvent(UserServiceEventType.GET_MY_HASH_KEY);
		isSaved = save.isSelected();

	}

	/**
	 * Make a row with given label, and an input area
	 * 
	 * @param label
	 * @param inputarea
	 *            the {@link Component} - input area for user
	 * @return component
	 */
	private JPanel makeRow(String label, Component inputarea) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel(label), BorderLayout.WEST);
		panel.add(inputarea, BorderLayout.EAST);
		return panel;
	}

	public String getUserName() {
		return username;
	}

	public static String getServerIp() {
		return null;
	}

}
