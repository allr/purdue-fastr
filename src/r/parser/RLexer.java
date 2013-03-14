// $ANTLR 3.5 R.g 2013-03-14 18:02:51

package r.parser;
//Checkstyle: stop


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
public class RLexer extends Lexer {
	public static final int EOF=-1;
	public static final int T__84=84;
	public static final int AND=4;
	public static final int ARROW=5;
	public static final int ASSIGN=6;
	public static final int AT=7;
	public static final int BRAKET=8;
	public static final int BREAK=9;
	public static final int CALL=10;
	public static final int CARRET=11;
	public static final int COLON=12;
	public static final int COMMA=13;
	public static final int COMMENT=14;
	public static final int COMPLEX=15;
	public static final int DD=16;
	public static final int DIV=17;
	public static final int DOUBLE=18;
	public static final int ELEMENTWISEAND=19;
	public static final int ELEMENTWISEOR=20;
	public static final int ELSE=21;
	public static final int EQ=22;
	public static final int ESCAPE=23;
	public static final int ESC_SEQ=24;
	public static final int EXPONENT=25;
	public static final int FALSE=26;
	public static final int FIELD=27;
	public static final int FOR=28;
	public static final int FUNCTION=29;
	public static final int GE=30;
	public static final int GT=31;
	public static final int HEX_DIGIT=32;
	public static final int HEX_ESC=33;
	public static final int ID=34;
	public static final int ID_NAME=35;
	public static final int IF=36;
	public static final int IN=37;
	public static final int INTEGER=38;
	public static final int KW=39;
	public static final int LBB=40;
	public static final int LBRACE=41;
	public static final int LBRAKET=42;
	public static final int LE=43;
	public static final int LINE_BREAK=44;
	public static final int LPAR=45;
	public static final int LT=46;
	public static final int MINUS=47;
	public static final int MISSING_VAL=48;
	public static final int MOD=49;
	public static final int MULT=50;
	public static final int NA=51;
	public static final int NE=52;
	public static final int NEWLINE=53;
	public static final int NEXT=54;
	public static final int NOT=55;
	public static final int NS_GET=56;
	public static final int NS_GET_INT=57;
	public static final int NULL=58;
	public static final int OCTAL_ESC=59;
	public static final int OP=60;
	public static final int OP_NAME=61;
	public static final int OR=62;
	public static final int PARMS=63;
	public static final int PLUS=64;
	public static final int RBRACE=65;
	public static final int RBRAKET=66;
	public static final int REPEAT=67;
	public static final int RIGHT_ARROW=68;
	public static final int RPAR=69;
	public static final int SEMICOLON=70;
	public static final int SEQUENCE=71;
	public static final int STRING=72;
	public static final int SUPER_ARROW=73;
	public static final int SUPER_RIGHT_ARROW=74;
	public static final int TILDE=75;
	public static final int TRUE=76;
	public static final int UMINUS=77;
	public static final int UNICODE_ESC=78;
	public static final int UPLUS=79;
	public static final int UTILDE=80;
	public static final int VARIATIC=81;
	public static final int WHILE=82;
	public static final int WS=83;

	    public final int MAX_INCOMPLETE_SIZE = 1000;
	    int incomplete_stack[] = new int[MAX_INCOMPLETE_SIZE]; // TODO probably go for an ArrayList of int :S
	    int incomplete_depth;
	    
	    public void resetIncomplete() {
	    	incomplete_stack[incomplete_depth = 0] = 0;
	    }
	    
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
	@Override public String getGrammarFileName() { return "R.g"; }

