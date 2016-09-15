package org.geoserver.security.onelogin.test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.geoserver.data.test.SystemTestData;
import org.geoserver.security.auth.AbstractAuthenticationProviderTest;
import org.geoserver.security.config.PreAuthenticatedUserNameFilterConfig.PreAuthenticatedUserNameRoleSource;
import org.geoserver.security.onelogin.OneloginAuthenticationFilter;
import org.geoserver.security.onelogin.OneloginAuthenticationFilterConfig;
import org.geotools.data.Base64;
import org.geotools.util.logging.Logging;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.opensaml.common.SAMLObject;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class OneloginAuthenticationTest extends AbstractAuthenticationProviderTest {

    static final Logger LOGGER = Logging.getLogger(OneloginAuthenticationTest.class);

    @Rule
    public WireMockRule idpSamlService = new WireMockRule(wireMockConfig().httpsPort(8443)
            .notifier(new ConsoleNotifier(true)));

    @BeforeClass
    public static void before() throws Exception {
        SSLUtilities.registerKeyStore("keystore");
    }

    @Override
    protected void onSetUp(SystemTestData testData) throws Exception {
        super.onSetUp(testData);

        idpSamlService.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(
                urlEqualTo("/saml/metadata")).willReturn(
                aResponse().withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_XML_VALUE)
                        .withBodyFile("metadata.xml")));

        idpSamlService.stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(
                urlPathEqualTo("/trust/saml2/http-redirect/sso")).willReturn(
                aResponse().withStatus(302).withHeader("Location", "http://localhost:8443/login")));

    }

    @Test
    public void firstTest() throws Exception {
        String oneloginFilterName = "testOneloginFilter";
        OneloginAuthenticationFilterConfig config = new OneloginAuthenticationFilterConfig();
        config.setClassName(OneloginAuthenticationFilter.class.getName());
        config.setUserGroupServiceName("ug1");
        config.setName(oneloginFilterName);
        config.setRoleSource(PreAuthenticatedUserNameRoleSource.UserGroupService);
        config.setEntityId("geoserver");
        config.setMetadataURL("https://localhost:" + idpSamlService.httpsPort() + "/saml/metadata");

        getSecurityManager().saveFilter(config);

        prepareFilterChain(pattern, oneloginFilterName);

        modifyChain(pattern, false, true, null);

        SecurityContextHolder.getContext().setAuthentication(null);

        // Test entry point
        MockHttpServletRequest request = createRequest("/foo/bar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        getProxy().doFilter(request, response, chain);

        assertTrue(response.getStatus() == MockHttpServletResponse.SC_MOVED_TEMPORARILY);
        String redirectURL = response.getHeader("Location");
        URIBuilder uriBuilder = new URIBuilder(redirectURL);
        List<NameValuePair> urlParameters = uriBuilder.getQueryParams();
        String samlRequest = null;
        for (NameValuePair par : urlParameters) {
            if (par.getName().equals("SAMLRequest")) {
                samlRequest = par.getValue();
                break;
            }
        }

        assertNotNull(samlRequest);

        StringSamlDecoder decoder = new StringSamlDecoder();
        SAMLObject samlRequestObject = decoder.decode(samlRequest);

        assertNotNull(samlRequestObject);

        String xml = IOUtils.toString(this.getClass().getResourceAsStream("/__files/response.xml"),
                "UTF-8");

        DateTime now = new DateTime();

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        Document doc = domFactory.newDocumentBuilder().parse(
                new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));

        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xpath.evaluate("//@IssueInstant", doc, XPathConstants.NODESET);

        for (int idx = 0; idx < nodes.getLength(); idx++) {
            Node value = nodes.item(idx);
            value.setNodeValue(now.toString("yyyy-MM-dd'T'HH:mm:ssZ"));
        }

        nodes = (NodeList) xpath.evaluate("//@NotOnOrAfter", doc, XPathConstants.NODESET);

        for (int idx = 0; idx < nodes.getLength(); idx++) {
            Node value = nodes.item(idx);
            value.setNodeValue(now.toString("yyyy-MM-dd'T'HH:mm:ssZ"));
        }

        nodes = (NodeList) xpath.evaluate("//@NotBefore", doc, XPathConstants.NODESET);

        for (int idx = 0; idx < nodes.getLength(); idx++) {
            Node value = nodes.item(idx);
            value.setNodeValue(now.toString("yyyy-MM-dd'T'HH:mm:ssZ"));
        }

        nodes = (NodeList) xpath.evaluate("//@AuthnInstant", doc, XPathConstants.NODESET);

        for (int idx = 0; idx < nodes.getLength(); idx++) {
            Node value = nodes.item(idx);
            value.setNodeValue(now.toString("yyyy-MM-dd'T'HH:mm:ssZ"));
        }

        nodes = (NodeList) xpath.evaluate("//@SessionNotOnOrAfter", doc, XPathConstants.NODESET);

        for (int idx = 0; idx < nodes.getLength(); idx++) {
            Node value = nodes.item(idx);
            value.setNodeValue(now.plusDays(1).toString("yyyy-MM-dd'T'HH:mm:ssZ"));
        }

        Transformer xformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        xformer.transform(new DOMSource(doc), new StreamResult(writer));
        String output = writer.getBuffer().toString();
        String encodedResponseMessage = Base64.encodeBytes(output.getBytes("UTF-8"),
                Base64.DONT_BREAK_LINES).trim();

        MockHttpServletRequest request2 = createRequest(SAMLProcessingFilter.FILTER_URL);
        request2.setMethod("POST");
        request2.addParameter("SAMLResponse", encodedResponseMessage);
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        getProxy().doFilter(request2, response2, chain);

        SecurityContext ctx = (SecurityContext) request2.getSession(false).getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
        assertNotNull(ctx);
        Authentication auth = ctx.getAuthentication();
        assertNotNull(auth);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        checkForAuthenticatedRole(auth);
        assertEquals("abc@xyz.com", auth.getPrincipal());
    }
}
