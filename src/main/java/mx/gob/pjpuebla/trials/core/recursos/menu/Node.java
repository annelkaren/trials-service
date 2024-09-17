package mx.gob.pjpuebla.trials.core.recursos.menu;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Node {

    private String name;
    private String displayName;
    private String path;
    private List<Node> items;

    public Node() {
        this.items = new ArrayList<>();
    }

    public Node(String fileName) {
        this.items = new ArrayList<>();
        this.name = fileName;
    }

    public Node(String fileName, String displayName, String uri) {
        this.items = new ArrayList<>();
        this.name = fileName;
        this.path = uri;
        this.displayName = displayName;
    }

    public Node findNode(String data) {
        if (this.items == null || this.items.isEmpty()) {
            return null;
        }
        // check Node list to see if there are any that already exist
        return this.items.stream().filter(node -> node.getName().equalsIgnoreCase(data)).findFirst().orElse(null);
    }
}
