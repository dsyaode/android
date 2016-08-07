package dstest.filesearch;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "tb_appfile")
public class AppFileChangeData implements Serializable{

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(columnName = "packageName")
    public String packageName;
    @DatabaseField(columnName = "path")
    public String path;
    @DatabaseField(columnName = "eventType")
    public int eventType;
    @DatabaseField(columnName = "time")
    public long time;

    public AppFileChangeData(){}

    public AppFileChangeData(String packageName, String path, int eventType, long time){
        this.packageName = packageName;
        this.path = path;
        this.eventType = eventType;
        this.time = time;
    }
}
