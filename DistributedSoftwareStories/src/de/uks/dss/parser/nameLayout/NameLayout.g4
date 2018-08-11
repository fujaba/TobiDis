grammar NameLayout;

options {
	language = Java;
}

// Parser rules:

nameLayout: rules+;

rules: (singleRule | optional)+;
optional: LBRACKET rules+ RBRACKET;

singleRule: ANYCHARS? LT tagName GT ANYCHARS?;
tagName: ANYCHARS; 

RBRACKET: ')';
LBRACKET: '(';
GT: '>';
LT: '<';

//WHITESPACE: [ \t\r\n]+ -> skip;
ANYCHARS: ~('<' | '>' | '(' | ')')+;

