package pl.edu.mimuw.matrix.implementations;

import java.util.Arrays;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.Shape;

public class Diagonal implements IDoubleMatrix {
  private double[] values;

  public Diagonal(double... values) {
    this.values = values;
  }

  @Override
  public IDoubleMatrix times(IDoubleMatrix other) {
    return other.timesLeft(this);
  }

  @Override
  public IDoubleMatrix times(double scalar) {
    if (Math.abs(scalar) == 0) {
      return new Zero(this.shape());
    }

    if (scalar == 1) {
      return this;
    }

    double[] newValues = this.values.clone();

    for (int i = 0; i < this.values.length; i++) {
      newValues[i] *= scalar;
    }

    return new Diagonal(newValues);
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    assert other != null;
    assert other.shape() == this.shape();

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plus(double scalar) {
    double[][] values = new double[this.values.length][this.values.length];

    for (int i = 0; i < this.values.length; i++) {
      for (int j = 0; j < this.values.length; j++) {
        values[i][j] = scalar + this.get(i, j);
      }
    }

    return new Full(values);
  }

  @Override
  public IDoubleMatrix minus(IDoubleMatrix other) {
    return this.plus(other.times(-1));
  }

  @Override
  public IDoubleMatrix minus(double scalar) {
    return this.plus(-scalar);
  }

  @Override
  public double get(int row, int column) {
    assert 0 <= row & row < this.values.length;
    assert 0 <= column & column < this.values.length;

    if (row == column) {
      return this.values[row];
    } else {
      return 0;
    }
  }

  @Override
  public double[][] data() {
    double[][] data = new double[this.values.length][this.values.length];

    for (int i = 0; i < this.values.length; i++) {
      for (int j = 0; j < this.values.length; j++) {
        data[i][j] = this.get(i, j);
      }
    }

    return data;
  }

  @Override
  public double normOne() {
    double max = 0;

    for (double v : this.values) {
      max = Math.max(Math.abs(v), max);
    }

    return max;
  }

  @Override
  public double normInfinity() {
    return this.normOne();
  }

  @Override
  public double frobeniusNorm() {
    double sum = 0;

    for (double v : this.values) {
      sum += Math.abs(v);
    }

    return Math.sqrt(sum);
  }

  @Override
  public Shape shape() {
    return Shape.matrix(this.values.length, this.values.length);
  }

  @Override
  public IDoubleMatrix plusLeft(Zero other) {
    assert other != null;
    assert other.shape() == this.shape();

    return this;
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
