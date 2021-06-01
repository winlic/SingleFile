package sample;

import java.io.File;
import static sample.Common.isComprise;
import static sample.Main.*;

public class CalculateFilesTask {

    private int count = 0;

    public Integer call() {
        recurs(CHECK_PATH);
        return count;
    }

    private void recurs(String path){
        if (!CANCELED){
            File dir = new File(path);
            if (dir.isDirectory()) {
                File[] list = dir.listFiles();
                if (list != null) for (File name : list) recurs(name.getPath());
            }
            else if (dir.isFile()){
                //исключать из подсчета каталог с резервными копиями
                if (!isComprise() || (isComprise() && dir.getPath().indexOf(COPY_PATH)!=0)){
                    count++;
                    Main.getLogicTask().setMessage(" Files: "+count);
                }
            }
        }
    }
}
