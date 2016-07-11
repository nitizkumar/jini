package com.jini.server;

import com.jini.FileUtils;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class MainServer extends JFrame {
	private Server server;
	private String initialDir;
	public static Properties appProp;

	public MainServer(String initialDir) {
		this.initialDir = initialDir;
		initialize();
	}

	private void initialize() {
		JLabel label = new JLabel("Path");
		final JTextField textField = new JTextField();
		final JFileChooser fc = new JFileChooser();
		JButton browseButton = new JButton("Browse");
		final JButton findButton = new JButton("Start");
		JButton cancelButton = new JButton("Close");
		JButton staticBuildButton = new JButton("Build Static");
		JTextArea jTextArea = new JTextArea();
		jTextArea.setWrapStyleWord(true);
		MessageBox.setTextArea(jTextArea);
		setTitle("Jini");
		if (this.initialDir != null) {
			textField.setText(this.initialDir);
			System.out.println(this.initialDir);
		}
		fc.setFileSelectionMode(1);

		browseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(MainServer.this);
				if (returnVal == 0) {
					File file = fc.getSelectedFile();
					textField.setText(file.getAbsolutePath());
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		staticBuildButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final String dir = textField.getText();
				try {
					Thread t = new Thread(new Runnable() {
						public void run() {
							try {
								new Exporter().exportStatic(dir);
							} catch (Exception e) {
								e.printStackTrace();
							}
							MessageBox
									.addMessage("Static files created in build folder");
						}
					});
					t.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		findButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (MainServer.this.server != null) {
					try {
						textField.setText("");
						textField.setEnabled(true);
						MainServer.this.server.stop();
						MessageBox.addMessage("Server stopped");
						MainServer.this.server = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
					findButton.setText("Start");
				} else {
					Thread t = new Thread(new Runnable() {
						public void run() {
							try {
								WebAppContext context = new WebAppContext();
								context.setDescriptor("./WEB-INF/web.xml");
								context.setResourceBase("./webapp");
								context.setContextPath("/jini");

								String text = textField.getText();
								try {
									String homeDir = System
											.getProperty("user.home");

									File prefFile = new File(homeDir,
											"Jini.pref");

									FileUtils.writeToFile(prefFile, text);

									MainServer.appProp = new Properties();
									File propFile = new File(text,
											"Jini.properties");
									if (propFile.exists()) {
										MainServer.appProp
												.load(new FileInputStream(
														propFile));
									} else {
										System.err
												.println("No Properties File present");
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								int portNumber = 9090;
								while (MainServer.this
										.isPortOccupied(portNumber)) {
									portNumber++;
								}
								MainServer.this.server = new Server(portNumber);

								CustomResourceHandler customResourceHandler = new CustomResourceHandler(
										text);

								context.setServer(MainServer.this.server);

								HandlerCollection handler = new HandlerCollection();
								handler.addHandler(customResourceHandler);

								MainServer.this.server
										.setHandler(customResourceHandler);
								MainServer.this.server.start();
								MessageBox.addMessage("Started Server on port "
										+ portNumber);

								MainServer.this.server.join();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					textField.setEnabled(false);
					t.start();
					findButton.setText("Stop");
				}
			}
		});
		textField.setPreferredSize(new Dimension(200, 20));

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup col1 = layout.createSequentialGroup()
				.addComponent(label).addComponent(textField)
				.addComponent(browseButton);

		jTextArea.setPreferredSize(new Dimension(200, 200));

		GroupLayout.SequentialGroup col2 = layout.createSequentialGroup()
				.addComponent(jTextArea);

		GroupLayout.SequentialGroup col3 = layout.createSequentialGroup()
				.addComponent(findButton).addComponent(cancelButton)
				.addComponent(staticBuildButton);

		layout.setHorizontalGroup(layout.createParallelGroup().addGroup(col1)
				.addGroup(col2).addGroup(col3));

		GroupLayout.ParallelGroup rrow1 = layout.createParallelGroup()
				.addComponent(label).addComponent(textField)
				.addComponent(browseButton);

		GroupLayout.ParallelGroup rrow2 = layout.createParallelGroup()
				.addComponent(jTextArea);

		GroupLayout.ParallelGroup rrow3 = layout.createParallelGroup()
				.addComponent(findButton).addComponent(cancelButton)
				.addComponent(staticBuildButton);

		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(rrow1)
				.addGroup(rrow2).addGroup(rrow3));

		pack();
		setDefaultCloseOperation(3);

		CustomLogger.getInstance().setLogFile(new File(initialDir, ".work/resource.log"));
	}

	public boolean isPortOccupied(int _port) {
		boolean portTaken = false;
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(_port);

			return portTaken;
		} catch (IOException e) {
			portTaken = true;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return portTaken;
	}

	public static void main(String[] args) throws Exception {
		try {
			String homeDir = System.getProperty("user.home");
			File prefFile = new File(homeDir, "Jini.pref");
			if (prefFile.exists()) {
				String readToString = FileUtils.readToString(prefFile);
				args = new String[] { readToString.trim() };
			} else {
				prefFile.createNewFile();
			}
		} catch (Exception e) {
		}
		String initialDir = null;
		if (args.length != 0) {
			initialDir = args[0];
		}
		final String dir = initialDir;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getCrossPlatformLookAndFeelClassName());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				new MainServer(dir).setVisible(true);
			}
		});
	}

}
