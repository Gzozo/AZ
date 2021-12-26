package AZ;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.json.JSONObject;

import jdk.net.*;

public class Main implements Runnable
{
	GamePanel game;
	JFrame openingFrame;
	ButtonGroup tanks;
	
	public void run()
	{
		
		openingFrame = new JFrame("Selection");
		openingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton client = new JButton("Client");
		JButton server = new JButton("Server");
		JPanel buttons = new JPanel(), topeast = new JPanel(), topwest = new JPanel(), top = new JPanel();
		JTextField serverIP = new JTextField(16), serverPort = new JTextField(5), createServerPort = new JTextField(5);
		JLabel serverIPLabel = new JLabel("Szerver IP címe:"), serverPortLabel = new JLabel("Szerver portja: "),
				createServerPortLabel = new JLabel("Szerver nytása ezen a porton:");
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Settings");
		JMenuItem controls = new JMenuItem("Controls");
		
		JPanel list = new JPanel();
		list.setLayout(new GridLayout());
		tanks = new ButtonGroup();
		for (int i = 0; i < Server.pics.length; i++)
		{
			try
			{
				JRadioButtonMenuItem b = new JRadioButtonMenuItem(
						new ImageIcon(ImageIO.read(Main.class.getResource(Const.Resources + Server.pics[i]))
								.getScaledInstance(50, 75, Image.SCALE_DEFAULT)));
				b.setSelected(i == 0);
				b.setActionCommand(i + "");
				tanks.add(b);
				list.add(b);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		
		try
		{
			BufferedReader br = new BufferedReader(
					new FileReader(new File(Main.class.getResource(Const.ConfigFile).toURI())));
			String read = br.readLine(), file = "";
			while (read != null)
			{
				file += read;
				read = br.readLine();
			}
			br.close();
			JSONObject json = new JSONObject(file);
			serverIP.setText(json.getString(Const.ServerIP));
			serverPort.setText(json.getString(Const.ServerPort));
			createServerPort.setText(json.getString(Const.ClientPort));
			
		}
		catch (IOException | URISyntaxException e2)
		{
			e2.printStackTrace();
		}
		
		class Listener implements ActionListener
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getActionCommand().equals("Server"))
				{
					Server s = new Server(Integer.valueOf(createServerPort.getText()));
					s.start();
				}
				else if (e.getActionCommand().equals("Client"))
				{
					Client(serverIP.getText(), Integer.valueOf(serverPort.getText()));
				}
				else if (e.getActionCommand().equals("Controls"))
				{
					Control();
				}
				
			}
		}
		client.addActionListener(new Listener());
		server.addActionListener(new Listener());
		controls.setActionCommand("Controls");
		controls.addActionListener(new Listener());
		
		topwest.add(serverIPLabel);
		topwest.add(serverIP);
		topwest.add(serverPortLabel);
		topwest.add(serverPort);
		topeast.add(createServerPortLabel);
		topeast.add(createServerPort);
		buttons.add(client);
		buttons.add(server);
		menu.add(controls);
		bar.add(menu);
		
		top.add(topwest);
		top.add(topeast);
		openingFrame.setJMenuBar(bar);
		openingFrame.add(top, BorderLayout.NORTH);
		openingFrame.add(buttons, BorderLayout.SOUTH);
		openingFrame.add(list, BorderLayout.CENTER);
		
		openingFrame.setVisible(true);
		openingFrame.pack();
		openingFrame.addWindowListener(new WindowAdapter()
		{
			
			@Override
			public void windowClosing(WindowEvent e)
			{
				try
				{
					File f = new File(Main.class.getResource(Const.ConfigFile).toURI());
					FileWriter fw = new FileWriter(f);
					JSONObject json = new JSONObject();
					json.put(Const.ServerIP, serverIP.getText());
					json.put(Const.ServerPort, serverPort.getText());
					json.put(Const.ClientPort, createServerPort.getText());
					fw.write(json.toString());
					fw.close();
				}
				catch (IOException | URISyntaxException e1)
				{
					e1.printStackTrace();
				}
				super.windowClosing(e);
			}
			
		});
		openingFrame.requestFocus();
		
	}
	
	ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public void Control()
	{
		JFrame frame = new JFrame("Controls");
		JTable table = new JTable(new Controls());
		table.setColumnModel(new Controls.ColumnModel());
		table.createDefaultColumnsFromModel();
		table.setFillsViewportHeight(true);
		table.setCellEditor(new Controls.KeyCellEditor());
		JScrollPane pane = new JScrollPane(table);
		frame.add(pane, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
		frame.pack();
	}
	
	public void Client(String IpAddress, int port)
	{
		
		DatagramSocket test = null;
		int testPort = 6969;
		while (test == null)
		{
			try
			{
				test = new DatagramSocket(testPort);
			}
			catch (SocketException e)
			{
				test = null;
				testPort++;
			}
		}
		JSONObject json = new JSONObject();
		json.put(Const.test, "");
		JDialog tester = new JDialog((Window) null);
		tester.setTitle("Test server");
		tester.add(new JLabel("Waiting for server response"));
		tester.setLocationRelativeTo(openingFrame);
		tester.setVisible(true);
		try
		{
			DatagramPacket dp = new DatagramPacket(json.toString().getBytes(), json.toString().getBytes().length,
					Inet4Address.getByName(IpAddress), port);
			test.setSoTimeout(5000);
			test.send(dp);
			test.receive(dp);
			json = new JSONObject(new String(dp.getData()));
			if (!json.has(Const.test))
			{
				throw new IOException("No test key in response JSON");
			}
			tester.setVisible(false);
		}
		catch (IOException e)
		{
			tester.setVisible(false);
			e.printStackTrace();
			JOptionPane.showMessageDialog(openingFrame, "The server is not responding");
			return;
		}
		
		JFrame frame = new JFrame("AZ alpha");
		try
		{
			// game = new GamePanel(Inet4Address.getByName("152.66.180.194"), 6666, 6969);
			
			int select = Integer.valueOf(tanks.getSelection().getActionCommand());
			game = new GamePanel(Inet4Address.getByName(IpAddress), port, 6969, Server.pics[select]);
			
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		frame.add(game);
		game.InitAfterFrame();
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.addKeyListener(game.new KeyboardListener());
		openingFrame.setFocusableWindowState(false);
		frame.requestFocus();
		scheduler.schedule(() -> openingFrame.setFocusableWindowState(true), 1, TimeUnit.SECONDS);
	}
	
	public static void main(String[] args)
	{
		try
		{
			ImageIO.read(Main.class.getResource("/resources/p1.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Main m = new Main();
		javax.swing.SwingUtilities.invokeLater(m);
		
	}
	
}
