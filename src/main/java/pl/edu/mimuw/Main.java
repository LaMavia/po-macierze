package pl.edu.mimuw;

import java.util.Arrays;

import pl.edu.mimuw.matrix.IDoubleMatrix;
import pl.edu.mimuw.matrix.MatrixCellValue;
import pl.edu.mimuw.matrix.Shape;
import pl.edu.mimuw.matrix.implementations.AntiDiagonal;
import pl.edu.mimuw.matrix.implementations.CSR;
import pl.edu.mimuw.matrix.implementations.Diagonal;
import pl.edu.mimuw.matrix.implementations.Full;
import pl.edu.mimuw.matrix.implementations.Identity;
import pl.edu.mimuw.matrix.implementations.Vector;

public class Main {

  private static void printMatrix(IDoubleMatrix m) {
    System.out.println("\n" + m);

    for (var row : m.data()) {
      System.out.println(Arrays.toString(row));
    }
  }

  public static void main(String[] args) {
    // /*
    var m = new CSR(
        Shape.matrix(3, 3),
        MatrixCellValue.cell(0, 0, 7),
        MatrixCellValue.cell(0, 1, 5),
        MatrixCellValue.cell(2, 1, 2),
        MatrixCellValue.cell(1, 2, 11),
        MatrixCellValue.cell(1, 1, 3));
    //
    // */

    System.out.println(m.shape());

    // printMatrix(m);
    printMatrix(m.plus(new AntiDiagonal(1, 9, 3)));

    // printMatrix(new AntiDiagonal(5, 9, 7).times(new Diagonal(1, 2, 3)));

    // var m = new Vector(5, 0, 1, 0, -1, -2, 0, 1, 0);
    /*
     * var m = new Diagonal(
     * 1, 2, 3);
     * 
     * System.out.println(m);
     * var n = new Vector(1, 2, 0);
     * 
     * System.out.println(m.times(n));
     */
    // for (var row : m.data()) {
    // System.out.println(Arrays.toString(row));
    // }

    /*
     * ROW_INDEX = [ 0 1 2 3 4 ]
     * COL_INDEX = [ 0 1 2 1 ]
     * V = [ 5 8 3 6 ]
     */

    /*
     * row:
     * [0, 1, 3, 4]
     * column:
     * [0, 1, 2, 1]
     * value:
     * [1.0, 3.0, 11.0, 2.0]
     * 
     * [1.0, 0.0, 0.0]
     * [0.0, 3.0, 11.0]
     * [0.0, 2.0, 0.0]
     */

    // Tu trzeba wpisać kod testujący toString dla poszczególnych macierzy i
    // wyników
    // operacji
  }
}
