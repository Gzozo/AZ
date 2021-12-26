package AZ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

/**
 * Az irányításra szolgáló gombokat tárolja
 */
public class Controls implements ActionListener
{
    
    public static LinkedHashMap<String, Integer> commands = new LinkedHashMap<>();
    public static HashMap<String, String> text = new HashMap<>();
    String selected = "";
    
    static
    {
        commands.put("w", KeyEvent.VK_W);
        commands.put("s", KeyEvent.VK_S);
        commands.put("a", KeyEvent.VK_A);
        commands.put("d", KeyEvent.VK_D);
        commands.put("fire", KeyEvent.VK_SPACE);
        text.put("w", "Előre");
        text.put("s", "Hátra");
        text.put("a", "Forgás balra");
        text.put("d", "Forgás jobbra");
        text.put("fire", "Tüzelés");
    }
    
    JFrame f;
    HashMap<String, JLabel> panels = new HashMap<>();
    
    /**
     * Beolvassa az adatokat egy JSON objektumb�l
     *
     * @param json A JSON objektum
     */
    public static void ReadJSON(JSONObject json)
    {
        for(Entry<String, Integer> entry : commands.entrySet())
        {
            commands.put(entry.getKey(), json.optInt(entry.getKey(), entry.getValue()));
        }
    }
    
    /**
     * Ki�rja, elmenti az adatokat egy JSON objektumba
     *
     * @param json A JSON objektum
     */
    public static void WriteJSON(JSONObject json)
    {
        for(Entry<String, Integer> entry : commands.entrySet())
        {
            json.put(entry.getKey(), entry.getValue());
        }
    }
    
    public Controls(JFrame f)
    {
        this.f = f;
        createGUI();
    }
    
    /**
     * L�trehozza az �r�ny�t�s testreszab�s�hoz sz�ks�ges framet
     */
    void createGUI()
    {
        JPanel west = new JPanel(), center = new JPanel(), east = new JPanel();
        west.setLayout(new GridLayout(commands.size(), 1));
        center.setLayout(new GridLayout(commands.size(), 1));
        east.setLayout(new GridLayout(commands.size(), 1));
        
        for(Entry<String, Integer> entry : commands.entrySet())
        {
            JLabel key = new JLabel(text.get(entry.getKey())),
                    value = new JLabel(KeyEvent.getKeyText(entry.getValue()), SwingConstants.CENTER);
            value.setName("Value");
            JButton modify = new JButton("Módosít");
            modify.setActionCommand(entry.getKey());
            modify.addActionListener(this);
            west.add(key);
            center.add(value);
            east.add(modify);
            panels.put(entry.getKey(), value);
        }
        f.add(west, BorderLayout.WEST);
        f.add(center, BorderLayout.CENTER);
        f.add(east, BorderLayout.EAST);
        f.addKeyListener(new Listener());
        f.setFocusable(true);
        f.requestFocus();
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(selected.equals(""))
        {
            selected = e.getActionCommand();
            panels.get(selected).setForeground(Color.RED);
        }
        else if(selected.equals(e.getActionCommand()))
        {
            selected = "";
            panels.get(selected).setForeground(Color.BLACK);
        }
        else
        {
            panels.get(selected).setForeground(Color.BLACK);
            selected = e.getActionCommand();
            panels.get(selected).setForeground(Color.RED);
        }
        f.requestFocus();
        f.repaint();
    }
    
    class Listener implements KeyListener
    {
        
        @Override
        public void keyTyped(KeyEvent e)
        {
        }
        
        @Override
        public void keyPressed(KeyEvent e)
        {
            System.out.println("FIREEEEE");
            if(selected.equals(""))
                return;
            commands.put(selected, e.getKeyCode());
            JLabel label = panels.get(selected);
            label.setText(KeyEvent.getKeyText(e.getKeyCode()));
            label.setForeground(Color.BLACK);
            f.pack();
            f.repaint();
            selected = "";
            // Arrays.stream(panel.getComponents()).filter(x ->
            // x.getName().equals(selected));
        }
        
        @Override
        public void keyReleased(KeyEvent e)
        {
        }
    }
    
}
