package lucraft.mods.lucraftcore.addonpacks;

public class AddonPackInfo {

    private final String name;
    private final String author;
    private final String description;
    private final String fileName;

    public AddonPackInfo(String name, String author, String description, String fileName) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return name + " by " + author;
    }
}
