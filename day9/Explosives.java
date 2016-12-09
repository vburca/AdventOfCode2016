import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Explosives {

  public static void main( String[] args ) {
    String fileName = args[0];

    System.out.println( part1( fileName ) );
    System.out.println( part2( fileName ) );
  }

  private static final String MARKER = "\\((\\d+)x(\\d+)\\)";
  private static final Pattern markerPattern = Pattern.compile( MARKER );

  private static int part1( String fileName ) {
    int count = 0;

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        String sequence = line;

        Matcher markerMatcher = markerPattern.matcher( sequence );
        while ( markerMatcher.find() ) {
          int len = Integer.parseInt( markerMatcher.group( 1 ) );
          int times = Integer.parseInt( markerMatcher.group( 2 ) );

          count += markerMatcher.start();

          // If we have enough chars for the current repetition subsequence
          if ( len <= sequence.length() - markerMatcher.end() ) {
            count += len * times;
          } else { // only repeat whatever we have left I guess
            count += ( sequence.length() - markerMatcher.end() ) * times;
          }

          int nextPosition = markerMatcher.end() + len;
          if ( nextPosition < sequence.length() ) {
            sequence = sequence.substring( nextPosition );
          } else {
            sequence = "";
          }

          markerMatcher = markerPattern.matcher( sequence );
        }

        count += sequence.length();
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    return count;
  }

  private static long part2( String fileName ) {
    long count = 0;

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        count += recursiveCountLength( line );
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    return count;
  }

  private static long recursiveCountLength( String sequence ) {
    Matcher markerMatcher = markerPattern.matcher( sequence );
    if ( !markerMatcher.find() ) {
      return sequence.length();
    }

    int len = Integer.parseInt( markerMatcher.group( 1 ) );
    int times = Integer.parseInt( markerMatcher.group( 2 ) );

    long preMatchCount = markerMatcher.start();

    String repeatSeq = "";

    // If we have enough chars for the current repetition subsequence
    if ( len <= sequence.length() - markerMatcher.end() ) {
      repeatSeq = sequence.substring(
        markerMatcher.end(),
        markerMatcher.end() + len );
    } else { // only repeat whatever we have left I guess
      repeatSeq = sequence.substring(
        markerMatcher.end(),
        sequence.length() );
    }

    int nextPosition = markerMatcher.end() + len;
    if ( nextPosition < sequence.length() ) {
      sequence = sequence.substring( nextPosition );
    } else {
      sequence = "";
    }

    return preMatchCount
      + recursiveCountLength( repeatSeq ) * times
      + recursiveCountLength( sequence );
  }
}
