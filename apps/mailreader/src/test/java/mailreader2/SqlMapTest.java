package mailreader2;

import java.util.List;

/**
 * @author Ted Husted
 * @version $Revision: 1.0 $ $Date: Jul 29, 2006 5:34:31 PM $
 */
public class SqlMapTest extends BaseSqlMapTest {

    // DEFAULTS FROM SAMPLE SCRIPT

    private static int SAMPLE_SIZE = 2;

    private static String locale_key = "01-en";

    private static String registration_key = "01-user";
    private static String username = "user";
    private static String password = "pass";
    private static String fullname = "John Q. User";
    private static String email_from = "John.User@somewhere.com";
    private static String email_replyto = "";
    private static String subscription_key = "01-hotmail";
    private static String subscription_host = "mail.hotmail.com";
    private static String host_user = "user1234";
    private static String host_pass = "bar";
    private static Integer host_auto = Constants.DB_FALSE;

    private static String registration_key2 = "02-zaphod";
    private static String username2 = "zaphod";
    private static String password2 = "G4rgl3Bl4st3r";
    private static String fullname2 = "Zaphod Beeblebrox";
    private static String email_from2 = "zaphod@magrathea.com";
    private static String email_replyto2 = "zb42@igmail.net";

    private static String protocol_name = "POP3 Protoocol";
    private static String protocol_key2 = "02-smtp";

    private static String subscription_key2 = "04-igmail";
    private static String subscription_host2 = "mail.igmail.net";
    private static String host_user2 = "zb42";
    private static String host_pass2 = "J4nxSp1r1t";
    private static Integer host_auto2 = Constants.DB_TRUE;

    // SETUP & TEARDOWN

    private AppData input;

    public void setUp() throws Exception {
        super.setUp();
        input =  new AppData();
    }

    public void testLOCALE_LIST() throws Exception {
        AppData output = (AppData) sqlMap.queryForObject(Constants.LOCALE_LIST,null);
        assertNotNull(output);
    }

    public void testREGISTRATION_INSERT_ASSERT_fail() throws Exception {
        input.setUsername(username);
        Object output = sqlMap.queryForObject(Constants.REGISTRATION_INSERT_ASSERT,input);
        Long count = (Long) output;
        assertTrue("Expected user to already exist",count.intValue()>0);
    }

    public void testREGISTRATION_INSERT_ASSERT() throws Exception {
        input.setUsername(username2);
        Object output = sqlMap.queryForObject(Constants.REGISTRATION_INSERT_ASSERT,input);
        Long count = (Long) output;
        assertTrue("Did not expected user to exist",count.intValue()==0);
    }

    public void testREGISTRATION_INSERT() throws Exception {
        input.setRegistration_key(registration_key2);
        input.setLocale_key(locale_key);
        input.setUsername(username2);
        input.setPassword(password2 );
        input.setFullname(fullname2);
        input.setEmail_from(email_from2);
        input.setEmail_replyto(email_replyto2);
        sqlMap.insert(Constants.REGISTRATION_INSERT,input);
        // Trust but verify
        input.setPassword(null);
        AppData output = (AppData) sqlMap.queryForObject(Constants.REGISTRATION_PASSWORD,input);
        assertEquals(password2,output.getPassword());
    }

    public void testREGISTRATION_PASSWORD()  throws Exception {
        input.setUsername(username);
        AppData output = (AppData) sqlMap.queryForObject(Constants.REGISTRATION_PASSWORD,input);
        assertEquals(password,output.getPassword());
    }

    public void testREGISTRATION_FULLNAME() throws Exception {
        input.setUsername(username);
        AppData output = (AppData) sqlMap.queryForObject(Constants.REGISTRATION_FULLNAME, input);
        assertEquals(fullname, output.getFullname());
        assertNull("Expected other fields to be null", output.getPassword());
        assertNull("Expected other fields to be null",output.getEmail_from());
    }

    public void testREGISTRATION_EDIT() throws Exception {
        input.setRegistration_key(registration_key);
        AppData output = (AppData) sqlMap.queryForObject(Constants.REGISTRATION_EDIT, input);
        assertNotNull("Registration not found!",output);
        assertEquals(fullname, output.getFullname());
        assertEquals(email_from, output.getEmail_from());
        assertEquals(email_replyto, output.getEmail_replyto());
    }

