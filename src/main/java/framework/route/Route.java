package framework.route;

import framework.annotations.httprequest.HttpType;

public class Route {
    private String path;
    private HttpType type;

    public Route(String path, HttpType type) {
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
}
