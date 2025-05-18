package views;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

public interface ViewsMethods {
	default ImageView loadImage(String path) {
		return new ImageView(new Image(getClass().getResourceAsStream(path)));
	}

	default Image convertToFxImage(BufferedImage bufferedImage) {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

	default public ImageView loadAnyImage(String url, double width, double height, Boolean preserveRatio, Boolean smooth) {
		try {
			Image img = new Image(url, width, height, preserveRatio, smooth);
			if (!img.isError())
				return new ImageView(img);
			BufferedImage buf = ImageIO.read(new File(new URI(url)));
			return new ImageView(convertToFxImage(buf));
		} catch (IOException | URISyntaxException e) {
			System.err.println("Load image failed");
			return null;
		}
	}

	default public ImageView loadAnyImage(String url) {
		try {
			Image img = new Image(url);
			if (!img.isError())
				return new ImageView(img);
			BufferedImage buf = ImageIO.read(new File(new URI(url)));
			return new ImageView(convertToFxImage(buf));
		} catch (IOException | URISyntaxException e) {
			System.err.println("Load image failed");
			return null;
		}
	}

	default public Button createIconButton(String iconPath, double size) {
		Button button = new Button();
		ImageView icon = loadImage(iconPath);
		icon.setFitWidth(size);
		icon.setFitHeight(size);
		button.setGraphic(icon);
		button.setStyle("-fx-background-color: transparent;");

		button.setOnMouseEntered(event -> button.setCursor(Cursor.HAND));
		button.setOnMouseExited(event -> button.setCursor(Cursor.DEFAULT));
		return button;
	}

	default public Font setFontPerso(String f, double d) {
		Font font = Font.loadFont(getClass().getResourceAsStream(f), d);
		return font;
	}
}
