// $ANTLR 3.4 R.g 2012-08-10 11:09:53

package r.parser;
//Checkstyle: stop


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all"})
public class RLexer extends Lexer {
    public static final int EOF=-1;
    public static final int T__80=80;
    public static final int AND=4;
    public static final int ARROW=5;
    public static final int ASSIGN=6;
    public static final int AT=7;
    public static final int BITWISEAND=8;
    public static final int BITWISEOR=9;
    public static final int BRAKET=10;
    public static final int BREAK=11;
    public static final int CALL=12;
    public static final int CARRET=13;
    public static final int COLUMN=14;
    public static final int COMMA=15;
    public static final int COMMENT=16;
    public static final int DD=17;
    public static final int DIV=18;
    public static final int ELSE=19;
    public static final int EQ=20;
    public static final int ESC_SEQ=21;
    public static final int EXPONENT=22;
    public static final int FALSE=23;
    public static final int FIELD=24;
    public static final int FOR=25;
    public static final int FUNCTION=26;
    public static final int GE=27;
    public static final int GT=28;
    public static final int HEX_DIGIT=29;
    public static final int HEX_ESC=30;
    public static final int ID=31;
    public static final int ID_NAME=32;
    public static final int IF=33;
    public static final int IN=34;
    public static final int KW=35;
    public static final int LBB=36;
    public static final int LBRACE=37;
    public static final int LBRAKET=38;
    public static final int LE=39;
    public static final int LINE_BREAK=40;
    public static final int LPAR=41;
    public static final int LT=42;
    public static final int MINUS=43;
    public static final int MISSING_VAL=44;
    public static final int MOD=45;
    public static final int MULT=46;
    public static final int NE=47;
    public static final int NEWLINE=48;
    public static final int NEXT=49;
    public static final int NOT=50;
    public static final int NS_GET=51;
    public static final int NS_GET_INT=52;
    public static final int NULL=53;
    public static final int NUMBER=54;
    public static final int OCTAL_ESC=55;
    public static final int OP=56;
    public static final int OP_NAME=57;
    public static final int OR=58;
    public static final int PARMS=59;
    public static final int PLUS=60;
    public static final int RBRACE=61;
    public static final int RBRAKET=62;
    public static final int REPEAT=63;
    public static final int RIGHT_ARROW=64;
    public static final int RPAR=65;
    public static final int SEMICOLUMN=66;
    public static final int SEQUENCE=67;
    public static final int STRING=68;
    public static final int SUPER_ARROW=69;
    public static final int SUPER_RIGHT_ARROW=70;
    public static final int TILDE=71;
    public static final int TRUE=72;
    public static final int UMINUS=73;
    public static final int UNICODE_ESC=74;
    public static final int UPLUS=75;
    public static final int UTILDE=76;
    public static final int VARIATIC=77;
    public static final int WHILE=78;
    public static final int WS=79;

        public final int MAX_INCOMPLETE_SIZE = 100;
        int incomplete_stack[] = new int[MAX_INCOMPLETE_SIZE]; // TODO probably go for an ArrayList of int :S
        int incomplete_depth;
        
        @Override
        public void reportError(RecognitionException e) {
            throw new RuntimeException(e);
        }


    // delegates
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public RLexer() {} 
    public RLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public RLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    @Override
    public String getGrammarFileName() { return "R.g"; }

