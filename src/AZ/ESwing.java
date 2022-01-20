package AZ;

import java.awt.*;
import java.util.ArrayList;

public class ESwing
{
    public static ArrayList<Component> getAllComponents(final Container c)
    {
        Component[] comps = c.getComponents();
        ArrayList<Component> compList = new ArrayList<Component>();
        for(Component comp : comps)
        {
            compList.add(comp);
            if(comp instanceof Container)
                compList.addAll(getAllComponents((Container) comp));
        }
        return compList;
    }
    
    public static Component getComponent(final Container c, String name)
    {
        Component[] comps = c.getComponents();
        for(Component comp : comps)
        {
            if(comp.getName().equals(name))
                return comp;
            if(comp instanceof Container)
                return getComponent((Container) comp, name);
        }
        return null;
    }
}
