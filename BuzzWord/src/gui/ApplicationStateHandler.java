package gui;

import apptemplate.AppTemplate;
import controller.BuzzWordController;
import data.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import propertymanager.PropertyManager;
import ui.AppMessageDialogSingleton;
import ui.LoginDialogSingleton;
import ui.YesNoCancelDialogSingleton;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static gui.BuzzWordProperties.*;
import static settings.AppPropertyType.LOAD_ERROR_MESSAGE;
import static settings.AppPropertyType.LOAD_ERROR_TITLE;

/**
 * @author Tejas
 */
public class ApplicationStateHandler {
	private AccountData account;
	private AccountLoader accountLoader;
	private AppTemplate appTemplate;
	private BuzzWordGraphics graphics;
	private BuzzWordController controller;
	private GameData gameData;
	private GameState gameState;
	private GameMode gameMode = null;
	private boolean loggedin;

	private Button createAccount;
	private Button accountPage;
	private Button login;
	private Button[] levelSelect;
	private MenuButton gameModeSelector;
	private MenuItem dictionaryMode;
	private MenuItem namesMode;
	private MenuItem animalMode;
	private MenuItem placesMode;
	private Button help;
	private Button playGame;
	private Button returnHome;
	private Button logout;
	private Button editAccount;
	private Button changePassword;
	private VBox toolBar;
	private BorderPane screenPane;

    public ApplicationStateHandler(AppTemplate appTemplate){
		this.appTemplate = appTemplate;
		graphics = new BuzzWordGraphics(this.appTemplate);
		toolBar = ((Workspace)appTemplate.getWorkspaceComponent()).getToolBar();
		accountLoader = new AccountLoader();
		gameData = GameData.getSingleton();
		controller = new BuzzWordController(ApplicationStateHandler.this);
		initializeButtons();
		setupHandlers();
		loggedin = false;
		gameState = GameState.UNLOGGEDIN_HOME;
		displayToolbar(gameState);
    }

    public void loadHomeScreen(){
    	setupShortCuts();
		((Workspace) appTemplate.getWorkspaceComponent()).resetGamePane();
		screenPane = ((Workspace) appTemplate.getWorkspaceComponent()).getGamePane();

		char[] letters = new char[16];
		for (int i = 0, k = 'a'; i < 16; i++, k++) {
			letters[i] = 0;
		}
		letters[0] = 'B';
		letters[1] = 'U';
		letters[4] = 'Z';
		letters[5] = 'Z';
		letters[10] = 'W';
		letters[11] = 'O';
		letters[14] = 'R';
		letters[15] = 'D';

		GridPane letterTiles = new GridPane();
		letterTiles.setPadding(new Insets(0, 50, 0, 50));
		letterTiles.setAlignment(Pos.CENTER);
		letterTiles.setVgap(20);
		letterTiles.setHgap(20);

		for (int i = 0, a = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++, a++) {
				StackPane letterCircle = new StackPane();
				Circle circle = new Circle(50, Color.WHITE);
				circle.setStroke(Color.BLACK);
				Text text = new Text(Character.toString(letters[a]));
				text.setFont(Font.font(15));
				if(letters[a] > 0) {
					circle.setStroke(Color.GREEN);
					circle.setEffect(new DropShadow(30, Color.FORESTGREEN));
				}
				letterCircle.getChildren().addAll(circle, text);
				letterTiles.add(letterCircle, j, i);
			}
		}

		screenPane.setCenter(letterTiles);

