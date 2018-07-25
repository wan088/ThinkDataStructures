package com.allendowney.thinkdast;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class WikiPhilosophy {

    final static List<String> visited = new ArrayList<String>();
    final static WikiFetcher wf = new WikiFetcher();

    /**
     * Tests a conjecture about Wikipedia and Philosophy.
     *
     * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
     *
     * 1. Clicking on the first non-parenthesized, non-italicized link
     * 2. Ignoring external links, links to the current page, or red links
     * 3. Stopping when reaching "Philosophy", a page with no links or a page
     *    that does not exist, or when a loop occurs
     *
     * @param args
     * @throws IOException
     */
    
    public static void main(String[] args) throws IOException {
        String destination = "https://en.wikipedia.org/wiki/Philosophy";
        String source = "https://en.wikipedia.org/wiki/Java_(programming_language)";
        testConjecture(destination, source, 10);   
    }

    /**
     * Starts from given URL and follows first link until it finds the destination or exceeds the limit.
     *
     * @param destination
     * @param source
     * @throws IOException
     */
    
    public static void testConjecture(String destination, String source, int limit) throws IOException {
		WikiFetcher wf = new WikiFetcher();
    	for(String url=source;;) {
			System.out.println("[java] Fetching "+url);
    		Elements eles = wf.fetchWikipedia(url);
    		boolean bs = false;
    		for(Element ele : eles) {
    			Iterable<Node> iterable = new WikiNodeIterable(ele);
    			Iterator itr = iterable.iterator();
    			Deque<Integer> deq = new ArrayDeque<Integer>();
    			while(itr.hasNext()) {
    				Node now = (Node)itr.next();
    				//텍스트라면 괄호스캔
    				if(now instanceof TextNode) {
    					String st = now.toString();
    					for(int j=0 ; j<st.length() ; j++) {
    						if(st.charAt(j)=='(') deq.add(1);
    						if(st.charAt(j)==')') deq.pop();
    					}
    				}
    				//엘리먼트라면 검사
    				if(!deq.isEmpty()) continue;
    				if(now instanceof Element && now.toString().substring(0, 2).equals("<a")) {
    					//부모중에 이탤릭체가 있는지 확인
    					boolean swit=false;
    					for(Node node=now; node.parentNode()==null; node=now.parentNode()) {
    						String st = node.toString();
    						if(st.substring(0,3).equals("<i>")||st.substring(0, 4).equals("<em>")) {
    							swit=true; break;
    						}
    					}
    					if(swit==true) continue;
    					
    					Element el = (Element)now;
    					url=el.attr("abs:href");
    					String title=el.attr("title");
    					if(visited.contains(title))return;
    					visited.add(title);
    					System.out.println("[java] **"+title+"**");
    					if(title.equals("Philosophy")) return;
    					bs=true;
    					break;
    				}
    			}
    			if(bs==true)break;
    		}
    	}
    }

}