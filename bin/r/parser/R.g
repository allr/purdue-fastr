grammar R;

options {
  language = Java ;
  memoize = true;
}

tokens {
  CALL;
  BRAKET;
  KW;
  PARMS;
  SEQUENCE;
  //NULL;
  MISSING_VAL;
  UPLUS;
  UMINUS;
  UTILDE;
 }

@header {
package r.parser;
import r.nodes.*;
//Checkstyle: stop
}
@lexer::header {
package r.parser;
//Checkstyle: stop
}
@rulecatch {
    catch(RecognitionException re){
        throw re; // Stop at first error
    }
}
@lexer::rulecatch {
    catch(RecognitionException re){
        throw re; // Stop at first error ??? Doesn't work at all ??? why ??
    }
}
@members {
    public void display_next_tokens(){
        System.err.print("Allowed tokens: ");
        for(int next: next_tokens())
            System.err.print(tokenNames[next]);
        System.err.println("");
    }
    public int[] next_tokens(){
        return state.following[state._fsp].toArray();
    }
}

@lexer::members{
    public final int MAX_INCOMPLETE_SIZE = 100;
    int incomplete_stack[] = new int[MAX_INCOMPLETE_SIZE]; // TODO probably go for an ArrayList of int :S
    int incomplete_depth;
    
    @Override
    public void reportError(RecognitionException e) {
        throw new RuntimeException(e);
    }
}
@lexer::init{
    incomplete_depth = 0;
    incomplete_stack[incomplete_depth] = 0;
}

/****************************************************
** Known errors : 
** - foo * if(...) ... because of priority
** - No help support '?' & '??'
** - %OP% not very robust, maybe allow everything
** - More than 3x '.' are handled like ...
** - '.' is a valid id
** - Line break are tolerated in strings even without a '\' !!! (ugly)
** - EOF does'nt work with unbalanced structs
** - Improve the stack of balanced structures 
**
** - Must add NA values ... 
*****************************************************/
script returns [Node v]
    @init{ArrayList<Node> stmts = new ArrayList<Node>();}
	@after{ $v = Factory.sequence(stmts);}
	: n_ (s=statement {stmts.add(s);})*
	;
interactive returns [Node v]
	: n_ e=statement {$v = e;}
	;
statement returns [Node v]
	: e=expr_or_assign n {$v = e;}
	| '--EOF--' .* EOF
	;

n_	: (NEWLINE | COMMENT)*;
n	: (NEWLINE | COMMENT)+ | EOF | SEMICOLUMN n_;

expr_or_assign returns [Node v]
	: a=alter_assign { v = a; }
	;
expr returns [Node v]
	: a=assign { v = a; }
	;	
expr_wo_assign returns [Node v]
	: while_expr
	| if_expr
	| for_expr
	| repeat_expr
	| function
	| NEXT ((LPAR)=>LPAR n_ RPAR)? 
	| BREAK ((LPAR)=>LPAR n_ RPAR)? 
	;
sequence returns [Node v]
    @init{ArrayList<Node> stmts = new ArrayList<Node>();}
    @after{ $v = Factory.sequence(stmts);}
	: LBRACE n_ (e=expr_or_assign (n e=expr_or_assign)* n?)?  RBRACE  
	;
assign returns [Node v]
	: l=tilde_expr	
		( ARROW n_ r=expr {v = Factory.binary(BinaryOperator.ASSIGN, l, r);}
		| SUPER_ARROW n_ r=expr {v = Factory.binary(BinaryOperator.SUPER_ASSIGN, l, r);}
		| a=RIGHT_ARROW n_ r=expr {v = Factory.binary(BinaryOperator.ASSIGN, r, l);}
		| a=SUPER_RIGHT_ARROW n_ r=expr {v = Factory.binary(BinaryOperator.SUPER_ASSIGN, r, l);}
		| { v = l;}
		)
	;
