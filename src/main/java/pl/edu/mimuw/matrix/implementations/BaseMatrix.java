package pl.edu.mimuw.matrix.implementations;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.Shape;

public abstract class BaseMatrix implements IDoubleMatrix {
  /*   
  @Override
  public IDoubleMatrix plus(double scalar) {
    // TODO Auto-generated method stub
    return null;
  } */

  @Override
  public IDoubleMatrix minus(IDoubleMatrix other) {
    assert other != null;

    return this.plus(other.times(-1));
  }

  @Override
  public IDoubleMatrix minus(double scalar) {
    return this.plus(-scalar);
  }
}
