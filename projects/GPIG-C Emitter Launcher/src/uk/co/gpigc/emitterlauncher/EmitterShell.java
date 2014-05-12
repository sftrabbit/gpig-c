package uk.co.gpigc.emitterlauncher;


import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class EmitterShell extends Shell{


	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			EmitterShell shell = new EmitterShell(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private final StyledText consoleView;
	private final Button serverButton;
	private final Button earthButton;
	private final Button testApp1Button;
	private final Button testApp2Button;

	public EmitterShell(Display display) {
		super(display);
		setImage(SWTResourceManager.getImage(EmitterShell.class, "/uk/co/gpigc/emitterlauncher/images/nosql.png"));
		setSize(600, 300);
		setMinimumSize(600, 300);
		setText("GPIG-C: Launcher");
		FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
		fillLayout.marginWidth = 10;
		fillLayout.marginHeight = 10;
		setLayout(fillLayout);

		Group grpRun = new Group(this, SWT.NONE);
		grpRun.setText("Run");
		grpRun.setLayout(new GridLayout(1, false));

		Label lblNewLabel = new Label(grpRun, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setText("\nClick to run the selected emmiter, click again to stop. \n ");
		GridData labelData = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
		labelData.horizontalAlignment = SWT.FILL;
		labelData.verticalAlignment = SWT.FILL;
		lblNewLabel.setLayoutData(labelData);
		
		Label label = new Label(grpRun, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData sepData = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
		sepData.horizontalAlignment = SWT.FILL;
		sepData.verticalAlignment = SWT.FILL;
		label.setLayoutData(sepData);
		
		
		Composite composite = new Composite(grpRun, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData buttonData = new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1);
		buttonData.horizontalAlignment = SWT.FILL;
		buttonData.verticalAlignment = SWT.FILL;
		composite.setLayoutData(buttonData);
		
		testApp2Button = new Button(composite, SWT.TOGGLE);
		testApp2Button.setImage(SWTResourceManager.getImage(EmitterShell.class, "/uk/co/gpigc/emitterlauncher/images/control_play_blue.png"));
		testApp2Button.setText("Test App 2");
		testApp2Button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});

		testApp1Button = new Button(composite, SWT.TOGGLE);
		testApp1Button.setImage(SWTResourceManager.getImage(EmitterShell.class, "/uk/co/gpigc/emitterlauncher/images/control_play_blue.png"));
		testApp1Button.setText("Test App 1");

		earthButton = new Button(composite, SWT.TOGGLE);
		earthButton.setImage(SWTResourceManager.getImage(EmitterShell.class, "/uk/co/gpigc/emitterlauncher/images/control_play_blue.png"));
		earthButton.setText("Earthquake Emitter");
		
		serverButton = new Button(composite, SWT.NONE);
		serverButton.setImage(SWTResourceManager.getImage(EmitterShell.class, "/uk/co/gpigc/emitterlauncher/images/control_play_blue.png"));
		serverButton.setText("Server Emitter");

		consoleView = new StyledText(grpRun, SWT.BORDER  | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gd_consoleView = new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1);
		gd_consoleView.horizontalAlignment = SWT.FILL;
		gd_consoleView.verticalAlignment = SWT.FILL;
		consoleView.setLayoutData(gd_consoleView);
		consoleView.setEditable(false);

	}
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}

