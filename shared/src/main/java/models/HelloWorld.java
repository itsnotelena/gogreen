package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HelloWorld {

    private long id;
    private String content;

    public HelloWorld() {

    }

    public HelloWorld(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    /**
     * Checks if object is equal.
     *
     * @param other The other object
     * @return Returns true if objects are equal, false otherwise
     */
    public boolean equals(Object other) {
        if (other instanceof HelloWorld) {
            return this.id == ((HelloWorld) other).getId()
                && Objects.equals(this.content, ((HelloWorld) other).getContent());
        }
        return false;
    }

    @Override
    public String toString() {
        return this.content;
    }
}
