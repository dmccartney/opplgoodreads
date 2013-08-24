package com.mleiseca.opplgoodreads.libraries.chicagoswan;

import com.mleiseca.opplgoodreads.libraries.LibraryQueryResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created with IntelliJ IDEA. User: mleiseca Date: 8/23/13 Time: 11:39 PM To change this template use File | Settings | File Templates.
 */
public class SwanBookQueryParser {

    public List<LibraryQueryResult> parse(String content){
//        b = doc.css('table.browseBibTable')
//        foundTitle= b.css("div.dpBibTitle/a").first.text.strip()
//        foundAuthor = b.css("div.dpBibTitle").children[4].text.strip()
//        foundAuthor.sub!(/^\/ /,"")
//
//        # for example:
//        # doc = Nokogiri::HTML(open('http://swanencore.mls.lib.il.us/iii/encore/search/C%7CSMarguerite+Feitlowitz+A+Lexicon+of+Terror%3A+Argentina+and+the+Legacies+of+Torture%7COrightresult%7CU1?lang=eng&suite=def'))
//        first_result = b.css("div.dpBibHoldingStatement")
//        if first_result and first_result.css("span.callLocation").text().strip().include? "Oak Pk Main"
//        results[:titles] << {
//            :title => foundTitle,
//            :author => foundAuthor,
//            :location=> first_result.css("span.callNum").text().strip().gsub("\u00A0", ""),
//            :status => first_result.css("span.dpBibHoldingStatus").text().strip().gsub("\u00A0", "")
//        }
//        end
//
//            available_table = b.css("table.itemTable")
//
//        available_table.children.each do |row|
//                                          cells = row.css("td")
//        if cells.size >= 2 and (cells[0].text.include? "Oak Pk Main" or cells[0].text.include? "Oak Park Main")
//        results[:titles] << {
//            :title => foundTitle,
//            :author => foundAuthor,
//            :location=>cells[1].text.strip.gsub("\u00A0", ""),
//            :status => cells[2].text.strip.gsub("\u00A0", "")
//        }
//        # print " - found "+foundTitle + " ("+foundAuthor+"): " + cells[1].text.strip + cells[2].text.strip + "\n"
//        end
//            end

        System.out.println("loading");
        List<LibraryQueryResult> statuses = newArrayList();
        Document doc = Jsoup.parse(content);
        Elements elements = doc.select(".browseResult");

        for (Element element : elements) {
            Elements titleElements  = element.select(".dpBibTitle");
            Elements authorElements = element.select(".dpBibAuthor");
            String title  = titleElements.size() > 0  ? titleElements.get(0).text()  : "";
            String author = authorElements.size() > 0 ? authorElements.get(0).text() : "";


            Elements locationTable = element.select("table.itemTable");
            Elements librariesAndStatuses = locationTable.select("td");
            for(int i = 0; i < librariesAndStatuses.size(); i += 3){
                String library    = librariesAndStatuses.get(i    ).text();
                String callNumber = librariesAndStatuses.get(i + 1).text();
                String status     = librariesAndStatuses.get(i + 2).text();

                if(library.contains("Oak Park")){
                    statuses.add(new LibraryQueryResult(title, author,  library, callNumber, status));
                }
            }


        }

        return statuses;
    }
}
