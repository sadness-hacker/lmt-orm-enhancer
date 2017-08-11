package com.lmt.orm.xml.parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.lmt.orm.xml.tag.ForeachNode;
import com.lmt.orm.xml.tag.IfNode;
import com.lmt.orm.xml.tag.SetNode;
import com.lmt.orm.xml.tag.SqlNode;
import com.lmt.orm.xml.tag.TextNode;
import com.lmt.orm.xml.tag.WhereNode;

/**
 * 
 * @author ducx
 * @date 2017-07-16
 * node解析器
 *
 */
public class NodeParser {
	
	private static final String TOKEN_START = "{";
	
	private static final String TOKEN_END = "}";
	
	private static final char TOKEN_PARAM_SIGN = '#';
	
	private static final char TOKEN_RESULT_SIGN = '@';
	
	private static final char TOKEN_CONCAT_SIGN = '$';

	public static SqlNode parse(Node node){
		if(Node.TEXT_NODE == node.getNodeType()){
			String content = node.getTextContent();
			return parseTextNode(content);
		}
		
		String name = node.getNodeName();
		switch (name) {
		case "if":
			return parseIfNode(node);
		case "set":
			return parseSetNode(node);
		case "where":
			return parseWhereNode(node);
		case "foreach":
			return parseForeachNode(node);
		default:
			break;
		}
		TextNode textNode = new TextNode();
		textNode.setSuffix(node.getTextContent());
		textNode.setType(TextNode.PLAIN);
		return textNode;
	}
	
	private static TextNode parseTextNode(String content){
		String text = content;
		//存在${、#{、@{,并且不是\${、\@{、\#{,则进行处理
		List<TextNode> list = new ArrayList<TextNode>();
		TextNode textNode = new TextNode();
		while(true){
			int index = text.indexOf(TOKEN_START);
			if(index < 1){//不存在需要处理的${、#{、@{
				textNode.setSuffix(text);
				textNode.setType(TextNode.PLAIN);
				break;
			}
			boolean deal = true;
			if(index > 1){
				char a = text.charAt(index - 2);
				if(a == '\\'){
					deal = false;
				}
			}
			if(deal){
				char sign = text.charAt(index - 1);
				int lindex = text.indexOf(TOKEN_END, index);
				if(lindex < 0){
					textNode.setSuffix(text);
					textNode.setType(TextNode.PLAIN);
					break;
				}
				switch (sign) {
				case TOKEN_PARAM_SIGN://参数化字段
					String prefix = text.substring(0, index - 1);
					String paramName = text.substring(index + 1, lindex).trim();
					TextNode tn = new TextNode();
					tn.setParamName(paramName);
					tn.setPrefix(prefix);
					tn.setSuffix("");
					tn.setType(TextNode.PARAM);
					text = text.substring(lindex + 1);
					list.add(tn);
					break;
				case TOKEN_CONCAT_SIGN://字符串拼接型字段
					prefix = text.substring(0, index - 1);
					paramName = text.substring(index + 1, lindex).trim();
					tn = new TextNode();
					tn.setParamName(paramName);
					tn.setPrefix(prefix);
					tn.setSuffix("");
					tn.setType(TextNode.CONCAT);
					text = text.substring(lindex + 1);
					list.add(tn);
					break;
				case TOKEN_RESULT_SIGN://返回结果型字段
					prefix = text.substring(0, index - 1);
					paramName = text.substring(index + 1, lindex).trim();
					tn = new TextNode();
					tn.setParamName(paramName);
					tn.setPrefix(prefix);
					tn.setSuffix("");
					tn.setType(TextNode.RESULT);
					text = text.substring(lindex + 1);
					list.add(tn);
					break;
				default:
					prefix = text.substring(0, index - 1);
					tn = new TextNode();
					tn.setPrefix(prefix);
					tn.setSuffix("");
					tn.setType(TextNode.PLAIN);
					text = text.substring(lindex + 1);
					list.add(tn);
					break;
				}
			}
		}
		textNode.setChildNodeList(list);
		return textNode;
	}
	
	private static IfNode parseIfNode(Node node){
		String expression = node.getAttributes().getNamedItem("test").getTextContent();
		IfNode ifNode = new IfNode();
		ifNode.setExpression(expression);
		NodeList nodeList = node.getChildNodes();
		List<SqlNode> list = new ArrayList<SqlNode>();
		for(int i=0;i<nodeList.getLength();i++){
			SqlNode n = parse(nodeList.item(i));
			list.add(n);
		}
		ifNode.setChildNodeList(list);
		return ifNode;
	}
	
	private static SetNode parseSetNode(Node node){
		SetNode setNode = new SetNode();
		NodeList nodeList = node.getChildNodes();
		List<SqlNode> list = new ArrayList<SqlNode>();
		for(int i=0;i<nodeList.getLength();i++){
			SqlNode n = parse(nodeList.item(i));
			list.add(n);
		}
		setNode.setChildNodeList(list);
		return setNode;
	}
	
	private static WhereNode parseWhereNode(Node node){
		WhereNode whereNode = new WhereNode();
		NodeList nodeList = node.getChildNodes();
		List<SqlNode> list = new ArrayList<SqlNode>();
		for(int i=0;i<nodeList.getLength();i++){
			SqlNode n = parse(nodeList.item(i));
			list.add(n);
		}
		whereNode.setChildNodeList(list);
		return whereNode;
	}
	
	//item，index，collection，open，separator，close
	private static ForeachNode parseForeachNode(Node node){
		ForeachNode foreachNode = new ForeachNode();
		NamedNodeMap map = node.getAttributes();
		if(map.getNamedItem("item") != null){
			String item = map.getNamedItem("item").getTextContent();
			foreachNode.setItem(item);
		}
		if(map.getNamedItem("index") != null){
			String index = map.getNamedItem("index").getTextContent();
			foreachNode.setIndex(index);
		}
		if(map.getNamedItem("collection") != null){
			String collection = map.getNamedItem("collection").getTextContent();
			foreachNode.setCollection(collection);
		}
		if(map.getNamedItem("open") != null){
			String open = map.getNamedItem("open").getTextContent();
			foreachNode.setOpen(open);
		}
		if(map.getNamedItem("separator") != null){
			String separator = map.getNamedItem("separator").getTextContent();
			foreachNode.setSeparator(separator);
		}
		if(map.getNamedItem("close") != null){
			String close = map.getNamedItem("close").getTextContent();
			foreachNode.setClose(close);
		}
		NodeList nodeList = node.getChildNodes();
		List<SqlNode> list = new ArrayList<SqlNode>();
		for(int i=0;i<nodeList.getLength();i++){
			SqlNode n = parse(nodeList.item(i));
			list.add(n);
		}
		foreachNode.setChildNodeList(list);
		return foreachNode;
	}
}
