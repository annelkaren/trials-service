package mx.gob.pjpuebla.trials.core.menu;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class MenuNode implements Serializable {

    private Integer id;
    private String name;
    private String path;
    private List<MenuNode> items;

    public MenuNode(Integer id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }
}
