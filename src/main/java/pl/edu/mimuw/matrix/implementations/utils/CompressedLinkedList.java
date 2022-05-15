package pl.edu.mimuw.matrix.implementations.utils;

/* Abandoned, NO LA TOQUES  */

public class CompressedLinkedList<T> {
  private int offsetFromPrevious;
  public CompressedLinkedList<T> next;
  private T val;

  public CompressedLinkedList(int offset, T val) {
    this.offsetFromPrevious = offset;
    this.val = val;
  }

  public CompressedLinkedList<T> append(CompressedLinkedList<T> next) {
    this.next = next;

    return this;
  }

  public T val() {
    return this.val;
  }

  public int offsetFromPrevious() {
    return this.offsetFromPrevious;
  }
}
