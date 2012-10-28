// $ANTLR !Unknown version! R.g 2012-10-27 18:03:53

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
public class RParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CALL", "BRAKET", "KW", "PARMS", "SEQUENCE", "MISSING_VAL", "UPLUS", "UMINUS", "UTILDE", "NEWLINE", "COMMENT", "SEMICOLON", "NEXT", "BREAK", "LBRACE", "RBRACE", "ARROW", "SUPER_ARROW", "RIGHT_ARROW", "SUPER_RIGHT_ARROW", "ASSIGN", "IF", "LPAR", "RPAR", "ELSE", "WHILE", "FOR", "ID", "IN", "REPEAT", "FUNCTION", "COMMA", "VARIATIC", "DD", "TILDE", "NOT", "OP", "COLON", "PLUS", "MINUS", "FIELD", "AT", "LBRAKET", "RBRAKET", "LBB", "NULL", "NS_GET", "NS_GET_INT", "INTEGER", "DOUBLE", "COMPLEX", "STRING", "TRUE", "FALSE", "NA", "OR", "BITWISEOR", "AND", "BITWISEAND", "GT", "GE", "LT", "LE", "EQ", "NE", "MULT", "DIV", "MOD", "CARRET", "LINE_BREAK", "WS", "EXPONENT", "HEX_DIGIT", "ID_NAME", "ESC_SEQ", "OP_NAME", "ESCAPE", "UNICODE_ESC", "OCTAL_ESC", "HEX_ESC", "'--EOF--'"
    };
    public static final int FUNCTION=34;
    public static final int PARMS=7;
    public static final int EXPONENT=75;
    public static final int LT=65;
    public static final int UTILDE=12;
    public static final int WHILE=29;
    public static final int MOD=71;
    public static final int LBB=48;
    public static final int RIGHT_ARROW=22;
    public static final int LBRACE=18;
    public static final int OCTAL_ESC=82;
    public static final int MISSING_VAL=9;
    public static final int FOR=30;
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
    public static final int LBRAKET=46;
    public static final int RBRACE=19;
    public static final int NS_GET_INT=51;
    public static final int UPLUS=10;
    public static final int UNICODE_ESC=81;
    public static final int NULL=49;
    public static final int ELSE=28;
    public static final int ID_NAME=77;
    public static final int HEX_DIGIT=76;
    public static final int UMINUS=11;
    public static final int SEMICOLON=15;
    public static final int MULT=69;
    public static final int MINUS=43;
    public static final int T__84=84;
    public static final int TRUE=56;
    public static final int VARIATIC=36;
    public static final int BITWISEOR=60;
    public static final int COLON=41;
    public static final int SEQUENCE=8;
    public static final int HEX_ESC=83;
    public static final int LINE_BREAK=73;
    public static final int WS=74;
    public static final int NEWLINE=13;
    public static final int KW=6;
    public static final int SUPER_RIGHT_ARROW=23;
    public static final int SUPER_ARROW=21;
    public static final int OP=40;
    public static final int OR=59;
    public static final int ASSIGN=24;
    public static final int GT=63;
    public static final int NS_GET=50;
    public static final int ARROW=20;
    public static final int FIELD=44;
    public static final int RPAR=27;
    public static final int REPEAT=33;
    public static final int OP_NAME=79;
    public static final int CARRET=72;
    public static final int CALL=4;
    public static final int DIV=70;
    public static final int FALSE=57;
    public static final int LE=66;
    public static final int STRING=55;

    // delegates
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
    // R.g:73:1: script returns [ASTNode v] : n_ (s= statement )* ;
    public final ASTNode script() throws RecognitionException {
        ASTNode v = null;
        int script_StartIndex = input.index();
        ASTNode s = null;


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

                if ( ((LA1_0>=NEXT && LA1_0<=LBRACE)||(LA1_0>=IF && LA1_0<=LPAR)||(LA1_0>=WHILE && LA1_0<=ID)||(LA1_0>=REPEAT && LA1_0<=FUNCTION)||(LA1_0>=VARIATIC && LA1_0<=NOT)||(LA1_0>=PLUS && LA1_0<=MINUS)||LA1_0==NULL||(LA1_0>=INTEGER && LA1_0<=NA)||LA1_0==84) ) {
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
            	    if ( state.backtracking==0 ) {
            	      stmts.add(s);
            	    }

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            if ( state.backtracking==0 ) {
               v = Sequence.create(stmts);
            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode e = null;


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
            if ( state.backtracking==0 ) {
              v = e;
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode e = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return v; }
            // R.g:94:2: (e= expr_or_assign n | '--EOF--' ( . )* EOF )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( ((LA3_0>=NEXT && LA3_0<=LBRACE)||(LA3_0>=IF && LA3_0<=LPAR)||(LA3_0>=WHILE && LA3_0<=ID)||(LA3_0>=REPEAT && LA3_0<=FUNCTION)||(LA3_0>=VARIATIC && LA3_0<=NOT)||(LA3_0>=PLUS && LA3_0<=MINUS)||LA3_0==NULL||(LA3_0>=INTEGER && LA3_0<=NA)) ) {
                alt3=1;
            }
            else if ( (LA3_0==84) ) {
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
                    if ( state.backtracking==0 ) {
                      v = e;
                    }

                    }
                    break;
                case 2 :
                    // R.g:95:4: '--EOF--' ( . )* EOF
                    {
                    match(input,84,FOLLOW_84_in_statement210); if (state.failed) return v;
                    // R.g:95:14: ( . )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0>=CALL && LA2_0<=84)) ) {
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

                if ( ((LA4_0>=NEWLINE && LA4_0<=COMMENT)) ) {
                    alt4=1;
                }


                switch (alt4) {
            	case 1 :
            	    // R.g:
            	    {
            	    if ( (input.LA(1)>=NEWLINE && input.LA(1)<=COMMENT) ) {
            	        input.consume();
            	        state.errorRecovery=false;state.failed=false;
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
            if ( state.backtracking>0 ) { memoize(input, 4, n__StartIndex); }
        }
        return ;
    }
    // $ANTLR end "n_"


    // $ANTLR start "n"
    // R.g:99:1: n : ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLON n_ );
    public final void n() throws RecognitionException {
        int n_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }
            // R.g:99:3: ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLON n_ )
            int alt6=3;
            switch ( input.LA(1) ) {
            case NEWLINE:
            case COMMENT:
                {
                alt6=1;
                }
                break;
            case EOF:
                {
                alt6=2;
                }
                break;
            case SEMICOLON:
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

                        if ( ((LA5_0>=NEWLINE && LA5_0<=COMMENT)) ) {
                            alt5=1;
                        }


                        switch (alt5) {
                    	case 1 :
                    	    // R.g:
                    	    {
                    	    if ( (input.LA(1)>=NEWLINE && input.LA(1)<=COMMENT) ) {
                    	        input.consume();
                    	        state.errorRecovery=false;state.failed=false;
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
                    // R.g:99:34: SEMICOLON n_
                    {
                    match(input,SEMICOLON,FOLLOW_SEMICOLON_in_n254); if (state.failed) return ;
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
        ASTNode a = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return v; }
            // R.g:102:2: (a= alter_assign )
            // R.g:102:4: a= alter_assign
            {
            pushFollow(FOLLOW_alter_assign_in_expr_or_assign271);
            a=alter_assign();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = a; 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode a = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return v; }
            // R.g:105:2: (a= assign )
            // R.g:105:4: a= assign
            {
            pushFollow(FOLLOW_assign_in_expr289);
            a=assign();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = a; 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 7, expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "expr"


    // $ANTLR start "expr_wo_assign"
    // R.g:107:1: expr_wo_assign returns [ASTNode v] : (w= while_expr | i= if_expr | f= for_expr | r= repeat_expr | fun= function | NEXT | BREAK );
    public final ASTNode expr_wo_assign() throws RecognitionException {
        ASTNode v = null;
        int expr_wo_assign_StartIndex = input.index();
        ASTNode w = null;

        ASTNode i = null;

        ASTNode f = null;

        ASTNode r = null;

        ASTNode fun = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return v; }
            // R.g:108:2: (w= while_expr | i= if_expr | f= for_expr | r= repeat_expr | fun= function | NEXT | BREAK )
            int alt7=7;
            switch ( input.LA(1) ) {
            case WHILE:
                {
                alt7=1;
                }
                break;
            case IF:
                {
                alt7=2;
                }
                break;
            case FOR:
                {
                alt7=3;
                }
                break;
            case REPEAT:
                {
                alt7=4;
                }
                break;
            case FUNCTION:
                {
                alt7=5;
                }
                break;
            case NEXT:
                {
                alt7=6;
                }
                break;
            case BREAK:
                {
                alt7=7;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // R.g:108:4: w= while_expr
                    {
                    pushFollow(FOLLOW_while_expr_in_expr_wo_assign308);
                    w=while_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = w; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:109:4: i= if_expr
                    {
                    pushFollow(FOLLOW_if_expr_in_expr_wo_assign317);
                    i=if_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = i; 
                    }

                    }
                    break;
                case 3 :
                    // R.g:110:4: f= for_expr
                    {
                    pushFollow(FOLLOW_for_expr_in_expr_wo_assign326);
                    f=for_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = f; 
                    }

                    }
                    break;
                case 4 :
                    // R.g:111:4: r= repeat_expr
                    {
                    pushFollow(FOLLOW_repeat_expr_in_expr_wo_assign335);
                    r=repeat_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = r; 
                    }

                    }
                    break;
                case 5 :
                    // R.g:112:4: fun= function
                    {
                    pushFollow(FOLLOW_function_in_expr_wo_assign344);
                    fun=function();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = fun; 
                    }

                    }
                    break;
                case 6 :
                    // R.g:113:4: NEXT
                    {
                    match(input,NEXT,FOLLOW_NEXT_in_expr_wo_assign351); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Next.create(); 
                    }

                    }
                    break;
                case 7 :
                    // R.g:114:4: BREAK
                    {
                    match(input,BREAK,FOLLOW_BREAK_in_expr_wo_assign361); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Break.create(); 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode e = null;


        ArrayList<ASTNode> stmts = new ArrayList<ASTNode>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return v; }
            // R.g:119:2: ( LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE )
            // R.g:119:4: LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE
            {
            match(input,LBRACE,FOLLOW_LBRACE_in_sequence395); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_sequence397);
            n_();

            state._fsp--;
            if (state.failed) return v;
            // R.g:119:14: (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )?
            int alt10=2;
            int LA10_0 = input.LA(1);

            if ( ((LA10_0>=NEXT && LA10_0<=LBRACE)||(LA10_0>=IF && LA10_0<=LPAR)||(LA10_0>=WHILE && LA10_0<=ID)||(LA10_0>=REPEAT && LA10_0<=FUNCTION)||(LA10_0>=VARIATIC && LA10_0<=NOT)||(LA10_0>=PLUS && LA10_0<=MINUS)||LA10_0==NULL||(LA10_0>=INTEGER && LA10_0<=NA)) ) {
                alt10=1;
            }
            switch (alt10) {
                case 1 :
                    // R.g:119:15: e= expr_or_assign ( n e= expr_or_assign )* ( n )?
                    {
                    pushFollow(FOLLOW_expr_or_assign_in_sequence402);
                    e=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       stmts.add(e); 
                    }
                    // R.g:119:50: ( n e= expr_or_assign )*
                    loop8:
                    do {
                        int alt8=2;
                        alt8 = dfa8.predict(input);
                        switch (alt8) {
                    	case 1 :
                    	    // R.g:119:51: n e= expr_or_assign
                    	    {
                    	    pushFollow(FOLLOW_n_in_sequence407);
                    	    n();

                    	    state._fsp--;
                    	    if (state.failed) return v;
                    	    pushFollow(FOLLOW_expr_or_assign_in_sequence411);
                    	    e=expr_or_assign();

                    	    state._fsp--;
                    	    if (state.failed) return v;
                    	    if ( state.backtracking==0 ) {
                    	       stmts.add(e); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop8;
                        }
                    } while (true);

                    // R.g:119:90: ( n )?
                    int alt9=2;
                    int LA9_0 = input.LA(1);

                    if ( (LA9_0==EOF||(LA9_0>=NEWLINE && LA9_0<=SEMICOLON)) ) {
                        alt9=1;
                    }
                    switch (alt9) {
                        case 1 :
                            // R.g:119:90: n
                            {
                            pushFollow(FOLLOW_n_in_sequence417);
                            n();

                            state._fsp--;
                            if (state.failed) return v;

                            }
                            break;

                    }


                    }
                    break;

            }

            match(input,RBRACE,FOLLOW_RBRACE_in_sequence423); if (state.failed) return v;

            }

            if ( state.backtracking==0 ) {
               v = Sequence.create(stmts);
            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 9, sequence_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "sequence"


    // $ANTLR start "assign"
    // R.g:121:1: assign returns [ASTNode v] : l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr | a= RIGHT_ARROW n_ r= expr | a= SUPER_RIGHT_ARROW n_ r= expr | ) ;
    public final ASTNode assign() throws RecognitionException {
        ASTNode v = null;
        int assign_StartIndex = input.index();
        Token a=null;
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return v; }
            // R.g:122:2: (l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr | a= RIGHT_ARROW n_ r= expr | a= SUPER_RIGHT_ARROW n_ r= expr | ) )
            // R.g:122:4: l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr | a= RIGHT_ARROW n_ r= expr | a= SUPER_RIGHT_ARROW n_ r= expr | )
            {
            pushFollow(FOLLOW_tilde_expr_in_assign441);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;
            // R.g:123:3: ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr | a= RIGHT_ARROW n_ r= expr | a= SUPER_RIGHT_ARROW n_ r= expr | )
            int alt11=5;
            switch ( input.LA(1) ) {
            case ARROW:
                {
                alt11=1;
                }
                break;
            case SUPER_ARROW:
                {
                alt11=2;
                }
                break;
            case RIGHT_ARROW:
                {
                alt11=3;
                }
                break;
            case SUPER_RIGHT_ARROW:
                {
                alt11=4;
                }
                break;
            case EOF:
            case NEWLINE:
            case COMMENT:
            case NEXT:
            case BREAK:
            case LBRACE:
            case RBRACE:
            case ASSIGN:
            case IF:
            case LPAR:
            case RPAR:
            case ELSE:
            case WHILE:
            case FOR:
            case ID:
            case IN:
            case REPEAT:
            case FUNCTION:
            case COMMA:
            case VARIATIC:
            case DD:
            case TILDE:
            case NOT:
            case PLUS:
            case MINUS:
            case RBRAKET:
            case NULL:
            case INTEGER:
            case DOUBLE:
            case COMPLEX:
            case STRING:
            case TRUE:
            case FALSE:
            case NA:
            case 84:
                {
                alt11=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // R.g:123:5: ARROW n_ r= expr
                    {
                    match(input,ARROW,FOLLOW_ARROW_in_assign448); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_assign450);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_in_assign454);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(false, l, r);
                    }

                    }
                    break;
                case 2 :
                    // R.g:124:5: SUPER_ARROW n_ r= expr
                    {
                    match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_assign462); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_assign464);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_in_assign468);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(true, l, r);
                    }

                    }
                    break;
                case 3 :
                    // R.g:125:5: a= RIGHT_ARROW n_ r= expr
                    {
                    a=(Token)match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_assign478); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_assign480);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_in_assign484);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(false, r, l);
                    }

                    }
                    break;
                case 4 :
                    // R.g:126:5: a= SUPER_RIGHT_ARROW n_ r= expr
                    {
                    a=(Token)match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_assign494); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_assign496);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_in_assign500);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(true, r, l);
                    }

                    }
                    break;
                case 5 :
                    // R.g:127:5: 
                    {
                    if ( state.backtracking==0 ) {
                       v = l;
                    }

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 10, assign_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "assign"


    // $ANTLR start "alter_assign"
    // R.g:130:1: alter_assign returns [ASTNode v] : l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | ) ;
    public final ASTNode alter_assign() throws RecognitionException {
        ASTNode v = null;
        int alter_assign_StartIndex = input.index();
        Token a=null;
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return v; }
            // R.g:131:2: (l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | ) )
            // R.g:131:4: l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | )
            {
            pushFollow(FOLLOW_tilde_expr_in_alter_assign528);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;
            // R.g:132:3: ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | )
            int alt12=6;
            alt12 = dfa12.predict(input);
            switch (alt12) {
                case 1 :
                    // R.g:132:5: ( ARROW )=> ARROW n_ r= expr_or_assign
                    {
                    match(input,ARROW,FOLLOW_ARROW_in_alter_assign539); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_alter_assign541);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign545);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(false, l, r);
                    }

                    }
                    break;
                case 2 :
                    // R.g:133:5: ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign
                    {
                    match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_alter_assign557); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_alter_assign559);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign563);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = AssignVariable.create(true, l, r);
                    }

                    }
                    break;
                case 3 :
                    // R.g:134:5: ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign
                    {
                    a=(Token)match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_alter_assign577); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_alter_assign579);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign583);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(false, r, l);
                    }

                    }
                    break;
                case 4 :
                    // R.g:135:5: ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign
                    {
                    a=(Token)match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_alter_assign597); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_alter_assign599);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign603);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = AssignVariable.create(true, r, l);
                    }

                    }
                    break;
                case 5 :
                    // R.g:136:5: ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign
                    {
                    a=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_alter_assign617); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_alter_assign619);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign623);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = AssignVariable.create(false, l, r);
                    }

                    }
                    break;
                case 6 :
                    // R.g:137:5: 
                    {
                    if ( state.backtracking==0 ) {
                       v = l;
                    }

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 11, alter_assign_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "alter_assign"


    // $ANTLR start "if_expr"
    // R.g:140:1: if_expr returns [ASTNode v] : IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) | ) ;
    public final ASTNode if_expr() throws RecognitionException {
        ASTNode v = null;
        int if_expr_StartIndex = input.index();
        ASTNode cond = null;

        ASTNode t = null;

        ASTNode f = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return v; }
            // R.g:141:2: ( IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) | ) )
            // R.g:142:2: IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) | )
            {
            match(input,IF,FOLLOW_IF_in_if_expr650); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_if_expr652);
            n_();

            state._fsp--;
            if (state.failed) return v;
            match(input,LPAR,FOLLOW_LPAR_in_if_expr654); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_if_expr656);
            n_();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_expr_or_assign_in_if_expr660);
            cond=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_n__in_if_expr662);
            n_();

            state._fsp--;
            if (state.failed) return v;
            match(input,RPAR,FOLLOW_RPAR_in_if_expr664); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_if_expr666);
            n_();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_expr_or_assign_in_if_expr670);
            t=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;
            // R.g:143:2: ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) | )
            int alt13=2;
            switch ( input.LA(1) ) {
            case NEWLINE:
            case COMMENT:
                {
                int LA13_1 = input.LA(2);

                if ( (synpred6_R()) ) {
                    alt13=1;
                }
                else if ( (true) ) {
                    alt13=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 1, input);

                    throw nvae;
                }
                }
                break;
            case ELSE:
                {
                int LA13_2 = input.LA(2);

                if ( (synpred6_R()) ) {
                    alt13=1;
                }
                else if ( (true) ) {
                    alt13=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 13, 2, input);

                    throw nvae;
                }
                }
                break;
            case EOF:
            case SEMICOLON:
            case RBRACE:
            case ARROW:
            case SUPER_ARROW:
            case RIGHT_ARROW:
            case SUPER_RIGHT_ARROW:
            case ASSIGN:
            case LPAR:
            case RPAR:
            case COMMA:
            case TILDE:
            case OP:
            case COLON:
            case PLUS:
            case MINUS:
            case FIELD:
            case AT:
            case LBRAKET:
            case RBRAKET:
            case LBB:
            case OR:
            case BITWISEOR:
            case AND:
            case BITWISEAND:
            case GT:
            case GE:
            case LT:
            case LE:
            case EQ:
            case NE:
            case MULT:
            case DIV:
            case MOD:
            case CARRET:
                {
                alt13=2;
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
                    // R.g:143:3: ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    {
                    // R.g:143:14: ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    // R.g:143:58: n_ ELSE n_ f= expr_or_assign
                    {
                    pushFollow(FOLLOW_n__in_if_expr696);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,ELSE,FOLLOW_ELSE_in_if_expr698); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_if_expr700);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_or_assign_in_if_expr704);
                    f=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = If.create(cond, t, f);
                    }

                    }


                    }
                    break;
                case 2 :
                    // R.g:144:7: 
                    {
                    if ( state.backtracking==0 ) {
                      v = If.create(cond, t);
                    }

                    }
                    break;

            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode c = null;

        ASTNode body = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return v; }
            // R.g:148:2: ( WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:148:4: WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign
            {
            match(input,WHILE,FOLLOW_WHILE_in_while_expr732); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_while_expr734);
            n_();

            state._fsp--;
            if (state.failed) return v;
            match(input,LPAR,FOLLOW_LPAR_in_while_expr736); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_while_expr738);
            n_();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_expr_or_assign_in_while_expr742);
            c=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_n__in_while_expr744);
            n_();

            state._fsp--;
            if (state.failed) return v;
            match(input,RPAR,FOLLOW_RPAR_in_while_expr746); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_while_expr748);
            n_();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_expr_or_assign_in_while_expr752);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = Loop.create(c, body); 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 13, while_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "while_expr"


    // $ANTLR start "for_expr"
    // R.g:150:1: for_expr returns [ASTNode v] : FOR n_ LPAR n_ i= ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final ASTNode for_expr() throws RecognitionException {
        ASTNode v = null;
        int for_expr_StartIndex = input.index();
        Token i=null;
        ASTNode in = null;

        ASTNode body = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return v; }
            // R.g:151:2: ( FOR n_ LPAR n_ i= ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:151:4: FOR n_ LPAR n_ i= ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign
            {
            match(input,FOR,FOLLOW_FOR_in_for_expr768); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_for_expr770);
            n_();

            state._fsp--;
            if (state.failed) return v;
            match(input,LPAR,FOLLOW_LPAR_in_for_expr772); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_for_expr774);
            n_();

            state._fsp--;
            if (state.failed) return v;
            i=(Token)match(input,ID,FOLLOW_ID_in_for_expr778); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_for_expr780);
            n_();

            state._fsp--;
            if (state.failed) return v;
            match(input,IN,FOLLOW_IN_in_for_expr782); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_for_expr784);
            n_();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_expr_or_assign_in_for_expr788);
            in=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_n__in_for_expr790);
            n_();

            state._fsp--;
            if (state.failed) return v;
            match(input,RPAR,FOLLOW_RPAR_in_for_expr792); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_for_expr794);
            n_();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_expr_or_assign_in_for_expr798);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = Loop.create((i!=null?i.getText():null), in, body); 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode body = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return v; }
            // R.g:154:2: ( REPEAT n_ body= expr_or_assign )
            // R.g:154:4: REPEAT n_ body= expr_or_assign
            {
            match(input,REPEAT,FOLLOW_REPEAT_in_repeat_expr815); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_repeat_expr817);
            n_();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_expr_or_assign_in_repeat_expr821);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
              v = Loop.create(body); 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode body = null;


         ArgumentList l = new ArgumentList.Default(); 
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return v; }
            // R.g:158:2: ( FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign )
            // R.g:158:4: FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function842); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_function844);
            n_();

            state._fsp--;
            if (state.failed) return v;
            match(input,LPAR,FOLLOW_LPAR_in_function846); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_function849);
            n_();

            state._fsp--;
            if (state.failed) return v;
            // R.g:158:25: ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )?
            int alt15=2;
            int LA15_0 = input.LA(1);

            if ( (LA15_0==ID||(LA15_0>=VARIATIC && LA15_0<=DD)) ) {
                alt15=1;
            }
            switch (alt15) {
                case 1 :
                    // R.g:158:26: par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_
                    {
                    pushFollow(FOLLOW_par_decl_in_function852);
                    par_decl(l);

                    state._fsp--;
                    if (state.failed) return v;
                    // R.g:158:38: ( n_ COMMA n_ par_decl[l] )*
                    loop14:
                    do {
                        int alt14=2;
                        alt14 = dfa14.predict(input);
                        switch (alt14) {
                    	case 1 :
                    	    // R.g:158:39: n_ COMMA n_ par_decl[l]
                    	    {
                    	    pushFollow(FOLLOW_n__in_function856);
                    	    n_();

                    	    state._fsp--;
                    	    if (state.failed) return v;
                    	    match(input,COMMA,FOLLOW_COMMA_in_function858); if (state.failed) return v;
                    	    pushFollow(FOLLOW_n__in_function860);
                    	    n_();

                    	    state._fsp--;
                    	    if (state.failed) return v;
                    	    pushFollow(FOLLOW_par_decl_in_function862);
                    	    par_decl(l);

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    }
                    	    break;

                    	default :
                    	    break loop14;
                        }
                    } while (true);

                    pushFollow(FOLLOW_n__in_function867);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }

            match(input,RPAR,FOLLOW_RPAR_in_function871); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_function873);
            n_();

            state._fsp--;
            if (state.failed) return v;
            pushFollow(FOLLOW_expr_or_assign_in_function877);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = Function.create(l, body); 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 16, function_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "function"


    // $ANTLR start "par_decl"
    // R.g:160:1: par_decl[ArgumentList l] : (i= ID | i= ID n_ ASSIGN n_ e= expr | v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr );
    public final void par_decl(ArgumentList l) throws RecognitionException {
        int par_decl_StartIndex = input.index();
        Token i=null;
        Token v=null;
        ASTNode e = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }
            // R.g:161:2: (i= ID | i= ID n_ ASSIGN n_ e= expr | v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr )
            int alt16=6;
            alt16 = dfa16.predict(input);
            switch (alt16) {
                case 1 :
                    // R.g:161:4: i= ID
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_par_decl894); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add((i!=null?i.getText():null), null); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:162:4: i= ID n_ ASSIGN n_ e= expr
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_par_decl904); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_par_decl906);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl908); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_par_decl910);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expr_in_par_decl914);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add((i!=null?i.getText():null), e); 
                    }

                    }
                    break;
                case 3 :
                    // R.g:163:4: v= VARIATIC
                    {
                    v=(Token)match(input,VARIATIC,FOLLOW_VARIATIC_in_par_decl923); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add((v!=null?v.getText():null), null); 
                    }

                    }
                    break;
                case 4 :
                    // R.g:168:5: VARIATIC n_ ASSIGN n_ expr
                    {
                    match(input,VARIATIC,FOLLOW_VARIATIC_in_par_decl973); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_par_decl975);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl977); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_par_decl979);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expr_in_par_decl981);
                    expr();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // R.g:169:5: DD
                    {
                    match(input,DD,FOLLOW_DD_in_par_decl987); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // R.g:170:5: DD n_ ASSIGN n_ expr
                    {
                    match(input,DD,FOLLOW_DD_in_par_decl993); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_par_decl995);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl997); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_par_decl999);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expr_in_par_decl1001);
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
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return v; }
            // R.g:173:2: (l= or_expr ( ( ( TILDE )=> TILDE n_ r= or_expr ) )* )
            // R.g:173:4: l= or_expr ( ( ( TILDE )=> TILDE n_ r= or_expr ) )*
            {
            pushFollow(FOLLOW_or_expr_in_tilde_expr1017);
            l=or_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:174:2: ( ( ( TILDE )=> TILDE n_ r= or_expr ) )*
            loop17:
            do {
                int alt17=2;
                int LA17_0 = input.LA(1);

                if ( (LA17_0==TILDE) ) {
                    int LA17_2 = input.LA(2);

                    if ( (synpred7_R()) ) {
                        alt17=1;
                    }


                }


                switch (alt17) {
            	case 1 :
            	    // R.g:174:4: ( ( TILDE )=> TILDE n_ r= or_expr )
            	    {
            	    // R.g:174:4: ( ( TILDE )=> TILDE n_ r= or_expr )
            	    // R.g:174:5: ( TILDE )=> TILDE n_ r= or_expr
            	    {
            	    match(input,TILDE,FOLLOW_TILDE_in_tilde_expr1029); if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_tilde_expr1031);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_or_expr_in_tilde_expr1035);
            	    r=or_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	      v = BinaryOperation.create(BinaryOperator.ADD, v, r);
            	    }

            	    }


            	    }
            	    break;

            	default :
            	    break loop17;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return v; }
            // R.g:177:2: (l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )* )
            // R.g:177:4: l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )*
            {
            pushFollow(FOLLOW_and_expr_in_or_expr1057);
            l=and_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:178:2: ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )*
            loop18:
            do {
                int alt18=2;
                int LA18_0 = input.LA(1);

                if ( (LA18_0==OR) ) {
                    int LA18_2 = input.LA(2);

                    if ( (synpred8_R()) ) {
                        alt18=1;
                    }


                }
                else if ( (LA18_0==BITWISEOR) ) {
                    int LA18_3 = input.LA(2);

                    if ( (synpred8_R()) ) {
                        alt18=1;
                    }


                }


                switch (alt18) {
            	case 1 :
            	    // R.g:178:3: ( ( or_operator )=>op= or_operator n_ r= and_expr )
            	    {
            	    // R.g:178:3: ( ( or_operator )=>op= or_operator n_ r= and_expr )
            	    // R.g:178:4: ( or_operator )=>op= or_operator n_ r= and_expr
            	    {
            	    pushFollow(FOLLOW_or_operator_in_or_expr1070);
            	    op=or_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_or_expr1072);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_and_expr_in_or_expr1076);
            	    r=and_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	      v = BinaryOperation.create(op, v, r);
            	    }

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
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return v; }
            // R.g:181:2: (l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )* )
            // R.g:181:4: l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )*
            {
            pushFollow(FOLLOW_not_expr_in_and_expr1098);
            l=not_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:182:5: ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )*
            loop19:
            do {
                int alt19=2;
                int LA19_0 = input.LA(1);

                if ( (LA19_0==AND) ) {
                    int LA19_2 = input.LA(2);

                    if ( (synpred9_R()) ) {
                        alt19=1;
                    }


                }
                else if ( (LA19_0==BITWISEAND) ) {
                    int LA19_3 = input.LA(2);

                    if ( (synpred9_R()) ) {
                        alt19=1;
                    }


                }


                switch (alt19) {
            	case 1 :
            	    // R.g:182:6: ( ( and_operator )=>op= and_operator n_ r= not_expr )
            	    {
            	    // R.g:182:6: ( ( and_operator )=>op= and_operator n_ r= not_expr )
            	    // R.g:182:7: ( and_operator )=>op= and_operator n_ r= not_expr
            	    {
            	    pushFollow(FOLLOW_and_operator_in_and_expr1114);
            	    op=and_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_and_expr1116);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_not_expr_in_and_expr1120);
            	    r=not_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	      v = BinaryOperation.create(op, v, r);
            	    }

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
            if ( state.backtracking>0 ) { memoize(input, 20, and_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "and_expr"


    // $ANTLR start "not_expr"
    // R.g:184:1: not_expr returns [ASTNode v] : ( NOT n_ l= not_expr | b= comp_expr );
    public final ASTNode not_expr() throws RecognitionException {
        ASTNode v = null;
        int not_expr_StartIndex = input.index();
        ASTNode l = null;

        ASTNode b = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return v; }
            // R.g:185:2: ( NOT n_ l= not_expr | b= comp_expr )
            int alt20=2;
            int LA20_0 = input.LA(1);

            if ( (LA20_0==NOT) ) {
                alt20=1;
            }
            else if ( ((LA20_0>=NEXT && LA20_0<=LBRACE)||(LA20_0>=IF && LA20_0<=LPAR)||(LA20_0>=WHILE && LA20_0<=ID)||(LA20_0>=REPEAT && LA20_0<=FUNCTION)||(LA20_0>=VARIATIC && LA20_0<=TILDE)||(LA20_0>=PLUS && LA20_0<=MINUS)||LA20_0==NULL||(LA20_0>=INTEGER && LA20_0<=NA)) ) {
                alt20=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 20, 0, input);

                throw nvae;
            }
            switch (alt20) {
                case 1 :
                    // R.g:185:4: NOT n_ l= not_expr
                    {
                    match(input,NOT,FOLLOW_NOT_in_not_expr1140); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_not_expr1142);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_not_expr_in_not_expr1146);
                    l=not_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = UnaryOperation.create(UnaryOperator.NOT, l);
                    }

                    }
                    break;
                case 2 :
                    // R.g:186:4: b= comp_expr
                    {
                    pushFollow(FOLLOW_comp_expr_in_not_expr1155);
                    b=comp_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v =b; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return v; }
            // R.g:189:2: (l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )* )
            // R.g:189:4: l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )*
            {
            pushFollow(FOLLOW_add_expr_in_comp_expr1173);
            l=add_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:190:5: ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )*
            loop21:
            do {
                int alt21=2;
                switch ( input.LA(1) ) {
                case GT:
                    {
                    int LA21_2 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt21=1;
                    }


                    }
                    break;
                case GE:
                    {
                    int LA21_3 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt21=1;
                    }


                    }
                    break;
                case LT:
                    {
                    int LA21_4 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt21=1;
                    }


                    }
                    break;
                case LE:
                    {
                    int LA21_5 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt21=1;
                    }


                    }
                    break;
                case EQ:
                    {
                    int LA21_6 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt21=1;
                    }


                    }
                    break;
                case NE:
                    {
                    int LA21_7 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt21=1;
                    }


                    }
                    break;

                }

                switch (alt21) {
            	case 1 :
            	    // R.g:190:6: ( ( comp_operator )=>op= comp_operator n_ r= add_expr )
            	    {
            	    // R.g:190:6: ( ( comp_operator )=>op= comp_operator n_ r= add_expr )
            	    // R.g:190:7: ( comp_operator )=>op= comp_operator n_ r= add_expr
            	    {
            	    pushFollow(FOLLOW_comp_operator_in_comp_expr1189);
            	    op=comp_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_comp_expr1191);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_add_expr_in_comp_expr1195);
            	    r=add_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	       v = BinaryOperation.create(op, v, r);
            	    }

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
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return v; }
            // R.g:193:2: (l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )* )
            // R.g:193:4: l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            {
            pushFollow(FOLLOW_mult_expr_in_add_expr1220);
            l=mult_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:194:3: ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            loop22:
            do {
                int alt22=2;
                int LA22_0 = input.LA(1);

                if ( (LA22_0==PLUS) ) {
                    int LA22_2 = input.LA(2);

                    if ( (synpred11_R()) ) {
                        alt22=1;
                    }


                }
                else if ( (LA22_0==MINUS) ) {
                    int LA22_3 = input.LA(2);

                    if ( (synpred11_R()) ) {
                        alt22=1;
                    }


                }


                switch (alt22) {
            	case 1 :
            	    // R.g:194:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    {
            	    // R.g:194:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    // R.g:194:5: ( add_operator )=>op= add_operator n_ r= mult_expr
            	    {
            	    pushFollow(FOLLOW_add_operator_in_add_expr1234);
            	    op=add_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_add_expr1236);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_mult_expr_in_add_expr1240);
            	    r=mult_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	       v = BinaryOperation.create(op, v, r);
            	    }

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
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return v; }
            // R.g:197:2: (l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )* )
            // R.g:197:4: l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            {
            pushFollow(FOLLOW_operator_expr_in_mult_expr1262);
            l=operator_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:198:2: ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            loop23:
            do {
                int alt23=2;
                switch ( input.LA(1) ) {
                case MULT:
                    {
                    int LA23_2 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case DIV:
                    {
                    int LA23_3 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case MOD:
                    {
                    int LA23_4 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt23=1;
                    }


                    }
                    break;

                }

                switch (alt23) {
            	case 1 :
            	    // R.g:198:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    {
            	    // R.g:198:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    // R.g:198:4: ( mult_operator )=>op= mult_operator n_ r= operator_expr
            	    {
            	    pushFollow(FOLLOW_mult_operator_in_mult_expr1275);
            	    op=mult_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_mult_expr1277);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_operator_expr_in_mult_expr1281);
            	    r=operator_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	       v = BinaryOperation.create(op, v, r);
            	    }

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
            if ( state.backtracking>0 ) { memoize(input, 24, mult_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "mult_expr"


    // $ANTLR start "operator_expr"
    // R.g:200:1: operator_expr returns [ASTNode v] : l= colon_expr ( ( ( OP )=>op= OP n_ r= colon_expr ) )* ;
    public final ASTNode operator_expr() throws RecognitionException {
        ASTNode v = null;
        int operator_expr_StartIndex = input.index();
        Token op=null;
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return v; }
            // R.g:201:2: (l= colon_expr ( ( ( OP )=>op= OP n_ r= colon_expr ) )* )
            // R.g:201:4: l= colon_expr ( ( ( OP )=>op= OP n_ r= colon_expr ) )*
            {
            pushFollow(FOLLOW_colon_expr_in_operator_expr1303);
            l=colon_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:202:2: ( ( ( OP )=>op= OP n_ r= colon_expr ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==OP) ) {
                    int LA24_2 = input.LA(2);

                    if ( (synpred13_R()) ) {
                        alt24=1;
                    }


                }


                switch (alt24) {
            	case 1 :
            	    // R.g:202:3: ( ( OP )=>op= OP n_ r= colon_expr )
            	    {
            	    // R.g:202:3: ( ( OP )=>op= OP n_ r= colon_expr )
            	    // R.g:202:4: ( OP )=>op= OP n_ r= colon_expr
            	    {
            	    op=(Token)match(input,OP,FOLLOW_OP_in_operator_expr1316); if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_operator_expr1318);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_colon_expr_in_operator_expr1322);
            	    r=colon_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	       v = null; 
            	    }

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
            if ( state.backtracking>0 ) { memoize(input, 25, operator_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "operator_expr"


    // $ANTLR start "colon_expr"
    // R.g:204:1: colon_expr returns [ASTNode v] : l= power_expr ( ( ( COLON )=>op= COLON n_ r= power_expr ) )* ;
    public final ASTNode colon_expr() throws RecognitionException {
        ASTNode v = null;
        int colon_expr_StartIndex = input.index();
        Token op=null;
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return v; }
            // R.g:205:2: (l= power_expr ( ( ( COLON )=>op= COLON n_ r= power_expr ) )* )
            // R.g:205:4: l= power_expr ( ( ( COLON )=>op= COLON n_ r= power_expr ) )*
            {
            pushFollow(FOLLOW_power_expr_in_colon_expr1349);
            l=power_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:206:2: ( ( ( COLON )=>op= COLON n_ r= power_expr ) )*
            loop25:
            do {
                int alt25=2;
                int LA25_0 = input.LA(1);

                if ( (LA25_0==COLON) ) {
                    int LA25_2 = input.LA(2);

                    if ( (synpred14_R()) ) {
                        alt25=1;
                    }


                }


                switch (alt25) {
            	case 1 :
            	    // R.g:206:3: ( ( COLON )=>op= COLON n_ r= power_expr )
            	    {
            	    // R.g:206:3: ( ( COLON )=>op= COLON n_ r= power_expr )
            	    // R.g:206:4: ( COLON )=>op= COLON n_ r= power_expr
            	    {
            	    op=(Token)match(input,COLON,FOLLOW_COLON_in_colon_expr1362); if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_colon_expr1364);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_power_expr_in_colon_expr1368);
            	    r=power_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	       v = BinaryOperation.create(BinaryOperator.COLON, v, r);
            	    }

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
            if ( state.backtracking>0 ) { memoize(input, 26, colon_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "colon_expr"


    // $ANTLR start "power_expr"
    // R.g:208:1: power_expr returns [ASTNode v] : l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) | ) ;
    public final ASTNode power_expr() throws RecognitionException {
        ASTNode v = null;
        int power_expr_StartIndex = input.index();
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return v; }
            // R.g:209:2: (l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) | ) )
            // R.g:209:4: l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) | )
            {
            pushFollow(FOLLOW_unary_expression_in_power_expr1390);
            l=unary_expression();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
              v =l;
            }
            // R.g:210:5: ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) | )
            int alt26=2;
            int LA26_0 = input.LA(1);

            if ( (LA26_0==CARRET) ) {
                int LA26_1 = input.LA(2);

                if ( (synpred15_R()) ) {
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
            else if ( (LA26_0==EOF||(LA26_0>=NEWLINE && LA26_0<=SEMICOLON)||(LA26_0>=RBRACE && LA26_0<=ASSIGN)||(LA26_0>=LPAR && LA26_0<=ELSE)||LA26_0==COMMA||LA26_0==TILDE||(LA26_0>=OP && LA26_0<=LBB)||(LA26_0>=OR && LA26_0<=MOD)) ) {
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
                    // R.g:210:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    {
                    // R.g:210:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    // R.g:210:7: ( power_operator )=>op= power_operator n_ r= power_expr
                    {
                    pushFollow(FOLLOW_power_operator_in_power_expr1406);
                    op=power_operator();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_power_expr1408);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_power_expr_in_power_expr1412);
                    r=power_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = BinaryOperation.create(op, l, r);
                    }

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
            if ( state.backtracking>0 ) { memoize(input, 27, power_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "power_expr"


    // $ANTLR start "unary_expression"
    // R.g:213:1: unary_expression returns [ASTNode v] : ( PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression | b= basic_expr );
    public final ASTNode unary_expression() throws RecognitionException {
        ASTNode v = null;
        int unary_expression_StartIndex = input.index();
        ASTNode l = null;

        ASTNode b = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return v; }
            // R.g:214:2: ( PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression | b= basic_expr )
            int alt27=4;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt27=1;
                }
                break;
            case MINUS:
                {
                alt27=2;
                }
                break;
            case TILDE:
                {
                alt27=3;
                }
                break;
            case NEXT:
            case BREAK:
            case LBRACE:
            case IF:
            case LPAR:
            case WHILE:
            case FOR:
            case ID:
            case REPEAT:
            case FUNCTION:
            case VARIATIC:
            case DD:
            case NULL:
            case INTEGER:
            case DOUBLE:
            case COMPLEX:
            case STRING:
            case TRUE:
            case FALSE:
            case NA:
                {
                alt27=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 27, 0, input);

                throw nvae;
            }

            switch (alt27) {
                case 1 :
                    // R.g:214:4: PLUS n_ l= unary_expression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unary_expression1441); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_unary_expression1443);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_unary_expression_in_unary_expression1447);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = UnaryOperation.create(UnaryOperator.PLUS, l);
                    }

                    }
                    break;
                case 2 :
                    // R.g:215:4: MINUS n_ l= unary_expression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unary_expression1454); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_unary_expression1456);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_unary_expression_in_unary_expression1460);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = UnaryOperation.create(UnaryOperator.MINUS, l);
                    }

                    }
                    break;
                case 3 :
                    // R.g:216:4: TILDE n_ l= unary_expression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unary_expression1467); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_unary_expression1469);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_unary_expression_in_unary_expression1473);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = UnaryOperation.create(UnaryOperator.MODEL, l);
                    }

                    }
                    break;
                case 4 :
                    // R.g:217:4: b= basic_expr
                    {
                    pushFollow(FOLLOW_basic_expr_in_unary_expression1482);
                    b=basic_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v =b; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
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
        ASTNode lhs = null;

        ASTNode subset = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return v; }
            // R.g:220:2: (lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) )
            // R.g:220:4: lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            {
            pushFollow(FOLLOW_simple_expr_in_basic_expr1500);
            lhs=simple_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = lhs; 
            }
            // R.g:221:2: ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            int alt29=2;
            alt29 = dfa29.predict(input);
            switch (alt29) {
                case 1 :
                    // R.g:221:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    {
                    // R.g:221:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    int cnt28=0;
                    loop28:
                    do {
                        int alt28=2;
                        switch ( input.LA(1) ) {
                        case FIELD:
                            {
                            int LA28_2 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt28=1;
                            }


                            }
                            break;
                        case AT:
                            {
                            int LA28_3 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt28=1;
                            }


                            }
                            break;
                        case LBRAKET:
                            {
                            int LA28_4 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt28=1;
                            }


                            }
                            break;
                        case LBB:
                            {
                            int LA28_5 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt28=1;
                            }


                            }
                            break;
                        case LPAR:
                            {
                            int LA28_6 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt28=1;
                            }


                            }
                            break;

                        }

                        switch (alt28) {
                    	case 1 :
                    	    // R.g:221:4: ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v]
                    	    {
                    	    pushFollow(FOLLOW_expr_subset_in_basic_expr1521);
                    	    subset=expr_subset(v);

                    	    state._fsp--;
                    	    if (state.failed) return v;
                    	    if ( state.backtracking==0 ) {
                    	       v = subset; 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt28 >= 1 ) break loop28;
                    	    if (state.backtracking>0) {state.failed=true; return v;}
                                EarlyExitException eee =
                                    new EarlyExitException(28, input);
                                throw eee;
                        }
                        cnt28++;
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
        Token name = null;

        ArgumentList subset = null;

        ArgumentList subscript = null;

        ArgumentList a = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return v; }
            // R.g:224:5: ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= args RBRAKET ) | ( LBB subscript= args RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) )
            int alt30=5;
            switch ( input.LA(1) ) {
            case FIELD:
                {
                alt30=1;
                }
                break;
            case AT:
                {
                alt30=2;
                }
                break;
            case LBRAKET:
                {
                alt30=3;
                }
                break;
            case LBB:
                {
                alt30=4;
                }
                break;
            case LPAR:
                {
                alt30=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 30, 0, input);

                throw nvae;
            }

            switch (alt30) {
                case 1 :
                    // R.g:224:7: ( FIELD n_ name= id )
                    {
                    // R.g:224:7: ( FIELD n_ name= id )
                    // R.g:224:8: FIELD n_ name= id
                    {
                    match(input,FIELD,FOLLOW_FIELD_in_expr_subset1554); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_expr_subset1556);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_id_in_expr_subset1560);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }

                    if ( state.backtracking==0 ) {
                       v = FieldAccess.create(FieldOperator.FIELD, i, name.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:225:7: ( AT n_ name= id )
                    {
                    // R.g:225:7: ( AT n_ name= id )
                    // R.g:225:8: AT n_ name= id
                    {
                    match(input,AT,FOLLOW_AT_in_expr_subset1573); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_expr_subset1575);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_id_in_expr_subset1579);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }

                    if ( state.backtracking==0 ) {
                       v = FieldAccess.create(FieldOperator.AT, i, name.getText()); 
                    }

                    }
                    break;
                case 3 :
                    // R.g:226:7: ( LBRAKET subset= args RBRAKET )
                    {
                    // R.g:226:7: ( LBRAKET subset= args RBRAKET )
                    // R.g:226:8: LBRAKET subset= args RBRAKET
                    {
                    match(input,LBRAKET,FOLLOW_LBRAKET_in_expr_subset1593); if (state.failed) return v;
                    pushFollow(FOLLOW_args_in_expr_subset1597);
                    subset=args();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1599); if (state.failed) return v;

                    }

                    if ( state.backtracking==0 ) {
                       v = Call.create(CallOperator.SUBSET, i, subset); 
                    }

                    }
                    break;
                case 4 :
                    // R.g:227:7: ( LBB subscript= args RBRAKET RBRAKET )
                    {
                    // R.g:227:7: ( LBB subscript= args RBRAKET RBRAKET )
                    // R.g:227:8: LBB subscript= args RBRAKET RBRAKET
                    {
                    match(input,LBB,FOLLOW_LBB_in_expr_subset1611); if (state.failed) return v;
                    pushFollow(FOLLOW_args_in_expr_subset1615);
                    subscript=args();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1617); if (state.failed) return v;
                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1619); if (state.failed) return v;

                    }

                    if ( state.backtracking==0 ) {
                       v = Call.create(CallOperator.SUBSCRIPT, i, subscript); 
                    }

                    }
                    break;
                case 5 :
                    // R.g:229:7: ( LPAR a= args RPAR )
                    {
                    // R.g:229:7: ( LPAR a= args RPAR )
                    // R.g:229:8: LPAR a= args RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_expr_subset1636); if (state.failed) return v;
                    pushFollow(FOLLOW_args_in_expr_subset1640);
                    a=args();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,RPAR,FOLLOW_RPAR_in_expr_subset1642); if (state.failed) return v;

                    }

                    if ( state.backtracking==0 ) {
                       v = Call.create(i, a); 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 30, expr_subset_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "expr_subset"


    // $ANTLR start "simple_expr"
    // R.g:232:1: simple_expr returns [ASTNode v] : (i= id | b= bool | DD | NULL | num= number | cstr= conststring | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR | s= sequence | e= expr_wo_assign );
    public final ASTNode simple_expr() throws RecognitionException {
        ASTNode v = null;
        int simple_expr_StartIndex = input.index();
        Token i = null;

        ASTNode b = null;

        ASTNode num = null;

        ASTNode cstr = null;

        ASTNode ea = null;

        ASTNode s = null;

        ASTNode e = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return v; }
            // R.g:233:2: (i= id | b= bool | DD | NULL | num= number | cstr= conststring | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR | s= sequence | e= expr_wo_assign )
            int alt31=11;
            alt31 = dfa31.predict(input);
            switch (alt31) {
                case 1 :
                    // R.g:233:4: i= id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1671);
                    i=id();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = AccessVariable.create(i.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:234:4: b= bool
                    {
                    pushFollow(FOLLOW_bool_in_simple_expr1680);
                    b=bool();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = b; 
                    }

                    }
                    break;
                case 3 :
                    // R.g:235:4: DD
                    {
                    match(input,DD,FOLLOW_DD_in_simple_expr1687); if (state.failed) return v;

                    }
                    break;
                case 4 :
                    // R.g:236:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_simple_expr1692); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Constant.getNull(); 
                    }

                    }
                    break;
                case 5 :
                    // R.g:237:4: num= number
                    {
                    pushFollow(FOLLOW_number_in_simple_expr1701);
                    num=number();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = num; 
                    }

                    }
                    break;
                case 6 :
                    // R.g:238:4: cstr= conststring
                    {
                    pushFollow(FOLLOW_conststring_in_simple_expr1710);
                    cstr=conststring();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = cstr; 
                    }

                    }
                    break;
                case 7 :
                    // R.g:239:4: id NS_GET n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1717);
                    id();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,NS_GET,FOLLOW_NS_GET_in_simple_expr1719); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_simple_expr1721);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_id_in_simple_expr1723);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 8 :
                    // R.g:240:4: id NS_GET_INT n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1728);
                    id();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,NS_GET_INT,FOLLOW_NS_GET_INT_in_simple_expr1730); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_simple_expr1732);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_id_in_simple_expr1734);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 9 :
                    // R.g:241:4: LPAR n_ ea= expr_or_assign n_ RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_simple_expr1739); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_simple_expr1741);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_or_assign_in_simple_expr1747);
                    ea=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_simple_expr1749);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,RPAR,FOLLOW_RPAR_in_simple_expr1751); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = ea; 
                    }

                    }
                    break;
                case 10 :
                    // R.g:242:4: s= sequence
                    {
                    pushFollow(FOLLOW_sequence_in_simple_expr1762);
                    s=sequence();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = s;
                    }

                    }
                    break;
                case 11 :
                    // R.g:243:4: e= expr_wo_assign
                    {
                    pushFollow(FOLLOW_expr_wo_assign_in_simple_expr1773);
                    e=expr_wo_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = e; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 31, simple_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "simple_expr"


    // $ANTLR start "number"
    // R.g:245:1: number returns [ASTNode n] : (i= INTEGER | d= DOUBLE | c= COMPLEX );
    public final ASTNode number() throws RecognitionException {
        ASTNode n = null;
        int number_StartIndex = input.index();
        Token i=null;
        Token d=null;
        Token c=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return n; }
            // R.g:246:5: (i= INTEGER | d= DOUBLE | c= COMPLEX )
            int alt32=3;
            switch ( input.LA(1) ) {
            case INTEGER:
                {
                alt32=1;
                }
                break;
            case DOUBLE:
                {
                alt32=2;
                }
                break;
            case COMPLEX:
                {
                alt32=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return n;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;
            }

            switch (alt32) {
                case 1 :
                    // R.g:246:7: i= INTEGER
                    {
                    i=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_number1794); if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = Constant.createIntConstant((i!=null?i.getText():null)); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:247:7: d= DOUBLE
                    {
                    d=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_number1806); if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = Constant.createDoubleConstant((d!=null?d.getText():null)); 
                    }

                    }
                    break;
                case 3 :
                    // R.g:248:7: c= COMPLEX
                    {
                    c=(Token)match(input,COMPLEX,FOLLOW_COMPLEX_in_number1818); if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = Constant.createComplexConstant((c!=null?c.getText():null)); 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 32, number_StartIndex); }
        }
        return n;
    }
    // $ANTLR end "number"


    // $ANTLR start "conststring"
    // R.g:250:1: conststring returns [ASTNode n] : s= STRING ;
    public final ASTNode conststring() throws RecognitionException {
        ASTNode n = null;
        int conststring_StartIndex = input.index();
        Token s=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return n; }
            // R.g:251:5: (s= STRING )
            // R.g:251:7: s= STRING
            {
            s=(Token)match(input,STRING,FOLLOW_STRING_in_conststring1842); if (state.failed) return n;
            if ( state.backtracking==0 ) {
               n = Constant.createStringConstant((s!=null?s.getText():null)); 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 33, conststring_StartIndex); }
        }
        return n;
    }
    // $ANTLR end "conststring"


    // $ANTLR start "id"
    // R.g:253:1: id returns [Token t] : (i= ID | v= VARIATIC );
    public final Token id() throws RecognitionException {
        Token t = null;
        int id_StartIndex = input.index();
        Token i=null;
        Token v=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return t; }
            // R.g:254:5: (i= ID | v= VARIATIC )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==ID) ) {
                alt33=1;
            }
            else if ( (LA33_0==VARIATIC) ) {
                alt33=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return t;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;
            }
            switch (alt33) {
                case 1 :
                    // R.g:254:7: i= ID
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_id1866); if (state.failed) return t;
                    if ( state.backtracking==0 ) {
                       t = i; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:255:7: v= VARIATIC
                    {
                    v=(Token)match(input,VARIATIC,FOLLOW_VARIATIC_in_id1878); if (state.failed) return t;
                    if ( state.backtracking==0 ) {
                       t = v; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 34, id_StartIndex); }
        }
        return t;
    }
    // $ANTLR end "id"


    // $ANTLR start "bool"
    // R.g:257:1: bool returns [ASTNode v] : ( TRUE | FALSE | NA );
    public final ASTNode bool() throws RecognitionException {
        ASTNode v = null;
        int bool_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return v; }
            // R.g:258:5: ( TRUE | FALSE | NA )
            int alt34=3;
            switch ( input.LA(1) ) {
            case TRUE:
                {
                alt34=1;
                }
                break;
            case FALSE:
                {
                alt34=2;
                }
                break;
            case NA:
                {
                alt34=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;
            }

            switch (alt34) {
                case 1 :
                    // R.g:258:7: TRUE
                    {
                    match(input,TRUE,FOLLOW_TRUE_in_bool1900); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = Constant.createBoolConstant(1); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:259:7: FALSE
                    {
                    match(input,FALSE,FOLLOW_FALSE_in_bool1910); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = Constant.createBoolConstant(0); 
                    }

                    }
                    break;
                case 3 :
                    // R.g:260:7: NA
                    {
                    match(input,NA,FOLLOW_NA_in_bool1920); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = Constant.createBoolConstant(RLogical.NA); 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 35, bool_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "bool"


    // $ANTLR start "or_operator"
    // R.g:262:1: or_operator returns [BinaryOperator v] : ( OR | BITWISEOR );
    public final BinaryOperator or_operator() throws RecognitionException {
        BinaryOperator v = null;
        int or_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return v; }
            // R.g:263:2: ( OR | BITWISEOR )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==OR) ) {
                alt35=1;
            }
            else if ( (LA35_0==BITWISEOR) ) {
                alt35=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // R.g:263:4: OR
                    {
                    match(input,OR,FOLLOW_OR_in_or_operator1939); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.OR; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:264:5: BITWISEOR
                    {
                    match(input,BITWISEOR,FOLLOW_BITWISEOR_in_or_operator1956); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.BITWISEOR; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 36, or_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "or_operator"


    // $ANTLR start "and_operator"
    // R.g:265:1: and_operator returns [BinaryOperator v] : ( AND | BITWISEAND );
    public final BinaryOperator and_operator() throws RecognitionException {
        BinaryOperator v = null;
        int and_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return v; }
            // R.g:266:2: ( AND | BITWISEAND )
            int alt36=2;
            int LA36_0 = input.LA(1);

            if ( (LA36_0==AND) ) {
                alt36=1;
            }
            else if ( (LA36_0==BITWISEAND) ) {
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
                    // R.g:266:4: AND
                    {
                    match(input,AND,FOLLOW_AND_in_and_operator1972); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.AND; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:267:4: BITWISEAND
                    {
                    match(input,BITWISEAND,FOLLOW_BITWISEAND_in_and_operator1988); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.BITWISEAND; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 37, and_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "and_operator"


    // $ANTLR start "comp_operator"
    // R.g:268:1: comp_operator returns [BinaryOperator v] : ( GT | GE | LT | LE | EQ | NE );
    public final BinaryOperator comp_operator() throws RecognitionException {
        BinaryOperator v = null;
        int comp_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return v; }
            // R.g:269:2: ( GT | GE | LT | LE | EQ | NE )
            int alt37=6;
            switch ( input.LA(1) ) {
            case GT:
                {
                alt37=1;
                }
                break;
            case GE:
                {
                alt37=2;
                }
                break;
            case LT:
                {
                alt37=3;
                }
                break;
            case LE:
                {
                alt37=4;
                }
                break;
            case EQ:
                {
                alt37=5;
                }
                break;
            case NE:
                {
                alt37=6;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 37, 0, input);

                throw nvae;
            }

            switch (alt37) {
                case 1 :
                    // R.g:269:4: GT
                    {
                    match(input,GT,FOLLOW_GT_in_comp_operator2004); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.GT; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:270:4: GE
                    {
                    match(input,GE,FOLLOW_GE_in_comp_operator2011); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.GE; 
                    }

                    }
                    break;
                case 3 :
                    // R.g:271:4: LT
                    {
                    match(input,LT,FOLLOW_LT_in_comp_operator2018); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.LT; 
                    }

                    }
                    break;
                case 4 :
                    // R.g:272:4: LE
                    {
                    match(input,LE,FOLLOW_LE_in_comp_operator2025); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.LE; 
                    }

                    }
                    break;
                case 5 :
                    // R.g:273:4: EQ
                    {
                    match(input,EQ,FOLLOW_EQ_in_comp_operator2032); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.EQ; 
                    }

                    }
                    break;
                case 6 :
                    // R.g:274:4: NE
                    {
                    match(input,NE,FOLLOW_NE_in_comp_operator2039); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.NE; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 38, comp_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "comp_operator"


    // $ANTLR start "add_operator"
    // R.g:275:1: add_operator returns [BinaryOperator v] : ( PLUS | MINUS );
    public final BinaryOperator add_operator() throws RecognitionException {
        BinaryOperator v = null;
        int add_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return v; }
            // R.g:276:2: ( PLUS | MINUS )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==PLUS) ) {
                alt38=1;
            }
            else if ( (LA38_0==MINUS) ) {
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
                    // R.g:276:4: PLUS
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_add_operator2053); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.ADD; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:277:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_add_operator2060); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.SUB; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 39, add_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "add_operator"


    // $ANTLR start "mult_operator"
    // R.g:278:1: mult_operator returns [BinaryOperator v] : ( MULT | DIV | MOD );
    public final BinaryOperator mult_operator() throws RecognitionException {
        BinaryOperator v = null;
        int mult_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return v; }
            // R.g:279:2: ( MULT | DIV | MOD )
            int alt39=3;
            switch ( input.LA(1) ) {
            case MULT:
                {
                alt39=1;
                }
                break;
            case DIV:
                {
                alt39=2;
                }
                break;
            case MOD:
                {
                alt39=3;
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
                    // R.g:279:4: MULT
                    {
                    match(input,MULT,FOLLOW_MULT_in_mult_operator2075); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.MULT; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:280:4: DIV
                    {
                    match(input,DIV,FOLLOW_DIV_in_mult_operator2082); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.DIV; 
                    }

                    }
                    break;
                case 3 :
                    // R.g:281:4: MOD
                    {
                    match(input,MOD,FOLLOW_MOD_in_mult_operator2090); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.MOD; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 40, mult_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "mult_operator"


    // $ANTLR start "power_operator"
    // R.g:282:1: power_operator returns [BinaryOperator v] : CARRET ;
    public final BinaryOperator power_operator() throws RecognitionException {
        BinaryOperator v = null;
        int power_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return v; }
            // R.g:283:2: ( CARRET )
            // R.g:283:4: CARRET
            {
            match(input,CARRET,FOLLOW_CARRET_in_power_operator2105); if (state.failed) return v;
            if ( state.backtracking==0 ) {
              v = BinaryOperator.POW; 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 41, power_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "power_operator"


    // $ANTLR start "args"
    // R.g:285:1: args returns [ArgumentList v] : ( n_ arg_expr[v] )? n_ ( COMMA ( | n_ arg_expr[v] ) n_ )* ;
    public final ArgumentList args() throws RecognitionException {
        ArgumentList v = null;
        int args_StartIndex = input.index();
         v = new ArgumentList.Default(); 
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return v; }
            // R.g:287:5: ( ( n_ arg_expr[v] )? n_ ( COMMA ( | n_ arg_expr[v] ) n_ )* )
            // R.g:287:7: ( n_ arg_expr[v] )? n_ ( COMMA ( | n_ arg_expr[v] ) n_ )*
            {
            // R.g:287:7: ( n_ arg_expr[v] )?
            int alt40=2;
            alt40 = dfa40.predict(input);
            switch (alt40) {
                case 1 :
                    // R.g:287:8: n_ arg_expr[v]
                    {
                    pushFollow(FOLLOW_n__in_args2130);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_arg_expr_in_args2132);
                    arg_expr(v);

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }

            pushFollow(FOLLOW_n__in_args2137);
            n_();

            state._fsp--;
            if (state.failed) return v;
            // R.g:287:28: ( COMMA ( | n_ arg_expr[v] ) n_ )*
            loop42:
            do {
                int alt42=2;
                int LA42_0 = input.LA(1);

                if ( (LA42_0==COMMA) ) {
                    alt42=1;
                }


                switch (alt42) {
            	case 1 :
            	    // R.g:287:29: COMMA ( | n_ arg_expr[v] ) n_
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_args2140); if (state.failed) return v;
            	    // R.g:287:35: ( | n_ arg_expr[v] )
            	    int alt41=2;
            	    alt41 = dfa41.predict(input);
            	    switch (alt41) {
            	        case 1 :
            	            // R.g:287:37: 
            	            {
            	            if ( state.backtracking==0 ) {
            	               v.add((ASTNode)null); 
            	            }

            	            }
            	            break;
            	        case 2 :
            	            // R.g:287:66: n_ arg_expr[v]
            	            {
            	            pushFollow(FOLLOW_n__in_args2148);
            	            n_();

            	            state._fsp--;
            	            if (state.failed) return v;
            	            pushFollow(FOLLOW_arg_expr_in_args2150);
            	            arg_expr(v);

            	            state._fsp--;
            	            if (state.failed) return v;

            	            }
            	            break;

            	    }

            	    pushFollow(FOLLOW_n__in_args2154);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    }
            	    break;

            	default :
            	    break loop42;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 42, args_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "args"


    // $ANTLR start "arg_expr"
    // R.g:289:1: arg_expr[ArgumentList l] : (e= expr | name= id n_ ASSIGN n_ val= expr | name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );
    public final void arg_expr(ArgumentList l) throws RecognitionException {
        int arg_expr_StartIndex = input.index();
        ASTNode e = null;

        Token name = null;

        ASTNode val = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return ; }
            // R.g:290:2: (e= expr | name= id n_ ASSIGN n_ val= expr | name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN )
            int alt43=5;
            alt43 = dfa43.predict(input);
            switch (alt43) {
                case 1 :
                    // R.g:290:4: e= expr
                    {
                    pushFollow(FOLLOW_expr_in_arg_expr2171);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add(e); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:291:4: name= id n_ ASSIGN n_ val= expr
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2180);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2182);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2184); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2186);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expr_in_arg_expr2190);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add(name.getText(), val); 
                    }

                    }
                    break;
                case 3 :
                    // R.g:292:4: name= id n_ ASSIGN
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2199);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2201);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2203); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add(name.getText(), null); 
                    }

                    }
                    break;
                case 4 :
                    // R.g:293:4: NULL n_ ASSIGN n_ val= expr
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2211); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2213);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2215); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2217);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expr_in_arg_expr2221);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       Utils.nyi(); 
                    }

                    }
                    break;
                case 5 :
                    // R.g:294:4: NULL n_ ASSIGN
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2228); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2230);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2232); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       Utils.nyi(); 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 43, arg_expr_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "arg_expr"

    // $ANTLR start synpred1_R
    public final void synpred1_R_fragment() throws RecognitionException {   
        // R.g:132:5: ( ARROW )
        // R.g:132:6: ARROW
        {
        match(input,ARROW,FOLLOW_ARROW_in_synpred1_R536); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_R

    // $ANTLR start synpred2_R
    public final void synpred2_R_fragment() throws RecognitionException {   
        // R.g:133:5: ( SUPER_ARROW )
        // R.g:133:6: SUPER_ARROW
        {
        match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_synpred2_R554); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_R

    // $ANTLR start synpred3_R
    public final void synpred3_R_fragment() throws RecognitionException {   
        // R.g:134:5: ( RIGHT_ARROW )
        // R.g:134:6: RIGHT_ARROW
        {
        match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_synpred3_R572); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_R

    // $ANTLR start synpred4_R
    public final void synpred4_R_fragment() throws RecognitionException {   
        // R.g:135:5: ( SUPER_RIGHT_ARROW )
        // R.g:135:6: SUPER_RIGHT_ARROW
        {
        match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_synpred4_R592); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_R

    // $ANTLR start synpred5_R
    public final void synpred5_R_fragment() throws RecognitionException {   
        // R.g:136:5: ( ASSIGN )
        // R.g:136:6: ASSIGN
        {
        match(input,ASSIGN,FOLLOW_ASSIGN_in_synpred5_R612); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_R

    // $ANTLR start synpred6_R
    public final void synpred6_R_fragment() throws RecognitionException {   
        // R.g:143:3: ( n_ ELSE )
        // R.g:143:4: n_ ELSE
        {
        pushFollow(FOLLOW_n__in_synpred6_R675);
        n_();

        state._fsp--;
        if (state.failed) return ;
        match(input,ELSE,FOLLOW_ELSE_in_synpred6_R677); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred6_R

    // $ANTLR start synpred7_R
    public final void synpred7_R_fragment() throws RecognitionException {   
        // R.g:174:5: ( TILDE )
        // R.g:174:6: TILDE
        {
        match(input,TILDE,FOLLOW_TILDE_in_synpred7_R1026); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_R

    // $ANTLR start synpred8_R
    public final void synpred8_R_fragment() throws RecognitionException {   
        // R.g:178:4: ( or_operator )
        // R.g:178:5: or_operator
        {
        pushFollow(FOLLOW_or_operator_in_synpred8_R1065);
        or_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_R

    // $ANTLR start synpred9_R
    public final void synpred9_R_fragment() throws RecognitionException {   
        // R.g:182:7: ( and_operator )
        // R.g:182:8: and_operator
        {
        pushFollow(FOLLOW_and_operator_in_synpred9_R1109);
        and_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_R

    // $ANTLR start synpred10_R
    public final void synpred10_R_fragment() throws RecognitionException {   
        // R.g:190:7: ( comp_operator )
        // R.g:190:8: comp_operator
        {
        pushFollow(FOLLOW_comp_operator_in_synpred10_R1184);
        comp_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_R

    // $ANTLR start synpred11_R
    public final void synpred11_R_fragment() throws RecognitionException {   
        // R.g:194:5: ( add_operator )
        // R.g:194:6: add_operator
        {
        pushFollow(FOLLOW_add_operator_in_synpred11_R1229);
        add_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_R

    // $ANTLR start synpred12_R
    public final void synpred12_R_fragment() throws RecognitionException {   
        // R.g:198:4: ( mult_operator )
        // R.g:198:5: mult_operator
        {
        pushFollow(FOLLOW_mult_operator_in_synpred12_R1270);
        mult_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_R

    // $ANTLR start synpred13_R
    public final void synpred13_R_fragment() throws RecognitionException {   
        // R.g:202:4: ( OP )
        // R.g:202:5: OP
        {
        match(input,OP,FOLLOW_OP_in_synpred13_R1311); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_R

    // $ANTLR start synpred14_R
    public final void synpred14_R_fragment() throws RecognitionException {   
        // R.g:206:4: ( COLON )
        // R.g:206:5: COLON
        {
        match(input,COLON,FOLLOW_COLON_in_synpred14_R1357); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_R

    // $ANTLR start synpred15_R
    public final void synpred15_R_fragment() throws RecognitionException {   
        // R.g:210:7: ( power_operator )
        // R.g:210:8: power_operator
        {
        pushFollow(FOLLOW_power_operator_in_synpred15_R1401);
        power_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_R

    // $ANTLR start synpred16_R
    public final void synpred16_R_fragment() throws RecognitionException {   
        // R.g:221:4: ( FIELD | AT | LBRAKET | LBB | LPAR )
        // R.g:
        {
        if ( input.LA(1)==LPAR||(input.LA(1)>=FIELD && input.LA(1)<=LBRAKET)||input.LA(1)==LBB ) {
            input.consume();
            state.errorRecovery=false;state.failed=false;
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
        // R.g:221:76: ( n_ )
        // R.g:221:77: n_
        {
        pushFollow(FOLLOW_n__in_synpred17_R1531);
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


    protected DFA8 dfa8 = new DFA8(this);
    protected DFA12 dfa12 = new DFA12(this);
    protected DFA14 dfa14 = new DFA14(this);
    protected DFA16 dfa16 = new DFA16(this);
    protected DFA29 dfa29 = new DFA29(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA41 dfa41 = new DFA41(this);
    protected DFA43 dfa43 = new DFA43(this);
    static final String DFA8_eotS =
        "\7\uffff";
    static final String DFA8_eofS =
        "\1\2\6\uffff";
    static final String DFA8_minS =
        "\2\15\1\20\1\15\2\uffff\1\15";
    static final String DFA8_maxS =
        "\1\23\3\72\2\uffff\1\72";
    static final String DFA8_acceptS =
        "\4\uffff\1\2\1\1\1\uffff";
    static final String DFA8_specialS =
        "\7\uffff}>";
    static final String[] DFA8_transitionS = {
            "\2\1\1\3\3\uffff\1\4",
            "\2\1\1\uffff\3\5\1\4\5\uffff\2\5\2\uffff\3\5\1\uffff\2\5\1"+
            "\uffff\4\5\2\uffff\2\5\5\uffff\1\5\2\uffff\7\5",
            "\3\5\1\4\5\uffff\2\5\2\uffff\3\5\1\uffff\2\5\1\uffff\4\5\2"+
            "\uffff\2\5\5\uffff\1\5\2\uffff\7\5",
            "\2\6\1\uffff\3\5\1\4\5\uffff\2\5\2\uffff\3\5\1\uffff\2\5\1"+
            "\uffff\4\5\2\uffff\2\5\5\uffff\1\5\2\uffff\7\5",
            "",
            "",
            "\2\6\1\uffff\3\5\1\4\5\uffff\2\5\2\uffff\3\5\1\uffff\2\5\1"+
            "\uffff\4\5\2\uffff\2\5\5\uffff\1\5\2\uffff\7\5"
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
            return "()* loopback of 119:50: ( n e= expr_or_assign )*";
        }
    }
    static final String DFA12_eotS =
        "\14\uffff";
    static final String DFA12_eofS =
        "\1\6\13\uffff";
    static final String DFA12_minS =
        "\1\15\5\0\6\uffff";
    static final String DFA12_maxS =
        "\1\110\5\0\6\uffff";
    static final String DFA12_acceptS =
        "\6\uffff\1\6\1\1\1\2\1\3\1\4\1\5";
    static final String DFA12_specialS =
        "\1\uffff\1\2\1\0\1\4\1\1\1\3\6\uffff}>";
    static final String[] DFA12_transitionS = {
            "\3\6\3\uffff\1\6\1\1\1\2\1\3\1\4\1\5\1\uffff\3\6\6\uffff\1\6"+
            "\2\uffff\1\6\1\uffff\11\6\12\uffff\16\6",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "",
            "",
            "",
            "",
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
            return "132:3: ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | )";
        }
        @Override
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA12_2 = input.LA(1);

                         
                        int index12_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_R()) ) {s = 8;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index12_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA12_4 = input.LA(1);

                         
                        int index12_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_R()) ) {s = 10;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index12_4);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA12_1 = input.LA(1);

                         
                        int index12_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_R()) ) {s = 7;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index12_1);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA12_5 = input.LA(1);

                         
                        int index12_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_R()) ) {s = 11;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index12_5);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA12_3 = input.LA(1);

                         
                        int index12_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_R()) ) {s = 9;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index12_3);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 12, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA14_eotS =
        "\4\uffff";
    static final String DFA14_eofS =
        "\4\uffff";
    static final String DFA14_minS =
        "\2\15\2\uffff";
    static final String DFA14_maxS =
        "\2\43\2\uffff";
    static final String DFA14_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA14_specialS =
        "\4\uffff}>";
    static final String[] DFA14_transitionS = {
            "\2\1\14\uffff\1\2\7\uffff\1\3",
            "\2\1\14\uffff\1\2\7\uffff\1\3",
            "",
            ""
    };

    static final short[] DFA14_eot = DFA.unpackEncodedString(DFA14_eotS);
    static final short[] DFA14_eof = DFA.unpackEncodedString(DFA14_eofS);
    static final char[] DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS);
    static final char[] DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS);
    static final short[] DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS);
    static final short[] DFA14_special = DFA.unpackEncodedString(DFA14_specialS);
    static final short[][] DFA14_transition;

    static {
        int numStates = DFA14_transitionS.length;
        DFA14_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA14_transition[i] = DFA.unpackEncodedString(DFA14_transitionS[i]);
        }
    }

    class DFA14 extends DFA {

        public DFA14(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 14;
            this.eot = DFA14_eot;
            this.eof = DFA14_eof;
            this.min = DFA14_min;
            this.max = DFA14_max;
            this.accept = DFA14_accept;
            this.special = DFA14_special;
            this.transition = DFA14_transition;
        }
        @Override
        public String getDescription() {
            return "()* loopback of 158:38: ( n_ COMMA n_ par_decl[l] )*";
        }
    }
    static final String DFA16_eotS =
        "\15\uffff";
    static final String DFA16_eofS =
        "\15\uffff";
    static final String DFA16_minS =
        "\1\37\4\15\2\uffff\1\15\2\uffff\1\15\2\uffff";
    static final String DFA16_maxS =
        "\1\45\4\43\2\uffff\1\43\2\uffff\1\43\2\uffff";
    static final String DFA16_acceptS =
        "\5\uffff\1\2\1\1\1\uffff\1\4\1\3\1\uffff\1\5\1\6";
    static final String DFA16_specialS =
        "\15\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\1\4\uffff\1\2\1\3",
            "\2\4\11\uffff\1\5\2\uffff\1\6\7\uffff\1\6",
            "\2\7\11\uffff\1\10\2\uffff\1\11\7\uffff\1\11",
            "\2\12\11\uffff\1\14\2\uffff\1\13\7\uffff\1\13",
            "\2\4\11\uffff\1\5\2\uffff\1\6\7\uffff\1\6",
            "",
            "",
            "\2\7\11\uffff\1\10\2\uffff\1\11\7\uffff\1\11",
            "",
            "",
            "\2\12\11\uffff\1\14\2\uffff\1\13\7\uffff\1\13",
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
            return "160:1: par_decl[ArgumentList l] : (i= ID | i= ID n_ ASSIGN n_ e= expr | v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr );";
        }
    }
    static final String DFA29_eotS =
        "\47\uffff";
    static final String DFA29_eofS =
        "\1\42\46\uffff";
    static final String DFA29_minS =
        "\1\15\5\0\41\uffff";
    static final String DFA29_maxS =
        "\1\110\5\0\41\uffff";
    static final String DFA29_acceptS =
        "\6\uffff\40\2\1\1";
    static final String DFA29_specialS =
        "\1\2\1\4\1\1\1\0\1\3\1\5\41\uffff}>";
    static final String[] DFA29_transitionS = {
            "\2\35\1\43\3\uffff\1\44\1\31\1\32\1\33\1\34\1\41\1\uffff\1\5"+
            "\1\37\1\45\6\uffff\1\36\2\uffff\1\30\1\uffff\1\10\1\7\1\14\1"+
            "\15\1\1\1\2\1\3\1\40\1\4\12\uffff\1\26\1\27\1\24\1\25\1\16\1"+
            "\17\1\20\1\21\1\22\1\23\1\11\1\12\1\13\1\6",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
            "\1\uffff",
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
            "",
            "",
            ""
    };

    static final short[] DFA29_eot = DFA.unpackEncodedString(DFA29_eotS);
    static final short[] DFA29_eof = DFA.unpackEncodedString(DFA29_eofS);
    static final char[] DFA29_min = DFA.unpackEncodedStringToUnsignedChars(DFA29_minS);
    static final char[] DFA29_max = DFA.unpackEncodedStringToUnsignedChars(DFA29_maxS);
    static final short[] DFA29_accept = DFA.unpackEncodedString(DFA29_acceptS);
    static final short[] DFA29_special = DFA.unpackEncodedString(DFA29_specialS);
    static final short[][] DFA29_transition;

    static {
        int numStates = DFA29_transitionS.length;
        DFA29_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA29_transition[i] = DFA.unpackEncodedString(DFA29_transitionS[i]);
        }
    }

    class DFA29 extends DFA {

        public DFA29(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 29;
            this.eot = DFA29_eot;
            this.eof = DFA29_eof;
            this.min = DFA29_min;
            this.max = DFA29_max;
            this.accept = DFA29_accept;
            this.special = DFA29_special;
            this.transition = DFA29_transition;
        }
        @Override
        public String getDescription() {
            return "221:2: ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)";
        }
        @Override
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA29_3 = input.LA(1);

                         
                        int index29_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index29_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA29_2 = input.LA(1);

                         
                        int index29_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index29_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA29_0 = input.LA(1);

                         
                        int index29_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA29_0==FIELD) ) {s = 1;}

                        else if ( (LA29_0==AT) ) {s = 2;}

                        else if ( (LA29_0==LBRAKET) ) {s = 3;}

                        else if ( (LA29_0==LBB) ) {s = 4;}

                        else if ( (LA29_0==LPAR) ) {s = 5;}

                        else if ( (LA29_0==CARRET) && (synpred17_R())) {s = 6;}

                        else if ( (LA29_0==COLON) && (synpred17_R())) {s = 7;}

                        else if ( (LA29_0==OP) && (synpred17_R())) {s = 8;}

                        else if ( (LA29_0==MULT) && (synpred17_R())) {s = 9;}

                        else if ( (LA29_0==DIV) && (synpred17_R())) {s = 10;}

                        else if ( (LA29_0==MOD) && (synpred17_R())) {s = 11;}

                        else if ( (LA29_0==PLUS) && (synpred17_R())) {s = 12;}

                        else if ( (LA29_0==MINUS) && (synpred17_R())) {s = 13;}

                        else if ( (LA29_0==GT) && (synpred17_R())) {s = 14;}

                        else if ( (LA29_0==GE) && (synpred17_R())) {s = 15;}

                        else if ( (LA29_0==LT) && (synpred17_R())) {s = 16;}

                        else if ( (LA29_0==LE) && (synpred17_R())) {s = 17;}

                        else if ( (LA29_0==EQ) && (synpred17_R())) {s = 18;}

                        else if ( (LA29_0==NE) && (synpred17_R())) {s = 19;}

                        else if ( (LA29_0==AND) && (synpred17_R())) {s = 20;}

                        else if ( (LA29_0==BITWISEAND) && (synpred17_R())) {s = 21;}

                        else if ( (LA29_0==OR) && (synpred17_R())) {s = 22;}

                        else if ( (LA29_0==BITWISEOR) && (synpred17_R())) {s = 23;}

                        else if ( (LA29_0==TILDE) && (synpred17_R())) {s = 24;}

                        else if ( (LA29_0==ARROW) && (synpred17_R())) {s = 25;}

                        else if ( (LA29_0==SUPER_ARROW) && (synpred17_R())) {s = 26;}

                        else if ( (LA29_0==RIGHT_ARROW) && (synpred17_R())) {s = 27;}

                        else if ( (LA29_0==SUPER_RIGHT_ARROW) && (synpred17_R())) {s = 28;}

                        else if ( ((LA29_0>=NEWLINE && LA29_0<=COMMENT)) && (synpred17_R())) {s = 29;}

                        else if ( (LA29_0==COMMA) && (synpred17_R())) {s = 30;}

                        else if ( (LA29_0==RPAR) && (synpred17_R())) {s = 31;}

                        else if ( (LA29_0==RBRAKET) && (synpred17_R())) {s = 32;}

                        else if ( (LA29_0==ASSIGN) && (synpred17_R())) {s = 33;}

                        else if ( (LA29_0==EOF) && (synpred17_R())) {s = 34;}

                        else if ( (LA29_0==SEMICOLON) && (synpred17_R())) {s = 35;}

                        else if ( (LA29_0==RBRACE) && (synpred17_R())) {s = 36;}

                        else if ( (LA29_0==ELSE) && (synpred17_R())) {s = 37;}

                         
                        input.seek(index29_0);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA29_4 = input.LA(1);

                         
                        int index29_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index29_4);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA29_1 = input.LA(1);

                         
                        int index29_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index29_1);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA29_5 = input.LA(1);

                         
                        int index29_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index29_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 29, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA31_eotS =
        "\16\uffff";
    static final String DFA31_eofS =
        "\1\uffff\2\14\13\uffff";
    static final String DFA31_minS =
        "\1\20\2\15\13\uffff";
    static final String DFA31_maxS =
        "\1\72\2\110\13\uffff";
    static final String DFA31_acceptS =
        "\3\uffff\1\2\1\3\1\4\1\5\1\6\1\11\1\12\1\13\1\10\1\1\1\7";
    static final String DFA31_specialS =
        "\16\uffff}>";
    static final String[] DFA31_transitionS = {
            "\2\12\1\11\6\uffff\1\12\1\10\2\uffff\2\12\1\1\1\uffff\2\12\1"+
            "\uffff\1\2\1\4\13\uffff\1\5\2\uffff\3\6\1\7\3\3",
            "\3\14\3\uffff\6\14\1\uffff\3\14\6\uffff\1\14\2\uffff\1\14\1"+
            "\uffff\11\14\1\uffff\1\15\1\13\7\uffff\16\14",
            "\3\14\3\uffff\6\14\1\uffff\3\14\6\uffff\1\14\2\uffff\1\14\1"+
            "\uffff\11\14\1\uffff\1\15\1\13\7\uffff\16\14",
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
            ""
    };

    static final short[] DFA31_eot = DFA.unpackEncodedString(DFA31_eotS);
    static final short[] DFA31_eof = DFA.unpackEncodedString(DFA31_eofS);
    static final char[] DFA31_min = DFA.unpackEncodedStringToUnsignedChars(DFA31_minS);
    static final char[] DFA31_max = DFA.unpackEncodedStringToUnsignedChars(DFA31_maxS);
    static final short[] DFA31_accept = DFA.unpackEncodedString(DFA31_acceptS);
    static final short[] DFA31_special = DFA.unpackEncodedString(DFA31_specialS);
    static final short[][] DFA31_transition;

    static {
        int numStates = DFA31_transitionS.length;
        DFA31_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA31_transition[i] = DFA.unpackEncodedString(DFA31_transitionS[i]);
        }
    }

    class DFA31 extends DFA {

        public DFA31(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 31;
            this.eot = DFA31_eot;
            this.eof = DFA31_eof;
            this.min = DFA31_min;
            this.max = DFA31_max;
            this.accept = DFA31_accept;
            this.special = DFA31_special;
            this.transition = DFA31_transition;
        }
        @Override
        public String getDescription() {
            return "232:1: simple_expr returns [ASTNode v] : (i= id | b= bool | DD | NULL | num= number | cstr= conststring | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR | s= sequence | e= expr_wo_assign );";
        }
    }
    static final String DFA40_eotS =
        "\4\uffff";
    static final String DFA40_eofS =
        "\4\uffff";
    static final String DFA40_minS =
        "\2\15\2\uffff";
    static final String DFA40_maxS =
        "\2\72\2\uffff";
    static final String DFA40_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA40_specialS =
        "\4\uffff}>";
    static final String[] DFA40_transitionS = {
            "\2\1\1\uffff\3\2\6\uffff\2\2\1\3\1\uffff\3\2\1\uffff\2\2\1\3"+
            "\4\2\2\uffff\2\2\3\uffff\1\3\1\uffff\1\2\2\uffff\7\2",
            "\2\1\1\uffff\3\2\6\uffff\2\2\1\3\1\uffff\3\2\1\uffff\2\2\1"+
            "\3\4\2\2\uffff\2\2\3\uffff\1\3\1\uffff\1\2\2\uffff\7\2",
            "",
            ""
    };

    static final short[] DFA40_eot = DFA.unpackEncodedString(DFA40_eotS);
    static final short[] DFA40_eof = DFA.unpackEncodedString(DFA40_eofS);
    static final char[] DFA40_min = DFA.unpackEncodedStringToUnsignedChars(DFA40_minS);
    static final char[] DFA40_max = DFA.unpackEncodedStringToUnsignedChars(DFA40_maxS);
    static final short[] DFA40_accept = DFA.unpackEncodedString(DFA40_acceptS);
    static final short[] DFA40_special = DFA.unpackEncodedString(DFA40_specialS);
    static final short[][] DFA40_transition;

    static {
        int numStates = DFA40_transitionS.length;
        DFA40_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA40_transition[i] = DFA.unpackEncodedString(DFA40_transitionS[i]);
        }
    }

    class DFA40 extends DFA {

        public DFA40(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 40;
            this.eot = DFA40_eot;
            this.eof = DFA40_eof;
            this.min = DFA40_min;
            this.max = DFA40_max;
            this.accept = DFA40_accept;
            this.special = DFA40_special;
            this.transition = DFA40_transition;
        }
        @Override
        public String getDescription() {
            return "287:7: ( n_ arg_expr[v] )?";
        }
    }
    static final String DFA41_eotS =
        "\4\uffff";
    static final String DFA41_eofS =
        "\4\uffff";
    static final String DFA41_minS =
        "\2\15\2\uffff";
    static final String DFA41_maxS =
        "\2\72\2\uffff";
    static final String DFA41_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA41_specialS =
        "\4\uffff}>";
    static final String[] DFA41_transitionS = {
            "\2\1\1\uffff\3\3\6\uffff\2\3\1\2\1\uffff\3\3\1\uffff\2\3\1\2"+
            "\4\3\2\uffff\2\3\3\uffff\1\2\1\uffff\1\3\2\uffff\7\3",
            "\2\1\1\uffff\3\3\6\uffff\2\3\1\2\1\uffff\3\3\1\uffff\2\3\1"+
            "\2\4\3\2\uffff\2\3\3\uffff\1\2\1\uffff\1\3\2\uffff\7\3",
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
            return "287:35: ( | n_ arg_expr[v] )";
        }
    }
    static final String DFA43_eotS =
        "\17\uffff";
    static final String DFA43_eofS =
        "\17\uffff";
    static final String DFA43_minS =
        "\1\20\1\uffff\10\15\2\uffff\1\15\2\uffff";
    static final String DFA43_maxS =
        "\1\72\1\uffff\3\110\1\57\1\72\1\57\2\72\2\uffff\1\72\2\uffff";
    static final String DFA43_acceptS =
        "\1\uffff\1\1\10\uffff\1\3\1\2\1\uffff\1\5\1\4";
    static final String DFA43_specialS =
        "\17\uffff}>";
    static final String[] DFA43_transitionS = {
            "\3\1\6\uffff\2\1\2\uffff\2\1\1\2\1\uffff\2\1\1\uffff\1\3\3\1"+
            "\2\uffff\2\1\5\uffff\1\4\2\uffff\7\1",
            "",
            "\2\5\5\uffff\4\1\1\6\1\uffff\2\1\7\uffff\1\1\2\uffff\1\1\1"+
            "\uffff\11\1\1\uffff\2\1\7\uffff\16\1",
            "\2\5\5\uffff\4\1\1\6\1\uffff\2\1\7\uffff\1\1\2\uffff\1\1\1"+
            "\uffff\11\1\1\uffff\2\1\7\uffff\16\1",
            "\2\7\5\uffff\4\1\1\10\1\uffff\2\1\7\uffff\1\1\2\uffff\1\1\1"+
            "\uffff\11\1\12\uffff\16\1",
            "\2\5\11\uffff\1\6\2\uffff\1\1\7\uffff\1\1\13\uffff\1\1",
            "\2\11\1\uffff\3\13\6\uffff\2\13\1\12\1\uffff\3\13\1\uffff\2"+
            "\13\1\12\4\13\2\uffff\2\13\3\uffff\1\12\1\uffff\1\13\2\uffff"+
            "\7\13",
            "\2\7\11\uffff\1\10\2\uffff\1\1\7\uffff\1\1\13\uffff\1\1",
            "\2\14\1\uffff\3\16\6\uffff\2\16\1\15\1\uffff\3\16\1\uffff\2"+
            "\16\1\15\4\16\2\uffff\2\16\3\uffff\1\15\1\uffff\1\16\2\uffff"+
            "\7\16",
            "\2\11\1\uffff\3\13\6\uffff\2\13\1\12\1\uffff\3\13\1\uffff\2"+
            "\13\1\12\4\13\2\uffff\2\13\3\uffff\1\12\1\uffff\1\13\2\uffff"+
            "\7\13",
            "",
            "",
            "\2\14\1\uffff\3\16\6\uffff\2\16\1\15\1\uffff\3\16\1\uffff\2"+
            "\16\1\15\4\16\2\uffff\2\16\3\uffff\1\15\1\uffff\1\16\2\uffff"+
            "\7\16",
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
            return "289:1: arg_expr[ArgumentList l] : (e= expr | name= id n_ ASSIGN n_ val= expr | name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );";
        }
    }
 

    public static final BitSet FOLLOW_n__in_script156 = new BitSet(new long[]{0x07F20CF6E6070002L,0x0000000000100000L});
    public static final BitSet FOLLOW_statement_in_script161 = new BitSet(new long[]{0x07F20CF6E6070002L,0x0000000000100000L});
    public static final BitSet FOLLOW_n__in_interactive179 = new BitSet(new long[]{0x07F20CF6E6070000L,0x0000000000100000L});
    public static final BitSet FOLLOW_statement_in_interactive183 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_or_assign_in_statement201 = new BitSet(new long[]{0x000000000000E000L});
    public static final BitSet FOLLOW_n_in_statement203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_statement210 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_EOF_in_statement215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_n_225 = new BitSet(new long[]{0x0000000000006002L});
    public static final BitSet FOLLOW_set_in_n239 = new BitSet(new long[]{0x0000000000006002L});
    public static final BitSet FOLLOW_EOF_in_n250 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_n254 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_n__in_n256 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alter_assign_in_expr_or_assign271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assign_in_expr289 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_while_expr_in_expr_wo_assign308 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_if_expr_in_expr_wo_assign317 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_for_expr_in_expr_wo_assign326 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_repeat_expr_in_expr_wo_assign335 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_expr_wo_assign344 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEXT_in_expr_wo_assign351 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_expr_wo_assign361 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_sequence395 = new BitSet(new long[]{0x07F20CF6E60F6000L});
    public static final BitSet FOLLOW_n__in_sequence397 = new BitSet(new long[]{0x07F20CF6E60F0000L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence402 = new BitSet(new long[]{0x000000000008E000L});
    public static final BitSet FOLLOW_n_in_sequence407 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence411 = new BitSet(new long[]{0x000000000008E000L});
    public static final BitSet FOLLOW_n_in_sequence417 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_RBRACE_in_sequence423 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_assign441 = new BitSet(new long[]{0x0000000000F00002L});
    public static final BitSet FOLLOW_ARROW_in_assign448 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_assign450 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_assign454 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_assign462 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_assign464 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_assign468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_assign478 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_assign480 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_assign484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_assign494 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_assign496 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_assign500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_alter_assign528 = new BitSet(new long[]{0x0000000001F00002L});
    public static final BitSet FOLLOW_ARROW_in_alter_assign539 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign541 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign545 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_alter_assign557 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign559 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign563 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_alter_assign577 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign579 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign583 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_alter_assign597 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign599 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign603 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_alter_assign617 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign619 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_if_expr650 = new BitSet(new long[]{0x0000000004006000L});
    public static final BitSet FOLLOW_n__in_if_expr652 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAR_in_if_expr654 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_if_expr656 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr660 = new BitSet(new long[]{0x0000000008006000L});
    public static final BitSet FOLLOW_n__in_if_expr662 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_if_expr664 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_if_expr666 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr670 = new BitSet(new long[]{0x0000000010006002L});
    public static final BitSet FOLLOW_n__in_if_expr696 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ELSE_in_if_expr698 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_if_expr700 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_while_expr732 = new BitSet(new long[]{0x0000000004006000L});
    public static final BitSet FOLLOW_n__in_while_expr734 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAR_in_while_expr736 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_while_expr738 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr742 = new BitSet(new long[]{0x0000000008006000L});
    public static final BitSet FOLLOW_n__in_while_expr744 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_while_expr746 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_while_expr748 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr752 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_for_expr768 = new BitSet(new long[]{0x0000000004006000L});
    public static final BitSet FOLLOW_n__in_for_expr770 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAR_in_for_expr772 = new BitSet(new long[]{0x0000000080006000L});
    public static final BitSet FOLLOW_n__in_for_expr774 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_ID_in_for_expr778 = new BitSet(new long[]{0x0000000100006000L});
    public static final BitSet FOLLOW_n__in_for_expr780 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_IN_in_for_expr782 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_for_expr784 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr788 = new BitSet(new long[]{0x0000000008006000L});
    public static final BitSet FOLLOW_n__in_for_expr790 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_for_expr792 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_for_expr794 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REPEAT_in_repeat_expr815 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_repeat_expr817 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_repeat_expr821 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function842 = new BitSet(new long[]{0x0000000004006000L});
    public static final BitSet FOLLOW_n__in_function844 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAR_in_function846 = new BitSet(new long[]{0x0000003088006000L});
    public static final BitSet FOLLOW_n__in_function849 = new BitSet(new long[]{0x0000003088000000L});
    public static final BitSet FOLLOW_par_decl_in_function852 = new BitSet(new long[]{0x0000000808006000L});
    public static final BitSet FOLLOW_n__in_function856 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_COMMA_in_function858 = new BitSet(new long[]{0x0000003080006000L});
    public static final BitSet FOLLOW_n__in_function860 = new BitSet(new long[]{0x0000003080000000L});
    public static final BitSet FOLLOW_par_decl_in_function862 = new BitSet(new long[]{0x0000000808006000L});
    public static final BitSet FOLLOW_n__in_function867 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_function871 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_function873 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_function877 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl894 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl904 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_par_decl906 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl908 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_par_decl910 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_par_decl914 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_par_decl923 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_par_decl973 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_par_decl975 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl977 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_par_decl979 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_par_decl981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_par_decl987 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_par_decl993 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_par_decl995 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl997 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_par_decl999 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_par_decl1001 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_expr_in_tilde_expr1017 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_TILDE_in_tilde_expr1029 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_tilde_expr1031 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_or_expr_in_tilde_expr1035 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_and_expr_in_or_expr1057 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_or_operator_in_or_expr1070 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_or_expr1072 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_and_expr_in_or_expr1076 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_not_expr_in_and_expr1098 = new BitSet(new long[]{0x6000000000000002L});
    public static final BitSet FOLLOW_and_operator_in_and_expr1114 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_and_expr1116 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_not_expr_in_and_expr1120 = new BitSet(new long[]{0x6000000000000002L});
    public static final BitSet FOLLOW_NOT_in_not_expr1140 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_not_expr1142 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_not_expr_in_not_expr1146 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_expr_in_not_expr1155 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1173 = new BitSet(new long[]{0x8000000000000002L,0x000000000000001FL});
    public static final BitSet FOLLOW_comp_operator_in_comp_expr1189 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_comp_expr1191 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1195 = new BitSet(new long[]{0x8000000000000002L,0x000000000000001FL});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1220 = new BitSet(new long[]{0x00000C0000000002L});
    public static final BitSet FOLLOW_add_operator_in_add_expr1234 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_add_expr1236 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1240 = new BitSet(new long[]{0x00000C0000000002L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1262 = new BitSet(new long[]{0x0000000000000002L,0x00000000000000E0L});
    public static final BitSet FOLLOW_mult_operator_in_mult_expr1275 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_mult_expr1277 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1281 = new BitSet(new long[]{0x0000000000000002L,0x00000000000000E0L});
    public static final BitSet FOLLOW_colon_expr_in_operator_expr1303 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_OP_in_operator_expr1316 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_operator_expr1318 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_colon_expr_in_operator_expr1322 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_power_expr_in_colon_expr1349 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_COLON_in_colon_expr1362 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_colon_expr1364 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_power_expr_in_colon_expr1368 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_unary_expression_in_power_expr1390 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_power_operator_in_power_expr1406 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_power_expr1408 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_power_expr_in_power_expr1412 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary_expression1441 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_unary_expression1443 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1447 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary_expression1454 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_unary_expression1456 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1460 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unary_expression1467 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_unary_expression1469 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1473 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_basic_expr_in_unary_expression1482 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_basic_expr1500 = new BitSet(new long[]{0x0001700004000002L});
    public static final BitSet FOLLOW_expr_subset_in_basic_expr1521 = new BitSet(new long[]{0x0001700004000002L});
    public static final BitSet FOLLOW_FIELD_in_expr_subset1554 = new BitSet(new long[]{0x0000001080006000L});
    public static final BitSet FOLLOW_n__in_expr_subset1556 = new BitSet(new long[]{0x0000001080000000L});
    public static final BitSet FOLLOW_id_in_expr_subset1560 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_expr_subset1573 = new BitSet(new long[]{0x0000001080006000L});
    public static final BitSet FOLLOW_n__in_expr_subset1575 = new BitSet(new long[]{0x0000001080000000L});
    public static final BitSet FOLLOW_id_in_expr_subset1579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRAKET_in_expr_subset1593 = new BitSet(new long[]{0x07F20CFEE6076000L});
    public static final BitSet FOLLOW_args_in_expr_subset1597 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBB_in_expr_subset1611 = new BitSet(new long[]{0x07F20CFEE6076000L});
    public static final BitSet FOLLOW_args_in_expr_subset1615 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1617 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_subset1636 = new BitSet(new long[]{0x07F20CFEE6076000L});
    public static final BitSet FOLLOW_args_in_expr_subset1640 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_subset1642 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_in_simple_expr1680 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_simple_expr1687 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_simple_expr1692 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_simple_expr1701 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conststring_in_simple_expr1710 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1717 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_NS_GET_in_simple_expr1719 = new BitSet(new long[]{0x0000001080006000L});
    public static final BitSet FOLLOW_n__in_simple_expr1721 = new BitSet(new long[]{0x0000001080000000L});
    public static final BitSet FOLLOW_id_in_simple_expr1723 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1728 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_NS_GET_INT_in_simple_expr1730 = new BitSet(new long[]{0x0000001080006000L});
    public static final BitSet FOLLOW_n__in_simple_expr1732 = new BitSet(new long[]{0x0000001080000000L});
    public static final BitSet FOLLOW_id_in_simple_expr1734 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_simple_expr1739 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_simple_expr1741 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_simple_expr1747 = new BitSet(new long[]{0x0000000008006000L});
    public static final BitSet FOLLOW_n__in_simple_expr1749 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_simple_expr1751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_simple_expr1762 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_wo_assign_in_simple_expr1773 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_number1794 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_number1806 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMPLEX_in_number1818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_conststring1842 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_id1866 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_id1878 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_bool1900 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_bool1910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NA_in_bool1920 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_or_operator1939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEOR_in_or_operator1956 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_and_operator1972 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEAND_in_and_operator1988 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_comp_operator2004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GE_in_comp_operator2011 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_comp_operator2018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LE_in_comp_operator2025 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_comp_operator2032 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NE_in_comp_operator2039 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_add_operator2053 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_add_operator2060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_in_mult_operator2075 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_in_mult_operator2082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mult_operator2090 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARRET_in_power_operator2105 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_args2130 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_arg_expr_in_args2132 = new BitSet(new long[]{0x0000000800006000L});
    public static final BitSet FOLLOW_n__in_args2137 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_COMMA_in_args2140 = new BitSet(new long[]{0x07F20CFEE6076000L});
    public static final BitSet FOLLOW_n__in_args2148 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_arg_expr_in_args2150 = new BitSet(new long[]{0x0000000800006000L});
    public static final BitSet FOLLOW_n__in_args2154 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_expr_in_arg_expr2171 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2180 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_arg_expr2182 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2184 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_arg_expr2186 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_arg_expr2190 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2199 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_arg_expr2201 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2203 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2211 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_arg_expr2213 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2215 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_arg_expr2217 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_arg_expr2221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2228 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_arg_expr2230 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_synpred1_R536 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_synpred2_R554 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_synpred3_R572 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_synpred4_R592 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_synpred5_R612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred6_R675 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ELSE_in_synpred6_R677 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_synpred7_R1026 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_operator_in_synpred8_R1065 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_operator_in_synpred9_R1109 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_operator_in_synpred10_R1184 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_operator_in_synpred11_R1229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_operator_in_synpred12_R1270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OP_in_synpred13_R1311 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_synpred14_R1357 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_operator_in_synpred15_R1401 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred16_R1507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred17_R1531 = new BitSet(new long[]{0x0000000000000002L});

}
