import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Elephant {

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        System.out.println( part1( Integer.parseInt( line ) ) );
        System.out.println( part2( Integer.parseInt( line ) ) );
        System.out.println();
        System.out.println();
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static int part1( int n ) {
    return ( ( n ^ Integer.highestOneBit( n ) ) << 1 ) + 1;
  }

  private static int part2( int n ) {
    int maxPower = (int) ( Math.log10( n ) / Math.log10( 3 ) );
    int maxThreePower = (int) Math.pow( 3, maxPower );
    int maxConsecutive = (int) ( 2 * maxThreePower );

    if ( n < maxConsecutive ) {
      return maxThreePower - ( maxConsecutive - n );
    } else {
      return maxThreePower + 2 * ( n - maxConsecutive );
    }

  }
}
