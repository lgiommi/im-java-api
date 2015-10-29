package es.upv.i3m.grycap.client;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import es.upv.i3m.grycap.file.FileIO;
import es.upv.i3m.grycap.im.api.InfrastructureManagerApiClient;
import es.upv.i3m.grycap.im.client.ServiceResponse;
import es.upv.i3m.grycap.im.exceptions.AuthFileNotFoundException;
import es.upv.i3m.grycap.logger.ImJavaApiLogger;

public class InfrastructureManagerApiClientTest {

	// Client needed to connect to the java api
	private static InfrastructureManagerApiClient imApiClient;
	// IM connection urls
	private static final String IM_DUMMY_PROVIDER_URL = "http://servproject.i3m.upv.es:8811";
	private static final String IM_URL = "http://servproject.i3m.upv.es:8800";
	// Authorization file path
	private static final String AUTH_FILE_PATH = "./src/test/resources/auth.dat";
	// IM RADLs
	private static final String RADL_ALTER_VM_FILE_PATH = "./src/test/resources/radls/alter-vm.radl";
	// IM TOSCA files
	private static final String TOSCA_FILE_PATH = "./src/test/resources/tosca/galaxy_tosca.yaml";
	// VM identifiers
	private static final String VM_DEFAULT_ID = "0";
	private static final String VM_ID_ONE = "1";
	// Possible VM states
	private static final String VM_STATE_RUNNING = "running";
	private static final String VM_STATE_PENDING = "pending";
	private static final String VM_STATE_STOPPED = "stopped";
	private static final String VM_STATE_UNCONFIGURED = "unconfigured";
	// VM properties
	private static final String VM_STATE_PROPERTY = "state";
	private static final String VM_CPU_COUNT_PROPERTY = "cpu.count";
	private static final Integer MAX_RETRY = 20;
	// Flag to activate/deactivate the dummy provider
	private static final boolean DUMMY_PROVIDER = true;
	// ID of the infrastructure created
	private String infrastructureId;

	private String getInfrastructureId() {
		return infrastructureId;
	}

	private void setInfrastructureId(String infrastructureId) {
		this.infrastructureId = infrastructureId;
	}
	
	private InfrastructureManagerApiClient getImApiClient() {
		return imApiClient;
	}

	private void waitUntilRunningOrUncofiguredState(String vmId) throws AuthFileNotFoundException {
		while (true) {
			String vmState = getImApiClient().getVMProperty(getInfrastructureId(), vmId, VM_STATE_PROPERTY).getResult();
			if (vmState.equals(VM_STATE_RUNNING) || vmState.equals(VM_STATE_UNCONFIGURED)) {
				break;
			}
			sleep(5000);
		}
	}

