package de.algorythm.jddb.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloController {
	private static final Logger log = LoggerFactory
			.getLogger(HelloController.class);

	@FXML
	private TextField firstNameField;
	@FXML
	private TextField lastNameField;
	@FXML
	private Label messageLabel;
	
	public void sayHello() {
		final String firstName = firstNameField.getText();
		final String lastName = lastNameField.getText();
		final StringBuilder builder = new StringBuilder();

		builder.append(firstName);

		if (builder.length() > 0)
			builder.append(' ');

		builder.append(lastName);

		if (builder.length() > 0) {
			final String name = builder.toString();
			
			log.debug("Saying hello to " + name);
			messageLabel.setText("Hello " + name);
		} else {
			log.debug("Neither first name nor last name was set, saying hello to anonymous person");
			messageLabel.setText("Hello mysterious person");
		}
	}
}