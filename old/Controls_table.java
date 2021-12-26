package AZ;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class Controls extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	public static HashMap<String, Integer> commands = new HashMap<>();
	public static HashMap<String, String> text = new HashMap<>();
	
	@SuppressWarnings("serial")
	static class ColumnModel extends DefaultTableColumnModel
	{
		
		@Override
		public TableColumn getColumn(int columnIndex)
		{
			// TODO Auto-generated method stub
			TableColumn tc = super.getColumn(columnIndex);
			tc.setCellEditor(new KeyCellEditor());
			return tc;
		}
		
	}
	
	static class KeyCellEditor extends DefaultCellEditor
	{
		
		private static final long serialVersionUID = 1L;
		
		@SuppressWarnings("serial")
		public KeyCellEditor(JTextField textField)
		{
			super(textField);
			textField.addKeyListener((CellEditorPro) delegate);
			clickCountToStart = 1;
			// TODO Auto-generated constructor stub
		}
		
		public KeyCellEditor()
		{
			super(new JTextField());
			getComponent().setName("Table.editor");
		}
		// TODO:
		// Override CellEditor, m�st kell visszaadni, aminek van KeyListenere is, �s az
		// �rt�ke att�l f�gg
		// Esetleg extends DefaultCellEditor.EditorDelegate impements KeyListener
		// El�tte megcsin�lni a men�t �s a menubart
		
		@SuppressWarnings("serial")
		class CellEditorPro extends EditorDelegate implements KeyListener
		{
			
			@Override
			public void setValue(Object value)
			{
			}
			
			@Override
			public void keyTyped(KeyEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				value = e.getKeyCode();
				stopCellEditing();
				
			}
			
			@Override
			public void keyReleased(KeyEvent e)
			{
				// TODO Auto-generated method stub
				
			}
			
		}
		
	}
	
	static
	{
		commands.put("w", KeyEvent.VK_W);
		commands.put("s", KeyEvent.VK_S);
		commands.put("a", KeyEvent.VK_A);
		commands.put("d", KeyEvent.VK_D);
		commands.put("fire", KeyEvent.VK_SPACE);
		text.put("w", "El�re");
		text.put("s", "H�tra");
		text.put("a", "Forg�s balra");
		text.put("d", "Forg�s jobbra");
		text.put("fire", "T�zel�s");
	}
	
	@Override
	public int getRowCount()
	{
		// TODO Auto-generated method stub
		return commands.size();
	}
	
	@Override
	public int getColumnCount()
	{
		// TODO Auto-generated method stub
		return 2;
	}
	
	@Override
	public String getColumnName(int arg0)
	{
		switch (arg0)
		{
			case 0:
				return "N�v";
			default:
				return "Parancs";
		}
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		return String.class;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		// TODO Auto-generated method stub
		return columnIndex == 1;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		Map.Entry<String, Integer> element = (new ArrayList<Map.Entry<String, Integer>>(commands.entrySet()))
				.get(rowIndex);
		commands.put(element.getKey(), element.getValue());
		fireTableRowsUpdated(rowIndex, rowIndex);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Map.Entry<String, Integer> element = (new ArrayList<Map.Entry<String, Integer>>(commands.entrySet()))
				.get(rowIndex);
		if (columnIndex == 0)
		{
			return text.get(element.getKey());
		}
		else
		{
			return KeyEvent.getKeyText(element.getValue());
		}
	}
	
}
