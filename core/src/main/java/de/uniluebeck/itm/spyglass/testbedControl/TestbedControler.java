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

    private String wisebedSkriptsHome;
    private String skriptExtension;

    public TestbedControler(TestbedControlSettingsXMLConfig config) {

        this.config = config;

        config.addPropertyChangeListener(this);


        wisebedSkriptsHome = System.getenv("WISEBED_HOME");

        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0) {

            skriptExtension = "";

        } else {

            skriptExtension = ".bat";

        }



    }

    public void send() {


        String message = config.getSentMessage();
        SecretReservationKey key = new SecretReservationKey();
        key.setSecretReservationKey(WisebedPacketReader.getCurrentSecretReservationKey());

        key.setUrnPrefix(WisebedPacketReader.getCurrentUrnPrefix());

        Runtime rt = Runtime.getRuntime();
        try {
            Process proc = rt.exec(new String[]{wisebedSkriptsHome + "/send" + skriptExtension, wisebedSkriptsHome + "/movedetect.properties", key.getUrnPrefix() + "," + key.getSecretReservationKey(), message});
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
            Process proc = rt.exec(new String[]{wisebedSkriptsHome + "/flash" + skriptExtension, wisebedSkriptsHome + "/movedetect.properties", key.getUrnPrefix() + "," + key.getSecretReservationKey(), image});

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
            Process proc = rt.exec(new String[]{wisebedSkriptsHome + "/reset" + skriptExtension, wisebedSkriptsHome + "/movedetect.property", key.getUrnPrefix() + "," + key.getSecretReservationKey()});
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

        BigInteger nodeInteger = new BigInteger(nodeId, 16);

        byte[] nodeIdByte = nodeInteger.toByteArray();

        String nodeKey = config.getNodeKey();

        BigInteger keyInt = new BigInteger(nodeKey, 16);

        byte type = 60;

        messageToGW = StringUtils.toHexString(type);
        messageToGW += ",";

        messageToGW += StringUtils.toHexString(nodeIdByte[1]);
        messageToGW += ",";


        messageToGW += StringUtils.toHexString(nodeIdByte[0]);
        messageToGW += ",";

        byte[] keyByte = keyInt.toByteArray();

        for (int i = 0; i < 15; i++) {

            messageToGW += StringUtils.toHexString(keyByte[i]);
            messageToGW += ",";
        }


        messageToGW += StringUtils.toHexString(keyByte[15]);

        SecretReservationKey key = new SecretReservationKey();

        key.setSecretReservationKey(WisebedPacketReader.getCurrentSecretReservationKey());

        key.setUrnPrefix(WisebedPacketReader.getCurrentUrnPrefix());

        String[] commandString = new String[]{wisebedSkriptsHome + "/send" + skriptExtension, wisebedSkriptsHome + "/movedetect.property", key.getUrnPrefix() + "," + key.getSecretReservationKey(), messageToGW, key.getUrnPrefix() + ":0x" + gateway};
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


        System.out.println("addnode");
    }

    public void removeNode() {

        String gateway = config.getGWID();

        String messageToGW;

        String nodeId = config.getNodeID();

        BigInteger nodeInteger = new BigInteger(nodeId, 16);

        byte[] nodeIdByte = nodeInteger.toByteArray();


        byte type = 61;

        messageToGW = StringUtils.toHexString(type);
        messageToGW += ",";

        messageToGW += StringUtils.toHexString(nodeIdByte[1]);
        messageToGW += ",";


        messageToGW += StringUtils.toHexString(nodeIdByte[0]);




        SecretReservationKey key = new SecretReservationKey();

        key.setSecretReservationKey(WisebedPacketReader.getCurrentSecretReservationKey());

        key.setUrnPrefix(WisebedPacketReader.getCurrentUrnPrefix());

        String[] commandString = new String[]{wisebedSkriptsHome + "/send" + skriptExtension, wisebedSkriptsHome +"/movedetect.property", key.getUrnPrefix() + "," + key.getSecretReservationKey(), messageToGW, key.getUrnPrefix() + ":" + gateway};
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
