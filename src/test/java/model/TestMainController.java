package model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import view.MainController;

@ExtendWith(ApplicationExtension.class)
class TestMainController
{

  @Start  //Before
  private void start(Stage stage)
  {
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(MainController.class.getResource("main.fxml"));
	    try {
	    	
	    BorderPane view = loader.load();
	    MainController cont = loader.getController();
	    CalcModel model =new CalcModel(); 
	    cont.setModel(model);
	   
	    
	    
	    Scene s = new Scene(view);
	    stage.setScene(s);
	    stage.show();
	    }
	    catch(IOException e)
	    {
	    	e.printStackTrace();
	    }
	    }
	  
  
  	void writeNum(FxRobot robot, double value, String textFieldName)
  	{
    	robot.clickOn(textFieldName);
    	robot.write(Double.toString(value));
  	}
  
    void writeNums(FxRobot robot, double first, double second)
    {
    	writeNum(robot, first, "#NumOneTFCSS");
    	writeNum(robot, second, "#NumTwoTFCSS");
    }
    
    void checkCalculation(FxRobot robot, String operationButtonName, String expectedResult)
    {
    	robot.clickOn(operationButtonName);
        Assertions.assertThat(robot.lookup("#ResultLabelCSS")
                .queryAs(Label.class)).hasText(expectedResult);    

    }
    
    void testFourOpsAtOnce(FxRobot robot, double first, double second)
    {

    	
    	DecimalFormat df = new DecimalFormat("#.###");
    	//df.setRoundingMode(RoundingMode.DOWN);
    	
    	checkCalculation(robot, "#AddBtnCSS", df.format(first+second));
    	
    	checkCalculation(robot, "#SubBtnCSS", df.format(first-second));

    	checkCalculation(robot, "#MultiplyBtnCSS", df.format(first*second));
    	
    	if(second == 0)
    	{
    		checkCalculation(robot, "#DivideBtnCSS", df.format(0));
    	}
    	else
    	{
        	checkCalculation(robot, "#DivideBtnCSS", df.format(first/second));
    	}
    	

    }
    
    void testWriteAndOps(FxRobot robot, double first, double second)
    {
    	writeNums(robot, first, second);
    	testFourOpsAtOnce(robot, first, second);
    }
    
    @SuppressWarnings("unchecked")
    ListView<Operation> getOps(FxRobot robot)
    {
     return (ListView<Operation>) robot.lookup("#HistoryViewCSS")
         .queryAll().iterator().next();
    }
    
    
    void testObservableListEquality(ObservableList<Operation> first,
  		  ObservableList<Operation> second)
    {
  	  if(first.size() != second.size()) 
  	  {
  		  fail("First observable list " + first.toString()
  		  + " doesn't have the same size as " + second.toString());
  	  }
  	  
  	  for(int i=0; i<first.size(); i++)
  	  {
  		  Operation firstOp = first.get(i);
  		  Operation secondOp = second.get(i);
  		  
  		  //page models assert that their associated
  		  //pages are equal
  		  assertEquals(firstOp, secondOp);
  	  }
    }
    
	@Test
	void testSingleCalculations(FxRobot robot)
	{
		testWriteAndOps(robot, 5.0, 10.0);
		
		testWriteAndOps(robot, 1.1, 2.2);
		testWriteAndOps(robot, 0.0, 10.1);
		testWriteAndOps(robot, 6.4, 0.0);
		testWriteAndOps(robot, 0.0, -6.2);
		testWriteAndOps(robot, 6.3, -5.1);
		testWriteAndOps(robot, -7.1, 10.0);

    	robot.clickOn("#NumOneTFCSS");
    	robot.write("WORDS");
    	robot.clickOn("#NumTwoTFCSS");
    	robot.write("6.9");
		testFourOpsAtOnce(robot, 0.0, 6.9);

    	robot.clickOn("#NumOneTFCSS");
    	robot.write("7.1");
    	robot.clickOn("#NumTwoTFCSS");
    	robot.write("WORDS");
		testFourOpsAtOnce(robot, 7.1, 0.0);

    	robot.clickOn("#NumOneTFCSS");
    	robot.write("WORDS");
    	robot.clickOn("#NumTwoTFCSS");
    	robot.write("WORDS");
		testFourOpsAtOnce(robot, 0.0, 0.0);

	}
	
	
	@Test
	void testOperationListView(FxRobot robot)
	{
		
		ObservableList<Operation> history = getOps(robot).getItems();
		testObservableListEquality(history,
		  		  FXCollections.observableArrayList());
		
		testWriteAndOps(robot, 5.0, 10.0);
		
		ArrayList<Operation> listOps = new ArrayList<Operation>();
		
		listOps.add(new Operation(5.0, " + ", 10.0, 15.0));
		listOps.add(new Operation(5.0, " - ", 10.0, -5.0));
		listOps.add(new Operation(5.0, " * ", 10.0, 50.0));
		listOps.add(new Operation(5.0, " / ", 10.0, 0.5));

		
		ObservableList<Operation> expected = FXCollections.observableArrayList(listOps);
		
		
		ObservableList<Operation> newHistory = getOps(robot).getItems();
		testObservableListEquality(newHistory,
		  		  expected);


		
	}
}
