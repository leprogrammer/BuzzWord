package ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.Optional;

/**
 * @author Tejas
 */
public class LoginDialogSingleton extends Stage{
	private static LoginDialogSingleton singleton = null;
	private Dialog loginDialog;
	private ButtonType loginButtonType;
	private Stage owner;
	private String username;
	private String password;

	public static LoginDialogSingleton getSingleton() {
		if (singleton == null)
			singleton = new LoginDialogSingleton();
		return singleton;
	}

	private LoginDialogSingleton() {
	}

	public void init(Stage owner, String buttonText){
		this.owner = owner;
		loginDialog = new Dialog();
		GridPane layoutFields = new GridPane();
		layoutFields.setHgap(15);
		layoutFields.setVgap(15);
		layoutFields.setPadding(new Insets(20, 100, 10, 10));

		TextField usernameField = new TextField();
		usernameField.setPromptText("Username");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");

		layoutFields.addRow(0, new Label("Username: "), usernameField);
		layoutFields.addRow(1, new Label("Password: "), passwordField);
		loginDialog.getDialogPane().setContent(layoutFields);

		loginButtonType = new ButtonType(buttonText, ButtonBar.ButtonData.OK_DONE);
		loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
		Node loginButton =  loginDialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		usernameField.textProperty().addListener((observable, oldValue, newValue) ->{
			loginButton.setDisable(newValue.trim().isEmpty());
		});
		loginDialog.setResultConverter(button -> {
			if(button == loginButtonType)
				return new Pair<>(usernameField.getText(), passwordField.getText());
			else
				return null;
		});

		Platform.runLater(() -> usernameField.requestFocus());
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);

	}

	public void showDialog(String title, String headerText, String buttonText){
		init(this.owner, buttonText);
		username = "";
		password = "";
		loginDialog.setHeaderText(headerText);
		loginDialog.setTitle(title);
		Optional<Pair> output = loginDialog.showAndWait();

		output.ifPresent(outputValues ->{
			username = ((String) outputValues.getKey());
			password = ((String) outputValues.getValue());
		});
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
