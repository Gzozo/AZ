package AZ;

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

public class TestImage
{
    @Test
    public void TestGraphic()
    {
        Image img = new BufferedImage(300, 200, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) img.getGraphics();
        
        assertEquals(200, g.getClipBounds().height);
        assertEquals(300, g.getClipBounds().width);
    }
}
