package AZ;

/**
 * Easter Egg
 */
public class Konami
{
	enum States
	{
		IDLE(false, 2, Controls.commands.get("w"))
		{
			@Override
			public States nextState()
			{
				return FEL;
			}
		},
		FEL(false, 2, Controls.commands.get("s"))
		{
			@Override
			public States nextState()
			{
				return LE;
			}
		},
		LE(false, 1, Controls.commands.get("a"))
		{
			@Override
			public States nextState()
			{
				return BAL;
			}
		},
		BAL(false, 1, Controls.commands.get("d"))
		{
			@Override
			public States nextState()
			{
				return JOBB;
			}
		},
		JOBB(false, 1, Controls.commands.get("a"))
		{
			@Override
			public States nextState()
			{
				return BAL2;
			}
		},
		BAL2(false, 1, Controls.commands.get("d"))
		{
			@Override
			public States nextState()
			{
				return JOBB2;
			}
		},
		JOBB2(false, 1, Controls.commands.get("fire"))
		{
			@Override
			public States nextState()
			{
				return B;
			}
		},
		B(false, 1, Controls.commands.get("a"))
		{
			@Override
			public States nextState()
			{
				return A;
			}
		},
		A(true, 2, Controls.commands.get("w"))
		{
			@Override
			public States nextState()
			{
				return FEL;
			}
		};
		
		boolean nyer;
		int same, key;
		
		States(boolean nyer, int same, int key)
		{
			this.nyer = nyer;
			this.same = same;
			this.key = key;
		}
		
		public abstract States nextState();
		
		public boolean joKey(int key)
		{
			return this.key == key;
		}
		
		public States processKey(int key)
		{
			if (joKey(key))
			{
				same--;
				if (same <= 0)
					return nextState();
				return this;
			}
			return IDLE;
		}
	}
	
	States state = States.IDLE;
	
	public void processKey(int keycode)
	{
		state = state.processKey(keycode);
		if (state.nyer)
		{
			SoundManager.PlaySound(Const.Music.RainsOfCastemere);
			state = States.IDLE;
		}
	}
	
}
