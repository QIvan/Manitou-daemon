/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import junit.framework.TestCase;
import org.junit.*;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.util.Properties;

/**
 *
 * @author ivan
 */
public class gNetSettingsTest extends TestCase
{
    
    public gNetSettingsTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }
    
    @Before
    public void setUp()
    {
        Session.getDefaultInstance(new Properties()).setDebug(true);
    }
    
    @After
    public void tearDown()
    {
        Session.getDefaultInstance(new Properties()).setDebug(false);
    }

    /**
     * Test of getInstance method, of class gNetSettings.
     */
    @Test
    public void testIsConnect()
    {
        gNetSettings settings = gNetSettings.getInstance();
        try {
            assertTrue(settings.getPopConnect().isConnected());
            assertTrue(settings.getImapConnect().isConnected());
            assertTrue(settings.getSmtpTransport().isConnected());
        } catch (MessagingException ex) {
            fail();
        }
        
        
    }


}
