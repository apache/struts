package mailreader2.service;

import com.ibatis.common.resources.Resources;
import com.ibatis.common.jdbc.ScriptRunner;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.io.Reader;
import java.util.Properties;

public class SqlMapAppConfig {

    // ---- TEST MODE SETUP ----

    protected void runScript(String script) throws Exception {
        DataSource ds = getSqlMap().getDataSource();

        Connection conn = ds.getConnection();

        Reader reader = Resources.getResourceAsReader(script);

        ScriptRunner runner = new ScriptRunner(conn, false, false);
        runner.setLogWriter(null);
        runner.setErrorLogWriter(null);

        runner.runScript(reader);
        conn.commit();
        conn.close();
        reader.close();
    }

    final static String[] CONFIG_SCRIPTS =
            {"sql/mailreader-schema.sql","sql/mailreader-sample.sql"};

    private boolean testMode = true;

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    // ---- SQL MAP SETUP ----

    final static String CONFIG_FILE = "sql-map-config.xml";

    protected String configFile;

    public String getConfigFile() {
        if (configFile==null) return CONFIG_FILE;
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    protected Properties configProps;

    public Properties getConfigProps() {
        return configProps;
    }

    public void setConfigProps(Properties configProps) {
        this.configProps = configProps;
    }

    private SqlMapClient sqlMap;

    public SqlMapClient getSqlMap() {
        return sqlMap;
    }

    public void setSqlMap(SqlMapClient sqlMap) {
        this.sqlMap = sqlMap;
    }

    public SqlMapAppConfig() {
        try {
            Reader reader =
                    Resources.getResourceAsReader(getConfigFile());
            setSqlMap(SqlMapClientBuilder.buildSqlMapClient(reader,
                    getConfigProps()));
            reader.close();

            if (isTestMode())
                for (String script : CONFIG_SCRIPTS) {
                    runScript(script);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
