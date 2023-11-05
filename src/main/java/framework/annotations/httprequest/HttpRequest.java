package framework.annotations.httprequest;

import java.util.Objects;

public class HttpRequest {
    private String path;
    private HttpType type;

    public HttpRequest(String path, HttpType type) {
        this.path = path;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public HttpType getType() {
        return type;
    }

    public void setType(HttpType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(path, that.path) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, type);
    }
}
