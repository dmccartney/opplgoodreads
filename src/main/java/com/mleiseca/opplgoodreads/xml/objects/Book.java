package com.mleiseca.opplgoodreads.xml.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Book {

    @Element
    private int id;

    @Element(required = false)
    private String isbn;

    @Element(required = false)
    private String isbn13;

    @Element(name = "text_reviews_count", required = false)
    private int textReviewsCount;

    @Element(required = false)
    private String title;

    @Element(name = "image_url", data = true, required = false)
    private String imageUrl;

    @Element(name = "small_image_url", data = true, required = false)
    private String smallImageUrl;

    @Element(required = false)
    private String link;

    @Element(name = "num_pages", required = false)
    private int numPages;

    public int getNumPages() {
        return numPages;
    }

    @Element(required = false)
    private String format;

    @Element(name = "edition_information", required = false)
    private String editionInformation;

    @Element(required = false)
    String publisher;

    @Element(name = "publication_day", required = false)
    private int publicationDay;

    @Element(name = "publication_year", required = false)
    private int publicationYear;

    @Element(name = "publication_month", required = false)
    private int publicationMonth;

    @Element(name = "average_rating")
    private float averageRating;

    @Element(name = "ratings_count")
    private int ratingsCount;

    @Element(required = false)
    private String description;

    @ElementList(required = false)
    private List<AuthorInternal> authors;

    /**
     * Returns a List of Author IDs. Use {@link GoodreadsAPI#getAuthorBooks(int)} to fetch the Author info.
     *
     * @return
     */
    public List<Integer> getAuthors() {
        if (authors == null) {
            return Collections.emptyList();
        }

        List<Integer> ret = new ArrayList<Integer>(authors.size());
        for (AuthorInternal authorInternal : authors) {
            ret.add(authorInternal.id);
        }

        return Collections.unmodifiableList(ret);
    }

    public String getFirstAuthor(){
        if(authors == null){
            return "";
        }else{
            AuthorInternal authorInternal = authors.get(0);
            return authorInternal == null ? "" : authorInternal.name;
        }
    }

    @Root(strict = false)
    private static class AuthorInternal {
        @Element
        int id;

        @Element
        String name;

//        <authors>
//        <author>
//        <id>526389</id>
//        <name>Gerald Grant</name>
//        <image_url><![CDATA[http://www.goodreads.com/assets/nophoto/nophoto-U-200x266-b0aaccaa9663f92f58dd54112a869d72.jpg]]></image_url>
//        <small_image_url><![CDATA[http://www.goodreads.com/assets/nophoto/nophoto-U-50x66-251a730d696018971ef4a443cdeaae05.jpg]]></small_image_url>
//        <link><![CDATA[http://www.goodreads.com/author/show/526389.Gerald_Grant]]></link>
//        <average_rating>3.47</average_rating>
//        <ratings_count>90</ratings_count>
//        <text_reviews_count>23</text_reviews_count>
//        </author>
//        </authors>
    }

    @Element(required = false)
    private String published;


    public int getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public int getTextReviewsCount() {
        return textReviewsCount;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public String getLink() {
        return link;
    }

    public String getFormat() {
        return format;
    }

    public String getEditionInformation() {
        return editionInformation;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPublicationDay() {
        return publicationDay;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public int getPublicationMonth() {
        return publicationMonth;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public String getDescription() {
        return description;
    }

    public String getPublished() {
        return published;
    }
}