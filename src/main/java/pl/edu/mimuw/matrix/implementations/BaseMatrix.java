package pl.edu.mimuw.matrix.implementations;

import pl.edu.mimuw.matrix.IDoubleMatrix;

public abstract class BaseMatrix implements IDoubleMatrix {
  @Override
  public IDoubleMatrix minus(IDoubleMatrix other) {
    assert other != null;

    return this.plus(other.times(-1));
  }

  @Override
  public IDoubleMatrix minus(double scalar) {
    return this.plus(-scalar);
  }

  @Override
  public IDoubleMatrix timesLeft(Full other) {
    assert other != null;
    assert other.shape().columns == this.shape().rows;

    double[][] data = new double[other.shape().rows][this.shape().columns];

    for (int r = 0; r < other.shape().rows; r++) {
      for (int c = 0; c < this.shape().columns; c++) {
        data[r][c] = 0;

        for (int k = 0; k < this.shape().rows; k++) {
          data[r][c] += other.get(r, k) * this.get(k, c);
        }
      }
    }

    return new Full(data);
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;
    assert this.shape().columns == other.shape().rows;

    double[][] data = new double[this.shape().rows][other.shape().columns];

    for (int r = 0; r < this.shape().rows; r++) {
      for (int c = 0; c < other.shape().columns; c++) {
        data[r][c] = 0;

        for (int k = 0; k < this.shape().columns; k++) {
          data[r][c] += this.get(r, k) * other.get(k, c);
        }
      }
    }

    return new Full(data);
  }
}
