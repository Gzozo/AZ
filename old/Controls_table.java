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
		}
		
		public KeyCellEditor()
		{
			super(new JTextField());
			getComponent().setName("Table.editor");
		}
		
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
		text.put("w", "Elõre");
		text.put("s", "Hátra");
		text.put("a", "Forgás balra");
		text.put("d", "Forgás jobbra");
		text.put("fire", "Tûzelés");
	}
	
	@Override
	public int getRowCount()
	{
		return commands.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return 2;
	}
	
	@Override
	public String getColumnName(int arg0)
	{
		switch (arg0)
		{
			case 0:
				return "Név";
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
