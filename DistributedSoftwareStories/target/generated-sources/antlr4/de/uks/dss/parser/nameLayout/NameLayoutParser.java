// Generated from NameLayout.g4 by ANTLR 4.4
package de.uks.dss.parser.nameLayout;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class NameLayoutParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		RBRACKET=1, LBRACKET=2, GT=3, LT=4, ANYCHARS=5;
	public static final String[] tokenNames = {
		"<INVALID>", "')'", "'('", "'>'", "'<'", "ANYCHARS"
	};
	public static final int
		RULE_nameLayout = 0, RULE_rules = 1, RULE_optional = 2, RULE_singleRule = 3, 
		RULE_tagName = 4;
	public static final String[] ruleNames = {
		"nameLayout", "rules", "optional", "singleRule", "tagName"
	};

	@Override
	public String getGrammarFileName() { return "NameLayout.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public NameLayoutParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class NameLayoutContext extends ParserRuleContext {
		public RulesContext rules(int i) {
			return getRuleContext(RulesContext.class,i);
		}
		public List<RulesContext> rules() {
			return getRuleContexts(RulesContext.class);
		}
		public NameLayoutContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nameLayout; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).enterNameLayout(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).exitNameLayout(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NameLayoutVisitor ) return ((NameLayoutVisitor<? extends T>)visitor).visitNameLayout(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameLayoutContext nameLayout() throws RecognitionException {
		NameLayoutContext _localctx = new NameLayoutContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_nameLayout);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(11); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(10); rules();
				}
				}
				setState(13); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACKET) | (1L << LT) | (1L << ANYCHARS))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RulesContext extends ParserRuleContext {
		public OptionalContext optional(int i) {
			return getRuleContext(OptionalContext.class,i);
		}
		public SingleRuleContext singleRule(int i) {
			return getRuleContext(SingleRuleContext.class,i);
		}
		public List<SingleRuleContext> singleRule() {
			return getRuleContexts(SingleRuleContext.class);
		}
		public List<OptionalContext> optional() {
			return getRuleContexts(OptionalContext.class);
		}
		public RulesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rules; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).enterRules(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).exitRules(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NameLayoutVisitor ) return ((NameLayoutVisitor<? extends T>)visitor).visitRules(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RulesContext rules() throws RecognitionException {
		RulesContext _localctx = new RulesContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_rules);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(17); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					setState(17);
					switch (_input.LA(1)) {
					case LT:
					case ANYCHARS:
						{
						setState(15); singleRule();
						}
						break;
					case LBRACKET:
						{
						setState(16); optional();
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(19); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OptionalContext extends ParserRuleContext {
		public TerminalNode RBRACKET() { return getToken(NameLayoutParser.RBRACKET, 0); }
		public RulesContext rules(int i) {
			return getRuleContext(RulesContext.class,i);
		}
		public List<RulesContext> rules() {
			return getRuleContexts(RulesContext.class);
		}
		public TerminalNode LBRACKET() { return getToken(NameLayoutParser.LBRACKET, 0); }
		public OptionalContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_optional; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).enterOptional(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).exitOptional(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NameLayoutVisitor ) return ((NameLayoutVisitor<? extends T>)visitor).visitOptional(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OptionalContext optional() throws RecognitionException {
		OptionalContext _localctx = new OptionalContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_optional);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21); match(LBRACKET);
			setState(23); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(22); rules();
				}
				}
				setState(25); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LBRACKET) | (1L << LT) | (1L << ANYCHARS))) != 0) );
			setState(27); match(RBRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SingleRuleContext extends ParserRuleContext {
		public List<TerminalNode> ANYCHARS() { return getTokens(NameLayoutParser.ANYCHARS); }
		public TerminalNode ANYCHARS(int i) {
			return getToken(NameLayoutParser.ANYCHARS, i);
		}
		public TerminalNode LT() { return getToken(NameLayoutParser.LT, 0); }
		public TerminalNode GT() { return getToken(NameLayoutParser.GT, 0); }
		public TagNameContext tagName() {
			return getRuleContext(TagNameContext.class,0);
		}
		public SingleRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_singleRule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).enterSingleRule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).exitSingleRule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NameLayoutVisitor ) return ((NameLayoutVisitor<? extends T>)visitor).visitSingleRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SingleRuleContext singleRule() throws RecognitionException {
		SingleRuleContext _localctx = new SingleRuleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_singleRule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			_la = _input.LA(1);
			if (_la==ANYCHARS) {
				{
				setState(29); match(ANYCHARS);
				}
			}

			setState(32); match(LT);
			setState(33); tagName();
			setState(34); match(GT);
			setState(36);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(35); match(ANYCHARS);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TagNameContext extends ParserRuleContext {
		public TerminalNode ANYCHARS() { return getToken(NameLayoutParser.ANYCHARS, 0); }
		public TagNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tagName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).enterTagName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof NameLayoutListener ) ((NameLayoutListener)listener).exitTagName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof NameLayoutVisitor ) return ((NameLayoutVisitor<? extends T>)visitor).visitTagName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TagNameContext tagName() throws RecognitionException {
		TagNameContext _localctx = new TagNameContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_tagName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(38); match(ANYCHARS);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\7+\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\6\2\16\n\2\r\2\16\2\17\3\3\3\3\6\3\24\n"+
		"\3\r\3\16\3\25\3\4\3\4\6\4\32\n\4\r\4\16\4\33\3\4\3\4\3\5\5\5!\n\5\3\5"+
		"\3\5\3\5\3\5\5\5\'\n\5\3\6\3\6\3\6\2\2\7\2\4\6\b\n\2\2+\2\r\3\2\2\2\4"+
		"\23\3\2\2\2\6\27\3\2\2\2\b \3\2\2\2\n(\3\2\2\2\f\16\5\4\3\2\r\f\3\2\2"+
		"\2\16\17\3\2\2\2\17\r\3\2\2\2\17\20\3\2\2\2\20\3\3\2\2\2\21\24\5\b\5\2"+
		"\22\24\5\6\4\2\23\21\3\2\2\2\23\22\3\2\2\2\24\25\3\2\2\2\25\23\3\2\2\2"+
		"\25\26\3\2\2\2\26\5\3\2\2\2\27\31\7\4\2\2\30\32\5\4\3\2\31\30\3\2\2\2"+
		"\32\33\3\2\2\2\33\31\3\2\2\2\33\34\3\2\2\2\34\35\3\2\2\2\35\36\7\3\2\2"+
		"\36\7\3\2\2\2\37!\7\7\2\2 \37\3\2\2\2 !\3\2\2\2!\"\3\2\2\2\"#\7\6\2\2"+
		"#$\5\n\6\2$&\7\5\2\2%\'\7\7\2\2&%\3\2\2\2&\'\3\2\2\2\'\t\3\2\2\2()\7\7"+
		"\2\2)\13\3\2\2\2\b\17\23\25\33 &";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}