alter_assign returns [Node v]
	: l=tilde_expr	
		( (ARROW)=>ARROW n_ r=expr_or_assign {v = Factory.binary(BinaryOperator.ASSIGN, l, r);}
		| (SUPER_ARROW)=>SUPER_ARROW n_ r=expr_or_assign {v = Factory.binary(BinaryOperator.SUPER_ASSIGN, l, r);}
		| (RIGHT_ARROW)=>a=RIGHT_ARROW n_ r=expr_or_assign { v = Factory.binary(BinaryOperator.ASSIGN, r, l);}
		| (SUPER_RIGHT_ARROW)=>a=SUPER_RIGHT_ARROW n_ r=expr_or_assign {v = Factory.binary(BinaryOperator.SUPER_ASSIGN, r, l);}
		| (ASSIGN)=>a=ASSIGN n_ r=expr_or_assign {v = Factory.binary(BinaryOperator.ASSIGN, l, r);}
		| { v = l;}
		)
	;
if_expr returns [Node v]
	:
	IF n_ LPAR n_ cond=expr_or_assign n_ RPAR n_ t=expr_or_assign
	((n_ ELSE)=>(options {greedy=false; backtrack = true;}: n_ ELSE n_ f=expr_or_assign { v = Factory.ternary(TernaryOperator.IF, cond, t, f);})
    | {v = Factory.binary(BinaryOperator.IF, cond, t);}
	)
	;
while_expr returns [Node v]
	: WHILE n_ LPAR n_ c=expr_or_assign n_ RPAR n_ body=expr_or_assign {v = Factory.binary(BinaryOperator.WHILE, c, body);}
	;
for_expr returns [Node v]
	: FOR n_ LPAR n_ ID n_ IN n_ in=expr_or_assign n_ RPAR n_ body=expr_or_assign 
	;
repeat_expr returns [Node v]
	: REPEAT n_ body=expr_or_assign {v = Factory.unary(UnaryOperator.REPEAT, body);}
	;
function returns [Node v]
	: FUNCTION n_ LPAR  n_ (par_decl (n_ COMMA n_ par_decl)* n_)? RPAR n_ body=expr_or_assign 
	;
par_decl
	: ID 
	| ID n_ ASSIGN n_ expr 
	| VARIATIC 
	;
tilde_expr returns [Node v]
	: l=or_expr 
	( ((TILDE)=>TILDE n_ r=tilde_expr {$v = Factory.binary(BinaryOperator.MODEL, l, r);} )
	| {$v=l;})
	;
or_expr returns [Node v]
	: l=and_expr
	(((or_operator)=>op=or_operator n_ r=tilde_expr {$v = Factory.binary(op, l, r);} )
    | {$v=l;})	
	;
and_expr returns [Node v]
	: l=comp_expr
    (((and_operator)=>op=and_operator n_ r=tilde_expr {$v = Factory.binary(op, l, r);} )
    | {$v=l;})
	;
comp_expr returns [Node v]
	: l=add_expr 
    (((comp_operator)=>op=comp_operator n_ r=tilde_expr {$v = Factory.binary(op, l, r);} )
    | {$v=l;})
    	;
add_expr returns [Node v]
	: l=mult_expr
	 (((add_operator)=>op=add_operator n_ r=tilde_expr {$v = Factory.binary(op, l, r);} )
    | {$v=l;})
	;
mult_expr returns [Node v]
	: l=operator_expr
	(((mult_operator)=>op=mult_operator n_ r=tilde_expr {$v = Factory.binary(op, l, r);} )
    | {$v=l;})
	;
operator_expr returns [Node v]
	: l=column_expr
	(((OP)=>op=OP n_ r=tilde_expr {$v = Factory.custom_operator(op, l, r);} )
    | {$v=l;})
	;
column_expr returns [Node v]
	: l=power_expr
	(((COLUMN)=>op=COLUMN n_ r=tilde_expr {$v = Factory.binary(BinaryOperator.COLUMN, l, r);} )
    | {$v=l;})
	;
power_expr returns [Node v]
	: l=unary_expression
    (((power_operator)=>op=power_operator n_ r=power_expr {$v = Factory.binary(op, l, r);} )
    | {$v=l;})
    ;
