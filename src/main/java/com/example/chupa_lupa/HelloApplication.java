package com.example.chupa_lupa;

import com.example.chupa_lupa.server.IncrementThread;
import com.example.chupa_lupa.server.ServerFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class HelloApplication extends Application {
    static final int PORT = 12345;
    static final String CLIENT_PATH = "/home/voin/IdeaProjects/chupa_lupa/out/artifacts/client/client.jar";
    static final String TERMINAL_PATH="/home/voin/IdeaProjects/chupa_lupa/out/artifacts/chupa_lupa_jar/chupa_lupa.jar";
    AtomicReference<Double> atomicReference = new AtomicReference<>(100D);
    ServerFactory serverFactory = new ServerFactory(PORT);
    IncrementThread thread1 = new IncrementThread(atomicReference);
    IncrementThread thread2 = new IncrementThread(atomicReference);


    @Override
    public void start(Stage stage) throws IOException {
        Text text = new Text("unknown");

        Button threadButton = new Button("запустить потоки");
        threadButton.setOnAction(actionEvent -> {
            thread1.setDaemon(true);
            thread2.setDaemon(true);
            thread1.start();
            thread2.start();
        });
        Button processButton = new Button("запустить процессы");
        processButton.setOnAction(actionEvent -> {
            Thread mainThread = new Thread(() -> {
                serverFactory.acceptResultFromClient(result -> {
                            System.out.println(result + " sri");
                            atomicReference.updateAndGet(value -> value + Double.parseDouble(result));
                        }
                );
            });
            mainThread.setDaemon(true);
            mainThread.start();

            try {
                serverFactory.startClient(CLIENT_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Button terminalButton = new Button("запустить terminal");
        terminalButton.setOnAction(actionEvent -> {
            try {
                serverFactory.startClient(TERMINAL_PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Slider slider1 = new Slider(Thread.MIN_PRIORITY, Thread.MAX_PRIORITY, Thread.NORM_PRIORITY);
        slider1.setShowTickMarks(true);
        slider1.setShowTickLabels(true);
        slider1.valueProperty().addListener((observable, oldValue, newValue) -> {
            thread1.setPriority(newValue.intValue());
        });
        Label label1=new Label("приоритет потока 1", slider1);
        label1.setContentDisplay(ContentDisplay.BOTTOM);

        Slider slider2 = new Slider(Thread.MIN_PRIORITY, Thread.MAX_PRIORITY, Thread.NORM_PRIORITY);
        slider2.setShowTickMarks(true);
        slider2.setShowTickLabels(true);
        slider2.valueProperty().addListener((observable, oldValue, newValue) -> {
            thread2.setPriority(newValue.intValue());
        });
        Label label2=new Label("приоритет потока 2", slider2);
        label2.setContentDisplay(ContentDisplay.BOTTOM);

        HBox hBox=new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(threadButton, processButton, terminalButton);
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().addAll(text,  hBox, label1, label2);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), (event) -> {
            text.setText(String.format("%.1f",atomicReference.get()) + " power");
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Scene scene = new Scene(vBox, 500, 200);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}