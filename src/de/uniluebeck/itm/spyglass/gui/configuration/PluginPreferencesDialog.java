package de.uniluebeck.itm.spyglass.gui.configuration;

import ishell.util.Logging;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Category;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.ConfigStore;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;

public class PluginPreferencesDialog {
	
	private class CustomPreferenceDialog extends PreferenceDialog {
		
		public CustomPreferenceDialog(final Shell parentShell, final PreferenceManager preferenceManager) {
			super(parentShell, preferenceManager);
			setShellStyle(SWT.DIALOG_TRIM | getDefaultOrientation());
		}
		
		@Override
		protected void configureShell(final Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("SpyGlass Preferences");
		}
		
		@Override
		protected void createButtonsForButtonBar(final Composite parent) {
			buttonSavePreferences = createButton(parent, "Save Preferences...", buttonSelectionListener);
			buttonLoadPreferences = createButton(parent, "Load Preferences...", buttonSelectionListener);
			buttonClose = createButton(parent, "Close", buttonSelectionListener);
		}
		
		@Override
		public void updateButtons() {
			// nothing to do
		}
		
		private Button createButton(final Composite parent, final String label, final SelectionListener selectionListener) {
			((GridLayout) parent.getLayout()).numColumns++;
			final Button button = new Button(parent, SWT.PUSH);
			button.setText(label);
			button.setFont(JFaceResources.getDialogFont());
			button.addSelectionListener(selectionListener);
			setButtonLayoutData(button);
			return button;
		}
		
	}
	
	private static final Category log = Logging.get(PluginPreferencesDialog.class);
	
	private final PreferenceManager preferenceManager;
	
	private final PreferenceDialog preferenceDialog;
	
	private final Spyglass spyglass;
	
	public PluginPreferencesDialog(final Shell parentShell, final Spyglass spyglass) {
		
		this.spyglass = spyglass;
		
		preferenceManager = new PreferenceManager();
		preferenceDialog = new CustomPreferenceDialog(parentShell, preferenceManager);
		
		addPreferenceNodes();
		
	}
	
	private Button buttonClose;
	
	private Button buttonSavePreferences;
	
	private Button buttonLoadPreferences;
	
