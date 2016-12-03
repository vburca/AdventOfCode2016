import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

public class SquaresThreeSides {

  public static void main( String[] args ) {
    String fileName = args[0];

    part1( fileName );
    part2( fileName );
  }

  private static void part1( String fileName ) {
    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      int count = 0;

      String line;
      while ( ( line = bufferedReader.readLine() ) != null ) {
        String[] sidesStr = line.trim().split( "\\s+" );

        int[] sides = new int[3];

        for ( int i = 0; i < 3; i++ ) {
          sides[i] = Integer.parseInt( sidesStr[i] );
        }

        if ( isTriangle( sides ) ) {
          count += 1;
        }
      }

      System.out.println( count );

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static boolean isTriangle( int[] sides ) {
    Arrays.sort( sides );

    if ( sides[0] + sides[1] > sides[2] ) {
      return true;
    }

    return false;
  }

  private static void part2( String fileName ) {
    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      int count = 0;
      int trianglesRead = 0;

      String line;

      int[][] sides = new int[3][3];
      while ( ( line = bufferedReader.readLine() ) != null ) {
        String[] sidesStr = line.trim().split( "\\s+" );

        trianglesRead += 1;

        for ( int i = 0; i < 3; i++ ) {
          sides[i][trianglesRead - 1] = Integer.parseInt( sidesStr[i] );
        }

        if ( trianglesRead == 3 ) {
          for ( int triangle = 0; triangle < 3; triangle++ ) {
            if ( isTriangle( sides[triangle] ) ) {
              count += 1;
            }
          }

          trianglesRead = 0;
        }
      }

      System.out.println( count );

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }
}
