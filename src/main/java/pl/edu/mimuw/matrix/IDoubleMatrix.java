package pl.edu.mimuw.matrix;

import pl.edu.mimuw.matrix.implementations.AntiDiagonal;
import pl.edu.mimuw.matrix.implementations.CSR;
import pl.edu.mimuw.matrix.implementations.Diagonal;
import pl.edu.mimuw.matrix.implementations.Full;
import pl.edu.mimuw.matrix.implementations.Identity;
import pl.edu.mimuw.matrix.implementations.Vector;
import pl.edu.mimuw.matrix.implementations.Zero;

public interface IDoubleMatrix {

  /* start: times */

  // other * this
  IDoubleMatrix timesLeft(Zero other);
  IDoubleMatrix timesLeft(CSR other);
  IDoubleMatrix timesLeft(Full other);
  IDoubleMatrix timesLeft(Diagonal other);
  IDoubleMatrix timesLeft(Vector other);
  IDoubleMatrix timesLeft(AntiDiagonal other);

  // this * other
  IDoubleMatrix times(IDoubleMatrix other);
  IDoubleMatrix times(double scalar);
  /* end: times */


  /* start: plus */ 
  
  // other + this
  IDoubleMatrix plusLeft(Identity other);
  IDoubleMatrix plusLeft(Zero other);
  IDoubleMatrix plusLeft(CSR other);
  IDoubleMatrix plusLeft(Full other);
  IDoubleMatrix plusLeft(Diagonal other);
  IDoubleMatrix plusLeft(Vector other);
  IDoubleMatrix plusLeft(AntiDiagonal other);

  // this + other
  IDoubleMatrix plus(IDoubleMatrix other);
  IDoubleMatrix plus(double scalar);

  /* end: plus */

  IDoubleMatrix minus(IDoubleMatrix other);

  IDoubleMatrix minus(double scalar);

  double get(int row, int column);

  double[][] data();

  double normOne();

  double normInfinity();

  double frobeniusNorm();

  String toString();

  Shape shape();
}
