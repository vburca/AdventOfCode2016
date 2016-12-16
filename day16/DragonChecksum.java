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


public class DragonChecksum {

  public static void main( String[] args ) {
    String fileName = args[0];

    String input = "";
    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        input = line;
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    // for test use discLength = 20
    System.out.println( calculateChecksum( input, 272 ) );
    System.out.println( calculateChecksum( input, 35651584 ) );
  }

  private static String calculateChecksum( String input, int discLength ) {
    while ( input.length() < discLength ) {
      input = generateDragonCurve( input );
    }

    String checksum = getChecksum( input.substring( 0, discLength ) );
    while ( checksum.length() % 2 == 0 ) {
      checksum = getChecksum( checksum );
    }

    return checksum;
  }

  private static String generateDragonCurve( String a ) {
    char[] auxB = a.toCharArray();

    StringBuilder sb = new StringBuilder( a );
    sb.append( '0' );
    for ( int i = auxB.length - 1; i >= 0; i-- ) {
      switch ( auxB[i] ) {
        case '0': sb.append( '1' ); break;
        case '1': sb.append( '0' ); break;
        default: sb.append( auxB[i] ); break;
      }
    }

    return sb.toString();
  }

  private static String getChecksum( String s ) {
    if ( s.length() % 2 != 0 ) {
      System.out.format( "Trying to calculate checksum to odd-length string: %s\n", s );
      System.exit( 1 );
    }

    char[] sChars = s.toCharArray();
    StringBuilder checksum = new StringBuilder();
    for ( int i = 0; i < s.length() - 1; i += 2 ) {
      if ( sChars[i] == sChars[i + 1] ) {
        checksum.append( '1' );
      } else {
        checksum.append( '0' );
      }
    }

    return checksum.toString();
  }
}
