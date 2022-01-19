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
}
