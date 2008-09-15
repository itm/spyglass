package de.uniluebeck.itm.spyglass.gui.configuration;

import ishell.util.Logging;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Category;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;

public class PluginPreferenceDialog implements PluginListChangeListener {
	
	private class ClassTree {
		
		private final Class<? extends Plugin> clazz;
		
		private final ArrayList<ClassTree> sons;
		
		public ClassTree(final Class<? extends Plugin> clazz) {
			this.clazz = clazz;
			this.sons = new ArrayList<ClassTree>();
		}
		
	}
	
	private class CustomPreferenceDialog extends PreferenceDialog implements IPageChangedListener,
			DisposeListener {
		
		public CustomPreferenceDialog(final Shell parentShell,
				final PreferenceManager preferenceManager) {
			super(parentShell, preferenceManager);
			setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | getDefaultOrientation());
			addPageChangedListener(this);
		}
		
		@Override
		protected void configureShell(final Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("SpyGlass Preferences");
			newShell.addDisposeListener(this);
		}
		
		private Button createButton(final Composite parent, final String label,
				final SelectionListener selectionListener) {
			((GridLayout) parent.getLayout()).numColumns++;
			final Button button = new Button(parent, SWT.PUSH);
			button.setText(label);
			button.setFont(JFaceResources.getDialogFont());
			button.addSelectionListener(selectionListener);
			setButtonLayoutData(button);
			return button;
		}
		
		@Override
		protected void createButtonsForButtonBar(final Composite parent) {
			buttonSavePreferences = createButton(parent, "Save Preferences...",
					buttonSelectionListener);
			buttonLoadPreferences = createButton(parent, "Load Preferences...",
					buttonSelectionListener);
			buttonClose = createButton(parent, "Close", buttonSelectionListener);
		}
		
		@Override
		public void pageChanged(final PageChangedEvent event) {
			try {
				final IPreferencePage selectedPage = (IPreferencePage) event.getSelectedPage();
				// final ConfigStore cs = spyglass.getConfigStore();
				
				if (selectedPage instanceof PluginPreferencePage<?, ?>) {
					
					// nothing to do
					
				}
			} catch (final Exception e) {
				log.error("", e);
			}
			
		}
		
		@Override
		public void updateButtons() {
			// nothing to do
		}
		
		@Override
		public void widgetDisposed(final DisposeEvent e) {
			spyglass.getPluginManager().removePluginListChangeListener(PluginPreferenceDialog.this);
		}
		
		public void selectPluginManagerPreferenceNode() {
			final IPreferenceNode pluginManagerNode = findNodeMatching(NODE_ID_PLUGINMANAGER);
			if (pluginManagerNode.getPage() == null) {
				pluginManagerNode.createPage();
			}
			showPage(pluginManagerNode);
			getTreeViewer()
					.setSelection(new StructuredSelection(findPluginManagerTreeItem()), true);
			getTreeViewer().refresh();
		}
		
