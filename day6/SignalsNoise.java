import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import java.lang.StringBuilder;

public class SignalsNoise {

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      ArrayList<HashMap<Character, Integer>> frequentChars =
        new ArrayList<HashMap<Character, Integer>>();

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        if ( frequentChars.size() < line.length() ) {
          frequentChars = initialize( line.length() );
        }

        for ( int i = 0; i < line.length(); i++ ) {
          HashMap<Character, Integer> hm = frequentChars.get( i );
          Character key = new Character( line.charAt( i ) );

          if ( hm.containsKey( key ) ) {
            int count = hm.remove( key );
            hm.put( key, count + 1 );
          } else {
            hm.put( key, 1 );
          }
        }

      }

      System.out.println( part1( frequentChars ) );
      System.out.println( part2( frequentChars ) );

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static String part1( ArrayList<HashMap<Character, Integer>> frequentChars ) {
    StringBuilder result = new StringBuilder();

    Comparator<Map.Entry<Character, Integer>> comparator =
      new CharacterCountComparator();

    for ( HashMap<Character, Integer> hm : frequentChars ) {
      Map.Entry<Character, Integer> max =
        Collections.max( hm.entrySet(), comparator );
      result.append( max.getKey() );
    }

    return result.toString();
  }

  private static String part2( ArrayList<HashMap<Character, Integer>> frequentChars ) {
    StringBuilder result = new StringBuilder();

    Comparator<Map.Entry<Character, Integer>> comparator =
      new CharacterCountComparator().reversed();

    for ( HashMap<Character, Integer> hm : frequentChars ) {
      Map.Entry<Character, Integer> max =
        Collections.max( hm.entrySet(), comparator );
      result.append( max.getKey() );
    }

    return result.toString();
  }

  private static ArrayList<HashMap<Character, Integer>> initialize( int n ) {
    ArrayList<HashMap<Character, Integer>> chars =
      new ArrayList<HashMap<Character, Integer>>( n );

    for ( int i = 0; i < n; i++ ) {
      chars.add( i, new HashMap<Character, Integer>() );
    }

    return chars;
  }
}
