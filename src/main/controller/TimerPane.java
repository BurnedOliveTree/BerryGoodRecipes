package main.controller;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.userModel.User;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class TimerPane extends OrdinaryButtonAction {
    DownTimer timer;
    Media media;
    public MediaPlayer mediaPlayer;
    @FXML
    public Label secondsTimer;
    @FXML
    public Label minutesTimer;
    @FXML
    public Label hoursTimer;
    @FXML
    private Button cancelButton;
    @FXML
    private Spinner<Integer> minutesBox;
    @FXML
    private Spinner<Integer> secondsBox;
    @FXML
    private Spinner<Integer> hoursBox;
    @FXML
    private Button startButton;
    @FXML
    private AnchorPane selectPane;
    @FXML
    private  AnchorPane showPane;

    public void initialize() {
        setSpinnerProperty(hoursBox);
        setSpinnerProperty(minutesBox);
        setSpinnerProperty(secondsBox);
        setMedia();

        Platform.runLater(() -> {
        Stage stage = (Stage) hoursBox.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            mediaPlayer.pause();
            if (timer != null)
                timer.stopTimer();
        });});
    }

    private void setMedia(){
        media = new Media(new File("src/resources/dingdong.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.5);
    }

    @FXML
    public void startTimer() {
        scrollUp();
        timer = new DownTimer(hoursBox.getValue(), minutesBox.getValue(), secondsBox.getValue());
    }

    @FXML
    public void onCancelButton() {
        timer.stopTimer();
        mediaPlayer.pause();
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
            mediaPlayer.pause();
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

                    Platform.runLater(() -> {
                        hoursTimer.setText(formatTime(time / 3600));
                        minutesTimer.setText(formatTime((time % 3600) / 60));
                        secondsTimer.setText(formatTime(time % 60));
                        if (time==0)
                            mediaPlayer.play();
                    });
                }
            }
        }
    }


}
