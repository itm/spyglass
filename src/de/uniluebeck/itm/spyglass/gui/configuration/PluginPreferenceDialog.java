package de.uniluebeck.itm.spyglass.gui.configuration;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceContentProvider;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceLabelProvider;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginListChangeListener;
import de.uniluebeck.itm.spyglass.util.SpyglassLogger;
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
		protected boolean showPage(final IPreferenceNode node) {
			informIfUnsavedChanges();
			final GridData data = (GridData) getTreeViewer().getControl().getLayoutData();
			data.widthHint = 220;
			return super.showPage(node);
		}
		
		private SelectionListener menuSelectionListener = new SelectionListener() {
			
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}
			
			@Override
			@SuppressWarnings("unchecked")
			public void widgetSelected(final SelectionEvent e) {
				
				final IStructuredSelection selection = (IStructuredSelection) getTreeViewer()
						.getSelection();
				Object o = null;
				final List<Plugin> plugins = new ArrayList<Plugin>();
				
				for (final Iterator it = selection.iterator(); it.hasNext();) {
					o = it.next();
					plugins.add(((PluginPreferenceNode) o).plugin);
				}
				
				if (e.getSource() == menuItemDeleteInstance) {
					final boolean b = MessageDialog.openConfirm(getShell(),
							"Confirm plugin instance removal", "Are you sure you want to remove "
									+ plugins.size() + " plugin instances?");
					if (b) {
						for (final Plugin p : plugins) {
							spyglass.getPluginManager().removePlugin(p);
						}
					}
				} else {
					
					for (final Plugin p : plugins) {
						if (e.getSource() == menuItemActivate) {
							p.setActive(true);
						} else if (e.getSource() == menuItemDeactivate) {
							p.setActive(false);
						} else if (e.getSource() == menuItemVisible) {
							p.setVisible(true);
						} else if (e.getSource() == menuItemInvisible) {
							p.setVisible(false);
						}
					}
					
				}
				
				getTreeViewer().refresh();
				
			}
			
		};
		
		private MenuItem menuItemActivate;
		private MenuItem menuItemVisible;
		private MenuItem menuItemDeactivate;
		private MenuItem menuItemInvisible;
		
		private MenuItem menuItemDeleteInstance;
		
		@Override
		protected TreeViewer createTreeViewer(final Composite parent) {
			
			final TreeViewer viewer = new TreeViewer(parent, SWT.MULTI);
			addListeners(viewer);
			viewer.setLabelProvider(new PreferenceLabelProvider());
			viewer.setContentProvider(new PreferenceContentProvider());
			viewer.setComparator(new ViewerComparator() {
				@Override
				public int compare(final Viewer v, final Object o1, final Object o2) {
					return ((PreferenceNode) o1).getLabelText().compareToIgnoreCase(
							((PreferenceNode) o2).getLabelText());
				}
			});
			
			final Menu menu = new Menu(viewer.getControl());
			
			menuItemActivate = new MenuItem(menu, SWT.NONE);
			menuItemActivate.setText("Activate");
			menuItemActivate.addSelectionListener(menuSelectionListener);
			
			menuItemDeactivate = new MenuItem(menu, SWT.NONE);
			menuItemDeactivate.setText("Deactivate");
			menuItemDeactivate.addSelectionListener(menuSelectionListener);
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			menuItemVisible = new MenuItem(menu, SWT.NONE);
			menuItemVisible.setText("Set Visible");
			menuItemVisible.addSelectionListener(menuSelectionListener);
			
			menuItemInvisible = new MenuItem(menu, SWT.NONE);
			menuItemInvisible.setText("Set Invisible");
			menuItemInvisible.addSelectionListener(menuSelectionListener);
			
			new MenuItem(menu, SWT.SEPARATOR);
			
			menuItemDeleteInstance = new MenuItem(menu, SWT.NONE);
			menuItemDeleteInstance.setText("Delete Instance");
			menuItemDeleteInstance.addSelectionListener(menuSelectionListener);
			
			viewer.getTree().setMenu(menu);
			viewer.getTree().addMenuDetectListener(new MenuDetectListener() {
				@Override
				@SuppressWarnings("unchecked")
				public void menuDetected(final MenuDetectEvent e) {
					
					final IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
					
					if (selection.isEmpty()) {
						e.doit = false;
						return;
					}
					
					boolean onlyInstancesSelected = true;
					Object o = null;
					
					for (final Iterator it = selection.iterator(); it.hasNext();) {
						o = it.next();
						if (!(o instanceof PluginPreferenceNode)) {
							onlyInstancesSelected = false;
						}
					}
					
					menuItemActivate.setEnabled(onlyInstancesSelected);
					menuItemDeactivate.setEnabled(onlyInstancesSelected);
					menuItemVisible.setEnabled(onlyInstancesSelected);
					menuItemInvisible.setEnabled(onlyInstancesSelected);
					menuItemDeleteInstance.setEnabled(onlyInstancesSelected);
					
				}
			});
			
			return viewer;
		}
		
		@Override
		protected void configureShell(final Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("SpyGlass Preferences");
			newShell.addDisposeListener(this);
		}
		
		@Override
		protected void handleShellCloseEvent() {
			clickedButtonClose();
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
			buttonSavePreferences = createButton(parent, "Save Preferences... (MS 2)",
					buttonSelectionListener);
			buttonSavePreferences.setEnabled(false);
			buttonLoadPreferences = createButton(parent, "Load Preferences... (MS 2)",
					buttonSelectionListener);
			buttonLoadPreferences.setEnabled(false);
			buttonClose = createButton(parent, "Close", buttonSelectionListener);
		}
		
		@Override
		public void pageChanged(final PageChangedEvent event) {
			// nothing to do
		}
		
		@Override
		public void updateButtons() {
			// nothing to do
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void widgetDisposed(final DisposeEvent e) {
			spyglass.getPluginManager().removePluginListChangeListener(PluginPreferenceDialog.this);
			for (final PluginPreferenceNode ppn : instancePreferenceNodes.values()) {
				((PluginPreferencePage) ppn.getPage()).removePropertyChangeListeners();
			}
		}
		
		public void selectPluginManagerPreferenceNode() {
			selectPreferenceNodeInternal(pluginManagerPreferenceNode.getId());
		}
		
		public void selectPreferenceNode(final Plugin p) {
			selectPreferenceNodeInternal(getPreferenceNodeId(p));
		}
		
		public void selectPreferenceNode(final Class<? extends Plugin> clazz) {
			selectPreferenceNodeInternal(clazz.getCanonicalName());
		}
		
		private void selectPreferenceNodeInternal(final String nodeId) {
			final IPreferenceNode node = findNodeMatching(nodeId);
			if (node.getPage() == null) {
				node.createPage();
			}
			showPage(node);
			getTreeViewer().setSelection(new StructuredSelection(node), true);
			getTreeViewer().refresh();
		}
		
	}
	
	private class PluginPreferenceNode extends PreferenceNode {
		
		private Plugin plugin;
		
		private PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig> preferencePage;
		
		private String instanceName;
		
		private Image currentImage;
		
		private Image imageActiveVisible;
		
		private Image imageNotVisible;
		
		private Image imageNotActive;
		
		private ImageDescriptor currentImageDescriptor;
		
		private ImageDescriptor imageDescriptorActiveVisible;
		
		private ImageDescriptor imageDescriptorNotVisible;
		
		private ImageDescriptor imageDescriptorNotActive;
		
		public PluginPreferenceNode(
				final String id,
				final PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig> preferencePage,
				final Plugin p) {
			
			super(id, preferencePage);
			
			this.plugin = p;
			this.preferencePage = preferencePage;
			
			updateTextAndLabels();
			
		}
		
		private void assureImageInit() {
			if (imageDescriptorActiveVisible == null) {
				imageDescriptorActiveVisible = createImageDescriptor("plugin_active_visible.png");
			}
			if (imageDescriptorNotActive == null) {
				imageDescriptorNotActive = createImageDescriptor("plugin_not_visible.png");
			}
			if (imageDescriptorNotVisible == null) {
				imageDescriptorNotVisible = createImageDescriptor("plugin_not_active.png");
			}
			if ((imageActiveVisible == null) || imageActiveVisible.isDisposed()) {
				imageActiveVisible = imageDescriptorActiveVisible.createImage();
			}
			if ((imageNotVisible == null) || imageNotVisible.isDisposed()) {
				imageNotVisible = imageDescriptorNotVisible.createImage();
			}
			if ((imageNotActive == null) || imageNotActive.isDisposed()) {
				imageNotActive = imageDescriptorNotActive.createImage();
			}
		}
		
		private void updateTextAndLabels() {
			
			assureImageInit();
			
			if (plugin.isActive() && plugin.isVisible()) {
				currentImageDescriptor = imageDescriptorActiveVisible;
				currentImage = imageActiveVisible;
			} else if (plugin.isActive()) {
				currentImageDescriptor = imageDescriptorNotVisible;
				currentImage = imageNotVisible;
			} else {
				currentImageDescriptor = imageDescriptorNotActive;
				currentImage = imageNotActive;
			}
			
			instanceName = plugin.getInstanceName();
			
			preferencePage.setTitle(instanceName);
			preferencePage.setImage(currentImage);
			
		}
		
		@Override
		protected ImageDescriptor getImageDescriptor() {
			updateTextAndLabels();
			return currentImageDescriptor;
		}
		
		@Override
		public Image getLabelImage() {
			updateTextAndLabels();
			return currentImage;
		}
		
		@Override
		public String getLabelText() {
			updateTextAndLabels();
			return instanceName;
		}
		
		@Override
		public void disposeResources() {
			
			currentImage = null;
			currentImageDescriptor = null;
			
			imageActiveVisible.dispose();
			imageNotActive.dispose();
			imageNotVisible.dispose();
			
			imageActiveVisible = null;
			imageNotActive = null;
			imageNotVisible = null;
			
			imageDescriptorActiveVisible = null;
			imageDescriptorNotVisible = null;
			imageDescriptorNotActive = null;
			
			plugin = null;
			preferencePage = null;
			instanceName = null;
			getPage().dispose();
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
	
	private static final Logger log = SpyglassLogger.get(PluginPreferenceDialog.class);
	
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
	
	private final Map<Plugin, PluginPreferenceNode> instancePreferenceNodes = new HashMap<Plugin, PluginPreferenceNode>();
	
	private final Map<Class<? extends Plugin>, CustomPreferenceNode> typePreferenceNodes = new HashMap<Class<? extends Plugin>, CustomPreferenceNode>();
	
	private CustomPreferenceNode pluginManagerPreferenceNode;
	
	private PreferenceNode generalPreferenceNode;
	
	public PluginPreferenceDialog(final Shell parentShell, final Spyglass spyglass) {
		
		this.spyglass = spyglass;
		
		preferenceManager = new PreferenceManager();
		preferenceDialog = new CustomPreferenceDialog(parentShell, preferenceManager);
		addPreferenceNodes();
		
	}
	
	private void addPreferenceNodes() {
		
		generalPreferenceNode = new PreferenceNode(NODE_ID_GENERAL_SETTINGS, "General",
				createImageDescriptor("general.png"), GeneralPreferencePage.class
						.getCanonicalName());
		pluginManagerPreferenceNode = new CustomPreferenceNode(NODE_ID_PLUGINMANAGER, "Plugins",
				createImageDescriptor("plugin_manager.png"), new PluginManagerPreferencePage(
						spyglass, this));
		
		preferenceManager.addToRoot(generalPreferenceNode);
		preferenceManager.addToRoot(pluginManagerPreferenceNode);
		
		final List<Class<? extends Plugin>> pluginTypes = spyglass.getPluginManager()
				.getAvailablePluginTypes();
		
		addPreferenceNodesRecursive(buildClassTree(pluginTypes), pluginManagerPreferenceNode);
		
	};
	
	private PluginPreferenceNode createInstancePreferenceNode(final Plugin p) {
		final PluginPreferenceNode node = new PluginPreferenceNode(getPreferenceNodeId(p), p
				.createPreferencePage(this, spyglass), p);
		// add to hashmap (needed for lookup when removing instances)
		instancePreferenceNodes.put(p, node);
		return node;
	}
	
	private CustomPreferenceNode createTypePreferenceNode(final Class<? extends Plugin> clazz) {
		// add to hashmap (needed for lookup when adding instances)
		final CustomPreferenceNode node = new CustomPreferenceNode(clazz.getCanonicalName(),
				getPluginName(clazz), getPluginImageDescriptor(), getTypePreferencePage(clazz));
		typePreferenceNodes.put(clazz, node);
		return node;
	}
	
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
		
		try {
			
			// add nodes for abstract and instantiable but not instantiated plugins
			preferenceNode = createTypePreferenceNode(classTree.clazz);
			parentPreferenceNode.add(preferenceNode);
			
			// add nodes for instantiated plugins
			for (final Plugin p : spyglass.getPluginManager().getPluginInstances(classTree.clazz,
					false)) {
				
				// add to parent tree node
				preferenceNode.add(createInstancePreferenceNode(p));
				
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
	
	private ImageDescriptor getPluginImageDescriptor() {
		return createImageDescriptor("plugin.png");
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
	
	private URL getResourceUrl(final String suffix) {
		return PluginPreferenceDialog.class.getResource(suffix);
	}
	
	private File getSaveLoadFileFromUser(final int swtStyle) {
		final FileDialog fd = new FileDialog(preferenceDialog.getShell(), swtStyle);
		fd.setFilterExtensions(new String[] { "*.xml" });
		final String path = fd.open();
		return path == null ? null : new File(path);
	}
	
	private ImageDescriptor createImageDescriptor(final String fileName) {
		return ImageDescriptor.createFromURL(getResourceUrl(fileName));
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
				onPluginAdded(p);
				break;
			case PLUGIN_REMOVED:
				onPluginRemoved(p);
				break;
			case PRIORITY_CHANGED:
				// nothing to do, since PluginManagerPreferencePage
				// receives the event itself and refreshes
				break;
		}
	}
	
	private void onPluginAdded(final Plugin p) {
		typePreferenceNodes.get(p.getClass()).add(createInstancePreferenceNode(p));
		preferenceDialog.getTreeViewer().refresh();
	}
	
	private void onPluginRemoved(final Plugin p) {
		removePreferenceNode(instancePreferenceNodes.get(p), preferenceManager.getRootSubNodes());
		preferenceDialog.selectPreferenceNode(p.getClass());
	}
	
	private boolean removePreferenceNode(final IPreferenceNode node,
			final IPreferenceNode[] parentNodes) {
		boolean removed;
		for (final IPreferenceNode parentNode : parentNodes) {
			if (parentNode == node) {
				return preferenceManager.remove(parentNode);
			}
			removed = removePreferenceNode(node, parentNode);
			if (removed) {
				return true;
			}
		}
		return false;
	}
	
	private boolean removePreferenceNode(final IPreferenceNode node,
			final IPreferenceNode parentNode) {
		boolean removed;
		for (final IPreferenceNode pn : parentNode.getSubNodes()) {
			if (pn == node) {
				return parentNode.remove(pn);
			}
			removed = removePreferenceNode(node, pn);
			if (removed) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns if the currently visible preference dialog page has unsaved changes
	 * 
	 * @return <code>true</code> if the currently visible preference dialog page has unsaved changes
	 */
	@SuppressWarnings("unchecked")
	private boolean hasUnsavedChanges() {
		final IPreferencePage selectedPage = (IPreferencePage) preferenceDialog.getSelectedPage();
		
		// no page selected so it's ok to continue
		if (selectedPage == null) {
			return false;
		}
		
		boolean hashUnsavedChanges = false;
		
		// check if we're looking at general preference page or plug-in preference page
		if ((selectedPage instanceof GeneralPreferencePage)
				|| (selectedPage instanceof PluginManagerPreferencePage)) {
			hashUnsavedChanges = !selectedPage.okToLeave();
		}
		
		if (selectedPage instanceof PluginPreferencePage) {
			
			// check if currently opened preference page contains unsaved values
			if (((PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig>) selectedPage)
					.hasUnsavedChanges()) {
				hashUnsavedChanges = true;
			}
			
		} else {
			
			if (!(selectedPage).okToLeave()) {
				hashUnsavedChanges = true;
			}
			
		}
		return hashUnsavedChanges;
	}
	
	private boolean proceedIfUnsavedChanges() {
		
		if (hasUnsavedChanges()) {
			final String message = "The currently opened preference page contains unsaved changes. Are you sure you want to proceed?";
			final boolean ok = MessageDialog.openQuestion(preferenceDialog.getShell(),
					"Unsaved changes", message);
			return ok;
		}
		
		return true;
		
	}
	
	/**
	 * Displays an information dialog which reminds the user that there are still unsaved changes at
	 * a preference page. In respect to the type of the preference page, the user will be offered
	 * the opportunity to store the changes before leaving the page.
	 */
	@SuppressWarnings("unchecked")
	private void informIfUnsavedChanges() {
		if (hasUnsavedChanges()) {
			final String message = "The currently opened preference page contains unsaved changes. Do you want to save now?";
			
			final IPreferencePage selectedPage = (IPreferencePage) preferenceDialog
					.getSelectedPage();
			if ((selectedPage != null)
					&& (selectedPage instanceof PluginPreferencePage)
					&& MessageDialog.openQuestion(preferenceDialog.getShell(), "Unsaved changes",
							message)) {
				((PluginPreferencePage<? extends Plugin, ? extends PluginXMLConfig>) selectedPage)
						.performApply();
			} else {
				MessageDialog
						.openInformation(preferenceDialog.getShell(), "Unsaved changes",
								"The currently opened preference page contains unsaved changes. Make sure you save later!");
			}
			
		}
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
	
	public void selectPreferencePage(final Plugin p) {
		preferenceDialog.selectPreferenceNode(p);
	}
	
	public void onPluginInstancePropertyChange() {
		preferenceDialog.getTreeViewer().refresh(true);
	}
}