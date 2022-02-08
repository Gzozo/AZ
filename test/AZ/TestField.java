package AZ;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TestField
{
	Field f, f2;
	int x, y;
	AtomicInteger gridSize = new AtomicInteger(25);
	
	@Parameters
	public static List<Object[]> parameters()
	{
		Random r = newXRandom();
		List<Object[]> params = new ArrayList<Object[]>();
		for (int i = 0; i < 10; i++)
		{
			int x = r.nextInt(10) + 1;
			int y = r.nextInt(10) + 1;
			params.add(new Object[] { x, y });
		}
		return params;
	}
	
	public TestField(int a, int b)
	{
		x = a;
		y = b;
	}
	
	@Before
	public void BeforeTest()
	{
		f = new Field(x, y, gridSize);
		f2 = new Field(x, y, gridSize);
	}
	
	@Test
	public void SizeTest()
	{
		assertEquals(x, f.mezok.length);
		for (int i = 0; i < x; i++)
		{
			assertEquals(y, f.mezok[i].length);
		}
	}
	
	@Test
	public void GenerateTest()
	{
		f.GenerateField();
		for (int i = 0; i < f.mezok.length; i++)
		{
			for (int j = 0; j < f.mezok[i].length; j++)
			{
				assertNotEquals(f.mezok[i][j].zart, new boolean[] { true, true, true, true });
			}
		}
		f2.GenerateField(f.seed);
		for (int i = 0; i < f.mezok.length; i++)
		{
			for (int j = 0; j < f.mezok[i].length; j++)
			{
				assertEquals(f.mezok[i][j].zart, f2.mezok[i][j]);
			}
		}
	}
}
