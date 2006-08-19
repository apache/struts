package mailreader2.service;

import mailreader2.AppService;
import mailreader2.Constants;
import mailreader2.AppData;

import java.util.List;

public class SqlMapAppService extends SqlMapAppConfig implements AppService {

    // ---- APPSERVICE IMPLEMENTATION ----

    public List getLocaleList() throws Exception {
        return getSqlMap().queryForList(Constants.LOCALE_LIST, null);
    }

    public void insertUser(AppData data) throws Exception {
        getSqlMap().insert(Constants.REGISTRATION_INSERT, data);
    }


}
