<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<pre>
package org.apache.struts2.showcase.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ActionSupport;

public class ExampleAction extends ActionSupport {

    public String getName() {
        return &quot;John Galt&quot;;
    }

    public String[] getBands() {
        return new String[] { &quot;Pink Floyd&quot;, &quot;Metallica&quot;, &quot;Guns & Roses&quot; };
    }
    
    public Book getBook() {
        return new Book(&quot;Iliad&quot;, &quot;Homer&quot;);
    }

    public List&lt;String&gt; getMovies() {
        return Arrays.asList(&quot;Lord of the Rings&quot;, &quot;Matrix&quot;);
    }

    public Map&lt;String, Book&gt; getBooks() {
        Map&lt;String, Book&gt; books = new HashMap&lt;String, Book&gt;();
        books.put(&quot;Iliad&quot;, new Book(&quot;Iliad&quot;, &quot;Homer&quot;));
        books.put(&quot;The Republic&quot;, new Book(&quot;The Replublic&quot;, &quot;Plato&quot;));
        books.put(&quot;Thus Spake Zarathustra&quot;, new Book(&quot;Thus Spake Zarathustra&quot;, &quot;Friedrich Nietzsche&quot;));
        return books;
    }
}

class Book {
    private String title;
    private String author;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
</pre>