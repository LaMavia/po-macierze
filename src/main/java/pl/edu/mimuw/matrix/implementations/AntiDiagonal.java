package pl.edu.mimuw.matrix.implementations;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.Shape;

public class AntiDiagonal implements IDoubleMatrix {
  private double[] values;

  public AntiDiagonal(double... values) {
    this.values = values;
  }

  public int size() {
    return this.values.length;
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    assert other != null;

    return other.timesLeft(this);
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix minus(IDoubleMatrix other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix minus(double scalar) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public double get(int row, int column) {
    assert 0 <= row & row < this.values.length;
    assert 0 <= column & column < this.values.length;

    if (column == this.size() - row - 1) {
      return this.values[row];
    } else {
      return 0;
    }
  }

  @Override
  public double[][] data() {
    double[][] data = new double[this.size()][this.size()];

    for (int i = 0; i < this.values.length; i++) {
      for (int j = 0; j < this.values.length; j++) {
        data[i][j] = this.get(i, j);
      }
    }

    return data;
  }

  @Override
  public double normOne() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double normInfinity() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public double frobeniusNorm() {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Shape shape() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(CSR other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(Full other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(Zero other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(CSR other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(Full other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(Diagonal other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(AntiDiagonal other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix timesLeft(Vector other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(Vector other) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IDoubleMatrix plusLeft(Identity other) {
    // TODO Auto-generated method stub
    return null;
  }

}
