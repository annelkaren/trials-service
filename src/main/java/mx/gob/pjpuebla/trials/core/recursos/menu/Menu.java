package mx.gob.pjpuebla.trials.core.recursos.menu;

import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class Menu {

    public static Node parseToMenu(Set<String> uris) {
        String parent = Arrays.stream(uris.stream().findFirst().get().split("/")).filter(x -> !x.equals("")).findFirst().get();
        for (String uri : uris) {
            if (!uri.startsWith(parent) && !uri.startsWith("/" + parent)) {
                parent = "/";
                break;
            }
        }
        Node root = new Node(parent);
        for (String uri : uris) {
            AddNode(formatUri(uri, parent), root, uri);
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

    public static Node AddNode(String filePath, Node rootNode, String uri) {
        // convenience method. this creates the queue that we need for recursion from
        // the filepath for you
        if (filePath.startsWith("/")) {
            filePath = filePath.split("/", 2)[1];
        }
        List<String> tokenList = Arrays.asList(filePath.split("/"));
        tokenList.remove(" ");
        // if you split a folder ending with / it leaves an empty string at the end and
        // we want to remove that
        if (StringUtils.isBlank(tokenList.get(tokenList.size() - 1))) {
            tokenList.remove(tokenList.size() - 1);
        }

        PriorityQueue<String> queue = new PriorityQueue<>();
        queue.addAll(tokenList);
        return AddNode(queue, rootNode, uri);
    }

    private static Node AddNode(Queue<String> tokens, Node rootNode, String uri) {
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
            return AddNode(tokens, foundNode, uri);
        } else {
            // node doesnt exist! add it manually and recurse
            return createNode(tokens, rootNode, current, uri);
        }
    }

    private static Node createNode(Queue<String> tokens, Node rootNode, String current, String uri) {
        String parent = null;
        String displayName = null;
        if (current.contains("--")) {
            String[] names = current.split("--");
            current = names[0];
            displayName = names[1];
            parent = names[2];
            uri = uri + "--";
        }
        return addNewNode(tokens, rootNode, current, displayName, uri, parent);
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

    private static Node addNewNode(Queue<String> tokens, Node rootNode, String current, String displayName, String uri, String parent) {
        Node newNode = new Node(current, displayName, getPath(uri), parent);
        rootNode.getItems().add(newNode);
        return AddNode(tokens, newNode, uri);
    }
}
