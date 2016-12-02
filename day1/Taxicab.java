import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Taxicab {

  public static void main( String[] args ) {
    String fileName = args[0];

    System.out.println( part1( fileName ) );
    System.out.println( part2( fileName ) );
  }

  private enum Direction {
    NORTH, EAST, SOUTH, WEST
  }

  private static Direction getDirection( Direction currentDirection, Character turn ) {
    boolean turnRight = turn == 'R';

    switch (currentDirection) {
      case NORTH:
        return turnRight ? Direction.EAST : Direction.WEST;
      case EAST:
        return turnRight ? Direction.SOUTH : Direction.NORTH;
      case SOUTH:
        return turnRight ? Direction.WEST : Direction.EAST;
      case WEST:
        return turnRight ? Direction.NORTH : Direction.SOUTH;
      default: return Direction.NORTH;
    }
  }

  private static int part1( String fileName ) {
    int distance = 0;
    int x = 0;
    int y = 0;
    Direction currentDirection = Direction.NORTH;

    try {
      Scanner sc = new Scanner( new File( fileName ) );
      sc.useDelimiter(", ");

      while ( sc.hasNext() ) {
        String direction = sc.next().trim();

        if ( !direction.isEmpty() ) {
          int d = Integer.parseInt( direction.substring( 1 ) );
          char turn = direction.charAt( 0 );

          currentDirection = getDirection( currentDirection, turn );
          switch (currentDirection) {
            case NORTH:
              y += d;
              break;
            case EAST:
              x += d;
              break;
            case SOUTH:
              y -= d;
              break;
            case WEST:
              x -= d;
              break;
          }
        }
      }

      sc.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    distance = Math.abs( x ) + Math.abs( y );
    return distance;
  }

  private static int part2( String fileName ) {
    int distance = 0;
    int x = 0;
    int y = 0;
    Direction currentDirection = Direction.NORTH;

    HashMap<String, Integer> prevPositions = new HashMap<String, Integer>();
    try {
      Scanner sc = new Scanner( new File( fileName ) );
      sc.useDelimiter(", ");

      while ( sc.hasNext() ) {
        String direction = sc.next().trim();

        if ( !direction.isEmpty() ) {
          int d = Integer.parseInt( direction.substring( 1 ) );
          char turn = direction.charAt( 0 );

          currentDirection = getDirection( currentDirection, turn );
          while ( d > 0 ) {
            switch (currentDirection) {
              case NORTH:
                y += 1;
                break;
              case EAST:
                x += 1;
                break;
              case SOUTH:
                y -= 1;
                break;
              case WEST:
                x -= 1;
                break;
            }

            distance = Math.abs( x ) + Math.abs( y );
            String key = String.format( "%d:%d", x, y );

            if ( prevPositions.containsKey( key ) ) {
              return prevPositions.get( key );
            } else {
              prevPositions.put( key, distance );
            }

            d -= 1;
          }
        }
      }

      sc.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    return distance;
  }
}
