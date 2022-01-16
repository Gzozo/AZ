package AZ;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Szerver
 */
public class Server extends Thread implements GameManager
{
    /**
     * A játék állapota
     * Mutatja a felhasználónak az állapotot, és tárolja hogy meghalhat e a tank
     */
    public enum GameState
    {
        JOINING(false, "Joining"), STARTING(false, "Starting"), PLAYINGBEGIN(true, "Playing"), PLAYING(true, "Playing"
    ), ENDING(true, "Ending"), IDLE(false, "Idle");
        
        public boolean death;
        public String state;
        
        GameState(Boolean b)
        {
            death = b;
            state = "";
        }
        
        GameState(Boolean b, String s)
        {
            death = b;
            state = s;
        }
        
    }
    
    GameState state;
    
    final int packetSize = 1024 * 1024;
    int ReceivePort;
    DatagramSocket server = null;
    // DatagramSocket sender = null;
    Field f;
    final int width = 20, height = 20;
    final int _tankWidth = 20, _tankHeight = 28;
    final int _gridSize = 50;
    final int minPlayer = 2;
    final Dimension refer = new Dimension(800, 800);
    final static String[] pics = new String[]{"p1.png", "p2.png", "p3.png", "p4.png", "p5.png"};
    AtomicInteger gridSize = new AtomicInteger(25);
    ReentrantLock lock = new ReentrantLock();
    boolean statsChanged = false;
    
    Dictionary players = new Dictionary();
    
    ArrayList<GameEntity> entities = new ArrayList<>();
    ArrayList<String> music = new ArrayList<>();
    
    /**
     * First always default!
     **/
    Ammo[] ammoTypes = new Ammo[]{new AP(), new HE(), new Sharpnel(), new Minigun()};
    double chancePU = 1.0 / 10 / 30;
    
    public boolean dying = false;
    
    public Server(int rp)
    {
        ReceivePort = rp;
        while(server == null)
        {
            try
            {
                server = new DatagramSocket(ReceivePort);
            }
            catch(IOException e)
            {
                server = null;
                ReceivePort++;
            }
        }
        /*
         * SendPort = ReceivePort + 1; while (sender == null) { try { sender = new
         * DatagramSocket(SendPort); } catch (IOException e) { sender = null;
         * SendPort++; } }
         */
        f = new Field(width, height, gridSize);
        f.GenerateField(1123);
        f.setGridSize(refer);
        
        state = GameState.JOINING;
    }
    
    /**
     * Új pálya generálása, a kliensek megkérése, hogy csatlakozzanak újra
     */
    public void newGame()
    {
        lock.lock();
        try
        {
            f.GenerateField();
            players.values().removeIf(x -> !x.joined);
            entities.removeIf(x ->
            {
                return x instanceof PowerUp;
            });
            JSONObject rejoin = new JSONObject();
            rejoin.put(Const.rejoin, true);
            DatagramPacket dp = new DatagramPacket(rejoin.toString().getBytes(), rejoin.toString().getBytes().length);
            for(Entry<SocketAddress, Client> entry : players.entrySet())
            {
                entry.getValue().joined = false;
                entry.getValue().t.dead = true;
                dp.setSocketAddress(entry.getKey());
                server.send(dp);
            }
            state = GameState.PLAYINGBEGIN;
            scheduler.schedule(() -> state = state == GameState.PLAYINGBEGIN ? GameState.PLAYING : state, 2,
                    TimeUnit.SECONDS);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            lock.unlock();
        }
    }
    
