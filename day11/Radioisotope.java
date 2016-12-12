import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Radioisotope {

  private static final int FLOORS = 4;

  private static final String OBJECT = "[a|an] ([a-z]{2})[a-z-]+ (m|g)[a-z]+";
  private static final Pattern objectPattern= Pattern.compile( OBJECT );

  private static final HashSet<String> elements = new HashSet<String>();
  private static final ArrayList<ArrayList<String>> floors =
    new ArrayList<ArrayList<String>>();

  private static final String DEFAULT = ". ";

  public static void main( String[] args ) {
    String fileName = args[0];

    try {
      FileReader reader = new FileReader( fileName );
      BufferedReader bufferedReader = new BufferedReader( reader );

      String line;

      while ( ( line = bufferedReader.readLine() ) != null ) {
        Matcher objectMatcher = objectPattern.matcher( line );

        ArrayList<String> floor = new ArrayList<String>();
        while ( objectMatcher.find() ) {
          String element = objectMatcher.group( 1 );
          String type = objectMatcher.group( 2 ).toUpperCase();

          elements.add( element );
          String object = String.format( "%s%s", element, type );

          floor.add( object );
        }

        floors.add( floor );
      }

      calculateStepsBFS( floors );

      reader.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

  private static final String GENERATOR = "^[a-z]{2}G$";
  private static final Pattern generatorPattern = Pattern.compile( GENERATOR );
  private static boolean isGenerator( String obj ) {
    Matcher generatorMatcher = generatorPattern.matcher( obj );
    return generatorMatcher.matches();
  }

  private static final String MICROCHIP = "^[a-z]{2}M$";
  private static final Pattern microchipPattern = Pattern.compile( MICROCHIP );
  private static boolean isMicrochip( String obj ) {
    Matcher microchipMatcher = microchipPattern.matcher( obj );
    return microchipMatcher.matches();
  }

  private static class State {
    ArrayList<ArrayList<String>> floors;
    int elevatorFloor;
    int steps;

    // This does not do a deep copy of the arrays, because we only use it to instantiate
    // the first state!
    State( ArrayList<ArrayList<String>> floors, int elevatorFloor, int steps ) {
      this.floors = floors;
      this.elevatorFloor = elevatorFloor;
      this.steps = steps;
    }

    private ArrayList<ArrayList<String>> copyFloors(
      ArrayList<ArrayList<String>> floors ) {

      ArrayList<ArrayList<String>> newFloors = new ArrayList<ArrayList<String>>();
      for ( ArrayList<String> floor : floors ) {
        newFloors.add( new ArrayList<String>( floor ) );
      }

      return newFloors;
    }

    public State getNewState( ArrayList<String> payload, int newFloor ) {
      // Can't go move more than 1 floor at a time by elevator
      if ( Math.abs( this.elevatorFloor - newFloor ) > 1 ) {
        System.out.format( "Can not move to Floor %d from Floor %d!\n",
          this.elevatorFloor, newFloor );
        return this;
      }

      // Deep copy the arrays; no need to deep copy the objects in the arrays, since
      // they are immutable strings anyway
      ArrayList<ArrayList<String>> newFloors = copyFloors( this.floors );
      // Remove the payload from the current floor, because we are taking the objects
      // with us on the elevator
      newFloors.get( this.elevatorFloor ).removeAll( payload );
      // Add the payload to the new floor
      newFloors.get( newFloor ).addAll( payload );

      return new State( newFloors, newFloor, this.steps + 1 );
    }

    // Check to see if all the floors of the state are valid.
    public boolean isValid() {
      for ( ArrayList<String> floor : this.floors ) {
        if ( !isValid( floor ) ) {
          return false;
        }
      }

      return true;
    }

    // A floor is INVALID if there are more than 0 generators and some unpaired microchips.
    private boolean isValid( ArrayList<String> floor ) {
      Set<String> microchipChems = new HashSet<String>();
      Set<String> generatorChems = new HashSet<String>();
      for ( String obj : floor ) {
        // If it's a microchip
        if ( isMicrochip( obj ) ) {
          // Only add the chemical element
          microchipChems.add( obj.substring( 0, 2 ) );
        } else {
          // else we know it's a generator
          generatorChems.add( obj.substring( 0, 2 ) );
        }
      }

      // Now remove all the microchips that are paired with a generator
      for ( String generatorChem : generatorChems ) {
        microchipChems.remove( generatorChem );
      }

      return !( microchipChems.size() > 0 && generatorChems.size() > 0 );
    }

    public boolean isTopFloor() {
      return elevatorFloor == FLOORS - 1;
    }

    public boolean isBottomFloor() {
      return elevatorFloor == 0;
    }

    // Lemma: Any 2 states that have the same number of generators and microchips per
    //      each floor, and the elevator on the same floor, are EQUIVALENT.
    // Proof: We can just replace the chemical names to create multiple equivalent states.
    // Axiom: Any 2 states are called EQUIVALENT if starting from any of them, it takes
    //      the same number of steps to move all the objects to the top floor.
    // We are going to use this observation in order to detect the seen states that are
    // equivalent, and ignore them from further processing.
    // Here we are generating unique "hashes" (identities) per equivalent state.
    // In order to do that, assuming that we are not parsing NON VALID states, we just
    // need to encode the number of generators and microchips per floor.
    // We also need to encode the floor on which the elevator is.
    public String getStateHash() {
      int[] generatorsCount = new int[FLOORS];
      int[] microchipsCount = new int[FLOORS];

      for ( int i = 0; i < this.floors.size(); i++ ) {
        ArrayList<String> floor = this.floors.get( i );
        for ( String obj : floor ) {
          if ( isGenerator( obj ) ) {
            generatorsCount[i] += 1;
          } else if ( isMicrochip( obj ) ) {
            microchipsCount[i] += 1;
          }
        }
      }

      // The resulting hash will be of the following form:
      // [elevatorFloor] 0:(g:g0;c:c0) 1:(g:g1;c:c1) 2:(g:g2;c:c2) ...
      // where g0 = generators on floor 0, c0 = microchips on floor 0, etc.
      // Yes, this does not need to be such an explicit encoding, but I just like
      // the clarity, unless it affects performance significantly.

      StringBuilder gensChipsCount = new StringBuilder();
      for ( int i = 0; i < FLOORS; i++ ) {
        gensChipsCount.append( String.format( " %d:(g:", i ) );
        gensChipsCount.append( String.valueOf( generatorsCount[i] ) );
        gensChipsCount.append( ";c:" );
        gensChipsCount.append( String.valueOf( microchipsCount[i] ) );
        gensChipsCount.append( ")" );
      }

      return String.format( "[%d]%s", this.elevatorFloor, gensChipsCount.toString() );
    }
  }

  // We are done when all floors BUT THE LAST ONE are empty
  private static boolean isDone( ArrayList<ArrayList<String>> floors ) {
    for ( int i = 0; i < FLOORS - 1; i++ ) {
      if ( floors.get( i ).size() > 0 ) {
        return false;
      }
    }

    return true;
  }

  private static void calculateStepsBFS( ArrayList<ArrayList<String>> floors ) {
    Set<String> visitedStates = new HashSet<String>();
    Queue<State> statesQueue = new LinkedList<State>();

    State startState = new State( floors, 0, 0 );
    statesQueue.add( startState );
    visitedStates.add( startState.getStateHash() );

    // Finished moving objects to top floor
    boolean done = false;

    while ( !statesQueue.isEmpty() && !done ) {
      State currentState = statesQueue.remove();

      // Check if we are done
      if ( isDone( currentState.floors ) ) {
        System.out.println( currentState.steps );
        done = true;
      } else {
        // else start to look for possible moves and queue new valid states
        ArrayList<String> currentFloor = currentState.floors.get( currentState.elevatorFloor );
        ArrayList<ArrayList<String>> payloads = getPossiblePayloads( currentFloor );

        // For all the possible payloads that can create adjacent states from our
        // current state (i.e. moving objects to floor above and below)
        for ( ArrayList<String> payload : payloads ) {
          // Try to go upwards with this payload
          if ( !currentState.isTopFloor() ) {
            State newUpState = currentState.getNewState( payload, currentState.elevatorFloor + 1 );
            String newUpStateHash = newUpState.getStateHash();
            // Only process further on if this new state is a valid one AND if
            // we haven't seen any other equivalent state so far
            if ( newUpState.isValid() &&
                !visitedStates.contains( newUpStateHash ) ) {
              visitedStates.add( newUpStateHash );
              statesQueue.add( newUpState );
            }
          }

          // Try to go downwards with this payload
          // TODO: Maybe do not go downwards if ALL the floors below are empty
          if ( !currentState.isBottomFloor() ) {
            State newDownState = currentState.getNewState( payload, currentState.elevatorFloor - 1 );
            String newDownStateHash = newDownState.getStateHash();
            // Only process further on if this new state is a valid one AND if
            // we haven't seen any other equivalent state so far
            if ( newDownState.isValid() &&
                !visitedStates.contains( newDownStateHash ) ) {
              visitedStates.add( newDownStateHash );
              statesQueue.add( newDownState );
            }
          }
        }
      }
    }
  }

  // Get the possible payloads that can be moved on the elevator, from the current
  // floor.
  private static ArrayList<ArrayList<String>> getPossiblePayloads(
    ArrayList<String> floor ) {

    ArrayList<ArrayList<String>> payloads = new ArrayList<ArrayList<String>>();

    // Generate all payloads of only 1 object
    for ( int i = 0; i < floor.size(); i++ ) {
      ArrayList<String> payload = new ArrayList<String>();
      payload.add( floor.get( i ) );

      payloads.add( payload );
    }

    // Now generate all combinations of 2; order does not matter
    // Avoid generating a combination of 2 with the same object
    for ( int i = 0; i < floor.size() - 1; i++ ) {
      for ( int j = i + 1; j < floor.size(); j++ ) {
        // Check if objects are compatible to be placed together in a payload
        if ( isCompatible( floor.get( i ), floor.get( j ) ) ) {
          ArrayList<String> payload = new ArrayList<String>();
          payload.add( floor.get( i ) );
          payload.add( floor.get( j ) );

          // Add payload to the list of all possible payloads
          payloads.add( payload );
        }
      }
    }

    return payloads;
  }

  // Check if object A is compatible with object B.
  // Two objects are compatible only if either:
  // * they match in the chemical part (polonium,
  // thulium, etc; this is true because that means that they are going to be a matching
  // pair of Generator-Microchip since these are the only 2 types that we play around
  // with, and there are no duplicates)
  // * they match in the type part (we only move generators or microchips)
  // The only incompatible payloads are when we try to move a generator of one chemical
  // with the microchip of a different chemical - this will cause the microchip to
  // fry out on the next floor, assuming that it was not frying out on the current floor
  // due to the presence of its chemical's generator.
  private static final String VALID_PAYLOAD =
    "([a-z]{2})[G|M] \\1[G|M]|[a-z]{2}(G|M) [a-z]{2}\\2";
  private static final Pattern validPayloadPattern = Pattern.compile( VALID_PAYLOAD );

  private static boolean isCompatible( String objA, String objB ) {
    // We are doing this to easily check for the compatible rules
    String payload = String.format( "%s %s", objA, objB );
    Matcher validPayloadMatcher = validPayloadPattern.matcher( payload );

    return validPayloadMatcher.matches();
  }

}
