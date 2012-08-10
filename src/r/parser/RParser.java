// $ANTLR 3.4 R.g 2012-08-10 11:09:53

package r.parser;
import r.nodes.*;
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
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "AND", "ARROW", "ASSIGN", "AT", "BITWISEAND", "BITWISEOR", "BRAKET", "BREAK", "CALL", "CARRET", "COLUMN", "COMMA", "COMMENT", "DD", "DIV", "ELSE", "EQ", "ESC_SEQ", "EXPONENT", "FALSE", "FIELD", "FOR", "FUNCTION", "GE", "GT", "HEX_DIGIT", "HEX_ESC", "ID", "ID_NAME", "IF", "IN", "KW", "LBB", "LBRACE", "LBRAKET", "LE", "LINE_BREAK", "LPAR", "LT", "MINUS", "MISSING_VAL", "MOD", "MULT", "NE", "NEWLINE", "NEXT", "NOT", "NS_GET", "NS_GET_INT", "NULL", "NUMBER", "OCTAL_ESC", "OP", "OP_NAME", "OR", "PARMS", "PLUS", "RBRACE", "RBRAKET", "REPEAT", "RIGHT_ARROW", "RPAR", "SEMICOLUMN", "SEQUENCE", "STRING", "SUPER_ARROW", "SUPER_RIGHT_ARROW", "TILDE", "TRUE", "UMINUS", "UNICODE_ESC", "UPLUS", "UTILDE", "VARIATIC", "WHILE", "WS", "'--EOF--'"
    };

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
    // R.g:80:1: script returns [Node v] : n_ (s= statement )* ;
    public final Node script() throws RecognitionException {
        Node v = null;

        int script_StartIndex = input.index();

        Node s =null;


        ArrayList<Node> stmts = new ArrayList<Node>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 1) ) { return v; }

            // R.g:83:2: ( n_ (s= statement )* )
            // R.g:83:4: n_ (s= statement )*
            {
            pushFollow(FOLLOW_n__in_script155);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:83:7: (s= statement )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==BREAK||LA1_0==DD||LA1_0==FALSE||(LA1_0 >= FOR && LA1_0 <= FUNCTION)||LA1_0==ID||LA1_0==IF||LA1_0==LBRACE||LA1_0==LPAR||LA1_0==MINUS||(LA1_0 >= NEXT && LA1_0 <= NOT)||(LA1_0 >= NULL && LA1_0 <= NUMBER)||LA1_0==PLUS||LA1_0==REPEAT||LA1_0==STRING||(LA1_0 >= TILDE && LA1_0 <= TRUE)||(LA1_0 >= VARIATIC && LA1_0 <= WHILE)||LA1_0==80) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // R.g:83:8: s= statement
            	    {
            	    pushFollow(FOLLOW_statement_in_script160);
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

            if ( state.backtracking==0 ) { v = Factory.sequence(stmts);}
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
    // R.g:85:1: interactive returns [Node v] : n_ e= statement ;
    public final Node interactive() throws RecognitionException {
        Node v = null;

        int interactive_StartIndex = input.index();

        Node e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 2) ) { return v; }

            // R.g:86:2: ( n_ e= statement )
            // R.g:86:4: n_ e= statement
            {
            pushFollow(FOLLOW_n__in_interactive178);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_statement_in_interactive182);
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
    // R.g:88:1: statement returns [Node v] : (e= expr_or_assign n | '--EOF--' ( . )* EOF );
    public final Node statement() throws RecognitionException {
        Node v = null;

        int statement_StartIndex = input.index();

        Node e =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 3) ) { return v; }

            // R.g:89:2: (e= expr_or_assign n | '--EOF--' ( . )* EOF )
            int alt3=2;
            int LA3_0 = input.LA(1);

            if ( (LA3_0==BREAK||LA3_0==DD||LA3_0==FALSE||(LA3_0 >= FOR && LA3_0 <= FUNCTION)||LA3_0==ID||LA3_0==IF||LA3_0==LBRACE||LA3_0==LPAR||LA3_0==MINUS||(LA3_0 >= NEXT && LA3_0 <= NOT)||(LA3_0 >= NULL && LA3_0 <= NUMBER)||LA3_0==PLUS||LA3_0==REPEAT||LA3_0==STRING||(LA3_0 >= TILDE && LA3_0 <= TRUE)||(LA3_0 >= VARIATIC && LA3_0 <= WHILE)) ) {
                alt3=1;
            }
            else if ( (LA3_0==80) ) {
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
                    // R.g:89:4: e= expr_or_assign n
                    {
                    pushFollow(FOLLOW_expr_or_assign_in_statement200);
                    e=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n_in_statement202);
                    n();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = e;}

                    }
                    break;
                case 2 :
                    // R.g:90:4: '--EOF--' ( . )* EOF
                    {
                    match(input,80,FOLLOW_80_in_statement209); if (state.failed) return v;

                    // R.g:90:14: ( . )*
                    loop2:
                    do {
                        int alt2=2;
                        int LA2_0 = input.LA(1);

                        if ( ((LA2_0 >= AND && LA2_0 <= 80)) ) {
                            alt2=1;
                        }
                        else if ( (LA2_0==EOF) ) {
                            alt2=2;
                        }


                        switch (alt2) {
                    	case 1 :
                    	    // R.g:90:14: .
                    	    {
                    	    matchAny(input); if (state.failed) return v;

                    	    }
                    	    break;

                    	default :
                    	    break loop2;
                        }
                    } while (true);


                    match(input,EOF,FOLLOW_EOF_in_statement214); if (state.failed) return v;

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
    // R.g:93:1: n_ : ( NEWLINE | COMMENT )* ;
    public final void n_() throws RecognitionException {
        int n__StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 4) ) { return ; }

            // R.g:93:4: ( ( NEWLINE | COMMENT )* )
            // R.g:93:6: ( NEWLINE | COMMENT )*
            {
            // R.g:93:6: ( NEWLINE | COMMENT )*
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
    // R.g:94:1: n : ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLUMN n_ );
    public final void n() throws RecognitionException {
        int n_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 5) ) { return ; }

            // R.g:94:3: ( ( NEWLINE | COMMENT )+ | EOF | SEMICOLUMN n_ )
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
                    // R.g:94:5: ( NEWLINE | COMMENT )+
                    {
                    // R.g:94:5: ( NEWLINE | COMMENT )+
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
                    // R.g:94:28: EOF
                    {
                    match(input,EOF,FOLLOW_EOF_in_n249); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // R.g:94:34: SEMICOLUMN n_
                    {
                    match(input,SEMICOLUMN,FOLLOW_SEMICOLUMN_in_n253); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_n255);
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
    // R.g:96:1: expr_or_assign returns [Node v] : a= alter_assign ;
    public final Node expr_or_assign() throws RecognitionException {
        Node v = null;

        int expr_or_assign_StartIndex = input.index();

        Node a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 6) ) { return v; }

            // R.g:97:2: (a= alter_assign )
            // R.g:97:4: a= alter_assign
            {
            pushFollow(FOLLOW_alter_assign_in_expr_or_assign270);
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
    // R.g:99:1: expr returns [Node v] : a= assign ;
    public final Node expr() throws RecognitionException {
        Node v = null;

        int expr_StartIndex = input.index();

        Node a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 7) ) { return v; }

            // R.g:100:2: (a= assign )
            // R.g:100:4: a= assign
            {
            pushFollow(FOLLOW_assign_in_expr288);
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
    // R.g:102:1: expr_wo_assign returns [Node v] : ( while_expr | if_expr | for_expr | repeat_expr | function | NEXT ( ( LPAR )=> LPAR n_ RPAR )? | BREAK ( ( LPAR )=> LPAR n_ RPAR )? );
    public final Node expr_wo_assign() throws RecognitionException {
        Node v = null;

        int expr_wo_assign_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 8) ) { return v; }

            // R.g:103:2: ( while_expr | if_expr | for_expr | repeat_expr | function | NEXT ( ( LPAR )=> LPAR n_ RPAR )? | BREAK ( ( LPAR )=> LPAR n_ RPAR )? )
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
                    // R.g:103:4: while_expr
                    {
                    pushFollow(FOLLOW_while_expr_in_expr_wo_assign305);
                    while_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 2 :
                    // R.g:104:4: if_expr
                    {
                    pushFollow(FOLLOW_if_expr_in_expr_wo_assign310);
                    if_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 3 :
                    // R.g:105:4: for_expr
                    {
                    pushFollow(FOLLOW_for_expr_in_expr_wo_assign315);
                    for_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 4 :
                    // R.g:106:4: repeat_expr
                    {
                    pushFollow(FOLLOW_repeat_expr_in_expr_wo_assign320);
                    repeat_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 5 :
                    // R.g:107:4: function
                    {
                    pushFollow(FOLLOW_function_in_expr_wo_assign325);
                    function();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 6 :
                    // R.g:108:4: NEXT ( ( LPAR )=> LPAR n_ RPAR )?
                    {
                    match(input,NEXT,FOLLOW_NEXT_in_expr_wo_assign330); if (state.failed) return v;

                    // R.g:108:9: ( ( LPAR )=> LPAR n_ RPAR )?
                    int alt7=2;
                    alt7 = dfa7.predict(input);
                    switch (alt7) {
                        case 1 :
                            // R.g:108:10: ( LPAR )=> LPAR n_ RPAR
                            {
                            match(input,LPAR,FOLLOW_LPAR_in_expr_wo_assign337); if (state.failed) return v;

                            pushFollow(FOLLOW_n__in_expr_wo_assign339);
                            n_();

                            state._fsp--;
                            if (state.failed) return v;

                            match(input,RPAR,FOLLOW_RPAR_in_expr_wo_assign341); if (state.failed) return v;

                            }
                            break;

                    }


                    }
                    break;
                case 7 :
                    // R.g:109:4: BREAK ( ( LPAR )=> LPAR n_ RPAR )?
                    {
                    match(input,BREAK,FOLLOW_BREAK_in_expr_wo_assign349); if (state.failed) return v;

                    // R.g:109:10: ( ( LPAR )=> LPAR n_ RPAR )?
                    int alt8=2;
                    alt8 = dfa8.predict(input);
                    switch (alt8) {
                        case 1 :
                            // R.g:109:11: ( LPAR )=> LPAR n_ RPAR
                            {
                            match(input,LPAR,FOLLOW_LPAR_in_expr_wo_assign356); if (state.failed) return v;

                            pushFollow(FOLLOW_n__in_expr_wo_assign358);
                            n_();

                            state._fsp--;
                            if (state.failed) return v;

                            match(input,RPAR,FOLLOW_RPAR_in_expr_wo_assign360); if (state.failed) return v;

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
    // R.g:111:1: sequence returns [Node v] : LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE ;
    public final Node sequence() throws RecognitionException {
        Node v = null;

        int sequence_StartIndex = input.index();

        Node e =null;


        ArrayList<Node> stmts = new ArrayList<Node>();
        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 9) ) { return v; }

            // R.g:114:2: ( LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE )
            // R.g:114:4: LBRACE n_ (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )? RBRACE
            {
            match(input,LBRACE,FOLLOW_LBRACE_in_sequence393); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_sequence395);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:114:14: (e= expr_or_assign ( n e= expr_or_assign )* ( n )? )?
            int alt12=2;
            int LA12_0 = input.LA(1);

            if ( (LA12_0==BREAK||LA12_0==DD||LA12_0==FALSE||(LA12_0 >= FOR && LA12_0 <= FUNCTION)||LA12_0==ID||LA12_0==IF||LA12_0==LBRACE||LA12_0==LPAR||LA12_0==MINUS||(LA12_0 >= NEXT && LA12_0 <= NOT)||(LA12_0 >= NULL && LA12_0 <= NUMBER)||LA12_0==PLUS||LA12_0==REPEAT||LA12_0==STRING||(LA12_0 >= TILDE && LA12_0 <= TRUE)||(LA12_0 >= VARIATIC && LA12_0 <= WHILE)) ) {
                alt12=1;
            }
            switch (alt12) {
                case 1 :
                    // R.g:114:15: e= expr_or_assign ( n e= expr_or_assign )* ( n )?
                    {
                    pushFollow(FOLLOW_expr_or_assign_in_sequence400);
                    e=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    // R.g:114:32: ( n e= expr_or_assign )*
                    loop10:
                    do {
                        int alt10=2;
                        alt10 = dfa10.predict(input);
                        switch (alt10) {
                    	case 1 :
                    	    // R.g:114:33: n e= expr_or_assign
                    	    {
                    	    pushFollow(FOLLOW_n_in_sequence403);
                    	    n();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    pushFollow(FOLLOW_expr_or_assign_in_sequence407);
                    	    e=expr_or_assign();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    }
                    	    break;

                    	default :
                    	    break loop10;
                        }
                    } while (true);


                    // R.g:114:54: ( n )?
                    int alt11=2;
                    int LA11_0 = input.LA(1);

                    if ( (LA11_0==EOF||LA11_0==COMMENT||LA11_0==NEWLINE||LA11_0==SEMICOLUMN) ) {
                        alt11=1;
                    }
                    switch (alt11) {
                        case 1 :
                            // R.g:114:54: n
                            {
                            pushFollow(FOLLOW_n_in_sequence411);
                            n();

                            state._fsp--;
                            if (state.failed) return v;

                            }
                            break;

                    }


                    }
                    break;

            }


            match(input,RBRACE,FOLLOW_RBRACE_in_sequence417); if (state.failed) return v;

            }

            if ( state.backtracking==0 ) { v = Factory.sequence(stmts);}
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
    // R.g:116:1: assign returns [Node v] : l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |) ;
    public final Node assign() throws RecognitionException {
        Node v = null;

        int assign_StartIndex = input.index();

        Token a=null;
        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 10) ) { return v; }

            // R.g:117:2: (l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |) )
            // R.g:117:4: l= tilde_expr ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |)
            {
            pushFollow(FOLLOW_tilde_expr_in_assign435);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:118:3: ( ARROW n_ r= expr | SUPER_ARROW n_ r= expr |a= RIGHT_ARROW n_ r= expr |a= SUPER_RIGHT_ARROW n_ r= expr |)
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
            case DD:
            case ELSE:
            case FALSE:
            case FOR:
            case FUNCTION:
            case ID:
            case IF:
            case IN:
            case LBRACE:
            case LPAR:
            case MINUS:
            case NEWLINE:
            case NEXT:
            case NOT:
            case NULL:
            case NUMBER:
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
            case 80:
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
                    // R.g:118:5: ARROW n_ r= expr
                    {
                    match(input,ARROW,FOLLOW_ARROW_in_assign442); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign444);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign448);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.ASSIGN, l, r);}

                    }
                    break;
                case 2 :
                    // R.g:119:5: SUPER_ARROW n_ r= expr
                    {
                    match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_assign456); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign458);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign462);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.SUPER_ASSIGN, l, r);}

                    }
                    break;
                case 3 :
                    // R.g:120:5: a= RIGHT_ARROW n_ r= expr
                    {
                    a=(Token)match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_assign472); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign474);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign478);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.ASSIGN, r, l);}

                    }
                    break;
                case 4 :
                    // R.g:121:5: a= SUPER_RIGHT_ARROW n_ r= expr
                    {
                    a=(Token)match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_assign488); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_assign490);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_assign494);
                    r=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.SUPER_ASSIGN, r, l);}

                    }
                    break;
                case 5 :
                    // R.g:122:5: 
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
    // R.g:125:1: alter_assign returns [Node v] : l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |) ;
    public final Node alter_assign() throws RecognitionException {
        Node v = null;

        int alter_assign_StartIndex = input.index();

        Token a=null;
        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 11) ) { return v; }

            // R.g:126:2: (l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |) )
            // R.g:126:4: l= tilde_expr ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |)
            {
            pushFollow(FOLLOW_tilde_expr_in_alter_assign522);
            l=tilde_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:127:3: ( ( ARROW )=> ARROW n_ r= expr_or_assign | ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign | ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign | ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign | ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign |)
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
                    // R.g:127:5: ( ARROW )=> ARROW n_ r= expr_or_assign
                    {
                    match(input,ARROW,FOLLOW_ARROW_in_alter_assign533); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign535);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign539);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.ASSIGN, l, r);}

                    }
                    break;
                case 2 :
                    // R.g:128:5: ( SUPER_ARROW )=> SUPER_ARROW n_ r= expr_or_assign
                    {
                    match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_alter_assign551); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign553);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign557);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.SUPER_ASSIGN, l, r);}

                    }
                    break;
                case 3 :
                    // R.g:129:5: ( RIGHT_ARROW )=>a= RIGHT_ARROW n_ r= expr_or_assign
                    {
                    a=(Token)match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_alter_assign571); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign573);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign577);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = Factory.binary(BinaryOperator.ASSIGN, r, l);}

                    }
                    break;
                case 4 :
                    // R.g:130:5: ( SUPER_RIGHT_ARROW )=>a= SUPER_RIGHT_ARROW n_ r= expr_or_assign
                    {
                    a=(Token)match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_alter_assign591); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign593);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign597);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.SUPER_ASSIGN, r, l);}

                    }
                    break;
                case 5 :
                    // R.g:131:5: ( ASSIGN )=>a= ASSIGN n_ r= expr_or_assign
                    {
                    a=(Token)match(input,ASSIGN,FOLLOW_ASSIGN_in_alter_assign611); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_alter_assign613);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_alter_assign617);
                    r=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.ASSIGN, l, r);}

                    }
                    break;
                case 6 :
                    // R.g:132:5: 
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
    // R.g:135:1: if_expr returns [Node v] : IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |) ;
    public final Node if_expr() throws RecognitionException {
        Node v = null;

        int if_expr_StartIndex = input.index();

        Node cond =null;

        Node t =null;

        Node f =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 12) ) { return v; }

            // R.g:136:2: ( IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |) )
            // R.g:137:2: IF n_ LPAR n_ cond= expr_or_assign n_ RPAR n_ t= expr_or_assign ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |)
            {
            match(input,IF,FOLLOW_IF_in_if_expr644); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr646);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_if_expr648); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr650);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_if_expr654);
            cond=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr656);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,RPAR,FOLLOW_RPAR_in_if_expr658); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_if_expr660);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_if_expr664);
            t=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            // R.g:138:2: ( ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign ) |)
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
                    // R.g:138:3: ( n_ ELSE )=> ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    {
                    // R.g:138:14: ( options {greedy=false; backtrack=true; } : n_ ELSE n_ f= expr_or_assign )
                    // R.g:138:58: n_ ELSE n_ f= expr_or_assign
                    {
                    pushFollow(FOLLOW_n__in_if_expr690);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,ELSE,FOLLOW_ELSE_in_if_expr692); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_if_expr694);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_or_assign_in_if_expr698);
                    f=expr_or_assign();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) { v = Factory.ternary(TernaryOperator.IF, cond, t, f);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:139:7: 
                    {
                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.IF, cond, t);}

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
    // R.g:142:1: while_expr returns [Node v] : WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final Node while_expr() throws RecognitionException {
        Node v = null;

        int while_expr_StartIndex = input.index();

        Node c =null;

        Node body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 13) ) { return v; }

            // R.g:143:2: ( WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:143:4: WHILE n_ LPAR n_ c= expr_or_assign n_ RPAR n_ body= expr_or_assign
            {
            match(input,WHILE,FOLLOW_WHILE_in_while_expr726); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr728);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_while_expr730); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr732);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_while_expr736);
            c=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr738);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,RPAR,FOLLOW_RPAR_in_while_expr740); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_while_expr742);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_while_expr746);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.WHILE, c, body);}

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
    // R.g:145:1: for_expr returns [Node v] : FOR n_ LPAR n_ ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign ;
    public final Node for_expr() throws RecognitionException {
        Node v = null;

        int for_expr_StartIndex = input.index();

        Node in =null;

        Node body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 14) ) { return v; }

            // R.g:146:2: ( FOR n_ LPAR n_ ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign )
            // R.g:146:4: FOR n_ LPAR n_ ID n_ IN n_ in= expr_or_assign n_ RPAR n_ body= expr_or_assign
            {
            match(input,FOR,FOLLOW_FOR_in_for_expr762); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr764);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_for_expr766); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr768);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,ID,FOLLOW_ID_in_for_expr770); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr772);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,IN,FOLLOW_IN_in_for_expr774); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr776);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_for_expr780);
            in=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr782);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,RPAR,FOLLOW_RPAR_in_for_expr784); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_for_expr786);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_for_expr790);
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
    // R.g:148:1: repeat_expr returns [Node v] : REPEAT n_ body= expr_or_assign ;
    public final Node repeat_expr() throws RecognitionException {
        Node v = null;

        int repeat_expr_StartIndex = input.index();

        Node body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 15) ) { return v; }

            // R.g:149:2: ( REPEAT n_ body= expr_or_assign )
            // R.g:149:4: REPEAT n_ body= expr_or_assign
            {
            match(input,REPEAT,FOLLOW_REPEAT_in_repeat_expr805); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_repeat_expr807);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_repeat_expr811);
            body=expr_or_assign();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) {v = Factory.unary(UnaryOperator.REPEAT, body);}

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
    // R.g:151:1: function returns [Node v] : FUNCTION n_ LPAR n_ ( par_decl ( n_ COMMA n_ par_decl )* n_ )? RPAR n_ body= expr_or_assign ;
    public final Node function() throws RecognitionException {
        Node v = null;

        int function_StartIndex = input.index();

        Node body =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 16) ) { return v; }

            // R.g:152:2: ( FUNCTION n_ LPAR n_ ( par_decl ( n_ COMMA n_ par_decl )* n_ )? RPAR n_ body= expr_or_assign )
            // R.g:152:4: FUNCTION n_ LPAR n_ ( par_decl ( n_ COMMA n_ par_decl )* n_ )? RPAR n_ body= expr_or_assign
            {
            match(input,FUNCTION,FOLLOW_FUNCTION_in_function827); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_function829);
            n_();

            state._fsp--;
            if (state.failed) return v;

            match(input,LPAR,FOLLOW_LPAR_in_function831); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_function834);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:152:25: ( par_decl ( n_ COMMA n_ par_decl )* n_ )?
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==ID||LA17_0==VARIATIC) ) {
                alt17=1;
            }
            switch (alt17) {
                case 1 :
                    // R.g:152:26: par_decl ( n_ COMMA n_ par_decl )* n_
                    {
                    pushFollow(FOLLOW_par_decl_in_function837);
                    par_decl();

                    state._fsp--;
                    if (state.failed) return v;

                    // R.g:152:35: ( n_ COMMA n_ par_decl )*
                    loop16:
                    do {
                        int alt16=2;
                        alt16 = dfa16.predict(input);
                        switch (alt16) {
                    	case 1 :
                    	    // R.g:152:36: n_ COMMA n_ par_decl
                    	    {
                    	    pushFollow(FOLLOW_n__in_function840);
                    	    n_();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    match(input,COMMA,FOLLOW_COMMA_in_function842); if (state.failed) return v;

                    	    pushFollow(FOLLOW_n__in_function844);
                    	    n_();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    pushFollow(FOLLOW_par_decl_in_function846);
                    	    par_decl();

                    	    state._fsp--;
                    	    if (state.failed) return v;

                    	    }
                    	    break;

                    	default :
                    	    break loop16;
                        }
                    } while (true);


                    pushFollow(FOLLOW_n__in_function850);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }


            match(input,RPAR,FOLLOW_RPAR_in_function854); if (state.failed) return v;

            pushFollow(FOLLOW_n__in_function856);
            n_();

            state._fsp--;
            if (state.failed) return v;

            pushFollow(FOLLOW_expr_or_assign_in_function860);
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
            if ( state.backtracking>0 ) { memoize(input, 16, function_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "function"



    // $ANTLR start "par_decl"
    // R.g:154:1: par_decl : ( ID | ID n_ ASSIGN n_ expr | VARIATIC );
    public final void par_decl() throws RecognitionException {
        int par_decl_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 17) ) { return ; }

            // R.g:155:2: ( ID | ID n_ ASSIGN n_ expr | VARIATIC )
            int alt18=3;
            alt18 = dfa18.predict(input);
            switch (alt18) {
                case 1 :
                    // R.g:155:4: ID
                    {
                    match(input,ID,FOLLOW_ID_in_par_decl871); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // R.g:156:4: ID n_ ASSIGN n_ expr
                    {
                    match(input,ID,FOLLOW_ID_in_par_decl877); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl879);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_par_decl881); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_par_decl883);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_par_decl885);
                    expr();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // R.g:157:4: VARIATIC
                    {
                    match(input,VARIATIC,FOLLOW_VARIATIC_in_par_decl891); if (state.failed) return ;

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
    // R.g:159:1: tilde_expr returns [Node v] : l= or_expr ( ( ( TILDE )=> TILDE n_ r= tilde_expr ) |) ;
    public final Node tilde_expr() throws RecognitionException {
        Node v = null;

        int tilde_expr_StartIndex = input.index();

        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 18) ) { return v; }

            // R.g:160:2: (l= or_expr ( ( ( TILDE )=> TILDE n_ r= tilde_expr ) |) )
            // R.g:160:4: l= or_expr ( ( ( TILDE )=> TILDE n_ r= tilde_expr ) |)
            {
            pushFollow(FOLLOW_or_expr_in_tilde_expr908);
            l=or_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:161:2: ( ( ( TILDE )=> TILDE n_ r= tilde_expr ) |)
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
            else if ( (LA19_0==EOF||(LA19_0 >= AND && LA19_0 <= BITWISEOR)||(LA19_0 >= CARRET && LA19_0 <= COMMENT)||(LA19_0 >= DIV && LA19_0 <= EQ)||LA19_0==FIELD||(LA19_0 >= GE && LA19_0 <= GT)||LA19_0==LBB||(LA19_0 >= LBRAKET && LA19_0 <= LE)||(LA19_0 >= LPAR && LA19_0 <= MINUS)||(LA19_0 >= MOD && LA19_0 <= NEWLINE)||LA19_0==OP||LA19_0==OR||(LA19_0 >= PLUS && LA19_0 <= RBRAKET)||(LA19_0 >= RIGHT_ARROW && LA19_0 <= SEMICOLUMN)||(LA19_0 >= SUPER_ARROW && LA19_0 <= SUPER_RIGHT_ARROW)) ) {
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
                    // R.g:161:4: ( ( TILDE )=> TILDE n_ r= tilde_expr )
                    {
                    // R.g:161:4: ( ( TILDE )=> TILDE n_ r= tilde_expr )
                    // R.g:161:5: ( TILDE )=> TILDE n_ r= tilde_expr
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_tilde_expr919); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_tilde_expr921);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_tilde_expr_in_tilde_expr925);
                    r=tilde_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.MODEL, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:162:4: 
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
    // R.g:164:1: or_expr returns [Node v] : l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= tilde_expr ) |) ;
    public final Node or_expr() throws RecognitionException {
        Node v = null;

        int or_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 19) ) { return v; }

            // R.g:165:2: (l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= tilde_expr ) |) )
            // R.g:165:4: l= and_expr ( ( ( or_operator )=>op= or_operator n_ r= tilde_expr ) |)
            {
            pushFollow(FOLLOW_and_expr_in_or_expr951);
            l=and_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:166:2: ( ( ( or_operator )=>op= or_operator n_ r= tilde_expr ) |)
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
                    // R.g:166:3: ( ( or_operator )=>op= or_operator n_ r= tilde_expr )
                    {
                    // R.g:166:3: ( ( or_operator )=>op= or_operator n_ r= tilde_expr )
                    // R.g:166:4: ( or_operator )=>op= or_operator n_ r= tilde_expr
                    {
                    pushFollow(FOLLOW_or_operator_in_or_expr962);
                    op=or_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_or_expr964);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_tilde_expr_in_or_expr968);
                    r=tilde_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:167:7: 
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
    // R.g:169:1: and_expr returns [Node v] : l= comp_expr ( ( ( and_operator )=>op= and_operator n_ r= tilde_expr ) |) ;
    public final Node and_expr() throws RecognitionException {
        Node v = null;

        int and_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 20) ) { return v; }

            // R.g:170:2: (l= comp_expr ( ( ( and_operator )=>op= and_operator n_ r= tilde_expr ) |) )
            // R.g:170:4: l= comp_expr ( ( ( and_operator )=>op= and_operator n_ r= tilde_expr ) |)
            {
            pushFollow(FOLLOW_comp_expr_in_and_expr998);
            l=comp_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:171:5: ( ( ( and_operator )=>op= and_operator n_ r= tilde_expr ) |)
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
                    // R.g:171:6: ( ( and_operator )=>op= and_operator n_ r= tilde_expr )
                    {
                    // R.g:171:6: ( ( and_operator )=>op= and_operator n_ r= tilde_expr )
                    // R.g:171:7: ( and_operator )=>op= and_operator n_ r= tilde_expr
                    {
                    pushFollow(FOLLOW_and_operator_in_and_expr1012);
                    op=and_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_and_expr1014);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_tilde_expr_in_and_expr1018);
                    r=tilde_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:172:7: 
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
    // R.g:174:1: comp_expr returns [Node v] : l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= tilde_expr ) |) ;
    public final Node comp_expr() throws RecognitionException {
        Node v = null;

        int comp_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 21) ) { return v; }

            // R.g:175:2: (l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= tilde_expr ) |) )
            // R.g:175:4: l= add_expr ( ( ( comp_operator )=>op= comp_operator n_ r= tilde_expr ) |)
            {
            pushFollow(FOLLOW_add_expr_in_comp_expr1047);
            l=add_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:176:5: ( ( ( comp_operator )=>op= comp_operator n_ r= tilde_expr ) |)
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
                    // R.g:176:6: ( ( comp_operator )=>op= comp_operator n_ r= tilde_expr )
                    {
                    // R.g:176:6: ( ( comp_operator )=>op= comp_operator n_ r= tilde_expr )
                    // R.g:176:7: ( comp_operator )=>op= comp_operator n_ r= tilde_expr
                    {
                    pushFollow(FOLLOW_comp_operator_in_comp_expr1062);
                    op=comp_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_comp_expr1064);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_tilde_expr_in_comp_expr1068);
                    r=tilde_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:177:7: 
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
    // R.g:179:1: add_expr returns [Node v] : l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= tilde_expr ) |) ;
    public final Node add_expr() throws RecognitionException {
        Node v = null;

        int add_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 22) ) { return v; }

            // R.g:180:2: (l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= tilde_expr ) |) )
            // R.g:180:4: l= mult_expr ( ( ( add_operator )=>op= add_operator n_ r= tilde_expr ) |)
            {
            pushFollow(FOLLOW_mult_expr_in_add_expr1101);
            l=mult_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:181:3: ( ( ( add_operator )=>op= add_operator n_ r= tilde_expr ) |)
            int alt23=2;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                int LA23_1 = input.LA(2);

                if ( (synpred13_R()) ) {
                    alt23=1;
                }
                else if ( (true) ) {
                    alt23=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 23, 1, input);

                    throw nvae;

                }
                }
                break;
            case MINUS:
                {
                int LA23_2 = input.LA(2);

                if ( (synpred13_R()) ) {
                    alt23=1;
                }
                else if ( (true) ) {
                    alt23=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 23, 2, input);

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
            case EQ:
            case FIELD:
            case GE:
            case GT:
            case LBB:
            case LBRAKET:
            case LE:
            case LPAR:
            case LT:
            case MOD:
            case MULT:
            case NE:
            case NEWLINE:
            case OP:
            case OR:
            case RBRACE:
            case RBRAKET:
            case RIGHT_ARROW:
            case RPAR:
            case SEMICOLUMN:
            case SUPER_ARROW:
            case SUPER_RIGHT_ARROW:
            case TILDE:
                {
                alt23=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 23, 0, input);

                throw nvae;

            }

            switch (alt23) {
                case 1 :
                    // R.g:181:4: ( ( add_operator )=>op= add_operator n_ r= tilde_expr )
                    {
                    // R.g:181:4: ( ( add_operator )=>op= add_operator n_ r= tilde_expr )
                    // R.g:181:5: ( add_operator )=>op= add_operator n_ r= tilde_expr
                    {
                    pushFollow(FOLLOW_add_operator_in_add_expr1113);
                    op=add_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_add_expr1115);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_tilde_expr_in_add_expr1119);
                    r=tilde_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:182:7: 
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
            if ( state.backtracking>0 ) { memoize(input, 22, add_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "add_expr"



    // $ANTLR start "mult_expr"
    // R.g:184:1: mult_expr returns [Node v] : l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= tilde_expr ) |) ;
    public final Node mult_expr() throws RecognitionException {
        Node v = null;

        int mult_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 23) ) { return v; }

            // R.g:185:2: (l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= tilde_expr ) |) )
            // R.g:185:4: l= operator_expr ( ( ( mult_operator )=>op= mult_operator n_ r= tilde_expr ) |)
            {
            pushFollow(FOLLOW_operator_expr_in_mult_expr1148);
            l=operator_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:186:2: ( ( ( mult_operator )=>op= mult_operator n_ r= tilde_expr ) |)
            int alt24=2;
            switch ( input.LA(1) ) {
            case MULT:
                {
                int LA24_1 = input.LA(2);

                if ( (synpred14_R()) ) {
                    alt24=1;
                }
                else if ( (true) ) {
                    alt24=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 1, input);

                    throw nvae;

                }
                }
                break;
            case DIV:
                {
                int LA24_2 = input.LA(2);

                if ( (synpred14_R()) ) {
                    alt24=1;
                }
                else if ( (true) ) {
                    alt24=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 2, input);

                    throw nvae;

                }
                }
                break;
            case MOD:
                {
                int LA24_3 = input.LA(2);

                if ( (synpred14_R()) ) {
                    alt24=1;
                }
                else if ( (true) ) {
                    alt24=2;
                }
                else {
                    if (state.backtracking>0) {state.failed=true; return v;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 24, 3, input);

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
                alt24=2;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 24, 0, input);

                throw nvae;

            }

            switch (alt24) {
                case 1 :
                    // R.g:186:3: ( ( mult_operator )=>op= mult_operator n_ r= tilde_expr )
                    {
                    // R.g:186:3: ( ( mult_operator )=>op= mult_operator n_ r= tilde_expr )
                    // R.g:186:4: ( mult_operator )=>op= mult_operator n_ r= tilde_expr
                    {
                    pushFollow(FOLLOW_mult_operator_in_mult_expr1159);
                    op=mult_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_mult_expr1161);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_tilde_expr_in_mult_expr1165);
                    r=tilde_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:187:7: 
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
            if ( state.backtracking>0 ) { memoize(input, 23, mult_expr_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "mult_expr"



    // $ANTLR start "operator_expr"
    // R.g:189:1: operator_expr returns [Node v] : l= column_expr ( ( ( OP )=>op= OP n_ r= tilde_expr ) |) ;
    public final Node operator_expr() throws RecognitionException {
        Node v = null;

        int operator_expr_StartIndex = input.index();

        Token op=null;
        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 24) ) { return v; }

            // R.g:190:2: (l= column_expr ( ( ( OP )=>op= OP n_ r= tilde_expr ) |) )
            // R.g:190:4: l= column_expr ( ( ( OP )=>op= OP n_ r= tilde_expr ) |)
            {
            pushFollow(FOLLOW_column_expr_in_operator_expr1194);
            l=column_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:191:2: ( ( ( OP )=>op= OP n_ r= tilde_expr ) |)
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
            else if ( (LA25_0==EOF||(LA25_0 >= AND && LA25_0 <= BITWISEOR)||(LA25_0 >= CARRET && LA25_0 <= COMMENT)||(LA25_0 >= DIV && LA25_0 <= EQ)||LA25_0==FIELD||(LA25_0 >= GE && LA25_0 <= GT)||LA25_0==LBB||(LA25_0 >= LBRAKET && LA25_0 <= LE)||(LA25_0 >= LPAR && LA25_0 <= MINUS)||(LA25_0 >= MOD && LA25_0 <= NEWLINE)||LA25_0==OR||(LA25_0 >= PLUS && LA25_0 <= RBRAKET)||(LA25_0 >= RIGHT_ARROW && LA25_0 <= SEMICOLUMN)||(LA25_0 >= SUPER_ARROW && LA25_0 <= TILDE)) ) {
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
                    // R.g:191:3: ( ( OP )=>op= OP n_ r= tilde_expr )
                    {
                    // R.g:191:3: ( ( OP )=>op= OP n_ r= tilde_expr )
                    // R.g:191:4: ( OP )=>op= OP n_ r= tilde_expr
                    {
                    op=(Token)match(input,OP,FOLLOW_OP_in_operator_expr1205); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_operator_expr1207);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_tilde_expr_in_operator_expr1211);
                    r=tilde_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.custom_operator(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:192:7: 
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
    // R.g:194:1: column_expr returns [Node v] : l= power_expr ( ( ( COLUMN )=>op= COLUMN n_ r= tilde_expr ) |) ;
    public final Node column_expr() throws RecognitionException {
        Node v = null;

        int column_expr_StartIndex = input.index();

        Token op=null;
        Node l =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 25) ) { return v; }

            // R.g:195:2: (l= power_expr ( ( ( COLUMN )=>op= COLUMN n_ r= tilde_expr ) |) )
            // R.g:195:4: l= power_expr ( ( ( COLUMN )=>op= COLUMN n_ r= tilde_expr ) |)
            {
            pushFollow(FOLLOW_power_expr_in_column_expr1240);
            l=power_expr();

            state._fsp--;
            if (state.failed) return v;

            // R.g:196:2: ( ( ( COLUMN )=>op= COLUMN n_ r= tilde_expr ) |)
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
            else if ( (LA26_0==EOF||(LA26_0 >= AND && LA26_0 <= BITWISEOR)||LA26_0==CARRET||(LA26_0 >= COMMA && LA26_0 <= COMMENT)||(LA26_0 >= DIV && LA26_0 <= EQ)||LA26_0==FIELD||(LA26_0 >= GE && LA26_0 <= GT)||LA26_0==LBB||(LA26_0 >= LBRAKET && LA26_0 <= LE)||(LA26_0 >= LPAR && LA26_0 <= MINUS)||(LA26_0 >= MOD && LA26_0 <= NEWLINE)||LA26_0==OP||LA26_0==OR||(LA26_0 >= PLUS && LA26_0 <= RBRAKET)||(LA26_0 >= RIGHT_ARROW && LA26_0 <= SEMICOLUMN)||(LA26_0 >= SUPER_ARROW && LA26_0 <= TILDE)) ) {
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
                    // R.g:196:3: ( ( COLUMN )=>op= COLUMN n_ r= tilde_expr )
                    {
                    // R.g:196:3: ( ( COLUMN )=>op= COLUMN n_ r= tilde_expr )
                    // R.g:196:4: ( COLUMN )=>op= COLUMN n_ r= tilde_expr
                    {
                    op=(Token)match(input,COLUMN,FOLLOW_COLUMN_in_column_expr1251); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_column_expr1253);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_tilde_expr_in_column_expr1257);
                    r=tilde_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(BinaryOperator.COLUMN, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:197:7: 
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
    // R.g:199:1: power_expr returns [Node v] : l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |) ;
    public final Node power_expr() throws RecognitionException {
        Node v = null;

        int power_expr_StartIndex = input.index();

        Node l =null;

        BinaryOperator op =null;

        Node r =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 26) ) { return v; }

            // R.g:200:2: (l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |) )
            // R.g:200:4: l= unary_expression ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |)
            {
            pushFollow(FOLLOW_unary_expression_in_power_expr1286);
            l=unary_expression();

            state._fsp--;
            if (state.failed) return v;

            // R.g:201:5: ( ( ( power_operator )=>op= power_operator n_ r= power_expr ) |)
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
            else if ( (LA27_0==EOF||(LA27_0 >= AND && LA27_0 <= BITWISEOR)||(LA27_0 >= COLUMN && LA27_0 <= COMMENT)||(LA27_0 >= DIV && LA27_0 <= EQ)||LA27_0==FIELD||(LA27_0 >= GE && LA27_0 <= GT)||LA27_0==LBB||(LA27_0 >= LBRAKET && LA27_0 <= LE)||(LA27_0 >= LPAR && LA27_0 <= MINUS)||(LA27_0 >= MOD && LA27_0 <= NEWLINE)||LA27_0==OP||LA27_0==OR||(LA27_0 >= PLUS && LA27_0 <= RBRAKET)||(LA27_0 >= RIGHT_ARROW && LA27_0 <= SEMICOLUMN)||(LA27_0 >= SUPER_ARROW && LA27_0 <= TILDE)) ) {
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
                    // R.g:201:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    {
                    // R.g:201:6: ( ( power_operator )=>op= power_operator n_ r= power_expr )
                    // R.g:201:7: ( power_operator )=>op= power_operator n_ r= power_expr
                    {
                    pushFollow(FOLLOW_power_operator_in_power_expr1300);
                    op=power_operator();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_power_expr1302);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_power_expr_in_power_expr1306);
                    r=power_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.binary(op, l, r);}

                    }


                    }
                    break;
                case 2 :
                    // R.g:202:7: 
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
    // R.g:204:1: unary_expression returns [Node v] : ( NOT n_ l= unary_expression | PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression |b= basic_expr );
    public final Node unary_expression() throws RecognitionException {
        Node v = null;

        int unary_expression_StartIndex = input.index();

        Node l =null;

        Node b =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 27) ) { return v; }

            // R.g:205:2: ( NOT n_ l= unary_expression | PLUS n_ l= unary_expression | MINUS n_ l= unary_expression | TILDE n_ l= unary_expression |b= basic_expr )
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
            case DD:
            case FALSE:
            case FOR:
            case FUNCTION:
            case ID:
            case IF:
            case LBRACE:
            case LPAR:
            case NEXT:
            case NULL:
            case NUMBER:
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
                    // R.g:205:4: NOT n_ l= unary_expression
                    {
                    match(input,NOT,FOLLOW_NOT_in_unary_expression1336); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1338);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1342);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.unary(UnaryOperator.NOT, l);}

                    }
                    break;
                case 2 :
                    // R.g:206:4: PLUS n_ l= unary_expression
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_unary_expression1349); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1351);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1355);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.unary(UnaryOperator.PLUS, l);}

                    }
                    break;
                case 3 :
                    // R.g:207:4: MINUS n_ l= unary_expression
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_unary_expression1362); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1364);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1368);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.unary(UnaryOperator.MINUS, l);}

                    }
                    break;
                case 4 :
                    // R.g:208:4: TILDE n_ l= unary_expression
                    {
                    match(input,TILDE,FOLLOW_TILDE_in_unary_expression1375); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_unary_expression1377);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_unary_expression_in_unary_expression1381);
                    l=unary_expression();

                    state._fsp--;
                    if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.unary(UnaryOperator.MODEL, l);}

                    }
                    break;
                case 5 :
                    // R.g:209:4: b= basic_expr
                    {
                    pushFollow(FOLLOW_basic_expr_in_unary_expression1390);
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
    // R.g:211:1: basic_expr returns [Node v] : lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) ;
    public final Node basic_expr() throws RecognitionException {
        Node v = null;

        int basic_expr_StartIndex = input.index();

        Node lhs =null;

        Node subset =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 28) ) { return v; }

            // R.g:212:2: (lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>) )
            // R.g:212:4: lhs= simple_expr ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
            {
            pushFollow(FOLLOW_simple_expr_in_basic_expr1408);
            lhs=simple_expr();

            state._fsp--;
            if (state.failed) return v;

            if ( state.backtracking==0 ) { v = lhs; }

            // R.g:213:2: ( ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+ | ( n_ )=>)
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
                    // R.g:213:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
                    {
                    // R.g:213:3: ( ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v] )+
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
                    	    // R.g:213:4: ( FIELD | AT | LBRAKET | LBB | LPAR )=>subset= expr_subset[v]
                    	    {
                    	    pushFollow(FOLLOW_expr_subset_in_basic_expr1429);
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
                    // R.g:213:76: ( n_ )=>
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
    // R.g:215:1: expr_subset[Node i] returns [Node v] : ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= expr_list RBRAKET ) | ( LBB subscript= expr_list RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) );
    public final Node expr_subset(Node i) throws RecognitionException {
        Node v = null;

        int expr_subset_StartIndex = input.index();

        Node name =null;

        Map<Id, Node> subset =null;

        Map<Id, Node> subscript =null;

        Map<Id, Node> a =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 29) ) { return v; }

            // R.g:216:5: ( ( FIELD n_ name= id ) | ( AT n_ name= id ) | ( LBRAKET subset= expr_list RBRAKET ) | ( LBB subscript= expr_list RBRAKET RBRAKET ) | ( LPAR a= args RPAR ) )
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
                    // R.g:216:7: ( FIELD n_ name= id )
                    {
                    // R.g:216:7: ( FIELD n_ name= id )
                    // R.g:216:8: FIELD n_ name= id
                    {
                    match(input,FIELD,FOLLOW_FIELD_in_expr_subset1462); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_expr_subset1464);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_expr_subset1468);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Factory.binary(BinaryOperator.FIELD, i, name); }

                    }
                    break;
                case 2 :
                    // R.g:217:7: ( AT n_ name= id )
                    {
                    // R.g:217:7: ( AT n_ name= id )
                    // R.g:217:8: AT n_ name= id
                    {
                    match(input,AT,FOLLOW_AT_in_expr_subset1481); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_expr_subset1483);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_id_in_expr_subset1487);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Factory.binary(BinaryOperator.AT, i, name); }

                    }
                    break;
                case 3 :
                    // R.g:218:7: ( LBRAKET subset= expr_list RBRAKET )
                    {
                    // R.g:218:7: ( LBRAKET subset= expr_list RBRAKET )
                    // R.g:218:8: LBRAKET subset= expr_list RBRAKET
                    {
                    match(input,LBRAKET,FOLLOW_LBRAKET_in_expr_subset1501); if (state.failed) return v;

                    pushFollow(FOLLOW_expr_list_in_expr_subset1505);
                    subset=expr_list();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1507); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Factory.call(CallOperator.SUBSET, i, subset); }

                    }
                    break;
                case 4 :
                    // R.g:219:7: ( LBB subscript= expr_list RBRAKET RBRAKET )
                    {
                    // R.g:219:7: ( LBB subscript= expr_list RBRAKET RBRAKET )
                    // R.g:219:8: LBB subscript= expr_list RBRAKET RBRAKET
                    {
                    match(input,LBB,FOLLOW_LBB_in_expr_subset1519); if (state.failed) return v;

                    pushFollow(FOLLOW_expr_list_in_expr_subset1523);
                    subscript=expr_list();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1525); if (state.failed) return v;

                    match(input,RBRAKET,FOLLOW_RBRAKET_in_expr_subset1527); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Factory.call(CallOperator.SUBSCRIPT, i, subscript); }

                    }
                    break;
                case 5 :
                    // R.g:221:7: ( LPAR a= args RPAR )
                    {
                    // R.g:221:7: ( LPAR a= args RPAR )
                    // R.g:221:8: LPAR a= args RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_expr_subset1544); if (state.failed) return v;

                    pushFollow(FOLLOW_args_in_expr_subset1548);
                    a=args();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,RPAR,FOLLOW_RPAR_in_expr_subset1550); if (state.failed) return v;

                    }


                    if ( state.backtracking==0 ) { v = Factory.call(CallOperator.CALL, i, a); }

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
    // R.g:224:1: simple_expr returns [Node n] : ( id |b= bool | DD | NULL | NUMBER | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ expr_or_assign n_ RPAR | sequence | expr_wo_assign );
    public final Node simple_expr() throws RecognitionException {
        Node n = null;

        int simple_expr_StartIndex = input.index();

        Node b =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 30) ) { return n; }

            // R.g:225:2: ( id |b= bool | DD | NULL | NUMBER | id NS_GET n_ id | id NS_GET_INT n_ id | LPAR n_ expr_or_assign n_ RPAR | sequence | expr_wo_assign )
            int alt32=10;
            switch ( input.LA(1) ) {
            case ID:
            case STRING:
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
                    if (state.backtracking>0) {state.failed=true; return n;}
                    NoViableAltException nvae =
                        new NoViableAltException("", 32, 1, input);

                    throw nvae;

                }

                }
                break;
            case FALSE:
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
            case NUMBER:
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
                if (state.backtracking>0) {state.failed=true; return n;}
                NoViableAltException nvae =
                    new NoViableAltException("", 32, 0, input);

                throw nvae;

            }

            switch (alt32) {
                case 1 :
                    // R.g:225:4: id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1577);
                    id();

                    state._fsp--;
                    if (state.failed) return n;

                    }
                    break;
                case 2 :
                    // R.g:226:4: b= bool
                    {
                    pushFollow(FOLLOW_bool_in_simple_expr1584);
                    b=bool();

                    state._fsp--;
                    if (state.failed) return n;

                    if ( state.backtracking==0 ) { n = b; }

                    }
                    break;
                case 3 :
                    // R.g:227:4: DD
                    {
                    match(input,DD,FOLLOW_DD_in_simple_expr1591); if (state.failed) return n;

                    }
                    break;
                case 4 :
                    // R.g:228:4: NULL
                    {
                    match(input,NULL,FOLLOW_NULL_in_simple_expr1596); if (state.failed) return n;

                    }
                    break;
                case 5 :
                    // R.g:229:4: NUMBER
                    {
                    match(input,NUMBER,FOLLOW_NUMBER_in_simple_expr1601); if (state.failed) return n;

                    }
                    break;
                case 6 :
                    // R.g:230:4: id NS_GET n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1606);
                    id();

                    state._fsp--;
                    if (state.failed) return n;

                    match(input,NS_GET,FOLLOW_NS_GET_in_simple_expr1608); if (state.failed) return n;

                    pushFollow(FOLLOW_n__in_simple_expr1610);
                    n_();

                    state._fsp--;
                    if (state.failed) return n;

                    pushFollow(FOLLOW_id_in_simple_expr1612);
                    id();

                    state._fsp--;
                    if (state.failed) return n;

                    }
                    break;
                case 7 :
                    // R.g:231:4: id NS_GET_INT n_ id
                    {
                    pushFollow(FOLLOW_id_in_simple_expr1617);
                    id();

                    state._fsp--;
                    if (state.failed) return n;

                    match(input,NS_GET_INT,FOLLOW_NS_GET_INT_in_simple_expr1619); if (state.failed) return n;

                    pushFollow(FOLLOW_n__in_simple_expr1621);
                    n_();

                    state._fsp--;
                    if (state.failed) return n;

                    pushFollow(FOLLOW_id_in_simple_expr1623);
                    id();

                    state._fsp--;
                    if (state.failed) return n;

                    }
                    break;
                case 8 :
                    // R.g:232:4: LPAR n_ expr_or_assign n_ RPAR
                    {
                    match(input,LPAR,FOLLOW_LPAR_in_simple_expr1628); if (state.failed) return n;

                    pushFollow(FOLLOW_n__in_simple_expr1630);
                    n_();

                    state._fsp--;
                    if (state.failed) return n;

                    pushFollow(FOLLOW_expr_or_assign_in_simple_expr1632);
                    expr_or_assign();

                    state._fsp--;
                    if (state.failed) return n;

                    pushFollow(FOLLOW_n__in_simple_expr1634);
                    n_();

                    state._fsp--;
                    if (state.failed) return n;

                    match(input,RPAR,FOLLOW_RPAR_in_simple_expr1636); if (state.failed) return n;

                    }
                    break;
                case 9 :
                    // R.g:233:4: sequence
                    {
                    pushFollow(FOLLOW_sequence_in_simple_expr1641);
                    sequence();

                    state._fsp--;
                    if (state.failed) return n;

                    }
                    break;
                case 10 :
                    // R.g:234:4: expr_wo_assign
                    {
                    pushFollow(FOLLOW_expr_wo_assign_in_simple_expr1646);
                    expr_wo_assign();

                    state._fsp--;
                    if (state.failed) return n;

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
        return n;
    }
    // $ANTLR end "simple_expr"



    // $ANTLR start "id"
    // R.g:236:1: id returns [Node n] : ( ID | STRING | VARIATIC );
    public final Node id() throws RecognitionException {
        Node n = null;

        int id_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 31) ) { return n; }

            // R.g:237:5: ( ID | STRING | VARIATIC )
            // R.g:
            {
            if ( input.LA(1)==ID||input.LA(1)==STRING||input.LA(1)==VARIATIC ) {
                input.consume();
                state.errorRecovery=false;
                state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return n;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 31, id_StartIndex); }

        }
        return n;
    }
    // $ANTLR end "id"



    // $ANTLR start "bool"
    // R.g:240:1: bool returns [Node v] : ( TRUE | FALSE );
    public final Node bool() throws RecognitionException {
        Node v = null;

        int bool_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 32) ) { return v; }

            // R.g:241:5: ( TRUE | FALSE )
            int alt33=2;
            int LA33_0 = input.LA(1);

            if ( (LA33_0==TRUE) ) {
                alt33=1;
            }
            else if ( (LA33_0==FALSE) ) {
                alt33=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 33, 0, input);

                throw nvae;

            }
            switch (alt33) {
                case 1 :
                    // R.g:241:7: TRUE
                    {
                    match(input,TRUE,FOLLOW_TRUE_in_bool1694); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.createConstant(true); }

                    }
                    break;
                case 2 :
                    // R.g:242:7: FALSE
                    {
                    match(input,FALSE,FOLLOW_FALSE_in_bool1704); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = Factory.createConstant(false); }

                    }
                    break;

            }
        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 32, bool_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "bool"



    // $ANTLR start "or_operator"
    // R.g:243:1: or_operator returns [BinaryOperator v] : ( OR | BITWISEOR );
    public final BinaryOperator or_operator() throws RecognitionException {
        BinaryOperator v = null;

        int or_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 33) ) { return v; }

            // R.g:244:2: ( OR | BITWISEOR )
            int alt34=2;
            int LA34_0 = input.LA(1);

            if ( (LA34_0==OR) ) {
                alt34=1;
            }
            else if ( (LA34_0==BITWISEOR) ) {
                alt34=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return v;}
                NoViableAltException nvae =
                    new NoViableAltException("", 34, 0, input);

                throw nvae;

            }
            switch (alt34) {
                case 1 :
                    // R.g:244:4: OR
                    {
                    match(input,OR,FOLLOW_OR_in_or_operator1718); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.OR; }

                    }
                    break;
                case 2 :
                    // R.g:245:5: BITWISEOR
                    {
                    match(input,BITWISEOR,FOLLOW_BITWISEOR_in_or_operator1735); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 33, or_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "or_operator"



    // $ANTLR start "and_operator"
    // R.g:246:1: and_operator returns [BinaryOperator v] : ( AND | BITWISEAND );
    public final BinaryOperator and_operator() throws RecognitionException {
        BinaryOperator v = null;

        int and_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 34) ) { return v; }

            // R.g:247:2: ( AND | BITWISEAND )
            int alt35=2;
            int LA35_0 = input.LA(1);

            if ( (LA35_0==AND) ) {
                alt35=1;
            }
            else if ( (LA35_0==BITWISEAND) ) {
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
                    // R.g:247:4: AND
                    {
                    match(input,AND,FOLLOW_AND_in_and_operator1751); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.AND; }

                    }
                    break;
                case 2 :
                    // R.g:248:4: BITWISEAND
                    {
                    match(input,BITWISEAND,FOLLOW_BITWISEAND_in_and_operator1767); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 34, and_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "and_operator"



    // $ANTLR start "comp_operator"
    // R.g:249:1: comp_operator returns [BinaryOperator v] : ( GT | GE | LT | LE | EQ | NE );
    public final BinaryOperator comp_operator() throws RecognitionException {
        BinaryOperator v = null;

        int comp_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 35) ) { return v; }

            // R.g:250:2: ( GT | GE | LT | LE | EQ | NE )
            int alt36=6;
            switch ( input.LA(1) ) {
            case GT:
                {
                alt36=1;
                }
                break;
            case GE:
                {
                alt36=2;
                }
                break;
            case LT:
                {
                alt36=3;
                }
                break;
            case LE:
                {
                alt36=4;
                }
                break;
            case EQ:
                {
                alt36=5;
                }
                break;
            case NE:
                {
                alt36=6;
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
                    // R.g:250:4: GT
                    {
                    match(input,GT,FOLLOW_GT_in_comp_operator1783); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.GT; }

                    }
                    break;
                case 2 :
                    // R.g:251:4: GE
                    {
                    match(input,GE,FOLLOW_GE_in_comp_operator1790); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.GE; }

                    }
                    break;
                case 3 :
                    // R.g:252:4: LT
                    {
                    match(input,LT,FOLLOW_LT_in_comp_operator1797); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.LT; }

                    }
                    break;
                case 4 :
                    // R.g:253:4: LE
                    {
                    match(input,LE,FOLLOW_LE_in_comp_operator1804); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.LE; }

                    }
                    break;
                case 5 :
                    // R.g:254:4: EQ
                    {
                    match(input,EQ,FOLLOW_EQ_in_comp_operator1811); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.EQ; }

                    }
                    break;
                case 6 :
                    // R.g:255:4: NE
                    {
                    match(input,NE,FOLLOW_NE_in_comp_operator1818); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 35, comp_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "comp_operator"



    // $ANTLR start "add_operator"
    // R.g:256:1: add_operator returns [BinaryOperator v] : ( PLUS | MINUS );
    public final BinaryOperator add_operator() throws RecognitionException {
        BinaryOperator v = null;

        int add_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 36) ) { return v; }

            // R.g:257:2: ( PLUS | MINUS )
            int alt37=2;
            int LA37_0 = input.LA(1);

            if ( (LA37_0==PLUS) ) {
                alt37=1;
            }
            else if ( (LA37_0==MINUS) ) {
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
                    // R.g:257:4: PLUS
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_add_operator1832); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.ADD; }

                    }
                    break;
                case 2 :
                    // R.g:258:4: MINUS
                    {
                    match(input,MINUS,FOLLOW_MINUS_in_add_operator1839); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 36, add_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "add_operator"



    // $ANTLR start "mult_operator"
    // R.g:259:1: mult_operator returns [BinaryOperator v] : ( MULT | DIV | MOD );
    public final BinaryOperator mult_operator() throws RecognitionException {
        BinaryOperator v = null;

        int mult_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 37) ) { return v; }

            // R.g:260:2: ( MULT | DIV | MOD )
            int alt38=3;
            switch ( input.LA(1) ) {
            case MULT:
                {
                alt38=1;
                }
                break;
            case DIV:
                {
                alt38=2;
                }
                break;
            case MOD:
                {
                alt38=3;
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
                    // R.g:260:4: MULT
                    {
                    match(input,MULT,FOLLOW_MULT_in_mult_operator1854); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.MULT; }

                    }
                    break;
                case 2 :
                    // R.g:261:4: DIV
                    {
                    match(input,DIV,FOLLOW_DIV_in_mult_operator1861); if (state.failed) return v;

                    if ( state.backtracking==0 ) {v = BinaryOperator.DIV; }

                    }
                    break;
                case 3 :
                    // R.g:262:4: MOD
                    {
                    match(input,MOD,FOLLOW_MOD_in_mult_operator1869); if (state.failed) return v;

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
            if ( state.backtracking>0 ) { memoize(input, 37, mult_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "mult_operator"



    // $ANTLR start "power_operator"
    // R.g:263:1: power_operator returns [BinaryOperator v] : CARRET ;
    public final BinaryOperator power_operator() throws RecognitionException {
        BinaryOperator v = null;

        int power_operator_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 38) ) { return v; }

            // R.g:264:2: ( CARRET )
            // R.g:264:4: CARRET
            {
            match(input,CARRET,FOLLOW_CARRET_in_power_operator1884); if (state.failed) return v;

            if ( state.backtracking==0 ) {v = BinaryOperator.POW; }

            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 38, power_operator_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "power_operator"



    // $ANTLR start "expr_list"
    // R.g:266:1: expr_list returns [Map<Id, Node> v] : ( n_ expr_list_arg )? n_ ( COMMA ( n_ expr_list_arg )? n_ )* ;
    public final Map<Id, Node> expr_list() throws RecognitionException {
        Map<Id, Node> v = null;

        int expr_list_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 39) ) { return v; }

            // R.g:267:2: ( ( n_ expr_list_arg )? n_ ( COMMA ( n_ expr_list_arg )? n_ )* )
            // R.g:267:4: ( n_ expr_list_arg )? n_ ( COMMA ( n_ expr_list_arg )? n_ )*
            {
            // R.g:267:4: ( n_ expr_list_arg )?
            int alt39=2;
            alt39 = dfa39.predict(input);
            switch (alt39) {
                case 1 :
                    // R.g:267:5: n_ expr_list_arg
                    {
                    pushFollow(FOLLOW_n__in_expr_list1901);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_list_arg_in_expr_list1903);
                    expr_list_arg();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }


            pushFollow(FOLLOW_n__in_expr_list1907);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:267:27: ( COMMA ( n_ expr_list_arg )? n_ )*
            loop41:
            do {
                int alt41=2;
                int LA41_0 = input.LA(1);

                if ( (LA41_0==COMMA) ) {
                    alt41=1;
                }


                switch (alt41) {
            	case 1 :
            	    // R.g:267:28: COMMA ( n_ expr_list_arg )? n_
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expr_list1910); if (state.failed) return v;

            	    // R.g:267:34: ( n_ expr_list_arg )?
            	    int alt40=2;
            	    alt40 = dfa40.predict(input);
            	    switch (alt40) {
            	        case 1 :
            	            // R.g:267:35: n_ expr_list_arg
            	            {
            	            pushFollow(FOLLOW_n__in_expr_list1913);
            	            n_();

            	            state._fsp--;
            	            if (state.failed) return v;

            	            pushFollow(FOLLOW_expr_list_arg_in_expr_list1915);
            	            expr_list_arg();

            	            state._fsp--;
            	            if (state.failed) return v;

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_n__in_expr_list1919);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    }
            	    break;

            	default :
            	    break loop41;
                }
            } while (true);


            }

        }

            catch(RecognitionException re){
                throw re; // Stop at first error
            }

        finally {
        	// do for sure before leaving
            if ( state.backtracking>0 ) { memoize(input, 39, expr_list_StartIndex); }

        }
        return v;
    }
    // $ANTLR end "expr_list"



    // $ANTLR start "expr_list_arg"
    // R.g:269:1: expr_list_arg : ( expr |name= id n_ ASSIGN n_ v= expr );
    public final void expr_list_arg() throws RecognitionException {
        int expr_list_arg_StartIndex = input.index();

        Node name =null;

        Node v =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 40) ) { return ; }

            // R.g:270:2: ( expr |name= id n_ ASSIGN n_ v= expr )
            int alt42=2;
            alt42 = dfa42.predict(input);
            switch (alt42) {
                case 1 :
                    // R.g:270:4: expr
                    {
                    pushFollow(FOLLOW_expr_in_expr_list_arg1932);
                    expr();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // R.g:271:4: name= id n_ ASSIGN n_ v= expr
                    {
                    pushFollow(FOLLOW_id_in_expr_list_arg1940);
                    name=id();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_expr_list_arg1942);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_expr_list_arg1944); if (state.failed) return ;

                    pushFollow(FOLLOW_n__in_expr_list_arg1946);
                    n_();

                    state._fsp--;
                    if (state.failed) return ;

                    pushFollow(FOLLOW_expr_in_expr_list_arg1950);
                    v=expr();

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
            if ( state.backtracking>0 ) { memoize(input, 40, expr_list_arg_StartIndex); }

        }
        return ;
    }
    // $ANTLR end "expr_list_arg"



    // $ANTLR start "args"
    // R.g:273:1: args returns [Map<Id, Node> v] : ( n_ arg_expr )? n_ ( COMMA ( n_ arg_expr )? n_ )* ;
    public final Map<Id, Node> args() throws RecognitionException {
        Map<Id, Node> v = null;

        int args_StartIndex = input.index();

        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 41) ) { return v; }

            // R.g:274:5: ( ( n_ arg_expr )? n_ ( COMMA ( n_ arg_expr )? n_ )* )
            // R.g:274:7: ( n_ arg_expr )? n_ ( COMMA ( n_ arg_expr )? n_ )*
            {
            // R.g:274:7: ( n_ arg_expr )?
            int alt43=2;
            alt43 = dfa43.predict(input);
            switch (alt43) {
                case 1 :
                    // R.g:274:8: n_ arg_expr
                    {
                    pushFollow(FOLLOW_n__in_args1969);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_arg_expr_in_args1971);
                    arg_expr();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;

            }


            pushFollow(FOLLOW_n__in_args1975);
            n_();

            state._fsp--;
            if (state.failed) return v;

            // R.g:274:25: ( COMMA ( n_ arg_expr )? n_ )*
            loop45:
            do {
                int alt45=2;
                int LA45_0 = input.LA(1);

                if ( (LA45_0==COMMA) ) {
                    alt45=1;
                }


                switch (alt45) {
            	case 1 :
            	    // R.g:274:26: COMMA ( n_ arg_expr )? n_
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_args1978); if (state.failed) return v;

            	    // R.g:274:32: ( n_ arg_expr )?
            	    int alt44=2;
            	    alt44 = dfa44.predict(input);
            	    switch (alt44) {
            	        case 1 :
            	            // R.g:274:33: n_ arg_expr
            	            {
            	            pushFollow(FOLLOW_n__in_args1981);
            	            n_();

            	            state._fsp--;
            	            if (state.failed) return v;

            	            pushFollow(FOLLOW_arg_expr_in_args1983);
            	            arg_expr();

            	            state._fsp--;
            	            if (state.failed) return v;

            	            }
            	            break;

            	    }


            	    pushFollow(FOLLOW_n__in_args1987);
            	    n_();

            	    state._fsp--;
            	    if (state.failed) return v;

            	    }
            	    break;

            	default :
            	    break loop45;
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
    // R.g:276:1: arg_expr returns [Map<Id, Node> v] : ( expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );
    public final Map<Id, Node> arg_expr() throws RecognitionException {
        Map<Id, Node> v = null;

        int arg_expr_StartIndex = input.index();

        Node name =null;

        Node val =null;


        try {
            if ( state.backtracking>0 && alreadyParsedRule(input, 42) ) { return v; }

            // R.g:277:2: ( expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN )
            int alt46=5;
            alt46 = dfa46.predict(input);
            switch (alt46) {
                case 1 :
                    // R.g:277:4: expr
                    {
                    pushFollow(FOLLOW_expr_in_arg_expr2004);
                    expr();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 2 :
                    // R.g:278:4: name= id n_ ASSIGN n_ val= expr
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2012);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_arg_expr2014);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2016); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_arg_expr2018);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_arg_expr2022);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 3 :
                    // R.g:279:4: name= id n_ ASSIGN
                    {
                    pushFollow(FOLLOW_id_in_arg_expr2030);
                    name=id();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_arg_expr2032);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2034); if (state.failed) return v;

                    }
                    break;
                case 4 :
                    // R.g:280:4: NULL n_ ASSIGN n_ val= expr
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2040); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_arg_expr2042);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2044); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_arg_expr2046);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    pushFollow(FOLLOW_expr_in_arg_expr2050);
                    val=expr();

                    state._fsp--;
                    if (state.failed) return v;

                    }
                    break;
                case 5 :
                    // R.g:281:4: NULL n_ ASSIGN
                    {
                    match(input,NULL,FOLLOW_NULL_in_arg_expr2056); if (state.failed) return v;

                    pushFollow(FOLLOW_n__in_arg_expr2058);
                    n_();

                    state._fsp--;
                    if (state.failed) return v;

                    match(input,ASSIGN,FOLLOW_ASSIGN_in_arg_expr2060); if (state.failed) return v;

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
        return v;
    }
    // $ANTLR end "arg_expr"

    // $ANTLR start synpred1_R
    public final void synpred1_R_fragment() throws RecognitionException {
        // R.g:108:10: ( LPAR )
        // R.g:108:11: LPAR
        {
        match(input,LPAR,FOLLOW_LPAR_in_synpred1_R334); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred1_R

    // $ANTLR start synpred2_R
    public final void synpred2_R_fragment() throws RecognitionException {
        // R.g:109:11: ( LPAR )
        // R.g:109:12: LPAR
        {
        match(input,LPAR,FOLLOW_LPAR_in_synpred2_R353); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred2_R

    // $ANTLR start synpred3_R
    public final void synpred3_R_fragment() throws RecognitionException {
        // R.g:127:5: ( ARROW )
        // R.g:127:6: ARROW
        {
        match(input,ARROW,FOLLOW_ARROW_in_synpred3_R530); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred3_R

    // $ANTLR start synpred4_R
    public final void synpred4_R_fragment() throws RecognitionException {
        // R.g:128:5: ( SUPER_ARROW )
        // R.g:128:6: SUPER_ARROW
        {
        match(input,SUPER_ARROW,FOLLOW_SUPER_ARROW_in_synpred4_R548); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred4_R

    // $ANTLR start synpred5_R
    public final void synpred5_R_fragment() throws RecognitionException {
        // R.g:129:5: ( RIGHT_ARROW )
        // R.g:129:6: RIGHT_ARROW
        {
        match(input,RIGHT_ARROW,FOLLOW_RIGHT_ARROW_in_synpred5_R566); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred5_R

    // $ANTLR start synpred6_R
    public final void synpred6_R_fragment() throws RecognitionException {
        // R.g:130:5: ( SUPER_RIGHT_ARROW )
        // R.g:130:6: SUPER_RIGHT_ARROW
        {
        match(input,SUPER_RIGHT_ARROW,FOLLOW_SUPER_RIGHT_ARROW_in_synpred6_R586); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred6_R

    // $ANTLR start synpred7_R
    public final void synpred7_R_fragment() throws RecognitionException {
        // R.g:131:5: ( ASSIGN )
        // R.g:131:6: ASSIGN
        {
        match(input,ASSIGN,FOLLOW_ASSIGN_in_synpred7_R606); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred7_R

    // $ANTLR start synpred8_R
    public final void synpred8_R_fragment() throws RecognitionException {
        // R.g:138:3: ( n_ ELSE )
        // R.g:138:4: n_ ELSE
        {
        pushFollow(FOLLOW_n__in_synpred8_R669);
        n_();

        state._fsp--;
        if (state.failed) return ;

        match(input,ELSE,FOLLOW_ELSE_in_synpred8_R671); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred8_R

    // $ANTLR start synpred9_R
    public final void synpred9_R_fragment() throws RecognitionException {
        // R.g:161:5: ( TILDE )
        // R.g:161:6: TILDE
        {
        match(input,TILDE,FOLLOW_TILDE_in_synpred9_R916); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred9_R

    // $ANTLR start synpred10_R
    public final void synpred10_R_fragment() throws RecognitionException {
        // R.g:166:4: ( or_operator )
        // R.g:166:5: or_operator
        {
        pushFollow(FOLLOW_or_operator_in_synpred10_R957);
        or_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred10_R

    // $ANTLR start synpred11_R
    public final void synpred11_R_fragment() throws RecognitionException {
        // R.g:171:7: ( and_operator )
        // R.g:171:8: and_operator
        {
        pushFollow(FOLLOW_and_operator_in_synpred11_R1007);
        and_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred11_R

    // $ANTLR start synpred12_R
    public final void synpred12_R_fragment() throws RecognitionException {
        // R.g:176:7: ( comp_operator )
        // R.g:176:8: comp_operator
        {
        pushFollow(FOLLOW_comp_operator_in_synpred12_R1057);
        comp_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred12_R

    // $ANTLR start synpred13_R
    public final void synpred13_R_fragment() throws RecognitionException {
        // R.g:181:5: ( add_operator )
        // R.g:181:6: add_operator
        {
        pushFollow(FOLLOW_add_operator_in_synpred13_R1108);
        add_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred13_R

    // $ANTLR start synpred14_R
    public final void synpred14_R_fragment() throws RecognitionException {
        // R.g:186:4: ( mult_operator )
        // R.g:186:5: mult_operator
        {
        pushFollow(FOLLOW_mult_operator_in_synpred14_R1154);
        mult_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred14_R

    // $ANTLR start synpred15_R
    public final void synpred15_R_fragment() throws RecognitionException {
        // R.g:191:4: ( OP )
        // R.g:191:5: OP
        {
        match(input,OP,FOLLOW_OP_in_synpred15_R1200); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred15_R

    // $ANTLR start synpred16_R
    public final void synpred16_R_fragment() throws RecognitionException {
        // R.g:196:4: ( COLUMN )
        // R.g:196:5: COLUMN
        {
        match(input,COLUMN,FOLLOW_COLUMN_in_synpred16_R1246); if (state.failed) return ;

        }

    }
    // $ANTLR end synpred16_R

    // $ANTLR start synpred17_R
    public final void synpred17_R_fragment() throws RecognitionException {
        // R.g:201:7: ( power_operator )
        // R.g:201:8: power_operator
        {
        pushFollow(FOLLOW_power_operator_in_synpred17_R1295);
        power_operator();

        state._fsp--;
        if (state.failed) return ;

        }

    }
    // $ANTLR end synpred17_R

    // $ANTLR start synpred18_R
    public final void synpred18_R_fragment() throws RecognitionException {
        // R.g:213:4: ( FIELD | AT | LBRAKET | LBB | LPAR )
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
        // R.g:213:76: ( n_ )
        // R.g:213:77: n_
        {
        pushFollow(FOLLOW_n__in_synpred19_R1439);
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
    protected DFA39 dfa39 = new DFA39(this);
    protected DFA40 dfa40 = new DFA40(this);
    protected DFA42 dfa42 = new DFA42(this);
    protected DFA43 dfa43 = new DFA43(this);
    protected DFA44 dfa44 = new DFA44(this);
    protected DFA46 dfa46 = new DFA46(this);
    static final String DFA7_eotS =
        "\7\uffff";
    static final String DFA7_eofS =
        "\1\2\6\uffff";
    static final String DFA7_minS =
        "\1\4\1\13\1\uffff\1\13\2\0\1\uffff";
    static final String DFA7_maxS =
        "\1\107\1\116\1\uffff\1\116\2\0\1\uffff";
    static final String DFA7_acceptS =
        "\2\uffff\1\2\3\uffff\1\1";
    static final String DFA7_specialS =
        "\4\uffff\1\1\1\0\1\uffff}>";
    static final String[] DFA7_transitionS = {
            "\6\2\3\uffff\4\2\1\uffff\3\2\3\uffff\1\2\2\uffff\2\2\7\uffff"+
            "\1\2\1\uffff\2\2\1\uffff\1\1\2\2\1\uffff\4\2\7\uffff\1\2\1\uffff"+
            "\1\2\1\uffff\3\2\1\uffff\3\2\2\uffff\3\2",
            "\1\2\3\uffff\1\2\1\3\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1"+
            "\3\2\2\2\uffff\2\2\5\uffff\1\2\2\uffff\1\2\1\uffff\1\4\2\uffff"+
            "\1\2\2\uffff\2\2\4\uffff\2\2",
            "",
            "\1\2\3\uffff\1\2\1\3\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1"+
            "\3\2\2\2\uffff\2\2\5\uffff\1\2\2\uffff\1\2\1\uffff\1\5\2\uffff"+
            "\1\2\2\uffff\2\2\4\uffff\2\2",
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
            return "108:9: ( ( LPAR )=> LPAR n_ RPAR )?";
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
        "\1\107\1\116\1\uffff\1\116\2\0\1\uffff";
    static final String DFA8_acceptS =
        "\2\uffff\1\2\3\uffff\1\1";
    static final String DFA8_specialS =
        "\4\uffff\1\1\1\0\1\uffff}>";
    static final String[] DFA8_transitionS = {
            "\6\2\3\uffff\4\2\1\uffff\3\2\3\uffff\1\2\2\uffff\2\2\7\uffff"+
            "\1\2\1\uffff\2\2\1\uffff\1\1\2\2\1\uffff\4\2\7\uffff\1\2\1\uffff"+
            "\1\2\1\uffff\3\2\1\uffff\3\2\2\uffff\3\2",
            "\1\2\3\uffff\1\2\1\3\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1"+
            "\3\2\2\2\uffff\2\2\5\uffff\1\2\2\uffff\1\2\1\uffff\1\4\2\uffff"+
            "\1\2\2\uffff\2\2\4\uffff\2\2",
            "",
            "\1\2\3\uffff\1\2\1\3\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1"+
            "\3\2\2\2\uffff\2\2\5\uffff\1\2\2\uffff\1\2\1\uffff\1\5\2\uffff"+
            "\1\2\2\uffff\2\2\4\uffff\2\2",
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
            return "109:10: ( ( LPAR )=> LPAR n_ RPAR )?";
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
        "\1\102\3\116\2\uffff\1\116";
    static final String DFA10_acceptS =
        "\4\uffff\1\2\1\1\1\uffff";
    static final String DFA10_specialS =
        "\7\uffff}>";
    static final String[] DFA10_transitionS = {
            "\1\1\37\uffff\1\1\14\uffff\1\4\4\uffff\1\3",
            "\1\5\4\uffff\1\1\1\5\5\uffff\1\5\1\uffff\2\5\4\uffff\1\5\1"+
            "\uffff\1\5\3\uffff\1\5\3\uffff\1\5\1\uffff\1\5\4\uffff\1\1\2"+
            "\5\2\uffff\2\5\5\uffff\1\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff"+
            "\2\5\4\uffff\2\5",
            "\1\5\5\uffff\1\5\5\uffff\1\5\1\uffff\2\5\4\uffff\1\5\1\uffff"+
            "\1\5\3\uffff\1\5\3\uffff\1\5\1\uffff\1\5\5\uffff\2\5\2\uffff"+
            "\2\5\5\uffff\1\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff\2\5\4\uffff"+
            "\2\5",
            "\1\5\4\uffff\1\6\1\5\5\uffff\1\5\1\uffff\2\5\4\uffff\1\5\1"+
            "\uffff\1\5\3\uffff\1\5\3\uffff\1\5\1\uffff\1\5\4\uffff\1\6\2"+
            "\5\2\uffff\2\5\5\uffff\1\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff"+
            "\2\5\4\uffff\2\5",
            "",
            "",
            "\1\5\4\uffff\1\6\1\5\5\uffff\1\5\1\uffff\2\5\4\uffff\1\5\1"+
            "\uffff\1\5\3\uffff\1\5\3\uffff\1\5\1\uffff\1\5\4\uffff\1\6\2"+
            "\5\2\uffff\2\5\5\uffff\1\5\1\4\1\uffff\1\5\4\uffff\1\5\2\uffff"+
            "\2\5\4\uffff\2\5"
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
            return "()* loopback of 114:32: ( n e= expr_or_assign )*";
        }
    }
    static final String DFA16_eotS =
        "\4\uffff";
    static final String DFA16_eofS =
        "\4\uffff";
    static final String DFA16_minS =
        "\2\17\2\uffff";
    static final String DFA16_maxS =
        "\2\101\2\uffff";
    static final String DFA16_acceptS =
        "\2\uffff\1\2\1\1";
    static final String DFA16_specialS =
        "\4\uffff}>";
    static final String[] DFA16_transitionS = {
            "\1\3\1\1\37\uffff\1\1\20\uffff\1\2",
            "\1\3\1\1\37\uffff\1\1\20\uffff\1\2",
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
            return "()* loopback of 152:35: ( n_ COMMA n_ par_decl )*";
        }
    }
    static final String DFA18_eotS =
        "\6\uffff";
    static final String DFA18_eofS =
        "\6\uffff";
    static final String DFA18_minS =
        "\1\37\1\6\1\uffff\1\6\2\uffff";
    static final String DFA18_maxS =
        "\1\115\1\101\1\uffff\1\101\2\uffff";
    static final String DFA18_acceptS =
        "\2\uffff\1\3\1\uffff\1\1\1\2";
    static final String DFA18_specialS =
        "\6\uffff}>";
    static final String[] DFA18_transitionS = {
            "\1\1\55\uffff\1\2",
            "\1\5\10\uffff\1\4\1\3\37\uffff\1\3\20\uffff\1\4",
            "",
            "\1\5\10\uffff\1\4\1\3\37\uffff\1\3\20\uffff\1\4",
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
            return "154:1: par_decl : ( ID | ID n_ ASSIGN n_ expr | VARIATIC );";
        }
    }
    static final String DFA39_eotS =
        "\4\uffff";
    static final String DFA39_eofS =
        "\4\uffff";
    static final String DFA39_minS =
        "\2\13\2\uffff";
    static final String DFA39_maxS =
        "\2\116\2\uffff";
    static final String DFA39_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA39_specialS =
        "\4\uffff}>";
    static final String[] DFA39_transitionS = {
            "\1\2\3\uffff\1\3\1\1\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1\2"+
            "\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1\1"+
            "\2\2\2\uffff\2\2\5\uffff\1\2\1\uffff\1\3\1\2\4\uffff\1\2\2\uffff"+
            "\2\2\4\uffff\2\2",
            "\1\2\3\uffff\1\3\1\1\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1"+
            "\1\2\2\2\uffff\2\2\5\uffff\1\2\1\uffff\1\3\1\2\4\uffff\1\2\2"+
            "\uffff\2\2\4\uffff\2\2",
            "",
            ""
    };

    static final short[] DFA39_eot = DFA.unpackEncodedString(DFA39_eotS);
    static final short[] DFA39_eof = DFA.unpackEncodedString(DFA39_eofS);
    static final char[] DFA39_min = DFA.unpackEncodedStringToUnsignedChars(DFA39_minS);
    static final char[] DFA39_max = DFA.unpackEncodedStringToUnsignedChars(DFA39_maxS);
    static final short[] DFA39_accept = DFA.unpackEncodedString(DFA39_acceptS);
    static final short[] DFA39_special = DFA.unpackEncodedString(DFA39_specialS);
    static final short[][] DFA39_transition;

    static {
        int numStates = DFA39_transitionS.length;
        DFA39_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA39_transition[i] = DFA.unpackEncodedString(DFA39_transitionS[i]);
        }
    }

    class DFA39 extends DFA {

        public DFA39(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 39;
            this.eot = DFA39_eot;
            this.eof = DFA39_eof;
            this.min = DFA39_min;
            this.max = DFA39_max;
            this.accept = DFA39_accept;
            this.special = DFA39_special;
            this.transition = DFA39_transition;
        }
        @Override
        public String getDescription() {
            return "267:4: ( n_ expr_list_arg )?";
        }
    }
    static final String DFA40_eotS =
        "\4\uffff";
    static final String DFA40_eofS =
        "\4\uffff";
    static final String DFA40_minS =
        "\2\13\2\uffff";
    static final String DFA40_maxS =
        "\2\116\2\uffff";
    static final String DFA40_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA40_specialS =
        "\4\uffff}>";
    static final String[] DFA40_transitionS = {
            "\1\2\3\uffff\1\3\1\1\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1\2"+
            "\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1\1"+
            "\2\2\2\uffff\2\2\5\uffff\1\2\1\uffff\1\3\1\2\4\uffff\1\2\2\uffff"+
            "\2\2\4\uffff\2\2",
            "\1\2\3\uffff\1\3\1\1\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1"+
            "\1\2\2\2\uffff\2\2\5\uffff\1\2\1\uffff\1\3\1\2\4\uffff\1\2\2"+
            "\uffff\2\2\4\uffff\2\2",
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
            return "267:34: ( n_ expr_list_arg )?";
        }
    }
    static final String DFA42_eotS =
        "\5\uffff";
    static final String DFA42_eofS =
        "\5\uffff";
    static final String DFA42_minS =
        "\1\13\1\uffff\1\4\1\6\1\uffff";
    static final String DFA42_maxS =
        "\1\116\1\uffff\1\107\1\76\1\uffff";
    static final String DFA42_acceptS =
        "\1\uffff\1\1\2\uffff\1\2";
    static final String DFA42_specialS =
        "\5\uffff}>";
    static final String[] DFA42_transitionS = {
            "\1\1\5\uffff\1\1\5\uffff\1\1\1\uffff\2\1\4\uffff\1\2\1\uffff"+
            "\1\1\3\uffff\1\1\3\uffff\1\1\1\uffff\1\1\5\uffff\2\1\2\uffff"+
            "\2\1\5\uffff\1\1\2\uffff\1\1\4\uffff\1\2\2\uffff\2\1\4\uffff"+
            "\1\2\1\1",
            "",
            "\2\1\1\4\3\1\3\uffff\3\1\1\3\1\uffff\1\1\1\uffff\1\1\3\uffff"+
            "\1\1\2\uffff\2\1\7\uffff\1\1\1\uffff\2\1\1\uffff\3\1\1\uffff"+
            "\3\1\1\3\2\uffff\2\1\3\uffff\1\1\1\uffff\1\1\1\uffff\1\1\1\uffff"+
            "\1\1\1\uffff\1\1\4\uffff\3\1",
            "\1\4\10\uffff\1\1\1\3\37\uffff\1\3\15\uffff\1\1",
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
            return "269:1: expr_list_arg : ( expr |name= id n_ ASSIGN n_ v= expr );";
        }
    }
    static final String DFA43_eotS =
        "\4\uffff";
    static final String DFA43_eofS =
        "\4\uffff";
    static final String DFA43_minS =
        "\2\13\2\uffff";
    static final String DFA43_maxS =
        "\2\116\2\uffff";
    static final String DFA43_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA43_specialS =
        "\4\uffff}>";
    static final String[] DFA43_transitionS = {
            "\1\2\3\uffff\1\3\1\1\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1\2"+
            "\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1\1"+
            "\2\2\2\uffff\2\2\5\uffff\1\2\2\uffff\1\2\1\uffff\1\3\2\uffff"+
            "\1\2\2\uffff\2\2\4\uffff\2\2",
            "\1\2\3\uffff\1\3\1\1\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1"+
            "\1\2\2\2\uffff\2\2\5\uffff\1\2\2\uffff\1\2\1\uffff\1\3\2\uffff"+
            "\1\2\2\uffff\2\2\4\uffff\2\2",
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
            return "274:7: ( n_ arg_expr )?";
        }
    }
    static final String DFA44_eotS =
        "\4\uffff";
    static final String DFA44_eofS =
        "\4\uffff";
    static final String DFA44_minS =
        "\2\13\2\uffff";
    static final String DFA44_maxS =
        "\2\116\2\uffff";
    static final String DFA44_acceptS =
        "\2\uffff\1\1\1\2";
    static final String DFA44_specialS =
        "\4\uffff}>";
    static final String[] DFA44_transitionS = {
            "\1\2\3\uffff\1\3\1\1\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1\2"+
            "\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1\1"+
            "\2\2\2\uffff\2\2\5\uffff\1\2\2\uffff\1\2\1\uffff\1\3\2\uffff"+
            "\1\2\2\uffff\2\2\4\uffff\2\2",
            "\1\2\3\uffff\1\3\1\1\1\2\5\uffff\1\2\1\uffff\2\2\4\uffff\1"+
            "\2\1\uffff\1\2\3\uffff\1\2\3\uffff\1\2\1\uffff\1\2\4\uffff\1"+
            "\1\2\2\2\uffff\2\2\5\uffff\1\2\2\uffff\1\2\1\uffff\1\3\2\uffff"+
            "\1\2\2\uffff\2\2\4\uffff\2\2",
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
            return "274:32: ( n_ arg_expr )?";
        }
    }
    static final String DFA46_eotS =
        "\16\uffff";
    static final String DFA46_eofS =
        "\16\uffff";
    static final String DFA46_minS =
        "\1\13\1\uffff\2\4\1\6\1\13\1\6\2\13\2\uffff\1\13\2\uffff";
    static final String DFA46_maxS =
        "\1\116\1\uffff\2\107\1\101\1\116\1\101\2\116\2\uffff\1\116\2\uffff";
    static final String DFA46_acceptS =
        "\1\uffff\1\1\7\uffff\1\2\1\3\1\uffff\1\4\1\5";
    static final String DFA46_specialS =
        "\16\uffff}>";
    static final String[] DFA46_transitionS = {
            "\1\1\5\uffff\1\1\5\uffff\1\1\1\uffff\2\1\4\uffff\1\2\1\uffff"+
            "\1\1\3\uffff\1\1\3\uffff\1\1\1\uffff\1\1\5\uffff\2\1\2\uffff"+
            "\1\3\1\1\5\uffff\1\1\2\uffff\1\1\4\uffff\1\2\2\uffff\2\1\4\uffff"+
            "\1\2\1\1",
            "",
            "\2\1\1\5\3\1\3\uffff\3\1\1\4\1\uffff\1\1\1\uffff\1\1\3\uffff"+
            "\1\1\2\uffff\2\1\7\uffff\1\1\1\uffff\2\1\1\uffff\3\1\1\uffff"+
            "\3\1\1\4\2\uffff\2\1\3\uffff\1\1\1\uffff\1\1\1\uffff\1\1\3\uffff"+
            "\2\1\3\uffff\3\1",
            "\2\1\1\7\3\1\3\uffff\3\1\1\6\1\uffff\1\1\1\uffff\1\1\3\uffff"+
            "\1\1\2\uffff\2\1\7\uffff\1\1\1\uffff\2\1\1\uffff\3\1\1\uffff"+
            "\3\1\1\6\7\uffff\1\1\1\uffff\1\1\1\uffff\1\1\3\uffff\2\1\3\uffff"+
            "\3\1",
            "\1\5\10\uffff\1\1\1\4\37\uffff\1\4\20\uffff\1\1",
            "\1\11\3\uffff\1\12\1\10\1\11\5\uffff\1\11\1\uffff\2\11\4\uffff"+
            "\1\11\1\uffff\1\11\3\uffff\1\11\3\uffff\1\11\1\uffff\1\11\4"+
            "\uffff\1\10\2\11\2\uffff\2\11\5\uffff\1\11\2\uffff\1\11\1\uffff"+
            "\1\12\2\uffff\1\11\2\uffff\2\11\4\uffff\2\11",
            "\1\7\10\uffff\1\1\1\6\37\uffff\1\6\20\uffff\1\1",
            "\1\14\3\uffff\1\15\1\13\1\14\5\uffff\1\14\1\uffff\2\14\4\uffff"+
            "\1\14\1\uffff\1\14\3\uffff\1\14\3\uffff\1\14\1\uffff\1\14\4"+
            "\uffff\1\13\2\14\2\uffff\2\14\5\uffff\1\14\2\uffff\1\14\1\uffff"+
            "\1\15\2\uffff\1\14\2\uffff\2\14\4\uffff\2\14",
            "\1\11\3\uffff\1\12\1\10\1\11\5\uffff\1\11\1\uffff\2\11\4\uffff"+
            "\1\11\1\uffff\1\11\3\uffff\1\11\3\uffff\1\11\1\uffff\1\11\4"+
            "\uffff\1\10\2\11\2\uffff\2\11\5\uffff\1\11\2\uffff\1\11\1\uffff"+
            "\1\12\2\uffff\1\11\2\uffff\2\11\4\uffff\2\11",
            "",
            "",
            "\1\14\3\uffff\1\15\1\13\1\14\5\uffff\1\14\1\uffff\2\14\4\uffff"+
            "\1\14\1\uffff\1\14\3\uffff\1\14\3\uffff\1\14\1\uffff\1\14\4"+
            "\uffff\1\13\2\14\2\uffff\2\14\5\uffff\1\14\2\uffff\1\14\1\uffff"+
            "\1\15\2\uffff\1\14\2\uffff\2\14\4\uffff\2\14",
            "",
            ""
    };

    static final short[] DFA46_eot = DFA.unpackEncodedString(DFA46_eotS);
    static final short[] DFA46_eof = DFA.unpackEncodedString(DFA46_eofS);
    static final char[] DFA46_min = DFA.unpackEncodedStringToUnsignedChars(DFA46_minS);
    static final char[] DFA46_max = DFA.unpackEncodedStringToUnsignedChars(DFA46_maxS);
    static final short[] DFA46_accept = DFA.unpackEncodedString(DFA46_acceptS);
    static final short[] DFA46_special = DFA.unpackEncodedString(DFA46_specialS);
    static final short[][] DFA46_transition;

    static {
        int numStates = DFA46_transitionS.length;
        DFA46_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA46_transition[i] = DFA.unpackEncodedString(DFA46_transitionS[i]);
        }
    }

    class DFA46 extends DFA {

        public DFA46(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 46;
            this.eot = DFA46_eot;
            this.eof = DFA46_eof;
            this.min = DFA46_min;
            this.max = DFA46_max;
            this.accept = DFA46_accept;
            this.special = DFA46_special;
            this.transition = DFA46_transition;
        }
        @Override
        public String getDescription() {
            return "276:1: arg_expr returns [Map<Id, Node> v] : ( expr |name= id n_ ASSIGN n_ val= expr |name= id n_ ASSIGN | NULL n_ ASSIGN n_ val= expr | NULL n_ ASSIGN );";
        }
    }
 

    public static final BitSet FOLLOW_n__in_script155 = new BitSet(new long[]{0x90660A2286820802L,0x0000000000016190L});
    public static final BitSet FOLLOW_statement_in_script160 = new BitSet(new long[]{0x90660A2286820802L,0x0000000000016190L});
    public static final BitSet FOLLOW_n__in_interactive178 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000016190L});
    public static final BitSet FOLLOW_statement_in_interactive182 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_or_assign_in_statement200 = new BitSet(new long[]{0x0001000000010000L,0x0000000000000004L});
    public static final BitSet FOLLOW_n_in_statement202 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_80_in_statement209 = new BitSet(new long[]{0xFFFFFFFFFFFFFFF0L,0x000000000001FFFFL});
    public static final BitSet FOLLOW_EOF_in_statement214 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EOF_in_n249 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SEMICOLUMN_in_n253 = new BitSet(new long[]{0x0001000000010000L});
    public static final BitSet FOLLOW_n__in_n255 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_alter_assign_in_expr_or_assign270 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_assign_in_expr288 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_while_expr_in_expr_wo_assign305 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_if_expr_in_expr_wo_assign310 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_for_expr_in_expr_wo_assign315 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_repeat_expr_in_expr_wo_assign320 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_function_in_expr_wo_assign325 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NEXT_in_expr_wo_assign330 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_wo_assign337 = new BitSet(new long[]{0x0001000000010000L,0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_expr_wo_assign339 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RPAR_in_expr_wo_assign341 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BREAK_in_expr_wo_assign349 = new BitSet(new long[]{0x0000020000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_wo_assign356 = new BitSet(new long[]{0x0001000000010000L,0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_expr_wo_assign358 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RPAR_in_expr_wo_assign360 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACE_in_sequence393 = new BitSet(new long[]{0xB0670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_sequence395 = new BitSet(new long[]{0xB0660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence400 = new BitSet(new long[]{0x2001000000010000L,0x0000000000000004L});
    public static final BitSet FOLLOW_n_in_sequence403 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_sequence407 = new BitSet(new long[]{0x2001000000010000L,0x0000000000000004L});
    public static final BitSet FOLLOW_n_in_sequence411 = new BitSet(new long[]{0x2000000000000000L});
    public static final BitSet FOLLOW_RBRACE_in_sequence417 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_assign435 = new BitSet(new long[]{0x0000000000000022L,0x0000000000000061L});
    public static final BitSet FOLLOW_ARROW_in_assign442 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_assign444 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_in_assign448 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_assign456 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_assign458 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_in_assign462 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_assign472 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_assign474 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_in_assign478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_assign488 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_assign490 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_in_assign494 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_tilde_expr_in_alter_assign522 = new BitSet(new long[]{0x0000000000000062L,0x0000000000000061L});
    public static final BitSet FOLLOW_ARROW_in_alter_assign533 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_alter_assign535 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign539 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_alter_assign551 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_alter_assign553 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign557 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_alter_assign571 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_alter_assign573 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_alter_assign591 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_alter_assign593 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign597 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_alter_assign611 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_alter_assign613 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_alter_assign617 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IF_in_if_expr644 = new BitSet(new long[]{0x0001020000010000L});
    public static final BitSet FOLLOW_n__in_if_expr646 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LPAR_in_if_expr648 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_if_expr650 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr654 = new BitSet(new long[]{0x0001000000010000L,0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_if_expr656 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RPAR_in_if_expr658 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_if_expr660 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr664 = new BitSet(new long[]{0x0001000000090002L});
    public static final BitSet FOLLOW_n__in_if_expr690 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_ELSE_in_if_expr692 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_if_expr694 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_if_expr698 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_WHILE_in_while_expr726 = new BitSet(new long[]{0x0001020000010000L});
    public static final BitSet FOLLOW_n__in_while_expr728 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LPAR_in_while_expr730 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_while_expr732 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr736 = new BitSet(new long[]{0x0001000000010000L,0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_while_expr738 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RPAR_in_while_expr740 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_while_expr742 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_while_expr746 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FOR_in_for_expr762 = new BitSet(new long[]{0x0001020000010000L});
    public static final BitSet FOLLOW_n__in_for_expr764 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LPAR_in_for_expr766 = new BitSet(new long[]{0x0001000080010000L});
    public static final BitSet FOLLOW_n__in_for_expr768 = new BitSet(new long[]{0x0000000080000000L});
    public static final BitSet FOLLOW_ID_in_for_expr770 = new BitSet(new long[]{0x0001000400010000L});
    public static final BitSet FOLLOW_n__in_for_expr772 = new BitSet(new long[]{0x0000000400000000L});
    public static final BitSet FOLLOW_IN_in_for_expr774 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_for_expr776 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr780 = new BitSet(new long[]{0x0001000000010000L,0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_for_expr782 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RPAR_in_for_expr784 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_for_expr786 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_for_expr790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_REPEAT_in_repeat_expr805 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_repeat_expr807 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_repeat_expr811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FUNCTION_in_function827 = new BitSet(new long[]{0x0001020000010000L});
    public static final BitSet FOLLOW_n__in_function829 = new BitSet(new long[]{0x0000020000000000L});
    public static final BitSet FOLLOW_LPAR_in_function831 = new BitSet(new long[]{0x0001000080010000L,0x0000000000002002L});
    public static final BitSet FOLLOW_n__in_function834 = new BitSet(new long[]{0x0000000080000000L,0x0000000000002002L});
    public static final BitSet FOLLOW_par_decl_in_function837 = new BitSet(new long[]{0x0001000000018000L,0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_function840 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_COMMA_in_function842 = new BitSet(new long[]{0x0001000080010000L,0x0000000000002000L});
    public static final BitSet FOLLOW_n__in_function844 = new BitSet(new long[]{0x0000000080000000L,0x0000000000002000L});
    public static final BitSet FOLLOW_par_decl_in_function846 = new BitSet(new long[]{0x0001000000018000L,0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_function850 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RPAR_in_function854 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_function856 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_function860 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl871 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_par_decl877 = new BitSet(new long[]{0x0001000000010040L});
    public static final BitSet FOLLOW_n__in_par_decl879 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_par_decl881 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_par_decl883 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_in_par_decl885 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_VARIATIC_in_par_decl891 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_expr_in_tilde_expr908 = new BitSet(new long[]{0x0000000000000002L,0x0000000000000080L});
    public static final BitSet FOLLOW_TILDE_in_tilde_expr919 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_tilde_expr921 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_tilde_expr_in_tilde_expr925 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_expr_in_or_expr951 = new BitSet(new long[]{0x0400000000000202L});
    public static final BitSet FOLLOW_or_operator_in_or_expr962 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_or_expr964 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_tilde_expr_in_or_expr968 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_expr_in_and_expr998 = new BitSet(new long[]{0x0000000000000112L});
    public static final BitSet FOLLOW_and_operator_in_and_expr1012 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_and_expr1014 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_tilde_expr_in_and_expr1018 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_expr_in_comp_expr1047 = new BitSet(new long[]{0x0000848018100002L});
    public static final BitSet FOLLOW_comp_operator_in_comp_expr1062 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_comp_expr1064 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_tilde_expr_in_comp_expr1068 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_expr_in_add_expr1101 = new BitSet(new long[]{0x1000080000000002L});
    public static final BitSet FOLLOW_add_operator_in_add_expr1113 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_add_expr1115 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_tilde_expr_in_add_expr1119 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_operator_expr_in_mult_expr1148 = new BitSet(new long[]{0x0000600000040002L});
    public static final BitSet FOLLOW_mult_operator_in_mult_expr1159 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_mult_expr1161 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_tilde_expr_in_mult_expr1165 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_column_expr_in_operator_expr1194 = new BitSet(new long[]{0x0100000000000002L});
    public static final BitSet FOLLOW_OP_in_operator_expr1205 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_operator_expr1207 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_tilde_expr_in_operator_expr1211 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_expr_in_column_expr1240 = new BitSet(new long[]{0x0000000000004002L});
    public static final BitSet FOLLOW_COLUMN_in_column_expr1251 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_column_expr1253 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_tilde_expr_in_column_expr1257 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_unary_expression_in_power_expr1286 = new BitSet(new long[]{0x0000000000002002L});
    public static final BitSet FOLLOW_power_operator_in_power_expr1300 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_power_expr1302 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_power_expr_in_power_expr1306 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NOT_in_unary_expression1336 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_unary_expression1338 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1342 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_unary_expression1349 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_unary_expression1351 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1355 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_unary_expression1362 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_unary_expression1364 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1368 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_unary_expression1375 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_unary_expression1377 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_unary_expression_in_unary_expression1381 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_basic_expr_in_unary_expression1390 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simple_expr_in_basic_expr1408 = new BitSet(new long[]{0x0000025001000082L});
    public static final BitSet FOLLOW_expr_subset_in_basic_expr1429 = new BitSet(new long[]{0x0000025001000082L});
    public static final BitSet FOLLOW_FIELD_in_expr_subset1462 = new BitSet(new long[]{0x0001000080010000L,0x0000000000002010L});
    public static final BitSet FOLLOW_n__in_expr_subset1464 = new BitSet(new long[]{0x0000000080000000L,0x0000000000002010L});
    public static final BitSet FOLLOW_id_in_expr_subset1468 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AT_in_expr_subset1481 = new BitSet(new long[]{0x0001000080010000L,0x0000000000002010L});
    public static final BitSet FOLLOW_n__in_expr_subset1483 = new BitSet(new long[]{0x0000000080000000L,0x0000000000002010L});
    public static final BitSet FOLLOW_id_in_expr_subset1487 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRAKET_in_expr_subset1501 = new BitSet(new long[]{0x90670A2286838800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_list_in_expr_subset1505 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1507 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBB_in_expr_subset1519 = new BitSet(new long[]{0x90670A2286838800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_list_in_expr_subset1523 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1525 = new BitSet(new long[]{0x4000000000000000L});
    public static final BitSet FOLLOW_RBRAKET_in_expr_subset1527 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_expr_subset1544 = new BitSet(new long[]{0x90670A2286838800L,0x0000000000006190L});
    public static final BitSet FOLLOW_args_in_expr_subset1548 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RPAR_in_expr_subset1550 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1577 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bool_in_simple_expr1584 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DD_in_simple_expr1591 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_simple_expr1596 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NUMBER_in_simple_expr1601 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1606 = new BitSet(new long[]{0x0008000000000000L});
    public static final BitSet FOLLOW_NS_GET_in_simple_expr1608 = new BitSet(new long[]{0x0001000080010000L,0x0000000000002010L});
    public static final BitSet FOLLOW_n__in_simple_expr1610 = new BitSet(new long[]{0x0000000080000000L,0x0000000000002010L});
    public static final BitSet FOLLOW_id_in_simple_expr1612 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_simple_expr1617 = new BitSet(new long[]{0x0010000000000000L});
    public static final BitSet FOLLOW_NS_GET_INT_in_simple_expr1619 = new BitSet(new long[]{0x0001000080010000L,0x0000000000002010L});
    public static final BitSet FOLLOW_n__in_simple_expr1621 = new BitSet(new long[]{0x0000000080000000L,0x0000000000002010L});
    public static final BitSet FOLLOW_id_in_simple_expr1623 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_simple_expr1628 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_simple_expr1630 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_or_assign_in_simple_expr1632 = new BitSet(new long[]{0x0001000000010000L,0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_simple_expr1634 = new BitSet(new long[]{0x0000000000000000L,0x0000000000000002L});
    public static final BitSet FOLLOW_RPAR_in_simple_expr1636 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_sequence_in_simple_expr1641 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expr_wo_assign_in_simple_expr1646 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TRUE_in_bool1694 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_FALSE_in_bool1704 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OR_in_or_operator1718 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEOR_in_or_operator1735 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_AND_in_and_operator1751 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_BITWISEAND_in_and_operator1767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GT_in_comp_operator1783 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GE_in_comp_operator1790 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LT_in_comp_operator1797 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LE_in_comp_operator1804 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_EQ_in_comp_operator1811 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NE_in_comp_operator1818 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_add_operator1832 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MINUS_in_add_operator1839 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MULT_in_mult_operator1854 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DIV_in_mult_operator1861 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MOD_in_mult_operator1869 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CARRET_in_power_operator1884 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_expr_list1901 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_list_arg_in_expr_list1903 = new BitSet(new long[]{0x0001000000018000L});
    public static final BitSet FOLLOW_n__in_expr_list1907 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_COMMA_in_expr_list1910 = new BitSet(new long[]{0x90670A2286838800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_expr_list1913 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_list_arg_in_expr_list1915 = new BitSet(new long[]{0x0001000000018000L});
    public static final BitSet FOLLOW_n__in_expr_list1919 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_expr_in_expr_list_arg1932 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_expr_list_arg1940 = new BitSet(new long[]{0x0001000000010040L});
    public static final BitSet FOLLOW_n__in_expr_list_arg1942 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_expr_list_arg1944 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_expr_list_arg1946 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_in_expr_list_arg1950 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_args1969 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_arg_expr_in_args1971 = new BitSet(new long[]{0x0001000000018000L});
    public static final BitSet FOLLOW_n__in_args1975 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_COMMA_in_args1978 = new BitSet(new long[]{0x90670A2286838800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_args1981 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_arg_expr_in_args1983 = new BitSet(new long[]{0x0001000000018000L});
    public static final BitSet FOLLOW_n__in_args1987 = new BitSet(new long[]{0x0000000000008002L});
    public static final BitSet FOLLOW_expr_in_arg_expr2004 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2012 = new BitSet(new long[]{0x0001000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2014 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2016 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_arg_expr2018 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_in_arg_expr2022 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_id_in_arg_expr2030 = new BitSet(new long[]{0x0001000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2032 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2034 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2040 = new BitSet(new long[]{0x0001000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2042 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2044 = new BitSet(new long[]{0x90670A2286830800L,0x0000000000006190L});
    public static final BitSet FOLLOW_n__in_arg_expr2046 = new BitSet(new long[]{0x90660A2286820800L,0x0000000000006190L});
    public static final BitSet FOLLOW_expr_in_arg_expr2050 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_NULL_in_arg_expr2056 = new BitSet(new long[]{0x0001000000010040L});
    public static final BitSet FOLLOW_n__in_arg_expr2058 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_ASSIGN_in_arg_expr2060 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_synpred1_R334 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LPAR_in_synpred2_R353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ARROW_in_synpred3_R530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_ARROW_in_synpred4_R548 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_RIGHT_ARROW_in_synpred5_R566 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SUPER_RIGHT_ARROW_in_synpred6_R586 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ASSIGN_in_synpred7_R606 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred8_R669 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_ELSE_in_synpred8_R671 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_TILDE_in_synpred9_R916 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_or_operator_in_synpred10_R957 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_and_operator_in_synpred11_R1007 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_comp_operator_in_synpred12_R1057 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_add_operator_in_synpred13_R1108 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_mult_operator_in_synpred14_R1154 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_OP_in_synpred15_R1200 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLUMN_in_synpred16_R1246 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_power_operator_in_synpred17_R1295 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_n__in_synpred19_R1439 = new BitSet(new long[]{0x0000000000000002L});

}
