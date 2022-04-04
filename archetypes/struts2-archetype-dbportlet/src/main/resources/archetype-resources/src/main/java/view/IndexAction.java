package ${package}.view;

import org.apache.struts2.dispatcher.DefaultActionSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.List;

/**
 * Loads and displays the contents of a database table
 */
public class IndexAction extends DefaultActionSupport {

    private static boolean initialized = false;
    private DataSource ds;
    private static List<Map<String,Object>> data;
    private static long lastLoaded;
    private static long CACHE_TIME = 1000 * 60;

    public void setDataSource(DataSource ds) {

        // Initializes the in-memory database (not necessary in production)
        if (!initialized) {
            SimpleJdbcTemplate jt = new SimpleJdbcTemplate(ds);
            jt.update("CREATE TABLE sample_table ( id INTEGER IDENTITY, str_col VARCHAR(256), num_col INTEGER)");
            jt.update("INSERT INTO sample_table(str_col,num_col) VALUES('Ford', 100)");
            jt.update("INSERT INTO sample_table(str_col,num_col) VALUES('Toyota', 200)");
            jt.update("INSERT INTO sample_table(str_col,num_col) VALUES('Mazda', 300)");
            initialized = true;
        }
        this.ds = ds;
    }

    public String execute() {

        // Only refresh the data every minute as needed
        long now = System.currentTimeMillis();
        if (lastLoaded + CACHE_TIME < now) {
            SimpleJdbcTemplate jt = new SimpleJdbcTemplate(ds);
            data = jt.queryForList("SELECT * FROM sample_table");
            lastLoaded = now;
        }
        return SUCCESS;
    }

    public List<Map<String,Object>> getData() {
        return data;
    }
}

