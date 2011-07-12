/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.gui.wizard;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.uniluebeck.itm.spyglass.SpyglassApp;
import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.io.wisebed.TestbedXMLConfig;
import de.uniluebeck.itm.spyglass.io.wisebed.WSNPacketReader;
import de.uniluebeck.itm.tr.util.UrlUtils;
import de.uniluebeck.itm.wisebed.cmdlineclient.BeanShellHelper;
import eu.wisebed.api.common.KeyValuePair;
import eu.wisebed.api.rs.ConfidentialReservationData;
import eu.wisebed.api.rs.GetReservations;
import eu.wisebed.api.rs.RS;
import eu.wisebed.api.rs.RSExceptionException;
import eu.wisebed.api.sm.SessionManagement;
import eu.wisebed.api.snaa.AuthenticationTriple;
import eu.wisebed.api.snaa.SNAA;
import eu.wisebed.api.snaa.SecretAuthenticationKey;
import eu.wisebed.testbed.api.rs.RSServiceHelper;
import eu.wisebed.testbed.api.snaa.helpers.SNAAServiceHelper;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Wizard for creating connection to Testbed
 *
 * @author jens kluttig
 */
public class TestbedWizard extends Wizard {

	private static final Logger log = LoggerFactory.getLogger(TestbedWizard.class);

	private static final DatatypeFactory datatypeFactory;

	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private TestbedXMLConfig config;

	private ConfigStore store;

	private final Map<AuthenticationTriple, SecretAuthenticationKey> authMap = Maps.newHashMap();

	private SelectReservationPage selectReservationPage;

	private AuthenticationPage authenticationPage;

	private SessionManagementEndpointUrlPage sessionManagementEndpointUrlPage;

