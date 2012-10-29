// $ANTLR !Unknown version! R.g 2012-10-29 13:41:24

package r.parser;
//Checkstyle: stop


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

public class RLexer extends Lexer {
    public static final int FUNCTION=34;
    public static final int PARMS=7;
    public static final int UTILDE=12;
    public static final int LT=65;
    public static final int EXPONENT=75;
    public static final int WHILE=29;
    public static final int MOD=71;
    public static final int LBB=48;
    public static final int RIGHT_ARROW=22;
    public static final int LBRACE=18;
    public static final int OCTAL_ESC=82;
    public static final int FOR=30;
    public static final int MISSING_VAL=9;
    public static final int COMPLEX=54;
    public static final int RBRAKET=47;
    public static final int NOT=39;
    public static final int AND=61;
    public static final int ID=31;
    public static final int EOF=-1;
    public static final int DD=37;
    public static final int BREAK=17;
    public static final int IF=25;
    public static final int AT=45;
    public static final int BITWISEAND=62;
    public static final int ESC_SEQ=78;
    public static final int IN=32;
    public static final int ESCAPE=80;
    public static final int LPAR=26;
    public static final int COMMA=35;
    public static final int TILDE=38;
    public static final int DOUBLE=53;
    public static final int PLUS=42;
    public static final int NEXT=16;
    public static final int EQ=67;
    public static final int COMMENT=14;
    public static final int NA=58;
    public static final int BRAKET=5;
    public static final int NE=68;
    public static final int INTEGER=52;
    public static final int GE=64;
    public static final int RBRACE=19;
    public static final int LBRAKET=46;
    public static final int UPLUS=10;
    public static final int NS_GET_INT=51;
    public static final int NULL=49;
    public static final int UNICODE_ESC=81;
    public static final int ELSE=28;
    public static final int UMINUS=11;
    public static final int HEX_DIGIT=76;
    public static final int ID_NAME=77;
    public static final int SEMICOLON=15;
    public static final int MINUS=43;
    public static final int MULT=69;
    public static final int T__84=84;
    public static final int VARIATIC=36;
    public static final int TRUE=56;
    public static final int BITWISEOR=60;
    public static final int COLON=41;
    public static final int SEQUENCE=8;
    public static final int HEX_ESC=83;
    public static final int LINE_BREAK=73;
    public static final int WS=74;
    public static final int NEWLINE=13;
    public static final int SUPER_RIGHT_ARROW=23;
    public static final int KW=6;
    public static final int SUPER_ARROW=21;
    public static final int OP=40;
    public static final int OR=59;
    public static final int ASSIGN=24;
    public static final int ARROW=20;
    public static final int NS_GET=50;
    public static final int GT=63;
    public static final int RPAR=27;
    public static final int FIELD=44;
    public static final int REPEAT=33;
    public static final int CALL=4;
    public static final int CARRET=72;
    public static final int OP_NAME=79;
    public static final int DIV=70;
    public static final int FALSE=57;
    public static final int LE=66;
    public static final int STRING=55;

        public final int MAX_INCOMPLETE_SIZE = 1000;
        int incomplete_stack[] = new int[MAX_INCOMPLETE_SIZE]; // TODO probably go for an ArrayList of int :S
        int incomplete_depth;
        
        @Override
        public void reportError(RecognitionException e) {
            throw new RuntimeException(e);
        }


    // delegates
    // delegators

