package de.unisiegen.zess.spyglass.plugin.dataanalyzer;

import de.uniluebeck.itm.spyglass.gui.actions.RecordRecordAction;
import java.io.File;
import com.google.common.collect.Lists;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.io.SpyglassPacketRecorder;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.StringTuple;
import de.uniluebeck.itm.tr.util.StringUtils;
import eu.wisebed.api.common.Message;
import eu.wisebed.api.controller.Controller;
import eu.wisebed.api.controller.RequestStatus;
import eu.wisebed.api.sm.SecretReservationKey;
import eu.wisebed.api.sm.SessionManagement;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.schema.ISqlJetIndexDef;
import org.tmatesoft.sqljet.core.schema.ISqlJetTableDef;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.ISqlJetTransaction;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.lang.Thread;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.util.List;

import de.unisiegen.zess.spyglass.plugin.dataanalyzer.DataAnalyzerPlugin;
import de.unisiegen.zess.spyglass.plugin.dataanalyzer.Injectable;

import static com.google.common.collect.Lists.newArrayList;

public class ZessPacketRecorder extends SpyglassPacketRecorder implements Injectable {

    private static final Logger log = LoggerFactory.getLogger(ZessPacketRecorder.class);
    private Spyglass spg;
    private DataAnalyzerPlugin dataPlugin;
    
    /**
     * Constructor for Simple XML reflection instantiation
     */
    /*@SuppressWarnings("unused")
    public ZessPacketRecorder() {
        setSourceType(SOURCE_TYPE.OTHER);
    }*/

    @Override
    public void init(final Spyglass spyglass) {
        super.init(spyglass);
        spg = spyglass;

        dataPlugin = new DataAnalyzerPlugin();
        dataPlugin.init(this);
    }

    @Override
    public void shutdown() throws IOException {
        log.debug("Shutting down ZessPacketRecorder...");

        if (dataPlugin != null) {
            dataPlugin.shutdown();
            dataPlugin = null;
            System.gc();
        }
  
        super.shutdown();
    }

    public void InjectPackage(SpyglassPacket pkg) {
        add(pkg);
    }
}
