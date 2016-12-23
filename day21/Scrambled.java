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


public class Scrambled {

  public static void main( String[] args ) {
    String fileName = args[0];

    ArrayList<String> instructions = new ArrayList<String>();

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        instructions.add( line );
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    System.out.println( part1( instructions, part1 ) );
    Collections.reverse( instructions );
    System.out.println( part2( instructions, part2 ) );
  }

  private static String part1( ArrayList<String> instructions, String salt ) {
    Matcher m;
    char[] chars = salt.toCharArray();
    System.out.println( chars );

    for ( String line : instructions ) {
      m = swapPosPattern.matcher( line );
      if ( m.matches() ) {
        chars = swapPositions( chars,
          Integer.parseInt( m.group( 1 ) ),
          Integer.parseInt( m.group( 2 ) ) );
        continue;
      }

      m = swapLetPattern.matcher( line );
      if ( m.matches() ) {
        chars = swapChars( chars,
          m.group( 1 ).charAt( 0 ),
          m.group( 2 ).charAt( 0 ) );
        continue;
      }

      m = revPosPattern.matcher( line );
      if ( m.matches() ) {
        chars = reverse( chars,
          Integer.parseInt( m.group( 1 ) ),
          Integer.parseInt( m.group( 2 ) ) );
        continue;
      }

      m = rotLeftPattern.matcher( line );
      if ( m.matches() ) {
        chars = rotate( chars,
          -1 * Integer.parseInt( m.group( 1 ) ) );
        continue;
      }

      m = rotRightPattern.matcher( line );
      if ( m.matches() ) {
        chars = rotate( chars,
          Integer.parseInt( m.group( 1 ) ) );
        continue;
      }

      m = rotPosPattern.matcher( line );
      if ( m.matches() ) {
        chars = rotatePosition( chars,
          m.group( 1 ).charAt( 0 ),
          false );
        continue;
      }

      m = movePosPattern.matcher( line );
      if ( m.matches() ) {
        chars = move( chars,
          Integer.parseInt( m.group( 1 ) ),
          Integer.parseInt( m.group( 2 ) ) );
        continue;
      }

      System.out.format( "Unknown instruction %s\n", line );
    }

    return new String( chars );
  }

  private static String part2( ArrayList<String> instructions, String salt ) {
    Matcher m;
    char[] chars = salt.toCharArray();
    System.out.println( chars );

    for ( String line : instructions ) {
      m = swapPosPattern.matcher( line );
      if ( m.matches() ) {
        chars = swapPositions( chars,
          Integer.parseInt( m.group( 1 ) ),
          Integer.parseInt( m.group( 2 ) ) );
        continue;
      }

      m = swapLetPattern.matcher( line );
      if ( m.matches() ) {
        chars = swapChars( chars,
          m.group( 1 ).charAt( 0 ),
          m.group( 2 ).charAt( 0 ) );
        continue;
      }

      m = revPosPattern.matcher( line );
      if ( m.matches() ) {
        chars = reverse( chars,
          Integer.parseInt( m.group( 1 ) ),
          Integer.parseInt( m.group( 2 ) ) );
        continue;
      }

      m = rotLeftPattern.matcher( line );
      if ( m.matches() ) {
        chars = rotate( chars,
          Integer.parseInt( m.group( 1 ) ) );
        continue;
      }

      m = rotRightPattern.matcher( line );
      if ( m.matches() ) {
        chars = rotate( chars,
          -1 * Integer.parseInt( m.group( 1 ) ) );
        continue;
      }

      m = rotPosPattern.matcher( line );
      if ( m.matches() ) {
        chars = rotatePosition( chars,
          m.group( 1 ).charAt( 0 ),
          true );
        continue;
      }

      m = movePosPattern.matcher( line );
      if ( m.matches() ) {
        chars = move( chars,
          Integer.parseInt( m.group( 2 ) ),
          Integer.parseInt( m.group( 1 ) ) );
        continue;
      }

      System.out.format( "Unknown instruction %s\n", line );
    }

    return new String( chars );
  }

