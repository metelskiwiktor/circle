package pl.wiktor.circle.infrastructure.saver;

enum GoogleMimeTypes {
    FOLDER("application/vnd.google-apps.folder");

    private final static String QUERY_PLACEHOLDER = "mimeType='%s'";
    private final String name;

    GoogleMimeTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getQuery() {
        return String.format(QUERY_PLACEHOLDER, name);
    }
}
