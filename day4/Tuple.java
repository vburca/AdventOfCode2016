public class Tuple<X, Y> {
  public final X x;
  public final Y y;

  public Tuple( X x, Y y) {
    this.x = x;
    this.y = y;
  }

  @Override public boolean equals( Object other ) {
    if ( other instanceof Tuple ) {
      Tuple that = (Tuple) other;
      return this.x == that.x && this.y == that.y;
    }

    return false;
  }

  @Override public int hashCode() {
    int hash = 1;

    hash = x == null ? 0 : hash * 17 + x.hashCode();
    hash = y == null ? 0 : hash * 31 + y.hashCode();

    return hash;
  }

  @Override public String toString() {
    return String.format( "( %s, %s )", this.x, this.y );
  }
}
