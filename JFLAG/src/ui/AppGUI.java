package ui;

import apptemplate.AppTemplate;
import components.AppStyleArbiter;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import propertymanager.PropertyManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static settings.AppPropertyType.*;
import static settings.InitializationParameters.APP_IMAGEDIR_PATH;

/**
 * This class provides the basic user interface for this application, including all the file controls, but it does not
 * include the workspace, which should be customizable and application dependent.
 *
 * @author Richard McKenna, Ritwik Banerjee
 * @author Tejas Prasad
 */
public class AppGUI implements AppStyleArbiter {

    protected Stage          primaryStage;     // the application window
    protected Scene          primaryScene;     // the scene graph
    protected BorderPane     appPane;          // the root node in the scene graph, to organize the containers
    protected VBox           toolbarPane;      // the top toolbar
    protected String         applicationTitle; // the application title

    private int appSpecificWindowWidth;  // optional parameter for window width that can be set by the application
    private int appSpecificWindowHeight; // optional parameter for window height that can be set by the application
    
    /**
     * This constructor initializes the file toolbar for use.
     *
     * @param initPrimaryStage The window for this application.
     * @param initAppTitle     The title of this application, which
     *                         will appear in the window bar.
     * @param app              The app within this gui is used.
     */
    public AppGUI(Stage initPrimaryStage, String initAppTitle, AppTemplate app) throws IOException, InstantiationException {
        this(initPrimaryStage, initAppTitle, app, -1, -1);
    }

    public AppGUI(Stage primaryStage, String applicationTitle, AppTemplate appTemplate, int appSpecificWindowWidth, int appSpecificWindowHeight) throws IOException, InstantiationException {
        this.appSpecificWindowWidth = appSpecificWindowWidth;
        this.appSpecificWindowHeight = appSpecificWindowHeight;
        this.primaryStage = primaryStage;
        this.applicationTitle = applicationTitle;
        toolbarPane = new VBox();
        initializeWindow();                     // start the app window (without the application-specific workspace)

    }

    public VBox getToolbarPane() { return toolbarPane; }

    public BorderPane getAppPane() { return appPane; }
    
    /**
     * Accessor method for getting this application's primary stage's,
     * scene.
     *
     * @return This application's window's scene.
     */
    public Scene getPrimaryScene() { return primaryScene; }
    
    /**
     * Accessor method for getting this application's window,
     * which is the primary stage within which the full GUI will be placed.
     *
     * @return This application's primary stage (i.e. window).
     */
    public Stage getWindow() { return primaryStage; }

    /****************************************************************************/
    /* BELOW ARE ALL THE PRIVATE HELPER METHODS WE USE FOR INITIALIZING OUR AppGUI */
    /****************************************************************************/


    // INITIALIZE THE WINDOW (i.e. STAGE) PUTTING ALL THE CONTROLS
    // THERE EXCEPT THE WORKSPACE, WHICH WILL BE ADDED THE FIRST
    // TIME A NEW Page IS CREATED OR LOADED
    private void initializeWindow() throws IOException {
        PropertyManager propertyManager = PropertyManager.getManager();


        // SET THE WINDOW TITLE
        primaryStage.setTitle(applicationTitle);

        // GET THE SIZE OF THE SCREEN
        Screen      screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        // AND USE IT TO SIZE THE WINDOW
        primaryStage.setMinHeight(Integer.parseInt(propertyManager.getPropertyValue(MIN_APP_WINDOW_HEIGHT)));
        primaryStage.setMinWidth(Integer.parseInt(propertyManager.getPropertyValue(MIN_APP_WINDOW_WIDTH)));

        // ADD THE TOOLBAR ONLY, NOTE THAT THE WORKSPACE
        // HAS BEEN CONSTRUCTED, BUT WON'T BE ADDED UNTIL
        // THE USER STARTS EDITING A COURSE
        appPane = new BorderPane();
        appPane.setLeft(toolbarPane);
        primaryScene = appSpecificWindowWidth < 1 || appSpecificWindowHeight < 1 ? new Scene(appPane)
                                                                                 : new Scene(appPane,
                                                                                             appSpecificWindowWidth,
                                                                                             appSpecificWindowHeight);

        URL imgDirURL = AppTemplate.class.getClassLoader().getResource(APP_IMAGEDIR_PATH.getParameter());
        if (imgDirURL == null)
            throw new FileNotFoundException("Image resources folder does not exist.");
        try (InputStream appLogoStream = Files.newInputStream(Paths.get(imgDirURL.toURI()).resolve(propertyManager.getPropertyValue(APP_LOGO)))) {
            primaryStage.getIcons().add(new Image(appLogoStream));
        } catch (URISyntaxException e) {
            AppMessageDialogSingleton message = AppMessageDialogSingleton.getSingleton();
            message.show(propertyManager.getPropertyValue(STARTUP_ERROR_TITLE), propertyManager.getPropertyValue(STARTUP_ERROR_MESSAGE)+ "\n"+ e.getMessage() + "\nThe application will now close.");
            System.exit(1);
        }

        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }
    
