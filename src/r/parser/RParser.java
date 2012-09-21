// $ANTLR 3.4 R.g 2012-09-21 17:58:15

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
        this.state.ruleMemo = new HashMap[61+1];
         

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
    // R.g:85:1: script returns [ASTNode v] : n_ (s= statement )* ;
    public final ASTNode script() throws RecognitionException {
        ASTNode v = null;

        int script_StartIndex = input.index();

        ASTNode s =null;


        ArrayList<ASTNode> stmts = new ArrayList<ASTNode>();
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
    // R.g:90:1: interactive returns [ASTNode v] : n_ e= statement ;
    public final ASTNode interactive() throws RecognitionException {
        ASTNode v = null;

        int interactive_StartIndex = input.index();

        ASTNode e =null;


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
    // R.g:93:1: statement returns [ASTNode v] : (e= expr_or_assign n | '--EOF--' ( . )* EOF );
    public final ASTNode statement() throws RecognitionException {
        ASTNode v = null;

        int statement_StartIndex = input.index();

        ASTNode e =null;


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
    // R.g:101:1: expr_or_assign returns [ASTNode v] : a= alter_assign ;
    public final ASTNode expr_or_assign() throws RecognitionException {
        ASTNode v = null;

        int expr_or_assign_StartIndex = input.index();

        ASTNode a =null;


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
    // R.g:104:1: expr returns [ASTNode v] : a= assign ;
    public final ASTNode expr() throws RecognitionException {
        ASTNode v = null;

        int expr_StartIndex = input.index();

        ASTNode a =null;


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
    // R.g:107:1: expr_wo_assign returns [ASTNode v] : (w= while_expr |i= if_expr |f= for_expr |r= repeat_expr |fun= function | NEXT ( ( LPAR )=> LPAR n_ RPAR )? | BREAK ( ( LPAR )=> LPAR n_ RPAR )? );
    public final ASTNode expr_wo_assign() throws RecognitionException {
        ASTNode v = null;

        int expr_wo_assign_StartIndex = input.index();

        ASTNode w =null;

        ASTNode i =null;

        ASTNode f =null;

        ASTNode r =null;

        ASTNode fun =null;


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
    // R.g:116:1: sequence returns [ASTNode v] : LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE ;
    public final ASTNode sequence() throws RecognitionException {
        ASTNode v = null;

        int sequence_StartIndex = input.index();

        ASTNode e =null;


        ArrayList<ASTNode> stmts = new ArrayList<ASTNode>();
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
    // R.g:121:1: assign returns [ASTNode v] : l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |) ;
    public final ASTNode assign() throws RecognitionException {
        ASTNode v = null;

        int assign_StartIndex = input.index();

        Token a=null;
        ASTNode l =null;

        ASTNode r =null;


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

                    if ( state.backtracking==0 ) { v = AssignVariable.create(false, l, r);}

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

                    if ( state.backtracking==0 ) { v = AssignVariable.create(true, l, r);}

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

                    if ( state.backtracking==0 ) { v = AssignVariable.create(false, r, l);}

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

                    if ( state.backtracking==0 ) { v = AssignVariable.create(true, r, l);}

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
    // R.g:130:1: alter_assign returns [ASTNode v] : l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |) ;
    public final ASTNode alter_assign() throws RecognitionException {
        ASTNode v = null;

        int alter_assign_StartIndex = input.index();

        Token a=null;
        ASTNode l =null;

        ASTNode r =null;


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

                    if ( state.backtracking==0 ) { v = AssignVariable.create(false, l, r);}

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

                    if ( state.backtracking==0 ) {v = AssignVariable.create(true, l, r);}

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

                    if ( state.backtracking==0 ) { v = AssignVariable.create(false, r, l);}

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

                    if ( state.backtracking==0 ) {v = AssignVariable.create(true, r, l);}

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

                    if ( state.backtracking==0 ) {v = AssignVariable.create(false, l, r);}

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
    // R.g:140:1: if_expr returns [ASTNode v] : IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |) ;
    public final ASTNode if_expr() throws RecognitionException {
        ASTNode v = null;

        int if_expr_StartIndex = input.index();

        ASTNode cond =null;

        ASTNode t =null;

        ASTNode f =null;


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
    // R.g:147:1: while_expr returns [ASTNode v] : WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final ASTNode while_expr() throws RecognitionException {
        ASTNode v = null;

        int while_expr_StartIndex = input.index();

        ASTNode c =null;

        ASTNode body =null;


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
    // R.g:150:1: for_expr returns [ASTNode v] : FOR n_ LPAR n_ ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final ASTNode for_expr() throws RecognitionException {
        ASTNode v = null;

        int for_expr_StartIndex = input.index();

        ASTNode in =null;

        ASTNode body =null;


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
    // R.g:153:1: repeat_expr returns [ASTNode v] : REPEAT n_ body= expr_or_assign ;
    public final ASTNode repeat_expr() throws RecognitionException {
        ASTNode v = null;

        int repeat_expr_StartIndex = input.index();

        ASTNode body =null;


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
    // R.g:156:1: function returns [ASTNode v] : FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign ;
    public final ASTNode function() throws RecognitionException {
        ASTNode v = null;

        int function_StartIndex = input.index();

        ASTNode body =null;


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
        ASTNode e =null;


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
    // R.g:172:1: tilde_expr returns [ASTNode v] : l= or_expr ( ( ( TILDE )=> TILDE n_ r= or_expr ) )* ;
    public final ASTNode tilde_expr() throws RecognitionException {
        ASTNode v = null;

        int tilde_expr_StartIndex = input.index();

        ASTNode l =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return v; }

            // R.g:173:2: (l= or_expr ( ( ( TILDE )=> TILDE n_ r= or_expr ) )* )
            // R.g:173:4: l= or_expr ( ( ( TILDE )=> TILDE n_ r= or_expr ) )*
            {
            pushFollow(FOLLOW_or_expr_in_tilde_expr1032);
            l=or_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:174:2: ( ( ( TILDE )=> TILDE n_ r= or_expr ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==TILDE) ) {
                    int LA19_2 = input.LA(2);

                    if ( (synpred9_R()) ) {
                        alt19=1;
                    }


                }


                switch (alt19) {
            	case 1 :
            	    // R.g:174:4: ( ( TILDE )=> TILDE n_ r= or_expr )
            	    {
            	    // R.g:174:4: ( ( TILDE )=> TILDE n_ r= or_expr )
            	    // R.g:174:5: ( TILDE )=> TILDE n_ r= or_expr
            	    {
            	    match(input,TILDE,FOLLOW_TILDE_in_tilde_expr1044); if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_tilde_expr1046);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_or_expr_in_tilde_expr1050);
            	    r=or_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) {v = BinaryOperation.create(BinaryOperator.ADD, v, r);}

            	    }


            	    }
            	    break;

            	default :
            	    break loop19;
                }
            } while (true);


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
    // R.g:176:1: or_expr returns [ASTNode v] : l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )* ;
    public final ASTNode or_expr() throws RecognitionException {
        ASTNode v = null;

        int or_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return v; }

            // R.g:177:2: (l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )* )
            // R.g:177:4: l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )*
            {
            pushFollow(FOLLOW_and_expr_in_or_expr1072);
            l=and_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:178:2: ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==OR) ) {
                    int LA20_2 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt20=1;
                    }


                }
                else if ( (LA20_0==BITWISEOR) ) {
                    int LA20_3 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt20=1;
                    }


                }


                switch (alt20) {
            	case 1 :
            	    // R.g:178:3: ( ( or_operator )=>op= or_operator n_ r= and_expr )
            	    {
            	    // R.g:178:3: ( ( or_operator )=>op= or_operator n_ r= and_expr )
            	    // R.g:178:4: ( or_operator )=>op= or_operator n_ r= and_expr
            	    {
            	    pushFollow(FOLLOW_or_operator_in_or_expr1085);
            	    op=or_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_or_expr1087);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_and_expr_in_or_expr1091);
            	    r=and_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) {v = BinaryOperation.create(op, v, r);}

            	    }


            	    }
            	    break;

            	default :
            	    break loop20;
                }
            } while (true);


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
    // R.g:180:1: and_expr returns [ASTNode v] : l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )* ;
    public final ASTNode and_expr() throws RecognitionException {
        ASTNode v = null;

        int and_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return v; }

            // R.g:181:2: (l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )* )
            // R.g:181:4: l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )*
            {
            pushFollow(FOLLOW_not_expr_in_and_expr1113);
            l=not_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:182:5: ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==AND) ) {
                    int LA21_2 = input.LA(2);

                    if ( (synpred11_R()) ) {
                        alt21=1;
                    }


                }
                else if ( (LA21_0==BITWISEAND) ) {
                    int LA21_3 = input.LA(2);

                    if ( (synpred11_R()) ) {
                        alt21=1;
                    }


                }


                switch (alt21) {
            	case 1 :
            	    // R.g:182:6: ( ( and_operator )=>op= and_operator n_ r= not_expr )
            	    {
            	    // R.g:182:6: ( ( and_operator )=>op= and_operator n_ r= not_expr )
            	    // R.g:182:7: ( and_operator )=>op= and_operator n_ r= not_expr
            	    {
            	    pushFollow(FOLLOW_and_operator_in_and_expr1129);
            	    op=and_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_and_expr1131);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_not_expr_in_and_expr1135);
            	    r=not_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) {v = BinaryOperation.create(op, v, r);}

            	    }


            	    }
            	    break;

            	default :
            	    break loop21;
                }
            } while (true);


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



    // $ANTLR start "not_expr"
    // R.g:184:1: not_expr returns [ASTNode v] : ( NOT n_ l= not_expr |b= comp_expr );
    public final ASTNode not_expr() throws RecognitionException {
        ASTNode v = null;

        int not_expr_StartIndex = input.index();

        ASTNode l =null;

        ASTNode b =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return v; }

            // R.g:185:2: ( NOT n_ l= not_expr |b= comp_expr )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==NOT) ) {
                alt22=1;
            }
            else if ( (LA22_0==BREAK||(LA22_0 >= COMPLEX && LA22_0 <= DD)||LA22_0==DOUBLE||LA22_0==FALSE||(LA22_0 >= FOR && LA22_0 <= FUNCTION)||LA22_0==ID||LA22_0==IF||LA22_0==INTEGER||LA22_0==LBRACE||LA22_0==LPAR||LA22_0==MINUS||LA22_0==NA||LA22_0==NEXT||LA22_0==NULL||LA22_0==PLUS||LA22_0==REPEAT||LA22_0==STRING||(LA22_0 >= TILDE && LA22_0 <= TRUE)||(LA22_0 >= VARIATIC && LA22_0 <= WHILE)) ) {
                alt22=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 22, 0, input);

                throw nvae;

            }
            switch (alt22) {
                case 1 :
                    // R.g:185:4: NOT n_ l= not_expr
                    {
                    match(input,NOT,FOLLOW_NOT_in_not_expr1155); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_not_expr1157);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_not_expr_in_not_expr1161);
                    l=not_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.NOT, l);}

                    }
                    break;
                case 2 :
                    // R.g:186:4: b= comp_expr
                    {
                    pushFollow(FOLLOW_comp_expr_in_not_expr1170);
                    b=comp_expr();

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
            if ( state.backtracking>0 ) { memoize(input, 21, not_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "not_expr"



    // $ANTLR start "comp_expr"
    // R.g:188:1: comp_expr returns [ASTNode v] : l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )* ;
    public final ASTNode comp_expr() throws RecognitionException {
        ASTNode v = null;

        int comp_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return v; }

            // R.g:189:2: (l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )* )
            // R.g:189:4: l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )*
            {
            pushFollow(FOLLOW_add_expr_in_comp_expr1188);
            l=add_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:190:5: ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )*
            loop23:
            do {
                int alt23=2;
                switch ( input.LA(1) ) {
                case GT:
                    {
                    int LA23_2 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case GE:
                    {
                    int LA23_3 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case LT:
                    {
                    int LA23_4 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case LE:
                    {
                    int LA23_5 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case EQ:
                    {
                    int LA23_6 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case NE:
                    {
                    int LA23_7 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt23=1;
                    }


                    }
                    break;

                }

                switch (alt23) {
            	case 1 :
            	    // R.g:190:6: ( ( comp_operator )=>op= comp_operator n_ r= add_expr )
            	    {
            	    // R.g:190:6: ( ( comp_operator )=>op= comp_operator n_ r= add_expr )
            	    // R.g:190:7: ( comp_operator )=>op= comp_operator n_ r= add_expr
            	    {
            	    pushFollow(FOLLOW_comp_operator_in_comp_expr1204);
            	    op=comp_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_comp_expr1206);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_add_expr_in_comp_expr1210);
            	    r=add_expr();

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
            if ( state.backtracking>0 ) { memoize(input, 22, comp_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "comp_expr"



    // $ANTLR start "add_expr"
    // R.g:192:1: add_expr returns [ASTNode v] : l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )* ;
    public final ASTNode add_expr() throws RecognitionException {
        ASTNode v = null;

        int add_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return v; }

            // R.g:193:2: (l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )* )
            // R.g:193:4: l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            {
            pushFollow(FOLLOW_mult_expr_in_add_expr1235);
            l=mult_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:194:3: ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==PLUS) ) {
                    int LA24_2 = input.LA(2);

                    if ( (synpred13_R()) ) {
                        alt24=1;
                    }


                }
                else if ( (LA24_0==MINUS) ) {
                    int LA24_3 = input.LA(2);

                    if ( (synpred13_R()) ) {
                        alt24=1;
                    }


                }


                switch (alt24) {
            	case 1 :
            	    // R.g:194:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    {
            	    // R.g:194:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    // R.g:194:5: ( add_operator )=>op= add_operator n_ r= mult_expr
            	    {
            	    pushFollow(FOLLOW_add_operator_in_add_expr1249);
            	    op=add_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_add_expr1251);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_mult_expr_in_add_expr1255);
            	    r=mult_expr();

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
            if ( state.backtracking>0 ) { memoize(input, 23, add_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "add_expr"



    // $ANTLR start "mult_expr"
    // R.g:196:1: mult_expr returns [ASTNode v] : l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )* ;
    public final ASTNode mult_expr() throws RecognitionException {
        ASTNode v = null;

        int mult_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return v; }

            // R.g:197:2: (l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )* )
            // R.g:197:4: l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            {
            pushFollow(FOLLOW_operator_expr_in_mult_expr1277);
            l=operator_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:198:2: ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            loop25:
            do {
                int alt25=2;
                switch ( input.LA(1) ) {
                case MULT:
                    {
                    int LA25_2 = input.LA(2);

                    if ( (synpred14_R()) ) {
                        alt25=1;
                    }


                    }
                    break;
                case DIV:
                    {
                    int LA25_3 = input.LA(2);

                    if ( (synpred14_R()) ) {
                        alt25=1;
                    }


                    }
                    break;
                case MOD:
                    {
                    int LA25_4 = input.LA(2);

                    if ( (synpred14_R()) ) {
                        alt25=1;
                    }


                    }
                    break;

                }

                switch (alt25) {
            	case 1 :
            	    // R.g:198:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    {
            	    // R.g:198:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    // R.g:198:4: ( mult_operator )=>op= mult_operator n_ r= operator_expr
            	    {
            	    pushFollow(FOLLOW_mult_operator_in_mult_expr1290);
            	    op=mult_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_mult_expr1292);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_operator_expr_in_mult_expr1296);
            	    r=operator_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) { v = BinaryOperation.create(op, v, r);}

            	    }


            	    }
            	    break;

            	default :
            	    break loop25;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 24, mult_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "mult_expr"



    // $ANTLR start "operator_expr"
    // R.g:200:1: operator_expr returns [ASTNode v] : l= column_expr ( ( ( OP )=>op= OP n_ r= column_expr ) )* ;
    public final ASTNode operator_expr() throws RecognitionException {
        ASTNode v = null;

        int operator_expr_StartIndex = input.index();

        Token op=null;
        ASTNode l =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return v; }

            // R.g:201:2: (l= column_expr ( ( ( OP )=>op= OP n_ r= column_expr ) )* )
            // R.g:201:4: l= column_expr ( ( ( OP )=>op= OP n_ r= column_expr ) )*
            {
            pushFollow(FOLLOW_column_expr_in_operator_expr1318);
            l=column_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:202:2: ( ( ( OP )=>op= OP n_ r= column_expr ) )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==OP) ) {
                    int LA26_2 = input.LA(2);

                    if ( (synpred15_R()) ) {
                        alt26=1;
                    }


                }


                switch (alt26) {
            	case 1 :
            	    // R.g:202:3: ( ( OP )=>op= OP n_ r= column_expr )
            	    {
            	    // R.g:202:3: ( ( OP )=>op= OP n_ r= column_expr )
            	    // R.g:202:4: ( OP )=>op= OP n_ r= column_expr
            	    {
            	    op=(Token)match(input,OP,FOLLOW_OP_in_operator_expr1331); if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_operator_expr1333);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_column_expr_in_operator_expr1337);
            	    r=column_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) { v = null; }

            	    }


            	    }
            	    break;

            	default :
            	    break loop26;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 25, operator_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "operator_expr"



    // $ANTLR start "column_expr"
    // R.g:204:1: column_expr returns [ASTNode v] : l= power_expr ( ( ( COLUMN )=>op= COLUMN n_ r= power_expr ) )* ;
    public final ASTNode column_expr() throws RecognitionException {
        ASTNode v = null;

        int column_expr_StartIndex = input.index();

        Token op=null;
        ASTNode l =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return v; }

            // R.g:205:2: (l= power_expr ( ( ( COLUMN )=>op= COLUMN n_ r= power_expr ) )* )
            // R.g:205:4: l= power_expr ( ( ( COLUMN )=>op= COLUMN n_ r= power_expr ) )*
            {
            pushFollow(FOLLOW_power_expr_in_column_expr1364);
            l=power_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:206:2: ( ( ( COLUMN )=>op= COLUMN n_ r= power_expr ) )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==COLUMN) ) {
                    int LA27_2 = input.LA(2);

                    if ( (synpred16_R()) ) {
                        alt27=1;
                    }


                }


                switch (alt27) {
            	case 1 :
            	    // R.g:206:3: ( ( COLUMN )=>op= COLUMN n_ r= power_expr )
            	    {
            	    // R.g:206:3: ( ( COLUMN )=>op= COLUMN n_ r= power_expr )
            	    // R.g:206:4: ( COLUMN )=>op= COLUMN n_ r= power_expr
            	    {
            	    op=(Token)match(input,COLUMN,FOLLOW_COLUMN_in_column_expr1377); if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_column_expr1379);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_power_expr_in_column_expr1383);
            	    r=power_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) { v = BinaryOperation.create(BinaryOperator.COLUMN, v, r);}

            	    }


            	    }
            	    break;

            	default :
            	    break loop27;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 26, column_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "column_expr"



    // $ANTLR start "power_expr"
    // R.g:208:1: power_expr returns [ASTNode v] : l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |) ;
    public final ASTNode power_expr() throws RecognitionException {
        ASTNode v = null;

        int power_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return v; }

            // R.g:209:2: (l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |) )
            // R.g:209:4: l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |)
            {
            pushFollow(FOLLOW_unary_expression_in_power_expr1405);
            l=unary_expression();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) {v =l;}

            // R.g:210:5: ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |)
            int alt28=2;
            int LA28_0 = input.LA(1);

            if ( (LA28_0==CARRET) ) {
                int LA28_1 = input.LA(2);

                if ( (synpred17_R()) ) {
                    alt28=1;
                }
                else if ( (true) ) {
                    alt28=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 28, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA28_0==EOF||(LA28_0 >= AND && LA28_0 <= BITWISEOR)||(LA28_0 >= COLUMN && LA28_0 <= COMMENT)||LA28_0==DIV||(LA28_0 >= ELSE && LA28_0 <= EQ)||LA28_0==FIELD||(LA28_0 >= GE && LA28_0 <= GT)||LA28_0==LBB||(LA28_0 >= LBRAKET && LA28_0 <= LE)||(LA28_0 >= LPAR && LA28_0 <= MINUS)||(LA28_0 >= MOD && LA28_0 <= MULT)||(LA28_0 >= NE && LA28_0 <= NEWLINE)||LA28_0==OP||LA28_0==OR||(LA28_0 >= PLUS && LA28_0 <= RBRAKET)||(LA28_0 >= RIGHT_ARROW && LA28_0 <= SEMICOLUMN)||(LA28_0 >= SUPER_ARROW && LA28_0 <= TILDE)) ) {
                alt28=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 28, 0, input);

                throw nvae;

            }
            switch (alt28) {
                case 1 :
                    // R.g:210:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    {
                    // R.g:210:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    // R.g:210:7: ( power_operator )=>op= power_operator n_ r= power_expr
                    {
                    pushFollow(FOLLOW_power_operator_in_power_expr1421);
                    op=power_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_power_expr1423);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_power_expr_in_power_expr1427);
                    r=power_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = BinaryOperation.create(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:211:6: 
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
            if ( state.backtracking>0 ) { memoize(input, 27, power_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "power_expr"



    // $ANTLR start "unary_expression"
    // R.g:213:1: unary_expression returns [ASTNode v] : ( PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression |b= basic_expr );
    public final ASTNode unary_expression() throws RecognitionException {
        ASTNode v = null;

        int unary_expression_StartIndex = input.index();

        ASTNode l =null;

        ASTNode b =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return v; }

            // R.g:214:2: ( PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression |b= basic_expr )
            int alt29=4;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt29=1;
                }
                break;
            case MINUS:
                {
                alt29=2;
                }
                break;
            case TILDE:
                {
                alt29=3;
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
                alt29=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;

            }

            switch (alt29) {
                case 1 :
                    // R.g:214:4: PLUS n_ l= unary_expression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unary_expression1456); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1458);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1462);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.PLUS, l);}

                    }
                    break;
                case 2 :
                    // R.g:215:4: MINUS n_ l= unary_expression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unary_expression1469); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1471);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1475);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.MINUS, l);}

                    }
                    break;
                case 3 :
                    // R.g:216:4: TILDE n_ l= unary_expression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unary_expression1482); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1484);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1488);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.MODEL, l);}

                    }
                    break;
                case 4 :
                    // R.g:217:4: b= basic_expr
                    {
                    pushFollow(FOLLOW_basic_expr_in_unary_expression1497);
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
            if ( state.backtracking>0 ) { memoize(input, 28, unary_expression_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "unary_expression"



    // $ANTLR start "basic_expr"
    // R.g:219:1: basic_expr returns [ASTNode v] : lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) ;
    public final ASTNode basic_expr() throws RecognitionException {
        ASTNode v = null;

        int basic_expr_StartIndex = input.index();

        ASTNode lhs =null;

        ASTNode subset =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return v; }

            // R.g:220:2: (lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) )
            // R.g:220:4: lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            {
            pushFollow(FOLLOW_simple_expr_in_basic_expr1515);
            lhs=simple_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = lhs; }

            // R.g:221:2: ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            int alt31=2;
            int LA31_0 = input.LA(1);

            if ( (LA31_0==FIELD) ) {
                int LA31_1 = input.LA(2);

                if ( (true) ) {
                    alt31=1;
                }
                else if ( (synpred19_R()) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 1, input);

                    throw nvae;

                }
            }
            else if ( (LA31_0==AT) ) {
                int LA31_2 = input.LA(2);

                if ( (true) ) {
                    alt31=1;
                }
                else if ( (synpred19_R()) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 2, input);

                    throw nvae;

                }
            }
            else if ( (LA31_0==LBRAKET) ) {
                int LA31_3 = input.LA(2);

                if ( (true) ) {
                    alt31=1;
                }
                else if ( (synpred19_R()) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 3, input);

                    throw nvae;

                }
            }
            else if ( (LA31_0==LBB) ) {
                int LA31_4 = input.LA(2);

                if ( (true) ) {
                    alt31=1;
                }
                else if ( (synpred19_R()) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 4, input);

                    throw nvae;

                }
            }
            else if ( (LA31_0==LPAR) ) {
                int LA31_5 = input.LA(2);

                if ( (true) ) {
                    alt31=1;
                }
                else if ( (synpred19_R()) ) {
                    alt31=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 31, 5, input);

                    throw nvae;

                }
            }
            else if ( (LA31_0==CARRET) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==COLUMN) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==OP) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==MULT) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==DIV) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==MOD) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==PLUS) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==MINUS) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==GT) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==GE) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==LT) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==LE) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==EQ) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==NE) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==AND) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==BITWISEAND) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==OR) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==BITWISEOR) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==TILDE) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==ARROW) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==SUPER_ARROW) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==RIGHT_ARROW) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==SUPER_RIGHT_ARROW) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==COMMENT||LA31_0==NEWLINE) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==COMMA) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==RPAR) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==RBRAKET) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==ASSIGN) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==EOF) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==SEMICOLUMN) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==RBRACE) && (synpred19_R())) {
                alt31=2;
            }
            else if ( (LA31_0==ELSE) && (synpred19_R())) {
                alt31=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 31, 0, input);

                throw nvae;

            }
            switch (alt31) {
                case 1 :
                    // R.g:221:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    {
                    // R.g:221:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    int cnt30=0;
                    loop30:
                    do {
                        int alt30=2;
                        switch ( input.LA(1) ) {
                        case FIELD:
                            {
                            int LA30_2 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt30=1;
                            }


                            }
                            break;
                        case AT:
                            {
                            int LA30_3 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt30=1;
                            }


                            }
                            break;
                        case LBRAKET:
                            {
                            int LA30_4 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt30=1;
                            }


                            }
                            break;
                        case LBB:
                            {
                            int LA30_5 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt30=1;
                            }


                            }
                            break;
                        case LPAR:
                            {
                            int LA30_6 = input.LA(2);

                            if ( (synpred18_R()) ) {
                                alt30=1;
                            }


                            }
                            break;

                        }

                        switch (alt30) {
                    	case 1 :
                    	    // R.g:221:4: ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v]
                    	    {
                    	    pushFollow(FOLLOW_expr_subset_in_basic_expr1536);
                    	    subset=expr_subset(v);

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    if ( state.backtracking==0 ) { v = subset; }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt30 >= 1 ) break loop30;
                    	    if (state.backtracking>0) {state.failed=true; return v;}
                                EarlyExitException eee =
                                    new EarlyExitException(30, input);
                                throw eee;
                        }
                        cnt30++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // R.g:221:76: ( n_ )=>
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
            if ( state.backtracking>0 ) { memoize(input, 29, basic_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "basic_expr"



    // $ANTLR start "expr_subset"
    // R.g:223:1: expr_subset[ASTNode i] returns [ASTNode v] : ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= args RBRAKET ) | ( LBB subscript= args RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) );
    public final ASTNode expr_subset(ASTNode i) throws RecognitionException {
        ASTNode v = null;

        int expr_subset_StartIndex = input.index();

        Token name =null;

        ArgumentList subset =null;

        ArgumentList subscript =null;

        ArgumentList a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return v; }

            // R.g:224:5: ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= args RBRAKET ) | ( LBB subscript= args RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) )
            int alt32=5;
            switch ( input.LA(1) ) {
            case FIELD:
                {
                alt32=1;
                }
                break;
            case AT:
                {
                alt32=2;
                }
                break;
            case LBRAKET:
                {
                alt32=3;
                }
                break;
            case LBB:
                {
                alt32=4;
                }
                break;
            case LPAR:
                {
                alt32=5;
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
                    // R.g:224:7: ( FIELD n_ name= id )
                    {
                    // R.g:224:7: ( FIELD n_ name= id )
                    // R.g:224:8: FIELD n_ name= id
                    {
                    match(input,FIELD,FOLLOW_FIELD_in_expr_subset1569); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_expr_subset1571);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_expr_subset1575);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = FieldAccess.create(FieldOperator.FIELD, i, name.getText()); }

                    }
                    break;
                case 2 :
                    // R.g:225:7: ( AT n_ name= id )
                    {
                    // R.g:225:7: ( AT n_ name= id )
                    // R.g:225:8: AT n_ name= id
                    {
                    match(input,AT,FOLLOW_AT_in_expr_subset1588); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_expr_subset1590);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_expr_subset1594);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = FieldAccess.create(FieldOperator.AT, i, name.getText()); }

                    }
                    break;
                case 3 :
                    // R.g:226:7: ( LBRAKET subset= args RBRAKET )
                    {
                    // R.g:226:7: ( LBRAKET subset= args RBRAKET )
                    // R.g:226:8: LBRAKET subset= args RBRAKET
                    {
                    match(input,LBRAKET,FOLLOW_LBRAKET_in_expr_subset1608); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1612);
                    subset=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1614); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Call.create(CallOperator.SUBSET, i, subset); }

                    }
                    break;
                case 4 :
                    // R.g:227:7: ( LBB subscript= args RBRAKET RBRAKET )
                    {
                    // R.g:227:7: ( LBB subscript= args RBRAKET RBRAKET )
                    // R.g:227:8: LBB subscript= args RBRAKET RBRAKET
                    {
                    match(input,LBB,FOLLOW_LBB_in_expr_subset1626); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1630);
                    subscript=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1632); if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1634); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Call.create(CallOperator.SUBSCRIPT, i, subscript); }

                    }
                    break;
                case 5 :
                    // R.g:229:7: ( LPAR a= args RPAR )
                    {
                    // R.g:229:7: ( LPAR a= args RPAR )
                    // R.g:229:8: LPAR a= args RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_expr_subset1651); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1655);
                    a=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RPAR,FOLLOW_RPAR_in_expr_subset1657); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 30, expr_subset_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "expr_subset"



    // $ANTLR start "simple_expr"
    // R.g:232:1: simple_expr returns [ASTNode v] : (i= id |b= bool | DD | NULL |num= number | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR |s= sequence |e= expr_wo_assign );
    public final ASTNode simple_expr() throws RecognitionException {
        ASTNode v = null;

        int simple_expr_StartIndex = input.index();

        Token i =null;

        ASTNode b =null;

        ASTNode num =null;

        ASTNode ea =null;

        ASTNode s =null;

        ASTNode e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return v; }

            // R.g:233:2: (i= id |b= bool | DD | NULL |num= number | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR |s= sequence |e= expr_wo_assign )
            int alt33=10;
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
                    alt33=1;
                    }
                    break;
                case NS_GET:
                    {
                    alt33=6;
                    }
                    break;
                case NS_GET_INT:
                    {
                    alt33=7;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 1, input);

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
                    alt33=1;
                    }
                    break;
                case NS_GET:
                    {
                    alt33=6;
                    }
                    break;
                case NS_GET_INT:
                    {
                    alt33=7;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 2, input);

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
                    alt33=1;
                    }
                    break;
                case NS_GET:
                    {
                    alt33=6;
                    }
                    break;
                case NS_GET_INT:
                    {
                    alt33=7;
                    }
                    break;
                default:
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 33, 3, input);

                    throw nvae;

                }

                }
                break;
            case FALSE:
            case NA:
            case TRUE:
                {
                alt33=2;
                }
                break;
            case DD:
                {
                alt33=3;
                }
                break;
            case NULL:
                {
                alt33=4;
                }
                break;
            case COMPLEX:
            case DOUBLE:
            case INTEGER:
                {
                alt33=5;
                }
                break;
            case LPAR:
                {
                alt33=8;
                }
                break;
            case LBRACE:
                {
                alt33=9;
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
                alt33=10;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;

            }

            switch (alt33) {
                case 1 :
                    // R.g:233:4: i= id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1686);
                    i=id();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AccessVariable.create(i.getText()); }

                    }
                    break;
                case 2 :
                    // R.g:234:4: b= bool
                    {
                    pushFollow(FOLLOW_bool_in_simple_expr1695);
                    b=bool();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = b; }

                    }
                    break;
                case 3 :
                    // R.g:235:4: DD
                    {
                    match(input,DD,FOLLOW_DD_in_simple_expr1702); if (state.failed) return v;

                    }
                    break;
                case 4 :
                    // R.g:236:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_simple_expr1707); if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = Constant.getNull(); }

                    }
                    break;
                case 5 :
                    // R.g:237:4: num= number
                    {
                    pushFollow(FOLLOW_number_in_simple_expr1716);
                    num=number();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = num; }

                    }
                    break;
                case 6 :
                    // R.g:238:4: id NS_GET n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1723);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,NS_GET,FOLLOW_NS_GET_in_simple_expr1725); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1727);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_simple_expr1729);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 7 :
                    // R.g:239:4: id NS_GET_INT n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1734);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,NS_GET_INT,FOLLOW_NS_GET_INT_in_simple_expr1736); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1738);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_simple_expr1740);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 8 :
                    // R.g:240:4: LPAR n_ ea= expr_or_assign n_ RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_simple_expr1745); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1747);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_simple_expr1753);
                    ea=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1755);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RPAR,FOLLOW_RPAR_in_simple_expr1757); if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = ea; }

                    }
                    break;
                case 9 :
                    // R.g:241:4: s= sequence
                    {
                    pushFollow(FOLLOW_sequence_in_simple_expr1768);
                    s=sequence();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = s;}

                    }
                    break;
                case 10 :
                    // R.g:242:4: e= expr_wo_assign
                    {
                    pushFollow(FOLLOW_expr_wo_assign_in_simple_expr1779);
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
            if ( state.backtracking>0 ) { memoize(input, 31, simple_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "simple_expr"



    // $ANTLR start "number"
    // R.g:244:1: number returns [ASTNode n] : (i= INTEGER |d= DOUBLE |c= COMPLEX );
    public final ASTNode number() throws RecognitionException {
        ASTNode n = null;

        int number_StartIndex = input.index();

        Token i=null;
        Token d=null;
        Token c=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return n; }

            // R.g:245:5: (i= INTEGER |d= DOUBLE |c= COMPLEX )
            int alt34=3;
            switch ( input.LA(1) ) {
            case INTEGER:
                {
                alt34=1;
                }
                break;
            case DOUBLE:
                {
                alt34=2;
                }
                break;
            case COMPLEX:
                {
                alt34=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return n;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;

            }

            switch (alt34) {
                case 1 :
                    // R.g:245:7: i= INTEGER
                    {
                    i=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_number1800); if (state.failed) return n;

                    if ( state.backtracking==0 ) { n = Constant.createIntConstant((i!=null?i.getText():null)); }

                    }
                    break;
                case 2 :
                    // R.g:246:7: d= DOUBLE
                    {
                    d=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_number1812); if (state.failed) return n;

                    if ( state.backtracking==0 ) { n = Constant.createDoubleConstant((d!=null?d.getText():null)); }

                    }
                    break;
                case 3 :
                    // R.g:247:7: c= COMPLEX
                    {
                    c=(Token)match(input,COMPLEX,FOLLOW_COMPLEX_in_number1824); if (state.failed) return n;

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
            if ( state.backtracking>0 ) { memoize(input, 32, number_StartIndex); }

        }
        return n;
    }
    // $ANTLR end "number"



    // $ANTLR start "id"
    // R.g:249:1: id returns [Token t] : (i= ID |s= STRING |v= VARIATIC );
    public final Token id() throws RecognitionException {
        Token t = null;

        int id_StartIndex = input.index();

        Token i=null;
        Token s=null;
        Token v=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return t; }

            // R.g:250:5: (i= ID |s= STRING |v= VARIATIC )
            int alt35=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt35=1;
                }
                break;
            case STRING:
                {
                alt35=2;
                }
                break;
            case VARIATIC:
                {
                alt35=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return t;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;

            }

            switch (alt35) {
                case 1 :
                    // R.g:250:7: i= ID
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_id1848); if (state.failed) return t;

                    if ( state.backtracking==0 ) { t = i; }

                    }
                    break;
                case 2 :
                    // R.g:251:7: s= STRING
                    {
                    s=(Token)match(input,STRING,FOLLOW_STRING_in_id1860); if (state.failed) return t;

                    if ( state.backtracking==0 ) { t = s; }

                    }
                    break;
                case 3 :
                    // R.g:252:7: v= VARIATIC
                    {
                    v=(Token)match(input,VARIATIC,FOLLOW_VARIATIC_in_id1872); if (state.failed) return t;

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
            if ( state.backtracking>0 ) { memoize(input, 33, id_StartIndex); }

        }
        return t;
    }
    // $ANTLR end "id"



    // $ANTLR start "bool"
    // R.g:254:1: bool returns [ASTNode v] : ( TRUE | FALSE | NA );
    public final ASTNode bool() throws RecognitionException {
        ASTNode v = null;

        int bool_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return v; }

            // R.g:255:5: ( TRUE | FALSE | NA )
            int alt36=3;
            switch ( input.LA(1) ) {
            case TRUE:
                {
                alt36=1;
                }
                break;
            case FALSE:
                {
                alt36=2;
                }
                break;
            case NA:
                {
                alt36=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 36, 0, input);

                throw nvae;

            }

            switch (alt36) {
                case 1 :
                    // R.g:255:7: TRUE
                    {
                    match(input,TRUE,FOLLOW_TRUE_in_bool1894); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Constant.createBoolConstant(1); }

                    }
                    break;
                case 2 :
                    // R.g:256:7: FALSE
                    {
                    match(input,FALSE,FOLLOW_FALSE_in_bool1904); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Constant.createBoolConstant(0); }

                    }
                    break;
                case 3 :
                    // R.g:257:7: NA
                    {
                    match(input,NA,FOLLOW_NA_in_bool1914); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 34, bool_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "bool"



    // $ANTLR start "or_operator"
    // R.g:259:1: or_operator returns [BinaryOperator v] : ( OR | BITWISEOR );
    public final BinaryOperator or_operator() throws RecognitionException {
        BinaryOperator v = null;

        int or_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return v; }

            // R.g:260:2: ( OR | BITWISEOR )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==OR) ) {
                alt37=1;
            }
            else if ( (LA37_0==BITWISEOR) ) {
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
                    // R.g:260:4: OR
                    {
                    match(input,OR,FOLLOW_OR_in_or_operator1933); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.OR; }

                    }
                    break;
                case 2 :
                    // R.g:261:5: BITWISEOR
                    {
                    match(input,BITWISEOR,FOLLOW_BITWISEOR_in_or_operator1950); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 35, or_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "or_operator"



    // $ANTLR start "and_operator"
    // R.g:262:1: and_operator returns [BinaryOperator v] : ( AND | BITWISEAND );
    public final BinaryOperator and_operator() throws RecognitionException {
        BinaryOperator v = null;

        int and_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return v; }

            // R.g:263:2: ( AND | BITWISEAND )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==AND) ) {
                alt38=1;
            }
            else if ( (LA38_0==BITWISEAND) ) {
                alt38=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 38, 0, input);

                throw nvae;

            }
            switch (alt38) {
                case 1 :
                    // R.g:263:4: AND
                    {
                    match(input,AND,FOLLOW_AND_in_and_operator1966); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.AND; }

                    }
                    break;
                case 2 :
                    // R.g:264:4: BITWISEAND
                    {
                    match(input,BITWISEAND,FOLLOW_BITWISEAND_in_and_operator1982); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 36, and_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "and_operator"



    // $ANTLR start "comp_operator"
    // R.g:265:1: comp_operator returns [BinaryOperator v] : ( GT | GE | LT | LE | EQ | NE );
    public final BinaryOperator comp_operator() throws RecognitionException {
        BinaryOperator v = null;

        int comp_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return v; }

            // R.g:266:2: ( GT | GE | LT | LE | EQ | NE )
            int alt39=6;
            switch ( input.LA(1) ) {
            case GT:
                {
                alt39=1;
                }
                break;
            case GE:
                {
                alt39=2;
                }
                break;
            case LT:
                {
                alt39=3;
                }
                break;
            case LE:
                {
                alt39=4;
                }
                break;
            case EQ:
                {
                alt39=5;
                }
                break;
            case NE:
                {
                alt39=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 39, 0, input);

                throw nvae;

            }

            switch (alt39) {
                case 1 :
                    // R.g:266:4: GT
                    {
                    match(input,GT,FOLLOW_GT_in_comp_operator1998); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.GT; }

                    }
                    break;
                case 2 :
                    // R.g:267:4: GE
                    {
                    match(input,GE,FOLLOW_GE_in_comp_operator2005); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.GE; }

                    }
                    break;
                case 3 :
                    // R.g:268:4: LT
                    {
                    match(input,LT,FOLLOW_LT_in_comp_operator2012); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.LT; }

                    }
                    break;
                case 4 :
                    // R.g:269:4: LE
                    {
                    match(input,LE,FOLLOW_LE_in_comp_operator2019); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.LE; }

                    }
                    break;
                case 5 :
                    // R.g:270:4: EQ
                    {
                    match(input,EQ,FOLLOW_EQ_in_comp_operator2026); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.EQ; }

                    }
                    break;
                case 6 :
                    // R.g:271:4: NE
                    {
                    match(input,NE,FOLLOW_NE_in_comp_operator2033); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 37, comp_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "comp_operator"



    // $ANTLR start "add_operator"
    // R.g:272:1: add_operator returns [BinaryOperator v] : ( PLUS | MINUS );
    public final BinaryOperator add_operator() throws RecognitionException {
        BinaryOperator v = null;

        int add_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return v; }

            // R.g:273:2: ( PLUS | MINUS )
            int alt40=2;
            int LA40_0 = input.LA(1);

            if ( (LA40_0==PLUS) ) {
                alt40=1;
            }
            else if ( (LA40_0==MINUS) ) {
                alt40=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 40, 0, input);

                throw nvae;

            }
            switch (alt40) {
                case 1 :
                    // R.g:273:4: PLUS
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_add_operator2047); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.ADD; }

                    }
                    break;
                case 2 :
                    // R.g:274:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_add_operator2054); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 38, add_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "add_operator"



    // $ANTLR start "mult_operator"
    // R.g:275:1: mult_operator returns [BinaryOperator v] : ( MULT | DIV | MOD );
    public final BinaryOperator mult_operator() throws RecognitionException {
        BinaryOperator v = null;

        int mult_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return v; }

            // R.g:276:2: ( MULT | DIV | MOD )
            int alt41=3;
            switch ( input.LA(1) ) {
            case MULT:
                {
                alt41=1;
                }
                break;
            case DIV:
                {
                alt41=2;
                }
                break;
            case MOD:
                {
                alt41=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 41, 0, input);

                throw nvae;

            }

            switch (alt41) {
                case 1 :
                    // R.g:276:4: MULT
                    {
                    match(input,MULT,FOLLOW_MULT_in_mult_operator2069); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.MULT; }

                    }
                    break;
                case 2 :
                    // R.g:277:4: DIV
                    {
                    match(input,DIV,FOLLOW_DIV_in_mult_operator2076); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.DIV; }

                    }
                    break;
                case 3 :
                    // R.g:278:4: MOD
                    {
                    match(input,MOD,FOLLOW_MOD_in_mult_operator2084); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 39, mult_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "mult_operator"



    // $ANTLR start "power_operator"
    // R.g:279:1: power_operator returns [BinaryOperator v] : CARRET ;
    public final BinaryOperator power_operator() throws RecognitionException {
        BinaryOperator v = null;

        int power_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return v; }

            // R.g:280:2: ( CARRET )
            // R.g:280:4: CARRET
            {
            match(input,CARRET,FOLLOW_CARRET_in_power_operator2099); if (state.failed) return v;

            if ( state.backtracking==0 ) {v = BinaryOperator.POW; }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 40, power_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "power_operator"



    // $ANTLR start "args"
    // R.g:282:1: args returns [ArgumentList v] : ( n_ arg_expr[v] )? n_ ( COMMA (| n_ arg_expr[v] ) n_ )* ;
    public final ArgumentList args() throws RecognitionException {
        ArgumentList v = null;

        int args_StartIndex = input.index();

         v = new ArgumentList.Default(); 
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return v; }

            // R.g:284:5: ( ( n_ arg_expr[v] )? n_ ( COMMA (| n_ arg_expr[v] ) n_ )* )
            // R.g:284:7: ( n_ arg_expr[v] )? n_ ( COMMA (| n_ arg_expr[v] ) n_ )*
            {
            // R.g:284:7: ( n_ arg_expr[v] )?
            int alt42=2;
            alt42 = dfa42.predict(input);
            switch (alt42) {
                case 1 :
                    // R.g:284:8: n_ arg_expr[v]
                    {
                    pushFollow(FOLLOW_n__in_args2124);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_arg_expr_in_args2126);
                    arg_expr(v);

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }


            pushFollow(FOLLOW_n__in_args2131);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:284:28: ( COMMA (| n_ arg_expr[v] ) n_ )*
            loop44:
            do {
                int alt44=2;
                int LA44_0 = input.LA(1);

                if ( (LA44_0==COMMA) ) {
                    alt44=1;
                }


                switch (alt44) {
            	case 1 :
            	    // R.g:284:29: COMMA (| n_ arg_expr[v] ) n_
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_args2134); if (state.failed) return v;

            	    // R.g:284:35: (| n_ arg_expr[v] )
            	    int alt43=2;
            	    alt43 = dfa43.predict(input);
            	    switch (alt43) {
            	        case 1 :
            	            // R.g:284:37: 
            	            {
            	            if ( state.backtracking==0 ) { v.add((ASTNode)null); }

            	            }
            	            break;
            	        case 2 :
            	            // R.g:284:66: n_ arg_expr[v]
            	            {
            	            pushFollow(FOLLOW_n__in_args2142);
            	            n_();

            	            state._fsp--;
            	            if (state.failed) return v;

            	            pushFollow(FOLLOW_arg_expr_in_args2144);
            	            arg_expr(v);

            	            state._fsp--;
            	            if (state.failed) return v;

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_n__in_args2148);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    }
            	    break;

            	default :
            	    break loop44;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 41, args_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "args"



    // $ANTLR start "arg_expr"
    // R.g:286:1: arg_expr[ArgumentList l] : (e= expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );
    public final void arg_expr(ArgumentList l) throws RecognitionException {
        int arg_expr_StartIndex = input.index();

        ASTNode e =null;

        Token name =null;

        ASTNode val =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return ; }

            // R.g:287:2: (e= expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN )
            int alt45=5;
            alt45 = dfa45.predict(input);
            switch (alt45) {
                case 1 :
                    // R.g:287:4: e= expr
                    {
                    pushFollow(FOLLOW_expr_in_arg_expr2165);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add(e); }

                    }
                    break;
                case 2 :
                    // R.g:288:4: name= id n_ ASSIGN n_ val= expr
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2174);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2176);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2178); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2180);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_arg_expr2184);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add(name.getText(), val); }

                    }
                    break;
                case 3 :
                    // R.g:289:4: name= id n_ ASSIGN
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2193);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2195);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2197); if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add(name.getText(), null); }

                    }
                    break;
                case 4 :
                    // R.g:290:4: NULL n_ ASSIGN n_ val= expr
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2205); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2207);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2209); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2211);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_arg_expr2215);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { Utils.nyi(); }

                    }
                    break;
                case 5 :
                    // R.g:291:4: NULL n_ ASSIGN
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2222); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2224);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2226); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 42, arg_expr_StartIndex); }

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
        match(input,TILDE,FOLLOW_TILDE_in_synpred9_R1041); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred9_R

    // $ANTLR start synpred10_R
    public final void synpred10_R_fragment() throws RecognitionException {
        // R.g:178:4: ( or_operator )
        // R.g:178:5: or_operator
        {
        pushFollow(FOLLOW_or_operator_in_synpred10_R1080);
        or_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred10_R

    // $ANTLR start synpred11_R
    public final void synpred11_R_fragment() throws RecognitionException {
        // R.g:182:7: ( and_operator )
        // R.g:182:8: and_operator
        {
        pushFollow(FOLLOW_and_operator_in_synpred11_R1124);
        and_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred11_R

    // $ANTLR start synpred12_R
    public final void synpred12_R_fragment() throws RecognitionException {
        // R.g:190:7: ( comp_operator )
        // R.g:190:8: comp_operator
        {
        pushFollow(FOLLOW_comp_operator_in_synpred12_R1199);
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
        pushFollow(FOLLOW_add_operator_in_synpred13_R1244);
        add_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred13_R

    // $ANTLR start synpred14_R
    public final void synpred14_R_fragment() throws RecognitionException {
        // R.g:198:4: ( mult_operator )
        // R.g:198:5: mult_operator
        {
        pushFollow(FOLLOW_mult_operator_in_synpred14_R1285);
        mult_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred14_R

    // $ANTLR start synpred15_R
    public final void synpred15_R_fragment() throws RecognitionException {
        // R.g:202:4: ( OP )
        // R.g:202:5: OP
        {
        match(input,OP,FOLLOW_OP_in_synpred15_R1326); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred15_R

    // $ANTLR start synpred16_R
    public final void synpred16_R_fragment() throws RecognitionException {
        // R.g:206:4: ( COLUMN )
        // R.g:206:5: COLUMN
        {
        match(input,COLUMN,FOLLOW_COLUMN_in_synpred16_R1372); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred16_R

    // $ANTLR start synpred17_R
    public final void synpred17_R_fragment() throws RecognitionException {
        // R.g:210:7: ( power_operator )
        // R.g:210:8: power_operator
        {
        pushFollow(FOLLOW_power_operator_in_synpred17_R1416);
        power_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred17_R

    // $ANTLR start synpred18_R
    public final void synpred18_R_fragment() throws RecognitionException {
        // R.g:221:4: ( FIELD | AT | LBRAKET | LBB | LPAR )
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
        // R.g:221:76: ( n_ )
        // R.g:221:77: n_
        {
        pushFollow(FOLLOW_n__in_synpred19_R1546);
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
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA43 dfa43 = new DFA43(this);
    protected DFA45 dfa45 = new DFA45(this);
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
            return "284:7: ( n_ arg_expr[v] )?";
        }
    }
    static final String DFA43_eotS =
        "\4\uffff";
    static final String DFA43_eofS =
        "\4\uffff";
    static final String DFA43_minS =
        "\2\13\2\uffff";
    static final String DFA43_maxS =
        "\2\121\2\uffff";
    static final String DFA43_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA43_specialS =
        "\4\uffff}>";
    static final String[] DFA43_transitionS = {
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

    static final short[] DFA43_eot = DFA.unpackEncodedString(DFA43_eotS);
    static final short[] DFA43_eof = DFA.unpackEncodedString(DFA43_eofS);
    static final char[] DFA43_min = DFA.unpackEncodedStringToUnsignedChars(DFA43_minS);
    static final char[] DFA43_max = DFA.unpackEncodedStringToUnsignedChars(DFA43_maxS);
    static final short[] DFA43_accept = DFA.unpackEncodedString(DFA43_acceptS);
    static final short[] DFA43_special = DFA.unpackEncodedString(DFA43_specialS);
    static final short[][] DFA43_transition;

    static {
        int numStates = DFA43_transitionS.length;
        DFA43_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA43_transition[i] = DFA.unpackEncodedString(DFA43_transitionS[i]);
        }
    }

    class DFA43 extends DFA {

        public DFA43(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 43;
            this.eot = DFA43_eot;
            this.eof = DFA43_eof;
            this.min = DFA43_min;
            this.max = DFA43_max;
            this.accept = DFA43_accept;
            this.special = DFA43_special;
            this.transition = DFA43_transition;
        }
        @Override
        public String getDescription() {
            return "284:35: (| n_ arg_expr[v] )";
        }
    }
    static final String DFA45_eotS =
        "\20\uffff";
    static final String DFA45_eofS =
        "\20\uffff";
    static final String DFA45_minS =
        "\1\13\1\uffff\4\4\1\6\1\13\1\6\2\13\2\uffff\1\13\2\uffff";
    static final String DFA45_maxS =
        "\1\121\1\uffff\4\112\1\104\1\121\1\104\2\121\2\uffff\1\121\2\uffff";
    static final String DFA45_acceptS =
        "\1\uffff\1\1\11\uffff\1\2\1\3\1\uffff\1\4\1\5";
    static final String DFA45_specialS =
        "\20\uffff}>";
    static final String[] DFA45_transitionS = {
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

    static final short[] DFA45_eot = DFA.unpackEncodedString(DFA45_eotS);
    static final short[] DFA45_eof = DFA.unpackEncodedString(DFA45_eofS);
    static final char[] DFA45_min = DFA.unpackEncodedStringToUnsignedChars(DFA45_minS);
    static final char[] DFA45_max = DFA.unpackEncodedStringToUnsignedChars(DFA45_maxS);
    static final short[] DFA45_accept = DFA.unpackEncodedString(DFA45_acceptS);
    static final short[] DFA45_special = DFA.unpackEncodedString(DFA45_specialS);
    static final short[][] DFA45_transition;

    static {
        int numStates = DFA45_transitionS.length;
        DFA45_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA45_transition[i] = DFA.unpackEncodedString(DFA45_transitionS[i]);
        }
    }

    class DFA45 extends DFA {

        public DFA45(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 45;
            this.eot = DFA45_eot;
            this.eof = DFA45_eof;
            this.min = DFA45_min;
            this.max = DFA45_max;
            this.accept = DFA45_accept;
            this.special = DFA45_special;
            this.transition = DFA45_transition;
        }
        @Override
        public String getDescription() {
            return "286:1: arg_expr[ArgumentList l] : (e= expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );";
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
    public static final BitSet FOLLOW_TILDE_in_tilde_expr1044 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_tilde_expr1046 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_or_expr_in_tilde_expr1050 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000400L});
    public static final BitSet FOLLOW_and_expr_in_or_expr1072 = new BitSet(new long[]{0x2000000000000202L});
    public static final BitSet FOLLOW_or_operator_in_or_expr1085 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_or_expr1087 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_and_expr_in_or_expr1091 = new BitSet(new long[]{0x2000000000000202L});
    public static final BitSet FOLLOW_not_expr_in_and_expr1113 = new BitSet(new long[]{0x0000000000000112L});
    public static final BitSet FOLLOW_and_operator_in_and_expr1129 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_and_expr1131 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_not_expr_in_and_expr1135 = new BitSet(new long[]{0x0000000000000112L});
    public static final BitSet FOLLOW_NOT_in_not_expr1155 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_not_expr1157 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_not_expr_in_not_expr1161 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_expr_in_not_expr1170 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1188 = new BitSet(new long[]{0x0008240060400002L});
    public static final BitSet FOLLOW_comp_operator_in_comp_expr1204 = new BitSet(new long[]{0x8234512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_comp_expr1206 = new BitSet(new long[]{0x8224512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1210 = new BitSet(new long[]{0x0008240060400002L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1235 = new BitSet(new long[]{0x8000400000000002L});
    public static final BitSet FOLLOW_add_operator_in_add_expr1249 = new BitSet(new long[]{0x8234512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_add_expr1251 = new BitSet(new long[]{0x8224512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1255 = new BitSet(new long[]{0x8000400000000002L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1277 = new BitSet(new long[]{0x0003000000080002L});
    public static final BitSet FOLLOW_mult_operator_in_mult_expr1290 = new BitSet(new long[]{0x8234512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_mult_expr1292 = new BitSet(new long[]{0x8224512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1296 = new BitSet(new long[]{0x0003000000080002L});
    public static final BitSet FOLLOW_column_expr_in_operator_expr1318 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_OP_in_operator_expr1331 = new BitSet(new long[]{0x8234512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_operator_expr1333 = new BitSet(new long[]{0x8224512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_column_expr_in_operator_expr1337 = new BitSet(new long[]{0x0800000000000002L});
    public static final BitSet FOLLOW_power_expr_in_column_expr1364 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_COLUMN_in_column_expr1377 = new BitSet(new long[]{0x8234512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_column_expr1379 = new BitSet(new long[]{0x8224512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_power_expr_in_column_expr1383 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_unary_expression_in_power_expr1405 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_power_operator_in_power_expr1421 = new BitSet(new long[]{0x8234512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_power_expr1423 = new BitSet(new long[]{0x8224512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_power_expr_in_power_expr1427 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary_expression1456 = new BitSet(new long[]{0x8234512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_unary_expression1458 = new BitSet(new long[]{0x8224512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary_expression1469 = new BitSet(new long[]{0x8234512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_unary_expression1471 = new BitSet(new long[]{0x8224512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1475 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unary_expression1482 = new BitSet(new long[]{0x8234512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_unary_expression1484 = new BitSet(new long[]{0x8224512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1488 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_basic_expr_in_unary_expression1497 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_basic_expr1515 = new BitSet(new long[]{0x0000128004000082L});
    public static final BitSet FOLLOW_expr_subset_in_basic_expr1536 = new BitSet(new long[]{0x0000128004000082L});
    public static final BitSet FOLLOW_FIELD_in_expr_subset1569 = new BitSet(new long[]{0x0010000200010000L,0x0000000000010080L});
    public static final BitSet FOLLOW_n__in_expr_subset1571 = new BitSet(new long[]{0x0000000200000000L,0x0000000000010080L});
    public static final BitSet FOLLOW_id_in_expr_subset1575 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_expr_subset1588 = new BitSet(new long[]{0x0010000200010000L,0x0000000000010080L});
    public static final BitSet FOLLOW_n__in_expr_subset1590 = new BitSet(new long[]{0x0000000200000000L,0x0000000000010080L});
    public static final BitSet FOLLOW_id_in_expr_subset1594 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRAKET_in_expr_subset1608 = new BitSet(new long[]{0x8274512A1A178800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_args_in_expr_subset1612 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1614 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBB_in_expr_subset1626 = new BitSet(new long[]{0x8274512A1A178800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_args_in_expr_subset1630 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1632 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1634 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_subset1651 = new BitSet(new long[]{0x8274512A1A178800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_args_in_expr_subset1655 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_expr_subset1657 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1686 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_in_simple_expr1695 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_simple_expr1702 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_simple_expr1707 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_simple_expr1716 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1723 = new BitSet(new long[]{0x0080000000000000L});
    public static final BitSet FOLLOW_NS_GET_in_simple_expr1725 = new BitSet(new long[]{0x0010000200010000L,0x0000000000010080L});
    public static final BitSet FOLLOW_n__in_simple_expr1727 = new BitSet(new long[]{0x0000000200000000L,0x0000000000010080L});
    public static final BitSet FOLLOW_id_in_simple_expr1729 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1734 = new BitSet(new long[]{0x0100000000000000L});
    public static final BitSet FOLLOW_NS_GET_INT_in_simple_expr1736 = new BitSet(new long[]{0x0010000200010000L,0x0000000000010080L});
    public static final BitSet FOLLOW_n__in_simple_expr1738 = new BitSet(new long[]{0x0000000200000000L,0x0000000000010080L});
    public static final BitSet FOLLOW_id_in_simple_expr1740 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_simple_expr1745 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_simple_expr1747 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_or_assign_in_simple_expr1753 = new BitSet(new long[]{0x0010000000010000L,0x0000000000000010L});
    public static final BitSet FOLLOW_n__in_simple_expr1755 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000010L});
    public static final BitSet FOLLOW_RPAR_in_simple_expr1757 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_simple_expr1768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_wo_assign_in_simple_expr1779 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_number1800 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_number1812 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMPLEX_in_number1824 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_id1848 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_id1860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_id1872 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_bool1894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_bool1904 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NA_in_bool1914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_or_operator1933 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEOR_in_or_operator1950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_and_operator1966 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEAND_in_and_operator1982 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_comp_operator1998 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GE_in_comp_operator2005 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_comp_operator2012 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LE_in_comp_operator2019 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_comp_operator2026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NE_in_comp_operator2033 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_add_operator2047 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_add_operator2054 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_in_mult_operator2069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_in_mult_operator2076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mult_operator2084 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARRET_in_power_operator2099 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_args2124 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_arg_expr_in_args2126 = new BitSet(new long[]{0x0010000000018000L});
    public static final BitSet FOLLOW_n__in_args2131 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_COMMA_in_args2134 = new BitSet(new long[]{0x8274512A1A178800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_args2142 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_arg_expr_in_args2144 = new BitSet(new long[]{0x0010000000018000L});
    public static final BitSet FOLLOW_n__in_args2148 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_expr_in_arg_expr2165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2174 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2176 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2178 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_arg_expr2180 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_arg_expr2184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2193 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2195 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2197 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2205 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2207 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2209 = new BitSet(new long[]{0x8274512A1A170800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_n__in_arg_expr2211 = new BitSet(new long[]{0x8264512A1A160800L,0x0000000000030C84L});
    public static final BitSet FOLLOW_expr_in_arg_expr2215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2222 = new BitSet(new long[]{0x0010000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2224 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_synpred1_R355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_synpred2_R374 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_synpred3_R555 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_synpred4_R573 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_synpred5_R591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_synpred6_R611 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_synpred7_R631 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred8_R694 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_ELSE_in_synpred8_R696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_synpred9_R1041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_operator_in_synpred10_R1080 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_operator_in_synpred11_R1124 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_operator_in_synpred12_R1199 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_operator_in_synpred13_R1244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_operator_in_synpred14_R1285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OP_in_synpred15_R1326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLUMN_in_synpred16_R1372 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_operator_in_synpred17_R1416 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred19_R1546 = new BitSet(new long[]{0x0000000000000002L});

}
