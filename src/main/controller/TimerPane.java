package main.controller;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import main.recipeModel.Recipe;
import main.userModel.User;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class TimerPane {
    private User activeUser;
    DownTimer timer;
    @FXML
    public Label secondsTimer;
    @FXML
    public Label minutesTimer;
    @FXML
    public Label hoursTimer;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<Integer> minutesBox;
    @FXML
    private ComboBox<Integer> secondsBox;
    @FXML
    private ComboBox<Integer> hoursBox;
    @FXML
    private Button startButton;
    @FXML
    private AnchorPane selectPane;
    @FXML
    private  AnchorPane showPane;

    public TimerPane(User activeUser) {
        this.activeUser = activeUser;
    }

    public void initialize() {
            ObservableList<Integer> hoursList = FXCollections.observableArrayList();
            ObservableList<Integer> minAndSecList = FXCollections.observableArrayList();
            for (int i=0; i <= 60; i++) {
                if (i <= 24) {
                    hoursList.add(i);
                }
                minAndSecList.add(i);
            }

            hoursBox.setItems(hoursList);
            minutesBox.setItems(minAndSecList);
            secondsBox.setItems(minAndSecList);

            hoursBox.setValue(0);
            minutesBox.setValue(0);
            secondsBox.setValue(0);
    }

    @FXML
    public void startTimer() {
        scrollUp();
        timer = new DownTimer(hoursBox.getValue(), minutesBox.getValue(), secondsBox.getValue());
    }

    @FXML
    public void onCancelButton() {
        timer.stopTimer();
        scrollDown();
    }

    void scrollUp() {
        TranslateTransition selectPaneUp = new TranslateTransition();
        selectPaneUp.setDuration(Duration.millis(100));
        selectPaneUp.setToX(0);
        selectPaneUp.setToY(-selectPane.getHeight());
        selectPaneUp.setNode(selectPane);
        TranslateTransition showPaneUp = new TranslateTransition();
        showPaneUp.setDuration(Duration.millis(100));
        showPaneUp.setFromX(0);
        showPaneUp.setFromY(showPane.getHeight());
        showPaneUp.setToX(0);
        showPaneUp.setToY(0);
        showPaneUp.setNode(showPane);
        ParallelTransition pt = new ParallelTransition(selectPaneUp, showPaneUp);
        pt.play();
    }
    void scrollDown() {
        TranslateTransition showPaneDown = new TranslateTransition();
        showPaneDown.setDuration(Duration.millis(100));
        showPaneDown.setToX(0);
        showPaneDown.setToY(showPane.getHeight());
        showPaneDown.setNode(showPane);
        TranslateTransition selectPaneDown = new TranslateTransition();
        selectPaneDown.setDuration(Duration.millis(100));
        selectPaneDown.setFromX(0);
        selectPaneDown.setFromY(-selectPane.getHeight());
        selectPaneDown.setToX(0);
        selectPaneDown.setToY(0);
        selectPaneDown.setNode(selectPane);
        ParallelTransition pt = new ParallelTransition(showPaneDown, selectPaneDown);
        pt.play();
    }

    public class DownTimer {
        DownTimerTask task;
        Timer timer;
        DownTimer(int hours, int minutes, int seconds) {
            this.timer = new Timer();
            long calcSeconds = toSec(hours, minutes, seconds);
            this.task = new DownTimerTask(calcSeconds);
            timer.schedule(task, 0, 1000);

        }

        public void stopTimer() {
            timer.cancel();
        }


        private int toSec(int hours, int minutes, int seconds) {
            minutes += hours * 60;
            seconds += minutes * 60;
            return seconds;
        }

        public class DownTimerTask extends TimerTask {
            private long time;

            DownTimerTask(long seconds) {
                this.time = seconds;
            }

            private String formatTime(long time) {
                String strTime = String.valueOf(time);
                if (time < 10)
                    strTime = "0" + strTime;
                return strTime;
            }

            @Override
            public void run() {
                if (time !=  0){
                    time -= 1;
                    System.out.println(time);
                    Platform.runLater(() -> {
                        hoursTimer.setText(formatTime(time / 3600));
                        minutesTimer.setText(formatTime((time % 3600) / 60));
                        secondsTimer.setText(formatTime(time % 60));
                    });
                }
            }
        }
    }


}
