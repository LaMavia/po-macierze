package pl.edu.mimuw.matrix;

public final class MatrixCellValue implements Comparable<MatrixCellValue> {

  public final int row;
  public final int column;
  public final double value;

  public MatrixCellValue(int row, int column, double value) {
    this.column = column;
    this.row = row;
    this.value = value;
  }

  @Override
  public String toString() {
    return "{" + value + " @[" + row + ", " + column + "]}";
  }

  @Override
  public int compareTo(MatrixCellValue o) {
    int dr = row - o.row;
    int dc = column - o.column;

    return dr != 0 ? dr : dc;
  }

  public static MatrixCellValue cell(int row, int column, double value) {
    return new MatrixCellValue(row, column, value);
  }
}