		if(loggedin){
			gameState = GameState.LOGGEDIN_HOME;
			displayToolbar(gameState);

			HBox playGameButton = new HBox();
			playGameButton.setPadding(new Insets(0, 0, 20, 0));
			playGameButton.setAlignment(Pos.CENTER);
			playGameButton.getChildren().add(playGame);
			screenPane.setBottom(playGameButton);
		}
		else{
			gameState = GameState.UNLOGGEDIN_HOME;
			displayToolbar(gameState);
		}
	}

	public void loadLevelSelectScreen(){
		gameState = GameState.LEVEL;
		displayToolbar(gameState);
		((Workspace) appTemplate.getWorkspaceComponent()).resetGamePane();
		screenPane = ((Workspace) appTemplate.getWorkspaceComponent()).getGamePane();
		if(gameMode == null){
			HBox messagePane = new HBox();
			Label noSelection = new Label("Please select a game mode first to then choose a level.");
			messagePane.setAlignment(Pos.CENTER);
			messagePane.getChildren().add(noSelection);
			screenPane.setCenter(messagePane);
		}
		else {
			HBox gameStateModePane = new HBox();
			Label gameStateLabel = new Label("Selected Game Mode: " + gameMode.toString());
			gameStateLabel.setFont(new Font(30));
			gameStateModePane.setAlignment(Pos.CENTER);
			gameStateModePane.getChildren().add(gameStateLabel);
			GridPane levelTiles = new GridPane();
			levelTiles.setPadding(new Insets(0, 50, 0, 50));
			levelTiles.setAlignment(Pos.CENTER);
			levelTiles.setVgap(20);
			levelTiles.setHgap(20);

			for (int i = 0; i < 8; i++) {
				levelSelect[i].setDisable(account.getHighestLevel(gameMode) < (i + 1));
				levelTiles.add(levelSelect[i], i % 4, i / 4);
			}
			screenPane.setTop(gameStateModePane);
			screenPane.setCenter(levelTiles);
		}
	}

	public void loadAccountScreen(){
		gameState = GameState.ACCOUNT;
		((Workspace) appTemplate.getWorkspaceComponent()).resetGamePane();
		screenPane = ((Workspace) appTemplate.getWorkspaceComponent()).getGamePane();
		displayToolbar(gameState);

		HBox accountHeaderLabel = new HBox();
		Label usernameField = new Label(account.getUsername() + "'s Account Page");
		usernameField.setFont(new Font(30));
		accountHeaderLabel.setAlignment(Pos.CENTER);
		accountHeaderLabel.getChildren().add(usernameField);

		GridPane accountDetails = new GridPane();
		accountDetails.setPadding(new Insets(0, 50, 0, 50));
		accountDetails.setVgap(20);
		accountDetails.setHgap(10);

		Label modesLabel = new Label("Game Modes");
		Label dictionaryLabel = new Label("Dictionary");
		Label animalLabel = new Label("Animal");
		Label namesLabel = new Label("Names");
		Label placesLabel = new Label("Places");
		modesLabel.setFont(new Font(25));
		dictionaryLabel.setFont(new Font(20));
		animalLabel.setFont(new Font(20));
		namesLabel.setFont(new Font(20));
		placesLabel.setFont(new Font(20));

		accountDetails.add(modesLabel, 0, 0);
		accountDetails.add(dictionaryLabel, 0, 1);
		accountDetails.add(namesLabel, 0, 4);
		accountDetails.add(animalLabel, 0, 7);
		accountDetails.add(placesLabel, 0, 10);
		int[][] scores = new int[4][8];
		for(int mode = 0; mode < 4; mode++){
			for(int modeLevel = 0; modeLevel < 8; modeLevel++){
				scores[mode][modeLevel] = new Random().nextInt(100);
			}
		}

		for(int mode = 0; mode < 4; mode++){
			for(int modeLevel = 0; modeLevel < 8; modeLevel++){
				Label scoreDetails = new Label("LVL" + (modeLevel + 1) + " High Score: " + ((account.getHighScore(mode, modeLevel + 1) < 0) ? "Level Locked" : account.getHighScore(mode, modeLevel + 1)));
				scoreDetails.setFont(new Font(15));
				accountDetails.add(scoreDetails, modeLevel % 4, (((modeLevel / 4) + 2) + (mode * 3)));
			}
		}

		HBox logoutButton = new HBox();
		logoutButton.setPadding(new Insets(0, 0, 20, 0));
		logoutButton.setAlignment(Pos.CENTER);
		logoutButton.getChildren().addAll(editAccount, logout);

		screenPane.setTop(accountHeaderLabel);
		screenPane.setCenter(accountDetails);
		screenPane.setBottom(logoutButton);
	}

	public void loadGameScreen(){
		try {
			gameState = GameState.GAME;
			((Workspace) appTemplate.getWorkspaceComponent()).resetGamePane();
			screenPane = ((Workspace) appTemplate.getWorkspaceComponent()).getGamePane();
			displayToolbar(gameState);

			graphics.renderGameScreen(screenPane, gameMode, gameData.getLevel());
			controller.setupHandlers();
		}
		catch(IOException e){
			AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
			PropertyManager properties = PropertyManager.getManager();
			dialog.show(properties.getPropertyValue(LOAD_ERROR_TITLE), properties.getPropertyValue(LOAD_ERROR_MESSAGE) + "\n"+ e.getMessage());
		}
	}

	public void loadHelpScreen(){
		gameState = GameState.HELP;
		((Workspace) appTemplate.getWorkspaceComponent()).resetGamePane();
		screenPane = ((Workspace) appTemplate.getWorkspaceComponent()).getGamePane();
		displayToolbar(gameState);

		FlowPane helpScreen = new FlowPane();
		helpScreen.getChildren().add(new Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer non diam ac tellus consequat vehicula a eu nisi. Vivamus sit amet faucibus arcu, in consectetur mi. Maecenas a ultrices elit. Praesent tristique est nunc. Quisque pulvinar justo nec enim venenatis aliquet. Donec vitae diam ut arcu aliquet auctor. Aliquam erat volutpat. Sed vel mi in lectus dapibus aliquam eu eget tortor. Nam feugiat, purus in auctor tincidunt, nisi ipsum vehicula metus, eu volutpat leo ante ut nunc. Integer aliquet dui sed suscipit efficitur. Nulla laoreet ut velit ac scelerisque. Phasellus eget arcu elit. Aliquam ullamcorper, nulla eu facilisis sollicitudin, urna ligula tristique ligula, ut molestie ligula tellus eu felis. In hac habitasse platea dictumst. Ut malesuada dui mauris, vitae facilisis ex placerat in.\n" +
				"\n" +
				"Donec vehicula diam eu tellus faucibus, in consequat purus bibendum. Suspendisse non sollicitudin risus. Etiam eu dui quis diam lobortis consequat id eget lectus. Nam maximus efficitur molestie. Duis sagittis sagittis nibh, vitae blandit magna aliquam eget. Nullam mollis vitae enim et egestas. Sed aliquet quis quam in mollis. Aliquam facilisis elit arcu, et malesuada lorem semper vitae. Proin ut vulputate nulla. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Etiam pretium arcu a erat dapibus, nec fringilla orci finibus. Nulla facilisi.\n" +
				"\n" +
				"In hac habitasse platea dictumst. Nunc gravida ante lorem, vitae placerat leo sagittis a. Phasellus eget tincidunt tortor, id finibus nisi. In bibendum ex ut finibus pulvinar. Sed pulvinar pellentesque ante sed luctus. In non fermentum metus. Aenean neque est, pretium eget pulvinar sit amet, ornare id ex. Integer vitae ex ut lectus sodales pharetra. Etiam auctor enim non aliquet egestas.\n" +
				"\n" +
				"Etiam ullamcorper augue arcu, vel egestas libero commodo eget. Aliquam erat volutpat. Nulla eu est sagittis arcu tristique accumsan quis a enim. Aenean ut odio in dui ornare rutrum ac a lectus. Aenean eget auctor tellus. Praesent et ante sagittis, euismod mi consequat, auctor justo. Nulla sollicitudin pellentesque quam, a dapibus velit iaculis ac. Duis at dolor leo.\n" +
				"\n" +
				"Mauris vehicula elit nibh, id ultrices magna commodo vel. Morbi blandit consectetur imperdiet. Quisque eu turpis quis nibh condimentum tempus ut eu ex. Praesent sed erat consectetur, tempus lacus a, laoreet augue. Nunc ut tortor purus. Nam sed erat quis orci molestie iaculis a eget est. Aenean auctor, turpis vitae condimentum porta, lacus dolor dictum lectus, sed scelerisque mauris turpis eget tortor. Nunc volutpat at diam ut eleifend. Sed tempus congue enim, vel sodales dui mattis nec. Fusce et dolor nisi. Sed consequat risus ac sem vestibulum, at tempus neque volutpat. Fusce quam quam, mollis id urna eget, vestibulum gravida eros. Integer at eros tempor, laoreet lorem ac, consectetur ligula. Ut eu urna a est sollicitudin hendrerit id et enim. Suspendisse et dolor tincidunt, malesuada nibh id, dictum urna.\n" +
				"\n" +
				"Duis sodales, risus at lacinia vestibulum, felis nunc eleifend eros, eget pulvinar augue dolor vitae velit. Fusce non commodo turpis. Integer ut turpis at enim fermentum mattis in vitae nisl. Proin in magna tristique, malesuada arcu nec, sagittis justo. Maecenas cursus tellus vel velit fermentum, eu tristique augue efficitur. Mauris sed lacus et risus ultricies dignissim. Nunc mattis metus ornare velit rutrum, sed gravida leo elementum. Etiam porta vitae magna consectetur placerat. Vivamus vestibulum pretium purus, iaculis sollicitudin purus.\n" +
				"\n" +
				"Nullam pulvinar lacus ac lorem ullamcorper tristique. Nulla facilisi. Nunc viverra elit non orci ultrices accumsan. Etiam euismod velit sit amet finibus accumsan. Sed hendrerit faucibus suscipit. In hac habitasse platea dictumst. Interdum et malesuada fames ac ante ipsum primis in faucibus. Nunc ut augue malesuada, faucibus justo vitae, luctus risus. Duis sed fermentum leo. Proin mattis euismod lorem vestibulum maximus. Pellentesque a turpis nec felis elementum ultrices.\n" +
				"\n" +
				"Sed vel blandit lacus. Fusce viverra faucibus diam, laoreet auctor massa convallis a. Morbi purus est, malesuada vel leo a, tincidunt sodales massa. Curabitur diam turpis, pellentesque eget odio non, laoreet tincidunt purus. Pellentesque eleifend neque a tortor hendrerit facilisis. Fusce nec justo odio. Pellentesque vestibulum molestie augue, ac porttitor ligula.\n" +
				"\n" +
				"Praesent eget purus id sem commodo tempor ac ac turpis. Vivamus diam purus, aliquet ultricies velit ut, condimentum malesuada ipsum. Cras id dolor in risus efficitur maximus. Cras at pretium arcu. Sed ultricies ligula at purus tincidunt iaculis. Nullam viverra vel tortor a aliquam. Maecenas vel orci a purus interdum elementum eu nec nisl. Morbi pretium gravida erat, vitae bibendum elit varius quis.\n" +
				"\n" +
				"Sed quis mauris id nibh semper viverra nec a neque. Donec varius ac lectus quis feugiat. Ut vitae felis varius, maximus risus non, pellentesque ante. Morbi viverra consectetur porta. In mauris tellus, dictum sit amet condimentum porta, tempor et turpis. Pellentesque ut massa non neque accumsan congue. Morbi ac erat lorem. Suspendisse a mauris purus.\n" +
				"\n" +
				"Maecenas posuere velit lectus. Maecenas vestibulum libero ac massa placerat elementum. In orci mauris, auctor vitae nulla eget, eleifend tempus quam. Quisque sodales dolor sapien. Duis porta tellus nibh, vel ultrices quam fermentum ac. Praesent malesuada elementum nisi a feugiat. In hac habitasse platea dictumst. Nam ut nulla in enim molestie tristique. Praesent ex lorem, eleifend quis mattis quis, porta interdum erat. Mauris auctor odio lacus, ut vulputate quam ullamcorper et. Quisque ut mollis augue. Praesent placerat nunc ligula, at tempor libero suscipit ut. Morbi consectetur rutrum ex pharetra ornare. Phasellus et ligula metus. Duis enim magna, suscipit nec nisi eu, congue ornare diam. Praesent dui justo, volutpat sit amet augue quis, ultricies suscipit massa.\n" +
				"\n" +
				"Sed eleifend dui sit amet libero molestie, sit amet eleifend dui elementum. Maecenas consequat magna id dictum efficitur. Phasellus a ultrices mi. Praesent sed nisi efficitur, aliquam dui accumsan, efficitur mi. Praesent convallis aliquet auctor. Curabitur placerat congue tortor sit amet laoreet. Vestibulum gravida sit amet justo at imperdiet. Cras nec posuere quam, et auctor purus. Cras id faucibus neque. Etiam et condimentum lacus. Ut ut ultricies libero, id hendrerit nunc.\n" +
				"\n" +
				"Cras euismod finibus suscipit. Mauris id elementum tortor, sed cursus enim. Donec egestas enim finibus mattis pharetra. Cras sit amet libero mollis, viverra ex vitae, scelerisque mi. Maecenas non diam eget quam lacinia malesuada. Sed vehicula feugiat neque sit amet congue. Donec sit amet sagittis ante, eget vestibulum metus. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nam in tortor ut mi pharetra pharetra. Duis venenatis tincidunt metus, consequat efficitur risus accumsan eu. Sed sollicitudin efficitur facilisis.\n" +
				"\n" +
				"Maecenas a orci quis enim rhoncus dictum. Vestibulum pharetra, libero vel eleifend mollis, est massa tristique felis, sit amet placerat diam lorem in massa. Donec faucibus mi ligula, quis consectetur neque rhoncus et. Suspendisse malesuada leo eu tempor efficitur. Curabitur tempus bibendum vehicula. Sed luctus mi lorem, id rutrum lorem ultricies eu. Proin mi quam, scelerisque lacinia nisi non, eleifend blandit eros.\n" +
				"\n" +
				"Donec quis risus dignissim, tristique eros ut, rutrum leo. Duis ac condimentum nisi. Quisque scelerisque, risus eget vulputate ornare, mauris ante accumsan lectus, id condimentum ante felis at urna. Aliquam cursus purus at purus aliquam hendrerit. Vestibulum luctus felis vitae facilisis faucibus. Suspendisse ac mollis erat. Suspendisse bibendum metus in congue dictum. Vivamus gravida vel augue sed volutpat. Sed malesuada, lorem vitae lacinia convallis, ante nibh condimentum massa, id lobortis orci ligula ut urna. Aenean eros magna, aliquam consequat sapien ac, dignissim rutrum est. Aliquam non est massa.\n" +
				"\n" +
				"Morbi mollis, urna at hendrerit posuere, tellus turpis blandit neque, id mattis ante eros mattis est. Cras at consectetur lacus. Vivamus faucibus faucibus sem, a lobortis enim vestibulum a. Suspendisse feugiat ullamcorper tortor, tincidunt maximus felis efficitur eget. Nullam vestibulum quis justo et tristique. Nulla eget eleifend ligula. Curabitur elementum est non est consequat, sit amet fringilla quam viverra. Aenean nec tellus non quam luctus pretium. Nulla volutpat porttitor justo, in consequat eros euismod ac. Morbi ornare nec mi quis condimentum. Quisque pharetra, magna in viverra porta, justo purus lacinia erat, a volutpat ipsum neque sed risus.\n" +
				"\n" +
				"Nulla ac risus non augue gravida iaculis. Nullam cursus maximus tristique. Curabitur imperdiet rhoncus luctus. In fermentum nisi et odio ornare sagittis. Integer vitae egestas orci, ornare iaculis purus. Etiam porttitor placerat enim, rhoncus dapibus sapien finibus hendrerit. Sed tellus tortor, tempus et velit eu, imperdiet lacinia erat. Praesent et justo quis tellus cursus viverra. Aliquam eu justo metus. Proin sit amet ligula a arcu imperdiet laoreet ut ut turpis.\n" +
				"\n" +
				"Nulla convallis dictum iaculis. Cras hendrerit egestas nibh et commodo. Phasellus pulvinar ante augue, vitae imperdiet massa elementum quis. Nunc vitae leo nec justo pretium varius nec id diam. Integer pretium arcu sit amet neque consequat, in rutrum arcu malesuada. Duis venenatis mauris dui, id pretium tellus consequat posuere. Phasellus vulputate auctor enim, ac tempor lorem facilisis eu.\n" +
				"\n" +
				"Duis ut faucibus arcu. Maecenas quis mauris ipsum. Nam maximus pellentesque massa, eu rutrum nisl ornare vitae. Suspendisse tempus dui et tortor porttitor fermentum. Duis id ullamcorper erat. Curabitur quis porttitor risus. Pellentesque mollis scelerisque nunc at interdum. Duis efficitur ultricies iaculis.\n" +
				"\n" +
				"Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Sed id scelerisque nisl, vitae condimentum urna. Quisque vehicula sem eget mi sodales interdum. Aenean id ullamcorper diam, vel eleifend lectus. Mauris volutpat pellentesque turpis, eu hendrerit elit. Quisque et porttitor nisi, id luctus ipsum. Morbi ac suscipit orci. Curabitur blandit nunc eu felis ullamcorper, nec rutrum tellus dignissim. Etiam sed tincidunt magna. Curabitur eget massa est. Donec placerat, lorem vitae dignissim bibendum, libero tellus facilisis ligula, eget euismod leo felis a leo. Duis quis purus at quam semper placerat eu ac turpis.\n" +
				"\n" +
				"Donec sed tincidunt ligula. Sed ut porttitor tortor. Nunc est lacus, luctus ac nisi nec, commodo rhoncus nibh. Etiam dui justo, venenatis sed mattis vitae, mattis eu purus. Fusce aliquam ante vitae ante mattis, ac commodo ligula cursus. Pellentesque eget dapibus nisl. Nam massa metus, tempor eget turpis id, eleifend luctus neque. Maecenas accumsan, ex in venenatis ornare, ipsum arcu pharetra ligula, a finibus nulla augue ut sem.\n" +
				"\n" +
				"Ut quis volutpat diam. Duis id leo sit amet nisi tempus dictum. Phasellus hendrerit rhoncus tincidunt. Aliquam nunc odio, placerat sed ipsum quis, dignissim eleifend sapien. Phasellus tortor elit, porta quis venenatis vitae, egestas sit amet augue. Sed suscipit cursus elit sit amet finibus. Maecenas non venenatis nisi, eget lacinia neque. Aenean eu dignissim nibh. Nullam gravida ex dui, vehicula porttitor quam ultrices sed. Phasellus interdum viverra consequat. Nulla in diam efficitur, ullamcorper odio et, tempor eros. Proin ullamcorper, lacus a tempor laoreet, arcu tellus lacinia nulla, et semper orci arcu vitae dolor.\n" +
				"\n" +
				"Nulla facilisi. Aliquam ut lorem molestie, venenatis nulla vitae, ultricies neque. Vivamus pretium diam in hendrerit semper. Cras sed ullamcorper ante, sit amet fermentum felis. Quisque gravida, urna vitae viverra luctus, mauris enim iaculis odio, in hendrerit nunc ipsum sit amet sapien. Maecenas et dictum eros. Fusce condimentum lacinia sollicitudin. Suspendisse enim purus, auctor id porta id, pellentesque id metus. Donec in dui metus. In scelerisque ipsum sed nibh sagittis rhoncus. Suspendisse id nunc nec purus scelerisque consectetur a ac quam. Nunc non dolor congue, pellentesque dolor a, ultricies nibh. Etiam rhoncus erat libero, rutrum scelerisque justo pharetra quis. Vestibulum nec arcu ligula. Etiam sed neque eros. Phasellus in semper magna, ac consequat eros.\n" +
				"\n" +
				"Proin tincidunt placerat odio, quis pharetra nisi lobortis id. Phasellus vehicula, lacus eget feugiat euismod, ex nisi commodo libero, a imperdiet nibh justo sit amet odio. Fusce mi velit, pellentesque vel feugiat vitae, feugiat sed est. Proin eleifend a leo eu semper. Duis sit amet ornare lacus, id blandit leo. Maecenas sed nisl in arcu ultricies vehicula. Pellentesque ultricies viverra eros at iaculis.\n" +
				"\n" +
				"Integer aliquet venenatis felis, non blandit nulla malesuada a. Pellentesque eget tellus semper, mattis augue sed, mattis nisl. Ut nec diam vitae arcu euismod posuere. Integer lacinia sodales orci quis lobortis. Proin ac dolor id nulla laoreet commodo congue in nibh. Ut eu felis ac ipsum fermentum scelerisque. Curabitur velit leo, viverra vitae velit eu, tincidunt pulvinar ante. Etiam finibus erat nec lacus auctor aliquam. Nullam interdum urna nunc, in vestibulum est vehicula a. Integer eleifend mauris et eros dignissim, vitae ullamcorper augue efficitur. Phasellus risus massa, lobortis id neque at, hendrerit fringilla nisi. Duis accumsan, tortor id dignissim tristique, magna leo mattis lectus, vitae vehicula mi neque in mi. Curabitur ultricies hendrerit accumsan.\n" +
				"\n" +
				"Nunc lorem purus, euismod in eros sit amet, tincidunt accumsan orci. Fusce faucibus urna eu orci posuere, quis auctor felis mollis. Ut porttitor risus turpis, ut scelerisque urna semper malesuada. Nunc et venenatis nisl. Maecenas lacinia felis in dui maximus euismod a vitae dolor. Quisque egestas magna tellus, id ornare justo ullamcorper quis. Cras ultricies odio ex, at faucibus tortor blandit eget.\n" +
				"\n" +
				"Suspendisse sed ante ut massa volutpat vehicula vel ut magna. Aliquam tincidunt mi nec purus varius, vel rutrum odio tempus. Maecenas non massa pharetra, fringilla nisl non, cursus neque. Suspendisse dictum commodo lacinia. Aenean fringilla risus nec neque convallis, vitae lobortis nisi pharetra. Etiam mollis, sem sed facilisis venenatis, ante dui elementum quam, id efficitur ex elit sit amet diam. Mauris varius porttitor augue eu placerat. Suspendisse id ornare lectus.\n" +
				"\n" +
				"Nunc pellentesque felis vitae lorem euismod imperdiet ac eget enim. Aliquam ex nibh, dapibus at libero ac, feugiat congue purus. Etiam nec fringilla dui. Phasellus pellentesque sem nulla, quis dictum elit pulvinar sed. Ut id pharetra quam, quis ultrices quam. Phasellus dapibus velit at cursus efficitur. Donec congue elit at lobortis finibus. Sed placerat urna vitae libero tempor imperdiet. Donec eu dapibus libero. Curabitur ultricies, purus lacinia posuere consequat, turpis ligula laoreet sapien, eu pulvinar justo leo eu sapien. Curabitur quis arcu vel tellus commodo placerat vel imperdiet nulla. Phasellus posuere rhoncus quam in iaculis. Nullam cursus nibh sit amet quam lobortis, et ullamcorper massa cursus. In scelerisque metus id diam tempus, ut sollicitudin nisi tristique.\n" +
				"\n" +
				"Mauris porttitor luctus mollis. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Phasellus varius fringilla neque, quis condimentum nibh semper condimentum. Quisque id lacus id orci vestibulum blandit. Fusce id lorem eu nisi euismod tristique sed a lectus. Vivamus pharetra sed eros a lacinia. Nulla id leo eget justo convallis dapibus. Donec condimentum ligula non purus vulputate, non bibendum ligula convallis. Vestibulum diam leo, gravida sit amet tortor non, tristique pellentesque enim. Fusce feugiat justo est, at vehicula nunc dignissim nec.\n" +
				"\n" +
				"Cras pulvinar ullamcorper sagittis. Vivamus et eros sagittis, dignissim libero id, lacinia lorem. Vivamus eget vehicula ipsum, et malesuada nibh. Curabitur viverra leo fermentum ligula hendrerit, quis condimentum velit euismod. Cras at elit blandit, lobortis leo sed, vehicula mi. Donec sit amet viverra lectus. Pellentesque sagittis efficitur scelerisque.\n" +
				"\n" +
				"Pellentesque at nisl nisi. Curabitur laoreet, quam ut feugiat mollis, erat lectus ullamcorper sapien, tincidunt suscipit ex sapien at nunc. Sed finibus tortor eu quam bibendum auctor. Nulla et augue auctor, euismod mi ut, accumsan lacus. Cras at lacus vel turpis vestibulum malesuada. Pellentesque sit amet purus facilisis, hendrerit odio eget, vestibulum sapien. Vivamus tincidunt arcu non magna iaculis, sed gravida odio varius. In hac habitasse platea dictumst. Integer facilisis rhoncus nulla in suscipit. Nullam vel libero feugiat, ornare tortor sed, facilisis risus. Etiam luctus ex non tortor sodales, quis tincidunt tellus iaculis. Donec laoreet nisl quis congue pellentesque. In cursus nibh id lacinia mollis.\n" +
				"\n" +
				"Donec nec risus quis nunc mattis hendrerit sit amet vel enim. Etiam posuere consectetur tincidunt. Phasellus feugiat mattis velit. Praesent sagittis consectetur lectus eu congue. Donec placerat ultrices arcu, nec interdum lectus finibus ut. Suspendisse interdum lorem ac dolor tincidunt accumsan. Integer a mi metus. Suspendisse aliquam arcu sit amet eros facilisis dignissim.\n" +
				"\n" +
				"Mauris at dolor non augue ornare imperdiet. In auctor leo felis, vel pellentesque mi ullamcorper vitae. Nulla dapibus viverra convallis. Morbi consequat ligula ut finibus malesuada. Proin ac arcu massa. Mauris vel turpis mattis, placerat sem a, posuere justo. Aliquam nec justo libero. Vestibulum feugiat nibh turpis, vel rhoncus enim dignissim at. Donec posuere cursus quam, finibus aliquet ligula ornare id.\n" +
				"\n" +
				"Vivamus aliquet nisi quis massa commodo eleifend sit amet sed enim. Duis vel mattis enim, nec gravida tellus. Fusce vel neque ut sapien facilisis mollis nec sit amet ligula. Quisque aliquam porttitor cursus. Nunc posuere magna bibendum ante egestas, at gravida nulla vehicula. Aenean lacus nisl, congue vitae justo quis, mattis dictum ex. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Mauris sem nisl, tempor eget tellus sed, posuere bibendum ipsum. In a nunc eget nunc ultrices varius eu eget magna. Vestibulum vulputate libero leo, sed porta lectus ullamcorper id. Pellentesque auctor dapibus nibh et iaculis. Suspendisse odio mi, porttitor vel dolor eu, interdum laoreet sem. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec porttitor turpis nibh, sed mollis libero tempus in. Sed massa augue, ornare ultricies blandit eget, sollicitudin ut turpis. Sed interdum facilisis malesuada.\n" +
				"\n" +
				"Quisque blandit ac purus non venenatis. Nulla facilisi. Nunc consectetur, diam id pretium lobortis, nisi nisi convallis massa, at fringilla sapien ex eu justo. Etiam aliquet id est at tincidunt. Phasellus euismod justo id magna lobortis venenatis. Phasellus commodo justo lorem, vitae rutrum nunc pulvinar eu. Interdum et malesuada fames ac ante ipsum primis in faucibus. Mauris mollis tristique risus, at gravida magna auctor eu. Cras scelerisque congue ex a posuere. Aenean pretium condimentum convallis.\n" +
				"\n" +
				"Quisque varius porttitor mattis. Donec urna nisi, consequat ut odio elementum, molestie eleifend nisl. Sed neque turpis, accumsan ac dapibus eu, pellentesque sed purus. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Cras at mi consequat dolor imperdiet hendrerit id non elit. Phasellus pellentesque est odio, vitae molestie dolor bibendum ornare. Phasellus cursus felis sed semper gravida. Pellentesque et lectus sit amet tellus varius pharetra. Proin pulvinar eu arcu ac laoreet. Proin vestibulum varius leo, et interdum leo maximus id.\n" +
				"\n" +
				"In fringilla volutpat mi, porttitor mollis velit sagittis eu. Vivamus sapien enim, fermentum id justo vel, laoreet sagittis justo. Quisque turpis ligula, consectetur a arcu id, suscipit aliquam metus. Cras ullamcorper cursus ex, et vestibulum leo tempus vitae. Fusce mollis elementum elit a hendrerit. Suspendisse ac luctus urna. Vestibulum accumsan dolor sit amet fermentum porttitor. Fusce sit amet magna at ex pulvinar ultricies.\n" +
				"\n" +
				"Praesent dapibus ut quam at auctor. Donec sapien tortor, efficitur sed gravida tristique, laoreet ut leo. Praesent auctor ut odio ac ullamcorper. Proin eget dapibus enim, in pharetra ligula. Nunc et finibus libero. Cras auctor sodales nibh, ut sagittis velit pretium vitae. Etiam massa ante, aliquam eget ultricies nec, congue sit amet erat. Aliquam erat volutpat. Sed laoreet eros turpis, suscipit mattis lectus ultrices sed. Cras faucibus ipsum sit amet purus hendrerit pharetra. Ut a ultrices dui, dapibus lobortis massa. Curabitur feugiat lectus ut diam fermentum tincidunt.\n" +
				"\n" +
				"Sed tortor velit, lobortis consectetur diam quis, mattis dignissim diam. Cras luctus, justo at rhoncus tincidunt, sem leo rhoncus ipsum, aliquam aliquam tortor eros non quam. Curabitur mi nisl, blandit nec posuere sed, maximus id metus. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Etiam dignissim nunc nec vulputate facilisis. Nulla porttitor dignissim eros ut fringilla. Ut vitae erat non nunc faucibus vestibulum et a ante.\n" +
				"\n" +
				"In sagittis orci feugiat, commodo purus quis, auctor risus. Donec varius orci eget eros vestibulum porta. Donec auctor magna nec sem accumsan iaculis. Aliquam erat volutpat. Sed hendrerit egestas mattis. Fusce maximus lobortis pellentesque. Mauris vitae mauris a nisl pretium ultrices. Vivamus sodales ante nec dolor posuere vehicula. In neque diam, consequat et euismod a, vulputate id augue. Donec ut neque accumsan, fermentum odio eu, interdum sem. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus rhoncus mauris eget arcu tempus ornare. Mauris eros ipsum, auctor id aliquam vehicula, ultricies et neque.\n" +
				"\n" +
				"Nulla massa mauris, efficitur malesuada faucibus sit amet, lobortis quis lorem. Sed consectetur orci in finibus ullamcorper. Quisque augue nunc, sagittis ac enim eu, lobortis rutrum nulla. Duis molestie luctus nisl, non condimentum felis. Donec ut urna sed ipsum consequat vehicula. Pellentesque maximus libero et quam convallis, vitae scelerisque felis fermentum. Duis at imperdiet justo.\n" +
				"\n" +
				"Duis sit amet velit velit. Donec molestie finibus magna eu cursus. Nunc faucibus tortor ligula, vel gravida risus ultrices sit amet. Integer molestie ante non eleifend ultricies. Curabitur mollis ex quis urna scelerisque pretium. Curabitur facilisis odio finibus mi faucibus fermentum eu quis dui. Nunc ut tempor est. Nullam ut porta nunc. Vivamus at arcu cursus lorem porta finibus. Quisque sagittis, lorem sed feugiat volutpat, lacus lectus laoreet sem, sed venenatis purus tellus non lectus. Cras efficitur erat nec sem luctus, a molestie mauris dapibus. Aliquam vitae odio nec ipsum mollis egestas sit amet id metus.\n" +
				"\n" +
				"Mauris mollis a ante sed finibus. Integer sit amet suscipit dui. Donec nec accumsan tellus. Etiam suscipit egestas elit, quis euismod libero hendrerit eget. Vivamus mattis eros ut est gravida, sit amet eleifend orci pellentesque. Integer non metus sit amet dui venenatis lacinia ut interdum nunc. Aliquam faucibus nulla quis sem fringilla, nec blandit felis tincidunt. Proin nec suscipit tellus.\n" +
				"\n" +
				"Nulla aliquet, odio et porttitor auctor, diam velit fermentum enim, non pellentesque ante massa in velit. Sed faucibus metus vel augue pretium semper. Mauris a aliquam nisl. Aenean elementum vitae lectus sit amet pretium. Maecenas finibus orci vitae accumsan venenatis. Nullam sodales magna eget sagittis bibendum. Nullam eget nunc pretium, posuere odio quis, venenatis ligula. Nam auctor rutrum erat, et vestibulum felis. Morbi aliquam, nisl at interdum ornare, nulla urna tempus ipsum, quis gravida tellus nibh sit amet arcu.\n" +
				"\n" +
				"Donec vitae sagittis elit, sit amet dapibus magna. Sed in arcu cursus, consectetur nisl eu, pulvinar lectus. Mauris eu mi eu justo condimentum tempor. Curabitur porta imperdiet blandit. Donec sapien felis, iaculis nec finibus sed, viverra quis arcu. Mauris ornare risus id mauris aliquet dapibus. Nunc ultricies quam nec magna bibendum placerat. Nunc at pulvinar mi. Donec viverra sodales dui, ut vestibulum tellus. Curabitur dignissim vulputate lorem, eget eleifend sapien egestas at. Etiam augue sem, tempor a eleifend vel, vulputate sit amet risus. Phasellus sed vulputate mauris. Aliquam et feugiat metus. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris mauris tellus, commodo vel est ac, sagittis viverra massa. Phasellus tempus, neque vitae varius sodales, orci mauris facilisis nunc, in condimentum ipsum dolor id turpis.\n" +
				"\n" +
				"Aenean vitae augue ipsum. Quisque a semper lacus. Duis at metus non dolor sollicitudin iaculis ut porta odio. Sed a nisl augue. Quisque cursus porta mauris, sed luctus justo ullamcorper eu. Donec egestas fermentum massa, nec rutrum metus laoreet maximus. Nam tincidunt elit urna, in varius orci aliquam id.\n" +
				"\n" +
				"Aliquam sodales elementum ultricies. Quisque viverra, purus at viverra venenatis, erat ante pellentesque lorem, ut laoreet eros lectus ut nulla. Pellentesque ac mollis est. Etiam lobortis, odio at vestibulum auctor, lorem elit facilisis lectus, mattis eleifend nulla dui sed purus. Aenean pretium eu ex vitae maximus. Quisque purus metus, hendrerit in ultricies a, euismod a nisl. Phasellus at commodo nisi. Proin et efficitur ante.\n" +
				"\n" +
				"Vivamus molestie augue sed massa malesuada, eu ullamcorper nulla aliquam. Proin sit amet tellus vel mauris finibus vulputate. Maecenas non libero suscipit, rutrum orci ac, vulputate velit. Pellentesque placerat, metus eu varius malesuada, metus elit pretium nisi, sit amet accumsan eros eros non leo. Nunc pretium pulvinar mi. Nunc sit amet tristique tellus. Praesent placerat sem et dui suscipit, vitae scelerisque urna tempus. Morbi vestibulum pellentesque ultrices. Ut rutrum a sapien sit amet rhoncus.\n" +
				"\n" +
				"Pellentesque quis felis porta, blandit nunc at, posuere nulla. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nulla pretium ultrices turpis, ultrices aliquet arcu ullamcorper eu. Donec ut nibh nisl. Nullam at augue at dolor facilisis imperdiet. Praesent dictum, libero at euismod placerat, nibh nibh aliquam quam, a vulputate dolor ipsum nec erat. Cras pretium lectus cursus mollis lobortis. Pellentesque ullamcorper purus rutrum felis vulputate, vitae pellentesque nunc convallis. Aliquam facilisis metus maximus, condimentum sem vel, venenatis ligula. Duis rutrum lacinia purus, sed laoreet justo tempus mollis. Aliquam dignissim venenatis felis, congue iaculis nunc sodales facilisis. Sed mollis condimentum semper.\n" +
				"\n" +
				"Aenean a turpis sed lacus pretium vulputate. Aenean non tristique massa. Nam varius felis in metus laoreet, maximus feugiat risus facilisis. Ut tristique diam vitae sagittis ultrices. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nam pharetra nibh felis, quis mollis lacus scelerisque id. Cras luctus venenatis risus in accumsan. Aliquam et tristique mauris, quis luctus quam. Sed cursus a nibh venenatis auctor. Duis quis elit non nisi blandit tempor. Donec elementum neque nisl, at convallis libero posuere nec. Donec nec eros cursus, posuere turpis facilisis, vestibulum eros."));
		helpScreen.setMaxWidth(screenPane.getWidth());
		ScrollPane helpScreenScroll = new ScrollPane();
		helpScreenScroll.setFitToWidth(true);
		helpScreenScroll.setFitToHeight(true);
		helpScreenScroll.setContent(helpScreen);
		screenPane.setCenter(helpScreenScroll);
	}

	public void displayLoginDialog(){
		LoginDialogSingleton loginDialog = LoginDialogSingleton.getSingleton();
		AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();
		loginDialog.showDialog("Login", "Login into your Account", "Login");

		try {
			if (!loginDialog.getUsername().equals("") && loginDialog.getPassword().equals("")) {
				messageDialog.show("No Password Entered", "You must enter a password.");
			}
			else if (!loginDialog.getUsername().equals("") && !loginDialog.getPassword().equals("")) {
				if (loginDialog.getUsername().length() > 0 && loginDialog.getPassword().length() > 0) {
					if (!accountLoader.checkAccountNameValid(loginDialog.getUsername())) {
						messageDialog.show("Invalid Username", "The entered username is not valid. A valid username only has numbers and letters.");
					} else {
						if (accountLoader.checkAccountExists(loginDialog.getUsername())) {
							account = AccountData.getSingleton();
							account = accountLoader.loadAccount(account, loginDialog.getUsername(), loginDialog.getPassword());
							loggedin = true;
							loadHomeScreen();
						} else {
							messageDialog.show("Account Already Exists", "An account with this username already exists.");
							createAccount();
						}
					}
				}
			}
		}
		catch(LoginException | IOException | NoSuchAlgorithmException e){
			AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
			dialog.show("Login Error", "There was an error while logging in.\n" + e.getMessage());
			displayLoginDialog();
		}
	}

	public void logoutAccount(){
		loggedin = false;
		account.reset();
		loadHomeScreen();
	}

	public void createAccount(){
		LoginDialogSingleton loginDialog = LoginDialogSingleton.getSingleton();
		AppMessageDialogSingleton messageDialog = AppMessageDialogSingleton.getSingleton();

		loginDialog.showDialog("Create Account", "Create an Account", "Create Account");
		if(!loginDialog.getUsername().equals("") && loginDialog.getPassword().equals("")){
			messageDialog.show("No Password Entered", "You must enter a password.");
		}
		else if(!loginDialog.getUsername().equals("") && !loginDialog.getPassword().equals("")) {
			if(loginDialog.getUsername().length() > 0 && loginDialog.getPassword().length() > 0) {
				if (!accountLoader.checkAccountNameValid(loginDialog.getUsername())) {
					messageDialog.show("Invalid Username", "The entered username is not valid. A valid username only has numbers and letters.");
				}

				else{
					if (accountLoader.checkAccountExists(loginDialog.getUsername())) {
						try {
							account = AccountData.getSingleton();
							accountLoader.createNewAccount(account, loginDialog.getUsername(), loginDialog.getPassword());
							loggedin = true;
							loadHomeScreen();
						} catch (FileNotFoundException e) {
							messageDialog.show("Error Creating Account", "There was an error creating a new account:\n" + e.getMessage());
						} catch (IOException e) {
							messageDialog.show("Error Creating Account", "There was an error creating a new account:\n" + e.getMessage());
						}
					} else {
						messageDialog.show("Account Already Exists", "An account with this username already exists.");
					}
				}
			}
		}
	}

	public AppTemplate getAppTemplate(){
		return appTemplate;
	}

	public AccountLoader getAccountLoader() {
		return accountLoader;
	}

	public BuzzWordGraphics getGraphics() {
		return graphics;
	}

	private void loadAccountCustomizationScreen(){
		gameState = GameState.EDIT_ACCOUNT;
		((Workspace) appTemplate.getWorkspaceComponent()).resetGamePane();
		screenPane = ((Workspace) appTemplate.getWorkspaceComponent()).getGamePane();
		displayToolbar(gameState);

		HBox accountHeaderLabel = new HBox();
		Label usernameField = new Label(account.getUsername() + "'s Account Page");
		usernameField.setFont(new Font(30));
		accountHeaderLabel.setAlignment(Pos.CENTER);
		accountHeaderLabel.getChildren().add(usernameField);

		GridPane accountDetails = new GridPane();
		accountDetails.setPadding(new Insets(0, 50, 0, 50));
		accountDetails.setVgap(20);
		accountDetails.setHgap(10);

		Label passwordLabel = new Label("Change Your Password:");
		passwordLabel.setFont(new Font(25));
		Label newPassword = new Label("Enter a new password:");
		newPassword.setFont(new Font(20));
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");

		accountDetails.add(passwordLabel, 0, 0);
		accountDetails.add(newPassword, 0, 1);
		accountDetails.add(passwordField, 1, 1);
		accountDetails.add(changePassword, 2, 1);

		changePassword.setOnMouseClicked(ev ->{
			if(!passwordField.getText().equals("")) {
				AppMessageDialogSingleton dialog = AppMessageDialogSingleton.getSingleton();
				try {
					account.setAndEncryptPassword(passwordField.getText());

					accountLoader.saveAccount(account);
					dialog.show("Password Changed", "Password Changed Successfully");
				}
				catch (IOException e){
					dialog.show("Password Change Error", "There was an error while changing the password.");
				}
			}
		});


		screenPane.setTop(accountHeaderLabel);
		screenPane.setCenter(accountDetails);
	}

	private void initializeButtons(){
		StackPane accountButtons = new StackPane();
		StackPane loginGameModeHolder = new StackPane();
		PropertyManager propertyManager = PropertyManager.getManager();

		createAccount = appTemplate.getGUI().initializeChildButton(accountButtons, "Create Account", CREATE_ACCOUNT_TOOLTIP.toString(), true);
		accountPage = appTemplate.getGUI().initializeChildButton(accountButtons,  "Account", ACCOUNT_TOOLTIP.toString(), true);
		dictionaryMode = new MenuItem("Dictionary");
		namesMode = new MenuItem("Names");
		animalMode = new MenuItem("Animals");
		placesMode = new MenuItem("Places");
		gameModeSelector = new MenuButton("Game Mode", null, dictionaryMode, namesMode, animalMode, placesMode);
		gameModeSelector.setMaxWidth(Double.MAX_VALUE);
		gameModeSelector.setDisable(true);
		gameModeSelector.setPopupSide(Side.RIGHT);
		login = appTemplate.getGUI().initializeChildButton(loginGameModeHolder, "Login", LOGIN_TOOLTIP.toString(), true);

		loginGameModeHolder.getChildren().add(gameModeSelector);
		toolBar.getChildren().add(accountButtons);
		toolBar.getChildren().add(loginGameModeHolder);
		help = appTemplate.getGUI().initializeChildButton(toolBar, "Help", HELP_TOOLTIP.toString(), true);
		returnHome = appTemplate.getGUI().initializeChildButton(toolBar, "Return to Home", RETURN_HOME_TOOLTIP.toString(), true);

		playGame = new Button("Start Playing");
		playGame.setMinWidth(150);
		Tooltip playGameTooltip = new Tooltip(propertyManager.getPropertyValue(PLAY_GAME_TOOLTIP.toString()));
		playGame.setTooltip(playGameTooltip);

		editAccount = new Button("Edit your Account");
		editAccount.setMinWidth(150);
		Tooltip editAccountTooltip = new Tooltip("Edit your Account");
		editAccount.setTooltip(editAccountTooltip);

		changePassword = new Button("Change your Password");
		changePassword.setMinWidth(150);
		Tooltip changePasswordTooltip = new Tooltip("Change your Password");
		changePassword.setTooltip(changePasswordTooltip);

		logout = new Button("Logout");
		logout.setMinWidth(150);
		Tooltip logoutTooltip = new Tooltip("Logout of your Account.");
		logout.setTooltip(logoutTooltip);

		createAccount.setVisible(false);
		accountPage.setVisible(false);
		gameModeSelector.setVisible(false);
		login.setVisible(false);
		help.setVisible(false);
		returnHome.setVisible(false);

		levelSelect = new Button[8];
		for(int i = 0; i < 8; i++) {
			levelSelect[i] = new Button(String.valueOf(i+1));
			levelSelect[i].setMinWidth(100);
			levelSelect[i].setMinHeight(100);
			levelSelect[i].setStyle("-fx-background-radius: 20em");
		}
	}

	private void setupHandlers(){
		for (Button levelButton : levelSelect){
			levelButton.setOnMouseClicked(e ->{
				gameData.setLevel(Integer.parseInt(levelButton.getText()));
				loadGameScreen();
			});
		}

		createAccount.setOnMouseClicked(e -> {
			gameState = GameState.CREATE_ACCOUNT;
			createAccount();
		});

		accountPage.setOnMouseClicked(e -> {
			if(controller.isGameInProgress()){
				controller.abruptPauseGame();
				YesNoCancelDialogSingleton dialog = YesNoCancelDialogSingleton.getSingleton();
				dialog.show("Exit?", "Do you want to quit the game and view your account page?");
				if(dialog.getSelection().equals(YesNoCancelDialogSingleton.YES)){
					controller.abruptEndGame();
					gameState = GameState.ACCOUNT;
					loadAccountScreen();
				}
				else {
					e.consume();
					controller.abruptContinueGame();
				}
			}
			else {
				gameState = GameState.ACCOUNT;
				loadAccountScreen();
			}
		});

		login.setOnMouseClicked(e -> {
			gameState = GameState.LOGIN;
			displayLoginDialog();
		});

		help.setOnMouseClicked(e -> {
			if(controller.isGameInProgress()) {
				controller.abruptPauseGame();
				YesNoCancelDialogSingleton dialog = YesNoCancelDialogSingleton.getSingleton();
				dialog.show("Exit?", "Do you want to quit the game and view your account page?");
				if (dialog.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
					controller.abruptEndGame();
					gameState = GameState.HELP;
					loadHelpScreen();
				} else {
					e.consume();
					controller.abruptContinueGame();
				}
			}
			else{
				gameState = GameState.HELP;
				loadHelpScreen();
			}
		});
		returnHome.setOnMouseClicked(e -> {
			if(controller.isGameInProgress()) {
				controller.abruptPauseGame();
				YesNoCancelDialogSingleton dialog = YesNoCancelDialogSingleton.getSingleton();
				dialog.show("Exit?", "Do you want to quit the game and view your account page?");
				if (dialog.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
					controller.abruptEndGame();
					gameState = GameState.LOGGEDIN_HOME;
					loadHomeScreen();
				} else {
					e.consume();
					controller.abruptContinueGame();
				}
			}
			else {
				gameState = GameState.LOGGEDIN_HOME;
				loadHomeScreen();
			}
		});

		dictionaryMode.setOnAction(e -> {
			gameModeSelector.setText("Dictionary");
			gameMode = GameMode.DICTIONARY;
			gameData.setGameMode(gameMode);
			gameData.loadTrie();
			gameData.saveTrie();
			if(gameState == GameState.LEVEL){
				loadLevelSelectScreen();
			}
		});
		namesMode.setOnAction(e -> {
			gameModeSelector.setText("Names");
			gameMode = GameMode.NAMES;
			gameData.setGameMode(gameMode);
			gameData.loadTrie();
			gameData.saveTrie();
			if(gameState == GameState.LEVEL){
				loadLevelSelectScreen();
			}
		});
		placesMode.setOnAction(e -> {
			gameModeSelector.setText("Places");
			gameMode = GameMode.PLACES;
			gameData.setGameMode(gameMode);
			gameData.loadTrie();
			gameData.saveTrie();
			if(gameState == GameState.LEVEL){
				loadLevelSelectScreen();
			}
		});
		animalMode.setOnAction(e -> {
			gameModeSelector.setText("Animals");
			gameMode = GameMode.ANIMALS;
			gameData.setGameMode(gameMode);
			gameData.loadTrie();
			gameData.saveTrie();
			if(gameState == GameState.LEVEL){
				loadLevelSelectScreen();
			}
		});

		editAccount.setOnMouseClicked(e ->{
			gameState = GameState.ACCOUNT;
			loadAccountCustomizationScreen();
		});

		playGame.setOnMouseClicked(e -> {
			gameState = GameState.LEVEL;
			loadLevelSelectScreen();
		});

		logout.setOnMouseClicked(e -> {
			gameState = GameState.UNLOGGEDIN_HOME;
			logoutAccount();
		});

		appTemplate.getGUI().getWindow().setOnCloseRequest(e -> exitRequest(e));
	}

	private void displayToolbar(GameState gameState){
		//gameModeSelector.setText("Game Mode");
		switch (gameState){
			case ACCOUNT:
				toggleButton(createAccount, false);
				toggleButton(login, false);
				toggleButton(accountPage, false);

				toggleButton(returnHome, true);
				toggleButton(help, true);
				gameModeSelector.setDisable(false);
				gameModeSelector.setVisible(true);
				break;
			case CREATE_ACCOUNT:
				break;
			case LOGIN:
				break;
			case EDIT_ACCOUNT:
				toggleButton(createAccount, false);
				toggleButton(login, false);
				toggleButton(accountPage, true);

				toggleButton(returnHome, true);
				toggleButton(help, true);
				gameModeSelector.setDisable(false);
				gameModeSelector.setVisible(true);
				break;
			case UNLOGGEDIN_HOME:
				toggleButton(createAccount,true);
				toggleButton(login,true);

				toggleButton(returnHome, false);
				toggleButton(accountPage, false);
				toggleButton(help, false);
				gameModeSelector.setDisable(true);
				gameModeSelector.setVisible(false);
				break;
			case LOGGEDIN_HOME:
				toggleButton(createAccount, false);
				toggleButton(login, false);
				toggleButton(returnHome, false);

				toggleButton(accountPage, true);
				toggleButton(help, true);
				gameModeSelector.setDisable(false);
				gameModeSelector.setVisible(true);
				break;
			case GAME:
				toggleButton(createAccount, false);
				toggleButton(login, false);

				toggleButton(returnHome, true);
				toggleButton(accountPage, true);
				toggleButton(help, true);
				gameModeSelector.setDisable(false);
				gameModeSelector.setVisible(true);
				break;
			case LEVEL:
				toggleButton(createAccount, false);
				toggleButton(login, false);

				toggleButton(returnHome, true);
				toggleButton(accountPage, true);
				toggleButton(help, true);
				gameModeSelector.setDisable(false);
				gameModeSelector.setVisible(true);
				break;
			case HELP:
				toggleButton(createAccount, false);
				toggleButton(login, false);

				toggleButton(returnHome, true);
				toggleButton(accountPage, true);
				toggleButton(help, false);
				gameModeSelector.setDisable(false);
				gameModeSelector.setVisible(true);
				break;
			default:
				toggleButton(createAccount, false);
				toggleButton(login, false);
				toggleButton(returnHome, false);

				toggleButton(accountPage, true);
				toggleButton(help, true);
				gameModeSelector.setDisable(false);
				gameModeSelector.setVisible(true);
				break;
		}
	}

	private void setupShortCuts(){
		KeyCombination ctrlShiftP = KeyCodeCombination.keyCombination("Ctrl+Shift+P");
		KeyCombination ctrlL = KeyCodeCombination.keyCombination("Ctrl+L");
		KeyCombination ctrlP = KeyCodeCombination.keyCombination("Ctrl+P");
		KeyCombination ctrlQ = KeyCodeCombination.keyCombination("Ctrl+Q");
		KeyCombination ctrlH = KeyCodeCombination.keyCombination("Ctrl+H");

		Scene scene = appTemplate.getGUI().getPrimaryScene();
		scene.setOnKeyPressed(e ->{
			if(ctrlShiftP.match(e)){
				createAccount();
			}
			else if(ctrlL.match(e)){
				if(loggedin){
					loggedin = false;
					gameState = GameState.UNLOGGEDIN_HOME;
					loadHomeScreen();
				}
				else{
					gameState = GameState.LOGIN;
					displayLoginDialog();
				}
			}
			else if(ctrlP.match(e)){
				gameState = GameState.LEVEL;
				loadLevelSelectScreen();
			}
			else if(ctrlQ.match(e)){
				exitRequest();
			}
			else if(ctrlH.match(e)){
				viewHomePage();
			}
		});
	}

	private void toggleButton(Button button, boolean visible){
		button.setVisible(visible);
		button.setDisable(!visible);
	}

	private void exitRequest(WindowEvent e){
		if(controller.isGameInProgress()){
			YesNoCancelDialogSingleton dialog = YesNoCancelDialogSingleton.getSingleton();
			dialog.show("Exit?", "Do you want to quit the game and exit?");
			if(dialog.getSelection().equals(YesNoCancelDialogSingleton.YES)){
				appTemplate.getGUI().getWindow().close();
			}
			else
				e.consume();
		}
		else {
			appTemplate.getGUI().getWindow().close();
		}
	}

	private void exitRequest(){
		if(controller.isGameInProgress()){
			YesNoCancelDialogSingleton dialog = YesNoCancelDialogSingleton.getSingleton();
			dialog.show("Exit?", "Do you want to quit the game and exit?");
			if(dialog.getSelection().equals(YesNoCancelDialogSingleton.YES)){
				appTemplate.getGUI().getWindow().close();
			}
		}
		else {
			appTemplate.getGUI().getWindow().close();
		}
	}

	private void viewHomePage(){
		if(controller.isGameInProgress()) {
			controller.abruptPauseGame();
			YesNoCancelDialogSingleton dialog = YesNoCancelDialogSingleton.getSingleton();
			dialog.show("Exit?", "Do you want to quit the game and view your account page?");
			if (dialog.getSelection().equals(YesNoCancelDialogSingleton.YES)) {
				controller.abruptEndGame();
				gameState = GameState.LOGGEDIN_HOME;
				loadHomeScreen();
			} else {
				controller.abruptContinueGame();
			}
		}
		else {
			gameState = GameState.LOGGEDIN_HOME;
			loadHomeScreen();
		}
	}
}