    /**
     * A klienseknek küldi az adatokat egyszer
     */
    @SuppressWarnings("unchecked")
    void ManageGame()
    {
        lock.lock();
        try
        {
            ((ArrayList<GameEntity>) entities.clone()).forEach(x -> x.Tick(this));
            String data = AllData();
            DatagramPacket dp = new DatagramPacket(data.getBytes(), data.getBytes().length);
            int alive = 0;
            for(Entry<SocketAddress, Client> entry : players.entrySet())
            {
                if(!entry.getValue().t.dead || (state == GameState.PLAYINGBEGIN && !entry.getValue().joined))
                    alive++;
                dp.setSocketAddress(entry.getKey());
                server.send(dp);
            }
            if(players.values().stream().mapToInt(c -> c.joined ? 1 : 0).sum() < 2 && state == GameState.PLAYING)
            {
                state = GameState.JOINING;
            }
            if(alive <= 1 && state == GameState.PLAYING)
            {
                state = GameState.ENDING;
                scheduler.schedule(this::newGame, 2, TimeUnit.SECONDS);
            }
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
    
    /**
     * Az összes adat JSON formátumú Stringben
     *
     * @return adat
     */
    String AllData()
    {
        JSONObject ret = new JSONObject();
        JSONObject entity = new JSONObject();
        for(int i = 0; i < entities.size(); i++)
        {
            entity.put(i + "", entities.get(i).toJSON());
        }
        ret.put(Const.entities, entity);
        
        entity = new JSONObject();
        JSONObject clients = new JSONObject();
        int index = 0;
        for(Entry<SocketAddress, Client> entry : players.entrySet())
        {
            clients.put(entry.getValue().name, entry.getValue().toJson());
            Tank val = entry.getValue().t;
            if(val.dead)
                continue;
            entity.put(index + "", val.toJSON());
            index++;
        }
        ret.put(Const.players, entity);
        
        if(statsChanged)
        {
            ret.put(Const.clients, clients);
            statsChanged = false;
        }
        if(music.size() > 0)
        {
            JSONArray array = new JSONArray();
            array.putAll(music);
            ret.put(Const.music, array);
            music.clear();
        }
        ret.put(Const.gameState, state.state);
        return ret.toString();
    }
    
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    /**
     * Figyeli a hálózatot, feldolgozza a kéréseket, és válaszol is ha kell
     */
    public void run()
    {
        
        scheduler.scheduleWithFixedDelay(() -> ManageGame(), 0, 33, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(() -> Tick(), 0, 17, TimeUnit.MILLISECONDS);
        System.out.println("Listening " + ReceivePort);
        try
        {
            server.setSoTimeout(1000);
        }
        catch(SocketException e1)
        {
            e1.printStackTrace();
        }
        while(!dying)
        {
            try
            {
                byte[] buf = new byte[packetSize];
                
                // receive request
                DatagramPacket packet = new DatagramPacket(buf, packetSize);
                server.receive(packet);
                if(dying)
                    break;
                lock.lock();
                try
                {
                    String s = new String(buf);
                    JSONObject receive = new JSONObject(s);
                    // System.out.println(receive);
                    JSONObject ret = new JSONObject();
                    if(receive.has(Const.join))
                    {
                        Random r = new Random();
                        int x = r.nextInt(width);
                        int y = r.nextInt(height);
                        
                        String pic = receive.getString(Const.join);
                        String kep = pic;
                        
                        if(!Arrays.stream(pics).anyMatch(d -> d.equals(pic)))
                        {
                            // pic = pics[r.nextInt(pics.length)];
                            kep = pics[r.nextInt(pics.length)];
                        }
                        Tank t = new Tank(0, 0, _tankWidth * gridSize.intValue() / _gridSize,
                                _tankHeight * gridSize.intValue() / _gridSize, kep, f, gridSize, this);
                        
                        t.setCenterx(f.mezok[x][y].centerx());
                        t.setCentery(f.mezok[x][y].centery());
                        
                        if(players.contains(packet.getSocketAddress()))
                        {
                            players.get(packet.getSocketAddress()).t = t;
                            players.get(packet.getSocketAddress()).joined = true;
                        }
                        else
                        {
                            Client c = new Client(t);
                            c.name = receive.optString(Const.name, "Player" + (players.size() + 1));
                            int sum = players.values().stream().mapToInt(client -> client.name == c.name ? 1 : 0).sum();
                            if(sum > 0)
                                c.name += "" + (sum + 1);
                            c.picture = kep;
                            players.put(packet.getSocketAddress(), c);
                            System.out.println("New Player: " + packet.getSocketAddress());
                            PlayMusic(Const.Music.playerJoined);
                        }
                        
                        if(state == GameState.JOINING && players.values().stream().mapToInt(c -> c.joined ? 1 : 0).sum() >= minPlayer)
                        {
                            scheduler.schedule(() -> StartGame(), 5, TimeUnit.SECONDS);
                            state = GameState.STARTING;
                        }
                        
                        JSONObject config = new JSONObject();
                        
                        config.put(Const.labirintusSeed, f.seed);
                        config.put(Const.gridSize, gridSize.intValue());
                        config.put(Const.width, width);
                        config.put(Const.height, height);
                        config.put(Const.tankWidth, _tankWidth);
                        config.put(Const.tankHeight, _tankHeight);
                        config.put(Const.panelWidth, refer.width);
                        config.put(Const.panelHeight, refer.height);
                        ret.put(Const.config, config);
                        Signal signal = new Signal(f.mezok[x][y].centerx(), f.mezok[x][y].centery(), 200,
                                Math.max(t.rectWidth, t.rectHeight), 0, Color.red);
                        ret.put(Const.highlight, signal.toJSON());
                        
                        DatagramPacket respond = new DatagramPacket(ret.toString().getBytes(),
                                ret.toString().length(), packet.getAddress(), packet.getPort());
                        server.send(respond);
                        statsChanged = true;
                    }
                    if(receive.has(Const.keyboard))
                    {
                        JSONObject keys = receive.getJSONObject(Const.keyboard);
                        Tank t = players.get(packet.getSocketAddress()).t;
                        t.processKey(keys.getInt(Const.key), keys.getBoolean(Const.pressed));
                    }
                    if(receive.has(Const.disconnect))
                    {
                        Client c = players.remove(packet.getSocketAddress());
                        System.out.println("Disconnect " + c);
                    }
                    if(receive.has(Const.test))
                    {
                        server.send(packet);
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    lock.unlock();
                }
                
                // System.out.println(receive.toString());
                
            }
            catch(SocketTimeoutException ignored)
            {
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        scheduler.shutdown();
        server.close();
    }
    
    /**
     * A játék valódi indítása (előtte általában várnak játékosok csatlakozására)
     */
    public void StartGame()
    {
        lock.lock();
        try
        {
            entities.clear();
            state = GameState.PLAYING;
            players.values().forEach(x -> x.t.ammo = Ammo.getDefaultAmmo());
            System.out.println("StartGame");
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
    
    public void AddEntity(GameEntity e)
    {
        synchronized(entities)
        {
            entities.add(e);
        }
    }
    
    public void RemoveEntity(GameEntity e)
    {
        synchronized(entities)
        {
            entities.remove(e);
        }
    }
    
    public void AddPowerUp()
    {
        Random r = new Random();
        int x = r.nextInt(width), y = r.nextInt(height);
        
        PowerUp pu = new PowerUp(f.mezok[x][y].centerx(), f.mezok[x][y].centery(), gridSize.intValue() / 4, f,
                ammoTypes[r.nextInt(ammoTypes.length - 1) + 1]);// First ammo is default
        AddEntity(pu);
    }
    
    public boolean CheckTank(Ammo ammo)
    {
        boolean hit = false;
        synchronized(players)
        {
            for(Entry<SocketAddress, Client> entry : players.entrySet())
            {
                Client val = entry.getValue();
                if(val.t.dead)
                    continue;
                if(val.t.CheckDestroy(ammo))
                {
                    hit = true;
                    if(!state.death && val.t.dead)
                    {
                        val.t.dead = false;
                    }
                    if(val.t.dead)
                    {
                        val.death++;
                        if(val.t != ammo.parent)
                        {
                            players.getOwner(ammo.parent).kill++;
                        }
                        PlayMusic(Const.Music.tankDestroy);
                        statsChanged = true;
                    }
                }
                
            }
        }
        return hit;
    }
    
    Random chance = new Random();
    
    /**
     * Időzítés megvalósítása, léptet
     */
    @SuppressWarnings("unchecked")
    void Tick()
    {
        lock.lock();
        try
        {
            players.forEach((x, y) -> y.t.Tick(this));
            ((ArrayList<GameEntity>) entities.clone()).forEach(x -> x.Tick(this));
            double dice = chance.nextDouble();
            if(dice <= chancePU)
                AddPowerUp();
            
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
    public void deadTank(Tank t)
    {
        Client c = players.getOwner(t);
        
        JSONObject val = new JSONObject();
        val.put(Const.dead, true);
        DatagramPacket dp = new DatagramPacket(val.toString().getBytes(), val.toString().getBytes().length,
                players.get(c));
        try
        {
            server.send(dp);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
    }
    
    @Override
    public void PlayMusic(String f)
    {
        music.add(f);
    }
}
