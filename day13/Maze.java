import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Maze {

  private static class Position {
    int x;
    int y;
    int steps;

    Position( int x, int y ) {
      this( x, y, 0 );
    }

    Position( int x, int y, int steps ) {
      this.x = x;
      this.y = y;
      this.steps = steps;
    }

    public String toString() {
      return String.format( "%d:%d", this.x, this.y );
    }

    public boolean isOpen() {
      int result = this.x * this.x + 3 * this.x + 2 * this.x * this.y + this.y +
        this.y * this.y + seed;

      return Integer.bitCount( result ) % 2 == 0;
    }

    @Override
    public boolean equals( Object obj ) {
      if ( !( obj instanceof Position ) ) {
        return false;
      }

      if ( obj == this ) {
        return true;
      }

      Position other = (Position) obj;

      return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
      int hash = 3;
      hash = 53 * hash + this.x;
      hash = 53 * hash + this.y;
      return hash;
    }
  }

  private static int seed;

  private static final int TARGET_X = 31;
  private static final int TARGET_Y = 39;

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        seed = Integer.parseInt( line );
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    System.out.println( walkBFS() );
    System.out.println( countPositionsBFS() );
  }

  private static ArrayList<Position> getValidPositions( Position currentPos ) {
    ArrayList<Position> possiblePos = new ArrayList<Position>();

    if ( currentPos.x - 1 >= 0 ) {
      possiblePos.add( new Position(
        currentPos.x - 1, currentPos.y, currentPos.steps + 1 ) );
    }

    possiblePos.add( new Position( currentPos.x + 1, currentPos.y, currentPos.steps + 1 ) );

    if ( currentPos.y - 1 >= 0 ) {
      possiblePos.add( new Position(
        currentPos.x, currentPos.y - 1, currentPos.steps + 1 ) );
    }

    possiblePos.add( new Position( currentPos.x, currentPos.y + 1, currentPos.steps + 1 ) );

    ArrayList<Position> validPos = new ArrayList<Position>();
    for ( Position pos : possiblePos ) {
      if ( pos.isOpen() ) {
        validPos.add( pos );
      }
    }

    return validPos;
  }

  private static int countPositionsBFS() {
    Set<Position> visitedPositions = new HashSet<Position>();
    Queue<Position> positionsQueue = new LinkedList<Position>();

    Position startPos = new Position( 1, 1 );

    visitedPositions.add( startPos );
    positionsQueue.add( startPos );

    while ( !positionsQueue.isEmpty() ) {
      Position currentPos = positionsQueue.remove();

      if ( currentPos.steps == 50 ) {
        return visitedPositions.size();
      } else {
        ArrayList<Position> validPositions = getValidPositions( currentPos );

        for ( Position pos : validPositions ) {
          if ( !visitedPositions.contains( pos ) ) {
            visitedPositions.add( pos );
            positionsQueue.add( pos );
          }
        }
      }
    }

    return -1;
  }

  private static int walkBFS() {
    Set<Position> visitedPositions = new HashSet<Position>();
    Queue<Position> positionsQueue = new LinkedList<Position>();

    Position startPos = new Position( 1, 1 );
    Position finalPos = new Position( TARGET_X, TARGET_Y );

    visitedPositions.add( startPos );
    positionsQueue.add( startPos );

    while ( !positionsQueue.isEmpty() ) {
      Position currentPos = positionsQueue.remove();

      if ( currentPos.equals( finalPos ) ) {
        return currentPos.steps;
      } else {
        ArrayList<Position> validPositions = getValidPositions( currentPos );

        for ( Position pos : validPositions ) {
          if ( !visitedPositions.contains( pos ) ) {
            visitedPositions.add( pos );
            positionsQueue.add( pos );
          }
        }
      }
    }

    return -1;
  }
}