  private static final String test = "abcde";
  private static final String part1 = "abcdefgh";
  private static final String part2 = "fbgdceah";

  private static final String SWAP_POS =
    "^swap position (\\d+) with position (\\d+)$";
  private static final Pattern swapPosPattern = Pattern.compile( SWAP_POS );

  private static final String SWAP_LET =
    "^swap letter ([a-z]) with letter ([a-z])$";
  private static final Pattern swapLetPattern = Pattern.compile( SWAP_LET );

  private static final String REV_POS =
    "^reverse positions (\\d+) through (\\d+)$";
  private static final Pattern revPosPattern = Pattern.compile( REV_POS );

  private static final String ROT_LEFT =
    "^rotate left (\\d+) step\\w*$";
  private static final Pattern rotLeftPattern = Pattern.compile( ROT_LEFT );

  private static final String ROT_RIGHT =
    "^rotate right (\\d+) step\\w*$";
  private static final Pattern rotRightPattern = Pattern.compile( ROT_RIGHT );

  private static final String ROT_POS =
    "^rotate based on position of letter ([a-z])$";
  private static final Pattern rotPosPattern = Pattern.compile( ROT_POS );

  private static final String MOVE_POS =
    "^move position (\\d+) to position (\\d+)$";
  private static final Pattern movePosPattern = Pattern.compile( MOVE_POS );

  private static char[] swapPositions( char[] chars, int x, int y ) {
    char aux = chars[x];
    chars[x] = chars[y];
    chars[y] = aux;

    return chars;
  }

  private static char[] swapChars( char[] chars, char x, char y ) {
    for ( int i = 0; i < chars.length; i++ ) {
      if ( chars[i] == x ) {
        chars[i] = y;
      } else if ( chars[i] == y ) {
        chars[i] = x;
      }
    }

    return chars;
  }

  private static char[] rotate( char[] chars, int pos ) {
    char[] newChars = new char[chars.length];
    int posSign = pos < 0 ? -1 : 1;
    pos = modulo( Math.abs( pos ), chars.length ) * posSign;

    if ( pos == 0 ) {
      return chars;
    }

    for ( int i = 0; i < chars.length; i++ ) {
      newChars[i] = chars[ modulo( i - pos, chars.length ) ];
    }

    return newChars;
  }

  private static char[] rotatePosition( char[] chars, char x, boolean inverse ) {
    int pos = -1;

    // Find position of letter x
    for ( int i = 0; i < chars.length; i++ ) {
      if ( chars[i] == x ) {
        pos = i;
        break;
      }
    }

    if ( pos == -1 ) {
      System.out.format( "rotatePosition: Letter %c was not found in %s",
        x, chars );
      System.exit( 1 );
    }

    int totalRotations = 0;
    if ( !inverse ) {
      int baseRotations = 1 + pos;
      totalRotations = pos >= 4 ? baseRotations + 1 : baseRotations;
    } else {
      int baseRotations = pos / 2;
      totalRotations = pos % 2 != 0 || pos == 0 ?
        baseRotations + 1 :
        baseRotations + 5;
      totalRotations *= -1;
    }

    return rotate( chars, totalRotations );
  }

  private static char[] reverse( char[] chars, int x, int y ) {
    char aux;

    for ( int i = 0; i < ( y - x + 1 ) / 2; i++ ) {
      aux = chars[x + i];
      chars[x + i] = chars[y - i];
      chars[y - i] = aux;
    }

    return chars;
  }

  private static char[] move( char[] chars, int x, int y ) {
    char[] newChars = new char[chars.length];
    newChars[y] = chars[x];

    int j = 0;
    for ( int i = 0; i < chars.length; i++ ) {
      if ( j == x ) {
        j += 1;
      }

      if ( i == y ) {
        continue;
      }

      newChars[i] = chars[j];
      j += 1;
    }

    return newChars;
  }

  private static int modulo( int a, int b ) {
    return ( ( a % b ) + b ) % b;
  }
}
