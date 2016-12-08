import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class TwoFactorAuth {

  private static boolean[][] display;
  private static int height;
  private static int width;

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      height = 6;
      width = 50;
      display = new boolean[height][width];

      while ( ( line = bufferedReader.readLine() ) != null ) {
        Matcher rectMatcher = rectPattern.matcher( line );
        Matcher rotateMatcher = rotatePattern.matcher( line );

        if ( rectMatcher.matches() ) {
          rectDisplay(
            Integer.parseInt( rectMatcher.group( 1 ) ),
            Integer.parseInt( rectMatcher.group( 2 ) ) );
        } else if ( rotateMatcher.matches() ) {
          rotateDisplay(
            rotateMatcher.group( 1 ),
            Integer.parseInt( rotateMatcher.group( 2 ) ),
            Integer.parseInt( rotateMatcher.group( 3 ) ) );
        }
      }

      System.out.println( part1() );
      part2();

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static final String RECT = "^rect (\\d+)x(\\d+)$";
  private static final String ROTATE = "^rotate (\\w+) \\w=(\\d+) by (\\d+)$";
  private static final Pattern rectPattern = Pattern.compile( RECT );
  private static final Pattern rotatePattern = Pattern.compile( ROTATE );

  private static void rectDisplay( int w, int h ) {
    for ( int i = 0; i < h; i++ ) {
      for ( int j = 0; j < w; j++ ) {
        display[i][j] = true;
      }
    }
  }

  private static void rotateDisplay( String direction, int n, int displ ) {
    switch ( direction ) {
      case "row": rotateRow( n, displ ); break;
      case "column": rotateCol( n, displ ); break;
    }
  }

  private static void rotateRow( int n, int displ ) {
    boolean[] copy = new boolean[width];

    // Copy original row
    for ( int i = 0; i < width; i++ ) {
      copy[i] = display[n][i];
    }

    // Do the rotation
    for ( int i = 0; i < width; i++ ) {
      display[n][( i + displ ) % width] = copy[i];
    }
  }

  private static void rotateCol( int n, int displ ) {
    boolean[] copy = new boolean[height];

    // Copy original column
    for ( int i = 0; i < height; i++ ) {
      copy[i] = display[i][n];
    }

    // Do the rotation
    for ( int i = 0; i < height; i++ ) {
      display[( i + displ ) % height][n] = copy[i];
    }
  }

  private static int part1() {
    int count = 0;

    for ( int i = 0; i < height; i++ ) {
      for ( int j = 0; j < width; j++ ) {
        count += display[i][j] ? 1 : 0;
      }
    }

    return count;
  }

  private static void part2() {
    printDisplay();
  }

  private static void printDisplay() {
    for ( int i = 0; i < height; i++ ) {
      for ( int j = 0; j < width; j++ ) {
        if ( display[i][j] ) {
          System.out.print( "#" );
        } else {
          System.out.print( " " );
        }
      }

      System.out.println();
    }
  }
}
