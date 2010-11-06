package de.uniluebeck.itm.spyglass.io;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.SpyglassLoggerFactory;
import eu.wisebed.testbed.api.rs.v1.ConfidentialReservationData;
import eu.wisebed.testbed.api.rs.v1.Data;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import eu.wisebed.testbed.api.wsn.v211.*;
import org.apache.log4j.Logger;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * reads packets from wsn-service
 *
 * @author jens kluttig
 */
@WebService(name = "ProxyController", targetNamespace = "urn:ControllerService")
public class WSNPacketReader extends AbstractPacketReader implements Controller {

    //public final static String CONTROLLER_URN = "http://localhost:8081/spyglass/controller";
    //public final static String CONTROLLER_URN = "http://"141.83.68.131":8081/spyglass/controller";
    public static String CONTROLLER_URN;

    private static Logger logger = SpyglassLoggerFactory.getLogger(WSNPacketReader.class);
    private SessionManagement smService;
    private List<SecretReservationKey> secretReservation;
    private Queue<SpyglassPacket> queue = new ArrayDeque<SpyglassPacket>();
    private static Map<List<SecretReservationKey>, Endpoint> endpointMap = Maps.newHashMap();

    private WSNPacketReader(SessionManagement sessionManagementUrn, List<SecretReservationKey> rsList) {
        try {
            CONTROLLER_URN = "http://" + InetAddress.getLocalHost().getHostAddress() + ":8081/spyglass/controller";
        } catch (UnknownHostException e) {
            logger.error("Unknown Host.", e);
        }
        secretReservation = rsList;
        smService = sessionManagementUrn;
        setSourceType(SOURCE_TYPE.OTHER);
    }

    public static WSNPacketReader createInstance(String sessionManagementUrn, ConfidentialReservationData reservation) {
        SessionManagement sm = WSNServiceHelper.getSessionManagementService(sessionManagementUrn);
        List<SecretReservationKey> rsList = Lists.newArrayList();
        for (Data data : reservation.getData()) {
            SecretReservationKey key = new SecretReservationKey();
            key.setUrnPrefix(data.getUrnPrefix());
            key.setSecretReservationKey(data.getSecretReservationKey());
            rsList.add(key);
        }
        WSNPacketReader reader = new WSNPacketReader(sm, rsList);
        logger.info("Start Controller Endpoint at " + CONTROLLER_URN);
        endpointMap.put(rsList, Endpoint.publish(CONTROLLER_URN, reader));
        try {
            sm.getInstance(rsList, CONTROLLER_URN);
        } catch (ExperimentNotRunningException_Exception e) {
            logger.error("Experiment not running.", e);
        } catch (UnknownReservationIdException_Exception e) {
            logger.error("Unknown Reservation.", e);
        }
        return reader;
    }

    @Override
    public void receive(@WebParam(name = "msg", targetNamespace = "") Message msg) {
        if (msg.getBinaryMessage() != null && msg.getBinaryMessage().getBinaryType() == 111/*145*/) {
            try {
                logger.debug("New Message received.");
                queue.add(factory.createInstance(msg.getBinaryMessage().getBinaryData()));
            } catch (SpyglassPacketException e) {
                logger.error("Error creating Spyglass-Packet.", e);
            }
        }
    }

    @Override
    public void receiveStatus(@WebParam(name = "status", targetNamespace = "") RequestStatus status) {
    }

    @Override
    public SpyglassPacket getNextPacket(boolean block) throws SpyglassPacketException, InterruptedException {
        return queue.poll();
    }

    @Override
    public void reset() throws IOException {
        queue.clear();
    }

    @Override
    public void shutdown() throws IOException {
        try {
            smService.free(secretReservation);
        } catch (ExperimentNotRunningException_Exception e) {
            logger.error("Experiment not running.", e);
        } catch (UnknownReservationIdException_Exception e) {
            logger.error("Unknown Reservation.", e);
        }
        endpointMap.get(secretReservation).stop();
        endpointMap.remove(secretReservation);
    }
}
