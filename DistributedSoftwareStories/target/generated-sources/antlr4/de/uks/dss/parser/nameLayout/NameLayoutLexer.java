// Generated from NameLayout.g4 by ANTLR 4.4
package de.uks.dss.parser.nameLayout;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NameLayoutLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RBRACKET=1, LBRACKET=2, GT=3, LT=4, ANYCHARS=5;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'"
	};
	public static final String[] ruleNames = {
		"RBRACKET", "LBRACKET", "GT", "LT", "ANYCHARS"
	};


	public NameLayoutLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "NameLayout.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\7\32\b\1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\6"+
		"\6\27\n\6\r\6\16\6\30\2\2\7\3\3\5\4\7\5\t\6\13\7\3\2\3\5\2*+>>@@\32\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\3\r\3\2\2"+
		"\2\5\17\3\2\2\2\7\21\3\2\2\2\t\23\3\2\2\2\13\26\3\2\2\2\r\16\7+\2\2\16"+
		"\4\3\2\2\2\17\20\7*\2\2\20\6\3\2\2\2\21\22\7@\2\2\22\b\3\2\2\2\23\24\7"+
		">\2\2\24\n\3\2\2\2\25\27\n\2\2\2\26\25\3\2\2\2\27\30\3\2\2\2\30\26\3\2"+
		"\2\2\30\31\3\2\2\2\31\f\3\2\2\2\4\2\30\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}