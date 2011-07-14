/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.gui.wizard;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.uniluebeck.itm.spyglass.SpyglassApp;
import de.uniluebeck.itm.spyglass.io.wisebed.WisebedPacketReader;
import de.uniluebeck.itm.spyglass.io.wisebed.WisebedPacketReaderXMLConfig;
import de.uniluebeck.itm.tr.util.UrlUtils;
import de.uniluebeck.itm.wisebed.cmdlineclient.BeanShellHelper;
import eu.wisebed.api.common.KeyValuePair;
import eu.wisebed.api.rs.*;
import eu.wisebed.api.sm.SecretReservationKey;
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
import javax.xml.ws.Holder;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Wizard for creating a PacketReader instance connected to WISEBED testbeds.
 *
 * @author Jens Kluttig, Daniel Bimschas
 */
public class WisebedPacketReaderConfigurationWizard extends Wizard {

	private static final Logger log = LoggerFactory.getLogger(WisebedPacketReaderConfigurationWizard.class);

	private static final DatatypeFactory datatypeFactory;

	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	private class SessionManagementEndpointUrlPage extends WizardPage implements IExtendedWizardPage {

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
					WisebedPacketReaderConfigurationWizard.this.config, WisebedPacketReaderXMLConfig.PROPERTYNAME_SM_ENDPOINT_URL
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

		private void fetchSnaaAndRsEndpointUrls() {

			SessionManagement sessionManagement = WSNServiceHelper.getSessionManagementService(
					sessionManagementText.getText()
			);

			final Holder<String> rsEndpointUrl = new Holder<String>();
			final Holder<String> snaaEndpointUrl = new Holder<String>();
			final Holder<List<KeyValuePair>> configurationOptions = new Holder<List<KeyValuePair>>();

			sessionManagement.getConfiguration(rsEndpointUrl, snaaEndpointUrl, configurationOptions);

			WisebedPacketReaderConfigurationWizard.this.config.setRsEndpointUrl(rsEndpointUrl.value);
			WisebedPacketReaderConfigurationWizard.this.config.setSnaaEndpointUrl(snaaEndpointUrl.value);
		}

	}

	private class AuthenticationPage extends WizardPage implements IExtendedWizardPage {

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

	private class SelectReservationPage extends WizardPage implements IExtendedWizardPage {

		private static final int RESERVATION_TABLE_COLUMN_FROM = 0;

		private static final int RESERVATION_TABLE_COLUMN_TO = 1;

		private static final int RESERVATION_TABLE_COLUMN_NODE_URNS = 2;

		private Table reservationsTable;

		private TableColumn[] columns;

		private java.util.List<ConfidentialReservationData> reservationList = Lists.newArrayList();

		protected SelectReservationPage(String pageName) {
			super(pageName);
			setMessage("Please choose the experiment reservation you want to connect to.\nDisplaying all reservations of the next 24 hours.");
		}

		@Override
		public void createControl(Composite composite) {

			Composite contentComposite = new Composite(composite, SWT.FILL);
			contentComposite.setLayout(new GridLayout(1, false));
			GridData contentCompositeLayoutData = new GridData(SWT.FILL);
			contentCompositeLayoutData.grabExcessHorizontalSpace = true;
			contentComposite.setData(contentCompositeLayoutData);

			GridData reservationsTableLayoutData = new GridData(GridData.FILL);
			reservationsTableLayoutData.grabExcessHorizontalSpace = true;
			reservationsTableLayoutData.grabExcessVerticalSpace = true;
			reservationsTableLayoutData.horizontalAlignment = GridData.FILL;
			reservationsTableLayoutData.verticalAlignment = GridData.FILL;

			reservationsTable = new Table(contentComposite, SWT.SINGLE);
			reservationsTable.setLayoutData(reservationsTableLayoutData);
			reservationsTable.setHeaderVisible(true);
			reservationsTable.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent selectionEvent) {
					setPageComplete(reservationsTable.getSelectionCount() == 1);
				}
			}
			);

			TableColumn fromColumn = new TableColumn(reservationsTable, SWT.NONE);
			fromColumn.setText("From");

			TableColumn toColumn = new TableColumn(reservationsTable, SWT.NONE);
			toColumn.setText("To");

			TableColumn nodeUrnsColumn = new TableColumn(reservationsTable, SWT.NONE);
			nodeUrnsColumn.setText("Node URNs");