unary_expression returns [Node v]
	: NOT n_ l=unary_expression {$v = Factory.unary(UnaryOperator.NOT, l);}
	| PLUS n_ l=unary_expression {$v = Factory.unary(UnaryOperator.PLUS, l);}
	| MINUS n_ l=unary_expression {$v = Factory.unary(UnaryOperator.MINUS, l);}
	| TILDE n_ l=unary_expression {$v = Factory.unary(UnaryOperator.MODEL, l);}
	| b=basic_expr { $v=b; }
	;
basic_expr returns [Node v]
	: lhs=simple_expr { $v = lhs; }
	(((FIELD|AT|LBRAKET|LBB|LPAR)=>subset=expr_subset[v] { $v = subset; })+ | (n_)=>)
	;
expr_subset [Node i] returns [Node v]
    : (FIELD n_ name=id) { v = Factory.binary(BinaryOperator.FIELD, i, name); } 
    | (AT n_ name=id)  { v = Factory.binary(BinaryOperator.AT, i, name); } 
    | (LBRAKET subset=expr_list RBRAKET) { v = Factory.call(CallOperator.SUBSET, i, subset); }
    | (LBB subscript=expr_list RBRAKET RBRAKET) { v = Factory.call(CallOperator.SUBSCRIPT, i, subscript); }
    // Must use RBRAKET instead of RBB beacause of : a[b[1]]
    | (LPAR a=args RPAR)  { v = Factory.call(CallOperator.CALL, i, a); } 
    //| { v = i; }
    ;
simple_expr returns [Node n]
	: id
	| b=bool { $n = b; }
	| DD
	| NULL
	| NUMBER
	| id NS_GET n_ id
	| id NS_GET_INT n_ id
	| LPAR n_ expr_or_assign n_ RPAR
	| sequence
	| expr_wo_assign
	;
id	returns [Node n]
    : ID
    | STRING
    | VARIATIC;
bool returns [Node v]
    : TRUE {$v = Factory.createConstant(true); }
    | FALSE {$v = Factory.createConstant(false); };
or_operator returns [BinaryOperator v]
	: OR          {$v = BinaryOperator.OR; }
 	| BITWISEOR   {$v = BinaryOperator.BITWISEOR; };
and_operator returns [BinaryOperator v]
	: AND          {$v = BinaryOperator.AND; }
	| BITWISEAND   {$v = BinaryOperator.BITWISEAND; };
comp_operator returns [BinaryOperator v]
	: GT {$v = BinaryOperator.GT; }
	| GE {$v = BinaryOperator.GE; }
	| LT {$v = BinaryOperator.LT; }
	| LE {$v = BinaryOperator.LE; }
	| EQ {$v = BinaryOperator.EQ; }
	| NE {$v = BinaryOperator.NE; };
add_operator returns [BinaryOperator v]
	: PLUS {$v = BinaryOperator.ADD; }
	| MINUS {$v = BinaryOperator.SUB; };	
mult_operator returns [BinaryOperator v]
	: MULT {$v = BinaryOperator.MULT; }
	| DIV  {$v = BinaryOperator.DIV; }
	| MOD  {$v = BinaryOperator.MOD; };
power_operator returns [BinaryOperator v]
	: CARRET {$v = BinaryOperator.POW; }
	;
expr_list returns [Map<Id, Node> v]
	: (n_ expr_list_arg)? n_ (COMMA (n_ expr_list_arg)? n_)* 
	;
expr_list_arg
	: expr 
	| name=id n_ ASSIGN n_ v=expr 
	;
args returns [Map<Id, Node> v]
    : (n_ arg_expr)? n_ (COMMA (n_ arg_expr)? n_)* 
	;
arg_expr returns [Map<Id, Node> v]
	: expr 
	| name=id n_ ASSIGN n_ val=expr 
	| name=id n_ ASSIGN 
	| NULL n_ ASSIGN n_ val=expr 
	| NULL n_ ASSIGN 
	;
///////////////////////////////////////////////////////////////////////////////
/// Lexer
///
COMMENT
    :   '#' ~('\n'|'\r'|'\f')* (LINE_BREAK | EOF)	{ if(incomplete_stack[incomplete_depth]>0) $channel=HIDDEN; }
    ;
ARROW
	: '<-' | ':='
	;
