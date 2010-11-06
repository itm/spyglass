/*----------------------------------------------------------------------------------------
 * This file is part of the
 * WSN visualization framework SpyGlass. Copyright (C) 2004-2007 by the SwarmNet (www.swarmnet.de)
 * project SpyGlass is free software; you can redistribute it and/or modify it under the terms of
 * the BSD License. Refer to spyglass-licence.txt file in the root of the SpyGlass source tree for
 * further details.
 * ---------------------------------------------------------------------------------------
 */

package de.uniluebeck.itm.spyglass.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import de.uniluebeck.itm.spyglass.SpyglassApp;
import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.gui.wizard.IExtendedWizardPage;
import de.uniluebeck.itm.spyglass.io.WSNPacketReader;
import de.uniluebeck.itm.spyglass.xmlconfig.TestbedXMLConfig;
import eu.wisebed.testbed.api.rs.RSServiceHelper;
import eu.wisebed.testbed.api.rs.v1.ConfidentialReservationData;
import eu.wisebed.testbed.api.rs.v1.RS;
import eu.wisebed.testbed.api.rs.v1.RSExceptionException;
import eu.wisebed.testbed.api.snaa.helpers.SNAAServiceHelper;
import eu.wisebed.testbed.api.snaa.v1.AuthenticationTriple;
import eu.wisebed.testbed.api.snaa.v1.SNAA;
import eu.wisebed.testbed.api.snaa.v1.SecretAuthenticationKey;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Map;

/**
 * Wizard for creating connection to Testbed
 *
 * @author jens kluttig
 */
public class TestbedWizard extends Wizard {

	private static final Logger log = LoggerFactory.getLogger(TestbedWizard.class);

	private TestbedXMLConfig config;

	private ConfigStore store;

	private Map<AuthenticationTriple, SecretAuthenticationKey> authMap = Maps.newHashMap();

	private ThirdPage thirdPage;

	private SecondPage secondPage;

	private FirstPage firstPage;