	/**
	 * Sleep the number of milliseconds specified unless the provider tested is
	 * the dummy one.<br>
	 * The dummy provider doesn't have wait times.
	 * 
	 * @param milliseconds
	 */
	private void sleep(long milliseconds) {
		if (!DUMMY_PROVIDER)
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				ImJavaApiLogger.severe(this.getClass(), e);
			}
	}

	/**
	 * Fail the test if the string is null or empty
	 * 
	 * @param s
	 */
	private void checkStringHasContent(String s) {
		if (s == null || s.isEmpty()) {
			Assert.fail();
		}
	}

	/**
	 * Fail the test if the response is not successful
	 * 
	 * @param response
	 */
	private void checkServiceResponse(ServiceResponse response) {
		if (!response.isReponseSuccessful()) {
			Assert.fail();
		}
	}

	/**
	 * Check if the VM state is the same as the 'state' parameter
	 * 
	 * @param vmId
	 * @param state
	 * @throws AuthFileNotFoundException
	 */
	private void checkVMState(String vmId, String state) throws AuthFileNotFoundException {
		ServiceResponse response = getImApiClient().getVMProperty(getInfrastructureId(), VM_DEFAULT_ID,
				VM_STATE_PROPERTY);
		checkServiceResponse(response);
		if (!response.getResult().equals(state)) {
			Assert.fail();
		}
	}

	@BeforeClass
	public static void setRestClient() {
		try {
			if (DUMMY_PROVIDER) {
				imApiClient = new InfrastructureManagerApiClient(IM_DUMMY_PROVIDER_URL, AUTH_FILE_PATH);
			} else {
				imApiClient = new InfrastructureManagerApiClient(IM_URL, AUTH_FILE_PATH);
			}
		} catch (AuthFileNotFoundException e) {
			ImJavaApiLogger.severe(InfrastructureManagerApiClientTest.class, e.getMessage());
			Assert.fail();
		}
	}

	@Before
	public void createInfrastructure() throws IOException, AuthFileNotFoundException {
		ServiceResponse response = getImApiClient().createInfrastructure(FileIO.readUTF8File(TOSCA_FILE_PATH));
		checkServiceResponse(response);
		String[] parsedURI = response.getResult().split("/");
		// Get the last element which is the infId
		setInfrastructureId(parsedURI[parsedURI.length - 1]);
	}

	@After
	public void destroyInfrastructure() throws AuthFileNotFoundException, IOException {
		checkServiceResponse(getImApiClient().destroyInfrastructure(getInfrastructureId()));
	}

	@Test
	public void testCreateAndDestroyInfrastructure() {
		// Functionality tested before and after each test
	}

	@Test
	public void testInfrasturesList() throws AuthFileNotFoundException {
		checkServiceResponse(getImApiClient().getInfrastructureList());
	}

	@Test
	public void testInfrastructureInfo() throws AuthFileNotFoundException, IOException {
		String test = null;
		if (DUMMY_PROVIDER) {
			test = IM_DUMMY_PROVIDER_URL + "/infrastructures/" + getInfrastructureId() + "/vms/0";
		} else {
			test = IM_URL + "/infrastructures/" + getInfrastructureId() + "/vms/0";
		}
		ServiceResponse response = getImApiClient().getInfrastructureInfo(getInfrastructureId());
		checkServiceResponse(response);
		if (!response.getResult().equals(test)) {
			Assert.fail();
		}
	}

	@Test
	public void testGetVMInfo() throws AuthFileNotFoundException, IOException {
		ServiceResponse response = getImApiClient().getVMInfo(getInfrastructureId(), VM_DEFAULT_ID);
		checkServiceResponse(response);
		checkStringHasContent(response.getResult());
	}

	@Test
	public void testGetVMProperty() throws AuthFileNotFoundException, IOException {
		if (DUMMY_PROVIDER) {
			checkVMState(VM_DEFAULT_ID, VM_STATE_RUNNING);
		} else {
			checkVMState(VM_DEFAULT_ID, VM_STATE_PENDING);
		}
	}

	@Test
	public void testGetInfrastructureContMsg() throws AuthFileNotFoundException, IOException {
		ServiceResponse response = getImApiClient().getInfrastructureContMsg(getInfrastructureId());
		checkServiceResponse(response);
		if (!DUMMY_PROVIDER) {
			checkStringHasContent(response.getResult());
		}
	}

	@Test
	public void testGetInfrastructureRADL() throws AuthFileNotFoundException, IOException {
		ServiceResponse response = getImApiClient().getInfrastructureRADL(getInfrastructureId());
		checkServiceResponse(response);
		checkStringHasContent(response.getResult());
	}

	@Test
	public void testAddResourceNoContext() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		// Wait for the machine to be properly configured
		ServiceResponse response = getImApiClient().addResource(getInfrastructureId(),
				FileIO.readUTF8File(TOSCA_FILE_PATH));
		int retry;
		for (retry = 0; retry < MAX_RETRY; retry++) {
			sleep(5000);
			response = getImApiClient().addResource(getInfrastructureId(), FileIO.readUTF8File(TOSCA_FILE_PATH));
			if (response.isReponseSuccessful()) {
				waitUntilRunningOrUncofiguredState(VM_ID_ONE);
				break;
			}
		}
		if (retry == MAX_RETRY) {
			Assert.fail();
		}
	}

	@Test
	public void testAddResourceContextTrue() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		// Wait for the machine to be properly configured
		ServiceResponse response = getImApiClient().addResource(getInfrastructureId(),
				FileIO.readUTF8File(TOSCA_FILE_PATH), true);
		int retry;
		for (retry = 0; retry < MAX_RETRY; retry++) {
			sleep(5000);
			response = getImApiClient().addResource(getInfrastructureId(), FileIO.readUTF8File(TOSCA_FILE_PATH), true);
			if (response.isReponseSuccessful()) {
				waitUntilRunningOrUncofiguredState(VM_ID_ONE);
				break;
			}
		}
		if (retry == MAX_RETRY) {
			Assert.fail();
		}
	}

	@Test
	public void testAddResourceContextFalse() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		// Wait for the machine to be properly configured
		ServiceResponse response = getImApiClient().addResource(getInfrastructureId(),
				FileIO.readUTF8File(TOSCA_FILE_PATH), false);
		int retry;
		for (retry = 0; retry < MAX_RETRY; retry++) {
			sleep(5000);
			response = getImApiClient().addResource(getInfrastructureId(), FileIO.readUTF8File(TOSCA_FILE_PATH), false);
			if (response.isReponseSuccessful()) {
				waitUntilRunningOrUncofiguredState(VM_ID_ONE);
				break;
			}
		}
		if (retry == MAX_RETRY) {
			Assert.fail();
		}
	}

	@Test
	public void testRemoveResourceNoContext() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().removeResource(getInfrastructureId(), VM_DEFAULT_ID);
		checkServiceResponse(response);
	}

	@Test
	public void testRemoveResourceContextTrue() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().removeResource(getInfrastructureId(), VM_DEFAULT_ID, true);
		checkServiceResponse(response);
	}

	@Test
	public void testRemoveResourceContextFalse() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().removeResource(getInfrastructureId(), VM_DEFAULT_ID, false);
		checkServiceResponse(response);
	}

	@Test
	public void testStopInfrastructure() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().stopInfrastructure(getInfrastructureId());
		checkServiceResponse(response);
		checkVMState(VM_DEFAULT_ID, VM_STATE_STOPPED);
	}

	@Test
	public void testStartInfrastructure() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().stopInfrastructure(getInfrastructureId());
		checkServiceResponse(response);
		checkVMState(VM_DEFAULT_ID, VM_STATE_STOPPED);
		sleep(3000);
		response = getImApiClient().startInfrastructure(getInfrastructureId());
		checkServiceResponse(response);
		if (DUMMY_PROVIDER) {
			checkVMState(VM_DEFAULT_ID, VM_STATE_RUNNING);
		} else {
			checkVMState(VM_DEFAULT_ID, VM_STATE_PENDING);
		}
	}

	@Test
	public void testStopVM() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().stopVM(getInfrastructureId(), VM_DEFAULT_ID);
		checkServiceResponse(response);
		checkVMState(VM_DEFAULT_ID, VM_STATE_STOPPED);
	}

	@Test
	public void testStartVM() throws AuthFileNotFoundException, IOException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().stopVM(getInfrastructureId(), VM_DEFAULT_ID);
		checkServiceResponse(response);
		checkVMState(VM_DEFAULT_ID, VM_STATE_STOPPED);
		sleep(3000);
		response = getImApiClient().startVM(getInfrastructureId(), VM_DEFAULT_ID);
		checkServiceResponse(response);
		if (DUMMY_PROVIDER) {
			checkVMState(VM_DEFAULT_ID, VM_STATE_RUNNING);
		} else {
			checkVMState(VM_DEFAULT_ID, VM_STATE_PENDING);
		}
	}

	@Test
	public void testAlterVM() throws AuthFileNotFoundException, IOException, InterruptedException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		// Wait for the machine to be properly configured
		sleep(45000);
		getImApiClient().alterVM(getInfrastructureId(), VM_DEFAULT_ID, FileIO.readUTF8File(RADL_ALTER_VM_FILE_PATH));
		ServiceResponse response = getImApiClient().getVMProperty(getInfrastructureId(), VM_DEFAULT_ID,
				VM_CPU_COUNT_PROPERTY);
		checkServiceResponse(response);
		// Check that the alteration of the VM has been successful
		String cpuCount = response.getResult();
		if (cpuCount == null || cpuCount.isEmpty() || !cpuCount.equals("2")) {
			Assert.fail();
		}
	}

	@Test
	public void testReconfigure() throws AuthFileNotFoundException, IOException, InterruptedException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().reconfigure(getInfrastructureId());
		checkServiceResponse(response);
	}

	@Test
	public void testReconfigureAllVms() throws AuthFileNotFoundException, IOException, InterruptedException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().addResource(getInfrastructureId(),
				FileIO.readUTF8File(TOSCA_FILE_PATH));
		checkServiceResponse(response);
		waitUntilRunningOrUncofiguredState(VM_ID_ONE);
		response = getImApiClient().reconfigure(getInfrastructureId(), FileIO.readUTF8File(TOSCA_FILE_PATH));
		checkServiceResponse(response);
	}

	@Test
	public void testReconfigureSomeVms() throws AuthFileNotFoundException, IOException, InterruptedException {
		waitUntilRunningOrUncofiguredState(VM_DEFAULT_ID);
		ServiceResponse response = getImApiClient().addResource(getInfrastructureId(),
				FileIO.readUTF8File(TOSCA_FILE_PATH));
		checkServiceResponse(response);
		waitUntilRunningOrUncofiguredState(VM_ID_ONE);
		response = getImApiClient().reconfigure(getInfrastructureId(), FileIO.readUTF8File(TOSCA_FILE_PATH),
				new int[] { 0 });
		checkServiceResponse(response);
	}
}