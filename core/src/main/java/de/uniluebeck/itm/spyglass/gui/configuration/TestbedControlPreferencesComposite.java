/*
 * --------------------------------------------------------------------------------
 * This file is part of the WSN visualization framework SpyGlass. Copyright (C)
 * 2004-2007 by the SwarmNet (www.swarmnet.de) project SpyGlass is free
 * software; you can redistribute it and/or modify it under the terms of the BSD
 * License. Refer to spyglass-licence.txt file in the root of the SpyGlass
 * source tree for further details.
 * --------------------------------------------------------------------------------
 */
package de.uniluebeck.itm.spyglass.gui.configuration;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.cloudgarden.resource.SWTResourceManager;
import de.uniluebeck.itm.spyglass.SpyglassEnvironment;

import de.uniluebeck.itm.spyglass.gui.databinding.validator.StringRegExValidator;
import de.uniluebeck.itm.spyglass.testbedControl.TestbedControler;
import de.uniluebeck.itm.spyglass.xmlconfig.TestbedControlSettingsXMLConfig;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.FileDialog;

public class TestbedControlPreferencesComposite extends org.eclipse.swt.widgets.Composite {

    {
        // Register as a resource user - SWTResourceManager will
        // handle the obtaining and disposing of resources
        SWTResourceManager.registerResourceUser(this);
    }
    private Group group1;
    private Label label1;
    private Label label11length;
    private Label label13;
    private Text nodeID;
    private Group group3;
    private Label label12;
    private Text scaleTime;
    private Text offsetY;
    private Label label10;
    private Label label9length;
    private Label label8;
    private Text offsetX;
    private Label label7;
    private Button FlashBrowseButton;
    private Button OTAPFlashBrowseButton;
    private Text scaleY;
    private Label label5;
    private Text scaleX;
    private Label label3;
    private Label label2;
    private Text ProgramToFlash;
    private Text OTAPProgramToFlash;
    private Text MessageToSend;
    private Text OTAPKey;
    private Text nodeKey;
    private Text GWID;
    private Button showRuler;
    private Group group2;
    private Button flash;
    private Button otapflash;
    private Button reset;
    private Button send;
    private Button RemoveNodeButton;
    private Button AddNodeButton;
    private Button confNodeButton;

    /**
     * Auto-generated main method to display this org.eclipse.swt.widgets.Composite inside a new
     * Shell.
     */
    public static void main(final String[] args) {
        showGUI();

    }

