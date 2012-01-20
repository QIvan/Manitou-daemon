/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net;

import org.junit.*;

import javax.mail.MessagingException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 * @author ivan
 */
public class gNetSettingsTest
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
    }
    
    @After
    public void tearDown()
    {
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
