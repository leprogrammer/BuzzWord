package settings;

/**
 * This enum provides properties that are to be loaded via
 * XML files to be used for setting up the application.
 *
 * @author Richard McKenna, Ritwik Banerjee
 * @author Tejas Prasad
 * @version 1.0
 */
@SuppressWarnings("unused")
public enum AppPropertyType {

    // from app-properties.xml
    APP_WINDOW_WIDTH,
    APP_WINDOW_HEIGHT,
    MIN_APP_WINDOW_WIDTH,
    MIN_APP_WINDOW_HEIGHT,
    APP_TITLE,
    APP_LOGO,
    APP_CSS,
    APP_PATH_CSS,
    GAME_DATA_PATH,
    SAVE_ICON,

    // APPLICATION TOOLTIPS FOR BUTTONS
    NEW_TOOLTIP,
    SAVE_TOOLTIP,
    LOAD_TOOLTIP,
    EXIT_TOOLTIP,

    // ERROR MESSAGES
    NEW_ERROR_MESSAGE,
    SAVE_ERROR_MESSAGE,
    LOAD_ERROR_MESSAGE,
    STARTUP_ERROR_MESSAGE,
    PROPERTIES_LOAD_ERROR_MESSAGE,

    // ERROR TITLES
    NEW_ERROR_TITLE,
    SAVE_ERROR_TITLE,
    LOAD_ERROR_TITLE,
    STARTUP_ERROR_TITLE,
    PROPERTIES_LOAD_ERROR_TITLE,

    // AND VERIFICATION MESSAGES AND TITLES
    NEW_COMPLETED_MESSAGE,
    NEW_COMPLETED_TITLE,
    SAVE_COMPLETED_MESSAGE,
    SAVE_COMPLETED_TITLE,
	LOAD_COMPLETED_TITLE,
	LOAD_COMPLETED_MESSAGE,
    SAVE_UNSAVED_WORK_TITLE,
    SAVE_UNSAVED_WORK_MESSAGE,

    SAVE_WORK_TITLE,
    LOAD_WORK_TITLE,
    WORK_FILE_EXT,
    WORK_FILE_EXT_DESC,
    PROPERTIES_
}
