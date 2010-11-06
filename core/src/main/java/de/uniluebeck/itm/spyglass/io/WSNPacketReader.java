package de.uniluebeck.itm.spyglass.io;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.tr.util.StringUtils;
import eu.wisebed.testbed.api.rs.v1.ConfidentialReservationData;
import eu.wisebed.testbed.api.rs.v1.Data;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import eu.wisebed.testbed.api.wsn.v211.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * reads packets from wsn-service
 *
 * @author jens kluttig
 */
@WebService(name = "ProxyController", targetNamespace = "urn:ControllerService")
public class WSNPacketReader extends AbstractPacketReader implements Controller {

	private static final Logger log = LoggerFactory.getLogger(WSNPacketReader.class);

	//public final static String CONTROLLER_URN = "http://localhost:8081/spyglass/controller";
	//public final static String CONTROLLER_URN = "http://"141.83.68.131":8081/spyglass/controller";

	public static String CONTROLLER_URN;

	private SessionManagement smService;

	private List<SecretReservationKey> secretReservation;

	private BlockingDeque<SpyglassPacket> queue = new LinkedBlockingDeque<SpyglassPacket>();

	private static Map<List<SecretReservationKey>, Endpoint> endpointMap = Maps.newHashMap();

	private WSNPacketReader(SessionManagement sessionManagementUrn, List<SecretReservationKey> rsList) {
		try {
			CONTROLLER_URN = "http://" + InetAddress.getLocalHost().getHostAddress() + ":8081/spyglass/controller";
		} catch (UnknownHostException e) {
			e.printStackTrace();
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
		
		log.debug("Started Controller endpoint at {}", CONTROLLER_URN);
		endpointMap.put(rsList, Endpoint.publish(CONTROLLER_URN, reader));

		try {
			sm.getInstance(rsList, CONTROLLER_URN);
		} catch (ExperimentNotRunningException_Exception e) {
			log.warn("" + e, e);
			return null;
		} catch (UnknownReservationIdException_Exception e) {
			log.warn("" + e, e);
			return null;
		}
		return reader;
	}

	@Override
	public void receive(@WebParam(name = "msg", targetNamespace = "") Message msg) {

		if (msg.getBinaryMessage() != null && msg.getBinaryMessage().getBinaryType() == 111) {

			try {

				if (log.isDebugEnabled()) {
					log.debug("Received SpyGlass packet. Type: {}, Data: {}",
							StringUtils.toHexString(msg.getBinaryMessage().getBinaryType()),
							StringUtils.toHexString(msg.getBinaryMessage().getBinaryData())
					);
				}

				queue.add(factory.createInstance(msg.getBinaryMessage().getBinaryData()));

			} catch (SpyglassPacketException e) {
				log.warn("Exception while parsing SpyGlass packet. Ignoring packet. Type: {}, Data: {}",
						StringUtils.toHexString(msg.getBinaryMessage().getBinaryType()),
						StringUtils.toHexString(msg.getBinaryMessage().getBinaryData())
				);
			}
		} else if (log.isDebugEnabled()) {

			if (msg.getBinaryMessage() != null) {
				log.debug("Received non-SpyGlass binary packet. Type: {}, Data: {}",
						StringUtils.toHexString(msg.getBinaryMessage().getBinaryType()),
						StringUtils.toHexString(msg.getBinaryMessage().getBinaryData())
				);
			} else if (msg.getTextMessage() != null) {
				log.debug("Received non-SpyGlass text packet. Level: {}, Msg: {}",
						msg.getTextMessage().getMessageLevel(),
						msg.getTextMessage().getMsg()
				);
			}

		}
	}

	@Override
	public void receiveStatus(@WebParam(name = "status", targetNamespace = "") RequestStatus status) {
		// nothing to do
	}

	@Override
	public SpyglassPacket getNextPacket(boolean block) throws SpyglassPacketException, InterruptedException {
		return block ? queue.take() : queue.poll();
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
			log.debug("Silently ignoring ExperimentNotRunningException while shutting down. Message: {}", e.getMessage());
		} catch (UnknownReservationIdException_Exception e) {
			log.debug("Silently ignoring UnknownReservationIdException while shutting down. Message: {}", e.getMessage());
		}
		endpointMap.get(secretReservation).stop();
		endpointMap.remove(secretReservation);
	}
}
