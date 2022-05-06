package com.example.wersja_3;

import com.jfoenix.controls.JFXSlider;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PlayerController{

    private String path;
    private boolean isPlaying = false;
    private boolean isMute = false;
    private double volumeBeforeMute = 0;

    public void changeToPlay() {
        Image play = new Image(Objects.requireNonNull(getClass().getResourceAsStream("play.png")));
        ImageView playIcon = new ImageView(play);
        play2Button.setGraphic(playIcon);
        isPlaying = false;
    }

    public void changeToPause() {
        Image pause = new Image(Objects.requireNonNull(getClass().getResourceAsStream("pause.png")));
        ImageView pauseIcon = new ImageView(pause);
        play2Button.setGraphic(pauseIcon);
        isPlaying = true;
    }

    public void changeToMute() {
        
    }

    public void changeToVolume() {
        
    }

    public String getTitle(String locationString) {
        String title = "";

        char[] array = locationString.toCharArray();
        int i = array.length - 5;
        while (array[i] != '/') {
            title = array[i] + title ;
            i--;
        }

        if (title.contains("%20")) {
            title = title.replace("%20", " ");
        }
        
        return title;
    }

    @FXML
    private Label actualTimeLabel;

    @FXML
    private Button addPlaylistButton;

    @FXML
    private Button back10Button;

    @FXML
    private Button computerSearchButton;

    @FXML
    private Label endTimeLabel;

    @FXML
    private JFXSlider equalizer128Slider;

    @FXML
    private JFXSlider equalizer16kSlider;

    @FXML
    private JFXSlider equalizer1kSlider;

    @FXML
    private JFXSlider equalizer256Slider;

    @FXML
    private JFXSlider equalizer2kSlider;

    @FXML
    private JFXSlider equalizer32Slider;

    @FXML
    private JFXSlider equalizer4kSlider;

    @FXML
    private JFXSlider equalizer64Slider;

    @FXML
    private JFXSlider equalizer512Slider;

    @FXML
    private JFXSlider equalizer8kSlider;

    @FXML
    private Button forward10Button;

    @FXML
    private Button internetSearchButton;

    @FXML
    private Button play2Button;

    @FXML
    private TextField linkTextField;

    @FXML
    private Button muteButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button previousSongButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button repeatButton;

    @FXML
    private Button resetEqualizerButton;

    @FXML
    private Label titleLabel;

    @FXML
    private Label valueEqualizer128Label;

    @FXML
    private Label valueEqualizer16kLabel;

    @FXML
    private Label valueEqualizer1kLabel;

    @FXML
    private Label valueEqualizer256Label;

    @FXML
    private Label valueEqualizer2kLabel;

    @FXML
    private Label valueEqualizer32Label;

    @FXML
    private Label valueEqualizer4kLabel;

    @FXML
    private Label valueEqualizer64Label;

    @FXML
    private Label valueEqualizer512Label;

    @FXML
    private Label valueEqualizer8kLabel;

    @FXML
    private JFXSlider volumeSlider;

    @FXML
    void addPlaylistMethod(ActionEvent event) {
        System.out.println("test");
    }

    @FXML
    void back10Method(ActionEvent event) {
        System.out.println("test");
    }

    @FXML
    void fileSearchMethod(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a .wav file", "*.wav");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(null);

        try {
            path = file.toURI().toString();
        } catch (Exception w) {
            System.out.println("Ściezka nie moze byc pusta.");
        }

        if (path != null){
            titleLabel.setText(getTitle(path));
            List<Double> amplifying = new ArrayList<>(List.of(equalizer32Slider.getValue(), equalizer64Slider.getValue(),
                    equalizer128Slider.getValue(), equalizer256Slider.getValue(),
                    equalizer512Slider.getValue(), equalizer1kSlider.getValue(),
                    equalizer2kSlider.getValue(), equalizer4kSlider.getValue(),
                    equalizer8kSlider.getValue(), equalizer16kSlider.getValue()));

            Player bartender = null;
            try {
                bartender = new Player(path.substring(6), amplifying, (int)volumeSlider.getValue());
            } catch (UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }
            Equalizer spy = bartender.getDj();
            int minutes = (int) Math.floor(spy.nbSamples/(spy.sampleReader.getFormat().getSampleRate() * 120));
            int seconds = (int) ((spy.nbSamples/(spy.sampleReader.getFormat().getSampleRate()))/2 - minutes*60);
            if (seconds < 10) {
                endTimeLabel.setText(minutes + ":0" + seconds);
            } else {
                String test = minutes + ":" + seconds;
                endTimeLabel.setText(test);
            }
        } else {
            System.out.println("Musisz wybrac plik.");
        }
    }

    @FXML
    void forward10Method(ActionEvent event) {
        System.out.println("test");
    }

    @FXML
    void linkSearchMetod(ActionEvent event) {
        path = linkTextField.getText();
        System.out.println(path);
        if (path != null) {
            /*
            Kod do odtwarzania muzyki
             */
            changeToPause();
        } else {
            System.out.println("Musisz podać link.");
        }
    }

    @FXML
    void muteMethod(ActionEvent event) {
        if (isMute) {
            changeToVolume();
        } else {
            changeToMute();
        }
    }

    @FXML
    void nextSongMethod(ActionEvent event) {
        System.out.println("test");
    }

    @FXML
    void playMethod(ActionEvent event) {
        if (isPlaying) {
            changeToPlay();
        } else {
            changeToPause();
        }
        List<Double> amplifying = new ArrayList<>(List.of(equalizer32Slider.getValue(), equalizer64Slider.getValue(),
                equalizer128Slider.getValue(), equalizer256Slider.getValue(),
                equalizer512Slider.getValue(), equalizer1kSlider.getValue(),
                equalizer2kSlider.getValue(), equalizer4kSlider.getValue(),
                equalizer8kSlider.getValue(), equalizer16kSlider.getValue()));
        path = path.replace("%20", " ");
        Player bartender = null;
        try {
            bartender = new Player(path.substring(6), amplifying, (int)volumeSlider.getValue());
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        Equalizer spy = bartender.getDj();
        int minutes = (int) Math.floor(spy.nbSamples/(spy.sampleReader.getFormat().getSampleRate() * 120));
        int seconds = (int) ((spy.nbSamples/(spy.sampleReader.getFormat().getSampleRate()))/2 - minutes*60);
        if (seconds < 10) {
            endTimeLabel.setText(minutes + ":0" + seconds);
        } else {
            String test = minutes + ":" + seconds;
            endTimeLabel.setText(test);
        }
        bartender.play();
    }

    @FXML
    void previousSongMethod(ActionEvent event) {
        System.out.println("test");
    }

    @FXML
    void repeatMethod(ActionEvent event) {
        System.out.println("test");
    }

    @FXML
    void resetEqualizerMethod(ActionEvent event) {
        JFXSlider[] namesOfSliders = {equalizer32Slider, equalizer64Slider, equalizer128Slider, equalizer256Slider,
                equalizer512Slider, equalizer1kSlider, equalizer2kSlider, equalizer4kSlider,
                equalizer8kSlider, equalizer16kSlider};

        Label[] equalizerLabelValues = {valueEqualizer32Label, valueEqualizer64Label, valueEqualizer128Label, valueEqualizer256Label,
                valueEqualizer512Label, valueEqualizer1kLabel, valueEqualizer2kLabel, valueEqualizer4kLabel,
                valueEqualizer8kLabel, valueEqualizer16kLabel};

    }

    @FXML
    void sliderValueMethod(MouseEvent event){
        JFXSlider[] namesOfSliders = {equalizer32Slider, equalizer64Slider, equalizer128Slider, equalizer256Slider,
                equalizer512Slider, equalizer1kSlider, equalizer2kSlider, equalizer4kSlider,
                equalizer8kSlider, equalizer16kSlider};

        Label[] equalizerLabelValues = {valueEqualizer32Label, valueEqualizer64Label, valueEqualizer128Label, valueEqualizer256Label,
                valueEqualizer512Label, valueEqualizer1kLabel, valueEqualizer2kLabel, valueEqualizer4kLabel,
                valueEqualizer8kLabel, valueEqualizer16kLabel};

        String source = event.getSource().toString();
        source = source.substring(13);
        char[] array = source.toCharArray();
        String id = "";
        int i = 0;
        while (array[i] != ',') {
            id = id + array[i];
            i++;
        }

        for (int z = 0; z < namesOfSliders.length; z++) {
            if (id.equals(namesOfSliders[z].getId())) {
                double value = namesOfSliders[z].getValue();
                namesOfSliders[z].setValueFactory(slider -> Bindings.createStringBinding(
                                        () -> (String.format("%.1f", (double) slider.getValue())),
                                        slider.valueProperty()
                                ));
                String roundedValued = String.format("%.1f", value);
                equalizerLabelValues[z].setText(roundedValued);
            }
        }
    }


}
