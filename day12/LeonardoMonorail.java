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


public class LeonardoMonorail {

  private static int[] registersPart1;
  private static int[] registersPart2;

  private static ArrayList<String> instructions;
  private static int totalInstructions;

  private static final String INT = "(\\d+)";
  private static final String CPY = "^cpy (.*) ([a-z])$";
  private static final String INC = "^inc ([a-z])$";
  private static final String DEC = "^dec ([a-z])$";
  private static final String JNZ = "^jnz (.*) (.*)$";

  private static final Pattern intPattern = Pattern.compile( INT );
  private static final Pattern cpyPattern = Pattern.compile( CPY );
  private static final Pattern incPattern = Pattern.compile( INC );
  private static final Pattern decPattern = Pattern.compile( DEC );
  private static final Pattern jnzPattern = Pattern.compile( JNZ );

  public static void main( String[] args ) {
    String fileName = args[0];

    // Indices: a = 0, b = 1, c = 2, d = 3
    registersPart1 = new int[4];
    registersPart2 = new int[4];
    registersPart2[2] = 1;

    instructions = new ArrayList<String>();
    totalInstructions = 0;

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        instructions.add( line );
        totalInstructions += 1;
      }

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }


    System.out.println( parseInstructions( registersPart1 ) );
    System.out.println( parseInstructions( registersPart2 ) );
  }

  private static int parseInstructions( int[] registers ) {

    int cursor = 0;
    while ( cursor < totalInstructions ) {
      String instruction = instructions.get( cursor );

      Matcher cpyMatcher = cpyPattern.matcher( instruction );
      Matcher incMatcher = incPattern.matcher( instruction );
      Matcher decMatcher = decPattern.matcher( instruction );
      Matcher jnzMatcher = jnzPattern.matcher( instruction );

      int jump = 1;
      if ( cpyMatcher.matches() ) {
        String fromStr = cpyMatcher.group( 1 );
        String to = cpyMatcher.group( 2 );

        Matcher intMatcher = intPattern.matcher( fromStr );
        if ( intMatcher.find() ) {
          int fromInt = Integer.parseInt( fromStr );
          cpy ( registers, fromInt, to );
        } else {
          cpy( registers, fromStr, to );
        }
      } else if ( incMatcher.matches() ) {
        inc( registers, incMatcher.group( 1 ) );
      } else if ( decMatcher.matches() ) {
        dec( registers, decMatcher.group( 1 ) );
      } else if ( jnzMatcher.matches() ) {
        String checkValueStr = jnzMatcher.group( 1 );
        int jumpValue = Integer.parseInt( jnzMatcher.group( 2 ) );

        Matcher intMatcher = intPattern.matcher( checkValueStr );
        if ( intMatcher.find() ) {
          int checkValueInt = Integer.parseInt( checkValueStr );
          jump = jnz( checkValueInt, jumpValue );
        } else {
          jump = jnz( registers, checkValueStr, jumpValue );
        }
      } else {
        System.out.format( "Unknown instruction: %s\n", instruction );
        System.exit( 1 );
      }

      cursor += jump;
    }

    return registers[0];
  }

  private static void cpy( int[]registers, int value, String destRegStr ) {
    char destReg = destRegStr.charAt( 0 );

    registers[destReg - 'a'] = value;
  }

  private static void cpy( int[] registers, String fromRegStr, String destRegStr ) {
    char fromReg = fromRegStr.charAt( 0 );
    char destReg = destRegStr.charAt( 0 );

    registers[destReg - 'a'] = registers[fromReg - 'a'];
  }

  private static void inc( int[] registers, String regStr ) {
    add( registers, regStr, 1 );
  }

  private static void dec( int[] registers, String regStr ) {
    add( registers, regStr, -1 );
  }

  private static void add( int[] registers, String regStr, int value ) {
    char reg = regStr.charAt( 0 );

    registers[reg - 'a'] += value;
  }

  // Return a jump in cursor; if the register value is not 0, return actual jump
  // specified in the instruction, otherwise just jump 1 ahead
  private static int jnz( int[] registers, String regStr, int jumpValue ) {
    char reg = regStr.charAt( 0 );
    int regValue = registers[reg - 'a'];

    return jnz( regValue, jumpValue );
  }

  private static int jnz( int checkValue, int jumpValue ) {
    if ( checkValue > 0 ) {
      return jumpValue;
    }

    return 1;
  }

}