    /**
     * Auto-generated method to display this org.eclipse.swt.widgets.Composite inside a new Shell.
     */
    public static void showGUI() {
        final Display display = Display.getDefault();
        final Shell shell = new Shell(display);
        //final GeneralPreferencesComposite inst = new GeneralPreferencesComposite(shell, SWT.NULL);
        final TestbedControlPreferencesComposite inst = new TestbedControlPreferencesComposite(shell, SWT.NULL);

        final Point size = inst.getSize();
        shell.setLayout(new FillLayout());
        shell.layout();
        if ((size.x == 0) && (size.y == 0)) {
            inst.pack();
            shell.pack();
        } else {
            final Rectangle shellBounds = shell.computeTrim(0, 0, size.x, size.y);
            shell.setSize(shellBounds.width, shellBounds.height);
        }
        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    public TestbedControlPreferencesComposite(final org.eclipse.swt.widgets.Composite parent, final int style) {
        super(parent, style);
        initGUI();
    }

    private void initGUI() {
        try {
            final GridLayout thisLayout = new GridLayout(1, true);
            thisLayout.makeColumnsEqualWidth = true;
            this.setLayout(thisLayout);
            this.setSize(330, 415);
            {
                group1 = new Group(this, SWT.NONE);
                final GridLayout group1Layout = new GridLayout();
                //group1Layout.makeColumnsEqualWidth = true;
                group1Layout.numColumns = 4;
                group1.setLayout(group1Layout);
                final GridData group1LData = new GridData();
                group1LData.heightHint = 120;
                group1LData.horizontalAlignment = GridData.FILL;
                group1LData.widthHint = 504;
                group1LData.grabExcessHorizontalSpace = true;
                group1.setLayoutData(group1LData);
                group1.setText("General");
                {





                    {
                        final GridData GeneralData = new GridData();
                        GeneralData.verticalAlignment = GridData.BEGINNING;
                        GeneralData.horizontalAlignment = GridData.BEGINNING;
                        //GeneralData.grabExcessHorizontalSpace = true;
                        flash = new Button(group1, SWT.PUSH | SWT.LEFT);
                        flash.setLayoutData(GeneralData);
                        flash.setText("Flash");
                        flash.addSelectionListener(new SelectionAdapter() {

                            @Override
                            public void widgetSelected(final SelectionEvent evt) {

                                new TestbedControler(config).flash();

                            }
                        });

                        final GridData text1LData = new GridData();
                        text1LData.widthHint = 150;
                        text1LData.heightHint = 17;
                        text1LData.verticalAlignment = GridData.BEGINNING;
                        text1LData.horizontalAlignment = GridData.BEGINNING;
                        text1LData.horizontalSpan = 2;
                        text1LData.grabExcessHorizontalSpace = true;

                        ProgramToFlash = new Text(group1, SWT.BORDER);
                        ProgramToFlash.setText("");
                        ProgramToFlash.setLayoutData(text1LData);
                        ProgramToFlash.addModifyListener(new ModifyListener() {

                            @Override
                            public void modifyText(ModifyEvent me) {
                                config.setFlashProgramImage(ProgramToFlash.getText());
                            }
                        });

                        FlashBrowseButton = new Button(group1, SWT.PUSH | SWT.FLAT);
                        final GridData FlashProgramLData = new GridData();
                        FlashProgramLData.verticalAlignment = GridData.BEGINNING;
                        FlashProgramLData.horizontalAlignment = GridData.BEGINNING;
                        FlashProgramLData.grabExcessHorizontalSpace = true;
                        //FlashProgramLData.verticalSpan = 2;
                        //FlashProgramLData.widthHint = 22;
                        //FlashProgramLData.heightHint = 56;
                        FlashBrowseButton.setLayoutData(FlashProgramLData);
                        FlashBrowseButton.setText("Browse");
                        //setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed.png"));

                        FlashBrowseButton.addSelectionListener(new SelectionAdapter() {

                            @Override
                            public void widgetSelected(final SelectionEvent evt) {
                                final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
                                fileDialog.setFilterExtensions(new String[]{"*.bin;*.hex;*.ihex", "*"});
                                fileDialog.setFilterPath(SpyglassEnvironment.getImageWorkingDirectory());
                                final String file = fileDialog.open();
                                if (file != null) {
                                    ProgramToFlash.setText(file);
                                }
                            }
                        });
                    }



                    {
                        final GridData GeneralData2 = new GridData();
                        GeneralData2.verticalAlignment = GridData.BEGINNING;
                        GeneralData2.horizontalAlignment = GridData.BEGINNING;
                        GeneralData2.grabExcessHorizontalSpace = true;
                        GeneralData2.horizontalSpan = 4;
                        reset = new Button(group1, SWT.PUSH | SWT.LEFT);
                        reset.setLayoutData(GeneralData2);
                        reset.setText("Reset");
                        reset.addSelectionListener(new SelectionAdapter() {

                            @Override
                            public void widgetSelected(final SelectionEvent evt) {

                                new TestbedControler(config).resetNode();


                            }
                        });


                    }
                    {
                        final GridData GeneralData3 = new GridData();
                        GeneralData3.verticalAlignment = GridData.BEGINNING;
                        GeneralData3.horizontalAlignment = GridData.BEGINNING;
                        GeneralData3.grabExcessHorizontalSpace = true;
                        send = new Button(group1, SWT.PUSH | SWT.LEFT);
                        send.setLayoutData(GeneralData3);
                        send.setText("Send");
                        send.addSelectionListener(new SelectionAdapter() {

                            @Override
                            public void widgetSelected(final SelectionEvent evt) {

                                new TestbedControler(config).send();

                            }
                        });


                        final GridData text2LData = new GridData();
                        text2LData.widthHint = 150;
                        text2LData.heightHint = 17;
                        text2LData.verticalAlignment = GridData.BEGINNING;
                        text2LData.horizontalAlignment = GridData.BEGINNING;
                        MessageToSend = new Text(group1, SWT.BORDER);
                        MessageToSend.setText("");
                        MessageToSend.setLayoutData(text2LData);
                        MessageToSend.addModifyListener(new ModifyListener() {

                            @Override
                            public void modifyText(ModifyEvent me) {
                                config.setSentMessage(MessageToSend.getText());
                            }
                        });

                    }

                }
            }




            {
                group2 = new Group(this, SWT.NONE);
                final GridLayout group2Layout = new GridLayout();
                //group1Layout.makeColumnsEqualWidth = true;
                group2Layout.numColumns = 4;
                group2.setLayout(group2Layout);
                final GridData group2LData = new GridData();
                group2LData.heightHint = 120;
                group2LData.horizontalAlignment = GridData.FILL;
                group2LData.widthHint = 504;
                group2LData.grabExcessHorizontalSpace = true;
                group2.setLayoutData(group2LData);
                group2.setText("OTAP");
                {

                    {
                        final GridData GeneralData = new GridData();
                        GeneralData.verticalAlignment = GridData.BEGINNING;
                        GeneralData.horizontalAlignment = GridData.BEGINNING;
                        //GeneralData.grabExcessHorizontalSpace = true;
                        otapflash = new Button(group2, SWT.PUSH | SWT.LEFT);
                        otapflash.setLayoutData(GeneralData);
                        otapflash.setText("FlashOTAP");
                        otapflash.addSelectionListener(new SelectionAdapter() {

                            @Override
                            public void widgetSelected(final SelectionEvent evt) {

                                new TestbedControler(config).FlashOTAP();
                            }
                        });

                        final GridData text1LData = new GridData();
                        text1LData.widthHint = 150;
                        text1LData.heightHint = 17;
                        text1LData.verticalAlignment = GridData.BEGINNING;
                        text1LData.horizontalAlignment = GridData.BEGINNING;
                        text1LData.horizontalSpan = 2;
                        text1LData.grabExcessHorizontalSpace = true;

                        OTAPProgramToFlash = new Text(group2, SWT.BORDER);
                        OTAPProgramToFlash.setText("");
                        OTAPProgramToFlash.setLayoutData(text1LData);
                        OTAPProgramToFlash.addModifyListener(new ModifyListener() {

                            @Override
                            public void modifyText(ModifyEvent me) {
                                config.setOTAPFlashProgramImage(OTAPProgramToFlash.getText());
                            }
                        });

                        OTAPFlashBrowseButton = new Button(group2, SWT.PUSH | SWT.FLAT);
                        final GridData FlashProgramLData = new GridData();
                        FlashProgramLData.verticalAlignment = GridData.BEGINNING;
                        FlashProgramLData.horizontalAlignment = GridData.BEGINNING;
                        FlashProgramLData.grabExcessHorizontalSpace = true;
                        //FlashProgramLData.verticalSpan = 2;
                        //FlashProgramLData.widthHint = 22;
                        //FlashProgramLData.heightHint = 56;
                        OTAPFlashBrowseButton.setLayoutData(FlashProgramLData);
                        OTAPFlashBrowseButton.setText("Browse");
                        //setImage(SWTResourceManager.getImage("de/uniluebeck/itm/spyglass/gui/configuration/chain_small_closed.png"));

                        OTAPFlashBrowseButton.addSelectionListener(new SelectionAdapter() {

                            @Override
                            public void widgetSelected(final SelectionEvent evt) {
                                final FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
                                fileDialog.setFilterExtensions(new String[]{"*.bin;*.hex;*.ihex", "*"});
                                fileDialog.setFilterPath(SpyglassEnvironment.getImageWorkingDirectory());
                                final String file = fileDialog.open();
                                if (file != null) {
                                    OTAPProgramToFlash.setText(file);
                                }
                            }
                        });
                    }




                    {


                        label12 = new Label(group2, SWT.NONE);
                        final GridData label12LData = new GridData();
                        label12.setLayoutData(label12LData);
                        label12.setText("Key: ");


                        final GridData text2LData = new GridData();
                        text2LData.widthHint = 150;
                        text2LData.heightHint = 17;
                        text2LData.verticalAlignment = GridData.BEGINNING;
                        text2LData.horizontalAlignment = GridData.BEGINNING;
                        OTAPKey = new Text(group2, SWT.BORDER);
                        OTAPKey.setText("");
                        OTAPKey.setLayoutData(text2LData);
                        OTAPKey.addModifyListener(new ModifyListener() {

                            @Override
                            public void modifyText(ModifyEvent me) {
                                config.setOTAPFlashProgramKey(OTAPKey.getText());
                            }
                        });

                    }

                }
            }

            {
                group3 = new Group(this, SWT.NONE);
                final GridLayout group3Layout = new GridLayout();
                group3Layout.numColumns = 2;
                group3.setLayout(group3Layout);
                group3.setText("Node Topology");
                final GridData group3LData = new GridData();
                group3LData.horizontalAlignment = GridData.FILL;
                group3LData.heightHint = 150;
                group3LData.grabExcessHorizontalSpace = true;
                group3.setLayoutData(group3LData);
                {
                    label12 = new Label(group3, SWT.NONE);
                    final GridData label12LData = new GridData();
                    label12.setLayoutData(label12LData);
                    label12.setText("NodeID: ");
                }
                {
                    nodeID = new Text(group3, SWT.BORDER);
                    final GridData text7LData = new GridData();
                    text7LData.widthHint = 46;
                    text7LData.heightHint = 17;
                    text7LData.verticalAlignment = GridData.BEGINNING;
                    text7LData.horizontalAlignment = GridData.BEGINNING;
                    nodeID.setLayoutData(text7LData);
                    nodeID.setText("");
                    nodeID.addModifyListener(new ModifyListener() {

                        @Override
                        public void modifyText(ModifyEvent me) {
                            config.setNodeID(nodeID.getText());
                        }
                    });
                }
                {
                    label12 = new Label(group3, SWT.NONE);
                    final GridData label12LData = new GridData();
                    label12.setLayoutData(label12LData);
                    label12.setText("GatewayID: ");
                }
                {
                    GWID = new Text(group3, SWT.BORDER);
                    final GridData text7LData = new GridData();
                    text7LData.widthHint = 46;
                    text7LData.heightHint = 17;
                    text7LData.verticalAlignment = GridData.BEGINNING;
                    text7LData.horizontalAlignment = GridData.BEGINNING;
                    GWID.setLayoutData(text7LData);
                    GWID.setText("");
                    GWID.addModifyListener(new ModifyListener() {

                        @Override
                        public void modifyText(ModifyEvent me) {
                            config.setGWID(GWID.getText());
                        }
                    });
                }

                {
                    label12 = new Label(group3, SWT.NONE);
                    final GridData label12LData = new GridData();
                    label12.setLayoutData(label12LData);
                    label12.setText("Key: ");
                }
                {
                    nodeKey = new Text(group3, SWT.BORDER);
                    final GridData text7LData = new GridData();
                    text7LData.widthHint = 150;
                    text7LData.heightHint = 17;
                    text7LData.verticalAlignment = GridData.BEGINNING;
                    text7LData.horizontalAlignment = GridData.BEGINNING;
                    nodeKey.setLayoutData(text7LData);
                    nodeKey.setText("");
                    nodeKey.addModifyListener(new ModifyListener() {

                        @Override
                        public void modifyText(ModifyEvent me) {
                            config.setNodeKey(nodeKey.getText());
                        }
                    });
                }



                {
                    AddNodeButton = new Button(group3, SWT.PUSH | SWT.FLAT);
                    final GridData FlashProgramLData = new GridData();
                    FlashProgramLData.verticalAlignment = GridData.BEGINNING;
                    FlashProgramLData.horizontalAlignment = GridData.BEGINNING;
                    FlashProgramLData.grabExcessHorizontalSpace = true;
                    AddNodeButton.setLayoutData(FlashProgramLData);
                    AddNodeButton.setText("AddNode");


                    AddNodeButton.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(final SelectionEvent evt) {
                            new TestbedControler(config).addNode();
                        }
                    });


                    RemoveNodeButton = new Button(group3, SWT.PUSH | SWT.FLAT);
                    final GridData NodeLData = new GridData();
                    NodeLData.verticalAlignment = GridData.BEGINNING;
                    NodeLData.horizontalAlignment = GridData.BEGINNING;
                    NodeLData.grabExcessHorizontalSpace = true;
                    RemoveNodeButton.setLayoutData(NodeLData);
                    RemoveNodeButton.setText("RemoveNode");


                    RemoveNodeButton.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(final SelectionEvent evt) {
                            new TestbedControler(config).removeNode();

                        }
                    });

                    confNodeButton = new Button(group3, SWT.PUSH | SWT.FLAT);
                    final GridData NodeL2Data = new GridData();
                    NodeL2Data.verticalAlignment = GridData.BEGINNING;
                    NodeL2Data.horizontalAlignment = GridData.BEGINNING;
                    NodeL2Data.grabExcessHorizontalSpace = true;
                    confNodeButton.setLayoutData(NodeL2Data);
                    confNodeButton.setText("Change Configeration");


                    confNodeButton.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(final SelectionEvent evt) {
                            
                            org.eclipse.swt.program.Program.launch("topology.xml");

                            String wisebedSkriptsHome = System.getenv("WISEBED_HOME");
                            String skriptExtension = "";
                            /*String commaSkip = "";
                            String shellProg = "bash";

                            String os = System.getProperty("os.name").toLowerCase();
                            if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {

                                skriptExtension = "";
                                commaSkip = "";
                                shellProg = "bash";

                            } else {

                                skriptExtension = ".bat";
                                commaSkip = "\"";
                                shellProg = "cmd.exe";

                            }*/

               

//                            Runtime rt = Runtime.getRuntime();
//                            String args[] = new String[]{shellProg, "/C", "start", "nodepad.exe", "-Dtestbed.secretreservationkeys=" + commaSkip + key.getUrnPrefix() + "," + key.getSecretReservationKey() + commaSkip, "-Dtestbed.nodeurns=", "-jar", commaSkip + wisebedSkriptsHome + System.getProperty("file.separator") + "..\\lib\\tr.scripting-client-0.7.2-SNAPSHOT-onejar.jar" + commaSkip, "-p", commaSkip + wisebedSkriptsHome + System.getProperty("file.separator") + "movedetect.properties" + commaSkip, "-f", commaSkip + wisebedSkriptsHome + System.getProperty("file.separator") + "..\\scripts\\reset.java" + commaSkip, "-v"};
//                            try {
//                                Process proc = rt.exec(args);
//                                proc.waitFor();
//                                BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//                                String line = "";
//                                while ((line = buf.readLine()) != null) {
//                                    System.out.println(line);
//                                }
//
//
//                            } catch (InterruptedException ex) {
//                                Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
//
//                            } catch (IOException ex) {
//                                Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
//                            }

                        }
                    });






                }
                {
                }
            }
            this.layout();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
    DataBindingContext dbc;
    TestbedControlSettingsXMLConfig config;
    Binding lockBinding;

    public void setDatabinding(final DataBindingContext dbc, final TestbedControlSettingsXMLConfig config) {

        this.config = config;
        this.dbc = dbc;


        final IObservableValue observableVisible = BeansObservables.observeValue(dbc.getValidationRealm(), config, "showRuler");
        dbc.bindValue(SWTObservables.observeSelection(this.showRuler), observableVisible,
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);

        final IObservableValue observableVisible2 = BeansObservables.observeValue(dbc.getValidationRealm(), config, "FlashProgramImage");
        dbc.bindValue(SWTObservables.observeSelection(this.FlashBrowseButton), observableVisible2,
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);

        final IObservableValue modelObservable2b = BeansObservables.observeValue(dbc.getValidationRealm(), config, "FlashProgramImage");
        dbc.bindValue(SWTObservables.observeText(this.ProgramToFlash, SWT.Modify), modelObservable2b, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_CONVERT),
                new UpdateValueStrategy());


        final IObservableValue observableVisible3 = BeansObservables.observeValue(dbc.getValidationRealm(), config, "OTAPFlashProgramImage");
        dbc.bindValue(SWTObservables.observeSelection(this.OTAPFlashBrowseButton), observableVisible3,
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_CONVERT), null);

        final IObservableValue modelObservable3b = BeansObservables.observeValue(dbc.getValidationRealm(), config, "OTAPFlashProgramKey");
        dbc.bindValue(SWTObservables.observeText(this.OTAPKey, SWT.Modify), modelObservable3b, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_CONVERT),
                new UpdateValueStrategy());
        final IObservableValue modelObservable3c = BeansObservables.observeValue(dbc.getValidationRealm(), config, "OTAPFlashProgramImage");
        dbc.bindValue(SWTObservables.observeText(this.OTAPProgramToFlash, SWT.Modify), modelObservable3c, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_CONVERT),
                new UpdateValueStrategy());



        final IObservableValue modelObservable4 = BeansObservables.observeValue(dbc.getValidationRealm(), config, "NodeID");
        dbc.bindValue(SWTObservables.observeText(this.nodeID, SWT.Modify), modelObservable4, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_CONVERT),
                new UpdateValueStrategy());
        final IObservableValue modelObservable4b = BeansObservables.observeValue(dbc.getValidationRealm(), config, "GWID");
        dbc.bindValue(SWTObservables.observeText(this.GWID, SWT.Modify), modelObservable4b, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_CONVERT),
                new UpdateValueStrategy());

        final IObservableValue modelObservable4c = BeansObservables.observeValue(dbc.getValidationRealm(), config, "NodeKey");
        dbc.bindValue(SWTObservables.observeText(this.nodeKey, SWT.Modify), modelObservable4c, new UpdateValueStrategy(
                UpdateValueStrategy.POLICY_CONVERT),
                new UpdateValueStrategy());



    }
}
