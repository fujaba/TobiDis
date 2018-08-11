package de.uks.dss.parser.nameLayout;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import de.uks.dss.model.DocumentDataType;
import de.uks.dss.model.Person;
import de.uks.dss.model.Task;
import de.uks.dss.model.util.DocumentDataSet;
import de.uks.dss.parser.nameLayout.NameLayoutParser.NameLayoutContext;
import de.uks.dss.parser.nameLayout.NameLayoutParser.OptionalContext;
import de.uks.dss.parser.nameLayout.NameLayoutParser.RulesContext;
import de.uks.dss.parser.nameLayout.NameLayoutParser.SingleRuleContext;
import de.uks.dss.parser.nameLayout.NameLayoutParser.TagNameContext;

public class NameLayoutReader extends NameLayoutBaseVisitor<String> {

    private DocumentDataSet docDataSet;

    public NameLayoutReader(DocumentDataSet docDataSet) {
	this.docDataSet = docDataSet;
    }

    public static String parsePerson(Person object, String nameLayout) {
	String parseDocDatas = parseDocDatas(object.getPersonData(), nameLayout);
	// in case, the Parsed Name is null, take the name of the Object
	if (!(parseDocDatas == null || parseDocDatas.equals(""))) {
	    return parseDocDatas;
	} else if (object.getName() != null) {
	    return object.getName();
	} else {
	    return "";
	}
    }

    public static String parseTask(Task object, String nameLayout) {
	String parseDocDatas = parseDocDatas(object.getTaskData(), nameLayout);
	// in case, the Parsed Name is null, take the name of the Object
	// return !(parseDocDatas == null || parseDocDatas.equals("")) ?
	// parseDocDatas : object.getName();
	if (!(parseDocDatas == null || parseDocDatas.equals(""))) {
	    return parseDocDatas;
	} else if (object.getName() != null) {
	    return object.getName();
	} else {
	    return "";
	}
    }

    public static String parseDocDatas(DocumentDataSet docData, String nameLayout) {
	NameLayoutLexer lexer = new NameLayoutLexer(new ANTLRInputStream(nameLayout));
	CommonTokenStream tokenStream = new CommonTokenStream(lexer);

	NameLayoutParser parser = new NameLayoutParser(tokenStream);

	NameLayoutContext nameLayout2 = parser.nameLayout();

	// don't take the LayoutInfo...
	NameLayoutReader visitor = new NameLayoutReader(
		docData.filter(t -> t.getTag() != null && !t.getTag().equals(DocumentDataType.LAYOUTINFO.toString())));

	String result = visitor.visit(nameLayout2);

	return result;
    }

    @Override
    public String visitOptional(OptionalContext ctx) {
	StringBuilder res = new StringBuilder();
	int childCount = ctx.getChildCount();

	for (int i = 0; i < childCount; i++) {
	    ParseTree child = ctx.getChild(i);
	    if (child instanceof RulesContext) {
		/*
		 * Check the Child of the Child.. If it's a SingleRule, the
		 * result must not be null in order for this Optional to be not
		 * null. If it's a Optional, only append if not null, else leave
		 * it out
		 */
		String visitRules = visitRules((RulesContext) child);
		if (visitRules == null) {
		    return null;
		}
		res.append(visitRules);
	    }
	}
	return res.toString();
    }

    @Override
    public String visitRules(RulesContext ctx) {
	StringBuilder res = new StringBuilder();
	int childCount = ctx.getChildCount();

	for (int i = 0; i < childCount; i++) {
	    ParseTree child = ctx.getChild(i);
	    if (child instanceof OptionalContext) {
		// only add, if all conditions inside are fulfilled
		if (child != null && !((OptionalContext) child).children.contains(null)) {
		    // add optional to result
		    // result.append(visitOptional((OptionalContext) child));
		    String visitOptional = visitOptional((OptionalContext) child);
		    if (visitOptional != null) {
			// TODO
			res.append(visitOptional);
		    }
		}
	    } else {
		String visitSingleRule = visitSingleRule((SingleRuleContext) child);
		if (visitSingleRule == null) {
		    return null;
		}
		res.append(visitSingleRule);
	    }
	}
	return res.toString();
    }

    @Override
    public String visitSingleRule(SingleRuleContext ctx) {
	// try to get the right docDataValue

	DocumentDataSet tag = this.docDataSet.filterTag(ctx.tagName().getText());
	if (tag.size() >= 1) {
	    // if there's a docData with the right Tag, we want to replace the
	    // "<TAG>" with "VALUE"
	    StringBuilder res = new StringBuilder();
	    // get the ANYCHARS of the ctx (the the characters in front and
	    // after the tag)
	    int childCount = ctx.ANYCHARS().size();
	    if (childCount == 2) {
		if (ctx.ANYCHARS(0) != null) {
		    res.append(ctx.ANYCHARS(0).toString());
		}
		res.append(tag.first().getValue());
		if (ctx.ANYCHARS(1) != null) {
		    res.append(ctx.ANYCHARS(1).toString());
		}
	    } else if (childCount == 1) {
		// find out, whether ANYCHARS is in front or behind tag
		for (int i = 0; i < ctx.getChildCount(); i++) {
		    if (ctx.getChild(i).equals(ctx.ANYCHARS(0))) {
			// if anychar equals current token, anychar is in front
			// of the tag
			if (ctx.ANYCHARS(0) != null) {
			    res.append(ctx.ANYCHARS(0).toString());
			}
			res.append(tag.first().getValue());
			break;
		    }
		    if (ctx.getChild(i) instanceof TagNameContext) {
			// if tag equals current token, anychar is behind the
			// tag...
			res.append(tag.first().getValue());
			if (ctx.ANYCHARS(0) != null) {
			    res.append(ctx.ANYCHARS(0).toString());
			}
			break;
		    }
		}
	    } else if (childCount == 0) {
		res.append(tag.first().getValue());
		// no characters in front or in the end of the Tag...
	    }
	    return res.toString();
	}

	return null;
    }

}
