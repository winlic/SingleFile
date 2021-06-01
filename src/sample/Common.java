package sample;

import javafx.application.Platform;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static sample.Main.*;

public class Common {

    public static boolean isComprise(){
        if (MAKE_COPY){
            if(COPY_PATH.indexOf(CHECK_PATH)==0){
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }

    public static void setLogRunLater(String string){
        Platform.runLater(new Runnable() {
            public void run() {
                if (!CANCELED){
                    Main.setLog(string);
                }
            }
        });
    }

    public static String getMD5(String path){
        try (InputStream is = Files.newInputStream(Paths.get(path))) {
            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            is.close();
            return md5;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String currentDate (){
        SimpleDateFormat formatOfCurrentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new Date();
        return formatOfCurrentDate.format(currentDate);
    }

    public static void saveHashMap (HashMap map){
        try{
            File file = new File (CHECK_PATH + "\\" + HASH_FILE_NAME);
            FileOutputStream f = new FileOutputStream(file);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(map);
            s.close();
            Common.setLogRunLater("Сохранение MD5 HashMap в файл - " + CHECK_PATH + "\\" + HASH_FILE_NAME);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

}
