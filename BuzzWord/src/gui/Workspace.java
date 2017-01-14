package gui;

import apptemplate.AppTemplate;
import components.AppWorkspaceComponent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import propertymanager.PropertyManager;
import ui.AppGUI;

import static gui.BuzzWordProperties.*;

/**
 * @author Tejas
 */
public class Workspace extends AppWorkspaceComponent{

    private AppGUI gui;
    private AppTemplate appTemplate;
    private ApplicationStateHandler stateHandler;

    private VBox toolBar;
    private HBox headPane;
    private BorderPane gamePane;
    private Label guiHeadingLabel;

    public Workspace(AppTemplate appTemplate){
        this.appTemplate = appTemplate;
        gui = this.appTemplate.getGUI();
        toolBar = gui.getToolbarPane();
    }

    @Override
    public void layoutGUI(){
        PropertyManager propertyManager = PropertyManager.getManager();
        stateHandler = new ApplicationStateHandler(this.appTemplate);
        headPane = new HBox();
        guiHeadingLabel = new Label(propertyManager.getPropertyValue(WORKSPACE_HEADING_LABEL));

        headPane.getChildren().add(guiHeadingLabel);
        headPane.setAlignment(Pos.CENTER);

        gamePane = new BorderPane();

        workspace = new BorderPane();
        stateHandler.loadHomeScreen();
        ((BorderPane)workspace).setTop(headPane);
        ((BorderPane)workspace).setCenter(gamePane);
    }

    public VBox getToolBar(){
        return toolBar;
    }

    public ApplicationStateHandler getStateHandler(){
        return stateHandler;
    }

    public BorderPane getGamePane(){
        return gamePane;
    }

    public void resetGamePane(){
        gamePane = new BorderPane();
        ((BorderPane) workspace).setCenter(gamePane);
    }

    @Override
    public void initStyle() {
        PropertyManager propertyManager = PropertyManager.getManager();

        gui.getAppPane().setId(propertyManager.getPropertyValue(ROOT_BORDERPANE_ID));
        gui.getToolbarPane().getStyleClass().setAll(propertyManager.getPropertyValue(SEGMENTED_BUTTON_BAR));
        gui.getToolbarPane().setId(propertyManager.getPropertyValue(TOP_TOOLBAR_ID));

        workspace.getStyleClass().add(CLASS_BORDERED_PANE);
        guiHeadingLabel.getStyleClass().setAll(propertyManager.getPropertyValue(HEADING_LABEL));
    }

    @Override
    public void reloadWorkspace() {

    }
}
