package com.idmgroup.dspace.rest.jersey;

import static com.idmgroup.dspace.rest.TestConstants.DEMO_DSPACE_ADMIN;
import static com.idmgroup.dspace.rest.TestConstants.DEMO_DSPACE_BAD_PASSWORD;
import static com.idmgroup.dspace.rest.TestConstants.DEMO_DSPACE_PASSWORD;
import static com.idmgroup.dspace.rest.TestConstants.DEMO_DSPACE_URL;
import static com.idmgroup.dspace.rest.jersey.JerseyTestUtils.user;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.ws.rs.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * Tests the REST client (Index).
 * 
 * @author arnaud
 */
public class TestDSpaceJerseyRestClientIndex {

    private DSpaceJerseyRestClient newClient(String url) throws Exception {
        ClientConfig cc = new DefaultClientConfig();
        Client cl = Client.create(cc);
        DSpaceJerseyRestClient client = new DSpaceJerseyRestClient(url, cl);
        client.init();
        return client;
    }

    /**
     * Tests the HTML returned by the root URL.
     * 
     * @throws Exception
     */
    @Test
    public void testIndex() throws Exception {
        DSpaceJerseyRestClient client = newClient(DEMO_DSPACE_URL);
        String index = client.root().getAsTextHtml(String.class);
        assertTrue("index title", index.indexOf("<title>DSpace REST - index</title>") >= 0);
        assertTrue("index index heading", index.indexOf("<h2>Index</h2>") >= 0);
        assertTrue("index communities heading", index.indexOf("<h2>Communities</h2>") >= 0);
        assertTrue("index collections heading", index.indexOf("<h2>Collections</h2>") >= 0);
        assertTrue("index items heading", index.indexOf("<h2>Items</h2>") >= 0);
        assertTrue("index bitstreams heading", index.indexOf("<h2>Bitstreams</h2>") >= 0);
    }

    /**
     * Tests the login.
     * 
     * @throws Exception
     */
    @Test
    public void testLogin() throws Exception {
        DSpaceJerseyRestClient client = newClient(DEMO_DSPACE_URL);
        String token = client.loginJsonAs(user(DEMO_DSPACE_ADMIN, DEMO_DSPACE_PASSWORD));
        assertTrue("dspace token", StringUtils.isNotBlank(token));
        assertTrue("dspace token format", token.matches("[-0-9A-Fa-f]+"));
    }

    /**
     * Tests the login with invalid credentials.
     * 
     * @throws Exception
     */
    @Test
    public void testLoginFail() throws Exception {
        DSpaceJerseyRestClient client = newClient(DEMO_DSPACE_URL);
        try {
            client.loginJsonAs(user(DEMO_DSPACE_ADMIN, DEMO_DSPACE_BAD_PASSWORD));
            fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            assertEquals("HTTP status", 403, e.getResponse().getStatus());
        }
    }

    /**
     * Tests the logout.
     * 
     * @throws Exception
     */
    @Test
    public void testLogout() throws Exception {
        DSpaceJerseyRestClient client = newClient(DEMO_DSPACE_URL);
        client.loginJsonAs(user(DEMO_DSPACE_ADMIN, DEMO_DSPACE_PASSWORD));
        client.logout();
    }

    /**
     * Tests the logout with no token.
     * 
     * @throws Exception
     */
    @Test
    public void testLogoutFail() throws Exception {
        DSpaceJerseyRestClient client = newClient(DEMO_DSPACE_URL);
        try {
            client.logout();
            fail("Expected WebApplicationException to be thrown");
        } catch (WebApplicationException e) {
            assertEquals("HTTP status", 400, e.getResponse().getStatus());
        }
    }

    /**
     * Tests the test URL.
     * 
     * @throws Exception
     */
    @Test
    public void testTest() throws Exception {
        DSpaceJerseyRestClient client = newClient(DEMO_DSPACE_URL);
        String testString = client.root().test().getAs(String.class);
        assertEquals("test string", "REST api is running.", testString);
    }

}