package com.ecommerce.dasient.broker;

import com.ecommerce.dasient.exceptions.CleanWorkUnitNotFoundException;
import com.ecommerce.dasient.exceptions.PersistBackupException;
import com.ecommerce.dasient.exceptions.WebServerAuthenticationException;
import com.ecommerce.dasient.exceptions.WebServerMismatchException;
import com.ecommerce.dasient.exceptions.WebServerNotFoundException;
import com.ecommerce.dasient.model.CleanHistory;
import com.ecommerce.dasient.model.Revision;
import com.ecommerce.dasient.model.RuleRevision;
import com.ecommerce.dasient.model.WebServer;
import com.ecommerce.dasient.vo.CleanWorkRequest;
import com.ecommerce.dasient.vo.CleanWorkResponse;
import com.ecommerce.sbs.BackupUploader;
import com.ecommerce.sbs.CleanBrokerLocal;
import com.ecommerce.sbs.RuleSetLocal;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ejb.EJB;
import javax.ejb.NoSuchEJBException;
import javax.naming.InitialContext;
import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.security.auth.x500.X500Principal;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFile;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.core.util.FileUtil;
import org.python.util.PythonInterpreter;

/**
 * A HTTP service for the clean script to get work from.
 *
 * This servlet has its context root at /dasient/broker.
 * It accepts HTTP GET requests at /dasient/broker/getWork and
 * HTTP multipart POST requests at /dasient/broker/submitResult.
 *
 * This servlet is responsible for:
 *   - providing a service for the clean script agents to call in and get work
 *   - authenticating the service clients
 *   - marshaling the communication into a format the clients understand (Python pickling)
 *
 * Everything else is the responsibility of the backing EJB CleanBrokerBean.
 */
