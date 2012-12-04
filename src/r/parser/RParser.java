// $ANTLR !Unknown version! R.g 2012-12-04 12:53:32

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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CALL", "BRAKET", "KW", "PARMS", "SEQUENCE", "MISSING_VAL", "UPLUS", "UMINUS", "UTILDE", "NEWLINE", "COMMENT", "SEMICOLON", "NEXT", "BREAK", "LBRACE", "RBRACE", "ARROW", "SUPER_ARROW", "RIGHT_ARROW", "SUPER_RIGHT_ARROW", "ASSIGN", "IF", "LPAR", "RPAR", "ELSE", "WHILE", "FOR", "ID", "IN", "REPEAT", "FUNCTION", "COMMA", "VARIATIC", "DD", "TILDE", "NOT", "OP", "COLON", "PLUS", "MINUS", "FIELD", "AT", "LBRAKET", "RBRAKET", "LBB", "NULL", "NS_GET", "NS_GET_INT", "INTEGER", "DOUBLE", "COMPLEX", "STRING", "TRUE", "FALSE", "NA", "OR", "ELEMENTWISEOR", "AND", "ELEMENTWISEAND", "GT", "GE", "LT", "LE", "EQ", "NE", "MULT", "DIV", "MOD", "CARRET", "LINE_BREAK", "WS", "EXPONENT", "HEX_DIGIT", "ID_NAME", "ESC_SEQ", "OP_NAME", "ESCAPE", "UNICODE_ESC", "OCTAL_ESC", "HEX_ESC", "'--EOF--'"
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
    public static final int ELEMENTWISEOR=60;
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
    public static final int ELEMENTWISEAND=62;
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
            this.state.ruleMemo = new HashMap[61+1];
             
             
        }
        

    @Override
    public String[] getTokenNames() { return RParser.tokenNames; }
    @Override
    public String getGrammarFileName() { return "R.g"; }


        public void display_next_tokens(){
            System.err.print("Expected tokens: ");
            for(int next: next_tokens()) {
            		if(next > 3)
                		System.err.print(tokenNames[next] + " ");
                }
            System.err.println("");
        }
        public int[] next_tokens(){
            return state.following[state._fsp].toArray();
        }



    // $ANTLR start "script"
    // R.g:75:1: script returns [ASTNode v] : n_ (s= statement )* ;
    public final ASTNode script() throws RecognitionException {
        ASTNode v = null;
        int script_StartIndex = input.index();
        ASTNode s = null;


        ArrayList<ASTNode> stmts = new ArrayList<ASTNode>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return v; }
            // R.g:90:2: ( n_ (s= statement )* )
            // R.g:90:4: n_ (s= statement )*
            {
            pushFollow(FOLLOW_n__in_script156);
            n_();

            state._fsp--;
            if (state.failed) return v;
            // R.g:90:7: (s= statement )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( ((LA1_0>=NEXT && LA1_0<=LBRACE)||(LA1_0>=IF && LA1_0<=LPAR)||(LA1_0>=WHILE && LA1_0<=ID)||(LA1_0>=REPEAT && LA1_0<=FUNCTION)||(LA1_0>=VARIATIC && LA1_0<=NOT)||(LA1_0>=PLUS && LA1_0<=MINUS)||LA1_0==NULL||(LA1_0>=INTEGER && LA1_0<=NA)||LA1_0==84) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // R.g:90:8: s= statement
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
    // R.g:92:1: interactive returns [ASTNode v] : n_ (s= statement )* ;
    public final ASTNode interactive() throws RecognitionException {
        ASTNode v = null;
        int interactive_StartIndex = input.index();
        ASTNode s = null;


        ArrayList<ASTNode> stmts = new ArrayList<ASTNode>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return v; }
            // R.g:95:2: ( n_ (s= statement )* )
            // R.g:95:4: n_ (s= statement )*
            {
            pushFollow(FOLLOW_n__in_interactive192);
            n_();

            state._fsp--;
            if (state.failed) return v;
            // R.g:95:7: (s= statement )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( ((LA2_0>=NEXT && LA2_0<=LBRACE)||(LA2_0>=IF && LA2_0<=LPAR)||(LA2_0>=WHILE && LA2_0<=ID)||(LA2_0>=REPEAT && LA2_0<=FUNCTION)||(LA2_0>=VARIATIC && LA2_0<=NOT)||(LA2_0>=PLUS && LA2_0<=MINUS)||LA2_0==NULL||(LA2_0>=INTEGER && LA2_0<=NA)||LA2_0==84) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // R.g:95:8: s= statement
            	    {
            	    pushFollow(FOLLOW_statement_in_interactive197);
            	    s=statement();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	      stmts.add(s);
            	    }

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            }

            if ( state.backtracking==0 ) {
               switch(stmts.size()) { case 0: v = null; break; case 1: v = stmts.get(0); break; default: v = Sequence.create(stmts);} 
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
    // R.g:97:1: statement returns [ASTNode v] : (e= expr_or_assign n | '--EOF--' ( . )* EOF );
    public final ASTNode statement() throws RecognitionException {
        ASTNode v = null;
        int statement_StartIndex = input.index();
        ASTNode e = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return v; }
            // R.g:98:2: (e= expr_or_assign n | '--EOF--' ( . )* EOF )
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( ((LA4_0>=NEXT && LA4_0<=LBRACE)||(LA4_0>=IF && LA4_0<=LPAR)||(LA4_0>=WHILE && LA4_0<=ID)||(LA4_0>=REPEAT && LA4_0<=FUNCTION)||(LA4_0>=VARIATIC && LA4_0<=NOT)||(LA4_0>=PLUS && LA4_0<=MINUS)||LA4_0==NULL||(LA4_0>=INTEGER && LA4_0<=NA)) ) {
                alt4=1;
            }
            else if ( (LA4_0==84) ) {
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
                    // R.g:98:4: e= expr_or_assign n
                    {
                    pushFollow(FOLLOW_expr_or_assign_in_statement217);
                    e=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_n_in_statement219);
                    n();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = e;
                    }

                    }
                    break;
                case 2 :
                    // R.g:99:4: '--EOF--' ( . )* EOF
                    {
                    match(input,84,FOLLOW_84_in_statement226); if (state.failed) return v;
                    // R.g:99:14: ( . )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( ((LA3_0>=CALL && LA3_0<=84)) ) {
                            alt3=1;
                        }
                        else if ( (LA3_0==EOF) ) {
                            alt3=2;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // R.g:99:14: .
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
            if ( state.backtracking>0 ) { memoize(input, 3, statement_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "statement"


    // $ANTLR start "n_"
    // R.g:102:1: n_ : ( NEWLINE | COMMENT )* ;
    public final void n_() throws RecognitionException {
        int n__StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return ; }
            // R.g:102:4: ( ( NEWLINE | COMMENT )* )
            // R.g:102:6: ( NEWLINE | COMMENT )*
            {
            // R.g:102:6: ( NEWLINE | COMMENT )*
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
            	    break loop5;
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
    // R.g:103:1: n : ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLON n_ );
    public final void n() throws RecognitionException {
        int n_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }
            // R.g:103:3: ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLON n_ )
            int alt7=3;
            switch ( input.LA(1) ) {
            case NEWLINE:
            case COMMENT:
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
                    // R.g:103:5: ( NEWLINE | COMMENT )+
                    {
                    // R.g:103:5: ( NEWLINE | COMMENT )+
                    int cnt6=0;
                    loop6:
                    do {
                        int alt6=2;
                        int LA6_0 = input.LA(1);

                        if ( ((LA6_0>=NEWLINE && LA6_0<=COMMENT)) ) {
                            alt6=1;
                        }


                        switch (alt6) {
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
                    // R.g:103:28: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_n266); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // R.g:103:34: SEMICOLON n_
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
            if ( state.backtracking>0 ) { memoize(input, 5, n_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "n"


    // $ANTLR start "expr_or_assign"
    // R.g:105:1: expr_or_assign returns [ASTNode v] : a= alter_assign ;
    public final ASTNode expr_or_assign() throws RecognitionException {
        ASTNode v = null;
        int expr_or_assign_StartIndex = input.index();
        ASTNode a = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return v; }
            // R.g:106:2: (a= alter_assign )
            // R.g:106:4: a= alter_assign
            {
            pushFollow(FOLLOW_alter_assign_in_expr_or_assign287);
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
    // R.g:108:1: expr returns [ASTNode v] : a= assign ;
    public final ASTNode expr() throws RecognitionException {
        ASTNode v = null;
        int expr_StartIndex = input.index();
        ASTNode a = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return v; }
            // R.g:109:2: (a= assign )
            // R.g:109:4: a= assign
            {
            pushFollow(FOLLOW_assign_in_expr305);
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
    // R.g:111:1: expr_wo_assign returns [ASTNode v] : (w= while_expr | i= if_expr | f= for_expr | r= repeat_expr | fun= function | NEXT | BREAK );
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
            // R.g:112:2: (w= while_expr | i= if_expr | f= for_expr | r= repeat_expr | fun= function | NEXT | BREAK )
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
                    // R.g:112:4: w= while_expr
                    {
                    pushFollow(FOLLOW_while_expr_in_expr_wo_assign324);
                    w=while_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = w; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:113:4: i= if_expr
                    {
                    pushFollow(FOLLOW_if_expr_in_expr_wo_assign333);
                    i=if_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = i; 
                    }

                    }
                    break;
                case 3 :
                    // R.g:114:4: f= for_expr
                    {
                    pushFollow(FOLLOW_for_expr_in_expr_wo_assign342);
                    f=for_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = f; 
                    }

                    }
                    break;
                case 4 :
                    // R.g:115:4: r= repeat_expr
                    {
                    pushFollow(FOLLOW_repeat_expr_in_expr_wo_assign351);
                    r=repeat_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = r; 
                    }

                    }
                    break;
                case 5 :
                    // R.g:116:4: fun= function
                    {
                    pushFollow(FOLLOW_function_in_expr_wo_assign360);
                    fun=function();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = fun; 
                    }

                    }
                    break;
                case 6 :
                    // R.g:117:4: NEXT
                    {
                    match(input,NEXT,FOLLOW_NEXT_in_expr_wo_assign367); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Next.create(); 
                    }

                    }
                    break;
                case 7 :
                    // R.g:118:4: BREAK
                    {
                    match(input,BREAK,FOLLOW_BREAK_in_expr_wo_assign377); if (state.failed) return v;
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
    // R.g:120:1: sequence returns [ASTNode v] : LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE ;
    public final ASTNode sequence() throws RecognitionException {
        ASTNode v = null;
        int sequence_StartIndex = input.index();
        ASTNode e = null;


        ArrayList<ASTNode> stmts = new ArrayList<ASTNode>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return v; }
            // R.g:123:2: ( LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE )
            // R.g:123:4: LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE
            {
            match(input,LBRACE,FOLLOW_LBRACE_in_sequence411); if (state.failed) return v;
            pushFollow(FOLLOW_n__in_sequence413);
            n_();

            state._fsp--;
            if (state.failed) return v;
            // R.g:123:14: (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )?
            int alt11=2;
            int LA11_0 = input.LA(1);

            if ( ((LA11_0>=NEXT && LA11_0<=LBRACE)||(LA11_0>=IF && LA11_0<=LPAR)||(LA11_0>=WHILE && LA11_0<=ID)||(LA11_0>=REPEAT && LA11_0<=FUNCTION)||(LA11_0>=VARIATIC && LA11_0<=NOT)||(LA11_0>=PLUS && LA11_0<=MINUS)||LA11_0==NULL||(LA11_0>=INTEGER && LA11_0<=NA)) ) {
                alt11=1;
            }
            switch (alt11) {
                case 1 :
                    // R.g:123:15: e= expr_or_assign ( n e= expr_or_assign )* ( n )?
                    {
                    pushFollow(FOLLOW_expr_or_assign_in_sequence418);
                    e=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       stmts.add(e); 
                    }
                    // R.g:123:50: ( n e= expr_or_assign )*
                    loop9:
                    do {
                        int alt9=2;
                        alt9 = dfa9.predict(input);
                        switch (alt9) {
                    	case 1 :
                    	    // R.g:123:51: n e= expr_or_assign
                    	    {
                    	    pushFollow(FOLLOW_n_in_sequence423);
                    	    n();

                    	    state._fsp--;
                    	    if (state.failed) return v;
                    	    pushFollow(FOLLOW_expr_or_assign_in_sequence427);
                    	    e=expr_or_assign();

                    	    state._fsp--;
                    	    if (state.failed) return v;
                    	    if ( state.backtracking==0 ) {
                    	       stmts.add(e); 
                    	    }

                    	    }
                    	    break;

                    	default :
                    	    break loop9;
                        }
                    } while (true);

                    // R.g:123:90: ( n )?
                    int alt10=2;
                    int LA10_0 = input.LA(1);

                    if ( (LA10_0==EOF||(LA10_0>=NEWLINE && LA10_0<=SEMICOLON)) ) {
                        alt10=1;
                    }
                    switch (alt10) {
                        case 1 :
                            // R.g:123:90: n
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
    // R.g:125:1: assign returns [ASTNode v] : l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr | a= RIGHT_ARROW n_ r= expr | a= SUPER_RIGHT_ARROW n_ r= expr | ) ;
    public final ASTNode assign() throws RecognitionException {
        ASTNode v = null;
        int assign_StartIndex = input.index();
        Token a=null;
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return v; }
            // R.g:126:2: (l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr | a= RIGHT_ARROW n_ r= expr | a= SUPER_RIGHT_ARROW n_ r= expr | ) )
            // R.g:126:4: l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr | a= RIGHT_ARROW n_ r= expr | a= SUPER_RIGHT_ARROW n_ r= expr | )
            {
            pushFollow(FOLLOW_tilde_expr_in_assign457);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;
            // R.g:127:3: ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr | a= RIGHT_ARROW n_ r= expr | a= SUPER_RIGHT_ARROW n_ r= expr | )
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
                    // R.g:127:5: ARROW n_ r= expr
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
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(false, l, r);
                    }

                    }
                    break;
                case 2 :
                    // R.g:128:5: SUPER_ARROW n_ r= expr
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
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(true, l, r);
                    }

                    }
                    break;
                case 3 :
                    // R.g:129:5: a= RIGHT_ARROW n_ r= expr
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
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(false, r, l);
                    }

                    }
                    break;
                case 4 :
                    // R.g:130:5: a= SUPER_RIGHT_ARROW n_ r= expr
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
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(true, r, l);
                    }

                    }
                    break;
                case 5 :
                    // R.g:131:5: 
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
    // R.g:134:1: alter_assign returns [ASTNode v] : l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | ) ;
    public final ASTNode alter_assign() throws RecognitionException {
        ASTNode v = null;
        int alter_assign_StartIndex = input.index();
        Token a=null;
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return v; }
            // R.g:135:2: (l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | ) )
            // R.g:135:4: l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | )
            {
            pushFollow(FOLLOW_tilde_expr_in_alter_assign544);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;
            // R.g:136:3: ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | )
            int alt13=6;
            alt13 = dfa13.predict(input);
            switch (alt13) {
                case 1 :
                    // R.g:136:5: ( ARROW )=> ARROW n_ r= expr_or_assign
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
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(false, l, r);
                    }

                    }
                    break;
                case 2 :
                    // R.g:137:5: ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign
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
                    if ( state.backtracking==0 ) {
                      v = AssignVariable.create(true, l, r);
                    }

                    }
                    break;
                case 3 :
                    // R.g:138:5: ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign
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
                    if ( state.backtracking==0 ) {
                       v = AssignVariable.create(false, r, l);
                    }

                    }
                    break;
                case 4 :
                    // R.g:139:5: ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign
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
                    if ( state.backtracking==0 ) {
                      v = AssignVariable.create(true, r, l);
                    }

                    }
                    break;
                case 5 :
                    // R.g:140:5: ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign
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
                    if ( state.backtracking==0 ) {
                      v = AssignVariable.create(false, l, r);
                    }

                    }
                    break;
                case 6 :
                    // R.g:141:5: 
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
    // R.g:144:1: if_expr returns [ASTNode v] : IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) | ) ;
    public final ASTNode if_expr() throws RecognitionException {
        ASTNode v = null;
        int if_expr_StartIndex = input.index();
        ASTNode cond = null;

        ASTNode t = null;

        ASTNode f = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return v; }
            // R.g:145:2: ( IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) | ) )
            // R.g:146:2: IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) | )
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
            // R.g:147:2: ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) | )
            int alt14=2;
            switch ( input.LA(1) ) {
            case NEWLINE:
            case COMMENT:
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
            case ELEMENTWISEOR:
            case AND:
            case ELEMENTWISEAND:
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
                    // R.g:147:3: ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    {
                    // R.g:147:14: ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    // R.g:147:58: n_ ELSE n_ f= expr_or_assign
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
                    if ( state.backtracking==0 ) {
                       v = If.create(cond, t, f);
                    }

                    }


                    }
                    break;
                case 2 :
                    // R.g:148:7: 
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
    // R.g:151:1: while_expr returns [ASTNode v] : WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final ASTNode while_expr() throws RecognitionException {
        ASTNode v = null;
        int while_expr_StartIndex = input.index();
        ASTNode c = null;

        ASTNode body = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return v; }
            // R.g:152:2: ( WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:152:4: WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign
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
    // R.g:154:1: for_expr returns [ASTNode v] : FOR n_ LPAR n_ i= ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final ASTNode for_expr() throws RecognitionException {
        ASTNode v = null;
        int for_expr_StartIndex = input.index();
        Token i=null;
        ASTNode in = null;

        ASTNode body = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return v; }
            // R.g:155:2: ( FOR n_ LPAR n_ i= ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:155:4: FOR n_ LPAR n_ i= ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign
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
    // R.g:157:1: repeat_expr returns [ASTNode v] : REPEAT n_ body= expr_or_assign ;
    public final ASTNode repeat_expr() throws RecognitionException {
        ASTNode v = null;
        int repeat_expr_StartIndex = input.index();
        ASTNode body = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return v; }
            // R.g:158:2: ( REPEAT n_ body= expr_or_assign )
            // R.g:158:4: REPEAT n_ body= expr_or_assign
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
    // R.g:160:1: function returns [ASTNode v] : FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign ;
    public final ASTNode function() throws RecognitionException {
        ASTNode v = null;
        int function_StartIndex = input.index();
        ASTNode body = null;


         ArgumentList l = new ArgumentList.Default(); 
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return v; }
            // R.g:162:2: ( FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign )
            // R.g:162:4: FUNCTION n_ LPAR n_ ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )? RPAR n_ body= expr_or_assign
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
            // R.g:162:25: ( par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_ )?
            int alt16=2;
            int LA16_0 = input.LA(1);

            if ( (LA16_0==ID||(LA16_0>=VARIATIC && LA16_0<=DD)) ) {
                alt16=1;
            }
            switch (alt16) {
                case 1 :
                    // R.g:162:26: par_decl[l] ( n_ COMMA n_ par_decl[l] )* n_
                    {
                    pushFollow(FOLLOW_par_decl_in_function868);
                    par_decl(l);

                    state._fsp--;
                    if (state.failed) return v;
                    // R.g:162:38: ( n_ COMMA n_ par_decl[l] )*
                    loop15:
                    do {
                        int alt15=2;
                        alt15 = dfa15.predict(input);
                        switch (alt15) {
                    	case 1 :
                    	    // R.g:162:39: n_ COMMA n_ par_decl[l]
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
            if ( state.backtracking==0 ) {
               v = Function.create(l, body); ArgumentList.Default.updateParent(v, l); 
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
    // R.g:164:1: par_decl[ArgumentList l] : (i= ID | i= ID n_ ASSIGN n_ e= expr | v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr );
    public final void par_decl(ArgumentList l) throws RecognitionException {
        int par_decl_StartIndex = input.index();
        Token i=null;
        Token v=null;
        ASTNode e = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }
            // R.g:165:2: (i= ID | i= ID n_ ASSIGN n_ e= expr | v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr )
            int alt17=6;
            alt17 = dfa17.predict(input);
            switch (alt17) {
                case 1 :
                    // R.g:165:4: i= ID
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_par_decl910); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add((i!=null?i.getText():null), null); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:166:4: i= ID n_ ASSIGN n_ e= expr
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
                    if ( state.backtracking==0 ) {
                       l.add((i!=null?i.getText():null), e); 
                    }

                    }
                    break;
                case 3 :
                    // R.g:167:4: v= VARIATIC
                    {
                    v=(Token)match(input,VARIATIC,FOLLOW_VARIATIC_in_par_decl939); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add((v!=null?v.getText():null), null); 
                    }

                    }
                    break;
                case 4 :
                    // R.g:172:5: VARIATIC n_ ASSIGN n_ expr
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
                    // R.g:173:5: DD
                    {
                    match(input,DD,FOLLOW_DD_in_par_decl1003); if (state.failed) return ;

                    }
                    break;
                case 6 :
                    // R.g:174:5: DD n_ ASSIGN n_ expr
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
            if ( state.backtracking>0 ) { memoize(input, 17, par_decl_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "par_decl"


    // $ANTLR start "tilde_expr"
    // R.g:176:1: tilde_expr returns [ASTNode v] : l= utilde_expr ( ( ( TILDE )=> TILDE n_ r= utilde_expr ) )* ;
    public final ASTNode tilde_expr() throws RecognitionException {
        ASTNode v = null;
        int tilde_expr_StartIndex = input.index();
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return v; }
            // R.g:177:2: (l= utilde_expr ( ( ( TILDE )=> TILDE n_ r= utilde_expr ) )* )
            // R.g:177:4: l= utilde_expr ( ( ( TILDE )=> TILDE n_ r= utilde_expr ) )*
            {
            pushFollow(FOLLOW_utilde_expr_in_tilde_expr1033);
            l=utilde_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:178:2: ( ( ( TILDE )=> TILDE n_ r= utilde_expr ) )*
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
            	    // R.g:178:4: ( ( TILDE )=> TILDE n_ r= utilde_expr )
            	    {
            	    // R.g:178:4: ( ( TILDE )=> TILDE n_ r= utilde_expr )
            	    // R.g:178:5: ( TILDE )=> TILDE n_ r= utilde_expr
            	    {
            	    match(input,TILDE,FOLLOW_TILDE_in_tilde_expr1045); if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_tilde_expr1047);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_utilde_expr_in_tilde_expr1051);
            	    r=utilde_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	      v = BinaryOperation.create(BinaryOperator.ADD, v, r);
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
            if ( state.backtracking>0 ) { memoize(input, 18, tilde_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "tilde_expr"


    // $ANTLR start "utilde_expr"
    // R.g:180:1: utilde_expr returns [ASTNode v] : ( TILDE n_ l= or_expr | l= or_expr );
    public final ASTNode utilde_expr() throws RecognitionException {
        ASTNode v = null;
        int utilde_expr_StartIndex = input.index();
        ASTNode l = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return v; }
            // R.g:181:2: ( TILDE n_ l= or_expr | l= or_expr )
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( (LA19_0==TILDE) ) {
                alt19=1;
            }
            else if ( ((LA19_0>=NEXT && LA19_0<=LBRACE)||(LA19_0>=IF && LA19_0<=LPAR)||(LA19_0>=WHILE && LA19_0<=ID)||(LA19_0>=REPEAT && LA19_0<=FUNCTION)||(LA19_0>=VARIATIC && LA19_0<=DD)||LA19_0==NOT||(LA19_0>=PLUS && LA19_0<=MINUS)||LA19_0==NULL||(LA19_0>=INTEGER && LA19_0<=NA)) ) {
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
                    // R.g:181:4: TILDE n_ l= or_expr
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_utilde_expr1071); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_utilde_expr1073);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_or_expr_in_utilde_expr1077);
                    l=or_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = UnaryOperation.create(UnaryOperator.MODEL, l);
                    }

                    }
                    break;
                case 2 :
                    // R.g:182:4: l= or_expr
                    {
                    pushFollow(FOLLOW_or_expr_in_utilde_expr1086);
                    l=or_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = l; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 19, utilde_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "utilde_expr"


    // $ANTLR start "or_expr"
    // R.g:183:1: or_expr returns [ASTNode v] : l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )* ;
    public final ASTNode or_expr() throws RecognitionException {
        ASTNode v = null;
        int or_expr_StartIndex = input.index();
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return v; }
            // R.g:184:2: (l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )* )
            // R.g:184:4: l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )*
            {
            pushFollow(FOLLOW_and_expr_in_or_expr1102);
            l=and_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:185:2: ( ( ( or_operator )=>op= or_operator n_ r= and_expr ) )*
            loop20:
            do {
                int alt20=2;
                int LA20_0 = input.LA(1);

                if ( (LA20_0==OR) ) {
                    int LA20_2 = input.LA(2);

                    if ( (synpred8_R()) ) {
                        alt20=1;
                    }


                }
                else if ( (LA20_0==ELEMENTWISEOR) ) {
                    int LA20_3 = input.LA(2);

                    if ( (synpred8_R()) ) {
                        alt20=1;
                    }


                }


                switch (alt20) {
            	case 1 :
            	    // R.g:185:3: ( ( or_operator )=>op= or_operator n_ r= and_expr )
            	    {
            	    // R.g:185:3: ( ( or_operator )=>op= or_operator n_ r= and_expr )
            	    // R.g:185:4: ( or_operator )=>op= or_operator n_ r= and_expr
            	    {
            	    pushFollow(FOLLOW_or_operator_in_or_expr1115);
            	    op=or_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_or_expr1117);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_and_expr_in_or_expr1121);
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
            	    break loop20;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 20, or_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "or_expr"


    // $ANTLR start "and_expr"
    // R.g:187:1: and_expr returns [ASTNode v] : l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )* ;
    public final ASTNode and_expr() throws RecognitionException {
        ASTNode v = null;
        int and_expr_StartIndex = input.index();
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return v; }
            // R.g:188:2: (l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )* )
            // R.g:188:4: l= not_expr ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )*
            {
            pushFollow(FOLLOW_not_expr_in_and_expr1143);
            l=not_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:189:5: ( ( ( and_operator )=>op= and_operator n_ r= not_expr ) )*
            loop21:
            do {
                int alt21=2;
                int LA21_0 = input.LA(1);

                if ( (LA21_0==AND) ) {
                    int LA21_2 = input.LA(2);

                    if ( (synpred9_R()) ) {
                        alt21=1;
                    }


                }
                else if ( (LA21_0==ELEMENTWISEAND) ) {
                    int LA21_3 = input.LA(2);

                    if ( (synpred9_R()) ) {
                        alt21=1;
                    }


                }


                switch (alt21) {
            	case 1 :
            	    // R.g:189:6: ( ( and_operator )=>op= and_operator n_ r= not_expr )
            	    {
            	    // R.g:189:6: ( ( and_operator )=>op= and_operator n_ r= not_expr )
            	    // R.g:189:7: ( and_operator )=>op= and_operator n_ r= not_expr
            	    {
            	    pushFollow(FOLLOW_and_operator_in_and_expr1159);
            	    op=and_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_and_expr1161);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_not_expr_in_and_expr1165);
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
            	    break loop21;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 21, and_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "and_expr"


    // $ANTLR start "not_expr"
    // R.g:191:1: not_expr returns [ASTNode v] : ( NOT n_ l= not_expr | b= comp_expr );
    public final ASTNode not_expr() throws RecognitionException {
        ASTNode v = null;
        int not_expr_StartIndex = input.index();
        ASTNode l = null;

        ASTNode b = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return v; }
            // R.g:192:2: ( NOT n_ l= not_expr | b= comp_expr )
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==NOT) ) {
                alt22=1;
            }
            else if ( ((LA22_0>=NEXT && LA22_0<=LBRACE)||(LA22_0>=IF && LA22_0<=LPAR)||(LA22_0>=WHILE && LA22_0<=ID)||(LA22_0>=REPEAT && LA22_0<=FUNCTION)||(LA22_0>=VARIATIC && LA22_0<=DD)||(LA22_0>=PLUS && LA22_0<=MINUS)||LA22_0==NULL||(LA22_0>=INTEGER && LA22_0<=NA)) ) {
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
                    // R.g:192:4: NOT n_ l= not_expr
                    {
                    match(input,NOT,FOLLOW_NOT_in_not_expr1185); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_not_expr1187);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_not_expr_in_not_expr1191);
                    l=not_expr();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = UnaryOperation.create(UnaryOperator.NOT, l);
                    }

                    }
                    break;
                case 2 :
                    // R.g:193:4: b= comp_expr
                    {
                    pushFollow(FOLLOW_comp_expr_in_not_expr1200);
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
            if ( state.backtracking>0 ) { memoize(input, 22, not_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "not_expr"


    // $ANTLR start "comp_expr"
    // R.g:195:1: comp_expr returns [ASTNode v] : l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )* ;
    public final ASTNode comp_expr() throws RecognitionException {
        ASTNode v = null;
        int comp_expr_StartIndex = input.index();
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return v; }
            // R.g:196:2: (l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )* )
            // R.g:196:4: l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )*
            {
            pushFollow(FOLLOW_add_expr_in_comp_expr1218);
            l=add_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:197:5: ( ( ( comp_operator )=>op= comp_operator n_ r= add_expr ) )*
            loop23:
            do {
                int alt23=2;
                switch ( input.LA(1) ) {
                case GT:
                    {
                    int LA23_2 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case GE:
                    {
                    int LA23_3 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case LT:
                    {
                    int LA23_4 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case LE:
                    {
                    int LA23_5 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case EQ:
                    {
                    int LA23_6 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt23=1;
                    }


                    }
                    break;
                case NE:
                    {
                    int LA23_7 = input.LA(2);

                    if ( (synpred10_R()) ) {
                        alt23=1;
                    }


                    }
                    break;

                }

                switch (alt23) {
            	case 1 :
            	    // R.g:197:6: ( ( comp_operator )=>op= comp_operator n_ r= add_expr )
            	    {
            	    // R.g:197:6: ( ( comp_operator )=>op= comp_operator n_ r= add_expr )
            	    // R.g:197:7: ( comp_operator )=>op= comp_operator n_ r= add_expr
            	    {
            	    pushFollow(FOLLOW_comp_operator_in_comp_expr1234);
            	    op=comp_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_comp_expr1236);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_add_expr_in_comp_expr1240);
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
            	    break loop23;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 23, comp_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "comp_expr"


    // $ANTLR start "add_expr"
    // R.g:199:1: add_expr returns [ASTNode v] : l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )* ;
    public final ASTNode add_expr() throws RecognitionException {
        ASTNode v = null;
        int add_expr_StartIndex = input.index();
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return v; }
            // R.g:200:2: (l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )* )
            // R.g:200:4: l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            {
            pushFollow(FOLLOW_mult_expr_in_add_expr1265);
            l=mult_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:201:3: ( ( ( add_operator )=>op= add_operator n_ r= mult_expr ) )*
            loop24:
            do {
                int alt24=2;
                int LA24_0 = input.LA(1);

                if ( (LA24_0==PLUS) ) {
                    int LA24_2 = input.LA(2);

                    if ( (synpred11_R()) ) {
                        alt24=1;
                    }


                }
                else if ( (LA24_0==MINUS) ) {
                    int LA24_3 = input.LA(2);

                    if ( (synpred11_R()) ) {
                        alt24=1;
                    }


                }


                switch (alt24) {
            	case 1 :
            	    // R.g:201:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    {
            	    // R.g:201:4: ( ( add_operator )=>op= add_operator n_ r= mult_expr )
            	    // R.g:201:5: ( add_operator )=>op= add_operator n_ r= mult_expr
            	    {
            	    pushFollow(FOLLOW_add_operator_in_add_expr1279);
            	    op=add_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_add_expr1281);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_mult_expr_in_add_expr1285);
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
            	    break loop24;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 24, add_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "add_expr"


    // $ANTLR start "mult_expr"
    // R.g:203:1: mult_expr returns [ASTNode v] : l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )* ;
    public final ASTNode mult_expr() throws RecognitionException {
        ASTNode v = null;
        int mult_expr_StartIndex = input.index();
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return v; }
            // R.g:204:2: (l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )* )
            // R.g:204:4: l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            {
            pushFollow(FOLLOW_operator_expr_in_mult_expr1307);
            l=operator_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:205:2: ( ( ( mult_operator )=>op= mult_operator n_ r= operator_expr ) )*
            loop25:
            do {
                int alt25=2;
                switch ( input.LA(1) ) {
                case MULT:
                    {
                    int LA25_2 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt25=1;
                    }


                    }
                    break;
                case DIV:
                    {
                    int LA25_3 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt25=1;
                    }


                    }
                    break;
                case MOD:
                    {
                    int LA25_4 = input.LA(2);

                    if ( (synpred12_R()) ) {
                        alt25=1;
                    }


                    }
                    break;

                }

                switch (alt25) {
            	case 1 :
            	    // R.g:205:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    {
            	    // R.g:205:3: ( ( mult_operator )=>op= mult_operator n_ r= operator_expr )
            	    // R.g:205:4: ( mult_operator )=>op= mult_operator n_ r= operator_expr
            	    {
            	    pushFollow(FOLLOW_mult_operator_in_mult_expr1320);
            	    op=mult_operator();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_mult_expr1322);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_operator_expr_in_mult_expr1326);
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
            	    break loop25;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 25, mult_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "mult_expr"


    // $ANTLR start "operator_expr"
    // R.g:207:1: operator_expr returns [ASTNode v] : l= colon_expr ( ( OP )=>opc= OP n_ r= colon_expr )* ;
    public final ASTNode operator_expr() throws RecognitionException {
        ASTNode v = null;
        int operator_expr_StartIndex = input.index();
        Token opc=null;
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return v; }
            // R.g:208:2: (l= colon_expr ( ( OP )=>opc= OP n_ r= colon_expr )* )
            // R.g:208:4: l= colon_expr ( ( OP )=>opc= OP n_ r= colon_expr )*
            {
            pushFollow(FOLLOW_colon_expr_in_operator_expr1348);
            l=colon_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:209:2: ( ( OP )=>opc= OP n_ r= colon_expr )*
            loop26:
            do {
                int alt26=2;
                int LA26_0 = input.LA(1);

                if ( (LA26_0==OP) ) {
                    int LA26_2 = input.LA(2);

                    if ( (synpred13_R()) ) {
                        alt26=1;
                    }


                }


                switch (alt26) {
            	case 1 :
            	    // R.g:209:3: ( OP )=>opc= OP n_ r= colon_expr
            	    {
            	    opc=(Token)match(input,OP,FOLLOW_OP_in_operator_expr1360); if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_operator_expr1362);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_colon_expr_in_operator_expr1366);
            	    r=colon_expr();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	       v = BinaryOperation.create((opc!=null?opc.getText():null), v, r); 
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
            if ( state.backtracking>0 ) { memoize(input, 26, operator_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "operator_expr"


    // $ANTLR start "colon_expr"
    // R.g:211:1: colon_expr returns [ASTNode v] : l= unary_expression ( ( ( COLON )=>op= COLON n_ r= unary_expression ) )* ;
    public final ASTNode colon_expr() throws RecognitionException {
        ASTNode v = null;
        int colon_expr_StartIndex = input.index();
        Token op=null;
        ASTNode l = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return v; }
            // R.g:212:2: (l= unary_expression ( ( ( COLON )=>op= COLON n_ r= unary_expression ) )* )
            // R.g:212:4: l= unary_expression ( ( ( COLON )=>op= COLON n_ r= unary_expression ) )*
            {
            pushFollow(FOLLOW_unary_expression_in_colon_expr1388);
            l=unary_expression();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = l ;
            }
            // R.g:213:2: ( ( ( COLON )=>op= COLON n_ r= unary_expression ) )*
            loop27:
            do {
                int alt27=2;
                int LA27_0 = input.LA(1);

                if ( (LA27_0==COLON) ) {
                    int LA27_2 = input.LA(2);

                    if ( (synpred14_R()) ) {
                        alt27=1;
                    }


                }


                switch (alt27) {
            	case 1 :
            	    // R.g:213:3: ( ( COLON )=>op= COLON n_ r= unary_expression )
            	    {
            	    // R.g:213:3: ( ( COLON )=>op= COLON n_ r= unary_expression )
            	    // R.g:213:4: ( COLON )=>op= COLON n_ r= unary_expression
            	    {
            	    op=(Token)match(input,COLON,FOLLOW_COLON_in_colon_expr1401); if (state.failed) return v;
            	    pushFollow(FOLLOW_n__in_colon_expr1403);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    pushFollow(FOLLOW_unary_expression_in_colon_expr1407);
            	    r=unary_expression();

            	    state._fsp--;
            	    if (state.failed) return v;
            	    if ( state.backtracking==0 ) {
            	       v = BinaryOperation.create(BinaryOperator.COLON, v, r);
            	    }

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
            if ( state.backtracking>0 ) { memoize(input, 27, colon_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "colon_expr"


    // $ANTLR start "unary_expression"
    // R.g:215:1: unary_expression returns [ASTNode v] : ( PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | b= power_expr );
    public final ASTNode unary_expression() throws RecognitionException {
        ASTNode v = null;
        int unary_expression_StartIndex = input.index();
        ASTNode l = null;

        ASTNode b = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return v; }
            // R.g:216:2: ( PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | b= power_expr )
            int alt28=3;
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
                alt28=3;
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
                    match(input,PLUS,FOLLOW_PLUS_in_unary_expression1427); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_unary_expression1429);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_unary_expression_in_unary_expression1433);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = UnaryOperation.create(UnaryOperator.PLUS, l);
                    }

                    }
                    break;
                case 2 :
                    // R.g:217:4: MINUS n_ l= unary_expression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unary_expression1440); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_unary_expression1442);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_unary_expression_in_unary_expression1446);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = UnaryOperation.create(UnaryOperator.MINUS, l);
                    }

                    }
                    break;
                case 3 :
                    // R.g:218:4: b= power_expr
                    {
                    pushFollow(FOLLOW_power_expr_in_unary_expression1455);
                    b=power_expr();

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


    // $ANTLR start "power_expr"
    // R.g:220:1: power_expr returns [ASTNode v] : l= basic_expr ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) | ) ;
    public final ASTNode power_expr() throws RecognitionException {
        ASTNode v = null;
        int power_expr_StartIndex = input.index();
        ASTNode l = null;

        BinaryOperator op = null;

        ASTNode r = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return v; }
            // R.g:221:2: (l= basic_expr ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) | ) )
            // R.g:221:4: l= basic_expr ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) | )
            {
            pushFollow(FOLLOW_basic_expr_in_power_expr1473);
            l=basic_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
              v =l;
            }
            // R.g:222:5: ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) | )
            int alt29=2;
            int LA29_0 = input.LA(1);

            if ( (LA29_0==CARRET) ) {
                int LA29_1 = input.LA(2);

                if ( (synpred15_R()) ) {
                    alt29=1;
                }
                else if ( (true) ) {
                    alt29=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 29, 1, input);

                    throw nvae;
                }
            }
            else if ( (LA29_0==EOF||(LA29_0>=NEWLINE && LA29_0<=SEMICOLON)||(LA29_0>=RBRACE && LA29_0<=ASSIGN)||(LA29_0>=LPAR && LA29_0<=ELSE)||LA29_0==COMMA||LA29_0==TILDE||(LA29_0>=OP && LA29_0<=LBB)||(LA29_0>=OR && LA29_0<=MOD)) ) {
                alt29=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 29, 0, input);

                throw nvae;
            }
            switch (alt29) {
                case 1 :
                    // R.g:222:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    {
                    // R.g:222:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    // R.g:222:7: ( power_operator )=>op= power_operator n_ r= power_expr
                    {
                    pushFollow(FOLLOW_power_operator_in_power_expr1489);
                    op=power_operator();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_power_expr1491);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_power_expr_in_power_expr1495);
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
                    // R.g:223:6: 
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
            if ( state.backtracking>0 ) { memoize(input, 29, power_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "power_expr"


    // $ANTLR start "basic_expr"
    // R.g:225:1: basic_expr returns [ASTNode v] : lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) ;
    public final ASTNode basic_expr() throws RecognitionException {
        ASTNode v = null;
        int basic_expr_StartIndex = input.index();
        ASTNode lhs = null;

        ASTNode subset = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return v; }
            // R.g:226:2: (lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) )
            // R.g:226:4: lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            {
            pushFollow(FOLLOW_simple_expr_in_basic_expr1525);
            lhs=simple_expr();

            state._fsp--;
            if (state.failed) return v;
            if ( state.backtracking==0 ) {
               v = lhs; 
            }
            // R.g:227:2: ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            int alt31=2;
            alt31 = dfa31.predict(input);
            switch (alt31) {
                case 1 :
                    // R.g:227:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    {
                    // R.g:227:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    int cnt30=0;
                    loop30:
                    do {
                        int alt30=2;
                        switch ( input.LA(1) ) {
                        case FIELD:
                            {
                            int LA30_2 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt30=1;
                            }


                            }
                            break;
                        case AT:
                            {
                            int LA30_3 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt30=1;
                            }


                            }
                            break;
                        case LBRAKET:
                            {
                            int LA30_4 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt30=1;
                            }


                            }
                            break;
                        case LBB:
                            {
                            int LA30_5 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt30=1;
                            }


                            }
                            break;
                        case LPAR:
                            {
                            int LA30_6 = input.LA(2);

                            if ( (synpred16_R()) ) {
                                alt30=1;
                            }


                            }
                            break;

                        }

                        switch (alt30) {
                    	case 1 :
                    	    // R.g:227:4: ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v]
                    	    {
                    	    pushFollow(FOLLOW_expr_subset_in_basic_expr1546);
                    	    subset=expr_subset(v);

                    	    state._fsp--;
                    	    if (state.failed) return v;
                    	    if ( state.backtracking==0 ) {
                    	       v = subset; 
                    	    }

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
                    // R.g:227:76: ( n_ )=>
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
            if ( state.backtracking>0 ) { memoize(input, 30, basic_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "basic_expr"


    // $ANTLR start "expr_subset"
    // R.g:229:1: expr_subset[ASTNode i] returns [ASTNode v] : ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= args RBRAKET ) | ( LBB subscript= args RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) );
    public final ASTNode expr_subset(ASTNode i) throws RecognitionException {
        ASTNode v = null;
        int expr_subset_StartIndex = input.index();
        Token name = null;

        ArgumentList subset = null;

        ArgumentList subscript = null;

        ArgumentList a = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return v; }
            // R.g:230:5: ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= args RBRAKET ) | ( LBB subscript= args RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) )
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
                    // R.g:230:7: ( FIELD n_ name= id )
                    {
                    // R.g:230:7: ( FIELD n_ name= id )
                    // R.g:230:8: FIELD n_ name= id
                    {
                    match(input,FIELD,FOLLOW_FIELD_in_expr_subset1579); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_expr_subset1581);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_id_in_expr_subset1585);
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
                    // R.g:231:7: ( AT n_ name= id )
                    {
                    // R.g:231:7: ( AT n_ name= id )
                    // R.g:231:8: AT n_ name= id
                    {
                    match(input,AT,FOLLOW_AT_in_expr_subset1598); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_expr_subset1600);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_id_in_expr_subset1604);
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
                    // R.g:232:7: ( LBRAKET subset= args RBRAKET )
                    {
                    // R.g:232:7: ( LBRAKET subset= args RBRAKET )
                    // R.g:232:8: LBRAKET subset= args RBRAKET
                    {
                    match(input,LBRAKET,FOLLOW_LBRAKET_in_expr_subset1618); if (state.failed) return v;
                    pushFollow(FOLLOW_args_in_expr_subset1622);
                    subset=args();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1624); if (state.failed) return v;

                    }

                    if ( state.backtracking==0 ) {
                       v = Call.create(CallOperator.SUBSET, i, subset); ArgumentList.Default.updateParent(v, subset); 
                    }

                    }
                    break;
                case 4 :
                    // R.g:233:7: ( LBB subscript= args RBRAKET RBRAKET )
                    {
                    // R.g:233:7: ( LBB subscript= args RBRAKET RBRAKET )
                    // R.g:233:8: LBB subscript= args RBRAKET RBRAKET
                    {
                    match(input,LBB,FOLLOW_LBB_in_expr_subset1636); if (state.failed) return v;
                    pushFollow(FOLLOW_args_in_expr_subset1640);
                    subscript=args();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1642); if (state.failed) return v;
                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1644); if (state.failed) return v;

                    }

                    if ( state.backtracking==0 ) {
                       v = Call.create(CallOperator.SUBSCRIPT, i, subscript); ArgumentList.Default.updateParent(v, subscript); 
                    }

                    }
                    break;
                case 5 :
                    // R.g:235:7: ( LPAR a= args RPAR )
                    {
                    // R.g:235:7: ( LPAR a= args RPAR )
                    // R.g:235:8: LPAR a= args RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_expr_subset1661); if (state.failed) return v;
                    pushFollow(FOLLOW_args_in_expr_subset1665);
                    a=args();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,RPAR,FOLLOW_RPAR_in_expr_subset1667); if (state.failed) return v;

                    }

                    if ( state.backtracking==0 ) {
                       v = Call.create(i, a);  ArgumentList.Default.updateParent(v, a); 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 31, expr_subset_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "expr_subset"


    // $ANTLR start "simple_expr"
    // R.g:238:1: simple_expr returns [ASTNode v] : (i= id | b= bool | DD | NULL | num= number | cstr= conststring | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR | s= sequence | e= expr_wo_assign );
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
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return v; }
            // R.g:239:2: (i= id | b= bool | DD | NULL | num= number | cstr= conststring | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR | s= sequence | e= expr_wo_assign )
            int alt33=11;
            alt33 = dfa33.predict(input);
            switch (alt33) {
                case 1 :
                    // R.g:239:4: i= id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1696);
                    i=id();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = AccessVariable.create(i.getText()); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:240:4: b= bool
                    {
                    pushFollow(FOLLOW_bool_in_simple_expr1705);
                    b=bool();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = b; 
                    }

                    }
                    break;
                case 3 :
                    // R.g:241:4: DD
                    {
                    match(input,DD,FOLLOW_DD_in_simple_expr1712); if (state.failed) return v;

                    }
                    break;
                case 4 :
                    // R.g:242:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_simple_expr1717); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = Constant.getNull(); 
                    }

                    }
                    break;
                case 5 :
                    // R.g:243:4: num= number
                    {
                    pushFollow(FOLLOW_number_in_simple_expr1726);
                    num=number();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = num; 
                    }

                    }
                    break;
                case 6 :
                    // R.g:244:4: cstr= conststring
                    {
                    pushFollow(FOLLOW_conststring_in_simple_expr1735);
                    cstr=conststring();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = cstr; 
                    }

                    }
                    break;
                case 7 :
                    // R.g:245:4: id NS_GET n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1742);
                    id();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,NS_GET,FOLLOW_NS_GET_in_simple_expr1744); if (state.failed) return v;
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
                    // R.g:246:4: id NS_GET_INT n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1753);
                    id();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,NS_GET_INT,FOLLOW_NS_GET_INT_in_simple_expr1755); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_simple_expr1757);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_id_in_simple_expr1759);
                    id();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 9 :
                    // R.g:247:4: LPAR n_ ea= expr_or_assign n_ RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_simple_expr1764); if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_simple_expr1766);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_expr_or_assign_in_simple_expr1772);
                    ea=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;
                    pushFollow(FOLLOW_n__in_simple_expr1774);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    match(input,RPAR,FOLLOW_RPAR_in_simple_expr1776); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = ea; 
                    }

                    }
                    break;
                case 10 :
                    // R.g:248:4: s= sequence
                    {
                    pushFollow(FOLLOW_sequence_in_simple_expr1787);
                    s=sequence();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                       v = s; 
                    }

                    }
                    break;
                case 11 :
                    // R.g:249:4: e= expr_wo_assign
                    {
                    pushFollow(FOLLOW_expr_wo_assign_in_simple_expr1798);
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
            if ( state.backtracking>0 ) { memoize(input, 32, simple_expr_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "simple_expr"


    // $ANTLR start "number"
    // R.g:251:1: number returns [ASTNode n] : (i= INTEGER | d= DOUBLE | c= COMPLEX );
    public final ASTNode number() throws RecognitionException {
        ASTNode n = null;
        int number_StartIndex = input.index();
        Token i=null;
        Token d=null;
        Token c=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return n; }
            // R.g:252:5: (i= INTEGER | d= DOUBLE | c= COMPLEX )
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
                    // R.g:252:7: i= INTEGER
                    {
                    i=(Token)match(input,INTEGER,FOLLOW_INTEGER_in_number1819); if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = Constant.createIntConstant((i!=null?i.getText():null)); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:253:7: d= DOUBLE
                    {
                    d=(Token)match(input,DOUBLE,FOLLOW_DOUBLE_in_number1831); if (state.failed) return n;
                    if ( state.backtracking==0 ) {
                       n = Constant.createDoubleConstant((d!=null?d.getText():null)); 
                    }

                    }
                    break;
                case 3 :
                    // R.g:254:7: c= COMPLEX
                    {
                    c=(Token)match(input,COMPLEX,FOLLOW_COMPLEX_in_number1843); if (state.failed) return n;
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
            if ( state.backtracking>0 ) { memoize(input, 33, number_StartIndex); }
        }
        return n;
    }
    // $ANTLR end "number"


    // $ANTLR start "conststring"
    // R.g:256:1: conststring returns [ASTNode n] : s= STRING ;
    public final ASTNode conststring() throws RecognitionException {
        ASTNode n = null;
        int conststring_StartIndex = input.index();
        Token s=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return n; }
            // R.g:257:5: (s= STRING )
            // R.g:257:7: s= STRING
            {
            s=(Token)match(input,STRING,FOLLOW_STRING_in_conststring1867); if (state.failed) return n;
            if ( state.backtracking==0 ) {
               n = Constant.createStringConstant((s!=null?s.getText():null)); 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 34, conststring_StartIndex); }
        }
        return n;
    }
    // $ANTLR end "conststring"


    // $ANTLR start "id"
    // R.g:259:1: id returns [Token t] : (i= ID | v= VARIATIC );
    public final Token id() throws RecognitionException {
        Token t = null;
        int id_StartIndex = input.index();
        Token i=null;
        Token v=null;

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return t; }
            // R.g:260:5: (i= ID | v= VARIATIC )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==ID) ) {
                alt35=1;
            }
            else if ( (LA35_0==VARIATIC) ) {
                alt35=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return t;}
                NoViableAltException nvae =
                    new NoViableAltException("", 35, 0, input);

                throw nvae;
            }
            switch (alt35) {
                case 1 :
                    // R.g:260:7: i= ID
                    {
                    i=(Token)match(input,ID,FOLLOW_ID_in_id1891); if (state.failed) return t;
                    if ( state.backtracking==0 ) {
                       t = i; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:261:7: v= VARIATIC
                    {
                    v=(Token)match(input,VARIATIC,FOLLOW_VARIATIC_in_id1903); if (state.failed) return t;
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
            if ( state.backtracking>0 ) { memoize(input, 35, id_StartIndex); }
        }
        return t;
    }
    // $ANTLR end "id"


    // $ANTLR start "bool"
    // R.g:263:1: bool returns [ASTNode v] : ( TRUE | FALSE | NA );
    public final ASTNode bool() throws RecognitionException {
        ASTNode v = null;
        int bool_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return v; }
            // R.g:264:5: ( TRUE | FALSE | NA )
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
                    // R.g:264:7: TRUE
                    {
                    match(input,TRUE,FOLLOW_TRUE_in_bool1925); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = Constant.createBoolConstant(1); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:265:7: FALSE
                    {
                    match(input,FALSE,FOLLOW_FALSE_in_bool1935); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = Constant.createBoolConstant(0); 
                    }

                    }
                    break;
                case 3 :
                    // R.g:266:7: NA
                    {
                    match(input,NA,FOLLOW_NA_in_bool1945); if (state.failed) return v;
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
            if ( state.backtracking>0 ) { memoize(input, 36, bool_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "bool"


    // $ANTLR start "or_operator"
    // R.g:268:1: or_operator returns [BinaryOperator v] : ( OR | ELEMENTWISEOR );
    public final BinaryOperator or_operator() throws RecognitionException {
        BinaryOperator v = null;
        int or_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return v; }
            // R.g:269:2: ( OR | ELEMENTWISEOR )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==OR) ) {
                alt37=1;
            }
            else if ( (LA37_0==ELEMENTWISEOR) ) {
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
                    // R.g:269:4: OR
                    {
                    match(input,OR,FOLLOW_OR_in_or_operator1964); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.OR; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:270:5: ELEMENTWISEOR
                    {
                    match(input,ELEMENTWISEOR,FOLLOW_ELEMENTWISEOR_in_or_operator1981); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.ELEMENTWISEOR; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 37, or_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "or_operator"


    // $ANTLR start "and_operator"
    // R.g:271:1: and_operator returns [BinaryOperator v] : ( AND | ELEMENTWISEAND );
    public final BinaryOperator and_operator() throws RecognitionException {
        BinaryOperator v = null;
        int and_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return v; }
            // R.g:272:2: ( AND | ELEMENTWISEAND )
            int alt38=2;
            int LA38_0 = input.LA(1);

            if ( (LA38_0==AND) ) {
                alt38=1;
            }
            else if ( (LA38_0==ELEMENTWISEAND) ) {
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
                    // R.g:272:4: AND
                    {
                    match(input,AND,FOLLOW_AND_in_and_operator1997); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.AND; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:273:4: ELEMENTWISEAND
                    {
                    match(input,ELEMENTWISEAND,FOLLOW_ELEMENTWISEAND_in_and_operator2013); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.ELEMENTWISEAND; 
                    }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 38, and_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "and_operator"


    // $ANTLR start "comp_operator"
    // R.g:274:1: comp_operator returns [BinaryOperator v] : ( GT | GE | LT | LE | EQ | NE );
    public final BinaryOperator comp_operator() throws RecognitionException {
        BinaryOperator v = null;
        int comp_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return v; }
            // R.g:275:2: ( GT | GE | LT | LE | EQ | NE )
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
                    // R.g:275:4: GT
                    {
                    match(input,GT,FOLLOW_GT_in_comp_operator2029); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.GT; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:276:4: GE
                    {
                    match(input,GE,FOLLOW_GE_in_comp_operator2036); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.GE; 
                    }

                    }
                    break;
                case 3 :
                    // R.g:277:4: LT
                    {
                    match(input,LT,FOLLOW_LT_in_comp_operator2043); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.LT; 
                    }

                    }
                    break;
                case 4 :
                    // R.g:278:4: LE
                    {
                    match(input,LE,FOLLOW_LE_in_comp_operator2050); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.LE; 
                    }

                    }
                    break;
                case 5 :
                    // R.g:279:4: EQ
                    {
                    match(input,EQ,FOLLOW_EQ_in_comp_operator2057); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.EQ; 
                    }

                    }
                    break;
                case 6 :
                    // R.g:280:4: NE
                    {
                    match(input,NE,FOLLOW_NE_in_comp_operator2064); if (state.failed) return v;
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
            if ( state.backtracking>0 ) { memoize(input, 39, comp_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "comp_operator"


    // $ANTLR start "add_operator"
    // R.g:281:1: add_operator returns [BinaryOperator v] : ( PLUS | MINUS );
    public final BinaryOperator add_operator() throws RecognitionException {
        BinaryOperator v = null;
        int add_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return v; }
            // R.g:282:2: ( PLUS | MINUS )
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
                    // R.g:282:4: PLUS
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_add_operator2078); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.ADD; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:283:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_add_operator2085); if (state.failed) return v;
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
            if ( state.backtracking>0 ) { memoize(input, 40, add_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "add_operator"


    // $ANTLR start "mult_operator"
    // R.g:284:1: mult_operator returns [BinaryOperator v] : ( MULT | DIV | MOD );
    public final BinaryOperator mult_operator() throws RecognitionException {
        BinaryOperator v = null;
        int mult_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return v; }
            // R.g:285:2: ( MULT | DIV | MOD )
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
                    // R.g:285:4: MULT
                    {
                    match(input,MULT,FOLLOW_MULT_in_mult_operator2100); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.MULT; 
                    }

                    }
                    break;
                case 2 :
                    // R.g:286:4: DIV
                    {
                    match(input,DIV,FOLLOW_DIV_in_mult_operator2107); if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v = BinaryOperator.DIV; 
                    }

                    }
                    break;
                case 3 :
                    // R.g:287:4: MOD
                    {
                    match(input,MOD,FOLLOW_MOD_in_mult_operator2115); if (state.failed) return v;
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
            if ( state.backtracking>0 ) { memoize(input, 41, mult_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "mult_operator"


    // $ANTLR start "power_operator"
    // R.g:289:1: power_operator returns [BinaryOperator v] : CARRET ;
    public final BinaryOperator power_operator() throws RecognitionException {
        BinaryOperator v = null;
        int power_operator_StartIndex = input.index();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return v; }
            // R.g:290:2: ( CARRET )
            // R.g:290:4: CARRET
            {
            match(input,CARRET,FOLLOW_CARRET_in_power_operator2132); if (state.failed) return v;
            if ( state.backtracking==0 ) {
              v = BinaryOperator.POW; 
            }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 42, power_operator_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "power_operator"


    // $ANTLR start "args"
    // R.g:292:1: args returns [ArgumentList v] : ( n_ ( arg_expr[v] n_ ( COMMA ( | n_ arg_expr[v] ) n_ )* )? | n_ ( COMMA ( | n_ arg_expr[v] ) n_ )+ );
    public final ArgumentList args() throws RecognitionException {
        ArgumentList v = null;
        int args_StartIndex = input.index();
         v = new ArgumentList.Default(); 
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 43) ) { return v; }
            // R.g:294:2: ( n_ ( arg_expr[v] n_ ( COMMA ( | n_ arg_expr[v] ) n_ )* )? | n_ ( COMMA ( | n_ arg_expr[v] ) n_ )+ )
            int alt47=2;
            alt47 = dfa47.predict(input);
            switch (alt47) {
                case 1 :
                    // R.g:294:4: n_ ( arg_expr[v] n_ ( COMMA ( | n_ arg_expr[v] ) n_ )* )?
                    {
                    pushFollow(FOLLOW_n__in_args2153);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    // R.g:294:7: ( arg_expr[v] n_ ( COMMA ( | n_ arg_expr[v] ) n_ )* )?
                    int alt44=2;
                    int LA44_0 = input.LA(1);

                    if ( ((LA44_0>=NEXT && LA44_0<=LBRACE)||(LA44_0>=IF && LA44_0<=LPAR)||(LA44_0>=WHILE && LA44_0<=ID)||(LA44_0>=REPEAT && LA44_0<=FUNCTION)||(LA44_0>=VARIATIC && LA44_0<=NOT)||(LA44_0>=PLUS && LA44_0<=MINUS)||LA44_0==NULL||(LA44_0>=INTEGER && LA44_0<=NA)) ) {
                        alt44=1;
                    }
                    switch (alt44) {
                        case 1 :
                            // R.g:294:8: arg_expr[v] n_ ( COMMA ( | n_ arg_expr[v] ) n_ )*
                            {
                            pushFollow(FOLLOW_arg_expr_in_args2156);
                            arg_expr(v);

                            state._fsp--;
                            if (state.failed) return v;
                            pushFollow(FOLLOW_n__in_args2159);
                            n_();

                            state._fsp--;
                            if (state.failed) return v;
                            // R.g:294:24: ( COMMA ( | n_ arg_expr[v] ) n_ )*
                            loop43:
                            do {
                                int alt43=2;
                                int LA43_0 = input.LA(1);

                                if ( (LA43_0==COMMA) ) {
                                    alt43=1;
                                }


                                switch (alt43) {
                            	case 1 :
                            	    // R.g:294:25: COMMA ( | n_ arg_expr[v] ) n_
                            	    {
                            	    match(input,COMMA,FOLLOW_COMMA_in_args2163); if (state.failed) return v;
                            	    // R.g:294:31: ( | n_ arg_expr[v] )
                            	    int alt42=2;
                            	    alt42 = dfa42.predict(input);
                            	    switch (alt42) {
                            	        case 1 :
                            	            // R.g:294:33: 
                            	            {
                            	            if ( state.backtracking==0 ) {
                            	               v.add((ASTNode)null); 
                            	            }

                            	            }
                            	            break;
                            	        case 2 :
                            	            // R.g:294:62: n_ arg_expr[v]
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
                            break;

                    }


                    }
                    break;
                case 2 :
                    // R.g:295:4: n_ ( COMMA ( | n_ arg_expr[v] ) n_ )+
                    {
                    pushFollow(FOLLOW_n__in_args2187);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;
                    if ( state.backtracking==0 ) {
                      v.add((ASTNode)null);
                    }
                    // R.g:295:32: ( COMMA ( | n_ arg_expr[v] ) n_ )+
                    int cnt46=0;
                    loop46:
                    do {
                        int alt46=2;
                        int LA46_0 = input.LA(1);

                        if ( (LA46_0==COMMA) ) {
                            alt46=1;
                        }


                        switch (alt46) {
                    	case 1 :
                    	    // R.g:295:33: COMMA ( | n_ arg_expr[v] ) n_
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_args2192); if (state.failed) return v;
                    	    // R.g:295:39: ( | n_ arg_expr[v] )
                    	    int alt45=2;
                    	    alt45 = dfa45.predict(input);
                    	    switch (alt45) {
                    	        case 1 :
                    	            // R.g:295:40: 
                    	            {
                    	            if ( state.backtracking==0 ) {
                    	              v.add((ASTNode)null);
                    	            }

                    	            }
                    	            break;
                    	        case 2 :
                    	            // R.g:295:67: n_ arg_expr[v]
                    	            {
                    	            pushFollow(FOLLOW_n__in_args2199);
                    	            n_();

                    	            state._fsp--;
                    	            if (state.failed) return v;
                    	            pushFollow(FOLLOW_arg_expr_in_args2201);
                    	            arg_expr(v);

                    	            state._fsp--;
                    	            if (state.failed) return v;

                    	            }
                    	            break;

                    	    }

                    	    pushFollow(FOLLOW_n__in_args2205);
                    	    n_();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt46 >= 1 ) break loop46;
                    	    if (state.backtracking>0) {state.failed=true; return v;}
                                EarlyExitException eee =
                                    new EarlyExitException(46, input);
                                throw eee;
                        }
                        cnt46++;
                    } while (true);


                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }
        finally {
            if ( state.backtracking>0 ) { memoize(input, 43, args_StartIndex); }
        }
        return v;
    }
    // $ANTLR end "args"


    // $ANTLR start "arg_expr"
    // R.g:297:1: arg_expr[ArgumentList l] : (e= expr | name= id n_ ASSIGN n_ val= expr | name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );
    public final void arg_expr(ArgumentList l) throws RecognitionException {
        int arg_expr_StartIndex = input.index();
        ASTNode e = null;

        Token name = null;

        ASTNode val = null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 44) ) { return ; }
            // R.g:298:2: (e= expr | name= id n_ ASSIGN n_ val= expr | name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN )
            int alt48=5;
            alt48 = dfa48.predict(input);
            switch (alt48) {
                case 1 :
                    // R.g:298:4: e= expr
                    {
                    pushFollow(FOLLOW_expr_in_arg_expr2221);
                    e=expr();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add(e); 
                    }

                    }
                    break;
                case 2 :
                    // R.g:299:4: name= id n_ ASSIGN n_ val= expr
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2230);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2232);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2234); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2236);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expr_in_arg_expr2240);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add(name.getText(), val); 
                    }

                    }
                    break;
                case 3 :
                    // R.g:300:4: name= id n_ ASSIGN
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2249);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2251);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2253); if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       l.add(name.getText(), null); 
                    }

                    }
                    break;
                case 4 :
                    // R.g:301:4: NULL n_ ASSIGN n_ val= expr
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2261); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2263);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2265); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2267);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    pushFollow(FOLLOW_expr_in_arg_expr2271);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return ;
                    if ( state.backtracking==0 ) {
                       Utils.nyi(); 
                    }

                    }
                    break;
                case 5 :
                    // R.g:302:4: NULL n_ ASSIGN
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2278); if (state.failed) return ;
                    pushFollow(FOLLOW_n__in_arg_expr2280);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;
                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2282); if (state.failed) return ;
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
            if ( state.backtracking>0 ) { memoize(input, 44, arg_expr_StartIndex); }
        }
        return ;
    }
    // $ANTLR end "arg_expr"

    // $ANTLR start synpred1_R
    public final void synpred1_R_fragment() throws RecognitionException {   
        // R.g:136:5: ( ARROW )
        // R.g:136:6: ARROW
        {
        match(input,ARROW,FOLLOW_ARROW_in_synpred1_R552); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_R

    // $ANTLR start synpred2_R
    public final void synpred2_R_fragment() throws RecognitionException {   
        // R.g:137:5: ( SUPER_ARROW )
        // R.g:137:6: SUPER_ARROW
        {
        match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_synpred2_R570); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_R

    // $ANTLR start synpred3_R
    public final void synpred3_R_fragment() throws RecognitionException {   
        // R.g:138:5: ( RIGHT_ARROW )
        // R.g:138:6: RIGHT_ARROW
        {
        match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_synpred3_R588); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred3_R

    // $ANTLR start synpred4_R
    public final void synpred4_R_fragment() throws RecognitionException {   
        // R.g:139:5: ( SUPER_RIGHT_ARROW )
        // R.g:139:6: SUPER_RIGHT_ARROW
        {
        match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_synpred4_R608); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred4_R

    // $ANTLR start synpred5_R
    public final void synpred5_R_fragment() throws RecognitionException {   
        // R.g:140:5: ( ASSIGN )
        // R.g:140:6: ASSIGN
        {
        match(input,ASSIGN,FOLLOW_ASSIGN_in_synpred5_R628); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred5_R

    // $ANTLR start synpred6_R
    public final void synpred6_R_fragment() throws RecognitionException {   
        // R.g:147:3: ( n_ ELSE )
        // R.g:147:4: n_ ELSE
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
        // R.g:178:5: ( TILDE )
        // R.g:178:6: TILDE
        {
        match(input,TILDE,FOLLOW_TILDE_in_synpred7_R1042); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred7_R

    // $ANTLR start synpred8_R
    public final void synpred8_R_fragment() throws RecognitionException {   
        // R.g:185:4: ( or_operator )
        // R.g:185:5: or_operator
        {
        pushFollow(FOLLOW_or_operator_in_synpred8_R1110);
        or_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred8_R

    // $ANTLR start synpred9_R
    public final void synpred9_R_fragment() throws RecognitionException {   
        // R.g:189:7: ( and_operator )
        // R.g:189:8: and_operator
        {
        pushFollow(FOLLOW_and_operator_in_synpred9_R1154);
        and_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred9_R

    // $ANTLR start synpred10_R
    public final void synpred10_R_fragment() throws RecognitionException {   
        // R.g:197:7: ( comp_operator )
        // R.g:197:8: comp_operator
        {
        pushFollow(FOLLOW_comp_operator_in_synpred10_R1229);
        comp_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred10_R

    // $ANTLR start synpred11_R
    public final void synpred11_R_fragment() throws RecognitionException {   
        // R.g:201:5: ( add_operator )
        // R.g:201:6: add_operator
        {
        pushFollow(FOLLOW_add_operator_in_synpred11_R1274);
        add_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred11_R

    // $ANTLR start synpred12_R
    public final void synpred12_R_fragment() throws RecognitionException {   
        // R.g:205:4: ( mult_operator )
        // R.g:205:5: mult_operator
        {
        pushFollow(FOLLOW_mult_operator_in_synpred12_R1315);
        mult_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred12_R

    // $ANTLR start synpred13_R
    public final void synpred13_R_fragment() throws RecognitionException {   
        // R.g:209:3: ( OP )
        // R.g:209:4: OP
        {
        match(input,OP,FOLLOW_OP_in_synpred13_R1355); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred13_R

    // $ANTLR start synpred14_R
    public final void synpred14_R_fragment() throws RecognitionException {   
        // R.g:213:4: ( COLON )
        // R.g:213:5: COLON
        {
        match(input,COLON,FOLLOW_COLON_in_synpred14_R1396); if (state.failed) return ;

        }
    }
    // $ANTLR end synpred14_R

    // $ANTLR start synpred15_R
    public final void synpred15_R_fragment() throws RecognitionException {   
        // R.g:222:7: ( power_operator )
        // R.g:222:8: power_operator
        {
        pushFollow(FOLLOW_power_operator_in_synpred15_R1484);
        power_operator();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred15_R

    // $ANTLR start synpred16_R
    public final void synpred16_R_fragment() throws RecognitionException {   
        // R.g:227:4: ( FIELD | AT | LBRAKET | LBB | LPAR )
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
        // R.g:227:76: ( n_ )
        // R.g:227:77: n_
        {
        pushFollow(FOLLOW_n__in_synpred17_R1556);
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


    protected DFA9 dfa9 = new DFA9(this);
    protected DFA13 dfa13 = new DFA13(this);
    protected DFA15 dfa15 = new DFA15(this);
    protected DFA17 dfa17 = new DFA17(this);
    protected DFA31 dfa31 = new DFA31(this);
    protected DFA33 dfa33 = new DFA33(this);
    protected DFA47 dfa47 = new DFA47(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA45 dfa45 = new DFA45(this);
    protected DFA48 dfa48 = new DFA48(this);
    static final String DFA9_eotS =
        "\7\uffff";
    static final String DFA9_eofS =
        "\1\2\6\uffff";
    static final String DFA9_minS =
        "\2\15\1\20\1\15\2\uffff\1\15";
    static final String DFA9_maxS =
        "\1\23\3\72\2\uffff\1\72";
    static final String DFA9_acceptS =
        "\4\uffff\1\2\1\1\1\uffff";
    static final String DFA9_specialS =
        "\7\uffff}>";
    static final String[] DFA9_transitionS = {
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
            return "()* loopback of 123:50: ( n e= expr_or_assign )*";
        }
    }
    static final String DFA13_eotS =
        "\14\uffff";
    static final String DFA13_eofS =
        "\1\6\13\uffff";
    static final String DFA13_minS =
        "\1\15\5\0\6\uffff";
    static final String DFA13_maxS =
        "\1\110\5\0\6\uffff";
    static final String DFA13_acceptS =
        "\6\uffff\1\6\1\1\1\2\1\3\1\4\1\5";
    static final String DFA13_specialS =
        "\1\uffff\1\2\1\1\1\3\1\0\1\4\6\uffff}>";
    static final String[] DFA13_transitionS = {
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

    static final short[] DFA13_eot = DFA.unpackEncodedString(DFA13_eotS);
    static final short[] DFA13_eof = DFA.unpackEncodedString(DFA13_eofS);
    static final char[] DFA13_min = DFA.unpackEncodedStringToUnsignedChars(DFA13_minS);
    static final char[] DFA13_max = DFA.unpackEncodedStringToUnsignedChars(DFA13_maxS);
    static final short[] DFA13_accept = DFA.unpackEncodedString(DFA13_acceptS);
    static final short[] DFA13_special = DFA.unpackEncodedString(DFA13_specialS);
    static final short[][] DFA13_transition;

    static {
        int numStates = DFA13_transitionS.length;
        DFA13_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA13_transition[i] = DFA.unpackEncodedString(DFA13_transitionS[i]);
        }
    }

    class DFA13 extends DFA {

        public DFA13(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 13;
            this.eot = DFA13_eot;
            this.eof = DFA13_eof;
            this.min = DFA13_min;
            this.max = DFA13_max;
            this.accept = DFA13_accept;
            this.special = DFA13_special;
            this.transition = DFA13_transition;
        }
        @Override
        public String getDescription() {
            return "136:3: ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign | )";
        }
        @Override
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA13_4 = input.LA(1);

                         
                        int index13_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred4_R()) ) {s = 10;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index13_4);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA13_2 = input.LA(1);

                         
                        int index13_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred2_R()) ) {s = 8;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index13_2);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA13_1 = input.LA(1);

                         
                        int index13_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_R()) ) {s = 7;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index13_1);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA13_3 = input.LA(1);

                         
                        int index13_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred3_R()) ) {s = 9;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index13_3);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA13_5 = input.LA(1);

                         
                        int index13_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred5_R()) ) {s = 11;}

                        else if ( (true) ) {s = 6;}

                         
                        input.seek(index13_5);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 13, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA15_eotS =
        "\4\uffff";
    static final String DFA15_eofS =
        "\4\uffff";
    static final String DFA15_minS =
        "\2\15\2\uffff";
    static final String DFA15_maxS =
        "\2\43\2\uffff";
    static final String DFA15_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA15_specialS =
        "\4\uffff}>";
    static final String[] DFA15_transitionS = {
            "\2\1\14\uffff\1\2\7\uffff\1\3",
            "\2\1\14\uffff\1\2\7\uffff\1\3",
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
            return "()* loopback of 162:38: ( n_ COMMA n_ par_decl[l] )*";
        }
    }
    static final String DFA17_eotS =
        "\15\uffff";
    static final String DFA17_eofS =
        "\15\uffff";
    static final String DFA17_minS =
        "\1\37\4\15\2\uffff\1\15\2\uffff\1\15\2\uffff";
    static final String DFA17_maxS =
        "\1\45\4\43\2\uffff\1\43\2\uffff\1\43\2\uffff";
    static final String DFA17_acceptS =
        "\5\uffff\1\2\1\1\1\uffff\1\3\1\4\1\uffff\1\5\1\6";
    static final String DFA17_specialS =
        "\15\uffff}>";
    static final String[] DFA17_transitionS = {
            "\1\1\4\uffff\1\2\1\3",
            "\2\4\11\uffff\1\5\2\uffff\1\6\7\uffff\1\6",
            "\2\7\11\uffff\1\11\2\uffff\1\10\7\uffff\1\10",
            "\2\12\11\uffff\1\14\2\uffff\1\13\7\uffff\1\13",
            "\2\4\11\uffff\1\5\2\uffff\1\6\7\uffff\1\6",
            "",
            "",
            "\2\7\11\uffff\1\11\2\uffff\1\10\7\uffff\1\10",
            "",
            "",
            "\2\12\11\uffff\1\14\2\uffff\1\13\7\uffff\1\13",
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
            return "164:1: par_decl[ArgumentList l] : (i= ID | i= ID n_ ASSIGN n_ e= expr | v= VARIATIC | VARIATIC n_ ASSIGN n_ expr | DD | DD n_ ASSIGN n_ expr );";
        }
    }
    static final String DFA31_eotS =
        "\47\uffff";
    static final String DFA31_eofS =
        "\1\42\46\uffff";
    static final String DFA31_minS =
        "\1\15\5\0\41\uffff";
    static final String DFA31_maxS =
        "\1\110\5\0\41\uffff";
    static final String DFA31_acceptS =
        "\6\uffff\40\2\1\1";
    static final String DFA31_specialS =
        "\1\3\1\4\1\5\1\0\1\2\1\1\41\uffff}>";
    static final String[] DFA31_transitionS = {
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
            return "227:2: ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)";
        }
        @Override
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA31_3 = input.LA(1);

                         
                        int index31_3 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index31_3);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA31_5 = input.LA(1);

                         
                        int index31_5 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index31_5);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA31_4 = input.LA(1);

                         
                        int index31_4 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index31_4);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA31_0 = input.LA(1);

                         
                        int index31_0 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (LA31_0==FIELD) ) {s = 1;}

                        else if ( (LA31_0==AT) ) {s = 2;}

                        else if ( (LA31_0==LBRAKET) ) {s = 3;}

                        else if ( (LA31_0==LBB) ) {s = 4;}

                        else if ( (LA31_0==LPAR) ) {s = 5;}

                        else if ( (LA31_0==CARRET) && (synpred17_R())) {s = 6;}

                        else if ( (LA31_0==COLON) && (synpred17_R())) {s = 7;}

                        else if ( (LA31_0==OP) && (synpred17_R())) {s = 8;}

                        else if ( (LA31_0==MULT) && (synpred17_R())) {s = 9;}

                        else if ( (LA31_0==DIV) && (synpred17_R())) {s = 10;}

                        else if ( (LA31_0==MOD) && (synpred17_R())) {s = 11;}

                        else if ( (LA31_0==PLUS) && (synpred17_R())) {s = 12;}

                        else if ( (LA31_0==MINUS) && (synpred17_R())) {s = 13;}

                        else if ( (LA31_0==GT) && (synpred17_R())) {s = 14;}

                        else if ( (LA31_0==GE) && (synpred17_R())) {s = 15;}

                        else if ( (LA31_0==LT) && (synpred17_R())) {s = 16;}

                        else if ( (LA31_0==LE) && (synpred17_R())) {s = 17;}

                        else if ( (LA31_0==EQ) && (synpred17_R())) {s = 18;}

                        else if ( (LA31_0==NE) && (synpred17_R())) {s = 19;}

                        else if ( (LA31_0==AND) && (synpred17_R())) {s = 20;}

                        else if ( (LA31_0==ELEMENTWISEAND) && (synpred17_R())) {s = 21;}

                        else if ( (LA31_0==OR) && (synpred17_R())) {s = 22;}

                        else if ( (LA31_0==ELEMENTWISEOR) && (synpred17_R())) {s = 23;}

                        else if ( (LA31_0==TILDE) && (synpred17_R())) {s = 24;}

                        else if ( (LA31_0==ARROW) && (synpred17_R())) {s = 25;}

                        else if ( (LA31_0==SUPER_ARROW) && (synpred17_R())) {s = 26;}

                        else if ( (LA31_0==RIGHT_ARROW) && (synpred17_R())) {s = 27;}

                        else if ( (LA31_0==SUPER_RIGHT_ARROW) && (synpred17_R())) {s = 28;}

                        else if ( ((LA31_0>=NEWLINE && LA31_0<=COMMENT)) && (synpred17_R())) {s = 29;}

                        else if ( (LA31_0==COMMA) && (synpred17_R())) {s = 30;}

                        else if ( (LA31_0==RPAR) && (synpred17_R())) {s = 31;}

                        else if ( (LA31_0==RBRAKET) && (synpred17_R())) {s = 32;}

                        else if ( (LA31_0==ASSIGN) && (synpred17_R())) {s = 33;}

                        else if ( (LA31_0==EOF) && (synpred17_R())) {s = 34;}

                        else if ( (LA31_0==SEMICOLON) && (synpred17_R())) {s = 35;}

                        else if ( (LA31_0==RBRACE) && (synpred17_R())) {s = 36;}

                        else if ( (LA31_0==ELSE) && (synpred17_R())) {s = 37;}

                         
                        input.seek(index31_0);
                        if ( s>=0 ) return s;
                        break;
                    case 4 : 
                        int LA31_1 = input.LA(1);

                         
                        int index31_1 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index31_1);
                        if ( s>=0 ) return s;
                        break;
                    case 5 : 
                        int LA31_2 = input.LA(1);

                         
                        int index31_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (true) ) {s = 38;}

                        else if ( (synpred17_R()) ) {s = 37;}

                         
                        input.seek(index31_2);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 31, _s, input);
            error(nvae);
            throw nvae;
        }
    }
    static final String DFA33_eotS =
        "\16\uffff";
    static final String DFA33_eofS =
        "\1\uffff\2\14\13\uffff";
    static final String DFA33_minS =
        "\1\20\2\15\13\uffff";
    static final String DFA33_maxS =
        "\1\72\2\110\13\uffff";
    static final String DFA33_acceptS =
        "\3\uffff\1\2\1\3\1\4\1\5\1\6\1\11\1\12\1\13\1\7\1\1\1\10";
    static final String DFA33_specialS =
        "\16\uffff}>";
    static final String[] DFA33_transitionS = {
            "\2\12\1\11\6\uffff\1\12\1\10\2\uffff\2\12\1\1\1\uffff\2\12\1"+
            "\uffff\1\2\1\4\13\uffff\1\5\2\uffff\3\6\1\7\3\3",
            "\3\14\3\uffff\6\14\1\uffff\3\14\6\uffff\1\14\2\uffff\1\14\1"+
            "\uffff\11\14\1\uffff\1\13\1\15\7\uffff\16\14",
            "\3\14\3\uffff\6\14\1\uffff\3\14\6\uffff\1\14\2\uffff\1\14\1"+
            "\uffff\11\14\1\uffff\1\13\1\15\7\uffff\16\14",
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

    static final short[] DFA33_eot = DFA.unpackEncodedString(DFA33_eotS);
    static final short[] DFA33_eof = DFA.unpackEncodedString(DFA33_eofS);
    static final char[] DFA33_min = DFA.unpackEncodedStringToUnsignedChars(DFA33_minS);
    static final char[] DFA33_max = DFA.unpackEncodedStringToUnsignedChars(DFA33_maxS);
    static final short[] DFA33_accept = DFA.unpackEncodedString(DFA33_acceptS);
    static final short[] DFA33_special = DFA.unpackEncodedString(DFA33_specialS);
    static final short[][] DFA33_transition;

    static {
        int numStates = DFA33_transitionS.length;
        DFA33_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA33_transition[i] = DFA.unpackEncodedString(DFA33_transitionS[i]);
        }
    }

    class DFA33 extends DFA {

        public DFA33(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 33;
            this.eot = DFA33_eot;
            this.eof = DFA33_eof;
            this.min = DFA33_min;
            this.max = DFA33_max;
            this.accept = DFA33_accept;
            this.special = DFA33_special;
            this.transition = DFA33_transition;
        }
        @Override
        public String getDescription() {
            return "238:1: simple_expr returns [ASTNode v] : (i= id | b= bool | DD | NULL | num= number | cstr= conststring | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ ea= expr_or_assign n_ RPAR | s= sequence | e= expr_wo_assign );";
        }
    }
    static final String DFA47_eotS =
        "\4\uffff";
    static final String DFA47_eofS =
        "\4\uffff";
    static final String DFA47_minS =
        "\2\15\2\uffff";
    static final String DFA47_maxS =
        "\2\72\2\uffff";
    static final String DFA47_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA47_specialS =
        "\4\uffff}>";
    static final String[] DFA47_transitionS = {
            "\2\1\1\uffff\3\2\6\uffff\3\2\1\uffff\3\2\1\uffff\2\2\1\3\4\2"+
            "\2\uffff\2\2\3\uffff\1\2\1\uffff\1\2\2\uffff\7\2",
            "\2\1\1\uffff\3\2\6\uffff\3\2\1\uffff\3\2\1\uffff\2\2\1\3\4"+
            "\2\2\uffff\2\2\3\uffff\1\2\1\uffff\1\2\2\uffff\7\2",
            "",
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

    class DFA47 extends DFA {

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
            return "292:1: args returns [ArgumentList v] : ( n_ ( arg_expr[v] n_ ( COMMA ( | n_ arg_expr[v] ) n_ )* )? | n_ ( COMMA ( | n_ arg_expr[v] ) n_ )+ );";
        }
    }
    static final String DFA42_eotS =
        "\4\uffff";
    static final String DFA42_eofS =
        "\4\uffff";
    static final String DFA42_minS =
        "\2\15\2\uffff";
    static final String DFA42_maxS =
        "\2\72\2\uffff";
    static final String DFA42_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA42_specialS =
        "\4\uffff}>";
    static final String[] DFA42_transitionS = {
            "\2\1\1\uffff\3\3\6\uffff\2\3\1\2\1\uffff\3\3\1\uffff\2\3\1\2"+
            "\4\3\2\uffff\2\3\3\uffff\1\2\1\uffff\1\3\2\uffff\7\3",
            "\2\1\1\uffff\3\3\6\uffff\2\3\1\2\1\uffff\3\3\1\uffff\2\3\1"+
            "\2\4\3\2\uffff\2\3\3\uffff\1\2\1\uffff\1\3\2\uffff\7\3",
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
            return "294:31: ( | n_ arg_expr[v] )";
        }
    }
    static final String DFA45_eotS =
        "\4\uffff";
    static final String DFA45_eofS =
        "\4\uffff";
    static final String DFA45_minS =
        "\2\15\2\uffff";
    static final String DFA45_maxS =
        "\2\72\2\uffff";
    static final String DFA45_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA45_specialS =
        "\4\uffff}>";
    static final String[] DFA45_transitionS = {
            "\2\1\1\uffff\3\3\6\uffff\2\3\1\2\1\uffff\3\3\1\uffff\2\3\1\2"+
            "\4\3\2\uffff\2\3\3\uffff\1\2\1\uffff\1\3\2\uffff\7\3",
            "\2\1\1\uffff\3\3\6\uffff\2\3\1\2\1\uffff\3\3\1\uffff\2\3\1"+
            "\2\4\3\2\uffff\2\3\3\uffff\1\2\1\uffff\1\3\2\uffff\7\3",
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
            return "295:39: ( | n_ arg_expr[v] )";
        }
    }
    static final String DFA48_eotS =
        "\17\uffff";
    static final String DFA48_eofS =
        "\17\uffff";
    static final String DFA48_minS =
        "\1\20\1\uffff\10\15\2\uffff\1\15\2\uffff";
    static final String DFA48_maxS =
        "\1\72\1\uffff\3\110\1\57\1\72\1\57\2\72\2\uffff\1\72\2\uffff";
    static final String DFA48_acceptS =
        "\1\uffff\1\1\10\uffff\1\2\1\3\1\uffff\1\4\1\5";
    static final String DFA48_specialS =
        "\17\uffff}>";
    static final String[] DFA48_transitionS = {
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
            "\2\11\1\uffff\3\12\6\uffff\2\12\1\13\1\uffff\3\12\1\uffff\2"+
            "\12\1\13\4\12\2\uffff\2\12\3\uffff\1\13\1\uffff\1\12\2\uffff"+
            "\7\12",
            "\2\7\11\uffff\1\10\2\uffff\1\1\7\uffff\1\1\13\uffff\1\1",
            "\2\14\1\uffff\3\15\6\uffff\2\15\1\16\1\uffff\3\15\1\uffff\2"+
            "\15\1\16\4\15\2\uffff\2\15\3\uffff\1\16\1\uffff\1\15\2\uffff"+
            "\7\15",
            "\2\11\1\uffff\3\12\6\uffff\2\12\1\13\1\uffff\3\12\1\uffff\2"+
            "\12\1\13\4\12\2\uffff\2\12\3\uffff\1\13\1\uffff\1\12\2\uffff"+
            "\7\12",
            "",
            "",
            "\2\14\1\uffff\3\15\6\uffff\2\15\1\16\1\uffff\3\15\1\uffff\2"+
            "\15\1\16\4\15\2\uffff\2\15\3\uffff\1\16\1\uffff\1\15\2\uffff"+
            "\7\15",
            "",
            ""
    };

    static final short[] DFA48_eot = DFA.unpackEncodedString(DFA48_eotS);
    static final short[] DFA48_eof = DFA.unpackEncodedString(DFA48_eofS);
    static final char[] DFA48_min = DFA.unpackEncodedStringToUnsignedChars(DFA48_minS);
    static final char[] DFA48_max = DFA.unpackEncodedStringToUnsignedChars(DFA48_maxS);
    static final short[] DFA48_accept = DFA.unpackEncodedString(DFA48_acceptS);
    static final short[] DFA48_special = DFA.unpackEncodedString(DFA48_specialS);
    static final short[][] DFA48_transition;

    static {
        int numStates = DFA48_transitionS.length;
        DFA48_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA48_transition[i] = DFA.unpackEncodedString(DFA48_transitionS[i]);
        }
    }

    class DFA48 extends DFA {

        public DFA48(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 48;
            this.eot = DFA48_eot;
            this.eof = DFA48_eof;
            this.min = DFA48_min;
            this.max = DFA48_max;
            this.accept = DFA48_accept;
            this.special = DFA48_special;
            this.transition = DFA48_transition;
        }
        @Override
        public String getDescription() {
            return "297:1: arg_expr[ArgumentList l] : (e= expr | name= id n_ ASSIGN n_ val= expr | name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );";
        }
    }
 

    public static final BitSet FOLLOW_n__in_script156 = new BitSet(new long[]{0x07F20CF6E6070002L,0x0000000000100000L});
    public static final BitSet FOLLOW_statement_in_script161 = new BitSet(new long[]{0x07F20CF6E6070002L,0x0000000000100000L});
    public static final BitSet FOLLOW_n__in_interactive192 = new BitSet(new long[]{0x07F20CF6E6070002L,0x0000000000100000L});
    public static final BitSet FOLLOW_statement_in_interactive197 = new BitSet(new long[]{0x07F20CF6E6070002L,0x0000000000100000L});
    public static final BitSet FOLLOW_expr_or_assign_in_statement217 = new BitSet(new long[]{0x000000000000E000L});
    public static final BitSet FOLLOW_n_in_statement219 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_84_in_statement226 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x00000000001FFFFFL});
    public static final BitSet FOLLOW_EOF_in_statement231 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_n_241 = new BitSet(new long[]{0x0000000000006002L});
    public static final BitSet FOLLOW_set_in_n255 = new BitSet(new long[]{0x0000000000006002L});
    public static final BitSet FOLLOW_EOF_in_n266 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLON_in_n270 = new BitSet(new long[]{0x0000000000006000L});
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
    public static final BitSet FOLLOW_LBRACE_in_sequence411 = new BitSet(new long[]{0x07F20CF6E60F6000L});
    public static final BitSet FOLLOW_n__in_sequence413 = new BitSet(new long[]{0x07F20CF6E60F0000L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence418 = new BitSet(new long[]{0x000000000008E000L});
    public static final BitSet FOLLOW_n_in_sequence423 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence427 = new BitSet(new long[]{0x000000000008E000L});
    public static final BitSet FOLLOW_n_in_sequence433 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_RBRACE_in_sequence439 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_assign457 = new BitSet(new long[]{0x0000000000F00002L});
    public static final BitSet FOLLOW_ARROW_in_assign464 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_assign466 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_assign470 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_assign478 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_assign480 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_assign484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_assign494 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_assign496 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_assign500 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_assign510 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_assign512 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_assign516 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_alter_assign544 = new BitSet(new long[]{0x0000000001F00002L});
    public static final BitSet FOLLOW_ARROW_in_alter_assign555 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign557 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign561 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_alter_assign573 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign575 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_alter_assign593 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign595 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign599 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_alter_assign613 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign615 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign619 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_alter_assign633 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_alter_assign635 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign639 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_if_expr666 = new BitSet(new long[]{0x0000000004006000L});
    public static final BitSet FOLLOW_n__in_if_expr668 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAR_in_if_expr670 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_if_expr672 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr676 = new BitSet(new long[]{0x0000000008006000L});
    public static final BitSet FOLLOW_n__in_if_expr678 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_if_expr680 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_if_expr682 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr686 = new BitSet(new long[]{0x0000000010006002L});
    public static final BitSet FOLLOW_n__in_if_expr712 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ELSE_in_if_expr714 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_if_expr716 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_while_expr748 = new BitSet(new long[]{0x0000000004006000L});
    public static final BitSet FOLLOW_n__in_while_expr750 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAR_in_while_expr752 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_while_expr754 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr758 = new BitSet(new long[]{0x0000000008006000L});
    public static final BitSet FOLLOW_n__in_while_expr760 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_while_expr762 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_while_expr764 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr768 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_for_expr784 = new BitSet(new long[]{0x0000000004006000L});
    public static final BitSet FOLLOW_n__in_for_expr786 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAR_in_for_expr788 = new BitSet(new long[]{0x0000000080006000L});
    public static final BitSet FOLLOW_n__in_for_expr790 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_ID_in_for_expr794 = new BitSet(new long[]{0x0000000100006000L});
    public static final BitSet FOLLOW_n__in_for_expr796 = new BitSet(new long[]{0x0000000100000000L});
    public static final BitSet FOLLOW_IN_in_for_expr798 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_for_expr800 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr804 = new BitSet(new long[]{0x0000000008006000L});
    public static final BitSet FOLLOW_n__in_for_expr806 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_for_expr808 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_for_expr810 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr814 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REPEAT_in_repeat_expr831 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_repeat_expr833 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_repeat_expr837 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function858 = new BitSet(new long[]{0x0000000004006000L});
    public static final BitSet FOLLOW_n__in_function860 = new BitSet(new long[]{0x0000000004000000L});
    public static final BitSet FOLLOW_LPAR_in_function862 = new BitSet(new long[]{0x0000003088006000L});
    public static final BitSet FOLLOW_n__in_function865 = new BitSet(new long[]{0x0000003088000000L});
    public static final BitSet FOLLOW_par_decl_in_function868 = new BitSet(new long[]{0x0000000808006000L});
    public static final BitSet FOLLOW_n__in_function872 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_COMMA_in_function874 = new BitSet(new long[]{0x0000003080006000L});
    public static final BitSet FOLLOW_n__in_function876 = new BitSet(new long[]{0x0000003080000000L});
    public static final BitSet FOLLOW_par_decl_in_function878 = new BitSet(new long[]{0x0000000808006000L});
    public static final BitSet FOLLOW_n__in_function883 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_function887 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_function889 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_function893 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl910 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl920 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_par_decl922 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl924 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_par_decl926 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_par_decl930 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_par_decl939 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_par_decl989 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_par_decl991 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl993 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_par_decl995 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_par_decl997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_par_decl1003 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_par_decl1009 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_par_decl1011 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl1013 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_par_decl1015 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_par_decl1017 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_utilde_expr_in_tilde_expr1033 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_TILDE_in_tilde_expr1045 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_tilde_expr1047 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_utilde_expr_in_tilde_expr1051 = new BitSet(new long[]{0x0000004000000002L});
    public static final BitSet FOLLOW_TILDE_in_utilde_expr1071 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_utilde_expr1073 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_or_expr_in_utilde_expr1077 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_expr_in_utilde_expr1086 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_expr_in_or_expr1102 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_or_operator_in_or_expr1115 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_or_expr1117 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_and_expr_in_or_expr1121 = new BitSet(new long[]{0x1800000000000002L});
    public static final BitSet FOLLOW_not_expr_in_and_expr1143 = new BitSet(new long[]{0x6000000000000002L});
    public static final BitSet FOLLOW_and_operator_in_and_expr1159 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_and_expr1161 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_not_expr_in_and_expr1165 = new BitSet(new long[]{0x6000000000000002L});
    public static final BitSet FOLLOW_NOT_in_not_expr1185 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_not_expr1187 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_not_expr_in_not_expr1191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_expr_in_not_expr1200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1218 = new BitSet(new long[]{0x8000000000000002L,0x000000000000001FL});
    public static final BitSet FOLLOW_comp_operator_in_comp_expr1234 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_comp_expr1236 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1240 = new BitSet(new long[]{0x8000000000000002L,0x000000000000001FL});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1265 = new BitSet(new long[]{0x00000C0000000002L});
    public static final BitSet FOLLOW_add_operator_in_add_expr1279 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_add_expr1281 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1285 = new BitSet(new long[]{0x00000C0000000002L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1307 = new BitSet(new long[]{0x0000000000000002L,0x00000000000000E0L});
    public static final BitSet FOLLOW_mult_operator_in_mult_expr1320 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_mult_expr1322 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1326 = new BitSet(new long[]{0x0000000000000002L,0x00000000000000E0L});
    public static final BitSet FOLLOW_colon_expr_in_operator_expr1348 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_OP_in_operator_expr1360 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_operator_expr1362 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_colon_expr_in_operator_expr1366 = new BitSet(new long[]{0x0000010000000002L});
    public static final BitSet FOLLOW_unary_expression_in_colon_expr1388 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_COLON_in_colon_expr1401 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_colon_expr1403 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_unary_expression_in_colon_expr1407 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary_expression1427 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_unary_expression1429 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1433 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary_expression1440 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_unary_expression1442 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1446 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_expr_in_unary_expression1455 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_basic_expr_in_power_expr1473 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000100L});
    public static final BitSet FOLLOW_power_operator_in_power_expr1489 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_power_expr1491 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_power_expr_in_power_expr1495 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_basic_expr1525 = new BitSet(new long[]{0x0001700004000002L});
    public static final BitSet FOLLOW_expr_subset_in_basic_expr1546 = new BitSet(new long[]{0x0001700004000002L});
    public static final BitSet FOLLOW_FIELD_in_expr_subset1579 = new BitSet(new long[]{0x0000001080006000L});
    public static final BitSet FOLLOW_n__in_expr_subset1581 = new BitSet(new long[]{0x0000001080000000L});
    public static final BitSet FOLLOW_id_in_expr_subset1585 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_expr_subset1598 = new BitSet(new long[]{0x0000001080006000L});
    public static final BitSet FOLLOW_n__in_expr_subset1600 = new BitSet(new long[]{0x0000001080000000L});
    public static final BitSet FOLLOW_id_in_expr_subset1604 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRAKET_in_expr_subset1618 = new BitSet(new long[]{0x07F20CFEE6076000L});
    public static final BitSet FOLLOW_args_in_expr_subset1622 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1624 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBB_in_expr_subset1636 = new BitSet(new long[]{0x07F20CFEE6076000L});
    public static final BitSet FOLLOW_args_in_expr_subset1640 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1642 = new BitSet(new long[]{0x0000800000000000L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1644 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_subset1661 = new BitSet(new long[]{0x07F20CFEE6076000L});
    public static final BitSet FOLLOW_args_in_expr_subset1665 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_expr_subset1667 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1696 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_in_simple_expr1705 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_simple_expr1712 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_simple_expr1717 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_number_in_simple_expr1726 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_conststring_in_simple_expr1735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1742 = new BitSet(new long[]{0x0004000000000000L});
    public static final BitSet FOLLOW_NS_GET_in_simple_expr1744 = new BitSet(new long[]{0x0000001080006000L});
    public static final BitSet FOLLOW_n__in_simple_expr1746 = new BitSet(new long[]{0x0000001080000000L});
    public static final BitSet FOLLOW_id_in_simple_expr1748 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1753 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_NS_GET_INT_in_simple_expr1755 = new BitSet(new long[]{0x0000001080006000L});
    public static final BitSet FOLLOW_n__in_simple_expr1757 = new BitSet(new long[]{0x0000001080000000L});
    public static final BitSet FOLLOW_id_in_simple_expr1759 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_simple_expr1764 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_simple_expr1766 = new BitSet(new long[]{0x07F20CF6E6070000L});
    public static final BitSet FOLLOW_expr_or_assign_in_simple_expr1772 = new BitSet(new long[]{0x0000000008006000L});
    public static final BitSet FOLLOW_n__in_simple_expr1774 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RPAR_in_simple_expr1776 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_simple_expr1787 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_wo_assign_in_simple_expr1798 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_INTEGER_in_number1819 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOUBLE_in_number1831 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMPLEX_in_number1843 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_conststring1867 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_id1891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_id1903 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_bool1925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_bool1935 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NA_in_bool1945 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_or_operator1964 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELEMENTWISEOR_in_or_operator1981 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_and_operator1997 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ELEMENTWISEAND_in_and_operator2013 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_comp_operator2029 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GE_in_comp_operator2036 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_comp_operator2043 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LE_in_comp_operator2050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_comp_operator2057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NE_in_comp_operator2064 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_add_operator2078 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_add_operator2085 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_in_mult_operator2100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_in_mult_operator2107 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mult_operator2115 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARRET_in_power_operator2132 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_args2153 = new BitSet(new long[]{0x07F20CF6E6076002L});
    public static final BitSet FOLLOW_arg_expr_in_args2156 = new BitSet(new long[]{0x0000000800006000L});
    public static final BitSet FOLLOW_n__in_args2159 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_COMMA_in_args2163 = new BitSet(new long[]{0x07F20CFEE6076000L});
    public static final BitSet FOLLOW_n__in_args2171 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_arg_expr_in_args2173 = new BitSet(new long[]{0x0000000800006000L});
    public static final BitSet FOLLOW_n__in_args2177 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_n__in_args2187 = new BitSet(new long[]{0x0000000800000000L});
    public static final BitSet FOLLOW_COMMA_in_args2192 = new BitSet(new long[]{0x07F20CFEE6076000L});
    public static final BitSet FOLLOW_n__in_args2199 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_arg_expr_in_args2201 = new BitSet(new long[]{0x0000000800006000L});
    public static final BitSet FOLLOW_n__in_args2205 = new BitSet(new long[]{0x0000000800000002L});
    public static final BitSet FOLLOW_expr_in_arg_expr2221 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2230 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_arg_expr2232 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2234 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_arg_expr2236 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_arg_expr2240 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2249 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_arg_expr2251 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2253 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2261 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_arg_expr2263 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2265 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_n__in_arg_expr2267 = new BitSet(new long[]{0x07F20CF6E6076000L});
    public static final BitSet FOLLOW_expr_in_arg_expr2271 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2278 = new BitSet(new long[]{0x0000000001006000L});
    public static final BitSet FOLLOW_n__in_arg_expr2280 = new BitSet(new long[]{0x0000000001000000L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2282 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_synpred1_R552 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_synpred2_R570 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_synpred3_R588 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_synpred4_R608 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_synpred5_R628 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred6_R691 = new BitSet(new long[]{0x0000000010000000L});
    public static final BitSet FOLLOW_ELSE_in_synpred6_R693 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_synpred7_R1042 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_operator_in_synpred8_R1110 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_operator_in_synpred9_R1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_operator_in_synpred10_R1229 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_operator_in_synpred11_R1274 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_operator_in_synpred12_R1315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OP_in_synpred13_R1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_synpred14_R1396 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_operator_in_synpred15_R1484 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_synpred16_R1532 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred17_R1556 = new BitSet(new long[]{0x0000000000000002L});

}
