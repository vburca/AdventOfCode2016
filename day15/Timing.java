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


public class Timing {

  private static class Disc {
    public int index;
    public int positions;
    public int startTime;
    public int position;

    Disc( int index, int positions, int startTime, int position ) {
      this.index = index;
      this.positions = positions;
      this.startTime = startTime;
      this.position = position;
    }
  }

  private static final String DESCRIPTION =
    "^Disc #(\\d+) has (\\d+) .* time=(\\d+), .* (\\d+).$";
  private static final Pattern descriptionPattern = Pattern.compile( DESCRIPTION );

  public static void main( String[] args ) {
    String fileName = args[0];

    ArrayList<Disc> discs = new ArrayList<Disc>();

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        Matcher descriptionMatcher = descriptionPattern.matcher( line );

        if ( descriptionMatcher.matches() ) {
          discs.add( new Disc(
            Integer.parseInt( descriptionMatcher.group( 1 ) ),
            Integer.parseInt( descriptionMatcher.group( 2 ) ),
            Integer.parseInt( descriptionMatcher.group( 3 ) ),
            Integer.parseInt( descriptionMatcher.group( 4 ) ) ) );
        }
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    System.out.println( part1( discs ) );
    System.out.println( part2( discs ) );
  }

  private static int part1( ArrayList<Disc> discs ) {
    return calculateTiming( discs );
  }

  private static int part2( ArrayList<Disc> discs ) {
    int lastPos = discs.size();
    discs.add( new Disc( lastPos + 1, 11, 0, 0 ) );

    return calculateTiming( discs );
  }

  private static int calculateTiming( ArrayList<Disc> discs ) {
    int time = 0;
    boolean done = false;

    while ( !done ) {
      done = true;
      for ( Disc d : discs ) {
        if ( !isSlot( d, time ) ) {
          done = false;
          break;
        }
      }

      time += done ? 0 : 1;
    }

    return time;
  }

  private static boolean isSlot( Disc d, int time ) {
    return ( d.startTime + d.position + d.index + time ) % d.positions == 0;
  }
}