			columns = new TableColumn[3];
			columns[RESERVATION_TABLE_COLUMN_FROM] = fromColumn;
			columns[RESERVATION_TABLE_COLUMN_TO] = toColumn;
			columns[RESERVATION_TABLE_COLUMN_NODE_URNS] = nodeUrnsColumn;

			Button refreshButton = new Button(contentComposite, SWT.NORMAL);
			refreshButton.setText("Refresh");
			refreshButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent selectionEvent) {
					clearReservations();
					fillReservations();
				}
			});
			GridData refreshButtonLayoutData = new GridData();
			refreshButtonLayoutData.horizontalAlignment = GridData.END;
			refreshButton.setData(refreshButtonLayoutData);

			setControl(contentComposite);
		}

		public void clearReservations() {
			reservationsTable.removeAll();
			reservationsTable.clearAll();
			reservationList.clear();
		}

		public ConfidentialReservationData getSelectedReservation() {
			if (reservationsTable.getSelectionIndex() >= 0) {
				return reservationList.get(reservationsTable.getSelectionIndex());
			} else {
				return null;
			}
		}

		public void addReservation(ConfidentialReservationData data) {

			String from = new DateTime(data.getFrom().toGregorianCalendar()).toString("dd.MM.yyyy HH:mm");
			String to = new DateTime(data.getTo().toGregorianCalendar()).toString("dd.MM.yyyy HH:mm");
			String nodeUrns = Joiner.on(",").join(data.getNodeURNs());

			TableItem item = new TableItem(reservationsTable, SWT.NONE);
			item.setText(new String[]{from, to, nodeUrns});

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

	private final Map<AuthenticationTriple, SecretAuthenticationKey> authMap = Maps.newHashMap();

	private WisebedPacketReaderXMLConfig config;

	private SelectReservationPage selectReservationPage;

	private AuthenticationPage authenticationPage;

	private SessionManagementEndpointUrlPage sessionManagementEndpointUrlPage;

	public WisebedPacketReaderConfigurationWizard() {
		super();

		config = SpyglassApp.spyglass.getConfigStore().getSpyglassConfig().getWisebedPacketReaderSettings();

		setWindowTitle("Wisebed Testbed Packet Source Configuration");

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
			MessageDialog.openWarning(
					getShell(),
					"Warning",
					"Please fill in the Session Management service endpoint URL."
			);
			return false;
		}

		if (!selectReservationPage.isComplete()) {
			MessageDialog.openWarning(
					getShell(),
					"Warning",
					"Please select a reservation."
			);
			return false;
		}

		String localControllerEndpointUrl = tryToAutoDetectLocalControllerEndpointUrl();

		if (localControllerEndpointUrl == null) {
			localControllerEndpointUrl = tryToGetLocalControllerEndpointUrlFromUser();
		}

		if (localControllerEndpointUrl == null) {
			return false;
		}

		WisebedPacketReaderConfigurationWizard.this.config.setControllerEndpointUrl(localControllerEndpointUrl);

		ConfidentialReservationData selectedReservation = selectReservationPage.getSelectedReservation();
		final List<SecretReservationKey> secretReservationKeys = newArrayList();
		for (Data data : selectedReservation.getData()) {
			final SecretReservationKey srk = new SecretReservationKey();
			srk.setUrnPrefix(data.getUrnPrefix());
			srk.setSecretReservationKey(data.getSecretReservationKey());
			secretReservationKeys.add(srk);
		}

		final WisebedPacketReader wisebedPacketReader = new WisebedPacketReader(
				config.getControllerEndpointUrl(),
				config.getSmEndpointUrl(),
				secretReservationKeys
		);

		SpyglassApp.spyglass.getConfigStore().getSpyglassConfig().setPacketReader(wisebedPacketReader);
		SpyglassApp.spyglass.getConfigStore().store();

		return true;
	}

	@Override
	public boolean canFinish() {
		return sessionManagementEndpointUrlPage.isComplete() &&
				authenticationPage.isComplete() &&
				selectReservationPage.isComplete();
	}

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
		final DateTime until = from.plusHours(24);

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

		if (selectReservationPage.reservationsTable.getItemCount() > 0) {
			selectReservationPage.reservationsTable.select(0);
		}
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

}
