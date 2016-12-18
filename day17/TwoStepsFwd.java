import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class TwoStepsFwd {

  private static final int MAX_X = 3;
  private static final int MAX_Y = 3;

  private static final int FINISH_X = 3;
  private static final int FINISH_Y = 3;

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        Position startPos = new Position( 0, 0, line );
        findVaultBFS( startPos, true );
      }
      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static class Position {
    int x;
    int y;
    ArrayList<Character> pathHistory;
    String salt;
    String doors;
    String passcode;

    Position( int x, int y, String salt ) {
      this( x, y, salt, new ArrayList<Character>() );
    }

    Position( int x, int y, String salt, ArrayList<Character> pathHistory ) {
      this.x = x;
      this.y = y;
      this.salt = salt;
      this.pathHistory = pathHistory;

      this.passcode = getPasscode();
      this.doors = getDoors();
    }

    public String getHash() {
      //return String.format( "(%d,%d)(%s)", this.x, this.y, this.doors );
      return this.passcode;
    }

    public ArrayList<Position> getNextValidPositions() {
      ArrayList<Position> nextPositions = new ArrayList<Position>();
      char[] doorOpen = this.doors.toCharArray();

      if ( y > 0 && doorOpen[0] == '1' ) { // door is open UP
        nextPositions.add( new Position(
          this.x, this.y - 1, this.salt,
          getNewPathHistory( new Character ( 'U' ) ) ) );
      }

      if ( y < MAX_Y && doorOpen[1] == '1' ) { // door is open DOWN
        nextPositions.add( new Position(
          this.x, this.y + 1, this.salt,
          getNewPathHistory( new Character ( 'D' ) ) ) );
      }

      if ( x > 0 && doorOpen[2] == '1' ) { // door is open LEFT
        nextPositions.add( new Position(
          this.x - 1, this.y, this.salt,
          getNewPathHistory( new Character ( 'L' ) ) ) );
      }

      if ( x < MAX_X && doorOpen[3] == '1' ) { // door is open RIGHT
        nextPositions.add( new Position(
          this.x + 1, this.y, this.salt,
          getNewPathHistory( new Character ( 'R' ) ) ) );
      }

      return nextPositions;
    }

    private ArrayList<Character> getNewPathHistory( Character direction ) {
      ArrayList<Character> newPathHistory = new ArrayList<Character>( this.pathHistory );
      newPathHistory.add( direction );

      return newPathHistory;
    }

    private static final String OPENDOOR = "bcdef";
    private String getDoors() {
      if ( this.passcode == null || this.passcode == "" ) {
        System.out.println( "No passcode found!" );
        System.exit( 1 );
      }

      char[] doorChars = this.passcode.toCharArray();

      StringBuilder sb = new StringBuilder();

      // Check UP door
      if ( this.y == 0 ) { // no UP door
        sb.append( '0' );
      } else if ( OPENDOOR.indexOf( doorChars[0] ) == -1 ) { // UP door not open
        sb.append( '0' );
      } else {
        sb.append( '1' ); // UP door open
      }

      // Check DOWN door
      if ( this.y == MAX_Y ) { // no DOWN door
        sb.append( '0' );
      } else if ( OPENDOOR.indexOf( doorChars[1] ) == -1 ) { // DOWN door not open
        sb.append( '0' );
      } else {
        sb.append( '1' );
      }

      if ( this.x == 0 ) { // no LEFT door
        sb.append( '0' );
      } else if ( OPENDOOR.indexOf( doorChars[2] ) == -1 ) { // LEFT door not open
        sb.append( '0' );
      } else {
        sb.append( '1' ); // LEFT door open
      }

      if ( this.x == MAX_X ) { // no RIGHT door
        sb.append( '0' );
      } else if ( OPENDOOR.indexOf( doorChars[3] ) == -1 ) { // RIGHT door not open
        sb.append( '0' );
      } else {
        sb.append( '1' );
      }

      return sb.toString();
    }

    public boolean hasOpenDoors() {
      return this.doors != null && this.doors.indexOf( '1' ) != -1;
    }

    private String getPasscode() {
      StringBuilder sb = new StringBuilder( salt );
      for ( Character ch : this.pathHistory ) {
        sb.append( ch );
      }

      return getMD5( sb.toString() ).substring( 0, 4 );
    }

    // We are done when all floors BUT THE LAST ONE are empty
    public boolean isDone() {
      return this.x == FINISH_X && this.y == FINISH_Y;
    }

  }

  private static void findVaultBFS( Position startPos, boolean findLongest ) {
    Set<String> visitedPositions = new HashSet<String>();
    Queue<Position> posQueue = new LinkedList<Position>();

    posQueue.add( startPos );
    visitedPositions.add( startPos.getHash() );

    // Temporary assignment
    Position finalPos = startPos;
    boolean done = false;
    int maxLengthPath = 0;
    while ( !posQueue.isEmpty() ) {
      Position currentPos = posQueue.remove();

      if ( currentPos.isDone() ) {
        if ( done == false ) { // this is the first (aka shortest) path found
          finalPos = currentPos; // part 1
          done = true;
        }

        // Also find the max length of all these paths
        if ( currentPos.pathHistory.size() > maxLengthPath ) {
          maxLengthPath = currentPos.pathHistory.size(); // part 2
        }
      } else {
        ArrayList<Position> nextPos = currentPos.getNextValidPositions();

        for ( Position p : nextPos ) {
          String posHash = p.getHash();

          if ( findLongest ) {
            posQueue.add( p );
          } else {
            if ( !visitedPositions.contains( posHash ) &&
                ( p.isDone() || p.hasOpenDoors() ) ) {
              visitedPositions.add( posHash );
              posQueue.add( p );
            }
          }
        }
      }
    }

    if ( done ) {
      System.out.format( "Shortest Path: %s\nLength of longest path: %d\n",
        charsToString( finalPos.pathHistory ),
        maxLengthPath );
    } else {
      System.out.println( "Unable to find vault!" );
    }
  }

  private static String charsToString( ArrayList<Character> chars ) {
    StringBuilder sb = new StringBuilder();

    for ( Character ch : chars ) {
      sb.append( ch );
    }

    return sb.toString();
  }

  private static String getMD5( String s ) {
    try {
      MessageDigest md = MessageDigest.getInstance( "MD5" );
      byte[] digest = md.digest( s.getBytes( "UTF-8" ) );

      String hash = byteArrToHexString( digest );

      return hash;
    } catch ( UnsupportedEncodingException uee ) {
      uee.printStackTrace();
    } catch ( NoSuchAlgorithmException nsae ) {
      nsae.printStackTrace();
    }

    return "";
  }

  private static String byteArrToHexString( byte[] bytes ) {
    StringBuilder hexString = new StringBuilder();

    for ( int i = 0; i < bytes.length; i++ ) {
      String hex = Integer.toHexString( 0xFF & bytes[i] );

      if ( hex.length() == 1 ) {
        hexString.append( '0' );
      }

      hexString.append( hex );
    }

    return hexString.toString();
  }

  private static void calculateStepsBFS( Position startPos ) {
  }


}
