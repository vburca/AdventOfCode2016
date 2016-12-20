import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.*;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Firewall {

  private static final String INTERVAL = "^(\\d+)-(\\d+)$";
  private static final Pattern intervalPattern = Pattern.compile( INTERVAL );

  public static void main( String[] args ) {
    String fileName = args[0];

    SortedMap<Long, Tuple<Long, Long>> blacklistStarts =
      new TreeMap<Long, Tuple<Long, Long>>();
    SortedMap<Long, Tuple<Long, Long>> blacklistEnds =
      new TreeMap<Long, Tuple<Long, Long>>();

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        Matcher intervalMatcher = intervalPattern.matcher( line );

        if ( intervalMatcher.matches() ) {
          Long start = Long.parseLong( intervalMatcher.group( 1 ) );
          Long end = Long.parseLong( intervalMatcher.group( 2 ) );

          Tuple<Long, Long> interval = new Tuple<Long, Long>( start, end );

          blacklistStarts.put( new Long( start ), interval );
          blacklistEnds.put( new Long( end ), interval );
        }
      }

      System.out.println( part1( blacklistStarts, blacklistEnds ) );
      System.out.println( part2( blacklistStarts ) );

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static boolean contains( Tuple<Long, Long> in, Long n ) {
    return in.x <= n && n <= in.y;
  }

  private static long part1(
    SortedMap<Long, Tuple<Long, Long>> blacklistStarts,
    SortedMap<Long, Tuple<Long, Long>> blacklistEnds ) {

    long result = new Long( 0l );
    boolean found = false;

    while ( !found ) {
      found = true;
      Set<Tuple<Long, Long>> possibleIntervals =
        new HashSet<Tuple<Long, Long>>(
          blacklistStarts.headMap( result + 1 ).values() );

      Set<Tuple<Long, Long>> lowerEnds =
        new HashSet<Tuple<Long, Long>>(
          blacklistEnds.headMap( result ).values() );

      possibleIntervals.removeAll( lowerEnds );
      for ( Tuple<Long, Long> interval : possibleIntervals ) {
        if ( contains( interval, result ) ) {
          found = false;
          break;
        }
      }

      result += found ? 0 : 1;
    }

    return result;
  }


  private static long part2(
    SortedMap<Long, Tuple<Long, Long>> blacklistStarts ) {

    long blacklistCount = 0l;

    Long firstKey = blacklistStarts.firstKey();
    Long tempStart = blacklistStarts.get( firstKey ).x;
    Long tempEnd = blacklistStarts.get( firstKey ).y;

    for ( Tuple<Long, Long> interval : blacklistStarts.values() ) {
      if ( interval.x > tempEnd + 1 ) {
        blacklistCount += tempEnd - tempStart + 1;
        tempStart = interval.x;
        tempEnd = interval.y;
      } else {
        tempEnd = Math.max( tempEnd, interval.y );
      }
    }

    return (long) ( Math.pow( 2, 32 ) ) -
      blacklistCount - ( tempEnd - tempStart + 1 );
  }
}
