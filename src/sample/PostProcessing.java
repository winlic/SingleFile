package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import static sample.Main.*;

public class PostProcessing {

    private Stage primaryStage;
    private ArrayList<String> arrayListWithDuplicatedFiles;

    public PostProcessing (Stage stage, ArrayList<String> arrayList){
        this.primaryStage = stage;
        this.arrayListWithDuplicatedFiles = arrayList;
    }

    public void start() {

        final VBox vBox = new VBox();
        final Label labelTitle = new Label("Выберете файл(ы) для удаления:");
        final Button buttonNext = new Button("Delete files and next >>>");
        final Button buttonSkip = new Button("Skip >>>");
        final Button buttonExit = new Button("Exit");

        buttonNext.setMinWidth(130);buttonSkip.setMinWidth(130);buttonExit.setMinWidth(130);
        final HBox hBox = new HBox(buttonNext, buttonSkip, buttonExit);
        hBox.setSpacing(20);
        hBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(hBox);

        labelTitle.setPadding(new Insets(10,0,5,0));
        vBox.getChildren().add(labelTitle);

        ArrayList<CheckBox> arrayListCheckBox = new ArrayList<>();
        ArrayList<Label> arrayListLabel = new ArrayList<>();
        ArrayList<String> arrayListPath = new ArrayList<>();
        String md5 = arrayListWithDuplicatedFiles.get(0);

        arrayListWithDuplicatedFiles.remove(0);
        for (int i=0; i<arrayListWithDuplicatedFiles.size();i++){
            final CheckBox checkBox = new CheckBox("");
            arrayListCheckBox.add(i,checkBox);
            String path = arrayListWithDuplicatedFiles.get(i);
            arrayListPath.add(i,path);
            final Label label = new Label(path);
            label.setTextFill(Color.BLUE);
            arrayListLabel.add(i,label);
            final HBox hBox1 = new HBox(checkBox, label);
            hBox1.setSpacing(5);
            hBox1.setPadding(new Insets(0,0,5,0));
            vBox.getChildren().add(hBox1);
        }

        vBox.setPadding(new Insets(10));
        vBox.setSpacing(5);

        Scene secondScene = new Scene(vBox,500, 400);

        // New window (Stage)
        Stage newWindowStage = new Stage();
        newWindowStage.setTitle("Second Stage");
        newWindowStage.setScene(secondScene);

        // Specifies the modality for new window.
        newWindowStage.initModality(Modality.WINDOW_MODAL);

        // Specifies the owner Window (parent) for new window
        newWindowStage.initOwner(primaryStage);

        // Set position of second window, related to primary window.
        newWindowStage.setX(primaryStage.getX() + 200);
        newWindowStage.setY(primaryStage.getY() + 100);

        newWindowStage.show();

        //setOnAction Label
        for (int i=0; i<arrayListLabel.size();i++){
            String path = arrayListPath.get(i);
            arrayListLabel.get(i).setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    openFileLocation(path);
                }
            });
            int j = i;
            arrayListLabel.get(i).setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    arrayListLabel.get(j).setTextFill(Color.DARKBLUE);
                    newWindowStage.getScene().setCursor(Cursor.HAND);
                }
            });
            arrayListLabel.get(i).setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    arrayListLabel.get(j).setTextFill(Color.BLUE);
                    newWindowStage.getScene().setCursor(Cursor.DEFAULT);
                }
            });
        }
        //setOnAction Lable




        //setOnAction buttonNext
        buttonNext.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                HashMap<String,ArrayList<String>> hashMap = getHashMap();
                //delete all not selected files
                for (int i=0; i<arrayListPath.size();i++){
                    if (arrayListCheckBox.get(i).isSelected()){
                        try {
                            File file = new File(arrayListPath.get(i));
                            if(file.delete()){
                                POST_PROCESSING_SAVE_CHANGES = true;
                                //and delete information about this file from hashMap
                                Common.setLogRunLater("Дубликат удален - " + arrayListPath.get(i));
                                ArrayList<String> arrayList = hashMap.get(md5);
                                for (int j=0; j<arrayList.size();j++) {
                                    if (arrayList.get(j).equals(arrayListPath.get(i))) {
                                        arrayList.remove(j);
                                    }
                                }
                                if (arrayList.size()>0){
                                    hashMap.put(md5, arrayList);
                                }
                                else {
                                    hashMap.remove(md5);
                                }
                            }
                        }
                        catch (Exception e){
                            System.out.println(e);
                        }
                    }
                }
                setHashMap(hashMap);
                newWindowStage.close();
                Main.resume();
            }
        });

        //setOnAction buttonSkip
        buttonSkip.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newWindowStage.close();
                Main.resume();
            }
        });

        //setOnAction buttonExit
        buttonExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newWindowStage.close();
                Main.resume();
                POST_PROCESSING_BREAK = true;
            }
        });

        //setOnAction CloseWindow
        newWindowStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Main.resume();
                POST_PROCESSING_BREAK = true;
            }
        });

    }

    public static void openFileLocation(String path) {
            try {
                Runtime.getRuntime().exec("explorer.exe /select," + path);
            } catch (IOException ex) {
                System.out.println( ex);;
            }

    }




}
