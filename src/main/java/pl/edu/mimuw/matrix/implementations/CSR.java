package pl.edu.mimuw.matrix.implementations;

import java.util.ArrayList;
import java.util.Arrays;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.MatrixCellValue;
import pl.edu.mimuw.matrix.Shape;

public class CSR extends BaseMatrix {
  public final int nnz;
  public final int ner;
  private final double[] value;
  private final int[] column;
  private final int[] row;
  private final int[] rowPointer;
  private final Shape shape;

  private int countNER(MatrixCellValue[] values) {
    assert values != null;

    if (values.length == 0) {
      return 0;
    }

    int previousRow = values[0].row;
    int ner = 1;

    for (MatrixCellValue v : values) {
      if (v.row != previousRow) {
        ner++;
        previousRow = v.row;
      }
    }

    return ner;
  }

  private int countNNZ(MatrixCellValue[] values) {
    assert values != null;

    int nnz = 0;

    for (MatrixCellValue v : values) {
      if (v.value != 0)
        nnz++;
    }

    return nnz;
  }

  private void assertInputValidity(Shape shape, MatrixCellValue... values) {
    assert shape != null;
    assert values != null;
    assert values.length > 0;

    for (MatrixCellValue v : values) {
      assert (0 <= v.row && v.row < shape.rows);
      assert (0 <= v.column && v.column < shape.columns);
    }
  }

  private CSR(
      double[] value,
      int[] rowPointer,
      int[] column,
      int[] row,
      Shape shape) {
    this.nnz = value.length;
    this.ner = row.length - 1;

    this.value = value;
    this.column = column;
    this.row = row;
    this.rowPointer = rowPointer;
    this.shape = shape;
  }

  public CSR(Shape shape, MatrixCellValue... values) {
    assertInputValidity(shape, values);

    Arrays.sort(values); // O(n log(n))

    this.shape = shape;
    this.nnz = countNNZ(values); // O(n)
    this.ner = countNER(values);

    this.value = new double[this.nnz];
    this.column = new int[this.nnz];
    this.row = new int[this.ner + 1];
    this.rowPointer = new int[this.ner + 1];

    int valueId = 0;
    int rowIndex = 0;
    int previousRow = -1;

    this.row[this.ner] = this.nnz;
    this.rowPointer[this.ner] = this.shape.rows;

    for (MatrixCellValue v : values) {
      if (v.value == 0)
        continue;

      if (v.row > previousRow) {
        this.rowPointer[rowIndex] = v.row;
        this.row[rowIndex] = valueId;
        rowIndex++;

        previousRow = v.row;
      }

      this.column[valueId] = v.column;
      this.value[valueId] = v.value;

      valueId++;
    }
  }

  public int getRowPointer(int r) {
    assert 0 <= r && r < this.shape.rows : String.format("%d/%d", r, this.shape.rows);

    int i = 0;

    for (; i < this.ner && this.rowPointer[i] <= r; i++) {
      if (this.rowPointer[i] == r) {
        return i;
      }
    }

    return this.ner;
  }

  public int getRowNumber(int i) {
    assert 0 <= i && i < this.ner;

    return this.rowPointer[i];
  }

  public int getRowStart(int i) {
    assert 0 <= i && i <= this.ner : String.format("%d/%d", i, this.ner);

    return this.row[i];
  }

  public int getRowEnd(int i) {
    assert 0 <= i && i <= this.ner;

    if (i == this.ner) {
      return this.row[this.ner];
    }

    return this.row[i + 1];
  }

  public int getColumn(int i) {
    assert 0 <= i && i < this.column.length;

    return this.column[i];
  }

  public double getValue(int i) {
    assert 0 <= i && i < this.value.length;

    return this.value[i];
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    if (scalar == 0.0) {
      return new Zero(this.shape);
    }
    if (scalar == 1.0) {
      return this;
    }

    double[] valueNew = this.value.clone();

    for (int i = 0; i < valueNew.length; i++) {
      valueNew[i] *= scalar;
    }

    return new CSR(valueNew, this.rowPointer, column, row, shape);
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;

    return other.timesLeft(this);
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert other != null;

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    if (Math.abs(scalar) == 0.0) {
      return this;
    }

    return new Full(this.data()).plus(scalar);
  }