    public RLexer() {;} 
    public RLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public RLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);

    }
    @Override
    public String getGrammarFileName() { return "R.g"; }

    // $ANTLR start "T__84"
    public final void mT__84() throws RecognitionException {
        try {
            int _type = T__84;
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
        }
    }
    // $ANTLR end "T__84"

    // $ANTLR start "COMMENT"
    public final void mCOMMENT() throws RecognitionException {
        try {
            int _type = COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:300:5: ( '#' (~ ( '\\n' | '\\r' | '\\f' ) )* ( LINE_BREAK | EOF ) )
            // R.g:300:9: '#' (~ ( '\\n' | '\\r' | '\\f' ) )* ( LINE_BREAK | EOF )
            {
            match('#'); 
            // R.g:300:13: (~ ( '\\n' | '\\r' | '\\f' ) )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>='\u0000' && LA1_0<='\t')||LA1_0=='\u000B'||(LA1_0>='\u000E' && LA1_0<='\uFFFF')) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // R.g:300:13: ~ ( '\\n' | '\\r' | '\\f' )
            	    {
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||input.LA(1)=='\u000B'||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            // R.g:300:32: ( LINE_BREAK | EOF )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0=='\n'||(LA2_0>='\f' && LA2_0<='\r')) ) {
                alt2=1;
            }
            else {
                alt2=2;}
            switch (alt2) {
                case 1 :
                    // R.g:300:33: LINE_BREAK
                    {
                    mLINE_BREAK(); 

                    }
                    break;
                case 2 :
                    // R.g:300:46: EOF
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
        }
    }
    // $ANTLR end "COMMENT"

    // $ANTLR start "ARROW"
    public final void mARROW() throws RecognitionException {
        try {
            int _type = ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:303:2: ( '<-' | ':=' )
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
                    // R.g:303:4: '<-'
                    {
                    match("<-"); 


                    }
                    break;
                case 2 :
                    // R.g:303:11: ':='
                    {
                    match(":="); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ARROW"

    // $ANTLR start "SUPER_ARROW"
    public final void mSUPER_ARROW() throws RecognitionException {
        try {
            int _type = SUPER_ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:306:2: ( '<<-' )
            // R.g:306:5: '<<-'
            {
            match("<<-"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SUPER_ARROW"

    // $ANTLR start "RIGHT_ARROW"
    public final void mRIGHT_ARROW() throws RecognitionException {
        try {
            int _type = RIGHT_ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:308:2: ( '->' )
            // R.g:308:4: '->'
            {
            match("->"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RIGHT_ARROW"

    // $ANTLR start "SUPER_RIGHT_ARROW"
    public final void mSUPER_RIGHT_ARROW() throws RecognitionException {
        try {
            int _type = SUPER_RIGHT_ARROW;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:311:2: ( '->>' )
            // R.g:311:4: '->>'
            {
            match("->>"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SUPER_RIGHT_ARROW"

    // $ANTLR start "VARIATIC"
    public final void mVARIATIC() throws RecognitionException {
        try {
            int _type = VARIATIC;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:314:2: ( '..' ( '.' )+ )
            // R.g:314:4: '..' ( '.' )+
            {
            match(".."); 

            // R.g:314:9: ( '.' )+
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
            	    // R.g:314:9: '.'
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
        }
    }
    // $ANTLR end "VARIATIC"

    // $ANTLR start "EQ"
    public final void mEQ() throws RecognitionException {
        try {
            int _type = EQ;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:316:4: ( '==' )
            // R.g:316:6: '=='
            {
            match("=="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "NE"
    public final void mNE() throws RecognitionException {
        try {
            int _type = NE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:317:5: ( '!=' )
            // R.g:317:7: '!='
            {
            match("!="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NE"

    // $ANTLR start "GE"
    public final void mGE() throws RecognitionException {
        try {
            int _type = GE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:318:4: ( '>=' )
            // R.g:318:6: '>='
            {
            match(">="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GE"

    // $ANTLR start "LE"
    public final void mLE() throws RecognitionException {
        try {
            int _type = LE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:319:4: ( '<=' )
            // R.g:319:6: '<='
            {
            match("<="); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LE"

    // $ANTLR start "GT"
    public final void mGT() throws RecognitionException {
        try {
            int _type = GT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:320:4: ( '>' )
            // R.g:320:6: '>'
            {
            match('>'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "LT"
    public final void mLT() throws RecognitionException {
        try {
            int _type = LT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:321:5: ( '<' )
            // R.g:321:7: '<'
            {
            match('<'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "ASSIGN"
    public final void mASSIGN() throws RecognitionException {
        try {
            int _type = ASSIGN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:323:2: ( '=' )
            // R.g:323:4: '='
            {
            match('='); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ASSIGN"

    // $ANTLR start "NS_GET_INT"
    public final void mNS_GET_INT() throws RecognitionException {
        try {
            int _type = NS_GET_INT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:327:2: ( ':::' )
            // R.g:327:4: ':::'
            {
            match(":::"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NS_GET_INT"

    // $ANTLR start "NS_GET"
    public final void mNS_GET() throws RecognitionException {
        try {
            int _type = NS_GET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:329:2: ( '::' )
            // R.g:329:4: '::'
            {
            match("::"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NS_GET"

    // $ANTLR start "COLON"
    public final void mCOLON() throws RecognitionException {
        try {
            int _type = COLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:332:2: ( ':' )
            // R.g:332:4: ':'
            {
            match(':'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "SEMICOLON"
    public final void mSEMICOLON() throws RecognitionException {
        try {
            int _type = SEMICOLON;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:334:2: ( ';' )
            // R.g:334:4: ';'
            {
            match(';'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "SEMICOLON"

    // $ANTLR start "COMMA"
    public final void mCOMMA() throws RecognitionException {
        try {
            int _type = COMMA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:336:2: ( ',' )
            // R.g:336:4: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "AND"
    public final void mAND() throws RecognitionException {
        try {
            int _type = AND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:338:2: ( '&&' )
            // R.g:338:4: '&&'
            {
            match("&&"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "BITWISEAND"
    public final void mBITWISEAND() throws RecognitionException {
        try {
            int _type = BITWISEAND;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:340:2: ( '&' )
            // R.g:340:4: '&'
            {
            match('&'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BITWISEAND"

    // $ANTLR start "OR"
    public final void mOR() throws RecognitionException {
        try {
            int _type = OR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:341:4: ( '||' )
            // R.g:341:6: '||'
            {
            match("||"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "BITWISEOR"
    public final void mBITWISEOR() throws RecognitionException {
        try {
            int _type = BITWISEOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:343:2: ( '|' )
            // R.g:343:3: '|'
            {
            match('|'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BITWISEOR"

    // $ANTLR start "LBRACE"
    public final void mLBRACE() throws RecognitionException {
        try {
            int _type = LBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:345:2: ( '{' )
            // R.g:345:4: '{'
            {
            match('{'); 
            incomplete_stack[++incomplete_depth] = 0; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRACE"

    // $ANTLR start "RBRACE"
    public final void mRBRACE() throws RecognitionException {
        try {
            int _type = RBRACE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:347:2: ( '}' )
            // R.g:347:4: '}'
            {
            match('}'); 
            incomplete_depth -- ;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRACE"

    // $ANTLR start "LPAR"
    public final void mLPAR() throws RecognitionException {
        try {
            int _type = LPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:349:2: ( '(' )
            // R.g:349:4: '('
            {
            match('('); 
             incomplete_stack[incomplete_depth] ++; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LPAR"

    // $ANTLR start "RPAR"
    public final void mRPAR() throws RecognitionException {
        try {
            int _type = RPAR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:351:2: ( ')' )
            // R.g:351:4: ')'
            {
            match(')'); 
             incomplete_stack[incomplete_depth]--; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RPAR"

    // $ANTLR start "LBB"
    public final void mLBB() throws RecognitionException {
        try {
            int _type = LBB;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:353:2: ( '[[' )
            // R.g:353:4: '[['
            {
            match("[["); 

             incomplete_stack[incomplete_depth] += 2; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBB"

    // $ANTLR start "LBRAKET"
    public final void mLBRAKET() throws RecognitionException {
        try {
            int _type = LBRAKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:355:2: ( '[' )
            // R.g:355:4: '['
            {
            match('['); 
             incomplete_stack[incomplete_depth] ++; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "LBRAKET"

    // $ANTLR start "RBRAKET"
    public final void mRBRAKET() throws RecognitionException {
        try {
            int _type = RBRAKET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:357:2: ( ']' )
            // R.g:357:4: ']'
            {
            match(']'); 
             incomplete_stack[incomplete_depth] --;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "RBRAKET"

    // $ANTLR start "CARRET"
    public final void mCARRET() throws RecognitionException {
        try {
            int _type = CARRET;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:359:2: ( '^' | '**' )
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
                    // R.g:359:4: '^'
                    {
                    match('^'); 

                    }
                    break;
                case 2 :
                    // R.g:359:10: '**'
                    {
                    match("**"); 


                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "CARRET"

    // $ANTLR start "TILDE"
    public final void mTILDE() throws RecognitionException {
        try {
            int _type = TILDE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:361:2: ( '~' )
            // R.g:361:4: '~'
            {
            match('~'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TILDE"

    // $ANTLR start "MOD"
    public final void mMOD() throws RecognitionException {
        try {
            int _type = MOD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:363:2: ( '%%' )
            // R.g:363:4: '%%'
            {
            match("%%"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MOD"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:366:2: ( '!' )
            // R.g:366:4: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "PLUS"
    public final void mPLUS() throws RecognitionException {
        try {
            int _type = PLUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:368:2: ( '+' )
            // R.g:368:4: '+'
            {
            match('+'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MULT"
    public final void mMULT() throws RecognitionException {
        try {
            int _type = MULT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:370:2: ( '*' )
            // R.g:370:4: '*'
            {
            match('*'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MULT"

    // $ANTLR start "DIV"
    public final void mDIV() throws RecognitionException {
        try {
            int _type = DIV;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:371:5: ( '/' )
            // R.g:371:7: '/'
            {
            match('/'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DIV"

    // $ANTLR start "MINUS"
    public final void mMINUS() throws RecognitionException {
        try {
            int _type = MINUS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:373:2: ( '-' )
            // R.g:373:4: '-'
            {
            match('-'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "FIELD"
    public final void mFIELD() throws RecognitionException {
        try {
            int _type = FIELD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:376:2: ( '$' )
            // R.g:376:4: '$'
            {
            match('$'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FIELD"

    // $ANTLR start "AT"
    public final void mAT() throws RecognitionException {
        try {
            int _type = AT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:377:4: ( '@' )
            // R.g:377:6: '@'
            {
            match('@'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "AT"

    // $ANTLR start "FUNCTION"
    public final void mFUNCTION() throws RecognitionException {
        try {
            int _type = FUNCTION;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:380:2: ( 'function' )
            // R.g:380:4: 'function'
            {
            match("function"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FUNCTION"

    // $ANTLR start "NULL"
    public final void mNULL() throws RecognitionException {
        try {
            int _type = NULL;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:382:2: ( 'NULL' )
            // R.g:382:4: 'NULL'
            {
            match("NULL"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "NA"
    public final void mNA() throws RecognitionException {
        try {
            int _type = NA;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:385:5: ( 'NA' )
            // R.g:385:7: 'NA'
            {
            match("NA"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NA"

    // $ANTLR start "TRUE"
    public final void mTRUE() throws RecognitionException {
        try {
            int _type = TRUE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:387:2: ( 'TRUE' )
            // R.g:387:4: 'TRUE'
            {
            match("TRUE"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "TRUE"

    // $ANTLR start "FALSE"
    public final void mFALSE() throws RecognitionException {
        try {
            int _type = FALSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:389:2: ( 'FALSE' )
            // R.g:389:4: 'FALSE'
            {
            match("FALSE"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FALSE"

    // $ANTLR start "WHILE"
    public final void mWHILE() throws RecognitionException {
        try {
            int _type = WHILE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:392:2: ( 'while' )
            // R.g:392:4: 'while'
            {
            match("while"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WHILE"

    // $ANTLR start "FOR"
    public final void mFOR() throws RecognitionException {
        try {
            int _type = FOR;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:393:5: ( 'for' )
            // R.g:393:7: 'for'
            {
            match("for"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "FOR"

    // $ANTLR start "REPEAT"
    public final void mREPEAT() throws RecognitionException {
        try {
            int _type = REPEAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:395:2: ( 'repeat' )
            // R.g:395:4: 'repeat'
            {
            match("repeat"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "REPEAT"

    // $ANTLR start "IN"
    public final void mIN() throws RecognitionException {
        try {
            int _type = IN;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:396:4: ( 'in' )
            // R.g:396:6: 'in'
            {
            match("in"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "IF"
    public final void mIF() throws RecognitionException {
        try {
            int _type = IF;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:397:4: ( 'if' )
            // R.g:397:6: 'if'
            {
            match("if"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "IF"

    // $ANTLR start "ELSE"
    public final void mELSE() throws RecognitionException {
        try {
            int _type = ELSE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:399:2: ( 'else' )
            // R.g:399:4: 'else'
            {
            match("else"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "ELSE"

    // $ANTLR start "NEXT"
    public final void mNEXT() throws RecognitionException {
        try {
            int _type = NEXT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:401:2: ( 'next' )
            // R.g:401:4: 'next'
            {
            match("next"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEXT"

    // $ANTLR start "BREAK"
    public final void mBREAK() throws RecognitionException {
        try {
            int _type = BREAK;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:403:2: ( 'break' )
            // R.g:403:4: 'break'
            {
            match("break"); 


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "BREAK"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:406:5: ( ( ' ' | '\\t' ) )
            // R.g:406:9: ( ' ' | '\\t' )
            {
            if ( input.LA(1)=='\t'||input.LA(1)==' ' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:411:2: ( LINE_BREAK )
            // R.g:411:4: LINE_BREAK
            {
            mLINE_BREAK(); 
             if(incomplete_stack[incomplete_depth]>0) _channel=HIDDEN; 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "NEWLINE"

    // $ANTLR start "INTEGER"
    public final void mINTEGER() throws RecognitionException {
        try {
            int _type = INTEGER;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:413:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* 'L' | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'L' | '0x' ( HEX_DIGIT )+ 'L' )
            int alt12=3;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // R.g:413:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* 'L'
                    {
                    // R.g:413:9: ( '0' .. '9' )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>='0' && LA6_0<='9')) ) {
                            alt6=1;
                        }


                        switch (alt6) {
                    	case 1 :
                    	    // R.g:413:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

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
                    // R.g:413:25: ( '0' .. '9' )*
                    loop7:
                    do {
                        int alt7=2;
                        int LA7_0 = input.LA(1);

                        if ( ((LA7_0>='0' && LA7_0<='9')) ) {
                            alt7=1;
                        }


                        switch (alt7) {
                    	case 1 :
                    	    // R.g:413:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop7;
                        }
                    } while (true);

                    match('L'); 
                    setText(getText().substring(0, getText().length()-1));

                    }
                    break;
                case 2 :
                    // R.g:414:9: ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'L'
                    {
                    // R.g:414:9: ( '.' )?
                    int alt8=2;
                    int LA8_0 = input.LA(1);

                    if ( (LA8_0=='.') ) {
                        alt8=1;
                    }
                    switch (alt8) {
                        case 1 :
                            // R.g:414:9: '.'
                            {
                            match('.'); 

                            }
                            break;

                    }

                    // R.g:414:14: ( '0' .. '9' )+
                    int cnt9=0;
                    loop9:
                    do {
                        int alt9=2;
                        int LA9_0 = input.LA(1);

                        if ( ((LA9_0>='0' && LA9_0<='9')) ) {
                            alt9=1;
                        }


                        switch (alt9) {
                    	case 1 :
                    	    // R.g:414:15: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt9 >= 1 ) break loop9;
                                EarlyExitException eee =
                                    new EarlyExitException(9, input);
                                throw eee;
                        }
                        cnt9++;
                    } while (true);

                    // R.g:414:26: ( EXPONENT )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0=='E'||LA10_0=='e') ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // R.g:414:26: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }

                    match('L'); 
                    setText(getText().substring(0, getText().length()-1));

                    }
                    break;
                case 3 :
                    // R.g:415:9: '0x' ( HEX_DIGIT )+ 'L'
                    {
                    match("0x"); 

                    // R.g:415:14: ( HEX_DIGIT )+
                    int cnt11=0;
                    loop11:
                    do {
                        int alt11=2;
                        int LA11_0 = input.LA(1);

                        if ( ((LA11_0>='0' && LA11_0<='9')||(LA11_0>='A' && LA11_0<='F')||(LA11_0>='a' && LA11_0<='f')) ) {
                            alt11=1;
                        }


                        switch (alt11) {
                    	case 1 :
                    	    // R.g:415:14: HEX_DIGIT
                    	    {
                    	    mHEX_DIGIT(); 

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

                    match('L'); 
                    setText(getText().substring(0, getText().length()-1));

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "INTEGER"

    // $ANTLR start "COMPLEX"
    public final void mCOMPLEX() throws RecognitionException {
        try {
            int _type = COMPLEX;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:418:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? 'i' | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'i' | '0x' HEX_DIGIT 'i' )
            int alt19=3;
            alt19 = dfa19.predict(input);
            switch (alt19) {
                case 1 :
                    // R.g:418:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? 'i'
                    {
                    // R.g:418:9: ( '0' .. '9' )+
                    int cnt13=0;
                    loop13:
                    do {
                        int alt13=2;
                        int LA13_0 = input.LA(1);

                        if ( ((LA13_0>='0' && LA13_0<='9')) ) {
                            alt13=1;
                        }


                        switch (alt13) {
                    	case 1 :
                    	    // R.g:418:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt13 >= 1 ) break loop13;
                                EarlyExitException eee =
                                    new EarlyExitException(13, input);
                                throw eee;
                        }
                        cnt13++;
                    } while (true);

                    match('.'); 
                    // R.g:418:25: ( '0' .. '9' )*
                    loop14:
                    do {
                        int alt14=2;
                        int LA14_0 = input.LA(1);

                        if ( ((LA14_0>='0' && LA14_0<='9')) ) {
                            alt14=1;
                        }


                        switch (alt14) {
                    	case 1 :
                    	    // R.g:418:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);

                    // R.g:418:37: ( EXPONENT )?
                    int alt15=2;
                    int LA15_0 = input.LA(1);

                    if ( (LA15_0=='E'||LA15_0=='e') ) {
                        alt15=1;
                    }
                    switch (alt15) {
                        case 1 :
                            // R.g:418:37: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }

                    match('i'); 
                    setText(getText().substring(0, getText().length()-1));

                    }
                    break;
                case 2 :
                    // R.g:419:9: ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'i'
                    {
                    // R.g:419:9: ( '.' )?
                    int alt16=2;
                    int LA16_0 = input.LA(1);

                    if ( (LA16_0=='.') ) {
                        alt16=1;
                    }
                    switch (alt16) {
                        case 1 :
                            // R.g:419:9: '.'
                            {
                            match('.'); 

                            }
                            break;

                    }

                    // R.g:419:14: ( '0' .. '9' )+
                    int cnt17=0;
                    loop17:
                    do {
                        int alt17=2;
                        int LA17_0 = input.LA(1);

                        if ( ((LA17_0>='0' && LA17_0<='9')) ) {
                            alt17=1;
                        }


                        switch (alt17) {
                    	case 1 :
                    	    // R.g:419:15: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

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

                    // R.g:419:26: ( EXPONENT )?
                    int alt18=2;
                    int LA18_0 = input.LA(1);

                    if ( (LA18_0=='E'||LA18_0=='e') ) {
                        alt18=1;
                    }
                    switch (alt18) {
                        case 1 :
                            // R.g:419:26: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }

                    match('i'); 
                    setText(getText().substring(0, getText().length()-1));

                    }
                    break;
                case 3 :
                    // R.g:420:9: '0x' HEX_DIGIT 'i'
                    {
                    match("0x"); 

                    mHEX_DIGIT(); 
                    match('i'); 
                    setText(getText().substring(0, getText().length()-1));

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "COMPLEX"

    // $ANTLR start "DOUBLE"
    public final void mDOUBLE() throws RecognitionException {
        try {
            int _type = DOUBLE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:423:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? | '0x' HEX_DIGIT )
            int alt26=3;
            alt26 = dfa26.predict(input);
            switch (alt26) {
                case 1 :
                    // R.g:423:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
                    {
                    // R.g:423:9: ( '0' .. '9' )+
                    int cnt20=0;
                    loop20:
                    do {
                        int alt20=2;
                        int LA20_0 = input.LA(1);

                        if ( ((LA20_0>='0' && LA20_0<='9')) ) {
                            alt20=1;
                        }


                        switch (alt20) {
                    	case 1 :
                    	    // R.g:423:10: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt20 >= 1 ) break loop20;
                                EarlyExitException eee =
                                    new EarlyExitException(20, input);
                                throw eee;
                        }
                        cnt20++;
                    } while (true);

                    match('.'); 
                    // R.g:423:25: ( '0' .. '9' )*
                    loop21:
                    do {
                        int alt21=2;
                        int LA21_0 = input.LA(1);

                        if ( ((LA21_0>='0' && LA21_0<='9')) ) {
                            alt21=1;
                        }


                        switch (alt21) {
                    	case 1 :
                    	    // R.g:423:26: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop21;
                        }
                    } while (true);

                    // R.g:423:37: ( EXPONENT )?
                    int alt22=2;
                    int LA22_0 = input.LA(1);

                    if ( (LA22_0=='E'||LA22_0=='e') ) {
                        alt22=1;
                    }
                    switch (alt22) {
                        case 1 :
                            // R.g:423:37: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 2 :
                    // R.g:424:9: ( '.' )? ( '0' .. '9' )+ ( EXPONENT )?
                    {
                    // R.g:424:9: ( '.' )?
                    int alt23=2;
                    int LA23_0 = input.LA(1);

                    if ( (LA23_0=='.') ) {
                        alt23=1;
                    }
                    switch (alt23) {
                        case 1 :
                            // R.g:424:9: '.'
                            {
                            match('.'); 

                            }
                            break;

                    }

                    // R.g:424:14: ( '0' .. '9' )+
                    int cnt24=0;
                    loop24:
                    do {
                        int alt24=2;
                        int LA24_0 = input.LA(1);

                        if ( ((LA24_0>='0' && LA24_0<='9')) ) {
                            alt24=1;
                        }


                        switch (alt24) {
                    	case 1 :
                    	    // R.g:424:15: '0' .. '9'
                    	    {
                    	    matchRange('0','9'); 

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt24 >= 1 ) break loop24;
                                EarlyExitException eee =
                                    new EarlyExitException(24, input);
                                throw eee;
                        }
                        cnt24++;
                    } while (true);

                    // R.g:424:26: ( EXPONENT )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0=='E'||LA25_0=='e') ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // R.g:424:26: EXPONENT
                            {
                            mEXPONENT(); 

                            }
                            break;

                    }


                    }
                    break;
                case 3 :
                    // R.g:425:7: '0x' HEX_DIGIT
                    {
                    match("0x"); 

                    mHEX_DIGIT(); 

                    }
                    break;

            }
            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLE"

    // $ANTLR start "DD"
    public final void mDD() throws RecognitionException {
        try {
            int _type = DD;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:427:4: ( '..' ( '0' .. '9' )+ )
            // R.g:427:6: '..' ( '0' .. '9' )+
            {
            match(".."); 

            // R.g:427:11: ( '0' .. '9' )+
            int cnt27=0;
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( ((LA27_0>='0' && LA27_0<='9')) ) {
                    alt27=1;
                }


                switch (alt27) {
            	case 1 :
            	    // R.g:427:12: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt27 >= 1 ) break loop27;
                        EarlyExitException eee =
                            new EarlyExitException(27, input);
                        throw eee;
                }
                cnt27++;
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "DD"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:429:5: ( ( '.' )* ID_NAME | '.' | '`' ( ESC_SEQ | ~ ( '\\\\' | '`' ) )* '`' )
            int alt30=3;
            switch ( input.LA(1) ) {
            case '.':
                {
                int LA30_1 = input.LA(2);

                if ( (LA30_1=='.'||(LA30_1>='A' && LA30_1<='Z')||LA30_1=='_'||(LA30_1>='a' && LA30_1<='z')) ) {
                    alt30=1;
                }
                else {
                    alt30=2;}
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
                alt30=1;
                }
                break;
            case '`':
                {
                alt30=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // R.g:429:7: ( '.' )* ID_NAME
                    {
                    // R.g:429:7: ( '.' )*
                    loop28:
                    do {
                        int alt28=2;
                        int LA28_0 = input.LA(1);

                        if ( (LA28_0=='.') ) {
                            alt28=1;
                        }


                        switch (alt28) {
                    	case 1 :
                    	    // R.g:429:7: '.'
                    	    {
                    	    match('.'); 

                    	    }
                    	    break;

                    	default :
                    	    break loop28;
                        }
                    } while (true);

                    mID_NAME(); 

                    }
                    break;
                case 2 :
                    // R.g:430:4: '.'
                    {
                    match('.'); 

                    }
                    break;
                case 3 :
                    // R.g:431:4: '`' ( ESC_SEQ | ~ ( '\\\\' | '`' ) )* '`'
                    {
                    match('`'); 
                    // R.g:431:8: ( ESC_SEQ | ~ ( '\\\\' | '`' ) )*
                    loop29:
                    do {
                        int alt29=3;
                        int LA29_0 = input.LA(1);

                        if ( (LA29_0=='\\') ) {
                            alt29=1;
                        }
                        else if ( ((LA29_0>='\u0000' && LA29_0<='[')||(LA29_0>=']' && LA29_0<='_')||(LA29_0>='a' && LA29_0<='\uFFFF')) ) {
                            alt29=2;
                        }


                        switch (alt29) {
                    	case 1 :
                    	    // R.g:431:10: ESC_SEQ
                    	    {
                    	    mESC_SEQ(); 

                    	    }
                    	    break;
                    	case 2 :
                    	    // R.g:431:20: ~ ( '\\\\' | '`' )
                    	    {
                    	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='_')||(input.LA(1)>='a' && input.LA(1)<='\uFFFF') ) {
                    	        input.consume();

                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;}


                    	    }
                    	    break;

                    	default :
                    	    break loop29;
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
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "OP"
    public final void mOP() throws RecognitionException {
        try {
            int _type = OP;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // R.g:433:4: ( '%' ( OP_NAME )+ '%' )
            // R.g:433:6: '%' ( OP_NAME )+ '%'
            {
            match('%'); 
            // R.g:433:10: ( OP_NAME )+
            int cnt31=0;
            loop31:
            do {
                int alt31=2;
                int LA31_0 = input.LA(1);

                if ( (LA31_0=='&'||(LA31_0>='*' && LA31_0<='/')||LA31_0==':'||(LA31_0>='<' && LA31_0<='>')||(LA31_0>='A' && LA31_0<='Z')||(LA31_0>='^' && LA31_0<='_')||(LA31_0>='a' && LA31_0<='z')||LA31_0=='|'||LA31_0=='~') ) {
                    alt31=1;
                }


                switch (alt31) {
            	case 1 :
            	    // R.g:433:10: OP_NAME
            	    {
            	    mOP_NAME(); 

            	    }
            	    break;

            	default :
            	    if ( cnt31 >= 1 ) break loop31;
                        EarlyExitException eee =
                            new EarlyExitException(31, input);
                        throw eee;
                }
                cnt31++;
            } while (true);

            match('%'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "OP"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            int i;

             final StringBuilder buf = new StringBuilder(); 
            // R.g:445:1: ( '\"' ( ESCAPE[buf] | i=~ ( '\\\\' | '\"' ) )* '\"' )
            // R.g:446:5: '\"' ( ESCAPE[buf] | i=~ ( '\\\\' | '\"' ) )* '\"'
            {
            match('\"'); 
            // R.g:447:5: ( ESCAPE[buf] | i=~ ( '\\\\' | '\"' ) )*
            loop32:
            do {
                int alt32=3;
                int LA32_0 = input.LA(1);

                if ( (LA32_0=='\\') ) {
                    alt32=1;
                }
                else if ( ((LA32_0>='\u0000' && LA32_0<='!')||(LA32_0>='#' && LA32_0<='[')||(LA32_0>=']' && LA32_0<='\uFFFF')) ) {
                    alt32=2;
                }


                switch (alt32) {
            	case 1 :
            	    // R.g:448:5: ESCAPE[buf]
            	    {
            	    mESCAPE(buf); 

            	    }
            	    break;
            	case 2 :
            	    // R.g:449:7: i=~ ( '\\\\' | '\"' )
            	    {
            	    i= input.LA(1);
            	    if ( (input.LA(1)>='\u0000' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='\uFFFF') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}

            	     buf.appendCodePoint(i); 

            	    }
            	    break;

            	default :
            	    break loop32;
                }
            } while (true);

            match('\"'); 
             setText(buf.toString()); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "ESCAPE"
    public final void mESCAPE(StringBuilder buf) throws RecognitionException {
        try {
            CommonToken a=null;
            CommonToken b=null;
            CommonToken c=null;
            CommonToken d=null;
            CommonToken e=null;
            CommonToken f=null;
            CommonToken g=null;
            CommonToken h=null;

            // R.g:455:36: ( '\\\\' ( 't' | 'n' | 'r' | 'b' | 'f' | '\"' | '\\\\' | 'x' a= HEX_DIGIT b= HEX_DIGIT | 'u' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT | 'U' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT e= HEX_DIGIT f= HEX_DIGIT g= HEX_DIGIT h= HEX_DIGIT ) )
            // R.g:456:5: '\\\\' ( 't' | 'n' | 'r' | 'b' | 'f' | '\"' | '\\\\' | 'x' a= HEX_DIGIT b= HEX_DIGIT | 'u' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT | 'U' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT e= HEX_DIGIT f= HEX_DIGIT g= HEX_DIGIT h= HEX_DIGIT )
            {
            match('\\'); 
            // R.g:457:5: ( 't' | 'n' | 'r' | 'b' | 'f' | '\"' | '\\\\' | 'x' a= HEX_DIGIT b= HEX_DIGIT | 'u' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT | 'U' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT e= HEX_DIGIT f= HEX_DIGIT g= HEX_DIGIT h= HEX_DIGIT )
            int alt33=10;
            switch ( input.LA(1) ) {
            case 't':
                {
                alt33=1;
                }
                break;
            case 'n':
                {
                alt33=2;
                }
                break;
            case 'r':
                {
                alt33=3;
                }
                break;
            case 'b':
                {
                alt33=4;
                }
                break;
            case 'f':
                {
                alt33=5;
                }
                break;
            case '\"':
                {
                alt33=6;
                }
                break;
            case '\\':
                {
                alt33=7;
                }
                break;
            case 'x':
                {
                alt33=8;
                }
                break;
            case 'u':
                {
                alt33=9;
                }
                break;
            case 'U':
                {
                alt33=10;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }

            switch (alt33) {
                case 1 :
                    // R.g:457:7: 't'
                    {
                    match('t'); 
                     buf.append('\t'); 

                    }
                    break;
                case 2 :
                    // R.g:458:7: 'n'
                    {
                    match('n'); 
                     buf.append('\n'); 

                    }
                    break;
                case 3 :
                    // R.g:459:7: 'r'
                    {
                    match('r'); 
                     buf.append('\r'); 

                    }
                    break;
                case 4 :
                    // R.g:460:7: 'b'
                    {
                    match('b'); 
                     buf.append('\b'); 

                    }
                    break;
                case 5 :
                    // R.g:461:7: 'f'
                    {
                    match('f'); 
                     buf.append('\f'); 

                    }
                    break;
                case 6 :
                    // R.g:462:7: '\"'
                    {
                    match('\"'); 
                     buf.append('\"'); 

                    }
                    break;
                case 7 :
                    // R.g:463:7: '\\\\'
                    {
                    match('\\'); 
                     buf.append('\\'); 

                    }
                    break;
                case 8 :
                    // R.g:464:7: 'x' a= HEX_DIGIT b= HEX_DIGIT
                    {
                    match('x'); 
                    int aStart1135 = getCharIndex();
                    mHEX_DIGIT(); 
                    a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart1135, getCharIndex()-1);
                    int bStart1141 = getCharIndex();
                    mHEX_DIGIT(); 
                    b = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, bStart1141, getCharIndex()-1);
                     buf.append(ParseUtil.hexChar((a!=null?a.getText():null), (b!=null?b.getText():null))); 

                    }
                    break;
                case 9 :
                    // R.g:465:7: 'u' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT
                    {
                    match('u'); 
                    int aStart1157 = getCharIndex();
                    mHEX_DIGIT(); 
                    a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart1157, getCharIndex()-1);
                    int bStart1163 = getCharIndex();
                    mHEX_DIGIT(); 
                    b = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, bStart1163, getCharIndex()-1);
                    int cStart1169 = getCharIndex();
                    mHEX_DIGIT(); 
                    c = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, cStart1169, getCharIndex()-1);
                    int dStart1175 = getCharIndex();
                    mHEX_DIGIT(); 
                    d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart1175, getCharIndex()-1);
                     buf.append(ParseUtil.hexChar((a!=null?a.getText():null), (b!=null?b.getText():null), (c!=null?c.getText():null), (d!=null?d.getText():null))); 

                    }
                    break;
                case 10 :
                    // R.g:466:7: 'U' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT e= HEX_DIGIT f= HEX_DIGIT g= HEX_DIGIT h= HEX_DIGIT
                    {
                    match('U'); 
                    int aStart1191 = getCharIndex();
                    mHEX_DIGIT(); 
                    a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart1191, getCharIndex()-1);
                    int bStart1197 = getCharIndex();
                    mHEX_DIGIT(); 
                    b = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, bStart1197, getCharIndex()-1);
                    int cStart1203 = getCharIndex();
                    mHEX_DIGIT(); 
                    c = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, cStart1203, getCharIndex()-1);
                    int dStart1209 = getCharIndex();
                    mHEX_DIGIT(); 
                    d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart1209, getCharIndex()-1);
                    int eStart1215 = getCharIndex();
                    mHEX_DIGIT(); 
                    e = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, eStart1215, getCharIndex()-1);
                    int fStart1221 = getCharIndex();
                    mHEX_DIGIT(); 
                    f = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, fStart1221, getCharIndex()-1);
                    int gStart1227 = getCharIndex();
                    mHEX_DIGIT(); 
                    g = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, gStart1227, getCharIndex()-1);
                    int hStart1233 = getCharIndex();
                    mHEX_DIGIT(); 
                    h = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, hStart1233, getCharIndex()-1);
                     buf.append(ParseUtil.hexChar((a!=null?a.getText():null), (b!=null?b.getText():null), (c!=null?c.getText():null), (d!=null?d.getText():null), (e!=null?e.getText():null), (f!=null?f.getText():null), (g!=null?g.getText():null), (h!=null?h.getText():null))); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "ESCAPE"

    // $ANTLR start "LINE_BREAK"
    public final void mLINE_BREAK() throws RecognitionException {
        try {
            // R.g:470:2: ( ( ( '\\f' | '\\r' )? '\\n' ) | ( ( '\\n' )? ( '\\r' | '\\f' ) ) )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( ((LA36_0>='\f' && LA36_0<='\r')) ) {
                int LA36_1 = input.LA(2);

                if ( (LA36_1=='\n') ) {
                    alt36=1;
                }
                else {
                    alt36=2;}
            }
            else if ( (LA36_0=='\n') ) {
                int LA36_2 = input.LA(2);

                if ( ((LA36_2>='\f' && LA36_2<='\r')) ) {
                    alt36=2;
                }
                else {
                    alt36=1;}
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;
            }
            switch (alt36) {
                case 1 :
                    // R.g:471:3: ( ( '\\f' | '\\r' )? '\\n' )
                    {
                    // R.g:471:3: ( ( '\\f' | '\\r' )? '\\n' )
                    // R.g:471:4: ( '\\f' | '\\r' )? '\\n'
                    {
                    // R.g:471:4: ( '\\f' | '\\r' )?
                    int alt34=2;
                    int LA34_0 = input.LA(1);

                    if ( ((LA34_0>='\f' && LA34_0<='\r')) ) {
                        alt34=1;
                    }
                    switch (alt34) {
                        case 1 :
                            // R.g:
                            {
                            if ( (input.LA(1)>='\f' && input.LA(1)<='\r') ) {
                                input.consume();

                            }
                            else {
                                MismatchedSetException mse = new MismatchedSetException(null,input);
                                recover(mse);
                                throw mse;}


                            }
                            break;

                    }

                    match('\n'); 

                    }


                    }
                    break;
                case 2 :
                    // R.g:472:4: ( ( '\\n' )? ( '\\r' | '\\f' ) )
                    {
                    // R.g:472:4: ( ( '\\n' )? ( '\\r' | '\\f' ) )
                    // R.g:472:5: ( '\\n' )? ( '\\r' | '\\f' )
                    {
                    // R.g:472:5: ( '\\n' )?
                    int alt35=2;
                    int LA35_0 = input.LA(1);

                    if ( (LA35_0=='\n') ) {
                        alt35=1;
                    }
                    switch (alt35) {
                        case 1 :
                            // R.g:472:5: '\\n'
                            {
                            match('\n'); 

                            }
                            break;

                    }

                    if ( (input.LA(1)>='\f' && input.LA(1)<='\r') ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "LINE_BREAK"

    // $ANTLR start "EXPONENT"
    public final void mEXPONENT() throws RecognitionException {
        try {
            // R.g:476:2: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
            // R.g:476:4: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
            {
            if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // R.g:476:14: ( '+' | '-' )?
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0=='+'||LA37_0=='-') ) {
                alt37=1;
            }
            switch (alt37) {
                case 1 :
                    // R.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }

            // R.g:476:25: ( '0' .. '9' )+
            int cnt38=0;
            loop38:
            do {
                int alt38=2;
                int LA38_0 = input.LA(1);

                if ( ((LA38_0>='0' && LA38_0<='9')) ) {
                    alt38=1;
                }


                switch (alt38) {
            	case 1 :
            	    // R.g:476:26: '0' .. '9'
            	    {
            	    matchRange('0','9'); 

            	    }
            	    break;

            	default :
            	    if ( cnt38 >= 1 ) break loop38;
                        EarlyExitException eee =
                            new EarlyExitException(38, input);
                        throw eee;
                }
                cnt38++;
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "OP_NAME"
    public final void mOP_NAME() throws RecognitionException {
        try {
            // R.g:480:2: ( ID_NAME | ( '*' | '/' | '+' | '-' | '>' | '<' | '=' | '|' | '&' | ':' | '^' | '.' | '~' | ',' ) )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( ((LA39_0>='A' && LA39_0<='Z')||LA39_0=='_'||(LA39_0>='a' && LA39_0<='z')) ) {
                alt39=1;
            }
            else if ( (LA39_0=='&'||(LA39_0>='*' && LA39_0<='/')||LA39_0==':'||(LA39_0>='<' && LA39_0<='>')||LA39_0=='^'||LA39_0=='|'||LA39_0=='~') ) {
                alt39=2;
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;
            }
            switch (alt39) {
                case 1 :
                    // R.g:480:4: ID_NAME
                    {
                    mID_NAME(); 

                    }
                    break;
                case 2 :
                    // R.g:481:4: ( '*' | '/' | '+' | '-' | '>' | '<' | '=' | '|' | '&' | ':' | '^' | '.' | '~' | ',' )
                    {
                    if ( input.LA(1)=='&'||(input.LA(1)>='*' && input.LA(1)<='/')||input.LA(1)==':'||(input.LA(1)>='<' && input.LA(1)<='>')||input.LA(1)=='^'||input.LA(1)=='|'||input.LA(1)=='~' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "OP_NAME"

    // $ANTLR start "ID_NAME"
    public final void mID_NAME() throws RecognitionException {
        try {
            // R.g:485:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )* )
            // R.g:485:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )*
            {
            if ( (input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}

            // R.g:485:28: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )*
            loop40:
            do {
                int alt40=2;
                int LA40_0 = input.LA(1);

                if ( (LA40_0=='.'||(LA40_0>='0' && LA40_0<='9')||(LA40_0>='A' && LA40_0<='Z')||LA40_0=='_'||(LA40_0>='a' && LA40_0<='z')) ) {
                    alt40=1;
                }


                switch (alt40) {
            	case 1 :
            	    // R.g:
            	    {
            	    if ( input.LA(1)=='.'||(input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z') ) {
            	        input.consume();

            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;}


            	    }
            	    break;

            	default :
            	    break loop40;
                }
            } while (true);


            }

        }
        finally {
        }
    }
    // $ANTLR end "ID_NAME"

    // $ANTLR start "ESC_SEQ"
    public final void mESC_SEQ() throws RecognitionException {
        try {
            // R.g:489:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '`' | '\\\\' | ' ' | 'a' | 'v' ) | '\\\\' LINE_BREAK | UNICODE_ESC | OCTAL_ESC | HEX_ESC )
            int alt41=5;
            int LA41_0 = input.LA(1);

            if ( (LA41_0=='\\') ) {
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
                    alt41=1;
                    }
                    break;
                case 'u':
                    {
                    alt41=3;
                    }
                    break;
                case 'x':
                    {
                    alt41=5;
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
                    alt41=4;
                    }
                    break;
                case '\n':
                case '\f':
                case '\r':
                    {
                    alt41=2;
                    }
                    break;
                default:
                    NoViableAltException nvae =
                        new NoViableAltException("", 41, 1, input);

                    throw nvae;
                }

            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;
            }
            switch (alt41) {
                case 1 :
                    // R.g:489:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '`' | '\\\\' | ' ' | 'a' | 'v' )
                    {
                    match('\\'); 
                    if ( input.LA(1)==' '||input.LA(1)=='\"'||input.LA(1)=='\''||input.LA(1)=='\\'||(input.LA(1)>='`' && input.LA(1)<='b')||input.LA(1)=='f'||input.LA(1)=='n'||input.LA(1)=='r'||input.LA(1)=='t'||input.LA(1)=='v' ) {
                        input.consume();

                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;}


                    }
                    break;
                case 2 :
                    // R.g:490:7: '\\\\' LINE_BREAK
                    {
                    match('\\'); 
                    mLINE_BREAK(); 

                    }
                    break;
                case 3 :
                    // R.g:491:9: UNICODE_ESC
                    {
                    mUNICODE_ESC(); 

                    }
                    break;
                case 4 :
                    // R.g:492:9: OCTAL_ESC
                    {
                    mOCTAL_ESC(); 

                    }
                    break;
                case 5 :
                    // R.g:493:7: HEX_ESC
                    {
                    mHEX_ESC(); 

                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "ESC_SEQ"

    // $ANTLR start "UNICODE_ESC"
    public final void mUNICODE_ESC() throws RecognitionException {
        try {
            // R.g:497:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
            // R.g:497:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
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
        }
    }
    // $ANTLR end "UNICODE_ESC"

    // $ANTLR start "HEX_ESC"
    public final void mHEX_ESC() throws RecognitionException {
        try {
            // R.g:501:2: ( '\\\\x' HEX_DIGIT ( HEX_DIGIT )? )
            // R.g:501:4: '\\\\x' HEX_DIGIT ( HEX_DIGIT )?
            {
            match("\\x"); 

            mHEX_DIGIT(); 
            // R.g:501:20: ( HEX_DIGIT )?
            int alt42=2;
            int LA42_0 = input.LA(1);

            if ( ((LA42_0>='0' && LA42_0<='9')||(LA42_0>='A' && LA42_0<='F')||(LA42_0>='a' && LA42_0<='f')) ) {
                alt42=1;
            }
            switch (alt42) {
                case 1 :
                    // R.g:501:20: HEX_DIGIT
                    {
                    mHEX_DIGIT(); 

                    }
                    break;

            }


            }

        }
        finally {
        }
    }
    // $ANTLR end "HEX_ESC"

    // $ANTLR start "HEX_DIGIT"
    public final void mHEX_DIGIT() throws RecognitionException {
        try {
            // R.g:505:2: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
            // R.g:505:4: ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' )
            {
            if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='F')||(input.LA(1)>='a' && input.LA(1)<='f') ) {
                input.consume();

            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;}


            }

        }
        finally {
        }
    }
    // $ANTLR end "HEX_DIGIT"

    // $ANTLR start "OCTAL_ESC"
    public final void mOCTAL_ESC() throws RecognitionException {
        try {
            // R.g:509:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
            int alt43=3;
            int LA43_0 = input.LA(1);

            if ( (LA43_0=='\\') ) {
                int LA43_1 = input.LA(2);

                if ( ((LA43_1>='0' && LA43_1<='3')) ) {
                    int LA43_2 = input.LA(3);

                    if ( ((LA43_2>='0' && LA43_2<='7')) ) {
                        int LA43_5 = input.LA(4);

                        if ( ((LA43_5>='0' && LA43_5<='7')) ) {
                            alt43=1;
                        }
                        else {
                            alt43=2;}
                    }
                    else {
                        alt43=3;}
                }
                else if ( ((LA43_1>='4' && LA43_1<='7')) ) {
                    int LA43_3 = input.LA(3);

                    if ( ((LA43_3>='0' && LA43_3<='7')) ) {
                        alt43=2;
                    }
                    else {
                        alt43=3;}
                }
                else {
                    NoViableAltException nvae =
                        new NoViableAltException("", 43, 1, input);

                    throw nvae;
                }
            }
            else {
                NoViableAltException nvae =
                    new NoViableAltException("", 43, 0, input);

                throw nvae;
            }
            switch (alt43) {
                case 1 :
                    // R.g:509:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // R.g:509:14: ( '0' .. '3' )
                    // R.g:509:15: '0' .. '3'
                    {
                    matchRange('0','3'); 

                    }

                    // R.g:509:25: ( '0' .. '7' )
                    // R.g:509:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // R.g:509:36: ( '0' .. '7' )
                    // R.g:509:37: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 2 :
                    // R.g:510:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
                    {
                    match('\\'); 
                    // R.g:510:14: ( '0' .. '7' )
                    // R.g:510:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }

                    // R.g:510:25: ( '0' .. '7' )
                    // R.g:510:26: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;
                case 3 :
                    // R.g:511:9: '\\\\' ( '0' .. '7' )
                    {
                    match('\\'); 
                    // R.g:511:14: ( '0' .. '7' )
                    // R.g:511:15: '0' .. '7'
                    {
                    matchRange('0','7'); 

                    }


                    }
                    break;

            }
        }
        finally {
        }
    }
    // $ANTLR end "OCTAL_ESC"

    @Override
    public void mTokens() throws RecognitionException {
        // R.g:1:8: ( T__84 | COMMENT | ARROW | SUPER_ARROW | RIGHT_ARROW | SUPER_RIGHT_ARROW | VARIATIC | EQ | NE | GE | LE | GT | LT | ASSIGN | NS_GET_INT | NS_GET | COLON | SEMICOLON | COMMA | AND | BITWISEAND | OR | BITWISEOR | LBRACE | RBRACE | LPAR | RPAR | LBB | LBRAKET | RBRAKET | CARRET | TILDE | MOD | NOT | PLUS | MULT | DIV | MINUS | FIELD | AT | FUNCTION | NULL | NA | TRUE | FALSE | WHILE | FOR | REPEAT | IN | IF | ELSE | NEXT | BREAK | WS | NEWLINE | INTEGER | COMPLEX | DOUBLE | DD | ID | OP | STRING )
        int alt44=62;
        alt44 = dfa44.predict(input);
        switch (alt44) {
            case 1 :
                // R.g:1:10: T__84
                {
                mT__84(); 

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
                // R.g:1:124: COLON
                {
                mCOLON(); 

                }
                break;
            case 18 :
                // R.g:1:130: SEMICOLON
                {
                mSEMICOLON(); 

                }
                break;
            case 19 :
                // R.g:1:140: COMMA
                {
                mCOMMA(); 

                }
                break;
            case 20 :
                // R.g:1:146: AND
                {
                mAND(); 

                }
                break;
            case 21 :
                // R.g:1:150: BITWISEAND
                {
                mBITWISEAND(); 

                }
                break;
            case 22 :
                // R.g:1:161: OR
                {
                mOR(); 

                }
                break;
            case 23 :
                // R.g:1:164: BITWISEOR
                {
                mBITWISEOR(); 

                }
                break;
            case 24 :
                // R.g:1:174: LBRACE
                {
                mLBRACE(); 

                }
                break;
            case 25 :
                // R.g:1:181: RBRACE
                {
                mRBRACE(); 

                }
                break;
            case 26 :
                // R.g:1:188: LPAR
                {
                mLPAR(); 

                }
                break;
            case 27 :
                // R.g:1:193: RPAR
                {
                mRPAR(); 

                }
                break;
            case 28 :
                // R.g:1:198: LBB
                {
                mLBB(); 

                }
                break;
            case 29 :
                // R.g:1:202: LBRAKET
                {
                mLBRAKET(); 

                }
                break;
            case 30 :
                // R.g:1:210: RBRAKET
                {
                mRBRAKET(); 

                }
                break;
            case 31 :
                // R.g:1:218: CARRET
                {
                mCARRET(); 

                }
                break;
            case 32 :
                // R.g:1:225: TILDE
                {
                mTILDE(); 

                }
                break;
            case 33 :
                // R.g:1:231: MOD
                {
                mMOD(); 

                }
                break;
            case 34 :
                // R.g:1:235: NOT
                {
                mNOT(); 

                }
                break;
            case 35 :
                // R.g:1:239: PLUS
                {
                mPLUS(); 

                }
                break;
            case 36 :
                // R.g:1:244: MULT
                {
                mMULT(); 

                }
                break;
            case 37 :
                // R.g:1:249: DIV
                {
                mDIV(); 

                }
                break;
            case 38 :
                // R.g:1:253: MINUS
                {
                mMINUS(); 

                }
                break;
            case 39 :
                // R.g:1:259: FIELD
                {
                mFIELD(); 

                }
                break;
            case 40 :
                // R.g:1:265: AT
                {
                mAT(); 

                }
                break;
            case 41 :
                // R.g:1:268: FUNCTION
                {
                mFUNCTION(); 

                }
                break;
            case 42 :
                // R.g:1:277: NULL
                {
                mNULL(); 

                }
                break;
            case 43 :
                // R.g:1:282: NA
                {
                mNA(); 

                }
                break;
            case 44 :
                // R.g:1:285: TRUE
                {
                mTRUE(); 

                }
                break;
            case 45 :
                // R.g:1:290: FALSE
                {
                mFALSE(); 

                }
                break;
            case 46 :
                // R.g:1:296: WHILE
                {
                mWHILE(); 

                }
                break;
            case 47 :
                // R.g:1:302: FOR
                {
                mFOR(); 

                }
                break;
            case 48 :
                // R.g:1:306: REPEAT
                {
                mREPEAT(); 

                }
                break;
            case 49 :
                // R.g:1:313: IN
                {
                mIN(); 

                }
                break;
            case 50 :
                // R.g:1:316: IF
                {
                mIF(); 

                }
                break;
            case 51 :
                // R.g:1:319: ELSE
                {
                mELSE(); 

                }
                break;
            case 52 :
                // R.g:1:324: NEXT
                {
                mNEXT(); 

                }
                break;
            case 53 :
                // R.g:1:329: BREAK
                {
                mBREAK(); 

                }
                break;
            case 54 :
                // R.g:1:335: WS
                {
                mWS(); 

                }
                break;
            case 55 :
                // R.g:1:338: NEWLINE
                {
                mNEWLINE(); 

                }
                break;
            case 56 :
                // R.g:1:346: INTEGER
                {
                mINTEGER(); 

                }
                break;
            case 57 :
                // R.g:1:354: COMPLEX
                {
                mCOMPLEX(); 

                }
                break;
            case 58 :
                // R.g:1:362: DOUBLE
                {
                mDOUBLE(); 

                }
                break;
            case 59 :
                // R.g:1:369: DD
                {
                mDD(); 

                }
                break;
            case 60 :
                // R.g:1:372: ID
                {
                mID(); 

                }
                break;
            case 61 :
                // R.g:1:375: OP
                {
                mOP(); 

                }
                break;
            case 62 :
                // R.g:1:378: STRING
                {
                mSTRING(); 

                }
                break;

        }

    }


    protected DFA12 dfa12 = new DFA12(this);
    protected DFA19 dfa19 = new DFA19(this);
    protected DFA26 dfa26 = new DFA26(this);
    protected DFA44 dfa44 = new DFA44(this);
    static final String DFA12_eotS =
        "\6\uffff";
    static final String DFA12_eofS =
        "\6\uffff";
    static final String DFA12_minS =
        "\2\56\1\uffff\1\56\2\uffff";
    static final String DFA12_maxS =
        "\1\71\1\170\1\uffff\1\145\2\uffff";
    static final String DFA12_acceptS =
        "\2\uffff\1\2\1\uffff\1\3\1\1";
    static final String DFA12_specialS =
        "\6\uffff}>";
    static final String[] DFA12_transitionS = {
            "\1\2\1\uffff\1\1\11\3",
            "\1\5\1\uffff\12\3\13\uffff\1\2\6\uffff\1\2\30\uffff\1\2\22"+
            "\uffff\1\4",
            "",
            "\1\5\1\uffff\12\3\13\uffff\1\2\6\uffff\1\2\30\uffff\1\2",
            "",
            ""
    };

    static final short[] DFA12_eot = DFA.unpackEncodedString(DFA12_eotS);
    static final short[] DFA12_eof = DFA.unpackEncodedString(DFA12_eofS);
    static final char[] DFA12_min = DFA.unpackEncodedStringToUnsignedChars(DFA12_minS);
    static final char[] DFA12_max = DFA.unpackEncodedStringToUnsignedChars(DFA12_maxS);
    static final short[] DFA12_accept = DFA.unpackEncodedString(DFA12_acceptS);
    static final short[] DFA12_special = DFA.unpackEncodedString(DFA12_specialS);
    static final short[][] DFA12_transition;

    static {
        int numStates = DFA12_transitionS.length;
        DFA12_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA12_transition[i] = DFA.unpackEncodedString(DFA12_transitionS[i]);
        }
    }

    class DFA12 extends DFA {

        public DFA12(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 12;
            this.eot = DFA12_eot;
            this.eof = DFA12_eof;
            this.min = DFA12_min;
            this.max = DFA12_max;
            this.accept = DFA12_accept;
            this.special = DFA12_special;
            this.transition = DFA12_transition;
        }
        @Override
        public String getDescription() {
            return "412:1: INTEGER : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* 'L' | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'L' | '0x' ( HEX_DIGIT )+ 'L' );";
        }
    }
    static final String DFA19_eotS =
        "\6\uffff";
    static final String DFA19_eofS =
        "\6\uffff";
    static final String DFA19_minS =
        "\2\56\1\uffff\1\56\2\uffff";
    static final String DFA19_maxS =
        "\1\71\1\170\1\uffff\1\151\2\uffff";
    static final String DFA19_acceptS =
        "\2\uffff\1\2\1\uffff\1\3\1\1";
    static final String DFA19_specialS =
        "\6\uffff}>";
    static final String[] DFA19_transitionS = {
            "\1\2\1\uffff\1\1\11\3",
            "\1\5\1\uffff\12\3\13\uffff\1\2\37\uffff\1\2\3\uffff\1\2\16"+
            "\uffff\1\4",
            "",
            "\1\5\1\uffff\12\3\13\uffff\1\2\37\uffff\1\2\3\uffff\1\2",
            "",
            ""
    };

    static final short[] DFA19_eot = DFA.unpackEncodedString(DFA19_eotS);
    static final short[] DFA19_eof = DFA.unpackEncodedString(DFA19_eofS);
    static final char[] DFA19_min = DFA.unpackEncodedStringToUnsignedChars(DFA19_minS);
    static final char[] DFA19_max = DFA.unpackEncodedStringToUnsignedChars(DFA19_maxS);
    static final short[] DFA19_accept = DFA.unpackEncodedString(DFA19_acceptS);
    static final short[] DFA19_special = DFA.unpackEncodedString(DFA19_specialS);
    static final short[][] DFA19_transition;

    static {
        int numStates = DFA19_transitionS.length;
        DFA19_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA19_transition[i] = DFA.unpackEncodedString(DFA19_transitionS[i]);
        }
    }

    class DFA19 extends DFA {

        public DFA19(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 19;
            this.eot = DFA19_eot;
            this.eof = DFA19_eof;
            this.min = DFA19_min;
            this.max = DFA19_max;
            this.accept = DFA19_accept;
            this.special = DFA19_special;
            this.transition = DFA19_transition;
        }
        @Override
        public String getDescription() {
            return "417:1: COMPLEX : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? 'i' | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'i' | '0x' HEX_DIGIT 'i' );";
        }
    }
    static final String DFA26_eotS =
        "\1\uffff\1\2\1\uffff\1\2\2\uffff";
    static final String DFA26_eofS =
        "\6\uffff";
    static final String DFA26_minS =
        "\2\56\1\uffff\1\56\2\uffff";
    static final String DFA26_maxS =
        "\1\71\1\170\1\uffff\1\71\2\uffff";
    static final String DFA26_acceptS =
        "\2\uffff\1\2\1\uffff\1\3\1\1";
    static final String DFA26_specialS =
        "\6\uffff}>";
    static final String[] DFA26_transitionS = {
            "\1\2\1\uffff\1\1\11\3",
            "\1\5\1\uffff\12\3\76\uffff\1\4",
            "",
            "\1\5\1\uffff\12\3",
            "",
            ""
    };

    static final short[] DFA26_eot = DFA.unpackEncodedString(DFA26_eotS);
    static final short[] DFA26_eof = DFA.unpackEncodedString(DFA26_eofS);
    static final char[] DFA26_min = DFA.unpackEncodedStringToUnsignedChars(DFA26_minS);
    static final char[] DFA26_max = DFA.unpackEncodedStringToUnsignedChars(DFA26_maxS);
    static final short[] DFA26_accept = DFA.unpackEncodedString(DFA26_acceptS);
    static final short[] DFA26_special = DFA.unpackEncodedString(DFA26_specialS);
    static final short[][] DFA26_transition;

    static {
        int numStates = DFA26_transitionS.length;
        DFA26_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA26_transition[i] = DFA.unpackEncodedString(DFA26_transitionS[i]);
        }
    }

    class DFA26 extends DFA {

        public DFA26(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 26;
            this.eot = DFA26_eot;
            this.eof = DFA26_eof;
            this.min = DFA26_min;
            this.max = DFA26_max;
            this.accept = DFA26_accept;
            this.special = DFA26_special;
            this.transition = DFA26_transition;
        }
        @Override
        public String getDescription() {
            return "422:1: DOUBLE : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? | '0x' HEX_DIGIT );";
        }
    }
    static final String DFA44_eotS =
        "\1\uffff\1\55\1\uffff\1\61\1\63\1\51\1\67\1\71\1\73\2\uffff\1\75"+
        "\1\77\4\uffff\1\101\2\uffff\1\102\6\uffff\12\51\2\uffff\2\125\3"+
        "\uffff\1\131\5\uffff\1\133\2\uffff\1\125\17\uffff\3\51\1\141\4\51"+
        "\1\146\1\147\3\51\4\uffff\1\125\5\uffff\1\160\1\uffff\1\51\1\162"+
        "\1\51\1\uffff\4\51\2\uffff\3\51\1\125\1\uffff\2\125\2\uffff\1\51"+
        "\1\uffff\1\176\1\177\3\51\1\u0083\1\u0084\1\51\1\uffff\1\125\1\51"+
        "\2\uffff\1\u0087\1\u0088\1\51\2\uffff\1\u008a\1\51\2\uffff\1\u008c"+
        "\1\uffff\1\51\1\uffff\1\u008e\1\uffff";
    static final String DFA44_eofS =
        "\u008f\uffff";
    static final String DFA44_minS =
        "\1\11\1\55\1\uffff\1\55\1\72\1\56\3\75\2\uffff\1\46\1\174\4\uffff"+
        "\1\133\2\uffff\1\52\1\uffff\1\45\4\uffff\1\157\1\101\1\122\1\101"+
        "\1\150\1\145\1\146\1\154\1\145\1\162\2\uffff\2\56\3\uffff\1\76\5"+
        "\uffff\1\72\1\uffff\1\56\1\60\17\uffff\1\156\1\162\1\114\1\56\1"+
        "\125\1\114\1\151\1\160\2\56\1\163\1\170\1\145\1\60\1\53\2\uffff"+
        "\1\60\5\uffff\1\56\1\uffff\1\143\1\56\1\114\1\uffff\1\105\1\123"+
        "\1\154\1\145\2\uffff\1\145\1\164\1\141\4\60\1\53\1\uffff\1\164\1"+
        "\uffff\2\56\1\105\1\145\1\141\2\56\1\153\2\60\1\151\2\uffff\2\56"+
        "\1\164\2\uffff\1\56\1\157\2\uffff\1\56\1\uffff\1\156\1\uffff\1\56"+
        "\1\uffff";
    static final String DFA44_maxS =
        "\1\176\1\76\1\uffff\2\75\1\71\3\75\2\uffff\1\46\1\174\4\uffff\1"+
        "\133\2\uffff\1\52\1\uffff\1\176\4\uffff\1\165\1\125\1\122\1\101"+
        "\1\150\1\145\1\156\1\154\1\145\1\162\2\uffff\1\170\1\151\3\uffff"+
        "\1\76\5\uffff\1\72\1\uffff\1\172\1\151\17\uffff\1\156\1\162\1\114"+
        "\1\172\1\125\1\114\1\151\1\160\2\172\1\163\1\170\1\145\1\146\1\71"+
        "\2\uffff\1\151\5\uffff\1\172\1\uffff\1\143\1\172\1\114\1\uffff\1"+
        "\105\1\123\1\154\1\145\2\uffff\1\145\1\164\1\141\1\151\1\71\2\151"+
        "\1\71\1\uffff\1\164\1\uffff\2\172\1\105\1\145\1\141\2\172\1\153"+
        "\1\71\2\151\2\uffff\2\172\1\164\2\uffff\1\172\1\157\2\uffff\1\172"+
        "\1\uffff\1\156\1\uffff\1\172\1\uffff";
    static final String DFA44_acceptS =
        "\2\uffff\1\2\6\uffff\1\22\1\23\2\uffff\1\30\1\31\1\32\1\33\1\uffff"+
        "\1\36\1\37\1\uffff\1\40\1\uffff\1\43\1\45\1\47\1\50\12\uffff\1\66"+
        "\1\67\2\uffff\1\74\1\76\1\1\1\uffff\1\46\1\3\1\4\1\13\1\15\1\uffff"+
        "\1\21\2\uffff\1\10\1\16\1\11\1\42\1\12\1\14\1\24\1\25\1\26\1\27"+
        "\1\34\1\35\1\44\1\41\1\75\17\uffff\1\71\1\72\1\uffff\1\70\1\6\1"+
        "\5\1\17\1\20\1\uffff\1\73\3\uffff\1\53\4\uffff\1\61\1\62\10\uffff"+
        "\1\7\1\uffff\1\57\13\uffff\1\52\1\54\3\uffff\1\63\1\64\2\uffff\1"+
        "\55\1\56\1\uffff\1\65\1\uffff\1\60\1\uffff\1\51";
    static final String DFA44_specialS =
        "\u008f\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\45\1\46\1\uffff\2\46\22\uffff\1\45\1\7\1\52\1\2\1\31\1\26"+
            "\1\13\1\uffff\1\17\1\20\1\24\1\27\1\12\1\1\1\5\1\30\1\47\11"+
            "\50\1\4\1\11\1\3\1\6\1\10\1\uffff\1\32\5\51\1\36\7\51\1\34\5"+
            "\51\1\35\6\51\1\21\1\uffff\1\22\1\23\3\51\1\44\2\51\1\42\1\33"+
            "\2\51\1\41\4\51\1\43\3\51\1\40\4\51\1\37\3\51\1\15\1\14\1\16"+
            "\1\25",
            "\1\53\20\uffff\1\54",
            "",
            "\1\56\16\uffff\1\57\1\60",
            "\1\62\2\uffff\1\56",
            "\1\64\1\uffff\12\65",
            "\1\66",
            "\1\70",
            "\1\72",
            "",
            "",
            "\1\74",
            "\1\76",
            "",
            "",
            "",
            "",
            "\1\100",
            "",
            "",
            "\1\23",
            "",
            "\1\103\1\104\3\uffff\6\104\12\uffff\1\104\1\uffff\3\104\2\uffff"+
            "\32\104\3\uffff\2\104\1\uffff\32\104\1\uffff\1\104\1\uffff\1"+
            "\104",
            "",
            "",
            "",
            "",
            "\1\106\5\uffff\1\105",
            "\1\110\23\uffff\1\107",
            "\1\111",
            "\1\112",
            "\1\113",
            "\1\114",
            "\1\116\7\uffff\1\115",
            "\1\117",
            "\1\120",
            "\1\121",
            "",
            "",
            "\1\126\1\uffff\12\50\13\uffff\1\123\6\uffff\1\127\30\uffff"+
            "\1\123\3\uffff\1\124\16\uffff\1\122",
            "\1\126\1\uffff\12\50\13\uffff\1\123\6\uffff\1\127\30\uffff"+
            "\1\123\3\uffff\1\124",
            "",
            "",
            "",
            "\1\130",
            "",
            "",
            "",
            "",
            "",
            "\1\132",
            "",
            "\1\134\1\uffff\12\135\7\uffff\32\51\4\uffff\1\51\1\uffff\32"+
            "\51",
            "\12\65\13\uffff\1\123\6\uffff\1\127\30\uffff\1\123\3\uffff"+
            "\1\124",
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
            "\1\136",
            "\1\137",
            "\1\140",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\142",
            "\1\143",
            "\1\144",
            "\1\145",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\150",
            "\1\151",
            "\1\152",
            "\12\153\7\uffff\6\153\32\uffff\6\153",
            "\1\154\1\uffff\1\154\2\uffff\12\155",
            "",
            "",
            "\12\156\13\uffff\1\157\6\uffff\1\127\30\uffff\1\157\3\uffff"+
            "\1\124",
            "",
            "",
            "",
            "",
            "",
            "\1\134\22\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "",
            "\1\161",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\163",
            "",
            "\1\164",
            "\1\165",
            "\1\166",
            "\1\167",
            "",
            "",
            "\1\170",
            "\1\171",
            "\1\172",
            "\12\127\7\uffff\6\127\5\uffff\1\127\24\uffff\6\127\2\uffff"+
            "\1\124",
            "\12\155",
            "\12\155\22\uffff\1\127\34\uffff\1\124",
            "\12\156\13\uffff\1\157\6\uffff\1\127\30\uffff\1\157\3\uffff"+
            "\1\124",
            "\1\173\1\uffff\1\173\2\uffff\12\174",
            "",
            "\1\175",
            "",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0080",
            "\1\u0081",
            "\1\u0082",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0085",
            "\12\174",
            "\12\174\57\uffff\1\124",
            "\1\u0086",
            "",
            "",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u0089",
            "",
            "",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "\1\u008b",
            "",
            "",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            "",
            "\1\u008d",
            "",
            "\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
            ""
    };

    static final short[] DFA44_eot = DFA.unpackEncodedString(DFA44_eotS);
    static final short[] DFA44_eof = DFA.unpackEncodedString(DFA44_eofS);
    static final char[] DFA44_min = DFA.unpackEncodedStringToUnsignedChars(DFA44_minS);
    static final char[] DFA44_max = DFA.unpackEncodedStringToUnsignedChars(DFA44_maxS);
    static final short[] DFA44_accept = DFA.unpackEncodedString(DFA44_acceptS);
    static final short[] DFA44_special = DFA.unpackEncodedString(DFA44_specialS);
    static final short[][] DFA44_transition;

    static {
        int numStates = DFA44_transitionS.length;
        DFA44_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA44_transition[i] = DFA.unpackEncodedString(DFA44_transitionS[i]);
        }
    }

    class DFA44 extends DFA {

        public DFA44(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 44;
            this.eot = DFA44_eot;
            this.eof = DFA44_eof;
            this.min = DFA44_min;
            this.max = DFA44_max;
            this.accept = DFA44_accept;
            this.special = DFA44_special;
            this.transition = DFA44_transition;
        }
        @Override
        public String getDescription() {
            return "1:1: Tokens : ( T__84 | COMMENT | ARROW | SUPER_ARROW | RIGHT_ARROW | SUPER_RIGHT_ARROW | VARIATIC | EQ | NE | GE | LE | GT | LT | ASSIGN | NS_GET_INT | NS_GET | COLON | SEMICOLON | COMMA | AND | BITWISEAND | OR | BITWISEOR | LBRACE | RBRACE | LPAR | RPAR | LBB | LBRAKET | RBRAKET | CARRET | TILDE | MOD | NOT | PLUS | MULT | DIV | MINUS | FIELD | AT | FUNCTION | NULL | NA | TRUE | FALSE | WHILE | FOR | REPEAT | IN | IF | ELSE | NEXT | BREAK | WS | NEWLINE | INTEGER | COMPLEX | DOUBLE | DD | ID | OP | STRING );";
        }
    }
 

}
