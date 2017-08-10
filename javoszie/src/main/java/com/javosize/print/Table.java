package com.javosize.print;

import com.javosize.log.Log;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Table {
	private static final int DEFAULTPERCENTAGESIZE = 15;
	private static final String INDENT = " | ";
	private List<Colum> colums = new ArrayList();
	private List<String[]> rows = new ArrayList();
	private RowComparator rowComparator = null;
	private int terminalWidth;
	private Log log = new Log(Table.class.toString());
	public static String rowSeparator = "\n   \n";

	public static final String EMPTY_VALUE = "NULL";

	public Table(int terminalWidth) {
		this(null, terminalWidth);
	}

	public Table(RowComparator rowComparator, int terminalWidth) {
		this.rowComparator = rowComparator;
		this.terminalWidth = (terminalWidth - 4);
	}

	public void addColum(String title) {
		addColum(title, 15);
	}

	public void addColum(String title, int percentageSize) {
		this.colums.add(new Colum(title, percentageSize));
	}

	public List<String[]> getRows() {
		return this.rows;
	}

	public void clearRows() {
		this.rows = new ArrayList();
	}

	public void addRow(String[] values) throws InvalidColumNumber {
		if (values.length != this.colums.size()) {
			throw new InvalidColumNumber(
					"Invalid number of colums in row:" + values.length + ". Table has " + this.colums.size());
		}
		for (int i = 0; i < values.length; i++) {
			if (values[i] == null) {
				values[i] = "";
			}
		}
		this.rows.add(values);
	}

	public String toString() {
		rowSeparator = generateRowSeparator();

		if (this.rowComparator != null) {
			Collections.sort(this.rows, this.rowComparator);
		}

		StringBuffer sb = new StringBuffer();
		String[] columsArray = new String[this.colums.size()];
		int i = 0;
		for (Iterator localIterator = this.colums.iterator(); localIterator.hasNext();) {
			Colum c = (Colum) localIterator.next();
			columsArray[(i++)] = c.getTitle();
		}

		printRow(columsArray, sb, true);

		String separator = new String(new char[this.terminalWidth]).replace("\000", "=") + "\n";
		sb.append(separator);

		for (String[] row : this.rows) {
			printRow(row, sb, false);
		}

		return sb.toString();
	}

	private void printRow(String[] row, StringBuffer sb, boolean isHeader) {
		String[] rowCopy = new String[row.length];
		for (int j = 0; j < row.length; j++) {
			rowCopy[j] = row[j];
		}

		boolean lineOverflow = false;
		for (int i = 0; i < rowCopy.length; i++) {
			int columSize = getColumSize(((Colum) this.colums.get(i)).getPercentage());
			sb.append(adaptString(rowCopy[i], columSize));
			sb.append(" | ");
			if (columSize < rowCopy[i].length()) {
				lineOverflow = true;
			}
		}

		while (lineOverflow) {
			sb.append("\n");
			lineOverflow = false;

			for (int i = 0; i < rowCopy.length; i++) {
				int columSize = getColumSize(((Colum) this.colums.get(i)).getPercentage());
				if (rowCopy[i].length() > columSize) {
					String tmp = rowCopy[i].substring(columSize, rowCopy[i].length());
					rowCopy[i] = tmp;
					sb.append(adaptString(rowCopy[i], columSize));
					sb.append(" | ");
				} else {
					sb.append(adaptString("", columSize));
					sb.append(" | ");
				}

				if (columSize < rowCopy[i].length()) {
					lineOverflow = true;
				}
			}
		}
		if (!isHeader) {
			sb.append(rowSeparator);
		} else {
			sb.append("\n");
		}
	}

	private int getColumSize(int percentage) {
		int availableColums = this.terminalWidth - " | ".length() * (this.colums.size() - 1);
		return (int) Math.floor(percentage * availableColums / 100.0D);
	}

	private String generateRowSeparator() {
		StringBuffer sb = new StringBuffer("\n");
		for (int i = 0; i < this.colums.size(); i++) {
			int columSize = getColumSize(((Colum) this.colums.get(i)).getPercentage());
			sb.append(adaptString("", columSize));
			sb.append(" + ");
		}
		sb.append("\n");
		return sb.toString();
	}

	private static String adaptString(String s, int colSize) {
		if (s == null) {
			s = "";
		}

		String res = s.substring(0, Math.min(s.length(), colSize));
		res = res + generateBlanks(colSize - s.length());

		return res;
	}

	private static String generateBlanks(int n) {
		if (n <= 0) {
			return "";
		}

		String ret = "";
		for (int i = 0; i < n; i++) {
			ret = ret + " ";
		}
		return ret;
	}

	public boolean hasRows() {
		return (this.rows != null) && (!this.rows.isEmpty());
	}

	public int numberOfRows() {
		if ((this.rows != null) && (!this.rows.isEmpty())) {
			return this.rows.size();
		}
		return 0;
	}

	public static String[] getFirstColumnValues(String printedTable) {
		Table table = getTableFromString(printedTable);
		return getFirstColumnValues(table);
	}

	public static String[] getFirstColumnValues(Table table) {
		String[] firstColumnValues = new String[table.rows.size()];

		int i = 0;
		for (String[] row : table.rows) {
			firstColumnValues[(i++)] = row[0].trim();
		}

		return firstColumnValues;
	}

	public static Table getTableFromString(String printedTable) {
		String[] lines = printedTable.split("\\n");
		int originalTermWidth = getTermWidthFromHeader(lines);
		Table result = new Table(originalTermWidth + 4);

		String rowSeparator = lines[(lines.length - 1)];

		int numOfColumns = rowSeparator.split(" \\+ ", -1).length - 1;
		int availableSizeForColumns = originalTermWidth - " | ".length() * (numOfColumns - 1);
		String headerSeparator = new String(new char[originalTermWidth]).replace("\000", "=");

		boolean header = true;
		List<Colum> columns = new ArrayList();
		List<String[]> rows = new ArrayList();
		int currentRow = 0;
		for (int i = 0; i < lines.length - 1; i++) {
			if (header) {
				if (lines[i].startsWith(headerSeparator)) {
					header = false;
				} else {
					updateColumns(columns, availableSizeForColumns, lines[i],
							lines[(i + 1)].startsWith(headerSeparator));
				}
			} else if (lines[i].startsWith(rowSeparator)) {
				currentRow++;
			} else {
				updateRows(rows, currentRow, numOfColumns, lines[i], lines[(i + 1)].startsWith(rowSeparator));
			}
		}

		result.colums = columns;
		result.rows = rows;

		return result;
	}

	private static int getTermWidthFromHeader(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("=====")) {
				return lines[i].length();
			}
		}
		return lines[0].length();
	}

	private static void updateColumns(List<Colum> columns, int availableSizeForColumns, String line, boolean lastLine) {
		String[] columnTitles = line.split(" \\| ");
		for (int i = 0; i < columnTitles.length; i++) {
			String text = lastLine ? columnTitles[i].trim() : columnTitles[i];
			if ((columns.size() - 1 < i) || (columns.get(i) == null)) {
				double percentaged = columnTitles[i].length() * 100.0D / availableSizeForColumns;
				int percentage = (int) Math.ceil(percentaged);
				Colum c = new Colum(text, percentage);
				columns.add(i, c);
			} else {
				String currentTitle = ((Colum) columns.get(i)).getTitle();
				((Colum) columns.get(i)).setTitle(currentTitle + text);
			}
		}
	}

	private static void updateRows(List<String[]> rows, int rowNumber, int numberOfColumns, String line,
			boolean lastLine) {
		String[] row = new String[numberOfColumns];
		if ((rows.size() - 1 < rowNumber) || (rows.get(rowNumber) == null)) {
			rows.add(rowNumber, row);
		} else {
			row = (String[]) rows.get(rowNumber);
		}

		String[] rowInfo = line.split(" \\| ");
		for (int i = 0; i < rowInfo.length; i++) {
			if (row[i] == null) {
				row[i] = "";
			}
			int tmp97_95 = i;
			String[] tmp97_93 = row;
			tmp97_93[tmp97_95] = (tmp97_93[tmp97_95] + (lastLine ? rowInfo[i].trim() : rowInfo[i]));
		}
	}

	public static boolean isTablePrinted(String printedString) {
		if ((printedString == null) || (printedString.isEmpty())) {
			return false;
		}

		String[] lines = printedString.split("\\n");

		if (lines.length < 2) {
			return false;
		}

		if (((lines[(lines.length - 1)].matches("[ \\+]+")) && (lines[(lines.length - 1)].contains("+")))
				|| (lines[(lines.length - 1)].matches("[=]+"))) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) throws InvalidColumNumber {
		Table t = new Table(20);
		t.addColum("50%", 50);
		t.addColum("1234567890123", 50);
		t.addRow(new String[] { "1234567890", "2" });
		t.addRow(new String[] { "sadasdas0", "23123123" });
		System.out.println(t.toString());
		System.out.println("");
		System.out.println(t.toString());
		System.out.println("");
		System.out.println(t.toString());
		System.out.println("");
		System.out.println("Ahora a deshacer!");
		getTableFromString(t.toString());
	}
}