		private IPreferenceNode findPluginManagerTreeItem() {
			for (final TreeItem item : getTreeViewer().getTree().getItems()) {
				if (((IPreferenceNode) item.getData()).getId().equals(NODE_ID_PLUGINMANAGER)) {
					return (IPreferenceNode) item.getData();
				}
			}
			// should never reach here
			return null;
		}
		
	}
	
	private class CustomPreferenceNode extends PreferenceNode {
		
		private final ImageDescriptor imageDescriptor;
		
		private final String labelText;
		
		/**
		 * Cached image, or <code>null</code> if none.
		 */
		private Image image;
		
		public CustomPreferenceNode(final String id, final String labelText,
				final ImageDescriptor image, final IPreferencePage preferencePage) {
			
			super(id, preferencePage);
			
			this.labelText = labelText;
			this.imageDescriptor = image;
			
			preferencePage.setTitle(labelText);
			preferencePage.setImageDescriptor(imageDescriptor);
			
		}
		
		@Override
		protected ImageDescriptor getImageDescriptor() {
			return imageDescriptor;
		}
		
		@Override
		public Image getLabelImage() {
			if ((image == null) && (imageDescriptor != null)) {
				image = imageDescriptor.createImage();
			}
			return image;
		}
		
		@Override
		public String getLabelText() {
			return labelText;
		}
		
		@Override
		public void disposeResources() {
			if (image != null) {
				image.dispose();
				image = null;
			}
		}
		
	}
	
	private static final Category log = Logging.get(PluginPreferenceDialog.class);
	
	private static final String NODE_ID_PLUGINMANAGER = "NodeIdPluginManager";
	
	private static final String NODE_ID_GENERAL_SETTINGS = "NodeIdGeneralSettings";
	
	private Button buttonClose;
	
	private Button buttonLoadPreferences;
	
	private Button buttonSavePreferences;
	
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
	
	private final CustomPreferenceDialog preferenceDialog;
	
	private final PreferenceManager preferenceManager;
	
	private final Spyglass spyglass;
	
	private final Map<Plugin, String> instancePreferenceNodes = new HashMap<Plugin, String>();
	
	public PluginPreferenceDialog(final Shell parentShell, final Spyglass spyglass) {
		
		this.spyglass = spyglass;
		
		preferenceManager = new PreferenceManager();
		preferenceDialog = new CustomPreferenceDialog(parentShell, preferenceManager);
		
		addPreferenceNodes();
		
	}
	
	private void addPreferenceNodes() {
		
		final PreferenceNode generalPreferenceNode = new PreferenceNode(NODE_ID_GENERAL_SETTINGS,
				"General", getImageDescriptor("general.png"), GeneralPreferencePage.class
						.getCanonicalName());
		final PreferenceNode pluginsPreferenceNode = new CustomPreferenceNode(
				NODE_ID_PLUGINMANAGER, "Plugins", getImageDescriptor("plugin_manager.png"),
				new PluginManagerPreferencePage(spyglass));
		
		preferenceManager.addToRoot(generalPreferenceNode);
		preferenceManager.addToRoot(pluginsPreferenceNode);
		
		final List<Class<? extends Plugin>> pluginTypes = spyglass.getPluginManager()
				.getAvailablePluginTypes();
		
		addPreferenceNodesRecursive(buildClassTree(pluginTypes), pluginsPreferenceNode);
		
	};
	
	private void addPreferenceNodesRecursive(final ClassTree classTree,
			final PreferenceNode parentPreferenceNode) {
		
		// ignore root of classTree, since it is the Plugin class
		if (classTree.clazz.getCanonicalName().equals(Plugin.class.getCanonicalName())) {
			
			for (final ClassTree ct : classTree.sons) {
				addPreferenceNodesRecursive(ct, parentPreferenceNode);
			}
			
			return;
			
		}
		
		final CustomPreferenceNode preferenceNode;
		CustomPreferenceNode instancePreferenceNode;
		String preferenceNodeId, preferenceNodeLabel;
		ImageDescriptor preferenceNodeImageDescriptor;
		PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig> preferencePage;
		
		try {
			
			// add nodes for abstract and instantiable but not instantiated plugins
			
			preferenceNodeId = classTree.clazz.getCanonicalName() + "_"
					+ System.currentTimeMillis();
			preferencePage = getTypePreferencePage(classTree.clazz);
			preferenceNodeLabel = getPluginName(classTree.clazz);
			preferenceNodeImageDescriptor = getPluginImageDescriptor();
			preferenceNode = new CustomPreferenceNode(preferenceNodeId, preferenceNodeLabel,
					preferenceNodeImageDescriptor, preferencePage);
			parentPreferenceNode.add(preferenceNode);
			
			// add nodes for instantiated plugins
			for (final Plugin p : spyglass.getPluginManager().getPluginInstances(classTree.clazz,
					false)) {
				
				preferenceNodeId = getPreferenceNodeId(p);
				preferencePage = getPreferencePage(classTree.clazz, p);
				preferenceNodeLabel = getInstanceName(classTree.clazz, p);
				preferenceNodeImageDescriptor = getInstanceImageDescriptor(classTree.clazz, p);
				instancePreferenceNode = new CustomPreferenceNode(preferenceNodeId,
						preferenceNodeLabel, preferenceNodeImageDescriptor, preferencePage);
				
				// add to parent tree node
				preferenceNode.add(instancePreferenceNode);
				
				// add to hashmap (needed for lookup when removing instances)
				instancePreferenceNodes.put(p, preferenceNodeId);
				
			}
			
			for (final ClassTree ct : classTree.sons) {
				
				addPreferenceNodesRecursive(ct, preferenceNode);
				
			}
			
		} catch (final Exception e) {
			log.error("", e);
			return;
		}
		
	}
	
	private String getPreferenceNodeId(final Plugin p) {
		return p.getClass().getCanonicalName() + "_" + p.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	private void addToClassSet(final Class<? extends Plugin> c,
			final Set<Class<? extends Plugin>> classSet) {
		
		classSet.add(c);
		
		if (!c.getSuperclass().getCanonicalName().equals(Plugin.class.getCanonicalName())) {
			addToClassSet((Class<? extends Plugin>) c.getSuperclass(), classSet);
		}
		
	}
	
	private void buildClassTree(final ClassTree classTree,
			final Set<Class<? extends Plugin>> classSet) {
		
		for (final Class<? extends Plugin> c : classSet) {
			
			if (c.getSuperclass().getCanonicalName().equals(classTree.clazz.getCanonicalName())) {
				
				final ClassTree son = new ClassTree(c);
				buildClassTree(son, classSet);
				classTree.sons.add(son);
				
			}
			
		}
		
	}
	
	public ClassTree buildClassTree(final List<Class<? extends Plugin>> pluginTypes) {
		
		// build class set
		final Set<Class<? extends Plugin>> classSet = new HashSet<Class<? extends Plugin>>();
		
		for (final Class<? extends Plugin> c : pluginTypes) {
			
			addToClassSet(c, classSet);
			
		}
		
		final ClassTree classTree = new ClassTree(Plugin.class);
		buildClassTree(classTree, classSet);
		
		return classTree;
		
	}
	
	private void clickedButtonClose() {
		if (proceedIfUnsavedChanges()) {
			preferenceDialog.close();
		}
	}
	
	private void clickedButtonLoadPreferences() {
		final File loadFile = getSaveLoadFileFromUser(SWT.OPEN);
		final String msg = loadFile == null ? "The user cancelled selecting a file."
				: "The preferences would now be loaded from " + loadFile.getAbsolutePath()
						+ " if it was implemented.";
		MessageDialog.openInformation(preferenceDialog.getShell(), "Save Preferences...", msg);
	}
	
	private void clickedButtonSavePreferences() {
		if (proceedIfUnsavedChanges()) {
			final File saveFile = getSaveLoadFileFromUser(SWT.SAVE);
			final String msg = saveFile == null ? "The user cancelled selecting a file."
					: "The preferences would now be saved to " + saveFile.getAbsolutePath()
							+ " if it was implemented.";
			MessageDialog.openInformation(preferenceDialog.getShell(), "Save Preferences...", msg);
		}
	}
	
	@Override
	public void finalize() throws Throwable {
		// nothing to do
	}
	
	private ImageDescriptor getImageDescriptor(final String fileName) {
		return ImageDescriptor.createFromURL(getResourceUrl(fileName));
	}
	
	private ImageDescriptor getPluginImageDescriptor() {
		return getImageDescriptor("plugin.png");
	}
	
	private ImageDescriptor getInstanceImageDescriptor(final Class<? extends Plugin> clazz,
			final Plugin p) {
		
		try {
			
			final boolean isActive = (Boolean) clazz.getMethod("isActive").invoke(p);
			final boolean isVisible = (Boolean) clazz.getMethod("isVisible").invoke(p);
			
			return (isActive && isVisible) ? getImageDescriptor("plugin_active_visible.png")
					: (isActive) ? getImageDescriptor("plugin_not_visible.png")
							: getImageDescriptor("plugin_not_active.png");
			
		} catch (final Exception e) {
			log.error("", e);
			return null;
		}
		
	}
	
	private String getInstanceName(final Class<? extends Plugin> clazz, final Plugin p) {
		
		try {
			
			return (String) clazz.getMethod("getInstanceName").invoke(p);
			
		} catch (final Exception e) {
			log.error("", e);
			return "SEE_ERROR_LOG";
		}
		
	}
	
	private String getPluginName(final Class<? extends Plugin> clazz) {
		
		try {
			
			return (String) clazz.getDeclaredMethod("getHumanReadableName").invoke(null);
			
		} catch (final NoSuchMethodException e) {
			
			final String message = "The SpyGlass Plug-In " + clazz.getCanonicalName()
					+ " must implement the method \"public static String getHumanReadableName()\"";
			MessageDialog.openWarning(preferenceDialog.getShell(),
					"Incorrect plug-in implementation", message);
			log.error("", e);
			
		} catch (final Exception e) {
			log.error("", e);
		}
		
		return "SEE_ERROR_LOG";
		
	}
	
	@SuppressWarnings("unchecked")
	private PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig> getPreferencePage(
			final Class<? extends Plugin> clazz, final Plugin p) {
		
		try {
			
			return (PluginPreferencePage) clazz.getMethod("createPreferencePage",
					PluginPreferenceDialog.class, Spyglass.class).invoke(p, this, spyglass);
			
		} catch (final Exception e) {
			log.error("", e);
		}
		
		return null;
		
	}
	
	private URL getResourceUrl(final String suffix) {
		return PluginPreferenceDialog.class.getResource(suffix);
	}
	
	private File getSaveLoadFileFromUser(final int swtStyle) {
		final FileDialog fd = new FileDialog(preferenceDialog.getShell(), swtStyle);
		fd.setFilterExtensions(new String[] { "*.xml" });
		final String path = fd.open();
		return path == null ? null : new File(path);
	}
	
	@SuppressWarnings("unchecked")
	private PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig> getTypePreferencePage(
			final Class<? extends Plugin> clazz) {
		
		try {
			
			return isAbstract(clazz) ? new AbstractPluginTypePreferencePage(this, spyglass,
					getPluginName(clazz)) : (PluginPreferencePage) clazz.getMethod(
					"createTypePreferencePage", PluginPreferenceDialog.class, Spyglass.class)
					.invoke(null, this, spyglass);
			
		} catch (final Exception e) {
			log.error("", e);
		}
		
		return null;
		
	}
	
	private boolean isAbstract(final Class<? extends Plugin> clazz) {
		
		return Modifier.isAbstract(clazz.getModifiers());
		
	}
	
	// --------------------------------------------------------------------------------
	/**
	 * @return see {@link org.eclipse.jface.window.Window#open()}
	 */
	public int open() {
		spyglass.getPluginManager().addPluginListChangeListener(this);
		return preferenceDialog.open();
	}
	
	@Override
	public void pluginListChanged(final Plugin p, final ListChangeEvent what) {
		switch (what) {
			case NEW_PLUGIN:
				// TODO
				break;
			case PLUGIN_REMOVED:
				preferenceManager.remove(instancePreferenceNodes.get(p));
				preferenceDialog.selectPluginManagerPreferenceNode();
				removePluginFromTree(preferenceDialog.getTreeViewer().getTree(), p);
				break;
			case PRIORITY_CHANGED:
				// TODO
				break;
		}
		
		// XXX: PLUGIN_STATE_CHANGED removed, since the PluginManager should not be responsible for
		// sending out notifications about changes in the configuration of a plugin. Use instead the
		// event listener inside the PluginXMLConfig objects. -- Dariush
	}
	
	private void removePluginFromTree(final Tree t, final Plugin p) {
		final String nodeId = getPreferenceNodeId(p);
		for (final TreeItem ti : t.getItems()) {
			((PreferenceNode) ti.getData()).remove(nodeId);
			// TODO
		}
	}
	
	@SuppressWarnings("unchecked")
	private boolean proceedIfUnsavedChanges() {
		
		final IPreferencePage selectedPage = (IPreferencePage) preferenceDialog.getSelectedPage();
		
		// no page selected so it's ok to continue
		if (selectedPage == null) {
			return true;
		}
		
		boolean needToAsk = false;
		
		// check if we're looking at general preference page or plugin preference page
		if ((selectedPage instanceof GeneralPreferencePage)
				|| (selectedPage instanceof PluginManagerPreferencePage)) {
			needToAsk = !selectedPage.okToLeave();
		}
		
		if (selectedPage instanceof PluginPreferencePage) {
			
			// check if currently opened preference page contains unsaved values
			if (((PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig>) selectedPage)
					.hasUnsavedChanges()) {
				needToAsk = true;
			}
			
		} else {
			
			if (!(selectedPage).okToLeave()) {
				needToAsk = true;
			}
			
		}
		
		if (needToAsk) {
			final String message = "The currently opened preference page contains unsaved changes. Are you sure you want to proceed?";
			final boolean ok = MessageDialog.openQuestion(preferenceDialog.getShell(),
					"Unsaved changes", message);
			return ok;
		}
		
		return true;
		
	}
	
	public static void main(final String[] args) {
		try {
			final Display display = Display.getDefault();
			final Shell shell = new Shell(display);
			final Spyglass sg = new Spyglass(true);
			final PluginPreferenceDialog inst = new PluginPreferenceDialog(shell, sg);
			inst.open();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
}