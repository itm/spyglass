package de.uniluebeck.itm.spyglass.testbedControl;

import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferenceDialog;
import de.uniluebeck.itm.spyglass.gui.configuration.PluginPreferencePage;
import de.uniluebeck.itm.spyglass.gui.configuration.PropertyBean;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.plugin.Plugin;
import de.uniluebeck.itm.spyglass.plugin.PluginManager;
import de.uniluebeck.itm.spyglass.xmlconfig.PluginXMLConfig;
import de.uniluebeck.itm.spyglass.xmlconfig.TestbedControlSettingsXMLConfig;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class testbedControler implements PropertyChangeListener{

private  TestbedControlSettingsXMLConfig config;




public testbedControler(TestbedControlSettingsXMLConfig config){
        
        this.config = config;

        config.addPropertyChangeListener(this);
}





public void send(){
   /* config.getConfigStore().getSpyglassConfig().getTestbedControlSettings().getFlashProgramKey();
    config.getConfigStore().getSpyglassConfig().getTestbedSettings().getControllerUrn();*/
        System.out.println("send");
}

public void flash(){
        System.out.println("flashing"+config.getNodeKey());
}



public void resetNode(){
    System.out.println("reset");
}

public void addNode(){
        System.out.println("addnode");
}


public void removeNode(){
        System.out.println("remove node");
    
}


public void FlashOTAP(){
        System.out.println("flash otap");
}

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        

    }










}