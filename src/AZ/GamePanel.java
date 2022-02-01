package AZ;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serial;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Kliens
 */
@SuppressWarnings("ALL")
public class GamePanel extends JPanel implements GameManager
{
    
    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GameManager.class.getName());
    
    //ArrayList<Tank> players = new ArrayList<>();
    HashMap<String, Tank> players = new HashMap<>();
    ArrayList<GameEntity> entities = new ArrayList<>();
    ArrayList<GameEntity> localEffects = new ArrayList<>();
    ReentrantLock lock = new ReentrantLock();
    Tank activePlayer = new Tank();
    Field f;
    int width = 20, height = 20;
    int _tankWidth = 25, _tankHeight = 35;
    int _gridSize = 50;
    AtomicInteger gridSize = new AtomicInteger(_gridSize);
    private final Dimension d = new Dimension(1000, 800);
    int x, y;
    
    DatagramSocket client;
    int ReceivePort;
    DatagramPacket dp;
    final int packetSize = 1024 * 1024;
    InetAddress serverIp;
    int serverPort;
    
    boolean dead = false;
    String tankPic, name;
    Konami k = new Konami();
    
    public JLabel state = null;
    JLabel ammoType, ammoCount;
    public JTable table = new JTable();
    //public TreeMap<String, Client> stats = new TreeMap<String, Client>(new Sort());
    public TreeSet<Client> stats = new TreeSet<>(new Sort());
    
    public GamePanel(InetAddress serverIp, int serverPort, int port) throws IOException
    {
        this(serverIp, serverPort, port, "", "");
    }
    
    public GamePanel(InetAddress serverIp, int serverPort, int port, String pic, String name) throws IOException
    {
        super();
        tankPic = pic;
        
        ReceivePort = port;
        while(client == null)
        {
            try
            {
                client = new DatagramSocket(ReceivePort);
            }
            catch(SocketException e)
            {
                client = null;
                ReceivePort++;
            }
        }
        this.serverPort = serverPort;
        this.serverIp = serverIp;
        JSONObject send = new JSONObject();
        send.put(Const.join, tankPic);
        send.put(Const.name, name);
        dp = new DatagramPacket(send.toString().getBytes(), send.toString().getBytes().length, serverIp, serverPort);
        client.send(dp);
        byte[] buf = new byte[packetSize];
        
        dp.setData(buf);
        dp.setLength(buf.length);
        
        LOGGER.log(Level.FINE, client.getLocalPort() + " " + serverIp);
        //Log.log(client.getLocalPort() + " " + serverIp);
        
        client.receive(dp);
        JSONObject receive = new JSONObject(new String(buf));
        // Config(receive.getJSONObject("Config"));
        processData(receive);
        LOGGER.log(Level.FINE, "Config Done");
        //Log.log("Config Done");
        
        // Panel settings
        setSize(d);
        setPreferredSize(getSize());
        setMinimumSize(getSize());
        
        //
        
        // Add Listeners
        // addKeyListener(new KeyboardListener());
        
        /*
         * players.add(new Tank(10, 10, _tankWidth * gridSize.intValue() / _gridSize,
         * _tankHeight * gridSize.intValue() / _gridSize, "p1.png", f, gridSize, this));
         * activePlayer = players.get(0);
         */
        
        new Thread(this::Listening).start();
        Thread t = new Thread(() ->
        {
            while(true)
            {
                try
                {
                    Tick();
                    Thread.sleep(17);
                }
                catch(SocketException e)
                {
                    break;
                }
                catch(InterruptedException e)
                {
                    e.printStackTrace();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.setName("GamePanelLoop");
        t.start();
        
        
    }
    
    private BufferedImage image;
    private BufferedImage maze;
    public BufferedImage moving;
    public BufferedImage effects;
    
    /**
     * JSON config objektumból betölti az adatokat
     *
     * @param config A JSON objektum
     */
    public void Config(JSONObject config)
    {
        gridSize.set(config.getInt(Const.gridSize));
        width = config.getInt(Const.width);
        height = config.getInt(Const.height);
        f = new Field(width, height, gridSize);
        f.GenerateField(config.getLong(Const.labirintusSeed));
        _tankWidth = config.getInt(Const.tankWidth);
        _tankHeight = config.getInt(Const.tankHeight);
        d.setSize(config.getInt(Const.panelWidth), config.getInt(Const.panelHeight));
        name = config.getString(Const.name);
        setSize(d);
        setPreferredSize(getSize());
        maze = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        maze.getGraphics().setColor(Color.white);
        maze.getGraphics().fillRect(0, 0, d.width, d.height);
        f.draw(maze.getGraphics(), d);
        image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        moving = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        effects = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        pixels = new int[(int) (d.getWidth() * d.getHeight())];
        activePlayer = new Tank(_tankWidth * gridSize.intValue() / _gridSize,
                _tankHeight * gridSize.intValue() / _gridSize);
        players.put(name, activePlayer);
        activePlayer.f = f;
        activePlayer.setFromJSON(config.getJSONObject(Const.Tank));
        activePlayer.gridSize = gridSize;
        Arrays.fill(pixels, 0);
    }
    
    /**
     * Az ablak bezárásakkor lefutó esemény beállítása
     */
    public void InitAfterFrame(JFrame topFrame)
    {
        topFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                OnClosing();
                super.windowClosing(e);
            }
        });
        topFrame.setTitle(topFrame.getTitle() + " " + name);
        topFrame.requestFocus();
        ammoType = (JLabel) ESwing.getComponent(topFrame, "ammoType");
        ammoCount = (JLabel) ESwing.getComponent(topFrame, "ammoCount");
        RefreshAmmoLabels();
    }
    
    private void RefreshAmmoLabels()
    {
        ammoType.setText(activePlayer.ammo.name);
        ammoCount.setText(activePlayer.ammo.shellCount + "");
    }
    
    /**
     * Folyton figyeli, hogy jött e üzenet, és feldolgozza őket
     */
    public void Listening()
    {
        byte[] buf = new byte[packetSize];
        dp.setData(buf);
        dp.setLength(packetSize);
        JSONObject receive;
        try
        {
            client.setSoTimeout(1000);
        }
        catch(SocketException e)
        {
            e.printStackTrace();
        }
        while(true)
        {
            try
            {
                client.receive(dp);
                lock.lock();
                receive = new JSONObject(new String(buf));
                processData(receive);
                repaint();
            }
            catch(ClosedChannelException e)
            {
                lock.unlock();
                break;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                lock.unlock();
            }
        }
    }
    
    /**
     * Szervertől jövő üzenet feldolgozása
     *
     * @param receive JSONObjektum a szervertől
     */
    private void processData(JSONObject receive) throws IOException
    {
        if(receive.has(Const.entities))
        {
            JSONObject entity = receive.getJSONObject(Const.entities);
            entities.forEach(x -> x.Erase((Graphics2D) moving.getGraphics()));
            int i = 0, j = 0;
            for(; i < entity.length() && j < entities.size(); i++, j++)
            {
                entities.get(i).setFromJSON(entity.getJSONObject(i + ""));
            }
            while(i < entity.length())
            {
                Ammo a = new AP();
                a.setFromJSON(entity.getJSONObject(i + ""));
                entities.add(a);
                i++;
            }
            if(entity.length() < entities.size())
            {
                entities.subList(i, entities.size()).clear();
            }
            //Log.log(entities.size());
        }
        if(receive.has(Const.players))
        {
            JSONObject entity = receive.getJSONObject(Const.players);
            players.forEach((s, x) ->
            {
                x.Erase((Graphics2D) moving.getGraphics());
                x.remove = true;
            });
            int i = 0, j = 0;
            Iterator<String> keys = entity.keys();
            for(; keys.hasNext(); )
            {
                String k = keys.next();
                if(name.equals(k))
                    continue;
                if(players.containsKey(k))
                    players.get(k).setFromJSON(entity.getJSONObject(k));
                else
                {
                    Tank a = new Tank(_tankWidth * gridSize.intValue() / _gridSize,
                            _tankHeight * gridSize.intValue() / _gridSize);
                    a.setFromJSON(entity.getJSONObject(k));
                    players.put(k, a);
                }
                
            }
            if(entity.length() < players.size())
            {
                players.keySet().removeIf(x -> !entity.has(x));
            }
            if(activePlayer.ReceiveClient(entity.getJSONObject(name)))
                RefreshAmmoLabels();
            
        }
        if(receive.has(Const.config))
        {
            Config(receive.getJSONObject(Const.config));
        }
        if(receive.has(Const.dead))
        {
            dead = receive.getBoolean(Const.dead);
        }
        if(receive.has(Const.rejoin))
        {
            dead = false;
            JSONObject send = new JSONObject();
            send.put(Const.join, tankPic);
            send.put(Const.name, name);
            DatagramPacket data = new DatagramPacket(send.toString().getBytes(), send.toString().getBytes().length,
                    serverIp, serverPort);
            client.send(data);
        }
        if(receive.has(Const.highlight))
        {
            JSONObject highlight = receive.getJSONObject(Const.highlight);
            Signal s = new Signal(90, Color.red);
            s.setFromJSON(highlight);
            localEffects.add(s);
        }
        if(receive.has(Const.music))
        {
            JSONArray musics = receive.getJSONArray(Const.music);
            musics.forEach(x -> SoundManager.PlaySound((String) x));
        }
        if(receive.has(Const.gameState) && state != null)
        {
            state.setText(receive.getString(Const.gameState));
        }
        if(receive.has(Const.clients))
        {
            stats.clear();
            JSONObject clients = receive.getJSONObject(Const.clients);
            for(String key : clients.keySet())
            {
                Client c = new Client();
                c.fromJSON(clients.getJSONObject(key));
                stats.add(c);
            }
            table.repaint();
        }
    }
    
    /**
     * A felhasználó által lenyomott billentyű elküldése
     *
     * @param e      A billentyű
     * @param lenyom lenyomta e?
     */
    public void SendKeys(KeyEvent e, boolean lenyom) throws IOException
    {
        JSONObject keys = new JSONObject();
        keys.put(Const.pressed, lenyom);
        keys.put(Const.key, e.getKeyCode());
        JSONObject send = new JSONObject();
        send.put(Const.keyboard, keys);
        SendData(send);
    }
    
    private void SendData(JSONObject send) throws IOException
    {
        DatagramPacket kdp = new DatagramPacket(send.toString().getBytes(), send.toString().getBytes().length);
        kdp.setAddress(serverIp);
        kdp.setPort(serverPort);
        client.send(kdp);
    }
    
    int[] pixels;
    
    /**
     * Kirajzolás
     *
     * @param g Vászon
     */
    @Override
    public void paint(Graphics g)
    {
        lock.lock();
        //		if (!d.equals(new Dimension(getWidth(), getHeight())))
        //		{
        //			d = new Dimension(getWidth(), getHeight());
        //			maze = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        //			maze.getGraphics().setColor(Color.white);
        //			maze.getGraphics().fillRect(0, 0, d.width, d.height);
        //			f.draw(maze.getGraphics(), d);
        //			image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        //			moving = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        //			effects = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        //			image.getGraphics().setColor(Color.white);
        //			image.getGraphics().fillRect(0, 0, d.width, d.height);
        //			pixels = new int[(int) (d.getWidth() * d.getHeight())];
        //			Arrays.fill(pixels, 0);
        //
        //		}
        // ((Graphics2D) effects.getGraphics()).setBackground(new Color(255, 255, 255,
        // 0));
        // effects.getGraphics().clearRect(0, 0, d.width, d.height);
        effects.setRGB(0, 0, (int) d.getWidth(), (int) d.getHeight(), pixels, 0, (int) d.getWidth());
        // Nem kell, a szervertõl jövõ válasz alapján rajzolunk
        entities.forEach(x -> x.Draw((Graphics2D) moving.getGraphics()));
        players.forEach((k, x) -> x.Draw((Graphics2D) moving.getGraphics()));
        localEffects.forEach(x -> x.Draw((Graphics2D) effects.getGraphics()));
        Graphics g0 = image.getGraphics();
        g0.drawImage(maze, 0, 0, null);
        g0.drawImage(moving, 0, 0, null);
        g0.drawImage(effects, 0, 0, null);
        g.drawImage(image, 0, 0, null);
        lock.unlock();
        
    }
    
    /**
     * Időzítés megvalósítása, léptet
     */
    @SuppressWarnings("unchecked")
    public void Tick() throws IOException
    {
        lock.lock();
        activePlayer.Tick(this);
        JSONObject send = new JSONObject();
        send.put(Const.Tank, activePlayer.SendClient());
        SendData(send);
        try
        {
            ((ArrayList<GameEntity>) localEffects.clone()).forEach(x -> x.Tick(this));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        repaint();
        lock.unlock();
    }
    
    public void AddEntity(GameEntity e)
    {
        localEffects.add(e);
    }
    
    public void RemoveEntity(GameEntity e)
    {
        lock.lock();
        localEffects.remove(e);
        e.Erase((Graphics2D) effects.getGraphics());
        lock.unlock();
    }
    
    public boolean CheckTank(Ammo ammo)
    {
        players.forEach((k, x) -> x.CheckDestroy(ammo));
        return false;
    }
    
    @Override
    public void deadTank(Tank t)
    {
        
    }
    
    /**
     * Bezáráskor értesíti a szervert
     */
    public void OnClosing()
    {
        lock.lock();
        try
        {
            JSONObject json = new JSONObject();
            json.put(Const.disconnect, "");
            DatagramPacket exit = new DatagramPacket(json.toString().getBytes(), json.toString().getBytes().length,
                    serverIp, serverPort);
            client.send(exit);
            client.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            lock.unlock();
        }
    }
    
    @Override
    public Dimension getMaximumSize()
    {
        return d;
    }
    
    @Override
    public Dimension getMinimumSize()
    {
        return d;
    }
    
    @Override
    public Dimension getPreferredSize()
    {
        return d;
    }
    
    public class KeyboardListener implements KeyListener
    {
        
        @Override
        public void keyTyped(KeyEvent e)
        {
        }
        
        @Override
        public void keyPressed(KeyEvent e)
        {
            switch(e.getKeyCode())
            {
                case KeyEvent.VK_N:
                {
                    /*f.GenerateField();
                    f.draw(maze.getGraphics(), d);
                    break;*/
                }
                case KeyEvent.VK_ESCAPE:
                {
                    // OnClosing();
                    SwingUtilities.getWindowAncestor(GamePanel.this).dispatchEvent(new WindowEvent(SwingUtilities.getWindowAncestor(GamePanel.this), WindowEvent.WINDOW_CLOSING));
                    dead = true;
                    break;
                }
            }
            if(!dead)
            {
                if(e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    JSONObject send = new JSONObject();
                    send.put(Const.Fire, true);
                    try
                    {
                        SendData(send);
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                else
                    activePlayer.processKey(e, true);
            }
            k.processKey(e.getKeyCode());
            RefreshAmmoLabels();
            // activePlayer.processKey(e, true);
            // activePlayer.CheckCollision();
            // repaint();
        }
        
        @Override
        public void keyReleased(KeyEvent e)
        {
            // activePlayer.processKey(e, false);
            if(!dead)
            {
                if(e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    JSONObject send = new JSONObject();
                    send.put(Const.Fire, false);
                    try
                    {
                        SendData(send);
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                activePlayer.processKey(e, false);
            }
        }
        
    }
    
    @Override
    public void PlayMusic(String f)
    {
        
    }
    
    class Sort implements Comparator<Client>
    {
        @Override
        public int compare(Client o1, Client o2)
        {
            int ret;
            if((ret = -Integer.compare(o1.kill, o2.kill)) == 0)
                if((ret = Integer.compare(o1.death, o2.death)) == 0)
                    return o1.name.compareTo(o2.name);
            return ret;
        }
    }
    
}
