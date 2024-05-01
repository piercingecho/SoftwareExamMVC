package model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

class TestCalcModel
{

	@BeforeEach
	void setUp() throws Exception
	{
	}
	
	void setNums(CalcModel calc, double numone, double numtwo)
	{
		calc.setNum1(new SimpleDoubleProperty(numone));
		calc.setNum2(new SimpleDoubleProperty(numtwo));
	}
	
	double getResultVal(CalcModel calc)
	{
		return calc.getResult().getValue();
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

	
	//tests
	
	
	@Test
	void testOperationToString()
	{
		Operation operation = new Operation(6, " + ", 4, 10);
		assertEquals(operation.toString(), "6.0 + 4.0 = 10.0");
		operation = new Operation(5.3, " / ", -4.5, (5.3/-4.5));
		assertEquals(operation.toString(), "5.3 / -4.5 = " + Double.toString(5.3/-4.5));
	}
	

	@Test
	void testCalcModel()
	{
		CalcModel calc = new CalcModel();
		setNums(calc, 5,10);
		calc.add();
		assertEquals(15, getResultVal(calc));
		calc.subtract();
		assertEquals(-5, getResultVal(calc));
		calc.multiply();
		assertEquals(50, getResultVal(calc));
		calc.divide();
		assertEquals(0.5, getResultVal(calc));

		setNums(calc, 5,0);
		calc.divide();
		assertEquals(0, getResultVal(calc));

		ObservableList<Operation> expectedList = FXCollections.observableArrayList();
		expectedList.add(new Operation(5, " + ", 10, 15));
		expectedList.add(new Operation(5, " - ", 10, -5));
		expectedList.add(new Operation(5, " * ", 10, 50));
		expectedList.add(new Operation(5, " / ", 10, 0.5));

		
		System.out.println(calc.getOperations());
		System.out.println(expectedList);
		testObservableListEquality(calc.getOperations(), expectedList);
		//assertEquals(calc.getOperations(),expectedList);
		
	}
	
	@Test
	void testSetOperations()
	{
		CalcModel calc = new CalcModel();
		ObservableList<Operation> startList = FXCollections.observableArrayList();
		
		startList.add(new Operation(5, " + ", -3, -2));
		startList.add(new Operation(6, " - ", 7, 2)); //intentionally bad

		setNums(calc,5,10);
		
		assertEquals(calc.getNum1().get(), 5);
		assertEquals(calc.getNum2().get(), 10);

		calc.setOperations(startList);
		
		calc.setResult(new SimpleDoubleProperty(100)); //should NOT affect the operation!
		
		calc.add();
		
		setNums(calc,5,0);
		calc.divide(); //should NOT add to operation
		assertEquals(0, calc.getResult().get());
		
		ObservableList<Operation> expectedList = FXCollections.observableArrayList();
		expectedList.add(new Operation(5, " + ", -3, -2));
		expectedList.add(new Operation(6, " - ", 7, 2)); //intentionally bad
		expectedList.add(new Operation(5, " + ", 10, 15));
		
		

		
		
	}

	
	
}