	public TestbedWizard() {
		super();
		try {
			store = SpyglassApp.spyglass.getConfigStore();
			config = store.getSpyglassConfig().getTestbedSettings();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setWindowTitle("Testbed Configuration");
		sessionManagementEndpointUrlPage = new SessionManagementEndpointUrlPage("Testbed URLs");
		authenticationPage = new AuthenticationPage("Authorization");
		selectReservationPage = new SelectReservationPage("Reservation");

		addPage(sessionManagementEndpointUrlPage);
		addPage(authenticationPage);
		addPage(selectReservationPage);
	}

	@Override
	public boolean performFinish() {
		if (!sessionManagementEndpointUrlPage.isComplete()) {
			MessageDialog
					.openWarning(getShell(), "Warning", "Please fill in the Session Management service endpoint URL.");
			return false;
		}
		if (!selectReservationPage.isComplete()) {
			MessageDialog.openWarning(getShell(), "Warning", "Please select a reservation.");
			return false;
		}
		String localControllerEndpointUrl = tryToAutoDetectLocalControllerEndpointUrl();
		if (localControllerEndpointUrl == null) {
			localControllerEndpointUrl = tryToGetLocalControllerEndpointUrlFromUser();
		}
		if (localControllerEndpointUrl == null) {
			return false;
		} else {
			TestbedWizard.this.config.setControllerEndpointUrl(localControllerEndpointUrl);
		}
		WSNPacketReader wsnPacketReader = WSNPacketReader.createInstance(
				config.getSmEndpointUrl(), config.getControllerEndpointUrl(),
				selectReservationPage.getSelectedReservation()
		);
		log.info("WSNPacketReader created by TestbedWizard.");
		if (wsnPacketReader == null) {
			return false;
		}
		SpyglassApp.spyglass.getConfigStore().getSpyglassConfig().setPacketReader(wsnPacketReader);
		store.store();
		return true;
	}

	private String tryToGetLocalControllerEndpointUrlFromUser() {

		String localControllerEndpointUrl = askLocalControllerEndpointUrlFromUser();
		if (localControllerEndpointUrl != null) {
			try {
				URL url = new URL(localControllerEndpointUrl);
				if (checkIfServerSocketCanBeOpened(url.getHost(), url.getPort())) {
					return localControllerEndpointUrl;
				}
				return null;
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	private String tryToAutoDetectLocalControllerEndpointUrl() {
		Vector<String> externalHostIps = BeanShellHelper.getExternalHostIps();
		final String localControllerEndpointUrl;
		String host = externalHostIps.get(0);
		int port = UrlUtils.getRandomUnprivilegedPort();
		int tries = 0;
		while (!checkIfServerSocketCanBeOpened(host, port)) {
			if (++tries == 3) {
				break;
			}
			int oldPort = port;
			port = UrlUtils.getRandomUnprivilegedPort();
			log.warn("Could not open ServerSocket on {}:{}. Retrying on port {}!", new Object[]{host, oldPort, port});
		}

		localControllerEndpointUrl = "http://" + host + ":" + port + "/controller";
		return tries == 3 ? null : localControllerEndpointUrl;
	}

	private boolean checkIfServerSocketCanBeOpened(String host, int port) {
		try {
			ServerSocket socket = new ServerSocket();
			socket.bind(new InetSocketAddress(host, port));
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	private String askLocalControllerEndpointUrlFromUser() {
		final InputDialog inputDialog = new InputDialog(
				null,
				"Please provide the local controller endpoint URL.",
				"Spyglass was unable to detect the external public IP of your computer. Please provide an "
						+ "endpoint URL for this computer which can be contacted from outside. The host and port "
						+ "in this URL must not be behind a firewall or NAT in order to work.",
				"http://REPLACE_WITH_FQDN.TLD:PORT/",
				new IInputValidator() {
					@Override
					public String isValid(final String s) {
						try {
							new URL(s);
							return null;
						} catch (MalformedURLException e) {
							return "The string \"" + s + "\" is not a valid URL!";
						}
					}
				}
		);
		inputDialog.setBlockOnOpen(true);
		int open = inputDialog.open();
		if (SWT.OK == open) {
			return inputDialog.getValue();
		}
		return null;
	}

	@Override
	public boolean canFinish() {
		return sessionManagementEndpointUrlPage.isComplete() &&
				authenticationPage.isComplete() &&
				selectReservationPage.isComplete();
	}

	/**
	 * Wizardpage for Service-Urls
	 */
	public class SessionManagementEndpointUrlPage extends WizardPage implements IExtendedWizardPage {

		private Text sessionManagementText;

		protected SessionManagementEndpointUrlPage(String pageName) {
			super(pageName);
			setMessage(
					"Please enter the endpoint URL of the Session Management service of the testbed you want to "
							+ "connect to."
			);
		}

		@Override
		public void createControl(Composite composite) {

			final GridLayout gridLayout = new GridLayout();
			gridLayout.makeColumnsEqualWidth = true;
			gridLayout.numColumns = 2;

			final Composite area = new Composite(composite, SWT.NONE);
			area.setLayout(gridLayout);

			final Label smLabel = new Label(area, SWT.NONE);
			smLabel.setText("Session Management Service Endpoint URL");

			sessionManagementText = new Text(area, SWT.BORDER);
			sessionManagementText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			setControl(area);

			final DataBindingContext context = new DataBindingContext(SWTObservables.getRealm(composite.getDisplay()));

			IObservableValue smUrlObservable = PojoObservables.observeValue(
					SWTObservables.getRealm(composite.getDisplay()),
					TestbedWizard.this.config, TestbedXMLConfig.PROPERTYNAME_SM_ENDPOINT_URL
			);

			context.bindValue(
					SWTObservables.observeText(sessionManagementText, SWT.Modify),
					smUrlObservable,
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE)
			);
		}

		@Override
		public void nextPressed() {
			StringBuilder builder = new StringBuilder();
			if (StringUtils.isBlank(sessionManagementText.getText())) {
				builder.append("URN of Session Management Service is missing!").append("\n");
			}
			if (StringUtils.isNotBlank(builder.toString())) {
				MessageDialog.openWarning(getShell(), "Warning", builder.toString());
			}
			fetchSnaaAndRsEndpointUrls();
		}

		private void fetchSnaaAndRsEndpointUrls() {

			SessionManagement sessionManagement = WSNServiceHelper.getSessionManagementService(
					sessionManagementText.getText()
			);

			final Holder<String> rsEndpointUrl = new Holder<String>();
			final Holder<String> snaaEndpointUrl = new Holder<String>();
			final Holder<List<KeyValuePair>> configurationOptions = new Holder<List<KeyValuePair>>();

			sessionManagement.getConfiguration(rsEndpointUrl, snaaEndpointUrl, configurationOptions);

			TestbedWizard.this.config.setRsEndpointUrl(rsEndpointUrl.value);
			TestbedWizard.this.config.setSnaaEndpointUrl(snaaEndpointUrl.value);
		}

		@Override
		public boolean isComplete() {
			boolean notBlank = StringUtils.isNotBlank(sessionManagementText.getText());
			if (notBlank) {
				String smEndpointUrl = sessionManagementText.getText();
				try {
					new URL(smEndpointUrl);
				} catch (MalformedURLException e) {
					return false;
				}
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * WizardPage for Authentication Data
	 */
	public class AuthenticationPage extends WizardPage implements IExtendedWizardPage {

		protected AuthenticationPage(String pageName) {
			super(pageName);
			setMessage("Please provide your authentication credentials.");
		}

		@Override
		public void createControl(Composite composite) {
			final Composite area = new Composite(composite, SWT.NONE);
			final GridLayout gridLayout = new GridLayout();
			gridLayout.makeColumnsEqualWidth = true;
			gridLayout.numColumns = 1;
			area.setLayout(gridLayout);
			final Composite upperArea = new Composite(area, SWT.NONE);
			final GridLayout upperLayout = new GridLayout();
			upperLayout.makeColumnsEqualWidth = true;
			upperLayout.numColumns = 3;
			upperArea.setLayout(upperLayout);
			upperArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			final Label urnLabel = new Label(upperArea, SWT.NONE);
			urnLabel.setText("URN-Prefix");
			final Label userLabel = new Label(upperArea, SWT.NONE);
			userLabel.setText("Username");
			final Label pwLabel = new Label(upperArea, SWT.NONE);
			pwLabel.setText("Password");
			final Text urnText = new Text(upperArea, SWT.BORDER);
			urnText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			urnText.setText(config.getUrnPrefix() == null ? "" : config.getUrnPrefix());
			final Text userText = new Text(upperArea, SWT.BORDER);
			userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			userText.setText(config.getUsername() == null ? "" : config.getUsername());
			final Text pwText = new Text(upperArea, SWT.BORDER);
			pwText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			pwText.setText(config.getPassword() == null ? "" : config.getPassword());
			pwText.setEchoChar('*');
			Label none1 = new Label(upperArea, SWT.NONE);
			Label none2 = new Label(upperArea, SWT.NONE);
			final Button loginButton = new Button(upperArea, SWT.NONE);
			loginButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
			loginButton.setText("add to Authentication Data");

			final Group lowerArea = new Group(area, SWT.NONE);
			lowerArea.setLayout(gridLayout);
			lowerArea.setText("Authentication Data");
			lowerArea.setLayoutData(new GridData(GridData.FILL_BOTH));
			final Table rsList = new Table(lowerArea, SWT.NONE);
			rsList.setHeaderVisible(true);
			rsList.setLayoutData(new GridData(GridData.FILL_BOTH));
			final TableColumn[] columns = new TableColumn[2];
			TableColumn urnColumn = new TableColumn(rsList, SWT.NONE);
			urnColumn.setText("URN-Prefix");
			columns[0] = urnColumn;
			TableColumn usrColumn = new TableColumn(rsList, SWT.NONE);
			usrColumn.setText("Username");
			columns[1] = usrColumn;

			setControl(area);

			loginButton.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					AuthenticationTriple triple = new AuthenticationTriple();
					triple.setPassword(pwText.getText());
					triple.setUrnPrefix(urnText.getText());
					triple.setUsername(userText.getText());
					for (AuthenticationTriple at : authMap.keySet()) {
						if (at.getUrnPrefix().equalsIgnoreCase(triple.getUrnPrefix()) &&
								at.getUsername().equalsIgnoreCase(triple.getUsername())) {
							MessageDialog.openInformation(getShell(), "Information",
									"User already added to Authentication Data"
							);
							return;
						}
					}
					if (authenticate(triple)) {
						TableItem item = new TableItem(rsList, SWT.NONE);
						item.setText(new String[]{urnText.getText(), userText.getText()});
						for (int i = 0, n = columns.length; i < n; i++) {
							columns[i].pack();
						}

						config.setUrnPrefix(urnText.getText());
						urnText.setText("");

						config.setUsername(userText.getText());
						userText.setText("");

						config.setPassword(pwText.getText());
						pwText.setText("");
					} else {
						MessageDialog.openWarning(getShell(), "Fehler", "Authentication Error");
					}

				}
			}

			);
		}

		@Override
		public void nextPressed() {
			fillReservations();
		}

		@Override
		public boolean isComplete() {
			return !authMap.isEmpty();
		}
	}

	/**
	 * WizardPage for Selecting Reservation
	 */
	public class SelectReservationPage extends WizardPage implements IExtendedWizardPage {

		private Table rsList;

		private TableColumn[] columns;

		private java.util.List<ConfidentialReservationData> reservationList = Lists.newArrayList();

		protected SelectReservationPage(String pageName) {
			super(pageName);
			setMessage("Please choose the experiment reservation you want to connect to.");
		}

		@Override
		public void createControl(Composite composite) {
			rsList = new Table(composite, SWT.SINGLE);
			rsList.setLayoutData(new GridData(GridData.FILL));
			rsList.setHeaderVisible(true);
			columns = new TableColumn[4];
			TableColumn fromColumn = new TableColumn(rsList, SWT.NONE);
			fromColumn.setText("From");
			columns[0] = fromColumn;
			TableColumn toColumn = new TableColumn(rsList, SWT.NONE);
			toColumn.setText("To");
			columns[1] = toColumn;
			TableColumn nodeColumn = new TableColumn(rsList, SWT.NONE);
			nodeColumn.setText("Node URNs");
			columns[2] = nodeColumn;
			TableColumn dataColumn = new TableColumn(rsList, SWT.NONE);
			dataColumn.setText("UserData");
			columns[3] = dataColumn;
			setControl(rsList);
			rsList.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent selectionEvent) {
					setPageComplete(rsList.getSelectionCount() == 1);
				}
			}
			);
		}

		public void clearReservations() {
			rsList.removeAll();
			rsList.clearAll();
			reservationList.clear();
		}

		public ConfidentialReservationData getSelectedReservation() {
			if (rsList.getSelectionIndex() >= 0) {
				return reservationList.get(rsList.getSelectionIndex());
			} else {
				return null;
			}
		}

		private String toStringWithSeperator(java.util.List list, String sep) {
			StringBuilder builder = new StringBuilder();
			for (Object o : list) {
				builder.append(o.toString()).append(sep);
			}
			return builder.toString();
		}

		private String printDate(XMLGregorianCalendar calendar) {
			return String.format("%d-%d-%d %d:%d", calendar.getYear(), calendar.getMonth(), calendar.getDay(),
					calendar.getHour(), calendar.getMinute()
			);
		}

		public void addReservation(ConfidentialReservationData data) {
			TableItem item = new TableItem(rsList, SWT.NONE);
			item.setText(new String[]{
					printDate(data.getFrom()), printDate(data.getTo()),
					toStringWithSeperator(data.getNodeURNs(), ";"), data.getUserData()
			}
			);
			for (int i = 0, n = columns.length; i < n; i++) {
				columns[i].pack();
			}
			reservationList.add(data);
		}

		@Override
		public void nextPressed() {
		}

		@Override
		public boolean isComplete() {
			return getSelectedReservation() != null;
		}
	}

	/**
	 * Authenticates User with his Authentication Triple (URN-Prefix, Username, Password)
	 *
	 * @param triple
	 *
	 * @return
	 */
	private boolean authenticate(AuthenticationTriple triple) {
		SNAA snaa = SNAAServiceHelper.getSNAAService(config.getSnaaEndpointUrl());
		java.util.List<SecretAuthenticationKey> list = null;
		try {
			list = snaa.authenticate(ImmutableList.of(triple));
		} catch (Exception e) {
			return false;
		}
		authMap.put(triple, list.get(0));
		return true;

	}


	private void fillReservations() {

		final RS rs = RSServiceHelper.getRSService(config.getRsEndpointUrl());
		final GetReservations getReservationsRequest = new GetReservations();
		final DateTime from = new DateTime();
		final DateTime until = from.plusHours(1);

		getReservationsRequest.setFrom(datatypeFactory.newXMLGregorianCalendar(from.toGregorianCalendar()));
		getReservationsRequest.setTo(datatypeFactory.newXMLGregorianCalendar(until.toGregorianCalendar()));

		List<eu.wisebed.api.rs.SecretAuthenticationKey> secretAuthenticationKeys =
				BeanShellHelper.copySnaaToRs(Lists.<SecretAuthenticationKey>newArrayList(authMap.values()));

		final List<ConfidentialReservationData> rsData;
		try {
			rsData = rs.getConfidentialReservations(secretAuthenticationKeys, getReservationsRequest);
		} catch (RSExceptionException e) {
			throw new RuntimeException(e);
		}

		selectReservationPage.clearReservations();

		for (ConfidentialReservationData data : rsData) {
			selectReservationPage.addReservation(data);
		}
	}

}