    public void testREGISTRATION_UPDATE() throws Exception {
        input.setRegistration_key(registration_key);
        input.setUsername(username2);
        input.setPassword(password2 );
        input.setFullname(fullname2);
        input.setEmail_from(email_from2);
        input.setEmail_replyto(email_replyto2);
        sqlMap.update(Constants.REGISTRATION_UPDATE,input);
        // Trust but verify
        input.setPassword(null);
        AppData output = (AppData) sqlMap.queryForObject(Constants.REGISTRATION_PASSWORD,input);
        assertEquals(password2,output.getPassword());
    }

    public void testREGISTRATION_UPDATE_fails() throws Exception {
        input.setRegistration_key(null);
        sqlMap.update(Constants.REGISTRATION_UPDATE,input);
    }

    public void testSUBSCRIPTION_INSERT_ASSERT_fail() throws Exception {
        input.setSubscription_key(subscription_key);
        input.setSubscription_host(subscription_host);
        Object output = sqlMap.queryForObject(Constants.SUBSCRIPTION_INSERT_ASSERT,input);
        Long count = (Long) output;
        assertTrue("Expected subscription to already exist",count.intValue()>0);
    }

    public void SUBSCRIPTION_INSERT_ASSERT() throws Exception {
        input.setSubscription_key(subscription_key2);
        input.setSubscription_host(subscription_host2);
        Object output = sqlMap.queryForObject(Constants.SUBSCRIPTION_INSERT_ASSERT,input);
        Long count = (Long) output;
        assertTrue("Did not expect subscription to exist",count.intValue()==0);
    }

    public void testSUBSCRIPTION_INSERT() throws Exception {
        input.setSubscription_key(subscription_key2);
        input.setRegistration_key(registration_key2);
        input.setProtocol_key(protocol_key2);
        input.setSubscription_host(subscription_host2);
        input.setHost_user(host_user2);
        input.setHost_pass(host_pass2);
        input.setHost_auto(host_auto2);
        sqlMap.insert(Constants.SUBSCRIPTION_INSERT,input);
        // Trust but verify
        Object output = sqlMap.queryForObject(Constants.SUBSCRIPTION_INSERT_ASSERT,input);
        Long count = (Long) output;
        assertTrue("Expected subscription to NOW exist",count.intValue()>0);
    }


   private void assertSubscription(AppData output) throws Exception {
       assertEquals(host_user, output.getHost_user());
       assertEquals(host_pass, output.getHost_pass());
       assertEquals(protocol_name, output.getProtocol_name());
       assertEquals(host_auto,output.getHost_auto());
       assertFalse(output.isHost_auto_checkbox());
   }

    public void testSUBSCRIPTION_LIST() throws Exception {
        input.setRegistration_key(registration_key);
        Object output = sqlMap.queryForList(Constants.SUBSCRIPTION_LIST,input);
        assertNotNull("Query failed!",output);
        List list = (List) output;
        assertEquals(SAMPLE_SIZE,list.size());
        AppData row1 = (AppData) list.get(0);
        assertSubscription(row1);
    }

    public void testSUBSCRIPTION_EDIT() throws Exception {
        input.setSubscription_key(subscription_key);
        AppData output = (AppData) sqlMap.queryForObject(Constants.SUBSCRIPTION_EDIT, input);
        assertNotNull("Subscription not found!",output);
        assertSubscription(output);
    }

    public void testSUBSCRIPTION_UPDATE() throws Exception {
        input.setSubscription_key(subscription_key);
        input.setRegistration_key(registration_key2);
        input.setProtocol_key(protocol_key2);
        input.setSubscription_host(subscription_host2);
        input.setHost_user(host_user2);
        input.setHost_pass(host_pass2);
        input.setHost_auto(host_auto2);
        sqlMap.update(Constants.SUBSCRIPTION_UPDATE,input);
        // Trust but verify
        Object output = sqlMap.queryForObject(Constants.SUBSCRIPTION_INSERT_ASSERT,input);
        Long count = (Long) output;
        assertTrue("Expected subscription to exist",count.intValue()>0);
    }

}
