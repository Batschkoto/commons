package de.batschkoto.commons.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author batschkoto
 */
public class CommonsUtil {

    public static boolean isNumeric( String str ) {
        if ( str == null ) {
            return false;
        }
        int sz = str.length();
        for ( int i = 0; i < sz; i++ ) {
            if ( !Character.isDigit( str.charAt( i ) ) ) {
                return false;
            }
        }
        return true;
    }

    public static boolean consistsOnlyOfChars( String s, char[] chars ) {
        List<Character> characterList = new ArrayList<>();
        for ( char c : chars ) {
            characterList.add( c );
        }

        for ( char c : s.toLowerCase().toCharArray() ) {
            if ( !characterList.contains( c ) ) {
                return false;
            }
        }

        return true;
    }

}
