package com.gpigc.core.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.awt.Toolkit;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

import java.awt.Font;
import java.awt.Color;

import javax.swing.border.LineBorder;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.ScrollPaneConstants;

public class CoreGUI extends JFrame {

	private final JPanel contentPane;
	private final JButton btnStart;
	private final JTextArea consoleArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CoreGUI frame = new CoreGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CoreGUI() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				CoreGUI.class.getResource("/com/gpigc/core/view/nosql.png")));
		setTitle("GPIG-C: HUMS CORE");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 700, 450);

		try {
			UIManager.setLookAndFeel(UIManager
					.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		contentPane.add(panel, BorderLayout.CENTER);

		Component verticalStrut = Box.createVerticalStrut(20);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(250, 250, 250));
		panel_1.setBorder(new LineBorder(new Color(217, 91, 67), 7, true));

		JLabel imageLabel = new JLabel(new ImageIcon(
				CoreGUI.class.getResource("/com/gpigc/core/view/nosql.png")));
		imageLabel.setFont(new Font("Lucida Grande", Font.BOLD, 30));

		JLabel lblCoreSystemGui = new JLabel("Core System GUI");
		lblCoreSystemGui.setFont(new Font("Lucida Grande", Font.BOLD, 30));
		lblCoreSystemGui.setHorizontalAlignment(SwingConstants.CENTER);

		consoleArea = new JTextArea();

		JScrollPane panel_2 = new JScrollPane(getConsoleArea());
		panel_2.setViewportBorder(new LineBorder(new Color(255, 255, 255), 6,
				true));
		panel_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel_2.setBackground(Color.WHITE);
		panel_2.setBorder(new LineBorder(new Color(83, 119, 122), 7, true));

		btnStart = new JButton("Start");
		getBtnStart().setForeground(Color.WHITE);
		getBtnStart().setBackground(new Color(192, 41, 66));
		getBtnStart().setOpaque(true);
		getBtnStart().setIcon(null);

		getBtnStart().setFont(new Font("Lucida Grande", Font.BOLD, 16));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addComponent(
														verticalStrut,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(154)
																.addComponent(
																		getBtnStart(),
																		GroupLayout.PREFERRED_SIZE,
																		407,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(14)
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.LEADING)
																				.addComponent(
																						panel_2,
																						GroupLayout.DEFAULT_SIZE,
																						670,
																						Short.MAX_VALUE)
																				.addComponent(
																						panel_1,
																						GroupLayout.DEFAULT_SIZE,
																						670,
																						Short.MAX_VALUE))))
								.addContainerGap(16, GroupLayout.PREFERRED_SIZE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_panel.createSequentialGroup()
						.addComponent(verticalStrut,
								GroupLayout.PREFERRED_SIZE, 0,
								GroupLayout.PREFERRED_SIZE)
						.addGap(25)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
						.addGap(33)
						.addComponent(getBtnStart(),
								GroupLayout.PREFERRED_SIZE, 39,
								GroupLayout.PREFERRED_SIZE)
						.addGap(51)
						.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 172,
								Short.MAX_VALUE).addContainerGap()));
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(
				Alignment.LEADING).addGroup(
				gl_panel_1
						.createSequentialGroup()
						.addContainerGap()
						.addComponent(imageLabel)
						.addGap(43)
						.addComponent(lblCoreSystemGui,
								GroupLayout.PREFERRED_SIZE, 373,
								GroupLayout.PREFERRED_SIZE).addGap(128)));
		gl_panel_1
				.setVerticalGroup(gl_panel_1
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								Alignment.TRAILING,
								gl_panel_1
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_panel_1
														.createParallelGroup(
																Alignment.TRAILING)
														.addComponent(
																imageLabel)
														.addComponent(
																lblCoreSystemGui,
																GroupLayout.DEFAULT_SIZE,
																46,
																Short.MAX_VALUE))
										.addContainerGap()));
		panel_1.setLayout(gl_panel_1);
		panel.setLayout(gl_panel);
	}

	public JButton getBtnStart() {
		return btnStart;
	}

	public JTextArea getConsoleArea() {
		return consoleArea;
	}
}
