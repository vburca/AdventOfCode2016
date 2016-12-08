import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class InternetProtocolV7 {

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      int count1 = 0;
      int count2 = 0;
      while ( ( line = bufferedReader.readLine() ) != null ) {
        count1 += part1( line ) ? 1 : 0;
        count2 += part2( line ) ? 1 : 0;
      }

      System.out.println( count1 );
      System.out.println( count2 );

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static final String HAS_ABBA =
    "^.*(.)((?!\\1).)\\2\\1.*$";
  private static final String HAS_ABBA_HYPERNET =
    "^.*\\[[^\\]]*?(.)((?!\\1).)\\2\\1.*\\].*$";
  private static final Pattern hasAbbaPattern =
    Pattern.compile( HAS_ABBA );
  private static final Pattern hasAbbaHypernetPattern =
    Pattern.compile( HAS_ABBA_HYPERNET );

  // IP supports TLS?
  private static boolean part1( String ip ) {
    Matcher hasAbbaMatcher = hasAbbaPattern.matcher( ip );
    Matcher hasAbbaHypernetMatcher = hasAbbaHypernetPattern.matcher( ip );

    return !hasAbbaHypernetMatcher.matches() && hasAbbaMatcher.matches();
  }

  private static final String SUPPORTS_SSL =
    "(?:^|\\])[a-z]*([a-z])((?!\\1)[a-z])\\1.*?\\[[a-z]*?\\2\\1\\2[a-z]*\\]|\\[[a-z]*?([a-z])((?!\\3)[a-z])\\3.*\\][a-z]*\\4\\3\\4.*$";
  private static final Pattern supportsSslPattern =
    Pattern.compile( SUPPORTS_SSL );

  // IP supports SSL?
  private static boolean part2( String ip ) {
    Matcher supportsSslMatcher = supportsSslPattern.matcher( ip );

    return supportsSslMatcher.find();
  }
}