SUPER_ARROW 
	:	 '<<-' ;
RIGHT_ARROW 
	: '->'
	;
SUPER_RIGHT_ARROW 
	:	'->>'
	;
VARIATIC 
	: '..' '.'+
	; // FIXME
EQ	: '==';
NE 	: '!=';
GE	: '>=';
LE	: '<=';
GT	: '>';
LT 	: '<';
ASSIGN 
	: '=';


NS_GET_INT
	: ':::';
NS_GET
	: '::';

COLUMN
	: ':';
SEMICOLUMN
	: ';';
COMMA
	: ',';
AND
	: '&&';
BITWISEAND 
	: '&';
OR	: '||';
BITWISEOR
	:'|';
LBRACE 
	: '{'	{incomplete_stack[++incomplete_depth] = 0; }; // TODO grow the stack
RBRACE 
	: '}'	{incomplete_depth -- ;};
LPAR 
	: '('	{ incomplete_stack[incomplete_depth] ++; };
RPAR
	: ')'	{ incomplete_stack[incomplete_depth]--; };
LBB
	: '[['	{ incomplete_stack[incomplete_depth] += 2; }; // Must increase by two beacause of ']'']' used for closing
LBRAKET
	: '['	{ incomplete_stack[incomplete_depth] ++; };
RBRAKET
	: ']'	{ incomplete_stack[incomplete_depth] --;};
CARRET
	: '^' | '**';
TILDE
	: '~' ;
MOD
	: '%%' ;

NOT
	: '!';
PLUS
	: '+';
MULT
	: '*';
DIV	: '/';
MINUS
	: '-';

FIELD
	: '$';
AT	: '@';

FUNCTION
	: 'function';
NULL
	: 'NULL';

TRUE
	: 'TRUE';
FALSE
	: 'FALSE';

WHILE 
	: 'while';
FOR	: 'for';
REPEAT
	: 'repeat';
IN	: 'in';
IF	: 'if';
ELSE
	: 'else';
NEXT
	: 'next';
BREAK
	: 'break';
// ?

WS  :   ( ' '
        | '\t'
        ) {$channel=HIDDEN;}
    ;
NEWLINE 
	: LINE_BREAK	{ if(incomplete_stack[incomplete_depth]>0) $channel=HIDDEN; };
NUMBER
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT? ('i'|'L')?
    |   '.'? ('0'..'9')+ EXPONENT? ('i'|'L')?
    |	'0x' HEX_DIGIT+ 'L'?
    ;
DD	: '..' ('0'..'9')+
	;  
ID  : '.'* ID_NAME
	| '.'
	| '`' ( ESC_SEQ | ~('\\'|'`') )* '`'  {setText(getText().substring(1, getText().length()-1));} 
	;
OP	: '%' OP_NAME+ '%'
	;
STRING
    :
    ( '"' ( ESC_SEQ | ~('\\'|'"') )* '"' 
    | '\'' ( ESC_SEQ | ~('\\'|'\'') )* '\'' 
    ) {setText(getText().substring(1, getText().length()-1));} 
    ;
fragment
LINE_BREAK
	:
	 (('\f'|'\r')? '\n')
	| ('\n'? ('\r'|'\f')) // This rule fix very old Mac/Dos/Windows encoded files
	;
fragment
EXPONENT
	: ('e'|'E') ('+'|'-')? ('0'..'9')+ 
	;
fragment
OP_NAME
	: ID_NAME
	| ('*'|'/'|'+'|'-'|'>'|'<'|'='|'|'|'&'|':'|'^'|'.'|'~'|',')
	;
fragment
ID_NAME
	: ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'.')*
	;
fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'"'|'\''|'`'|'\\'|' '|'a'|'v')
    |	'\\' LINE_BREAK // FIXME that's an ugly way to fix this
    |   UNICODE_ESC
    |   OCTAL_ESC
    |	HEX_ESC
    ;
fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
fragment
HEX_ESC
	: '\\x' HEX_DIGIT HEX_DIGIT?
	;
fragment
HEX_DIGIT
	: ('0'..'9'|'a'..'f'|'A'..'F')
	;
fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;
