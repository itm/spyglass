package de.uniluebeck.itm.spyglass.gui.actions;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import de.uniluebeck.itm.spyglass.SpyglassEnvironment;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;

public class LoadConfigurationAction extends Action {

	private static Logger log = SpyglassLoggerFactory.getLogger(LoadConfigurationAction.class);

	private final ImageDescriptor imageDescriptor = getImageDescriptor("page_gear.png");

	private final Spyglass spyglass;

	public LoadConfigurationAction(final Spyglass spyglass) {
		this.spyglass = spyglass;
	}

	@Override
	public void run() {
		loadFromFileSystem();
	}

	/**
	 * Loads the configuration from a file which is selected using a {@link FileDialog}
	 * 
	 * @return <code>true</code> if the configuration was set successfully
	 */
	public boolean loadFromFileSystem() {
		final FileDialog fd = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
		fd.setFilterExtensions(new String[] { "*.xml" });
		fd.setFilterPath(SpyglassEnvironment.getConfigFileWorkingDirectory());
		final String path = fd.open();
		if (path != null) {
			final File f = new File(path);
			if (!f.canRead()) {
				log.error("Could not read file " + f);
				return false;
			}

			// first we have to load the new config, to avoid that it is overwritten in the meantime
			try {
				spyglass.getConfigStore().importConfig(f);
			} catch (final InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (final Exception e) {
				log.error("Could not load the config.", e);
				return false;
			}

			try {
				SpyglassEnvironment.setConfigFilePath(f);
				SpyglassEnvironment.setConfigFileWorkingDirectory(f.getParent());
			} catch (final IOException e) {
				log.error("Could not store the new config path in my property file.",e);
			}
			return true;
		}
		return false;
	}

	@Override
	public String getText() {
		return "Load Configuration";
	}

	@Override
	public String getToolTipText() {
		return "Load Configuration";
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

}