    /**
     * This is a public helper method for initializing a simple button with
     * an icon and tooltip and placing it into a toolbar.
     *
     * @param toolbarPane Toolbar pane into which to place this button.
     * @param text        Text to appear on button.
     * @param tooltip     Tooltip to appear when the user mouses over the button.
     * @param disabled    true if the button is to start off disabled, false otherwise.
     * @return A constructed, fully initialized button placed into its appropriate
     * pane container.
     */
    public Button initializeChildButton(Pane toolbarPane, String text, String tooltip, boolean disabled){
        PropertyManager propertyManager = PropertyManager.getManager();

        Button button = new Button();
        button.setDisable(disabled);
        button.setText(text);
        Tooltip buttonTooltip = new Tooltip(propertyManager.getPropertyValue(tooltip));
        button.setTooltip(buttonTooltip);
        button.setMaxWidth(Double.MAX_VALUE);
        toolbarPane.getChildren().add(button);

        return button;
    }

    /**
     * This is a public helper method for initializing a simple button with
     * an icon and tooltip and placing it into a toolbar.
     *
     * @param toolbarPane Toolbar pane into which to place this button.
     * @param icon        Icon image file name for the button.
     * @param tooltip     Tooltip to appear when the user mouses over the button.
     * @param disabled    true if the button is to start off disabled, false otherwise.
     * @return A constructed, fully initialized button placed into its appropriate
     * pane container.
     */
    public Button initializeChildButtonWithIcon(Pane toolbarPane, String icon, String tooltip, boolean disabled) throws IOException {
        PropertyManager propertyManager = PropertyManager.getManager();

        URL imgDirURL = AppTemplate.class.getClassLoader().getResource(APP_IMAGEDIR_PATH.getParameter());
        if (imgDirURL == null)
            throw new FileNotFoundException("Image resources folder does not exist.");

        Button button = new Button();
        try (InputStream imgInputStream = Files.newInputStream(Paths.get(imgDirURL.toURI()).resolve(propertyManager.getPropertyValue(icon)))) {
            Image buttonImage = new Image(imgInputStream);
            button.setDisable(disabled);
            button.setGraphic(new ImageView(buttonImage));
            Tooltip buttonTooltip = new Tooltip(propertyManager.getPropertyValue(tooltip));
            button.setTooltip(buttonTooltip);
            toolbarPane.getChildren().add(button);
        } catch (URISyntaxException e) {
            AppMessageDialogSingleton message = AppMessageDialogSingleton.getSingleton();
            message.show(propertyManager.getPropertyValue(STARTUP_ERROR_TITLE), propertyManager.getPropertyValue(STARTUP_ERROR_MESSAGE)+ "\n"+ e.getMessage() + "\nThe application will now close.");
            System.exit(1);
        }

        return button;
    }

    /**
     * This function specifies the CSS style classes for the controls managed
     * by this framework.
     */
    @Override
    public void initStyle() {
        // currently, we do not provide any stylization at the framework-level
    }
}
