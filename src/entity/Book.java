package entity;

import javax.persistence.Id;
import java.io.Serializable;

@javax.persistence.Entity

public final class Book implements Entity, Serializable {
    @Id
    private String isbn;
    private String title;
    private double price;
    private String author;
    private int pages;

    public Book() { }

    public Book(String isbn, String title, double price, String author, int pages) {
        this.isbn = isbn;
        this.title = title;
        this.price = price;
        this.author = author;
        this.pages = pages;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public String getAuthor() {
        return author;
    }

    public int getPages() {
        return pages;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "Book: isbn: '" + isbn +
                "', title: '" + title +
                "', price: " + price +
                ", author: '" + author +
                "', pages: " + pages;
    }
}
