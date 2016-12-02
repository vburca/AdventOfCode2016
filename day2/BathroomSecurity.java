import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

public class BathroomSecurity {

  private static HashMap<Tuple<Integer, Integer>, Character> keypad;

  private static HashMap<Character, Tuple<Integer, Integer>> directions;

  public static void main( String[] args ) {
    String fileName = args[0];

    directions = new HashMap<Character, Tuple<Integer, Integer>>();
    directions.put( 'U', new Tuple( -1, 0 ) );
    directions.put( 'R', new Tuple( 0, 1 ) );
    directions.put( 'D', new Tuple( 1, 0 ) );
    directions.put( 'L', new Tuple( 0, -1 ) );

    System.out.println(
      solveKeypad( fileName, part1Keypad(), new Tuple<Integer, Integer>( 1, 1 ) ) );
    System.out.println(
      solveKeypad( fileName, part2Keypad(), new Tuple<Integer, Integer>( 2, 0 ) ) );
  }

  private static HashMap<Tuple<Integer, Integer>, Character> part1Keypad() {
    keypad = new HashMap<Tuple<Integer, Integer>, Character>();

    keypad.put( new Tuple( 0, 0 ), '1' );
    keypad.put( new Tuple( 0, 1 ), '2' );
    keypad.put( new Tuple( 0, 2 ), '3' );

    keypad.put( new Tuple( 1, 0 ), '4' );
    keypad.put( new Tuple( 1, 1 ), '5' );
    keypad.put( new Tuple( 1, 2 ), '6' );

    keypad.put( new Tuple( 2, 0 ), '7' );
    keypad.put( new Tuple( 2, 1 ), '8' );
    keypad.put( new Tuple( 2, 2 ), '9' );

    return keypad;
  }

  private static HashMap<Tuple<Integer, Integer>, Character> part2Keypad() {
    keypad = new HashMap<Tuple<Integer, Integer>, Character>();

    keypad.put( new Tuple( 0, 2 ), '1' );

    keypad.put( new Tuple( 1, 1 ), '2' );
    keypad.put( new Tuple( 1, 2 ), '3' );
    keypad.put( new Tuple( 1, 3 ), '4' );

    keypad.put( new Tuple( 2, 0 ), '5' );
    keypad.put( new Tuple( 2, 1 ), '6' );
    keypad.put( new Tuple( 2, 2 ), '7' );
    keypad.put( new Tuple( 2, 3 ), '8' );
    keypad.put( new Tuple( 2, 4 ), '9' );

    keypad.put( new Tuple( 3, 1 ), 'A' );
    keypad.put( new Tuple( 3, 2 ), 'B' );
    keypad.put( new Tuple( 3, 3 ), 'C' );

    keypad.put( new Tuple( 4, 2 ), 'D' );

    return keypad;
  }

  private static String solveKeypad(
    String fileName,
    HashMap<Tuple<Integer, Integer>, Character> keypad,
    Tuple<Integer, Integer> start ) {

    String code = "";

    Tuple<Integer, Integer> current = start;
    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;
      while ( ( line = bufferedReader.readLine() ) != null ) {

        for ( char dir : line.toCharArray() ) {
          Tuple<Integer, Integer> step = directions.get( dir );
          Tuple<Integer, Integer> next =
            new Tuple( current.x + step.x, current.y + step.y );

          if ( keypad.containsKey( next ) ) {
            current = new Tuple( next.x, next.y );
          }
        }

        code = String.format( "%s%c", code, keypad.get( current ) );
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    return code;
  }
}
