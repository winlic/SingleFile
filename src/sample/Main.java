package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static javafx.geometry.Pos.BOTTOM_LEFT;
import static sample.Common.currentDate;
import static sample.Common.saveHashMap;
import static sample.DuplicatedFilesTask.arrayListWithDuplicatedFiles;

public class Main extends Application {

    public final String version = "1.04"; //by I.Akhremchyk
    public static int MODE;
    public static String ORIGINAL_PATH;
    public static String CHECK_PATH;
    public static String COPY_PATH;
    public static boolean USE_NEW_HASH;
    public static boolean DELETE_DUPLICATE_FILES = false;
    public static boolean MAKE_COPY = false;
    private static String textAreaLogText = "";
    public static final String HASH_FILE_NAME = "hashMapOfFiles.HashMap";
    public static boolean CANCELED = false;
    public static boolean POST_PROCESSING_BREAK;
    public static boolean POST_PROCESSING_SAVE_CHANGES;


    //elements var list
    final Label labelMode = new Label("Режим работы программы:");
    final RadioButton rBtnMode1 = new RadioButton("проверка дубликатов файлов в указанном каталоге (и его подкаталогах)");
    final RadioButton rBtnMode2 = new RadioButton("проверка дубликатов файлов в указанном каталоге (и его подкаталогах), относительно каталога, содержащего оригинальные файлы");
    final Button buttonOriginalCatalog = new Button("Выбор дериктории, содержащей оригинальные файлы");
    final TextArea textAreaButtonOriginalCatalog = new TextArea();
    final Button buttonCheckCatalog = new Button("Выбор дериктории для поиска дубликатов:");
    final TextArea textAreaButtonCheckCatalog = new TextArea();
    final CheckBox checkBoxUseNewHash = new CheckBox("пересчитать Hash для указанного каталога");
    final CheckBox checkBoxDeleteDuplicateFiles = new CheckBox("удалить найденные дубликаты файлов (из дериктории для поиска дубликатов)");
    final CheckBox checkSetCopyForDeletedFiles = new CheckBox("сделать резервные копии найденых дубликатов файлов");
    final Button buttonReserveCopyCatalog = new Button("Выбор дериктории для сохранения резервных копий:");
    final TextArea textAreaReserveButtonCopyCatalog = new TextArea();
    final Button buttonStart = new Button("Старт");
    final Button buttonCancel = new Button("Отмена");
    final public static TextArea textAreaLog = new TextArea();
    final ProgressBar progressBar = new ProgressBar(0);
    final Label statusLabel = new Label();


