import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class ParserTest {

	Parser parser = new Parser();

	@Test
	public void testBasicAddCommand() throws Exception {
		String basicAddInput = "add unit testing";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.ADD, addCommand.getCommandType());
		assertEquals("unit testing", addCommand.getTaskName());
	}

	@Test
	public void testAddCommandWithDate() throws Exception {
		String basicAddInput = "add unit testing 10 Oct 2014";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.ADD, addCommand.getCommandType());
		assertEquals("unit testing", addCommand.getTaskName());
		Calendar actualCal = addCommand.getTaskDueDate();
		assertEquals(2014, actualCal.get(Calendar.YEAR));
		assertEquals(Calendar.OCTOBER, actualCal.get(Calendar.MONTH));
		assertEquals(10, actualCal.get(Calendar.DAY_OF_MONTH));
	}

	@Test
	public void testAddCommandWithDateAndTime12Hours() throws Exception {
		String basicAddInput = "add unit testing 10 Oct 2014 10.45pm";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.ADD, addCommand.getCommandType());
		assertEquals("unit testing", addCommand.getTaskName());
		Calendar actualCal = addCommand.getTaskDueDate();
		assertEquals(2014, actualCal.get(Calendar.YEAR));
		assertEquals(Calendar.OCTOBER, actualCal.get(Calendar.MONTH));
		assertEquals(10, actualCal.get(Calendar.DAY_OF_MONTH));
		assertEquals(22, actualCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(45, actualCal.get(Calendar.MINUTE));
	}

	@Test
	public void testAddCommandWithDateAndTime24Hours() throws Exception {
		String basicAddInput = "add unit testing 10 Oct 2014 2245";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.ADD, addCommand.getCommandType());
		assertEquals("unit testing", addCommand.getTaskName());
		Calendar actualCal = addCommand.getTaskDueDate();
		assertEquals(2014, actualCal.get(Calendar.YEAR));
		assertEquals(Calendar.OCTOBER, actualCal.get(Calendar.MONTH));
		assertEquals(10, actualCal.get(Calendar.DAY_OF_MONTH));
		assertEquals(22, actualCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(45, actualCal.get(Calendar.MINUTE));
	}

	@Test
	public void testEditCommandName() throws Exception {
		String basicAddInput = "edit t1 unit testing";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.EDIT, addCommand.getCommandType());
		assertEquals("t1", addCommand.getTaskID());
		assertEquals("unit testing", addCommand.getTaskName());
	}

	@Test
	public void testEditCommandDate() throws Exception {
		String basicAddInput = "edit t1 tomorrow";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.EDIT, addCommand.getCommandType());
		assertEquals("t1", addCommand.getTaskID());
		Calendar expectedCal = Calendar.getInstance();
		Calendar actualCal = addCommand.getTaskDueDate();
		assertEquals(expectedCal.get(Calendar.YEAR), actualCal.get(Calendar.YEAR));
		assertEquals(expectedCal.get(Calendar.MONTH), actualCal.get(Calendar.MONTH));
		assertEquals(expectedCal.get(Calendar.DAY_OF_MONTH)+1, actualCal.get(Calendar.DAY_OF_MONTH));
	}
}