package buzzword;

import apptemplate.AppTemplate;
import components.AppComponentsBuilder;
import components.AppDataComponent;
import components.AppFileComponent;
import components.AppWorkspaceComponent;
import data.AccountData;
import data.AccountLoader;
import gui.Workspace;

/**
 * Created by Tejas on 10/31/2016.
 */
public class BuzzWord extends AppTemplate{

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public AppComponentsBuilder makeAppBuilderHook() {
        return new AppComponentsBuilder() {
            @Override
            public AppDataComponent buildDataComponent() throws Exception {
                return AccountData.getSingleton();
            }

            @Override
            public AppFileComponent buildFileComponent() throws Exception {
                return new AccountLoader();
            }

            @Override
            public AppWorkspaceComponent buildWorkspaceComponent() throws Exception {
                return new Workspace(BuzzWord.this);
            }
        };
    }
}