	private final SelectionListener buttonSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			if (e.getSource() == buttonSavePreferences) {
				clickedButtonSavePreferences();
			} else if (e.getSource() == buttonLoadPreferences) {
				clickedButtonLoadPreferences();
			} else if (e.getSource() == buttonClose) {
				clickedButtonClose();
			}
		}
	};
	
	private void clickedButtonClose() {
		preferenceDialog.close();
	}
	
	private void clickedButtonSavePreferences() {
		final File saveFile = getSaveLoadFileFromUser(SWT.SAVE);
		final String msg = saveFile == null ? "The user cancelled selecting a file." : "The preferences would now be saved to "
				+ saveFile.getAbsolutePath() + " if it was implemented.";
		MessageDialog.openInformation(preferenceDialog.getShell(), "Save Preferences...", msg);
	}
	
	private File getSaveLoadFileFromUser(final int swtStyle) {
		final FileDialog fd = new FileDialog(preferenceDialog.getShell(), swtStyle);
		fd.setFilterExtensions(new String[] { "*.xml" });
		final String path = fd.open();
		return path == null ? null : new File(path);
	}
	
	private void clickedButtonLoadPreferences() {
		final File loadFile = getSaveLoadFileFromUser(SWT.OPEN);
		final String msg = loadFile == null ? "The user cancelled selecting a file." : "The preferences would now be loaded from "
				+ loadFile.getAbsolutePath() + " if it was implemented.";
		MessageDialog.openInformation(preferenceDialog.getShell(), "Save Preferences...", msg);
	};
	
	private void addPreferenceNodes() {
		
		final PreferenceNode generalPreferenceNode = new PreferenceNode("General", "General", null, GeneralPreferencePage.class.getCanonicalName());
		final PreferenceNode pluginsPreferenceNode = new PreferenceNode("Plugins", "Plugins", null, PluginManagerPreferencePage.class
				.getCanonicalName());
		
		preferenceManager.addToRoot(generalPreferenceNode);
		preferenceManager.addToRoot(pluginsPreferenceNode);
		
		final List<Class<? extends Plugin>> pluginTypes = spyglass.getPluginManager().getAvailablePluginTypes();
		
		addPreferenceNodesRecursive(buildClassTree(pluginTypes), pluginsPreferenceNode);
		
	}
	
	private void addPreferenceNodesRecursive(final ClassTree classTree, final PreferenceNode parentPreferenceNode) {
		
		// ignore root of classTree, since it is the Plugin class
		if (classTree.clazz.getCanonicalName().equals(Plugin.class.getCanonicalName())) {
			
			for (final ClassTree ct : classTree.sons) {
				addPreferenceNodesRecursive(ct, parentPreferenceNode);
			}
			
			return;
			
		}
		
		final PreferenceNode preferenceNode;
		PreferenceNode instancePreferenceNode;
		String preferenceNodeId;
		PluginPreferencePage<? extends Plugin> preferencePage;
		
		try {
			
			// add nodes for abstract and instantiable but not instantiated plugins
			
			preferenceNodeId = classTree.clazz.getCanonicalName() + "_" + System.currentTimeMillis();
			preferencePage = getTypePreferencePage(classTree.clazz);
			preferenceNode = new PreferenceNode(preferenceNodeId, preferencePage);
			parentPreferenceNode.add(preferenceNode);
			
			// add nodes for instantiated plugins
			for (final Plugin p : spyglass.getPluginManager().getPluginInstances(classTree.clazz)) {
				
				preferenceNodeId = p.getClass().getCanonicalName() + "_" + p.hashCode();
				preferencePage = getPreferencePage(classTree.clazz, p);
				instancePreferenceNode = new PreferenceNode(preferenceNodeId, preferencePage);
				
				preferenceNode.add(instancePreferenceNode);
				
			}
			
			for (final ClassTree ct : classTree.sons) {
				
				addPreferenceNodesRecursive(ct, preferenceNode);
				
			}
			
		} catch (final Exception e) {
			log.error("", e);
			return;
		}
		
	}
	
	private boolean isAbstract(final Class<? extends Plugin> clazz) {
		
		return Modifier.isAbstract(clazz.getModifiers());
		
	}
	
	private String getPluginName(final Class<? extends Plugin> clazz) {
		
		try {
			
			return (String) clazz.getDeclaredMethod("getHumanReadableName").invoke(null);
			
		} catch (final Exception e) {
			log.error("", e);
		}
		
		return "SEE_ERROR_LOG";
		
	}
	
	@SuppressWarnings("unchecked")
	private PluginPreferencePage<? extends Plugin> getPreferencePage(final Class<? extends Plugin> clazz, final Plugin p) {
		
		try {
			
			return (PluginPreferencePage) clazz.getMethod("createPreferencePage", ConfigStore.class).invoke(p, spyglass.getConfigStore());
			
		} catch (final Exception e) {
			log.error("", e);
		}
		
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	private PluginPreferencePage<? extends Plugin> getTypePreferencePage(final Class<? extends Plugin> clazz) {
		
		try {
			
			return isAbstract(clazz) ? new AbstractPluginTypePreferencePage(getPluginName(clazz)) : (PluginPreferencePage) clazz.getMethod(
					"createTypePreferencePage", ConfigStore.class).invoke(null, spyglass.getConfigStore());
			
		} catch (final Exception e) {
			log.error("", e);
		}
		
		return null;
		
	}
	
	private class ClassTree {
		
		private final Class<? extends Plugin> clazz;
		
		private final ArrayList<ClassTree> sons;
		
		public ClassTree(final Class<? extends Plugin> clazz) {
			this.clazz = clazz;
			this.sons = new ArrayList<ClassTree>();
		}
		
	}
	
	private ClassTree buildClassTree(final List<Class<? extends Plugin>> pluginTypes) {
		
		// build class set
		final Set<Class<? extends Plugin>> classSet = new HashSet<Class<? extends Plugin>>();
		
		for (final Class<? extends Plugin> c : pluginTypes) {
			
			addToClassSet(c, classSet);
			
		}
		
		final ClassTree classTree = new ClassTree(Plugin.class);
		buildClassTree(classTree, classSet);
		
		return classTree;
		
	}
	
	private void buildClassTree(final ClassTree classTree, final Set<Class<? extends Plugin>> classSet) {
		
		for (final Class<? extends Plugin> c : classSet) {
			
			if (c.getSuperclass().getCanonicalName().equals(classTree.clazz.getCanonicalName())) {
				
				final ClassTree son = new ClassTree(c);
				buildClassTree(son, classSet);
				classTree.sons.add(son);
				
			}
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void addToClassSet(final Class<? extends Plugin> c, final Set<Class<? extends Plugin>> classSet) {
		
		classSet.add(c);
		
		if (!c.getSuperclass().getCanonicalName().equals(Plugin.class.getCanonicalName())) {
			addToClassSet((Class<? extends Plugin>) c.getSuperclass(), classSet);
		}
		
	}
	
	@Override
	public void finalize() throws Throwable {
		// TODO
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return see {@link org.eclipse.jface.window.Window#open()}
	 */
	public int open() {
		return preferenceDialog.open();
	}
	
}