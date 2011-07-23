package de.uniluebeck.itm.spyglass.io.wisebed;

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

import static com.google.common.collect.Lists.newArrayList;

/**
 * reads packets from wsn-service
 *
 * @author Jens Kluttig, Daniel Bimschas
 */
public class WisebedPacketReader extends SpyglassPacketRecorder {

    private static final Logger log = LoggerFactory.getLogger(WisebedPacketReader.class);
    private static final String DB_NAME = "db.sqlite";
    private static final String TABLE_NAME = "events";
    private static final String NODE_ID_FIELD = "node_id";
    private static final String POS_X = "pos_x";
    private static final String POS_Y = "pos_y";
    private static final String EVENT_TIME = "event_time";
    private static final String LOG_TIME = "log_time";
    private static final String EVENT_TYPE = "event_type";
    private static final String NODE_ID_INDEX = "full_name_index";
    private static final String EVTYPE_INDEX = "dob_index";
    private SqlJetDb db;
    private File dbFile;

    @WebService(name = "ProxyController", targetNamespace = "urn:ControllerService")
    public class WisebedPacketReaderController implements Controller {

        @Override
        public void experimentEnded() {
            MessageDialog.openInformation(
                    null,
                    "Testbed experiment ended",
                    "The experiment on the testbed ended and all data was received.");
        }

