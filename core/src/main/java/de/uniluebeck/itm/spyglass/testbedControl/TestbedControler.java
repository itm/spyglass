package de.uniluebeck.itm.spyglass.testbedControl;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;

import de.uniluebeck.itm.spyglass.io.wisebed.WisebedPacketReader;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.TestbedControlSettingsXMLConfig;
import de.uniluebeck.itm.tr.util.StringUtils;
import eu.wisebed.api.sm.SecretReservationKey;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.log4j.spi.LoggerFactory;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import sun.util.logging.resources.logging;

import java.util.*;
import java.nio.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;

import eu.wisebed.testbed.api.rs.RSServiceHelper;
//import eu.wisebed.testbed.api.rs.v1.PublicReservationData;
//import eu.wisebed.testbed.api.rs.v1.RS;

//import eu.wisebed.testbed.api.snaa.v1.SNAA;
//import eu.wisebed.testbed.api.snaa.v1.AuthenticationTriple;
//import eu.wisebed.testbed.api.snaa.v1.SecretAuthenticationKey;
import eu.wisebed.testbed.api.snaa.helpers.SNAAServiceHelper;

import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import eu.wisebed.api.controller.*;
import eu.wisebed.api.common.*;
import eu.wisebed.api.sm.*;
import eu.wisebed.api.wsn.*;

import de.uniluebeck.itm.tr.util.*;
import de.itm.uniluebeck.tr.wiseml.WiseMLHelper;

