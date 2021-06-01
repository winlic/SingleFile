package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static sample.Common.getMD5;
import static sample.Common.isComprise;
import static sample.Main.*;

public class DuplicatedFilesTask {

    private int duplicateFilesCount = 0;
    private HashMap<String,ArrayList> hashMap;
    public DuplicatedFilesTask (HashMap hm){
        this.hashMap = hm;
    }

    public void call() {
        if (MODE == 1){
            findDuplicate1();
            Main.getLogicTask().setMessage(" Total duplicated files: " + duplicateFilesCount);
        }
        if (MODE == 2) {
            recurs(ORIGINAL_PATH);
            Main.getLogicTask().setMessage(" Total duplicated files: " + duplicateFilesCount);
        }
    }

    public static ArrayList<ArrayList<String>> arrayListWithDuplicatedFiles;

    private void findDuplicate1(){
        if (!CANCELED){
            arrayListWithDuplicatedFiles = new ArrayList<>();
            for (Map.Entry<String, ArrayList> entry : hashMap.entrySet()){
                ArrayList<String> arrayList = entry.getValue();
                if (arrayList.size()>1){
                    ArrayList<String> arrayListWithDuplicatedFiles2 = new ArrayList<>();
                    arrayListWithDuplicatedFiles2.add(entry.getKey()); //add MD5 to PostProcessing Array
                    for (int i=0;i<arrayList.size();i++){
                        Common.setLogRunLater("Найдены дубликаты - " + arrayList.get(i));
                        arrayListWithDuplicatedFiles2.add(arrayList.get(i));
                    }
                    Common.setLogRunLater("");
                    System.out.println("");
                    Main.getLogicTask().setMessage(" Current duplicated files count: " + duplicateFilesCount);
                    duplicateFilesCount++;
                    arrayListWithDuplicatedFiles.add(arrayListWithDuplicatedFiles2);
                }
            }
        }
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
                    String pth = dir.getAbsolutePath();
                    String md5 = getMD5(pth);
                    Main.getLogicTask().setMessage(" Total duplicated files: " + duplicateFilesCount + ". Current file: " + pth);
                    findDuplicate(dir,md5);
                }
            }
        }
    }

    private void findDuplicate(File dir, String md5){
        if(hashMap.containsKey(md5)){
            ArrayList<String> arrayList = hashMap.get(md5);
            for(int i=0; i<arrayList.size();i++){
                duplicateFilesCount++;
                Common.setLogRunLater("Найден дубликат файла - " + dir.getAbsolutePath() + " --->>> " + arrayList.get(i));

                //если стоит делать резервные копии
                if (MAKE_COPY){
                    File source = new File(arrayList.get(i));
                    if (source.exists()){
                        String destDirectory = dir.getParent();
                        String path = COPY_PATH + "\\" + destDirectory.substring(3,destDirectory.length());
                        new File(path).mkdirs();
                        File dest = new File(path + "\\" + dir.getName());
                        try{
                            copyFileUsingStream(source,dest);
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }

                //если стоит удалить
                if (DELETE_DUPLICATE_FILES){
                    File file = new File(arrayList.get(i));
                    if (file.delete()){
                        Common.setLogRunLater("Дубликат удален - " + arrayList.get(i));
                    }
                    else{
                        Common.setLogRunLater("Ошибка удаления дубликата или файл удален ранее - " + arrayList.get(i));
                    }
                }
            }
        }
    }

    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }


}