        @Override
        public void receive(@WebParam(name = "msg", targetNamespace = "") final List<Message> messages) {

            for (Message msg : messages) {

                boolean isValidSpyglassPacket = msg.getBinaryData() != null
                        && msg.getBinaryData().length > 0
                        && msg.getBinaryData()[0] == 111;

                if (isValidSpyglassPacket) {

                    try {

                        if (log.isDebugEnabled()) {
                            log.debug("Received SpyGlass packet from \"{}\" @ {}: {}",
                                    new Object[]{
                                        msg.getSourceNodeId(),
                                        msg.getTimestamp(),
                                        StringUtils.toPrintableString(msg.getBinaryData())
                                    });
                        }

                        add(factory.createInstance(extractBinaryPayload(msg)));



                    } catch (SpyglassPacketException e) {
                        log.warn("Exception while parsing SpyGlass packet. Ignoring packet from \"\" @ {}: {}",
                                new Object[]{
                                    msg.getSourceNodeId(),
                                    msg.getTimestamp(),
                                    StringUtils.toPrintableString(msg.getBinaryData())
                                });
                    }

                    SpyglassPacket spyglassPacket = null;
                    try {
                        spyglassPacket = factory.createInstance(extractBinaryPayload(msg));
                    } catch (SpyglassPacketException ex) {
                        java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                    }



                    long logTime = System.currentTimeMillis();
                    int nodeID = spyglassPacket.getSenderId();
                    int posx = spyglassPacket.getPosition().x;
                    int posy = spyglassPacket.getPosition().y;
                    long eventTime = spyglassPacket.getTime().getMillis();
                    String eventType = String.valueOf(spyglassPacket.getSemanticType());
                    try {
                        db = SqlJetDb.open(dbFile, true);
                    } catch (SqlJetException ex) {
                        java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    System.out.println();
                    System.out.println(">Database schema objects:");
                    System.out.println();
                    //System.out.println(db.getSchema());
                    //System.out.println(db.getOptions());

                    // insert rows:

                    try {
                        db.beginTransaction(SqlJetTransactionMode.WRITE);
                        ISqlJetTable table = db.getTable(TABLE_NAME);
                        table.insert(logTime, nodeID, posx, posy, eventTime, eventType);




                    } catch (SqlJetException ex) {
                        java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            db.commit();
                        } catch (SqlJetException ex) {
                            java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else {
                    if (msg != null && msg.getBinaryData() != null
                            && msg.getBinaryData().length > 0) {
                        dataPlugin.processPacket(extractBinaryPayload(msg));
                    }
                }

                /*if (log.isDebugEnabled()) {
                log.debug("Received non-SpyGlass binary packet from \"{}\" @ {}: {}",
                new Object[]{
                msg.getSourceNodeId(),
                msg.getTimestamp(),
                StringUtils.toPrintableString(msg.getBinaryData())
                });
                }*/
            }
        }

        @Override
        public void receiveNotification(
                @WebParam(name = "msg", targetNamespace = "") final List<String> notifications) {
            for (String notification : notifications) {
                MessageDialog.openInformation(
                        null,
                        "Testbed Notification",
                        notification);
            }
        }

        @Override
        public void receiveStatus(
                @WebParam(name = "status", targetNamespace = "") final List<RequestStatus> requestStatuses) {
            // nothing to do
        }

        private byte[] extractBinaryPayload(final Message msg) {
            byte[] binaryData = msg.getBinaryData();
            byte[] binaryPayload = new byte[binaryData.length - 1];
            System.arraycopy(binaryData, 1, binaryPayload, 0, binaryPayload.length);
            return binaryPayload;
        }
    }
    private WisebedPacketReaderController controller = new WisebedPacketReaderController();
    private Endpoint controllerEndpoint;
    @Element(required = true)
    private String sessionManagementEndpointUrl;
    @Element(required = true)
    private String controllerEndpointUrl;
    @ElementList(type = StringTuple.class, required = true)
    private List<StringTuple> secretReservationKeys = Lists.newArrayList();
    private DataAnalyzerPlugin dataPlugin;
    private static String currentSecretReservationKey;
    private static String currentUrnPrefix;

    public static String getCurrentUrnPrefix() {
        return currentUrnPrefix;
    }

    public static String getCurrentSecretReservationKey() {
        return currentSecretReservationKey;
    }

    /**
     * Constructor for Simple XML reflection instantiation
     */
    @SuppressWarnings("unused")
    WisebedPacketReader() {
        setSourceType(SOURCE_TYPE.OTHER);
    }

    public WisebedPacketReader(final String controllerEndpointUrl, final String sessionManagementEndpointUrl,
            final List<SecretReservationKey> secretReservationKeys) {

        this.controllerEndpointUrl = controllerEndpointUrl;
        this.sessionManagementEndpointUrl = sessionManagementEndpointUrl;

        copySRKsToXMLRepresentation(secretReservationKeys);

        dbFile = new File(DB_NAME);
        if (!dbFile.exists()) {
            try {

                dbFile.delete();

                // create database, table and two indices:
                db = null;
                try {
                    db = SqlJetDb.open(dbFile, true);
                } catch (SqlJetException ex) {
                    java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    // set DB option that have to be set before running any transactions: 
                    db.getOptions().setAutovacuum(true);
                } catch (SqlJetException ex) {
                    java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    // set DB option that have to be set in a transaction: 
                    db.runTransaction(new ISqlJetTransaction() {

                        public Object run(SqlJetDb db) throws SqlJetException {
                            db.getOptions().setUserVersion(1);
                            return true;
                        }
                    }, SqlJetTransactionMode.WRITE);
                } catch (SqlJetException ex) {
                    java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    db.beginTransaction(SqlJetTransactionMode.WRITE);
                } catch (SqlJetException ex) {
                    java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                }
                try {
                    // String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" + POS_X + " TEXT NOT NULL PRIMARY KEY , " + NODE_ID_FIELD + " TEXT NOT NULL, " + POS_Y + " INTEGER NOT NULL)";
                    String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" + LOG_TIME + " INTEGER NOT NULL PRIMARY KEY , " + NODE_ID_FIELD + " TEXT , " + POS_X + " INTEGER , " + POS_Y + " INTEGER , " + EVENT_TIME + " INTEGER , " + EVENT_TYPE + " TEXT NOT NULL)";
                    //String createFirstNameIndexQuery = "CREATE INDEX " + NODE_ID_INDEX + " ON " + TABLE_NAME + "(" + NODE_ID_FIELD + ")";
                    //String createDateIndexQuery = "CREATE INDEX " + EVTYPE_INDEX + " ON " + TABLE_NAME + "(" + EVENT_TYPE + ")";
                    //System.out.println();
                    //System.out.println(">DB schema queries:");
                    //System.out.println();
                    //System.out.println(createTableQuery);
                    //System.out.println(createFirstNameIndexQuery);
                    //System.out.println(createDateIndexQuery);
                    try {
                        db.createTable(createTableQuery);
                    } catch (SqlJetException ex) {
                        java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                    try {
//                        db.createIndex(createFirstNameIndexQuery);
//                    } catch (SqlJetException ex) {
//                        java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                    try {
//                        db.createIndex(createDateIndexQuery);
//                    } catch (SqlJetException ex) {
//                        java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
//                    }
                } finally {
                    try {
                        db.commit();
                    } catch (SqlJetException ex) {
                        java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                // close DB and open it again (as part of example code)

                db.close();
            } catch (SqlJetException ex) {
                java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void init(final Spyglass spyglass) {

        super.init(spyglass);

        try {
            startLocalControllerEndpoint();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            connectToExperiment();
        } catch (Exception e) {
            log.warn("Exception while connecting to experiment: " + e, e);
            stopLocalControllerEndpointIfRunning();
            throw new RuntimeException(e);
        }

        dataPlugin = new DataAnalyzerPlugin();
        dataPlugin.init(this);
    }

    @Override
    public void shutdown() throws IOException {
        log.debug("Shutting down WisebedPacketReader...");
        if (controllerEndpoint.isPublished()) {
            controllerEndpoint.stop();
        }
        dataPlugin.shutdown();
        dataPlugin = null;
        System.gc();
        super.shutdown();
    }

    private void stopLocalControllerEndpointIfRunning() {
        if (controllerEndpoint != null && controllerEndpoint.isPublished()) {
            controllerEndpoint.stop();
        }
    }

    private void startLocalControllerEndpoint() throws Exception {
        stopLocalControllerEndpointIfRunning();
        controllerEndpoint = Endpoint.publish(controllerEndpointUrl, controller);
        log.debug("Started local Controller endpoint at {}", controllerEndpointUrl);
    }

    private void connectToExperiment() throws Exception {
        try {

            SessionManagement sm = WSNServiceHelper.getSessionManagementService(sessionManagementEndpointUrl);
            sm.getInstance(copyXMLRepresentationToSRKs(), controllerEndpointUrl);
            log.debug("Successfully connected to experiment!");

        } catch (Exception e) {
            log.error("Calling getInstance() on the Session Management service failed: {}" + e, e);
            throw e;
        }
    }

    private void copySRKsToXMLRepresentation(final List<SecretReservationKey> secretReservationKeys) {
        this.secretReservationKeys = newArrayList();
        for (SecretReservationKey secretReservationKey : secretReservationKeys) {
            this.secretReservationKeys.add(new StringTuple(secretReservationKey.getUrnPrefix(),
                    secretReservationKey.getSecretReservationKey()));

            this.currentSecretReservationKey = secretReservationKey.getSecretReservationKey();
            this.currentUrnPrefix = secretReservationKey.getUrnPrefix();
        }
    }

    private List<SecretReservationKey> copyXMLRepresentationToSRKs() {
        List<SecretReservationKey> reservation = Lists.newArrayList();
        for (StringTuple tuple : this.secretReservationKeys) {
            SecretReservationKey key = new SecretReservationKey();
            key.setUrnPrefix(tuple.first);
            key.setSecretReservationKey(tuple.second);
            reservation.add(key);
            this.currentSecretReservationKey = tuple.second;
            this.currentUrnPrefix = tuple.first;

        }
        return reservation;
    }

    public void InjectPackage(SpyglassPacket pkg) {
        add(pkg);


                    long logTime = System.currentTimeMillis();
                    int nodeID = pkg.getSenderId();
                    int posx = pkg.getPosition().x;
                    int posy = pkg.getPosition().y;
                    long eventTime = pkg.getTime().getMillis();
                    String eventType = String.valueOf(pkg.getSemanticType());
                    try {
                        db = SqlJetDb.open(dbFile, true);
                    } catch (SqlJetException ex) {
                        java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    System.out.println();
                    System.out.println(">Database schema objects:");
                    System.out.println();
                    //System.out.println(db.getSchema());
                    //System.out.println(db.getOptions());

                    // insert rows:

                    try {
                        db.beginTransaction(SqlJetTransactionMode.WRITE);
                        ISqlJetTable table = db.getTable(TABLE_NAME);
                        table.insert(logTime, nodeID, posx, posy, eventTime, eventType);




                    } catch (SqlJetException ex) {
                        java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        try {
                            db.commit();
                        } catch (SqlJetException ex) {
                            java.util.logging.Logger.getLogger(WisebedPacketReader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

    }
}