import de.uniluebeck.itm.wisebed.cmdlineclient.*;
import de.uniluebeck.itm.wisebed.cmdlineclient.wrapper.*;
import java.util.concurrent.Future;
import com.google.common.collect.*;
import de.uniluebeck.itm.wisebed.cmdlineclient.jobs.JobResult;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class TestbedControler implements PropertyChangeListener {

    private TestbedControlSettingsXMLConfig config;
    private static String wisebedSkriptsHome;
    private static String skriptExtension;
    private static String commaSkip;
    private static String shellProg;

    public TestbedControler(TestbedControlSettingsXMLConfig config) {

        this.config = config;

        config.addPropertyChangeListener(this);


        wisebedSkriptsHome = System.getenv("WISEBED_HOME");

        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {

            skriptExtension = "";
            commaSkip = "";
            shellProg = "";

        } else {

            skriptExtension = ".bat";
            commaSkip = "\"";
            shellProg = "cmd /c start ";

        }



    }

    public static void send(String message) {

        
        String localControllerEndpointURL = null;
        try {
            localControllerEndpointURL = "http://" + InetAddress.getLocalHost().getCanonicalHostName() + ":8089/controller";
        } catch (UnknownHostException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }
        //String secretReservationKeys = System.getProperty("testbed.secretreservationkeys");
        String messageToSend = message;
        String nodeUrnToFlash = null;//System.getProperty("testbed.nodeurns");

        // Endpoint URLs of Authentication (SNAA), Reservation (RS) and Experimentation (iWSN) services
        String sessionManagementEndpointURL = WisebedPacketReader.getSessionManagementEndpointUrl();//System.getProperty("testbed.sm.endpointurl");

        // Retrieve Java proxies of the endpoint URLs above
        SessionManagement sessionManagement = WSNServiceHelper.getSessionManagementService(sessionManagementEndpointURL);


//--------------------------------------------------------------------------
// Application logic
//--------------------------------------------------------------------------
        String wsnEndpointURL = null;

        List keys = new ArrayList();
        keys.add(WisebedPacketReader.getCurrentWholeReservationKey());
        try {
            try {
                wsnEndpointURL = sessionManagement.getInstance(keys, localControllerEndpointURL);
            } catch (ExperimentNotRunningException_Exception ex) {
                Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (UnknownReservationIdException_Exception e) {
            //log.warn("There was not reservation found with the given secret reservation key. Exiting.");
            System.exit(1);
        }

        //log.info("Got a WSN instance URL, endpoint is: {}", wsnEndpointURL);
        WSN wsnService = WSNServiceHelper.getWSNService(wsnEndpointURL);
        final WSNAsyncWrapper wsn = WSNAsyncWrapper.of(wsnService);
        Controller controller = new Controller() {

            public void receive(List msg) {
                // nothing to do
            }

            public void receiveStatus(List requestStatuses) {
                wsn.receive(requestStatuses);
            }

            public void receiveNotification(List msgs) {
//                for (int i = 0; i < msgs.size(); i++) {
//                    log.info(msgs.get(i));
//                }
            }

            public void experimentEnded() {
                //log.info("Experiment ended");
                System.exit(0);
            }
        };

        DelegatingController delegator = new DelegatingController(controller);
        try {
            delegator.publish(localControllerEndpointURL);
            //log.info("Local controller published on url: {}", localControllerEndpointURL);
        } catch (MalformedURLException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }
        //log.info("Local controller published on url: {}", localControllerEndpointURL);





        // retrieve reserved node URNs from testbed
        List nodeURNs = null;
        if (nodeUrnToFlash != null && !"".equals(nodeUrnToFlash)) {
            nodeURNs = Lists.newArrayList(nodeUrnToFlash.split(","));
        } else {
            try {
                nodeURNs = WiseMLHelper.getNodeUrns(wsn.getNetwork().get(), new String[]{});
            } catch (InterruptedException ex) {
                Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //log.info("Retrieved the following node URNs: {}", nodeURNs);

        // Constructing UART Message from Input String (Delimited by ",")
        // Supported Prefixes are "0x" and "0b", otherwise Base_10 (DEZ) is assumed
        String[] splitMessage = messageToSend.split(",");
        byte[] messageToSendBytes = new byte[splitMessage.length];
        String messageForOutputInLog = "";
        for (int i = 0; i < splitMessage.length; i++) {
            int type = 10;
            if (splitMessage[i].startsWith("0x")) {
                type = 16;
                splitMessage[i] = splitMessage[i].replace("0x", "");
            } else if (splitMessage[i].startsWith("0b")) {
                type = 2;
                splitMessage[i] = splitMessage[i].replace("0b", "");
            }
            BigInteger b = new BigInteger(splitMessage[i], type);
            messageToSendBytes[i] = (byte) b.intValue();
            messageForOutputInLog = messageForOutputInLog + b.intValue() + " ";
        }

        //log.info("Sending Message [ " + messageForOutputInLog + "] to nodes...");

        // Constructing the Message
        Message binaryMessage = new Message();
        binaryMessage.setBinaryData(messageToSendBytes);

        GregorianCalendar c = new GregorianCalendar();
        c.setTimeInMillis(System.currentTimeMillis());
        try {
            binaryMessage.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }
        binaryMessage.setSourceNodeId("urn:wisebed:uzl1:0xFFFF");

        Future sendFuture = wsn.send(nodeURNs, binaryMessage, 3, TimeUnit.MINUTES);
        try {
            JobResult sendJobResult = (JobResult) sendFuture.get();
            // log.info("{}", sendJobResult);
            // log.info("Shutting down...");
            // System.exit(0);
        } catch (InterruptedException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void send2(String message) {
        wisebedSkriptsHome = System.getenv("WISEBED_HOME");

        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {
            skriptExtension = "";
            commaSkip = "";
            shellProg = "";
        } else {
            skriptExtension = ".bat";
            commaSkip = "\"";
            shellProg = "cmd.exe";
        }

        SecretReservationKey key = new SecretReservationKey();
        key.setSecretReservationKey(WisebedPacketReader.getCurrentSecretReservationKey());
        key.setUrnPrefix(WisebedPacketReader.getCurrentUrnPrefix());
//shellProg, "/c", "start", 
        Runtime rt = Runtime.getRuntime();
        String args[] = new String[]{"java", "-Dtestbed.secretreservationkeys=" + commaSkip + key.getUrnPrefix() + "," + key.getSecretReservationKey() + commaSkip, "-Dtestbed.message=" + commaSkip + message + commaSkip, "-Dtestbed.nodeurns=", "-jar", commaSkip + wisebedSkriptsHome + System.getProperty("file.separator") + "..\\lib\\tr.scripting-client-0.7.2-SNAPSHOT-onejar.jar" + commaSkip, "-p", commaSkip + wisebedSkriptsHome + System.getProperty("file.separator") + "movedetect.properties" + commaSkip, "-f", commaSkip + wisebedSkriptsHome + System.getProperty("file.separator") + "..\\scripts\\send.java" + commaSkip, "-v"};

        try {

            Process proc = rt.exec(args);

        } catch (IOException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void send() {


        String message = config.getSentMessage();
        SecretReservationKey key = new SecretReservationKey();
        key.setSecretReservationKey(WisebedPacketReader.getCurrentSecretReservationKey());

        key.setUrnPrefix(WisebedPacketReader.getCurrentUrnPrefix());

        Runtime rt = Runtime.getRuntime();
        try {
            Process proc = rt.exec(new String[]{shellProg + wisebedSkriptsHome + System.getProperty("file.separator") + "send" + skriptExtension, wisebedSkriptsHome + System.getProperty("file.separator") + "movedetect.properties", commaSkip + key.getUrnPrefix() + "," + key.getSecretReservationKey() + commaSkip, commaSkip + message + commaSkip});
            proc.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }


        } catch (InterruptedException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }



        System.out.println("send");
    }

    public void flash() {

        String image = config.getFlashProgramImage();
        SecretReservationKey key = new SecretReservationKey();
        key.setSecretReservationKey(WisebedPacketReader.getCurrentSecretReservationKey());

        key.setUrnPrefix(WisebedPacketReader.getCurrentUrnPrefix());

        Runtime rt = Runtime.getRuntime();
        try {
            System.out.println("flashing");
            Process proc = rt.exec(new String[]{shellProg + wisebedSkriptsHome + System.getProperty("file.separator") + "flash" + skriptExtension, wisebedSkriptsHome + System.getProperty("file.separator") + "movedetect.properties", commaSkip + key.getUrnPrefix() + "," + key.getSecretReservationKey() + commaSkip, image});

            proc.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }


        } catch (InterruptedException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    public void resetNode() {

        SecretReservationKey key = new SecretReservationKey();
        key.setSecretReservationKey(WisebedPacketReader.getCurrentSecretReservationKey());

        key.setUrnPrefix(WisebedPacketReader.getCurrentUrnPrefix());

        Runtime rt = Runtime.getRuntime();
        try {
            Process proc = rt.exec(new String[]{shellProg + wisebedSkriptsHome + System.getProperty("file.separator") + System.getProperty("file.separator") + "reset" + skriptExtension, wisebedSkriptsHome + System.getProperty("file.separator") + "movedetect.properties", commaSkip + key.getUrnPrefix() + "," + key.getSecretReservationKey() + commaSkip});
            proc.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }


        } catch (InterruptedException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }



        System.out.println("reset");
    }

    public void addNode() {

        String gateway = config.getGWID();

        String messageToGW;

        String nodeId = config.getNodeID();



        String nodeKey = config.getNodeKey();

        BigInteger keyInt = null;

        if (!nodeKey.equals("")) {

            keyInt = new BigInteger(nodeKey, 16);

        }

        messageToGW = "0x0A,";

        byte type = 60;

        messageToGW += StringUtils.toHexString(type);
        messageToGW += ",";

        messageToGW += "0x" + nodeId.substring(0, 2);
        messageToGW += ",";


        messageToGW += "0x" + nodeId.substring(2, 4);

        if (keyInt != null) {
            messageToGW += ",";

            byte[] keyByte = keyInt.toByteArray();

            for (int i = 0; i < keyByte.length - 1; i++) {

                messageToGW += StringUtils.toHexString(keyByte[i]);
                messageToGW += ",";
            }


            messageToGW += StringUtils.toHexString(keyByte[keyByte.length - 1]);

        }

        SecretReservationKey key = new SecretReservationKey();

        key.setSecretReservationKey(WisebedPacketReader.getCurrentSecretReservationKey());

        key.setUrnPrefix(WisebedPacketReader.getCurrentUrnPrefix());

        String[] commandString = new String[]{shellProg + wisebedSkriptsHome + System.getProperty("file.separator") + "send" + skriptExtension, wisebedSkriptsHome + System.getProperty("file.separator") + "movedetect.properties", commaSkip + key.getUrnPrefix() + "," + key.getSecretReservationKey() + commaSkip, commaSkip + messageToGW + commaSkip, key.getUrnPrefix() + "0x" + gateway};
//        for (int i = 0; i < commandString.length; i++) {
//            System.out.print(commandString[i] + " ");
//        }


        Runtime rt = Runtime.getRuntime();
        try {
            Process proc = rt.exec(commandString);
            proc.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }


        } catch (InterruptedException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }


        System.out.println("addnode");
    }

    public void removeNode() {

        String gateway = config.getGWID();

        String messageToGW;

        String nodeId = config.getNodeID();


        messageToGW = "0x0A,";
        byte type = 61;

        messageToGW += StringUtils.toHexString(type);
        messageToGW += ",";

        messageToGW += "0x" + nodeId.substring(0, 2);
        messageToGW += ",";


        messageToGW += "0x" + nodeId.substring(2, 4);




        SecretReservationKey key = new SecretReservationKey();

        key.setSecretReservationKey(WisebedPacketReader.getCurrentSecretReservationKey());

        key.setUrnPrefix(WisebedPacketReader.getCurrentUrnPrefix());

        String[] commandString = new String[]{shellProg + wisebedSkriptsHome + System.getProperty("file.separator") + "send" + skriptExtension, wisebedSkriptsHome + System.getProperty("file.separator") + "movedetect.properties", commaSkip + key.getUrnPrefix() + "," + key.getSecretReservationKey() + commaSkip, commaSkip + messageToGW + commaSkip, key.getUrnPrefix() + "0x" + gateway};
        for (int i = 0; i < commandString.length; i++) {
            System.out.print(commandString[i] + " ");
        }


        Runtime rt = Runtime.getRuntime();
        try {
            Process proc = rt.exec(commandString);
            proc.waitFor();
            BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = "";
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
            }


        } catch (InterruptedException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestbedControler.class.getName()).log(Level.SEVERE, null, ex);
        }



        System.out.println("remove node");

    }

    public void FlashOTAP() {
        System.out.println("flash otap");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}
