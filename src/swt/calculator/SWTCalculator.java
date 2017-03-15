package swt.calculator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

// TODO add javadocs to all public constructors and methods
public class SWTCalculator {

	private Shell shell;
	private double resultValue;
	private Text result;
	private History history;
	private Composite composite;
	private TabFolder tabFolder;

	public SWTCalculator(Display display) {
		initUI(display);
	}

	private void initUI(Display display) {
		shell = new Shell(display, SWT.SHELL_TRIM | SWT.CENTER);
		shell.setText("SWT Calculator");
		shell.setMinimumSize(310, 350);
		
		shell.addListener(SWT.Resize, event -> resizeTab(event));

		tabFolder = new TabFolder(shell, SWT.NONE);

		makeCalculatorTab(tabFolder);
		history = new History(tabFolder);

		tabFolder.pack();
		shell.pack();
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private void resizeTab(Event event) {
		tabFolder.setSize(shell.getSize().x - 15, shell.getSize().y - 15);
	}

	private void makeCalculatorTab(TabFolder tabFolder) {

		TabItem item1 = new TabItem(tabFolder, SWT.NONE);
		item1.setText("Calculator");

		composite = new Composite(tabFolder, SWT.NONE);
		GridLayout grid = new GridLayout(3, false);
		composite.setLayout(grid);

		Text textField1 = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textField1.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false)); 
		textField1.addListener(SWT.Verify, event -> Verifier.verifyDigits(event));

		Combo dropDownList = new Combo(composite, SWT.VERTICAL | SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		dropDownList.add(Operations.SUM.getLiteral());
		dropDownList.add(Operations.SUBSTRACTION.getLiteral());
		dropDownList.add(Operations.DIVISION.getLiteral());
		dropDownList.add(Operations.MULTIPLICATION.getLiteral());
		dropDownList.select(0);
		dropDownList.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false)); 

		Text textField2 = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textField2.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		textField2.addListener(SWT.Verify, event -> Verifier.verifyDigits(event));

		item1.setControl(composite);

		Button checkButton = new Button(composite, SWT.CHECK);
		checkButton.setText("Calculate on the fly");
		checkButton.setSelection(false);

		GridData gridData1 = new GridData(SWT.LEFT, SWT.TOP, true, false);
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.horizontalSpan = 2;
		checkButton.setLayoutData(gridData1);

		Button calculateButton = new Button(composite, SWT.PUSH);
		calculateButton.setText("Calculate");
		calculateButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		calculateButton.addListener(SWT.Selection, event -> calculate(textField1, textField2, dropDownList));
		checkButton.addListener(SWT.Selection,
				event -> checkButtonSelected(checkButton, calculateButton, dropDownList, textField1, textField2));

		setResultLabel(composite);
	}

	private void setResultLabel(Composite composite) {
		Label label = new Label(composite, SWT.LEFT);
		label.setText("Result: ");

		result = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
		GridData resultGridData = new GridData();
		resultGridData.horizontalAlignment = GridData.FILL;
		resultGridData.horizontalSpan = 2;
		result.setLayoutData(resultGridData);
	}

	private void calculate(Text textField1, Text textField2, Combo dropDownList) {

		if (Verifier.isDigit(textField1.getText()) && Verifier.isDigit(textField2.getText())) {
			double number1 = Double.parseDouble(textField1.getText());
			double number2 = Double.parseDouble(textField2.getText());
			
			if (Verifier.numLenght(number1) > 17 | Verifier.numLenght(number2) > 17) {
				calculateBigDecimals(number1, number2, dropDownList);
			}
			
			switch (Operations.get(dropDownList.getText())) {
			case SUM: {
				resultValue = number1 + number2;
				result.setText(String.valueOf(resultValue));
				history.printHistory(number1, number2, resultValue, Operations.SUM);
				break;
			}
			case SUBSTRACTION: {
				resultValue = number1 - number2;
				result.setText(String.valueOf(resultValue));
				history.printHistory(number1, number2, resultValue, Operations.SUBSTRACTION);
				break;
			}
			case DIVISION: {
				resultValue = number1 / number2;
				result.setText(String.valueOf(resultValue));
				history.printHistory(number1, number2, resultValue, Operations.DIVISION);
				break;
			}
			case MULTIPLICATION: {
				resultValue = number1 * number2;
				result.setText(String.valueOf(resultValue));
				history.printHistory(number1, number2, resultValue, Operations.MULTIPLICATION);
				break;
			}
			default: {
				System.out.println("No such operation!");
			}
			}

		}
	}
	
	private void calculateBigDecimals(double number1, double number2, Combo dropDownList) {
		// TODO finish it
	}

	private void checkButtonSelected(Button checkButton, Button calculateButton, Combo dropDownList, Text textField1,
			Text textField2) {
		if (checkButton.getSelection()) {
			calculateButton.setEnabled(false);
			dropDownList.addListener(SWT.Selection, event -> calculate(textField1, textField2, dropDownList));
		} else {
			calculateButton.setEnabled(true);
			// remove on the fly calculation
			if (dropDownList.isListening(SWT.Selection)) {
				dropDownList.removeListener(SWT.Selection, dropDownList.getListeners(SWT.Selection)[0]);
			}
		}
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) {

		Display display = new Display();
		SWTCalculator calculator = new SWTCalculator(display);
		display.dispose();
	}

}
