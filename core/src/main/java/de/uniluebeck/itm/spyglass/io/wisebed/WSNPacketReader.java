package de.uniluebeck.itm.spyglass.io.wisebed;

import com.google.common.collect.Lists;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.io.AbstractPacketReader;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacket;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.StringTuple;
import de.uniluebeck.itm.tr.util.StringUtils;
import de.uniluebeck.itm.wisebed.cmdlineclient.BeanShellHelper;
import eu.wisebed.api.common.Message;
import eu.wisebed.api.controller.Controller;
import eu.wisebed.api.controller.RequestStatus;
import eu.wisebed.api.rs.ConfidentialReservationData;
import eu.wisebed.api.rs.Data;
import eu.wisebed.api.rs.SecretReservationKey;
import eu.wisebed.api.sm.ExperimentNotRunningException_Exception;
import eu.wisebed.api.sm.SessionManagement;
import eu.wisebed.api.sm.UnknownReservationIdException_Exception;
import eu.wisebed.testbed.api.wsn.WSNServiceHelper;
import org.eclipse.jface.dialogs.MessageDialog;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
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

	private static Endpoint controllerEndpoint;

	private BlockingDeque<SpyglassPacket> queue = new LinkedBlockingDeque<SpyglassPacket>();

	@Element(required = true)
	private String sessionManagementUrn;

	@Element(required = true)
	private String controllerUrn;

	@ElementList(type = StringTuple.class, required = true)
	private List<StringTuple> reservationData = Lists.newArrayList();

	public WSNPacketReader() {
		setSourceType(SOURCE_TYPE.OTHER);
	}

	public static WSNPacketReader createInstance(String sessionManagementUrn, String controllerUrn,
												 ConfidentialReservationData reservation) {

		List<SecretReservationKey> rsList = Lists.newArrayList();
		for (Data data : reservation.getData()) {
			SecretReservationKey key = new SecretReservationKey();
			key.setUrnPrefix(data.getUrnPrefix());
			key.setSecretReservationKey(data.getSecretReservationKey());
			rsList.add(key);
		}

		WSNPacketReader reader = new WSNPacketReader();
		reader.controllerUrn = controllerUrn;

		if (startService(sessionManagementUrn, rsList, reader)) {
			log.error("WSNPacketReader could not start!");
			return null;
		}

		reader.sessionManagementUrn = sessionManagementUrn;
		for (SecretReservationKey key : rsList) {
			reader.reservationData.add(new StringTuple(key.getSecretReservationKey(), key.getUrnPrefix()));
		}

		return reader;
	}

	private static boolean startService(String sessionManagementUrn, List<SecretReservationKey> rsList,
										WSNPacketReader reader) {
		SessionManagement sm = WSNServiceHelper.getSessionManagementService(sessionManagementUrn);
		if (controllerEndpoint != null && controllerEndpoint.isPublished()) {
			controllerEndpoint.stop();
		}
		controllerEndpoint = Endpoint.publish(reader.controllerUrn, reader);
		log.debug("Started Controller endpoint at {}", reader.controllerUrn);
		try {
			sm.getInstance(BeanShellHelper.copyRsToWsn(rsList), reader.controllerUrn);
		} catch (ExperimentNotRunningException_Exception e) {
			log.error("Experiment not found!");
			controllerEndpoint.stop();
			return true;
		} catch (UnknownReservationIdException_Exception e) {
			log.error("Reservation is unknown!");
			controllerEndpoint.stop();
			return true;
		} catch (Exception e) {
			// original Exception ClientTransportException results in maven errors
			log.error("SessionManagement-Service on " + sessionManagementUrn + " offline!");
			controllerEndpoint.stop();
			return true;
		}
		return false;
	}

	public static String generateControllerURN() {
		String controllerUrn;
		try {
			controllerUrn = "http://" + InetAddress.getLocalHost().getHostAddress() + ":8081/spyglass/controller";
		} catch (UnknownHostException e) {
			log.error("Unknown Host", e);
			return null;
		}
		return controllerUrn;
	}

	@Override
	protected void initConfig(final Spyglass app) {
		super.initConfig(app);
		log.debug("Initialized for SessionManagement-Service at " + sessionManagementUrn);
		if (startService(sessionManagementUrn, buildSecretReservationKey(), this)) {
			log.error("WSNPacketReader could not start!");
		}
	}

	private List<SecretReservationKey> buildSecretReservationKey() {
		List<SecretReservationKey> reservation = Lists.newArrayList();
		for (StringTuple tuple : this.reservationData) {
			SecretReservationKey key = new SecretReservationKey();
			key.setSecretReservationKey(tuple.first);
			key.setUrnPrefix(tuple.second);
			reservation.add(key);
		}
		return reservation;
	}

	@Override
	public SpyglassPacket getNextPacket(boolean block) throws SpyglassPacketException, InterruptedException {
		return block ? queue.take() : queue.poll();
	}

	@Override
	public void reset() throws IOException {
		log.debug("resetting");
		queue.clear();
	}

	@Override
	public void shutdown() throws IOException {
		log.debug("shutting down");
		if (controllerEndpoint.isPublished()) {
			controllerEndpoint.stop();
		}
	}

	@Override
	public void experimentEnded() {
		MessageDialog.openInformation(
				null,
				"Testbed experiment ended",
				"The experiment on the testbed ended and all data was received."
		);
	}

	@Override
	public void receive(@WebParam(name = "msg", targetNamespace = "") final List<Message> messages) {

		for (Message msg : messages) {

			boolean isValidSpyglassPacket = msg.getBinaryData() != null &&
					msg.getBinaryData().length > 0 &&
					msg.getBinaryData()[0] == 111;

			if (isValidSpyglassPacket) {

				try {

					if (log.isDebugEnabled()) {
						log.debug("Received SpyGlass packet from \"{}\" @ {}: {}",
								new Object[]{
										msg.getSourceNodeId(),
										msg.getTimestamp(),
										StringUtils.toPrintableString(msg.getBinaryData())
								}
						);
					}

					queue.add(factory.createInstance(extractBinaryPayload(msg)));

				} catch (SpyglassPacketException e) {
					log.warn("Exception while parsing SpyGlass packet. Ignoring packet from \"\" @ {}: {}",
							new Object[]{
									msg.getSourceNodeId(),
									msg.getTimestamp(),
									StringUtils.toPrintableString(msg.getBinaryData())
							}
					);
				}

			} else if (log.isDebugEnabled()) {
				log.debug("Received non-SpyGlass binary packet from \"{}\" @ {}: {}",
						new Object[]{
								msg.getSourceNodeId(),
								msg.getTimestamp(),
								StringUtils.toPrintableString(msg.getBinaryData())
						}
				);
			}
		}
	}

	@Override
	public void receiveNotification(@WebParam(name = "msg", targetNamespace = "") final List<String> notifications) {
		for (String notification : notifications) {
			MessageDialog.openInformation(
					null,
					"Testbed Notification",
					notification
			);
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
