// $ANTLR 3.4 R.g 2012-11-12 13:51:02

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "ARROW", "ASSIGN", "AT", "BITWISEAND", "BITWISEOR", "BRAKET", "BREAK", "CALL", "CARRET", "COLON", "COMMA", "COMMENT", "COMPLEX", "DD", "DIV", "DOUBLE", "ELSE", "EQ", "ESCAPE", "ESC_SEQ", "EXPONENT", "FALSE", "FIELD", "FOR", "FUNCTION", "GE", "GT", "HEX_DIGIT", "HEX_ESC", "ID", "ID_NAME", "IF", "IN", "INTEGER", "KW", "LBB", "LBRACE", "LBRAKET", "LE", "LINE_BREAK", "LPAR", "LT", "MAT_MULT", "MINUS", "MISSING_VAL", "MOD", "MULT", "NA", "NE", "NEWLINE", "NEXT", "NOT", "NS_GET", "NS_GET_INT", "NULL", "OCTAL_ESC", "OP", "OP_NAME", "OR", "PARMS", "PLUS", "RBRACE", "RBRAKET", "REPEAT", "RIGHT_ARROW", "RPAR", "SEMICOLON", "SEQUENCE", "STRING", "SUPER_ARROW", "SUPER_RIGHT_ARROW", "TILDE", "TRUE", "UMINUS", "UNICODE_ESC", "UPLUS", "UTILDE", "VARIATIC", "WHILE", "WS", "'--EOF--'"
    };

    public static final int EOF=-1;
    public static final int T__85=85;
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
    public static final int COLON=14;
    public static final int COMMA=15;
    public static final int COMMENT=16;
    public static final int COMPLEX=17;
    public static final int DD=18;
    public static final int DIV=19;
    public static final int DOUBLE=20;
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
    public static final int MAT_MULT=47;
    public static final int MINUS=48;
    public static final int MISSING_VAL=49;
    public static final int MOD=50;
    public static final int MULT=51;
    public static final int NA=52;
    public static final int NE=53;
    public static final int NEWLINE=54;
    public static final int NEXT=55;
    public static final int NOT=56;
    public static final int NS_GET=57;
    public static final int NS_GET_INT=58;
    public static final int NULL=59;
    public static final int OCTAL_ESC=60;
    public static final int OP=61;
    public static final int OP_NAME=62;
    public static final int OR=63;
    public static final int PARMS=64;
    public static final int PLUS=65;
    public static final int RBRACE=66;
    public static final int RBRAKET=67;
    public static final int REPEAT=68;
    public static final int RIGHT_ARROW=69;
    public static final int RPAR=70;
    public static final int SEMICOLON=71;
    public static final int SEQUENCE=72;
    public static final int STRING=73;
    public static final int SUPER_ARROW=74;
    public static final int SUPER_RIGHT_ARROW=75;
    public static final int TILDE=76;
    public static final int TRUE=77;
    public static final int UMINUS=78;
    public static final int UNICODE_ESC=79;
    public static final int UPLUS=80;
    public static final int UTILDE=81;
    public static final int VARIATIC=82;
    public static final int WHILE=83;
    public static final int WS=84;

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

                if ( (LA1_0==BREAK||(LA1_0 >= COMPLEX && LA1_0 <= DD)||LA1_0==DOUBLE||LA1_0==FALSE||(LA1_0 >= FOR && LA1_0 <= FUNCTION)||LA1_0==ID||LA1_0==IF||LA1_0==INTEGER||LA1_0==LBRACE||LA1_0==LPAR||LA1_0==MINUS||LA1_0==NA||(LA1_0 >= NEXT && LA1_0 <= NOT)||LA1_0==NULL||LA1_0==PLUS||LA1_0==REPEAT||LA1_0==STRING||(LA1_0 >= TILDE && LA1_0 <= TRUE)||(LA1_0 >= VARIATIC && LA1_0 <= WHILE)||LA1_0==85) ) {
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
    // R.g:90:1: interactive returns [ASTNode v] : n_ (s= statement )* ;
    public final ASTNode interactive() throws RecognitionException {
        ASTNode v = null;

        int interactive_StartIndex = input.index();

        ASTNode s =null;


        ArrayList<ASTNode> stmts = new ArrayList<ASTNode>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return v; }

            // R.g:93:2: ( n_ (s= statement )* )
            // R.g:93:4: n_ (s= statement )*
            {
            pushFollow(FOLLOW_n__in_interactive192);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:93:7: (s= statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0==BREAK||(LA2_0 >= COMPLEX && LA2_0 <= DD)||LA2_0==DOUBLE||LA2_0==FALSE||(LA2_0 >= FOR && LA2_0 <= FUNCTION)||LA2_0==ID||LA2_0==IF||LA2_0==INTEGER||LA2_0==LBRACE||LA2_0==LPAR||LA2_0==MINUS||LA2_0==NA||(LA2_0 >= NEXT && LA2_0 <= NOT)||LA2_0==NULL||LA2_0==PLUS||LA2_0==REPEAT||LA2_0==STRING||(LA2_0 >= TILDE && LA2_0 <= TRUE)||(LA2_0 >= VARIATIC && LA2_0 <= WHILE)||LA2_0==85) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // R.g:93:8: s= statement
            	    {
            	    pushFollow(FOLLOW_statement_in_interactive197);
            	    s=statement();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) {stmts.add(s);}

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            if ( state.backtracking==0 ) { if(stmts.size() > 1) v = Sequence.create(stmts); else v = stmts.get(0); }
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
    // R.g:95:1: statement returns [ASTNode v] : (e= expr_or_assign n | '--EOF--' ( . )* EOF );
    public final ASTNode statement() throws RecognitionException {
        ASTNode v = null;

        int statement_StartIndex = input.index();

        ASTNode e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return v; }

            // R.g:96:2: (e= expr_or_assign n | '--EOF--' ( . )* EOF )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==BREAK||(LA4_0 >= COMPLEX && LA4_0 <= DD)||LA4_0==DOUBLE||LA4_0==FALSE||(LA4_0 >= FOR && LA4_0 <= FUNCTION)||LA4_0==ID||LA4_0==IF||LA4_0==INTEGER||LA4_0==LBRACE||LA4_0==LPAR||LA4_0==MINUS||LA4_0==NA||(LA4_0 >= NEXT && LA4_0 <= NOT)||LA4_0==NULL||LA4_0==PLUS||LA4_0==REPEAT||LA4_0==STRING||(LA4_0 >= TILDE && LA4_0 <= TRUE)||(LA4_0 >= VARIATIC && LA4_0 <= WHILE)) ) {
                alt4=1;
            }
            else if ( (LA4_0==85) ) {
                alt4=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 4, 0, input);

                throw nvae;

            }
            switch (alt4) {
                case 1 :
                    // R.g:96:4: e= expr_or_assign n
                    {
                    pushFollow(FOLLOW_expr_or_assign_in_statement217);
                    e=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n_in_statement219);
                    n();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = e;}

                    }
                    break;
                case 2 :
                    // R.g:97:4: '--EOF--' ( . )* EOF
                    {
                    match(input,85,FOLLOW_85_in_statement226); if (state.failed) return v;

                    // R.g:97:14: ( . )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0 >= AND && LA3_0 <= 85)) ) {
                            alt3=1;
                        }
                        else if ( (LA3_0==EOF) ) {
                            alt3=2;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // R.g:97:14: .
                    	    {
                    	    matchAny(input); if (state.failed) return v;

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    match(input,EOF,FOLLOW_EOF_in_statement231); if (state.failed) return v;

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
    // R.g:100:1: n_ : ( NEWLINE | COMMENT )* ;
    public final void n_() throws RecognitionException {
        int n__StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return ; }

            // R.g:100:4: ( ( NEWLINE | COMMENT )* )
            // R.g:100:6: ( NEWLINE | COMMENT )*
            {
            // R.g:100:6: ( NEWLINE | COMMENT )*
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
            	    break loop5;
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
    // R.g:101:1: n : ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLON n_ );
    public final void n() throws RecognitionException {
        int n_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }

            // R.g:101:3: ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLON n_ )
            int alt7=3;
            switch ( input.LA(1) ) {
            case COMMENT:
            case NEWLINE:
                {
                alt7=1;
                }
                break;
            case EOF:
                {
                alt7=2;
                }
                break;
            case SEMICOLON:
                {
                alt7=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }

            switch (alt7) {
                case 1 :
                    // R.g:101:5: ( NEWLINE | COMMENT )+
                    {
                    // R.g:101:5: ( NEWLINE | COMMENT )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( (LA6_0==COMMENT||LA6_0==NEWLINE) ) {
                            alt6=1;
                        }


                        switch (alt6) {
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
                    	    if ( cnt6 >= 1 ) break loop6;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(6, input);
                                throw eee;
                        }
                        cnt6++;
                    } while (true);


                    }
                    break;
                case 2 :
                    // R.g:101:28: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_n266); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // R.g:101:34: SEMICOLON n_
                    {
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_n270); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_n272);
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
    // R.g:103:1: expr_or_assign returns [ASTNode v] : a= alter_assign ;
    public final ASTNode expr_or_assign() throws RecognitionException {
        ASTNode v = null;

        int expr_or_assign_StartIndex = input.index();

        ASTNode a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return v; }

            // R.g:104:2: (a= alter_assign )
            // R.g:104:4: a= alter_assign
            {
            pushFollow(FOLLOW_alter_assign_in_expr_or_assign287);
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
    // R.g:106:1: expr returns [ASTNode v] : a= assign ;
    public final ASTNode expr() throws RecognitionException {
        ASTNode v = null;

        int expr_StartIndex = input.index();

        ASTNode a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return v; }

            // R.g:107:2: (a= assign )
            // R.g:107:4: a= assign
            {
            pushFollow(FOLLOW_assign_in_expr305);
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
    // R.g:109:1: expr_wo_assign returns [ASTNode v] : (w= while_expr |i= if_expr |f= for_expr |r= repeat_expr |fun= function | NEXT | BREAK );
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

            // R.g:110:2: (w= while_expr |i= if_expr |f= for_expr |r= repeat_expr |fun= function | NEXT | BREAK )
            int alt8=7;
            switch ( input.LA(1) ) {
            case WHILE:
                {
                alt8=1;
                }
                break;
            case IF:
                {
                alt8=2;
                }
                break;
            case FOR:
                {
                alt8=3;
                }
                break;
            case REPEAT:
                {
                alt8=4;
                }
                break;
            case FUNCTION:
                {
                alt8=5;
                }
                break;
            case NEXT:
                {
                alt8=6;
                }
                break;
            case BREAK:
                {
                alt8=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 8, 0, input);

                throw nvae;

            }

            switch (alt8) {
                case 1 :
                    // R.g:110:4: w= while_expr
                    {
                    pushFollow(FOLLOW_while_expr_in_expr_wo_assign324);
                    w=while_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = w; }

                    }
                    break;
                case 2 :
                    // R.g:111:4: i= if_expr
                    {
                    pushFollow(FOLLOW_if_expr_in_expr_wo_assign333);
                    i=if_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = i; }

                    }
                    break;
                case 3 :
                    // R.g:112:4: f= for_expr
                    {
                    pushFollow(FOLLOW_for_expr_in_expr_wo_assign342);
                    f=for_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = f; }

                    }
                    break;
                case 4 :
                    // R.g:113:4: r= repeat_expr
                    {
                    pushFollow(FOLLOW_repeat_expr_in_expr_wo_assign351);
                    r=repeat_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = r; }

                    }
                    break;
                case 5 :
                    // R.g:114:4: fun= function
                    {
                    pushFollow(FOLLOW_function_in_expr_wo_assign360);
                    fun=function();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = fun; }

                    }
                    break;
                case 6 :
                    // R.g:115:4: NEXT
                    {
                    match(input,NEXT,FOLLOW_NEXT_in_expr_wo_assign367); if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = Next.create(); }

                    }
                    break;
                case 7 :
                    // R.g:116:4: BREAK
                    {
                    match(input,BREAK,FOLLOW_BREAK_in_expr_wo_assign377); if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = Break.create(); }

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
    // R.g:118:1: sequence returns [ASTNode v] : LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE ;
    public final ASTNode sequence() throws RecognitionException {
        ASTNode v = null;

        int sequence_StartIndex = input.index();

        ASTNode e =null;


        ArrayList<ASTNode> stmts = new ArrayList<ASTNode>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return v; }

            // R.g:121:2: ( LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE )
            // R.g:121:4: LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE
            {
            match(input,LBRACE,FOLLOW_LBRACE_in_sequence411); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_sequence413);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:121:14: (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( (LA11_0==BREAK||(LA11_0 >= COMPLEX && LA11_0 <= DD)||LA11_0==DOUBLE||LA11_0==FALSE||(LA11_0 >= FOR && LA11_0 <= FUNCTION)||LA11_0==ID||LA11_0==IF||LA11_0==INTEGER||LA11_0==LBRACE||LA11_0==LPAR||LA11_0==MINUS||LA11_0==NA||(LA11_0 >= NEXT && LA11_0 <= NOT)||LA11_0==NULL||LA11_0==PLUS||LA11_0==REPEAT||LA11_0==STRING||(LA11_0 >= TILDE && LA11_0 <= TRUE)||(LA11_0 >= VARIATIC && LA11_0 <= WHILE)) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // R.g:121:15: e= expr_or_assign ( n e= expr_or_assign )* ( n )?
                    {
                    pushFollow(FOLLOW_expr_or_assign_in_sequence418);
                    e=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { stmts.add(e); }

                    // R.g:121:50: ( n e= expr_or_assign )*
                    loop9:
                    do {
                        int alt9=2;
                        alt9 = dfa9.predict(input);
                        switch (alt9) {
                    	case 1 :
                    	    // R.g:121:51: n e= expr_or_assign
                    	    {
                    	    pushFollow(FOLLOW_n_in_sequence423);
                    	    n();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    pushFollow(FOLLOW_expr_or_assign_in_sequence427);
                    	    e=expr_or_assign();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    if ( state.backtracking==0 ) { stmts.add(e); }

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);


                    // R.g:121:90: ( n )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==EOF||LA10_0==COMMENT||LA10_0==NEWLINE||LA10_0==SEMICOLON) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // R.g:121:90: n
                            {
                            pushFollow(FOLLOW_n_in_sequence433);
                            n();

                            state._fsp--;
                            if (state.failed) return v;

                            }
                            break;

                    }


                    }
                    break;

            }


            match(input,RBRACE,FOLLOW_RBRACE_in_sequence439); if (state.failed) return v;

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
    // R.g:123:1: assign returns [ASTNode v] : l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |) ;
    public final ASTNode assign() throws RecognitionException {
        ASTNode v = null;

        int assign_StartIndex = input.index();

        Token a=null;
        ASTNode l =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return v; }

            // R.g:124:2: (l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |) )
            // R.g:124:4: l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |)
            {
            pushFollow(FOLLOW_tilde_expr_in_assign457);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:125:3: ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |)
            int alt12=5;
            switch ( input.LA(1) ) {
            case ARROW:
                {
                alt12=1;
                }
                break;
            case SUPER_ARROW:
                {
                alt12=2;
                }
                break;
            case RIGHT_ARROW:
                {
                alt12=3;
                }
                break;
            case SUPER_RIGHT_ARROW:
                {
                alt12=4;
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
            case 85:
                {
                alt12=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 12, 0, input);

                throw nvae;

            }

            switch (alt12) {
                case 1 :
                    // R.g:125:5: ARROW n_ r= expr
                    {
                    match(input,ARROW,FOLLOW_ARROW_in_assign464); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign466);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign470);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(false, l, r);}

                    }
                    break;
                case 2 :
                    // R.g:126:5: SUPER_ARROW n_ r= expr
                    {
                    match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_assign478); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign480);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign484);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(true, l, r);}

                    }
                    break;
                case 3 :
                    // R.g:127:5: a= RIGHT_ARROW n_ r= expr
                    {
                    a=(Token)match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_assign494); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign496);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign500);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(false, r, l);}

                    }
                    break;
                case 4 :
                    // R.g:128:5: a= SUPER_RIGHT_ARROW n_ r= expr
                    {
                    a=(Token)match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_assign510); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign512);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign516);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(true, r, l);}

                    }
                    break;
                case 5 :
                    // R.g:129:5: 
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
    // R.g:132:1: alter_assign returns [ASTNode v] : l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |) ;
    public final ASTNode alter_assign() throws RecognitionException {
        ASTNode v = null;

        int alter_assign_StartIndex = input.index();

        Token a=null;
        ASTNode l =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return v; }

            // R.g:133:2: (l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |) )
            // R.g:133:4: l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |)
            {
            pushFollow(FOLLOW_tilde_expr_in_alter_assign544);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:134:3: ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |)
            int alt13=6;
            switch ( input.LA(1) ) {
            case ARROW:
                {
                int LA13_1 = input.LA(2);

                if ( (synpred1_R()) ) {
                    alt13=1;
                }
                else if ( (true) ) {
                    alt13=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    throw nvae;

                }
                }
                break;
            case SUPER_ARROW:
                {
                int LA13_2 = input.LA(2);

                if ( (synpred2_R()) ) {
                    alt13=2;
                }
                else if ( (true) ) {
                    alt13=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 2, input);

                    throw nvae;

                }
                }
                break;
            case RIGHT_ARROW:
                {
                int LA13_3 = input.LA(2);

                if ( (synpred3_R()) ) {
                    alt13=3;
                }
                else if ( (true) ) {
                    alt13=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 3, input);

                    throw nvae;

                }
                }
                break;
            case SUPER_RIGHT_ARROW:
                {
                int LA13_4 = input.LA(2);

                if ( (synpred4_R()) ) {
                    alt13=4;
                }
                else if ( (true) ) {
                    alt13=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 4, input);

                    throw nvae;

                }
                }
                break;
            case ASSIGN:
                {
                int LA13_5 = input.LA(2);

                if ( (synpred5_R()) ) {
                    alt13=5;
                }
                else if ( (true) ) {
                    alt13=6;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 5, input);

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
            case COLON:
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
            case MAT_MULT:
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
            case SEMICOLON:
            case TILDE:
                {
                alt13=6;
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
                    // R.g:134:5: ( ARROW )=> ARROW n_ r= expr_or_assign
                    {
                    match(input,ARROW,FOLLOW_ARROW_in_alter_assign555); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign557);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign561);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(false, l, r);}

                    }
                    break;
                case 2 :
                    // R.g:135:5: ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign
                    {
                    match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_alter_assign573); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign575);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign579);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = AssignVariable.create(true, l, r);}

                    }
                    break;
                case 3 :
                    // R.g:136:5: ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign
                    {
                    a=(Token)match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_alter_assign593); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign595);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign599);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AssignVariable.create(false, r, l);}

                    }
                    break;
                case 4 :
                    // R.g:137:5: ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign
                    {
                    a=(Token)match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_alter_assign613); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign615);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign619);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = AssignVariable.create(true, r, l);}

                    }
                    break;
                case 5 :
                    // R.g:138:5: ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign
                    {
                    a=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_alter_assign633); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign635);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign639);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = AssignVariable.create(false, l, r);}

                    }
                    break;
                case 6 :
                    // R.g:139:5: 
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
    // R.g:142:1: if_expr returns [ASTNode v] : IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |) ;
    public final ASTNode if_expr() throws RecognitionException {
        ASTNode v = null;

        int if_expr_StartIndex = input.index();

        ASTNode cond =null;

        ASTNode t =null;

        ASTNode f =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return v; }

            // R.g:143:2: ( IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |) )
            // R.g:144:2: IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |)
            {
            match(input,IF,FOLLOW_IF_in_if_expr666); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr668);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_if_expr670); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr672);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_if_expr676);
            cond=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr678);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,RPAR,FOLLOW_RPAR_in_if_expr680); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr682);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_if_expr686);
            t=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            // R.g:145:2: ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |)
            int alt14=2;
            switch ( input.LA(1) ) {
            case COMMENT:
            case NEWLINE:
                {
                int LA14_1 = input.LA(2);

                if ( (synpred6_R()) ) {
                    alt14=1;
                }
                else if ( (true) ) {
                    alt14=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 1, input);

                    throw nvae;

                }
                }
                break;
            case ELSE:
                {
                int LA14_2 = input.LA(2);

                if ( (synpred6_R()) ) {
                    alt14=1;
                }
                else if ( (true) ) {
                    alt14=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 14, 2, input);

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
            case COLON:
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
            case MAT_MULT:
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
            case SEMICOLON:
            case SUPER_ARROW:
            case SUPER_RIGHT_ARROW:
            case TILDE:
                {
                alt14=2;
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
                    // R.g:145:3: ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    {
                    // R.g:145:14: ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    // R.g:145:58: n_ ELSE n_ f= expr_or_assign
                    {
                    pushFollow(FOLLOW_n__in_if_expr712);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,ELSE,FOLLOW_ELSE_in_if_expr714); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_if_expr716);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_if_expr720);
                    f=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = If.create(cond, t, f);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:146:7: 
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
    // R.g:149:1: while_expr returns [ASTNode v] : WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final ASTNode while_expr() throws RecognitionException {
        ASTNode v = null;

        int while_expr_StartIndex = input.index();

        ASTNode c =null;

        ASTNode body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return v; }

            // R.g:150:2: ( WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:150:4: WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign
            {
            match(input,WHILE,FOLLOW_WHILE_in_while_expr748); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr750);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_while_expr752); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr754);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_while_expr758);
            c=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr760);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,RPAR,FOLLOW_RPAR_in_while_expr762); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr764);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_while_expr768);
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
    // R.g:152:1: for_expr returns [ASTNode v] : FOR n_ LPAR n_ i= ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final ASTNode for_expr() throws RecognitionException {
        ASTNode v = null;

        int for_expr_StartIndex = input.index();

        Token i=null;
        ASTNode in =null;

        ASTNode body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return v; }

            // R.g:153:2: ( FOR n_ LPAR n_ i= ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:153:4: FOR n_ LPAR n_ i= ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign
            {
            match(input,FOR,FOLLOW_FOR_in_for_expr784); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr786);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_for_expr788); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr790);
            n_();

            state._fsp--;
            if (state.failed) return v;

            i=(Token)match(input,ID,FOLLOW_ID_in_for_expr794); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr796);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,IN,FOLLOW_IN_in_for_expr798); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr800);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_for_expr804);
            in=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr806);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,RPAR,FOLLOW_RPAR_in_for_expr808); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr810);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_for_expr814);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = Loop.create((i!=null?i.getText():null), in, body); }

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
    // R.g:155:1: repeat_expr returns [ASTNode v] : REPEAT n_ body= expr_or_assign ;
    public final ASTNode repeat_expr() throws RecognitionException {
        ASTNode v = null;

        int repeat_expr_StartIndex = input.index();

        ASTNode body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return v; }

            // R.g:156:2: ( REPEAT n_ body= expr_or_assign )
            // R.g:156:4: REPEAT n_ body= expr_or_assign
            {
            match(input,REPEAT,FOLLOW_REPEAT_in_repeat_expr831); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_repeat_expr833);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_repeat_expr837);
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
    // R.g:158:1: function returns [ASTNode v] : FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign ;
    public final ASTNode function() throws RecognitionException {
        ASTNode v = null;

        int function_StartIndex = input.index();

        ASTNode body =null;


         ArgumentList l = new ArgumentList.Default(); 
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return v; }

            // R.g:160:2: ( FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign )
            // R.g:160:4: FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function858); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_function860);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_function862); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_function865);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:160:25: ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==DD||LA16_0==ID||LA16_0==VARIATIC) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // R.g:160:26: par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_
                    {
                    pushFollow(FOLLOW_par_decl_in_function868);
                    par_decl(l);

                    state._fsp--;
                    if (state.failed) return v;

                    // R.g:160:38: ( n_ COMMA n_ par_decl[l] )*
                    loop15:
                    do {
                        int alt15=2;
                        alt15 = dfa15.predict(input);
                        switch (alt15) {
                    	case 1 :
                    	    // R.g:160:39: n_ COMMA n_ par_decl[l]
                    	    {
                    	    pushFollow(FOLLOW_n__in_function872);
                    	    n_();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    match(input,COMMA,FOLLOW_COMMA_in_function874); if (state.failed) return v;

                    	    pushFollow(FOLLOW_n__in_function876);
                    	    n_();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    pushFollow(FOLLOW_par_decl_in_function878);
                    	    par_decl(l);

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    pushFollow(FOLLOW_n__in_function883);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }


            match(input,RPAR,FOLLOW_RPAR_in_function887); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_function889);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_function893);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = Function.create(l, body); ArgumentList.Default.updateParent(v, l); }

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
    // R.g:162:1: par_decl[ArgumentList l] : (i= ID |i= ID n_ ASSIGN n_ e= expr |v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr );
    public final void par_decl(ArgumentList l) throws RecognitionException {
        int par_decl_StartIndex = input.index();

        Token i=null;
        Token v=null;
        ASTNode e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }

            // R.g:163:2: (i= ID |i= ID n_ ASSIGN n_ e= expr |v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr )
            int alt17=6;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // R.g:163:4: i= ID
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_par_decl910); if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add((i!=null?i.getText():null), null); }

                    }
                    break;
                case 2 :
                    // R.g:164:4: i= ID n_ ASSIGN n_ e= expr
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_par_decl920); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl922);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl924); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl926);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_par_decl930);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add((i!=null?i.getText():null), e); }

                    }
                    break;
                case 3 :
                    // R.g:165:4: v= VARIATIC
                    {
                    v=(Token)match(input,VARIATIC,FOLLOW_VARIATIC_in_par_decl939); if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add((v!=null?v.getText():null), null); }

                    }
                    break;
                case 4 :
                    // R.g:170:5: VARIATIC n_ ASSIGN n_ expr
                    {
                    match(input,VARIATIC,FOLLOW_VARIATIC_in_par_decl989); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl991);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl993); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl995);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_par_decl997);
                    expr();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // R.g:171:5: DD
                    {
                    match(input,DD,FOLLOW_DD_in_par_decl1003); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // R.g:172:5: DD n_ ASSIGN n_ expr
                    {
                    match(input,DD,FOLLOW_DD_in_par_decl1009); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl1011);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl1013); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl1015);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_par_decl1017);
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
    // R.g:174:1: tilde_expr returns [ASTNode v] : l= or_expr ( ( ( TILDE )=> TILDE n_ r= or_expr ) )* ;
    public final ASTNode tilde_expr() throws RecognitionException {
        ASTNode v = null;

        int tilde_expr_StartIndex = input.index();

        ASTNode l =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return v; }

            // R.g:175:2: (l= or_expr ( ( ( TILDE )=> TILDE n_ r= or_expr ) )* )
            // R.g:175:4: l= or_expr ( ( ( TILDE )=> TILDE n_ r= or_expr ) )*
            {
            pushFollow(FOLLOW_or_expr_in_tilde_expr1033);
            l=or_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:176:2: ( ( ( TILDE )=> TILDE n_ r= or_expr ) )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==TILDE) ) {
                    int LA18_2 = input.LA(2);

                    if ( (synpred7_R()) ) {
                        alt18=1;
                    }


                }


                switch (alt18) {
            	case 1 :
            	    // R.g:176:4: ( ( TILDE )=> TILDE n_ r= or_expr )
            	    {
            	    // R.g:176:4: ( ( TILDE )=> TILDE n_ r= or_expr )
            	    // R.g:176:5: ( TILDE )=> TILDE n_ r= or_expr
            	    {
            	    match(input,TILDE,FOLLOW_TILDE_in_tilde_expr1045); if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_tilde_expr1047);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_or_expr_in_tilde_expr1051);
            	    r=or_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) {v = BinaryOperation.create(BinaryOperator.ADD, v, r);}

            	    }


            	    }
            	    break;

            	default :
            	    break loop18;
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
    // R.g:178:1: or_expr returns [ASTNode v] : l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )* ;
    public final ASTNode or_expr() throws RecognitionException {
        ASTNode v = null;

        int or_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return v; }

            // R.g:179:2: (l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )* )
            // R.g:179:4: l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )*
            {
            pushFollow(FOLLOW_and_expr_in_or_expr1073);
            l=and_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:180:2: ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==OR) ) {
                    int LA19_2 = input.LA(2);

                    if ( (synpred8_R()) ) {
                        alt19=1;
                    }


                }
                else if ( (LA19_0==BITWISEOR) ) {
                    int LA19_3 = input.LA(2);

                    if ( (synpred8_R()) ) {
                        alt19=1;
                    }


                }


                switch (alt19) {
            	case 1 :
            	    // R.g:180:3: ( ( or_operator )=>op= or_operator n_ r= and_expr )
            	    {
            	    // R.g:180:3: ( ( or_operator )=>op= or_operator n_ r= and_expr )
            	    // R.g:180:4: ( or_operator )=>op= or_operator n_ r= and_expr
            	    {
            	    pushFollow(FOLLOW_or_operator_in_or_expr1086);
            	    op=or_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_or_expr1088);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_and_expr_in_or_expr1092);
            	    r=and_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) {v = BinaryOperation.create(op, v, r);}

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
            if ( state.backtracking>0 ) { memoize(input, 19, or_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "or_expr"



    // $ANTLR start "and_expr"
    // R.g:182:1: and_expr returns [ASTNode v] : l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )* ;
    public final ASTNode and_expr() throws RecognitionException {
        ASTNode v = null;

        int and_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return v; }

            // R.g:183:2: (l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )* )
            // R.g:183:4: l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )*
            {
            pushFollow(FOLLOW_not_expr_in_and_expr1114);
            l=not_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:184:5: ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==AND) ) {
                    int LA20_2 = input.LA(2);

                    if ( (synpred9_R()) ) {
                        alt20=1;
                    }


                }
                else if ( (LA20_0==BITWISEAND) ) {
                    int LA20_3 = input.LA(2);

                    if ( (synpred9_R()) ) {
                        alt20=1;
                    }


                }


                switch (alt20) {
            	case 1 :
            	    // R.g:184:6: ( ( and_operator )=>op= and_operator n_ r= not_expr )
            	    {
            	    // R.g:184:6: ( ( and_operator )=>op= and_operator n_ r= not_expr )
            	    // R.g:184:7: ( and_operator )=>op= and_operator n_ r= not_expr
            	    {
            	    pushFollow(FOLLOW_and_operator_in_and_expr1130);
            	    op=and_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_and_expr1132);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_not_expr_in_and_expr1136);
            	    r=not_expr();

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
            if ( state.backtracking>0 ) { memoize(input, 20, and_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "and_expr"



    // $ANTLR start "not_expr"
    // R.g:186:1: not_expr returns [ASTNode v] : ( NOT n_ l= not_expr |b= comp_expr );
    public final ASTNode not_expr() throws RecognitionException {
        ASTNode v = null;

        int not_expr_StartIndex = input.index();

        ASTNode l =null;

        ASTNode b =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return v; }

            // R.g:187:2: ( NOT n_ l= not_expr |b= comp_expr )
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==NOT) ) {
                alt21=1;
            }
            else if ( (LA21_0==BREAK||(LA21_0 >= COMPLEX && LA21_0 <= DD)||LA21_0==DOUBLE||LA21_0==FALSE||(LA21_0 >= FOR && LA21_0 <= FUNCTION)||LA21_0==ID||LA21_0==IF||LA21_0==INTEGER||LA21_0==LBRACE||LA21_0==LPAR||LA21_0==MINUS||LA21_0==NA||LA21_0==NEXT||LA21_0==NULL||LA21_0==PLUS||LA21_0==REPEAT||LA21_0==STRING||(LA21_0 >= TILDE && LA21_0 <= TRUE)||(LA21_0 >= VARIATIC && LA21_0 <= WHILE)) ) {
                alt21=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 21, 0, input);

                throw nvae;

            }
            switch (alt21) {
                case 1 :
                    // R.g:187:4: NOT n_ l= not_expr
                    {
                    match(input,NOT,FOLLOW_NOT_in_not_expr1156); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_not_expr1158);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_not_expr_in_not_expr1162);
                    l=not_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.NOT, l);}

                    }
                    break;
                case 2 :
                    // R.g:188:4: b= comp_expr
                    {
                    pushFollow(FOLLOW_comp_expr_in_not_expr1171);
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
    // R.g:190:1: comp_expr returns [ASTNode v] : l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )* ;
    public final ASTNode comp_expr() throws RecognitionException {
        ASTNode v = null;

        int comp_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return v; }

            // R.g:191:2: (l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )* )
            // R.g:191:4: l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )*
            {
            pushFollow(FOLLOW_add_expr_in_comp_expr1189);
            l=add_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:192:5: ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )*
            loop22:
            do {
                int alt22=2;
                switch ( input.LA(1) ) {
                case GT:
                    {
                    int LA22_2 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt22=1;
                    }


                    }
                    break;
                case GE:
                    {
                    int LA22_3 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt22=1;
                    }


                    }
                    break;
                case LT:
                    {
                    int LA22_4 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt22=1;
                    }


                    }
                    break;
                case LE:
                    {
                    int LA22_5 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt22=1;
                    }


                    }
                    break;
                case EQ:
                    {
                    int LA22_6 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt22=1;
                    }


                    }
                    break;
                case NE:
                    {
                    int LA22_7 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt22=1;
                    }


                    }
                    break;

                }

                switch (alt22) {
            	case 1 :
            	    // R.g:192:6: ( ( comp_operator )=>op= comp_operator n_ r= add_expr )
            	    {
            	    // R.g:192:6: ( ( comp_operator )=>op= comp_operator n_ r= add_expr )
            	    // R.g:192:7: ( comp_operator )=>op= comp_operator n_ r= add_expr
            	    {
            	    pushFollow(FOLLOW_comp_operator_in_comp_expr1205);
            	    op=comp_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_comp_expr1207);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_add_expr_in_comp_expr1211);
            	    r=add_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) { v = BinaryOperation.create(op, v, r);}

            	    }


            	    }
            	    break;

            	default :
            	    break loop22;
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
    // R.g:194:1: add_expr returns [ASTNode v] : l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )* ;
    public final ASTNode add_expr() throws RecognitionException {
        ASTNode v = null;

        int add_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return v; }

            // R.g:195:2: (l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )* )
            // R.g:195:4: l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            {
            pushFollow(FOLLOW_mult_expr_in_add_expr1236);
            l=mult_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:196:3: ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==PLUS) ) {
                    int LA23_2 = input.LA(2);

                    if ( (synpred11_R()) ) {
                        alt23=1;
                    }


                }
                else if ( (LA23_0==MINUS) ) {
                    int LA23_3 = input.LA(2);

                    if ( (synpred11_R()) ) {
                        alt23=1;
                    }


                }


                switch (alt23) {
            	case 1 :
            	    // R.g:196:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    {
            	    // R.g:196:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    // R.g:196:5: ( add_operator )=>op= add_operator n_ r= mult_expr
            	    {
            	    pushFollow(FOLLOW_add_operator_in_add_expr1250);
            	    op=add_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_add_expr1252);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_mult_expr_in_add_expr1256);
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
            if ( state.backtracking>0 ) { memoize(input, 23, add_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "add_expr"



    // $ANTLR start "mult_expr"
    // R.g:198:1: mult_expr returns [ASTNode v] : l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )* ;
    public final ASTNode mult_expr() throws RecognitionException {
        ASTNode v = null;

        int mult_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return v; }

            // R.g:199:2: (l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )* )
            // R.g:199:4: l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            {
            pushFollow(FOLLOW_operator_expr_in_mult_expr1278);
            l=operator_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:200:2: ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            loop24:
            do {
                int alt24=2;
                switch ( input.LA(1) ) {
                case MULT:
                    {
                    int LA24_2 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt24=1;
                    }


                    }
                    break;
                case MAT_MULT:
                    {
                    int LA24_3 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt24=1;
                    }


                    }
                    break;
                case DIV:
                    {
                    int LA24_4 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt24=1;
                    }


                    }
                    break;
                case MOD:
                    {
                    int LA24_5 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt24=1;
                    }


                    }
                    break;

                }

                switch (alt24) {
            	case 1 :
            	    // R.g:200:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    {
            	    // R.g:200:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    // R.g:200:4: ( mult_operator )=>op= mult_operator n_ r= operator_expr
            	    {
            	    pushFollow(FOLLOW_mult_operator_in_mult_expr1291);
            	    op=mult_operator();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_mult_expr1293);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_operator_expr_in_mult_expr1297);
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
            if ( state.backtracking>0 ) { memoize(input, 24, mult_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "mult_expr"



    // $ANTLR start "operator_expr"
    // R.g:202:1: operator_expr returns [ASTNode v] : l= colon_expr ( ( ( OP )=>op= OP n_ r= colon_expr ) )* ;
    public final ASTNode operator_expr() throws RecognitionException {
        ASTNode v = null;

        int operator_expr_StartIndex = input.index();

        Token op=null;
        ASTNode l =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return v; }

            // R.g:203:2: (l= colon_expr ( ( ( OP )=>op= OP n_ r= colon_expr ) )* )
            // R.g:203:4: l= colon_expr ( ( ( OP )=>op= OP n_ r= colon_expr ) )*
            {
            pushFollow(FOLLOW_colon_expr_in_operator_expr1319);
            l=colon_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:204:2: ( ( ( OP )=>op= OP n_ r= colon_expr ) )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==OP) ) {
                    int LA25_2 = input.LA(2);

                    if ( (synpred13_R()) ) {
                        alt25=1;
                    }


                }


                switch (alt25) {
            	case 1 :
            	    // R.g:204:3: ( ( OP )=>op= OP n_ r= colon_expr )
            	    {
            	    // R.g:204:3: ( ( OP )=>op= OP n_ r= colon_expr )
            	    // R.g:204:4: ( OP )=>op= OP n_ r= colon_expr
            	    {
            	    op=(Token)match(input,OP,FOLLOW_OP_in_operator_expr1332); if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_operator_expr1334);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_colon_expr_in_operator_expr1338);
            	    r=colon_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) { v = null; }

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
            if ( state.backtracking>0 ) { memoize(input, 25, operator_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "operator_expr"



    // $ANTLR start "colon_expr"
    // R.g:206:1: colon_expr returns [ASTNode v] : l= power_expr ( ( ( COLON )=>op= COLON n_ r= power_expr ) )* ;
    public final ASTNode colon_expr() throws RecognitionException {
        ASTNode v = null;

        int colon_expr_StartIndex = input.index();

        Token op=null;
        ASTNode l =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return v; }

            // R.g:207:2: (l= power_expr ( ( ( COLON )=>op= COLON n_ r= power_expr ) )* )
            // R.g:207:4: l= power_expr ( ( ( COLON )=>op= COLON n_ r= power_expr ) )*
            {
            pushFollow(FOLLOW_power_expr_in_colon_expr1365);
            l=power_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = l ;}

            // R.g:208:2: ( ( ( COLON )=>op= COLON n_ r= power_expr ) )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==COLON) ) {
                    int LA26_2 = input.LA(2);

                    if ( (synpred14_R()) ) {
                        alt26=1;
                    }


                }


                switch (alt26) {
            	case 1 :
            	    // R.g:208:3: ( ( COLON )=>op= COLON n_ r= power_expr )
            	    {
            	    // R.g:208:3: ( ( COLON )=>op= COLON n_ r= power_expr )
            	    // R.g:208:4: ( COLON )=>op= COLON n_ r= power_expr
            	    {
            	    op=(Token)match(input,COLON,FOLLOW_COLON_in_colon_expr1378); if (state.failed) return v;

            	    pushFollow(FOLLOW_n__in_colon_expr1380);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    pushFollow(FOLLOW_power_expr_in_colon_expr1384);
            	    r=power_expr();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    if ( state.backtracking==0 ) { v = BinaryOperation.create(BinaryOperator.COLON, v, r);}

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
            if ( state.backtracking>0 ) { memoize(input, 26, colon_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "colon_expr"



    // $ANTLR start "power_expr"
    // R.g:210:1: power_expr returns [ASTNode v] : l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |) ;
    public final ASTNode power_expr() throws RecognitionException {
        ASTNode v = null;

        int power_expr_StartIndex = input.index();

        ASTNode l =null;

        BinaryOperator op =null;

        ASTNode r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return v; }

            // R.g:211:2: (l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |) )
            // R.g:211:4: l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |)
            {
            pushFollow(FOLLOW_unary_expression_in_power_expr1406);
            l=unary_expression();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) {v =l;}

            // R.g:212:5: ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |)
            int alt27=2;
            int LA27_0 = input.LA(1);

            if ( (LA27_0==CARRET) ) {
                int LA27_1 = input.LA(2);

                if ( (synpred15_R()) ) {
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
            else if ( (LA27_0==EOF||(LA27_0 >= AND && LA27_0 <= BITWISEOR)||(LA27_0 >= COLON && LA27_0 <= COMMENT)||LA27_0==DIV||(LA27_0 >= ELSE && LA27_0 <= EQ)||LA27_0==FIELD||(LA27_0 >= GE && LA27_0 <= GT)||LA27_0==LBB||(LA27_0 >= LBRAKET && LA27_0 <= LE)||(LA27_0 >= LPAR && LA27_0 <= MINUS)||(LA27_0 >= MOD && LA27_0 <= MULT)||(LA27_0 >= NE && LA27_0 <= NEWLINE)||LA27_0==OP||LA27_0==OR||(LA27_0 >= PLUS && LA27_0 <= RBRAKET)||(LA27_0 >= RIGHT_ARROW && LA27_0 <= SEMICOLON)||(LA27_0 >= SUPER_ARROW && LA27_0 <= TILDE)) ) {
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
                    // R.g:212:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    {
                    // R.g:212:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    // R.g:212:7: ( power_operator )=>op= power_operator n_ r= power_expr
                    {
                    pushFollow(FOLLOW_power_operator_in_power_expr1422);
                    op=power_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_power_expr1424);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_power_expr_in_power_expr1428);
                    r=power_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = BinaryOperation.create(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:213:6: 
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
    // R.g:215:1: unary_expression returns [ASTNode v] : ( PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression |b= basic_expr );
    public final ASTNode unary_expression() throws RecognitionException {
        ASTNode v = null;

        int unary_expression_StartIndex = input.index();

        ASTNode l =null;

        ASTNode b =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return v; }

            // R.g:216:2: ( PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression |b= basic_expr )
            int alt28=4;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt28=1;
                }
                break;
            case MINUS:
                {
                alt28=2;
                }
                break;
            case TILDE:
                {
                alt28=3;
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
                alt28=4;
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
                    // R.g:216:4: PLUS n_ l= unary_expression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unary_expression1457); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1459);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1463);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.PLUS, l);}

                    }
                    break;
                case 2 :
                    // R.g:217:4: MINUS n_ l= unary_expression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unary_expression1470); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1472);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1476);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.MINUS, l);}

                    }
                    break;
                case 3 :
                    // R.g:218:4: TILDE n_ l= unary_expression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unary_expression1483); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1485);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1489);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = UnaryOperation.create(UnaryOperator.MODEL, l);}

                    }
                    break;
                case 4 :
                    // R.g:219:4: b= basic_expr
                    {
                    pushFollow(FOLLOW_basic_expr_in_unary_expression1498);
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
    // R.g:221:1: basic_expr returns [ASTNode v] : lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) ;
    public final ASTNode basic_expr() throws RecognitionException {
        ASTNode v = null;

        int basic_expr_StartIndex = input.index();

        ASTNode lhs =null;

        ASTNode subset =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return v; }

            // R.g:222:2: (lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) )
            // R.g:222:4: lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            {
            pushFollow(FOLLOW_simple_expr_in_basic_expr1516);
            lhs=simple_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = lhs; }

            // R.g:223:2: ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            int alt30=2;
            int LA30_0 = input.LA(1);

            if ( (LA30_0==FIELD) ) {
                int LA30_1 = input.LA(2);

                if ( (true) ) {
                    alt30=1;
                }
                else if ( (synpred17_R()) ) {
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
                else if ( (synpred17_R()) ) {
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
                else if ( (synpred17_R()) ) {
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
                else if ( (synpred17_R()) ) {
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
                else if ( (synpred17_R()) ) {
                    alt30=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 30, 5, input);

                    throw nvae;

                }
            }
            else if ( (LA30_0==CARRET) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==COLON) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==OP) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==MULT) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==MAT_MULT) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==DIV) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==MOD) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==PLUS) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==MINUS) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==GT) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==GE) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==LT) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==LE) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==EQ) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==NE) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==AND) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==BITWISEAND) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==OR) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==BITWISEOR) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==TILDE) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==ARROW) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==SUPER_ARROW) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==RIGHT_ARROW) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==SUPER_RIGHT_ARROW) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==COMMENT||LA30_0==NEWLINE) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==COMMA) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==RPAR) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==RBRAKET) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==ASSIGN) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==EOF) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==SEMICOLON) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==RBRACE) && (synpred17_R())) {
                alt30=2;
            }
            else if ( (LA30_0==ELSE) && (synpred17_R())) {
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
                    // R.g:223:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    {
                    // R.g:223:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    int cnt29=0;
                    loop29:
                    do {
                        int alt29=2;
                        switch ( input.LA(1) ) {
                        case FIELD:
                            {
                            int LA29_2 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt29=1;
                            }


                            }
                            break;
                        case AT:
                            {
                            int LA29_3 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt29=1;
                            }


                            }
                            break;
                        case LBRAKET:
                            {
                            int LA29_4 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt29=1;
                            }


                            }
                            break;
                        case LBB:
                            {
                            int LA29_5 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt29=1;
                            }


                            }
                            break;
                        case LPAR:
                            {
                            int LA29_6 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt29=1;
                            }


                            }
                            break;

                        }

                        switch (alt29) {
                    	case 1 :
                    	    // R.g:223:4: ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v]
                    	    {
                    	    pushFollow(FOLLOW_expr_subset_in_basic_expr1537);
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
                    // R.g:223:76: ( n_ )=>
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
    // R.g:225:1: expr_subset[ASTNode i] returns [ASTNode v] : ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= args RBRAKET ) | ( LBB subscript= args RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) );
    public final ASTNode expr_subset(ASTNode i) throws RecognitionException {
        ASTNode v = null;

        int expr_subset_StartIndex = input.index();

        Token name =null;

        ArgumentList subset =null;

        ArgumentList subscript =null;

        ArgumentList a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return v; }

            // R.g:226:5: ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= args RBRAKET ) | ( LBB subscript= args RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) )
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
                    // R.g:226:7: ( FIELD n_ name= id )
                    {
                    // R.g:226:7: ( FIELD n_ name= id )
                    // R.g:226:8: FIELD n_ name= id
                    {
                    match(input,FIELD,FOLLOW_FIELD_in_expr_subset1570); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_expr_subset1572);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_expr_subset1576);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = FieldAccess.create(FieldOperator.FIELD, i, name.getText()); }

                    }
                    break;
                case 2 :
                    // R.g:227:7: ( AT n_ name= id )
                    {
                    // R.g:227:7: ( AT n_ name= id )
                    // R.g:227:8: AT n_ name= id
                    {
                    match(input,AT,FOLLOW_AT_in_expr_subset1589); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_expr_subset1591);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_expr_subset1595);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = FieldAccess.create(FieldOperator.AT, i, name.getText()); }

                    }
                    break;
                case 3 :
                    // R.g:228:7: ( LBRAKET subset= args RBRAKET )
                    {
                    // R.g:228:7: ( LBRAKET subset= args RBRAKET )
                    // R.g:228:8: LBRAKET subset= args RBRAKET
                    {
                    match(input,LBRAKET,FOLLOW_LBRAKET_in_expr_subset1609); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1613);
                    subset=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1615); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Call.create(CallOperator.SUBSET, i, subset); ArgumentList.Default.updateParent(v, subset); }

                    }
                    break;
                case 4 :
                    // R.g:229:7: ( LBB subscript= args RBRAKET RBRAKET )
                    {
                    // R.g:229:7: ( LBB subscript= args RBRAKET RBRAKET )
                    // R.g:229:8: LBB subscript= args RBRAKET RBRAKET
                    {
                    match(input,LBB,FOLLOW_LBB_in_expr_subset1627); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1631);
                    subscript=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1633); if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1635); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Call.create(CallOperator.SUBSCRIPT, i, subscript); ArgumentList.Default.updateParent(v, subscript); }

                    }
                    break;
                case 5 :
                    // R.g:231:7: ( LPAR a= args RPAR )
                    {
                    // R.g:231:7: ( LPAR a= args RPAR )
                    // R.g:231:8: LPAR a= args RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_expr_subset1652); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1656);
                    a=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RPAR,FOLLOW_RPAR_in_expr_subset1658); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Call.create(i, a);  ArgumentList.Default.updateParent(v, a); }

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
    // R.g:234:1: simple_expr returns [ASTNode v] : (i= id |b= bool | DD | NULL |num= number |cstr= conststring | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR |s= sequence |e= expr_wo_assign );
    public final ASTNode simple_expr() throws RecognitionException {
        ASTNode v = null;

        int simple_expr_StartIndex = input.index();

        Token i =null;

        ASTNode b =null;

        ASTNode num =null;

        ASTNode cstr =null;

        ASTNode ea =null;

        ASTNode s =null;

        ASTNode e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return v; }

            // R.g:235:2: (i= id |b= bool | DD | NULL |num= number |cstr= conststring | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR |s= sequence |e= expr_wo_assign )
            int alt32=11;
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
                case COLON:
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
                case MAT_MULT:
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
                case SEMICOLON:
                case SUPER_ARROW:
                case SUPER_RIGHT_ARROW:
                case TILDE:
                    {
                    alt32=1;
                    }
                    break;
                case NS_GET:
                    {
                    alt32=7;
                    }
                    break;
                case NS_GET_INT:
                    {
                    alt32=8;
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
                case COLON:
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
                case MAT_MULT:
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
                case SEMICOLON:
                case SUPER_ARROW:
                case SUPER_RIGHT_ARROW:
                case TILDE:
                    {
                    alt32=1;
                    }
                    break;
                case NS_GET:
                    {
                    alt32=7;
                    }
                    break;
                case NS_GET_INT:
                    {
                    alt32=8;
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
            case STRING:
                {
                alt32=6;
                }
                break;
            case LPAR:
                {
                alt32=9;
                }
                break;
            case LBRACE:
                {
                alt32=10;
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
                alt32=11;
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
                    // R.g:235:4: i= id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1687);
                    i=id();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = AccessVariable.create(i.getText()); }

                    }
                    break;
                case 2 :
                    // R.g:236:4: b= bool
                    {
                    pushFollow(FOLLOW_bool_in_simple_expr1696);
                    b=bool();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = b; }

                    }
                    break;
                case 3 :
                    // R.g:237:4: DD
                    {
                    match(input,DD,FOLLOW_DD_in_simple_expr1703); if (state.failed) return v;

                    }
                    break;
                case 4 :
                    // R.g:238:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_simple_expr1708); if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = Constant.getNull(); }

                    }
                    break;
                case 5 :
                    // R.g:239:4: num= number
                    {
                    pushFollow(FOLLOW_number_in_simple_expr1717);
                    num=number();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = num; }

                    }
                    break;
                case 6 :
                    // R.g:240:4: cstr= conststring
                    {
                    pushFollow(FOLLOW_conststring_in_simple_expr1726);
                    cstr=conststring();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = cstr; }

                    }
                    break;
                case 7 :
                    // R.g:241:4: id NS_GET n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1733);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,NS_GET,FOLLOW_NS_GET_in_simple_expr1735); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1737);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_simple_expr1739);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 8 :
                    // R.g:242:4: id NS_GET_INT n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1744);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,NS_GET_INT,FOLLOW_NS_GET_INT_in_simple_expr1746); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1748);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_simple_expr1750);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 9 :
                    // R.g:243:4: LPAR n_ ea= expr_or_assign n_ RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_simple_expr1755); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1757);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_simple_expr1763);
                    ea=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_simple_expr1765);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RPAR,FOLLOW_RPAR_in_simple_expr1767); if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = ea; }

                    }
                    break;
                case 10 :
                    // R.g:244:4: s= sequence
                    {
                    pushFollow(FOLLOW_sequence_in_simple_expr1778);
                    s=sequence();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = s;}

                    }
                    break;
                case 11 :
                    // R.g:245:4: e= expr_wo_assign
                    {
                    pushFollow(FOLLOW_expr_wo_assign_in_simple_expr1789);
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
    // R.g:247:1: number returns [ASTNode n] : (i= INTEGER |d= DOUBLE |c= COMPLEX );
    public final ASTNode number() throws RecognitionException {
        ASTNode n = null;

        int number_StartIndex = input.index();

        Token i=null;
        Token d=null;
        Token c=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return n; }

            // R.g:248:5: (i= INTEGER |d= DOUBLE |c= COMPLEX )
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
                    // R.g:248:7: i= INTEGER
                    {
                    i=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_number1810); if (state.failed) return n;

                    if ( state.backtracking==0 ) { n = Constant.createIntConstant((i!=null?i.getText():null)); }

                    }
                    break;
                case 2 :
                    // R.g:249:7: d= DOUBLE
                    {
                    d=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_number1822); if (state.failed) return n;

                    if ( state.backtracking==0 ) { n = Constant.createDoubleConstant((d!=null?d.getText():null)); }

                    }
                    break;
                case 3 :
                    // R.g:250:7: c= COMPLEX
                    {
                    c=(Token)match(input,COMPLEX,FOLLOW_COMPLEX_in_number1834); if (state.failed) return n;

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



    // $ANTLR start "conststring"
    // R.g:252:1: conststring returns [ASTNode n] : s= STRING ;
    public final ASTNode conststring() throws RecognitionException {
        ASTNode n = null;

        int conststring_StartIndex = input.index();

        Token s=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return n; }

            // R.g:253:5: (s= STRING )
            // R.g:253:7: s= STRING
            {
            s=(Token)match(input,STRING,FOLLOW_STRING_in_conststring1858); if (state.failed) return n;

            if ( state.backtracking==0 ) { n = Constant.createStringConstant((s!=null?s.getText():null)); }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 33, conststring_StartIndex); }

        }
        return n;
    }
    // $ANTLR end "conststring"



    // $ANTLR start "id"
    // R.g:255:1: id returns [Token t] : (i= ID |v= VARIATIC );
    public final Token id() throws RecognitionException {
        Token t = null;

        int id_StartIndex = input.index();

        Token i=null;
        Token v=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return t; }

            // R.g:256:5: (i= ID |v= VARIATIC )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==ID) ) {
                alt34=1;
            }
            else if ( (LA34_0==VARIATIC) ) {
                alt34=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return t;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;

            }
            switch (alt34) {
                case 1 :
                    // R.g:256:7: i= ID
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_id1882); if (state.failed) return t;

                    if ( state.backtracking==0 ) { t = i; }

                    }
                    break;
                case 2 :
                    // R.g:257:7: v= VARIATIC
                    {
                    v=(Token)match(input,VARIATIC,FOLLOW_VARIATIC_in_id1894); if (state.failed) return t;

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
            if ( state.backtracking>0 ) { memoize(input, 34, id_StartIndex); }

        }
        return t;
    }
    // $ANTLR end "id"



    // $ANTLR start "bool"
    // R.g:259:1: bool returns [ASTNode v] : ( TRUE | FALSE | NA );
    public final ASTNode bool() throws RecognitionException {
        ASTNode v = null;

        int bool_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return v; }

            // R.g:260:5: ( TRUE | FALSE | NA )
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
                    // R.g:260:7: TRUE
                    {
                    match(input,TRUE,FOLLOW_TRUE_in_bool1916); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Constant.createBoolConstant(1); }

                    }
                    break;
                case 2 :
                    // R.g:261:7: FALSE
                    {
                    match(input,FALSE,FOLLOW_FALSE_in_bool1926); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Constant.createBoolConstant(0); }

                    }
                    break;
                case 3 :
                    // R.g:262:7: NA
                    {
                    match(input,NA,FOLLOW_NA_in_bool1936); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 35, bool_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "bool"



    // $ANTLR start "or_operator"
    // R.g:264:1: or_operator returns [BinaryOperator v] : ( OR | BITWISEOR );
    public final BinaryOperator or_operator() throws RecognitionException {
        BinaryOperator v = null;

        int or_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return v; }

            // R.g:265:2: ( OR | BITWISEOR )
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
                    // R.g:265:4: OR
                    {
                    match(input,OR,FOLLOW_OR_in_or_operator1955); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.OR; }

                    }
                    break;
                case 2 :
                    // R.g:266:5: BITWISEOR
                    {
                    match(input,BITWISEOR,FOLLOW_BITWISEOR_in_or_operator1972); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 36, or_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "or_operator"



    // $ANTLR start "and_operator"
    // R.g:267:1: and_operator returns [BinaryOperator v] : ( AND | BITWISEAND );
    public final BinaryOperator and_operator() throws RecognitionException {
        BinaryOperator v = null;

        int and_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return v; }

            // R.g:268:2: ( AND | BITWISEAND )
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
                    // R.g:268:4: AND
                    {
                    match(input,AND,FOLLOW_AND_in_and_operator1988); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.AND; }

                    }
                    break;
                case 2 :
                    // R.g:269:4: BITWISEAND
                    {
                    match(input,BITWISEAND,FOLLOW_BITWISEAND_in_and_operator2004); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 37, and_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "and_operator"



    // $ANTLR start "comp_operator"
    // R.g:270:1: comp_operator returns [BinaryOperator v] : ( GT | GE | LT | LE | EQ | NE );
    public final BinaryOperator comp_operator() throws RecognitionException {
        BinaryOperator v = null;

        int comp_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return v; }

            // R.g:271:2: ( GT | GE | LT | LE | EQ | NE )
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
                    // R.g:271:4: GT
                    {
                    match(input,GT,FOLLOW_GT_in_comp_operator2020); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.GT; }

                    }
                    break;
                case 2 :
                    // R.g:272:4: GE
                    {
                    match(input,GE,FOLLOW_GE_in_comp_operator2027); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.GE; }

                    }
                    break;
                case 3 :
                    // R.g:273:4: LT
                    {
                    match(input,LT,FOLLOW_LT_in_comp_operator2034); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.LT; }

                    }
                    break;
                case 4 :
                    // R.g:274:4: LE
                    {
                    match(input,LE,FOLLOW_LE_in_comp_operator2041); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.LE; }

                    }
                    break;
                case 5 :
                    // R.g:275:4: EQ
                    {
                    match(input,EQ,FOLLOW_EQ_in_comp_operator2048); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.EQ; }

                    }
                    break;
                case 6 :
                    // R.g:276:4: NE
                    {
                    match(input,NE,FOLLOW_NE_in_comp_operator2055); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 38, comp_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "comp_operator"



    // $ANTLR start "add_operator"
    // R.g:277:1: add_operator returns [BinaryOperator v] : ( PLUS | MINUS );
    public final BinaryOperator add_operator() throws RecognitionException {
        BinaryOperator v = null;

        int add_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return v; }

            // R.g:278:2: ( PLUS | MINUS )
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
                    // R.g:278:4: PLUS
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_add_operator2069); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.ADD; }

                    }
                    break;
                case 2 :
                    // R.g:279:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_add_operator2076); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 39, add_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "add_operator"



    // $ANTLR start "mult_operator"
    // R.g:280:1: mult_operator returns [BinaryOperator v] : ( MULT | MAT_MULT | DIV | MOD );
    public final BinaryOperator mult_operator() throws RecognitionException {
        BinaryOperator v = null;

        int mult_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return v; }

            // R.g:281:2: ( MULT | MAT_MULT | DIV | MOD )
            int alt40=4;
            switch ( input.LA(1) ) {
            case MULT:
                {
                alt40=1;
                }
                break;
            case MAT_MULT:
                {
                alt40=2;
                }
                break;
            case DIV:
                {
                alt40=3;
                }
                break;
            case MOD:
                {
                alt40=4;
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
                    // R.g:281:4: MULT
                    {
                    match(input,MULT,FOLLOW_MULT_in_mult_operator2091); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.MULT; }

                    }
                    break;
                case 2 :
                    // R.g:282:4: MAT_MULT
                    {
                    match(input,MAT_MULT,FOLLOW_MAT_MULT_in_mult_operator2098); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.MAT_MULT; }

                    }
                    break;
                case 3 :
                    // R.g:283:4: DIV
                    {
                    match(input,DIV,FOLLOW_DIV_in_mult_operator2105); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.DIV; }

                    }
                    break;
                case 4 :
                    // R.g:284:4: MOD
                    {
                    match(input,MOD,FOLLOW_MOD_in_mult_operator2113); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 40, mult_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "mult_operator"



    // $ANTLR start "power_operator"
    // R.g:285:1: power_operator returns [BinaryOperator v] : CARRET ;
    public final BinaryOperator power_operator() throws RecognitionException {
        BinaryOperator v = null;

        int power_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return v; }

            // R.g:286:2: ( CARRET )
            // R.g:286:4: CARRET
            {
            match(input,CARRET,FOLLOW_CARRET_in_power_operator2128); if (state.failed) return v;

            if ( state.backtracking==0 ) {v = BinaryOperator.POW; }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 41, power_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "power_operator"



    // $ANTLR start "args"
    // R.g:288:1: args returns [ArgumentList v] : ( n_ arg_expr[v] )? n_ ( COMMA (| n_ arg_expr[v] ) n_ )* ;
    public final ArgumentList args() throws RecognitionException {
        ArgumentList v = null;

        int args_StartIndex = input.index();

         v = new ArgumentList.Default(); 
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return v; }

            // R.g:290:5: ( ( n_ arg_expr[v] )? n_ ( COMMA (| n_ arg_expr[v] ) n_ )* )
            // R.g:290:7: ( n_ arg_expr[v] )? n_ ( COMMA (| n_ arg_expr[v] ) n_ )*
            {
            // R.g:290:7: ( n_ arg_expr[v] )?
            int alt41=2;
            alt41 = dfa41.predict(input);
            switch (alt41) {
                case 1 :
                    // R.g:290:8: n_ arg_expr[v]
                    {
                    pushFollow(FOLLOW_n__in_args2153);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_arg_expr_in_args2155);
                    arg_expr(v);

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }


            pushFollow(FOLLOW_n__in_args2160);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:290:28: ( COMMA (| n_ arg_expr[v] ) n_ )*
            loop43:
            do {
                int alt43=2;
                int LA43_0 = input.LA(1);

                if ( (LA43_0==COMMA) ) {
                    alt43=1;
                }


                switch (alt43) {
            	case 1 :
            	    // R.g:290:29: COMMA (| n_ arg_expr[v] ) n_
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_args2163); if (state.failed) return v;

            	    // R.g:290:35: (| n_ arg_expr[v] )
            	    int alt42=2;
            	    alt42 = dfa42.predict(input);
            	    switch (alt42) {
            	        case 1 :
            	            // R.g:290:37: 
            	            {
            	            if ( state.backtracking==0 ) { v.add((ASTNode)null); }

            	            }
            	            break;
            	        case 2 :
            	            // R.g:290:66: n_ arg_expr[v]
            	            {
            	            pushFollow(FOLLOW_n__in_args2171);
            	            n_();

            	            state._fsp--;
            	            if (state.failed) return v;

            	            pushFollow(FOLLOW_arg_expr_in_args2173);
            	            arg_expr(v);

            	            state._fsp--;
            	            if (state.failed) return v;

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_n__in_args2177);
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
            if ( state.backtracking>0 ) { memoize(input, 42, args_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "args"



    // $ANTLR start "arg_expr"
    // R.g:292:1: arg_expr[ArgumentList l] : (e= expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );
    public final void arg_expr(ArgumentList l) throws RecognitionException {
        int arg_expr_StartIndex = input.index();

        ASTNode e =null;

        Token name =null;

        ASTNode val =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return ; }

            // R.g:293:2: (e= expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN )
            int alt44=5;
            alt44 = dfa44.predict(input);
            switch (alt44) {
                case 1 :
                    // R.g:293:4: e= expr
                    {
                    pushFollow(FOLLOW_expr_in_arg_expr2194);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add(e); }

                    }
                    break;
                case 2 :
                    // R.g:294:4: name= id n_ ASSIGN n_ val= expr
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2203);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2205);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2207); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2209);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_arg_expr2213);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add(name.getText(), val); }

                    }
                    break;
                case 3 :
                    // R.g:295:4: name= id n_ ASSIGN
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2222);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2224);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2226); if (state.failed) return ;

                    if ( state.backtracking==0 ) { l.add(name.getText(), null); }

                    }
                    break;
                case 4 :
                    // R.g:296:4: NULL n_ ASSIGN n_ val= expr
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2234); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2236);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2238); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2240);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_arg_expr2244);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;

                    if ( state.backtracking==0 ) { Utils.nyi(); }

                    }
                    break;
                case 5 :
                    // R.g:297:4: NULL n_ ASSIGN
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2251); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_arg_expr2253);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2255); if (state.failed) return ;

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
            if ( state.backtracking>0 ) { memoize(input, 43, arg_expr_StartIndex); }

        }
        return ;
    }
    // $ANTLR end "arg_expr"

    // $ANTLR start synpred1_R
    public final void synpred1_R_fragment() throws RecognitionException {
        // R.g:134:5: ( ARROW )
        // R.g:134:6: ARROW
        {
        match(input,ARROW,FOLLOW_ARROW_in_synpred1_R552); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_R

    // $ANTLR start synpred2_R
    public final void synpred2_R_fragment() throws RecognitionException {
        // R.g:135:5: ( SUPER_ARROW )
        // R.g:135:6: SUPER_ARROW
        {
        match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_synpred2_R570); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_R

    // $ANTLR start synpred3_R
    public final void synpred3_R_fragment() throws RecognitionException {
        // R.g:136:5: ( RIGHT_ARROW )
        // R.g:136:6: RIGHT_ARROW
        {
        match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_synpred3_R588); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred3_R

    // $ANTLR start synpred4_R
    public final void synpred4_R_fragment() throws RecognitionException {
        // R.g:137:5: ( SUPER_RIGHT_ARROW )
        // R.g:137:6: SUPER_RIGHT_ARROW
        {
        match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_synpred4_R608); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred4_R

    // $ANTLR start synpred5_R
    public final void synpred5_R_fragment() throws RecognitionException {
        // R.g:138:5: ( ASSIGN )
        // R.g:138:6: ASSIGN
        {
        match(input,ASSIGN,FOLLOW_ASSIGN_in_synpred5_R628); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred5_R

    // $ANTLR start synpred6_R
    public final void synpred6_R_fragment() throws RecognitionException {
        // R.g:145:3: ( n_ ELSE )
        // R.g:145:4: n_ ELSE
        {
        pushFollow(FOLLOW_n__in_synpred6_R691);
        n_();

        state._fsp--;
        if (state.failed) return ;

        match(input,ELSE,FOLLOW_ELSE_in_synpred6_R693); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred6_R

    // $ANTLR start synpred7_R
    public final void synpred7_R_fragment() throws RecognitionException {
        // R.g:176:5: ( TILDE )
        // R.g:176:6: TILDE
        {
        match(input,TILDE,FOLLOW_TILDE_in_synpred7_R1042); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred7_R

    // $ANTLR start synpred8_R
    public final void synpred8_R_fragment() throws RecognitionException {
        // R.g:180:4: ( or_operator )
        // R.g:180:5: or_operator
        {
        pushFollow(FOLLOW_or_operator_in_synpred8_R1081);
        or_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred8_R

    // $ANTLR start synpred9_R
    public final void synpred9_R_fragment() throws RecognitionException {
        // R.g:184:7: ( and_operator )
        // R.g:184:8: and_operator
        {
        pushFollow(FOLLOW_and_operator_in_synpred9_R1125);
        and_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred9_R

    // $ANTLR start synpred10_R
    public final void synpred10_R_fragment() throws RecognitionException {
        // R.g:192:7: ( comp_operator )
        // R.g:192:8: comp_operator
        {
        pushFollow(FOLLOW_comp_operator_in_synpred10_R1200);
        comp_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred10_R

    // $ANTLR start synpred11_R
    public final void synpred11_R_fragment() throws RecognitionException {
        // R.g:196:5: ( add_operator )
        // R.g:196:6: add_operator
        {
        pushFollow(FOLLOW_add_operator_in_synpred11_R1245);
        add_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred11_R

    // $ANTLR start synpred12_R
    public final void synpred12_R_fragment() throws RecognitionException {
        // R.g:200:4: ( mult_operator )
        // R.g:200:5: mult_operator
        {
        pushFollow(FOLLOW_mult_operator_in_synpred12_R1286);
        mult_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred12_R

    // $ANTLR start synpred13_R
    public final void synpred13_R_fragment() throws RecognitionException {
        // R.g:204:4: ( OP )
        // R.g:204:5: OP
        {
        match(input,OP,FOLLOW_OP_in_synpred13_R1327); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred13_R

    // $ANTLR start synpred14_R
    public final void synpred14_R_fragment() throws RecognitionException {
        // R.g:208:4: ( COLON )
        // R.g:208:5: COLON
        {
        match(input,COLON,FOLLOW_COLON_in_synpred14_R1373); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred14_R

    // $ANTLR start synpred15_R
    public final void synpred15_R_fragment() throws RecognitionException {
        // R.g:212:7: ( power_operator )
        // R.g:212:8: power_operator
        {
        pushFollow(FOLLOW_power_operator_in_synpred15_R1417);
        power_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred15_R

    // $ANTLR start synpred16_R
    public final void synpred16_R_fragment() throws RecognitionException {
        // R.g:223:4: ( FIELD | AT | LBRAKET | LBB | LPAR )
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
    // $ANTLR end synpred16_R

    // $ANTLR start synpred17_R
    public final void synpred17_R_fragment() throws RecognitionException {
        // R.g:223:76: ( n_ )
        // R.g:223:77: n_
        {
        pushFollow(FOLLOW_n__in_synpred17_R1547);
        n_();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred17_R

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


    protected DFA9 dfa9 = new DFA9(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA41 dfa41 = new DFA41(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA44 dfa44 = new DFA44(this);
    static final String DFA9_eotS =
        "\7\uffff";
    static final String DFA9_eofS =
        "\1\2\6\uffff";
    static final String DFA9_minS =
        "\1\20\3\13\2\uffff\1\13";
    static final String DFA9_maxS =
        "\1\107\3\123\2\uffff\1\123";
    static final String DFA9_acceptS =
        "\4\uffff\1\2\1\1\1\uffff";
    static final String DFA9_specialS =
        "\7\uffff}>";
    static final String[] DFA9_transitionS = {
            "\1\1\45\uffff\1\1\13\uffff\1\4\4\uffff\1\3",
            "\1\5\4\uffff\1\1\2\5\1\uffff\1\5\5\uffff\1\5\1\uffff\2\5\4"+
            "\uffff\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\3\uffff\1\5\2"+
            "\uffff\1\5\3\uffff\1\5\1\uffff\1\1\2\5\2\uffff\1\5\5\uffff\1"+
            "\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff\2\5\4\uffff\2\5",
            "\1\5\5\uffff\2\5\1\uffff\1\5\5\uffff\1\5\1\uffff\2\5\4\uffff"+
            "\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\3\uffff\1\5\2\uffff"+
            "\1\5\3\uffff\1\5\2\uffff\2\5\2\uffff\1\5\5\uffff\1\5\1\4\1\uffff"+
            "\1\5\4\uffff\1\5\2\uffff\2\5\4\uffff\2\5",
            "\1\5\4\uffff\1\6\2\5\1\uffff\1\5\5\uffff\1\5\1\uffff\2\5\4"+
            "\uffff\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\3\uffff\1\5\2"+
            "\uffff\1\5\3\uffff\1\5\1\uffff\1\6\2\5\2\uffff\1\5\5\uffff\1"+
            "\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff\2\5\4\uffff\2\5",
            "",
            "",
            "\1\5\4\uffff\1\6\2\5\1\uffff\1\5\5\uffff\1\5\1\uffff\2\5\4"+
            "\uffff\1\5\1\uffff\1\5\1\uffff\1\5\2\uffff\1\5\3\uffff\1\5\2"+
            "\uffff\1\5\3\uffff\1\5\1\uffff\1\6\2\5\2\uffff\1\5\5\uffff\1"+
            "\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff\2\5\4\uffff\2\5"
    };

    static final short[] DFA9_eot = DFA.unpackEncodedString(DFA9_eotS);
    static final short[] DFA9_eof = DFA.unpackEncodedString(DFA9_eofS);
    static final char[] DFA9_min = DFA.unpackEncodedStringToUnsignedChars(DFA9_minS);
    static final char[] DFA9_max = DFA.unpackEncodedStringToUnsignedChars(DFA9_maxS);
    static final short[] DFA9_accept = DFA.unpackEncodedString(DFA9_acceptS);
    static final short[] DFA9_special = DFA.unpackEncodedString(DFA9_specialS);
    static final short[][] DFA9_transition;

    static {
        int numStates = DFA9_transitionS.length;
        DFA9_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA9_transition[i] = DFA.unpackEncodedString(DFA9_transitionS[i]);
        }
    }

    class DFA9 extends DFA {

        public DFA9(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 9;
            this.eot = DFA9_eot;
            this.eof = DFA9_eof;
            this.min = DFA9_min;
            this.max = DFA9_max;
            this.accept = DFA9_accept;
            this.special = DFA9_special;
            this.transition = DFA9_transition;
        }
        @Override
        public String getDescription() {
            return "()* loopback of 121:50: ( n e= expr_or_assign )*";
        }
    }
    static final String DFA15_eotS =
        "\4\uffff";
    static final String DFA15_eofS =
        "\4\uffff";
    static final String DFA15_minS =
        "\2\17\2\uffff";
    static final String DFA15_maxS =
        "\2\106\2\uffff";
    static final String DFA15_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA15_specialS =
        "\4\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\3\1\1\45\uffff\1\1\17\uffff\1\2",
            "\1\3\1\1\45\uffff\1\1\17\uffff\1\2",
            "",
            ""
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        @Override
        public String getDescription() {
            return "()* loopback of 160:38: ( n_ COMMA n_ par_decl[l] )*";
        }
    }
    static final String DFA17_eotS =
        "\15\uffff";
    static final String DFA17_eofS =
        "\15\uffff";
    static final String DFA17_minS =
        "\1\22\4\6\2\uffff\1\6\2\uffff\1\6\2\uffff";
    static final String DFA17_maxS =
        "\1\122\4\106\2\uffff\1\106\2\uffff\1\106\2\uffff";
    static final String DFA17_acceptS =
        "\5\uffff\1\1\1\2\1\uffff\1\3\1\4\1\uffff\1\5\1\6";
    static final String DFA17_specialS =
        "\15\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\3\17\uffff\1\1\57\uffff\1\2",
            "\1\6\10\uffff\1\5\1\4\45\uffff\1\4\17\uffff\1\5",
            "\1\11\10\uffff\1\10\1\7\45\uffff\1\7\17\uffff\1\10",
            "\1\14\10\uffff\1\13\1\12\45\uffff\1\12\17\uffff\1\13",
            "\1\6\10\uffff\1\5\1\4\45\uffff\1\4\17\uffff\1\5",
            "",
            "",
            "\1\11\10\uffff\1\10\1\7\45\uffff\1\7\17\uffff\1\10",
            "",
            "",
            "\1\14\10\uffff\1\13\1\12\45\uffff\1\12\17\uffff\1\13",
            "",
            ""
    };

    static final short[] DFA17_eot = DFA.unpackEncodedString(DFA17_eotS);
    static final short[] DFA17_eof = DFA.unpackEncodedString(DFA17_eofS);
    static final char[] DFA17_min = DFA.unpackEncodedStringToUnsignedChars(DFA17_minS);
    static final char[] DFA17_max = DFA.unpackEncodedStringToUnsignedChars(DFA17_maxS);
    static final short[] DFA17_accept = DFA.unpackEncodedString(DFA17_acceptS);
    static final short[] DFA17_special = DFA.unpackEncodedString(DFA17_specialS);
    static final short[][] DFA17_transition;

    static {
        int numStates = DFA17_transitionS.length;
        DFA17_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA17_transition[i] = DFA.unpackEncodedString(DFA17_transitionS[i]);
        }
    }

    class DFA17 extends DFA {

        public DFA17(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 17;
            this.eot = DFA17_eot;
            this.eof = DFA17_eof;
            this.min = DFA17_min;
            this.max = DFA17_max;
            this.accept = DFA17_accept;
            this.special = DFA17_special;
            this.transition = DFA17_transition;
        }
        @Override
        public String getDescription() {
            return "162:1: par_decl[ArgumentList l] : (i= ID |i= ID n_ ASSIGN n_ e= expr |v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr );";
        }
    }
    static final String DFA41_eotS =
        "\4\uffff";
    static final String DFA41_eofS =
        "\4\uffff";
    static final String DFA41_minS =
        "\2\13\2\uffff";
    static final String DFA41_maxS =
        "\2\123\2\uffff";
    static final String DFA41_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA41_specialS =
        "\4\uffff}>";
    static final String[] DFA41_transitionS = {
            "\1\2\3\uffff\1\3\1\1\2\2\1\uffff\1\2\5\uffff\1\2\1\uffff\2\2"+
            "\4\uffff\1\2\1\uffff\1\2\1\uffff\1\2\2\uffff\1\2\3\uffff\1\2"+
            "\2\uffff\1\2\3\uffff\1\2\1\uffff\1\1\2\2\2\uffff\1\2\5\uffff"+
            "\1\2\1\uffff\1\3\1\2\1\uffff\1\3\2\uffff\1\2\2\uffff\2\2\4\uffff"+
            "\2\2",
            "\1\2\3\uffff\1\3\1\1\2\2\1\uffff\1\2\5\uffff\1\2\1\uffff\2"+
            "\2\4\uffff\1\2\1\uffff\1\2\1\uffff\1\2\2\uffff\1\2\3\uffff\1"+
            "\2\2\uffff\1\2\3\uffff\1\2\1\uffff\1\1\2\2\2\uffff\1\2\5\uffff"+
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
            return "290:7: ( n_ arg_expr[v] )?";
        }
    }
    static final String DFA42_eotS =
        "\4\uffff";
    static final String DFA42_eofS =
        "\4\uffff";
    static final String DFA42_minS =
        "\2\13\2\uffff";
    static final String DFA42_maxS =
        "\2\123\2\uffff";
    static final String DFA42_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA42_specialS =
        "\4\uffff}>";
    static final String[] DFA42_transitionS = {
            "\1\3\3\uffff\1\2\1\1\2\3\1\uffff\1\3\5\uffff\1\3\1\uffff\2\3"+
            "\4\uffff\1\3\1\uffff\1\3\1\uffff\1\3\2\uffff\1\3\3\uffff\1\3"+
            "\2\uffff\1\3\3\uffff\1\3\1\uffff\1\1\2\3\2\uffff\1\3\5\uffff"+
            "\1\3\1\uffff\1\2\1\3\1\uffff\1\2\2\uffff\1\3\2\uffff\2\3\4\uffff"+
            "\2\3",
            "\1\3\3\uffff\1\2\1\1\2\3\1\uffff\1\3\5\uffff\1\3\1\uffff\2"+
            "\3\4\uffff\1\3\1\uffff\1\3\1\uffff\1\3\2\uffff\1\3\3\uffff\1"+
            "\3\2\uffff\1\3\3\uffff\1\3\1\uffff\1\1\2\3\2\uffff\1\3\5\uffff"+
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
            return "290:35: (| n_ arg_expr[v] )";
        }
    }
    static final String DFA44_eotS =
        "\17\uffff";
    static final String DFA44_eofS =
        "\17\uffff";
    static final String DFA44_minS =
        "\1\13\1\uffff\3\4\1\6\1\13\1\6\2\13\2\uffff\1\13\2\uffff";
    static final String DFA44_maxS =
        "\1\123\1\uffff\3\114\1\106\1\123\1\106\2\123\2\uffff\1\123\2\uffff";
    static final String DFA44_acceptS =
        "\1\uffff\1\1\10\uffff\1\2\1\3\1\uffff\1\4\1\5";
    static final String DFA44_specialS =
        "\17\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\1\5\uffff\2\1\1\uffff\1\1\5\uffff\1\1\1\uffff\2\1\4\uffff"+
            "\1\2\1\uffff\1\1\1\uffff\1\1\2\uffff\1\1\3\uffff\1\1\2\uffff"+
            "\1\1\3\uffff\1\1\2\uffff\2\1\2\uffff\1\4\5\uffff\1\1\2\uffff"+
            "\1\1\4\uffff\1\1\2\uffff\2\1\4\uffff\1\3\1\1",
            "",
            "\2\1\1\6\3\1\3\uffff\3\1\1\5\2\uffff\1\1\2\uffff\1\1\4\uffff"+
            "\1\1\2\uffff\2\1\10\uffff\1\1\1\uffff\2\1\1\uffff\4\1\1\uffff"+
            "\2\1\1\uffff\1\1\1\5\2\uffff\2\1\2\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\1\1\1\uffff\2\1\3\uffff\3\1",
            "\2\1\1\6\3\1\3\uffff\3\1\1\5\2\uffff\1\1\2\uffff\1\1\4\uffff"+
            "\1\1\2\uffff\2\1\10\uffff\1\1\1\uffff\2\1\1\uffff\4\1\1\uffff"+
            "\2\1\1\uffff\1\1\1\5\2\uffff\2\1\2\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\1\1\1\uffff\2\1\3\uffff\3\1",
            "\2\1\1\10\3\1\3\uffff\3\1\1\7\2\uffff\1\1\2\uffff\1\1\4\uffff"+
            "\1\1\2\uffff\2\1\10\uffff\1\1\1\uffff\2\1\1\uffff\4\1\1\uffff"+
            "\2\1\1\uffff\1\1\1\7\6\uffff\1\1\1\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\2\1\3\uffff\3\1",
            "\1\6\10\uffff\1\1\1\5\45\uffff\1\5\14\uffff\1\1\2\uffff\1\1",
            "\1\12\3\uffff\1\13\1\11\2\12\1\uffff\1\12\5\uffff\1\12\1\uffff"+
            "\2\12\4\uffff\1\12\1\uffff\1\12\1\uffff\1\12\2\uffff\1\12\3"+
            "\uffff\1\12\2\uffff\1\12\3\uffff\1\12\1\uffff\1\11\2\12\2\uffff"+
            "\1\12\5\uffff\1\12\1\uffff\1\13\1\12\1\uffff\1\13\2\uffff\1"+
            "\12\2\uffff\2\12\4\uffff\2\12",
            "\1\10\10\uffff\1\1\1\7\45\uffff\1\7\14\uffff\1\1\2\uffff\1"+
            "\1",
            "\1\15\3\uffff\1\16\1\14\2\15\1\uffff\1\15\5\uffff\1\15\1\uffff"+
            "\2\15\4\uffff\1\15\1\uffff\1\15\1\uffff\1\15\2\uffff\1\15\3"+
            "\uffff\1\15\2\uffff\1\15\3\uffff\1\15\1\uffff\1\14\2\15\2\uffff"+
            "\1\15\5\uffff\1\15\1\uffff\1\16\1\15\1\uffff\1\16\2\uffff\1"+
            "\15\2\uffff\2\15\4\uffff\2\15",
            "\1\12\3\uffff\1\13\1\11\2\12\1\uffff\1\12\5\uffff\1\12\1\uffff"+
            "\2\12\4\uffff\1\12\1\uffff\1\12\1\uffff\1\12\2\uffff\1\12\3"+
            "\uffff\1\12\2\uffff\1\12\3\uffff\1\12\1\uffff\1\11\2\12\2\uffff"+
            "\1\12\5\uffff\1\12\1\uffff\1\13\1\12\1\uffff\1\13\2\uffff\1"+
            "\12\2\uffff\2\12\4\uffff\2\12",
            "",
            "",
            "\1\15\3\uffff\1\16\1\14\2\15\1\uffff\1\15\5\uffff\1\15\1\uffff"+
            "\2\15\4\uffff\1\15\1\uffff\1\15\1\uffff\1\15\2\uffff\1\15\3"+
            "\uffff\1\15\2\uffff\1\15\3\uffff\1\15\1\uffff\1\14\2\15\2\uffff"+
            "\1\15\5\uffff\1\15\1\uffff\1\16\1\15\1\uffff\1\16\2\uffff\1"+
            "\15\2\uffff\2\15\4\uffff\2\15",
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
            return "292:1: arg_expr[ArgumentList l] : (e= expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );";
        }
    }
 

    public static final BitSet FOLLOW_n__in_script156 = new BitSet(new long[]{0x0991225434160802L,0x00000000002C3212L});
    public static final BitSet FOLLOW_statement_in_script161 = new BitSet(new long[]{0x0991225434160802L,0x00000000002C3212L});
    public static final BitSet FOLLOW_n__in_interactive192 = new BitSet(new long[]{0x0991225434160802L,0x00000000002C3212L});
    public static final BitSet FOLLOW_statement_in_interactive197 = new BitSet(new long[]{0x0991225434160802L,0x00000000002C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_statement217 = new BitSet(new long[]{0x0040000000010000L,0x0000000000000080L});
    public static final BitSet FOLLOW_n_in_statement219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_85_in_statement226 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000003FFFFFL});
    public static final BitSet FOLLOW_EOF_in_statement231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_n266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_n270 = new BitSet(new long[]{0x0040000000010000L});
    public static final BitSet FOLLOW_n__in_n272 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alter_assign_in_expr_or_assign287 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assign_in_expr305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_while_expr_in_expr_wo_assign324 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_if_expr_in_expr_wo_assign333 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_for_expr_in_expr_wo_assign342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_repeat_expr_in_expr_wo_assign351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_expr_wo_assign360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEXT_in_expr_wo_assign367 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_expr_wo_assign377 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_sequence411 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3216L});
    public static final BitSet FOLLOW_n__in_sequence413 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3216L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence418 = new BitSet(new long[]{0x0040000000010000L,0x0000000000000084L});
    public static final BitSet FOLLOW_n_in_sequence423 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence427 = new BitSet(new long[]{0x0040000000010000L,0x0000000000000084L});
    public static final BitSet FOLLOW_n_in_sequence433 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000004L});
    public static final BitSet FOLLOW_RBRACE_in_sequence439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_assign457 = new BitSet(new long[]{0x0000000000000022L,0x0000000000000C20L});
    public static final BitSet FOLLOW_ARROW_in_assign464 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_assign466 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_in_assign470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_assign478 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_assign480 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_in_assign484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_assign494 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_assign496 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_in_assign500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_assign510 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_assign512 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_in_assign516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_alter_assign544 = new BitSet(new long[]{0x0000000000000062L,0x0000000000000C20L});
    public static final BitSet FOLLOW_ARROW_in_alter_assign555 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_alter_assign557 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_alter_assign573 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_alter_assign575 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_alter_assign593 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_alter_assign595 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_alter_assign613 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_alter_assign615 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_alter_assign633 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_alter_assign635 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_if_expr666 = new BitSet(new long[]{0x0040200000010000L});
    public static final BitSet FOLLOW_n__in_if_expr668 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_LPAR_in_if_expr670 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_if_expr672 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr676 = new BitSet(new long[]{0x0040000000010000L,0x0000000000000040L});
    public static final BitSet FOLLOW_n__in_if_expr678 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RPAR_in_if_expr680 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_if_expr682 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr686 = new BitSet(new long[]{0x0040000000210002L});
    public static final BitSet FOLLOW_n__in_if_expr712 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_ELSE_in_if_expr714 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_if_expr716 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_while_expr748 = new BitSet(new long[]{0x0040200000010000L});
    public static final BitSet FOLLOW_n__in_while_expr750 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_LPAR_in_while_expr752 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_while_expr754 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr758 = new BitSet(new long[]{0x0040000000010000L,0x0000000000000040L});
    public static final BitSet FOLLOW_n__in_while_expr760 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RPAR_in_while_expr762 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_while_expr764 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_for_expr784 = new BitSet(new long[]{0x0040200000010000L});
    public static final BitSet FOLLOW_n__in_for_expr786 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_LPAR_in_for_expr788 = new BitSet(new long[]{0x0040000400010000L});
    public static final BitSet FOLLOW_n__in_for_expr790 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_ID_in_for_expr794 = new BitSet(new long[]{0x0040002000010000L});
    public static final BitSet FOLLOW_n__in_for_expr796 = new BitSet(new long[]{0x0000002000000000L});
    public static final BitSet FOLLOW_IN_in_for_expr798 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_for_expr800 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr804 = new BitSet(new long[]{0x0040000000010000L,0x0000000000000040L});
    public static final BitSet FOLLOW_n__in_for_expr806 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RPAR_in_for_expr808 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_for_expr810 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REPEAT_in_repeat_expr831 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_repeat_expr833 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_repeat_expr837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function858 = new BitSet(new long[]{0x0040200000010000L});
    public static final BitSet FOLLOW_n__in_function860 = new BitSet(new long[]{0x0000200000000000L});
    public static final BitSet FOLLOW_LPAR_in_function862 = new BitSet(new long[]{0x0040000400050000L,0x0000000000040040L});
    public static final BitSet FOLLOW_n__in_function865 = new BitSet(new long[]{0x0000000400040000L,0x0000000000040040L});
    public static final BitSet FOLLOW_par_decl_in_function868 = new BitSet(new long[]{0x0040000000018000L,0x0000000000000040L});
    public static final BitSet FOLLOW_n__in_function872 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_function874 = new BitSet(new long[]{0x0040000400050000L,0x0000000000040000L});
    public static final BitSet FOLLOW_n__in_function876 = new BitSet(new long[]{0x0000000400040000L,0x0000000000040000L});
    public static final BitSet FOLLOW_par_decl_in_function878 = new BitSet(new long[]{0x0040000000018000L,0x0000000000000040L});
    public static final BitSet FOLLOW_n__in_function883 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RPAR_in_function887 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_function889 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_function893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl920 = new BitSet(new long[]{0x0040000000010040L});
    public static final BitSet FOLLOW_n__in_par_decl922 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl924 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_par_decl926 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_in_par_decl930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_par_decl939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_par_decl989 = new BitSet(new long[]{0x0040000000010040L});
    public static final BitSet FOLLOW_n__in_par_decl991 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl993 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_par_decl995 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_in_par_decl997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_par_decl1003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_par_decl1009 = new BitSet(new long[]{0x0040000000010040L});
    public static final BitSet FOLLOW_n__in_par_decl1011 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl1013 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_par_decl1015 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_in_par_decl1017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_expr_in_tilde_expr1033 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_TILDE_in_tilde_expr1045 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_tilde_expr1047 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_or_expr_in_tilde_expr1051 = new BitSet(new long[]{0x0000000000000002L,0x0000000000001000L});
    public static final BitSet FOLLOW_and_expr_in_or_expr1073 = new BitSet(new long[]{0x8000000000000202L});
    public static final BitSet FOLLOW_or_operator_in_or_expr1086 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_or_expr1088 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_and_expr_in_or_expr1092 = new BitSet(new long[]{0x8000000000000202L});
    public static final BitSet FOLLOW_not_expr_in_and_expr1114 = new BitSet(new long[]{0x0000000000000112L});
    public static final BitSet FOLLOW_and_operator_in_and_expr1130 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_and_expr1132 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_not_expr_in_and_expr1136 = new BitSet(new long[]{0x0000000000000112L});
    public static final BitSet FOLLOW_NOT_in_not_expr1156 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_not_expr1158 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_not_expr_in_not_expr1162 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_expr_in_not_expr1171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1189 = new BitSet(new long[]{0x00204800C0400002L});
    public static final BitSet FOLLOW_comp_operator_in_comp_expr1205 = new BitSet(new long[]{0x08D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_comp_expr1207 = new BitSet(new long[]{0x0891225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1211 = new BitSet(new long[]{0x00204800C0400002L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1236 = new BitSet(new long[]{0x0001000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_add_operator_in_add_expr1250 = new BitSet(new long[]{0x08D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_add_expr1252 = new BitSet(new long[]{0x0891225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1256 = new BitSet(new long[]{0x0001000000000002L,0x0000000000000002L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1278 = new BitSet(new long[]{0x000C800000080002L});
    public static final BitSet FOLLOW_mult_operator_in_mult_expr1291 = new BitSet(new long[]{0x08D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_mult_expr1293 = new BitSet(new long[]{0x0891225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1297 = new BitSet(new long[]{0x000C800000080002L});
    public static final BitSet FOLLOW_colon_expr_in_operator_expr1319 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_OP_in_operator_expr1332 = new BitSet(new long[]{0x08D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_operator_expr1334 = new BitSet(new long[]{0x0891225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_colon_expr_in_operator_expr1338 = new BitSet(new long[]{0x2000000000000002L});
    public static final BitSet FOLLOW_power_expr_in_colon_expr1365 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_COLON_in_colon_expr1378 = new BitSet(new long[]{0x08D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_colon_expr1380 = new BitSet(new long[]{0x0891225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_power_expr_in_colon_expr1384 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_unary_expression_in_power_expr1406 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_power_operator_in_power_expr1422 = new BitSet(new long[]{0x08D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_power_expr1424 = new BitSet(new long[]{0x0891225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_power_expr_in_power_expr1428 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary_expression1457 = new BitSet(new long[]{0x08D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_unary_expression1459 = new BitSet(new long[]{0x0891225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1463 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary_expression1470 = new BitSet(new long[]{0x08D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_unary_expression1472 = new BitSet(new long[]{0x0891225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1476 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unary_expression1483 = new BitSet(new long[]{0x08D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_unary_expression1485 = new BitSet(new long[]{0x0891225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1489 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_basic_expr_in_unary_expression1498 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_basic_expr1516 = new BitSet(new long[]{0x0000250008000082L});
    public static final BitSet FOLLOW_expr_subset_in_basic_expr1537 = new BitSet(new long[]{0x0000250008000082L});
    public static final BitSet FOLLOW_FIELD_in_expr_subset1570 = new BitSet(new long[]{0x0040000400010000L,0x0000000000040000L});
    public static final BitSet FOLLOW_n__in_expr_subset1572 = new BitSet(new long[]{0x0000000400000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_id_in_expr_subset1576 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_expr_subset1589 = new BitSet(new long[]{0x0040000400010000L,0x0000000000040000L});
    public static final BitSet FOLLOW_n__in_expr_subset1591 = new BitSet(new long[]{0x0000000400000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_id_in_expr_subset1595 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRAKET_in_expr_subset1609 = new BitSet(new long[]{0x09D1225434178800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_args_in_expr_subset1613 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1615 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBB_in_expr_subset1627 = new BitSet(new long[]{0x09D1225434178800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_args_in_expr_subset1631 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1633 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000008L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1635 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_subset1652 = new BitSet(new long[]{0x09D1225434178800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_args_in_expr_subset1656 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RPAR_in_expr_subset1658 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_in_simple_expr1696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_simple_expr1703 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_simple_expr1708 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_simple_expr1717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conststring_in_simple_expr1726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1733 = new BitSet(new long[]{0x0200000000000000L});
    public static final BitSet FOLLOW_NS_GET_in_simple_expr1735 = new BitSet(new long[]{0x0040000400010000L,0x0000000000040000L});
    public static final BitSet FOLLOW_n__in_simple_expr1737 = new BitSet(new long[]{0x0000000400000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_id_in_simple_expr1739 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1744 = new BitSet(new long[]{0x0400000000000000L});
    public static final BitSet FOLLOW_NS_GET_INT_in_simple_expr1746 = new BitSet(new long[]{0x0040000400010000L,0x0000000000040000L});
    public static final BitSet FOLLOW_n__in_simple_expr1748 = new BitSet(new long[]{0x0000000400000000L,0x0000000000040000L});
    public static final BitSet FOLLOW_id_in_simple_expr1750 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_simple_expr1755 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_simple_expr1757 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_or_assign_in_simple_expr1763 = new BitSet(new long[]{0x0040000000010000L,0x0000000000000040L});
    public static final BitSet FOLLOW_n__in_simple_expr1765 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000040L});
    public static final BitSet FOLLOW_RPAR_in_simple_expr1767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_simple_expr1778 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_wo_assign_in_simple_expr1789 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_number1810 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_number1822 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMPLEX_in_number1834 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_conststring1858 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_id1882 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_id1894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_bool1916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_bool1926 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NA_in_bool1936 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_or_operator1955 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEOR_in_or_operator1972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_and_operator1988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEAND_in_and_operator2004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_comp_operator2020 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GE_in_comp_operator2027 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_comp_operator2034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LE_in_comp_operator2041 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_comp_operator2048 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NE_in_comp_operator2055 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_add_operator2069 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_add_operator2076 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_in_mult_operator2091 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MAT_MULT_in_mult_operator2098 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_in_mult_operator2105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mult_operator2113 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARRET_in_power_operator2128 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_args2153 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_arg_expr_in_args2155 = new BitSet(new long[]{0x0040000000018000L});
    public static final BitSet FOLLOW_n__in_args2160 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_COMMA_in_args2163 = new BitSet(new long[]{0x09D1225434178800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_args2171 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_arg_expr_in_args2173 = new BitSet(new long[]{0x0040000000018000L});
    public static final BitSet FOLLOW_n__in_args2177 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_expr_in_arg_expr2194 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2203 = new BitSet(new long[]{0x0040000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2205 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2207 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_arg_expr2209 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_in_arg_expr2213 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2222 = new BitSet(new long[]{0x0040000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2224 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2226 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2234 = new BitSet(new long[]{0x0040000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2236 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2238 = new BitSet(new long[]{0x09D1225434170800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_n__in_arg_expr2240 = new BitSet(new long[]{0x0991225434160800L,0x00000000000C3212L});
    public static final BitSet FOLLOW_expr_in_arg_expr2244 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2251 = new BitSet(new long[]{0x0040000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2253 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_synpred1_R552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_synpred2_R570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_synpred3_R588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_synpred4_R608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_synpred5_R628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred6_R691 = new BitSet(new long[]{0x0000000000200000L});
    public static final BitSet FOLLOW_ELSE_in_synpred6_R693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_synpred7_R1042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_operator_in_synpred8_R1081 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_operator_in_synpred9_R1125 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_operator_in_synpred10_R1200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_operator_in_synpred11_R1245 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_operator_in_synpred12_R1286 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OP_in_synpred13_R1327 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_synpred14_R1373 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_operator_in_synpred15_R1417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred17_R1547 = new BitSet(new long[]{0x0000000000000002L});

}
