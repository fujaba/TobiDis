// Generated from NameLayout.g4 by ANTLR 4.4
package de.uks.dss.parser.nameLayout;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link NameLayoutParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface NameLayoutVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link NameLayoutParser#singleRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleRule(@NotNull NameLayoutParser.SingleRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link NameLayoutParser#nameLayout}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNameLayout(@NotNull NameLayoutParser.NameLayoutContext ctx);
	/**
	 * Visit a parse tree produced by {@link NameLayoutParser#rules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRules(@NotNull NameLayoutParser.RulesContext ctx);
	/**
	 * Visit a parse tree produced by {@link NameLayoutParser#optional}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOptional(@NotNull NameLayoutParser.OptionalContext ctx);
	/**
	 * Visit a parse tree produced by {@link NameLayoutParser#tagName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTagName(@NotNull NameLayoutParser.TagNameContext ctx);
}