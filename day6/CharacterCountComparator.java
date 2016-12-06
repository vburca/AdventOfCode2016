import java.util.Comparator;
import java.util.Map;

public class CharacterCountComparator implements Comparator<Map.Entry<Character, Integer>> {

  @Override
  public int compare( Map.Entry<Character, Integer> first, Map.Entry<Character, Integer> second ) {
    return first.getValue() - second.getValue();
  }
}
