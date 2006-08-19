package mailreader2;

import java.util.List;

public class WelcomeTest extends BaseAppDataTest {

    private Welcome welcome = new Welcome();

    public void setUp() {

    }

    public void testLocaleList() throws Exception {
        welcome.prepare();
        List list = welcome.getAppData().getLocale_list();
        assertLocale_list(list);
        assertTrue("Expected locale list",list.size()>2);
    }

}