  @Override
  public IDoubleMatrix minus(IDoubleMatrix other) {
    return this.plus(other.times(-1.0));
  }

  @Override
  public IDoubleMatrix minus(double scalar) {
    if (Math.abs(scalar) == 0.0) {
      return this;
    }

    return new Full(this.data()).minus(scalar);
  }

  @Override
  public double get(int row, int column) {
    assert 0 <= row && row < this.shape.rows;
    assert 0 <= column && column < this.shape.columns;

    int start = this.getRowStart(this.getRowPointer(row));
    int end = this.getRowEnd(this.getRowPointer(row));

    for (int ptr = start; ptr < end; ptr++) {
      if (this.getColumn(ptr) == column) {
        return this.getValue(ptr);
      }
    }

    return 0;
  }

  @Override
  public double[][] data() {
    double[][] data = new double[this.shape.rows][this.shape.columns];

    for (int i = 0; i < this.shape.rows; i++) {
      for (int j = 0; j < this.shape.columns; j++) {
        data[i][j] = this.get(i, j);
      }
    }

    return data;
  }

  @Override
  public double normOne() {
    // Calculate column sums
    double[] columnSums = new double[this.shape.columns];

    for (int ri = 0; ri < this.ner; ri++) {
      for (int ptr = this.getRowStart(ri); ptr < this.getRowEnd(ri); ptr++) {
        columnSums[this.getColumn(ptr)] += Math.abs(this.getValue(ptr));
      }
    }

    // Pick the max
    double maxColumnSum = -1;

    for (int i = 0; i < columnSums.length; i++) {
      maxColumnSum = Math.max(maxColumnSum, columnSums[i]);
    }

    return maxColumnSum;
  }

  @Override
  public double normInfinity() {
    double maxRowSum = 0;

    for (int i = 0; i < this.ner; i++) {
      int rowPtr = this.getRowStart(i);
      int rowPtrEnd = this.getRowEnd(i);

      double rowSum = 0;

      while (rowPtr < rowPtrEnd) {
        rowSum += Math.abs(this.getValue(rowPtr++));
      }

      maxRowSum = Math.max(maxRowSum, rowSum);
    }

    return maxRowSum;
  }

  @Override
  public double frobeniusNorm() {
    double sum = 0;

    for (double v : this.value) {
      sum += v * v;
    }

    return Math.sqrt(sum);
  }

