package AZ;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class StatTableModel extends DefaultTableModel
{
    GamePanel manager;
    
    
    public StatTableModel(GamePanel manager)
    {
        this.manager = manager;
        
        
    }
    
    
    @Override
    public int getRowCount()
    {
        if(manager == null || manager.stats == null)
            return 0;
        return manager.stats.size();
    }
    
    @Override
    public int getColumnCount()
    {
        return 3;
    }
    
    @Override
    public String getColumnName(int column)
    {
        //Miért nem jelenik meg??
        switch(column)
        {
            case 0:
                return "Name";
            case 1:
                return "Kill";
            default:
                return "Death";
        }
    }
    
    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
    
    @Override
    public Object getValueAt(int row, int column)
    {
        return switch(column)
                {
                    case 0 -> manager.stats.stream().skip(row).findFirst().orElse(new Client()).name;
                    case 1 -> manager.stats.stream().skip(row).findFirst().orElse(new Client()).kill;
                    default -> manager.stats.stream().skip(row).findFirst().orElse(new Client()).death;
                };
        
    }
    
    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if(columnIndex == 0)
            return String.class;
        return Integer.class;
    }
}
