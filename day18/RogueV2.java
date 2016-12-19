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


public class RogueV2 {

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        // System.out.println( getTilesCount( line, 10 ) ); // test
        // System.out.println( getTilesCount( line, 40 ) ); // part 1
        // System.out.println( getTilesCount( line, 400000 ) ); // part 2
        // System.out.println( getTilesCount( line, 4000000000000l ) ); // ante
        System.out.println( getTilesCount( line, 12345678910111213l ) ); // ante 2
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static long getTilesCount( String line, long rows ) {
    HashMap<String, Long> history = new HashMap<String, Long>();
    HashMap<Long, Long> counts = new HashMap<Long, Long>();

    long count = 0;
    for ( char ch : line.toCharArray() ) {
      if ( ch == '.' ) {
        count += 1;
      }
    }

    counts.put( 0l, count );
    history.put( line, 0l );

    for ( long i = 1; i < rows; i++ ) {
      StringBuilder newLine = new StringBuilder();
      for ( int chIndex = 0; chIndex < line.length(); chIndex++ ) {
        if ( isSafe( line, chIndex ) ) {
          count += 1;
          newLine.append( '.' );
        } else {
          newLine.append( '^' );
        }
      }

      line = newLine.toString();

      if ( history.containsKey( line ) ) {
        System.out.format( "Found cycle: %s\n", line );
        long startCycleIndex = history.get( line );
        long remainingRows = rows - startCycleIndex;
        long cycleLength = i - startCycleIndex;

        long preCycleCount = startCycleIndex > 0 ? counts.get( startCycleIndex - 1 ) : 0;
        // full cycles
        long fullCycleCount =
          ( remainingRows / cycleLength ) * getCount( counts, startCycleIndex, i - 1 ) ;

        // part of cycle
        long partCycleCount = getCount( counts, startCycleIndex,
          startCycleIndex + ( remainingRows % cycleLength - 1) );

        return preCycleCount + fullCycleCount + partCycleCount;
      }

      counts.put( i, count );
      history.put( line, i );
    }

    return count;
  }

  private static long getCount( HashMap<Long, Long> counts, long start, long end ) {
    long prevCount = start > 0 ? counts.get( start - 1 ) : 0;
    return counts.get( end ) - prevCount;
  }

  private static final String TRAP = "^\\.\\^\\^|\\^\\^\\.|\\^\\.\\.|\\.\\.\\^$";
  private static final Pattern trapPattern = Pattern.compile( TRAP );
  private static boolean isSafe( String prevLine, int index ) {
    String subs = "";
    if ( index == 0 ) {
      subs = String.format( "%c%s", '.', prevLine.substring( index, index + 2 ) );
    } else if ( index == prevLine.length() - 1 ) {
      subs = String.format( "%s%c", prevLine.substring( index - 1, index + 1 ), '.' );
    } else {
      subs = prevLine.substring( index - 1, index + 2 );
    }

    Matcher trapMatcher = trapPattern.matcher( subs );

    return !trapMatcher.matches();
  }
}