  @Override
  public Shape shape() {
    return this.shape;
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    assert other != null;

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    assert other != null;
    assert other.shape.equals(this.shape);

    int index = 0;
    MatrixCellValue[] values = new MatrixCellValue[this.nnz + other.nnz];

    int thisRi = 0;
    int otherRi = 0;

    while (thisRi < this.ner && otherRi < other.ner) {
      if (this.getRowNumber(thisRi) == other.getRowNumber(otherRi)) {
        int r = this.getRowNumber(thisRi);

        int thisPtr = this.getRowStart(thisRi);
        int otherPtr = other.getRowStart(otherRi);

        while (thisPtr < this.getRowEnd(thisRi) && otherPtr < other.getRowEnd(otherRi)) {
          int thisC = this.getColumn(thisPtr);
          int otherC = other.getColumn(otherPtr);

          if (thisC == otherC) {
            values[index++] = new MatrixCellValue(r, thisC, this.getValue(thisPtr++) + other.getValue(otherPtr++));
          } else if (thisC < otherC) {
            values[index++] = new MatrixCellValue(r, thisC, this.getValue(thisPtr++));
          } else {
            values[index++] = new MatrixCellValue(r, otherC, other.getValue(otherPtr++));
          }
        }

        // Push unmatched columns
        while (thisPtr < this.getRowEnd(thisRi)) {
          values[index++] = new MatrixCellValue(r, this.getColumn(thisPtr), this.getValue(thisPtr));
          thisPtr++;
        }

        while (otherPtr < other.getRowEnd(otherRi)) {
          values[index++] = new MatrixCellValue(r, other.getColumn(otherPtr), other.getValue(otherPtr));
          otherPtr++;
        }

        thisRi++;
        otherRi++;
      } else if (this.getRowNumber(thisRi) < other.getRowNumber(otherRi)) {
        // Push unmatched row from this
        for (int thisPtr = this.getRowStart(thisRi); thisPtr < this.getRowEnd(thisRi); thisPtr++) {
          values[index++] = new MatrixCellValue(this.getRowNumber(thisRi), this.getColumn(thisPtr),
              this.getValue(thisPtr));
        }

        thisRi++;
      } else {
        // Push unmatched row from other
        for (int otherPtr = other.getRowStart(otherRi); otherPtr < other.getRowEnd(otherRi); otherPtr++) {
          values[index++] = new MatrixCellValue(other.getRowNumber(otherRi), other.getColumn(otherPtr),
              other.getValue(otherPtr));
          otherPtr++;
        }

        otherRi++;
      }
    }

    for (; thisRi < this.ner; thisRi++) {
      for (int thisPtr = this.getRowStart(thisRi); thisPtr < this.getRowEnd(thisRi); thisPtr++) {
        values[index++] = new MatrixCellValue(this.getRowNumber(thisRi), this.getColumn(thisPtr),
            this.getValue(thisPtr));
      }
    }

    for (; otherRi < other.ner; otherRi++) {
      for (int otherPtr = other.getRowStart(otherRi); otherPtr < other.getRowEnd(otherRi); otherPtr++) {
        values[index++] = new MatrixCellValue(other.getRowNumber(otherRi), other.getColumn(otherPtr),
            other.getValue(otherPtr));
        otherPtr++;
      }
    }

    values = Arrays.copyOf(values, index);

    return new CSR(this.shape, values);
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    double[][] data = new double[this.shape.rows][this.shape.columns];
    for (int i = 0; i < this.shape.rows; i++) {
      for (int j = 0; j < this.shape.columns; j++) {
        data[i][j] = other.get(i, j);
      }

      int ri = this.getRowPointer(i);

      for (int ptr = this.getRowStart(ri); ptr < this.getRowEnd(ri); ptr++) {
        data[i][this.getColumn(ptr)] += this.getValue(ptr);
      }
    }

    return new Full(data);
  }

