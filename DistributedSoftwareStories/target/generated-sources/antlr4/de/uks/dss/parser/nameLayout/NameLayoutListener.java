// Generated from NameLayout.g4 by ANTLR 4.4
package de.uks.dss.parser.nameLayout;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link NameLayoutParser}.
 */
public interface NameLayoutListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link NameLayoutParser#singleRule}.
	 * @param ctx the parse tree
	 */
	void enterSingleRule(@NotNull NameLayoutParser.SingleRuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link NameLayoutParser#singleRule}.
	 * @param ctx the parse tree
	 */
	void exitSingleRule(@NotNull NameLayoutParser.SingleRuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link NameLayoutParser#nameLayout}.
	 * @param ctx the parse tree
	 */
	void enterNameLayout(@NotNull NameLayoutParser.NameLayoutContext ctx);
	/**
	 * Exit a parse tree produced by {@link NameLayoutParser#nameLayout}.
	 * @param ctx the parse tree
	 */
	void exitNameLayout(@NotNull NameLayoutParser.NameLayoutContext ctx);
	/**
	 * Enter a parse tree produced by {@link NameLayoutParser#rules}.
	 * @param ctx the parse tree
	 */
	void enterRules(@NotNull NameLayoutParser.RulesContext ctx);
	/**
	 * Exit a parse tree produced by {@link NameLayoutParser#rules}.
	 * @param ctx the parse tree
	 */
	void exitRules(@NotNull NameLayoutParser.RulesContext ctx);
	/**
	 * Enter a parse tree produced by {@link NameLayoutParser#optional}.
	 * @param ctx the parse tree
	 */
	void enterOptional(@NotNull NameLayoutParser.OptionalContext ctx);
	/**
	 * Exit a parse tree produced by {@link NameLayoutParser#optional}.
	 * @param ctx the parse tree
	 */
	void exitOptional(@NotNull NameLayoutParser.OptionalContext ctx);
	/**
	 * Enter a parse tree produced by {@link NameLayoutParser#tagName}.
	 * @param ctx the parse tree
	 */
	void enterTagName(@NotNull NameLayoutParser.TagNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link NameLayoutParser#tagName}.
	 * @param ctx the parse tree
	 */
	void exitTagName(@NotNull NameLayoutParser.TagNameContext ctx);
}