package sample.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ResourceBundle;

import static sample.controller.GameController.base;

/**
 * ThemeController
 * setup theme view
 * opens file chooser pop up
 * preview chosen images
 */
public class ThemeController extends BaseController {
    public TextArea themeText;
    final FileChooser fileChooser = new FileChooser();
    public HBox imageContainer;


    public void onLoadButtonClicked(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Scene scene = source.getScene();
        String theme = themeText.getText(); // the Name of the Theme

        if (!theme.isEmpty()) { // meaning that the user input something as a name
            String dirPath = getClass().getResource(GameController.base).getPath() + "/" + theme; // we create the right path
            fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Images", "jpg", "png", "bmp")); // we can add jpg/png/bmp extension
            List<File> list = fileChooser.showOpenMultipleDialog(scene.getWindow());

            if (list != null) {
                // make dir only if at least one image is choosen
                new File(dirPath).mkdir();

                for (File file : list) {
                    // get file relative path
                    String relativeFilePath = getClass().getResource(GameController.base).getPath() + "/" + theme + "/" + file.getName();
                    // if file already exit do nothing
                    if (new File(relativeFilePath).exists())
                        continue;
                    try {
                        byte[] bytes = readImage(file.getAbsolutePath());
                        writeImage(theme, file.getName(), bytes);
                        displayImage(theme, file.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * display image after choosing them
     * @param theme
     * @param fileName
     */
    private void displayImage(String theme, String fileName) {
        String folder = GameController.base + "/" + theme;
        Image image = new Image(folder + "/" + fileName);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitHeight(imageContainer.getPrefHeight());
        imageView.setPreserveRatio(true);
        imageContainer.getChildren().add(imageView);
    }

    /**
     * Read all the byte from the file specified
     * @param fileName
     * @return byte array of the file
     * @throws IOException
     */
    private byte[] readImage(String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(fileName));
    }

    /**
     * write all the byte from the given array to a specific theme folder destination in a file
     * @param theme
     * @param fileName
     * @param bytes
     * @throws IOException
     */
    private void writeImage(String theme, String fileName, byte[] bytes) throws IOException {
        String folder = GameController.base + "/" + theme;
        URL path = getClass().getResource(folder);
        File file = new File(path.getPath());
        String filenamePath = file.getAbsolutePath() + "/" + fileName;
        Files.write(Paths.get(filenamePath), bytes, StandardOpenOption.CREATE);
    }

    public void onExitPressed(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Scene scene = source.getScene();
        changeView(scene, menuView);
    }
}
