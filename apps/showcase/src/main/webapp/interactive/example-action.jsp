<%
    request.setAttribute("decorator", "none");
    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
%>
<pre style="font-style: normal;">
<span class="kw">package</span> org.apache.struts2.showcase.action;

<span class="kw">import</span> java.util.Arrays;
<span class="kw">import</span> java.util.Date;
<span class="kw">import</span> java.util.HashMap;
<span class="kw">import</span> java.util.List;
<span class="kw">import</span> java.util.Map;

<span class="kw">import</span> com.opensymphony.xwork2.ActionSupport;

<span class="kw">public class</span> ExampleAction <span class="kw">extends</span> ActionSupport {
    <span class="kw">public static final</span> String CONSTANT = "Struts Rocks!";
        
    <span class="kw">public static</span> Date getCurrentDate() {
        return new Date();
    }
    
    <span class="kw">public</span> String getName() {
        return "John Galt";
    }

    <span class="kw">public</span> String[] getBands() {
        return new String[] { "Pink Floyd", "Metallica", "Guns & Roses" };
    }

    <span class="kw">public</span> List&lt;String&gt; getMovies() {
        return Arrays.asList("Lord of the Rings", "Matrix");
    }

    <span class="kw">public</span> Book getBook() {
        <span class="kw">return</span> new Book("Iliad", "Homer");
    }

    <span class="kw">public</span> Map&lt;String, Book&gt; getBooks() {
        Map&lt;String, Book&gt; books = new HashMap&lt;String, Book&gt;();
        books.put("Iliad", new Book("Iliad", "Homer"));
        books.put("The Republic", new Book("The Replublic", "Plato"));
        books.put("Thus Spake Zarathustra", new Book("Thus Spake Zarathustra",
            "Friedrich Nietzsche"));
        return books;
    }
}

<span class="kw">class</span> Book {
    <span class="kw">private</span> String title;
    <span class="kw">private</span> String author;

    <span class="kw">public</span> Book(String title, String author) {
        this.title = title;
        this.author = author;
    }

    <span class="kw">public</span> String getTitle() {
        <span class="kw">return</span> title;
    }

    <span class="kw">public void</span> setTitle(String title) {
        this.title = title;
    }

    <span class="kw">public</span> String getAuthor() {
        return author;
    }

    <span class="kw">public void</span> setAuthor(String author) {
        this.author = author;
    }
}
</pre>