public class CleanBrokerServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CleanBrokerServlet.class);

    /**
     * Path which the "request for work" call is served at.
     */
    private static final String GET_WORK_PATH = "/getWork";

    /**
     * Path which the "submit work result" call is served at.
     */
    private static final String SUBMIT_RESULT_PATH = "/submitResult";

    /**
     * A reference to the persistence context.
     */
    @PersistenceContext(unitName = "DasientPU")
    private EntityManager dasientEM;

    /**
     * Backing bean for the clean broker.
     */
    @EJB(mappedName = "DasientIntegration/CleanBrokerBean/local")
    private CleanBrokerLocal cleanBrokerBean;

    /**
     * A python interpreter for (un)pickling.
     *
     * The interpreter is not thread-safe, its use needs to be guarded by "synchronized (python) { }".
     *
     * The following system properties should be set for this to work:
     *   python.home: path to python.jar
     *   python.cachedir: a writable directory for the interpreters package cache
     */

    protected final PythonInterpreter python;

    public CleanBrokerServlet() {
        Properties pOverride = new Properties();

        pOverride.put("python.cachedir.skip", "true");

        PySystemState.initialize(PySystemState.getBaseProperties(), pOverride);

        python = new PythonInterpreter();
    }

    /**
     * Initializes the python interpreter used for (un)pickling.
     *
     * @throws ServletException if the initialization of the servlet or the python interpreter fails
     */
    @Override
    public void init() throws ServletException {
        super.init();

        try {
            // No synchronized() necessary, the servlet spec guarantees
            // that there are no other threads serving requests yet
            python.execfile(getPythonResource("pickle.py"), "pickle.py");
        } catch (IOException exc) {
            throw new ServletException(exc);
        }
    }

    /**
     * Cleans up the python interpreter used for (un)pickling.
     *
     * The interpreter cleanup procedure will calls pythons atexit routines.
     */
    @Override
    public void destroy() {
        try {
            // It is possible that service() calls are still running in other threads,
            // so synchronize() is necessary
            synchronized (python) {
                python.cleanup();
            }
        } catch (RuntimeException exc) {
            // Servlet.destroy should not throw exceptions, just log them
            logger.warn("Failed to cleanly shut down python interpreter", exc);
        }

        super.destroy();
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String method = request.getMethod();
        String pathInfo = request.getPathInfo();

        // accept HTTP GET requests at /getWork
        if (GET_WORK_PATH.equals(pathInfo)) {
            if ("GET".equals(method)) {
                processGetWork(request, response);
            } else {
                response.setHeader("Allow", "GET");
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                        request.getRequestURI());
            }
        } // accept HTTP POST requests at /submitResult
        else if (SUBMIT_RESULT_PATH.equals(pathInfo)) {
            if ("POST".equals(method)) {
                processSubmitResult(request, response);
            } else {
                response.setHeader("Allow", "POST");
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                        request.getRequestURI());
            }
        } // anything else is "404 Not Found"
        else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    request.getRequestURI());
        }

    }

    /**
     * Returns the public key of the CA certificate that all web servers have to authenticate with.
     *
     * The certificate is read from a keystore, path to the keystore
     * is configurable.
     *
     * If the keystoreFile is a relative path, it is resolved with
     * jboss.server.home.dir as base.
     *
     * @throws IOException if the keystore file can not be read
     * @throws CertificateException if the certificate can not be parsed
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore cannot be found
     * @throws KeyStoreException if the keystore can not be parsed
     * @throws CertificateException if any of the certificates in the keystore could not be loaded
     * @throws WebServerAuthenticationException if the CA certificate can not be found in the keystore
     * @return the public key of the CA certificate
     */
    private PublicKey getAuthorityPublicKey() throws NoSuchAlgorithmException, KeyStoreException,
            CertificateException, IOException, WebServerAuthenticationException {

        String keystoreFilename = System.getProperty("dasient.ca_keystore_file", "conf/server.truststore");
        String alias = System.getProperty("dasient.ca_alias", "webca");

        File keystoreFile = new File(keystoreFilename);
        if (!keystoreFile.isAbsolute()) {
            keystoreFile = new File(System.getProperty("jboss.server.home.dir"), keystoreFilename);
        }

        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());

        FileInputStream fin = new FileInputStream(keystoreFile);
        try {
            store.load(fin, null);
        } finally {
            fin.close();
        }

        Certificate cert = store.getCertificate(alias);
        if (cert == null) {
            throw new WebServerAuthenticationException(String.format("No key store alias named '%s' found", alias));
        }

        return cert.getPublicKey();
    }

    /**
     * Returns the subject of the configured master certificate.
     *
     * For testing purposes a configured master certificate is allowed,
     * which can be used to authenticate as any web server.
     *
     * If using the master certificate to authenticate, the web server name
     * must be provided as separate parameter named "webserver".
     *
     * @return the master certificate subject
     */
    private LdapName getMasterCertificateSubject() {
        LdapName masterCertSubject = null;

        String rawMasterCertSubject = System.getProperty("dasient.master_cert");

        if (rawMasterCertSubject != null && rawMasterCertSubject.trim().length() > 0) {
            try {
                masterCertSubject = new LdapName(rawMasterCertSubject);
            } catch (InvalidNameException exc) {
                logger.warn(String.format(
                        "Invalid configuration for master certificate: %s",
                        rawMasterCertSubject), exc);
            }
        }

        if (masterCertSubject != null) {
            logger.debug(String.format("Configured master certificate is %s", masterCertSubject.toString()));
        }

        return masterCertSubject;
    }

    /**
     * Returns the subject of the given certificate.
     *
     * LdapName is used to parse the certificate subject because they are in the same format, RFC 2253.
     * The last element of the RDN should be "cn=webXXX.opentransfer.com", which is used to extract the
     * clients web server name.
     *
     * @param request servlet request
     * @return the certificate subject
     */
    private LdapName getCertificateSubject(X509Certificate certificate)
            throws WebServerAuthenticationException {

        LdapName certSubject;

        String rawCertSubject = certificate.getSubjectX500Principal().getName(X500Principal.RFC2253);

        try {
            certSubject = new LdapName(rawCertSubject);
        } catch (InvalidNameException exc) {
            throw new WebServerAuthenticationException(exc);
        }

        logger.debug(String.format("Client certificate subject is %s", certSubject.toString()));

        return certSubject;
    }

    /**
     * Extracts the client certificate from the given HttpServletRequest.
     *
     * If the client sends multiple certificates, only the first one is
     * paid attention to.
     *
     * The certificate is verified for validity it must not be expired
     * and its issuer must be trusted.
     *
     * It is also verified that the certificate is signed by the authority
     * for web servers client certificates.
     *
     * @param request servlet request
     * @throws WebServerAuthenticationException if the clients identity can not be verified
     * @return the clients certificate
     */
    private X509Certificate getClientCertificate(HttpServletRequest request)
            throws WebServerAuthenticationException {

        X509Certificate[] certChain = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        if (certChain == null || certChain.length == 0) {
            throw new WebServerAuthenticationException("Client provided no certificate");
        }

        try {
            certChain[0].checkValidity();
        } catch (CertificateExpiredException exc) {
            throw new WebServerAuthenticationException(exc);
        } catch (CertificateNotYetValidException exc) {
            throw new WebServerAuthenticationException(exc);
        }

        try {
            certChain[0].verify(getAuthorityPublicKey());
        } catch (GeneralSecurityException exc) {
            throw new WebServerAuthenticationException(exc);
        } catch (IOException exc) {
            throw new WebServerAuthenticationException(exc);
        }

        return certChain[0];
    }

    /**
     * Gets the clients identity based on its IP and provided client certificate.
     *
     * @param request servlet request
     * @throws WebServerAuthenticationException if the clients identity can not be verified
     * @return a web server entity representing the client
     */
    private WebServer getClientIdentity(HttpServletRequest request)
            throws WebServerAuthenticationException {

        WebServer webserver;

        LdapName certSubject = getCertificateSubject(getClientCertificate(request));

        LdapName masterCertSubject = getMasterCertificateSubject();

        boolean usingMasterCertificate = certSubject.equals(masterCertSubject);

        if (usingMasterCertificate) {
            logger.debug(String.format("Client from %s provided master certificate",
                    request.getRemoteAddr()));
        }

        String webserverName;

        if (usingMasterCertificate) {
            // Using the master certificate, the fully qualified domain
            // name of the web server is in the parameter "webserver"
            webserverName = request.getParameter("webserver");

            if (webserverName == null) {
                throw new WebServerAuthenticationException(
                        "Need to provide explicit webserver name if using master certificate");
            }
        } else {
            // Client certificates are required to have the fully
            // qualified domain name of the web server as subject
            webserverName = (String) certSubject.getRdn(certSubject.size() - 1).getValue();
        }

        try {
            webserver = (WebServer) dasientEM.createNamedQuery("getWebServerByName")
                    .setParameter("name", webserverName)
                    .getSingleResult();
        } catch (NoResultException exc) {
            throw new WebServerAuthenticationException(String.format(
                    "No web server with name '%s' known",
                    webserverName), exc);
        }

        if (usingMasterCertificate) {
            // The master certificate can connect from anywhere, no IP check performed
            logger.warn(String.format("Client from %s used master certificate to authenticate as %s",
                    request.getRemoteAddr(), webserverName));
        } else {
            if (!request.getRemoteAddr().equals(webserver.getIpAddress())) {
                throw new WebServerAuthenticationException(String.format(
                        "Client %s failed IP address verification: IP address on record is %s, but client is %s",
                        webserverName, webserver.getIpAddress(), request.getRemoteAddr()));
            }
        }

        return webserver;
    }

    /**
     * Authenticates the client based on its IP and provided client certificate.
     *
     * This method will either succeed with authentication, or throw an Exception.
     *
     * The proper response to a thrown WebServerAuthenticationException is
     * to tell the client the access is forbidden.
     *
     * More information about failed authentication can be found in the logs,
     * at log level WARN.
     *
     * @param request servlet request
     * @throws WebServerAuthenticationException if the clients identity can not be verified
     * @return a web server entity representing the client
     */
    private WebServer authenticateClient(HttpServletRequest request)
            throws WebServerAuthenticationException {

        WebServer webserver;

        logger.debug(String.format("Trying to authenticate client from %s", request.getRemoteAddr()));

        try {
            webserver = getClientIdentity(request);

            logger.debug(String.format("Client from %s successfully authenticated as %s",
                    request.getRemoteAddr(), webserver.getName()));

        } catch (WebServerAuthenticationException exc) {
            logger.warn(String.format("Failed to authenticate client from '%s': %s",
                    request.getRemoteAddr(), exc.getMessage()), exc);
            throw exc;
        }

        return webserver;
    }

    /**
     * Returns an InputStream for a python script that is distributed with the application.
     *
     * This will look for a file with the given name within this package.
     *
     * If no file with the given name is found, this method will throw an IOException.
     *
     * @param filename the file name of the python script
     * @throws IOException if an I/O error occurs
     * @return an InputStream to read the script from
     */
    private InputStream getPythonResource(String filename) throws IOException {
        StringBuilder resourceName = new StringBuilder();

        // Prepend with "/" to denote an absolute path
        resourceName.append('/');

        // Replaces all dots in the package name with slashes, to get the path to the package files
        resourceName.append(getClass().getPackage().getName().replace('.', '/'));
        resourceName.append('/');
        resourceName.append(filename);

        // Use the same ClassLoader as this class to resolve the file within the same JAR
        InputStream pythonResource = getClass().getResourceAsStream(resourceName.toString());

        if (pythonResource == null) {
            throw new IOException(String.format("Python resource is missing: %s", resourceName));
        }

        return pythonResource;
    }

    /**
     * Converts the list of work orders into a Python pickle stream.
     *
     * @param output the stream to write the pickle to
     * @param cleanSchedule the minutes of the hour at which the cleaning agent should schedule the next request for work
     * @param newestRevision the newest revision of the rule set the cleaning agent should use
     * @param newestRules the rule set the cleaning agent should use
     * @param workOrders the list of work orders to pickle
     * @throws ServletException if Python throws an exception
     * @throws IOException if an I/O error occurs
     */
    protected void pickleWork(OutputStream output, int cleanSchedule, Revision newestRevision, List<RuleRevision> newestRules, Collection<CleanWorkRequest> workOrders)
            throws IOException, ServletException {

        synchronized (python) {
            try {
                PyObject pickleDef = python.get("pickle_work");
                PyFile pyOutput = FileUtil.wrap(output);
                PyObject[] args = {pyOutput, Py.java2py(cleanSchedule), Py.java2py(newestRevision), Py.java2py(newestRules), Py.java2py(workOrders)};
                pickleDef.__call__(args);
                pyOutput.flush();
            } catch (PyException exc) {
                throw new ServletException(exc);
            }
        }
    }

    /**
     * Converts a Python pickle stream into a work response.
     *
     * @param input the stream to read the pickle from
     * @throws ServletException if Python throws an exception
     * @throws IOException if an I/O error occurs
     * @return the unpickled data as a POJO
     */
    protected CleanWorkResponse unpickleWorkResponse(InputStream input)
            throws IOException, ServletException {

        synchronized (python) {
            try {
                PyObject pickleDef = python.get("unpickle_work_response");
                PyObject workResponse = pickleDef.__call__(FileUtil.wrap(input));
                return Py.tojava(workResponse, CleanWorkResponse.class);
            } catch (PyException exc) {
                throw new ServletException(exc);
            }
        }
    }

    private static class RuleSet {
        Revision newestRevision;
        List<RuleRevision> newestRules;
    }

    /**
     * Returns the revision and rule details about the
     * cleaning rules that are currently in effect.
     *
     * @throws ServletException if the JNDI lookup for the RuleSet bean fails
     */
    private RuleSet getActiveRuleSet() throws ServletException {
        RuleSet set = new RuleSet();

        RuleSetLocal ruleSetBean = null;

        try {
            ruleSetBean = (RuleSetLocal) new InitialContext()
                .lookup("DasientIntegration/RuleSetBean/local");

            set.newestRevision = ruleSetBean.getLatestRevision();

            if (set.newestRevision != null) {
                set.newestRules = ruleSetBean.getAliveRulesByRevision(set.newestRevision.getId());
            }
            else {
                set.newestRules = Collections.emptyList();
            }
        }
        catch (NamingException exc) {
            throw new ServletException(exc);
        }
        finally {
            if (ruleSetBean != null) {
                try {
                    ruleSetBean.release();
                }
                catch (NoSuchEJBException exc) {
                    // thrown if the EJB was released already
                }
            }
        }

        return set;
    }

    /**
     * Handles a call from a clean script agent that requests work.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void processGetWork(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        WebServer webserver;

        try {
            webserver = authenticateClient(request);
        } catch (WebServerAuthenticationException exc) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    request.getRequestURI());
            return;
        }

        logger.debug(String.format("Processing %s for client from %s",
                GET_WORK_PATH, request.getRemoteAddr()));

        RuleSet ruleSet = getActiveRuleSet();

        logger.debug(String.format("Reporting %d rules (revision %d) to client from %s (authenticated as %s)",
                ruleSet.newestRules.size(), ruleSet.newestRevision != null ? ruleSet.newestRevision.getId() : 0,
                request.getRemoteAddr(), webserver.getName()));

        List<CleanWorkRequest> workOrders;

        try {
            workOrders = cleanBrokerBean.getWorkOrders(webserver.getName(), true);
        } catch (WebServerNotFoundException exc) {
            throw new ServletException(exc);
        }

        logger.debug(String.format("Reporting %d work orders to client from %s (authenticated as %s)",
                workOrders.size(), request.getRemoteAddr(), webserver.getName()));

        // The MIME type is text/plain, because that will lead browsers
        // to display it directly instead of wanting to download it.
        // Pickle data is supposed to be printable ASCII only, thus the charset.
        response.setContentType("text/plain; charset=ASCII");

        OutputStream out = response.getOutputStream();
        pickleWork(out, webserver.getCleanSchedule(), ruleSet.newestRevision, ruleSet.newestRules, workOrders);
        out.close();
    }

    private String getBackupFileName(FileItem item, long historyId) {
        String fileName = item.getName();
        
        if (fileName != null) {
            // strips the path and returns only the base name
            fileName = FilenameUtils.getName(fileName);
        }
        else {
            fileName = String.format("backup-%d.zip", historyId);
        }

        return fileName;
    }

    /**
     * Handles a call from a clean script agent that responds with the result of a clean.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void processSubmitResult(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!ServletFileUpload.isMultipartContent(request)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Payload must be multipart/form-data");
            return;
        }

        WebServer webServer;

        try {
            webServer = authenticateClient(request);
        } catch (WebServerAuthenticationException exc) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    request.getRequestURI());
            return;
        }

        logger.debug(String.format("Processing %s for client from %s",
                SUBMIT_RESULT_PATH, request.getRemoteAddr()));

        Map<String, FileItem> parts;

        try {
            parts = parseMultipartRequest(request);
        } catch (FileUploadException exc) {
            throw new ServletException(exc);
        }

        InputStream resultStream = null;
        InputStream backupStream = null;

        try {
            FileItem resultPart = parts.get("data");

            if (resultPart == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Missing payload: data");
                return;
            }

            CleanWorkResponse workResult = unpickleWorkResponse(resultPart.getInputStream());

            workResult.setWebServer(webServer.getName());

            UserTransaction tx = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");

            tx.begin();

            try {
                CleanHistory history = cleanBrokerBean.submitWorkResult(workResult);

                FileItem backupPart = parts.get("backup");

                if (backupPart != null) {
                    backupStream = backupPart.getInputStream();

                    BackupUploader uploader = new BackupUploader(history);

                    String backupFileName = getBackupFileName(backupPart, history.getId());

                    uploader.uploadFile(backupStream, backupFileName);
                }

                tx.commit();
            }
            catch (RuntimeException exc) {
                tx.rollback();
                throw exc;
            }

        } catch (NamingException exc) {
            throw new ServletException(exc);
        } catch (NotSupportedException exc) {
            throw new ServletException(exc);
        } catch (SystemException exc) {
            throw new ServletException(exc);
        } catch (RollbackException exc) {
            throw new ServletException(exc);
        } catch (HeuristicMixedException exc) {
            throw new ServletException(exc);
        } catch (HeuristicRollbackException exc) {
            throw new ServletException(exc);
        } catch (CleanWorkUnitNotFoundException exc) {
            throw new ServletException(exc);
        } catch (WebServerNotFoundException exc) {
            throw new ServletException(exc);
        } catch (WebServerMismatchException exc) {
            throw new ServletException(exc);
        } catch (PersistBackupException exc) {
            throw new ServletException(exc);
        } finally {
            if (resultStream != null) {
                try {
                    resultStream.close();
                } catch (IOException exc) {
                    logger.debug("Failed to close multipart attachment", exc);
                }
            }

            if (backupStream != null) {
                try {
                    backupStream.close();
                } catch (IOException exc) {
                    logger.debug("Failed to close multipart attachment", exc);
                }
            }

            for (FileItem part : parts.values()) {
                part.delete();
            }
        }
    }

    /**
     * Parses HTTP multipart POST data into its components.
     *
     * Any form field data gets thrown away, this method returns only the file attachments.
     *
     * @param request servlet request
     * @return a dictionary of file attachments and their names
     * @throws FileUploadException if the data is malformed or persisting the uploaded data in temporary files fails
     */
    @SuppressWarnings("unchecked")
    private Map<String, FileItem> parseMultipartRequest(HttpServletRequest request)
            throws FileUploadException {

        long maxFileSize = Long.getLong("dasient.max_file_size", 10 * 1024 * 1024);

        DiskFileItemFactory factory = new DiskFileItemFactory();

        ServletFileUpload upload = new ServletFileUpload(factory);

        upload.setFileSizeMax(maxFileSize);

        List<FileItem> rawParts = upload.parseRequest(request);

        Map<String, FileItem> mappedParts = new HashMap<String, FileItem>();

        for (FileItem item : rawParts) {
            if (!item.isFormField()) {
                FileItem prevItem = mappedParts.put(item.getFieldName(), item);

                if (prevItem != null) {
                    prevItem.delete();
                }
            } else {
                item.delete();
            }
        }

        return mappedParts;
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Clean Broker Servlet";
    }
}
