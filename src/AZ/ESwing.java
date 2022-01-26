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
            //Log.log(comp.getName());
            if(comp.getName() != null && comp.getName().equals(name))
                return comp;
            if(comp instanceof Container)
            {
                Component com = getComponent((Container) comp, name);
                if(com != null && com.getName() != null)
                    return com;
            }
        }
        return null;
    }
}
