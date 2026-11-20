package vinsearch.logic;

/** Part: entity of the KT2 logical model; fields are encapsulated. */
public final class Part {
    private final String partId;
    private final String article;
    private final String name;
    private final String partGroup;

    public Part(String partId, String article, String name, String partGroup) {
        this.partId = partId;
        this.article = article;
        this.name = name;
        this.partGroup = partGroup;
    }

    public String getPartId() { return partId; }
    public String getArticle() { return article; }
    public String getName() { return name; }
    public String getPartGroup() { return partGroup; }
}
