import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SecurityObscurity {

  public static void main( String[] args ) {
    String fileName = args[0];

    part1( fileName );
    part2( fileName );
    // To actually get the solution for part2, grep for north pole stuff:
    // java SecurityObscurity day4.in | grep "pole"
  }

  private static final String ROOM_SECTOR = new String( "([a-z-]+)-(\\d+)\\[([a-z]{5})\\]" );
  private static final Pattern roomSectorPattern = Pattern.compile( ROOM_SECTOR );
  private static void part1( String fileName ) {
    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      int sum = 0;

      String line;

      roomAnalyzer:
      while ( ( line = bufferedReader.readLine() ) != null ) {
        Matcher matcher = roomSectorPattern.matcher( line );

        if ( !matcher.find() ) {
          System.out.format( "No matches found on line %s\n", line );
        } else {
          String roomName = matcher.group( 1 ).replaceAll( "-", "" );
          Integer sectorId = Integer.parseInt( matcher.group( 2 ) );
          String checksum = matcher.group( 3 );

          HashMap<Character, Integer> charCount = new HashMap<Character, Integer>();

          for ( char ch : roomName.toCharArray() ) {
            if ( charCount.containsKey( ch ) ) {
              int count = charCount.get( ch );
              charCount.put( ch, count + 1 );
            } else {
              charCount.put( ch, 1 );
            }
          }

          Comparator<Tuple<Character, Integer>> charCountComparator =
            new CharacterCountComparator();
          PriorityQueue<Tuple<Character, Integer>> queue =
            new PriorityQueue<Tuple<Character, Integer>>( 10, charCountComparator );

          for ( char ch : charCount.keySet() ) {
            queue.offer( new Tuple( ch, charCount.get( ch ) ) );
          }

          while ( queue.size() > 0 && checksum.length() > 1 ) {
            if ( queue.poll().x != checksum.charAt( 0 ) ) {
              continue roomAnalyzer;
            }

            checksum = checksum.substring( 1 );
          }

          sum += sectorId;
        }
      }

      System.out.println( sum );

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }


  private static void part2( String fileName ) {
    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      roomAnalyzer:
      while ( ( line = bufferedReader.readLine() ) != null ) {
        Matcher matcher = roomSectorPattern.matcher( line );

        if ( !matcher.find() ) {
          System.out.format( "No matches found on line %s\n", line );
        } else {
          String originalRoomName = matcher.group( 1 );
          String roomName = originalRoomName.replaceAll( "-", "" );
          Integer sectorId = Integer.parseInt( matcher.group( 2 ) );
          String checksum = matcher.group( 3 );

          HashMap<Character, Integer> charCount = new HashMap<Character, Integer>();

          for ( char ch : roomName.toCharArray() ) {
            if ( charCount.containsKey( ch ) ) {
              int count = charCount.get( ch );
              charCount.put( ch, count + 1 );
            } else {
              charCount.put( ch, 1 );
            }
          }

          Comparator<Tuple<Character, Integer>> charCountComparator =
            new CharacterCountComparator();
          PriorityQueue<Tuple<Character, Integer>> queue =
            new PriorityQueue<Tuple<Character, Integer>>( 10, charCountComparator );

          for ( char ch : charCount.keySet() ) {
            queue.offer( new Tuple( ch, charCount.get( ch ) ) );
          }

          while ( queue.size() > 0 && checksum.length() > 1 ) {
            if ( queue.poll().x != checksum.charAt( 0 ) ) {
              continue roomAnalyzer;
            }

            checksum = checksum.substring( 1 );
          }

          decryptRoomName( originalRoomName, sectorId );
        }
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static void decryptRoomName( String roomName, int sectorId ) {
    String result = "";
    for ( char ch : roomName.toCharArray() ) {
      result = String.format( "%s%c", result, decryptChar( ch, sectorId ) );
    }

    System.out.format( "%s   - %d\n", result, sectorId );
  }

  private static char decryptChar( char ch, int sectorId ) {
    switch ( ch ) {
      case '-': return ' ';
      default: return (char) ( ( ( ch - 'a' ) + sectorId ) % 26 + 'a' );
    }
  }
}