  private IDoubleMatrix plusLeftGeneric(IDoubleMatrix other) {
    assert other != null;
    assert other.shape().equals(this.shape);

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    return this.plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    return this.plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(Vector other) {
    return this.plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix plusLeft(Identity other) {
    return this.plusLeftGeneric(other);
  }

  @Override
  public IDoubleMatrix timesLeft(Zero other) {
    assert other != null;
    assert other.shape.columns == this.shape.rows;

    return new Zero(Shape.matrix(other.shape.rows, this.shape.columns));
  }

  @Override
  public IDoubleMatrix timesLeft(CSR other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    int nnz = 0;

    ArrayList<MatrixCellValue> values = new ArrayList<>();
    MatrixCellValue[] data = new MatrixCellValue[0];

    for (int ri = 0; ri < other.ner; ri++) {
      for (int c = 0; c < this.shape.columns; c++) {
        double sum = 0;

        for (int ptr = other.getRowStart(ri); ptr < other.getRowEnd(ri); ptr++) {
          sum += other.getValue(ptr) *
              this.get(other.getColumn(ptr), c);
        }

        if (sum != 0) {
          nnz++;
          values.add(new MatrixCellValue(other.getRowNumber(ri), c, sum));
        }

      }
    }

    Shape newShape = Shape.matrix(other.shape.rows, this.shape.columns);

    if (nnz == 0) {
      return new Zero(newShape);
    }

    return new CSR(newShape, values.toArray(data));
  }

  @Override
  public IDoubleMatrix timesLeft(Full other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    ArrayList<MatrixCellValue> values = new ArrayList<>();
    MatrixCellValue[] data = new MatrixCellValue[0];

    for (int r = 0; r < other.shape().rows; r++) {
      for (int c = 0; c < this.shape.columns; c++) {
        double sum = 0;

        for (int k = 0; k < other.shape().columns; k++) {
          sum += other.get(r, k) * this.get(k, c);
        }

        if (sum != 0) {
          values.add(new MatrixCellValue(r, c, sum));
        }

      }
    }

    Shape newShape = Shape.matrix(other.shape().rows, this.shape.columns);

    if (values.size() == 0) {
      return new Zero(newShape);
    }

    return new CSR(newShape, values.toArray(data));
  }

  @Override
  public IDoubleMatrix timesLeft(Diagonal other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    int index = 0;
    MatrixCellValue[] values = new MatrixCellValue[this.nnz];

    for (int r = 0; r < other.shape().rows; r++) {
      double scalar = other.get(r, other.indexCompliment(r));
      int ri = this.getRowPointer(other.indexCompliment(r));

      if (scalar == 0) {
        continue;
      }

      for (int ptr = this.getRowStart(ri); ptr < this
          .getRowEnd(ri); ptr++) {
        double value = scalar * this.getValue(ptr);

        if (value != 0) {
          values[index++] = new MatrixCellValue(r, this.getColumn(ptr), value);
        }
      }
    }

    Shape newShape = Shape.matrix(other.shape().rows, this.shape.columns);

    if (index == 0) {
      return new Zero(newShape);
    }

    values = Arrays.copyOf(values, index);

    return new CSR(newShape, values);
  }

  @Override
  public IDoubleMatrix timesLeft(AntiDiagonal other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    return this.timesLeft((Diagonal) other);
  }

  @Override
  public IDoubleMatrix timesLeft(Vector other) {
    assert other != null;
    assert other.shape().columns == this.shape.rows;

    int index = 0;
    MatrixCellValue[] values = new MatrixCellValue[this.nnz * other.numberOfRows()];

    for (int r = 0; r < other.numberOfRows(); r++) {
      double scalar = other.get(r, 0);

      if (scalar == 0) {
        continue;
      }

      for (int ptr = this.getRowStart(r); ptr < this.getRowEnd(r); ptr++) {
        double value = scalar * this.getValue(ptr);

        if (value != 0) {
          values[index++] = new MatrixCellValue(r, this.getColumn(ptr), value);
        }
      }
    }

    Shape newShape = Shape.matrix(other.numberOfRows(), this.shape.columns);

    if (index == 0) {
      return new Zero(newShape);
    }

    return new CSR(newShape, Arrays.copyOf(values, index));
  }

  private String stringifyCell(int ptr, int distance, boolean last) {
    String out = "";

    if (distance >= 3) {
      out = this.getValue(ptr) + " ... ";
    } else if (distance == 2) {
      out = this.getValue(ptr) + " 0 ";
    } else {
      out = this.getValue(ptr) + " " + (last ? "0" : "");
    }

    return out;
  }

  private String stringifyRow(int row) {
    int ri = this.getRowPointer(row);

    int ptr = this.getRowStart(ri);
    int ptrEnd = this.getRowEnd(ri);

    String out = "";

    if (ptr < ptrEnd) {

      switch (this.getColumn(ptr)) {
        case 0:
          break;
        case 1:
          out += "0 ";
          break;
        case 2:
          out += "0 0 ";
          break;
        default:
          out += "0 ... ";
          break;
      }

      for (; ptr < ptrEnd - 1; ptr++) {
        out += this.stringifyCell(ptr, this.getColumn(ptr + 1) - this.getColumn(ptr), false);
      }

      out += this.stringifyCell(ptr, this.shape.columns - 1 - this.getColumn(ptr), true) + "\n";

      return out;
    } else {
      return "0 ... 0\n";
    }

  }

  @Override
  public String toString() {
    String out = super.toString();

    for (int r = 0; r < this.shape.rows; r++) {
      out += this.stringifyRow(r);
    }

    return out;
  }
}
