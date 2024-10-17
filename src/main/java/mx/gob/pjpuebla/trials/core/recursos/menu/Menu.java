package mx.gob.pjpuebla.trials.core.recursos.menu;

import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class Menu {

    public static Node parseToMenu(Set<String> uris) {
        String parent = Arrays.stream(uris.stream().findFirst().get().split("/")).filter(x -> !x.isEmpty()).findFirst().get();

        Node root = new Node(parent);
        for (String uri : uris) {
            String formattedUri = formatUri(uri,parent);
            addNode(formattedUri, root, uri);
        }
        return root;
    }

    private static String formatUri(String uri, String parent) {
        uri = uri.replace("/*", "");
        if (uri.startsWith(parent) || uri.startsWith("/" + parent)) {
            uri = uri.substring(parent.length() + 1);
        }
        return uri;
    }

    public static Node addNode(String filePath, Node rootNode, String uri) {
        // convenience method. this creates the queue that we need for recursion from
        if (filePath.startsWith("/")) {
            filePath = filePath.substring(1);
        }
        List<String> tokenList = Arrays.asList(filePath.split("/"));
        tokenList.removeIf(StringUtils::isBlank);

        Queue<String> queue = new LinkedList<>(tokenList);
        return addNode(queue, rootNode, uri);
    }

    private static Node addNode(Queue<String> tokens, Node rootNode, String uri) {
        // base case -> node wasnt found and tokens are gone :(
        if (tokens == null || tokens.isEmpty()) {
            return null;
        }
        // get current token, leaving only unsearched ones in the tokens object
        String current = tokens.remove();
        // create node if not already exists
        Node foundNode = rootNode.findNode(current);
        if (foundNode != null) {
            // node exists! recurse
            return addNode(tokens, foundNode, uri);
        } else {
            // node doesnt exist! add it manually and recurse
            return createNode(tokens, rootNode, current, uri);
        }
    }

    private static Node createNode(Queue<String> tokens, Node rootNode, String current, String uri) {
        String displayName = null;
        if (current.contains("--")) {
            String[] names = current.split("--");
            current = names[0];
            displayName = names[1];
            uri = uri + "--";
        }
        return addNewNode(tokens, rootNode, current, displayName, uri);
    }

    private static String getPath(String uri) {
        if (uri != null && uri.endsWith("--")) {
            String[] values = uri.split("--");
            uri = values[0];
            uri = (uri != null && uri.endsWith("/*")) ? uri.replace("/*", "") : uri;
            return uri;
        }
        return null;
    }

    private static Node addNewNode(Queue<String> tokens, Node rootNode, String current, String displayName, String uri) {
        Node newNode = new Node(current, displayName, getPath(uri));
        rootNode.getItems().add(newNode);
        return addNode(tokens, newNode, uri);
    }
}
