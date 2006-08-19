package mailreader2;

import java.util.List;

public interface AppService {

    public List getLocaleList() throws Exception;

    public void insertUser(AppData data) throws Exception;

}
