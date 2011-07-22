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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import sun.util.logging.resources.logging;

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
        String args[] = new String[]{"java", "-Dtestbed.secretreservationkeys=" + commaSkip + key.getUrnPrefix() + "," + key.getSecretReservationKey() + commaSkip, "-Dtestbed.message=" + commaSkip + message + commaSkip, "-Dtestbed.nodeurns=", "-jar", commaSkip + wisebedSkriptsHome + System.getProperty("file.separator") + "..\\lib\\tr.scripting-client-0.7.2-SNAPSHOT-onejar.jar" + commaSkip, "-p", commaSkip + wisebedSkriptsHome + System.getProperty("file.separator") + "movedetect.properties" + commaSkip,  "-f", commaSkip + wisebedSkriptsHome + System.getProperty("file.separator") + "..\\scripts\\send.java" + commaSkip, "-v"};

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