	// $ANTLR start "T__84"
	public final void mT__84() throws RecognitionException {
		try {
			int _type = T__84;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:35:7: ( '--EOF--' )
			// R.g:35:9: '--EOF--'
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
	// $ANTLR end "T__84"

	// $ANTLR start "COMMENT"
	public final void mCOMMENT() throws RecognitionException {
		try {
			int _type = COMMENT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:312:5: ( '#' (~ ( '\\n' | '\\r' | '\\f' ) )* ( LINE_BREAK | EOF ) )
			// R.g:312:9: '#' (~ ( '\\n' | '\\r' | '\\f' ) )* ( LINE_BREAK | EOF )
			{
			match('#'); 
			// R.g:312:13: (~ ( '\\n' | '\\r' | '\\f' ) )*
			loop1:
			while (true) {
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
			}

			// R.g:312:32: ( LINE_BREAK | EOF )
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
					// R.g:312:33: LINE_BREAK
					{
					mLINE_BREAK(); 

					}
					break;
				case 2 :
					// R.g:312:46: EOF
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
			// R.g:315:2: ( '<-' | ':=' )
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
					// R.g:315:4: '<-'
					{
					match("<-"); 

					}
					break;
				case 2 :
					// R.g:315:11: ':='
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
			// R.g:318:2: ( '<<-' )
			// R.g:318:5: '<<-'
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
			// R.g:320:2: ( '->' )
			// R.g:320:4: '->'
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
			// R.g:323:2: ( '->>' )
			// R.g:323:4: '->>'
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
			// R.g:326:2: ( '..' ( '.' )+ )
			// R.g:326:4: '..' ( '.' )+
			{
			match(".."); 

			// R.g:326:9: ( '.' )+
			int cnt4=0;
			loop4:
			while (true) {
				int alt4=2;
				int LA4_0 = input.LA(1);
				if ( (LA4_0=='.') ) {
					alt4=1;
				}

				switch (alt4) {
				case 1 :
					// R.g:326:9: '.'
					{
					match('.'); 
					}
					break;

				default :
					if ( cnt4 >= 1 ) break loop4;
					EarlyExitException eee = new EarlyExitException(4, input);
					throw eee;
				}
				cnt4++;
			}

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
			// R.g:328:4: ( '==' )
			// R.g:328:6: '=='
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
			// R.g:329:5: ( '!=' )
			// R.g:329:7: '!='
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
			// R.g:330:4: ( '>=' )
			// R.g:330:6: '>='
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
			// R.g:331:4: ( '<=' )
			// R.g:331:6: '<='
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
			// R.g:332:4: ( '>' )
			// R.g:332:6: '>'
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
			// R.g:333:5: ( '<' )
			// R.g:333:7: '<'
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
			// R.g:335:2: ( '=' )
			// R.g:335:4: '='
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
			// R.g:339:2: ( ':::' )
			// R.g:339:4: ':::'
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
			// R.g:341:2: ( '::' )
			// R.g:341:4: '::'
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

	// $ANTLR start "COLON"
	public final void mCOLON() throws RecognitionException {
		try {
			int _type = COLON;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:344:2: ( ':' )
			// R.g:344:4: ':'
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
	// $ANTLR end "COLON"

	// $ANTLR start "SEMICOLON"
	public final void mSEMICOLON() throws RecognitionException {
		try {
			int _type = SEMICOLON;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:346:2: ( ';' )
			// R.g:346:4: ';'
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
	// $ANTLR end "SEMICOLON"

	// $ANTLR start "COMMA"
	public final void mCOMMA() throws RecognitionException {
		try {
			int _type = COMMA;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:348:2: ( ',' )
			// R.g:348:4: ','
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
			// R.g:350:2: ( '&&' )
			// R.g:350:4: '&&'
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

	// $ANTLR start "ELEMENTWISEAND"
	public final void mELEMENTWISEAND() throws RecognitionException {
		try {
			int _type = ELEMENTWISEAND;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:352:2: ( '&' )
			// R.g:352:4: '&'
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
	// $ANTLR end "ELEMENTWISEAND"

	// $ANTLR start "OR"
	public final void mOR() throws RecognitionException {
		try {
			int _type = OR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:353:4: ( '||' )
			// R.g:353:6: '||'
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

	// $ANTLR start "ELEMENTWISEOR"
	public final void mELEMENTWISEOR() throws RecognitionException {
		try {
			int _type = ELEMENTWISEOR;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:355:2: ( '|' )
			// R.g:355:3: '|'
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
	// $ANTLR end "ELEMENTWISEOR"

	// $ANTLR start "LBRACE"
	public final void mLBRACE() throws RecognitionException {
		try {
			int _type = LBRACE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:357:2: ( '{' )
			// R.g:357:4: '{'
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
			// R.g:359:2: ( '}' )
			// R.g:359:4: '}'
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
			// R.g:361:2: ( '(' )
			// R.g:361:4: '('
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
			// R.g:363:2: ( ')' )
			// R.g:363:4: ')'
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
			// R.g:365:2: ( '[[' )
			// R.g:365:4: '[['
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
			// R.g:367:2: ( '[' )
			// R.g:367:4: '['
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
			// R.g:369:2: ( ']' )
			// R.g:369:4: ']'
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
			// R.g:371:2: ( '^' | '**' )
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
					// R.g:371:4: '^'
					{
					match('^'); 
					}
					break;
				case 2 :
					// R.g:371:10: '**'
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
			// R.g:373:2: ( '~' )
			// R.g:373:4: '~'
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

	// $ANTLR start "NOT"
	public final void mNOT() throws RecognitionException {
		try {
			int _type = NOT;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:375:2: ( '!' )
			// R.g:375:4: '!'
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
			// R.g:377:2: ( '+' )
			// R.g:377:4: '+'
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
			// R.g:379:2: ( '*' )
			// R.g:379:4: '*'
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

	// $ANTLR start "MOD"
	public final void mMOD() throws RecognitionException {
		try {
			int _type = MOD;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:381:2: ( '%%' )
			// R.g:381:4: '%%'
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

	// $ANTLR start "DIV"
	public final void mDIV() throws RecognitionException {
		try {
			int _type = DIV;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:383:5: ( '/' )
			// R.g:383:7: '/'
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
			// R.g:385:2: ( '-' )
			// R.g:385:4: '-'
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
			// R.g:388:2: ( '$' )
			// R.g:388:4: '$'
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
			// R.g:389:4: ( '@' )
			// R.g:389:6: '@'
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
			// R.g:392:2: ( 'function' )
			// R.g:392:4: 'function'
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
			// R.g:394:2: ( 'NULL' )
			// R.g:394:4: 'NULL'
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

	// $ANTLR start "NA"
	public final void mNA() throws RecognitionException {
		try {
			int _type = NA;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:397:5: ( 'NA' )
			// R.g:397:7: 'NA'
			{
			match("NA"); 

			}

			state.type = _type;
			state.channel = _channel;
		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "NA"

	// $ANTLR start "TRUE"
	public final void mTRUE() throws RecognitionException {
		try {
			int _type = TRUE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:399:2: ( 'TRUE' )
			// R.g:399:4: 'TRUE'
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
			// R.g:401:2: ( 'FALSE' )
			// R.g:401:4: 'FALSE'
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
			// R.g:404:2: ( 'while' )
			// R.g:404:4: 'while'
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
			// R.g:405:5: ( 'for' )
			// R.g:405:7: 'for'
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
			// R.g:407:2: ( 'repeat' )
			// R.g:407:4: 'repeat'
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
			// R.g:408:4: ( 'in' )
			// R.g:408:6: 'in'
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
			// R.g:409:4: ( 'if' )
			// R.g:409:6: 'if'
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
			// R.g:411:2: ( 'else' )
			// R.g:411:4: 'else'
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
			// R.g:413:2: ( 'next' )
			// R.g:413:4: 'next'
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
			// R.g:415:2: ( 'break' )
			// R.g:415:4: 'break'
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
			// R.g:418:5: ( ( '\\u0009' | '\\u0020' | '\\u00A0' ) )
			// R.g:418:10: ( '\\u0009' | '\\u0020' | '\\u00A0' )
			{
			if ( input.LA(1)=='\t'||input.LA(1)==' '||input.LA(1)=='\u00A0' ) {
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
			// R.g:421:2: ( LINE_BREAK )
			// R.g:421:4: LINE_BREAK
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

	// $ANTLR start "INTEGER"
	public final void mINTEGER() throws RecognitionException {
		try {
			int _type = INTEGER;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:423:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* 'L' | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'L' | '0x' ( HEX_DIGIT )+ 'L' )
			int alt12=3;
			alt12 = dfa12.predict(input);
			switch (alt12) {
				case 1 :
					// R.g:423:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* 'L'
					{
					// R.g:423:9: ( '0' .. '9' )+
					int cnt6=0;
					loop6:
					while (true) {
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
							EarlyExitException eee = new EarlyExitException(6, input);
							throw eee;
						}
						cnt6++;
					}

					match('.'); 
					// R.g:423:25: ( '0' .. '9' )*
					loop7:
					while (true) {
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
					}

					match('L'); 
					setText(getText().substring(0, getText().length()-1));
					}
					break;
				case 2 :
					// R.g:424:9: ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'L'
					{
					// R.g:424:9: ( '.' )?
					int alt8=2;
					int LA8_0 = input.LA(1);
					if ( (LA8_0=='.') ) {
						alt8=1;
					}
					switch (alt8) {
						case 1 :
							// R.g:424:9: '.'
							{
							match('.'); 
							}
							break;

					}

					// R.g:424:14: ( '0' .. '9' )+
					int cnt9=0;
					loop9:
					while (true) {
						int alt9=2;
						int LA9_0 = input.LA(1);
						if ( ((LA9_0 >= '0' && LA9_0 <= '9')) ) {
							alt9=1;
						}

						switch (alt9) {
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
							if ( cnt9 >= 1 ) break loop9;
							EarlyExitException eee = new EarlyExitException(9, input);
							throw eee;
						}
						cnt9++;
					}

					// R.g:424:26: ( EXPONENT )?
					int alt10=2;
					int LA10_0 = input.LA(1);
					if ( (LA10_0=='E'||LA10_0=='e') ) {
						alt10=1;
					}
					switch (alt10) {
						case 1 :
							// R.g:424:26: EXPONENT
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
					// R.g:425:9: '0x' ( HEX_DIGIT )+ 'L'
					{
					match("0x"); 

					// R.g:425:14: ( HEX_DIGIT )+
					int cnt11=0;
					loop11:
					while (true) {
						int alt11=2;
						int LA11_0 = input.LA(1);
						if ( ((LA11_0 >= '0' && LA11_0 <= '9')||(LA11_0 >= 'A' && LA11_0 <= 'F')||(LA11_0 >= 'a' && LA11_0 <= 'f')) ) {
							alt11=1;
						}

						switch (alt11) {
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
							if ( cnt11 >= 1 ) break loop11;
							EarlyExitException eee = new EarlyExitException(11, input);
							throw eee;
						}
						cnt11++;
					}

					match('L'); 
					setText(getText().substring(0, getText().length()-1));
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
	// $ANTLR end "INTEGER"

	// $ANTLR start "COMPLEX"
	public final void mCOMPLEX() throws RecognitionException {
		try {
			int _type = COMPLEX;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:428:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? 'i' | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'i' | '0x' HEX_DIGIT 'i' )
			int alt19=3;
			alt19 = dfa19.predict(input);
			switch (alt19) {
				case 1 :
					// R.g:428:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? 'i'
					{
					// R.g:428:9: ( '0' .. '9' )+
					int cnt13=0;
					loop13:
					while (true) {
						int alt13=2;
						int LA13_0 = input.LA(1);
						if ( ((LA13_0 >= '0' && LA13_0 <= '9')) ) {
							alt13=1;
						}

						switch (alt13) {
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
							if ( cnt13 >= 1 ) break loop13;
							EarlyExitException eee = new EarlyExitException(13, input);
							throw eee;
						}
						cnt13++;
					}

					match('.'); 
					// R.g:428:25: ( '0' .. '9' )*
					loop14:
					while (true) {
						int alt14=2;
						int LA14_0 = input.LA(1);
						if ( ((LA14_0 >= '0' && LA14_0 <= '9')) ) {
							alt14=1;
						}

						switch (alt14) {
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
							break loop14;
						}
					}

					// R.g:428:37: ( EXPONENT )?
					int alt15=2;
					int LA15_0 = input.LA(1);
					if ( (LA15_0=='E'||LA15_0=='e') ) {
						alt15=1;
					}
					switch (alt15) {
						case 1 :
							// R.g:428:37: EXPONENT
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
					// R.g:429:9: ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'i'
					{
					// R.g:429:9: ( '.' )?
					int alt16=2;
					int LA16_0 = input.LA(1);
					if ( (LA16_0=='.') ) {
						alt16=1;
					}
					switch (alt16) {
						case 1 :
							// R.g:429:9: '.'
							{
							match('.'); 
							}
							break;

					}

					// R.g:429:14: ( '0' .. '9' )+
					int cnt17=0;
					loop17:
					while (true) {
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
							EarlyExitException eee = new EarlyExitException(17, input);
							throw eee;
						}
						cnt17++;
					}

					// R.g:429:26: ( EXPONENT )?
					int alt18=2;
					int LA18_0 = input.LA(1);
					if ( (LA18_0=='E'||LA18_0=='e') ) {
						alt18=1;
					}
					switch (alt18) {
						case 1 :
							// R.g:429:26: EXPONENT
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
					// R.g:430:9: '0x' HEX_DIGIT 'i'
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
			// do for sure before leaving
		}
	}
	// $ANTLR end "COMPLEX"

	// $ANTLR start "DOUBLE"
	public final void mDOUBLE() throws RecognitionException {
		try {
			int _type = DOUBLE;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:433:5: ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? | '0x' ( HEX_DIGIT )+ )
			int alt27=3;
			alt27 = dfa27.predict(input);
			switch (alt27) {
				case 1 :
					// R.g:433:9: ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )?
					{
					// R.g:433:9: ( '0' .. '9' )+
					int cnt20=0;
					loop20:
					while (true) {
						int alt20=2;
						int LA20_0 = input.LA(1);
						if ( ((LA20_0 >= '0' && LA20_0 <= '9')) ) {
							alt20=1;
						}

						switch (alt20) {
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
							if ( cnt20 >= 1 ) break loop20;
							EarlyExitException eee = new EarlyExitException(20, input);
							throw eee;
						}
						cnt20++;
					}

					match('.'); 
					// R.g:433:25: ( '0' .. '9' )*
					loop21:
					while (true) {
						int alt21=2;
						int LA21_0 = input.LA(1);
						if ( ((LA21_0 >= '0' && LA21_0 <= '9')) ) {
							alt21=1;
						}

						switch (alt21) {
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
							break loop21;
						}
					}

					// R.g:433:37: ( EXPONENT )?
					int alt22=2;
					int LA22_0 = input.LA(1);
					if ( (LA22_0=='E'||LA22_0=='e') ) {
						alt22=1;
					}
					switch (alt22) {
						case 1 :
							// R.g:433:37: EXPONENT
							{
							mEXPONENT(); 

							}
							break;

					}

					}
					break;
				case 2 :
					// R.g:434:9: ( '.' )? ( '0' .. '9' )+ ( EXPONENT )?
					{
					// R.g:434:9: ( '.' )?
					int alt23=2;
					int LA23_0 = input.LA(1);
					if ( (LA23_0=='.') ) {
						alt23=1;
					}
					switch (alt23) {
						case 1 :
							// R.g:434:9: '.'
							{
							match('.'); 
							}
							break;

					}

					// R.g:434:14: ( '0' .. '9' )+
					int cnt24=0;
					loop24:
					while (true) {
						int alt24=2;
						int LA24_0 = input.LA(1);
						if ( ((LA24_0 >= '0' && LA24_0 <= '9')) ) {
							alt24=1;
						}

						switch (alt24) {
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
							if ( cnt24 >= 1 ) break loop24;
							EarlyExitException eee = new EarlyExitException(24, input);
							throw eee;
						}
						cnt24++;
					}

					// R.g:434:26: ( EXPONENT )?
					int alt25=2;
					int LA25_0 = input.LA(1);
					if ( (LA25_0=='E'||LA25_0=='e') ) {
						alt25=1;
					}
					switch (alt25) {
						case 1 :
							// R.g:434:26: EXPONENT
							{
							mEXPONENT(); 

							}
							break;

					}

					}
					break;
				case 3 :
					// R.g:435:7: '0x' ( HEX_DIGIT )+
					{
					match("0x"); 

					// R.g:435:12: ( HEX_DIGIT )+
					int cnt26=0;
					loop26:
					while (true) {
						int alt26=2;
						int LA26_0 = input.LA(1);
						if ( ((LA26_0 >= '0' && LA26_0 <= '9')||(LA26_0 >= 'A' && LA26_0 <= 'F')||(LA26_0 >= 'a' && LA26_0 <= 'f')) ) {
							alt26=1;
						}

						switch (alt26) {
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
							if ( cnt26 >= 1 ) break loop26;
							EarlyExitException eee = new EarlyExitException(26, input);
							throw eee;
						}
						cnt26++;
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
	// $ANTLR end "DOUBLE"

	// $ANTLR start "DD"
	public final void mDD() throws RecognitionException {
		try {
			int _type = DD;
			int _channel = DEFAULT_TOKEN_CHANNEL;
			// R.g:437:4: ( '..' ( '0' .. '9' )+ )
			// R.g:437:6: '..' ( '0' .. '9' )+
			{
			match(".."); 

			// R.g:437:11: ( '0' .. '9' )+
			int cnt28=0;
			loop28:
			while (true) {
				int alt28=2;
				int LA28_0 = input.LA(1);
				if ( ((LA28_0 >= '0' && LA28_0 <= '9')) ) {
					alt28=1;
				}

				switch (alt28) {
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
					if ( cnt28 >= 1 ) break loop28;
					EarlyExitException eee = new EarlyExitException(28, input);
					throw eee;
				}
				cnt28++;
			}

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
			// R.g:439:5: ( ( '.' )* ID_NAME | '.' | '`' ( ESC_SEQ |~ ( '\\\\' | '`' ) )* '`' )
			int alt31=3;
			switch ( input.LA(1) ) {
			case '.':
				{
				int LA31_1 = input.LA(2);
				if ( (LA31_1=='.'||(LA31_1 >= 'A' && LA31_1 <= 'Z')||LA31_1=='_'||(LA31_1 >= 'a' && LA31_1 <= 'z')) ) {
					alt31=1;
				}

				else {
					alt31=2;
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
				alt31=1;
				}
				break;
			case '`':
				{
				alt31=3;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 31, 0, input);
				throw nvae;
			}
			switch (alt31) {
				case 1 :
					// R.g:439:7: ( '.' )* ID_NAME
					{
					// R.g:439:7: ( '.' )*
					loop29:
					while (true) {
						int alt29=2;
						int LA29_0 = input.LA(1);
						if ( (LA29_0=='.') ) {
							alt29=1;
						}

						switch (alt29) {
						case 1 :
							// R.g:439:7: '.'
							{
							match('.'); 
							}
							break;

						default :
							break loop29;
						}
					}

					mID_NAME(); 

					}
					break;
				case 2 :
					// R.g:440:4: '.'
					{
					match('.'); 
					}
					break;
				case 3 :
					// R.g:441:4: '`' ( ESC_SEQ |~ ( '\\\\' | '`' ) )* '`'
					{
					match('`'); 
					// R.g:441:8: ( ESC_SEQ |~ ( '\\\\' | '`' ) )*
					loop30:
					while (true) {
						int alt30=3;
						int LA30_0 = input.LA(1);
						if ( (LA30_0=='\\') ) {
							alt30=1;
						}
						else if ( ((LA30_0 >= '\u0000' && LA30_0 <= '[')||(LA30_0 >= ']' && LA30_0 <= '_')||(LA30_0 >= 'a' && LA30_0 <= '\uFFFF')) ) {
							alt30=2;
						}

						switch (alt30) {
						case 1 :
							// R.g:441:10: ESC_SEQ
							{
							mESC_SEQ(); 

							}
							break;
						case 2 :
							// R.g:441:20: ~ ( '\\\\' | '`' )
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
							break loop30;
						}
					}

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
			// R.g:443:4: ( '%' ( OP_NAME )+ '%' )
			// R.g:443:6: '%' ( OP_NAME )+ '%'
			{
			match('%'); 
			// R.g:443:10: ( OP_NAME )+
			int cnt32=0;
			loop32:
			while (true) {
				int alt32=2;
				int LA32_0 = input.LA(1);
				if ( (LA32_0=='&'||(LA32_0 >= '*' && LA32_0 <= '/')||LA32_0==':'||(LA32_0 >= '<' && LA32_0 <= '>')||(LA32_0 >= 'A' && LA32_0 <= 'Z')||(LA32_0 >= '^' && LA32_0 <= '_')||(LA32_0 >= 'a' && LA32_0 <= 'z')||LA32_0=='|'||LA32_0=='~') ) {
					alt32=1;
				}

				switch (alt32) {
				case 1 :
					// R.g:443:10: OP_NAME
					{
					mOP_NAME(); 

					}
					break;

				default :
					if ( cnt32 >= 1 ) break loop32;
					EarlyExitException eee = new EarlyExitException(32, input);
					throw eee;
				}
				cnt32++;
			}

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
			int i;

			 final StringBuilder buf = new StringBuilder(); 
			// R.g:455:5: ( ( '\\\"' ( ESCAPE[buf] |i=~ ( '\\\\' | '\"' ) )* '\\\"' ) | ( '\\'' ( ESCAPE[buf] |i=~ ( '\\\\' | '\\'' ) )* '\\'' ) )
			int alt35=2;
			int LA35_0 = input.LA(1);
			if ( (LA35_0=='\"') ) {
				alt35=1;
			}
			else if ( (LA35_0=='\'') ) {
				alt35=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 35, 0, input);
				throw nvae;
			}

			switch (alt35) {
				case 1 :
					// R.g:456:5: ( '\\\"' ( ESCAPE[buf] |i=~ ( '\\\\' | '\"' ) )* '\\\"' )
					{
					// R.g:456:5: ( '\\\"' ( ESCAPE[buf] |i=~ ( '\\\\' | '\"' ) )* '\\\"' )
					// R.g:456:6: '\\\"' ( ESCAPE[buf] |i=~ ( '\\\\' | '\"' ) )* '\\\"'
					{
					match('\"'); 
					// R.g:457:5: ( ESCAPE[buf] |i=~ ( '\\\\' | '\"' ) )*
					loop33:
					while (true) {
						int alt33=3;
						int LA33_0 = input.LA(1);
						if ( (LA33_0=='\\') ) {
							alt33=1;
						}
						else if ( ((LA33_0 >= '\u0000' && LA33_0 <= '!')||(LA33_0 >= '#' && LA33_0 <= '[')||(LA33_0 >= ']' && LA33_0 <= '\uFFFF')) ) {
							alt33=2;
						}

						switch (alt33) {
						case 1 :
							// R.g:458:5: ESCAPE[buf]
							{
							mESCAPE(buf); 

							}
							break;
						case 2 :
							// R.g:459:7: i=~ ( '\\\\' | '\"' )
							{
							i= input.LA(1);
							if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '!')||(input.LA(1) >= '#' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							 buf.appendCodePoint(i); 
							}
							break;

						default :
							break loop33;
						}
					}

					match('\"'); 
					 setText(buf.toString()); 
					}

					}
					break;
				case 2 :
					// R.g:465:5: ( '\\'' ( ESCAPE[buf] |i=~ ( '\\\\' | '\\'' ) )* '\\'' )
					{
					// R.g:465:5: ( '\\'' ( ESCAPE[buf] |i=~ ( '\\\\' | '\\'' ) )* '\\'' )
					// R.g:466:5: '\\'' ( ESCAPE[buf] |i=~ ( '\\\\' | '\\'' ) )* '\\''
					{
					match('\''); 
					// R.g:467:5: ( ESCAPE[buf] |i=~ ( '\\\\' | '\\'' ) )*
					loop34:
					while (true) {
						int alt34=3;
						int LA34_0 = input.LA(1);
						if ( (LA34_0=='\\') ) {
							alt34=1;
						}
						else if ( ((LA34_0 >= '\u0000' && LA34_0 <= '&')||(LA34_0 >= '(' && LA34_0 <= '[')||(LA34_0 >= ']' && LA34_0 <= '\uFFFF')) ) {
							alt34=2;
						}

						switch (alt34) {
						case 1 :
							// R.g:468:5: ESCAPE[buf]
							{
							mESCAPE(buf); 

							}
							break;
						case 2 :
							// R.g:469:7: i=~ ( '\\\\' | '\\'' )
							{
							i= input.LA(1);
							if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '&')||(input.LA(1) >= '(' && input.LA(1) <= '[')||(input.LA(1) >= ']' && input.LA(1) <= '\uFFFF') ) {
								input.consume();
							}
							else {
								MismatchedSetException mse = new MismatchedSetException(null,input);
								recover(mse);
								throw mse;
							}
							 buf.appendCodePoint(i); 
							}
							break;

						default :
							break loop34;
						}
					}

					match('\''); 
					 setText(buf.toString()); 
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

			// R.g:477:36: ( '\\\\' ( 't' | 'n' | 'r' | 'b' | 'f' | '\"' | '\\'' | '\\\\' | 'x' a= HEX_DIGIT b= HEX_DIGIT | 'u' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT | 'U' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT e= HEX_DIGIT f= HEX_DIGIT g= HEX_DIGIT h= HEX_DIGIT ) )
			// R.g:478:5: '\\\\' ( 't' | 'n' | 'r' | 'b' | 'f' | '\"' | '\\'' | '\\\\' | 'x' a= HEX_DIGIT b= HEX_DIGIT | 'u' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT | 'U' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT e= HEX_DIGIT f= HEX_DIGIT g= HEX_DIGIT h= HEX_DIGIT )
			{
			match('\\'); 
			// R.g:479:5: ( 't' | 'n' | 'r' | 'b' | 'f' | '\"' | '\\'' | '\\\\' | 'x' a= HEX_DIGIT b= HEX_DIGIT | 'u' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT | 'U' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT e= HEX_DIGIT f= HEX_DIGIT g= HEX_DIGIT h= HEX_DIGIT )
			int alt36=11;
			switch ( input.LA(1) ) {
			case 't':
				{
				alt36=1;
				}
				break;
			case 'n':
				{
				alt36=2;
				}
				break;
			case 'r':
				{
				alt36=3;
				}
				break;
			case 'b':
				{
				alt36=4;
				}
				break;
			case 'f':
				{
				alt36=5;
				}
				break;
			case '\"':
				{
				alt36=6;
				}
				break;
			case '\'':
				{
				alt36=7;
				}
				break;
			case '\\':
				{
				alt36=8;
				}
				break;
			case 'x':
				{
				alt36=9;
				}
				break;
			case 'u':
				{
				alt36=10;
				}
				break;
			case 'U':
				{
				alt36=11;
				}
				break;
			default:
				NoViableAltException nvae =
					new NoViableAltException("", 36, 0, input);
				throw nvae;
			}
			switch (alt36) {
				case 1 :
					// R.g:479:7: 't'
					{
					match('t'); 
					 buf.append('\t'); 
					}
					break;
				case 2 :
					// R.g:480:7: 'n'
					{
					match('n'); 
					 buf.append('\n'); 
					}
					break;
				case 3 :
					// R.g:481:7: 'r'
					{
					match('r'); 
					 buf.append('\r'); 
					}
					break;
				case 4 :
					// R.g:482:7: 'b'
					{
					match('b'); 
					 buf.append('\b'); 
					}
					break;
				case 5 :
					// R.g:483:7: 'f'
					{
					match('f'); 
					 buf.append('\f'); 
					}
					break;
				case 6 :
					// R.g:484:7: '\"'
					{
					match('\"'); 
					 buf.append('\"'); 
					}
					break;
				case 7 :
					// R.g:485:7: '\\''
					{
					match('\''); 
					 buf.append('\''); 
					}
					break;
				case 8 :
					// R.g:486:7: '\\\\'
					{
					match('\\'); 
					 buf.append('\\'); 
					}
					break;
				case 9 :
					// R.g:487:7: 'x' a= HEX_DIGIT b= HEX_DIGIT
					{
					match('x'); 
					int aStart1139 = getCharIndex();
					int aStartLine1139 = getLine();
					int aStartCharPos1139 = getCharPositionInLine();
					mHEX_DIGIT(); 
					a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart1139, getCharIndex()-1);
					a.setLine(aStartLine1139);
					a.setCharPositionInLine(aStartCharPos1139);

					int bStart1145 = getCharIndex();
					int bStartLine1145 = getLine();
					int bStartCharPos1145 = getCharPositionInLine();
					mHEX_DIGIT(); 
					b = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, bStart1145, getCharIndex()-1);
					b.setLine(bStartLine1145);
					b.setCharPositionInLine(bStartCharPos1145);

					 buf.append(ParseUtil.hexChar((a!=null?a.getText():null), (b!=null?b.getText():null))); 
					}
					break;
				case 10 :
					// R.g:488:7: 'u' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT
					{
					match('u'); 
					int aStart1161 = getCharIndex();
					int aStartLine1161 = getLine();
					int aStartCharPos1161 = getCharPositionInLine();
					mHEX_DIGIT(); 
					a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart1161, getCharIndex()-1);
					a.setLine(aStartLine1161);
					a.setCharPositionInLine(aStartCharPos1161);

					int bStart1167 = getCharIndex();
					int bStartLine1167 = getLine();
					int bStartCharPos1167 = getCharPositionInLine();
					mHEX_DIGIT(); 
					b = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, bStart1167, getCharIndex()-1);
					b.setLine(bStartLine1167);
					b.setCharPositionInLine(bStartCharPos1167);

					int cStart1173 = getCharIndex();
					int cStartLine1173 = getLine();
					int cStartCharPos1173 = getCharPositionInLine();
					mHEX_DIGIT(); 
					c = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, cStart1173, getCharIndex()-1);
					c.setLine(cStartLine1173);
					c.setCharPositionInLine(cStartCharPos1173);

					int dStart1179 = getCharIndex();
					int dStartLine1179 = getLine();
					int dStartCharPos1179 = getCharPositionInLine();
					mHEX_DIGIT(); 
					d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart1179, getCharIndex()-1);
					d.setLine(dStartLine1179);
					d.setCharPositionInLine(dStartCharPos1179);

					 buf.append(ParseUtil.hexChar((a!=null?a.getText():null), (b!=null?b.getText():null), (c!=null?c.getText():null), (d!=null?d.getText():null))); 
					}
					break;
				case 11 :
					// R.g:489:7: 'U' a= HEX_DIGIT b= HEX_DIGIT c= HEX_DIGIT d= HEX_DIGIT e= HEX_DIGIT f= HEX_DIGIT g= HEX_DIGIT h= HEX_DIGIT
					{
					match('U'); 
					int aStart1195 = getCharIndex();
					int aStartLine1195 = getLine();
					int aStartCharPos1195 = getCharPositionInLine();
					mHEX_DIGIT(); 
					a = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, aStart1195, getCharIndex()-1);
					a.setLine(aStartLine1195);
					a.setCharPositionInLine(aStartCharPos1195);

					int bStart1201 = getCharIndex();
					int bStartLine1201 = getLine();
					int bStartCharPos1201 = getCharPositionInLine();
					mHEX_DIGIT(); 
					b = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, bStart1201, getCharIndex()-1);
					b.setLine(bStartLine1201);
					b.setCharPositionInLine(bStartCharPos1201);

					int cStart1207 = getCharIndex();
					int cStartLine1207 = getLine();
					int cStartCharPos1207 = getCharPositionInLine();
					mHEX_DIGIT(); 
					c = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, cStart1207, getCharIndex()-1);
					c.setLine(cStartLine1207);
					c.setCharPositionInLine(cStartCharPos1207);

					int dStart1213 = getCharIndex();
					int dStartLine1213 = getLine();
					int dStartCharPos1213 = getCharPositionInLine();
					mHEX_DIGIT(); 
					d = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, dStart1213, getCharIndex()-1);
					d.setLine(dStartLine1213);
					d.setCharPositionInLine(dStartCharPos1213);

					int eStart1219 = getCharIndex();
					int eStartLine1219 = getLine();
					int eStartCharPos1219 = getCharPositionInLine();
					mHEX_DIGIT(); 
					e = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, eStart1219, getCharIndex()-1);
					e.setLine(eStartLine1219);
					e.setCharPositionInLine(eStartCharPos1219);

					int fStart1225 = getCharIndex();
					int fStartLine1225 = getLine();
					int fStartCharPos1225 = getCharPositionInLine();
					mHEX_DIGIT(); 
					f = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, fStart1225, getCharIndex()-1);
					f.setLine(fStartLine1225);
					f.setCharPositionInLine(fStartCharPos1225);

					int gStart1231 = getCharIndex();
					int gStartLine1231 = getLine();
					int gStartCharPos1231 = getCharPositionInLine();
					mHEX_DIGIT(); 
					g = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, gStart1231, getCharIndex()-1);
					g.setLine(gStartLine1231);
					g.setCharPositionInLine(gStartCharPos1231);

					int hStart1237 = getCharIndex();
					int hStartLine1237 = getLine();
					int hStartCharPos1237 = getCharPositionInLine();
					mHEX_DIGIT(); 
					h = new CommonToken(input, Token.INVALID_TOKEN_TYPE, Token.DEFAULT_CHANNEL, hStart1237, getCharIndex()-1);
					h.setLine(hStartLine1237);
					h.setCharPositionInLine(hStartCharPos1237);

					 buf.append(ParseUtil.hexChar((a!=null?a.getText():null), (b!=null?b.getText():null), (c!=null?c.getText():null), (d!=null?d.getText():null), (e!=null?e.getText():null), (f!=null?f.getText():null), (g!=null?g.getText():null), (h!=null?h.getText():null))); 
					}
					break;

			}

			}

		}
		finally {
			// do for sure before leaving
		}
	}
	// $ANTLR end "ESCAPE"

	// $ANTLR start "LINE_BREAK"
	public final void mLINE_BREAK() throws RecognitionException {
		try {
			// R.g:494:2: ( ( ( '\\f' | '\\r' )? '\\n' ) | ( ( '\\n' )? ( '\\r' | '\\f' ) ) )
			int alt39=2;
			int LA39_0 = input.LA(1);
			if ( ((LA39_0 >= '\f' && LA39_0 <= '\r')) ) {
				int LA39_1 = input.LA(2);
				if ( (LA39_1=='\n') ) {
					alt39=1;
				}

				else {
					alt39=2;
				}

			}
			else if ( (LA39_0=='\n') ) {
				int LA39_2 = input.LA(2);
				if ( ((LA39_2 >= '\f' && LA39_2 <= '\r')) ) {
					alt39=2;
				}

				else {
					alt39=1;
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 39, 0, input);
				throw nvae;
			}

			switch (alt39) {
				case 1 :
					// R.g:495:3: ( ( '\\f' | '\\r' )? '\\n' )
					{
					// R.g:495:3: ( ( '\\f' | '\\r' )? '\\n' )
					// R.g:495:4: ( '\\f' | '\\r' )? '\\n'
					{
					// R.g:495:4: ( '\\f' | '\\r' )?
					int alt37=2;
					int LA37_0 = input.LA(1);
					if ( ((LA37_0 >= '\f' && LA37_0 <= '\r')) ) {
						alt37=1;
					}
					switch (alt37) {
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
					// R.g:496:4: ( ( '\\n' )? ( '\\r' | '\\f' ) )
					{
					// R.g:496:4: ( ( '\\n' )? ( '\\r' | '\\f' ) )
					// R.g:496:5: ( '\\n' )? ( '\\r' | '\\f' )
					{
					// R.g:496:5: ( '\\n' )?
					int alt38=2;
					int LA38_0 = input.LA(1);
					if ( (LA38_0=='\n') ) {
						alt38=1;
					}
					switch (alt38) {
						case 1 :
							// R.g:496:5: '\\n'
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
			// R.g:500:2: ( ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+ )
			// R.g:500:4: ( 'e' | 'E' ) ( '+' | '-' )? ( '0' .. '9' )+
			{
			if ( input.LA(1)=='E'||input.LA(1)=='e' ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// R.g:500:14: ( '+' | '-' )?
			int alt40=2;
			int LA40_0 = input.LA(1);
			if ( (LA40_0=='+'||LA40_0=='-') ) {
				alt40=1;
			}
			switch (alt40) {
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

			// R.g:500:25: ( '0' .. '9' )+
			int cnt41=0;
			loop41:
			while (true) {
				int alt41=2;
				int LA41_0 = input.LA(1);
				if ( ((LA41_0 >= '0' && LA41_0 <= '9')) ) {
					alt41=1;
				}

				switch (alt41) {
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
					if ( cnt41 >= 1 ) break loop41;
					EarlyExitException eee = new EarlyExitException(41, input);
					throw eee;
				}
				cnt41++;
			}

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
			// R.g:504:2: ( ID_NAME | ( '*' | '/' | '+' | '-' | '>' | '<' | '=' | '|' | '&' | ':' | '^' | '.' | '~' | ',' ) )
			int alt42=2;
			int LA42_0 = input.LA(1);
			if ( ((LA42_0 >= 'A' && LA42_0 <= 'Z')||LA42_0=='_'||(LA42_0 >= 'a' && LA42_0 <= 'z')) ) {
				alt42=1;
			}
			else if ( (LA42_0=='&'||(LA42_0 >= '*' && LA42_0 <= '/')||LA42_0==':'||(LA42_0 >= '<' && LA42_0 <= '>')||LA42_0=='^'||LA42_0=='|'||LA42_0=='~') ) {
				alt42=2;
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 42, 0, input);
				throw nvae;
			}

			switch (alt42) {
				case 1 :
					// R.g:504:4: ID_NAME
					{
					mID_NAME(); 

					}
					break;
				case 2 :
					// R.g:505:4: ( '*' | '/' | '+' | '-' | '>' | '<' | '=' | '|' | '&' | ':' | '^' | '.' | '~' | ',' )
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
			// R.g:509:2: ( ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )* )
			// R.g:509:4: ( 'a' .. 'z' | 'A' .. 'Z' | '_' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )*
			{
			if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
				input.consume();
			}
			else {
				MismatchedSetException mse = new MismatchedSetException(null,input);
				recover(mse);
				throw mse;
			}
			// R.g:509:28: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '.' )*
			loop43:
			while (true) {
				int alt43=2;
				int LA43_0 = input.LA(1);
				if ( (LA43_0=='.'||(LA43_0 >= '0' && LA43_0 <= '9')||(LA43_0 >= 'A' && LA43_0 <= 'Z')||LA43_0=='_'||(LA43_0 >= 'a' && LA43_0 <= 'z')) ) {
					alt43=1;
				}

				switch (alt43) {
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
					break loop43;
				}
			}

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
			// R.g:513:5: ( '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '`' | '\\\\' | ' ' | 'a' | 'v' ) | '\\\\' LINE_BREAK | UNICODE_ESC | OCTAL_ESC | HEX_ESC )
			int alt44=5;
			int LA44_0 = input.LA(1);
			if ( (LA44_0=='\\') ) {
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
					alt44=1;
					}
					break;
				case 'u':
					{
					alt44=3;
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
					alt44=4;
					}
					break;
				case 'x':
					{
					alt44=5;
					}
					break;
				case '\n':
				case '\f':
				case '\r':
					{
					alt44=2;
					}
					break;
				default:
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 44, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}
			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 44, 0, input);
				throw nvae;
			}

			switch (alt44) {
				case 1 :
					// R.g:513:9: '\\\\' ( 'b' | 't' | 'n' | 'f' | 'r' | '\"' | '\\'' | '`' | '\\\\' | ' ' | 'a' | 'v' )
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
					// R.g:514:7: '\\\\' LINE_BREAK
					{
					match('\\'); 
					mLINE_BREAK(); 

					}
					break;
				case 3 :
					// R.g:515:9: UNICODE_ESC
					{
					mUNICODE_ESC(); 

					}
					break;
				case 4 :
					// R.g:516:9: OCTAL_ESC
					{
					mOCTAL_ESC(); 

					}
					break;
				case 5 :
					// R.g:517:7: HEX_ESC
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
			// R.g:521:5: ( '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT )
			// R.g:521:9: '\\\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
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
			// R.g:525:2: ( '\\\\x' HEX_DIGIT ( HEX_DIGIT )? )
			// R.g:525:4: '\\\\x' HEX_DIGIT ( HEX_DIGIT )?
			{
			match("\\x"); 

			mHEX_DIGIT(); 

			// R.g:525:20: ( HEX_DIGIT )?
			int alt45=2;
			int LA45_0 = input.LA(1);
			if ( ((LA45_0 >= '0' && LA45_0 <= '9')||(LA45_0 >= 'A' && LA45_0 <= 'F')||(LA45_0 >= 'a' && LA45_0 <= 'f')) ) {
				alt45=1;
			}
			switch (alt45) {
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
			// R.g:529:2: ( ( '0' .. '9' | 'a' .. 'f' | 'A' .. 'F' ) )
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
			// R.g:533:5: ( '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) ( '0' .. '7' ) | '\\\\' ( '0' .. '7' ) )
			int alt46=3;
			int LA46_0 = input.LA(1);
			if ( (LA46_0=='\\') ) {
				int LA46_1 = input.LA(2);
				if ( ((LA46_1 >= '0' && LA46_1 <= '3')) ) {
					int LA46_2 = input.LA(3);
					if ( ((LA46_2 >= '0' && LA46_2 <= '7')) ) {
						int LA46_4 = input.LA(4);
						if ( ((LA46_4 >= '0' && LA46_4 <= '7')) ) {
							alt46=1;
						}

						else {
							alt46=2;
						}

					}

					else {
						alt46=3;
					}

				}
				else if ( ((LA46_1 >= '4' && LA46_1 <= '7')) ) {
					int LA46_3 = input.LA(3);
					if ( ((LA46_3 >= '0' && LA46_3 <= '7')) ) {
						alt46=2;
					}

					else {
						alt46=3;
					}

				}

				else {
					int nvaeMark = input.mark();
					try {
						input.consume();
						NoViableAltException nvae =
							new NoViableAltException("", 46, 1, input);
						throw nvae;
					} finally {
						input.rewind(nvaeMark);
					}
				}

			}

			else {
				NoViableAltException nvae =
					new NoViableAltException("", 46, 0, input);
				throw nvae;
			}

			switch (alt46) {
				case 1 :
					// R.g:533:9: '\\\\' ( '0' .. '3' ) ( '0' .. '7' ) ( '0' .. '7' )
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
					// R.g:534:9: '\\\\' ( '0' .. '7' ) ( '0' .. '7' )
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
					// R.g:535:9: '\\\\' ( '0' .. '7' )
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
		// R.g:1:8: ( T__84 | COMMENT | ARROW | SUPER_ARROW | RIGHT_ARROW | SUPER_RIGHT_ARROW | VARIATIC | EQ | NE | GE | LE | GT | LT | ASSIGN | NS_GET_INT | NS_GET | COLON | SEMICOLON | COMMA | AND | ELEMENTWISEAND | OR | ELEMENTWISEOR | LBRACE | RBRACE | LPAR | RPAR | LBB | LBRAKET | RBRAKET | CARRET | TILDE | NOT | PLUS | MULT | MOD | DIV | MINUS | FIELD | AT | FUNCTION | NULL | NA | TRUE | FALSE | WHILE | FOR | REPEAT | IN | IF | ELSE | NEXT | BREAK | WS | NEWLINE | INTEGER | COMPLEX | DOUBLE | DD | ID | OP | STRING )
		int alt47=62;
		alt47 = dfa47.predict(input);
		switch (alt47) {
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
				// R.g:1:150: ELEMENTWISEAND
				{
				mELEMENTWISEAND(); 

				}
				break;
			case 22 :
				// R.g:1:165: OR
				{
				mOR(); 

				}
				break;
			case 23 :
				// R.g:1:168: ELEMENTWISEOR
				{
				mELEMENTWISEOR(); 

				}
				break;
			case 24 :
				// R.g:1:182: LBRACE
				{
				mLBRACE(); 

				}
				break;
			case 25 :
				// R.g:1:189: RBRACE
				{
				mRBRACE(); 

				}
				break;
			case 26 :
				// R.g:1:196: LPAR
				{
				mLPAR(); 

				}
				break;
			case 27 :
				// R.g:1:201: RPAR
				{
				mRPAR(); 

				}
				break;
			case 28 :
				// R.g:1:206: LBB
				{
				mLBB(); 

				}
				break;
			case 29 :
				// R.g:1:210: LBRAKET
				{
				mLBRAKET(); 

				}
				break;
			case 30 :
				// R.g:1:218: RBRAKET
				{
				mRBRAKET(); 

				}
				break;
			case 31 :
				// R.g:1:226: CARRET
				{
				mCARRET(); 

				}
				break;
			case 32 :
				// R.g:1:233: TILDE
				{
				mTILDE(); 

				}
				break;
			case 33 :
				// R.g:1:239: NOT
				{
				mNOT(); 

				}
				break;
			case 34 :
				// R.g:1:243: PLUS
				{
				mPLUS(); 

				}
				break;
			case 35 :
				// R.g:1:248: MULT
				{
				mMULT(); 

				}
				break;
			case 36 :
				// R.g:1:253: MOD
				{
				mMOD(); 

				}
				break;
			case 37 :
				// R.g:1:257: DIV
				{
				mDIV(); 

				}
				break;
			case 38 :
				// R.g:1:261: MINUS
				{
				mMINUS(); 

				}
				break;
			case 39 :
				// R.g:1:267: FIELD
				{
				mFIELD(); 

				}
				break;
			case 40 :
				// R.g:1:273: AT
				{
				mAT(); 

				}
				break;
			case 41 :
				// R.g:1:276: FUNCTION
				{
				mFUNCTION(); 

				}
				break;
			case 42 :
				// R.g:1:285: NULL
				{
				mNULL(); 

				}
				break;
			case 43 :
				// R.g:1:290: NA
				{
				mNA(); 

				}
				break;
			case 44 :
				// R.g:1:293: TRUE
				{
				mTRUE(); 

				}
				break;
			case 45 :
				// R.g:1:298: FALSE
				{
				mFALSE(); 

				}
				break;
			case 46 :
				// R.g:1:304: WHILE
				{
				mWHILE(); 

				}
				break;
			case 47 :
				// R.g:1:310: FOR
				{
				mFOR(); 

				}
				break;
			case 48 :
				// R.g:1:314: REPEAT
				{
				mREPEAT(); 

				}
				break;
			case 49 :
				// R.g:1:321: IN
				{
				mIN(); 

				}
				break;
			case 50 :
				// R.g:1:324: IF
				{
				mIF(); 

				}
				break;
			case 51 :
				// R.g:1:327: ELSE
				{
				mELSE(); 

				}
				break;
			case 52 :
				// R.g:1:332: NEXT
				{
				mNEXT(); 

				}
				break;
			case 53 :
				// R.g:1:337: BREAK
				{
				mBREAK(); 

				}
				break;
			case 54 :
				// R.g:1:343: WS
				{
				mWS(); 

				}
				break;
			case 55 :
				// R.g:1:346: NEWLINE
				{
				mNEWLINE(); 

				}
				break;
			case 56 :
				// R.g:1:354: INTEGER
				{
				mINTEGER(); 

				}
				break;
			case 57 :
				// R.g:1:362: COMPLEX
				{
				mCOMPLEX(); 

				}
				break;
			case 58 :
				// R.g:1:370: DOUBLE
				{
				mDOUBLE(); 

				}
				break;
			case 59 :
				// R.g:1:377: DD
				{
				mDD(); 

				}
				break;
			case 60 :
				// R.g:1:380: ID
				{
				mID(); 

				}
				break;
			case 61 :
				// R.g:1:383: OP
				{
				mOP(); 

				}
				break;
			case 62 :
				// R.g:1:386: STRING
				{
				mSTRING(); 

				}
				break;

		}
	}


	protected DFA12 dfa12 = new DFA12(this);
	protected DFA19 dfa19 = new DFA19(this);
	protected DFA27 dfa27 = new DFA27(this);
	protected DFA47 dfa47 = new DFA47(this);
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
			"\1\5\1\uffff\12\3\13\uffff\1\2\6\uffff\1\2\30\uffff\1\2\22\uffff\1\4",
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

	protected class DFA12 extends DFA {

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
			return "422:1: INTEGER : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* 'L' | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'L' | '0x' ( HEX_DIGIT )+ 'L' );";
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
			"\1\5\1\uffff\12\3\13\uffff\1\2\37\uffff\1\2\3\uffff\1\2\16\uffff\1\4",
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

	protected class DFA19 extends DFA {

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
			return "427:1: COMPLEX : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? 'i' | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? 'i' | '0x' HEX_DIGIT 'i' );";
		}
	}

	static final String DFA27_eotS =
		"\1\uffff\1\2\1\uffff\1\2\2\uffff";
	static final String DFA27_eofS =
		"\6\uffff";
	static final String DFA27_minS =
		"\2\56\1\uffff\1\56\2\uffff";
	static final String DFA27_maxS =
		"\1\71\1\170\1\uffff\1\71\2\uffff";
	static final String DFA27_acceptS =
		"\2\uffff\1\2\1\uffff\1\3\1\1";
	static final String DFA27_specialS =
		"\6\uffff}>";
	static final String[] DFA27_transitionS = {
			"\1\2\1\uffff\1\1\11\3",
			"\1\5\1\uffff\12\3\76\uffff\1\4",
			"",
			"\1\5\1\uffff\12\3",
			"",
			""
	};

	static final short[] DFA27_eot = DFA.unpackEncodedString(DFA27_eotS);
	static final short[] DFA27_eof = DFA.unpackEncodedString(DFA27_eofS);
	static final char[] DFA27_min = DFA.unpackEncodedStringToUnsignedChars(DFA27_minS);
	static final char[] DFA27_max = DFA.unpackEncodedStringToUnsignedChars(DFA27_maxS);
	static final short[] DFA27_accept = DFA.unpackEncodedString(DFA27_acceptS);
	static final short[] DFA27_special = DFA.unpackEncodedString(DFA27_specialS);
	static final short[][] DFA27_transition;

	static {
		int numStates = DFA27_transitionS.length;
		DFA27_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA27_transition[i] = DFA.unpackEncodedString(DFA27_transitionS[i]);
		}
	}

	protected class DFA27 extends DFA {

		public DFA27(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 27;
			this.eot = DFA27_eot;
			this.eof = DFA27_eof;
			this.min = DFA27_min;
			this.max = DFA27_max;
			this.accept = DFA27_accept;
			this.special = DFA27_special;
			this.transition = DFA27_transition;
		}
		@Override
		public String getDescription() {
			return "432:1: DOUBLE : ( ( '0' .. '9' )+ '.' ( '0' .. '9' )* ( EXPONENT )? | ( '.' )? ( '0' .. '9' )+ ( EXPONENT )? | '0x' ( HEX_DIGIT )+ );";
		}
	}

	static final String DFA47_eotS =
		"\1\uffff\1\55\1\uffff\1\61\1\63\1\51\1\67\1\71\1\73\2\uffff\1\75\1\77"+
		"\4\uffff\1\101\2\uffff\1\102\6\uffff\12\51\2\uffff\2\127\3\uffff\1\131"+
		"\5\uffff\1\133\2\uffff\1\127\17\uffff\3\51\1\141\4\51\1\146\1\147\3\51"+
		"\1\uffff\1\127\10\uffff\1\160\1\uffff\1\51\1\162\1\51\1\uffff\4\51\2\uffff"+
		"\3\51\2\127\2\uffff\1\127\1\uffff\1\51\1\uffff\1\177\1\u0080\3\51\1\u0084"+
		"\1\u0085\1\51\1\127\1\uffff\1\127\1\51\2\uffff\1\u0088\1\u0089\1\51\2"+
		"\uffff\1\u008b\1\51\2\uffff\1\u008d\1\uffff\1\51\1\uffff\1\u008f\1\uffff";
	static final String DFA47_eofS =
		"\u0090\uffff";
	static final String DFA47_minS =
		"\1\11\1\55\1\uffff\1\55\1\72\1\56\3\75\2\uffff\1\46\1\174\4\uffff\1\133"+
		"\2\uffff\1\52\2\uffff\1\45\3\uffff\1\157\1\101\1\122\1\101\1\150\1\145"+
		"\1\146\1\154\1\145\1\162\2\uffff\2\56\3\uffff\1\76\5\uffff\1\72\1\uffff"+
		"\1\56\1\60\17\uffff\1\156\1\162\1\114\1\56\1\125\1\114\1\151\1\160\2\56"+
		"\1\163\1\170\1\145\2\60\1\53\7\uffff\1\56\1\uffff\1\143\1\56\1\114\1\uffff"+
		"\1\105\1\123\1\154\1\145\2\uffff\1\145\1\164\1\141\2\60\1\53\2\60\1\uffff"+
		"\1\164\1\uffff\2\56\1\105\1\145\1\141\2\56\1\153\3\60\1\151\2\uffff\2"+
		"\56\1\164\2\uffff\1\56\1\157\2\uffff\1\56\1\uffff\1\156\1\uffff\1\56\1"+
		"\uffff";
	static final String DFA47_maxS =
		"\1\u00a0\1\76\1\uffff\2\75\1\71\3\75\2\uffff\1\46\1\174\4\uffff\1\133"+
		"\2\uffff\1\52\2\uffff\1\176\3\uffff\1\165\1\125\1\122\1\101\1\150\1\145"+
		"\1\156\1\154\1\145\1\162\2\uffff\1\170\1\151\3\uffff\1\76\5\uffff\1\72"+
		"\1\uffff\1\172\1\151\17\uffff\1\156\1\162\1\114\1\172\1\125\1\114\1\151"+
		"\1\160\2\172\1\163\1\170\1\145\1\146\1\151\1\71\7\uffff\1\172\1\uffff"+
		"\1\143\1\172\1\114\1\uffff\1\105\1\123\1\154\1\145\2\uffff\1\145\1\164"+
		"\1\141\2\151\2\71\1\151\1\uffff\1\164\1\uffff\2\172\1\105\1\145\1\141"+
		"\2\172\1\153\1\146\1\71\2\151\2\uffff\2\172\1\164\2\uffff\1\172\1\157"+
		"\2\uffff\1\172\1\uffff\1\156\1\uffff\1\172\1\uffff";
	static final String DFA47_acceptS =
		"\2\uffff\1\2\6\uffff\1\22\1\23\2\uffff\1\30\1\31\1\32\1\33\1\uffff\1\36"+
		"\1\37\1\uffff\1\40\1\42\1\uffff\1\45\1\47\1\50\12\uffff\1\66\1\67\2\uffff"+
		"\1\74\1\76\1\1\1\uffff\1\46\1\3\1\4\1\13\1\15\1\uffff\1\21\2\uffff\1\10"+
		"\1\16\1\11\1\41\1\12\1\14\1\24\1\25\1\26\1\27\1\34\1\35\1\43\1\44\1\75"+
		"\20\uffff\1\70\1\71\1\72\1\6\1\5\1\17\1\20\1\uffff\1\73\3\uffff\1\53\4"+
		"\uffff\1\61\1\62\10\uffff\1\7\1\uffff\1\57\14\uffff\1\52\1\54\3\uffff"+
		"\1\63\1\64\2\uffff\1\55\1\56\1\uffff\1\65\1\uffff\1\60\1\uffff\1\51";
	static final String DFA47_specialS =
		"\u0090\uffff}>";
	static final String[] DFA47_transitionS = {
			"\1\45\1\46\1\uffff\2\46\22\uffff\1\45\1\7\1\52\1\2\1\31\1\27\1\13\1\52"+
			"\1\17\1\20\1\24\1\26\1\12\1\1\1\5\1\30\1\47\11\50\1\4\1\11\1\3\1\6\1"+
			"\10\1\uffff\1\32\5\51\1\36\7\51\1\34\5\51\1\35\6\51\1\21\1\uffff\1\22"+
			"\1\23\3\51\1\44\2\51\1\42\1\33\2\51\1\41\4\51\1\43\3\51\1\40\4\51\1\37"+
			"\3\51\1\15\1\14\1\16\1\25\41\uffff\1\45",
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
			"",
			"\1\103\1\104\3\uffff\6\104\12\uffff\1\104\1\uffff\3\104\2\uffff\32\104"+
			"\3\uffff\2\104\1\uffff\32\104\1\uffff\1\104\1\uffff\1\104",
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
			"\1\123\1\uffff\12\50\13\uffff\1\124\6\uffff\1\125\30\uffff\1\124\3\uffff"+
			"\1\126\16\uffff\1\122",
			"\1\123\1\uffff\12\50\13\uffff\1\124\6\uffff\1\125\30\uffff\1\124\3\uffff"+
			"\1\126",
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
			"\1\134\1\uffff\12\135\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			"\12\65\13\uffff\1\124\6\uffff\1\125\30\uffff\1\124\3\uffff\1\126",
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
			"\12\154\13\uffff\1\155\6\uffff\1\125\30\uffff\1\155\3\uffff\1\126",
			"\1\156\1\uffff\1\156\2\uffff\12\157",
			"",
			"",
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
			"\12\173\7\uffff\6\173\5\uffff\1\125\24\uffff\6\173\2\uffff\1\126",
			"\12\154\13\uffff\1\155\6\uffff\1\125\30\uffff\1\155\3\uffff\1\126",
			"\1\174\1\uffff\1\174\2\uffff\12\175",
			"\12\157",
			"\12\157\22\uffff\1\125\34\uffff\1\126",
			"",
			"\1\176",
			"",
			"\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			"\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			"\1\u0081",
			"\1\u0082",
			"\1\u0083",
			"\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			"\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			"\1\u0086",
			"\12\173\7\uffff\6\173\5\uffff\1\125\24\uffff\6\173",
			"\12\175",
			"\12\175\57\uffff\1\126",
			"\1\u0087",
			"",
			"",
			"\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			"\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			"\1\u008a",
			"",
			"",
			"\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			"\1\u008c",
			"",
			"",
			"\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			"",
			"\1\u008e",
			"",
			"\1\51\1\uffff\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51",
			""
	};

	static final short[] DFA47_eot = DFA.unpackEncodedString(DFA47_eotS);
	static final short[] DFA47_eof = DFA.unpackEncodedString(DFA47_eofS);
	static final char[] DFA47_min = DFA.unpackEncodedStringToUnsignedChars(DFA47_minS);
	static final char[] DFA47_max = DFA.unpackEncodedStringToUnsignedChars(DFA47_maxS);
	static final short[] DFA47_accept = DFA.unpackEncodedString(DFA47_acceptS);
	static final short[] DFA47_special = DFA.unpackEncodedString(DFA47_specialS);
	static final short[][] DFA47_transition;

	static {
		int numStates = DFA47_transitionS.length;
		DFA47_transition = new short[numStates][];
		for (int i=0; i<numStates; i++) {
			DFA47_transition[i] = DFA.unpackEncodedString(DFA47_transitionS[i]);
		}
	}

	protected class DFA47 extends DFA {

		public DFA47(BaseRecognizer recognizer) {
			this.recognizer = recognizer;
			this.decisionNumber = 47;
			this.eot = DFA47_eot;
			this.eof = DFA47_eof;
			this.min = DFA47_min;
			this.max = DFA47_max;
			this.accept = DFA47_accept;
			this.special = DFA47_special;
			this.transition = DFA47_transition;
		}
		@Override
		public String getDescription() {
			return "1:1: Tokens : ( T__84 | COMMENT | ARROW | SUPER_ARROW | RIGHT_ARROW | SUPER_RIGHT_ARROW | VARIATIC | EQ | NE | GE | LE | GT | LT | ASSIGN | NS_GET_INT | NS_GET | COLON | SEMICOLON | COMMA | AND | ELEMENTWISEAND | OR | ELEMENTWISEOR | LBRACE | RBRACE | LPAR | RPAR | LBB | LBRAKET | RBRAKET | CARRET | TILDE | NOT | PLUS | MULT | MOD | DIV | MINUS | FIELD | AT | FUNCTION | NULL | NA | TRUE | FALSE | WHILE | FOR | REPEAT | IN | IF | ELSE | NEXT | BREAK | WS | NEWLINE | INTEGER | COMPLEX | DOUBLE | DD | ID | OP | STRING );";
		}
	}

}
