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

	@Test
	public void testBasicDeleteCommand() throws Exception {
		String basicAddInput = "delete t1";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.DELETE, addCommand.getCommandType());
		String[] expectedArray = {"t1"};
		assertArrayEquals(expectedArray, addCommand.getTaskIDsToDelete());
	}

	@Test
	public void testMultipleDeleteCommand() throws Exception {
		String basicAddInput = "delete t1 r2 f3";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.DELETE, addCommand.getCommandType());
		String[] expectedArray = {"t1", "r2", "f3"};
		assertArrayEquals(expectedArray, addCommand.getTaskIDsToDelete());
	}

	@Test
	public void testInvalidIDDeleteCommand() throws Exception {
		String basicAddInput = "delete z3";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.DELETE, addCommand.getCommandType());
		assertNull(addCommand.getTaskIDsToDelete());
	}

	@Test
	public void testListCommandSingleDate() throws Exception {
		String basicAddInput = "list today";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.LIST, addCommand.getCommandType());
		Calendar expectedDate = Calendar.getInstance();
		Calendar actualStartDate = addCommand.getSearchStartDate();
		Calendar actualEndDate = addCommand.getSearchEndDate();
		assertEquals(expectedDate.get(Calendar.YEAR), actualStartDate.get(Calendar.YEAR));
		assertEquals(expectedDate.get(Calendar.MONTH), actualStartDate.get(Calendar.MONTH));
		assertEquals(expectedDate.get(Calendar.DAY_OF_MONTH), actualStartDate.get(Calendar.DAY_OF_MONTH));
		assertEquals(0, actualStartDate.get(Calendar.HOUR_OF_DAY));
		assertEquals(0, actualStartDate.get(Calendar.MINUTE));
		assertEquals(0, actualStartDate.get(Calendar.SECOND));
		assertEquals(expectedDate.get(Calendar.YEAR), actualEndDate.get(Calendar.YEAR));
		assertEquals(expectedDate.get(Calendar.MONTH), actualEndDate.get(Calendar.MONTH));
		assertEquals(expectedDate.get(Calendar.DAY_OF_MONTH), actualEndDate.get(Calendar.DAY_OF_MONTH));
		assertEquals(23, actualEndDate.get(Calendar.HOUR_OF_DAY));
		assertEquals(59, actualEndDate.get(Calendar.MINUTE));
		assertEquals(59, actualEndDate.get(Calendar.SECOND));
	}

	@Test
	public void testListCommandDateRange() throws Exception {
		String basicAddInput = "list today to 2 days after";
		Command addCommand = parser.parseCommand(basicAddInput);
		assertEquals(Command.COMMAND_TYPE.LIST, addCommand.getCommandType());
		Calendar expectedDate = Calendar.getInstance();
		Calendar actualStartDate = addCommand.getSearchStartDate();
		Calendar actualEndDate = addCommand.getSearchEndDate();
		assertEquals(expectedDate.get(Calendar.YEAR), actualStartDate.get(Calendar.YEAR));
		assertEquals(expectedDate.get(Calendar.MONTH), actualStartDate.get(Calendar.MONTH));
		assertEquals(expectedDate.get(Calendar.DAY_OF_MONTH), actualStartDate.get(Calendar.DAY_OF_MONTH));
//		assertEquals(0, actualStartDate.get(Calendar.HOUR_OF_DAY));
//		assertEquals(0, actualStartDate.get(Calendar.MINUTE));
//		assertEquals(0, actualStartDate.get(Calendar.SECOND));
		assertEquals(expectedDate.get(Calendar.YEAR), actualEndDate.get(Calendar.YEAR));
		assertEquals(expectedDate.get(Calendar.MONTH), actualEndDate.get(Calendar.MONTH));
		assertEquals(expectedDate.get(Calendar.DAY_OF_MONTH)+2, actualEndDate.get(Calendar.DAY_OF_MONTH));
//		assertEquals(23, actualEndDate.get(Calendar.HOUR_OF_DAY));
//		assertEquals(59, actualEndDate.get(Calendar.MINUTE));
//		assertEquals(59, actualEndDate.get(Calendar.SECOND));
	}
}