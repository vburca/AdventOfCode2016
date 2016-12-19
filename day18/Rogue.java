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


public class Rogue {

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        System.out.println( getTilesCount( line, 40 ) ); // part 1
        System.out.println( getTilesCount( line, 400000 ) ); // part 2
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static int getTilesCount( String line, int rows ) {
    int count = 0;
    for ( char ch : line.toCharArray() ) {
      if ( ch == '.' ) {
        count += 1;
      }
    }

    for ( int i = 1; i < rows; i++ ) {
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
    }

    return count;
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
