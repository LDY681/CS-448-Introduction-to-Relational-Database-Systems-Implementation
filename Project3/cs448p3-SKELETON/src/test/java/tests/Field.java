package tests;

import global.AttrType;
import org.json.simple.JSONObject;

public class Field {

    private int no;
    private int type;
    private int length;
    private String name;

    Field(JSONObject obj) {
        no = ((Long) obj.get("no")).intValue();
        type = AttrType.fromString((String) obj.get("type"));
        length = ((Long) obj.get("length")).intValue();
        name = (String) obj.get("name");
    }

    public Object getValue(String val) {
        switch (type) {
            case AttrType.INTEGER: {
                return Integer.parseInt(val.trim());
            }

            case AttrType.FLOAT: {
                return Float.parseFloat(val);
            }

            default: {
                return val;
            }
        }
    }

    public int getNo() {
        return no;
    }

    public int getType() {
        return type;
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }
}
