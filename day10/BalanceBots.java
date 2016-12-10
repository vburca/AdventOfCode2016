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


public class BalanceBots {

  private static HashMap<Integer, ArrayList<Integer>> bots = new HashMap<>();
  private static HashMap<Integer, ArrayList<Integer>> outputs = new HashMap<>();
  private static HashMap<Integer, String> transferStorage = new HashMap<>();

  private static final int TARGET_CHIP_1 = 61;
  private static final int TARGET_CHIP_2 = 17;

  // Format: {low chip recipient} - {high chip recipient}
  private static final String TransferStorageFormat = "%s%d - %s%d";
  private static final String TRANSFER_STORAGE =
    "^([a-z]+)(\\d+) - ([a-z]+)(\\d+)$";
  private static final Pattern transferStoragePattern =
    Pattern.compile( TRANSFER_STORAGE );

  private static final String ASSIGNMENT =
    "^value (\\d+) .* (\\d+)$";
  private static final String TRANSFER =
    "^[a-z]+ (\\d+) .* ([a-z]+) (\\d+) .* ([a-z]+) (\\d+)$";
  private static final Pattern assignmentPattern =
    Pattern.compile( ASSIGNMENT );
  private static final Pattern transferPattern =
    Pattern.compile( TRANSFER );

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        Matcher assignmentMatcher = assignmentPattern.matcher( line );
        Matcher transferMatcher = transferPattern.matcher( line );

        // If we directly assign a chip id to a bot, do that
        if ( assignmentMatcher.matches() ) {
          int chipId = Integer.parseInt( assignmentMatcher.group( 1 ) );
          int botId = Integer.parseInt( assignmentMatcher.group( 2 ) );

          addChipId( bots, botId, chipId );
        } else if ( transferMatcher.matches() ) {
          // else store an encoding of a bot transfer
          int botId = Integer.parseInt( transferMatcher.group( 1 ) );
          String transfer = String.format(
            TransferStorageFormat,
            transferMatcher.group( 2 ),                       // low chip recipient type
            Integer.parseInt( transferMatcher.group( 3 ) ),   // low chip recipient id
            transferMatcher.group( 4 ),                       // high chip recipient type
            Integer.parseInt( transferMatcher.group( 5 ) ) ); // high chip recipient id

          transferStorage.put( botId, transfer );
        } else {
          System.out.format( "Unknown input instruction: \n%s\n", line );
          System.exit( 1 );
        }
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }

    // Now we go through all the bots ...
    while ( bots.size() > 0 ) {
      HashSet<Map.Entry<Integer, ArrayList<Integer>>> botSet =
        new HashSet( bots.entrySet() );

      for ( Map.Entry<Integer, ArrayList<Integer>> bot : botSet ) {
        int botId = bot.getKey();
        ArrayList<Integer> chipIds = bot.getValue();
        // ... and if a bot is holding 2 chips ...
        if ( chipIds.size() == 2 ) {
          // ... first check if we have the chips we want to look for ...
          if ( chipIds.contains( TARGET_CHIP_1 ) &&
              chipIds.contains( TARGET_CHIP_2 ) ) {
            System.out.println( botId ); // (part1) print the bot that holds these
          }

          // ... then execute the transfer instruction
          String transfer = transferStorage.get( botId );
          Matcher transferStorageMatcher =
            transferStoragePattern.matcher( transfer );

          // just for safety
          if ( !transferStorageMatcher.matches() ) {
            System.out.format( "Transfer Storage format not recognized: \n%s\n",
              transfer );
            System.exit( 1 );
          }

          String lowRecip = transferStorageMatcher.group( 1 );
          int lowRecipId = Integer.parseInt( transferStorageMatcher.group( 2 ) );
          String highRecip = transferStorageMatcher.group( 3 );
          int highRecipId = Integer.parseInt( transferStorageMatcher.group( 4 ) );

          // sort the chip ids so that we know which one is low and high
          Collections.sort( chipIds );

          switch ( lowRecip ) {
            case "bot": addChipId( bots, lowRecipId, chipIds.get( 0 ) ); break;
            case "output": addChipId( outputs, lowRecipId, chipIds.get( 0 ) ); break;
          }

          switch( highRecip ) {
            case "bot": addChipId( bots, highRecipId, chipIds.get( 1 ) ); break;
            case "output": addChipId( outputs, highRecipId, chipIds.get( 1 ) ); break;
          }

          // finally remove the bot from the map, since the bot is done with work
          bots.remove( botId );
        }
      }
    }

    // (part2) multiply together the values of one cheap in each of the outputs 0, 1, 2
    long result = 1;
    for ( int outputId = 0; outputId <= 2; outputId++ ) {
      result *= outputs.get( outputId ).get( 0 );
    }
    System.out.println( result );
  }

  private static void addChipId(
    HashMap<Integer, ArrayList<Integer>> map, int id, int chipId ) {

    if ( !map.containsKey( id ) ) {
      map.put( id, new ArrayList<>() );
    }

    map.get( id ).add( chipId );
  }
}
