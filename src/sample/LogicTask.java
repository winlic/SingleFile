package sample;

import javafx.concurrent.Task;
import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static sample.Common.currentDate;
import static sample.Main.*;

public class LogicTask extends Task {
    @Override
    protected Object call() throws Exception {

        Common.setLogRunLater("Начало работы - " + currentDate());
        System.out.println("Режим работы программы - " + MODE);

        Common.setLogRunLater("Определение количества файлов, подлежащих проверке, в каталоге - " + CHECK_PATH);
        CalculateFilesTask calculateFilesTask = new CalculateFilesTask();
        int totalFiles = calculateFilesTask.call();
        Common.setLogRunLater("Всего файлов - " + totalFiles);

        setHashMap(null);
        if (!CANCELED && USE_NEW_HASH){
            Common.setLogRunLater("Создание MD5 HashMap...");
            CalculateHashTask calculateHashTask = new CalculateHashTask(totalFiles);
            setHashMap(calculateHashTask.call());
            Common.setLogRunLater("Создание MD5 HashMap завершено");
        }
        else if (!CANCELED && !USE_NEW_HASH){
            //считать в hashMap из файла хэша
            if(new File(CHECK_PATH + "\\" + HASH_FILE_NAME).exists()){
                Common.setLogRunLater("Чтение хэша для каталога из файла...");
                FileInputStream fileInputStream = new FileInputStream(CHECK_PATH + "\\" + HASH_FILE_NAME);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                setHashMap((HashMap<String,ArrayList>)objectInputStream.readObject());
                objectInputStream.close();
                fileInputStream.close();
                Common.setLogRunLater("Чтение хэша успешно завершено");
            }
            else {
                Common.setLogRunLater("Ошибка чтения файла хэша");
                CANCELED = true;
            }
        }

        if (!CANCELED){
            Common.setLogRunLater("Поиск дубликатов файлов...");
            DuplicatedFilesTask duplicatedFilesTask = new DuplicatedFilesTask(getHashMap());
            duplicatedFilesTask.call();
        }


        return null;
    }

    public void setMessage(String string){
        this.updateMessage(string);
    }

}
