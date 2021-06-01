package sample;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static sample.Common.*;
import static sample.Main.*;

public class CalculateHashTask {

    private int count = 0;
    private int totalCount;
    private HashMap<String,ArrayList> hasMap = new HashMap();

    public CalculateHashTask(int i){
        this.totalCount = i;
    }

    public HashMap call() {
        recurs(CHECK_PATH);
        saveHashMap(hasMap);
        return hasMap;
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
                    //progress
                    count++;
                    double currentPercent = (count*100.d/totalCount);
                    DecimalFormat df = new DecimalFormat("###.##");
                    //hash
                     String pth = dir.getAbsolutePath();
                    Main.getLogicTask().setMessage(" Total progress: "+ df.format(currentPercent) + "%. Current file: "+pth);
                    String md5 = getMD5(pth);
                    if (hasMap.containsKey(md5)){
                        ArrayList<String> arrayList = hasMap.get(md5);
                        arrayList.add(pth);
                        hasMap.put(md5,arrayList);
                    }
                    else{
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList.add(pth);
                        hasMap.put(md5,arrayList);
                    }
                }
            }
        }
    }
}
