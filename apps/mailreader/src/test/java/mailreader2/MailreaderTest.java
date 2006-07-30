package mailreader2;

/**
 * @author Ted Husted
 * @version $Revision: 1.0 $ $Date: Jul 29, 2006 5:34:31 PM $
 */
public class MailreaderTest extends BaseSqlMapTest {

    // SETUP & TEARDOWN

    public void testUser_locale() throws Exception {
        AppData output = (AppData) sqlMap.queryForObject("locale_list",null);
        assertNotNull(output);
    }

    public void testUser_fullname() throws Exception {
        AppData input =  new AppData();
        input.setUsername("user");
        AppData output = (AppData) sqlMap.queryForObject("registration_fullname", input);
        assertEquals("John Q. User", output.getFullname());
    }
}