	public TestbedWizard() {
		super();
		try {
			store = SpyglassApp.spyglass.getConfigStore();
			config = store.getSpyglassConfig().getTestbedSettings();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setWindowTitle("Testbed Configuration");
		firstPage = new FirstPage("Testbed URLs");
		secondPage = new SecondPage("Authorization");
		thirdPage = new ThirdPage("Reservation");

		addPage(firstPage);
		addPage(secondPage);
		addPage(thirdPage);
	}

	@Override
	public boolean performFinish() {
		if (!firstPage.isComplete()) {
			MessageDialog.openWarning(getShell(), "Warning", "Not all Service URNs specified!");
			return false;
		}
		if (!thirdPage.isComplete()) {
			MessageDialog.openWarning(getShell(), "Warning", "No Reservation selected!");
			return false;
		}
		WSNPacketReader wsnPacketReader = WSNPacketReader.createInstance(
				config.getSessionManagementUrl(),
				thirdPage.getSelectedReservation()
		);
		if (wsnPacketReader == null) {
			return false;
		}
		SpyglassApp.spyglass.getConfigStore().getSpyglassConfig().setPacketReader(wsnPacketReader);
		store.store();
		return true;
	}

	@Override
	public boolean canFinish() {
		return firstPage.isComplete() && secondPage.isComplete() && thirdPage.isComplete();
	}

	public class FirstPage extends WizardPage implements IExtendedWizardPage {

		Text snaaText, rsText, smText;

		protected FirstPage(String pageName) {
			super(pageName);
			setMessage("Enter Service URLs");
		}

		@Override
		public void createControl(Composite composite) {
			final Composite area = new Composite(composite, SWT.NONE);
			final GridLayout gridLayout = new GridLayout();
			gridLayout.makeColumnsEqualWidth = true;
			gridLayout.numColumns = 2;
			area.setLayout(gridLayout);
			Label snaaLabel = new Label(area, SWT.NONE);
			snaaLabel.setText("SNAA Service");
			snaaText = new Text(area, SWT.BORDER);
			snaaText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Label rsLabel = new Label(area, SWT.NONE);
			rsLabel.setText("Reservation Service");
			rsText = new Text(area, SWT.BORDER);
			rsText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Label smLabel = new Label(area, SWT.NONE);
			smLabel.setText("SessionManagement Service");
			smText = new Text(area, SWT.BORDER);
			smText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			setControl(area);
			final DataBindingContext context = new DataBindingContext(SWTObservables.getRealm(composite.getDisplay()));
			IObservableValue snaaUrlObservable = PojoObservables
					.observeValue(SWTObservables.getRealm(composite.getDisplay()), TestbedWizard.this.config, "snaaUrl"
					);
			IObservableValue snaaTextObservable = SWTObservables.observeText(snaaText, SWT.Modify);
			context.bindValue(snaaTextObservable, snaaUrlObservable,
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE)
			);
			IObservableValue rsUrlObservable = PojoObservables
					.observeValue(SWTObservables.getRealm(composite.getDisplay()), TestbedWizard.this.config,
							"reservationUrl"
					);
			IObservableValue rsTextObservable = SWTObservables.observeText(rsText, SWT.Modify);
			context.bindValue(rsTextObservable, rsUrlObservable,
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE)
			);
			IObservableValue smUrlObservable = PojoObservables
					.observeValue(SWTObservables.getRealm(composite.getDisplay()), TestbedWizard.this.config,
							"sessionManagementUrl"
					);
			IObservableValue smTextObservable = SWTObservables.observeText(smText, SWT.Modify);
			context.bindValue(smTextObservable, smUrlObservable,
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE),
					new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE)
			);

		}

		@Override
		public void nextPressed() {
			StringBuilder builder = new StringBuilder();
			if (StringUtils.isBlank(snaaText.getText())) {
				builder.append("URN of SNAA Service is missing!").append("\n");
			}
			if (StringUtils.isBlank(rsText.getText())) {
				builder.append("URN of Reservation Service is missing!").append("\n");
			}
			if (StringUtils.isBlank(smText.getText())) {
				builder.append("URN of SessionManagement Service is missing!").append("\n");
			}
			if (StringUtils.isNotBlank(builder.toString())) {
				MessageDialog.openWarning(getShell(), "Warning", builder.toString());
			}
		}

		@Override
		public boolean isComplete() {
			return StringUtils.isNotBlank(snaaText.getText()) && StringUtils.isNotBlank(rsText.getText()) && StringUtils
					.isNotBlank(smText.getText());
		}
	}

	public class SecondPage extends WizardPage implements IExtendedWizardPage {

		protected SecondPage(String pageName) {
			super(pageName);
			setMessage("Select your Reservation");
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
			userText.setText(config.getUserName() == null ? "" : config.getUserName());
			final Text pwText = new Text(upperArea, SWT.BORDER);
			pwText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			pwText.setText(config.getPassword() == null ? "" : config.getPassword());
			pwText.setEchoChar('*');
			Label none1 = new Label(upperArea, SWT.NONE);
			Label none2 = new Label(upperArea, SWT.NONE);
			final Button loginButton = new Button(upperArea, SWT.NONE);
			loginButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true, 1, 1));
			loginButton.setText("check for reservations");

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
					if (authenticate(urnText.getText(), userText.getText(), pwText.getText())) {
						TableItem item = new TableItem(rsList, SWT.NONE);
						item.setText(new String[]{urnText.getText(), userText.getText()});
						for (int i = 0, n = columns.length; i < n; i++) {
							columns[i].pack();
						}

						config.setUrnPrefix(urnText.getText());
						urnText.setText("");

						config.setUserName(userText.getText());
						userText.setText("");

						config.setPassword(pwText.getText());
						pwText.setText("");

						fillReservations();
					}
				}
			}
			);
		}

		@Override
		public void nextPressed() {
		}

		@Override
		public boolean isComplete() {
			return true;
		}
	}

	public class ThirdPage extends WizardPage implements IExtendedWizardPage {

		private Table rsList;

		private TableColumn[] columns;

		private java.util.List<ConfidentialReservationData> reservationList = Lists.newArrayList();

		protected ThirdPage(String pageName) {
			super(pageName);
			setMessage("Please choose your Reservation");
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

	private boolean authenticate(String urnPrefix, String username, String password) {
		SNAA snaa = SNAAServiceHelper.getSNAAService(config.getSnaaUrl());
		AuthenticationTriple triple = new AuthenticationTriple();
		triple.setPassword(password);
		triple.setUrnPrefix(urnPrefix);
		triple.setUsername(username);
		java.util.List<SecretAuthenticationKey> list = null;
		try {
			list = snaa.authenticate(ImmutableList.of(triple));
		} catch (Exception e) {
			log.debug("" + e, e);
			return false;
		}
		authMap.put(triple, list.get(0));
		return true;

	}


	private void fillReservations() {
		RS rs = RSServiceHelper.getRSService(config.getReservationUrl());
		eu.wisebed.testbed.api.rs.v1.GetReservations getRs =
				new eu.wisebed.testbed.api.rs.v1.GetReservations();
		org.joda.time.DateTime date = new org.joda.time.DateTime();
		getRs.setFrom(XMLGregorianCalendarImpl.createDateTime(date.getYear(), date.getMonthOfYear(),
				date.getDayOfMonth(), date.getHourOfDay(), date.getMinuteOfHour(), 0
		)
		);
		date = date.plusHours(1);
		getRs.setTo(XMLGregorianCalendarImpl.createDateTime(date.getYear(), date.getMonthOfYear(),
				date.getDayOfMonth(), date.getHourOfDay(), date.getMinuteOfHour(), 0
		)
		);
		java.util.List<eu.wisebed.testbed.api.rs.v1.SecretAuthenticationKey> list
				= Lists.newArrayList();
		for (SecretAuthenticationKey key : authMap.values()) {
			eu.wisebed.testbed.api.rs.v1.SecretAuthenticationKey newKey =
					new eu.wisebed.testbed.api.rs.v1.SecretAuthenticationKey();
			newKey.setSecretAuthenticationKey(key.getSecretAuthenticationKey());
			newKey.setUrnPrefix(key.getUrnPrefix());
			newKey.setUsername(key.getUsername());
			list.add(newKey);
		}
		java.util.List<ConfidentialReservationData> rsData = null;
		try {
			rsData = rs.getConfidentialReservations(list, getRs);
		} catch (RSExceptionException e) {
			e.printStackTrace();
		}
		thirdPage.clearReservations();
		for (ConfidentialReservationData data : rsData) {
			thirdPage.addReservation(data);
		}
	}


}