    @Override
    public void start(Stage primaryStage) throws Exception{

        try {
            Path path = Paths.get("log");
            if (!Files.exists(path)){
                new File("log").mkdir();
            }
            PrintStream printStream = new PrintStream(new File("log/" + currentDate().replace(":","-") + ".txt"));
            System.setOut(printStream);
            System.setErr(printStream);
        }
        catch(Exception e){
            e.printStackTrace();
        }


        //var list
        final DirectoryChooser directoryChooser = new DirectoryChooser();

         //---->elements position
        ToggleGroup toggleGroup = new ToggleGroup();
        rBtnMode1.setToggleGroup(toggleGroup);
        rBtnMode2.setToggleGroup(toggleGroup);
        VBox vBoxMode = new VBox(labelMode, rBtnMode1, rBtnMode2);
        vBoxMode.setPadding(new Insets(10));
        vBoxMode.setSpacing(5);
        //
        buttonOriginalCatalog.setMinWidth(340);
        textAreaButtonOriginalCatalog.setMinHeight(25);
        textAreaButtonOriginalCatalog.setMaxHeight(25);
        textAreaButtonOriginalCatalog.setWrapText(true);
        HBox hBoxOriginalCatalog = new HBox(buttonOriginalCatalog,textAreaButtonOriginalCatalog);
        hBoxOriginalCatalog.setSpacing(5);
        VBox vBoxOriginalCatalog = new VBox(hBoxOriginalCatalog);
        vBoxOriginalCatalog.setPadding(new Insets(10));
        //
        buttonCheckCatalog.setMinWidth(340);
        textAreaButtonCheckCatalog.setMinHeight(25);
        textAreaButtonCheckCatalog.setMaxHeight(25);
        textAreaButtonCheckCatalog.setWrapText(true);
        HBox hBoxCheckCatalog = new HBox(buttonCheckCatalog,textAreaButtonCheckCatalog);
        hBoxCheckCatalog.setSpacing(5);
        VBox vBoxNewHash = new VBox(checkBoxUseNewHash);
        vBoxNewHash.setSpacing(5);
        VBox vBoxCheckCatalog = new VBox(hBoxCheckCatalog, vBoxNewHash);
        vBoxCheckCatalog.setPadding(new Insets(10));
        vBoxCheckCatalog.setSpacing(5);
        //
        textAreaReserveButtonCopyCatalog.setMinHeight(25);
        textAreaReserveButtonCopyCatalog.setMaxHeight(25);
        textAreaReserveButtonCopyCatalog.setWrapText(true);
        HBox hBoxCopyCatalog = new HBox(buttonReserveCopyCatalog,textAreaReserveButtonCopyCatalog);
        hBoxCopyCatalog.setSpacing(5);
        VBox vBoxDeletedFiles = new VBox(checkBoxDeleteDuplicateFiles,checkSetCopyForDeletedFiles,hBoxCopyCatalog);
        vBoxDeletedFiles.setPadding(new Insets(10));
        vBoxDeletedFiles.setSpacing(5);
        //
        HBox hBoxButtonStart = new HBox(buttonStart,buttonCancel);
        hBoxButtonStart.setSpacing(5);
        VBox vBoxButton = new VBox(hBoxButtonStart);
        vBoxButton.setPadding(new Insets(10));
        vBoxButton.setAlignment(BOTTOM_LEFT);
        VBox vBoxLog = new VBox(textAreaLog);
        vBoxLog.setPadding(new Insets(10));
        vBoxLog.setSpacing(5);
        //
        statusLabel.setMinWidth(250);
        statusLabel.setTextFill(Color.BLUE);
        HBox hBoxStatus = new HBox(progressBar, statusLabel);
        VBox vBoxStatus = new VBox(hBoxStatus);
        vBoxStatus.setPadding(new Insets(10));
        vBoxStatus.setSpacing(5);
        //
        VBox vBoxMain = new VBox(vBoxMode, vBoxOriginalCatalog, vBoxCheckCatalog, vBoxDeletedFiles, vBoxButton, vBoxLog, vBoxStatus);
        Scene scene = new Scene(vBoxMain, 850, 580);
        primaryStage.setTitle("SingleFile program ver. "+version);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.toFront();
        //elements position<-----

        //initialize elements status
        MODE = 2;
        rBtnMode2.setSelected(true);
        textAreaButtonOriginalCatalog.setEditable(false);
        textAreaButtonCheckCatalog.setEditable(false);
        checkBoxUseNewHash.setDisable(true);
        buttonReserveCopyCatalog.setDisable(true);
        textAreaReserveButtonCopyCatalog.setEditable(false);
        textAreaReserveButtonCopyCatalog.setDisable(true);
        buttonStart.setDisable(true);
        buttonCancel.setDisable(true);
        textAreaLog.setEditable(false);
        progressBar.setVisible(false);
        progressBar.setProgress(0);

        //setOnAction other elements
        rBtnMode1.setOnAction(event -> {
            MODE=1;
            checkBoxDeleteDuplicateFiles.setDisable(true);
            checkSetCopyForDeletedFiles.setDisable(true);
            buttonOriginalCatalog.setDisable(true);
            buttonReserveCopyCatalog.setDisable(true);
            checkStartCondition();
        });
        rBtnMode2.setOnAction(event -> {
            MODE=2;
            buttonOriginalCatalog.setDisable(false);
            if (checkSetCopyForDeletedFiles.isSelected()) buttonReserveCopyCatalog.setDisable(false);
            checkBoxDeleteDuplicateFiles.setDisable(false);
            checkSetCopyForDeletedFiles.setDisable(false);
            checkStartCondition();
        });
        buttonOriginalCatalog.setOnAction(event -> {
            configuringDirectoryChooser(directoryChooser,ORIGINAL_PATH);
            File dir = directoryChooser.showDialog(primaryStage);
            if (dir != null) {
                ORIGINAL_PATH = dir.getAbsolutePath();
                textAreaButtonOriginalCatalog.setText(ORIGINAL_PATH);
            } else {
                ORIGINAL_PATH = null;
                textAreaButtonOriginalCatalog.setText(null);
            }
            checkStartCondition();
        });
        buttonCheckCatalog.setOnAction(event -> {
            configuringDirectoryChooser(directoryChooser,CHECK_PATH);
            File dir = directoryChooser.showDialog(primaryStage);
            if (dir != null) {
                CHECK_PATH = dir.getAbsolutePath();
                textAreaButtonCheckCatalog.setText(CHECK_PATH);
                if(isHashFileExist()){
                    checkBoxUseNewHash.setDisable(false);
                    checkBoxUseNewHash.setSelected(false);
                }
                else{
                    checkBoxUseNewHash.setDisable(true);
                    checkBoxUseNewHash.setSelected(true);
                }
            } else {
                checkBoxUseNewHash.setDisable(true);
                CHECK_PATH = null;
                textAreaButtonCheckCatalog.setText(null);
            }
            checkStartCondition();
        });
        checkBoxDeleteDuplicateFiles.setOnAction(event ->{
            if (checkBoxDeleteDuplicateFiles.isSelected()){
                DELETE_DUPLICATE_FILES = true;
            }
            else {
                DELETE_DUPLICATE_FILES = false;
            }
        });
        checkSetCopyForDeletedFiles.setOnAction(event -> {
            if (checkSetCopyForDeletedFiles.isSelected()){
                buttonReserveCopyCatalog.setDisable(false);
                textAreaReserveButtonCopyCatalog.setDisable(false);
                MAKE_COPY = true;
            }
            else {
                buttonReserveCopyCatalog.setDisable(true);
                textAreaReserveButtonCopyCatalog.setDisable(true);
                MAKE_COPY = false;
            }
            checkStartCondition();
        });
        buttonReserveCopyCatalog.setOnAction(event -> {
            configuringDirectoryChooser(directoryChooser,COPY_PATH);
            File dir = directoryChooser.showDialog(primaryStage);
            if (dir != null) {
                COPY_PATH = dir.getAbsolutePath();
                textAreaReserveButtonCopyCatalog.setText(COPY_PATH);
            } else {
                COPY_PATH = null;
                textAreaReserveButtonCopyCatalog.setText(null);
            }
            checkStartCondition();
        });

        //setOnAction buttonStart
        buttonStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CANCELED = false;
                buttonStart.setDisable(true);
                progressBar.setVisible(true);
                ORIGINAL_PATH = (textAreaButtonOriginalCatalog.getText().equals("")) ? null : textAreaButtonOriginalCatalog.getText();
                CHECK_PATH = (textAreaButtonCheckCatalog.getText().equals("")) ? null : textAreaButtonCheckCatalog.getText();
                COPY_PATH = (textAreaReserveButtonCopyCatalog.getText().equals("")) ? null : textAreaReserveButtonCopyCatalog.getText();
                USE_NEW_HASH = (checkBoxUseNewHash.isSelected()) ? true : false;
                setElementsDisable(true);

                // Create a Task.
                logicTask = new LogicTask();
                // Unbind progress property
                progressBar.progressProperty().unbind();
                // Bind progress property
                progressBar.progressProperty().bind(logicTask.progressProperty());
                // Unbind text property for Label.
                statusLabel.textProperty().unbind();
                // Bind the text property of Label with message property of Task
                statusLabel.textProperty().bind(logicTask.messageProperty());
                // Start the Task.
                new Thread(logicTask).start();
                buttonCancel.setDisable(false);

                // When completed tasks
                logicTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        //PostProcessing for MODE 1
                        if (MODE==1){
                            POST_PROCESSING_BREAK = false;
                            POST_PROCESSING_SAVE_CHANGES = false;
                            setLog("Всего дубликатов - " + arrayListWithDuplicatedFiles.size());
                            for (int i = 0; i< arrayListWithDuplicatedFiles.size(); i++){
                                PostProcessing postProcessing = new PostProcessing(primaryStage, arrayListWithDuplicatedFiles.get(i));
                                postProcessing.start();
                                //wait for postprocessing
                                pause();
                                if (POST_PROCESSING_BREAK) break;
                            }
                            if (POST_PROCESSING_SAVE_CHANGES) saveHashMap(hashMap);
                        }
                        setElementsStatus();
                        checkBoxUseNewHash.setDisable(false);
                        checkBoxUseNewHash.setSelected(false);
                        setLog("Завершено успешно - " + currentDate() + "\n");
                    }
                });
                // When canceled tasks
                logicTask.addEventHandler(WorkerStateEvent.WORKER_STATE_CANCELLED, new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        setElementsStatus();
                        setLog("Отмена"+"\n");
                    }
                });
            }
        });

        // setOnAction buttonCancel
        buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                buttonCancel.setDisable(true);
                CANCELED = true;
                logicTask.cancel(true);
            }
        });

    }

    private static LogicTask logicTask;
    public static LogicTask getLogicTask(){
        return logicTask;
    }
    private static HashMap<String, ArrayList<String>> hashMap;
    public static void setHashMap(HashMap map){hashMap=map;}
    public static HashMap<String,ArrayList<String>> getHashMap(){
        return hashMap;
    }


    private static final Object PAUSE_KEY = new Object();
    private static void pause() {
        Platform.enterNestedEventLoop(PAUSE_KEY);
    }
    public static void resume() {
        Platform.exitNestedEventLoop(PAUSE_KEY, null);
    }


    public static void setLog (String string){
        System.out.println(string); //save to logFile too
        if (textAreaLogText.equals("")){
            textAreaLogText = string;
        }
        else {
            textAreaLogText = textAreaLogText +"\n"+ string;
        }
        textAreaLog.setText(textAreaLogText);
        textAreaLog.selectPositionCaret(textAreaLog.getLength());
        textAreaLog.deselect();
    }

    private void configuringDirectoryChooser(DirectoryChooser directoryChooser, String currentDirectory) {
        // Set title for DirectoryChooser
        directoryChooser.setTitle("Select Some Directories");

        // Set Initial Directory
        if (currentDirectory==null || currentDirectory.equals("")){
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        else{
            directoryChooser.setInitialDirectory(new File(currentDirectory));
        }
    }

    private boolean isHashFileExist(){
        File file = new File(CHECK_PATH+"\\"+HASH_FILE_NAME);
        if (file.exists()){
            return true;
        }
        else return false;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void setElementsDisable (boolean status){
        buttonOriginalCatalog.setDisable(status);
        buttonCheckCatalog.setDisable(status);
        buttonReserveCopyCatalog.setDisable(status);
        checkBoxUseNewHash.setDisable(status);
        checkBoxDeleteDuplicateFiles.setDisable(status);
        checkSetCopyForDeletedFiles.setDisable(status);
        rBtnMode1.setDisable(status);
        rBtnMode2.setDisable(status);
    }

    private void checkStartCondition(){
        if (MODE==1){
            if (CHECK_PATH!=null) buttonStart.setDisable(false);
            else buttonStart.setDisable(true);
        }
        if (MODE==2){
            if (ORIGINAL_PATH!=null && CHECK_PATH!=null){
                if (checkSetCopyForDeletedFiles.isSelected() && COPY_PATH!=null)buttonStart.setDisable(false);
                else if (!checkSetCopyForDeletedFiles.isSelected()) buttonStart.setDisable(false);
                else buttonStart.setDisable(true);
            }
            else buttonStart.setDisable(true);
        }
    }

    private void setElementsStatus(){
        statusLabel.textProperty().unbind();
        progressBar.progressProperty().unbind();
        progressBar.setProgress(0);
        buttonStart.setDisable(false);
        buttonCancel.setDisable(true);
        setElementsDisable(false);
        if (MODE==1){
            checkBoxDeleteDuplicateFiles.setDisable(true);
            checkSetCopyForDeletedFiles.setDisable(true);
            buttonOriginalCatalog.setDisable(true);
            buttonReserveCopyCatalog.setDisable(true);
        }
        if (MODE==2 && !checkSetCopyForDeletedFiles.isSelected()){
            buttonReserveCopyCatalog.setDisable(true);
        }
    }

}