    // $ANTLR start "T__80"
    public final void mT__80() throws RecognitionException {
        try {
            int _type = T__80;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:31:7: ( '--EOF--' )
            // R.g:31:9: '--EOF--'
            {
            match("--EOF--"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__80"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:287:5: ( '#' (~ ( '\\n' | '\\r' | '\\f' ) )* ( LINE_BREAK | EOF ) )
            // R.g:287:9: '#' (~ ( '\\n' | '\\r' | '\\f' ) )* ( LINE_BREAK | EOF )
            {
            match('#'); 

            // R.g:287:13: (~ ( '\\n' | '\\r' | '\\f' ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0 >= '\u0000' && LA1_0 <= '\t')||LA1_0=='\u000B'||(LA1_0 >= '\u000E' && LA1_0 <= '\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // R.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||input.LA(1)=='\u000B'||(input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            // R.g:287:32: ( LINE_BREAK | EOF )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\n'||(LA2_0 >= '\f' && LA2_0 <= '\r')) ) {
                alt2=1;
            }
            else {
                alt2=2;
            }
            switch (alt2) {
                case 1 :
                    // R.g:287:33: LINE_BREAK
                    {
                    mLINE_BREAK(); 


                    }
                    break;
                case 2 :
                    // R.g:287:46: EOF
                    {
                    match(EOF); 


                    }
                    break;

            }


             if(incomplete_stack[incomplete_depth]>0) _channel=HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "ARROW"
    public final void mARROW() throws RecognitionException {
        try {
            int _type = ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:290:2: ( '<-' | ':=' )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0=='<') ) {
                alt3=1;
            }
            else if ( (LA3_0==':') ) {
                alt3=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // R.g:290:4: '<-'
                    {
                    match("<-"); 



                    }
                    break;
                case 2 :
                    // R.g:290:11: ':='
                    {
                    match(":="); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ARROW"

    // $ANTLR start "SUPER_ARROW"
    public final void mSUPER_ARROW() throws RecognitionException {
        try {
            int _type = SUPER_ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:293:2: ( '<<-' )
            // R.g:293:5: '<<-'
            {
            match("<<-"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SUPER_ARROW"

    // $ANTLR start "RIGHT_ARROW"
    public final void mRIGHT_ARROW() throws RecognitionException {
        try {
            int _type = RIGHT_ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:295:2: ( '->' )
            // R.g:295:4: '->'
            {
            match("->"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RIGHT_ARROW"

    // $ANTLR start "SUPER_RIGHT_ARROW"
    public final void mSUPER_RIGHT_ARROW() throws RecognitionException {
        try {
            int _type = SUPER_RIGHT_ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:298:2: ( '->>' )
            // R.g:298:4: '->>'
            {
            match("->>"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SUPER_RIGHT_ARROW"

    // $ANTLR start "VARIATIC"
    public final void mVARIATIC() throws RecognitionException {
        try {
            int _type = VARIATIC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:301:2: ( '..' ( '.' )+ )
            // R.g:301:4: '..' ( '.' )+
            {
            match(".."); 



            // R.g:301:9: ( '.' )+
            int cnt4=0;
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0=='.') ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // R.g:301:9: '.'
            	    {
            	    match('.'); 

            	    }
            	    break;

            	default :
            	    if ( cnt4 >= 1 ) break loop4;
                        EarlyExitException eee =
                            new EarlyExitException(4, input);
                        throw eee;
                }
                cnt4++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "VARIATIC"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:303:4: ( '==' )
            // R.g:303:6: '=='
            {
            match("=="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "NE"
    public final void mNE() throws RecognitionException {
        try {
            int _type = NE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:304:5: ( '!=' )
            // R.g:304:7: '!='
            {
            match("!="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NE"

    // $ANTLR start "GE"
    public final void mGE() throws RecognitionException {
        try {
            int _type = GE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:305:4: ( '>=' )
            // R.g:305:6: '>='
            {
            match(">="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GE"

    // $ANTLR start "LE"
    public final void mLE() throws RecognitionException {
        try {
            int _type = LE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:306:4: ( '<=' )
            // R.g:306:6: '<='
            {
            match("<="); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LE"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:307:4: ( '>' )
            // R.g:307:6: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:308:5: ( '<' )
            // R.g:308:7: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:310:2: ( '=' )
            // R.g:310:4: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ASSIGN"

    // $ANTLR start "NS_GET_INT"
    public final void mNS_GET_INT() throws RecognitionException {
        try {
            int _type = NS_GET_INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:314:2: ( ':::' )
            // R.g:314:4: ':::'
            {
            match(":::"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NS_GET_INT"

    // $ANTLR start "NS_GET"
    public final void mNS_GET() throws RecognitionException {
        try {
            int _type = NS_GET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:316:2: ( '::' )
            // R.g:316:4: '::'
            {
            match("::"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NS_GET"

    // $ANTLR start "COLUMN"
    public final void mCOLUMN() throws RecognitionException {
        try {
            int _type = COLUMN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:319:2: ( ':' )
            // R.g:319:4: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COLUMN"

    // $ANTLR start "SEMICOLUMN"
    public final void mSEMICOLUMN() throws RecognitionException {
        try {
            int _type = SEMICOLUMN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:321:2: ( ';' )
            // R.g:321:4: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SEMICOLUMN"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:323:2: ( ',' )
            // R.g:323:4: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:325:2: ( '&&' )
            // R.g:325:4: '&&'
            {
            match("&&"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "BITWISEAND"
    public final void mBITWISEAND() throws RecognitionException {
        try {
            int _type = BITWISEAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:327:2: ( '&' )
            // R.g:327:4: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BITWISEAND"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:328:4: ( '||' )
            // R.g:328:6: '||'
            {
            match("||"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "BITWISEOR"
    public final void mBITWISEOR() throws RecognitionException {
        try {
            int _type = BITWISEOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:330:2: ( '|' )
            // R.g:330:3: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BITWISEOR"

    // $ANTLR start "LBRACE"
    public final void mLBRACE() throws RecognitionException {
        try {
            int _type = LBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:332:2: ( '{' )
            // R.g:332:4: '{'
            {
            match('{'); 

            incomplete_stack[++incomplete_depth] = 0; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LBRACE"

    // $ANTLR start "RBRACE"
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:334:2: ( '}' )
            // R.g:334:4: '}'
            {
            match('}'); 

            incomplete_depth -- ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RBRACE"

    // $ANTLR start "LPAR"
    public final void mLPAR() throws RecognitionException {
        try {
            int _type = LPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:336:2: ( '(' )
            // R.g:336:4: '('
            {
            match('('); 

             incomplete_stack[incomplete_depth] ++; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LPAR"

    // $ANTLR start "RPAR"
    public final void mRPAR() throws RecognitionException {
        try {
            int _type = RPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:338:2: ( ')' )
            // R.g:338:4: ')'
            {
            match(')'); 

             incomplete_stack[incomplete_depth]--; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RPAR"

    // $ANTLR start "LBB"
    public final void mLBB() throws RecognitionException {
        try {
            int _type = LBB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:340:2: ( '[[' )
            // R.g:340:4: '[['
            {
            match("[["); 



             incomplete_stack[incomplete_depth] += 2; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LBB"

    // $ANTLR start "LBRAKET"
    public final void mLBRAKET() throws RecognitionException {
        try {
            int _type = LBRAKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:342:2: ( '[' )
            // R.g:342:4: '['
            {
            match('['); 

             incomplete_stack[incomplete_depth] ++; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LBRAKET"

    // $ANTLR start "RBRAKET"
    public final void mRBRAKET() throws RecognitionException {
        try {
            int _type = RBRAKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:344:2: ( ']' )
            // R.g:344:4: ']'
            {
            match(']'); 

             incomplete_stack[incomplete_depth] --;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "RBRAKET"

    // $ANTLR start "CARRET"
    public final void mCARRET() throws RecognitionException {
        try {
            int _type = CARRET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:346:2: ( '^' | '**' )
            int alt5=2;
            int LA5_0 = input.LA(1);

            if ( (LA5_0=='^') ) {
                alt5=1;
            }
            else if ( (LA5_0=='*') ) {
                alt5=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 5, 0, input);

                throw nvae;

            }
            switch (alt5) {
                case 1 :
                    // R.g:346:4: '^'
                    {
                    match('^'); 

                    }
                    break;
                case 2 :
                    // R.g:346:10: '**'
                    {
                    match("**"); 



                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "CARRET"

    // $ANTLR start "TILDE"
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:348:2: ( '~' )
            // R.g:348:4: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TILDE"

    // $ANTLR start "MOD"
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:350:2: ( '%%' )
            // R.g:350:4: '%%'
            {
            match("%%"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MOD"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:353:2: ( '!' )
            // R.g:353:4: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:355:2: ( '+' )
            // R.g:355:4: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MULT"
    public final void mMULT() throws RecognitionException {
        try {
            int _type = MULT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:357:2: ( '*' )
            // R.g:357:4: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MULT"

    // $ANTLR start "DIV"
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:358:5: ( '/' )
            // R.g:358:7: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DIV"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:360:2: ( '-' )
            // R.g:360:4: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "FIELD"
    public final void mFIELD() throws RecognitionException {
        try {
            int _type = FIELD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:363:2: ( '$' )
            // R.g:363:4: '$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FIELD"

    // $ANTLR start "AT"
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:364:4: ( '@' )
            // R.g:364:6: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "AT"

    // $ANTLR start "FUNCTION"
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:367:2: ( 'function' )
            // R.g:367:4: 'function'
            {
            match("function"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FUNCTION"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:369:2: ( 'NULL' )
            // R.g:369:4: 'NULL'
            {
            match("NULL"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:372:2: ( 'TRUE' )
            // R.g:372:4: 'TRUE'
            {
            match("TRUE"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:374:2: ( 'FALSE' )
            // R.g:374:4: 'FALSE'
            {
            match("FALSE"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "WHILE"
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:377:2: ( 'while' )
            // R.g:377:4: 'while'
            {
            match("while"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WHILE"

    // $ANTLR start "FOR"
    public final void mFOR() throws RecognitionException {
        try {
            int _type = FOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:378:5: ( 'for' )
            // R.g:378:7: 'for'
            {
            match("for"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FOR"

    // $ANTLR start "REPEAT"
    public final void mREPEAT() throws RecognitionException {
        try {
            int _type = REPEAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:380:2: ( 'repeat' )
            // R.g:380:4: 'repeat'
            {
            match("repeat"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "REPEAT"

    // $ANTLR start "IN"
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:381:4: ( 'in' )
            // R.g:381:6: 'in'
            {
            match("in"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "IF"
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:382:4: ( 'if' )
            // R.g:382:6: 'if'
            {
            match("if"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "IF"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:384:2: ( 'else' )
            // R.g:384:4: 'else'
            {
            match("else"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ELSE"

    // $ANTLR start "NEXT"
    public final void mNEXT() throws RecognitionException {
        try {
            int _type = NEXT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:386:2: ( 'next' )
            // R.g:386:4: 'next'
            {
            match("next"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NEXT"

    // $ANTLR start "BREAK"
    public final void mBREAK() throws RecognitionException {
        try {
            int _type = BREAK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:388:2: ( 'break' )
            // R.g:388:4: 'break'
            {
            match("break"); 



            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "BREAK"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:391:5: ( ( ' ' | '\\t' ) )
            // R.g:391:9: ( ' ' | '\\t' )
            {
            if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:396:2: ( LINE_BREAK )
            // R.g:396:4: LINE_BREAK
            {
            mLINE_BREAK(); 


             if(incomplete_stack[incomplete_depth]>0) _channel=HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NEWLINE"

    // $ANTLR start "NUMBER"
    public final void mNUMBER() throws RecognitionException {
        try {
            int _type = NUMBER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:398:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? ( 'i' | 'L' )? | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? ( 'i' | 'L' )? | '0x' ( HEX_DIGIT )+ ( 'L' )? )
            int alt16=3;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // R.g:398:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? ( 'i' | 'L' )?
                    {
                    // R.g:398:9: ( '0' .. '9' )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0 >= '0' && LA6_0 <= '9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // R.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt6 >= 1 ) break loop6;
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);


                    match('.'); 

                    // R.g:398:25: ( '0' .. '9' )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0 >= '0' && LA7_0 <= '9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // R.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);


                    // R.g:398:37: ( EXPONENT )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0=='E'||LA8_0=='e') ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // R.g:398:37: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    // R.g:398:47: ( 'i' | 'L' )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0=='L'||LA9_0=='i') ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // R.g:
                            {
                            if ( input.LA(1)=='L'||input.LA(1)=='i' ) {
                                input.consume();
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // R.g:399:9: ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? ( 'i' | 'L' )?
                    {
                    // R.g:399:9: ( '.' )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='.') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // R.g:399:9: '.'
                            {
                            match('.'); 

                            }
                            break;

                    }


                    // R.g:399:14: ( '0' .. '9' )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0 >= '0' && LA11_0 <= '9')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // R.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt11 >= 1 ) break loop11;
                                EarlyExitException eee =
                                    new EarlyExitException(11, input);
                                throw eee;
                        }
                        cnt11++;
                    } while (true);


                    // R.g:399:26: ( EXPONENT )?
                    int alt12=2;
                    int LA12_0 = input.LA(1);

                    if ( (LA12_0=='E'||LA12_0=='e') ) {
                        alt12=1;
                    }
                    switch (alt12) {
                        case 1 :
                            // R.g:399:26: EXPONENT
                            {
                            mEXPONENT(); 


                            }
                            break;

                    }


                    // R.g:399:36: ( 'i' | 'L' )?
                    int alt13=2;
                    int LA13_0 = input.LA(1);

                    if ( (LA13_0=='L'||LA13_0=='i') ) {
                        alt13=1;
                    }
                    switch (alt13) {
                        case 1 :
                            // R.g:
                            {
                            if ( input.LA(1)=='L'||input.LA(1)=='i' ) {
                                input.consume();
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;
                            }


                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // R.g:400:7: '0x' ( HEX_DIGIT )+ ( 'L' )?
                    {
                    match("0x"); 



                    // R.g:400:12: ( HEX_DIGIT )+
                    int cnt14=0;
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0 >= '0' && LA14_0 <= '9')||(LA14_0 >= 'A' && LA14_0 <= 'F')||(LA14_0 >= 'a' && LA14_0 <= 'f')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // R.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt14 >= 1 ) break loop14;
                                EarlyExitException eee =
                                    new EarlyExitException(14, input);
                                throw eee;
                        }
                        cnt14++;
                    } while (true);


                    // R.g:400:23: ( 'L' )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0=='L') ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // R.g:400:23: 'L'
                            {
                            match('L'); 

                            }
                            break;

                    }


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NUMBER"

    // $ANTLR start "DD"
    public final void mDD() throws RecognitionException {
        try {
            int _type = DD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:402:4: ( '..' ( '0' .. '9' )+ )
            // R.g:402:6: '..' ( '0' .. '9' )+
            {
            match(".."); 



            // R.g:402:11: ( '0' .. '9' )+
            int cnt17=0;
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( ((LA17_0 >= '0' && LA17_0 <= '9')) ) {
                    alt17=1;
                }


                switch (alt17) {
            	case 1 :
            	    // R.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt17 >= 1 ) break loop17;
                        EarlyExitException eee =
                            new EarlyExitException(17, input);
                        throw eee;
                }
                cnt17++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "DD"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:404:5: ( ( '.' )* ID_NAME | '.' | '`' ( ESC_SEQ |~ ( '\\\\' | '`' ) )* '`' )
            int alt20=3;
            switch ( input.LA(1) ) {
            case '.':
                {
                int LA20_1 = input.LA(2);

                if ( (LA20_1=='.'||(LA20_1 >= 'A' && LA20_1 <= 'Z')||LA20_1=='_'||(LA20_1 >= 'a' && LA20_1 <= 'z')) ) {
                    alt20=1;
                }
                else {
                    alt20=2;
                }
                }
                break;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '_':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
            case 'g':
            case 'h':
            case 'i':
            case 'j':
            case 'k':
            case 'l':
            case 'm':
            case 'n':
            case 'o':
            case 'p':
            case 'q':
            case 'r':
            case 's':
            case 't':
            case 'u':
            case 'v':
            case 'w':
            case 'x':
            case 'y':
            case 'z':
                {
                alt20=1;
                }
                break;
            case '`':
                {
                alt20=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;

            }

            switch (alt20) {
                case 1 :
                    // R.g:404:7: ( '.' )* ID_NAME
                    {
                    // R.g:404:7: ( '.' )*
                    loop18:
                    do {
                        int alt18=2;
                        int LA18_0 = input.LA(1);

                        if ( (LA18_0=='.') ) {
                            alt18=1;
                        }


                        switch (alt18) {
                    	case 1 :
                    	    // R.g:404:7: '.'
                    	    {
                    	    match('.'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop18;
                        }
                    } while (true);


                    mID_NAME(); 


                    }
                    break;
                case 2 :
                    // R.g:405:4: '.'
                    {
                    match('.'); 

                    }
                    break;
                case 3 :
                    // R.g:406:4: '`' ( ESC_SEQ |~ ( '\\\\' | '`' ) )* '`'
                    {
                    match('`'); 

                    // R.g:406:8: ( ESC_SEQ |~ ( '\\\\' | '`' ) )*
                    loop19:
                    do {
                        int alt19=3;
                        int LA19_0 = input.LA(1);

                        if ( (LA19_0=='\\') ) {
                            alt19=1;
                        }
                        else if ( ((LA19_0 >= '\u0000' && LA19_0 <= '[')||(LA19_0 >= ']' && LA19_0 <= '_')||(LA19_0 >= 'a' && LA19_0 <= '\uFFFF')) ) {
                            alt19=2;
                        }


                        switch (alt19) {
                    	case 1 :
                    	    // R.g:406:10: ESC_SEQ
                    	    {
                    	    mESC_SEQ(); 


                    	    }
                    	    break;
                    	case 2 :
                    	    // R.g:406:20: ~ ( '\\\\' | '`' )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '_')||(input.LA(1) >= 'a' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop19;
                        }
                    } while (true);


                    match('`'); 

                    setText(getText().substring(1, getText().length()-1));

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "OP"
    public final void mOP() throws RecognitionException {
        try {
            int _type = OP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:408:4: ( '%' ( OP_NAME )+ '%' )
            // R.g:408:6: '%' ( OP_NAME )+ '%'
            {
            match('%'); 

            // R.g:408:10: ( OP_NAME )+
            int cnt21=0;
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0=='&'||(LA21_0 >= '*' && LA21_0 <= '/')||LA21_0==':'||(LA21_0 >= '<' && LA21_0 <= '>')||(LA21_0 >= 'A' && LA21_0 <= 'Z')||(LA21_0 >= '^' && LA21_0 <= '_')||(LA21_0 >= 'a' && LA21_0 <= 'z')||LA21_0=='|'||LA21_0=='~') ) {
                    alt21=1;
                }


                switch (alt21) {
            	case 1 :
            	    // R.g:408:10: OP_NAME
            	    {
            	    mOP_NAME(); 


            	    }
            	    break;

            	default :
            	    if ( cnt21 >= 1 ) break loop21;
                        EarlyExitException eee =
                            new EarlyExitException(21, input);
                        throw eee;
                }
                cnt21++;
            } while (true);


            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OP"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:411:5: ( ( '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"' | '\\'' ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )* '\\'' ) )
            // R.g:412:5: ( '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"' | '\\'' ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )* '\\'' )
            {
            // R.g:412:5: ( '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"' | '\\'' ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )* '\\'' )
            int alt24=2;
            int LA24_0 = input.LA(1);

            if ( (LA24_0=='\"') ) {
                alt24=1;
            }
            else if ( (LA24_0=='\'') ) {
                alt24=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;

            }
            switch (alt24) {
                case 1 :
                    // R.g:412:7: '\"' ( ESC_SEQ |~ ( '\\\\' | '\"' ) )* '\"'
                    {
                    match('\"'); 

                    // R.g:412:11: ( ESC_SEQ |~ ( '\\\\' | '\"' ) )*
                    loop22:
                    do {
                        int alt22=3;
                        int LA22_0 = input.LA(1);

                        if ( (LA22_0=='\\') ) {
                            alt22=1;
                        }
                        else if ( ((LA22_0 >= '\u0000' && LA22_0 <= '!')||(LA22_0 >= '#' && LA22_0 <= '[')||(LA22_0 >= ']' && LA22_0 <= '\uFFFF')) ) {
                            alt22=2;
                        }


                        switch (alt22) {
                    	case 1 :
                    	    // R.g:412:13: ESC_SEQ
                    	    {
                    	    mESC_SEQ(); 


                    	    }
                    	    break;
                    	case 2 :
                    	    // R.g:412:23: ~ ( '\\\\' | '\"' )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop22;
                        }
                    } while (true);


                    match('\"'); 

                    }
                    break;
                case 2 :
                    // R.g:413:7: '\\'' ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )* '\\''
                    {
                    match('\''); 

                    // R.g:413:12: ( ESC_SEQ |~ ( '\\\\' | '\\'' ) )*
                    loop23:
                    do {
                        int alt23=3;
                        int LA23_0 = input.LA(1);

                        if ( (LA23_0=='\\') ) {
                            alt23=1;
                        }
                        else if ( ((LA23_0 >= '\u0000' && LA23_0 <= '&')||(LA23_0 >= '(' && LA23_0 <= '[')||(LA23_0 >= ']' && LA23_0 <= '\uFFFF')) ) {
                            alt23=2;
                        }


                        switch (alt23) {
                    	case 1 :
                    	    // R.g:413:14: ESC_SEQ
                    	    {
                    	    mESC_SEQ(); 


                    	    }
                    	    break;
                    	case 2 :
                    	    // R.g:413:24: ~ ( '\\\\' | '\\'' )
                    	    {
                    	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop23;
                        }
                    } while (true);


                    match('\''); 

                    }
                    break;

            }


            setText(getText().substring(1, getText().length()-1));

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "LINE_BREAK"
    public final void mLINE_BREAK() throws RecognitionException {
        try {
            // R.g:419:2: ( ( ( '\\f' | '\\r' )? '\\n' ) | ( ( '\\n' )? ( '\\r' | '\\f' ) ) )
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( ((LA27_0 >= '\f' && LA27_0 <= '\r')) ) {
                int LA27_1 = input.LA(2);

                if ( (LA27_1=='\n') ) {
                    alt27=1;
                }
                else {
                    alt27=2;
                }
            }
            else if ( (LA27_0=='\n') ) {
                int LA27_2 = input.LA(2);

                if ( ((LA27_2 >= '\f' && LA27_2 <= '\r')) ) {
                    alt27=2;
                }
                else {
                    alt27=1;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;

            }
            switch (alt27) {
                case 1 :
                    // R.g:420:3: ( ( '\\f' | '\\r' )? '\\n' )
                    {
                    // R.g:420:3: ( ( '\\f' | '\\r' )? '\\n' )
                    // R.g:420:4: ( '\\f' | '\\r' )? '\\n'
                    {
                    // R.g:420:4: ( '\\f' | '\\r' )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( ((LA25_0 >= '\f' && LA25_0 <= '\r')) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // R.g:
                            {
                            if ( (input.LA(1) >= '\f' && input.LA(1) <= '\r') ) {
                                input.consume();
                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;
                            }


                            }
                            break;

                    }


                    match('\n'); 

                    }


                    }
                    break;
                case 2 :
                    // R.g:421:4: ( ( '\\n' )? ( '\\r' | '\\f' ) )
                    {
                    // R.g:421:4: ( ( '\\n' )? ( '\\r' | '\\f' ) )
                    // R.g:421:5: ( '\\n' )? ( '\\r' | '\\f' )
                    {
                    // R.g:421:5: ( '\\n' )?
                    int alt26=2;
                    int LA26_0 = input.LA(1);

                    if ( (LA26_0=='\n') ) {
                        alt26=1;
                    }
                    switch (alt26) {
                        case 1 :
                            // R.g:421:5: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }


                    if ( (input.LA(1) >= '\f' && input.LA(1) <= '\r') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "LINE_BREAK"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // R.g:425:2: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // R.g:425:4: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // R.g:425:14: ( '+' | '-' )?
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0=='+'||LA28_0=='-') ) {
                alt28=1;
            }
            switch (alt28) {
                case 1 :
                    // R.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // R.g:425:25: ( '0' .. '9' )+
            int cnt29=0;
            loop29:
            do {
                int alt29=2;
                int LA29_0 = input.LA(1);

                if ( ((LA29_0 >= '0' && LA29_0 <= '9')) ) {
                    alt29=1;
                }


                switch (alt29) {
            	case 1 :
            	    // R.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt29 >= 1 ) break loop29;
                        EarlyExitException eee =
                            new EarlyExitException(29, input);
                        throw eee;
                }
                cnt29++;
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "OP_NAME"
    public final void mOP_NAME() throws RecognitionException {
        try {
            // R.g:429:2: ( ID_NAME | ( '*' | '/' | '+' | '-' | '>' | '<' | '=' | '|' | '&' | ':' | '^' | '.' | '~' | ',' ) )
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( ((LA30_0 >= 'A' && LA30_0 <= 'Z')||LA30_0=='_'||(LA30_0 >= 'a' && LA30_0 <= 'z')) ) {
                alt30=1;
            }
            else if ( (LA30_0=='&'||(LA30_0 >= '*' && LA30_0 <= '/')||LA30_0==':'||(LA30_0 >= '<' && LA30_0 <= '>')||LA30_0=='^'||LA30_0=='|'||LA30_0=='~') ) {
                alt30=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;

            }
            switch (alt30) {
                case 1 :
                    // R.g:429:4: ID_NAME
                    {
                    mID_NAME(); 


                    }
                    break;
                case 2 :
                    // R.g:430:4: ( '*' | '/' | '+' | '-' | '>' | '<' | '=' | '|' | '&' | ':' | '^' | '.' | '~' | ',' )
                    {
                    if ( input.LA(1)=='&'||(input.LA(1) >= '*' && input.LA(1) <= '/')||input.LA(1)==':'||(input.LA(1) >= '<' && input.LA(1) <= '>')||input.LA(1)=='^'||input.LA(1)=='|'||input.LA(1)=='~' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OP_NAME"

    // $ANTLR start "ID_NAME"
    public final void mID_NAME() throws RecognitionException {
        try {
            // R.g:434:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )* )
            // R.g:434:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // R.g:434:28: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )*
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0=='.'||(LA31_0 >= '0' && LA31_0 <= '9')||(LA31_0 >= 'A' && LA31_0 <= 'Z')||LA31_0=='_'||(LA31_0 >= 'a' && LA31_0 <= 'z')) ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // R.g:
            	    {
            	    if ( input.LA(1)=='.'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop31;
                }
            } while (true);


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID_NAME"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // R.g:438:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '`' | '\\\\' | ' ' | 'a' | 'v' ) | '\\\\' LINE_BREAK | UNICODE_ESC | OCTAL_ESC | HEX_ESC )
            int alt32=5;
            int LA32_0 = input.LA(1);

            if ( (LA32_0=='\\') ) {
                switch ( input.LA(2) ) {
                case ' ':
                case '\"':
                case '\'':
                case '\\':
                case '`':
                case 'a':
                case 'b':
                case 'f':
                case 'n':
                case 'r':
                case 't':
                case 'v':
                    {
                    alt32=1;
                    }
                    break;
                case 'u':
                    {
                    alt32=3;
                    }
                    break;
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    {
                    alt32=4;
                    }
                    break;
                case 'x':
                    {
                    alt32=5;
                    }
                    break;
                case '\n':
                case '\f':
                case '\r':
                    {
                    alt32=2;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;

                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;

            }
            switch (alt32) {
                case 1 :
                    // R.g:438:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '`' | '\\\\' | ' ' | 'a' | 'v' )
                    {
                    match('\\'); 

                    if ( input.LA(1)==' '||input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||(input.LA(1) >= '`' && input.LA(1) <= 'b')||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t'||input.LA(1)=='v' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // R.g:439:7: '\\\\' LINE_BREAK
                    {
                    match('\\'); 

                    mLINE_BREAK(); 


                    }
                    break;
                case 3 :
                    // R.g:440:9: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 


                    }
                    break;
                case 4 :
                    // R.g:441:9: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 


                    }
                    break;
                case 5 :
                    // R.g:442:7: HEX_ESC
                    {
                    mHEX_ESC(); 


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            // R.g:446:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // R.g:446:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
            {
            match('\\'); 

            match('u'); 

            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            mHEX_DIGIT(); 


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "UNICODE_ESC"

    // $ANTLR start "HEX_ESC"
    public final void mHEX_ESC() throws RecognitionException {
        try {
            // R.g:450:2: ( '\\\\x' HEX_DIGIT ( HEX_DIGIT )? )
            // R.g:450:4: '\\\\x' HEX_DIGIT ( HEX_DIGIT )?
            {
            match("\\x"); 



            mHEX_DIGIT(); 


            // R.g:450:20: ( HEX_DIGIT )?
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( ((LA33_0 >= '0' && LA33_0 <= '9')||(LA33_0 >= 'A' && LA33_0 <= 'F')||(LA33_0 >= 'a' && LA33_0 <= 'f')) ) {
                alt33=1;
            }
            switch (alt33) {
                case 1 :
                    // R.g:
                    {
                    if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HEX_ESC"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // R.g:454:2: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // R.g:
            {
            if ( (input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'F')||(input.LA(1) >= 'a' && input.LA(1) <= 'f') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }


        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            // R.g:458:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt34=3;
            int LA34_0 = input.LA(1);

            if ( (LA34_0=='\\') ) {
                int LA34_1 = input.LA(2);

                if ( ((LA34_1 >= '0' && LA34_1 <= '3')) ) {
                    int LA34_2 = input.LA(3);

                    if ( ((LA34_2 >= '0' && LA34_2 <= '7')) ) {
                        int LA34_4 = input.LA(4);

                        if ( ((LA34_4 >= '0' && LA34_4 <= '7')) ) {
                            alt34=1;
                        }
                        else {
                            alt34=2;
                        }
                    }
                    else {
                        alt34=3;
                    }
                }
                else if ( ((LA34_1 >= '4' && LA34_1 <= '7')) ) {
                    int LA34_3 = input.LA(3);

                    if ( ((LA34_3 >= '0' && LA34_3 <= '7')) ) {
                        alt34=2;
                    }
                    else {
                        alt34=3;
                    }
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 34, 1, input);

                    throw nvae;

                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;

            }
            switch (alt34) {
                case 1 :
                    // R.g:458:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '3') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // R.g:459:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;
                case 3 :
                    // R.g:460:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 

                    if ( (input.LA(1) >= '0' && input.LA(1) <= '7') ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }

        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "OCTAL_ESC"

    @Override
    public void mTokens() throws RecognitionException {
        // R.g:1:8: ( T__80 | COMMENT | ARROW | SUPER_ARROW | RIGHT_ARROW | SUPER_RIGHT_ARROW | VARIATIC | EQ | NE | GE | LE | GT | LT | ASSIGN | NS_GET_INT | NS_GET | COLUMN | SEMICOLUMN | COMMA | AND | BITWISEAND | OR | BITWISEOR | LBRACE | RBRACE | LPAR | RPAR | LBB | LBRAKET | RBRAKET | CARRET | TILDE | MOD | NOT | PLUS | MULT | DIV | MINUS | FIELD | AT | FUNCTION | NULL | TRUE | FALSE | WHILE | FOR | REPEAT | IN | IF | ELSE | NEXT | BREAK | WS | NEWLINE | NUMBER | DD | ID | OP | STRING )
        int alt35=59;
        alt35 = dfa35.predict(input);
        switch (alt35) {
            case 1 :
                // R.g:1:10: T__80
                {
                mT__80(); 


                }
                break;
            case 2 :
                // R.g:1:16: COMMENT
                {
                mCOMMENT(); 


                }
                break;
            case 3 :
                // R.g:1:24: ARROW
                {
                mARROW(); 


                }
                break;
            case 4 :
                // R.g:1:30: SUPER_ARROW
                {
                mSUPER_ARROW(); 


                }
                break;
            case 5 :
                // R.g:1:42: RIGHT_ARROW
                {
                mRIGHT_ARROW(); 


                }
                break;
            case 6 :
                // R.g:1:54: SUPER_RIGHT_ARROW
                {
                mSUPER_RIGHT_ARROW(); 


                }
                break;
            case 7 :
                // R.g:1:72: VARIATIC
                {
                mVARIATIC(); 


                }
                break;
            case 8 :
                // R.g:1:81: EQ
                {
                mEQ(); 


                }
                break;
            case 9 :
                // R.g:1:84: NE
                {
                mNE(); 


                }
                break;
            case 10 :
                // R.g:1:87: GE
                {
                mGE(); 


                }
                break;
            case 11 :
                // R.g:1:90: LE
                {
                mLE(); 


                }
                break;
            case 12 :
                // R.g:1:93: GT
                {
                mGT(); 


                }
                break;
            case 13 :
                // R.g:1:96: LT
                {
                mLT(); 


                }
                break;
            case 14 :
                // R.g:1:99: ASSIGN
                {
                mASSIGN(); 


                }
                break;
            case 15 :
                // R.g:1:106: NS_GET_INT
                {
                mNS_GET_INT(); 


                }
                break;
            case 16 :
                // R.g:1:117: NS_GET
                {
                mNS_GET(); 


                }
                break;
            case 17 :
                // R.g:1:124: COLUMN
                {
                mCOLUMN(); 


                }
                break;
            case 18 :
                // R.g:1:131: SEMICOLUMN
                {
                mSEMICOLUMN(); 


                }
                break;
            case 19 :
                // R.g:1:142: COMMA
                {
                mCOMMA(); 


                }
                break;
            case 20 :
                // R.g:1:148: AND
                {
                mAND(); 


                }
                break;
            case 21 :
                // R.g:1:152: BITWISEAND
                {
                mBITWISEAND(); 


                }
                break;
            case 22 :
                // R.g:1:163: OR
                {
                mOR(); 


                }
                break;
            case 23 :
                // R.g:1:166: BITWISEOR
                {
                mBITWISEOR(); 


                }
                break;
            case 24 :
                // R.g:1:176: LBRACE
                {
                mLBRACE(); 


                }
                break;
            case 25 :
                // R.g:1:183: RBRACE
                {
                mRBRACE(); 


                }
                break;
            case 26 :
                // R.g:1:190: LPAR
                {
                mLPAR(); 


                }
                break;
            case 27 :
                // R.g:1:195: RPAR
                {
                mRPAR(); 


                }
                break;
            case 28 :
                // R.g:1:200: LBB
                {
                mLBB(); 


                }
                break;
            case 29 :
                // R.g:1:204: LBRAKET
                {
                mLBRAKET(); 


                }
                break;
            case 30 :
                // R.g:1:212: RBRAKET
                {
                mRBRAKET(); 


                }
                break;
            case 31 :
                // R.g:1:220: CARRET
                {
                mCARRET(); 


                }
                break;
            case 32 :
                // R.g:1:227: TILDE
                {
                mTILDE(); 


                }
                break;
            case 33 :
                // R.g:1:233: MOD
                {
                mMOD(); 


                }
                break;
            case 34 :
                // R.g:1:237: NOT
                {
                mNOT(); 


                }
                break;
            case 35 :
                // R.g:1:241: PLUS
                {
                mPLUS(); 


                }
                break;
            case 36 :
                // R.g:1:246: MULT
                {
                mMULT(); 


                }
                break;
            case 37 :
                // R.g:1:251: DIV
                {
                mDIV(); 


                }
                break;
            case 38 :
                // R.g:1:255: MINUS
                {
                mMINUS(); 


                }
                break;
            case 39 :
                // R.g:1:261: FIELD
                {
                mFIELD(); 


                }
                break;
            case 40 :
                // R.g:1:267: AT
                {
                mAT(); 


                }
                break;
            case 41 :
                // R.g:1:270: FUNCTION
                {
                mFUNCTION(); 


                }
                break;
            case 42 :
                // R.g:1:279: NULL
                {
                mNULL(); 


                }
                break;
            case 43 :
                // R.g:1:284: TRUE
                {
                mTRUE(); 


                }
                break;
            case 44 :
                // R.g:1:289: FALSE
                {
                mFALSE(); 


                }
                break;
            case 45 :
                // R.g:1:295: WHILE
                {
                mWHILE(); 


                }
                break;
            case 46 :
                // R.g:1:301: FOR
                {
                mFOR(); 


                }
                break;
            case 47 :
                // R.g:1:305: REPEAT
                {
                mREPEAT(); 


                }
                break;
            case 48 :
                // R.g:1:312: IN
                {
                mIN(); 


                }
                break;
            case 49 :
                // R.g:1:315: IF
                {
                mIF(); 


                }
                break;
            case 50 :
                // R.g:1:318: ELSE
                {
                mELSE(); 


                }
                break;
            case 51 :
                // R.g:1:323: NEXT
                {
                mNEXT(); 


                }
                break;
            case 52 :
                // R.g:1:328: BREAK
                {
                mBREAK(); 


                }
                break;
            case 53 :
                // R.g:1:334: WS
                {
                mWS(); 


                }
                break;
            case 54 :
                // R.g:1:337: NEWLINE
                {
                mNEWLINE(); 


                }
                break;
            case 55 :
                // R.g:1:345: NUMBER
                {
                mNUMBER(); 


                }
                break;
            case 56 :
                // R.g:1:352: DD
                {
                mDD(); 


                }
                break;
            case 57 :
                // R.g:1:355: ID
                {
                mID(); 


                }
                break;
            case 58 :
                // R.g:1:358: OP
                {
                mOP(); 


                }
                break;
            case 59 :
                // R.g:1:361: STRING
                {
                mSTRING(); 


                }
                break;

        }

    }


    protected DFA16 dfa16 = new DFA16(this);
    protected DFA35 dfa35 = new DFA35(this);
    static final String DFA16_eotS =
        "\1\uffff\1\2\1\uffff\1\2\2\uffff";
    static final String DFA16_eofS =
        "\6\uffff";
    static final String DFA16_minS =
        "\2\56\1\uffff\1\56\2\uffff";
    static final String DFA16_maxS =
        "\1\71\1\170\1\uffff\1\71\2\uffff";
    static final String DFA16_acceptS =
        "\2\uffff\1\2\1\uffff\1\3\1\1";
    static final String DFA16_specialS =
        "\6\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\2\1\uffff\1\1\11\3",
            "\1\5\1\uffff\12\3\76\uffff\1\4",
            "",
            "\1\5\1\uffff\12\3",
            "",
            ""
    };

    static final short[] DFA16_eot = DFA.unpackEncodedString(DFA16_eotS);
    static final short[] DFA16_eof = DFA.unpackEncodedString(DFA16_eofS);
    static final char[] DFA16_min = DFA.unpackEncodedStringToUnsignedChars(DFA16_minS);
    static final char[] DFA16_max = DFA.unpackEncodedStringToUnsignedChars(DFA16_maxS);
    static final short[] DFA16_accept = DFA.unpackEncodedString(DFA16_acceptS);
    static final short[] DFA16_special = DFA.unpackEncodedString(DFA16_specialS);
    static final short[][] DFA16_transition;

    static {
        int numStates = DFA16_transitionS.length;
        DFA16_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA16_transition[i] = DFA.unpackEncodedString(DFA16_transitionS[i]);
        }
    }

    class DFA16 extends DFA {

        public DFA16(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 16;
            this.eot = DFA16_eot;
            this.eof = DFA16_eof;
            this.min = DFA16_min;
            this.max = DFA16_max;
            this.accept = DFA16_accept;
            this.special = DFA16_special;
            this.transition = DFA16_transition;
        }
        @Override
        public String getDescription() {
            return "397:1: NUMBER : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? ( 'i' | 'L' )? | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? ( 'i' | 'L' )? | '0x' ( HEX_DIGIT )+ ( 'L' )? );";
        }
    }
    static final String DFA35_eotS =
        "\1\uffff\1\54\1\uffff\1\60\1\62\1\50\1\65\1\67\1\71\2\uffff\1\73"+
        "\1\75\4\uffff\1\77\2\uffff\1\100\6\uffff\12\50\6\uffff\1\120\5\uffff"+
        "\1\122\21\uffff\7\50\1\134\1\135\3\50\4\uffff\1\141\1\uffff\1\50"+
        "\1\143\5\50\2\uffff\3\50\1\uffff\1\50\1\uffff\1\155\1\156\3\50\1"+
        "\162\1\163\2\50\2\uffff\1\166\1\167\1\50\2\uffff\1\171\1\50\2\uffff"+
        "\1\173\1\uffff\1\50\1\uffff\1\175\1\uffff";
    static final String DFA35_eofS =
        "\176\uffff";
    static final String DFA35_minS =
        "\1\11\1\55\1\uffff\1\55\1\72\1\56\3\75\2\uffff\1\46\1\174\4\uffff"+
        "\1\133\2\uffff\1\52\1\uffff\1\45\4\uffff\1\157\1\125\1\122\1\101"+
        "\1\150\1\145\1\146\1\154\1\145\1\162\6\uffff\1\76\5\uffff\1\72\1"+
        "\uffff\1\56\17\uffff\1\156\1\162\1\114\1\125\1\114\1\151\1\160\2"+
        "\56\1\163\1\170\1\145\4\uffff\1\56\1\uffff\1\143\1\56\1\114\1\105"+
        "\1\123\1\154\1\145\2\uffff\1\145\1\164\1\141\1\uffff\1\164\1\uffff"+
        "\2\56\1\105\1\145\1\141\2\56\1\153\1\151\2\uffff\2\56\1\164\2\uffff"+
        "\1\56\1\157\2\uffff\1\56\1\uffff\1\156\1\uffff\1\56\1\uffff";
    static final String DFA35_maxS =
        "\1\176\1\76\1\uffff\2\75\1\71\3\75\2\uffff\1\46\1\174\4\uffff\1"+
        "\133\2\uffff\1\52\1\uffff\1\176\4\uffff\1\165\1\125\1\122\1\101"+
        "\1\150\1\145\1\156\1\154\1\145\1\162\6\uffff\1\76\5\uffff\1\72\1"+
        "\uffff\1\172\17\uffff\1\156\1\162\1\114\1\125\1\114\1\151\1\160"+
        "\2\172\1\163\1\170\1\145\4\uffff\1\172\1\uffff\1\143\1\172\1\114"+
        "\1\105\1\123\1\154\1\145\2\uffff\1\145\1\164\1\141\1\uffff\1\164"+
        "\1\uffff\2\172\1\105\1\145\1\141\2\172\1\153\1\151\2\uffff\2\172"+
        "\1\164\2\uffff\1\172\1\157\2\uffff\1\172\1\uffff\1\156\1\uffff\1"+
        "\172\1\uffff";
    static final String DFA35_acceptS =
        "\2\uffff\1\2\6\uffff\1\22\1\23\2\uffff\1\30\1\31\1\32\1\33\1\uffff"+
        "\1\36\1\37\1\uffff\1\40\1\uffff\1\43\1\45\1\47\1\50\12\uffff\1\65"+
        "\1\66\1\67\1\71\1\73\1\1\1\uffff\1\46\1\3\1\4\1\13\1\15\1\uffff"+
        "\1\21\1\uffff\1\10\1\16\1\11\1\42\1\12\1\14\1\24\1\25\1\26\1\27"+
        "\1\34\1\35\1\44\1\41\1\72\14\uffff\1\6\1\5\1\17\1\20\1\uffff\1\70"+
        "\7\uffff\1\60\1\61\3\uffff\1\7\1\uffff\1\56\11\uffff\1\52\1\53\3"+
        "\uffff\1\62\1\63\2\uffff\1\54\1\55\1\uffff\1\64\1\uffff\1\57\1\uffff"+
        "\1\51";
    static final String DFA35_specialS =
        "\176\uffff}>";
    static final String[] DFA35_transitionS = {
            "\1\45\1\46\1\uffff\2\46\22\uffff\1\45\1\7\1\51\1\2\1\31\1\26"+
            "\1\13\1\51\1\17\1\20\1\24\1\27\1\12\1\1\1\5\1\30\12\47\1\4\1"+
            "\11\1\3\1\6\1\10\1\uffff\1\32\5\50\1\36\7\50\1\34\5\50\1\35"+
            "\6\50\1\21\1\uffff\1\22\1\23\3\50\1\44\2\50\1\42\1\33\2\50\1"+
            "\41\4\50\1\43\3\50\1\40\4\50\1\37\3\50\1\15\1\14\1\16\1\25",
            "\1\52\20\uffff\1\53",
            "",
            "\1\55\16\uffff\1\56\1\57",
            "\1\61\2\uffff\1\55",
            "\1\63\1\uffff\12\47",
            "\1\64",
            "\1\66",
            "\1\70",
            "",
            "",
            "\1\72",
            "\1\74",
            "",
            "",
            "",
            "",
            "\1\76",
            "",
            "",
            "\1\23",
            "",
            "\1\101\1\102\3\uffff\6\102\12\uffff\1\102\1\uffff\3\102\2\uffff"+
            "\32\102\3\uffff\2\102\1\uffff\32\102\1\uffff\1\102\1\uffff\1"+
            "\102",
            "",
            "",
            "",
            "",
            "\1\104\5\uffff\1\103",
            "\1\105",
            "\1\106",
            "\1\107",
            "\1\110",
            "\1\111",
            "\1\113\7\uffff\1\112",
            "\1\114",
            "\1\115",
            "\1\116",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\117",
            "",
            "",
            "",
            "",
            "",
            "\1\121",
            "",
            "\1\123\1\uffff\12\124\7\uffff\32\50\4\uffff\1\50\1\uffff\32"+
            "\50",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "\1\125",
            "\1\126",
            "\1\127",
            "\1\130",
            "\1\131",
            "\1\132",
            "\1\133",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\136",
            "\1\137",
            "\1\140",
            "",
            "",
            "",
            "",
            "\1\123\22\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "",
            "\1\142",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\144",
            "\1\145",
            "\1\146",
            "\1\147",
            "\1\150",
            "",
            "",
            "\1\151",
            "\1\152",
            "\1\153",
            "",
            "\1\154",
            "",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\157",
            "\1\160",
            "\1\161",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\164",
            "\1\165",
            "",
            "",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\170",
            "",
            "",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "\1\172",
            "",
            "",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            "",
            "\1\174",
            "",
            "\1\50\1\uffff\12\50\7\uffff\32\50\4\uffff\1\50\1\uffff\32\50",
            ""
    };

    static final short[] DFA35_eot = DFA.unpackEncodedString(DFA35_eotS);
    static final short[] DFA35_eof = DFA.unpackEncodedString(DFA35_eofS);
    static final char[] DFA35_min = DFA.unpackEncodedStringToUnsignedChars(DFA35_minS);
    static final char[] DFA35_max = DFA.unpackEncodedStringToUnsignedChars(DFA35_maxS);
    static final short[] DFA35_accept = DFA.unpackEncodedString(DFA35_acceptS);
    static final short[] DFA35_special = DFA.unpackEncodedString(DFA35_specialS);
    static final short[][] DFA35_transition;

    static {
        int numStates = DFA35_transitionS.length;
        DFA35_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA35_transition[i] = DFA.unpackEncodedString(DFA35_transitionS[i]);
        }
    }

    class DFA35 extends DFA {

        public DFA35(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 35;
            this.eot = DFA35_eot;
            this.eof = DFA35_eof;
            this.min = DFA35_min;
            this.max = DFA35_max;
            this.accept = DFA35_accept;
            this.special = DFA35_special;
            this.transition = DFA35_transition;
        }
        @Override
        public String getDescription() {
            return "1:1: Tokens : ( T__80 | COMMENT | ARROW | SUPER_ARROW | RIGHT_ARROW | SUPER_RIGHT_ARROW | VARIATIC | EQ | NE | GE | LE | GT | LT | ASSIGN | NS_GET_INT | NS_GET | COLUMN | SEMICOLUMN | COMMA | AND | BITWISEAND | OR | BITWISEOR | LBRACE | RBRACE | LPAR | RPAR | LBB | LBRAKET | RBRAKET | CARRET | TILDE | MOD | NOT | PLUS | MULT | DIV | MINUS | FIELD | AT | FUNCTION | NULL | TRUE | FALSE | WHILE | FOR | REPEAT | IN | IF | ELSE | NEXT | BREAK | WS | NEWLINE | NUMBER | DD | ID | OP | STRING );";
        }
    }
 

}
