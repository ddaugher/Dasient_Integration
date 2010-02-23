package com.ecommerce.dasient.broker;

import com.ecommerce.dasient.model.Revision;
import com.ecommerce.dasient.model.RuleRevision;
import com.ecommerce.dasient.vo.CleanWorkRequest;
import com.ecommerce.dasient.vo.CleanWorkResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.servlet.ServletException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.core.PySystemState;

public class CleanBrokerServletTest {

    private CleanBrokerServlet servlet;

    @BeforeClass
    public static void setUpClass() {
        // This tells Jython to not use a cache, to not write package indexes during the tests
        Properties pyProps = new Properties();
        pyProps.put("python.cachedir.skip", "true");
        PySystemState.initialize(pyProps, null);
    }

    @Before
    public void setUp() throws ServletException {
        servlet = new CleanBrokerServlet();
        servlet.init();
    }

    @After
    public void tearDown() {
        servlet.destroy();
    }

    /**
     * Test of pickleWorkOrders method, of class CleanBrokerServlet.
     */
    @Test
    @Ignore
    public void testPickleWorkOrders() throws IOException, ServletException {
        List<CleanWorkRequest> orders = new ArrayList<CleanWorkRequest>();

        orders.add(createCleanRequest(1));
        orders.add(createCleanRequest(2));
        orders.add(createCleanRequest(3));

        Revision newestRevision = null;

        List<RuleRevision> newestRules = Collections.emptyList();

        ByteArrayOutputStream outbuffer = new ByteArrayOutputStream();

        servlet.pickleWork(outbuffer, 0, newestRevision, newestRules, orders);

        String actualPickle = new String(outbuffer.toByteArray(), "ASCII");
        String expectedPickle = expectedCleanRequestPickle.replace("\n", System.getProperty("line.separator"));

        Assert.assertEquals(expectedPickle, actualPickle);
    }

    private CleanWorkRequest createCleanRequest(int salt) {
        CleanWorkRequest req = new CleanWorkRequest();

        req.setWorkUnitId(1L);
        req.setControlPanel(String.format("cp%d.opentransfer.com", salt % 10));
        req.setWebServer(String.format("web%d.opentransfer.com", salt));
        req.setWebUsername(String.format("testuser%d", salt));
        req.setAccountId(123);

        return req;
    }

    private static final String expectedCleanRequestPickle =
            "(dp0\nS'delay'\n" +
            "p1\nI0\nsS'ruleSet'\n" +
            "p2\n(d" +
            "p3\nS'contents'\n" +
            "p4\n(l" +
            "p5\nsS'id'\n" +
            "p6\nNssS'workUnits'\n" +
            "p7\n(l" +
            "p8\n(d" +
            "p9\nS'scanHistory'\n" +
            "p10\nNsS'workUnitId'\n" +
            "p11\nVtest-unit-1\n" +
            "p12\nsS'username'\n" +
            "p13\nVtestuser1\n" +
            "p14\nsS'controlPanel'\n" +
            "p15\nVcp1.opentransfer.com\n" +
            "p16\nsS'webServer'\n" +
            "p17\nVweb1.opentransfer.com\n" +
            "p18\nsS'domainName'\n" +
            "p19\nVtestdomain1.com\n" +
            "p20\nsa(d" +
            "p21\ng10\nNsg11\nVtest-unit-2\n" +
            "p22\nsg13\nVtestuser2\n" +
            "p23\nsg15\nVcp2.opentransfer.com\n" +
            "p24\nsg17\nVweb2.opentransfer.com\n" +
            "p25\nsg19\nVtestdomain2.com\n" +
            "p26\nsa(d" +
            "p27\ng10\nNsg11\nVtest-unit-3\n" +
            "p28\nsg13\nVtestuser3\n" +
            "p29\nsg15\nVcp3.opentransfer.com\n" +
            "p30\nsg17\nVweb3.opentransfer.com\n" +
            "p31\nsg19\nVtestdomain3.com\n" +
            "p32\nsas.";

    /**
     * Test of pickleWorkOrders method, of class CleanBrokerServlet.
     */
    @Test
    @Ignore
    public void testUnpickleWorkResponse() throws IOException, ServletException {
        ByteArrayInputStream inbuffer = new ByteArrayInputStream(createCleanResponsePickle());

        CleanWorkResponse response = servlet.unpickleWorkResponse(inbuffer);

        Assert.assertEquals(response.getWorkUnitId(), "test-unit-1");
    }

    private byte[] createCleanResponsePickle() throws UnsupportedEncodingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        synchronized (servlet.python) {
            try {
                PyObject pickleDef = servlet.python.get("pickle").__getattr__("dump");
                pickleDef.__call__(servlet.python.eval(cleanResponseDefinition), Py.java2py(buffer));
            } catch (PyException exc) {
                exc.printStackTrace();
            }
        }

        return buffer.toByteArray();
    }

    private static final String cleanResponseDefinition =
            "{" +
              "'workUnitId': 'test-unit-1',\n" +
              "'cleanLog': 'test output'\n" +
            "}";
}