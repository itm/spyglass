package de.uniluebeck.itm.spyglass.packetgenerator.gui;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

/**
 * Window to set the config path fpr the Plugingenerator.
 * 
 * this is my first SWT code, so please don't scream when you see it.
 * 
 * @author dariush
 */
public class IShellTab extends org.eclipse.swt.widgets.Composite {
	private static Logger log = SpyglassLoggerFactory.getLogger(IShellTab.class);

	Label label;
	Text text;
	Button button;

	/**
	 * The (currently hardcoded) default path to the config file.
	 */
	private final String CONFIG_DEFAULT_PATH = "config/packetGeneratorConfigSpringEmbedderTest.xml";

	public IShellTab(final org.eclipse.swt.widgets.Composite parent, final PacketGeneratorIShellPlugin plugin) {
		super(parent, SWT.NULL);

		final GridLayout l = new GridLayout();
		l.verticalSpacing = 20;
		l.marginHeight = 20;
		l.marginWidth = 80;
		l.numColumns = 2;
		l.makeColumnsEqualWidth = true;
		this.setLayout(l);

		label = new Label(this, SWT.NONE);
		text = new Text(this, SWT.NONE);
		button = new Button(this, SWT.NONE);

		button.setFocus();
		label.setText("Path to config file: ");
		text.setText(this.CONFIG_DEFAULT_PATH);
		button.setText("Reload config and restart generator");

		//

		/*
		 * this button stopps the generator, creates a new one based on the given config, and starts
		 * the generator again.
		 */
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {

				plugin.shutdown();
				button.setText("Please wait...");
				try {
					Thread.sleep(2000); // TODO: there are better ways to avoid
					// races
				} catch (final InterruptedException e1) {
					//
				}
				try {
					plugin.startGenerator();
				} catch (final Exception e2) {
					final MessageBox box = new MessageBox(button.getShell());
					box.setMessage(e2.getMessage());
					box.setText("Error");
					box.open();
				}
				button.setText("Reload config and restart generator");
			}
		});

	}

	/**
	 * Return the configpath set by the user
	 */
	public String getConfigPath() {
		return this.text.getText();
	}

}
