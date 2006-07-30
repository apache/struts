package mailreader2;

/**
 * @author Ted Husted
 * @version $Revision: 1.0 $ $Date: Jul 29, 2006 5:34:31 PM $
 */
public class MailreaderTest extends BaseSqlMapTest {

    // DEFAULTS FROM SAMPLE SCRIPT

    private static String locale_key = "01-en";

    private static String registration_key = "01-user";
    private static String username = "user";
    private static String password = "pass";
    private static String fullname = "John Q. User";
    private static String email_from = "zaphod@magrathea.com";
    private static String email_replyto = "zb42@igmail.com";

    private static String registration_key2 = "02-zaphod";
    private static String username2 = "zaphod";
    private static String password2 = "g4rgl3Bl4st3r";
    private static String fullname2 = "Zaphod Beeblebrox";
    private static String email_from2 = "zaphod@magrathea.com";
    private static String email_replyto2 = "zb42@igmail.com";

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
        assertTrue("Expected user to already exist",count.intValue()==0);
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

    public void test() throws Exception {
    }

    public void testREGISTRATION_FULLNAME() throws Exception {
        input.setUsername(username);
        AppData output = (AppData) sqlMap.queryForObject(Constants.REGISTRATION_FULLNAME, input);
        assertEquals(fullname, output.getFullname());
        assertNull("Expected other fields to be null", output.getPassword());
        assertNull("Expected other fields to be null",output.getEmail_from());
    }

}
