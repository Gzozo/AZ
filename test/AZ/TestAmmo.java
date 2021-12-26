package AZ;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import AZ.Server.GameState;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.awt.event.KeyEvent;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class TestAmmo
{
    
    Server server;
    
    Field f;
    
    Ammo a;
    Tank t;
    
    @Before
    public void Before()
    {
        
        server = new Server(6666);
        server.state = GameState.PLAYING;
        f = new Field(1, 1, new AtomicInteger(5));
        f.setField(new Mezo[][]{new Mezo[]{new Mezo(0, 0, new AtomicInteger(5))}});
        a = new AP(0, 0, 0, f);
        t = new Tank((int) a.speedx * 2, (int) a.speedy * 2, 30, 40, "", f, new AtomicInteger(5), server);
        server.players.put(null, new Client(t));
        /*
         * when(f.mezok[0][0].Collision(Mockito.anyDouble(), Mockito.anyDouble(),
         * Mockito.anyDouble())).thenReturn(2);
         */
        
    }
    
    @Test
    public void LovedekTest()
    {
        
        // when(server.CheckTank(a)).thenReturn(false);
        
        double speedx = a.speedx;
        double speedy = a.speedy;
        
        a.Tick(server, false);//3
        
        assertEquals(speedx, a.x, 0.1);
        assertEquals(speedy, a.y, 0.1);
        
    }
    
    @Test
    public void PowerUpTest()
    {
        HE he = new HE();
        PowerUp pu = new PowerUp(5, 5, 30, null, he);
        
        a.Tick(server, false);
        assertTrue(t.dead);//4
        assertTrue(t.ammo instanceof AP);
        pu.Tick(server, false);
        assertTrue(t.ammo instanceof AP);
        t.dead = false;
        pu.Tick(server, false);//5
        assertTrue(t.ammo instanceof HE);
        t.ammo.shellCount = 2;
        t.ammo.cooldown = 1;
        t.processKey(KeyEvent.VK_SPACE, true);
        assertTrue(t.keys.get(KeyEvent.VK_SPACE));//6
        int entityCount = server.entities.size();
        t.Tick(server);//7
        assertEquals(entityCount + 1, server.entities.size());
        assertTrue(server.entities.get(entityCount) instanceof HE);
        t.Tick(server);
        assertEquals(entityCount + 1, server.entities.size());
        t.Tick(server);
        assertEquals(entityCount + 2, server.entities.size());
        assertTrue(server.entities.get(entityCount) instanceof HE);
        assertTrue(t.ammo instanceof AP);
        
    }
    
}
