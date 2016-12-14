import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class OneTimePad {

  private static HashMap<Integer, String> hashes;
  private static String salt;
  private static boolean IS_PART_TWO = false;

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        salt = line;
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    System.out.println( findLastIndex() );
    IS_PART_TWO = true;
    System.out.println( findLastIndex() );
  }

  private static final String CHARS3 = "(.)\\1\\1";
  private static final String CHARS5 = "(.)\\1\\1\\1\\1";
  private static final Pattern chars3Pattern = Pattern.compile( CHARS3 );
  private static final Pattern chars5Pattern = Pattern.compile( CHARS5 );

  private static int findLastIndex() {
    int foundKeys = 0;
    int index = 0;

    hashes = new HashMap<Integer, String>();

    while ( foundKeys != 64 ) {
      String hash = "";
      if ( hashes.containsKey( index ) ) {
        hash = hashes.get( index );
      } else {
        hash = computeHash( index );
        hashes.put( index, hash );
      }

      Matcher chars3Matcher = chars3Pattern.matcher( hash );

      if ( chars3Matcher.find() ) {
        Character ch = new Character( chars3Matcher.group( 1 ).charAt( 0 ) );

        if ( ch != null ) {
          String futureHash = "";
          boolean found = false;

          for ( int i = index + 1; i < index + 1000 && !found; i++ ) {
            if ( hashes.containsKey( i ) ) {
              futureHash = hashes.get( i );
            } else {
              futureHash = computeHash( i );
              hashes.put( i, futureHash );
            }

            Matcher chars5Matcher = chars5Pattern.matcher( futureHash );

            if ( chars5Matcher.find() ) {
              Character futureCh = new Character( chars5Matcher.group( 1 ).charAt( 0 ) );

              if ( ch.equals( futureCh ) ) {
                foundKeys += 1;
                found = true;
              }
            }
          }
        }
      }

      index += 1;
    }

    return index - 1;
  }

  private static String computeHash( int index ) {
    try {
      String seed = String.format( "%s%d", salt, index );
      MessageDigest md = MessageDigest.getInstance( "MD5" );
      byte[] digest = md.digest( seed.getBytes( "UTF-8" ) );

      String hash = byteArrToHexString( digest );

      if ( IS_PART_TWO ) {
        for ( int i = 0; i < 2016; i++ ) {
          digest = md.digest( hash.getBytes( "UTF-8" ) );
          hash = byteArrToHexString( digest );
        }
      }

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
}
