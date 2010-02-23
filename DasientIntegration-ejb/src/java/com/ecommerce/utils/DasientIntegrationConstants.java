package com.ecommerce.utils;

/**
 * @author djdaugherty
 */
public class DasientIntegrationConstants {

    // Queue name definitions
    public static final String DLQ_QUEUE = "/queue/DLQ";
    public static final String EXPIRY_QUEUE = "/queue/ExpiryQueue";
    public static final String SCANDOMAIN_QUEUE = "/queue/scanDomainQueue";
    public static final String SCANDOMAINAUGMENTED_QUEUE = "/queue/scanDomainAugmentedQueue";
    public static final String DASIENTACK_QUEUE = "/queue/dasientAckQueue";
    public static final String DASIENTACKEXPIRY_QUEUE = "/queue/dasientAckExpiryQueue";
    public static final String DASIENTRESULT_QUEUE = "/queue/dasientResultQueue";
    public static final String PROCESSSCANRESULT_QUEUE = "/queue/processScanResultQueue";
    public static final String DOMAINCLEANED_QUEUE = "/queue/domainCleanedQueue";
    public static final String INITDOMAIN_QUEUE = "/queue/addDomainQueue";

    // Processer cron schedule
	// every 60 seconds between the hours of 4AM and Midnight
    public static final String PROCESSOR_CRON_SCHEDULE = "0/60 * 4-23 * * ?";
	// firest status report every hour, on the hour
    public static final String STATUS_REPORT_CRON_SCHEDULE = "0 0/60 4-23 * * ?";
    public static final String HSPHERE_SYNC_CRON_SCHEDULE = "0 * * * * ?";
    public static final String ARCHIVER_CRON_SCHEDULE = "0/60 * * * * ?";

	// list of constants identifying the keyValues used within the MapMessages
	// put to the queue.
	public static final String MESSAGE_TYPE = "messageType";
	public static final String DOMAIN_NAME = "domainName";
	public static final String DOMAIN_ID = "domainId";
	public static final String CONTROL_PANEL = "controlPanel";
	public static final String START_TIME = "startTime";
	public static final String REQUEST_TYPE = "requestType";
	public static final String WEBSERVER = "webserver";
	public static final String HOSTIP = "hostIp";

	// list of messageType constants
	public static final String ADD_DOMAIN_MESSAGETYPE = "AddDomain";
	public static final String SCAN_DOMAIN_MESSAGETYPE = "ScanDomain";
	public static final String SCAN_DOMAIN_AUGMENTED_MESSAGETYPE = "ScanDomainAugmented";
	public static final String CLEAN_DOMAIN_MESSAGETYPE = "CleanDomain";
	public static final String DOMAIN_CLEANED_MESSAGETYPE = "DomainCleaned";
	public static final String PROCESS_SCAN_REQUEST_MESSAGETYPE = "ProcessScanRequest";
	public static final String PROCESS_SCAN_RESULT_MESSAGETYPE = "ProcessScanResult";
	public static final String DASIENT_ACK_MESSAGETYPE = "DasientAck";
	public static final String DASIENT_RESULT_MESSAGETYPE = "DasientResult";

}