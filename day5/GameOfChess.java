import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.lang.StringBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class GameOfChess {

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        int iteration = 0;
        StringBuilder result1 = new StringBuilder( 8 );
        StringBuilder result2 = new StringBuilder( "        " );

        while ( result1.length() < 8 || result2.indexOf( " " ) >= 0 ) {
          String seed = String.format( "%s%d", line, iteration );
          String md5Hash = getHash( seed );

          if ( result1.length() < 8 ) {
            result1 = getNextPwdChar( md5Hash, goodHashPattern1, result1 );
          }

          if ( result2.indexOf( " " ) >= 0 ) {
            result2 = getNextPwdChar( md5Hash, goodHashPattern2, result2 );
          }

          iteration += 1;
        }

        System.out.println( result1.toString() );
        System.out.println( result2.toString() );
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static String getHash( String seed ) {
    try {
      MessageDigest md = MessageDigest.getInstance( "MD5" );
      byte[] digest = md.digest( seed.getBytes( "UTF-8" ) );

      return byteArrToHexString( digest );
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

  private static final String GOOD_HASH_1 = new String( "^00000([a-z0-9]{1}).*" );
  private static final String GOOD_HASH_2 = new String( "^00000([0-7]{1})([a-z0-9]{1}).*" );
  private static final Pattern goodHashPattern1 = Pattern.compile( GOOD_HASH_1 );
  private static final Pattern goodHashPattern2 = Pattern.compile( GOOD_HASH_2 );
  private static StringBuilder getNextPwdChar(
    String md5Hash, Pattern p, StringBuilder result ) {

    Matcher m = p.matcher( md5Hash );
    if ( !m.find() ) {
      return result;
    }

    System.out.format( "Hash: %s\n", md5Hash );
    if ( p == goodHashPattern1 ) {
      result.append( m.group( 1 ).charAt( 0 ) );
    } else if ( p == goodHashPattern2 ) {
      int index = Integer.parseInt( m.group( 1 ) );
      if ( result.charAt( index ) == ' ' ) {
        result.setCharAt( index, m.group( 2 ).charAt( 0 ) );
      }
    }

    return result;
  }
}
