package de.uniluebeck.itm.spyglass.io.wisebed;

import com.google.common.collect.Lists;
import de.uniluebeck.itm.spyglass.core.Spyglass;
import de.uniluebeck.itm.spyglass.io.SpyglassPacketRecorder;
import de.uniluebeck.itm.spyglass.packet.SpyglassPacketException;
import de.uniluebeck.itm.spyglass.util.StringTuple;
import de.uniluebeck.itm.tr.util.StringUtils;
import eu.wisebed.api.common.Message;
import eu.wisebed.api.controller.Controller;
import eu.wisebed.api.controller.RequestStatus;
import eu.wisebed.api.sm.SecretReservationKey;
import eu.wisebed.api.sm.SessionManagement;
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
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * reads packets from wsn-service
 *
 * @author Jens Kluttig, Daniel Bimschas
 */
public class WisebedPacketReader extends SpyglassPacketRecorder {

	private static final Logger log = LoggerFactory.getLogger(WisebedPacketReader.class);

	@WebService(name = "ProxyController", targetNamespace = "urn:ControllerService")
	public class WisebedPacketReaderController implements Controller {

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

						add(factory.createInstance(extractBinaryPayload(msg)));

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
		public void receiveNotification(
				@WebParam(name = "msg", targetNamespace = "") final List<String> notifications) {
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

	private WisebedPacketReaderController controller = new WisebedPacketReaderController();

	private Endpoint controllerEndpoint;

	@Element(required = true)
	private String sessionManagementEndpointUrl;

	@Element(required = true)
	private String controllerEndpointUrl;

	@ElementList(type = StringTuple.class, required = true)
	private List<StringTuple> secretReservationKeys = Lists.newArrayList();

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
	}

	@Override
	public void shutdown() throws IOException {
		log.debug("Shutting down WisebedPacketReader...");
		if (controllerEndpoint.isPublished()) {
			controllerEndpoint.stop();
		}
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
					secretReservationKey.getSecretReservationKey()
			)
			);
		}
	}

	private List<SecretReservationKey> copyXMLRepresentationToSRKs() {
		List<SecretReservationKey> reservation = Lists.newArrayList();
		for (StringTuple tuple : this.secretReservationKeys) {
			SecretReservationKey key = new SecretReservationKey();
			key.setUrnPrefix(tuple.first);
			key.setSecretReservationKey(tuple.second);
			reservation.add(key);
		}
		return reservation;
	}
}
