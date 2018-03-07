package tdt4140.gr1837.app.ui;

import static org.junit.Assert.assertEquals;

import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;

import com.jfoenix.controls.JFXButton;
import static org.testfx.api.FxAssert.verifyThat;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ProfileTabTest extends ApplicationTest {

	Parent root;
	Stage stage;

    @BeforeClass
    public static void headless() {
    		if (Boolean.valueOf(System.getProperty("gitlab-ci", "false"))) {
    			GitlabCISupport.headless();
    		}
    }

	@Override
    public void start(Stage stage) throws Exception {
		this.stage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("ProfileTab.fxml"));
        
        Scene scene = new Scene(root);
        this.root = scene.getRoot();
        
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void testElementsExist() {
    		Node field = from(root).lookup("#clientName").query();
    		Assert.assertTrue(field instanceof Text);
    		Node list = from(root).lookup("#exerciseList").query();
    		Assert.assertTrue(list instanceof TableView);
    }
    
    @Test
    public void testTableAction() {
    		clickOn("#type");
    		clickOn("#set");
    		clickOn("#repetitions");
    		clickOn("#weight");
    }
    
    
}