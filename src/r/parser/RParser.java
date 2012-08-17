// $ANTLR 3.4 R.g 2012-08-16 22:29:04

package r.parser;

import r.*;
import r.data.*;
import r.nodes.*;
import r.nodes.Call.*;
import r.nodes.UnaryOperation.*;
import r.nodes.BinaryOperation.*;
//Checkstyle: stop


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({"all"})
public class RParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "ARROW", "ASSIGN", "AT", "BITWISEAND", "BITWISEOR", "BRAKET", "BREAK", "CALL", "CARRET", "COLUMN", "COMMA", "COMMENT", "COMPLEX", "DD", "DIV", "DOUBLE", "ELSE", "EQ", "ESC_SEQ", "EXPONENT", "FALSE", "FIELD", "FOR", "FUNCTION", "GE", "GT", "HEX_DIGIT", "HEX_ESC", "ID", "ID_NAME", "IF", "IN", "INTEGER", "KW", "LBB", "LBRACE", "LBRAKET", "LE", "LINE_BREAK", "LPAR", "LT", "MINUS", "MISSING_VAL", "MOD", "MULT", "NA", "NE", "NEWLINE", "NEXT", "NOT", "NS_GET", "NS_GET_INT", "NULL", "OCTAL_ESC", "OP", "OP_NAME", "OR", "PARMS", "PLUS", "RBRACE", "RBRAKET", "REPEAT", "RIGHT_ARROW", "RPAR", "SEMICOLUMN", "SEQUENCE", "STRING", "SUPER_ARROW", "SUPER_RIGHT_ARROW", "TILDE", "TRUE", "UMINUS", "UNICODE_ESC", "UPLUS", "UTILDE", "VARIATIC", "WHILE", "WS", "'--EOF--'"
    };

    public static final int EOF=-1;
    public static final int T__83=83;
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
    public static final int COMPLEX=17;
    public static final int DD=18;
    public static final int DIV=19;
    public static final int DOUBLE=20;
    public static final int ELSE=21;
    public static final int EQ=22;
    public static final int ESC_SEQ=23;
    public static final int EXPONENT=24;
    public static final int FALSE=25;
    public static final int FIELD=26;
    public static final int FOR=27;
    public static final int FUNCTION=28;
    public static final int GE=29;
    public static final int GT=30;
    public static final int HEX_DIGIT=31;
    public static final int HEX_ESC=32;
    public static final int ID=33;
    public static final int ID_NAME=34;
    public static final int IF=35;
    public static final int IN=36;
    public static final int INTEGER=37;
    public static final int KW=38;
    public static final int LBB=39;
    public static final int LBRACE=40;
    public static final int LBRAKET=41;
    public static final int LE=42;
    public static final int LINE_BREAK=43;
    public static final int LPAR=44;
    public static final int LT=45;
    public static final int MINUS=46;
    public static final int MISSING_VAL=47;
    public static final int MOD=48;
    public static final int MULT=49;
    public static final int NA=50;
    public static final int NE=51;
    public static final int NEWLINE=52;
    public static final int NEXT=53;
    public static final int NOT=54;
    public static final int NS_GET=55;
    public static final int NS_GET_INT=56;
    public static final int NULL=57;
    public static final int OCTAL_ESC=58;
    public static final int OP=59;
    public static final int OP_NAME=60;
    public static final int OR=61;
    public static final int PARMS=62;
    public static final int PLUS=63;
    public static final int RBRACE=64;
    public static final int RBRAKET=65;
    public static final int REPEAT=66;
    public static final int RIGHT_ARROW=67;
    public static final int RPAR=68;
    public static final int SEMICOLUMN=69;
    public static final int SEQUENCE=70;
    public static final int STRING=71;
    public static final int SUPER_ARROW=72;
    public static final int SUPER_RIGHT_ARROW=73;
    public static final int TILDE=74;
    public static final int TRUE=75;
    public static final int UMINUS=76;
    public static final int UNICODE_ESC=77;
    public static final int UPLUS=78;
    public static final int UTILDE=79;
    public static final int VARIATIC=80;
    public static final int WHILE=81;
    public static final int WS=82;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public RParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public RParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
        this.state.ruleMemo = new HashMap[60+1];
         

    }

    @Override
    public String[] getTokenNames() { return RParser.tokenNames; }
    @Override
    public String getGrammarFileName() { return "R.g"; }


        public void display_next_tokens(){
            System.err.print("Allowed tokens: ");
            for(int next: next_tokens())
                System.err.print(tokenNames[next]);
            System.err.println("");
        }
        public int[] next_tokens(){
            return state.following[state._fsp].toArray();
        }



    // $ANTLR start "script"
    // R.g:85:1: script returns [Node v] : n_ (s= statement )* ;
    public final Node script() throws RecognitionException {
        Node v = null;

        int script_StartIndex = input.index();

        Node s =null;


        ArrayList<Node> stmts = new ArrayList<Node>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return v; }

            // R.g:88:2: ( n_ (s= statement )* )
            // R.g:88:4: n_ (s= statement )*
            {
            pushFollow(FOLLOW_n__in_script156);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:88:7: (s= statement )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==BREAK||(LA1_0 >= COMPLEX && LA1_0 <= DD)||LA1_0==DOUBLE||LA1_0==FALSE||(LA1_0 >= FOR && LA1_0 <= FUNCTION)||LA1_0==ID||LA1_0==IF||LA1_0==INTEGER||LA1_0==LBRACE||LA1_0==LPAR||LA1_0==MINUS||LA1_0==NA||(LA1_0 >= NEXT && LA1_0 <= NOT)||LA1_0==NULL||LA1_0==PLUS||LA1_0==REPEAT||LA1_0==STRING||(LA1_0 >= TILDE && LA1_0 <= TRUE)||(LA1_0 >= VARIATIC && LA1_0 <= WHILE)||LA1_0==83) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // R.g:88:8: s= statement
            	    {
            	    pushFollow(FOLLOW_statement_in_script161);
            	    s=statement();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) {stmts.add(s);}

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            if ( state.backtracking==0 ) { v = Sequence.create(stmts);}
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 1, script_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "script"



    // $ANTLR start "interactive"
    // R.g:90:1: interactive returns [Node v] : n_ e= statement ;
    public final Node interactive() throws RecognitionException {
        Node v = null;

        int interactive_StartIndex = input.index();

        Node e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return v; }

            // R.g:91:2: ( n_ e= statement )
            // R.g:91:4: n_ e= statement
            {
            pushFollow(FOLLOW_n__in_interactive179);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_statement_in_interactive183);
            e=statement();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) {v = e;}

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 2, interactive_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "interactive"



    // $ANTLR start "statement"
    // R.g:93:1: statement returns [Node v] : (e= expr_or_assign n | '--EOF--' ( . )* EOF );
    public final Node statement() throws RecognitionException {
        Node v = null;

        int statement_StartIndex = input.index();

        Node e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return v; }

            // R.g:94:2: (e= expr_or_assign n | '--EOF--' ( . )* EOF )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==BREAK||(LA3_0 >= COMPLEX && LA3_0 <= DD)||LA3_0==DOUBLE||LA3_0==FALSE||(LA3_0 >= FOR && LA3_0 <= FUNCTION)||LA3_0==ID||LA3_0==IF||LA3_0==INTEGER||LA3_0==LBRACE||LA3_0==LPAR||LA3_0==MINUS||LA3_0==NA||(LA3_0 >= NEXT && LA3_0 <= NOT)||LA3_0==NULL||LA3_0==PLUS||LA3_0==REPEAT||LA3_0==STRING||(LA3_0 >= TILDE && LA3_0 <= TRUE)||(LA3_0 >= VARIATIC && LA3_0 <= WHILE)) ) {
                alt3=1;
            }
            else if ( (LA3_0==83) ) {
                alt3=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }
            switch (alt3) {
                case 1 :
                    // R.g:94:4: e= expr_or_assign n
                    {
                    pushFollow(FOLLOW_expr_or_assign_in_statement201);
                    e=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n_in_statement203);
                    n();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = e;}

                    }
                    break;
                case 2 :
                    // R.g:95:4: '--EOF--' ( . )* EOF
                    {
                    match(input,83,FOLLOW_83_in_statement210); if (state.failed) return v;

                    // R.g:95:14: ( . )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0 >= AND && LA2_0 <= 83)) ) {
                            alt2=1;
                        }
                        else if ( (LA2_0==EOF) ) {
                            alt2=2;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // R.g:95:14: .
                    	    {
                    	    matchAny(input); if (state.failed) return v;

                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);


                    match(input,EOF,FOLLOW_EOF_in_statement215); if (state.failed) return v;

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 3, statement_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "statement"



    // $ANTLR start "n_"
    // R.g:98:1: n_ : ( NEWLINE | COMMENT )* ;
    public final void n_() throws RecognitionException {
        int n__StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return ; }

            // R.g:98:4: ( ( NEWLINE | COMMENT )* )
            // R.g:98:6: ( NEWLINE | COMMENT )*
            {
            // R.g:98:6: ( NEWLINE | COMMENT )*
            loop4:
            do {
                int alt4=2;
                int LA4_0 = input.LA(1);

                if ( (LA4_0==COMMENT||LA4_0==NEWLINE) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // R.g:
            	    {
            	    if ( input.LA(1)==COMMENT||input.LA(1)==NEWLINE ) {
            	        input.consume();
            	        state.errorRecovery=false;
            	        state.failed=false;
            	    }
            	    else {
            	        if (state.backtracking>0) {state.failed=true; return ;}
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 4, n__StartIndex); }

        }
        return ;
    }
    // $ANTLR end "n_"



    // $ANTLR start "n"
    // R.g:99:1: n : ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLUMN n_ );
    public final void n() throws RecognitionException {
        int n_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }

            // R.g:99:3: ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLUMN n_ )
            int alt6=3;
            switch ( input.LA(1) ) {
            case COMMENT:
            case NEWLINE:
                {
                alt6=1;
                }
                break;
            case EOF:
                {
                alt6=2;
                }
                break;
            case SEMICOLUMN:
                {
                alt6=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 6, 0, input);

                throw nvae;

            }

            switch (alt6) {
                case 1 :
                    // R.g:99:5: ( NEWLINE | COMMENT )+
                    {
                    // R.g:99:5: ( NEWLINE | COMMENT )+
                    int cnt5=0;
                    loop5:
                    do {
                        int alt5=2;
                        int LA5_0 = input.LA(1);

                        if ( (LA5_0==COMMENT||LA5_0==NEWLINE) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // R.g:
                    	    {
                    	    if ( input.LA(1)==COMMENT||input.LA(1)==NEWLINE ) {
                    	        input.consume();
                    	        state.errorRecovery=false;
                    	        state.failed=false;
                    	    }
                    	    else {
                    	        if (state.backtracking>0) {state.failed=true; return ;}
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    if ( cnt5 >= 1 ) break loop5;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(5, input);
                                throw eee;
                        }
                        cnt5++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // R.g:99:28: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_n250); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // R.g:99:34: SEMICOLUMN n_
                    {
                    match(input,SEMICOLUMN,FOLLOW_SEMICOLUMN_in_n254); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_n256);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 5, n_StartIndex); }

        }
        return ;
    }
    // $ANTLR end "n"



    // $ANTLR start "expr_or_assign"
    // R.g:101:1: expr_or_assign returns [Node v] : a= alter_assign ;
    public final Node expr_or_assign() throws RecognitionException {
        Node v = null;

        int expr_or_assign_StartIndex = input.index();

        Node a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return v; }

            // R.g:102:2: (a= alter_assign )
            // R.g:102:4: a= alter_assign
            {
            pushFollow(FOLLOW_alter_assign_in_expr_or_assign271);
            a=alter_assign();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = a; }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 6, expr_or_assign_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "expr_or_assign"



    // $ANTLR start "expr"
    // R.g:104:1: expr returns [Node v] : a= assign ;
    public final Node expr() throws RecognitionException {
        Node v = null;

        int expr_StartIndex = input.index();

        Node a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return v; }

            // R.g:105:2: (a= assign )
            // R.g:105:4: a= assign
            {
            pushFollow(FOLLOW_assign_in_expr289);
            a=assign();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = a; }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 7, expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "expr"



    // $ANTLR start "expr_wo_assign"
    // R.g:107:1: expr_wo_assign returns [Node v] : (w= while_expr |i= if_expr |f= for_expr |r= repeat_expr |fun= function | NEXT ( ( LPAR )=> LPAR n_ RPAR )? | BREAK ( ( LPAR )=> LPAR n_ RPAR )? );
    public final Node expr_wo_assign() throws RecognitionException {
        Node v = null;

        int expr_wo_assign_StartIndex = input.index();

        Node w =null;

        Node i =null;

        Node f =null;

        Node r =null;

        Node fun =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return v; }

            // R.g:108:2: (w= while_expr |i= if_expr |f= for_expr |r= repeat_expr |fun= function | NEXT ( ( LPAR )=> LPAR n_ RPAR )? | BREAK ( ( LPAR )=> LPAR n_ RPAR )? )
            int alt9=7;
            switch ( input.LA(1) ) {
            case WHILE:
                {
                alt9=1;
                }
                break;
            case IF:
                {
                alt9=2;
                }
                break;
            case FOR:
                {
                alt9=3;
                }
                break;
            case REPEAT:
                {
                alt9=4;
                }
                break;
            case FUNCTION:
                {
                alt9=5;
                }
                break;
            case NEXT:
                {
                alt9=6;
                }
                break;
            case BREAK:
                {
                alt9=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 9, 0, input);

                throw nvae;

            }

            switch (alt9) {
                case 1 :
                    // R.g:108:4: w= while_expr
                    {
                    pushFollow(FOLLOW_while_expr_in_expr_wo_assign308);
                    w=while_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = w; }

                    }
                    break;
                case 2 :
                    // R.g:109:4: i= if_expr
                    {
                    pushFollow(FOLLOW_if_expr_in_expr_wo_assign317);
                    i=if_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = i; }

                    }
                    break;
                case 3 :
                    // R.g:110:4: f= for_expr
                    {
                    pushFollow(FOLLOW_for_expr_in_expr_wo_assign326);
                    f=for_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = f; }

                    }
                    break;
                case 4 :
                    // R.g:111:4: r= repeat_expr
                    {
                    pushFollow(FOLLOW_repeat_expr_in_expr_wo_assign335);
                    r=repeat_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = r; }

                    }
                    break;
                case 5 :
                    // R.g:112:4: fun= function
                    {
                    pushFollow(FOLLOW_function_in_expr_wo_assign344);
                    fun=function();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = fun; }

                    }
                    break;
                case 6 :
                    // R.g:113:4: NEXT ( ( LPAR )=> LPAR n_ RPAR )?
                    {
                    match(input,NEXT,FOLLOW_NEXT_in_expr_wo_assign351); if (state.failed) return v;

                    // R.g:113:9: ( ( LPAR )=> LPAR n_ RPAR )?
                    int alt7=2;
                    alt7 = dfa7.predict(input);
                    switch (alt7) {
                        case 1 :
                            // R.g:113:10: ( LPAR )=> LPAR n_ RPAR
                            {
                            match(input,LPAR,FOLLOW_LPAR_in_expr_wo_assign358); if (state.failed) return v;

                            pushFollow(FOLLOW_n__in_expr_wo_assign360);
                            n_();

                            state._fsp--;
                            if (state.failed) return v;

                            match(input,RPAR,FOLLOW_RPAR_in_expr_wo_assign362); if (state.failed) return v;

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // R.g:114:4: BREAK ( ( LPAR )=> LPAR n_ RPAR )?
                    {
                    match(input,BREAK,FOLLOW_BREAK_in_expr_wo_assign370); if (state.failed) return v;

                    // R.g:114:10: ( ( LPAR )=> LPAR n_ RPAR )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // R.g:114:11: ( LPAR )=> LPAR n_ RPAR
                            {
                            match(input,LPAR,FOLLOW_LPAR_in_expr_wo_assign377); if (state.failed) return v;

                            pushFollow(FOLLOW_n__in_expr_wo_assign379);
                            n_();

                            state._fsp--;
                            if (state.failed) return v;

                            match(input,RPAR,FOLLOW_RPAR_in_expr_wo_assign381); if (state.failed) return v;

                            }
                            break;

                    }


                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 8, expr_wo_assign_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "expr_wo_assign"



    // $ANTLR start "sequence"
    // R.g:116:1: sequence returns [Node v] : LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE ;
    public final Node sequence() throws RecognitionException {
        Node v = null;

        int sequence_StartIndex = input.index();

        Node e =null;


        ArrayList<Node> stmts = new ArrayList<Node>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return v; }

            // R.g:119:2: ( LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE )
            // R.g:119:4: LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE
            {
            match(input,LBRACE,FOLLOW_LBRACE_in_sequence414); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_sequence416);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:119:14: (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==BREAK||(LA12_0 >= COMPLEX && LA12_0 <= DD)||LA12_0==DOUBLE||LA12_0==FALSE||(LA12_0 >= FOR && LA12_0 <= FUNCTION)||LA12_0==ID||LA12_0==IF||LA12_0==INTEGER||LA12_0==LBRACE||LA12_0==LPAR||LA12_0==MINUS||LA12_0==NA||(LA12_0 >= NEXT && LA12_0 <= NOT)||LA12_0==NULL||LA12_0==PLUS||LA12_0==REPEAT||LA12_0==STRING||(LA12_0 >= TILDE && LA12_0 <= TRUE)||(LA12_0 >= VARIATIC && LA12_0 <= WHILE)) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // R.g:119:15: e= expr_or_assign ( n e= expr_or_assign )* ( n )?
                    {
                    pushFollow(FOLLOW_expr_or_assign_in_sequence421);
                    e=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { stmts.add(e); }

                    // R.g:119:50: ( n e= expr_or_assign )*
                    loop10:
                    do {
                        int alt10=2;
                        alt10 = dfa10.predict(input);
                        switch (alt10) {
                    	case 1 :
                    	    // R.g:119:51: n e= expr_or_assign
                    	    {
                    	    pushFollow(FOLLOW_n_in_sequence426);
                    	    n();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    pushFollow(FOLLOW_expr_or_assign_in_sequence430);
                    	    e=expr_or_assign();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    if ( state.backtracking==0 ) { stmts.add(e); }

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    // R.g:119:90: ( n )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==EOF||LA11_0==COMMENT||LA11_0==NEWLINE||LA11_0==SEMICOLUMN) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // R.g:119:90: n
                            {
                            pushFollow(FOLLOW_n_in_sequence436);
                            n();

                            state._fsp--;
                            if (state.failed) return v;

                            }
                            break;

                    }


                    }
                    break;

            }


            match(input,RBRACE,FOLLOW_RBRACE_in_sequence442); if (state.failed) return v;

            }

            if ( state.backtracking==0 ) { v = Sequence.create(stmts);}
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 9, sequence_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "sequence"



    // $ANTLR start "assign"
    // R.g:121:1: assign returns [Node v] : l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |) ;
    public final Node assign() throws RecognitionException {
        Node v = null;

        int assign_StartIndex = input.index();

        Token a=null;
        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return v; }

            // R.g:122:2: (l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |) )
            // R.g:122:4: l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |)
            {
            pushFollow(FOLLOW_tilde_expr_in_assign460);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:123:3: ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |)
            int alt13=5;
            switch ( input.LA(1) ) {
            case ARROW:
                {
                alt13=1;
                }
                break;
            case SUPER_ARROW:
                {
                alt13=2;
                }
                break;
            case RIGHT_ARROW:
                {
                alt13=3;
                }
                break;
            case SUPER_RIGHT_ARROW:
                {
                alt13=4;
                }
                break;
            case EOF:
            case ASSIGN:
            case BREAK:
            case COMMA:
            case COMMENT:
            case COMPLEX:
            case DD:
            case DOUBLE:
            case ELSE:
            case FALSE:
            case FOR:
            case FUNCTION:
            case ID:
            case IF:
            case IN:
            case INTEGER:
            case LBRACE:
            case LPAR:
            case MINUS:
            case NA:
            case NEWLINE:
            case NEXT:
            case NOT:
            case NULL:
            case PLUS:
            case RBRACE:
            case RBRAKET:
            case REPEAT:
            case RPAR:
            case STRING:
            case TILDE:
            case TRUE:
            case VARIATIC:
            case WHILE:
            case 83:
                {
                alt13=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 13, 0, input);

                throw nvae;

            }

            switch (alt13) {
                case 1 :
                    // R.g:123:5: ARROW n_ r= expr
                    {
                    match(input,ARROW,FOLLOW_ARROW_in_assign467); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign469);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign473);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(l, r);}

                    }
                    break;
                case 2 :
                    // R.g:124:5: SUPER_ARROW n_ r= expr
                    {
                    match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_assign481); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign483);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign487);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.createSuper(l, r);}

                    }
                    break;
                case 3 :
                    // R.g:125:5: a= RIGHT_ARROW n_ r= expr
                    {
                    a=(Token)match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_assign497); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign499);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign503);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(r, l);}

                    }
                    break;
                case 4 :
                    // R.g:126:5: a= SUPER_RIGHT_ARROW n_ r= expr
                    {
                    a=(Token)match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_assign513); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign515);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign519);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.createSuper(r, l);}

                    }
                    break;
                case 5 :
                    // R.g:127:5: 
                    {
                    if ( state.backtracking==0 ) { v = l;}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 10, assign_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "assign"



    // $ANTLR start "alter_assign"
    // R.g:130:1: alter_assign returns [Node v] : l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |) ;
    public final Node alter_assign() throws RecognitionException {
        Node v = null;

        int alter_assign_StartIndex = input.index();

        Token a=null;
        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return v; }

            // R.g:131:2: (l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |) )
            // R.g:131:4: l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |)
            {
            pushFollow(FOLLOW_tilde_expr_in_alter_assign547);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:132:3: ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |)
            int alt14=6;
            switch ( input.LA(1) ) {
            case ARROW:
                {
                int LA14_1 = input.LA(2);

                if ( (synpred3_R()) ) {
                    alt14=1;
                }
                else if ( (true) ) {
                    alt14=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    throw nvae;

                }
                }
                break;
            case SUPER_ARROW:
                {
                int LA14_2 = input.LA(2);

                if ( (synpred4_R()) ) {
                    alt14=2;
                }
                else if ( (true) ) {
                    alt14=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 2, input);

                    throw nvae;

                }
                }
                break;
            case RIGHT_ARROW:
                {
                int LA14_3 = input.LA(2);

                if ( (synpred5_R()) ) {
                    alt14=3;
                }
                else if ( (true) ) {
                    alt14=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 3, input);

                    throw nvae;

                }
                }
                break;
            case SUPER_RIGHT_ARROW:
                {
                int LA14_4 = input.LA(2);

                if ( (synpred6_R()) ) {
                    alt14=4;
                }
                else if ( (true) ) {
                    alt14=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 4, input);

                    throw nvae;

                }
                }
                break;
            case ASSIGN:
                {
                int LA14_5 = input.LA(2);

                if ( (synpred7_R()) ) {
                    alt14=5;
                }
                else if ( (true) ) {
                    alt14=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 5, input);

                    throw nvae;

                }
                }
                break;
            case EOF:
            case AND:
            case AT:
            case BITWISEAND:
            case BITWISEOR:
            case CARRET:
            case COLUMN:
            case COMMA:
            case COMMENT:
            case DIV:
            case ELSE:
            case EQ:
            case FIELD:
            case GE:
            case GT:
            case LBB:
            case LBRAKET:
            case LE:
            case LPAR:
            case LT:
            case MINUS:
            case MOD:
            case MULT:
            case NE:
            case NEWLINE:
            case OP:
            case OR:
            case PLUS:
            case RBRACE:
            case RBRAKET:
            case RPAR:
            case SEMICOLUMN:
            case TILDE:
                {
                alt14=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 14, 0, input);

                throw nvae;

            }

            switch (alt14) {
                case 1 :
                    // R.g:132:5: ( ARROW )=> ARROW n_ r= expr_or_assign
                    {
                    match(input,ARROW,FOLLOW_ARROW_in_alter_assign558); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign560);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign564);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(l, r);}

                    }
                    break;
                case 2 :
                    // R.g:133:5: ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign
                    {
                    match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_alter_assign576); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign578);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign582);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = AssignVariable.createSuper(l, r);}

                    }
                    break;
                case 3 :
                    // R.g:134:5: ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign
                    {
                    a=(Token)match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_alter_assign596); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign598);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign602);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(r, l);}

                    }
                    break;
                case 4 :
                    // R.g:135:5: ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign
                    {
                    a=(Token)match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_alter_assign616); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign618);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign622);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = AssignVariable.createSuper(r, l);}

                    }
                    break;
                case 5 :
                    // R.g:136:5: ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign
                    {
                    a=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_alter_assign636); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign638);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign642);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = AssignVariable.create(l, r);}

                    }
                    break;
                case 6 :
                    // R.g:137:5: 
                    {
                    if ( state.backtracking==0 ) { v = l;}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 11, alter_assign_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "alter_assign"



    // $ANTLR start "if_expr"
    // R.g:140:1: if_expr returns [Node v] : IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |) ;
    public final Node if_expr() throws RecognitionException {
        Node v = null;

        int if_expr_StartIndex = input.index();

        Node cond =null;

        Node t =null;

        Node f =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return v; }

            // R.g:141:2: ( IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |) )
            // R.g:142:2: IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |)
            {
            match(input,IF,FOLLOW_IF_in_if_expr669); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr671);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_if_expr673); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr675);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_if_expr679);
            cond=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr681);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,RPAR,FOLLOW_RPAR_in_if_expr683); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr685);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_if_expr689);
            t=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            // R.g:143:2: ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |)
            int alt15=2;
            switch ( input.LA(1) ) {
            case COMMENT:
            case NEWLINE:
                {
                int LA15_1 = input.LA(2);

                if ( (synpred8_R()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 1, input);

                    throw nvae;

                }
                }
                break;
            case ELSE:
                {
                int LA15_2 = input.LA(2);

                if ( (synpred8_R()) ) {
                    alt15=1;
                }
                else if ( (true) ) {
                    alt15=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 15, 2, input);

                    throw nvae;

                }
                }
                break;
            case EOF:
            case AND:
            case ARROW:
            case ASSIGN:
            case AT:
            case BITWISEAND:
            case BITWISEOR:
            case CARRET:
            case COLUMN:
            case COMMA:
            case DIV:
            case EQ:
            case FIELD:
            case GE:
            case GT:
            case LBB:
            case LBRAKET:
            case LE:
            case LPAR:
            case LT:
            case MINUS:
            case MOD:
            case MULT:
            case NE:
            case OP:
            case OR:
            case PLUS:
            case RBRACE:
            case RBRAKET:
            case RIGHT_ARROW:
            case RPAR:
            case SEMICOLUMN:
            case SUPER_ARROW:
            case SUPER_RIGHT_ARROW:
            case TILDE:
                {
                alt15=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 15, 0, input);

                throw nvae;

            }

            switch (alt15) {
                case 1 :
                    // R.g:143:3: ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    {
                    // R.g:143:14: ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    // R.g:143:58: n_ ELSE n_ f= expr_or_assign
                    {
                    pushFollow(FOLLOW_n__in_if_expr715);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,ELSE,FOLLOW_ELSE_in_if_expr717); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_if_expr719);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_if_expr723);
                    f=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = If.create(cond, t, f);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:144:7: 
                    {
                    if ( state.backtracking==0 ) {v = If.create(cond, t);}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 12, if_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "if_expr"



    // $ANTLR start "while_expr"
    // R.g:147:1: while_expr returns [Node v] : WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final Node while_expr() throws RecognitionException {
        Node v = null;

        int while_expr_StartIndex = input.index();

        Node c =null;

        Node body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return v; }

            // R.g:148:2: ( WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:148:4: WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign
            {
            match(input,WHILE,FOLLOW_WHILE_in_while_expr751); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr753);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_while_expr755); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr757);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_while_expr761);
            c=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr763);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,RPAR,FOLLOW_RPAR_in_while_expr765); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr767);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_while_expr771);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = Loop.create(c, body); }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 13, while_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "while_expr"



    // $ANTLR start "for_expr"
    // R.g:150:1: for_expr returns [Node v] : FOR n_ LPAR n_ ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final Node for_expr() throws RecognitionException {
        Node v = null;

        int for_expr_StartIndex = input.index();

        Node in =null;

        Node body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return v; }

            // R.g:151:2: ( FOR n_ LPAR n_ ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:151:4: FOR n_ LPAR n_ ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign
            {
            match(input,FOR,FOLLOW_FOR_in_for_expr787); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr789);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_for_expr791); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr793);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,ID,FOLLOW_ID_in_for_expr795); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr797);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,IN,FOLLOW_IN_in_for_expr799); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr801);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_for_expr805);
            in=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr807);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,RPAR,FOLLOW_RPAR_in_for_expr809); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr811);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_for_expr815);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 14, for_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "for_expr"



    // $ANTLR start "repeat_expr"
    // R.g:153:1: repeat_expr returns [Node v] : REPEAT n_ body= expr_or_assign ;
    public final Node repeat_expr() throws RecognitionException {
        Node v = null;

        int repeat_expr_StartIndex = input.index();

        Node body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return v; }

            // R.g:154:2: ( REPEAT n_ body= expr_or_assign )
            // R.g:154:4: REPEAT n_ body= expr_or_assign
            {
            match(input,REPEAT,FOLLOW_REPEAT_in_repeat_expr830); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_repeat_expr832);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_repeat_expr836);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) {v = Loop.create(body); }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 15, repeat_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "repeat_expr"



    // $ANTLR start "function"
    // R.g:156:1: function returns [Node v] : FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign ;
    public final Node function() throws RecognitionException {
        Node v = null;

        int function_StartIndex = input.index();

        Node body =null;


         ArgumentList l = new ArgumentList.Default(); 
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return v; }

            // R.g:158:2: ( FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign )
            // R.g:158:4: FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function857); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_function859);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_function861); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_function864);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:158:25: ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==DD||LA17_0==ID||LA17_0==VARIATIC) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // R.g:158:26: par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_
                    {
                    pushFollow(FOLLOW_par_decl_in_function867);
                    par_decl(l);

                    state._fsp--;
                    if (state.failed) return v;

                    // R.g:158:38: ( n_ COMMA n_ par_decl[l] )*
                    loop16:
                    do {
                        int alt16=2;
                        alt16 = dfa16.predict(input);
                        switch (alt16) {
                    	case 1 :
                    	    // R.g:158:39: n_ COMMA n_ par_decl[l]
                    	    {
                    	    pushFollow(FOLLOW_n__in_function871);
                    	    n_();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    match(input,COMMA,FOLLOW_COMMA_in_function873); if (state.failed) return v;

                    	    pushFollow(FOLLOW_n__in_function875);
                    	    n_();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    pushFollow(FOLLOW_par_decl_in_function877);
                    	    par_decl(l);

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);


                    pushFollow(FOLLOW_n__in_function882);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }


            match(input,RPAR,FOLLOW_RPAR_in_function886); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_function888);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_function892);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = Function.create(l, body); }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 16, function_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "function"



    // $ANTLR start "par_decl"
    // R.g:160:1: par_decl[ArgumentList l] : (i= ID |i= ID n_ ASSIGN n_ e= expr |v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr );
    public final void par_decl(ArgumentList l) throws RecognitionException {
        int par_decl_StartIndex = input.index();

        Token i=null;
        Token v=null;
        Node e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }

            // R.g:161:2: (i= ID |i= ID n_ ASSIGN n_ e= expr |v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr )
            int alt18=6;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // R.g:161:4: i= ID
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_par_decl909); if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add((i!=null?i.getText():null), null); }

                    }
                    break;
                case 2 :
                    // R.g:162:4: i= ID n_ ASSIGN n_ e= expr
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_par_decl919); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl921);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl923); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl925);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_par_decl929);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add((i!=null?i.getText():null), e); }

                    }
                    break;
                case 3 :
                    // R.g:163:4: v= VARIATIC
                    {
                    v=(Token)match(input,VARIATIC,FOLLOW_VARIATIC_in_par_decl938); if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add((v!=null?v.getText():null), null); }

                    }
                    break;
                case 4 :
                    // R.g:168:5: VARIATIC n_ ASSIGN n_ expr
                    {
                    match(input,VARIATIC,FOLLOW_VARIATIC_in_par_decl988); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl990);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl992); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl994);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_par_decl996);
                    expr();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // R.g:169:5: DD
                    {
                    match(input,DD,FOLLOW_DD_in_par_decl1002); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // R.g:170:5: DD n_ ASSIGN n_ expr
                    {
                    match(input,DD,FOLLOW_DD_in_par_decl1008); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl1010);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl1012); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl1014);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_par_decl1016);
                    expr();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 17, par_decl_StartIndex); }

        }
        return ;
    }
    // $ANTLR end "par_decl"



    // $ANTLR start "tilde_expr"
    // R.g:172:1: tilde_expr returns [Node v] : l= or_expr ( ( ( TILDE )=> TILDE n_ r= tilde_expr ) |) ;
    public final Node tilde_expr() throws RecognitionException {
        Node v = null;

        int tilde_expr_StartIndex = input.index();

        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return v; }

            // R.g:173:2: (l= or_expr ( ( ( TILDE )=> TILDE n_ r= tilde_expr ) |) )
            // R.g:173:4: l= or_expr ( ( ( TILDE )=> TILDE n_ r= tilde_expr ) |)
            {
            pushFollow(FOLLOW_or_expr_in_tilde_expr1032);
            l=or_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:174:2: ( ( ( TILDE )=> TILDE n_ r= tilde_expr ) |)
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==TILDE) ) {
                int LA19_1 = input.LA(2);

                if ( (synpred9_R()) ) {
                    alt19=1;
                }
                else if ( (true) ) {
                    alt19=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 19, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA19_0==EOF||(LA19_0 >= AND && LA19_0 <= BITWISEOR)||(LA19_0 >= CARRET && LA19_0 <= COMMENT)||LA19_0==DIV||(LA19_0 >= ELSE && LA19_0 <= EQ)||LA19_0==FIELD||(LA19_0 >= GE && LA19_0 <= GT)||LA19_0==LBB||(LA19_0 >= LBRAKET && LA19_0 <= LE)||(LA19_0 >= LPAR && LA19_0 <= MINUS)||(LA19_0 >= MOD && LA19_0 <= MULT)||(LA19_0 >= NE && LA19_0 <= NEWLINE)||LA19_0==OP||LA19_0==OR||(LA19_0 >= PLUS && LA19_0 <= RBRAKET)||(LA19_0 >= RIGHT_ARROW && LA19_0 <= SEMICOLUMN)||(LA19_0 >= SUPER_ARROW && LA19_0 <= SUPER_RIGHT_ARROW)) ) {
                alt19=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 19, 0, input);

                throw nvae;

            }
            switch (alt19) {
                case 1 :
                    // R.g:174:4: ( ( TILDE )=> TILDE n_ r= tilde_expr )
                    {
                    // R.g:174:4: ( ( TILDE )=> TILDE n_ r= tilde_expr )
                    // R.g:174:5: ( TILDE )=> TILDE n_ r= tilde_expr
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_tilde_expr1043); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_tilde_expr1045);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_tilde_expr_in_tilde_expr1049);
                    r=tilde_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperation.create(BinaryOperator.ADD, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:175:4: 
                    {
                    if ( state.backtracking==0 ) {v =l;}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 18, tilde_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "tilde_expr"



    // $ANTLR start "or_expr"
    // R.g:177:1: or_expr returns [Node v] : l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= or_expr ) |) ;
    public final Node or_expr() throws RecognitionException {
        Node v = null;

        int or_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return v; }

            // R.g:178:2: (l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= or_expr ) |) )
            // R.g:178:4: l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= or_expr ) |)
            {
            pushFollow(FOLLOW_and_expr_in_or_expr1075);
            l=and_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:179:2: ( ( ( or_operator )=>op= or_operator n_ r= or_expr ) |)
            int alt20=2;
            switch ( input.LA(1) ) {
            case OR:
                {
                int LA20_1 = input.LA(2);

                if ( (synpred10_R()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 1, input);

                    throw nvae;

                }
                }
                break;
            case BITWISEOR:
                {
                int LA20_2 = input.LA(2);

                if ( (synpred10_R()) ) {
                    alt20=1;
                }
                else if ( (true) ) {
                    alt20=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 20, 2, input);

                    throw nvae;

                }
                }
                break;
            case EOF:
            case AND:
            case ARROW:
            case ASSIGN:
            case AT:
            case BITWISEAND:
            case CARRET:
            case COLUMN:
            case COMMA:
            case COMMENT:
            case DIV:
            case ELSE:
            case EQ:
            case FIELD:
            case GE:
            case GT:
            case LBB:
            case LBRAKET:
            case LE:
            case LPAR:
            case LT:
            case MINUS:
            case MOD:
            case MULT:
            case NE:
            case NEWLINE:
            case OP:
            case PLUS:
            case RBRACE:
            case RBRAKET:
            case RIGHT_ARROW:
            case RPAR:
            case SEMICOLUMN:
            case SUPER_ARROW:
            case SUPER_RIGHT_ARROW:
            case TILDE:
                {
                alt20=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;

            }

            switch (alt20) {
                case 1 :
                    // R.g:179:3: ( ( or_operator )=>op= or_operator n_ r= or_expr )
                    {
                    // R.g:179:3: ( ( or_operator )=>op= or_operator n_ r= or_expr )
                    // R.g:179:4: ( or_operator )=>op= or_operator n_ r= or_expr
                    {
                    pushFollow(FOLLOW_or_operator_in_or_expr1086);
                    op=or_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_or_expr1088);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_or_expr_in_or_expr1092);
                    r=or_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperation.create(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:180:7: 
                    {
                    if ( state.backtracking==0 ) {v =l;}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 19, or_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "or_expr"



    // $ANTLR start "and_expr"
    // R.g:182:1: and_expr returns [Node v] : l= comp_expr ( ( ( and_operator )=>op= and_operator n_ r= and_expr ) |) ;
    public final Node and_expr() throws RecognitionException {
        Node v = null;

        int and_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return v; }

            // R.g:183:2: (l= comp_expr ( ( ( and_operator )=>op= and_operator n_ r= and_expr ) |) )
            // R.g:183:4: l= comp_expr ( ( ( and_operator )=>op= and_operator n_ r= and_expr ) |)
            {
            pushFollow(FOLLOW_comp_expr_in_and_expr1122);
            l=comp_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:184:5: ( ( ( and_operator )=>op= and_operator n_ r= and_expr ) |)
            int alt21=2;
            switch ( input.LA(1) ) {
            case AND:
                {
                int LA21_1 = input.LA(2);

                if ( (synpred11_R()) ) {
                    alt21=1;
                }
                else if ( (true) ) {
                    alt21=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 1, input);

                    throw nvae;

                }
                }
                break;
            case BITWISEAND:
                {
                int LA21_2 = input.LA(2);

                if ( (synpred11_R()) ) {
                    alt21=1;
                }
                else if ( (true) ) {
                    alt21=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 21, 2, input);

                    throw nvae;

                }
                }
                break;
            case EOF:
            case ARROW:
            case ASSIGN:
            case AT:
            case BITWISEOR:
            case CARRET:
            case COLUMN:
            case COMMA:
            case COMMENT:
            case DIV:
            case ELSE:
            case EQ:
            case FIELD:
            case GE:
            case GT:
            case LBB:
            case LBRAKET:
            case LE:
            case LPAR:
            case LT:
            case MINUS:
            case MOD:
            case MULT:
            case NE:
            case NEWLINE:
            case OP:
            case OR:
            case PLUS:
            case RBRACE:
            case RBRAKET:
            case RIGHT_ARROW:
            case RPAR:
            case SEMICOLUMN:
            case SUPER_ARROW:
            case SUPER_RIGHT_ARROW:
            case TILDE:
                {
                alt21=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;

            }

            switch (alt21) {
                case 1 :
                    // R.g:184:6: ( ( and_operator )=>op= and_operator n_ r= and_expr )
                    {
                    // R.g:184:6: ( ( and_operator )=>op= and_operator n_ r= and_expr )
                    // R.g:184:7: ( and_operator )=>op= and_operator n_ r= and_expr
                    {
                    pushFollow(FOLLOW_and_operator_in_and_expr1136);
                    op=and_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_and_expr1138);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_and_expr_in_and_expr1142);
                    r=and_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperation.create(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:185:7: 
                    {
                    if ( state.backtracking==0 ) {v =l;}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 20, and_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "and_expr"



    // $ANTLR start "comp_expr"
    // R.g:187:1: comp_expr returns [Node v] : l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= comp_expr ) |) ;
    public final Node comp_expr() throws RecognitionException {
        Node v = null;

        int comp_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return v; }

            // R.g:188:2: (l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= comp_expr ) |) )
            // R.g:188:4: l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= comp_expr ) |)
            {
            pushFollow(FOLLOW_add_expr_in_comp_expr1171);
            l=add_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:189:5: ( ( ( comp_operator )=>op= comp_operator n_ r= comp_expr ) |)
            int alt22=2;
            switch ( input.LA(1) ) {
            case GT:
                {
                int LA22_1 = input.LA(2);

                if ( (synpred12_R()) ) {
                    alt22=1;
                }
                else if ( (true) ) {
                    alt22=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 1, input);

                    throw nvae;

                }
                }
                break;
            case GE:
                {
                int LA22_2 = input.LA(2);

                if ( (synpred12_R()) ) {
                    alt22=1;
                }
                else if ( (true) ) {
                    alt22=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 2, input);

                    throw nvae;

                }
                }
                break;
            case LT:
                {
                int LA22_3 = input.LA(2);

                if ( (synpred12_R()) ) {
                    alt22=1;
                }
                else if ( (true) ) {
                    alt22=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 3, input);

                    throw nvae;

                }
                }
                break;
            case LE:
                {
                int LA22_4 = input.LA(2);

                if ( (synpred12_R()) ) {
                    alt22=1;
                }
                else if ( (true) ) {
                    alt22=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 4, input);

                    throw nvae;

                }
                }
                break;
            case EQ:
                {
                int LA22_5 = input.LA(2);

                if ( (synpred12_R()) ) {
                    alt22=1;
                }
                else if ( (true) ) {
                    alt22=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 5, input);

                    throw nvae;

                }
                }
                break;
            case NE:
                {
                int LA22_6 = input.LA(2);

                if ( (synpred12_R()) ) {
                    alt22=1;
                }
                else if ( (true) ) {
                    alt22=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 22, 6, input);

                    throw nvae;

                }
                }
                break;
            case EOF:
            case AND:
            case ARROW:
            case ASSIGN:
            case AT:
            case BITWISEAND:
            case BITWISEOR:
            case CARRET:
            case COLUMN:
            case COMMA:
            case COMMENT:
            case DIV:
            case ELSE:
            case FIELD:
            case LBB:
            case LBRAKET:
            case LPAR:
            case MINUS:
            case MOD:
            case MULT:
            case NEWLINE:
            case OP:
            case OR:
            case PLUS:
            case RBRACE:
            case RBRAKET:
            case RIGHT_ARROW:
            case RPAR:
            case SEMICOLUMN:
            case SUPER_ARROW:
            case SUPER_RIGHT_ARROW:
            case TILDE:
                {
                alt22=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;

            }

            switch (alt22) {
                case 1 :
                    // R.g:189:6: ( ( comp_operator )=>op= comp_operator n_ r= comp_expr )
                    {
                    // R.g:189:6: ( ( comp_operator )=>op= comp_operator n_ r= comp_expr )
                    // R.g:189:7: ( comp_operator )=>op= comp_operator n_ r= comp_expr
                    {
                    pushFollow(FOLLOW_comp_operator_in_comp_expr1186);
                    op=comp_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_comp_expr1188);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_comp_expr_in_comp_expr1192);
                    r=comp_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = BinaryOperation.create(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:190:7: 
                    {
                    if ( state.backtracking==0 ) {v =l;}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 21, comp_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "comp_expr"



    // $ANTLR start "add_expr"
    // R.g:192:1: add_expr returns [Node v] : l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )* ;
    public final Node add_expr() throws RecognitionException {
        Node v = null;

        int add_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return v; }

            // R.g:193:2: (l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )* )
            // R.g:193:4: l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            {
            pushFollow(FOLLOW_mult_expr_in_add_expr1224);
            l=mult_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:194:3: ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==PLUS) ) {
                    int LA23_2 = input.LA(2);

                    if ( (synpred13_R()) ) {
                        alt23=1;
                    }


                }
                else if ( (LA23_0==MINUS) ) {
                    int LA23_3 = input.LA(2);

                    if ( (synpred13_R()) ) {
                        alt23=1;
                    }


                }


                switch (alt23) {
            	case 1 :
            	    // R.g:194:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    {
            	    // R.g:194:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    // R.g:194:5: ( add_operator )=>op= add_operator n_ r= mult_expr
            	    {
            	    pushFollow(FOLLOW_add_operator_in_add_expr1238);
            	    op=add_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_add_expr1240);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_mult_expr_in_add_expr1244);
            	    r=mult_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) { v = BinaryOperation.create(op, v, r);}

            	    }


            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 22, add_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "add_expr"



    // $ANTLR start "mult_expr"
    // R.g:197:1: mult_expr returns [Node v] : l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )* ;
    public final Node mult_expr() throws RecognitionException {
        Node v = null;

        int mult_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return v; }

            // R.g:198:2: (l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )* )
            // R.g:198:4: l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            {
            pushFollow(FOLLOW_operator_expr_in_mult_expr1267);
            l=operator_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:199:2: ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            loop24:
            do {
                int alt24=2;
                switch ( input.LA(1) ) {
                case MULT:
                    {
                    int LA24_2 = input.LA(2);

                    if ( (synpred14_R()) ) {
                        alt24=1;
                    }


                    }
                    break;
                case DIV:
                    {
                    int LA24_3 = input.LA(2);

                    if ( (synpred14_R()) ) {
                        alt24=1;
                    }


                    }
                    break;
                case MOD:
                    {
                    int LA24_4 = input.LA(2);

                    if ( (synpred14_R()) ) {
                        alt24=1;
                    }


                    }
                    break;

                }

                switch (alt24) {
            	case 1 :
            	    // R.g:199:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    {
            	    // R.g:199:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    // R.g:199:4: ( mult_operator )=>op= mult_operator n_ r= operator_expr
            	    {
            	    pushFollow(FOLLOW_mult_operator_in_mult_expr1279);
            	    op=mult_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_mult_expr1281);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_operator_expr_in_mult_expr1285);
            	    r=operator_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) { v = BinaryOperation.create(op, v, r);}

            	    }


            	    }
            	    break;

            	default :
            	    break loop24;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 23, mult_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "mult_expr"



    // $ANTLR start "operator_expr"
    // R.g:201:1: operator_expr returns [Node v] : l= column_expr ( ( ( OP )=>op= OP n_ r= operator_expr ) |) ;
    public final Node operator_expr() throws RecognitionException {
        Node v = null;

        int operator_expr_StartIndex = input.index();

        Token op=null;
        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return v; }

            // R.g:202:2: (l= column_expr ( ( ( OP )=>op= OP n_ r= operator_expr ) |) )
            // R.g:202:4: l= column_expr ( ( ( OP )=>op= OP n_ r= operator_expr ) |)
            {
            pushFollow(FOLLOW_column_expr_in_operator_expr1307);
            l=column_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:203:2: ( ( ( OP )=>op= OP n_ r= operator_expr ) |)
            int alt25=2;
            int LA25_0 = input.LA(1);

            if ( (LA25_0==OP) ) {
                int LA25_1 = input.LA(2);

                if ( (synpred15_R()) ) {
                    alt25=1;
                }
                else if ( (true) ) {
                    alt25=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 25, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA25_0==EOF||(LA25_0 >= AND && LA25_0 <= BITWISEOR)||(LA25_0 >= CARRET && LA25_0 <= COMMENT)||LA25_0==DIV||(LA25_0 >= ELSE && LA25_0 <= EQ)||LA25_0==FIELD||(LA25_0 >= GE && LA25_0 <= GT)||LA25_0==LBB||(LA25_0 >= LBRAKET && LA25_0 <= LE)||(LA25_0 >= LPAR && LA25_0 <= MINUS)||(LA25_0 >= MOD && LA25_0 <= MULT)||(LA25_0 >= NE && LA25_0 <= NEWLINE)||LA25_0==OR||(LA25_0 >= PLUS && LA25_0 <= RBRAKET)||(LA25_0 >= RIGHT_ARROW && LA25_0 <= SEMICOLUMN)||(LA25_0 >= SUPER_ARROW && LA25_0 <= TILDE)) ) {
                alt25=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 25, 0, input);

                throw nvae;

            }
            switch (alt25) {
                case 1 :
                    // R.g:203:3: ( ( OP )=>op= OP n_ r= operator_expr )
                    {
                    // R.g:203:3: ( ( OP )=>op= OP n_ r= operator_expr )
                    // R.g:203:4: ( OP )=>op= OP n_ r= operator_expr
                    {
                    op=(Token)match(input,OP,FOLLOW_OP_in_operator_expr1318); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_operator_expr1320);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_operator_expr_in_operator_expr1324);
                    r=operator_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = null ; /*BinaryOperation.create(op, l, r); */ }

                    }


                    }
                    break;
                case 2 :
                    // R.g:204:7: 
                    {
                    if ( state.backtracking==0 ) {v =l;}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 24, operator_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "operator_expr"



    // $ANTLR start "column_expr"
    // R.g:206:1: column_expr returns [Node v] : l= power_expr ( ( ( COLUMN )=>op= COLUMN n_ r= column_expr ) |) ;
    public final Node column_expr() throws RecognitionException {
        Node v = null;

        int column_expr_StartIndex = input.index();

        Token op=null;
        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return v; }

            // R.g:207:2: (l= power_expr ( ( ( COLUMN )=>op= COLUMN n_ r= column_expr ) |) )
            // R.g:207:4: l= power_expr ( ( ( COLUMN )=>op= COLUMN n_ r= column_expr ) |)
            {
            pushFollow(FOLLOW_power_expr_in_column_expr1353);
            l=power_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:208:2: ( ( ( COLUMN )=>op= COLUMN n_ r= column_expr ) |)
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==COLUMN) ) {
                int LA26_1 = input.LA(2);

                if ( (synpred16_R()) ) {
                    alt26=1;
                }
                else if ( (true) ) {
                    alt26=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 26, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA26_0==EOF||(LA26_0 >= AND && LA26_0 <= BITWISEOR)||LA26_0==CARRET||(LA26_0 >= COMMA && LA26_0 <= COMMENT)||LA26_0==DIV||(LA26_0 >= ELSE && LA26_0 <= EQ)||LA26_0==FIELD||(LA26_0 >= GE && LA26_0 <= GT)||LA26_0==LBB||(LA26_0 >= LBRAKET && LA26_0 <= LE)||(LA26_0 >= LPAR && LA26_0 <= MINUS)||(LA26_0 >= MOD && LA26_0 <= MULT)||(LA26_0 >= NE && LA26_0 <= NEWLINE)||LA26_0==OP||LA26_0==OR||(LA26_0 >= PLUS && LA26_0 <= RBRAKET)||(LA26_0 >= RIGHT_ARROW && LA26_0 <= SEMICOLUMN)||(LA26_0 >= SUPER_ARROW && LA26_0 <= TILDE)) ) {
                alt26=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;

            }
            switch (alt26) {
                case 1 :
                    // R.g:208:3: ( ( COLUMN )=>op= COLUMN n_ r= column_expr )
                    {
                    // R.g:208:3: ( ( COLUMN )=>op= COLUMN n_ r= column_expr )
                    // R.g:208:4: ( COLUMN )=>op= COLUMN n_ r= column_expr
                    {
                    op=(Token)match(input,COLUMN,FOLLOW_COLUMN_in_column_expr1364); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_column_expr1366);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_column_expr_in_column_expr1370);
                    r=column_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = BinaryOperation.create(BinaryOperator.COLUMN, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:209:7: 
                    {
                    if ( state.backtracking==0 ) {v =l;}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 25, column_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "column_expr"



    // $ANTLR start "power_expr"
    // R.g:211:1: power_expr returns [Node v] : l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |) ;
    public final Node power_expr() throws RecognitionException {
        Node v = null;

        int power_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return v; }

            // R.g:212:2: (l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |) )
            // R.g:212:4: l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |)
            {
            pushFollow(FOLLOW_unary_expression_in_power_expr1400);
            l=unary_expression();

            state._fsp--;
            if (state.failed) return v;

            // R.g:213:5: ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |)
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==CARRET) ) {
                int LA27_1 = input.LA(2);

                if ( (synpred17_R()) ) {
                    alt27=1;
                }
                else if ( (true) ) {
                    alt27=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 27, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA27_0==EOF||(LA27_0 >= AND && LA27_0 <= BITWISEOR)||(LA27_0 >= COLUMN && LA27_0 <= COMMENT)||LA27_0==DIV||(LA27_0 >= ELSE && LA27_0 <= EQ)||LA27_0==FIELD||(LA27_0 >= GE && LA27_0 <= GT)||LA27_0==LBB||(LA27_0 >= LBRAKET && LA27_0 <= LE)||(LA27_0 >= LPAR && LA27_0 <= MINUS)||(LA27_0 >= MOD && LA27_0 <= MULT)||(LA27_0 >= NE && LA27_0 <= NEWLINE)||LA27_0==OP||LA27_0==OR||(LA27_0 >= PLUS && LA27_0 <= RBRAKET)||(LA27_0 >= RIGHT_ARROW && LA27_0 <= SEMICOLUMN)||(LA27_0 >= SUPER_ARROW && LA27_0 <= TILDE)) ) {
                alt27=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;

            }
            switch (alt27) {
                case 1 :
                    // R.g:213:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    {
                    // R.g:213:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    // R.g:213:7: ( power_operator )=>op= power_operator n_ r= power_expr
                    {
                    pushFollow(FOLLOW_power_operator_in_power_expr1414);
                    op=power_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_power_expr1416);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_power_expr_in_power_expr1420);
                    r=power_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = BinaryOperation.create(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:214:7: 
                    {
                    if ( state.backtracking==0 ) {v =l;}

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 26, power_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "power_expr"



    // $ANTLR start "unary_expression"
    // R.g:216:1: unary_expression returns [Node v] : ( NOT n_ l= unary_expression | PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression |b= basic_expr );
    public final Node unary_expression() throws RecognitionException {
        Node v = null;

        int unary_expression_StartIndex = input.index();

        Node l =null;

        Node b =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return v; }

            // R.g:217:2: ( NOT n_ l= unary_expression | PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression |b= basic_expr )
            int alt28=5;
            switch ( input.LA(1) ) {
            case NOT:
                {
                alt28=1;
                }
                break;
            case PLUS:
                {
                alt28=2;
                }
                break;
            case MINUS:
                {
                alt28=3;
                }
                break;
            case TILDE:
                {
                alt28=4;
                }
                break;
            case BREAK:
            case COMPLEX:
            case DD:
            case DOUBLE:
            case FALSE:
            case FOR:
            case FUNCTION:
            case ID:
            case IF:
            case INTEGER:
            case LBRACE:
            case LPAR:
            case NA:
            case NEXT:
            case NULL:
            case REPEAT:
            case STRING:
            case TRUE:
            case VARIATIC:
            case WHILE:
                {
                alt28=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;

            }

            switch (alt28) {
                case 1 :
                    // R.g:217:4: NOT n_ l= unary_expression
                    {
                    match(input,NOT,FOLLOW_NOT_in_unary_expression1451); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1453);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1457);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.NOT, l);}

                    }
                    break;
                case 2 :
                    // R.g:218:4: PLUS n_ l= unary_expression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unary_expression1464); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1466);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1470);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.PLUS, l);}

                    }
                    break;
                case 3 :
                    // R.g:219:4: MINUS n_ l= unary_expression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unary_expression1477); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1479);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1483);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.MINUS, l);}

                    }
                    break;
                case 4 :
                    // R.g:220:4: TILDE n_ l= unary_expression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unary_expression1490); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1492);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1496);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.MODEL, l);}

                    }
                    break;
                case 5 :
                    // R.g:221:4: b= basic_expr
                    {
                    pushFollow(FOLLOW_basic_expr_in_unary_expression1505);
                    b=basic_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v =b; }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 27, unary_expression_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "unary_expression"



    // $ANTLR start "basic_expr"
    // R.g:223:1: basic_expr returns [Node v] : lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) ;
    public final Node basic_expr() throws RecognitionException {
        Node v = null;

        int basic_expr_StartIndex = input.index();

        Node lhs =null;

        Node subset =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return v; }

            // R.g:224:2: (lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) )
            // R.g:224:4: lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            {
            pushFollow(FOLLOW_simple_expr_in_basic_expr1523);
            lhs=simple_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = lhs; }

            // R.g:225:2: ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==FIELD) ) {
                int LA30_1 = input.LA(2);

                if ( (true) ) {
                    alt30=1;
                }
                else if ( (synpred19_R()) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA30_0==AT) ) {
                int LA30_2 = input.LA(2);

                if ( (true) ) {
                    alt30=1;
                }
                else if ( (synpred19_R()) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 2, input);

                    throw nvae;

                }
            }
            else if ( (LA30_0==LBRAKET) ) {
                int LA30_3 = input.LA(2);

                if ( (true) ) {
                    alt30=1;
                }
                else if ( (synpred19_R()) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 3, input);

                    throw nvae;

                }
            }
            else if ( (LA30_0==LBB) ) {
                int LA30_4 = input.LA(2);

                if ( (true) ) {
                    alt30=1;
                }
                else if ( (synpred19_R()) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 4, input);

                    throw nvae;

                }
            }
            else if ( (LA30_0==LPAR) ) {
                int LA30_5 = input.LA(2);

                if ( (true) ) {
                    alt30=1;
                }
                else if ( (synpred19_R()) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 5, input);

                    throw nvae;

                }
            }
            else if ( (LA30_0==CARRET) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==COLUMN) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==OP) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==MULT) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==DIV) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==MOD) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==PLUS) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==MINUS) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==GT) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==GE) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==LT) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==LE) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==EQ) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==NE) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==AND) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==BITWISEAND) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==OR) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==BITWISEOR) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==TILDE) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==ARROW) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==SUPER_ARROW) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==RIGHT_ARROW) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==SUPER_RIGHT_ARROW) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==COMMENT||LA30_0==NEWLINE) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==COMMA) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==RPAR) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==RBRAKET) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==ASSIGN) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==EOF) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==SEMICOLUMN) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==RBRACE) && (synpred19_R())) {
                alt30=2;
            }
            else if ( (LA30_0==ELSE) && (synpred19_R())) {
                alt30=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;

            }
            switch (alt30) {
                case 1 :
                    // R.g:225:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    {
                    // R.g:225:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    int cnt29=0;
                    loop29:
                    do {
                        int alt29=2;
                        switch ( input.LA(1) ) {
                        case FIELD:
                            {
                            int LA29_2 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt29=1;
                            }


                            }
                            break;
                        case AT:
                            {
                            int LA29_3 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt29=1;
                            }


                            }
                            break;
                        case LBRAKET:
                            {
                            int LA29_4 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt29=1;
                            }


                            }
                            break;
                        case LBB:
                            {
                            int LA29_5 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt29=1;
                            }


                            }
                            break;
                        case LPAR:
                            {
                            int LA29_6 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt29=1;
                            }


                            }
                            break;

                        }

                        switch (alt29) {
                    	case 1 :
                    	    // R.g:225:4: ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v]
                    	    {
                    	    pushFollow(FOLLOW_expr_subset_in_basic_expr1544);
                    	    subset=expr_subset(v);

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    if ( state.backtracking==0 ) { v = subset; }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt29 >= 1 ) break loop29;
                    	    if (state.backtracking>0) {state.failed=true; return v;}
                                EarlyExitException eee =
                                    new EarlyExitException(29, input);
                                throw eee;
                        }
                        cnt29++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // R.g:225:76: ( n_ )=>
                    {
                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 28, basic_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "basic_expr"



    // $ANTLR start "expr_subset"
    // R.g:227:1: expr_subset[Node i] returns [Node v] : ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= args RBRAKET ) | ( LBB subscript= args RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) );
    public final Node expr_subset(Node i) throws RecognitionException {
        Node v = null;

        int expr_subset_StartIndex = input.index();

        Token name =null;

        ArgumentList subset =null;

        ArgumentList subscript =null;

        ArgumentList a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return v; }

            // R.g:228:5: ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= args RBRAKET ) | ( LBB subscript= args RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) )
            int alt31=5;
            switch ( input.LA(1) ) {
            case FIELD:
                {
                alt31=1;
                }
                break;
            case AT:
                {
                alt31=2;
                }
                break;
            case LBRAKET:
                {
                alt31=3;
                }
                break;
            case LBB:
                {
                alt31=4;
                }
                break;
            case LPAR:
                {
                alt31=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;

            }

            switch (alt31) {
                case 1 :
                    // R.g:228:7: ( FIELD n_ name= id )
                    {
                    // R.g:228:7: ( FIELD n_ name= id )
                    // R.g:228:8: FIELD n_ name= id
                    {
                    match(input,FIELD,FOLLOW_FIELD_in_expr_subset1577); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_expr_subset1579);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_expr_subset1583);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = FieldAccess.create(FieldOperator.FIELD, i, name.getText()); }

                    }
                    break;
                case 2 :
                    // R.g:229:7: ( AT n_ name= id )
                    {
                    // R.g:229:7: ( AT n_ name= id )
                    // R.g:229:8: AT n_ name= id
                    {
                    match(input,AT,FOLLOW_AT_in_expr_subset1596); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_expr_subset1598);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_expr_subset1602);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = FieldAccess.create(FieldOperator.AT, i, name.getText()); }

                    }
                    break;
                case 3 :
                    // R.g:230:7: ( LBRAKET subset= args RBRAKET )
                    {
                    // R.g:230:7: ( LBRAKET subset= args RBRAKET )
                    // R.g:230:8: LBRAKET subset= args RBRAKET
                    {
                    match(input,LBRAKET,FOLLOW_LBRAKET_in_expr_subset1616); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1620);
                    subset=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1622); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Call.create(CallOperator.SUBSET, i, subset); }

                    }
                    break;
                case 4 :
                    // R.g:231:7: ( LBB subscript= args RBRAKET RBRAKET )
                    {
                    // R.g:231:7: ( LBB subscript= args RBRAKET RBRAKET )
                    // R.g:231:8: LBB subscript= args RBRAKET RBRAKET
                    {
                    match(input,LBB,FOLLOW_LBB_in_expr_subset1634); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1638);
                    subscript=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1640); if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1642); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Call.create(CallOperator.SUBSCRIPT, i, subscript); }

                    }
                    break;
                case 5 :
                    // R.g:233:7: ( LPAR a= args RPAR )
                    {
                    // R.g:233:7: ( LPAR a= args RPAR )
                    // R.g:233:8: LPAR a= args RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_expr_subset1659); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1663);
                    a=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RPAR,FOLLOW_RPAR_in_expr_subset1665); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Call.create(i, a); }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 29, expr_subset_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "expr_subset"



    // $ANTLR start "simple_expr"
    // R.g:236:1: simple_expr returns [Node v] : (i= id |b= bool | DD | NULL |num= number | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR |s= sequence |e= expr_wo_assign );
    public final Node simple_expr() throws RecognitionException {
        Node v = null;

        int simple_expr_StartIndex = input.index();

        Token i =null;

        Node b =null;

        Node num =null;

        Node ea =null;

        Node s =null;

        Node e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return v; }

            // R.g:237:2: (i= id |b= bool | DD | NULL |num= number | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR |s= sequence |e= expr_wo_assign )
            int alt32=10;
            switch ( input.LA(1) ) {
            case ID:
                {
                switch ( input.LA(2) ) {
                case EOF:
                case AND:
                case ARROW:
                case ASSIGN:
                case AT:
                case BITWISEAND:
                case BITWISEOR:
                case CARRET:
                case COLUMN:
                case COMMA:
                case COMMENT:
                case DIV:
                case ELSE:
                case EQ:
                case FIELD:
                case GE:
                case GT:
                case LBB:
                case LBRAKET:
                case LE:
                case LPAR:
                case LT:
                case MINUS:
                case MOD:
                case MULT:
                case NE:
                case NEWLINE:
                case OP:
                case OR:
                case PLUS:
                case RBRACE:
                case RBRAKET:
                case RIGHT_ARROW:
                case RPAR:
                case SEMICOLUMN:
                case SUPER_ARROW:
                case SUPER_RIGHT_ARROW:
                case TILDE:
                    {
                    alt32=1;
                    }
                    break;
                case NS_GET:
                    {
                    alt32=6;
                    }
                    break;
                case NS_GET_INT:
                    {
                    alt32=7;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;

                }

                }
                break;
            case STRING:
                {
                switch ( input.LA(2) ) {
                case EOF:
                case AND:
                case ARROW:
                case ASSIGN:
                case AT:
                case BITWISEAND:
                case BITWISEOR:
                case CARRET:
                case COLUMN:
                case COMMA:
                case COMMENT:
                case DIV:
                case ELSE:
                case EQ:
                case FIELD:
                case GE:
                case GT:
                case LBB:
                case LBRAKET:
                case LE:
                case LPAR:
                case LT:
                case MINUS:
                case MOD:
                case MULT:
                case NE:
                case NEWLINE:
                case OP:
                case OR:
                case PLUS:
                case RBRACE:
                case RBRAKET:
                case RIGHT_ARROW:
                case RPAR:
                case SEMICOLUMN:
                case SUPER_ARROW:
                case SUPER_RIGHT_ARROW:
                case TILDE:
                    {
                    alt32=1;
                    }
                    break;
                case NS_GET:
                    {
                    alt32=6;
                    }
                    break;
                case NS_GET_INT:
                    {
                    alt32=7;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 2, input);

                    throw nvae;

                }

                }
                break;
            case VARIATIC:
                {
                switch ( input.LA(2) ) {
                case EOF:
                case AND:
                case ARROW:
                case ASSIGN:
                case AT:
                case BITWISEAND:
                case BITWISEOR:
                case CARRET:
                case COLUMN:
                case COMMA:
                case COMMENT:
                case DIV:
                case ELSE:
                case EQ:
                case FIELD:
                case GE:
                case GT:
                case LBB:
                case LBRAKET:
                case LE:
                case LPAR:
                case LT:
                case MINUS:
                case MOD:
                case MULT:
                case NE:
                case NEWLINE:
                case OP:
                case OR:
                case PLUS:
                case RBRACE:
                case RBRAKET:
                case RIGHT_ARROW:
                case RPAR:
                case SEMICOLUMN:
                case SUPER_ARROW:
                case SUPER_RIGHT_ARROW:
                case TILDE:
                    {
                    alt32=1;
                    }
                    break;
                case NS_GET:
                    {
                    alt32=6;
                    }
                    break;
                case NS_GET_INT:
                    {
                    alt32=7;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 3, input);

                    throw nvae;

                }

                }
                break;
            case FALSE:
            case NA:
            case TRUE:
                {
                alt32=2;
                }
                break;
            case DD:
                {
                alt32=3;
                }
                break;
            case NULL:
                {
                alt32=4;
                }
                break;
            case COMPLEX:
            case DOUBLE:
            case INTEGER:
                {
                alt32=5;
                }
                break;
            case LPAR:
                {
                alt32=8;
                }
                break;
            case LBRACE:
                {
                alt32=9;
                }
                break;
            case BREAK:
            case FOR:
            case FUNCTION:
            case IF:
            case NEXT:
            case REPEAT:
            case WHILE:
                {
                alt32=10;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;

            }

            switch (alt32) {
                case 1 :
                    // R.g:237:4: i= id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1694);
                    i=id();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AccessVariable.create(i.getText()); }

                    }
                    break;
                case 2 :
                    // R.g:238:4: b= bool
                    {
                    pushFollow(FOLLOW_bool_in_simple_expr1703);
                    b=bool();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = b; }

                    }
                    break;
                case 3 :
                    // R.g:239:4: DD
                    {
                    match(input,DD,FOLLOW_DD_in_simple_expr1710); if (state.failed) return v;

                    }
                    break;
                case 4 :
                    // R.g:240:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_simple_expr1715); if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = Constant.getNull(); }

                    }
                    break;
                case 5 :
                    // R.g:241:4: num= number
                    {
                    pushFollow(FOLLOW_number_in_simple_expr1724);
                    num=number();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = num; }

                    }
                    break;
                case 6 :
                    // R.g:242:4: id NS_GET n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1731);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,NS_GET,FOLLOW_NS_GET_in_simple_expr1733); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1735);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_simple_expr1737);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 7 :
                    // R.g:243:4: id NS_GET_INT n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1742);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,NS_GET_INT,FOLLOW_NS_GET_INT_in_simple_expr1744); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1746);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_simple_expr1748);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 8 :
                    // R.g:244:4: LPAR n_ ea= expr_or_assign n_ RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_simple_expr1753); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1755);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_simple_expr1761);
                    ea=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1763);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RPAR,FOLLOW_RPAR_in_simple_expr1765); if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = ea; }

                    }
                    break;
                case 9 :
                    // R.g:245:4: s= sequence
                    {
                    pushFollow(FOLLOW_sequence_in_simple_expr1776);
                    s=sequence();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = s;}

                    }
                    break;
                case 10 :
                    // R.g:246:4: e= expr_wo_assign
                    {
                    pushFollow(FOLLOW_expr_wo_assign_in_simple_expr1787);
                    e=expr_wo_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = e; }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 30, simple_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "simple_expr"



    // $ANTLR start "number"
    // R.g:248:1: number returns [Node n] : (i= INTEGER |d= DOUBLE |c= COMPLEX );
    public final Node number() throws RecognitionException {
        Node n = null;

        int number_StartIndex = input.index();

        Token i=null;
        Token d=null;
        Token c=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return n; }

            // R.g:249:5: (i= INTEGER |d= DOUBLE |c= COMPLEX )
            int alt33=3;
            switch ( input.LA(1) ) {
            case INTEGER:
                {
                alt33=1;
                }
                break;
            case DOUBLE:
                {
                alt33=2;
                }
                break;
            case COMPLEX:
                {
                alt33=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return n;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;

            }

            switch (alt33) {
                case 1 :
                    // R.g:249:7: i= INTEGER
                    {
                    i=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_number1808); if (state.failed) return n;

                    if ( state.backtracking==0 ) { n = Constant.createIntConstant((i!=null?i.getText():null)); }

                    }
                    break;
                case 2 :
                    // R.g:250:7: d= DOUBLE
                    {
                    d=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_number1820); if (state.failed) return n;

                    if ( state.backtracking==0 ) { n = Constant.createDoubleConstant((d!=null?d.getText():null)); }

                    }
                    break;
                case 3 :
                    // R.g:251:7: c= COMPLEX
                    {
                    c=(Token)match(input,COMPLEX,FOLLOW_COMPLEX_in_number1832); if (state.failed) return n;

                    if ( state.backtracking==0 ) { n = Constant.createComplexConstant((c!=null?c.getText():null)); }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 31, number_StartIndex); }

        }
        return n;
    }
    // $ANTLR end "number"



    // $ANTLR start "id"
    // R.g:253:1: id returns [Token t] : (i= ID |s= STRING |v= VARIATIC );
    public final Token id() throws RecognitionException {
        Token t = null;

        int id_StartIndex = input.index();

        Token i=null;
        Token s=null;
        Token v=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return t; }

            // R.g:254:5: (i= ID |s= STRING |v= VARIATIC )
            int alt34=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt34=1;
                }
                break;
            case STRING:
                {
                alt34=2;
                }
                break;
            case VARIATIC:
                {
                alt34=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return t;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;

            }

            switch (alt34) {
                case 1 :
                    // R.g:254:7: i= ID
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_id1856); if (state.failed) return t;

                    if ( state.backtracking==0 ) { t = i; }

                    }
                    break;
                case 2 :
                    // R.g:255:7: s= STRING
                    {
                    s=(Token)match(input,STRING,FOLLOW_STRING_in_id1868); if (state.failed) return t;

                    if ( state.backtracking==0 ) { t = s; }

                    }
                    break;
                case 3 :
                    // R.g:256:7: v= VARIATIC
                    {
                    v=(Token)match(input,VARIATIC,FOLLOW_VARIATIC_in_id1880); if (state.failed) return t;

                    if ( state.backtracking==0 ) { t = v; }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 32, id_StartIndex); }

        }
        return t;
    }
    // $ANTLR end "id"



    // $ANTLR start "bool"
    // R.g:258:1: bool returns [Node v] : ( TRUE | FALSE | NA );
    public final Node bool() throws RecognitionException {
        Node v = null;

        int bool_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return v; }

            // R.g:259:5: ( TRUE | FALSE | NA )
            int alt35=3;
            switch ( input.LA(1) ) {
            case TRUE:
                {
                alt35=1;
                }
                break;
            case FALSE:
                {
                alt35=2;
                }
                break;
            case NA:
                {
                alt35=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;

            }

            switch (alt35) {
                case 1 :
                    // R.g:259:7: TRUE
                    {
                    match(input,TRUE,FOLLOW_TRUE_in_bool1902); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Constant.createBoolConstant(1); }

                    }
                    break;
                case 2 :
                    // R.g:260:7: FALSE
                    {
                    match(input,FALSE,FOLLOW_FALSE_in_bool1912); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Constant.createBoolConstant(0); }

                    }
                    break;
                case 3 :
                    // R.g:261:7: NA
                    {
                    match(input,NA,FOLLOW_NA_in_bool1922); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Constant.createBoolConstant(RLogical.NA); }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 33, bool_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "bool"



    // $ANTLR start "or_operator"
    // R.g:263:1: or_operator returns [BinaryOperator v] : ( OR | BITWISEOR );
    public final BinaryOperator or_operator() throws RecognitionException {
        BinaryOperator v = null;

        int or_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return v; }

            // R.g:264:2: ( OR | BITWISEOR )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==OR) ) {
                alt36=1;
            }
            else if ( (LA36_0==BITWISEOR) ) {
                alt36=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;

            }
            switch (alt36) {
                case 1 :
                    // R.g:264:4: OR
                    {
                    match(input,OR,FOLLOW_OR_in_or_operator1941); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.OR; }

                    }
                    break;
                case 2 :
                    // R.g:265:5: BITWISEOR
                    {
                    match(input,BITWISEOR,FOLLOW_BITWISEOR_in_or_operator1958); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.BITWISEOR; }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 34, or_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "or_operator"



    // $ANTLR start "and_operator"
    // R.g:266:1: and_operator returns [BinaryOperator v] : ( AND | BITWISEAND );
    public final BinaryOperator and_operator() throws RecognitionException {
        BinaryOperator v = null;

        int and_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return v; }

            // R.g:267:2: ( AND | BITWISEAND )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==AND) ) {
                alt37=1;
            }
            else if ( (LA37_0==BITWISEAND) ) {
                alt37=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;

            }
            switch (alt37) {
                case 1 :
                    // R.g:267:4: AND
                    {
                    match(input,AND,FOLLOW_AND_in_and_operator1974); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.AND; }

                    }
                    break;
                case 2 :
                    // R.g:268:4: BITWISEAND
                    {
                    match(input,BITWISEAND,FOLLOW_BITWISEAND_in_and_operator1990); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.BITWISEAND; }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 35, and_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "and_operator"



    // $ANTLR start "comp_operator"
    // R.g:269:1: comp_operator returns [BinaryOperator v] : ( GT | GE | LT | LE | EQ | NE );
    public final BinaryOperator comp_operator() throws RecognitionException {
        BinaryOperator v = null;

        int comp_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return v; }

            // R.g:270:2: ( GT | GE | LT | LE | EQ | NE )
            int alt38=6;
            switch ( input.LA(1) ) {
            case GT:
                {
                alt38=1;
                }
                break;
            case GE:
                {
                alt38=2;
                }
                break;
            case LT:
                {
                alt38=3;
                }
                break;
            case LE:
                {
                alt38=4;
                }
                break;
            case EQ:
                {
                alt38=5;
                }
                break;
            case NE:
                {
                alt38=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;

            }

            switch (alt38) {
                case 1 :
                    // R.g:270:4: GT
                    {
                    match(input,GT,FOLLOW_GT_in_comp_operator2006); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.GT; }

                    }
                    break;
                case 2 :
                    // R.g:271:4: GE
                    {
                    match(input,GE,FOLLOW_GE_in_comp_operator2013); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.GE; }

                    }
                    break;
                case 3 :
                    // R.g:272:4: LT
                    {
                    match(input,LT,FOLLOW_LT_in_comp_operator2020); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.LT; }

                    }
                    break;
                case 4 :
                    // R.g:273:4: LE
                    {
                    match(input,LE,FOLLOW_LE_in_comp_operator2027); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.LE; }

                    }
                    break;
                case 5 :
                    // R.g:274:4: EQ
                    {
                    match(input,EQ,FOLLOW_EQ_in_comp_operator2034); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.EQ; }

                    }
                    break;
                case 6 :
                    // R.g:275:4: NE
                    {
                    match(input,NE,FOLLOW_NE_in_comp_operator2041); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.NE; }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 36, comp_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "comp_operator"



    // $ANTLR start "add_operator"
    // R.g:276:1: add_operator returns [BinaryOperator v] : ( PLUS | MINUS );
    public final BinaryOperator add_operator() throws RecognitionException {
        BinaryOperator v = null;

        int add_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return v; }

            // R.g:277:2: ( PLUS | MINUS )
            int alt39=2;
            int LA39_0 = input.LA(1);

            if ( (LA39_0==PLUS) ) {
                alt39=1;
            }
            else if ( (LA39_0==MINUS) ) {
                alt39=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;

            }
            switch (alt39) {
                case 1 :
                    // R.g:277:4: PLUS
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_add_operator2055); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.ADD; }

                    }
                    break;
                case 2 :
                    // R.g:278:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_add_operator2062); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.SUB; }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 37, add_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "add_operator"



    // $ANTLR start "mult_operator"
    // R.g:279:1: mult_operator returns [BinaryOperator v] : ( MULT | DIV | MOD );
    public final BinaryOperator mult_operator() throws RecognitionException {
        BinaryOperator v = null;

        int mult_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return v; }

            // R.g:280:2: ( MULT | DIV | MOD )
            int alt40=3;
            switch ( input.LA(1) ) {
            case MULT:
                {
                alt40=1;
                }
                break;
            case DIV:
                {
                alt40=2;
                }
                break;
            case MOD:
                {
                alt40=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;

            }

            switch (alt40) {
                case 1 :
                    // R.g:280:4: MULT
                    {
                    match(input,MULT,FOLLOW_MULT_in_mult_operator2077); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.MULT; }

                    }
                    break;
                case 2 :
                    // R.g:281:4: DIV
                    {
                    match(input,DIV,FOLLOW_DIV_in_mult_operator2084); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.DIV; }

                    }
                    break;
                case 3 :
                    // R.g:282:4: MOD
                    {
                    match(input,MOD,FOLLOW_MOD_in_mult_operator2092); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.MOD; }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 38, mult_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "mult_operator"



    // $ANTLR start "power_operator"
    // R.g:283:1: power_operator returns [BinaryOperator v] : CARRET ;
    public final BinaryOperator power_operator() throws RecognitionException {
        BinaryOperator v = null;

        int power_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return v; }

            // R.g:284:2: ( CARRET )
            // R.g:284:4: CARRET
            {
            match(input,CARRET,FOLLOW_CARRET_in_power_operator2107); if (state.failed) return v;

            if ( state.backtracking==0 ) {v = BinaryOperator.POW; }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 39, power_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "power_operator"



    // $ANTLR start "args"
    // R.g:286:1: args returns [ArgumentList v] : ( n_ arg_expr[v] )? n_ ( COMMA (| n_ arg_expr[v] ) n_ )* ;
    public final ArgumentList args() throws RecognitionException {
        ArgumentList v = null;

        int args_StartIndex = input.index();

         v = new ArgumentList.Default(); 
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return v; }

            // R.g:288:5: ( ( n_ arg_expr[v] )? n_ ( COMMA (| n_ arg_expr[v] ) n_ )* )
            // R.g:288:7: ( n_ arg_expr[v] )? n_ ( COMMA (| n_ arg_expr[v] ) n_ )*
            {
            // R.g:288:7: ( n_ arg_expr[v] )?
            int alt41=2;
            alt41 = dfa41.predict(input);
            switch (alt41) {
                case 1 :
                    // R.g:288:8: n_ arg_expr[v]
                    {
                    pushFollow(FOLLOW_n__in_args2132);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_arg_expr_in_args2134);
                    arg_expr(v);

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }


            pushFollow(FOLLOW_n__in_args2139);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:288:28: ( COMMA (| n_ arg_expr[v] ) n_ )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==COMMA) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // R.g:288:29: COMMA (| n_ arg_expr[v] ) n_
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_args2142); if (state.failed) return v;

            	    // R.g:288:35: (| n_ arg_expr[v] )
            	    int alt42=2;
            	    alt42 = dfa42.predict(input);
            	    switch (alt42) {
            	        case 1 :
            	            // R.g:288:37: 
            	            {
            	            if ( state.backtracking==0 ) { v.add((Node)null); }

            	            }
            	            break;
            	        case 2 :
            	            // R.g:288:63: n_ arg_expr[v]
            	            {
            	            pushFollow(FOLLOW_n__in_args2150);
            	            n_();

            	            state._fsp--;
            	            if (state.failed) return v;

            	            pushFollow(FOLLOW_arg_expr_in_args2152);
            	            arg_expr(v);

            	            state._fsp--;
            	            if (state.failed) return v;

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_n__in_args2156);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    }
            	    break;

            	default :
            	    break loop43;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 40, args_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "args"



    // $ANTLR start "arg_expr"
    // R.g:290:1: arg_expr[ArgumentList l] : (e= expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );
    public final void arg_expr(ArgumentList l) throws RecognitionException {
        int arg_expr_StartIndex = input.index();

        Node e =null;

        Token name =null;

        Node val =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return ; }

            // R.g:291:2: (e= expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN )
            int alt44=5;
            alt44 = dfa44.predict(input);
            switch (alt44) {
                case 1 :
                    // R.g:291:4: e= expr
                    {
                    pushFollow(FOLLOW_expr_in_arg_expr2173);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add(e); }

                    }
                    break;
                case 2 :
                    // R.g:292:4: name= id n_ ASSIGN n_ val= expr
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2182);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2184);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2186); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2188);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_arg_expr2192);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add(name.getText(), val); }

                    }
                    break;
                case 3 :
                    // R.g:293:4: name= id n_ ASSIGN
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2201);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2203);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2205); if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add(name.getText(), null); }

                    }
                    break;
                case 4 :
                    // R.g:294:4: NULL n_ ASSIGN n_ val= expr
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2213); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2215);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2217); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2219);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_arg_expr2223);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { Utils.nyi(); }

                    }
                    break;
                case 5 :
                    // R.g:295:4: NULL n_ ASSIGN
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2230); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2232);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2234); if (state.failed) return ;

                    if ( state.backtracking==0 ) { Utils.nyi(); }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 41, arg_expr_StartIndex); }

        }
        return ;
    }
    // $ANTLR end "arg_expr"

    // $ANTLR start synpred1_R
    public final void synpred1_R_fragment() throws RecognitionException {
        // R.g:113:10: ( LPAR )
        // R.g:113:11: LPAR
        {
        match(input,LPAR,FOLLOW_LPAR_in_synpred1_R355); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_R

    // $ANTLR start synpred2_R
    public final void synpred2_R_fragment() throws RecognitionException {
        // R.g:114:11: ( LPAR )
        // R.g:114:12: LPAR
        {
        match(input,LPAR,FOLLOW_LPAR_in_synpred2_R374); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_R

    // $ANTLR start synpred3_R
    public final void synpred3_R_fragment() throws RecognitionException {
        // R.g:132:5: ( ARROW )
        // R.g:132:6: ARROW
        {
        match(input,ARROW,FOLLOW_ARROW_in_synpred3_R555); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred3_R

    // $ANTLR start synpred4_R
    public final void synpred4_R_fragment() throws RecognitionException {
        // R.g:133:5: ( SUPER_ARROW )
        // R.g:133:6: SUPER_ARROW
        {
        match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_synpred4_R573); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred4_R

    // $ANTLR start synpred5_R
    public final void synpred5_R_fragment() throws RecognitionException {
        // R.g:134:5: ( RIGHT_ARROW )
        // R.g:134:6: RIGHT_ARROW
        {
        match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_synpred5_R591); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred5_R

    // $ANTLR start synpred6_R
    public final void synpred6_R_fragment() throws RecognitionException {
        // R.g:135:5: ( SUPER_RIGHT_ARROW )
        // R.g:135:6: SUPER_RIGHT_ARROW
        {
        match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_synpred6_R611); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred6_R

    // $ANTLR start synpred7_R
    public final void synpred7_R_fragment() throws RecognitionException {
        // R.g:136:5: ( ASSIGN )
        // R.g:136:6: ASSIGN
        {
        match(input,ASSIGN,FOLLOW_ASSIGN_in_synpred7_R631); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred7_R

    // $ANTLR start synpred8_R
    public final void synpred8_R_fragment() throws RecognitionException {
        // R.g:143:3: ( n_ ELSE )
        // R.g:143:4: n_ ELSE
        {
        pushFollow(FOLLOW_n__in_synpred8_R694);
        n_();

        state._fsp--;
        if (state.failed) return ;

        match(input,ELSE,FOLLOW_ELSE_in_synpred8_R696); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred8_R

    // $ANTLR start synpred9_R
    public final void synpred9_R_fragment() throws RecognitionException {
        // R.g:174:5: ( TILDE )
        // R.g:174:6: TILDE
        {
        match(input,TILDE,FOLLOW_TILDE_in_synpred9_R1040); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred9_R

    // $ANTLR start synpred10_R
    public final void synpred10_R_fragment() throws RecognitionException {
        // R.g:179:4: ( or_operator )
        // R.g:179:5: or_operator
        {
        pushFollow(FOLLOW_or_operator_in_synpred10_R1081);
        or_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred10_R

    // $ANTLR start synpred11_R
    public final void synpred11_R_fragment() throws RecognitionException {
        // R.g:184:7: ( and_operator )
        // R.g:184:8: and_operator
        {
        pushFollow(FOLLOW_and_operator_in_synpred11_R1131);
        and_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred11_R

    // $ANTLR start synpred12_R
    public final void synpred12_R_fragment() throws RecognitionException {
        // R.g:189:7: ( comp_operator )
        // R.g:189:8: comp_operator
        {
        pushFollow(FOLLOW_comp_operator_in_synpred12_R1181);
        comp_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred12_R

    // $ANTLR start synpred13_R
    public final void synpred13_R_fragment() throws RecognitionException {
        // R.g:194:5: ( add_operator )
        // R.g:194:6: add_operator
        {
        pushFollow(FOLLOW_add_operator_in_synpred13_R1233);
        add_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred13_R

    // $ANTLR start synpred14_R
    public final void synpred14_R_fragment() throws RecognitionException {
        // R.g:199:4: ( mult_operator )
        // R.g:199:5: mult_operator
        {
        pushFollow(FOLLOW_mult_operator_in_synpred14_R1274);
        mult_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred14_R

    // $ANTLR start synpred15_R
    public final void synpred15_R_fragment() throws RecognitionException {
        // R.g:203:4: ( OP )
        // R.g:203:5: OP
        {
        match(input,OP,FOLLOW_OP_in_synpred15_R1313); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred15_R

    // $ANTLR start synpred16_R
    public final void synpred16_R_fragment() throws RecognitionException {
        // R.g:208:4: ( COLUMN )
        // R.g:208:5: COLUMN
        {
        match(input,COLUMN,FOLLOW_COLUMN_in_synpred16_R1359); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred16_R

    // $ANTLR start synpred17_R
    public final void synpred17_R_fragment() throws RecognitionException {
        // R.g:213:7: ( power_operator )
        // R.g:213:8: power_operator
        {
        pushFollow(FOLLOW_power_operator_in_synpred17_R1409);
        power_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred17_R

    // $ANTLR start synpred18_R
    public final void synpred18_R_fragment() throws RecognitionException {
        // R.g:225:4: ( FIELD | AT | LBRAKET | LBB | LPAR )
        // R.g:
        {
        if ( input.LA(1)==AT||input.LA(1)==FIELD||input.LA(1)==LBB||input.LA(1)==LBRAKET||input.LA(1)==LPAR ) {
            input.consume();
            state.errorRecovery=false;
            state.failed=false;
        }
        else {
            if (state.backtracking>0) {state.failed=true; return ;}
            MismatchedSetException mse = new MismatchedSetException(null,input);
            throw mse;
        }


        }

    }
    // $ANTLR end synpred18_R

    // $ANTLR start synpred19_R
    public final void synpred19_R_fragment() throws RecognitionException {
        // R.g:225:76: ( n_ )
        // R.g:225:77: n_
        {
        pushFollow(FOLLOW_n__in_synpred19_R1554);
        n_();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred19_R

    // Delegated rules

    public final boolean synpred17_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred17_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred16_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred16_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred8_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred8_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred14_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred14_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred11_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred11_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred12_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred12_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred3_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred3_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred6_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred6_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred13_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred13_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred18_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred18_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred4_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred4_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred7_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred7_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred15_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred15_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred10_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred10_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred2_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred9_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred9_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred19_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred19_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred5_R() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred5_R_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA7 dfa7 = new DFA7(this);
    protected DFA8 dfa8 = new DFA8(this);
    protected DFA10 dfa10 = new DFA10(this);
    protected DFA16 dfa16 = new DFA16(this);
    protected DFA18 dfa18 = new DFA18(this);
    protected DFA41 dfa41 = new DFA41(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA44 dfa44 = new DFA44(this);
    static final String DFA7_eotS =
        "\7\uffff";
    static final String DFA7_eofS =
        "\1\2\6\uffff";
    static final String DFA7_minS =
        "\1\4\1\13\1\uffff\1\13\2\0\1\uffff";
    static final String DFA7_maxS =
        "\1\112\1\121\1\uffff\1\121\2\0\1\uffff";
    static final String DFA7_acceptS =
        "\2\uffff\1\2\3\uffff\1\1";
    static final String DFA7_specialS =
        "\4\uffff\1\1\1\0\1\uffff}>";
    static final String[] DFA7_transitionS = {
            "\6\2\3\uffff\4\2\2\uffff\1\2\1\uffff\2\2\3\uffff\1\2\2\uffff"+
            "\2\2\10\uffff\1\2\1\uffff\2\2\1\uffff\1\1\2\2\1\uffff\2\2\1"+
            "\uffff\2\2\6\uffff\1\2\1\uffff\1\2\1\uffff\3\2\1\uffff\3\2\2"+
            "\uffff\3\2",
            "\1\2\3\uffff\1\2\1\3\2\2\1\uffff\1\2\4\uffff\1\2\1\uffff\2"+
            "\2\4\uffff\1\2\1\uffff\1\2\1\uffff\1\2\2\uffff\1\2\3\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\1\uffff\1\3\2\2\2\uffff\1\2\5\uffff"+
            "\1\2\2\uffff\1\2\1\uffff\1\4\2\uffff\1\2\2\uffff\2\2\4\uffff"+
            "\2\2",
            "",
            "\1\2\3\uffff\1\2\1\3\2\2\1\uffff\1\2\4\uffff\1\2\1\uffff\2"+
            "\2\4\uffff\1\2\1\uffff\1\2\1\uffff\1\2\2\uffff\1\2\3\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\1\uffff\1\3\2\2\2\uffff\1\2\5\uffff"+
            "\1\2\2\uffff\1\2\1\uffff\1\5\2\uffff\1\2\2\uffff\2\2\4\uffff"+
            "\2\2",
            "\1\uffff",
            "\1\uffff",
            ""
    };

    static final short[] DFA7_eot = DFA.unpackEncodedString(DFA7_eotS);
    static final short[] DFA7_eof = DFA.unpackEncodedString(DFA7_eofS);
    static final char[] DFA7_min = DFA.unpackEncodedStringToUnsignedChars(DFA7_minS);
    static final char[] DFA7_max = DFA.unpackEncodedStringToUnsignedChars(DFA7_maxS);
    static final short[] DFA7_accept = DFA.unpackEncodedString(DFA7_acceptS);
    static final short[] DFA7_special = DFA.unpackEncodedString(DFA7_specialS);
    static final short[][] DFA7_transition;

    static {
        int numStates = DFA7_transitionS.length;
        DFA7_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA7_transition[i] = DFA.unpackEncodedString(DFA7_transitionS[i]);
        }
    }

    class DFA7 extends DFA {

        public DFA7(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 7;
            this.eot = DFA7_eot;
            this.eof = DFA7_eof;
            this.min = DFA7_min;
            this.max = DFA7_max;
            this.accept = DFA7_accept;
            this.special = DFA7_special;
            this.transition = DFA7_transition;
        }
        @Override
        public String getDescription() {
            return "113:9: ( ( LPAR )=> LPAR n_ RPAR )?";
        }
        @Override
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA7_5 = input.LA(1);

                         
                        int index7_5 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_R()) ) {s = 6;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index7_5);

                        if ( s>=0 ) return s;
                        break;

                    case 1 : 
                        int LA7_4 = input.LA(1);

                         
                        int index7_4 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred1_R()) ) {s = 6;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index7_4);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 7, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA8_eotS =
        "\7\uffff";
    static final String DFA8_eofS =
        "\1\2\6\uffff";
    static final String DFA8_minS =
        "\1\4\1\13\1\uffff\1\13\2\0\1\uffff";
    static final String DFA8_maxS =
        "\1\112\1\121\1\uffff\1\121\2\0\1\uffff";
    static final String DFA8_acceptS =
        "\2\uffff\1\2\3\uffff\1\1";
    static final String DFA8_specialS =
        "\4\uffff\1\1\1\0\1\uffff}>";
    static final String[] DFA8_transitionS = {
            "\6\2\3\uffff\4\2\2\uffff\1\2\1\uffff\2\2\3\uffff\1\2\2\uffff"+
            "\2\2\10\uffff\1\2\1\uffff\2\2\1\uffff\1\1\2\2\1\uffff\2\2\1"+
            "\uffff\2\2\6\uffff\1\2\1\uffff\1\2\1\uffff\3\2\1\uffff\3\2\2"+
            "\uffff\3\2",
            "\1\2\3\uffff\1\2\1\3\2\2\1\uffff\1\2\4\uffff\1\2\1\uffff\2"+
            "\2\4\uffff\1\2\1\uffff\1\2\1\uffff\1\2\2\uffff\1\2\3\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\1\uffff\1\3\2\2\2\uffff\1\2\5\uffff"+
            "\1\2\2\uffff\1\2\1\uffff\1\4\2\uffff\1\2\2\uffff\2\2\4\uffff"+
            "\2\2",
            "",
            "\1\2\3\uffff\1\2\1\3\2\2\1\uffff\1\2\4\uffff\1\2\1\uffff\2"+
            "\2\4\uffff\1\2\1\uffff\1\2\1\uffff\1\2\2\uffff\1\2\3\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\1\uffff\1\3\2\2\2\uffff\1\2\5\uffff"+
            "\1\2\2\uffff\1\2\1\uffff\1\5\2\uffff\1\2\2\uffff\2\2\4\uffff"+
            "\2\2",
            "\1\uffff",
            "\1\uffff",
            ""
    };

    static final short[] DFA8_eot = DFA.unpackEncodedString(DFA8_eotS);
    static final short[] DFA8_eof = DFA.unpackEncodedString(DFA8_eofS);
    static final char[] DFA8_min = DFA.unpackEncodedStringToUnsignedChars(DFA8_minS);
    static final char[] DFA8_max = DFA.unpackEncodedStringToUnsignedChars(DFA8_maxS);
    static final short[] DFA8_accept = DFA.unpackEncodedString(DFA8_acceptS);
    static final short[] DFA8_special = DFA.unpackEncodedString(DFA8_specialS);
    static final short[][] DFA8_transition;

    static {
        int numStates = DFA8_transitionS.length;
        DFA8_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA8_transition[i] = DFA.unpackEncodedString(DFA8_transitionS[i]);
        }
    }

    class DFA8 extends DFA {

        public DFA8(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 8;
            this.eot = DFA8_eot;
            this.eof = DFA8_eof;
            this.min = DFA8_min;
            this.max = DFA8_max;
            this.accept = DFA8_accept;
            this.special = DFA8_special;
            this.transition = DFA8_transition;
        }
        @Override
        public String getDescription() {
            return "114:10: ( ( LPAR )=> LPAR n_ RPAR )?";
        }
        @Override
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA8_5 = input.LA(1);

                         
                        int index8_5 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_R()) ) {s = 6;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index8_5);

                        if ( s>=0 ) return s;
                        break;

                    case 1 : 
                        int LA8_4 = input.LA(1);

                         
                        int index8_4 = input.index();
                        input.rewind();

                        s = -1;
                        if ( (synpred2_R()) ) {s = 6;}

                        else if ( (true) ) {s = 2;}

                         
                        input.seek(index8_4);

                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}

            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 8, _s, input);
            error(nvae);
            throw nvae;
        }

    }
    static final String DFA10_eotS =
        "\7\uffff";
    static final String DFA10_eofS =
        "\1\2\6\uffff";
    static final String DFA10_minS =
        "\1\20\3\13\2\uffff\1\13";
    static final String DFA10_maxS =
        "\1\105\3\121\2\uffff\1\121";
    static final String DFA10_acceptS =
        "\4\uffff\1\2\1\1\1\uffff";
    static final String DFA10_specialS =
        "\7\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\1\43\uffff\1\1\13\uffff\1\4\4\uffff\1\3",
            "\1\5\4\uffff\1\1\2\5\1\uffff\1\5\4\uffff\1\5\1\uffff\2\5\4"+
            "\uffff\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\3\uffff\1\5\1"+
            "\uffff\1\5\3\uffff\1\5\1\uffff\1\1\2\5\2\uffff\1\5\5\uffff\1"+
            "\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff\2\5\4\uffff\2\5",
            "\1\5\5\uffff\2\5\1\uffff\1\5\4\uffff\1\5\1\uffff\2\5\4\uffff"+
            "\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\3\uffff\1\5\1\uffff"+
            "\1\5\3\uffff\1\5\2\uffff\2\5\2\uffff\1\5\5\uffff\1\5\1\4\1\uffff"+
            "\1\5\4\uffff\1\5\2\uffff\2\5\4\uffff\2\5",
            "\1\5\4\uffff\1\6\2\5\1\uffff\1\5\4\uffff\1\5\1\uffff\2\5\4"+
            "\uffff\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\3\uffff\1\5\1"+
            "\uffff\1\5\3\uffff\1\5\1\uffff\1\6\2\5\2\uffff\1\5\5\uffff\1"+
            "\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff\2\5\4\uffff\2\5",
            "",
            "",
            "\1\5\4\uffff\1\6\2\5\1\uffff\1\5\4\uffff\1\5\1\uffff\2\5\4"+
            "\uffff\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\3\uffff\1\5\1"+
            "\uffff\1\5\3\uffff\1\5\1\uffff\1\6\2\5\2\uffff\1\5\5\uffff\1"+
            "\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff\2\5\4\uffff\2\5"
    };

    static final short[] DFA10_eot = DFA.unpackEncodedString(DFA10_eotS);
    static final short[] DFA10_eof = DFA.unpackEncodedString(DFA10_eofS);
    static final char[] DFA10_min = DFA.unpackEncodedStringToUnsignedChars(DFA10_minS);
    static final char[] DFA10_max = DFA.unpackEncodedStringToUnsignedChars(DFA10_maxS);
    static final short[] DFA10_accept = DFA.unpackEncodedString(DFA10_acceptS);
    static final short[] DFA10_special = DFA.unpackEncodedString(DFA10_specialS);
    static final short[][] DFA10_transition;

    static {
        int numStates = DFA10_transitionS.length;
        DFA10_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA10_transition[i] = DFA.unpackEncodedString(DFA10_transitionS[i]);
        }
    }

    class DFA10 extends DFA {

        public DFA10(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 10;
            this.eot = DFA10_eot;
            this.eof = DFA10_eof;
            this.min = DFA10_min;
            this.max = DFA10_max;
            this.accept = DFA10_accept;
            this.special = DFA10_special;
            this.transition = DFA10_transition;
        }
        @Override
        public String getDescription() {
            return "()* loopback of 119:50: ( n e= expr_or_assign )*";
        }
    }
    static final String DFA16_eotS =
        "\4\uffff";
    static final String DFA16_eofS =
        "\4\uffff";
    static final String DFA16_minS =
        "\2\17\2\uffff";
    static final String DFA16_maxS =
        "\2\104\2\uffff";
    static final String DFA16_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA16_specialS =
        "\4\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\3\1\1\43\uffff\1\1\17\uffff\1\2",
            "\1\3\1\1\43\uffff\1\1\17\uffff\1\2",
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
            return "()* loopback of 158:38: ( n_ COMMA n_ par_decl[l] )*";
        }
    }
    static final String DFA18_eotS =
        "\15\uffff";
    static final String DFA18_eofS =
        "\15\uffff";
    static final String DFA18_minS =
        "\1\22\4\6\2\uffff\1\6\2\uffff\1\6\2\uffff";
    static final String DFA18_maxS =
        "\1\120\4\104\2\uffff\1\104\2\uffff\1\104\2\uffff";
    static final String DFA18_acceptS =
        "\5\uffff\1\1\1\2\1\uffff\1\3\1\4\1\uffff\1\5\1\6";
    static final String DFA18_specialS =
        "\15\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\3\16\uffff\1\1\56\uffff\1\2",
            "\1\6\10\uffff\1\5\1\4\43\uffff\1\4\17\uffff\1\5",
            "\1\11\10\uffff\1\10\1\7\43\uffff\1\7\17\uffff\1\10",
            "\1\14\10\uffff\1\13\1\12\43\uffff\1\12\17\uffff\1\13",
            "\1\6\10\uffff\1\5\1\4\43\uffff\1\4\17\uffff\1\5",
            "",
            "",
            "\1\11\10\uffff\1\10\1\7\43\uffff\1\7\17\uffff\1\10",
            "",
            "",
            "\1\14\10\uffff\1\13\1\12\43\uffff\1\12\17\uffff\1\13",
            "",
            ""
    };

    static final short[] DFA18_eot = DFA.unpackEncodedString(DFA18_eotS);
    static final short[] DFA18_eof = DFA.unpackEncodedString(DFA18_eofS);
    static final char[] DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS);
    static final char[] DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS);
    static final short[] DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS);
    static final short[] DFA18_special = DFA.unpackEncodedString(DFA18_specialS);
    static final short[][] DFA18_transition;

    static {
        int numStates = DFA18_transitionS.length;
        DFA18_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA18_transition[i] = DFA.unpackEncodedString(DFA18_transitionS[i]);
        }
    }

    class DFA18 extends DFA {

        public DFA18(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 18;
            this.eot = DFA18_eot;
            this.eof = DFA18_eof;
            this.min = DFA18_min;
            this.max = DFA18_max;
            this.accept = DFA18_accept;
            this.special = DFA18_special;
            this.transition = DFA18_transition;
        }
        @Override
        public String getDescription() {
            return "160:1: par_decl[ArgumentList l] : (i= ID |i= ID n_ ASSIGN n_ e= expr |v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr );";
        }
    }
    static final String DFA41_eotS =
        "\4\uffff";
    static final String DFA41_eofS =
        "\4\uffff";
    static final String DFA41_minS =
        "\2\13\2\uffff";
    static final String DFA41_maxS =
        "\2\121\2\uffff";
    static final String DFA41_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA41_specialS =
        "\4\uffff}>";
    static final String[] DFA41_transitionS = {
            "\1\2\3\uffff\1\3\1\1\2\2\1\uffff\1\2\4\uffff\1\2\1\uffff\2\2"+
            "\4\uffff\1\2\1\uffff\1\2\1\uffff\1\2\2\uffff\1\2\3\uffff\1\2"+
            "\1\uffff\1\2\3\uffff\1\2\1\uffff\1\1\2\2\2\uffff\1\2\5\uffff"+
            "\1\2\1\uffff\1\3\1\2\1\uffff\1\3\2\uffff\1\2\2\uffff\2\2\4\uffff"+
            "\2\2",
            "\1\2\3\uffff\1\3\1\1\2\2\1\uffff\1\2\4\uffff\1\2\1\uffff\2"+
            "\2\4\uffff\1\2\1\uffff\1\2\1\uffff\1\2\2\uffff\1\2\3\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\1\uffff\1\1\2\2\2\uffff\1\2\5\uffff"+
            "\1\2\1\uffff\1\3\1\2\1\uffff\1\3\2\uffff\1\2\2\uffff\2\2\4\uffff"+
            "\2\2",
            "",
            ""
    };

    static final short[] DFA41_eot = DFA.unpackEncodedString(DFA41_eotS);
    static final short[] DFA41_eof = DFA.unpackEncodedString(DFA41_eofS);
    static final char[] DFA41_min = DFA.unpackEncodedStringToUnsignedChars(DFA41_minS);
    static final char[] DFA41_max = DFA.unpackEncodedStringToUnsignedChars(DFA41_maxS);
    static final short[] DFA41_accept = DFA.unpackEncodedString(DFA41_acceptS);
    static final short[] DFA41_special = DFA.unpackEncodedString(DFA41_specialS);
    static final short[][] DFA41_transition;

    static {
        int numStates = DFA41_transitionS.length;
        DFA41_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA41_transition[i] = DFA.unpackEncodedString(DFA41_transitionS[i]);
        }
    }

    class DFA41 extends DFA {

        public DFA41(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 41;
            this.eot = DFA41_eot;
            this.eof = DFA41_eof;
            this.min = DFA41_min;
            this.max = DFA41_max;
            this.accept = DFA41_accept;
            this.special = DFA41_special;
            this.transition = DFA41_transition;
        }
        @Override
        public String getDescription() {
            return "288:7: ( n_ arg_expr[v] )?";
        }
    }
    static final String DFA42_eotS =
        "\4\uffff";
    static final String DFA42_eofS =
        "\4\uffff";
    static final String DFA42_minS =
        "\2\13\2\uffff";
    static final String DFA42_maxS =
        "\2\121\2\uffff";
    static final String DFA42_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA42_specialS =
        "\4\uffff}>";
    static final String[] DFA42_transitionS = {
            "\1\3\3\uffff\1\2\1\1\2\3\1\uffff\1\3\4\uffff\1\3\1\uffff\2\3"+
            "\4\uffff\1\3\1\uffff\1\3\1\uffff\1\3\2\uffff\1\3\3\uffff\1\3"+
            "\1\uffff\1\3\3\uffff\1\3\1\uffff\1\1\2\3\2\uffff\1\3\5\uffff"+
            "\1\3\1\uffff\1\2\1\3\1\uffff\1\2\2\uffff\1\3\2\uffff\2\3\4\uffff"+
            "\2\3",
            "\1\3\3\uffff\1\2\1\1\2\3\1\uffff\1\3\4\uffff\1\3\1\uffff\2"+
            "\3\4\uffff\1\3\1\uffff\1\3\1\uffff\1\3\2\uffff\1\3\3\uffff\1"+
            "\3\1\uffff\1\3\3\uffff\1\3\1\uffff\1\1\2\3\2\uffff\1\3\5\uffff"+
            "\1\3\1\uffff\1\2\1\3\1\uffff\1\2\2\uffff\1\3\2\uffff\2\3\4\uffff"+
            "\2\3",
            "",
            ""
    };

    static final short[] DFA42_eot = DFA.unpackEncodedString(DFA42_eotS);
    static final short[] DFA42_eof = DFA.unpackEncodedString(DFA42_eofS);
    static final char[] DFA42_min = DFA.unpackEncodedStringToUnsignedChars(DFA42_minS);
    static final char[] DFA42_max = DFA.unpackEncodedStringToUnsignedChars(DFA42_maxS);
    static final short[] DFA42_accept = DFA.unpackEncodedString(DFA42_acceptS);
    static final short[] DFA42_special = DFA.unpackEncodedString(DFA42_specialS);
    static final short[][] DFA42_transition;

    static {
        int numStates = DFA42_transitionS.length;
        DFA42_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA42_transition[i] = DFA.unpackEncodedString(DFA42_transitionS[i]);
        }
    }

    class DFA42 extends DFA {

        public DFA42(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 42;
            this.eot = DFA42_eot;
            this.eof = DFA42_eof;
            this.min = DFA42_min;
            this.max = DFA42_max;
            this.accept = DFA42_accept;
            this.special = DFA42_special;
            this.transition = DFA42_transition;
        }
        @Override
        public String getDescription() {
            return "288:35: (| n_ arg_expr[v] )";
        }
    }
    static final String DFA44_eotS =
        "\20\uffff";
    static final String DFA44_eofS =
        "\20\uffff";
    static final String DFA44_minS =
        "\1\13\1\uffff\4\4\1\6\1\13\1\6\2\13\2\uffff\1\13\2\uffff";
    static final String DFA44_maxS =
        "\1\121\1\uffff\4\112\1\104\1\121\1\104\2\121\2\uffff\1\121\2\uffff";
    static final String DFA44_acceptS =
        "\1\uffff\1\1\11\uffff\1\2\1\3\1\uffff\1\4\1\5";
    static final String DFA44_specialS =
        "\20\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\1\5\uffff\2\1\1\uffff\1\1\4\uffff\1\1\1\uffff\2\1\4\uffff"+
            "\1\2\1\uffff\1\1\1\uffff\1\1\2\uffff\1\1\3\uffff\1\1\1\uffff"+
            "\1\1\3\uffff\1\1\2\uffff\2\1\2\uffff\1\5\5\uffff\1\1\2\uffff"+
            "\1\1\4\uffff\1\3\2\uffff\2\1\4\uffff\1\4\1\1",
            "",
            "\2\1\1\7\3\1\3\uffff\3\1\1\6\2\uffff\1\1\2\uffff\1\1\3\uffff"+
            "\1\1\2\uffff\2\1\10\uffff\1\1\1\uffff\2\1\1\uffff\3\1\1\uffff"+
            "\2\1\1\uffff\1\1\1\6\2\uffff\2\1\2\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\1\1\1\uffff\2\1\3\uffff\3\1",
            "\2\1\1\7\3\1\3\uffff\3\1\1\6\2\uffff\1\1\2\uffff\1\1\3\uffff"+
            "\1\1\2\uffff\2\1\10\uffff\1\1\1\uffff\2\1\1\uffff\3\1\1\uffff"+
            "\2\1\1\uffff\1\1\1\6\2\uffff\2\1\2\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\1\1\1\uffff\2\1\3\uffff\3\1",
            "\2\1\1\7\3\1\3\uffff\3\1\1\6\2\uffff\1\1\2\uffff\1\1\3\uffff"+
            "\1\1\2\uffff\2\1\10\uffff\1\1\1\uffff\2\1\1\uffff\3\1\1\uffff"+
            "\2\1\1\uffff\1\1\1\6\2\uffff\2\1\2\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\1\1\1\uffff\2\1\3\uffff\3\1",
            "\2\1\1\11\3\1\3\uffff\3\1\1\10\2\uffff\1\1\2\uffff\1\1\3\uffff"+
            "\1\1\2\uffff\2\1\10\uffff\1\1\1\uffff\2\1\1\uffff\3\1\1\uffff"+
            "\2\1\1\uffff\1\1\1\10\6\uffff\1\1\1\uffff\1\1\1\uffff\1\1\1"+
            "\uffff\1\1\1\uffff\2\1\3\uffff\3\1",
            "\1\7\10\uffff\1\1\1\6\43\uffff\1\6\14\uffff\1\1\2\uffff\1\1",
            "\1\13\3\uffff\1\14\1\12\2\13\1\uffff\1\13\4\uffff\1\13\1\uffff"+
            "\2\13\4\uffff\1\13\1\uffff\1\13\1\uffff\1\13\2\uffff\1\13\3"+
            "\uffff\1\13\1\uffff\1\13\3\uffff\1\13\1\uffff\1\12\2\13\2\uffff"+
            "\1\13\5\uffff\1\13\1\uffff\1\14\1\13\1\uffff\1\14\2\uffff\1"+
            "\13\2\uffff\2\13\4\uffff\2\13",
            "\1\11\10\uffff\1\1\1\10\43\uffff\1\10\14\uffff\1\1\2\uffff"+
            "\1\1",
            "\1\16\3\uffff\1\17\1\15\2\16\1\uffff\1\16\4\uffff\1\16\1\uffff"+
            "\2\16\4\uffff\1\16\1\uffff\1\16\1\uffff\1\16\2\uffff\1\16\3"+
            "\uffff\1\16\1\uffff\1\16\3\uffff\1\16\1\uffff\1\15\2\16\2\uffff"+
            "\1\16\5\uffff\1\16\1\uffff\1\17\1\16\1\uffff\1\17\2\uffff\1"+
            "\16\2\uffff\2\16\4\uffff\2\16",
            "\1\13\3\uffff\1\14\1\12\2\13\1\uffff\1\13\4\uffff\1\13\1\uffff"+
            "\2\13\4\uffff\1\13\1\uffff\1\13\1\uffff\1\13\2\uffff\1\13\3"+
            "\uffff\1\13\1\uffff\1\13\3\uffff\1\13\1\uffff\1\12\2\13\2\uffff"+
            "\1\13\5\uffff\1\13\1\uffff\1\14\1\13\1\uffff\1\14\2\uffff\1"+
            "\13\2\uffff\2\13\4\uffff\2\13",
            "",
            "",
            "\1\16\3\uffff\1\17\1\15\2\16\1\uffff\1\16\4\uffff\1\16\1\uffff"+
            "\2\16\4\uffff\1\16\1\uffff\1\16\1\uffff\1\16\2\uffff\1\16\3"+
            "\uffff\1\16\1\uffff\1\16\3\uffff\1\16\1\uffff\1\15\2\16\2\uffff"+
            "\1\16\5\uffff\1\16\1\uffff\1\17\1\16\1\uffff\1\17\2\uffff\1"+
            "\16\2\uffff\2\16\4\uffff\2\16",
            "",
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
            return "290:1: arg_expr[ArgumentList l] : (e= expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );";
        }
    }
 

    public static final BitSet FOLLOW_n__in_script156 = new BitSet(new long[]{0x8264512A1A160802L,0x00000000000B0C84L});
    public static final BitSet FOLLOW_statement_in_script161 = new BitSet(new long[]{0x8264512A1A160802L,0x00000000000B0C84L});
    public static final BitSet FOLLOW_n__in_interactive179 = new BitSet(new long[]{0x8264512A1A160800L,0x00000000000B0C84L});
    public static final BitSet FOLLOW_statement_in_interactive183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_or_assign_in_statement201 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000020L});
    public static final BitSet FOLLOW_n_in_statement203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_83_in_statement210 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000000FFFFFL});
    public static final BitSet FOLLOW_EOF_in_statement215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_n250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLUMN_in_n254 = new BitSet(new long[]{0x0010000000010000L});
    public static final BitSet FOLLOW_n__in_n256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alter_assign_in_expr_or_assign271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assign_in_expr289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_while_expr_in_expr_wo_assign308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_if_expr_in_expr_wo_assign317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_for_expr_in_expr_wo_assign326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_repeat_expr_in_expr_wo_assign335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_expr_wo_assign344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEXT_in_expr_wo_assign351 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_wo_assign358 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000010L});
    public static final BitSet FOLLOW_n__in_expr_wo_assign360 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_expr_wo_assign362 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_expr_wo_assign370 = new BitSet(new long[]{0x0000100000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_wo_assign377 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000010L});
    public static final BitSet FOLLOW_n__in_expr_wo_assign379 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_expr_wo_assign381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_sequence414 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C85L});
    public static final BitSet FOLLOW_n__in_sequence416 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C85L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence421 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000021L});
    public static final BitSet FOLLOW_n_in_sequence426 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence430 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000021L});
    public static final BitSet FOLLOW_n_in_sequence436 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000001L});
    public static final BitSet FOLLOW_RBRACE_in_sequence442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_assign460 = new BitSet(new long[]{0x0000000000000022L,0x0000000000000308L});
    public static final BitSet FOLLOW_ARROW_in_assign467 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_assign469 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_assign473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_assign481 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_assign483 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_assign487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_assign497 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_assign499 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_assign503 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_assign513 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_assign515 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_assign519 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_alter_assign547 = new BitSet(new long[]{0x0000000000000062L,0x0000000000000308L});
    public static final BitSet FOLLOW_ARROW_in_alter_assign558 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_alter_assign560 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign564 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_alter_assign576 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_alter_assign578 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign582 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_alter_assign596 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_alter_assign598 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_alter_assign616 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_alter_assign618 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_alter_assign636 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_alter_assign638 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_if_expr669 = new BitSet(new long[]{0x0010100000010000L});
    public static final BitSet FOLLOW_n__in_if_expr671 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAR_in_if_expr673 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_if_expr675 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr679 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000010L});
    public static final BitSet FOLLOW_n__in_if_expr681 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_if_expr683 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_if_expr685 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr689 = new BitSet(new long[]{0x0010000000210002L});
    public static final BitSet FOLLOW_n__in_if_expr715 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_ELSE_in_if_expr717 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_if_expr719 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_while_expr751 = new BitSet(new long[]{0x0010100000010000L});
    public static final BitSet FOLLOW_n__in_while_expr753 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAR_in_while_expr755 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_while_expr757 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr761 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000010L});
    public static final BitSet FOLLOW_n__in_while_expr763 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_while_expr765 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_while_expr767 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr771 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_for_expr787 = new BitSet(new long[]{0x0010100000010000L});
    public static final BitSet FOLLOW_n__in_for_expr789 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAR_in_for_expr791 = new BitSet(new long[]{0x0010000200010000L});
    public static final BitSet FOLLOW_n__in_for_expr793 = new BitSet(new long[]{0x0000000200000000L});
    public static final BitSet FOLLOW_ID_in_for_expr795 = new BitSet(new long[]{0x0010001000010000L});
    public static final BitSet FOLLOW_n__in_for_expr797 = new BitSet(new long[]{0x0000001000000000L});
    public static final BitSet FOLLOW_IN_in_for_expr799 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_for_expr801 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr805 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000010L});
    public static final BitSet FOLLOW_n__in_for_expr807 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_for_expr809 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_for_expr811 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr815 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REPEAT_in_repeat_expr830 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_repeat_expr832 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_repeat_expr836 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function857 = new BitSet(new long[]{0x0010100000010000L});
    public static final BitSet FOLLOW_n__in_function859 = new BitSet(new long[]{0x0000100000000000L});
    public static final BitSet FOLLOW_LPAR_in_function861 = new BitSet(new long[]{0x0010000200050000L,0x0000000000010010L});
    public static final BitSet FOLLOW_n__in_function864 = new BitSet(new long[]{0x0000000200040000L,0x0000000000010010L});
    public static final BitSet FOLLOW_par_decl_in_function867 = new BitSet(new long[]{0x0010000000018000L,0x0000000000000010L});
    public static final BitSet FOLLOW_n__in_function871 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_function873 = new BitSet(new long[]{0x0010000200050000L,0x0000000000010000L});
    public static final BitSet FOLLOW_n__in_function875 = new BitSet(new long[]{0x0000000200040000L,0x0000000000010000L});
    public static final BitSet FOLLOW_par_decl_in_function877 = new BitSet(new long[]{0x0010000000018000L,0x0000000000000010L});
    public static final BitSet FOLLOW_n__in_function882 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_function886 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_function888 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_function892 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl909 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl919 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_par_decl921 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl923 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_par_decl925 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_par_decl929 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_par_decl938 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_par_decl988 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_par_decl990 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl992 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_par_decl994 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_par_decl996 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_par_decl1002 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_par_decl1008 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_par_decl1010 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl1012 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_par_decl1014 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_par_decl1016 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_expr_in_tilde_expr1032 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_TILDE_in_tilde_expr1043 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_tilde_expr1045 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_tilde_expr_in_tilde_expr1049 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_expr_in_or_expr1075 = new BitSet(new long[]{0x2000000000000202L});
    public static final BitSet FOLLOW_or_operator_in_or_expr1086 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_or_expr1088 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_or_expr_in_or_expr1092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_expr_in_and_expr1122 = new BitSet(new long[]{0x0000000000000112L});
    public static final BitSet FOLLOW_and_operator_in_and_expr1136 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_and_expr1138 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_and_expr_in_and_expr1142 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1171 = new BitSet(new long[]{0x0008240060400002L});
    public static final BitSet FOLLOW_comp_operator_in_comp_expr1186 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_comp_expr1188 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_comp_expr_in_comp_expr1192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1224 = new BitSet(new long[]{0x8000400000000002L});
    public static final BitSet FOLLOW_add_operator_in_add_expr1238 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_add_expr1240 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1244 = new BitSet(new long[]{0x8000400000000002L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1267 = new BitSet(new long[]{0x0003000000080002L});
    public static final BitSet FOLLOW_mult_operator_in_mult_expr1279 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_mult_expr1281 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1285 = new BitSet(new long[]{0x0003000000080002L});
    public static final BitSet FOLLOW_column_expr_in_operator_expr1307 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_OP_in_operator_expr1318 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_operator_expr1320 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_operator_expr_in_operator_expr1324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_expr_in_column_expr1353 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_COLUMN_in_column_expr1364 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_column_expr1366 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_column_expr_in_column_expr1370 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_expression_in_power_expr1400 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_power_operator_in_power_expr1414 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_power_expr1416 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_power_expr_in_power_expr1420 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_unary_expression1451 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_unary_expression1453 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1457 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary_expression1464 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_unary_expression1466 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary_expression1477 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_unary_expression1479 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1483 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unary_expression1490 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_unary_expression1492 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1496 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_basic_expr_in_unary_expression1505 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_basic_expr1523 = new BitSet(new long[]{0x0000128004000082L});
    public static final BitSet FOLLOW_expr_subset_in_basic_expr1544 = new BitSet(new long[]{0x0000128004000082L});
    public static final BitSet FOLLOW_FIELD_in_expr_subset1577 = new BitSet(new long[]{0x0010000200010000L,0x0000000000010080L});
    public static final BitSet FOLLOW_n__in_expr_subset1579 = new BitSet(new long[]{0x0000000200000000L,0x0000000000010080L});
    public static final BitSet FOLLOW_id_in_expr_subset1583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_expr_subset1596 = new BitSet(new long[]{0x0010000200010000L,0x0000000000010080L});
    public static final BitSet FOLLOW_n__in_expr_subset1598 = new BitSet(new long[]{0x0000000200000000L,0x0000000000010080L});
    public static final BitSet FOLLOW_id_in_expr_subset1602 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRAKET_in_expr_subset1616 = new BitSet(new long[]{0x8274512A1A178800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_args_in_expr_subset1620 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1622 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBB_in_expr_subset1634 = new BitSet(new long[]{0x8274512A1A178800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_args_in_expr_subset1638 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1640 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_subset1659 = new BitSet(new long[]{0x8274512A1A178800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_args_in_expr_subset1663 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_expr_subset1665 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_in_simple_expr1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_simple_expr1710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_simple_expr1715 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_simple_expr1724 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1731 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_NS_GET_in_simple_expr1733 = new BitSet(new long[]{0x0010000200010000L,0x0000000000010080L});
    public static final BitSet FOLLOW_n__in_simple_expr1735 = new BitSet(new long[]{0x0000000200000000L,0x0000000000010080L});
    public static final BitSet FOLLOW_id_in_simple_expr1737 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1742 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_NS_GET_INT_in_simple_expr1744 = new BitSet(new long[]{0x0010000200010000L,0x0000000000010080L});
    public static final BitSet FOLLOW_n__in_simple_expr1746 = new BitSet(new long[]{0x0000000200000000L,0x0000000000010080L});
    public static final BitSet FOLLOW_id_in_simple_expr1748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_simple_expr1753 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_simple_expr1755 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_simple_expr1761 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000010L});
    public static final BitSet FOLLOW_n__in_simple_expr1763 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_simple_expr1765 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_simple_expr1776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_wo_assign_in_simple_expr1787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_number1808 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_number1820 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMPLEX_in_number1832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_id1856 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_id1868 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_id1880 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_bool1902 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_bool1912 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NA_in_bool1922 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_or_operator1941 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEOR_in_or_operator1958 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_and_operator1974 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEAND_in_and_operator1990 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_comp_operator2006 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GE_in_comp_operator2013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_comp_operator2020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LE_in_comp_operator2027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_comp_operator2034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NE_in_comp_operator2041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_add_operator2055 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_add_operator2062 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_in_mult_operator2077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_in_mult_operator2084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mult_operator2092 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARRET_in_power_operator2107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_args2132 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_arg_expr_in_args2134 = new BitSet(new long[]{0x0010000000018000L});
    public static final BitSet FOLLOW_n__in_args2139 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_COMMA_in_args2142 = new BitSet(new long[]{0x8274512A1A178800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_args2150 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_arg_expr_in_args2152 = new BitSet(new long[]{0x0010000000018000L});
    public static final BitSet FOLLOW_n__in_args2156 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_expr_in_arg_expr2173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2182 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2184 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2186 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_arg_expr2188 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_arg_expr2192 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2201 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2203 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2205 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2213 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2215 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2217 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_arg_expr2219 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_arg_expr2223 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2230 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2232 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2234 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_synpred1_R355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_synpred2_R374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_synpred3_R555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_synpred4_R573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_synpred5_R591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_synpred6_R611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_synpred7_R631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred8_R694 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_ELSE_in_synpred8_R696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_synpred9_R1040 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_operator_in_synpred10_R1081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_operator_in_synpred11_R1131 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_operator_in_synpred12_R1181 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_operator_in_synpred13_R1233 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_operator_in_synpred14_R1274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OP_in_synpred15_R1313 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLUMN_in_synpred16_R1359 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_operator_in_synpred17_R1409 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred19_R1554 = new BitSet(new long[]{0x0000000000000002L});

}
