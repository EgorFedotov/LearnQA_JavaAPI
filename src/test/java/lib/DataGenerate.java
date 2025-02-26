package lib;

import java.text.SimpleDateFormat;

public class DataGenerate {
    public static String getRandomEmail(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "learnqa"+timestamp+"@example.com";
    }
}
