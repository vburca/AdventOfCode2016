import java.util.Comparator;

public class CharacterCountComparator implements Comparator<Tuple<Character, Integer>> {

  // Reversing the ordering to make sure that the head of the PQ will be the highest
  // element with respect to my ordering:
  // - First look at the count
  // - If the count is equal, look at the natural ordering of chars
  @Override
  public int compare( Tuple<Character, Integer> first, Tuple<Character, Integer> second ) {
    if ( first.y != second.y ) {
      // Since head of PQ is the *least* element with respect to this ordering
      return -1 * ( first.y - second.y );
    } else {
      // Here the natural ordering of chars is what we want, because the *least* element
      // is the char that comes first in alphabetical order
      return first.x - second.x;
    }
  }
}
