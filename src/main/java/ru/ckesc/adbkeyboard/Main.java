package ru.ckesc.adbkeyboard;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.ckesc.adbkeyboard.connection.ConnectionListener;
import ru.ckesc.adbkeyboard.connection.DeviceConnection;
import ru.ckesc.adbkeyboard.connection.KeyAction;
import ru.ckesc.adbkeyboard.connection.MonkeyDeviceConnection;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application implements ConnectionListener {

    private Map<KeyCode, Integer> adbEventMap = new HashMap<>();
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    public DeviceConnection deviceConnection;

    private void initMap() {
//        19 -->  "KEYCODE_DPAD_UP"
//        20 -->  "KEYCODE_DPAD_DOWN"
//        21 -->  "KEYCODE_DPAD_LEFT"
//        22 -->  "KEYCODE_DPAD_RIGHT"

        adbEventMap.put(KeyCode.UP, 19);
        adbEventMap.put(KeyCode.DOWN, 20);
        adbEventMap.put(KeyCode.LEFT, 21);
        adbEventMap.put(KeyCode.RIGHT, 22);
        adbEventMap.put(KeyCode.ENTER, 66);
        adbEventMap.put(KeyCode.ESCAPE, 4);
        adbEventMap.put(KeyCode.BACK_SPACE, 67);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        scene = new Scene(new Group());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Adb Key Monkey!");
        primaryStage.show();

        initMap();
        scene.setFill(Color.WHEAT);
        scene.setOnKeyPressed(new KeyDownEventHandler());
        scene.setOnKeyReleased(new KeyUpEventHandler());

//        deviceConnection = new ShellDeviceConnection();
        deviceConnection = new MonkeyDeviceConnection();
        deviceConnection.setConnectionListener(this);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                deviceConnection.connect();
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        deviceConnection.disconnect();
    }

    @Override
    public void onConnectionLost() {
        scene.setFill(Color.DARKRED);
    }

    @Override
    public void onConnectionOk() {
        scene.setFill(Color.WHITE);
    }

    private class KeyDownEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (adbEventMap.containsKey(keyEvent.getCode())) {
                deviceConnection.sendEventToDevice(adbEventMap.get(keyEvent.getCode()), KeyAction.DOWN);
            } else {
                deviceConnection.sendTextToDevice(keyEvent.getText(), KeyAction.DOWN);
            }
        }
    }

    private class KeyUpEventHandler implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (adbEventMap.containsKey(keyEvent.getCode())) {
                deviceConnection.sendEventToDevice(adbEventMap.get(keyEvent.getCode()), KeyAction.UP);
            } else {
                deviceConnection.sendTextToDevice(keyEvent.getText(), KeyAction.UP);
            }
        }
    }
}