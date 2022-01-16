package AZ;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
import javax.swing.*;
import javax.swing.table.TableRowSorter;

import org.json.JSONException;
import org.json.JSONObject;

import jdk.net.*;

@SuppressWarnings("ConstantConditions")
public class Main implements Runnable
{
    JFrame openingFrame;
    ButtonGroup tanks;
    String title = "AZ v0.3 beta";
    
    /**
     * Létrehozza a framet, és feltölti a Componensekkel
     * Beolvassa a config fileból a kezdő értékeket
     */
    public void run()
    {
        
        openingFrame = new JFrame("Selection");
        ImageIcon img = new ImageIcon(Main.class.getResource(Const.Resources + Const.IconFile));
        openingFrame.setIconImage(img.getImage());
        openingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JButton client = new JButton("Client");
        JButton server = new JButton("Server");
        JPanel buttons = new JPanel(), topEast = new JPanel(), topWest = new JPanel(), topCenter = new JPanel(), top
                = new JPanel();
        JTextField serverIP = new JTextField("152.66.180.194", 16), serverPort = new JTextField("6666", 5),
                createServerPort = new JTextField("6666", 5), playerNameText = new JTextField("Player", 16);
        JLabel serverIPLabel = new JLabel("Szerver IP címe:"), serverPortLabel = new JLabel("Szerver portja: "),
                createServerPortLabel = new JLabel("Szerver nyitása ezen a porton:"), playerName = new JLabel("Játékos név: ");
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Settings");
        JMenuItem controls = new JMenuItem("Controls");
        
        JPanel list = new JPanel();
        list.setLayout(new GridLayout());
        tanks = new ButtonGroup();
        int selected = 0;
        try
        {
            System.out.println(Const.ConfigFile);
            //BufferedReader br = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(Const
            // .ConfigFile)));
            BufferedReader br = new BufferedReader(new FileReader(Const.ConfigFile));
            String read = br.readLine(), file = "";
            while(read != null)
            {
                file += read;
                read = br.readLine();
            }
            br.close();
            JSONObject json = new JSONObject(file);
            serverIP.setText(json.optString(Const.ServerIP));
            serverPort.setText(json.optString(Const.ServerPort));
            createServerPort.setText(json.optString(Const.ClientPort));
            playerNameText.setText(json.optString(Const.playerName));
            selected = json.optInt(Const.Selected, 0);
            Controls.ReadJSON(json);
            
        }
        catch(IOException | IllegalArgumentException | JSONException e2)
        {
            e2.printStackTrace();
        }
        for(int i = 0; i < Server.pics.length; i++)
        {
            try
            {
                JRadioButtonMenuItem b =
                        new JRadioButtonMenuItem(new ImageIcon(ImageIO.read(Main.class.getResource(Const.Resources + Server.pics[i])).getScaledInstance(50, 75, Image.SCALE_DEFAULT)));
                b.setSelected(i == selected);
                b.setActionCommand(i + "");
                tanks.add(b);
                list.add(b);
            }
            catch(IOException e1)
            {
                e1.printStackTrace();
            }
        }
        
        class Listener implements ActionListener
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(e.getActionCommand().equals("Server"))
                {
                    String createServerPortText = createServerPort.getText();
                    if(createServerPortText == null || createServerPortText.equals(""))
                    {
                        return;
                    }
                    Server s = new Server(Integer.parseInt(createServerPortText));
                    s.start();
                    JFrame f = new JFrame("Server");
                    f.add(new JLabel("Listening on Port " + s.ReceivePort), BorderLayout.CENTER);
                    f.setLocationRelativeTo(openingFrame);
                    f.setSize(240, 100);
                    f.setVisible(true);
                    f.addWindowListener(new WindowAdapter()
                    {
                        
                        @Override
                        public void windowClosing(WindowEvent e)
                        {
                            s.dying = true;
                            s.server.disconnect();
                            super.windowClosing(e);
                        }
                        
                    });
                    openingFrame.requestFocus();
                }
                else if(e.getActionCommand().equals("Client"))
                {
                    String text = serverPort.getText();
                    if(text == null || text.equals(""))
                    {
                        return;
                    }
                    Client(serverIP.getText(), Integer.parseInt(text), playerNameText.getText());
                }
                else if(e.getActionCommand().equals("Controls"))
                {
                    Control();
                }
                
            }
        }
        client.addActionListener(new Listener());
        server.addActionListener(new Listener());
        controls.setActionCommand("Controls");
        controls.addActionListener(new Listener());
        
        topWest.add(serverIPLabel);
        topWest.add(serverIP);
        topWest.add(serverPortLabel);
        topWest.add(serverPort);
        
        topEast.add(createServerPortLabel);
        topEast.add(createServerPort);
        
        topCenter.add(playerName);
        topCenter.add(playerNameText);
        
        buttons.add(client);
        buttons.add(server);
        
        menu.add(controls);
        
        bar.add(menu);
        
        top.setLayout(new BorderLayout());
        JPanel toptop = new JPanel();
        toptop.add(topWest);
        toptop.add(topEast);
        top.add(toptop, BorderLayout.NORTH);
        top.add(topCenter, BorderLayout.CENTER);
        
        openingFrame.setJMenuBar(bar);
        openingFrame.add(top, BorderLayout.NORTH);
        openingFrame.add(buttons, BorderLayout.SOUTH);
        openingFrame.add(list, BorderLayout.CENTER);
        
        openingFrame.setVisible(true);
        openingFrame.pack();
        
        openingFrame.addWindowListener(new WindowAdapter()
        {
            
            void Close()
            {
                File f = new File(Const.ConfigFile);
                try
                {
                    FileWriter fw = new FileWriter(f);
                    JSONObject json = new JSONObject();
                    json.put(Const.ServerIP, serverIP.getText());
                    json.put(Const.ServerPort, serverPort.getText());
                    json.put(Const.ClientPort, createServerPort.getText());
                    json.put(Const.playerName, playerNameText.getText());
                    int select = Integer.parseInt(tanks.getSelection().getActionCommand());
                    json.put(Const.Selected, select);
                    Controls.WriteJSON(json);
                    fw.write(json.toString());
                    fw.close();
                }
                catch(IOException e1)
                {
                    e1.printStackTrace();
                    if(f.getParentFile().mkdirs())
                        Close();
                }
                
            }
            
            @Override
            public void windowClosing(WindowEvent e)
            {
                Close();
                super.windowClosing(e);
            }
            
        });
        openingFrame.requestFocus();
        // SoundManager.PlaySound(Const.Music.RainsOfCastemere);
        
    }
    
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    
    /**
     * Létrehozza az irányítás testreszabásához a framet
     */
    public void Control()
    {
        JFrame frame = new JFrame("Controls");
        Controls c = new Controls(frame);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(openingFrame);
        frame.setVisible(true);
        frame.pack();
    }
    
    /**
     * Létrehoz egy kliens framet
     *
     * @param IpAddress A szerver gép IP címe
     * @param port      A szerver port száma
     */
    public void Client(String IpAddress, int port, String name)
    {
        GamePanel game;
        DatagramSocket test = null;
        int testPort = 6969;
        while(test == null)
        {
            try
            {
                test = new DatagramSocket(testPort);
            }
            catch(SocketException e)
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
            if(!json.has(Const.test))
            {
                throw new IOException("No test key in response JSON");
            }
            tester.setVisible(false);
        }
        catch(IOException e)
        {
            tester.setVisible(false);
            e.printStackTrace();
            JOptionPane.showMessageDialog(openingFrame, "The server is not responding");
            return;
        }
        
        JFrame frame = new JFrame(title);
        ImageIcon img = new ImageIcon(Main.class.getResource(Const.Resources + Const.IconFile));
        frame.setIconImage(img.getImage());
        try
        {
            // game = new GamePanel(Inet4Address.getByName("152.66.180.194"), 6666, 6969);
            
            int select = Integer.parseInt(tanks.getSelection().getActionCommand());
            game = new GamePanel(Inet4Address.getByName(IpAddress), port, 6969, Server.pics[select], name);
            
        }
        catch(IOException e1)
        {
            e1.printStackTrace();
            return;
        }
        JLabel gameState = new JLabel("", SwingConstants.CENTER);
        JTable table = new JTable();
        gameState.setFont(new Font("Serif", Font.PLAIN, 20));
        // JPanel panel = new JPanel();
        // panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        game.state = gameState;
        // panel.add(gameState);
        // frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(),
        // BoxLayout.Y_AXIS));
        frame.add(gameState, BorderLayout.SOUTH);
        frame.add(game, BorderLayout.CENTER);
        game.InitAfterFrame();
        game.table = table;
        table.setModel(new StatTableModel(game));
        table.setFillsViewportHeight(true);
        JScrollPane pane = new JScrollPane(table);
        frame.add(pane, BorderLayout.EAST);
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // frame.setSize(game.getWidth(), game.getHeight() + gameState.getHeight() +
        // frame.getHeight() + 10);
        frame.setVisible(true);
        // frame.setResizable(false);
        frame.addKeyListener(game.new KeyboardListener());
        table.addKeyListener(game.new KeyboardListener());
        /*frame.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {
                System.out.println(frame.getSize().toString());
            }
        });*/
        //frame.pack();
        frame.setSize(1272, 868);
        openingFrame.setFocusableWindowState(false);
        frame.requestFocus();
        // System.out.println(frame.getWidth() + " " + frame.getHeight());
        // System.out.println(game.getWidth() + " " + game.getHeight());
        scheduler.schedule(() -> openingFrame.setFocusableWindowState(true), 1, TimeUnit.SECONDS);
    }
    
    public static void main(String[] args)
    {
        try
        {
            ImageIO.read(Main.class.getResource("/resources/p1.png"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        Main m = new Main();
        javax.swing.SwingUtilities.invokeLater(m);
        
    }
    
}
