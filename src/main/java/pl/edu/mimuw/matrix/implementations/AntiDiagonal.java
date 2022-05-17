package pl.edu.mimuw.matrix.implementations;

import pl.edu.mimuw.matrix.IDoubleMatrix;

public class AntiDiagonal extends Diagonal {
  public AntiDiagonal(double... values) {
    super(values);
  }

  public int size() {
    return this.values.length;
  }

  @Override
  public IDoubleMatrix plus(IDoubleMatrix other) {
    return other.plusLeft(this);
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

    double[] values = this.values.clone();

    for (int i = 0; i < values.length; i++) {
      values[i] *= scalar;
    }

    return new AntiDiagonal(values);
  }

  @Override
  protected int indexCompliment(int i) {
    return this.size() - i - 1;
  }

  @Override
  public IDoubleMatrix plusLeft(Diagonal other) {
    assert other != null;

    return other.plusLeft(this);
  }

  @Override
  public IDoubleMatrix plusLeft(AntiDiagonal other) {
    assert other != null;
    assert other.shape().equals(this.shape());

    double[] values = new double[this.size()];

    for (int i = 0; i < this.size(); i++) {
      values[i] = this.get(i, this.indexCompliment(i)) + other.get(i, this.indexCompliment(i));
    }

    return new AntiDiagonal(values);
  }

  @Override
  public IDoubleMatrix timesLeft(Diagonal other) {
    assert other != null;
    assert other.size() == this.size();

    double[] values = new double[this.size()];

    for (int i = 0; i < this.size(); i++) {
      values[i] = this.get(i, this.indexCompliment(i)) * other.get(i, other.indexCompliment(i));
    }

    return new AntiDiagonal(values);
  }

  @Override
  public IDoubleMatrix timesLeft(AntiDiagonal other) {
    assert other != null;
    assert other.size() == this.size();

    int zeroCount = 0, oneCount = 0;
    double[] values = new double[this.size()];

    for (int i = 0; i < this.size(); i++) {
      values[i] = this.get(this.indexCompliment(i), i) * other.get(i, other.indexCompliment(i));
    }

    if (zeroCount == this.size()) {
      return new Zero(this.shape());
    }
    if (oneCount == this.size()) {
      return new Identity(this.size());
    }

    return new Diagonal(values);
  }

  @Override
  protected int leftDistance(int r) {
    return this.size() - r - 1;
  }

  @Override
  protected int rightDistance(int r) {
    return r;
  }
}
